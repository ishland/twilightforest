package twilightforest.client.renderer.entity;

import net.minecraft.client.model.SheepModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.SheepRenderer;
import net.minecraft.client.renderer.entity.layers.SheepFurLayer;
import net.minecraft.client.renderer.entity.layers.SheepWoolLayer;
import net.minecraft.client.renderer.entity.state.SheepRenderState;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.Sheep;
import twilightforest.TwilightForestMod;

public class BighornRenderer extends SheepRenderer {

	public static final ResourceLocation TEXTURE = TwilightForestMod.getModelTexture("bighorn.png");

	public BighornRenderer(EntityRendererProvider.Context context, SheepModel baseModel, float shadowSize) {
		super(context);
		this.shadowRadius = shadowSize;
		this.model = baseModel;
		this.addLayer(new SheepWoolLayer(this, context.getModelSet()));
	}

	@Override
	public ResourceLocation getTextureLocation(SheepRenderState entity) {
		return TEXTURE;
	}
}
