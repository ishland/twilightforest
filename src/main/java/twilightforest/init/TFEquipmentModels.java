package twilightforest.init;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.equipment.EquipmentModel;
import twilightforest.TwilightForestMod;

import java.util.function.BiConsumer;

public class TFEquipmentModels {

	public static final ResourceLocation IRONWOOD = TwilightForestMod.prefix("ironwood");
	public static final ResourceLocation STEELEAF = TwilightForestMod.prefix("steeleaf");
	public static final ResourceLocation NAGA = TwilightForestMod.prefix("naga");
	public static final ResourceLocation FIERY = TwilightForestMod.prefix("fiery");
	public static final ResourceLocation KNIGHTMETAL = TwilightForestMod.prefix("knightmetal");
	public static final ResourceLocation PHANTOM = TwilightForestMod.prefix("phantom");
	public static final ResourceLocation ARCTIC = TwilightForestMod.prefix("arctic");
	public static final ResourceLocation YETI = TwilightForestMod.prefix("yeti");

	public static void bootstrap(BiConsumer<ResourceLocation, EquipmentModel> consumer) {
		consumer.accept(IRONWOOD, EquipmentModel.builder().addHumanoidLayers(TwilightForestMod.prefix("ironwood"), false).build());
		consumer.accept(STEELEAF, EquipmentModel.builder().addHumanoidLayers(TwilightForestMod.prefix("steeleaf"), false).build());
		consumer.accept(NAGA, EquipmentModel.builder().addHumanoidLayers(TwilightForestMod.prefix("naga"), false).build());
		consumer.accept(FIERY, EquipmentModel.builder().addHumanoidLayers(TwilightForestMod.prefix("fiery"), false).build());
		consumer.accept(KNIGHTMETAL, EquipmentModel.builder().addHumanoidLayers(TwilightForestMod.prefix("knightmetal"), false).build());
		consumer.accept(PHANTOM, EquipmentModel.builder().addHumanoidLayers(TwilightForestMod.prefix("phantom"), false).build());
		consumer.accept(ARCTIC, EquipmentModel.builder()
			.addHumanoidLayers(TwilightForestMod.prefix("arctic_dyed"), true)
			.addHumanoidLayers(TwilightForestMod.prefix("arctic_overlay"), false)
			.build());
		consumer.accept(YETI, EquipmentModel.builder().addHumanoidLayers(TwilightForestMod.prefix("yeti"), false).build());
	}
}
