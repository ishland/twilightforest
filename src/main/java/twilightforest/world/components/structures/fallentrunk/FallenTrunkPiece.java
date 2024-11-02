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
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import twilightforest.TwilightForestMod;
import twilightforest.init.TFBlocks;
import twilightforest.init.TFEntities;
import twilightforest.init.TFStructurePieceTypes;
import twilightforest.world.components.structures.type.FallenTrunkStructure;

public class FallenTrunkPiece extends StructurePiece {
	public static final BlockStateProvider DEFAULT_LOG = BlockStateProvider.simple(TFBlocks.TWILIGHT_OAK_LOG.get());
	public static final Holder<EntityType<?>> DEFAULT_DUNGEON_MONSTER = TFEntities.SWARM_SPIDER;

	private static final int ERODED_LENGTH = 3;
	private static final float MOSS_CHANCE = 0.44F;
	private final BlockStateProvider log;
	private final int length;
	private final int radius;
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
	public void postProcess(@NotNull WorldGenLevel level, @NotNull StructureManager structureManager, @NotNull ChunkGenerator generator, @NotNull RandomSource random,
							@NotNull BoundingBox box, @NotNull ChunkPos chunkPos, @NotNull BlockPos pos) {
		// TODO: add trunks with size 1
		if (radius == FallenTrunkStructure.radiuses.get(1))
			generateFallenTrunk(level, random, box, pos, random.nextBoolean(), false);
		if (radius == FallenTrunkStructure.radiuses.get(2))
			generateFallenTrunk(level, RandomSource.create(pos.asLong()), box, pos, random.nextBoolean(), true);
	}

	private void generateFallenTrunk(WorldGenLevel level, RandomSource random, BoundingBox box, BlockPos pos, boolean hasHole, boolean hasSpawnerAndChests) {
		generateTrunk(level, random, box, pos, hasHole);
		if (hasSpawnerAndChests)
			generateSpawnerAndChests(level, random, box, pos);
	}

	private void generateTrunk(WorldGenLevel level, RandomSource random, BoundingBox box, BlockPos pos, boolean hasHole) {
		int hollow = radius / 2;
		int diameter = radius * 2;

		Vec3 holeConeVertex = new Vec3(radius + (random.nextFloat() - 0.5) * hollow, hollow + random.nextFloat() * hollow, random.nextFloat() * (length - 4 * ERODED_LENGTH) + 2 * ERODED_LENGTH);
		Vec3 holeConeAxis = Vec3.directionFromRotation((float) -random.triangle(50, 40), (float) (random.triangle(135, 30)));
		double holeConeAngleCos = Math.cos(random.triangle(Math.PI / 7, Math.PI / 50));

// FIXME: Delete debug prints
//		System.out.println(holeConeVertex);
//		System.out.println(holeConeAxis);
//		System.out.println(holeConeAngleCos);
//		System.out.println("--------------");

		for (int dx = 0; dx <= diameter; dx++) {
			for (int dy = 0; dy <= diameter; dy++) {
				int dist = getDist(dx, dy);
				if (dist <= radius && dist > hollow) {
					for (int dz = ERODED_LENGTH; dz < length; dz++) {
						if (dz > length - ERODED_LENGTH && random.nextBoolean()) {dz = length + 1; continue;}
						BlockPos offsetPos = pos.above(dy).east(dx).south(dz);
						if (hasHole) {
							Vec3 blockCenter = new BlockPos(dx, dy, dz).getCenter();
							Vec3 blockCenterRadiusNormalized = blockCenter.subtract(holeConeVertex).normalize();
							if (holeConeAxis.dot(blockCenterRadiusNormalized) >= holeConeAngleCos)
								continue;
						}

						this.placeLog(level, log.getState(random, offsetPos).trySetValue(RotatedPillarBlock.AXIS, Direction.Axis.Z),
							dx, dy, dz, box, random);
					}
					for (int dz = ERODED_LENGTH; dz > 0; dz--) {
						if (random.nextBoolean()) {dz = 0; continue;}
						BlockPos offsetPos = pos.above(dy).east(dx).south(dz);
						this.placeLog(level, log.getState(random, offsetPos).trySetValue(RotatedPillarBlock.AXIS, Direction.Axis.Z),
							dx, dy, dz, box, random);
					}
				}
			}
		}
// FIXME: Delete debug blocks
//		placeBlock(level, Blocks.DIAMOND_BLOCK.defaultBlockState(), (int) holeConeVertex.x, (int) holeConeVertex.y, (int) holeConeVertex.z, box);
//		placeBlock(level, Blocks.EMERALD_BLOCK.defaultBlockState(), (int) (holeConeAxis.x * 5 + holeConeVertex.x), (int) (holeConeAxis.y * 5 + holeConeVertex.y), (int) (holeConeAxis.z * 5 + holeConeVertex.z), box);
	}

	private void generateHole(WorldGenLevel level, RandomSource random, BoundingBox box, BlockPos pos) {

	}

	private void generateSpawnerAndChests(WorldGenLevel level, RandomSource random, BoundingBox box, BlockPos pos) {
		BlockPos spawnerPos = new BlockPos(radius, 2, length / 2);
		this.placeBlock(level, Blocks.SPAWNER.defaultBlockState(), spawnerPos.getX(), spawnerPos.getY(), spawnerPos.getZ(), box);
		BlockPos worldSpawnerPos = getWorldPos(spawnerPos.getX(), spawnerPos.getY(), spawnerPos.getZ());
		if (level.getBlockEntity(worldSpawnerPos) instanceof SpawnerBlockEntity spawner)
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

	private void placeLog(WorldGenLevel level, BlockState blockstate, int x, int y, int z, BoundingBox boundingbox, RandomSource random) {
		BlockState blockState = this.getBlock(level, x, y, z, boundingbox);
		if (blockState.is(BlockTags.REPLACEABLE_BY_TREES) || blockState.isEmpty() || random.nextBoolean()) {
			placeBlock(level, blockstate, x, y, z, boundingbox);
			if (random.nextFloat() <= MOSS_CHANCE && this.getBlock(level, x, y + 1, z, boundingbox).is(BlockTags.REPLACEABLE)) {
				placeBlock(level, TFBlocks.MOSS_PATCH.get().defaultBlockState(), x, y + 1, z, boundingbox);
				level.blockUpdated(getWorldPos(x, y + 1, z), TFBlocks.MOSS_PATCH.get());  // to connect moss patches
			}
		}
	}

	@NotNull
	@Override
	public Direction getOrientation() {
		return MoreObjects.firstNonNull(orientation, Direction.NORTH);  // orientation is always not null, just to remove warnings
	}
}
