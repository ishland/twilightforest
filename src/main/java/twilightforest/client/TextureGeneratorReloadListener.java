package twilightforest.client;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.platform.TextureUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import twilightforest.TwilightForestMod;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

public class TextureGeneratorReloadListener implements ResourceManagerReloadListener {
	public static final TextureGeneratorReloadListener INSTANCE = new TextureGeneratorReloadListener();
	private static final Map<String, AbstractTexture> BOAT_CACHE = new HashMap<>();
	private static final AtomicReference<NativeImage> ref = new AtomicReference<>();
	private static final List<String> TF_BOATS = List.of("twilight_oak", "canopy", "mangrove", "dark", "time", "transformation", "mining", "sorting");

	@Override
	public void onResourceManagerReload(ResourceManager manager) {
		// Get a default boat chest texture
		ResourceLocation oak = getTextureLocation(ResourceLocation.withDefaultNamespace("oak"));

		manager.getResource(oak).ifPresent(vanillaResource -> {
			try (InputStream vanillaStream = vanillaResource.open()) {
				NativeImage vanillaImage = NativeImage.read(vanillaStream);
				int defaultScale = 128;
				int vanillaScale = vanillaImage.getWidth() / defaultScale;
				for (String type : TF_BOATS) {
					ResourceLocation location = getTextureLocation(TwilightForestMod.prefix(type));
					if (location.getNamespace().equals(TwilightForestMod.ID)) { // We only want to do this to our boats
						manager.getResource(location).ifPresent(tfResource -> {
							try (InputStream tfStream = tfResource.open()) {
								NativeImage tfImage = NativeImage.read(tfStream);
								int tfScale = tfImage.getWidth() / defaultScale;

								for (int x = 0; x < 48 * tfScale; x++) {
									for (int y = 58 * tfScale; y < 96 * tfScale; y++) {
										// If the loaded tf boat chest texture has non-transparent pixels below the boat section of the texture, return
										if (tfImage.getPixel(x, y) != 0x00000000) return;
									}
								}

								if (vanillaScale > tfScale) {
									try (NativeImage newImage = new NativeImage(defaultScale * vanillaScale, defaultScale * vanillaScale, false)) {
										newImage.copyFrom(vanillaImage);
										for (int x = 0; x < 102 * vanillaScale; x++) {
											for (int y = 0; y < 52 * vanillaScale; y++) {
												newImage.setPixel(x, y, tfImage.getPixel(x / (vanillaScale / tfScale), y / (vanillaScale / tfScale)));
											}
										}

										ref.set(newImage);

										if (BOAT_CACHE.containsKey(type)) {
											BOAT_CACHE.get(type).load(manager);
										} else {
											AbstractTexture texture = new AbstractTexture() {
												@Override
												public void load(ResourceManager resourceManager) {
													if (ref.get() == null)
														return;
													TextureUtil.prepareImage(this.getId(), 0, ref.get().getWidth(), ref.get().getHeight());
													ref.get().upload(0, 0, 0, 0, 0, ref.get().getWidth(), ref.get().getHeight(), false, false, false, true);
												}
											};
											Minecraft.getInstance().getTextureManager().register(location, texture);
											BOAT_CACHE.put(type, texture);
										}
									}
								} else {
									for (int x = 0; x < 48 * tfScale; x++) {
										for (int y = 58 * tfScale; y < 96 * tfScale; y++) {
											tfImage.setPixel(x, y, vanillaImage.getPixel(x / (tfScale / vanillaScale), y / (tfScale / vanillaScale)));
										}
									}

									ref.set(tfImage);

									if (BOAT_CACHE.containsKey(type)) {
										BOAT_CACHE.get(type).load(manager);
									} else {
										AbstractTexture texture = new AbstractTexture() {
											@Override
											public void load(ResourceManager resourceManager) {
												if (ref.get() == null)
													return;
												TextureUtil.prepareImage(this.getId(), 0, ref.get().getWidth(), ref.get().getHeight());
												ref.get().upload(0, 0, 0, 0, 0, ref.get().getWidth(), ref.get().getHeight(), false, false, false, true);
											}
										};
										Minecraft.getInstance().getTextureManager().register(location, texture);
										BOAT_CACHE.put(type, texture);
									}
								}
							} catch (IOException e) {
								// Fail silently, no boat texture bullshit here
							}
						});
					}
				}
			} catch (IOException e) {
				// Fail silently, no boat texture bullshit here
			}
		});
		ref.set(null);
	}

	private static ResourceLocation getTextureLocation(ResourceLocation texture) {
		return texture.withPrefix("textures/entity/chest_boat/").withSuffix(".png");
	}
}
