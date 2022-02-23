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
package team.unnamed.hephaestus;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import team.unnamed.hephaestus.view.BukkitModelView;

import java.util.logging.Logger;

public class ModelShowTask implements Runnable {

    private static final Logger LOGGER = Logger.getLogger(ModelShowTask.class.getName());

    private final ModelRegistry modelRegistry;
    private final int rangeSquared;

    public ModelShowTask(ModelRegistry modelRegistry) {
        this.modelRegistry = modelRegistry;

        int range = Integer.getInteger("hephaestus.view-range", 25);
        this.rangeSquared = range * range;
    }

    @Override
    public void run() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            Location playerLocation = player.getLocation();

            for (BukkitModelView view : modelRegistry.views()) {
                Location location = view.location();

                if (!location.getWorld().equals(playerLocation.getWorld())) {
                    // different world, hide
                    if (view.removeViewer(player)) {
                        LOGGER.info("Hiding view " + view + " from player " + player.getName()
                                + " because they are in a different world");
                    }
                    continue;
                }

                if (location.distanceSquared(playerLocation) < rangeSquared) {
                    // in range, show
                    if (view.addViewer(player)) {
                        LOGGER.info("Showing view " + view + " to player " + player.getName()
                                + "because they entered the model vision range");
                    }
                } else {
                    if (view.removeViewer(player)) {
                        LOGGER.info("Hiding view " + view + " from player " + player.getName()
                                + "because they left the model vision range");
                    }
                }
            }
        }
    }

}
