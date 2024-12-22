package twilightforest.world.components.structures.fallentrunk;

import com.google.common.base.MoreObjects;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.SpawnerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.ChestType;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.StructurePieceAccessor;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;
import net.minecraft.world.level.storage.loot.LootTable;
import org.jetbrains.annotations.NotNull;
import twilightforest.TwilightForestMod;
import twilightforest.init.TFBlocks;
import twilightforest.init.TFEntities;
import twilightforest.init.TFStructurePieceTypes;
import twilightforest.world.components.structures.TerraformingPiece;
import twilightforest.world.components.structures.type.FallenTrunkStructure;

public class FallenTrunkPiece extends StructurePiece {
	public static final BlockStateProvider DEFAULT_LOG = BlockStateProvider.simple(TFBlocks.TWILIGHT_OAK_LOG.get());
	public static final Holder<EntityType<?>> DEFAULT_DUNGEON_MONSTER = TFEntities.SWARM_SPIDER;

	public static final int ERODED_LENGTH = 3;
	private static final float MOSS_CHANCE = 0.44F;
	private final BlockStateProvider log;
	final int length;
	public final int radius;
	private final ResourceKey<LootTable> chestLootTable;
	private final Holder<EntityType<?>> spawnerMonster;

	public FallenTrunkPiece(int length, int radius, BlockStateProvider log, ResourceKey<LootTable> chestLootTable, Holder<EntityType<?>> spawnerMonster, Direction orientation, BoundingBox boundingBox) {
		super(TFStructurePieceTypes.TFFallenTrunk.value(), 0, boundingBox);
		this.length = length;
		this.radius = radius;
		this.log = log;
		this.chestLootTable = chestLootTable;
		this.spawnerMonster = spawnerMonster;
		setOrientation(orientation);
	}

	public FallenTrunkPiece(StructurePieceSerializationContext context, CompoundTag tag) {
		super(TFStructurePieceTypes.TFFallenTrunk.value(), tag);
		this.length = tag.getInt("length");
		this.radius = tag.getInt("radius");

		RegistryOps<Tag> ops = RegistryOps.create(NbtOps.INSTANCE, context.registryAccess());
		log = BlockStateProvider.CODEC.parse(ops, tag.getCompound("log")).result().orElse(DEFAULT_LOG);
		chestLootTable = ResourceKey.create(Registries.LOOT_TABLE, ResourceLocation.parse(tag.getString("chest_loot_table")));
		ResourceKey<EntityType<?>> dungeonMonster = ResourceKey.create(Registries.ENTITY_TYPE, ResourceLocation.parse(tag.getString("spawner_monster")));
		this.spawnerMonster = context.registryAccess().registry(Registries.ENTITY_TYPE)
			.<Holder<EntityType<?>>>flatMap(reg -> reg.getHolder(dungeonMonster))
			.orElse(DEFAULT_DUNGEON_MONSTER);
	}

	@Override
	protected void addAdditionalSaveData(@NotNull StructurePieceSerializationContext context, CompoundTag tag) {
		tag.putInt("length", this.length);
		tag.putInt("radius", this.radius);
		tag.put("log", BlockStateProvider.CODEC.encodeStart(NbtOps.INSTANCE, this.log).resultOrPartial(TwilightForestMod.LOGGER::error).orElseGet(CompoundTag::new));
		tag.putString("chest_loot_table", this.chestLootTable.location().toString());
		tag.putString("spawner_monster", BuiltInRegistries.ENTITY_TYPE.getKey(this.spawnerMonster.value()).toString());
	}

	@Override
	public void addChildren(@NotNull StructurePiece parent, StructurePieceAccessor list, @NotNull RandomSource rand) {
		StructurePiece terraformingPiece = new TerraformingPiece(TFStructurePieceTypes.TFFallenTrunk.value(), 0, boundingBox.inflatedBy(16));
		list.addPiece(terraformingPiece);
	}

