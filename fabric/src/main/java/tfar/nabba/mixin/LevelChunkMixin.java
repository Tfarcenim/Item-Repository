package tfar.nabba.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import tfar.nabba.block.BetterBarrelBlock;

@Mixin(LevelChunk.class)
public abstract class LevelChunkMixin {
	@Shadow @Final Level level;

	@Shadow public abstract void removeBlockEntity(BlockPos pPos);

	@Inject(at = @At(value = "INVOKE",
			target = "Lnet/minecraft/world/level/chunk/LevelChunkSection;getBlockState(III)Lnet/minecraft/world/level/block/state/BlockState;"),
			method = "setBlockState",locals = LocalCapture.CAPTURE_FAILHARD)
	private void removeBlockEntityOnClient(BlockPos pPos, BlockState pState, boolean pIsMoving, CallbackInfoReturnable<BlockState> cir,
																				 int i, LevelChunkSection levelchunksection, boolean flag, int j, int k, int l, BlockState blockstate) {
		if (this.level.isClientSide) {
			if (blockstate.hasProperty(BetterBarrelBlock.DISCRETE) && pState.hasProperty(BetterBarrelBlock.DISCRETE)) {
				if (blockstate.getValue(BetterBarrelBlock.DISCRETE) != pState.getValue(BetterBarrelBlock.DISCRETE)) {
					this.removeBlockEntity(pPos);
				}
			}
		}
	}
}
