package net.thenova.titan.spigot.module.envoy.handler;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.thenova.titan.core.message.MessageBuilder;
import net.thenova.titan.core.message.MessageHandler;
import net.thenova.titan.core.message.placeholders.Placeholder;
import net.thenova.titan.spigot.message.RecipientSpigot;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

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
@AllArgsConstructor
public final class EnvoyRunnable extends BukkitRunnable {

    @Getter private int counter;

    @Override
    public void run() {
        this.counter--;

        if(EnvoyHandler.INSTANCE.getStatus() == EnvoyHandler.Status.WAITING) {
            switch (this.counter) {
                case 600:
                case 300:
                    MessageBuilder builder = MessageHandler.INSTANCE.build("module.envoy.starting.minutes")
                            .placeholder(new Placeholder("time", (counter / 60)));
                    Bukkit.getOnlinePlayers().forEach(player -> builder.send(new RecipientSpigot(player)));
                    break;
                case 30:
                case 15:
                case 10:
                case 5:
                case 4:
                case 3:
                case 2:
                case 1:
                    builder = MessageHandler.INSTANCE.build("module.envoy.starting.seconds")
                            .placeholder(new Placeholder("time", counter),
                                    new Placeholder("value", "second" + (counter > 1 ? "s" : "")));
                    Bukkit.getOnlinePlayers().forEach(player -> builder.send(new RecipientSpigot(player)));
                    break;
            }

            if (this.counter <= 0) {
                EnvoyHandler.INSTANCE.start(false);
                this.cancel();
            }
        } else {
            if(this.counter <= 0) {
                EnvoyHandler.INSTANCE.end(false);
                this.cancel();
            }
        }
    }
}
