package twilightforest.world.components.structures.lichtowerrevamp;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.FrontAndTop;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.levelgen.structure.*;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePiecesBuilder;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import net.neoforged.neoforge.common.world.PieceBeardifierModifier;
import org.jetbrains.annotations.Nullable;
import twilightforest.TwilightForestMod;
import twilightforest.init.TFStructurePieceTypes;
import twilightforest.util.BoundingBoxUtils;
import twilightforest.util.jigsaw.JigsawPlaceContext;
import twilightforest.util.jigsaw.JigsawRecord;
import twilightforest.world.components.processors.MetaBlockProcessor;
import twilightforest.world.components.structures.TwilightJigsawPiece;
import twilightforest.world.components.structures.util.SortablePiece;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public class LichPerimeterFence extends TwilightJigsawPiece implements PieceBeardifierModifier, SortablePiece {
	public LichPerimeterFence(StructurePieceSerializationContext ctx, CompoundTag compoundTag) {
		super(TFStructurePieceTypes.LICH_PERIMETER_FENCE.value(), compoundTag, ctx, readSettings(compoundTag));

		this.placeSettings.addProcessor(MetaBlockProcessor.INSTANCE);
	}

	public LichPerimeterFence(StructureTemplateManager structureManager, JigsawPlaceContext jigsawContext, ResourceLocation templateId) {
		super(TFStructurePieceTypes.LICH_PERIMETER_FENCE.value(), 0, structureManager, templateId, jigsawContext);

		this.placeSettings.addProcessor(MetaBlockProcessor.INSTANCE);
	}

	@Override
	public BoundingBox getBeardifierBox() {
		return this.boundingBox;
	}

	@Override
	public TerrainAdjustment getTerrainAdjustment() {
		return TerrainAdjustment.BEARD_BOX;
	}

	@Override
	public int getGroundLevelDelta() {
		return 1;
	}

	public BlockPos bottomCenter() {
		return BoundingBoxUtils.bottomCenterOf(this.boundingBox);
	}

	@Override
	protected void processJigsaw(StructurePiece parent, StructurePieceAccessor pieceAccessor, RandomSource random, JigsawRecord connection, int jigsawIndex) {
	}

	public List<JigsawRecord> getLeftJunctions() {
		return this.matchSpareJigsaws(r -> "twilightforest:lich_tower/fence_edge_left".equals(r.name()));
	}

	public List<JigsawRecord> getRightJunctions() {
		return this.matchSpareJigsaws(r -> "twilightforest:lich_tower/fence_edge_right".equals(r.name()));
	}

	public static void generateFence(StructurePiece startingPiece, Structure.GenerationContext context, StructurePiecesBuilder structurePiecesBuilder, StructureTemplateManager structureManager, WorldgenRandom random, Direction direction, BlockPos fenceCenter) {
		LichPerimeterFence frontFence = startPerimeterFence(startingPiece, context, structurePiecesBuilder, structureManager, random, direction, fenceCenter);
		if (frontFence == null) return;

		Optional<StructurePiece> towerBase = structurePiecesBuilder.pieces.stream().filter(piece -> piece instanceof LichTowerBase).findFirst();
		if (towerBase.isEmpty() || !(towerBase.get() instanceof LichTowerBase base)) return;

		BlockPos baseBottomCenter = BoundingBoxUtils.bottomCenterOf(base.getBoundingBox());
		Direction sourceJigsawFront = base.getSourceJigsaw().orientation().front();

		BoundingBox leftDest = getClosestTrimOnGround(structurePiecesBuilder.pieces, baseBottomCenter.relative(sourceJigsawFront.getClockWise(), 64));
		BoundingBox rightDest = getClosestTrimOnGround(structurePiecesBuilder.pieces, baseBottomCenter.relative(sourceJigsawFront.getCounterClockWise(), 64));

		if (leftDest == null || rightDest == null) return;

		generatePerimeter(frontFence, structureManager, structurePiecesBuilder, random, context, leftDest.inflatedBy(-1), rightDest.inflatedBy(-1));
	}

	@Nullable
	private static BoundingBox getClosestTrimOnGround(List<StructurePiece> pieces, BlockPos pos) {
		LichTowerWingBeard closestPiece = null;
		float minDistSq = Float.MAX_VALUE;

		for (StructurePiece piece : pieces) {
			if (!(piece instanceof LichTowerWingBeard beard) || !beard.isTrim())
				continue;

			BlockPos bottomCenter = BoundingBoxUtils.bottomCenterOf(piece.getBoundingBox());
			float distSqr = horizontalDist(bottomCenter, pos);
			if (distSqr < minDistSq) {
				minDistSq = distSqr;
				closestPiece = beard;
			}
		}

		return closestPiece == null ? null : closestPiece.getBoundingBox();
	}

	private static float horizontalDist(BlockPos first, BlockPos second) {
		int dX = second.getX() - first.getX();
		int dZ = second.getZ() - first.getZ();
		return Mth.sqrt(dX * dX + dZ * dZ);
	}

	// Provides the first fence piece, that can be used for calling generatePerimeter()
	@Nullable
	public static LichPerimeterFence startPerimeterFence(StructurePiece vestibule, Structure.GenerationContext context, StructurePiecesBuilder structurePiecesBuilder, StructureTemplateManager structureManager, WorldgenRandom random, Direction direction, BlockPos fenceCenter) {
		FrontAndTop orientation = FrontAndTop.fromFrontAndTop(Direction.UP, direction);
		int baseY = context.chunkGenerator().getBaseHeight(fenceCenter.getX(), fenceCenter.getZ(), Heightmap.Types.WORLD_SURFACE_WG, context.heightAccessor(), context.randomState());

		JigsawPlaceContext placeableJunction = JigsawPlaceContext.pickPlaceableJunction(fenceCenter.atY(baseY - 2), BlockPos.ZERO, orientation, structureManager, TwilightForestMod.prefix("lich_tower/outer_fence_7"), "twilightforest:lich_tower/fence_source", random);

		if (placeableJunction == null) return null;

		LichPerimeterFence fenceStarter = new LichPerimeterFence(structureManager, placeableJunction, TwilightForestMod.prefix("lich_tower/outer_fence_7"));
		structurePiecesBuilder.addPiece(fenceStarter);
		fenceStarter.addChildren(vestibule, structurePiecesBuilder, random);

		return fenceStarter;
	}

	// Bifurcated fence generation, using frontFence as the starting piece
	public static void generatePerimeter(LichPerimeterFence frontFence, StructureTemplateManager structureManager, StructurePiecesBuilder structurePiecesBuilder, WorldgenRandom random, Structure.GenerationContext context, BoundingBox leftDest, BoundingBox rightDest) {
		ResourceLocation fullFenceId = TwilightForestMod.prefix("lich_tower/outer_fence_7");

		generateSidedPerimeter(frontFence, structureManager, structurePiecesBuilder, random, context, fullFenceId, leftDest, LichPerimeterFence::getLeftJunctions, Rotation.CLOCKWISE_90);
		generateSidedPerimeter(frontFence, structureManager, structurePiecesBuilder, random, context, fullFenceId, rightDest, LichPerimeterFence::getRightJunctions, Rotation.COUNTERCLOCKWISE_90);
	}

	private static void generateSidedPerimeter(LichPerimeterFence frontFence, StructureTemplateManager structureManager, StructurePiecesBuilder structurePiecesBuilder, WorldgenRandom random, Structure.GenerationContext context, ResourceLocation fullFenceId, BoundingBox leftDest, Function<LichPerimeterFence, List<JigsawRecord>> leftJunctionGetter, Rotation rotation) {
		LichPerimeterFence left = nextFence(frontFence, structureManager, structurePiecesBuilder, random, leftJunctionGetter.apply(frontFence), Rotation.NONE, context, fullFenceId);
		if (left == null) return;

		left = generateUntilNearDest(structureManager, structurePiecesBuilder, random, context, leftDest, 4, left, rotation, leftJunctionGetter, fullFenceId);
		List<JigsawRecord> leftJunctions = leftJunctionGetter.apply(left);
		if (left == null || leftJunctions.isEmpty()) return;

		// Generate until collision
		JigsawRecord first = leftJunctions.getFirst();
		BlockPos fencePostPos = left.templatePosition.offset(first.pos());
		// Since the fencepost is directly in front of one of the box's faces; this should the same as a regular distance function. If not, then a deeper issue has happened inside generateUntilNearDest()
		for (int distance = BoundingBoxUtils.manhattanDistance(leftDest, fencePostPos); distance > 2;) {
			int stepSize = Math.min(distance, 7);
			distance -= stepSize;

			left = nextFence(left, structureManager, structurePiecesBuilder, random, leftJunctionGetter.apply(left), rotation, context, TwilightForestMod.prefix("lich_tower/outer_fence_" + stepSize));
			if (left == null) return;
			rotation = Rotation.NONE;
		}
	}

	@Deprecated(forRemoval = true)
	public static void generatePerimeter2(LichPerimeterFence frontFence, StructureTemplateManager structureManager, StructurePiecesBuilder structurePiecesBuilder, WorldgenRandom random, Structure.GenerationContext context, BoundingBox leftDest, BoundingBox rightDest) {
		ResourceLocation fullFenceId = TwilightForestMod.prefix("lich_tower/outer_fence_7");

		generateSidedPerimeter(frontFence, structureManager, structurePiecesBuilder, random, context, fullFenceId, leftDest, LichPerimeterFence::getLeftJunctions, Rotation.CLOCKWISE_90);

		// TODO deduplicate below into above

		Function<LichPerimeterFence, List<JigsawRecord>> rightJunctionGetter = LichPerimeterFence::getRightJunctions;
		LichPerimeterFence right = nextFence(frontFence, structureManager, structurePiecesBuilder, random, rightJunctionGetter.apply(frontFence), Rotation.NONE, context, fullFenceId);
		if (right == null) return;
		right = generateUntilNearDest(structureManager, structurePiecesBuilder, random, context, rightDest, 4, right, Rotation.COUNTERCLOCKWISE_90, LichPerimeterFence::getRightJunctions, fullFenceId);
	}

	/**
	 * Generates fences from the starting piece up until the furthest fencepost sits directly in front of the destination sidetower's box
	 */
	@Nullable
	private static LichPerimeterFence generateUntilNearDest(StructureTemplateManager structureManager, StructurePiecesBuilder structurePiecesBuilder, WorldgenRandom random, Structure.GenerationContext context, BoundingBox destBox, int turnAtIndex, LichPerimeterFence fence, Rotation turn, Function<LichPerimeterFence, List<JigsawRecord>> junctionGetter, ResourceLocation templateId) {
		for (int idx = 0; idx < 16; idx++) {
			boolean makeTurn = idx == turnAtIndex;
			boolean marchTowardsDest = idx > turnAtIndex;

			if (fence == null)
				break; // This shouldn't happen ever

			List<JigsawRecord> junctions = junctionGetter.apply(fence);

			if (junctions.isEmpty())
				break; // This shouldn't happen either

			if (marchTowardsDest) {
				// Tests if the current spare fencepost aligns with a sidetower
				JigsawRecord first = junctions.getFirst();
				BlockPos checkPos = fence.templatePosition.offset(first.pos());
				BlockPos destPos = BoundingBoxUtils.clampedInside(destBox, checkPos);

				BlockPos directionOffset = destPos.subtract(checkPos);
				// Rotate to test for orthogonality via dot product (to check if dot() == 0)
				var targetDirecton = turn.rotate(first.orientation().top()).getNormal();

				// 0 from (horizontal) dot product means orthogonal, can approach from a right-angle turn now
				if (directionOffset.getX() * targetDirecton.getX() + directionOffset.getZ() * targetDirecton.getZ() == 0)
					break; // The spare fence-post has aligned with the grounded sidetower, break loop
			}

			fence = nextFence(fence, structureManager, structurePiecesBuilder, random, junctions, makeTurn ? turn : Rotation.NONE, context, templateId);
		}

		return fence;
	}

	@Nullable
	public static LichPerimeterFence nextFence(LichPerimeterFence parentFence, StructureTemplateManager structureManager, StructurePiecesBuilder structurePiecesBuilder, WorldgenRandom random, List<JigsawRecord> junctions, Rotation rotation, Structure.GenerationContext context, ResourceLocation templateId) {
		if (junctions.isEmpty()) return null;

		JigsawRecord junction = junctions.getFirst();
		FrontAndTop orientation = junction.orientation();
		FrontAndTop connectOrientation = FrontAndTop.fromFrontAndTop(orientation.front().getOpposite(), rotation.rotate(orientation.top()));
		BlockPos bottomCenter = parentFence.bottomCenter();

		int baseY = context.chunkGenerator().getBaseHeight(bottomCenter.getX(), bottomCenter.getZ(), Heightmap.Types.WORLD_SURFACE_WG, context.heightAccessor(), context.randomState());
		BlockPos parentPos = parentFence.templatePosition().above(baseY - 2 - parentFence.templatePosition().getY());

		JigsawPlaceContext placeContext = JigsawPlaceContext.pickPlaceableJunction(parentPos, junction.pos(), connectOrientation, structureManager, templateId, junction.target(), random);

		if (placeContext == null) return null;

		LichPerimeterFence nextFence = new LichPerimeterFence(structureManager, placeContext, templateId);
		structurePiecesBuilder.addPiece(nextFence);
		nextFence.addChildren(parentFence, structurePiecesBuilder, random);

		return nextFence;
	}

	@Override
	public int getSortKey() {
		// Make higher-ups generate later so the lower fences' slabs don't replace full blocks
		return this.boundingBox.maxY();
	}
}
