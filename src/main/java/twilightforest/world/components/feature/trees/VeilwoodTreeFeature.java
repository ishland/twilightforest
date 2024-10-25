package twilightforest.world.components.feature.trees;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.TreeFeature;
import net.minecraft.world.phys.Vec3;
import twilightforest.init.TFBlocks;
import twilightforest.util.RootPlacer;
import twilightforest.util.features.FeaturePlacers;
import twilightforest.util.features.FeatureUtil;
import twilightforest.util.iterators.VoxelBresenhamIterator;
import twilightforest.world.components.feature.config.VeilwoodTreeConfig;

import java.util.*;
import java.util.function.BiConsumer;

public class VeilwoodTreeFeature extends TFTreeFeature<VeilwoodTreeConfig> {
	private final static List<Direction> DIRECTIONS = List.of(Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST);
	private final static Float QUARTER_PI = Mth.HALF_PI * 0.5F;
	protected Map<Vec3, Vec3> planes = new HashMap<>();

	public VeilwoodTreeFeature(Codec<VeilwoodTreeConfig> config) {
		super(config);
	}

	@Override
	@SuppressWarnings("deprecation")
	public boolean generate(WorldGenLevel world, RandomSource random, BlockPos pos, BiConsumer<BlockPos, BlockState> trunkPlacer, BiConsumer<BlockPos, BlockState> leavesPlacer, RootPlacer decorationPlacer, VeilwoodTreeConfig config) {
		final int height = config.minSize + random.nextInt(config.maxSize - config.minSize + 1);

		// do we have enough height?
		if (world.isOutsideBuildHeight(pos.getY() + 1)
			|| world.isOutsideBuildHeight(pos.getY() + height)
			|| FeatureUtil.isAnyMatchInArea(pos.subtract(new Vec3i(1, 4, 1)), 3, 4, 3, blockPos -> world.getBlockState(blockPos).is(BlockTags.FEATURES_CANNOT_REPLACE))
			|| FeatureUtil.isAnyMatchInArea(pos.subtract(new Vec3i(1, 0, 1)), 3, 16, 3, blockPos -> !TreeFeature.validTreePos(world, blockPos) || blockPos == pos)) {
			return false;
		}

		// check if we're on dirt or grass
		if (world.getBlockState(pos.below()).canSustainPlant(world, pos.below(), Direction.UP, TFBlocks.TIME_SAPLING.get().defaultBlockState()).isFalse()) {
			return false;
		}

		this.planes.clear();

		double tilt = 3.0D + random.nextDouble() * 3.0D;
		double thinScale = config.scale * 0.666D;

		Vec3 deadCenter = Vec3.atLowerCornerWithOffset(pos, 1.0D, 0.0D, 1.0D);
		Vec3 angle = new Vec3(tilt, 0.0D, 0.0D).yRot(random.nextInt(4) * Mth.HALF_PI + QUARTER_PI + QUARTER_PI * (random.nextFloat() - 0.5F));
		Vec3 offset = deadCenter.add(angle);
		Vec3 reverse = deadCenter.subtract(angle);

		Vec3 one = deadCenter.add(config.scale, 0.0D, -config.scale);
		one = one.relative(Direction.UP, one.distanceTo(reverse) * 0.5D);

		Vec3 two = deadCenter.add(config.scale, 0.0D, config.scale);
		two = two.relative(Direction.UP, two.distanceTo(reverse) * 0.5D);

		Vec3 three = deadCenter.add(-config.scale, 0.0D, config.scale);
		three = three.relative(Direction.UP, three.distanceTo(reverse) * 0.5D);

		Vec3 four = deadCenter.add(-config.scale, 0.0D, -config.scale);
		four = four.relative(Direction.UP, four.distanceTo(reverse) * 0.5D);

		Vec3 peak = offset.relative(Direction.UP, height * 0.5D + tilt);

		Vec3 oneTwo = peak.add(thinScale, 0.0D, -thinScale);
		this.planes.put(calcNormal(oneTwo, one, two), oneTwo);

		Vec3 twoThree = peak.add(thinScale, 0.0D, thinScale);
		this.planes.put(calcNormal(twoThree, two, three), twoThree);

		Vec3 threeFour = peak.add(-thinScale, 0.0D, thinScale);
		this.planes.put(calcNormal(threeFour, three, four), threeFour);

		Vec3 fourOne = peak.add(-thinScale, 0.0D, -thinScale);
		this.planes.put(calcNormal(fourOne, four, one), fourOne);

		// Start with roots first, so they don't fail placement because they intersect the trunk shell first
		// 3-5 roots at the bottom
		//HollowTreeFeature.buildBranchRing(world, trunkPlacer, leavesPlacer, random, pos, radius, 1, 0, 12, 0.75D, 3, 5, 3, false, config);

		// several more taproots
		//buildBranchRing(world, trunkPlacer, leavesPlacer, random, pos, radius, 1, 2, 18, 0.9D, 3, 5, 3, false, config);

		// make a tree!

		// build the trunk
		this.buildTrunk(world, trunkPlacer, random, pos, config.scale, height, config, decorationPlacer);

		return true;
	}

