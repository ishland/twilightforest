package twilightforest.world.components.feature;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import twilightforest.block.HangingWebBlock;
import twilightforest.init.TFBlocks;

public class WebFeature extends Feature<NoneFeatureConfiguration> {

	public WebFeature(Codec<NoneFeatureConfiguration> config) {
		super(config);
	}

	private static boolean isValidMaterial(BlockState state) {
		return state.is(BlockTags.LOGS) || state.is(BlockTags.LEAVES);
	}

	public boolean plaace(FeaturePlaceContext<NoneFeatureConfiguration> context) {
		WorldGenLevel level = context.level();
		BlockPos blockpos = context.origin();
		context.config();
		if (level.isEmptyBlock(blockpos)) {
			BlockState state = TFBlocks.HANGING_WEB.get().defaultBlockState();
			for (Direction direction : Direction.values()) {
				if (HangingWebBlock.isAcceptableNeighbour(level, blockpos.relative(direction), direction) && isValidMaterial(level.getBlockState(blockpos.relative(direction)))) {
					level.setBlock(blockpos, state.setValue(HangingWebBlock.getPropertyForFace(direction), true), 2);
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> config) {
		WorldGenLevel level = config.level();
		BlockPos pos = config.origin().above(config.random().nextInt(level.getMaxBuildHeight() - config.origin().getY()));
		while (pos.getY() > config.origin().getY()) {
			pos = pos.below();
			if (isValidMaterial(level.getBlockState(pos)) && (level.isEmptyBlock(pos.below()) || (level.isEmptyBlock(pos.below(2)) && config.random().nextFloat() <= 0.25F))) {
				BlockState web = TFBlocks.HANGING_WEB.get().defaultBlockState();
				for (Direction direction : Direction.Plane.HORIZONTAL) {
					BlockPos.MutableBlockPos relative = pos.relative(direction).mutable();
					Direction opposite = direction.getOpposite();
					if (level.isEmptyBlock(relative) && HangingWebBlock.isAcceptableNeighbour(level, pos, opposite) && level.setBlock(relative, web.setValue(HangingWebBlock.getPropertyForFace(opposite), true), HangingWebBlock.UPDATE_CLIENTS)) {
						for (int i = 0; i < 1 + config.random().nextInt(7); i++) {
							relative.move(Direction.DOWN);
							if (level.isEmptyBlock(relative) && !level.setBlock(relative, web.setValue(HangingWebBlock.getPropertyForFace(opposite), true), HangingWebBlock.UPDATE_CLIENTS)) break;
						}
					}
				}
				return true;
			}
		}

		return false;
	}
}
