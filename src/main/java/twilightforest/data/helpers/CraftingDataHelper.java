package twilightforest.data.helpers;

import com.mojang.datafixers.util.Pair;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.*;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ChestBlock;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.registries.DeferredHolder;
import twilightforest.TwilightForestMod;
import twilightforest.block.TFTrappedChestBlock;
import twilightforest.data.tags.ItemTagGenerator;
import twilightforest.init.TFBlocks;

public abstract class CraftingDataHelper extends RecipeProvider {
	public CraftingDataHelper(RecipeOutput output, HolderLookup.Provider provider) {
		super(provider, output);
	}

	protected final void charmRecipe(HolderGetter<Item> getter, String name, DeferredHolder<Item, ? extends Item> result, DeferredHolder<Item, ? extends Item> item) {
		ShapelessRecipeBuilder.shapeless(getter, RecipeCategory.TOOLS, result.get())
			.requires(item.get(), 4)
			.unlockedBy("has_item", has(item.get()))
			.save(this.output, this.createKey(name));
	}

	protected final void castleBlock(HolderGetter<Item> getter, DeferredHolder<Block, ? extends Block> result, ItemLike... ingredients) {
		ShapedRecipeBuilder.shaped(getter, RecipeCategory.BUILDING_BLOCKS, result.get(), 4)
			.pattern("##")
			.pattern("##")
			.define('#', Ingredient.of(ingredients))
			.unlockedBy("has_castle_brick", has(TFBlocks.CASTLE_BRICK.get()))
			.save(this.output, locCastle(BuiltInRegistries.BLOCK.getKey(result.get()).getPath()));
	}

	protected final void woodenStairsBlock(HolderGetter<Item> getter, ResourceKey<Recipe<?>> loc, DeferredHolder<Block, ? extends Block> result, DeferredHolder<Block, ? extends Block> criteria, ItemLike... ingredients) {
		ShapedRecipeBuilder.shaped(getter, RecipeCategory.BUILDING_BLOCKS, result.get(), 8)
			.pattern("#  ")
			.pattern("## ")
			.pattern("###")
			.define('#', Ingredient.of(ingredients))
			.unlockedBy("has_item", has(criteria.get()))
			.group("wooden_stairs")
			.save(this.output, loc);
	}

	protected final void stairsBlock(HolderGetter<Item> getter, ResourceKey<Recipe<?>> loc, DeferredHolder<Block, ? extends Block> result, DeferredHolder<Block, ? extends Block> criteria, ItemLike... ingredients) {
		ShapedRecipeBuilder.shaped(getter, RecipeCategory.BUILDING_BLOCKS, result.get(), 8)
			.pattern("#  ")
			.pattern("## ")
			.pattern("###")
			.define('#', Ingredient.of(ingredients))
			.unlockedBy("has_item", has(criteria.get()))
			.save(this.output, loc);
	}

	protected final void stairsRightBlock(HolderGetter<Item> getter, ResourceKey<Recipe<?>> loc, DeferredHolder<Block, ? extends Block> result, DeferredHolder<Block, ? extends Block> criteria, ItemLike... ingredients) {
		ShapedRecipeBuilder.shaped(getter, RecipeCategory.BUILDING_BLOCKS, result.get(), 8)
			.pattern("###")
			.pattern(" ##")
			.pattern("  #")
			.define('#', Ingredient.of(ingredients))
			.unlockedBy("has_item", has(criteria.get()))
			.save(this.output, loc);
	}

	protected final void compressedBlock(HolderGetter<Item> getter, String name, DeferredHolder<Block, ? extends Block> result, TagKey<Item> ingredient) {
		ShapedRecipeBuilder.shaped(getter, RecipeCategory.BUILDING_BLOCKS, result.get())
			.pattern("###")
			.pattern("###")
			.pattern("###")
			.define('#', ingredient)
			.unlockedBy("has_item", has(ingredient))
			.save(this.output, this.createKey("compressed_blocks/" + name));
	}

	protected final void reverseCompressBlock(HolderGetter<Item> getter, String name, DeferredHolder<Item, ? extends Item> result, TagKey<Item> ingredient) {
		ShapelessRecipeBuilder.shapeless(getter, RecipeCategory.MISC, result.get(), 9)
			.requires(ingredient)
			.unlockedBy("has_item", has(ingredient))
			.save(this.output, this.createKey("compressed_blocks/reversed/" + name));
	}

	protected final void helmetItem(HolderGetter<Item> getter, DeferredHolder<Item, ? extends Item> result, TagKey<Item> material) {
		this.helmetItem(getter, result, material, DataComponentPatch.builder());
	}