	protected void buildTrunk(LevelAccessor world, BiConsumer<BlockPos, BlockState> trunkPlacer, RandomSource random, BlockPos pos, double scale, int height, VeilwoodTreeConfig config, RootPlacer decorationPlacer) {
		int radius = (int) (scale * 4.0D);
		int minY = pos.getY();
		int maxY = minY + height - 1;
		int branch = height / 2;

		Map<Direction, BlockPos> starters = new HashMap<>();
		List<BlockPos> trunk = new ArrayList<>();
		List<BlockPos> bottoms = new ArrayList<>();

		for (BlockPos blockPos : BlockPos.betweenClosed(pos.offset(radius, 0, radius), pos.offset(-radius, height + 1, -radius))) {
			if (isInside(blockPos, minY, maxY)) {
				boolean exposed = false;
				if (!isInside(blockPos.above(), minY, maxY)) {
					if (random.nextBoolean() && FeaturePlacers.placeIfValidTreePos(world, trunkPlacer, random, blockPos.above(), config.branchProvider)) {
						trunk.add(blockPos.above().mutable());
					} else {
						exposed = true;
					}
				}

				if (!isInside(blockPos.below(), minY, maxY)) {
					if (random.nextBoolean() && FeaturePlacers.placeIfValidTreePos(world, trunkPlacer, random, blockPos.below(), config.branchProvider)) {
						trunk.add(blockPos.below().mutable());
					} else {
						exposed = true;
					}
				}

				if (FeaturePlacers.placeIfValidTreePos(world, trunkPlacer, random, blockPos, !exposed ? config.trunkProvider : config.branchProvider)) {
					if (blockPos.getY() == minY) bottoms.add(blockPos.mutable());
					else trunk.add(blockPos.mutable());
					if (starters.size() < 4) {
						int diff = blockPos.getY() - minY;
						if (diff >= branch && diff <= branch + 4) {
							Direction direction = DIRECTIONS.get(random.nextInt(DIRECTIONS.size()));
							if (!starters.containsKey(direction)) starters.put(direction, blockPos.mutable());
						}
					}
				}
			}
		}

		if (trunk.isEmpty()) return;

		if (starters.isEmpty()) {
			Collections.sort(trunk);
			for (Direction direction : DIRECTIONS) {
				starters.put(direction, trunk.get(Math.min(trunk.size() / 2 + random.nextInt(4), trunk.size() - 1)));
			}
		} else {
			List<Direction> viable = new ArrayList<>();
			for (Direction direction : DIRECTIONS) if (starters.containsKey(direction)) viable.add(direction);
			for (Direction direction : DIRECTIONS) {
				if (!starters.containsKey(direction)) starters.put(direction, starters.get(viable.get(random.nextInt(viable.size()))));
			}
		}

		List<BlockPos> rootStarters = new ArrayList<>();

		int i = random.nextBoolean() ? 3 : 4;
		for (BlockPos blockPos : bottoms) {
			for (Direction direction : DIRECTIONS) {
				BlockPos relative = blockPos.relative(direction);
				if (!rootStarters.contains(relative) && bottoms.stream().noneMatch(trunks -> trunks.equals(relative) || trunks.equals(relative.relative(direction.getClockWise())) || trunks.equals(relative.relative(direction.getCounterClockWise()))) && !isInside(relative.above(), minY, maxY)) {
					if (rootStarters.contains(relative.north()) || rootStarters.contains(relative.east()) || rootStarters.contains(relative.south()) || rootStarters.contains(relative.west())) continue;
					if (FeaturePlacers.placeIfValidTreePos(world, trunkPlacer, random, relative, config.branchProvider)) {
						rootStarters.add(relative.mutable());
						i--;
					}
				}
				if (i <= 0) break;
			}
			if (i == 0) break;
		}

		for (BlockPos starter : rootStarters) {
			// root bulb
			FeaturePlacers.placeIfValidRootPos(world, decorationPlacer, random, starter.below(), config.rootsProvider);

			// roots!
			int numRoots = 2 + random.nextInt(2);
			float offset = random.nextFloat();
			for (int b = 0; b < numRoots; b++) {
				FeaturePlacers.buildRoot(world, decorationPlacer, random, starter, offset, b, config.rootsProvider);
			}
		}

		// build the crown
		starters.forEach((direction, start) -> {
			int length = config.minBranchLength + random.nextInt(config.maxBranchLength - config.minBranchLength + 1);
			BlockPos end = start.relative(direction, length).relative(Direction.UP, random.nextBoolean() ? 3 : 2);
			this.createBranch(world, trunkPlacer, length, direction, random, config, random.nextDouble() <= config.branchOffCount, config.branchOffCount - 1, new VoxelBresenhamIterator(start, end));
		});
	}

