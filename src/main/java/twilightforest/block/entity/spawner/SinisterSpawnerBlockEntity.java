package twilightforest.block.entity.spawner;

import com.mojang.datafixers.util.Either;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.BaseSpawner;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.SpawnData;
import net.minecraft.world.level.Spawner;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import twilightforest.init.TFBlockEntities;
import twilightforest.init.TFBlocks;

import javax.annotation.Nullable;
import java.util.List;

public class SinisterSpawnerBlockEntity extends BlockEntity implements Spawner {
	private final SinisterSpawnerLogic spawner = new SinisterSpawnerLogic() {
		@Override
		public Either<BlockEntity, Entity> getOwner() {
			return Either.left(SinisterSpawnerBlockEntity.this);
		}
	};

	public SinisterSpawnerBlockEntity(BlockPos pos, BlockState blockState) {
		super(TFBlockEntities.SINISTER_SPAWNER.value(), pos, blockState);
	}

	@Override
	protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
		super.loadAdditional(tag, registries);
		this.spawner.load(this.level, this.worldPosition, tag);
	}

	@Override
	protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
		super.saveAdditional(tag, registries);
		this.spawner.save(tag);
	}

	public static void clientTick(Level level, BlockPos pos, BlockState state, SinisterSpawnerBlockEntity blockEntity) {
		blockEntity.spawner.clientTick(level, pos);
	}

	public static void serverTick(Level level, BlockPos pos, BlockState state, SinisterSpawnerBlockEntity blockEntity) {
		blockEntity.spawner.serverTick((ServerLevel) level, pos);
	}

	@Override
	public ClientboundBlockEntityDataPacket getUpdatePacket() {
		return ClientboundBlockEntityDataPacket.create(this);
	}

	@Override
	public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
		CompoundTag compoundtag = this.saveCustomOnly(registries);
		compoundtag.remove("SpawnPotentials");
		return compoundtag;
	}

	@Override
	public boolean triggerEvent(int id, int type) {
		return this.spawner.onEventTriggered(this.level, id) || super.triggerEvent(id, type);
	}

	@Override
	public boolean onlyOpCanSetNbt() {
		return true;
	}

	@Override
	public void setEntityId(EntityType<?> type, RandomSource random) {
		this.spawner.setEntityId(type, this.level, random, this.worldPosition);
		this.setChanged();
	}

	public SinisterSpawnerLogic getSpawner() {
		return this.spawner;
	}

	public boolean setParticles(List<ParticleOptions> particles) {
		return this.getSpawner().setParticles(particles);
	}

	public boolean addParticle(ParticleOptions particle) {
		return this.getSpawner().addParticle(particle);
	}

	public boolean removeParticle(ParticleOptions particle) {
		return this.getSpawner().removeParticle(particle);
	}

	public void sendChanges() {
		this.getLevel().sendBlockUpdated(this.getBlockPos(), this.getBlockState(), this.getBlockState(), 3);
	}
}
