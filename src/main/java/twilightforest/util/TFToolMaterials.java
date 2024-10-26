package twilightforest.util;

import net.minecraft.world.item.ToolMaterial;
import twilightforest.data.tags.BlockTagGenerator;
import twilightforest.data.tags.ItemTagGenerator;

public class TFToolMaterials {
	public static final ToolMaterial IRONWOOD = new ToolMaterial(BlockTagGenerator.INCORRECT_FOR_IRONWOOD_TOOL, 512, 6.5F, 2, 25, ItemTagGenerator.REPAIRS_IRONWOOD_TOOLS);
	public static final ToolMaterial FIERY = new ToolMaterial(BlockTagGenerator.INCORRECT_FOR_FIERY_TOOL, 1024, 9.0F, 4, 10, ItemTagGenerator.REPAIRS_FIERY_TOOLS);
	public static final ToolMaterial STEELEAF = new ToolMaterial(BlockTagGenerator.INCORRECT_FOR_STEELEAF_TOOL, 131, 8.0F, 3, 9, ItemTagGenerator.REPAIRS_STEELEAF_TOOLS);
	public static final ToolMaterial KNIGHTMETAL = new ToolMaterial(BlockTagGenerator.INCORRECT_FOR_KNIGHTMETAL_TOOL, 512, 8.0F, 3, 8, ItemTagGenerator.REPAIRS_KNIGHTMETAL_TOOLS);
	public static final ToolMaterial GIANT = new ToolMaterial(BlockTagGenerator.INCORRECT_FOR_GIANT_TOOL, 1024, 4.0F, 1.0F, 5, ItemTagGenerator.REPAIRS_GIANT_TOOLS);
	public static final ToolMaterial ICE = new ToolMaterial(BlockTagGenerator.INCORRECT_FOR_ICE_TOOL, 32, 1.0F, 3.5F, 5, ItemTagGenerator.REPAIRS_ICE_TOOLS);
	public static final ToolMaterial GLASS = new ToolMaterial(BlockTagGenerator.INCORRECT_FOR_GLASS_TOOL, 1, 1.0F, 36.0F, 30, ItemTagGenerator.REPAIRS_GLASS_TOOLS);
}
