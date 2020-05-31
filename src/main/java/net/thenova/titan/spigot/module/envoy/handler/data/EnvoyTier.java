package net.thenova.titan.spigot.module.envoy.handler.data;

import de.arraying.kotys.JSON;
import net.thenova.titan.library.file.json.JSONFile;
import net.thenova.titan.library.util.URandom;
import net.thenova.titan.spigot.module.envoy.handler.EnvoyHandler;
import org.apache.commons.lang.WordUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
public enum EnvoyTier {
    COMMON(77),
    RARE(20),
    LEGENDARY(3);

    private final int chance;
    private final List<EnvoyReward> rewards = new ArrayList<>();

    EnvoyTier(final int chance) {
        this.chance = chance;

        final JSON json =  new JSONFile(null, EnvoyHandler.REWARDS_PATH, this.toString().toLowerCase()).getJSON();
        json.raw().keySet().forEach(key -> this.rewards.add(json.json(key).marshal(EnvoyReward.class)));
    }

    /* Getters */
    public final String getDisplayName() {
        return WordUtils.capitalize(this.toString().toLowerCase());
    }
    public final EnvoyReward getReward() {
        return this.rewards.get(URandom.r(0, rewards.size() - 1));
    }

    public static EnvoyTier getTier() {
        int random = URandom.r(0, 100);
        return Arrays.stream(values()).filter(tier -> random <= tier.chance).findFirst().orElse(COMMON);
    }

}
