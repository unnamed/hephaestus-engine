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
package team.unnamed.hephaestus.command;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import team.unnamed.hephaestus.Model;
import team.unnamed.hephaestus.ModelRegistry;
import team.unnamed.hephaestus.view.BukkitModelView;
import team.unnamed.hephaestus.view.ModelViewRenderer;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Locale;

import static org.bukkit.ChatColor.DARK_GREEN;
import static org.bukkit.ChatColor.GREEN;
import static org.bukkit.ChatColor.RED;

public class ModelCommand implements CommandExecutor {

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
                    sender.sendMessage(RED + "Model not found: " + modelName);
                    return true;
                }

                Location location = player.getLocation();
                String viewId = ModelRegistry.generateViewId();
                BukkitModelView view = modelRenderer.render(model, location);

                // register so it is shown for players in next iterations
                modelRegistry.registerView(viewId, view);
                sender.sendMessage(GREEN + "Created view with id "
                        + DARK_GREEN + viewId
                        + GREEN + " with model " + modelName);
            }
            default -> {
                sender.sendMessage(RED + "Unknown subcommand");
            }
        }

        return true;
    }

}
