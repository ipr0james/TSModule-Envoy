package net.thenova.titan.spigot.module.envoy.handler.data;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.thenova.titan.core.message.MessageBuilder;
import net.thenova.titan.core.message.placeholders.Placeholder;
import net.thenova.titan.spigot.TitanPluginSpigot;
import net.thenova.titan.spigot.compatibility.compat.XMaterial;
import org.bukkit.Location;
import org.bukkit.Material;

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
@Getter
@RequiredArgsConstructor
public final class EnvoyLocation {

    private final Location location;

    private EnvoyTier tier;
    private Hologram holo;
    private boolean claimed = false;

    /**
     * Handle when an Envoy location is being spawned.
     */
    public void spawn(final MessageBuilder format) {
        this.claimed = false;
        this.tier = EnvoyTier.getTier();

        this.location.getBlock().setType(XMaterial.END_PORTAL_FRAME.getMaterial());

        this.holo = HologramsAPI.createHologram(TitanPluginSpigot.getPlugin(),
                new Location(this.location.getWorld(),
                        this.location.getBlockX() + 0.5,
                        this.location.getBlockY() + 0.5,
                        this.location.getZ() + 0.5));
        this.holo.appendTextLine(format.placeholder(new Placeholder("tier", tier.getDisplayName())).getMessage() /*"&7&l» &b&l" + this.tier.getDisplayName() + " Crate &7&l«"*/);
    }

    /**
     * Set the location to claimed and handle clean-up
     */
    public void claim() {
        this.claimed = true;

        this.remove();
    }

    /**
     * Remove the location, clean-up holo/block
     */
    public void remove() {
        this.holo.delete();
        this.location.getBlock().setType(Material.AIR);
    }
}
