package twilightforest.item.recipe;

import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import twilightforest.init.TFItems;
import twilightforest.init.TFRecipes;

import java.util.ArrayList;
import java.util.List;

public class MoonwormQueenRepairRecipe extends CustomRecipe {

	public MoonwormQueenRepairRecipe(CraftingBookCategory category) {
		super(category);
	}

	@Override
	public boolean matches(CraftingInput input, Level level) {
		ItemStack queen = ItemStack.EMPTY;
		int berries = 0;

		for (int i = 0; i < input.size(); ++i) {
			ItemStack checkedStack = input.getItem(i);
			if (!checkedStack.isEmpty()) {
				if (checkedStack.is(TFItems.MOONWORM_QUEEN.get()) && checkedStack.isDamaged()) {
					queen = checkedStack;
				}
				if (checkedStack.is(TFItems.TORCHBERRIES.get())) {
					berries++;
				}
			}
		}
		return !queen.isEmpty() && berries > 0;
	}

	@Override
	public ItemStack assemble(CraftingInput input, HolderLookup.Provider access) {
		int berries = 0;
		ItemStack queen = ItemStack.EMPTY;
		for (int i = 0; i < input.size(); ++i) {
			ItemStack itemstack = input.getItem(i);
			if (!itemstack.isEmpty()) {
				if (itemstack.is(TFItems.MOONWORM_QUEEN.get())) {
					if (queen.isEmpty()) {
						queen = itemstack;
					} else {
						//Only accept 1 queen
						return ItemStack.EMPTY;
					}
				}

				if (itemstack.is(TFItems.TORCHBERRIES.get())) {
					//add all berries in the grid to a list to determine the amount to repair
					berries++;
				}
			}
		}

		if (berries > 0 && !queen.isEmpty() && queen.isDamaged()) {
			ItemStack newQueen = TFItems.MOONWORM_QUEEN.toStack();
			//each berry repairs 64 durability
			newQueen.setDamageValue(queen.getDamageValue() - (berries * 64));
			return newQueen;
		}

		return ItemStack.EMPTY;
	}

	@Override
	public RecipeSerializer<? extends CustomRecipe> getSerializer() {
		return TFRecipes.MOONWORM_QUEEN_REPAIR_RECIPE.get();
	}
}
