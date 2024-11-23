package twilightforest.world.components.structures.lichtowerrevamp;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.FrontAndTop;
import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.StructurePieceAccessor;
import net.minecraft.world.level.levelgen.structure.TerrainAdjustment;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import net.neoforged.neoforge.common.world.PieceBeardifierModifier;
import org.jetbrains.annotations.Nullable;
import twilightforest.TFRegistries;
import twilightforest.entity.MagicPainting;
import twilightforest.entity.MagicPaintingVariant;
import twilightforest.init.TFEntities;
import twilightforest.init.TFStructurePieceTypes;
import twilightforest.init.custom.MagicPaintingVariants;
import twilightforest.util.jigsaw.JigsawPlaceContext;
import twilightforest.util.jigsaw.JigsawRecord;
import twilightforest.world.components.structures.SpawnIndexProvider;
import twilightforest.world.components.structures.TwilightJigsawPiece;

import java.util.Optional;

public class LichTowerMagicGallery extends TwilightJigsawPiece implements PieceBeardifierModifier, SpawnIndexProvider {
	public LichTowerMagicGallery(StructurePieceSerializationContext ctx, CompoundTag compoundTag) {
		super(TFStructurePieceTypes.LICH_MAGIC_GALLERY.value(), compoundTag, ctx, readSettings(compoundTag));

		LichTowerUtil.addDefaultProcessors(this.placeSettings.addProcessor(LichTowerUtil.ROOM_SPAWNERS));
	}

	public LichTowerMagicGallery(int genDepth, StructureTemplateManager structureManager, ResourceLocation templateLocation, JigsawPlaceContext jigsawContext) {
		super(TFStructurePieceTypes.LICH_MAGIC_GALLERY.value(), genDepth, structureManager, templateLocation, jigsawContext);

		LichTowerUtil.addDefaultProcessors(this.placeSettings.addProcessor(LichTowerUtil.ROOM_SPAWNERS));
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

	@Override
	protected void processJigsaw(StructurePiece parent, StructurePieceAccessor pieceAccessor, RandomSource random, JigsawRecord connection, int jigsawIndex) {
		if ("twilightforest:lich_tower/roof".equals(connection.target())) {
			ResourceLocation fallbackRoof = LichTowerUtil.rollGalleryRoof(random, this.boundingBox);
			FrontAndTop orientationToMatch = LichTowerWingRoom.getVerticalOrientation(connection, Direction.UP, this);
			LichTowerWingRoom.tryRoof(pieceAccessor, random, connection, fallbackRoof, orientationToMatch, true, this, this.genDepth + 1, this.structureManager);
		}
	}

	@Override
	protected void handleDataMarker(String label, BlockPos pos, WorldGenLevel level, RandomSource random, BoundingBox chunkBounds, ChunkGenerator chunkGen) {
		level.removeBlock(pos, false);

		Direction direction = this.placeSettings.getRotation().rotate(Direction.SOUTH);

		Optional<Holder.Reference<MagicPaintingVariant>> variantHolderOpt = variantForGallery(level, this.templateName);
		MagicPainting galleryPainting = TFEntities.MAGIC_PAINTING.value().create(level.getLevel());
		if (variantHolderOpt.isPresent() && galleryPainting != null) {
			galleryPainting.setDirection(direction);
			galleryPainting.setVariant(variantHolderOpt.get());

			variantHolderOpt.get().value();
			this.placeSettings.getRotation();
			galleryPainting.moveTo(pos.getBottomCenter(), 0, 0);

			level.addFreshEntityWithPassengers(galleryPainting);
		}
	}

	private static Optional<Holder.Reference<MagicPaintingVariant>> variantForGallery(ServerLevelAccessor level, String roomId) {
		ResourceKey<MagicPaintingVariant> variantId = switch (roomId) {
			case "twilightforest:lich_tower/gallery/castaway_paradise" -> MagicPaintingVariants.CASTAWAY_PARADISE;
			case "twilightforest:lich_tower/gallery/darkness" -> MagicPaintingVariants.DARKNESS;
			case "twilightforest:lich_tower/gallery/lucid_lands" -> MagicPaintingVariants.LUCID_LANDS;
			case "twilightforest:lich_tower/gallery/music_in_the_mire" -> MagicPaintingVariants.MUSIC_IN_THE_MIRE;
			case "twilightforest:lich_tower/gallery/the_hostile_paradise" -> MagicPaintingVariants.THE_HOSTILE_PARADISE;
			default -> null;
		};

		if (variantId == null) return Optional.empty();

		return level.registryAccess().registryOrThrow(TFRegistries.Keys.MAGIC_PAINTINGS).getHolder(variantId);
	}

	public static void tryPlaceGallery(RandomSource random, StructurePieceAccessor pieceAccessor, @Nullable ResourceLocation roomId, JigsawRecord connection, TwilightJigsawPiece parent, int newDepth, StructureTemplateManager structureManager, String jigsawLabel) {
		JigsawPlaceContext placeableJunction = JigsawPlaceContext.pickPlaceableJunction(parent.templatePosition(), connection.pos(), connection.orientation(), structureManager, roomId, jigsawLabel, random);
		if (placeableJunction != null) {
			StructurePiece room = new LichTowerMagicGallery(newDepth, structureManager, roomId, placeableJunction);
			pieceAccessor.addPiece(room);
			room.addChildren(parent, pieceAccessor, random);
		}
	}

	@Override
	public int getSpawnIndex() {
		return LichTowerPieces.INTERIOR_SPAWNS;
	}
}
