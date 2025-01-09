package twilightforest.data;

import net.minecraft.client.renderer.texture.atlas.sources.SingleFile;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.common.data.SpriteSourceProvider;
import twilightforest.TwilightForestMod;
import twilightforest.client.MagicPaintingTextureManager;
import twilightforest.entity.MagicPaintingVariant;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class AtlasGenerator extends SpriteSourceProvider {
	public static final Map<ResourceLocation, MagicPaintingVariant> MAGIC_PAINTING_HELPER = new HashMap<>();

	public AtlasGenerator(PackOutput output, CompletableFuture<HolderLookup.Provider> provider) {
		super(output, provider, TwilightForestMod.ID);
	}

	@Override
	protected void gather() {
		this.atlas(SHIELD_PATTERNS_ATLAS).addSource(new SingleFile(TwilightForestMod.prefix("entity/knightmetal_shield"), Optional.empty()));
		this.atlas(MagicPaintingTextureManager.ATLAS_INFO_LOCATION).addSource(new SingleFile(MagicPaintingTextureManager.BACK_SPRITE_LOCATION, Optional.empty()));

		MAGIC_PAINTING_HELPER.forEach((location, parallaxVariant) -> {
			location = location.withPrefix(MagicPaintingTextureManager.MAGIC_PAINTING_PATH + "/");
			for (MagicPaintingVariant.Layer layer : parallaxVariant.layers()) {
				this.atlas(MagicPaintingTextureManager.ATLAS_INFO_LOCATION).addSource(new SingleFile(location.withSuffix("/" + layer.path()), Optional.empty()));
			}
		});
	}
}
