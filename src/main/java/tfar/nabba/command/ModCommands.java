package tfar.nabba.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandRuntimeException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.UuidArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import tfar.nabba.NABBA;

import java.util.UUID;

public class ModCommands {

    public static void register(CommandDispatcher<CommandSourceStack> commandDispatcher) {
        commandDispatcher.register(Commands.literal(NABBA.MODID)
                .then(Commands.literal("clear")
                        .requires(commandSourceStack -> commandSourceStack.hasPermission(3))
                        .then(Commands.argument("id", UuidArgument.uuid())
                                .executes(ModCommands::clearID))
                )
                .then(Commands.literal("reset_frequency")
                        .executes(ModCommands::resetFrequency)
                )
        );
    }

    private static int clearID(CommandContext<CommandSourceStack> context) {
        UUID id = UuidArgument.getUuid(context, "id");
        boolean success = true;//NABBA.instance.data.clearId(id);
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
