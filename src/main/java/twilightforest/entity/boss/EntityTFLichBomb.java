package twilightforest.entity.boss;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import twilightforest.entity.EntityTFThrowable;

public class EntityTFLichBomb extends EntityTFThrowable {

	public EntityTFLichBomb(EntityType<? extends EntityTFLichBomb> type, World world) {
		super(type, world);
	}

	public EntityTFLichBomb(EntityType<? extends EntityTFLichBomb> type, World world, LivingEntity thrower) {
		super(type, world, thrower);
	}

	@Override
	public void tick() {
		super.tick();
		makeTrail();
	}

	private void makeTrail() {
		for (int i = 0; i < 1; i++) {
			double sx = 0.5 * (rand.nextDouble() - rand.nextDouble()) + this.motionX;
			double sy = 0.5 * (rand.nextDouble() - rand.nextDouble()) + this.motionY;
			double sz = 0.5 * (rand.nextDouble() - rand.nextDouble()) + this.motionZ;


			double dx = posX + sx;
			double dy = posY + sy;
			double dz = posZ + sz;

			world.addParticle(ParticleTypes.FLAME, dx, dy, dz, sx * -0.25, sy * -0.25, sz * -0.25);
		}
	}

	@Override
	public boolean isBurning() {
		return true;
	}

	@Override
	public boolean canBeCollidedWith() {
		return true;
	}

	@Override
	public float getCollisionBorderSize() {
		return 1.0F;
	}

	@Override
	public boolean attackEntityFrom(DamageSource source, float amount) {
		super.attackEntityFrom(source, amount);

		if (source.getImmediateSource() != null) {
			if (!source.isExplosion())
				explode();
			return true;
		} else {
			return false;
		}
	}

	private void explode() {
		if (!this.world.isRemote) {
			this.world.newExplosion(this, this.posX, this.posY, this.posZ, 2F, false, false);
			this.setDead();
		}
	}

	@Override
	protected float getGravityVelocity() {
		return 0.001F;
	}

	@Override
	protected void onImpact(RayTraceResult result) {
		if (result.entityHit instanceof EntityTFLichBolt
				|| result.entityHit instanceof EntityTFLichBomb
				|| result.entityHit instanceof EntityTFLich) {
			return;
		}

		explode();
	}
}
