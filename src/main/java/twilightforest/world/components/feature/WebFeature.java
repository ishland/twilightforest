package twilightforest.world.components.feature;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import twilightforest.block.HangingWebBlock;
import twilightforest.data.tags.BlockTagGenerator;
import twilightforest.init.TFBlocks;

public class WebFeature extends Feature<NoneFeatureConfiguration> {

	public WebFeature(Codec<NoneFeatureConfiguration> config) {
		super(config);
	}

	private static boolean isValid(WorldGenLevel level, BlockPos pos, RandomSource random) {
		BlockState state = level.getBlockState(pos);
		if (state.is(BlockTagGenerator.WEBS_GENERATE_ON_TOP_OF)) return true;
		return state.is(BlockTagGenerator.WEBS_GENERATE_HANGING_FROM) && (level.isEmptyBlock(pos.below()) || (level.isEmptyBlock(pos.below(2)) && random.nextFloat() <= 0.25F));
	}

	private static boolean placeOrAdd(WorldGenLevel level, BlockPos pos, BlockState web, Direction direction) {
		if (level.isEmptyBlock(pos)) return level.setBlock(pos, web.setValue(HangingWebBlock.getPropertyForFace(direction), true), HangingWebBlock.UPDATE_CLIENTS);
		BlockState state = level.getBlockState(pos);
		if (state.is(TFBlocks.HANGING_WEB)) return level.setBlock(pos, state.setValue(HangingWebBlock.getPropertyForFace(direction), true), HangingWebBlock.UPDATE_CLIENTS);
		return false;
	}

	@Override
	public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> config) {
		WorldGenLevel level = config.level();
		RandomSource random = config.random();
		BlockPos pos = config.origin().above(random.nextInt(level.getMaxBuildHeight() - config.origin().getY()));
		while (pos.getY() > config.origin().getY()) {
			pos = pos.below();
			if (isValid(level, pos, random)) {
				BlockState web = TFBlocks.HANGING_WEB.get().defaultBlockState();
				int count = 0;

				for (Direction direction : Direction.Plane.HORIZONTAL.shuffledCopy(random)) {
					BlockPos.MutableBlockPos relative = pos.relative(direction).mutable();
					Direction opposite = direction.getOpposite();
					if (random.nextInt(4) + 1 > count && HangingWebBlock.isAcceptableNeighbour(level, pos, opposite) && placeOrAdd(level, relative, web, opposite)) {
						count++;
						for (int i = 0; i < 1 + random.nextInt(7); i++) {
							relative.move(Direction.DOWN);
							if (!placeOrAdd(level, relative, web, opposite)) break;
						}
					}
				}

				if (count > 1 && random.nextFloat() <= 0.33F && HangingWebBlock.isAcceptableNeighbour(level, pos, Direction.DOWN)) placeOrAdd(level, pos.above(), web, Direction.DOWN);
				return count > 0;
			}
		}

		return false;
	}
}
