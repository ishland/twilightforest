package twilightforest.item;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.item.ToolMaterial;
import net.minecraft.world.level.block.state.BlockState;
import twilightforest.data.tags.BlockTagGenerator;

import javax.annotation.Nonnull;

public class MazebreakerPickItem extends PickaxeItem {
	public MazebreakerPickItem(ToolMaterial material, Properties properties) {
		super(material, 1.0F, -2.8F, properties);
	}

	@Override
	public float getDestroySpeed(@Nonnull ItemStack stack, BlockState state) {
		float destroySpeed = super.getDestroySpeed(stack, state);
		return state.is(BlockTagGenerator.MAZEBREAKER_ACCELERATED) ? destroySpeed * 16.0F : destroySpeed;
	}
}