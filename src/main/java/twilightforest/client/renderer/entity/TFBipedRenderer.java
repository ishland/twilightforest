package twilightforest.client.renderer.entity;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Mob;
import twilightforest.TwilightForestMod;

public class TFBipedRenderer<T extends Mob, S extends HumanoidRenderState, M extends HumanoidModel<S>> extends HumanoidMobRenderer<T, S, M> {

	private final S state;
	private final ResourceLocation texture;

	public TFBipedRenderer(EntityRendererProvider.Context context, S state, M model, float shadowSize, String textureName) {
		super(context, model, shadowSize);
		this.state = state;

		if (textureName.startsWith("textures")) {
			this.texture = ResourceLocation.withDefaultNamespace(textureName);
		} else {
			this.texture = TwilightForestMod.getModelTexture(textureName);
		}
	}

	public TFBipedRenderer(EntityRendererProvider.Context context, S state, M model, M innerArmor, M outerArmor, float shadowSize, String textureName) {
		this(context, state, model, shadowSize, textureName);
		this.addLayer(new HumanoidArmorLayer<>(this, innerArmor, outerArmor, context.getModelManager()));
	}

	@Override
	public ResourceLocation getTextureLocation(S entity) {
		return this.texture;
	}

	@Override
	public S createRenderState() {
		return this.state;
	}

	@Override
	public void extractRenderState(T entity, S state, float partialTick) {
		super.extractRenderState(p_365075_, p_361774_, p_363123_);
	}
}
