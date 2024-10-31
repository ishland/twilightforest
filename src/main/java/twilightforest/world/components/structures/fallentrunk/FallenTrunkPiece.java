package twilightforest.world.components.structures.fallentrunk;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;
import net.minecraft.world.level.storage.loot.LootTable;
import twilightforest.TwilightForestMod;
import twilightforest.init.TFBlocks;
import twilightforest.init.TFEntities;
import twilightforest.init.TFStructurePieceTypes;
import twilightforest.world.components.structures.hollowtree.HollowTreePiece;

public class FallenTrunkPiece extends StructurePiece {
	public static BlockStateProvider DEFFAULT_LOG = BlockStateProvider.simple(TFBlocks.TWILIGHT_OAK_LOG.get());
	public static final Holder<EntityType<?>> DEFAULT_DUNGEON_MONSTER = TFEntities.SWARM_SPIDER;

	private final BlockStateProvider log;
	private final int length;
	private final int radius;
	private final ResourceKey<LootTable> chestLootTable;
	private final Holder<EntityType<?>> spawnerMonster;
	public FallenTrunkPiece(int length, int radius, BlockStateProvider log, ResourceKey<LootTable> chestLootTable, Holder<EntityType<?>> spawnerMonster, BoundingBox boundingBox) {
		super(TFStructurePieceTypes.TFFallenTrunk.value(), 0, boundingBox);
		this.length = length;
		this.radius = radius;
		this.log = log;
		this.chestLootTable = chestLootTable;
		this.spawnerMonster = spawnerMonster;
	}

	public FallenTrunkPiece(StructurePieceSerializationContext context, CompoundTag tag) {
		super(TFStructurePieceTypes.TFFallenTrunk.value(), tag);
		this.length = tag.getInt("length");
		this.radius = tag.getInt("radius");

		RegistryOps<Tag> ops = RegistryOps.create(NbtOps.INSTANCE, context.registryAccess());
		log = BlockStateProvider.CODEC.parse(ops, tag.getCompound("log")).result().orElse(HollowTreePiece.DEFAULT_LOG);
		chestLootTable = ResourceKey.create(Registries.LOOT_TABLE, ResourceLocation.parse(tag.getString("chest_loot_table")));
		ResourceKey<EntityType<?>> dungeonMonster = ResourceKey.create(Registries.ENTITY_TYPE, ResourceLocation.parse(tag.getString("spawner_monster")));
		this.spawnerMonster = context.registryAccess().registry(Registries.ENTITY_TYPE)
			.<Holder<EntityType<?>>>flatMap(reg -> reg.getHolder(dungeonMonster))
			.orElse(DEFAULT_DUNGEON_MONSTER);
	}

	@Override
	protected void addAdditionalSaveData(StructurePieceSerializationContext context, CompoundTag tag) {
		tag.putInt("length", this.length);
		tag.putInt("radius", this.radius);
		tag.put("log", BlockStateProvider.CODEC.encodeStart(NbtOps.INSTANCE, this.log).resultOrPartial(TwilightForestMod.LOGGER::error).orElseGet(CompoundTag::new));
		tag.putString("chest_loot_table", this.chestLootTable.location().toString());
		tag.putString("spawner_monster", BuiltInRegistries.ENTITY_TYPE.getKey(this.spawnerMonster.value()).toString());
	}

	@Override
	public void postProcess(WorldGenLevel level, StructureManager structureManager, ChunkGenerator generator, RandomSource random, BoundingBox box, ChunkPos chunkPos, BlockPos pos) {
		int hollow = radius / 2;
		for (int dx = 0; dx <= 2 * this.radius; dx++) {
			for (int dy = radius == 4 ? -1 : 0; dy <= 2 * this.radius; dy++) {
				// determine how far we are from the center.
				int ax = Math.abs(dx - this.radius);
				int az = Math.abs(dy - this.radius);
				int dist = (int) (Math.max(ax, az) + (Math.min(ax, az) * 0.5));

				for (int dz = 0; dz <= this.length; dz++) {
					// fill the body of the trunk
					if (dist <= this.radius && dist > hollow) {
						level.setBlock(pos.above(dy).west(dz).north(dx), Blocks.SPONGE.defaultBlockState(), Block.UPDATE_ALL);
					}
				}
			}
		}
	}
}
