package tfar.nabba.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandRuntimeException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import tfar.nabba.NABBA;

public class RepositoryCommands {

    public static void register(CommandDispatcher<CommandSourceStack> commandDispatcher) {
        commandDispatcher.register(Commands.literal(NABBA.MODID)
                .then(Commands.literal("clear")
                        .requires(commandSourceStack -> commandSourceStack.hasPermission(3))
                        .then(Commands.literal("all")
                                .executes(RepositoryCommands::clearAll))

                        .then(Commands.argument("id", IntegerArgumentType.integer(0))
                                .executes(RepositoryCommands::clearID))
                )
                .then(Commands.literal("reset_frequency")
                        .executes(RepositoryCommands::resetFrequency)
                )
        );
    }

    private static int clearAll(CommandContext<CommandSourceStack> context) {
        NABBA.instance.data.clearAll();
        return 1;
    }

    private static int clearID(CommandContext<CommandSourceStack> context) {
        int id = IntegerArgumentType.getInteger(context, "id");
        boolean success = NABBA.instance.data.clearId(id);
        if (!success) {
            throw new CommandRuntimeException(Component.translatable("dankstorage.command.clear_id.invalid_id"));
        }
        return 1;
    }
    private static int resetFrequency(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        CommandSourceStack sourceStack = context.getSource();
        Player player = sourceStack.getPlayerOrException();

        ItemStack dank = player.getMainHandItem();

     //   if (dank.getItem() instanceof Repositor) {
      //      dank.setTag(null);
      //      return 1;
      //  } else {
            throw new CommandRuntimeException(Component.translatable("dankstorage.command.reset_frequency.not_a_dank"));
  //      }
    }
}
