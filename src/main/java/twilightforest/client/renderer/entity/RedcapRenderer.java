package twilightforest.client.renderer.entity;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.resources.ResourceLocation;
import twilightforest.TwilightForestMod;
import twilightforest.client.model.TFModelLayers;
import twilightforest.client.model.entity.RedcapModel;
import twilightforest.entity.monster.Redcap;

public class RedcapRenderer extends HumanoidMobRenderer<Redcap, HumanoidRenderState, RedcapModel> {

	private static final ResourceLocation TEXTURE = TwilightForestMod.getModelTexture("redcap.png");

	public RedcapRenderer(EntityRendererProvider.Context context) {
		super(context, new RedcapModel(context.bakeLayer(TFModelLayers.REDCAP)), 0.4F);
		this.addLayer(new HumanoidArmorLayer<>(this, new RedcapModel(context.bakeLayer(TFModelLayers.REDCAP_ARMOR_INNER)), new RedcapModel(context.bakeLayer(TFModelLayers.REDCAP_ARMOR_OUTER)), context.getEquipmentRenderer()));
	}

	@Override
	public HumanoidRenderState createRenderState() {
		return new HumanoidRenderState();
	}

	@Override
	public ResourceLocation getTextureLocation(HumanoidRenderState state) {
		return TEXTURE;
	}
}