	@Override
	public void postProcess(@NotNull WorldGenLevel level, @NotNull StructureManager structureManager, @NotNull ChunkGenerator generator, @NotNull RandomSource random,
							@NotNull BoundingBox box, @NotNull ChunkPos chunkPos, @NotNull BlockPos pos) {
//		placeBlockEdges(level, box, Blocks.GOLD_BLOCK.defaultBlockState());
//		placeDiamondBlockEdges(level, this.boundingBox);
		if (radius == FallenTrunkStructure.radiuses.get(0))
			generateSmallFallenTrunk(level, RandomSource.create(pos.asLong()), box, pos, random.nextBoolean());
		if (radius == FallenTrunkStructure.radiuses.get(1))
			generateFallenTrunk(level, RandomSource.create(pos.asLong()), box, pos, random.nextBoolean(), false);
		if (radius == FallenTrunkStructure.radiuses.get(2))
			generateFallenTrunk(level, RandomSource.create(pos.asLong()), box, pos, false, true);
	}

	public static void placeBlockEdges(WorldGenLevel level, BoundingBox box, BlockState state) {
		// Edges along the X-axis
		for (int x = box.minX(); x <= box.maxX(); x++) {
			// Bottom edges (y = minY)
			level.setBlock(new BlockPos(x, box.minY(), box.minZ()), state, 2);
			level.setBlock(new BlockPos(x, box.minY(), box.maxZ()), state, 2);
			// Top edges (y = maxY)
			level.setBlock(new BlockPos(x, box.maxY(), box.minZ()), state, 2);
			level.setBlock(new BlockPos(x, box.maxY(), box.maxZ()), state, 2);
		}

		// Edges along the Y-axis
		for (int y = box.minY(); y <= box.maxY(); y++) {
			// Vertical edges at minX
			level.setBlock(new BlockPos(box.minX(), y, box.minZ()), state, 2);
			level.setBlock(new BlockPos(box.minX(), y, box.maxZ()), state, 2);
			// Vertical edges at maxX
			level.setBlock(new BlockPos(box.maxX(), y, box.minZ()), state, 2);
			level.setBlock(new BlockPos(box.maxX(), y, box.maxZ()), state, 2);
		}

		// Edges along the Z-axis
		for (int z = box.minZ(); z <= box.maxZ(); z++) {
			// Bottom edges (y = minY)
			level.setBlock(new BlockPos(box.minX(), box.minY(), z), state, 2);
			level.setBlock(new BlockPos(box.maxX(), box.minY(), z), state, 2);
			// Top edges (y = maxY)
			level.setBlock(new BlockPos(box.minX(), box.maxY(), z), state, 2);
			level.setBlock(new BlockPos(box.maxX(), box.maxY(), z), state, 2);
		}
	}

	private void generateSmallFallenTrunk(WorldGenLevel level, RandomSource random, BoundingBox box, BlockPos pos, boolean hasHole) {
		Hole hole = new Hole(this, random);
		for (int dx = 0; dx <= 3; dx++) {
			for (int dy = 0; dy <= 3; dy++) {
				if (Math.abs(dx - 1.5) + Math.abs(dy - 1.5) != 2)
					continue;

				generateTrunkRod(level, random, box, pos, dx, dy, hasHole, hole);
			}
		}
	}

	private void generateFallenTrunk(WorldGenLevel level, RandomSource random, BoundingBox box, BlockPos pos, boolean hasHole, boolean hasSpawnerAndChests) {
		generateTrunk(level, random, box, pos, hasHole);
		if (hasSpawnerAndChests)
			generateSpawnerAndChests(level, random, box);
	}

	private void generateTrunk(WorldGenLevel level, RandomSource random, BoundingBox box, BlockPos pos, boolean hasHole) {
		int hollow = radius / 2;
		int diameter = radius * 2;
		Hole hole = new Hole(this, random);

		for (int dx = 0; dx <= diameter; dx++) {
			for (int dy = 0; dy <= diameter; dy++) {
				int dist = getDist(dx, dy);
				if (dist > radius || dist <= hollow)
					continue;

				generateTrunkRod(level, random, box, pos, dx, dy, hasHole, hole);
			}
		}
	}

	private void generateTrunkRod(WorldGenLevel level, RandomSource random, BoundingBox box, BlockPos pos, int dx, int dy, boolean hasHole, Hole hole) {
		generateTrunkMainRod(level, random, box, pos, dx, dy, hasHole, hole);
		generateErodedEnds(level, random, box, pos, dx, dy, hasHole, hole);
	}

	private void generateTrunkMainRod(WorldGenLevel level, RandomSource random, BoundingBox box, BlockPos pos, int dx, int dy, boolean hasHole, Hole hole) {
		for (int dz = ERODED_LENGTH; dz < length - 1 - ERODED_LENGTH; dz++) {
			BlockPos offsetPos = pos.offset(dx, dy, dz);
			this.placeLog(level, getLogState(random, offsetPos), dx, dy, dz, box, random, hasHole, hole);
		}
	}

