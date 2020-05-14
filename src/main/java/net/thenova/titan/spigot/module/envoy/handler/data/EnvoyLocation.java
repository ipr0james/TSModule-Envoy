package net.thenova.titan.spigot.module.envoy.handler.data;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import net.thenova.titan.spigot.TitanSpigot;
import net.thenova.titan.spigot.data.compatability.model.CompMaterial;
import net.thenova.titan.spigot.util.UColor;
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
public final class EnvoyLocation {

    private final Location location;

    private EnvoyTier tier;
    private Hologram holo;
    private boolean claimed = false;

    public EnvoyLocation(final Location location) {
        this.location = location;
    }

    public void spawn() {
        this.claimed = false;
        this.tier = EnvoyTier.getTier();

        this.location.getBlock().setType(CompMaterial.END_PORTAL_FRAME.getMaterial());

        this.holo = HologramsAPI.createHologram(TitanSpigot.INSTANCE.getPlugin(),
                new Location(this.location.getWorld(),
                        this.location.getBlockX() + 0.5,
                        this.location.getBlockY() + 0.5,
                        this.location.getZ() + 0.5));
        this.holo.appendTextLine(UColor.colorize("&7&l» &b&l" + this.tier.getDisplayName() + " Crate &7&l«"));
    }

    public void claim() {
        this.claimed = true;

        this.remove();
    }

    public void remove() {
        this.holo.delete();
        this.location.getBlock().setType(Material.AIR);
    }

    public boolean isClaimed() {
        return claimed;
    }
    public final Location getLocation() {
        return this.location;
    }
    public final EnvoyTier getTier() {
        return this.tier;
    }
}
