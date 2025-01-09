package twilightforest.item;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidArmorModel;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.resources.model.EquipmentClientInfo;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.equipment.ArmorMaterial;
import net.minecraft.world.item.equipment.ArmorType;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import twilightforest.client.model.TFModelLayers;
import twilightforest.init.TFItems;

import java.util.List;

public class ArcticArmorItem extends ArmorItem {
	private static final MutableComponent TOOLTIP = Component.translatable("item.twilightforest.arctic_armor.desc").withStyle(ChatFormatting.GRAY);
	public static final int DEFAULT_COLOR = 0xFFBDCFD9;

	public ArcticArmorItem(ArmorMaterial armorMaterial, ArmorType type, Properties properties) {
		super(armorMaterial, type, properties);
	}

	@Override
	public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
		super.appendHoverText(stack, context, tooltip, flag);
		tooltip.add(TOOLTIP);
	}

	@Override
	public boolean canWalkOnPowderedSnow(ItemStack stack, LivingEntity wearer) {
		return stack.is(TFItems.ARCTIC_BOOTS.get());
	}

	public static final class ArmorRender implements IClientItemExtensions {
		public static final ArmorRender INSTANCE = new ArmorRender();

		@Override
		public int getDefaultDyeColor(ItemStack stack) {
			return DEFAULT_COLOR;
		}

		@Override
		public Model getHumanoidArmorModel(ItemStack stack, EquipmentClientInfo.LayerType type, Model original) {
			EntityModelSet models = Minecraft.getInstance().getEntityModels();
			ModelPart root = models.bakeLayer(type == EquipmentClientInfo.LayerType.HUMANOID_LEGGINGS ? TFModelLayers.ARCTIC_ARMOR_INNER : TFModelLayers.ARCTIC_ARMOR_OUTER);
			return new HumanoidArmorModel<>(root);
		}
	}
}