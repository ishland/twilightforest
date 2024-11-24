package twilightforest.world.components.structures.lichtowerrevamp;

import com.google.common.base.Suppliers;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2IntArrayMap;
import it.unimi.dsi.fastutil.objects.Object2IntMaps;
import net.minecraft.Util;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import org.jetbrains.annotations.Nullable;
import twilightforest.beans.Autowired;
import twilightforest.init.TFBlocks;
import twilightforest.init.TFEntities;
import twilightforest.util.ArrayUtil;
import twilightforest.world.components.processors.*;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

public class LichTowerUtil {
	@Autowired
	private static LichTowerPieces lichRoomPieces;

	private static final Supplier<StructureProcessor> ROOM_SPAWNERS = Suppliers.memoize(() -> SpawnerProcessor.compile(2, 0.8f, Object2IntMaps.unmodifiable(Util.make(new Object2IntArrayMap<>(), map -> {
		// 1/3 chance for any spider variant, 1/3 chance for skeleton, 1/3 chance for zombie
		map.put(EntityType.SPIDER, 1);
		map.put(EntityType.CAVE_SPIDER, 1);
		map.put(TFEntities.SWARM_SPIDER.get(), 1);
		map.put(TFEntities.HEDGE_SPIDER.get(), 1);
		map.put(EntityType.SKELETON, 4);
		map.put(EntityType.ZOMBIE, 4);
	}))));
	private static final Supplier<StructureProcessor> CENTRAL_SPAWNERS = Suppliers.memoize(() -> SpawnerProcessor.compile(4, Object2IntMaps.unmodifiable(Util.make(new Object2IntArrayMap<>(), map -> {
		map.put(EntityType.SKELETON, 2);
		map.put(EntityType.ZOMBIE, 1);
		map.put(TFEntities.SWARM_SPIDER.get(), 1);
	}))));
	private static final Supplier<List<Block>> STAIR_DECAY_BLOCKS = Suppliers.memoize(() -> List.of(
		TFBlocks.TWILIGHT_OAK_SLAB.value(),
		TFBlocks.CANOPY_SLAB.value(),
		TFBlocks.TWILIGHT_OAK_BANISTER.value(),
		TFBlocks.CANOPY_BANISTER.value()
	));
	private static final Supplier<StructureProcessor[]> STAIR_DECAY_PROCESSORS = Suppliers.memoize(() -> new StructureProcessor[]{
		new VerticalDecayProcessor(STAIR_DECAY_BLOCKS.get(), 0.05f),
		new VerticalDecayProcessor(STAIR_DECAY_BLOCKS.get(), 0.1f),
		new VerticalDecayProcessor(STAIR_DECAY_BLOCKS.get(), 0.15f),
		new VerticalDecayProcessor(STAIR_DECAY_BLOCKS.get(), 0.2f),
		new VerticalDecayProcessor(STAIR_DECAY_BLOCKS.get(), 0.25f),
		new VerticalDecayProcessor(STAIR_DECAY_BLOCKS.get(), 0.3f),
		new VerticalDecayProcessor(STAIR_DECAY_BLOCKS.get(), 0.35f)
	});
	private static final Supplier<StructureProcessor> UPDATE_MARKER = Suppliers.memoize(() -> UpdateMarkingProcessor.forBlocks(Blocks.STONE_BRICK_WALL, Blocks.MOSSY_STONE_BRICK_WALL, TFBlocks.WROUGHT_IRON_FENCE.value(), TFBlocks.CANOPY_FENCE.value()));

	public static StructureProcessor getRoomSpawnerProcessor() {
		return ROOM_SPAWNERS.get();
	}

	public static StructureProcessor getCentralBridgeSpawnerProcessor() {
		return CENTRAL_SPAWNERS.get();
	}

	public static List<Block> getStairDecayBlocks() {
		return STAIR_DECAY_BLOCKS.get();
	}

	public static StructureProcessor[] getStairDecayProcessors() {
		return STAIR_DECAY_PROCESSORS.get();
	}

	@Nullable
	public static ResourceLocation rollRandomRoom(RandomSource randomSource, int size) {
		return ArrayUtil.randomOrNull(ArrayUtil.orNull(lichRoomPieces.ROOMS, size), randomSource);
	}