	protected final void helmetItem(HolderGetter<Item> getter, DeferredHolder<Item, ? extends Item> result, TagKey<Item> material, DataComponentPatch.Builder component) {
		ShapedRecipeBuilder.shaped(getter, RecipeCategory.COMBAT, new ItemStack(result, 1, component.build()))
			.pattern("###")
			.pattern("# #")
			.define('#', material)
			.unlockedBy("has_item", has(material))
			.save(this.output, locEquip(result.getId().getPath()));
	}

	protected final void chestplateItem(HolderGetter<Item> getter, DeferredHolder<Item, ? extends Item> result, TagKey<Item> material) {
		this.chestplateItem(getter, result, material, DataComponentPatch.builder());
	}

	protected final void chestplateItem(HolderGetter<Item> getter, DeferredHolder<Item, ? extends Item> result, TagKey<Item> material, DataComponentPatch.Builder component) {
		ShapedRecipeBuilder.shaped(getter, RecipeCategory.COMBAT, new ItemStack(result, 1, component.build()))
			.pattern("# #")
			.pattern("###")
			.pattern("###")
			.define('#', material)
			.unlockedBy("has_item", has(material))
			.save(this.output, locEquip(result.getId().getPath()));
	}

	protected final void leggingsItem(HolderGetter<Item> getter, DeferredHolder<Item, ? extends Item> result, TagKey<Item> material) {
		this.leggingsItem(getter, result, material, DataComponentPatch.builder());
	}

	protected final void leggingsItem(HolderGetter<Item> getter, DeferredHolder<Item, ? extends Item> result, TagKey<Item> material, DataComponentPatch.Builder component) {
		ShapedRecipeBuilder.shaped(getter, RecipeCategory.COMBAT, new ItemStack(result, 1, component.build()))
			.pattern("###")
			.pattern("# #")
			.pattern("# #")
			.define('#', material)
			.unlockedBy("has_item", has(material))
			.save(this.output, locEquip(result.getId().getPath()));
	}

	protected final void bootsItem(HolderGetter<Item> getter, DeferredHolder<Item, ? extends Item> result, TagKey<Item> material) {
		this.bootsItem(getter, result, material, DataComponentPatch.builder());
	}

	protected final void bootsItem(HolderGetter<Item> getter, DeferredHolder<Item, ? extends Item> result, TagKey<Item> material, DataComponentPatch.Builder component) {
		ShapedRecipeBuilder.shaped(getter, RecipeCategory.COMBAT, new ItemStack(result, 1, component.build()))
			.pattern("# #")
			.pattern("# #")
			.define('#', material)
			.unlockedBy("has_item", has(material))
			.save(this.output, locEquip(result.getId().getPath()));
	}

	protected final void pickaxeItem(HolderGetter<Item> getter, DeferredHolder<Item, ? extends Item> result, TagKey<Item> material, TagKey<Item> handle) {
		this.pickaxeItem(getter, result, material, handle, DataComponentPatch.builder());
	}

	protected final void pickaxeItem(HolderGetter<Item> getter, DeferredHolder<Item, ? extends Item> result, TagKey<Item> material, TagKey<Item> handle, DataComponentPatch.Builder component) {
		ShapedRecipeBuilder.shaped(getter, RecipeCategory.TOOLS, new ItemStack(result, 1, component.build()))
			.pattern("###")
			.pattern(" X ")
			.pattern(" X ")
			.define('#', material)
			.define('X', handle)
			.unlockedBy("has_item", has(material))
			.save(this.output, locEquip(result.getId().getPath()));
	}

	protected final void swordItem(HolderGetter<Item> getter, DeferredHolder<Item, ? extends Item> result, TagKey<Item> material, TagKey<Item> handle) {
		this.swordItem(getter, result, material, handle, DataComponentPatch.builder());
	}

	protected final void swordItem(HolderGetter<Item> getter, DeferredHolder<Item, ? extends Item> result, TagKey<Item> material, TagKey<Item> handle, DataComponentPatch.Builder component) {
		ShapedRecipeBuilder.shaped(getter, RecipeCategory.COMBAT, new ItemStack(result, 1, component.build()))
			.pattern("#")
			.pattern("#")
			.pattern("X")
			.define('#', material)
			.define('X', handle)
			.unlockedBy("has_item", has(material))
			.save(this.output, locEquip(result.getId().getPath()));
	}

	protected final void axeItem(HolderGetter<Item> getter, DeferredHolder<Item, ? extends Item> result, TagKey<Item> material, TagKey<Item> handle) {
		this.axeItem(getter, result, material, handle, DataComponentPatch.builder());
	}

