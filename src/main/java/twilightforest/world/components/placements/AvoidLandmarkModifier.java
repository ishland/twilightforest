package twilightforest.world.components.placements;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.placement.PlacementContext;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;
import net.minecraft.world.level.levelgen.placement.PlacementModifierType;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import twilightforest.init.TFFeatureModifiers;
import twilightforest.util.landmarks.LandmarkUtil;
import twilightforest.util.landmarks.LegacyLandmarkPlacements;
import twilightforest.world.components.structures.util.DecorationClearance;

import java.util.Optional;
import java.util.stream.Stream;

public class AvoidLandmarkModifier extends PlacementModifier {

	public static final MapCodec<AvoidLandmarkModifier> CODEC = RecordCodecBuilder.<AvoidLandmarkModifier>mapCodec(instance -> instance.group(
		Codec.BOOL.fieldOf("occupies_surface").forGetter(o -> o.occupiesSurface),
		Codec.BOOL.fieldOf("occupies_underground").forGetter(o -> o.occupiesUnderground),
		Codec.BOOL.fieldOf("occupies_vegetation").forGetter(o -> o.occupiesVegetation),
		Codec.INT.fieldOf("additional_clearance").forGetter(o -> o.additionalClearance)
	).apply(instance, AvoidLandmarkModifier::new)).validate(AvoidLandmarkModifier::validate);

	private final boolean occupiesSurface;
	private final boolean occupiesUnderground;
	private final boolean occupiesVegetation;
	private final int additionalClearance;

	public AvoidLandmarkModifier(boolean occupiesSurface, boolean occupiesUnderground, int additionalClearance) {
		this(occupiesSurface, occupiesUnderground, false, additionalClearance);
	}

	public AvoidLandmarkModifier(boolean occupiesSurface, boolean occupiesUnderground, boolean occupiesVegetation, int additionalClearance) {
		this.occupiesSurface = occupiesSurface;
		this.occupiesUnderground = occupiesUnderground;
		this.occupiesVegetation = occupiesVegetation;
		this.additionalClearance = additionalClearance;
	}

	public static AvoidLandmarkModifier checkSurface() {
		return new AvoidLandmarkModifier(true, false, false, 0);
	}

	public static AvoidLandmarkModifier checkUnderground() {
		return new AvoidLandmarkModifier(false, true, false, 0);
	}

	public static AvoidLandmarkModifier checkBoth() {
		return new AvoidLandmarkModifier(true, true, false, 0);
	}

	public static AvoidLandmarkModifier checkVegetation() {
		return new AvoidLandmarkModifier(false, false, true, 0);
	}

	@Override
	public Stream<BlockPos> getPositions(PlacementContext worldDecoratingHelper, RandomSource random, BlockPos blockPos) {
		// Feature Center
		BlockPos.MutableBlockPos featurePos = LegacyLandmarkPlacements.getNearestCenterXZ(blockPos.getX() >> 4, blockPos.getZ() >> 4).mutable();

		Optional<StructureStart> possibleStructureStart = LandmarkUtil.locateNearestLandmarkStart(worldDecoratingHelper.getLevel(), SectionPos.blockToSectionCoord(featurePos.getX()), SectionPos.blockToSectionCoord(featurePos.getZ()));
		if (possibleStructureStart.isEmpty() || !(possibleStructureStart.get().getStructure() instanceof DecorationClearance decorationClearance))
			return Stream.of(blockPos);

		if ((!this.occupiesSurface || decorationClearance.isSurfaceDecorationsAllowed()) && (!this.occupiesUnderground || decorationClearance.isUndergroundDecoAllowed()) && (!this.occupiesVegetation || decorationClearance.isGrassDecoAllowed()))
			return Stream.of(blockPos);

		// Turn Feature Center into Feature Offset
		featurePos.set(Math.abs(featurePos.getX() - blockPos.getX()), 0, Math.abs(featurePos.getZ() - blockPos.getZ()));
		float size = decorationClearance.chunkClearanceRadius() * 16f + this.additionalClearance;

		return featurePos.getX() < size && featurePos.getZ() < size ? Stream.empty() : Stream.of(blockPos);
	}

	@Override
	public PlacementModifierType<?> type() {
		return TFFeatureModifiers.NO_STRUCTURE.get();
	}

	private static DataResult<AvoidLandmarkModifier> validate(AvoidLandmarkModifier config) {
		return config.occupiesSurface || config.occupiesUnderground || config.occupiesVegetation ? DataResult.success(config) : DataResult.error(() -> "Feature Decorator cannot have no occupancy");
	}
}