	private void generateErodedEnds(WorldGenLevel level, RandomSource random, BoundingBox box, BlockPos pos, int dx, int dy, boolean hasHole, Hole hole) {
		for (int dz = ERODED_LENGTH - 1; dz >= 0; dz--) {
			if (random.nextBoolean())
				break;

			BlockPos offsetPos = pos.offset(dx, dy, dz);
			this.placeLog(level, getLogState(random, offsetPos), dx, dy, dz, box, random, hasHole, hole);
		}

		for (int dz = length - 1 - ERODED_LENGTH; dz < length - 1; dz++) {
			if (random.nextBoolean())
				break;

			BlockPos offsetPos = pos.offset(dx, dy, dz);
			this.placeLog(level, getLogState(random, offsetPos), dx, dy, dz, box, random, hasHole, hole);
		}
	}


	private void generateSpawnerAndChests(WorldGenLevel level, RandomSource random, BoundingBox box) {
		BlockPos spawnerPos = new BlockPos(radius, 2, (length - 1) / 2);
		this.placeBlock(level, Blocks.SPAWNER.defaultBlockState(), spawnerPos.getX(), spawnerPos.getY(), spawnerPos.getZ(), box);
		BlockPos worldSpawnerPos = getWorldPos(spawnerPos.getX(), spawnerPos.getY(), spawnerPos.getZ());
		if (box.isInside(worldSpawnerPos.getX(), worldSpawnerPos.getY(), worldSpawnerPos.getZ()) && level.getBlockEntity(worldSpawnerPos) instanceof SpawnerBlockEntity spawner)
			spawner.setEntityId(spawnerMonster.value(), random);

		Direction orientation = this.getOrientation().getClockWise();
		if (this.mirror == Mirror.LEFT_RIGHT)
			orientation = orientation.getOpposite();
		BlockState singleChestState = Blocks.CHEST.defaultBlockState().setValue(ChestBlock.FACING, orientation);
		BlockPos singleChestPos = getWorldPos(spawnerPos.getX() - 1, spawnerPos.getY(), spawnerPos.getZ() + 2);
		this.createChest(level, box, random, singleChestPos, chestLootTable, singleChestState);

		ChestType chestType = mirror != Mirror.NONE ? ChestType.RIGHT : ChestType.LEFT;
		BlockState doubleChest0 = Blocks.CHEST.defaultBlockState().setValue(ChestBlock.FACING, orientation.getOpposite()).setValue(ChestBlock.TYPE, chestType);
		BlockPos doubleChestPos0 = getWorldPos(spawnerPos.getX() + 2, spawnerPos.getY() + 1, spawnerPos.getZ() - 3);
		BlockState doubleChest1 = Blocks.CHEST.defaultBlockState().setValue(ChestBlock.FACING, orientation.getOpposite()).setValue(ChestBlock.TYPE, chestType.getOpposite());
		BlockPos doubleChestPos1 = getWorldPos(spawnerPos.getX() + 2, spawnerPos.getY() + 1, spawnerPos.getZ() - 2);
		this.createChest(level, box, random, doubleChestPos0, chestLootTable, doubleChest0);
		this.createChest(level, box, random, doubleChestPos1, chestLootTable, doubleChest1);
	}

	private int getDist(int dx, int dy) {
		int ax = Math.abs(dx - this.radius);
		int az = Math.abs(dy - this.radius);
		return (int) (Math.max(ax, az) + (Math.min(ax, az) * 0.5));
	}

	private BlockState getLogState(RandomSource random, BlockPos pos) {
		return log.getState(random, pos).trySetValue(RotatedPillarBlock.AXIS, Direction.Axis.Z);
	}

