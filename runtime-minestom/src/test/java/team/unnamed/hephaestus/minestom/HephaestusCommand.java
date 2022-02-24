/*
 * This file is part of hephaestus-engine, licensed under the MIT license
 *
 * Copyright (c) 2021-2022 Unnamed Team
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

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.MinecraftServer;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.CommandContext;
import net.minestom.server.command.builder.CommandExecutor;
import net.minestom.server.command.builder.arguments.ArgumentType;
import net.minestom.server.command.builder.suggestion.SuggestionEntry;
import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.Player;
import net.minestom.server.resourcepack.ResourcePack;
import net.minestom.server.utils.time.TimeUnit;
import team.unnamed.hephaestus.Model;

import java.util.Locale;
import java.util.Objects;
import java.util.function.BiConsumer;

// available commands:
// - /hephaestus summon <model>
// - /hephaestus animate <view> <animation>
// - /hephaestus colorize <view> <color>
// - /hephaestus pack apply
// - /hephaestus pack reload
final class HephaestusCommand extends Command {

    public HephaestusCommand(
            ModelRegistry registry,
            MemoizedCallable<ResourcePack> resourcePackProvider
    ) {
        super("hephaestus");

        var modelArg = ArgumentType.Word("model")
                .setSuggestionCallback((sender, context, suggestion) -> {
                    String input = suggestion.getInput();
                    registry.models().stream()
                            .map(Model::name)
                            .filter(name -> name.toLowerCase(Locale.ROOT).startsWith(input))
                            .forEach(name -> suggestion.addEntry(new SuggestionEntry(name)));
                });

        var viewArg = ArgumentType.Word("view")
                .setSuggestionCallback((sender, context, suggestion) -> {
                    String input = suggestion.getInput();
                    registry.viewIds().stream()
                            .filter(id -> id.toLowerCase(Locale.ROOT).startsWith(input))
                            .forEach(id -> suggestion.addEntry(new SuggestionEntry(id)));
                });

        var animationArg = ArgumentType.Word("animation")
                .setSuggestionCallback((sender, context, suggestion) -> {
                    String viewId = context.get(viewArg);
                    MinestomModelView view = registry.view(viewId);
                    if (view != null) {
                        String input = suggestion.getInput();
                        view.model().animations().keySet()
                                .stream()
                                .filter(name -> name.toLowerCase(Locale.ROOT).startsWith(input))
                                .forEach(name -> suggestion.addEntry(new SuggestionEntry(name)));
                    }
                });

        addSubcommand(new Command("summon") {{
            addSyntax(playerExecutor((player, context) -> {
                String modelName = context.get(modelArg);
                Model model = registry.model(modelName);

                if (model == null) {
                    player.sendMessage(Component.text("Unknown model: " + modelName, NamedTextColor.RED));
                    return;
                }

                MinestomModelView view = new MinestomModelView(EntityType.ARMOR_STAND, model) {
                    @Override
                    public void tick(long time) {
                        super.tick(time);
                        tickAnimations();
                    }
                };

                view.interactListener((interactedView, interactor, action) -> {
                    double yaw = Math.toRadians(interactor.getPosition().yaw());

                    interactedView.colorize(0xDC3232);

                    MinecraftServer.getSchedulerManager()
                            .buildTask(interactedView::colorizeDefault)
                            .delay(4L, TimeUnit.SERVER_TICK)
                            .schedule();

                    ((MinestomModelView) interactedView).takeKnockback(0.4F, Math.sin(yaw), -Math.cos(yaw));
                    MinecraftServer.LOGGER.info(
                            "[hephaestus] player {} interacted with {}, action: {}",
                            interactor.getUsername(),
                            interactedView,
                            action
                    );
                });
                view.setInstance(
                        Objects.requireNonNull(player.getInstance(), "player instance"),
                        player.getPosition()
                );

                String viewId = ModelRegistry.generateViewId();
                registry.view(viewId, view);
                player.sendMessage(
                        Component.text()
                                .color(NamedTextColor.GREEN)
                                .content("Created model view with id: ")
                                .append(Component.text(viewId, NamedTextColor.DARK_GREEN))
                );
            }), modelArg);
        }});

        addSubcommand(new Command("animate") {{
            addSyntax(playerExecutor((player, context) -> {
                String viewId = context.get(viewArg);
                String animationName = context.get(animationArg);

                MinestomModelView view = registry.view(viewId);
                if (view == null) {
                    player.sendMessage("Unknown view: " + viewId);
                    return;
                }

                view.playAnimation(animationName);
            }), viewArg, animationArg);
        }});

        addSubcommand(new Command("pack") {{
            addSubcommand(playerCommand("apply", (player, context) ->
                    player.setResourcePack(resourcePackProvider.get())));

            addSubcommand(playerCommand("reload", (player, context) -> {
                ResourcePack pack;
                try {
                    pack = resourcePackProvider.call();
                } catch (Exception e) {
                    player.sendMessage(Component.text("Failed to export resource pack", NamedTextColor.RED));
                    e.printStackTrace();
                    return;
                }

                player.setResourcePack(pack);
            }));
        }});
    }

    private static CommandExecutor playerExecutor(BiConsumer<Player, CommandContext> executor) {
        return (sender, context) -> {
            if (!(sender instanceof Player player)) {
                sender.sendMessage("Only players can execute this command");
                return;
            }

            executor.accept(player, context);
        };
    }

    private static Command playerCommand(String name, BiConsumer<Player, CommandContext> executor) {
        return new Command(name) {{
            setDefaultExecutor(playerExecutor(executor));
        }};
    }

}
