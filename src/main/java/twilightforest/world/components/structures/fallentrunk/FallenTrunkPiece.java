package twilightforest.world.components.structures.fallentrunk;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.RegistryOps;
import net.minecraft.util.RandomSource;
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
import twilightforest.init.TFBlocks;
import twilightforest.init.TFStructurePieceTypes;
import twilightforest.world.components.structures.hollowtree.HollowTreePiece;

public class FallenTrunkPiece extends StructurePiece {
	public static BlockStateProvider DEFFAULT_LOG = BlockStateProvider.simple(TFBlocks.TWILIGHT_OAK_LOG.get());
	private BlockStateProvider log;
	public FallenTrunkPiece(int length, int radius, BlockStateProvider log, BoundingBox boundingBox) {
		super(TFStructurePieceTypes.TFFallenTrunk.value(), 0, boundingBox);
	}

	public FallenTrunkPiece(StructurePieceSerializationContext context, CompoundTag tag) {
		super(TFStructurePieceTypes.TFFallenTrunk.value(), tag);

		RegistryOps<Tag> ops = RegistryOps.create(NbtOps.INSTANCE, context.registryAccess());
		log = BlockStateProvider.CODEC.parse(ops, tag.getCompound("log")).result().orElse(HollowTreePiece.DEFAULT_LOG);
	}

	@Override
	protected void addAdditionalSaveData(StructurePieceSerializationContext context, CompoundTag tag) {

	}

	@Override
	public void postProcess(WorldGenLevel level, StructureManager structureManager, ChunkGenerator generator, RandomSource random, BoundingBox box, ChunkPos chunkPos, BlockPos pos) {
		level.setBlock(pos, Blocks.SPONGE.defaultBlockState(), Block.UPDATE_ALL);
		level.setBlock(pos.above(10), Blocks.SPONGE.defaultBlockState(), Block.UPDATE_ALL);
	}
}