	private void placeLog(WorldGenLevel level, BlockState blockstate, int x, int y, int z, BoundingBox boundingbox, RandomSource random, boolean hasHole, Hole hole) {
		int holeCoordinates = convertXYtoLength(x, y);
		if (hasHole && z > ERODED_LENGTH && z < length - 1 - ERODED_LENGTH - 1 && hole.isInHole(holeCoordinates, z - ERODED_LENGTH - 1)) {
			return;
		}
		BlockState blockState = this.getBlock(level, x, y, z, boundingbox);
		if (blockState.is(BlockTags.REPLACEABLE_BY_TREES) || blockState.is(BlockTags.FLOWERS) || blockState.isEmpty() || random.nextBoolean()) {
			placeBlock(level, blockstate, x, y, z, boundingbox);
			if (random.nextFloat() <= MOSS_CHANCE && this.getBlock(level, x, y + 1, z, boundingbox).is(BlockTags.REPLACEABLE)) {
				placeBlock(level, TFBlocks.MOSS_PATCH.get().defaultBlockState(), x, y + 1, z, boundingbox);
				level.blockUpdated(getWorldPos(x, y + 1, z), TFBlocks.MOSS_PATCH.get());  // to connect moss patches
				level.getChunk(getWorldPos(x, y + 1, z)).markPosForPostprocessing(getWorldPos(x, y + 1, z));
			}
		}
	}

	@NotNull
	@Override
	public Direction getOrientation() {
		return MoreObjects.firstNonNull(orientation, Direction.NORTH);  // orientation is always not null, just to remove warnings
	}

	private int getSideLength() {
		return radius == 1 ? 2 : this.radius * 2 - 1;
	}

	private int convertXYtoLength(int x, int y) {
		int sideLength = getSideLength();
		int length = 0;

		if (x == 0) {
			length = y;
		} else if (y == sideLength + 1) {
			length = sideLength + x;
		} else if (x == sideLength + 1) {
			length = 3 * sideLength - y + 1;
		} else if (y == 0) {
			length = 4 * sideLength - x;
		}

		return length - 1;
	}

	private static class Hole {
		private final int X_MIN_SIZE;
		private final int X_MAX_SIZE;
		private final int Y_MIN_SIZE;
		private final int Y_MAX_SIZE;
		private final boolean[][] hole;
		private Hole(FallenTrunkPiece piece, RandomSource random) {
			this.X_MIN_SIZE = piece.length / 6;
			this.X_MAX_SIZE = piece.length / 4 + 1;
			this.Y_MIN_SIZE = piece.getSideLength();
			this.Y_MAX_SIZE = piece.getSideLength() * 2;
			this.hole = buildHoleArray(piece.length, piece.getSideLength() * 4, random, piece);
		}

		private boolean[][] buildHoleArray(int length, int height, RandomSource random, FallenTrunkPiece piece) {
			int arrayLength = length - (ERODED_LENGTH + 1) * 2;
			boolean[][] arr = new boolean[height][arrayLength];


			int xSize;
			int ySize = random.nextInt(Y_MIN_SIZE, Y_MAX_SIZE);

			int yOffset = random.nextInt(1, Y_MAX_SIZE - ySize + 1);

			int previousX1 = Integer.MIN_VALUE, previousX2 = Integer.MAX_VALUE;
			for (int dy = yOffset; dy < yOffset + ySize; dy++) {
				xSize = random.nextInt(X_MIN_SIZE, X_MAX_SIZE + 1);
				int x1 = random.nextInt(arrayLength - xSize - 1);
				int x2 = x1 + xSize - 1;

				int tries = 0;
				while (tries < 10000 && (x1 == previousX1 || x2 == previousX2 && dy % Y_MIN_SIZE != 0) || x2 < previousX1 || x1 > previousX2
					|| (float) (Math.min(x2, previousX2) - Math.max(previousX1, x1) + 1) / (Math.max(x2, previousX2) - Math.min(x1, previousX1) + 1) < 1 / 3f) {
					xSize = random.nextInt(X_MIN_SIZE, X_MAX_SIZE + 1);
					x1 = random.nextInt(Math.max(0, previousX1 - xSize), Math.min(arrayLength - xSize - 1, previousX2 + xSize));
					x2 = x1 + xSize - 1;
					tries++;
				}

				if (tries >= 10000)
					TwilightForestMod.LOGGER.error("Too many tries during generation! Please contact TF devs with seed and {}", piece.boundingBox.getCenter().toString());

				previousX1 = x1;
				previousX2 = x2;

				for (int x = x1; x <= x2; x++) {
					arr[dy][x] = true;
				}
			}

//			printHole(arr);
			return arr;
		}

		private boolean isInHole(int x, int y) {
			return hole[x][y];
		}

		private static void printHole(boolean[][] hole) {
			System.out.println("\n\n\n");
			for (boolean[] row : hole) {
				for (boolean cell : row) {
					System.out.print(cell ? "0" : "1");
				}
				System.out.println();
			}
		}
	}
}
