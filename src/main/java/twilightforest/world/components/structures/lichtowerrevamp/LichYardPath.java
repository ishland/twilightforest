package twilightforest.world.components.structures.lichtowerrevamp;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.FrontAndTop;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.levelgen.structure.*;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePiecesBuilder;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import net.neoforged.neoforge.common.world.PieceBeardifierModifier;
import twilightforest.init.TFStructurePieceTypes;
import twilightforest.util.BoundingBoxUtils;
import twilightforest.util.jigsaw.JigsawPlaceContext;
import twilightforest.util.jigsaw.JigsawRecord;

import java.util.ArrayList;
import java.util.List;

public class LichYardPath extends StructurePiece implements PieceBeardifierModifier {
	private final int edgeFeatheringRange;
	private final Direction.Axis placeGraveAxis;
	private final StructureTemplateManager structureManager;

	public LichYardPath(BoundingBox boundingBox, int edgeFeatheringRange, Direction.Axis placeGraveAxis, StructureTemplateManager structureManager) {
		super(TFStructurePieceTypes.LICH_YARD_PATH.value(), 0, boundingBox);

		this.edgeFeatheringRange = edgeFeatheringRange;
		this.placeGraveAxis = placeGraveAxis;
		this.structureManager = structureManager;
	}

	public LichYardPath(StructurePieceSerializationContext ctx, CompoundTag tag) {
		super(TFStructurePieceTypes.LICH_YARD_PATH.value(), tag);

		this.edgeFeatheringRange = tag.getInt("feather");
		this.placeGraveAxis = tag.contains("axis") ? Direction.Axis.values()[tag.getInt("axis")] : Direction.Axis.Y;
		this.structureManager = ctx.structureTemplateManager();
	}

	@Override
	protected void addAdditionalSaveData(StructurePieceSerializationContext context, CompoundTag tag) {
		tag.putInt("feather", this.edgeFeatheringRange);
		tag.putInt("axis", this.placeGraveAxis.ordinal());
	}

	@Override
	public void postProcess(WorldGenLevel level, StructureManager structureManager, ChunkGenerator chunkGen, RandomSource random, BoundingBox chunkBounds, ChunkPos chunkPos, BlockPos structureCenterPos) {
		BoundingBox boxIntersection = BoundingBoxUtils.getIntersectionOfSBBs(this.boundingBox, chunkBounds);

		if (boxIntersection == null)
			return;

		for (int z = boxIntersection.minZ(); z <= boxIntersection.maxZ(); z++) {
			for (int x = boxIntersection.minX(); x <= boxIntersection.maxX(); x++) {
				int y = level.getHeight(Heightmap.Types.WORLD_SURFACE_WG, x, z) - 1;

				int xBorderDist = Math.min(x - this.boundingBox.minX(), this.boundingBox.maxX() - x);
				int zBorderDist = Math.min(z - this.boundingBox.minZ(), this.boundingBox.maxZ() - z);
				int borderDist = Math.min(xBorderDist, zBorderDist);

				float featherLevel = borderDist > this.edgeFeatheringRange ? 1f : (float) borderDist / this.edgeFeatheringRange;

				float f = random.nextFloat();
				if (f * f > featherLevel) continue;

				BlockPos placeAt = new BlockPos(x, y, z);
				level.setBlock(placeAt, Blocks.DIRT_PATH.defaultBlockState(), Block.UPDATE_ALL);
				// Remove the darned plants
				level.setBlock(placeAt.above(), Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL);
				level.setBlock(placeAt.above(2), Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL);
			}
		}
	}

	@Override
	public BoundingBox getBeardifierBox() {
		return this.boundingBox;
	}

	@Override
	public TerrainAdjustment getTerrainAdjustment() {
		return TerrainAdjustment.NONE;
	}

	@Override
	public int getGroundLevelDelta() {
		return 0;
	}

	public static void beginYard(LichTowerFoyer foyerPiece, Structure.GenerationContext context, StructurePiecesBuilder structurePiecesBuilder) {
		WorldgenRandom random = context.random();
		StructureTemplateManager structureManager = context.structureTemplateManager();

		JigsawRecord path = foyerPiece.matchSpareJigsaws(r -> "twilightforest:lich_tower/path".equals(r.target())).getFirst();
		if (path == null) return;

		int pathLength = random.nextInt(24, 32);
		Direction direction = foyerPiece.getRotation().rotate(Direction.SOUTH);

		BlockPos generatePos = foyerPiece.templatePosition().offset(path.pos());
		BlockPos fenceCenter = generatePos.relative(direction, pathLength);
		LichPerimeterFence.generateFence(foyerPiece, context, structurePiecesBuilder, structureManager, random, direction, fenceCenter);

		BlockPos nearVestibule = generatePos.relative(direction, 2).above(4);
		BlockPos nearFence = fenceCenter.relative(direction.getOpposite(), 4).below(4);
		generateYard(foyerPiece, structurePiecesBuilder, nearVestibule, nearFence, random, direction, context);
	}

