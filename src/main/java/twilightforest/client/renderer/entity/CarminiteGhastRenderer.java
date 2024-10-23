package twilightforest.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import twilightforest.client.model.entity.TFGhastModel;
import twilightforest.client.state.TFGhastRenderState;
import twilightforest.entity.monster.CarminiteGhastguard;

/**
 * This is a copy of the GhastRenderer class that changes the model
 */
public class CarminiteGhastRenderer extends TFGhastRenderer<CarminiteGhastguard, TFGhastModel> {

	public CarminiteGhastRenderer(EntityRendererProvider.Context renderManager, TFGhastModel model, float f) {
		super(renderManager, model, f);
	}

	@Override
	protected void scale(TFGhastRenderState state, PoseStack stack) {
		float scaleVariable = state.attackTimer / 20.0F;
		if (scaleVariable < 0.0F) {
			scaleVariable = 0.0F;
		}

		scaleVariable = 1.0F / (scaleVariable * scaleVariable * scaleVariable * scaleVariable * scaleVariable * 2.0F + 1.0F);
		float ghastScale = 8.0F;
		float yScale = (ghastScale + scaleVariable) / 2.0F;
		float xzScale = (ghastScale + 1.0F / scaleVariable) / 2.0F;
		stack.scale(xzScale, yScale, xzScale);
	}
}
