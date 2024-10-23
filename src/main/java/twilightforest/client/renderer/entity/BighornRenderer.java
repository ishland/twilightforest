package twilightforest.client.renderer.entity;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.SheepRenderer;
import net.minecraft.client.renderer.entity.layers.SheepWoolLayer;
import net.minecraft.client.renderer.entity.state.SheepRenderState;
import net.minecraft.resources.ResourceLocation;
import twilightforest.TwilightForestMod;
import twilightforest.client.model.TFModelLayers;
import twilightforest.client.model.entity.BighornModel;

public class BighornRenderer extends SheepRenderer {

	public static final ResourceLocation TEXTURE = TwilightForestMod.getModelTexture("bighorn.png");

	public BighornRenderer(EntityRendererProvider.Context context) {
		super(context);
		this.shadowRadius = 0.7F;
		this.model = new BighornModel(context.bakeLayer(TFModelLayers.BIGHORN_SHEEP));
		this.addLayer(new SheepWoolLayer(this, context.getModelSet()));
	}

	@Override
	public ResourceLocation getTextureLocation(SheepRenderState entity) {
		return TEXTURE;
	}
}
