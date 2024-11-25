package twilightforest.world.components.structures.lichtowerrevamp;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.StructurePieceAccessor;
import net.minecraft.world.level.levelgen.structure.TerrainAdjustment;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import net.neoforged.neoforge.common.world.PieceBeardifierModifier;
import twilightforest.init.TFStructurePieceTypes;
import twilightforest.util.jigsaw.JigsawPlaceContext;
import twilightforest.util.jigsaw.JigsawRecord;
import twilightforest.world.components.processors.MetaBlockProcessor;
import twilightforest.world.components.structures.TwilightJigsawPiece;

public class LichYardGrave extends TwilightJigsawPiece implements PieceBeardifierModifier {
	public LichYardGrave(StructurePieceSerializationContext ctx, CompoundTag compoundTag) {
		super(TFStructurePieceTypes.LICH_YARD_GRAVE.value(), compoundTag, ctx, readSettings(compoundTag));

		this.placeSettings().addProcessor(MetaBlockProcessor.INSTANCE);
	}

	public LichYardGrave(StructureTemplateManager structureManager, JigsawPlaceContext jigsawContext, ResourceLocation templateId) {
		super(TFStructurePieceTypes.LICH_YARD_GRAVE.value(), 0, structureManager, templateId, jigsawContext);

		this.placeSettings().addProcessor(MetaBlockProcessor.INSTANCE);
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

	@Override
	protected void processJigsaw(StructurePiece parent, StructurePieceAccessor pieceAccessor, RandomSource random, JigsawRecord connection, int jigsawIndex) {
	}
}
