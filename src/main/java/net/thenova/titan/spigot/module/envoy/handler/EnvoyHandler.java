package net.thenova.titan.spigot.module.envoy.handler;

import de.arraying.kotys.JSON;
import de.arraying.kotys.JSONArray;
import de.arraying.openboard.OpenBoardAPI;
import me.clip.placeholderapi.PlaceholderAPI;
import me.clip.placeholderapi.PlaceholderHook;
import net.thenova.titan.library.Titan;
import net.thenova.titan.library.file.FileHandler;
import net.thenova.titan.library.file.json.JSONFile;
import net.thenova.titan.library.util.UConvert;
import net.thenova.titan.spigot.TitanSpigot;
import net.thenova.titan.spigot.data.message.MessageHandler;
import net.thenova.titan.spigot.data.message.placeholders.Placeholder;
import net.thenova.titan.spigot.data.message.placeholders.PlayerPlaceholder;
import net.thenova.titan.spigot.module.ModuleHandler;
import net.thenova.titan.spigot.module.envoy.handler.data.EnvoyDataFile;
import net.thenova.titan.spigot.module.envoy.handler.data.EnvoyLocation;
import net.thenova.titan.spigot.module.envoy.handler.data.EnvoyReward;
import net.thenova.titan.spigot.module.envoy.handler.data.EnvoyTier;
import net.thenova.titan.spigot.util.ULocation;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.*;

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
public enum EnvoyHandler {
    INSTANCE;

    public static final String REWARDS_PATH = ModuleHandler.INSTANCE.getDataFolder().toPath().toString() + File.separator + "envoy_rewards";

    /* Configuration */
    private final Set<EnvoyLocation> locations = new HashSet<>();
    private JSONFile file;
    private int cratesTotal;
    private int timerCooldown;
    private int timerDuration;

    /* Updating */
    private EnvoyRunnable runnable;
    private Status status;
    private int cratesClaimed;

    public final void load() {
        this.file = FileHandler.INSTANCE.loadJSONFile(EnvoyDataFile.class);
        final JSON json = file.getJSON();

        final JSONArray locations = json.array("locations");
        for(int i = 0; i < locations.length(); i++) {
            this.locations.add(new EnvoyLocation(ULocation.fromString(locations.string(i))));
        }

        this.cratesTotal = json.integer("maximum-crates");
        this.timerCooldown = json.json("timers").integer("cooldown");
        this.timerDuration = json.json("timers").integer("duration");

        // Create files...
        Arrays.stream(EnvoyTier.values()).forEach(EnvoyTier::getDisplayName);

        (runnable = new EnvoyRunnable(json.integer("timer"))).runTaskTimer(TitanSpigot.INSTANCE.getPlugin(), 0, 20);

        PlaceholderAPI.registerPlaceholderHook("envoy", new PlaceholderHook() {
            @Override
            public String onPlaceholderRequest(Player player, String placeholder) {
                switch (placeholder) {
                    case "count_remaining":
                        return (cratesTotal - cratesClaimed) + "";
                    case "count_max":
                        return cratesTotal + "";
                    case "time":
                        return UConvert.getTimeShort(runnable.getCounter());
                }
                return "";
            }
        });
    }

    public void shutdown() {
        this.end(true);

        JSON json = this.file.getJSON();
        json.put("timer", runnable.getCounter());
        this.file.save(json);
    }

    public void start(final boolean force) {
        if(force) {
            this.runnable.cancel();
        }
        (this.runnable = new EnvoyRunnable(this.timerDuration)).runTaskTimer(TitanSpigot.INSTANCE.getPlugin(), 0, 20);

        this.status = Status.INPROGRESS;
        this.cratesClaimed = 0;

        try {
            ArrayList<EnvoyLocation> locations = new ArrayList<>(this.locations);
            Collections.shuffle(locations);

            for (int i = 0; i < this.cratesTotal; i++) {
                locations.get(i).spawn();
            }
        } catch (final IndexOutOfBoundsException ex) {
            Titan.INSTANCE.getLogger().info("[TitanSpigot] [Module] [Envoy] The current number of total crate locations ({}) is less than the maximum set {}",
                    this.locations.size(), this.cratesTotal, ex);
        }

        Bukkit.getOnlinePlayers().forEach(player -> OpenBoardAPI.setScoreboard(player, "envoy"));
        MessageHandler.INSTANCE.build("module.envoy.begin").broadcast();
    }

    public void end(final boolean shutdown) {
        if(this.status != Status.INPROGRESS) {
            return;
        }

        this.runnable.cancel();
        if(!shutdown) {
            (this.runnable = new EnvoyRunnable(timerCooldown)).runTaskTimer(TitanSpigot.INSTANCE.getPlugin(), 0, 20);
        }

        this.status = Status.WAITING;
        this.locations.forEach(EnvoyLocation::remove);
        Bukkit.getOnlinePlayers().forEach(player -> OpenBoardAPI.setScoreboard(player, "default"));

        MessageHandler.INSTANCE.build("module.envoy.end").broadcast();
    }

    public void claim(final Player player, final Location loc) {
        final EnvoyLocation location = this.locations.stream().filter(check -> check.getLocation().equals(loc)).findFirst().orElse(null);

        if(location == null
                || location.isClaimed()) {
            return;
        }

        location.claim();
        this.cratesClaimed++;
        final EnvoyReward reward = location.getTier().getReward();
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), reward.getCommand().replace("{player}", player.getName()));

        MessageHandler.INSTANCE.build("module.envoy.claim")
                .placeholder(new PlayerPlaceholder(player),
                        new Placeholder("tier", location.getTier().getDisplayName()),
                        new Placeholder("count_claimed", this.cratesClaimed),
                        new Placeholder("count_total", this.cratesTotal))
                .broadcast();

        if(this.cratesClaimed >= this.cratesTotal) {
            end(false);
        }
    }

    public void addLocation(final Player player, final Location location) {
        this.locations.add(new EnvoyLocation(location));

        JSON json = this.file.getJSON();
        json.array("locations").append(ULocation.toString(location));
        this.file.save(json);
        MessageHandler.INSTANCE.build("module.envoy.admin.created-location").send(player);
    }

    public boolean isLocation(final Location location) {
        return this.locations.stream().anyMatch(loc -> loc.getLocation().equals(location));
    }

    public final EnvoyRunnable getRunnable() {
        return this.runnable;
    }
    public final Status getStatus() {
        return this.status;
    }
    public final int getCratesClaimed() {
        return this.cratesClaimed;
    }

    public enum Status {
        INPROGRESS, WAITING
    }
}
