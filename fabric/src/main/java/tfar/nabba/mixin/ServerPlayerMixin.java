package tfar.nabba.mixin;

import com.mojang.authlib.GameProfile;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import tfar.nabba.NABBA;

import java.util.OptionalInt;

@Mixin(ServerPlayer.class)
public abstract class ServerPlayerMixin extends Player {
    public ServerPlayerMixin(Level level, BlockPos blockPos, float f, GameProfile gameProfile) {
        super(level, blockPos, f, gameProfile);
    }

    @Inject(method = "openMenu",at = @At(value = "INVOKE",target = "Ljava/util/OptionalInt;of(I)Ljava/util/OptionalInt;"))
    private void onOpenMenu(MenuProvider menuProvider, CallbackInfoReturnable<OptionalInt> cir) {
        NABBA.onContainerOpen(this.containerMenu,this);
    }
}