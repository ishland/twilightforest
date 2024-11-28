package twilightforest.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.world.entity.*;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.event.EventHooks;
import twilightforest.TwilightForestMod;
import twilightforest.init.TFDamageTypes;
import twilightforest.init.TFDataMaps;
import twilightforest.util.datamaps.EntityTransformation;

import java.util.UUID;

public class OminousFireBlock extends BaseFireBlock {
	public static final MapCodec<OminousFireBlock> CODEC = simpleCodec(OminousFireBlock::new);

	@Override
	public MapCodec<OminousFireBlock> codec() {
		return CODEC;
	}

	public OminousFireBlock(BlockBehaviour.Properties properties) {
		super(properties, 1.0F);
	}

	@Override
	protected BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor level, BlockPos currentPos, BlockPos facingPos) {
		return this.canSurvive(state, level, currentPos) ? this.defaultBlockState() : Blocks.AIR.defaultBlockState();
	}

	@Override
	public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
		return level.getBlockState(pos.below()).isFaceSturdy(level, pos.below(), Direction.UP) && level.getFluidState(pos).isEmpty();
	}

	@Override
	protected boolean canBurn(BlockState state) {
		return true;
	}

	@Override
	@SuppressWarnings("deprecation")
	protected void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
		if (!entity.getType().is(EntityTypeTags.UNDEAD)) {
			if (entity.hurt(TFDamageTypes.getDamageSource(level, TFDamageTypes.OMINOUS_FIRE), 1.0F) && level instanceof ServerLevel && entity instanceof LivingEntity target && target.isDeadOrDying()) {
                EntityTransformation dataMap = target.getType().builtInRegistryHolder().getData(TFDataMaps.OMINOUS_FIRE);

				if (dataMap != null) {
					Entity newEntity = dataMap.result().create(level);
					if (newEntity == null) return;

					newEntity.moveTo(target.getX(), target.getY(), target.getZ(), target.getYRot(), target.getXRot());
					if (newEntity instanceof Mob mob && target.level() instanceof ServerLevelAccessor world) {
						EventHooks.finalizeMobSpawn(mob, world, target.level().getCurrentDifficultyAt(target.blockPosition()), MobSpawnType.CONVERSION, null);
					}

					if (target instanceof Saddleable saddleable && saddleable.isSaddled() && !(newEntity instanceof Saddleable)) {
						newEntity.spawnAtLocation(Items.SADDLE);
					}

					try { // try copying what can be copied
						UUID uuid = newEntity.getUUID();
						newEntity.load(target.saveWithoutId(newEntity.saveWithoutId(new CompoundTag())));
						newEntity.setUUID(uuid);
						if (newEntity instanceof LivingEntity living) {
							living.setHealth(living.getMaxHealth());
						}
					} catch (Exception e) {
						TwilightForestMod.LOGGER.warn("Couldn't transform entity NBT data", e);
					}

					target.level().addFreshEntity(newEntity);
					target.discard();

					if (target instanceof Mob mob) {
						mob.spawnAnim();
						mob.spawnAnim();
					}
					if (newEntity instanceof LivingEntity living) EventHooks.onLivingConvert(target, living);
					level.levelEvent(null, 1026, pos, 0);
				}
			}
        }
	}
}
