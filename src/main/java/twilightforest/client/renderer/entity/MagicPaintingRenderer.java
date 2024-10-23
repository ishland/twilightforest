package twilightforest.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import twilightforest.client.MagicPaintingTextureManager;
import twilightforest.client.state.MagicPaintingRenderState;
import twilightforest.entity.MagicPainting;
import twilightforest.entity.MagicPaintingVariant;
import twilightforest.entity.MagicPaintingVariant.Layer.OpacityModifier;
import twilightforest.entity.MagicPaintingVariant.Layer.Parallax;

import javax.annotation.Nullable;
import java.util.Optional;

public class MagicPaintingRenderer extends EntityRenderer<MagicPainting, MagicPaintingRenderState> {
	public static long lastLightning = 0L;

	public MagicPaintingRenderer(EntityRendererProvider.Context context) {
		super(context);
	}

	@Override
	public void render(MagicPaintingRenderState state, PoseStack stack, MultiBufferSource buffer, int packedLight) {
		MagicPaintingVariant variant = state.variant;

		if (variant != null) {
			stack.pushPose();
			stack.mulPose(Axis.YP.rotationDegrees(180.0F - state.direction.get2DDataValue() * 90));
			stack.scale(0.0625F, 0.0625F, 0.0625F);
			MagicPaintingTextureManager manager = MagicPaintingTextureManager.instance;
			TextureAtlasSprite textureatlassprite = manager.getBackSprite();
			VertexConsumer vertexconsumer = buffer.getBuffer(RenderType.entitySolidZOffsetForward(textureatlassprite.atlasLocation()));
			this.renderPainting(stack, vertexconsumer, state.lightCoords, variant.width(), variant.height(), state, manager, textureatlassprite);
			stack.popPose();
			super.render(state, stack, buffer, packedLight);
		}
	}

