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

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;
import team.unnamed.hephaestus.Model;
import team.unnamed.hephaestus.plugin.ModelRegistry;
import team.unnamed.hephaestus.animation.Animation;
import team.unnamed.hephaestus.bukkit.ModelView;
import team.unnamed.hephaestus.bukkit.ModelViewRenderer;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static org.bukkit.ChatColor.DARK_GREEN;
import static org.bukkit.ChatColor.DARK_RED;
import static org.bukkit.ChatColor.GREEN;
import static org.bukkit.ChatColor.RED;

public class ModelCommand
        implements CommandExecutor, TabCompleter {

    private final ModelRegistry modelRegistry;
    private final ModelViewRenderer modelRenderer;

    public ModelCommand(
            ModelRegistry modelRegistry,
            ModelViewRenderer modelRenderer
    ) {
        this.modelRegistry = modelRegistry;
        this.modelRenderer = modelRenderer;
    }

    @Override
    @ParametersAreNonnullByDefault
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (args.length < 1) {
            sender.sendMessage(RED + "/" + label + " spawn");
            return true;
        }

        String subcommand = args[0].toLowerCase(Locale.ROOT);

        switch (subcommand) {
            case "spawn" -> {
                if (!(sender instanceof Player player)) {
                    sender.sendMessage(RED + "You must be a player to do this");
                    return true;
                }

                if (args.length != 2) {
                    sender.sendMessage(RED + "/" + label + " spawn <type>");
                    return true;
                }

                String modelName = args[1].toLowerCase(Locale.ROOT);
                @Nullable Model model = modelRegistry.model(modelName);

                if (model == null) {
                    sender.sendMessage(RED + "Model not found: " + DARK_RED + modelName);
                    return true;
                }

                Location location = player.getLocation();
                String viewId = ModelRegistry.generateViewId();
                ModelView view = modelRenderer.render(model, location);

                // register so it is shown for players in next iterations
                modelRegistry.registerView(viewId, view);
                sender.sendMessage(GREEN + "Created view with id "
                        + DARK_GREEN + viewId
                        + GREEN + " with model "
                        + DARK_GREEN + modelName);
            }
            case "animate" -> {
                if (args.length != 3) {
                    sender.sendMessage(RED + "/" + label + " animate <view> <animation>");
                    return true;
                }

                String viewId = args[1];
                String animationName = args[2];

                @Nullable ModelView view = modelRegistry.view(viewId);

                if (view == null) {
                    sender.sendMessage(RED + "View not found: " + DARK_RED + viewId);
                    return true;
                }

                Map<String, Animation> animations = view.model().animations();
                @Nullable Animation animation = animations.get(animationName);

                if (animation == null) {
                    sender.sendMessage(
                            RED + "Animation not found: "
                                    + DARK_RED + animationName
                                    + RED + ". Available animations: "
                                    + DARK_RED + String.join(", ", animations.keySet())
                    );
                    return true;
                }

                view.animationController().queue(animation);
            }
            default -> sender.sendMessage(RED + "Unknown subcommand");
        }

        return true;
    }

    @Override
    @ParametersAreNonnullByDefault
    public @Nullable List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        List<String> suggestions = new ArrayList<>();

        switch (args.length) {
            case 1 -> StringUtil.copyPartialMatches(args[0], List.of("spawn", "animate"), suggestions);
            case 2 ->  {
                Iterable<String> toSuggest = switch (args[0].toLowerCase(Locale.ROOT)) {
                    case "spawn" -> modelRegistry.modelNames();
                    case "animate" -> modelRegistry.viewIds();
                    default -> List.of();
                };

                StringUtil.copyPartialMatches(args[1], toSuggest, suggestions);
            }
            case 3 -> {
                if (args[0].equalsIgnoreCase("animate")) {
                    String viewId = args[1];
                    @Nullable ModelView view = modelRegistry.view(viewId);

                    if (view != null) {
                        StringUtil.copyPartialMatches(args[2], view.model().animations().keySet(), suggestions);
                    }
                }
            }
        }

        suggestions.sort(String.CASE_INSENSITIVE_ORDER);
        return suggestions;
    }

}
