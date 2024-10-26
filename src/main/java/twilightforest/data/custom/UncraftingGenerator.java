package twilightforest.data.custom;

import net.minecraft.core.HolderGetter;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;

public class UncraftingGenerator {

	public static void buildRecipes(HolderGetter<Item> items, RecipeOutput output) {
		UncraftingRecipeBuilder.uncrafting(items, Items.TIPPED_ARROW, 8).setCost(4)
			.pattern("AAA")
			.pattern("A A")
			.pattern("AAA")
			.define('A', Ingredient.of(Items.ARROW)).save(output);

		UncraftingRecipeBuilder.uncrafting(items, Items.WRITTEN_BOOK).setCost(0)
			.pattern("B")
			.define('B', Items.BOOK).save(output);
	}
}
