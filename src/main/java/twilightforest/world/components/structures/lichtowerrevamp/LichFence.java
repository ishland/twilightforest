package twilightforest.world.components.structures.lichtowerrevamp;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.FrontAndTop;
import net.minecraft.nbt.CompoundTag;
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

import java.util.List;

public class LichFence extends TwilightJigsawPiece implements PieceBeardifierModifier {
	public LichFence(StructurePieceSerializationContext ctx, CompoundTag compoundTag) {
		super(TFStructurePieceTypes.LICH_FENCE.value(), compoundTag, ctx, readSettings(compoundTag));

		this.placeSettings.addProcessor(MetaBlockProcessor.INSTANCE);
	}

	public LichFence(StructureTemplateManager structureManager, JigsawPlaceContext jigsawContext) {
		super(TFStructurePieceTypes.LICH_FENCE.value(), 0, structureManager, TwilightForestMod.prefix("lich_tower/outer_fence"), jigsawContext);

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

	@Nullable
	public static LichFence startPerimeterFence(StructurePiece startingPiece, Structure.GenerationContext context, StructurePiecesBuilder structurePiecesBuilder, int length, StructureTemplateManager structureManager, WorldgenRandom random, BlockPos entrancePos, Direction direction) {
		FrontAndTop orientation = FrontAndTop.fromFrontAndTop(Direction.UP, direction);
		BlockPos fenceCenter = entrancePos.relative(orientation.top(), length);
		int baseY = context.chunkGenerator().getBaseHeight(fenceCenter.getX(), fenceCenter.getZ(), Heightmap.Types.WORLD_SURFACE_WG, context.heightAccessor(), context.randomState());

		JigsawPlaceContext placeableJunction = JigsawPlaceContext.pickPlaceableJunction(fenceCenter.atY(baseY - 2), BlockPos.ZERO, orientation, structureManager, TwilightForestMod.prefix("lich_tower/outer_fence"), "twilightforest:lich_tower/fence_source", random);

		if (placeableJunction == null) return null;

		LichFence fenceStarter = new LichFence(structureManager, placeableJunction);
		structurePiecesBuilder.addPiece(fenceStarter);
		fenceStarter.addChildren(startingPiece, structurePiecesBuilder, random);

		return fenceStarter;
	}

	public static void generatePerimeter(LichFence frontFence, StructureTemplateManager structureManager, StructurePiecesBuilder structurePiecesBuilder, WorldgenRandom random, Structure.GenerationContext context) {
		LichFence left = nextFence(frontFence, structureManager, structurePiecesBuilder, random, frontFence.getLeftJunctions(), Rotation.NONE, context);
		LichFence right = nextFence(frontFence, structureManager, structurePiecesBuilder, random, frontFence.getRightJunctions(), Rotation.NONE, context);
		for (int idx = 0; idx < 15; idx++) {
			boolean makeTurn = idx == 4;
			if (left != null) {
				left = nextFence(left, structureManager, structurePiecesBuilder, random, left.getLeftJunctions(), makeTurn ? Rotation.CLOCKWISE_90 : Rotation.NONE, context);
			}
			if (right != null) {
				right = nextFence(right, structureManager, structurePiecesBuilder, random, right.getRightJunctions(), makeTurn ? Rotation.COUNTERCLOCKWISE_90 : Rotation.NONE, context);
			}
		}

		// TODO Close gaps in back by meeting fence ends with sidetowers on ground
	}

	@Nullable
	public static LichFence nextFence(LichFence parentFence, StructureTemplateManager structureManager, StructurePiecesBuilder structurePiecesBuilder, WorldgenRandom random, List<JigsawRecord> junctions, Rotation rotation, Structure.GenerationContext context) {
		if (!junctions.isEmpty()) {
			JigsawRecord junction = junctions.getFirst();
			FrontAndTop orientation = junction.orientation();
			FrontAndTop connectOrientation = FrontAndTop.fromFrontAndTop(orientation.front().getOpposite(), rotation.rotate(orientation.top()));
			BlockPos bottomCenter = parentFence.bottomCenter();

			int baseY = context.chunkGenerator().getBaseHeight(bottomCenter.getX(), bottomCenter.getZ(), Heightmap.Types.WORLD_SURFACE_WG, context.heightAccessor(), context.randomState());
			BlockPos parentPos = parentFence.templatePosition().above(baseY - 2 - parentFence.templatePosition().getY());

			JigsawPlaceContext placeContext = JigsawPlaceContext.pickPlaceableJunction(parentPos, junction.pos(), connectOrientation, structureManager, TwilightForestMod.prefix("lich_tower/outer_fence"), junction.target(), random);
			if (placeContext != null) {
				LichFence nextFence = new LichFence(structureManager, placeContext);
				structurePiecesBuilder.addPiece(nextFence);
				nextFence.addChildren(parentFence, structurePiecesBuilder, random);

				return nextFence;
			}
		}

		return null;
	}
}
