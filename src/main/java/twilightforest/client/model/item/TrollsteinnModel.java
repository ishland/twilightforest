package twilightforest.client.model.item;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.block.model.BakedOverrides;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.model.BakedModelWrapper;
import org.jetbrains.annotations.Nullable;
import twilightforest.TwilightForestMod;
import twilightforest.block.TrollsteinnBlock;

public class TrollsteinnModel extends BakedModelWrapper<BakedModel> {
	public static final ModelResourceLocation LIT_TROLLSTEINN = ModelResourceLocation.standalone(TwilightForestMod.prefix("item/trollsteinn_light"));
	@Nullable
	private BakedModel litTrollsteinnModel;
	private final BakedOverrides overrides = new BakedOverrides() {
		@Override
		public @Nullable BakedModel findOverride(ItemStack stack, @Nullable ClientLevel level, @Nullable LivingEntity entity, int seed) {
			if (TrollsteinnModel.this.litTrollsteinnModel == null)
				TrollsteinnModel.this.litTrollsteinnModel = Minecraft.getInstance().getModelManager().getModel(LIT_TROLLSTEINN);

			Entity itemEntity = (entity == null) ? stack.getEntityRepresentation() : entity;

			if (level == null || itemEntity == null) {
				return TrollsteinnModel.this.originalModel;
			}

			int brightness = level.getMaxLocalRawBrightness(itemEntity.blockPosition(), TrollsteinnBlock.calculateServerSkyDarken(level));
			if (brightness > TrollsteinnBlock.LIGHT_THRESHOLD) {
				return TrollsteinnModel.this.litTrollsteinnModel;
			} else {
				return TrollsteinnModel.this.originalModel;
			}
		}
	};

	public TrollsteinnModel(BakedModel originalModel) {
		super(originalModel);
	}

	@Override
	public BakedOverrides overrides() {
		return overrides;
	}
}
