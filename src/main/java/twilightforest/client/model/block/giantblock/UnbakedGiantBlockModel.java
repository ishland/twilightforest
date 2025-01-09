package twilightforest.client.model.block.giantblock;

import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.block.model.TextureSlots;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.*;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.Locale;

public record UnbakedGiantBlockModel(ResourceLocation parent) implements UnbakedModel {

	@Override
	public BakedModel bake(TextureSlots textureSlots, ModelBaker baker, ModelState modelState, boolean hasAmbientOcclusion, boolean useBlockLight, ItemTransforms transforms) {
		TextureAtlasSprite[] sprites;
		if (textureSlots.getMaterial("all") != null) {
			sprites = new TextureAtlasSprite[]{baker.findSprite(textureSlots, "all")};
		} else {
			ArrayList<TextureAtlasSprite> materials = new ArrayList<>();
			for (Direction dir : Direction.values()) {
				materials.add(baker.findSprite(textureSlots, dir.getName().toLowerCase(Locale.ROOT)));
			}
			sprites = materials.toArray(new TextureAtlasSprite[]{});
		}

		return new GiantBlockModel(sprites, baker.findSprite(textureSlots, "particle"), transforms);
	}

	@Override
	public void resolveDependencies(Resolver resolver) {

	}
}
