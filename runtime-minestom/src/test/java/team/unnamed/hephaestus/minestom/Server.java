/*
 * This file is part of hephaestus-engine, licensed under the MIT license
 *
 * Copyright (c) 2021-2025 Unnamed Team
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package team.unnamed.hephaestus.minestom;

import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.GameMode;
import net.minestom.server.event.GlobalEventHandler;
import net.minestom.server.event.player.AsyncPlayerConfigurationEvent;
import net.minestom.server.event.player.PlayerSpawnEvent;
import net.minestom.server.instance.InstanceContainer;
import net.minestom.server.instance.LightingChunk;
import net.minestom.server.instance.block.Block;
import team.unnamed.creative.BuiltResourcePack;
import team.unnamed.creative.server.ResourcePackServer;
import team.unnamed.creative.server.handler.ResourcePackRequestHandler;
import team.unnamed.hephaestus.minestom.entity.RedstoneMonstrosity;
import team.unnamed.hephaestus.view.modifier.BoneModifierType;
import team.unnamed.hephaestus.view.modifier.player.skin.SkinProvider;
import team.unnamed.hephaestus.view.modifier.player.rig.PlayerRig;

import java.io.InputStream;
import java.util.logging.LogManager;

public class Server {

    private final static SkinProvider SKIN_PROVIDER = SkinProvider.minetools();

    public static void main(String[] args) throws Exception {
        // configure logger
        try (final InputStream stream = Server.class.getResourceAsStream("/logging.properties")) {
            LogManager.getLogManager().readConfiguration(stream);
        }

        final MinecraftServer server = MinecraftServer.init();
        final InstanceContainer instance = MinecraftServer.getInstanceManager().createInstanceContainer();

        instance.setGenerator(unit -> unit.modifier().fillHeight(0, 40, Block.GRASS_BLOCK));
        instance.setChunkSupplier(LightingChunk::new);

        final GlobalEventHandler eventHandler = MinecraftServer.getGlobalEventHandler();
        eventHandler.addListener(AsyncPlayerConfigurationEvent.class, event -> {
            final var player = event.getPlayer();
            player.setGameMode(GameMode.CREATIVE);
            event.setSpawningInstance(instance);
            player.setRespawnPoint(new Pos(0, 42, 0));
        });

        // Register model interaction listener
        ModelClickListener.register(eventHandler);

        // Create our ModelRegistry
        final ModelRegistry registry = new ModelRegistry();
        registry.loadModelsFromResourcesFolder("models");

        eventHandler.addListener(PlayerSpawnEvent.class, event -> {
            final var player = event.getPlayer();

            // add a custom model entity (RedstoneMonstrosity)
            // which has AI
            final RedstoneMonstrosity redstoneMonstrosity = new RedstoneMonstrosity(
                    registry.model("redstone_monstrosity")
            );
            redstoneMonstrosity.setInstance(instance, new Pos(10, 43, 0));
            MinestomModelEngine.minestom().tracker().startGlobalTracking(redstoneMonstrosity);
            registry.view("redstone_monstrosity_" + player.getUsername(), redstoneMonstrosity);
        });

        BuiltResourcePack resourcePack = ResourcePackFactory.create(registry);
        MinecraftServer.getCommandManager().register(new HephaestusCommand(registry, resourcePack));

        {
            // add a test model entity with 'player_anims' model
            final ModelEntity testView = MinestomModelEngine.minestom().createViewAndTrack(
                    registry.model("player_anims"),
                    instance,
                    new Pos(0, 43, 0),
                    1f
            );
            registry.view("test", testView);
        }

        {
            // add a custom model entity (PlayerModelEntity)
            final var playerModel = new ModelEntity(
                    EntityType.ARMOR_STAND,
                    registry.model("player_anims"),
                    1f
            );

            final var skin = SKIN_PROVIDER.fetch("biconsumer");
            final var rig = PlayerRig.vanilla();

            playerModel.bones().forEach(bone -> {
                final var type = rig.get(bone.bone().name());
                if (type != null) {
                    bone.configure(BoneModifierType.PLAYER_PART, modifier -> {
                        modifier.type(type);
                        modifier.skin(skin);
                    });
                }
            });

            playerModel.setInstance(instance, new Pos(5, 43, 0));
            MinestomModelEngine.minestom().tracker().startGlobalTracking(playerModel);
            registry.view("playertest", playerModel);
        }

        // create resource pack server, start and schedule stop
        ResourcePackServer resourcePackServer = ResourcePackServer.server()
                .address("127.0.0.1", 7270)
                .handler(ResourcePackRequestHandler.fixed(resourcePack))
                .build();

        resourcePackServer.start();
        MinecraftServer.getSchedulerManager().buildShutdownTask(() -> resourcePackServer.stop(0));

        // start minecraft server
        server.start("127.0.0.1", 25565);
    }
}
