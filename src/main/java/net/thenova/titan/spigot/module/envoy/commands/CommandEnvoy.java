package net.thenova.titan.spigot.module.envoy.commands;

import net.thenova.titan.library.command.data.CommandContext;
import net.thenova.titan.spigot.command.SpigotCommand;
import net.thenova.titan.spigot.command.sender.SpigotSender;
import net.thenova.titan.spigot.data.message.MessageHandler;
import net.thenova.titan.spigot.data.message.placeholders.Placeholder;
import net.thenova.titan.spigot.module.envoy.handler.EnvoyHandler;
import net.thenova.titan.spigot.util.UConvert;

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
public final class CommandEnvoy extends SpigotCommand<SpigotSender> {

    public CommandEnvoy() {
        super("envoy", "envoys");
    }

    @Override
    public void execute(SpigotSender sender, CommandContext context) {
        if(EnvoyHandler.INSTANCE.getStatus() == EnvoyHandler.Status.INPROGRESS) {
            MessageHandler.INSTANCE.build("module.envoy.command.in-progress").send(sender);
            return;
        }

        if(sender.hasPermission("nova.admin")
                && context.getArguments().length > 0
                && context.getArgument(0).equalsIgnoreCase("forcestart")) {
            EnvoyHandler.INSTANCE.start(true);
            MessageHandler.INSTANCE.build("module.envoy.command.admin.force-start").send(sender);
            return;
        }

        MessageHandler.INSTANCE.build("module.envoy.command.info")
                .placeholder(new Placeholder("timer", UConvert.getTimeFull(EnvoyHandler.INSTANCE.getRunnable().getCounter())))
                .send(sender);
    }
}
