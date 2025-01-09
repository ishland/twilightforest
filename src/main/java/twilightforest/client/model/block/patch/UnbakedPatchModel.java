package twilightforest.client.model.block.patch;

import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.block.model.TextureSlots;
import net.minecraft.client.resources.model.*;

public record UnbakedPatchModel(boolean shaggify) implements UnbakedModel {

	@Override
	public BakedModel bake(TextureSlots textureSlots, ModelBaker baker, ModelState modelState, boolean hasAmbientOcclusion, boolean useBlockLight, ItemTransforms transforms) {
		return new PatchModel(baker.findSprite(textureSlots, "texture"), this.shaggify());
	}

	@Override
	public void resolveDependencies(Resolver resolver) {

	}
}
