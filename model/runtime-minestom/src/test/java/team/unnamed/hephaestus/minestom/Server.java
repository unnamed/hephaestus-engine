package team.unnamed.hephaestus.minestom;

import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.Player;
import net.minestom.server.event.GlobalEventHandler;
import net.minestom.server.event.player.PlayerChatEvent;
import net.minestom.server.event.player.PlayerLoginEvent;
import net.minestom.server.instance.Chunk;
import net.minestom.server.instance.ChunkGenerator;
import net.minestom.server.instance.ChunkPopulator;
import net.minestom.server.instance.InstanceContainer;
import net.minestom.server.instance.batch.ChunkBatch;
import net.minestom.server.instance.block.Block;
import net.minestom.server.resourcepack.ResourcePack;
import net.minestom.server.world.biomes.Biome;
import org.jetbrains.annotations.NotNull;
import team.unnamed.hephaestus.io.Streams;
import team.unnamed.hephaestus.io.TreeOutputStream;
import team.unnamed.hephaestus.model.Model;
import team.unnamed.hephaestus.model.reader.BBModelReader;
import team.unnamed.hephaestus.model.resourcepack.ModelResourcePackWriter;
import team.unnamed.hephaestus.resourcepack.ResourcePackInfo;
import team.unnamed.hephaestus.resourcepack.ResourcePackWriter;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.DigestOutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.zip.ZipOutputStream;

public class Server {

    public static void main(String[] args) throws Exception {

        // read model
        Model model;
        try (InputStream input = Server.class.getClassLoader()
                .getResourceAsStream("redstone_monstrosity.bbmodel")) {
            Objects.requireNonNull(input, "redstone monstrosity");
            model = new BBModelReader().read(new InputStreamReader(input));
        }

        MinecraftServer server = MinecraftServer.init();
        InstanceContainer instance = MinecraftServer.getInstanceManager().createInstanceContainer();

        instance.setChunkGenerator(new ChunkGenerator() {
            @Override
            public void generateChunkData(@NotNull ChunkBatch batch, int chunkX, int chunkZ) {
                for (byte x = 0; x < Chunk.CHUNK_SIZE_X; x++)
                    for (byte z = 0; z < Chunk.CHUNK_SIZE_Z; z++)
                        batch.setBlock(x, 70, z, Block.GRASS_BLOCK);
            }

            public void fillBiomes(Biome[] biomes, int chunkX, int chunkZ) { Arrays.fill(biomes, Biome.PLAINS); }
            public List<ChunkPopulator> getPopulators() { return null; }
        });

        MinecraftServer.getExtensionManager().setLoadOnStartup(false);

        GlobalEventHandler eventHandler = MinecraftServer.getGlobalEventHandler();
        eventHandler.addListener(PlayerLoginEvent.class, event -> {
            Player player = event.getPlayer();
            event.setSpawningInstance(instance);
            player.setRespawnPoint(new Pos(0, 72, 0));
        });

        Collection<MinestomModelView> views = new HashSet<>();

        eventHandler.addListener(PlayerChatEvent.class, event -> {
            Player player = event.getPlayer();
            String message = event.getMessage().toLowerCase();

            if (message.startsWith("animate ")) {
                String animation = message.substring("animate ".length()).trim();
                for (MinestomModelView view : views) {
                    view.playAnimation(animation);
                }
            }

            switch (message) {

                case "resourcepack" -> {
                    try {
                        player.setResourcePack(new MCPacksHttpExporter().export(ResourcePackWriter.compose(
                                ResourcePackInfo.builder()
                                        .setFormat(7) // 1.17
                                        .setDescription("Hephaestus generated resource pack")
                                        .build()
                                        .toWriter(),
                                new ModelResourcePackWriter(Collections.singletonList(
                                        model.getAsset()
                                ), "hephaestus")
                        )));
                    } catch (IOException e) {
                        throw new IllegalStateException(
                                "Cannot upload resource pack",
                                e
                        );
                    }
                }

                case "spawn" -> {
                    MinestomModelView view = new MinestomModelView(
                            EntityType.HORSE,
                            model
                    );

                    MinecraftServer.getSchedulerManager()
                            .buildTask(view::tickAnimations)
                            .schedule();

                    view.setInstance(
                            Objects.requireNonNull(player.getInstance(), "player instance"),
                            player.getPosition().sub(0, 0.725, 0)
                    );
                    views.add(view);
                }
            }
        });

        server.start("127.0.0.1", 25565);
    }

    /**
     * Fluent-style class for exporting resource
     * packs and upload it using HTTP servers like
     * <a href="https://mc-packs.net">MCPacks</a>,
     * that requires us to compute the SHA-1 hash and
     * upload the file
     */
    private static class MCPacksHttpExporter {

        private static final String UPLOAD_URL = "https://mc-packs.net/";
        private static final String DOWNLOAD_URL_TEMPLATE = "https://download.mc-packs.net/pack/" +
                "%HASH%.zip";

        private static final String BOUNDARY = "HephaestusBoundary";
        private static final String LINE_FEED = "\r\n";

        private final URL url;

        public MCPacksHttpExporter() throws MalformedURLException {
            this.url = new URL(UPLOAD_URL);
        }

        @NotNull
        public ResourcePack export(ResourcePackWriter writer) throws IOException {

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            connection.setConnectTimeout(10000);

            connection.setRequestMethod("POST");
            connection.setDoOutput(true);

            connection.setRequestProperty("User-Agent", "Unnamed-Emojis");
            connection.setRequestProperty("Charset", "utf-8");
            connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + BOUNDARY);

            String hashString;
            byte[] hash;

            // write http request body
            try (OutputStream output = connection.getOutputStream()) {
                Streams.writeUTF(
                        output,
                        "--" + BOUNDARY + LINE_FEED
                                + "Content-Disposition: form-data; name=\"file\"; filename=\"emojis.zip\""
                                + LINE_FEED + "Content-Type: application/zip" + LINE_FEED + LINE_FEED
                );

                MessageDigest digest;

                try {
                    digest = MessageDigest.getInstance("SHA-1");
                } catch (NoSuchAlgorithmException e) {
                    throw new IOException("Cannot find SHA-1 algorithm");
                }

                TreeOutputStream treeOutput = TreeOutputStream.forZip(
                        new ZipOutputStream(new DigestOutputStream(output, digest))
                );
                try {
                    writer.write(treeOutput);
                } finally {
                    treeOutput.finish();
                }

                hash = digest.digest();
                int len = hash.length;
                StringBuilder hashBuilder = new StringBuilder(len * 2);
                for (byte b : hash) {
                    int part1 = (b >> 4) & 0xF;
                    int part2 = b & 0xF;
                    hashBuilder
                            .append(hex(part1))
                            .append(hex(part2));
                }

                hashString = hashBuilder.toString();

                Streams.writeUTF(
                        output,
                        LINE_FEED + "--" + BOUNDARY + "--" + LINE_FEED
                );
            }

            // execute request and close, no response expected
            connection.getInputStream().close();

            return ResourcePack.forced(
                    DOWNLOAD_URL_TEMPLATE.replace("%HASH%", hashString),
                    hashString
            );
        }

        private char hex(int c) {
            return "0123456789abcdef".charAt(c);
        }

    }

}
