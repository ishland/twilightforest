package twilightforest.world.components.structures.type;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.SectionPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.RegistryFixedCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.RandomSource;
import net.minecraft.util.random.WeightedRandomList;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.structure.*;
import net.minecraft.world.level.storage.loot.LootTable;
import twilightforest.init.TFBlocks;
import twilightforest.init.TFEntities;
import twilightforest.init.TFStructureTypes;
import twilightforest.loot.TFLootTables;
import twilightforest.util.WorldUtil;
import twilightforest.world.components.structures.fallentrunk.FallenTrunkPiece;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class FallenTrunkStructure extends Structure {
	public static final MapCodec<FallenTrunkStructure> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		Structure.settingsCodec(instance),
		IntProvider.codec(8, 32).fieldOf("length").forGetter(s -> s.length),
		BlockStateProvider.CODEC.fieldOf("log").forGetter(s -> s.log),
		ResourceKey.codec(Registries.LOOT_TABLE).fieldOf("chest_loot_table").forGetter(s -> s.chestLootTable),
		RegistryFixedCodec.create(Registries.ENTITY_TYPE).fieldOf("spawner_monster").forGetter(s -> s.spawnerMonster)
	).apply(instance, FallenTrunkStructure::new));

	private final IntProvider length;
	private final BlockStateProvider log;
	private final List<Integer> radiuses = List.of(1, 2, 4);
	private final ResourceKey<LootTable> chestLootTable;
	private final Holder<EntityType<?>> spawnerMonster;

	protected FallenTrunkStructure(StructureSettings settings, IntProvider length, BlockStateProvider log, ResourceKey<LootTable> chestLootTable, Holder<EntityType<?>> spawnerMonster) {
		super(settings);
		this.length = length;
		this.log = log;
		this.chestLootTable = chestLootTable;
		this.spawnerMonster = spawnerMonster;
	}

	@Override
	public Optional<GenerationStub> findGenerationPoint(GenerationContext context) {
		ChunkPos chunkPos = context.chunkPos();

		RandomSource random = RandomSource.create(context.seed() + chunkPos.x * 25117L + chunkPos.z * 151121L);

		int x = SectionPos.sectionToBlockCoord(chunkPos.x, random.nextInt(16));
		int z = SectionPos.sectionToBlockCoord(chunkPos.z, random.nextInt(16));
		int seaFloorY = context.chunkGenerator().getFirstOccupiedHeight(x, z, Heightmap.Types.OCEAN_FLOOR_WG, context.heightAccessor(), context.randomState());
		int worldY = context.chunkGenerator().getFirstOccupiedHeight(x, z, Heightmap.Types.WORLD_SURFACE_WG, context.heightAccessor(), context.randomState());

		int length = this.length.sample(random);

		if (length < 16 || (seaFloorY < worldY))
			return Optional.empty();
		if (!this.getModifiedStructureSettings().biomes().contains(context.chunkGenerator().getBiomeSource().getNoiseBiome(x >> 2, worldY >> 2, z >> 2, context.randomState().sampler())))
			return Optional.empty();


		int radius = WorldUtil.getRandomElement(radiuses, random);

		BoundingBox boundingBox = new BoundingBox(x - (length + 1), worldY, z - (length + 1), x + length + 1, worldY + radius * 2 + 1, z + length + 1);

		return Optional.of(new GenerationStub(new BlockPos(x, worldY, z), structurePiecesBuilder -> {
			StructurePiece piece = new FallenTrunkPiece(length, radius, log, chestLootTable, spawnerMonster, boundingBox);
			structurePiecesBuilder.addPiece(piece);
		}));
	}

	@Override
	public StructureType<?> type() {
		return TFStructureTypes.FALLEN_TRUNK.get();
	}

	public static FallenTrunkStructure buildStructureConfig(HolderSet<Biome> biomes) {
		return new FallenTrunkStructure(
			new Structure.StructureSettings(
				biomes,
				Arrays.stream(MobCategory.values()).collect(Collectors.toMap(category -> category, category -> new StructureSpawnOverride(StructureSpawnOverride.BoundingBoxType.STRUCTURE, WeightedRandomList.create()))), // Landmarks have Controlled Mob spawning
				GenerationStep.Decoration.SURFACE_STRUCTURES,
				TerrainAdjustment.NONE
			),
			UniformInt.of(10, 20), BlockStateProvider.simple(TFBlocks.TWILIGHT_OAK_LOG.get()), TFLootTables.TREE_CACHE, TFEntities.SWARM_SPIDER
		);
	}
}
