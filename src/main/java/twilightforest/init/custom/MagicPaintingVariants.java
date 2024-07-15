package twilightforest.init.custom;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import net.minecraft.core.Holder;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.RegistryFileCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import twilightforest.TFRegistries;
import twilightforest.TwilightForestMod;
import twilightforest.data.AtlasGenerator;
import twilightforest.data.LangGenerator;
import twilightforest.entity.MagicPaintingVariant;
import twilightforest.entity.MagicPaintingVariant.Layer;

import java.util.List;

import static twilightforest.entity.MagicPaintingVariant.Layer.OpacityModifier;
import static twilightforest.entity.MagicPaintingVariant.Layer.Parallax;

public class MagicPaintingVariants {
	public static final Codec<Holder<MagicPaintingVariant>> CODEC = RegistryFileCodec.create(TFRegistries.Keys.MAGIC_PAINTINGS, MagicPaintingVariant.CODEC, false);
	public static final StreamCodec<? super RegistryFriendlyByteBuf, Holder<MagicPaintingVariant>> STREAM_CODEC = ByteBufCodecs.holderRegistry(TFRegistries.Keys.MAGIC_PAINTINGS);

	public static final ResourceKey<MagicPaintingVariant> DARKNESS = makeKey(TwilightForestMod.prefix("darkness"));
	public static final ResourceKey<MagicPaintingVariant> LUCID_LANDS = makeKey(TwilightForestMod.prefix("lucid_lands"));
	public static final ResourceKey<MagicPaintingVariant> THE_HOSTILE_PARADISE = makeKey(TwilightForestMod.prefix("the_hostile_paradise"));
	public static final ResourceKey<MagicPaintingVariant> CASTAWAY_PARADISE = makeKey(TwilightForestMod.prefix("castaway_paradise"));
	public static final ResourceKey<MagicPaintingVariant> MUSIC_IN_THE_MIRE = makeKey(TwilightForestMod.prefix("music_in_the_mire"));

	public static final ResourceKey<MagicPaintingVariant> DEFAULT = MagicPaintingVariants.LUCID_LANDS; // FIXME Switch to a smaller one once available or create a blank 1x1 that's not accessible by normal means

	private static ResourceKey<MagicPaintingVariant> makeKey(ResourceLocation name) {
		return ResourceKey.create(TFRegistries.Keys.MAGIC_PAINTINGS, name);
	}

