package net.thenova.titan.spigot.module.envoy;

import net.thenova.titan.library.command.data.Command;
import net.thenova.titan.library.database.connection.IDatabase;
import net.thenova.titan.library.database.sql.table.DatabaseTable;
import net.thenova.titan.spigot.data.message.MessageHandler;
import net.thenova.titan.spigot.module.envoy.commands.CommandEnvoy;
import net.thenova.titan.spigot.module.envoy.handler.EnvoyHandler;
import net.thenova.titan.spigot.module.envoy.listeners.ConnectionEvent;
import net.thenova.titan.spigot.module.envoy.listeners.InteractEvent;
import net.thenova.titan.spigot.plugin.IPlugin;
import net.thenova.titan.spigot.users.user.module.UserModule;
import org.bukkit.event.Listener;

import java.util.Arrays;
import java.util.Collections;
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
public final class Envoy implements IPlugin {

    @Override
    public String name() {
        return "Envoy";
    }

    @Override
    public void load() {
        EnvoyHandler.INSTANCE.load();
    }

    @Override
    public void messages(final MessageHandler handler) {
        handler.add("prefix.envoy", "&8[&3Envoy&8]&7");

        handler.add("module.envoy.command.in-progress", "%prefix.envoy% There is already an envoy in progress.");
        handler.add("module.envoy.command.admin.force-start", "%prefix.info% I hope you're an admin. I started an envoy for you boi.");
        handler.add("module.envoy.command.info", "The next envoy will begin in %timer%&7.");

        handler.add("module.envoy.timer.seconds", "%prefix.envoy% An envoy will begin in &b%time% &7%value%.");
        handler.add("module.envoy.timer.minutes", "%prefix.envoy% An envoy will begin in &b%time% &7minutes.");

        handler.add("module.envoy.begin", "%prefix.envoy% An envoy event has begun! &b/spawn");
        handler.add("module.envoy.end", "%prefix.envoy% The current envoy has ended.");

        handler.add("module.envoy.claim", "%prefix.envoy% &b%player% &7found a &b%tier% &7crate. (&b%count_claimed%&7/&b%count_total%&7)");

        handler.add("module.envoy.admin.create.exists", "%prefix.error% This location is already a registered envoy spawn location");
        handler.add("module.envoy.admin.created-location", "%prefix.envoy% Envoy location has been created.");
    }

    @Override
    public void reload() {

    }

    @Override
    public void shutdown() {
        EnvoyHandler.INSTANCE.shutdown();
    }

    @Override
    public IDatabase database() {
        return null;
    }

    @Override
    public List<DatabaseTable> tables() {
        return null;
    }

    @Override
    public List<Listener> listeners() {
        return Arrays.asList(new ConnectionEvent(), new InteractEvent());
    }

    @SuppressWarnings("rawtypes")
    @Override
    public List<Command> commands() {
        return Collections.singletonList(new CommandEnvoy());
    }

    @Override
    public List<Class<? extends UserModule>> user() {
        return null;
    }
}
