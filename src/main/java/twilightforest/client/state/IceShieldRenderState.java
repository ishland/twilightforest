package twilightforest.client.state;

import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.ColorResolver;
import net.minecraft.world.level.EmptyBlockAndTintGetter;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.lighting.LevelLightEngine;
import net.minecraft.world.level.material.FluidState;

import javax.annotation.Nullable;

public class IceShieldRenderState extends EntityRenderState implements BlockAndTintGetter {
	public BlockPos blockPos;
	@Nullable
	public Holder<Biome> biome;
	public BlockAndTintGetter level = EmptyBlockAndTintGetter.INSTANCE;

	@Override
	public float getShade(Direction direction, boolean shade) {
		return this.level.getShade(direction, shade);
	}

	@Override
	public LevelLightEngine getLightEngine() {
		return this.level.getLightEngine();
	}

	@Override
	public int getBlockTint(BlockPos pos, ColorResolver resolver) {
		return this.biome == null ? -1 : resolver.getColor(this.biome.value(), pos.getX(), pos.getZ());
	}

	@Nullable
	@Override
	public BlockEntity getBlockEntity(BlockPos pos) {
		return null;
	}

	@Override
	public BlockState getBlockState(BlockPos pos) {
		return Blocks.PACKED_ICE.defaultBlockState();
	}

	@Override
	public FluidState getFluidState(BlockPos pos) {
		return this.getBlockState(pos).getFluidState();
	}

	@Override
	public int getHeight() {
		return 1;
	}

	@Override
	public int getMinY() {
		return this.blockPos.getY();
	}
}