	protected final void axeItem(HolderGetter<Item> getter, DeferredHolder<Item, ? extends Item> result, TagKey<Item> material, TagKey<Item> handle, DataComponentPatch.Builder component) {
		ShapedRecipeBuilder.shaped(getter, RecipeCategory.TOOLS, new ItemStack(result, 1, component.build()))
			.pattern("##")
			.pattern("#X")
			.pattern(" X")
			.define('#', material)
			.define('X', handle)
			.unlockedBy("has_item", has(material))
			.save(this.output, locEquip(result.getId().getPath()));
	}

	protected final void shovelItem(HolderGetter<Item> getter, DeferredHolder<Item, ? extends Item> result, TagKey<Item> material, TagKey<Item> handle, DataComponentPatch.Builder component) {
		ShapedRecipeBuilder.shaped(getter, RecipeCategory.TOOLS, new ItemStack(result, 1, component.build()))
			.pattern("#")
			.pattern("X")
			.pattern("X")
			.define('#', material)
			.define('X', handle)
			.unlockedBy("has_item", has(material))
			.save(this.output, locEquip(result.getId().getPath()));
	}

	protected final void hoeItem(HolderGetter<Item> getter, DeferredHolder<Item, ? extends Item> result, TagKey<Item> material, TagKey<Item> handle, DataComponentPatch.Builder component) {
		ShapedRecipeBuilder.shaped(getter, RecipeCategory.TOOLS, new ItemStack(result, 1, component.build()))
			.pattern("##")
			.pattern(" X")
			.pattern(" X")
			.define('#', material)
			.define('X', handle)
			.unlockedBy("has_item", has(material))
			.save(this.output, locEquip(result.getId().getPath()));
	}

	@SafeVarargs
	protected final DataComponentPatch.Builder buildEnchants(HolderLookup.Provider provider, Pair<ResourceKey<Enchantment>, Integer>... enchantments) {
		HolderLookup.RegistryLookup<Enchantment> lookup = provider.lookupOrThrow(Registries.ENCHANTMENT);
		var itemEnchants = new ItemEnchantments.Mutable(ItemEnchantments.EMPTY);
		for (var pair : enchantments) {
			itemEnchants.set(lookup.getOrThrow(pair.getFirst()), pair.getSecond());
		}
		return DataComponentPatch.builder().set(DataComponents.ENCHANTMENTS, itemEnchants.toImmutable());
	}

	protected final void buttonBlock(HolderGetter<Item> getter, String name, DeferredHolder<Block, ? extends Block> result, DeferredHolder<Block, ? extends Block> material) {
		ShapelessRecipeBuilder.shapeless(getter, RecipeCategory.REDSTONE, result.get())
			.requires(material.get())
			.unlockedBy("has_item", has(material.get()))
			.group("wooden_button")
			.save(this.output, locWood(name + "_button"));
	}

	protected final void doorBlock(HolderGetter<Item> getter, String name, DeferredHolder<Block, ? extends Block> result, DeferredHolder<Block, ? extends Block> material) {
		ShapedRecipeBuilder.shaped(getter, RecipeCategory.REDSTONE, result.get(), 3)
			.pattern("##")
			.pattern("##")
			.pattern("##")
			.define('#', material.get())
			.unlockedBy("has_item", has(material.get()))
			.group("wooden_door")
			.save(this.output, locWood(name + "_door"));
	}

	protected final void fenceBlock(HolderGetter<Item> getter, String name, DeferredHolder<Block, ? extends Block> result, DeferredHolder<Block, ? extends Block> material) {
		ShapedRecipeBuilder.shaped(getter, RecipeCategory.DECORATIONS, result.get(), 3)
			.pattern("#S#")
			.pattern("#S#")
			.define('#', material.get())
			.define('S', Tags.Items.RODS_WOODEN)
			.unlockedBy("has_item", has(material.get()))
			.group("wooden_fence")
			.save(this.output, locWood(name + "_fence"));
	}

	protected final void gateBlock(HolderGetter<Item> getter, String name, DeferredHolder<Block, ? extends Block> result, DeferredHolder<Block, ? extends Block> material) {
		ShapedRecipeBuilder.shaped(getter, RecipeCategory.REDSTONE, result.get())
			.pattern("S#S")
			.pattern("S#S")
			.define('#', material.get())
			.define('S', Tags.Items.RODS_WOODEN)
			.unlockedBy("has_item", has(material.get()))
			.group("wooden_fence_gate")
			.save(this.output, locWood(name + "_gate"));
	}