	public void createBranch(LevelAccessor world, BiConsumer<BlockPos, BlockState> trunkPlacer, int length, Direction direction, RandomSource random, VeilwoodTreeConfig config, boolean shouldBranch, double branchOffCount, VoxelBresenhamIterator iterator) {
		int distance = 0;
		int count = Mth.ceil(branchOffCount);
		boolean clockwise = false;
		boolean counterclockwise = false;
		while (iterator.hasNext()) {
			BlockPos blockPos = iterator.next();
			distance++;
			if (FeaturePlacers.placeIfValidTreePos(world, trunkPlacer, random, blockPos, config.branchProvider, state -> state.trySetValue(RotatedPillarBlock.AXIS, direction.getAxis())) && shouldBranch && iterator.hasNext() && distance > 2 + count) {
				boolean both = !clockwise && !counterclockwise && random.nextBoolean() && random.nextBoolean();
				boolean one = random.nextBoolean();

				blockPos = blockPos.relative(direction);

				if (!clockwise && (one || both || length - distance == 0)) {
					clockwise = true;
					int newLength = length - distance + random.nextInt(3);
					if (newLength > 0) {
						Direction clockWise = direction.getClockWise();
						BlockPos clock = blockPos.relative(clockWise);
						int up = 1 + random.nextInt(2 + count * 2);
						int side = 1 + random.nextInt(2) + count * 3;
						BlockPos endClock = clock.relative(Direction.UP, up).relative(direction, newLength).relative(clockWise, side);
						this.createBranch(world, trunkPlacer, newLength, direction, random, config, random.nextDouble() <= branchOffCount, branchOffCount - 1, new VoxelBresenhamIterator(clock, endClock));
					}
				}

				if (!counterclockwise && (!one || both || length - distance == 0)) {
					counterclockwise = true;
					int newLength = length - distance + random.nextInt(3);
					if (newLength > 0) {
						Direction counterWise = direction.getCounterClockWise();
						BlockPos counter = blockPos.relative(counterWise);
						int up = 1 + random.nextInt(2 + count * 2);
						int side = 1 + random.nextInt(2) + count * 3;
						BlockPos endCounter = counter.relative(Direction.UP, up).relative(direction, newLength).relative(counterWise, side);
						this.createBranch(world, trunkPlacer, newLength, direction, random, config, random.nextDouble() <= branchOffCount, branchOffCount - 1, new VoxelBresenhamIterator(counter, endCounter));
					}
				}

				if (clockwise && counterclockwise) break;
			}
		}
	}

	protected static Vec3 calcNormal(Vec3 top, Vec3 a, Vec3 b) {
		Vec3 v1 = a.subtract(top);
		Vec3 v2 = b.subtract(top);
		Vec3 surfaceNormal = v1.cross(v2);
		return surfaceNormal.normalize();
	}

	protected boolean isInside(BlockPos pos, int minY, int maxY) {
		if (pos.getY() < minY || pos.getY() > maxY) return false;
		int diff = pos.getY() - minY;
		for (Map.Entry<Vec3, Vec3> entry : this.planes.entrySet()) {
			if (pos.getCenter().subtract(entry.getValue()).dot(entry.getKey()) < -0.33D + (diff * 0.025D)) return false;
		}
		return true;
	}
}