	private void renderPainting(PoseStack stack, VertexConsumer vertex, int[] worldLight, int width, int height, MagicPaintingRenderState state, MagicPaintingTextureManager manager, TextureAtlasSprite backSprite) {
		ResourceLocation textureLocation = state.texture;

		int widthAsBlock = width / 16;
		int heightAsBlock = height / 16;

		PoseStack.Pose pose = stack.last();

		float x = (float) (-width) / 2.0F;
		float y = (float) (-height) / 2.0F;
		float z = 0.5F;

		double widthFactor = 1.0D / (double) widthAsBlock;
		double heightFactor = 1.0D / (double) heightAsBlock;

		Direction direction = state.direction;

		for (MagicPaintingVariant.Layer layer : state.variant.layers()) {
			float alpha = this.getAlpha(layer.opacityModifier(), state, state.partialTick);
			if (alpha <= 0.0F) continue;

			Parallax parallax = layer.parallax();

			int layerWidth = parallax != null ? parallax.width() : width;
			int layerHeight = parallax != null ? parallax.height() : height;

			double layerWidthAsBlock = layerWidth / 16.0D;
			double layerHeightAsBlock = layerHeight / 16.0D;

			double layerWidthFactor = 1.0D / layerWidthAsBlock;
			double layerHeightFactor = 1.0D / layerHeightAsBlock;

			double widthDiff = parallax != null ? (widthFactor - layerWidthFactor) * (double) widthAsBlock * 0.5D : 0.0D;
			double widthOffset = widthDiff != 0.0D ? this.getWidthOffset(parallax, state, widthDiff, state.partialTick) : 0.0D;

			double heightDiff = parallax != null ? (heightFactor - layerHeightFactor) * (double) heightAsBlock * 0.5D : 0.0D;
			double heightOffset = heightDiff != 0.0D ? this.getHeightOffset(parallax, state, heightDiff, state.partialTick) : 0.0D;

			TextureAtlasSprite layerTexture = manager.getLayerSprite(textureLocation, layer);

			for (int k = 0; k < widthAsBlock; ++k) {
				for (int l = 0; l < heightAsBlock; ++l) {
					float xMax = x + (float) ((k + 1) * 16);
					float xMin = x + (float) (k * 16);
					float yMax = y + (float) ((l + 1) * 16);
					float yMin = y + (float) (l * 16);

					int light = layer.fullbright() ? 15728850 : worldLight[widthAsBlock + heightAsBlock * width];
					float xEnd = layerTexture.getU((float) (layerWidthFactor * (double) (widthAsBlock - k) + widthOffset));
					float xStart = layerTexture.getU((float) (layerWidthFactor * (double) (widthAsBlock - (k + 1)) + widthOffset));
					float yEnd = layerTexture.getV((float) (layerHeightFactor * (double) (heightAsBlock - l) + heightOffset));
					float yStart = layerTexture.getV((float) (layerHeightFactor * (double) (heightAsBlock - (l + 1)) + heightOffset));
					this.vertex(pose, vertex, xMax, yMin, -z, xStart, yEnd, 0, 0, -1, light, alpha);
					this.vertex(pose, vertex, xMin, yMin, -z, xEnd, yEnd, 0, 0, -1, light, alpha);
					this.vertex(pose, vertex, xMin, yMax, -z, xEnd, yStart, 0, 0, -1, light, alpha);
					this.vertex(pose, vertex, xMax, yMax, -z, xStart, yStart, 0, 0, -1, light, alpha);
				}
			}
		}

		float u0 = backSprite.getU0();
		float u1 = backSprite.getU1();
		float v0 = backSprite.getV0();
		float v1 = backSprite.getV1();
		float u01 = backSprite.getU0();
		float u11 = backSprite.getU1();
		float v01 = backSprite.getV0();
		float v = backSprite.getV(0.0625F);
		float u02 = backSprite.getU0();
		float u = backSprite.getU(0.0625F);
		float v02 = backSprite.getV0();
		float v11 = backSprite.getV1();

		for (int w = 0; w < widthAsBlock; ++w) {
			for (int h = 0; h < heightAsBlock; ++h) {
				float xMax = x + (float) ((w + 1) * 16);
				float xMin = x + (float) (w * 16);
				float yMax = y + (float) ((h + 1) * 16);
				float yMin = y + (float) (h * 16);

				int light = worldLight[widthAsBlock + heightAsBlock * width];
				this.vertex(pose, vertex, xMax, yMax, z, u1, v0, 0, 0, 1, light);
				this.vertex(pose, vertex, xMin, yMax, z, u0, v0, 0, 0, 1, light);
				this.vertex(pose, vertex, xMin, yMin, z, u0, v1, 0, 0, 1, light);
				this.vertex(pose, vertex, xMax, yMin, z, u1, v1, 0, 0, 1, light);
				this.vertex(pose, vertex, xMax, yMax, -z, u01, v01, 0, 1, 0, light);
				this.vertex(pose, vertex, xMin, yMax, -z, u11, v01, 0, 1, 0, light);
				this.vertex(pose, vertex, xMin, yMax, z, u11, v, 0, 1, 0, light);
				this.vertex(pose, vertex, xMax, yMax, z, u01, v, 0, 1, 0, light);
				this.vertex(pose, vertex, xMax, yMin, z, u01, v01, 0, -1, 0, light);
				this.vertex(pose, vertex, xMin, yMin, z, u11, v01, 0, -1, 0, light);
				this.vertex(pose, vertex, xMin, yMin, -z, u11, v, 0, -1, 0, light);
				this.vertex(pose, vertex, xMax, yMin, -z, u01, v, 0, -1, 0, light);
				this.vertex(pose, vertex, xMax, yMax, z, u, v02, -1, 0, 0, light);
				this.vertex(pose, vertex, xMax, yMin, z, u, v11, -1, 0, 0, light);
				this.vertex(pose, vertex, xMax, yMin, -z, u02, v11, -1, 0, 0, light);
				this.vertex(pose, vertex, xMax, yMax, -z, u02, v02, -1, 0, 0, light);
				this.vertex(pose, vertex, xMin, yMax, -z, u, v02, 1, 0, 0, light);
				this.vertex(pose, vertex, xMin, yMin, -z, u, v11, 1, 0, 0, light);
				this.vertex(pose, vertex, xMin, yMin, z, u02, v11, 1, 0, 0, light);
				this.vertex(pose, vertex, xMin, yMax, z, u02, v02, 1, 0, 0, light);
			}
		}
	}

	protected void vertex(PoseStack.Pose pose, VertexConsumer vertex, float x, float y, float z, float u, float v, int normX, int normY, int normZ, int light) {
		this.vertex(pose, vertex, x, y, z, u, v, normX, normY, normZ, light, 1.0F);
	}

	protected void vertex(PoseStack.Pose pose, VertexConsumer vertex, float x, float y, float z, float u, float v, int normX, int normY, int normZ, int light, float a) {
		vertex.addVertex(pose, x, y, z).setColor(255, 255, 255, (int) (255.0F * a)).setUv(u, v).setOverlay(OverlayTexture.NO_OVERLAY).setLight(light).setNormal(pose, normX, normY, normZ);
	}

