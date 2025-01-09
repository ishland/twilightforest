package twilightforest.client.model.block.forcefield;

import net.minecraft.client.renderer.block.model.BlockElement;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.block.model.TextureSlots;
import net.minecraft.client.resources.model.*;
import net.minecraft.util.context.ContextMap;
import net.neoforged.neoforge.client.model.ExtendedUnbakedModel;

import java.util.Map;

public record UnbakedForceFieldModel(Map<BlockElement, ForceFieldModelLoader.Condition> elementsAndConditions) implements ExtendedUnbakedModel {

	@Override
	public BakedModel bake(TextureSlots textures, ModelBaker baker, ModelState modelState, boolean useAmbientOcclusion, boolean usesBlockLight, ItemTransforms itemTransforms, ContextMap additionalProperties) {
		return new ForceFieldModel(this.elementsAndConditions, s -> baker.findSprite(textures, s), useAmbientOcclusion, usesBlockLight, itemTransforms);
	}

	@Override
	public void resolveDependencies(Resolver resolver) {

	}
}