	protected final void planksBlock(HolderGetter<Item> getter, String name, DeferredHolder<Block, ? extends Block> result, TagKey<Item> material) {
		ShapelessRecipeBuilder.shapeless(getter, RecipeCategory.BUILDING_BLOCKS, result.get(), 4)
			.requires(material)
			.unlockedBy("has_item", has(material))
			.group("planks")
			.save(this.output, locWood(name + "_planks"));
	}

	protected final void plateBlock(HolderGetter<Item> getter, String name, DeferredHolder<Block, ? extends Block> result, DeferredHolder<Block, ? extends Block> material) {
		ShapedRecipeBuilder.shaped(getter, RecipeCategory.REDSTONE, result.get())
			.pattern("##")
			.define('#', material.get())
			.unlockedBy("has_item", has(material.get()))
			.group("wooden_pressure_plate")
			.save(this.output, locWood(name + "_plate"));
	}

	protected final void woodenSlabBlock(HolderGetter<Item> getter, String name, DeferredHolder<Block, ? extends Block> result, DeferredHolder<Block, ? extends Block> material) {
		ShapedRecipeBuilder.shaped(getter, RecipeCategory.BUILDING_BLOCKS, result.get(), 6)
			.pattern("###")
			.define('#', material.get())
			.unlockedBy("has_item", has(material.get()))
			.group("wooden_slab")
			.save(this.output, locWood(name + "_slab"));
	}

	protected final void slabBlock(HolderGetter<Item> getter, String name, DeferredHolder<Block, ? extends Block> result, DeferredHolder<Block, ? extends Block> material) {
		ShapedRecipeBuilder.shaped(getter, RecipeCategory.BUILDING_BLOCKS, result.get(), 6)
			.pattern("###")
			.define('#', material.get())
			.unlockedBy("has_item", has(material.get()))
			.save(this.output, locWood(name + "_slab"));
	}

	protected final void bannerPattern(HolderGetter<Item> getter, String name, DeferredHolder<Block, ? extends Block> trophy, DeferredHolder<Item, ? extends Item> result) {
		ShapelessRecipeBuilder.shapeless(getter, RecipeCategory.MISC, result.get())
			.requires(Ingredient.of(getter.getOrThrow(ItemTagGenerator.PAPER)))
			.requires(Ingredient.of(trophy.get().asItem()))
			.unlockedBy("has_trophy", has(trophy.get()))
			.save(this.output);
	}

	protected final void trapdoorBlock(HolderGetter<Item> getter, String name, DeferredHolder<Block, ? extends Block> result, DeferredHolder<Block, ? extends Block> material) {
		ShapedRecipeBuilder.shaped(getter, RecipeCategory.REDSTONE, result.get(), 2)
			.pattern("###")
			.pattern("###")
			.define('#', material.get())
			.unlockedBy("has_item", has(material.get()))
			.group("wooden_trapdoor")
			.save(this.output, locWood(name + "_trapdoor"));
	}

	protected final void woodBlock(HolderGetter<Item> getter, String name, DeferredHolder<Block, ? extends Block> result, DeferredHolder<Block, ? extends Block> material) {
		ShapedRecipeBuilder.shaped(getter, RecipeCategory.BUILDING_BLOCKS, result.get(), 3)
			.pattern("##")
			.pattern("##")
			.define('#', material.get())
			.unlockedBy("has_item", has(material.get()))
			.group("bark")
			.save(this.output, locWood(name + "_wood"));
	}

	protected final void strippedWoodBlock(HolderGetter<Item> getter, String name, DeferredHolder<Block, ? extends Block> result, DeferredHolder<Block, ? extends Block> material) {
		ShapedRecipeBuilder.shaped(getter, RecipeCategory.BUILDING_BLOCKS, result.get(), 3)
			.pattern("##")
			.pattern("##")
			.define('#', material.get())
			.unlockedBy("has_item", has(material.get()))
			.save(this.output, locWood(name + "_stripped_wood"));
	}

	protected final void signBlock(HolderGetter<Item> getter, String name, DeferredHolder<Item, ? extends Item> result, DeferredHolder<Block, ? extends Block> material) {
		ShapedRecipeBuilder.shaped(getter, RecipeCategory.DECORATIONS, result.get(), 3)
			.pattern("###")
			.pattern("###")
			.pattern(" - ")
			.define('#', material.get())
			.define('-', Tags.Items.RODS_WOODEN)
			.unlockedBy("has_item", has(material.get()))
			.group("wooden_sign")
			.save(this.output, locWood(name + "_sign"));
	}

