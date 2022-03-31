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
package team.unnamed.hephaestus.plugin.command;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;
import team.unnamed.hephaestus.Model;
import team.unnamed.hephaestus.animation.Animation;
import team.unnamed.hephaestus.bukkit.ModelEngine;
import team.unnamed.hephaestus.bukkit.ModelEntity;
import team.unnamed.hephaestus.plugin.ModelRegistry;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import static org.bukkit.ChatColor.DARK_GREEN;
import static org.bukkit.ChatColor.DARK_RED;
import static org.bukkit.ChatColor.GREEN;
import static org.bukkit.ChatColor.RED;

public class ModelCommand
        implements CommandExecutor, TabCompleter {

    private final ModelRegistry modelRegistry;
    private final ModelEngine modelSpawner;

    public ModelCommand(
            ModelRegistry modelRegistry,
            ModelEngine modelSpawner
    ) {
        this.modelRegistry = modelRegistry;
        this.modelSpawner = modelSpawner;
    }

    private void spawn(Player source, Model model) {
        ModelEntity entity = modelSpawner.spawn(model, source.getLocation());
        source.sendMessage(GREEN + "Created view with id "
                + DARK_GREEN + entity.getUniqueId()
                + GREEN + " with model "
                + DARK_GREEN + model.name());
    }

    private void animate(Player source, ModelEntity entity, String animationName) {
        Map<String, Animation> animations = entity.model().animations();
        @Nullable Animation animation = animations.get(animationName);

        if (animation == null) {
            source.sendMessage(
                    RED + "Animation not found: "
                            + DARK_RED + animationName
                            + RED + ". Available animations: "
                            + DARK_RED + String.join(", ", animations.keySet())
            );
        } else {
            entity.animationController().queue(animation);
        }
    }

    @Override
    @ParametersAreNonnullByDefault
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player player)) {
            sender.sendMessage(RED + "You must be a player to do this");
            return true;
        }

        // we need a subcommand
        if (args.length < 1) {
            sender.sendMessage(RED + "/" + label);
            return true;
        }

        String subcommand = args[0].toLowerCase(Locale.ROOT);

        switch (subcommand) {
            case "spawn" -> {
                if (args.length != 2) {
                    sender.sendMessage(RED + "/" + label + " spawn <type>");
                    return true;
                }

                String modelName = args[1];
                @Nullable Model model = modelRegistry.model(modelName);

                if (model == null) {
                    sender.sendMessage(RED + "Model not found: " + DARK_RED + modelName);
                    return true;
                }

                spawn(player, model);
            }
            case "view" -> {
                if (args.length < 2) {
                    sender.sendMessage(RED + "/" + label + " view <view> [...]");
                    return true;
                }

                String viewId = args[1];
                @Nullable ModelEntity entity = getModelEntityById(viewId);

                if (entity == null) {
                    sender.sendMessage(RED + "View not found: " + DARK_RED + viewId);
                    return true;
                }

                switch (args[2].toLowerCase(Locale.ROOT)) {
                    case "animate" -> {
                        if (args.length != 4) {
                            sender.sendMessage(RED + "/" + label + " view <view> animate <animation>");
                            return true;
                        }

                        animate(player, entity, args[3]);
                    }
                    case "colorize" -> {
                        if (args.length != 4) {
                            sender.sendMessage(RED + "/" + label + " view <view> colorize <color>");
                            return true;
                        }

                        String colorArg = args[3];
                        int color;

                        try {
                            if (!colorArg.startsWith("#")) {
                                // looks cooler with a # at the start
                                throw new NumberFormatException();
                            }
                            color = Integer.parseInt(colorArg.substring(1), 16);
                        } catch (NumberFormatException e) {
                            sender.sendMessage(RED + "Invalid hexadecimal color: " + DARK_RED + colorArg);
                            return true;
                        }

                        entity.colorize(color);
                    }
                    case "tphere" -> entity.teleport(player);
                    case "delete" -> entity.remove();
                }
            }
            default -> sender.sendMessage(RED + "Unknown subcommand");
        }

        return true;
    }

    @Override
    @ParametersAreNonnullByDefault
    public @Nullable List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {

        if (args.length == 0) {
            return List.of();
        }

        List<String> suggestions = new ArrayList<>();
        String subcommand = args[0].toLowerCase(Locale.ROOT);

        if (args.length == 1) {
            // complete the subcommand
            StringUtil.copyPartialMatches(subcommand, List.of("spawn", "view"), suggestions);
            return suggestions;
        }

        switch (subcommand) {
            case "spawn" -> {
                if (args.length == 2) {
                    StringUtil.copyPartialMatches(args[1], modelRegistry.modelNames(), suggestions);
                }
            }
            case "view" -> {
                String viewId = args[1];

                if (args.length == 2) {
                    // complete view id
                    List<String> ids = new ArrayList<>();
                    for (World world : Bukkit.getWorlds()) {
                        for (Entity entity : world.getEntities()) {
                            if (entity instanceof ModelEntity) {
                                ids.add(entity.getUniqueId().toString());
                            }
                        }
                    }
                    StringUtil.copyPartialMatches(viewId, ids, suggestions);
                    break;
                }

                String action = args[2].toLowerCase(Locale.ROOT);
                if (args.length == 3) {
                    // complete action
                    StringUtil.copyPartialMatches(action, List.of("animate", "colorize", "tphere", "delete"), suggestions);
                    break;
                }

                if ("animate".equals(action) && args.length == 4) {
                    // complete animations
                    ModelEntity entity = getModelEntityById(viewId);
                    if (entity != null) {
                        StringUtil.copyPartialMatches(args[3], entity.model().animations().keySet(), suggestions);
                    }
                }
            }
        }

        suggestions.sort(String.CASE_INSENSITIVE_ORDER);
        return suggestions;
    }

    private @Nullable ModelEntity getModelEntityById(String id) {
        UUID uuid;
        try {
            uuid = UUID.fromString(id);
        } catch (IllegalArgumentException ignored) {
            return null;
        }

        Entity entity = Bukkit.getEntity(uuid);
        return entity instanceof ModelEntity modelEntity
                ? modelEntity
                : null;
    }

}
