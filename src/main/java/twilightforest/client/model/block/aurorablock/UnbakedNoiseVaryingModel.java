package twilightforest.client.model.block.aurorablock;

import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.block.model.TextureSlots;
import net.minecraft.client.resources.model.*;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class UnbakedNoiseVaryingModel implements UnbakedModel {
	private final String[] importVariants;
	private final List<BlockModel> variants;

	public UnbakedNoiseVaryingModel(String[] variants) {
		this.importVariants = variants;
		this.variants = new ArrayList<>(this.importVariants.length);
	}

	@Override
	public void resolveDependencies(UnbakedModel.Resolver modelGetter) {
		for (String variant : this.importVariants) {
			BlockModel checkedParent = resolveParent(modelGetter, variant);

			this.variants.add(checkedParent);
		}
	}

	@NotNull
	private static BlockModel resolveParent(UnbakedModel.Resolver modelGetter, String variant) {
		if (modelGetter.resolve(ResourceLocation.parse(variant)) instanceof BlockModel blockModel) {
			blockModel.resolveDependencies(modelGetter);
			return blockModel;
		}

		return (BlockModel) modelGetter.resolve(ResourceLocation.withDefaultNamespace("builtin/missing"));
	}

	@Override
	public BakedModel bake(TextureSlots textureSlots, ModelBaker baker, ModelState modelState, boolean hasAmbientOcclusion, boolean useBlockLight, ItemTransforms transforms) {
		BakedModel[] bakedVariants = new BakedModel[this.importVariants.length];

		for (int i = 0; i < bakedVariants.length; i++) {
			BlockModel variant = this.variants.get(i);
			bakedVariants[i] = variant.bake(textureSlots, baker, modelState, hasAmbientOcclusion, useBlockLight, transforms);
		}

		return new NoiseVaryingModel(bakedVariants);
	}
}