	protected final void hangingSignBlock(HolderGetter<Item> getter, String name, DeferredHolder<Item, ? extends Item> result, DeferredHolder<Block, ? extends Block> material) {
		ShapedRecipeBuilder.shaped(getter, RecipeCategory.DECORATIONS, result.get(), 6)
			.pattern("| |")
			.pattern("###")
			.pattern("###")
			.define('#', material.get())
			.define('|', Items.CHAIN)
			.unlockedBy("has_item", has(material.get()))
			.group("hanging_sign")
			.save(this.output, locWood(name + "_hanging_sign"));
	}

	protected final void banisterBlock(HolderGetter<Item> getter, String name, DeferredHolder<Block, ? extends Block> result, DeferredHolder<Block, ? extends Block> material) {
		this.banisterBlock(getter, name, result, material.get());
	}

	protected final void banisterBlock(HolderGetter<Item> getter, String name, DeferredHolder<Block, ? extends Block> result, Block material) {
		ShapedRecipeBuilder.shaped(getter, RecipeCategory.DECORATIONS, result.get(), 3)
			.pattern("---")
			.pattern("| |")
			.define('-', material)
			.define('|', Tags.Items.RODS_WOODEN)
			.unlockedBy("has_item", has(material))
			.group("wooden_banister")
			.save(this.output, locWood(name + "_banister"));
	}

	protected final void chestBlock(HolderGetter<Item> getter, String name, DeferredHolder<Block, ? extends ChestBlock> chest, DeferredHolder<Block, ? extends TFTrappedChestBlock> trapped, DeferredHolder<Block, ? extends Block> material) {
		ShapedRecipeBuilder.shaped(getter, RecipeCategory.DECORATIONS, chest.get(), 2)
			.pattern("###")
			.pattern("#C#")
			.pattern("###")
			.define('#', material.get())
			.define('C', Items.CHEST)
			.unlockedBy("has_item", has(material.get()))
			.group("chest")
			.save(this.output, locWood(name + "_chest"));

		ShapedRecipeBuilder.shaped(getter, RecipeCategory.REDSTONE, trapped.get(), 2)
			.pattern("###")
			.pattern("#C#")
			.pattern("###")
			.define('#', material.get())
			.define('C', Items.TRAPPED_CHEST)
			.unlockedBy("has_item", has(material.get()))
			.group("trapped_chest")
			.save(this.output, locWood(name + "_trapped_chest"));
	}

	protected final void fieryConversion(HolderGetter<Item> getter, DeferredHolder<Item, ? extends Item> result, Item armor, int vials) {
		ShapelessRecipeBuilder.shapeless(getter, RecipeCategory.COMBAT, result.get())
			.requires(armor)
			.requires(Ingredient.of(getter.getOrThrow(ItemTagGenerator.FIERY_VIAL)), vials)
			.unlockedBy("has_item", has(ItemTagGenerator.FIERY_VIAL))
			.group(result.getId().getPath())
			.save(this.output, locEquip("fiery_" + BuiltInRegistries.ITEM.getKey(armor).getPath()));
	}

	protected final void buildBoats(HolderGetter<Item> getter, DeferredHolder<Item, ? extends Item> boat, DeferredHolder<Item, ? extends Item> chestBoat, DeferredHolder<Block, ? extends Block> planks) {
		ShapedRecipeBuilder.shaped(getter, RecipeCategory.TRANSPORTATION, boat.get())
			.pattern("P P")
			.pattern("PPP")
			.define('P', planks.get())
			.group("boat")
			.unlockedBy("in_water", insideOf(Blocks.WATER))
			.save(this.output);

		ShapelessRecipeBuilder.shapeless(getter, RecipeCategory.TRANSPORTATION, chestBoat.get())
			.requires(boat.get())
			.requires(Tags.Items.CHESTS_WOODEN)
			.group("chest_boat")
			.unlockedBy("has_boat", has(ItemTags.BOATS))
			.save(this.output);
	}

	protected final ResourceKey<Recipe<?>> locCastle(String name) {
		return this.createKey("castleblock/" + name);
	}

	protected final ResourceKey<Recipe<?>> locEquip(String name) {
		return this.createKey("equipment/" + name);
	}

	protected final ResourceKey<Recipe<?>> locNaga(String name) {
		return this.createKey("nagastone/" + name);
	}

	protected final ResourceKey<Recipe<?>> locWood(String name) {
		return this.createKey("wood/" + name);
	}

	protected ResourceKey<Recipe<?>> createKey(String name) {
		return ResourceKey.create(Registries.RECIPE, TwilightForestMod.prefix(name));
	}
}
