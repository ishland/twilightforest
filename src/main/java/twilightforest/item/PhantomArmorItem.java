package twilightforest.item;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidArmorModel;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.equipment.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.equipment.ArmorType;
import net.minecraft.world.item.equipment.EquipmentModel;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import twilightforest.client.model.TFModelLayers;

import java.util.List;

public class PhantomArmorItem extends ArmorItem {
	private static final MutableComponent TOOLTIP = Component.translatable("item.twilightforest.phantom_armor.desc").setStyle(Style.EMPTY.withColor(ChatFormatting.GRAY));

	public PhantomArmorItem(ArmorMaterial material, ArmorType type, Properties properties) {
		super(material, type, properties);
	}

	@Override
	public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
		tooltip.add(TOOLTIP);
	}

	public static final class ArmorRender implements IClientItemExtensions {
		public static final ArmorRender INSTANCE = new ArmorRender();

		@Override
		public Model getHumanoidArmorModel(ItemStack stack, EquipmentModel.LayerType type, Model original) {
			EntityModelSet models = Minecraft.getInstance().getEntityModels();
			ModelPart root = models.bakeLayer(type == EquipmentModel.LayerType.HUMANOID_LEGGINGS ? TFModelLayers.PHANTOM_ARMOR_INNER : TFModelLayers.PHANTOM_ARMOR_OUTER);
			return new HumanoidArmorModel<>(root);
		}
	}
}