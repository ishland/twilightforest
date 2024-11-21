package twilightforest.world.components.structures.type;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.util.RandomSource;
import net.minecraft.util.random.WeightedRandomList;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.DensityFunctions;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.structure.*;
import net.minecraft.world.level.saveddata.maps.MapDecorationType;
import twilightforest.data.tags.BiomeTagGenerator;
import twilightforest.init.TFMapDecorations;
import twilightforest.init.TFStructureTypes;
import twilightforest.world.components.chunkgenerators.AbsoluteDifferenceFunction;
import twilightforest.world.components.structures.CustomDensitySource;
import twilightforest.world.components.structures.HedgeMazeComponent;
import twilightforest.world.components.structures.util.DecorationClearance;
import twilightforest.world.components.structures.util.LandmarkStructure;

import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;

public class HedgeMazeStructure extends LandmarkStructure implements CustomDensitySource {
	public static final MapCodec<HedgeMazeStructure> CODEC = RecordCodecBuilder.mapCodec(instance -> landmarkCodec(instance).apply(instance, HedgeMazeStructure::new));

	public HedgeMazeStructure(DecorationConfig decorationConfig, boolean centerInChunk, Optional<Holder<MapDecorationType>> structureIcon, StructureSettings structureSettings) {
		super(decorationConfig, centerInChunk, structureIcon, structureSettings);
	}

	@Override
	protected StructurePiece getFirstPiece(GenerationContext context, RandomSource random, ChunkPos chunkPos, int x, int y, int z) {
		return new HedgeMazeComponent(0, x + 1, y + 4, z + 1);
	}

	@Override
	public StructureType<?> type() {
		return TFStructureTypes.HEDGE_MAZE.get();
	}

	public static HedgeMazeStructure buildStructureConfig(BootstrapContext<Structure> context) {
		return new HedgeMazeStructure(
			new DecorationClearance.DecorationConfig(2, false, true, true),
			true, Optional.of(TFMapDecorations.HEDGE_MAZE),
			new Structure.StructureSettings(
				context.lookup(Registries.BIOME).getOrThrow(BiomeTagGenerator.VALID_HEDGE_MAZE_BIOMES),
				Arrays.stream(MobCategory.values()).collect(Collectors.toMap(category -> category, category -> new StructureSpawnOverride(StructureSpawnOverride.BoundingBoxType.STRUCTURE, WeightedRandomList.create()))), // Landmarks have Controlled Mob spawning
				GenerationStep.Decoration.SURFACE_STRUCTURES,
				TerrainAdjustment.BEARD_BOX
			)
		);
	}

	@Override
	public DensityFunction getStructureTerraformer(ChunkPos chunkPosAt, StructureStart structurePieceSource) {
		BlockPos centerPos = structurePieceSource.getBoundingBox().getCenter();
		return DensityFunctions.min(
			DensityFunctions.zero(),
			DensityFunctions.max(
				DensityFunctions.yClampedGradient(1, 6, 0, -100000),
				DensityFunctions.mul(
					DensityFunctions.constant(-1),
					DensityFunctions.add(
						DensityFunctions.yClampedGradient(12 + centerPos.getY(), -77, 26, -12),
						DensityFunctions.mul(
							DensityFunctions.constant(-0.75),
							new AbsoluteDifferenceFunction.Max(32, centerPos.getX() + 0.5F, centerPos.getZ() + 0.5F)
						)
					)
				)
			)
		);
	}
}