	protected double getWidthOffset(@Nullable Parallax parallax, MagicPaintingRenderState state, double widthDiff, float partialTicks) {
		if (parallax != null) switch (parallax.type()) {
			case VIEW_ANGLE -> {
				Vec3 camPos = Minecraft.getInstance().cameraEntity != null ?
					Minecraft.getInstance().cameraEntity.getEyePosition(partialTicks) :
					Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();

				Vec3 paintPos = state.position.relative(state.direction.getOpposite(), 1.0D);

				double x = camPos.x - paintPos.x;
				double z = camPos.z - paintPos.z;
				double yRot = Mth.wrapDegrees((float) (Mth.atan2(z, x) * (double) (180F / (float) Math.PI)) - 90.0F - state.yRot);
				return widthDiff + Mth.clamp(yRot * parallax.multiplier() * widthDiff, -widthDiff, widthDiff);
			}
			case SINE_TIME -> {
				return widthDiff + (Math.sin(state.ageInTicks * parallax.multiplier()) * widthDiff);
			}
			case LINEAR_TIME -> {
				double trueTick = state.ageInTicks * parallax.multiplier();
				double wholeDiff = widthDiff * 2.0D;
				return widthDiff + (parallax.multiplier() > 0.0D ? -widthDiff + (trueTick % wholeDiff) : widthDiff - (trueTick % wholeDiff));
			}
		}
		return 0.0D;
	}

	protected double getHeightOffset(@Nullable Parallax parallax, MagicPaintingRenderState state, double heightDiff, float partialTicks) {
		if (parallax != null) switch (parallax.type()) {
			case VIEW_ANGLE -> {
				Vec3 camPos = Minecraft.getInstance().cameraEntity != null ?
					Minecraft.getInstance().cameraEntity.getEyePosition(partialTicks) :
					Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();

				Vec3 paintPos = new Vec3(state.x, state.y, state.z).relative(state.direction.getOpposite(), 1.0D);

				double x = camPos.x - paintPos.x;
				double y = camPos.y - paintPos.y;
				double z = camPos.z - paintPos.z;
				double pythagoras = Math.sqrt(x * x + z * z);
				double xRot = Mth.wrapDegrees((float) (-(Mth.atan2(y, pythagoras) * (double) (180F / (float) Math.PI))));

				return heightDiff - Mth.clamp(xRot * parallax.multiplier() * heightDiff, -heightDiff, heightDiff);
			}
			case SINE_TIME -> {
				return heightDiff - (Math.cos(state.ageInTicks * parallax.multiplier()) * heightDiff);
			}
			case LINEAR_TIME -> {
				double trueTick = state.ageInTicks * parallax.multiplier();
				double wholeDiff = heightDiff * 2.0D;
				return heightDiff - (parallax.multiplier() > 0.0D ? -heightDiff + (trueTick % wholeDiff) : heightDiff - (trueTick % wholeDiff));
			}
		}
		return 0.0D;
	}

	protected static final float DAY_LENGTH = 24000.0F;