	public static void bootstrap(BootstrapContext<MagicPaintingVariant> context) {
		register(context, DARKNESS, "Darkness", "???", 4, 2, List.of(
			new Layer("background", null, null, true),
			new Layer("sky", new Parallax(Parallax.Type.VIEW_ANGLE, 0.01F, 128, 32), new OpacityModifier(OpacityModifier.Type.SINE_TIME, 0.03F, false, 0.0F, 1.0F), true),
			new Layer("terrain", null, null, false),
			new Layer("gems", null, null, true),
			new Layer("gems", null, new OpacityModifier(OpacityModifier.Type.DAY_TIME, 2.0F, true, 0.0F, 1.0F, 1, 23999), true),
			new Layer("lightning", null, new OpacityModifier(OpacityModifier.Type.LIGHTNING, 1.0F, false, 0.0F, 1.0F), true),
			new Layer("frame", null, null, false)
		));
		register(context, LUCID_LANDS, "Lucid Lands", "Androsa", 2, 2, List.of(
			new Layer("background", null, null, true),
			new Layer("clouds", new Parallax(Parallax.Type.SINE_TIME, 0.01F, 48, 32), null, true),
			new Layer("volcanic_lands", null, null, true),
			new Layer("agate_jungle", new Parallax(Parallax.Type.VIEW_ANGLE, 0.005F, 44, 32), null, true),
			new Layer("crystal_plains", new Parallax(Parallax.Type.VIEW_ANGLE, 0.006F, 58, 32), null, true),
			new Layer("frame", null, null, false)
		));
		register(context, THE_HOSTILE_PARADISE, "The Hostile Paradise", "Oz", 3, 2, List.of(
			new Layer("1_background", null, null, true),
			new Layer("2_distant_islands_par", new Parallax(Parallax.Type.VIEW_ANGLE, 0.02F, 86, 38), null, true),
			new Layer("3_gold_dungeon_par", new Parallax(Parallax.Type.VIEW_ANGLE, 0.02F, 78, 36), null, true),
			new Layer("4_fog_1_opa", null, new OpacityModifier(OpacityModifier.Type.DISTANCE, 1.0F, false, 0.0F, 1.0F, 4.0F, 12.0F), true),
			new Layer("5_silver_dungeon_par", new Parallax(Parallax.Type.VIEW_ANGLE, 0.02F, 70, 36), null, true),
			new Layer("6_fog_2_opa", null, new OpacityModifier(OpacityModifier.Type.DISTANCE, 1.0F, false, 0.0F, 1.0F, 5.0F, 15.0F), true),
			new Layer("7_portal_par", new Parallax(Parallax.Type.VIEW_ANGLE, 0.02F, 62, 34), null, true),
			new Layer("8_fog_3_opa", null, new OpacityModifier(OpacityModifier.Type.DISTANCE, 1.0F, false, 0.0F, 1.0F, 6.0F, 18.0F), true),
			new Layer("9_field_par", new Parallax(Parallax.Type.VIEW_ANGLE, 0.02F, 54, 34), null, true),
			new Layer("10_flowers_par", null, null, true),
			new Layer("11_fog_4_opa", null, new OpacityModifier(OpacityModifier.Type.DISTANCE, 1.0F, false, 0.0F, 1.0F, 12.0F, 24.0F), true),
			new Layer("12_clouds_par", new Parallax(Parallax.Type.VIEW_ANGLE, 0.02F, 58, 32), new OpacityModifier(OpacityModifier.Type.DISTANCE, 1.0F, false, 0.0F, 1.0F, 12.0F, 24.0F), true),
			new Layer("13_frame", null, null, false)
		));
		register(context, CASTAWAY_PARADISE, "Castaway Paradise", "HexaBlu", 2, 4, List.of(
			new Layer("sunset", new Parallax(Parallax.Type.VIEW_ANGLE, 0.01F, 64, 64), null, true),
			new Layer("sun", null, null, true),
			new Layer("faraway_palm", null, null, false),
			new Layer("ocean", null, null, false),
			new Layer("deckchair", null, null, false),
			new Layer("palm_tree", null, null, false),
			new Layer("frame", null, null, false)
		));
		register(context, MUSIC_IN_THE_MIRE, "Music in the Mire", "TripleHeadedSheep", 4, 3, List.of(
			new Layer("bl_sky", new Parallax(Parallax.Type.VIEW_ANGLE, -0.015F, 80, 56), null, false),
			new Layer("bl_roots", new Parallax(Parallax.Type.VIEW_ANGLE, -0.015F, 78, 52), null, true),
			new Layer("bl_lightning", new Parallax(Parallax.Type.VIEW_ANGLE, -0.015F, 78, 52), new OpacityModifier(OpacityModifier.Type.LIGHTNING, 1.0F, false, 0.0F, 1.0F), false),
			new Layer("bl_mid", new Parallax(Parallax.Type.VIEW_ANGLE, -0.015F, 76, 52), null, false),
			new Layer("bl_mid_lightning", new Parallax(Parallax.Type.VIEW_ANGLE, -0.015F, 76, 52), new OpacityModifier(OpacityModifier.Type.LIGHTNING, 1.0F / 10.0F, false, 0.0F, 1.0F), false),
			new Layer("bl_fog", new Parallax(Parallax.Type.VIEW_ANGLE, -0.015F, 76, 52), null, false),
			new Layer("bl_foreground", new Parallax(Parallax.Type.VIEW_ANGLE, -0.015F, 74, 48), null, false),
			new Layer("bl_foreground_lightning", new Parallax(Parallax.Type.VIEW_ANGLE, -0.015F, 74, 48), new OpacityModifier(OpacityModifier.Type.LIGHTNING, 1.0F / 11.0F, false, 0.0F, 1.0F), false),
			new Layer("bl_greeblings", new Parallax(Parallax.Type.VIEW_ANGLE, -0.015F, 74, 48), null, false),
			new Layer("bl_greeblings_lightning", new Parallax(Parallax.Type.VIEW_ANGLE, -0.015F, 74, 48), new OpacityModifier(OpacityModifier.Type.LIGHTNING, 1.0F / 12.0F, false, 0.0F, 1.0F), false),
			new Layer("bl_shade", new Parallax(Parallax.Type.VIEW_ANGLE, -0.015F, 80, 48), null, false),
			new Layer("bl_frame", null, null, false)
		));
	}

	@SuppressWarnings("SameParameterValue")
	private static void register(BootstrapContext<MagicPaintingVariant> context, ResourceKey<MagicPaintingVariant> key, String title, String author, int width, int height, List<Layer> layers) {
		MagicPaintingVariant variant = new MagicPaintingVariant(width * 16, height * 16, layers);
		AtlasGenerator.MAGIC_PAINTING_HELPER.put(key.location(), variant);
		LangGenerator.MAGIC_PAINTING_HELPER.put(key.location(), Pair.of(title, author));
		context.register(key, variant);
	}
}