	private static void generateYard(LichTowerFoyer foyerPiece, StructurePiecesBuilder structurePiecesBuilder, BlockPos nearVestibule, BlockPos nearFence, WorldgenRandom random, Direction dirFromVestibule, Structure.GenerationContext context) {
		List<LichYardPath> pieces = new ArrayList<>(); // Add all pieces to a list instead of immediately adding children, so that paths can generate before graves check for overlap

		// First path, from the vestibule
		BoundingBox firstPathBox = BoundingBoxUtils.wrappedCoordinates(3, nearVestibule, nearFence);

		LichYardPath lichYardPath = new LichYardPath(firstPathBox, 3, Direction.Axis.Y, context.structureTemplateManager());
		structurePiecesBuilder.addPiece(lichYardPath);
		pieces.add(lichYardPath);

		// Second path, crossing the path from the vestibule
		BlockPos randomPos = lerpBlockPos(Mth.lerp(random.nextFloat(), 0.2f, 0.8f), nearVestibule, nearFence);
		int crossPathSpan = 24;
		BlockPos pathLeft = randomPos.relative(dirFromVestibule.getClockWise(), crossPathSpan);
		BlockPos pathRight = randomPos.relative(dirFromVestibule.getCounterClockWise(), crossPathSpan);

		BoundingBox crossPathBox = BoundingBoxUtils.wrappedCoordinates(2, pathLeft, pathRight);

		LichYardPath crossPath = new LichYardPath(crossPathBox, 2, dirFromVestibule.getClockWise().getAxis(), context.structureTemplateManager());
		structurePiecesBuilder.addPiece(crossPath);
		pieces.add(crossPath);

		// Last two paths, to the sides of the vestibule
		pieces.add(putSidePath(structurePiecesBuilder, nearVestibule, dirFromVestibule, dirFromVestibule.getClockWise(), pathLeft, crossPathSpan, context.structureTemplateManager()));
		pieces.add(putSidePath(structurePiecesBuilder, nearVestibule, dirFromVestibule, dirFromVestibule.getCounterClockWise(), pathRight, crossPathSpan, context.structureTemplateManager()));

		// Now that all paths are generated, call addChildren on each so graves are placed
		for (LichYardPath piece : pieces) {
			piece.addDecoration(foyerPiece, structurePiecesBuilder, random, context);
		}
	}

	private static LichYardPath putSidePath(StructurePiecesBuilder structurePiecesBuilder, BlockPos nearVestibule, Direction dirFromVestibule, Direction sideDirection, BlockPos pathEnd, int spread, StructureTemplateManager manager) {
		BlockPos fromVestibule = nearVestibule.relative(sideDirection, 24);

		BoundingBox pathBox = BoundingBoxUtils.wrappedCoordinates(2, pathEnd, fromVestibule.relative(dirFromVestibule.getOpposite(), spread));
		LichYardPath path = new LichYardPath(pathBox, 2, dirFromVestibule.getAxis(), manager);
		structurePiecesBuilder.addPiece(path);
		return path;
	}

	private static BlockPos lerpBlockPos(float delta, BlockPos first, BlockPos second) {
		return new BlockPos(Mth.lerpDiscrete(delta, first.getX(), second.getX()), Mth.lerpDiscrete(delta, first.getY(), second.getY()), Mth.lerpDiscrete(delta, first.getZ(), second.getZ()));
	}

	public void addDecoration(StructurePiece piece, StructurePieceAccessor pieces, RandomSource random, Structure.GenerationContext context) {
		this.addChildren(piece, pieces, random);

		if (this.placeGraveAxis == Direction.Axis.Y) return;

		for (int i = 0; i < 5; i++) {
			Direction side = Direction.fromAxisAndDirection(this.placeGraveAxis, random.nextBoolean() ? Direction.AxisDirection.NEGATIVE : Direction.AxisDirection.POSITIVE).getClockWise();

			BlockPos randomPos = BoundingBoxUtils.lerpPosInside(this.boundingBox, this.placeGraveAxis, Mth.lerp(random.nextFloat(), 0.05f, 0.95f)).relative(side, 2);

			FrontAndTop orientation = FrontAndTop.fromFrontAndTop(side, Direction.UP);
			int baseY = context.chunkGenerator().getBaseHeight(randomPos.getX(), randomPos.getZ(), Heightmap.Types.WORLD_SURFACE_WG, context.heightAccessor(), context.randomState());

			JigsawPlaceContext placeableJunction = JigsawPlaceContext.pickPlaceableJunction(randomPos.atY(baseY - 1), BlockPos.ZERO, orientation, context.structureTemplateManager(), LichTowerPieces.YARD_GRAVE, "twilightforest:lich_tower/grave", random);

			if (placeableJunction == null) continue;

			LichYardGrave grave = new LichYardGrave(context.structureTemplateManager(), placeableJunction);
			if (pieces.findCollisionPiece(grave.getBoundingBox()) == null) {
				pieces.addPiece(grave);
				grave.addChildren(piece, pieces, random);
			}
		}
	}
}
