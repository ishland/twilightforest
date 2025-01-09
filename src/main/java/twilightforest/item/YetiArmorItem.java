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
import net.minecraft.network.chat.Style;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.equipment.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.equipment.ArmorType;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import org.jetbrains.annotations.Nullable;
import twilightforest.client.model.TFModelLayers;
import twilightforest.init.TFItems;

import java.util.List;

public class YetiArmorItem extends ArmorItem {
	private static final MutableComponent TOOLTIP = Component.translatable("item.twilightforest.yeti_armor.desc").setStyle(Style.EMPTY.withColor(ChatFormatting.GRAY));

	public YetiArmorItem(ArmorMaterial material, ArmorType type, Properties properties) {
		super(material, type, properties);
	}

	@Override
	public void appendHoverText(ItemStack stack, @Nullable TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
		super.appendHoverText(stack, context, tooltip, flag);
		tooltip.add(TOOLTIP);
	}

	@Override
	public boolean canWalkOnPowderedSnow(ItemStack stack, LivingEntity wearer) {
		return stack.is(TFItems.YETI_BOOTS.get());
	}

	public static final class ArmorRender implements IClientItemExtensions {
		public static final ArmorRender INSTANCE = new ArmorRender();

		@Override
		public Model getHumanoidArmorModel(ItemStack stack, EquipmentClientInfo.LayerType type, Model original) {
			EntityModelSet models = Minecraft.getInstance().getEntityModels();
			ModelPart root = models.bakeLayer(type == EquipmentClientInfo.LayerType.HUMANOID_LEGGINGS ? TFModelLayers.YETI_ARMOR_INNER : TFModelLayers.YETI_ARMOR_OUTER);
			return new HumanoidArmorModel<>(root);
		}
	}
}