	@Nullable
	public static ResourceLocation rollTowerGallery(RandomSource randomSource) {
		return ArrayUtil.randomOrNull(lichRoomPieces.GALLERY_ROOMS, randomSource);
	}

	@Nullable
	public static ResourceLocation rollGalleryRoof(RandomSource randomSource, BoundingBox box) {
		boolean odd = (Math.min(box.getXSpan(), box.getZSpan()) & 1) == 1;
		return ArrayUtil.randomOrNull(odd ? lichRoomPieces.GALLERY_ROOFS_ODD : lichRoomPieces.GALLERY_ROOFS_EVEN, randomSource);
	}

	public static ResourceLocation rollRandomMobBridge(RandomSource randomSource) {
		return Util.getRandom(lichRoomPieces.MOB_BRIDGES, randomSource);
	}

	public static ResourceLocation rollRandomCover(RandomSource randomSource) {
		return Util.getRandom(lichRoomPieces.BRIDGE_COVERS, randomSource);
	}

	public static ResourceLocation rollRandomDecor(RandomSource randomSource, boolean inCentralTower) {
		return Util.getRandom(inCentralTower ? lichRoomPieces.CENTER_DECORS : lichRoomPieces.ROOM_DECORS, randomSource);
	}

	public static Iterable<ResourceLocation> shuffledCenterBridges(RandomSource randomSource) {
		return Util.shuffledCopy(lichRoomPieces.CENTER_BRIDGES, randomSource);
	}

	public static Iterable<ResourceLocation> shuffledRoomBridges(RandomSource randomSource) {
		return Util.shuffledCopy(lichRoomPieces.ROOM_BRIDGES, randomSource);
	}

	public static Iterable<ResourceLocation> shuffledRoofs(RandomSource randomSource, int size, boolean doSideRoofOnly) {
		return ArrayUtil.safeShuffledCopy(ArrayUtil.orNull(doSideRoofOnly ? lichRoomPieces.SIDE_ROOFS : lichRoomPieces.ROOFS, size), randomSource);
	}

	public static Iterable<ResourceLocation> shuffledBeards(RandomSource randomSource, int size) {
		return ArrayUtil.safeShuffledCopy(ArrayUtil.orNull(lichRoomPieces.WING_BEARDS, size - 1), randomSource);
	}

	@Nullable
	public static ResourceLocation getTrim(int size) {
		return ArrayUtil.orNull(lichRoomPieces.WING_TRIMS, size - 1);
	}

	public static Set<String> getLadderPlacementsForSize(int size) {
		return switch (size) {
			case 1 -> lichRoomPieces.LADDER_PLACEMENTS_1;
			case 2 -> lichRoomPieces.LADDER_PLACEMENTS_2;
			case 3 -> lichRoomPieces.LADDER_PLACEMENTS_3;
			default -> Collections.emptySet();
		};
	}

	@Nullable
	public static ResourceLocation getRoomUpwards(RandomSource random, int size, int ladderOffset) {
		if (size > 0 && size <= 3) {
			Int2ObjectMap<List<ResourceLocation>> roomsForSize = lichRoomPieces.LADDER_ROOMS.get(size - 1);
			List<ResourceLocation> roomsForLadderPlacement = roomsForSize.getOrDefault(ladderOffset, Collections.emptyList());
			return roomsForLadderPlacement.isEmpty() ? null : roomsForLadderPlacement.get(random.nextInt(roomsForLadderPlacement.size()));
		}

		return null;
	}

	@Nullable
	public static ResourceLocation getFallbackRoof(int size, boolean sideAttachment) {
		return ArrayUtil.orNull(sideAttachment ? lichRoomPieces.FLAT_SIDE_ROOFS : lichRoomPieces.FLAT_ROOFS, size);
	}

	@Nullable
	public static ResourceLocation getFallbackBeard(int size) {
		return ArrayUtil.orNull(lichRoomPieces.FLAT_BEARDS, size - 1);
	}

	public static void addDefaultProcessors(StructurePlaceSettings settings) {
		settings.addProcessor(MetaBlockProcessor.INSTANCE)
			.addProcessor(StoneBricksVariants.INSTANCE)
			.addProcessor(CobbleVariants.INSTANCE)
			.addProcessor(UPDATE_MARKER.get());
	}

	private LichTowerUtil() {
		throw new IllegalStateException("How did we get here?");
	}
}
