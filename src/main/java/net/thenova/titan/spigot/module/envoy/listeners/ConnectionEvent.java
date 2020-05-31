package net.thenova.titan.spigot.module.envoy.listeners;

import de.arraying.openboard.OpenBoardAPI;
import net.thenova.titan.core.task.TaskHandler;
import net.thenova.titan.spigot.module.envoy.Envoy;
import net.thenova.titan.spigot.module.envoy.handler.EnvoyHandler;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

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
public final class ConnectionEvent implements Listener {

    @EventHandler(priority = EventPriority.MONITOR)
    public final void onJoin(final PlayerJoinEvent event) {
        TaskHandler.INSTANCE.getScheduler().scheduleSyncDelayed(Envoy.class, () -> {
            if (EnvoyHandler.INSTANCE.getStatus() == EnvoyHandler.Status.IN_PROGRESS) {
                OpenBoardAPI.setScoreboard(event.getPlayer(), "envoy");
            }
        }, 10);
    }
}