	protected float getAlpha(@Nullable OpacityModifier opacityModifier, MagicPaintingRenderState state, float partialTicks) {
		ClientLevel level = Minecraft.getInstance().level;
		if (level == null || opacityModifier == null) return 1.0F;

		float a = 1.0F;
		switch (opacityModifier.type()) {
			case DISTANCE -> {
				Vec3 camPos = Optional.ofNullable(Minecraft.getInstance().cameraEntity).map(Entity::getEyePosition).orElse(Minecraft.getInstance().gameRenderer.getMainCamera().getPosition());
				a = fromTo(opacityModifier.from(), opacityModifier.to(), (float) camPos.distanceTo(state.position));
			}
			case WEATHER -> a = level.getRainLevel(partialTicks);
			case STORM -> {
				a = (level.getRainLevel(partialTicks) + level.getThunderLevel(partialTicks)) * 0.5F;
			}
			case LIGHTNING -> {
				a = 1.0F - ((float) (level.getGameTime() - lastLightning) - partialTicks) * opacityModifier.multiplier();
				if (a > 0.0F) a = a * a;
			}
			case DAY_TIME -> {
				float time = level.dimensionType().fixedTime().orElse(level.dayTime()) + partialTicks;

				if (opacityModifier.from() < opacityModifier.to()) {
					a = 1.0F - Math.abs(((time - opacityModifier.from()) / (opacityModifier.to() - opacityModifier.from())) - 0.5F) * 2.0F;
				} else {
					if (time < opacityModifier.to()) time += DAY_LENGTH;
					a = 1.0F - Math.abs(((time - opacityModifier.from()) / (opacityModifier.to() + DAY_LENGTH - opacityModifier.from())) - 0.5F) * 2.0F;
				}
			}
			case SINE_TIME -> a = (float) (Math.sin(state.ageInTicks * opacityModifier.multiplier())) * 0.5F + 0.5F;
			case HEALTH -> {
				if (Minecraft.getInstance().getCameraEntity() instanceof LivingEntity living) {
					a = fromTo(opacityModifier.from(), opacityModifier.to(), living.getHealth());
				}
			}
			case HUNGER -> {
				if (Minecraft.getInstance().getCameraEntity() instanceof Player player) {
					FoodData food = player.getFoodData();
					a = fromTo(opacityModifier.from(), opacityModifier.to(), (float) food.getFoodLevel());
				}
			}
			case HOLDING_ITEM -> {
				if (Minecraft.getInstance().getCameraEntity() instanceof LivingEntity living) {
					ItemStack key = opacityModifier.item();
					if (key != null && !living.isHolding(stack -> ItemStack.isSameItemSameComponents(stack, key)))
						a = 0.0F;
				}
			}
			case MOB_EFFECT_CATEGORY -> {
				if (Minecraft.getInstance().getCameraEntity() instanceof LivingEntity living) {
					boolean flag = false;
					for (MobEffectInstance effect : living.getActiveEffects()) {
						if (opacityModifier.effectCategory().isPresent()) {
							if (effect.getEffect().value().getCategory() == opacityModifier.effectCategory().get()) {
								flag = true;
								break;
							}
						}
					}
					a = flag ? 1.0F : 0.0F;
				}
			}
		}

		a = Mth.clamp(a, 0.0F, 1.0F);
		if (opacityModifier.type().powerOfMultiplier()) a = (float) Math.pow(a, opacityModifier.multiplier());
		if (opacityModifier.invert()) a = 1.0F - a;
		a = a * (opacityModifier.max() - opacityModifier.min()) + opacityModifier.min();
		return a;
	}

	protected static float fromTo(float from, float to, float value) {
		if (from < to) return (value - from) / (to - from);
		else return (from - value) / (from - to);
	}

	protected int getFrameUV(int i, int maxI) {
		if (maxI <= 1) return 4;
		else if (i == 0) return 1;
		else if (i == maxI - 1) return 3;
		else return 2;
	}

	@Override
	public MagicPaintingRenderState createRenderState() {
		return new MagicPaintingRenderState();
	}

	@Override
	public void extractRenderState(MagicPainting entity, MagicPaintingRenderState state, float partialTick) {
		super.extractRenderState(entity, state, partialTick);
		Direction direction = entity.getDirection();
		MagicPaintingVariant variant = entity.getVariant().value();
		state.direction = direction;
		state.variant = variant;
		state.texture = MagicPaintingVariant.getVariantResourceLocation(entity.level().registryAccess(), variant);
		state.yRot = Mth.lerp(partialTick, entity.yRotO, entity.getYRot());
		int i = variant.width();
		int j = variant.height();
		if (state.lightCoords.length != i * j) {
			state.lightCoords = new int[i * j];
		}

		float f = (float)(-i) / 2.0F;
		float f1 = (float)(-j) / 2.0F;
		Level level = entity.level();

		for (int k = 0; k < j; k++) {
			for (int l = 0; l < i; l++) {
				float f2 = (float)l + f + 0.5F;
				float f3 = (float)k + f1 + 0.5F;
				int i1 = entity.getBlockX();
				int j1 = Mth.floor(entity.getY() + (double)f3);
				int k1 = entity.getBlockZ();
				switch (direction) {
					case NORTH:
						i1 = Mth.floor(entity.getX() + (double)f2);
						break;
					case WEST:
						k1 = Mth.floor(entity.getZ() - (double)f2);
						break;
					case SOUTH:
						i1 = Mth.floor(entity.getX() - (double)f2);
						break;
					case EAST:
						k1 = Mth.floor(entity.getZ() + (double)f2);
				}

				state.lightCoords[l + k * i] = LevelRenderer.getLightColor(level, new BlockPos(i1, j1, k1));
			}
		}
	}
}