package team.unnamed.hephaestus.minestom;

import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.Pos;
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
import team.unnamed.creative.base.Writable;
import team.unnamed.creative.file.FileTree;
import team.unnamed.creative.metadata.Metadata;
import team.unnamed.creative.metadata.PackMeta;
import team.unnamed.creative.texture.PackInfo;
import team.unnamed.hephaestus.resourcepack.ModelResourcePackWriter;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.DigestOutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.zip.ZipOutputStream;

public class Server {

    public static void main(String[] args) {

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

        Set<MinestomModelView> views = ConcurrentHashMap.newKeySet();

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
                        ModelResourcePackWriter modelWriter = new ModelResourcePackWriter(Collections.singletonList(
                                Models.REDSTONE_MONSTROSITY.getAsset()
                        ), "hephaestus");

                        player.setResourcePack(
                                new MCPacksHttpExporter()
                                        .export(tree -> {
                                            try {
                                                modelWriter.write(tree);

                                                tree.write(PackInfo.builder()
                                                        .icon(Writable.resource(
                                                                Server.class.getClassLoader(),
                                                                "hephaestus.png"
                                                        ))
                                                        .meta(Metadata.builder()
                                                                .add(PackMeta.of(
                                                                        7,
                                                                        "Hephaestus generated resource pack"
                                                                ))
                                                                .build())
                                                        .build());
                                            } catch (IOException e) {
                                                throw new UncheckedIOException(e);
                                            }
                                        })
                        );
                    } catch (IOException e) {
                        throw new IllegalStateException(
                                "Cannot upload resource pack",
                                e
                        );
                    }
                }

                case "spawn" -> {
                    MinestomModelView view = new RedstoneMonstrosityView();
                    view.setTarget(player);

                    view.setInstance(
                            Objects.requireNonNull(player.getInstance(), "player instance"),
                            player.getPosition()
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
        public ResourcePack export(Consumer<FileTree> writer) throws IOException {

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
                writeUTF(
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

                try (FileTree tree = FileTree.zip(new ZipOutputStream(new DigestOutputStream(output, digest)))) {
                    writer.accept(tree);
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

                writeUTF(
                        output,
                        LINE_FEED + "--" + BOUNDARY + "--" + LINE_FEED
                );
            }

            // execute request and close, no response expected
            connection.getInputStream().close();

            System.out.println(DOWNLOAD_URL_TEMPLATE.replace("%HASH%", hashString));

            return ResourcePack.forced(
                    DOWNLOAD_URL_TEMPLATE.replace("%HASH%", hashString),
                    hashString
            );
        }

        private static void writeUTF(
                OutputStream output,
                String string
        ) throws IOException {
            byte[] data = string.getBytes(StandardCharsets.UTF_8);
            output.write(data, 0, data.length);
        }

        private static char hex(int c) {
            return "0123456789abcdef".charAt(c);
        }

    }

}
