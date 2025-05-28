package uk.shiz.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.TextArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

import java.util.function.BiFunction;

public class CommandRegister {
    private CommandRegistryAccess commandRegistryAccess;
    private CommandDispatcher<ServerCommandSource> dispatcher;

    public CommandRegister(
            CommandRegistryAccess commandRegistryAccess,
            CommandDispatcher<ServerCommandSource> dispatcher
    ) {
        this.commandRegistryAccess = commandRegistryAccess;
        this.dispatcher = dispatcher;
    }

    public class CommandBuilder {
        private final LiteralArgumentBuilder<ServerCommandSource> builder;

        public CommandBuilder(LiteralArgumentBuilder<ServerCommandSource> builder) {
            this.builder = builder;
        }

        public CommandBuilder registerThis() {
            dispatcher.register(builder);
            return this;
        }

        public LiteralArgumentBuilder<ServerCommandSource> getBuilder() {
            return builder;
        }
    }

    public CommandBuilder newCommand(
            String prefix,
            BiFunction<CommandContext<ServerCommandSource>, Text, Integer> cmdCallback
    ) {
        var cmdArgs = CommandManager.argument("args", TextArgumentType.text(this.commandRegistryAccess))
                .executes(context -> CommandCallback(context, cmdCallback));
        LiteralArgumentBuilder<ServerCommandSource> builder = CommandManager.literal(prefix).then(cmdArgs);
        return new CommandBuilder(builder);
    }

    private int CommandCallback(
            CommandContext<ServerCommandSource> context,
            BiFunction<CommandContext<ServerCommandSource>, Text, Integer> action
    ) {
        Text text = TextArgumentType.getTextArgument(context, "args");
        return action.apply(context, text);
    }
}
