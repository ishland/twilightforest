package twilightforest.item;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidArmorModel;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.equipment.ArmorMaterial;
import net.minecraft.world.item.equipment.ArmorType;
import net.minecraft.world.item.equipment.EquipmentModel;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import twilightforest.client.model.TFModelLayers;

public class KnightmetalArmorItem extends ArmorItem {

	public KnightmetalArmorItem(ArmorMaterial material, ArmorType type, Properties properties) {
		super(material, type, properties);
	}

	public static final class ArmorRender implements IClientItemExtensions {
		public static final ArmorRender INSTANCE = new ArmorRender();

		@Override
		public Model getHumanoidArmorModel(ItemStack stack, EquipmentModel.LayerType type, Model original) {
			EntityModelSet models = Minecraft.getInstance().getEntityModels();
			ModelPart root = models.bakeLayer(type == EquipmentModel.LayerType.HUMANOID_LEGGINGS ? TFModelLayers.KNIGHTMETAL_ARMOR_INNER : TFModelLayers.KNIGHTMETAL_ARMOR_OUTER);
			return new HumanoidArmorModel<>(root);
		}
	}
}