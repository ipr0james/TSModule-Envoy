package net.thenova.titan.spigot.module.envoy.listeners;

import net.thenova.titan.spigot.data.message.MessageHandler;
import net.thenova.titan.spigot.module.envoy.handler.EnvoyHandler;
import net.thenova.titan.spigot.util.UValidate;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

/**
 * Copyright 2019 ipr0james
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
public final class InteractEvent implements Listener {

    @EventHandler(priority = EventPriority.NORMAL)
    public void onEnvoyClaim(final PlayerInteractEvent event) {
        final Block block = event.getClickedBlock();
        final Action action = event.getAction();
        final Player player = event.getPlayer();

        if(event.isCancelled()
                || !(action == Action.RIGHT_CLICK_BLOCK || action == Action.LEFT_CLICK_BLOCK)
                || EnvoyHandler.INSTANCE.getStatus() == EnvoyHandler.Status.WAITING) {
            return;
        }

        EnvoyHandler.INSTANCE.claim(player, block.getLocation());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onEnvoyCreate(final PlayerInteractEvent event) {
        final Block block = event.getClickedBlock();
        final Action action = event.getAction();
        final Player player = event.getPlayer();

        if(event.isCancelled()
                || action != Action.RIGHT_CLICK_BLOCK
                || !player.hasPermission("module.envoy.admin")
                || !UValidate.notNull(player.getItemInHand())
                || player.getItemInHand().getType() != Material.STICK
                || !UValidate.notNull(block) || block.getType() != Material.GLOWSTONE) {
            return;
        }

        if(EnvoyHandler.INSTANCE.isLocation(block.getLocation())) {
            MessageHandler.INSTANCE.build("module.envoy.admin.create.exists").send(player);
            return;
        }

        EnvoyHandler.INSTANCE.addLocation(player, block.getLocation());
    }
}
