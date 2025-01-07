package twilightforest.data.recipes;

import com.mojang.datafixers.util.Pair;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.*;
import net.minecraft.util.Unit;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.crafting.CompoundIngredient;
import net.neoforged.neoforge.common.crafting.DataComponentIngredient;
import twilightforest.data.custom.NoSmithingTemplateRecipeBuilder;
import twilightforest.data.custom.ScepterRecipeBuilder;
import twilightforest.data.custom.UncraftingGenerator;
import twilightforest.data.helpers.CraftingDataHelper;
import twilightforest.data.tags.ItemTagGenerator;
import twilightforest.init.TFBlocks;
import twilightforest.init.TFDataComponents;
import twilightforest.init.TFItems;
import twilightforest.item.recipe.*;

public class CraftingGenerator extends CraftingDataHelper {

	public CraftingGenerator(RecipeOutput output, HolderLookup.Provider provider) {
		super(output, provider);
	}

	@Override
	protected void buildRecipes() {
		HolderGetter<Item> getter = this.registries.lookupOrThrow(Registries.ITEM);
		StonecuttingGenerator.buildRecipes(getter, this.output);
		UncraftingGenerator.buildRecipes(getter, this.output);

		blockCompressionRecipes(getter);
		equipmentRecipes(getter, this.registries);
		emptyMapRecipes(getter);
		woodRecipes(getter);
		fieryConversions(getter);

		nagastoneRecipes(getter);
		darkTowerRecipes(getter);
		castleRecipes(getter);

		bannerPattern(getter, "naga_banner_pattern", TFBlocks.NAGA_TROPHY, TFItems.NAGA_BANNER_PATTERN);
		bannerPattern(getter, "lich_banner_pattern", TFBlocks.LICH_TROPHY, TFItems.LICH_BANNER_PATTERN);
		bannerPattern(getter, "minoshroom_banner_pattern", TFBlocks.MINOSHROOM_TROPHY, TFItems.MINOSHROOM_BANNER_PATTERN);
		bannerPattern(getter, "hydra_banner_pattern", TFBlocks.HYDRA_TROPHY, TFItems.HYDRA_BANNER_PATTERN);
		bannerPattern(getter, "knight_phantom_banner_pattern", TFBlocks.KNIGHT_PHANTOM_TROPHY, TFItems.KNIGHT_PHANTOM_BANNER_PATTERN);
		bannerPattern(getter, "ur_ghast_banner_pattern", TFBlocks.UR_GHAST_TROPHY, TFItems.UR_GHAST_BANNER_PATTERN);
		bannerPattern(getter, "alpha_yeti_banner_pattern", TFBlocks.ALPHA_YETI_TROPHY, TFItems.ALPHA_YETI_BANNER_PATTERN);
		bannerPattern(getter, "snow_queen_banner_pattern", TFBlocks.SNOW_QUEEN_TROPHY, TFItems.SNOW_QUEEN_BANNER_PATTERN);
//		bannerPattern(getter, "questing_ram_banner_pattern", TFBlocks.QUEST_RAM_TROPHY, TFItems.QUEST_RAM_BANNER_PATTERN);

		ShapedRecipeBuilder.shaped(getter, RecipeCategory.BUILDING_BLOCKS, Blocks.MOSS_BLOCK)
			.pattern("mmm")
			.pattern("mtm")
			.pattern("mmm")
			.define('m', Ingredient.of(TFBlocks.MOSS_PATCH.get()))
			.define('t', Ingredient.of(TFItems.TRANSFORMATION_POWDER.get()))
			.unlockedBy("has_item", has(TFItems.TRANSFORMATION_POWDER.get()))
			.save(this.output, this.createKey("tf_moss_to_vanilla"));

		ShapelessRecipeBuilder.shapeless(getter, RecipeCategory.DECORATIONS, TFBlocks.MOSS_PATCH.get(), 8)
			.requires(Ingredient.of(Items.MOSS_BLOCK))
			.requires(Ingredient.of(TFItems.TRANSFORMATION_POWDER.get()))
			.unlockedBy("has_item", has(TFItems.TRANSFORMATION_POWDER.get()))
			.save(this.output, this.createKey("vanilla_to_tf_moss"));

		ShapelessRecipeBuilder.shapeless(getter, RecipeCategory.DECORATIONS, TFBlocks.HUGE_LILY_PAD.get())
			.requires(Ingredient.of(Blocks.LILY_PAD), 4)
			.requires(Ingredient.of(TFItems.TRANSFORMATION_POWDER.get()))
			.unlockedBy("has_item", has(TFItems.TRANSFORMATION_POWDER.get()))
			.save(this.output, this.createKey("vanilla_to_tf_lilypad"));

		ShapelessRecipeBuilder.shapeless(getter, RecipeCategory.DECORATIONS, Blocks.LILY_PAD, 4)
			.requires(Ingredient.of(TFBlocks.HUGE_LILY_PAD.get()))
			.requires(Ingredient.of(TFItems.TRANSFORMATION_POWDER.get()))
			.unlockedBy("has_item", has(TFItems.TRANSFORMATION_POWDER.get()))
			.save(this.output, this.createKey("tf_to_vanilla_lilypad"));

		slabBlock(getter, "aurora_slab", TFBlocks.AURORA_SLAB, TFBlocks.AURORA_BLOCK);
		ShapedRecipeBuilder.shaped(getter, RecipeCategory.BUILDING_BLOCKS, TFBlocks.AURORA_PILLAR.get(), 2)
			.pattern("#")
			.pattern("#")
			.define('#', Ingredient.of(TFBlocks.AURORA_BLOCK.get()))
			.unlockedBy("has_slab", has(TFBlocks.AURORA_SLAB.get()))
			.save(this.output);

		ShapedRecipeBuilder.shaped(getter, RecipeCategory.DECORATIONS, TFBlocks.IRON_LADDER.get(), 3)
			.pattern("-#-")
			.pattern("-#-")
			.define('#', Ingredient.of(Blocks.IRON_BARS))
			.define('-', Tags.Items.NUGGETS_IRON)
			.unlockedBy("has_iron_bars", has(Blocks.IRON_BARS))
			.save(this.output);

		ShapedRecipeBuilder.shaped(getter, RecipeCategory.DECORATIONS, TFBlocks.ROPE.get(), 8)
			.pattern("#")
			.pattern("#")
			.pattern("#")
			.define('#', Ingredient.of(TFBlocks.ROOT_STRAND.get()))
			.unlockedBy("has_root_strand", has(TFBlocks.ROOT_STRAND.get()))
			.save(this.output);

//		ShapedRecipeBuilder.shaped(getter, RecipeCategory.DECORATIONS, TFBlocks.CANOPY_WINDOW.value(), 4)
//			.pattern("GPG")
//			.pattern("PPP")
//			.pattern("GPG")
//			.define('G', Ingredient.of(Tags.Items.GLASS_BLOCKS))
//			.define('P', Ingredient.of(TFBlocks.CANOPY_PLANKS.value()))
//			.unlockedBy("has_planks", has(TFBlocks.CANOPY_PLANKS.value()))
//			.save(this.output);
//
//		ShapedRecipeBuilder.shaped(getter, RecipeCategory.DECORATIONS, TFBlocks.CANOPY_WINDOW_PANE.value(), 16)
//			.pattern("GGG")
//			.pattern("GGG")
//			.define('G', Ingredient.of(TFBlocks.CANOPY_WINDOW.value()))
//			.unlockedBy("has_windows", has(TFBlocks.CANOPY_WINDOW.value()))
//			.save(this.output);

		ShapedRecipeBuilder.shaped(getter, RecipeCategory.DECORATIONS, TFItems.MASON_JAR.get(), 4)
			.pattern("GLG")
			.pattern("G G")
			.pattern("GGG")
			.define('G', Ingredient.of(Items.GLASS))
			.define('L', Ingredient.of(TFBlocks.TWILIGHT_OAK_LOG.get()))
			.unlockedBy("has_tf_oak", has(TFBlocks.TWILIGHT_OAK_LOG.value()))
			.save(this.output);

		ShapelessRecipeBuilder.shapeless(getter, RecipeCategory.DECORATIONS, TFBlocks.FIREFLY_JAR.get())
			.requires(Ingredient.of(TFBlocks.FIREFLY.get()))
			.requires(Ingredient.of(TFItems.MASON_JAR.get()))
			.unlockedBy("has_item", has(TFBlocks.FIREFLY.get()))
			.save(this.output);

		ShapelessRecipeBuilder.shapeless(getter, RecipeCategory.DECORATIONS, TFBlocks.FIREFLY_SPAWNER.get())
			.requires(Ingredient.of(TFBlocks.FIREFLY_JAR.get()))
			.requires(Ingredient.of(TFBlocks.FIREFLY.get()))
			.requires(Ingredient.of(Blocks.POPPY))
			.unlockedBy("has_jar", has(TFBlocks.FIREFLY_JAR.get()))
			.save(this.output);

		ShapelessRecipeBuilder.shapeless(getter, RecipeCategory.DECORATIONS, TFBlocks.CICADA_JAR.get())
			.requires(Ingredient.of(TFBlocks.CICADA.get()))
			.requires(Ingredient.of(TFItems.MASON_JAR.get()))
			.unlockedBy("has_item", has(TFBlocks.CICADA.get()))
			.save(this.output);

		ShapelessRecipeBuilder.shapeless(getter, RecipeCategory.MISC, Items.MAGENTA_DYE)
			.requires(Ingredient.of(TFBlocks.HUGE_WATER_LILY.get()))
			.unlockedBy("has_item", has(TFBlocks.HUGE_WATER_LILY.get()))
			.save(this.output, this.createKey("waterlily_to_magenta"));

		ShapelessRecipeBuilder.shapeless(getter, RecipeCategory.MISC, Items.RED_DYE)
			.requires(Ingredient.of(TFBlocks.THORN_ROSE.get()))
			.unlockedBy("has_item", has(TFBlocks.THORN_ROSE.get()))
			.save(this.output, this.createKey("thorn_rose_to_red"));

		ShapelessRecipeBuilder.shapeless(getter, RecipeCategory.MISC, Items.STICK)
			.requires(Ingredient.of(TFBlocks.ROOT_STRAND.get()))
			.unlockedBy("has_item", has(TFBlocks.ROOT_STRAND.get()))
			.group("sticks")
			.save(this.output, this.createKey("root_stick"));

		ShapedRecipeBuilder.shaped(getter, RecipeCategory.DECORATIONS, Blocks.TORCH, 5)
			.pattern("∴")
			.pattern("|")
			.define('∴', Ingredient.of(TFItems.TORCHBERRIES.get()))
			.define('|', Tags.Items.RODS_WOODEN)
			.unlockedBy("has_item", has(TFItems.TORCHBERRIES.get()))
			.save(this.output, this.createKey("berry_torch"));

		ShapedRecipeBuilder.shaped(getter, RecipeCategory.DECORATIONS, TFBlocks.UNCRAFTING_TABLE.get())
			.pattern("###")
			.pattern("#X#")
			.pattern("###")
			.define('#', Blocks.CRAFTING_TABLE)
			.define('X', TFItems.MAZE_MAP_FOCUS.get())
			.unlockedBy("has_uncrafting_table", has(TFBlocks.UNCRAFTING_TABLE.get()))
			.save(this.output.withConditions(UncraftingTableCondition.INSTANCE), this.createKey("uncrafting_table"));

		cookingRecipes("smelted", RecipeSerializer.SMELTING_RECIPE, SmeltingRecipe::new, 200);
		cookingRecipes("smoked", RecipeSerializer.SMOKING_RECIPE, SmokingRecipe::new, 100);
		cookingRecipes("campfired", RecipeSerializer.CAMPFIRE_COOKING_RECIPE, CampfireCookingRecipe::new, 600);

		ingotRecipes("smelted", RecipeSerializer.SMELTING_RECIPE, SmeltingRecipe::new, 200);
		ingotRecipes("blasted", RecipeSerializer.BLASTING_RECIPE, BlastingRecipe::new, 100);

		crackedWoodRecipes();
		crackedStoneRecipes();

//		ShapedRecipeBuilder.shaped(getter, RecipeCategory.BUILDING_BLOCKS, TFBlocks.CHISELED_CANOPY_BOOKSHELF.get())
//			.pattern("---")
//			.pattern("   ")
//			.pattern("---")
//			.define('-', TFBlocks.CANOPY_SLAB.get())
//			.unlockedBy("has_item", has(TFBlocks.CANOPY_SLAB.get()))
//			.save(this.output);

		ShapedRecipeBuilder.shaped(getter, RecipeCategory.BUILDING_BLOCKS, TFBlocks.CANOPY_BOOKSHELF.get())
			.pattern("---")
			.pattern("B B")
			.pattern("---")
			.define('-', TFBlocks.CANOPY_PLANKS.get())
			.define('B', Items.BOOK)
			.unlockedBy("has_item", has(TFBlocks.CANOPY_PLANKS.get()))
			.save(this.output);

//		ShapedRecipeBuilder.shaped(getter, RecipeCategory.DECORATIONS, TFBlocks.CANDELABRA.get())
//			.pattern("III")
//			.pattern(" W ")
//			.define('W', TFBlocks.WROUGHT_IRON_FENCE.get())
//			.define('I', Tags.Items.INGOTS_IRON)
//			.unlockedBy("has_item", has(TFBlocks.WROUGHT_IRON_FENCE.get()))
//			.save(this.output);

		ShapelessRecipeBuilder.shapeless(getter, RecipeCategory.MISC, TFItems.ARMOR_SHARD_CLUSTER.get())
			.requires(Ingredient.of(TFItems.ARMOR_SHARD.get()), 9)
			.unlockedBy("has_item", has(TFItems.ARMOR_SHARD.get()))
			.save(this.output, this.createKey("material/" + TFItems.ARMOR_SHARD_CLUSTER.getId().getPath()));

		ShapelessRecipeBuilder.shapeless(getter, RecipeCategory.BUILDING_BLOCKS, TFBlocks.MOSSY_UNDERBRICK.get(), 1)
			.requires(Ingredient.of(Blocks.VINE, Blocks.MOSS_BLOCK))
			.requires(Ingredient.of(TFBlocks.UNDERBRICK.get()))
			.unlockedBy("has_item", has(TFBlocks.UNDERBRICK.get()))
			.save(this.output);

		ShapelessRecipeBuilder.shapeless(getter, RecipeCategory.BUILDING_BLOCKS, TFBlocks.MOSSY_MAZESTONE.get(), 1)
			.requires(Ingredient.of(Blocks.VINE, Blocks.MOSS_BLOCK))
			.requires(Ingredient.of(TFBlocks.MAZESTONE_BRICK.get()))
			.unlockedBy("has_item", has(TFBlocks.MAZESTONE_BRICK.get()))
			.save(this.output, this.createKey("maze_stone/mossy_mazestone"));

		ShapelessRecipeBuilder.shapeless(getter, RecipeCategory.MISC, TFItems.CARMINITE.get())
			.requires(Ingredient.of(TFItems.BORER_ESSENCE.get()))
			.requires(Tags.Items.DUSTS_REDSTONE)
			.requires(Ingredient.of(TFItems.BORER_ESSENCE.get()))
			.requires(Tags.Items.DUSTS_REDSTONE)
			.requires(Ingredient.of(Items.GHAST_TEAR))
			.requires(Tags.Items.DUSTS_REDSTONE)
			.requires(Ingredient.of(TFItems.BORER_ESSENCE.get()))
			.requires(Tags.Items.DUSTS_REDSTONE)
			.requires(Ingredient.of(TFItems.BORER_ESSENCE.get()))
			.unlockedBy("has_item", has(TFItems.BORER_ESSENCE.get()))
			.save(this.output, this.createKey("material/" + TFItems.CARMINITE.getId().getPath()));

		ShapelessRecipeBuilder.shapeless(getter, RecipeCategory.MISC, TFItems.RAW_IRONWOOD.get(), 2)
			.requires(Ingredient.of(TFItems.LIVEROOT.get()))
			.requires(Ingredient.of(Items.RAW_IRON))
			.requires(Tags.Items.NUGGETS_GOLD)
			.unlockedBy("has_item", has(TFItems.LIVEROOT.get()))
			.save(this.output, this.createKey("material/" + TFItems.RAW_IRONWOOD.getId().getPath()));

		ShapedRecipeBuilder.shaped(getter, RecipeCategory.BUILDING_BLOCKS, TFBlocks.RAINY_CLOUD.get(), 8)
			.pattern("ccc")
			.pattern("cbc")
			.pattern("ccc")
			.define('c', Ingredient.of(TFBlocks.FLUFFY_CLOUD.get()))
			.define('b', Ingredient.of(Items.WATER_BUCKET))
			.unlockedBy("has_item", has(TFBlocks.FLUFFY_CLOUD.get()))
			.save(this.output, this.createKey("rainy_cloud"));

		ShapedRecipeBuilder.shaped(getter, RecipeCategory.BUILDING_BLOCKS, TFBlocks.SNOWY_CLOUD.get(), 8)
			.pattern("ccc")
			.pattern("cbc")
			.pattern("ccc")
			.define('c', Ingredient.of(TFBlocks.FLUFFY_CLOUD.get()))
			.define('b', Ingredient.of(Items.POWDER_SNOW_BUCKET))
			.unlockedBy("has_item", has(TFBlocks.FLUFFY_CLOUD.get()))
			.save(this.output, this.createKey("snowy_cloud"));

//		ShapedRecipeBuilder.shaped(getter, RecipeCategory.COMBAT, TFItems.LICH_CROWN, 1)
//			.pattern("ttt")
//			.pattern("t t")
//			.pattern("ttt")
//			.define('t', Ingredient.of(TFItems.CROWN_SPLINTER))
//			.unlockedBy("has_item", has(TFItems.CROWN_SPLINTER))
//			.save(this.output);
	}

	private void darkTowerRecipes(HolderGetter<Item> getter) {
		ShapedRecipeBuilder.shaped(getter, RecipeCategory.REDSTONE, TFBlocks.ENCASED_FIRE_JET.get())
			.pattern("#∴#")
			.pattern("∴^∴")
			.pattern("uuu")
			.define('∴', Tags.Items.DUSTS_REDSTONE)
			.define('#', Ingredient.of(TFBlocks.ENCASED_TOWERWOOD.get()))
			.define('^', Ingredient.of(TFBlocks.FIRE_JET.get()))
			.define('u', Ingredient.of(Items.LAVA_BUCKET))
			.unlockedBy("has_item", has(TFBlocks.FIRE_JET.get()))
			.save(this.output);

		ShapedRecipeBuilder.shaped(getter, RecipeCategory.REDSTONE, TFBlocks.ENCASED_SMOKER.get())
			.pattern("#∴#")
			.pattern("∴^∴")
			.pattern("#∴#")
			.define('∴', Tags.Items.DUSTS_REDSTONE)
			.define('#', Ingredient.of(TFBlocks.ENCASED_TOWERWOOD.get()))
			.define('^', Ingredient.of(TFBlocks.SMOKER.get()))
			.unlockedBy("has_item", has(TFBlocks.SMOKER.get()))
			.save(this.output);

		ShapedRecipeBuilder.shaped(getter, RecipeCategory.REDSTONE, TFBlocks.CARMINITE_BUILDER.get())
			.pattern("#6#")
			.pattern("6o6")
			.pattern("#6#")
			.define('6', ItemTagGenerator.CARMINITE_GEMS)
			.define('#', Ingredient.of(TFBlocks.ENCASED_TOWERWOOD.get()))
			.define('o', Ingredient.of(Blocks.DISPENSER))
			.unlockedBy("has_item", has(ItemTagGenerator.CARMINITE_GEMS))
			.save(this.output);

		ShapedRecipeBuilder.shaped(getter, RecipeCategory.REDSTONE, TFBlocks.CARMINITE_REACTOR.get())
			.pattern("#6#")
			.pattern("6%6")
			.pattern("#6#")
			.define('6', ItemTagGenerator.CARMINITE_GEMS)
			.define('#', Ingredient.of(TFBlocks.ENCASED_TOWERWOOD.get()))
			.define('%', Tags.Items.ORES_REDSTONE)
			.unlockedBy("has_item", has(ItemTagGenerator.CARMINITE_GEMS))
			.save(this.output);

		ShapedRecipeBuilder.shaped(getter, RecipeCategory.REDSTONE, TFBlocks.REAPPEARING_BLOCK.get(), 2)
			.pattern("#∴#")
			.pattern("∴6∴")
			.pattern("#∴#")
			.define('∴', Tags.Items.DUSTS_REDSTONE)
			.define('#', Ingredient.of(TFBlocks.ENCASED_TOWERWOOD.get()))
			.define('6', ItemTagGenerator.CARMINITE_GEMS)
			.unlockedBy("has_item", has(TFBlocks.REAPPEARING_BLOCK.get()))
			.save(this.output);

		ShapedRecipeBuilder.shaped(getter, RecipeCategory.REDSTONE, TFBlocks.VANISHING_BLOCK.get(), 8)
			.pattern("#w#")
			.pattern("w6w")
			.pattern("#w#")
			.define('w', ItemTagGenerator.TOWERWOOD)
			.define('#', Ingredient.of(TFBlocks.ENCASED_TOWERWOOD.get()))
			.define('6', ItemTagGenerator.CARMINITE_GEMS)
			.unlockedBy("has_item", has(TFBlocks.REAPPEARING_BLOCK.get()))
			.save(this.output);

		ShapelessRecipeBuilder.shapeless(getter, RecipeCategory.BUILDING_BLOCKS, TFBlocks.MOSSY_TOWERWOOD.get())
			.requires(Ingredient.of(TFBlocks.TOWERWOOD.get()))
			.requires(Ingredient.of(Blocks.VINE, Blocks.MOSS_BLOCK))
			.unlockedBy("has_item", has(TFBlocks.TOWERWOOD.get()))
			.save(this.output, this.createKey("wood/" + TFBlocks.MOSSY_TOWERWOOD.getId().getPath()));

	}

	private void equipmentRecipes(HolderGetter<Item> getter, HolderLookup.Provider provider) {
		bootsItem(getter, TFItems.IRONWOOD_BOOTS, ItemTagGenerator.IRONWOOD_INGOTS, this.buildEnchants(provider, Pair.of(Enchantments.FEATHER_FALLING, 1)));
		leggingsItem(getter, TFItems.IRONWOOD_LEGGINGS, ItemTagGenerator.IRONWOOD_INGOTS, this.buildEnchants(provider, Pair.of(Enchantments.PROTECTION, 1)));
		chestplateItem(getter, TFItems.IRONWOOD_CHESTPLATE, ItemTagGenerator.IRONWOOD_INGOTS, this.buildEnchants(provider, Pair.of(Enchantments.PROTECTION, 1)));
		helmetItem(getter, TFItems.IRONWOOD_HELMET, ItemTagGenerator.IRONWOOD_INGOTS, this.buildEnchants(provider, Pair.of(Enchantments.AQUA_AFFINITY, 1)));
		swordItem(getter, TFItems.IRONWOOD_SWORD, ItemTagGenerator.IRONWOOD_INGOTS, Tags.Items.RODS_WOODEN, this.buildEnchants(provider, Pair.of(Enchantments.KNOCKBACK, 1)));
		pickaxeItem(getter, TFItems.IRONWOOD_PICKAXE, ItemTagGenerator.IRONWOOD_INGOTS, Tags.Items.RODS_WOODEN, this.buildEnchants(provider, Pair.of(Enchantments.EFFICIENCY, 1)));
		axeItem(getter, TFItems.IRONWOOD_AXE, ItemTagGenerator.IRONWOOD_INGOTS, Tags.Items.RODS_WOODEN, this.buildEnchants(provider, Pair.of(Enchantments.FORTUNE, 1)));
		shovelItem(getter, TFItems.IRONWOOD_SHOVEL, ItemTagGenerator.IRONWOOD_INGOTS, Tags.Items.RODS_WOODEN, this.buildEnchants(provider, Pair.of(Enchantments.UNBREAKING, 1)));
		hoeItem(getter, TFItems.IRONWOOD_HOE, ItemTagGenerator.IRONWOOD_INGOTS, Tags.Items.RODS_WOODEN, this.buildEnchants(provider, Pair.of(Enchantments.EFFICIENCY, 1)));

		bootsItem(getter, TFItems.STEELEAF_BOOTS, ItemTagGenerator.STEELEAF_INGOTS, this.buildEnchants(provider, Pair.of(Enchantments.FEATHER_FALLING, 2)));
		leggingsItem(getter, TFItems.STEELEAF_LEGGINGS, ItemTagGenerator.STEELEAF_INGOTS, this.buildEnchants(provider, Pair.of(Enchantments.FIRE_PROTECTION, 2)));
		chestplateItem(getter, TFItems.STEELEAF_CHESTPLATE, ItemTagGenerator.STEELEAF_INGOTS, this.buildEnchants(provider, Pair.of(Enchantments.BLAST_PROTECTION, 2)));
		helmetItem(getter, TFItems.STEELEAF_HELMET, ItemTagGenerator.STEELEAF_INGOTS, this.buildEnchants(provider, Pair.of(Enchantments.PROJECTILE_PROTECTION, 2)));
		swordItem(getter, TFItems.STEELEAF_SWORD, ItemTagGenerator.STEELEAF_INGOTS, Tags.Items.RODS_WOODEN, this.buildEnchants(provider, Pair.of(Enchantments.LOOTING, 2)));
		pickaxeItem(getter, TFItems.STEELEAF_PICKAXE, ItemTagGenerator.STEELEAF_INGOTS, Tags.Items.RODS_WOODEN, this.buildEnchants(provider, Pair.of(Enchantments.FORTUNE, 2)));
		axeItem(getter, TFItems.STEELEAF_AXE, ItemTagGenerator.STEELEAF_INGOTS, Tags.Items.RODS_WOODEN, this.buildEnchants(provider, Pair.of(Enchantments.EFFICIENCY, 2)));
		shovelItem(getter, TFItems.STEELEAF_SHOVEL, ItemTagGenerator.STEELEAF_INGOTS, Tags.Items.RODS_WOODEN, this.buildEnchants(provider, Pair.of(Enchantments.EFFICIENCY, 2)));
		hoeItem(getter, TFItems.STEELEAF_HOE, ItemTagGenerator.STEELEAF_INGOTS, Tags.Items.RODS_WOODEN, this.buildEnchants(provider, Pair.of(Enchantments.FORTUNE, 2)));

		bootsItem(getter, TFItems.ARCTIC_BOOTS, ItemTagGenerator.ARCTIC_FUR);
		chestplateItem(getter, TFItems.ARCTIC_CHESTPLATE, ItemTagGenerator.ARCTIC_FUR);
		helmetItem(getter, TFItems.ARCTIC_HELMET, ItemTagGenerator.ARCTIC_FUR);
		leggingsItem(getter, TFItems.ARCTIC_LEGGINGS, ItemTagGenerator.ARCTIC_FUR);

		bootsItem(getter, TFItems.KNIGHTMETAL_BOOTS, ItemTagGenerator.KNIGHTMETAL_INGOTS);
		chestplateItem(getter, TFItems.KNIGHTMETAL_CHESTPLATE, ItemTagGenerator.KNIGHTMETAL_INGOTS);
		helmetItem(getter, TFItems.KNIGHTMETAL_HELMET, ItemTagGenerator.KNIGHTMETAL_INGOTS);
		leggingsItem(getter, TFItems.KNIGHTMETAL_LEGGINGS, ItemTagGenerator.KNIGHTMETAL_INGOTS);
		pickaxeItem(getter, TFItems.KNIGHTMETAL_PICKAXE, ItemTagGenerator.KNIGHTMETAL_INGOTS, Tags.Items.RODS_WOODEN);
		swordItem(getter, TFItems.KNIGHTMETAL_SWORD, ItemTagGenerator.KNIGHTMETAL_INGOTS, Tags.Items.RODS_WOODEN);
		axeItem(getter, TFItems.KNIGHTMETAL_AXE, ItemTagGenerator.KNIGHTMETAL_INGOTS, Tags.Items.RODS_WOODEN);

		ShapedRecipeBuilder.shaped(getter, RecipeCategory.COMBAT, TFItems.FIERY_BOOTS)
			.pattern("# #")
			.pattern("# #")
			.define('#', ItemTagGenerator.FIERY_INGOTS)
			.unlockedBy("has_item", has(ItemTagGenerator.FIERY_INGOTS))
			.group("fiery_boots")
			.save(this.output, locEquip(TFItems.FIERY_BOOTS.getId().getPath()));

		ShapedRecipeBuilder.shaped(getter, RecipeCategory.COMBAT, TFItems.FIERY_LEGGINGS)
			.pattern("###")
			.pattern("# #")
			.pattern("# #")
			.define('#', ItemTagGenerator.FIERY_INGOTS)
			.unlockedBy("has_item", has(ItemTagGenerator.FIERY_INGOTS))
			.group("fiery_leggings")
			.save(this.output, locEquip(TFItems.FIERY_LEGGINGS.getId().getPath()));

		ShapedRecipeBuilder.shaped(getter, RecipeCategory.COMBAT, TFItems.FIERY_CHESTPLATE)
			.pattern("# #")
			.pattern("###")
			.pattern("###")
			.define('#', ItemTagGenerator.FIERY_INGOTS)
			.unlockedBy("has_item", has(ItemTagGenerator.FIERY_INGOTS))
			.group("fiery_chestplate")
			.save(this.output, locEquip(TFItems.FIERY_CHESTPLATE.getId().getPath()));

		ShapedRecipeBuilder.shaped(getter, RecipeCategory.COMBAT, TFItems.FIERY_HELMET)
			.pattern("###")
			.pattern("# #")
			.define('#', ItemTagGenerator.FIERY_INGOTS)
			.unlockedBy("has_item", has(ItemTagGenerator.FIERY_INGOTS))
			.group("fiery_helmet")
			.save(this.output, locEquip(TFItems.FIERY_HELMET.getId().getPath()));

		ShapedRecipeBuilder.shaped(getter, RecipeCategory.TOOLS, TFItems.FIERY_PICKAXE)
			.pattern("###")
			.pattern(" X ")
			.pattern(" X ")
			.define('#', ItemTagGenerator.FIERY_INGOTS)
			.define('X', Tags.Items.RODS_BLAZE)
			.unlockedBy("has_item", has(ItemTagGenerator.FIERY_INGOTS))
			.group("fiery_pickaxe")
			.save(this.output, locEquip(TFItems.FIERY_PICKAXE.getId().getPath()));

		ShapedRecipeBuilder.shaped(getter, RecipeCategory.COMBAT, TFItems.FIERY_SWORD)
			.pattern("#")
			.pattern("#")
			.pattern("X")
			.define('#', ItemTagGenerator.FIERY_INGOTS)
			.define('X', Tags.Items.RODS_BLAZE)
			.unlockedBy("has_item", has(ItemTagGenerator.FIERY_INGOTS))
			.group("fiery_sword")
			.save(this.output, locEquip(TFItems.FIERY_SWORD.getId().getPath()));

		ShapedRecipeBuilder.shaped(getter, RecipeCategory.COMBAT, new ItemStack(TFItems.NAGA_CHESTPLATE, 1, this.buildEnchants(provider, Pair.of(Enchantments.FIRE_PROTECTION, 3)).build()))
			.pattern("# #")
			.pattern("###")
			.pattern("###")
			.define('#', TFItems.NAGA_SCALE)
			.unlockedBy("has_item", has(TFItems.NAGA_SCALE))
			.save(this.output, locEquip(TFItems.NAGA_CHESTPLATE.getId().getPath()));

		ShapedRecipeBuilder.shaped(getter, RecipeCategory.COMBAT, new ItemStack(TFItems.NAGA_LEGGINGS, 1, this.buildEnchants(provider, Pair.of(Enchantments.PROTECTION, 3)).build()))
			.pattern("###")
			.pattern("# #")
			.pattern("# #")
			.define('#', TFItems.NAGA_SCALE)
			.unlockedBy("has_item", has(TFItems.NAGA_SCALE))
			.save(this.output, locEquip(TFItems.NAGA_LEGGINGS.getId().getPath()));

		ShapedRecipeBuilder.shaped(getter, RecipeCategory.COMBAT, new ItemStack(TFItems.YETI_HELMET, 1, this.buildEnchants(provider, Pair.of(Enchantments.PROTECTION, 2)).build()))
			.pattern("###")
			.pattern("# #")
			.define('#', TFItems.ALPHA_YETI_FUR)
			.unlockedBy("has_item", has(TFItems.ALPHA_YETI_FUR))
			.save(this.output, locEquip(TFItems.YETI_HELMET.getId().getPath()));

		ShapedRecipeBuilder.shaped(getter, RecipeCategory.COMBAT, new ItemStack(TFItems.YETI_CHESTPLATE, 1, this.buildEnchants(provider, Pair.of(Enchantments.PROTECTION, 2)).build()))
			.pattern("# #")
			.pattern("###")
			.pattern("###")
			.define('#', TFItems.ALPHA_YETI_FUR)
			.unlockedBy("has_item", has(TFItems.ALPHA_YETI_FUR))
			.save(this.output, locEquip(TFItems.YETI_CHESTPLATE.getId().getPath()));

		ShapedRecipeBuilder.shaped(getter, RecipeCategory.COMBAT, new ItemStack(TFItems.YETI_LEGGINGS, 1, this.buildEnchants(provider, Pair.of(Enchantments.PROTECTION, 2)).build()))
			.pattern("###")
			.pattern("# #")
			.pattern("# #")
			.define('#', TFItems.ALPHA_YETI_FUR)
			.unlockedBy("has_item", has(TFItems.ALPHA_YETI_FUR))
			.save(this.output, locEquip(TFItems.YETI_LEGGINGS.getId().getPath()));

		ShapedRecipeBuilder.shaped(getter, RecipeCategory.COMBAT, new ItemStack(TFItems.YETI_BOOTS, 1, this.buildEnchants(provider, Pair.of(Enchantments.PROTECTION, 2), Pair.of(Enchantments.FEATHER_FALLING, 4)).build()))
			.pattern("# #")
			.pattern("# #")
			.define('#', TFItems.ALPHA_YETI_FUR)
			.unlockedBy("has_item", has(TFItems.ALPHA_YETI_FUR))
			.save(this.output, locEquip(TFItems.YETI_BOOTS.getId().getPath()));

		ShapedRecipeBuilder.shaped(getter, RecipeCategory.TOOLS, TFItems.GIANT_PICKAXE.get())
			.pattern("###")
			.pattern(" X ")
			.pattern(" X ")
			.define('#', TFBlocks.GIANT_COBBLESTONE.get())
			.define('X', TFBlocks.GIANT_LOG.get())
			.unlockedBy("has_item", has(TFBlocks.GIANT_COBBLESTONE.get()))
			.save(this.output, locEquip(TFItems.GIANT_PICKAXE.getId().getPath()));

		ShapedRecipeBuilder.shaped(getter, RecipeCategory.TOOLS, TFItems.GIANT_SWORD.get())
			.pattern("#")
			.pattern("#")
			.pattern("X")
			.define('#', TFBlocks.GIANT_COBBLESTONE.get())
			.define('X', TFBlocks.GIANT_LOG.get())
			.unlockedBy("has_item", has(TFBlocks.GIANT_COBBLESTONE.get()))
			.save(this.output, locEquip(TFItems.GIANT_SWORD.getId().getPath()));

		charmRecipe(getter, "charm_of_keeping_2", TFItems.CHARM_OF_KEEPING_2, TFItems.CHARM_OF_KEEPING_1);
		charmRecipe(getter, "charm_of_keeping_3", TFItems.CHARM_OF_KEEPING_3, TFItems.CHARM_OF_KEEPING_2);
		charmRecipe(getter, "charm_of_life_2", TFItems.CHARM_OF_LIFE_2, TFItems.CHARM_OF_LIFE_1);

		SpecialRecipeBuilder.special(MoonwormQueenRepairRecipe::new).save(this.output, this.createKey("moonworm_queen_repair_recipe").toString());
		SpecialRecipeBuilder.special(MagicMapCloningRecipe::new).save(this.output, this.createKey("magic_map_cloning_recipe").toString());
		SpecialRecipeBuilder.special(MazeMapCloningRecipe::new).save(this.output, this.createKey("maze_map_cloning_recipe").toString());
		SpecialRecipeBuilder.special(EmperorsClothRecipe::new).save(this.output, this.createKey("emperors_cloth_recipe").toString());

		NoSmithingTemplateRecipeBuilder
			.noTemplate(Ingredient.of(getter.getOrThrow(Tags.Items.ARMORS)), Ingredient.of(TFItems.EMPERORS_CLOTH.get()), RecipeCategory.MISC)
			.attachData(TFDataComponents.EMPERORS_CLOTH::value, Unit.INSTANCE)
			.unlocks("has_cloth", has(TFItems.EMPERORS_CLOTH))
			.save(this.output, this.createKey("emperors_cloth_smithing"));

		ShapelessRecipeBuilder.shapeless(getter, RecipeCategory.BUILDING_BLOCKS, Blocks.COBBLESTONE, 64)
			.requires(TFBlocks.GIANT_COBBLESTONE.get())
			.unlockedBy("has_item", has(TFBlocks.GIANT_COBBLESTONE.get()))
			.save(this.output, this.createKey(TFBlocks.GIANT_COBBLESTONE.getId().getPath() + "_to_" + BuiltInRegistries.ITEM.getKey(Items.COBBLESTONE).getPath()));

		ShapelessRecipeBuilder.shapeless(getter, RecipeCategory.BUILDING_BLOCKS, Blocks.OAK_PLANKS, 64)
			.requires(TFBlocks.GIANT_LOG.get())
			.unlockedBy("has_item", has(TFBlocks.GIANT_LOG.get()))
			.save(this.output, this.createKey(TFBlocks.GIANT_LOG.getId().getPath() + "_to_" + BuiltInRegistries.ITEM.getKey(Items.OAK_PLANKS).getPath()));

		ShapelessRecipeBuilder.shapeless(getter, RecipeCategory.BUILDING_BLOCKS, Blocks.OAK_LEAVES, 64)
			.requires(TFBlocks.GIANT_LEAVES.get())
			.unlockedBy("has_item", has(TFBlocks.GIANT_LEAVES.get()))
			.save(this.output, this.createKey(TFBlocks.GIANT_LEAVES.getId().getPath() + "_to_" + BuiltInRegistries.ITEM.getKey(Items.OAK_LEAVES).getPath()));

		ShapelessRecipeBuilder.shapeless(getter, RecipeCategory.BUILDING_BLOCKS, Blocks.OBSIDIAN, 64)
			.requires(TFBlocks.GIANT_OBSIDIAN.get())
			.unlockedBy("has_item", has(TFBlocks.GIANT_OBSIDIAN.get()))
			.save(this.output, this.createKey(TFBlocks.GIANT_OBSIDIAN.getId().getPath() + "_to_" + BuiltInRegistries.ITEM.getKey(Items.OBSIDIAN).getPath()));

		ShapelessRecipeBuilder.shapeless(getter, RecipeCategory.COMBAT, TFItems.BLOCK_AND_CHAIN.get())
			.requires(Ingredient.of(getter.getOrThrow(ItemTagGenerator.STORAGE_BLOCKS_KNIGHTMETAL)))
			.requires(Ingredient.of(getter.getOrThrow(ItemTagGenerator.KNIGHTMETAL_INGOTS)), 3)
			.requires(Ingredient.of(TFItems.KNIGHTMETAL_RING.get()))
			.unlockedBy("has_block", has(ItemTagGenerator.STORAGE_BLOCKS_KNIGHTMETAL))
			.unlockedBy("has_ingot", has(ItemTagGenerator.KNIGHTMETAL_INGOTS))
			.unlockedBy("has_ring", has(TFItems.KNIGHTMETAL_RING.get()))
			.save(this.output, locEquip(TFItems.BLOCK_AND_CHAIN.getId().getPath()));

		ShapedRecipeBuilder.shaped(getter, RecipeCategory.MISC, TFItems.KNIGHTMETAL_RING.get())
			.pattern(" - ")
			.pattern("- -")
			.pattern(" - ")
			.define('-', ItemTagGenerator.KNIGHTMETAL_INGOTS)
			.unlockedBy("has_item", has(ItemTagGenerator.KNIGHTMETAL_INGOTS))
			.save(this.output, locEquip(TFItems.KNIGHTMETAL_RING.getId().getPath()));

		ShapedRecipeBuilder.shaped(getter, RecipeCategory.COMBAT, TFItems.KNIGHTMETAL_SHIELD.get())
			.pattern("-#")
			.pattern("-o")
			.pattern("-#")
			.define('-', ItemTagGenerator.KNIGHTMETAL_INGOTS)
			.define('#', ItemTagGenerator.TOWERWOOD)
			.define('o', Ingredient.of(TFItems.KNIGHTMETAL_RING.get()))
			.unlockedBy("has_ingot", has(ItemTagGenerator.KNIGHTMETAL_INGOTS))
			.unlockedBy("has_ring", has(TFItems.KNIGHTMETAL_RING.get()))
			.save(this.output, locEquip(TFItems.KNIGHTMETAL_SHIELD.getId().getPath()));

		ScepterRecipeBuilder.repairFor(getter, TFItems.LIFEDRAIN_SCEPTER.get(), 9)
			.addRepairIngredient(Items.FERMENTED_SPIDER_EYE)
			.save(this.output, locEquip(TFItems.LIFEDRAIN_SCEPTER.getId().getPath()));

		ScepterRecipeBuilder.repairFor(getter, TFItems.FORTIFICATION_SCEPTER.get(), 9)
			.addRepairIngredient(Ingredient.of(Items.GOLDEN_APPLE))
			.save(this.output, locEquip(TFItems.FORTIFICATION_SCEPTER.getId().getPath()));

		ScepterRecipeBuilder.repairFor(getter, TFItems.TWILIGHT_SCEPTER.get(), 9)
			.addRepairIngredient(Tags.Items.ENDER_PEARLS)
			.save(this.output, locEquip(TFItems.TWILIGHT_SCEPTER.getId().getPath()));

		ScepterRecipeBuilder.repairFor(getter, TFItems.ZOMBIE_SCEPTER.get(), 9)
			.addRepairIngredient(CompoundIngredient.of(
				DataComponentIngredient.of(false, DataComponents.POTION_CONTENTS, new PotionContents(Potions.STRENGTH), Items.POTION),
				DataComponentIngredient.of(false, DataComponents.POTION_CONTENTS, new PotionContents(Potions.LONG_STRENGTH), Items.POTION),
				DataComponentIngredient.of(false, DataComponents.POTION_CONTENTS, new PotionContents(Potions.STRONG_STRENGTH), Items.POTION)
			))
			.addRepairIngredient(Items.ROTTEN_FLESH)
			.save(this.output, locEquip(TFItems.ZOMBIE_SCEPTER.getId().getPath()));
	}

	private void blockCompressionRecipes(HolderGetter<Item> getter) {
		reverseCompressBlock(getter, "arctic_block_to_item", TFItems.ARCTIC_FUR, ItemTagGenerator.STORAGE_BLOCKS_ARCTIC_FUR);
		reverseCompressBlock(getter, "carminite_block_to_item", TFItems.CARMINITE, ItemTagGenerator.STORAGE_BLOCKS_CARMINITE);
		reverseCompressBlock(getter, "ironwood_block_ingot", TFItems.IRONWOOD_INGOT, ItemTagGenerator.STORAGE_BLOCKS_IRONWOOD);
		reverseCompressBlock(getter, "knightmetal_block_ingot", TFItems.KNIGHTMETAL_INGOT, ItemTagGenerator.STORAGE_BLOCKS_KNIGHTMETAL);
		reverseCompressBlock(getter, "steeleaf_block_ingot", TFItems.STEELEAF_INGOT, ItemTagGenerator.STORAGE_BLOCKS_STEELEAF);

		ShapelessRecipeBuilder.shapeless(getter, RecipeCategory.MISC, TFItems.FIERY_INGOT, 9)
			.requires(ItemTagGenerator.STORAGE_BLOCKS_FIERY)
			.unlockedBy("has_item", has(ItemTagGenerator.STORAGE_BLOCKS_FIERY))
			.group("fiery_ingot")
			.save(this.output, this.createKey("compressed_blocks/reversed/fiery_block_to_ingot"));

		compressedBlock(getter, "arctic_block", TFBlocks.ARCTIC_FUR_BLOCK, ItemTagGenerator.ARCTIC_FUR);
		compressedBlock(getter, "carminite_block", TFBlocks.CARMINITE_BLOCK, ItemTagGenerator.CARMINITE_GEMS);
		compressedBlock(getter, "fiery_block", TFBlocks.FIERY_BLOCK, ItemTagGenerator.FIERY_INGOTS);
		compressedBlock(getter, "ironwood_block", TFBlocks.IRONWOOD_BLOCK, ItemTagGenerator.IRONWOOD_INGOTS);
		compressedBlock(getter, "knightmetal_block", TFBlocks.KNIGHTMETAL_BLOCK, ItemTagGenerator.KNIGHTMETAL_INGOTS);
		compressedBlock(getter, "steeleaf_block", TFBlocks.STEELEAF_BLOCK, ItemTagGenerator.STEELEAF_INGOTS);
	}

	private void emptyMapRecipes(HolderGetter<Item> getter) {
		ShapelessRecipeBuilder.shapeless(getter, RecipeCategory.MISC, TFItems.MAGIC_MAP_FOCUS.get())
			.requires(TFItems.RAVEN_FEATHER.get())
			.requires(TFItems.TORCHBERRIES.get())
			.requires(Tags.Items.DUSTS_GLOWSTONE)
			.unlockedBy("has_berries", has(TFItems.TORCHBERRIES.get()))
			.unlockedBy("has_feather", has(TFItems.RAVEN_FEATHER.get()))
			.save(this.output);

		ShapedRecipeBuilder.shaped(getter, RecipeCategory.MISC, TFItems.MAGIC_MAP.get())
			.pattern("###")
			.pattern("#•#")
			.pattern("###")
			.define('#', ItemTagGenerator.PAPER)
			.define('•', Ingredient.of(TFItems.MAGIC_MAP_FOCUS.get()))
			.unlockedBy("has_item", has(TFItems.MAGIC_MAP_FOCUS.get()))
			.save(this.output);

		ShapedRecipeBuilder.shaped(getter, RecipeCategory.MISC, TFItems.MAZE_MAP.get())
			.pattern("###")
			.pattern("#•#")
			.pattern("###")
			.define('#', ItemTagGenerator.PAPER)
			.define('•', Ingredient.of(TFItems.MAZE_MAP_FOCUS.get()))
			.unlockedBy("has_item", has(TFItems.MAZE_MAP_FOCUS.get()))
			.save(this.output);

		ShapelessRecipeBuilder.shapeless(getter, RecipeCategory.MISC, TFItems.ORE_MAP.get())
			.requires(TFItems.MAZE_MAP.get())
			.requires(Tags.Items.STORAGE_BLOCKS_DIAMOND)
			.requires(Tags.Items.STORAGE_BLOCKS_GOLD)
			.requires(Tags.Items.STORAGE_BLOCKS_IRON)
			.unlockedBy("has_item", has(TFItems.MAZE_MAP.get()))
			.save(this.output);
	}

	private void woodRecipes(HolderGetter<Item> getter) {
		buttonBlock(getter, "canopy", TFBlocks.CANOPY_BUTTON, TFBlocks.CANOPY_PLANKS);
		buttonBlock(getter, "dark", TFBlocks.DARK_BUTTON, TFBlocks.DARK_PLANKS);
		buttonBlock(getter, "mangrove", TFBlocks.MANGROVE_BUTTON, TFBlocks.MANGROVE_PLANKS);
		buttonBlock(getter, "mining", TFBlocks.MINING_BUTTON, TFBlocks.MINING_PLANKS);
		buttonBlock(getter, "sorting", TFBlocks.SORTING_BUTTON, TFBlocks.SORTING_PLANKS);
		buttonBlock(getter, "time", TFBlocks.TIME_BUTTON, TFBlocks.TIME_PLANKS);
		buttonBlock(getter, "transformation", TFBlocks.TRANSFORMATION_BUTTON, TFBlocks.TRANSFORMATION_PLANKS);
		buttonBlock(getter, "twilight_oak", TFBlocks.TWILIGHT_OAK_BUTTON, TFBlocks.TWILIGHT_OAK_PLANKS);

		doorBlock(getter, "canopy", TFBlocks.CANOPY_DOOR, TFBlocks.CANOPY_PLANKS);
		doorBlock(getter, "dark", TFBlocks.DARK_DOOR, TFBlocks.DARK_PLANKS);
		doorBlock(getter, "mangrove", TFBlocks.MANGROVE_DOOR, TFBlocks.MANGROVE_PLANKS);
		doorBlock(getter, "mining", TFBlocks.MINING_DOOR, TFBlocks.MINING_PLANKS);
		doorBlock(getter, "sorting", TFBlocks.SORTING_DOOR, TFBlocks.SORTING_PLANKS);
		doorBlock(getter, "time", TFBlocks.TIME_DOOR, TFBlocks.TIME_PLANKS);
		doorBlock(getter, "transformation", TFBlocks.TRANSFORMATION_DOOR, TFBlocks.TRANSFORMATION_PLANKS);
		doorBlock(getter, "twilight_oak", TFBlocks.TWILIGHT_OAK_DOOR, TFBlocks.TWILIGHT_OAK_PLANKS);

		fenceBlock(getter, "canopy", TFBlocks.CANOPY_FENCE, TFBlocks.CANOPY_PLANKS);
		fenceBlock(getter, "dark", TFBlocks.DARK_FENCE, TFBlocks.DARK_PLANKS);
		fenceBlock(getter, "mangrove", TFBlocks.MANGROVE_FENCE, TFBlocks.MANGROVE_PLANKS);
		fenceBlock(getter, "mining", TFBlocks.MINING_FENCE, TFBlocks.MINING_PLANKS);
		fenceBlock(getter, "sorting", TFBlocks.SORTING_FENCE, TFBlocks.SORTING_PLANKS);
		fenceBlock(getter, "time", TFBlocks.TIME_FENCE, TFBlocks.TIME_PLANKS);
		fenceBlock(getter, "transformation", TFBlocks.TRANSFORMATION_FENCE, TFBlocks.TRANSFORMATION_PLANKS);
		fenceBlock(getter, "twilight_oak", TFBlocks.TWILIGHT_OAK_FENCE, TFBlocks.TWILIGHT_OAK_PLANKS);

		gateBlock(getter, "canopy", TFBlocks.CANOPY_GATE, TFBlocks.CANOPY_PLANKS);
		gateBlock(getter, "dark", TFBlocks.DARK_GATE, TFBlocks.DARK_PLANKS);
		gateBlock(getter, "mangrove", TFBlocks.MANGROVE_GATE, TFBlocks.MANGROVE_PLANKS);
		gateBlock(getter, "mining", TFBlocks.MINING_GATE, TFBlocks.MINING_PLANKS);
		gateBlock(getter, "sorting", TFBlocks.SORTING_GATE, TFBlocks.SORTING_PLANKS);
		gateBlock(getter, "time", TFBlocks.TIME_GATE, TFBlocks.TIME_PLANKS);
		gateBlock(getter, "transformation", TFBlocks.TRANSFORMATION_GATE, TFBlocks.TRANSFORMATION_PLANKS);
		gateBlock(getter, "twilight_oak", TFBlocks.TWILIGHT_OAK_GATE, TFBlocks.TWILIGHT_OAK_PLANKS);

		planksBlock(getter, "canopy", TFBlocks.CANOPY_PLANKS, ItemTagGenerator.CANOPY_LOGS);
		planksBlock(getter, "dark", TFBlocks.DARK_PLANKS, ItemTagGenerator.DARKWOOD_LOGS);
		planksBlock(getter, "mangrove", TFBlocks.MANGROVE_PLANKS, ItemTagGenerator.MANGROVE_LOGS);
		planksBlock(getter, "mining", TFBlocks.MINING_PLANKS, ItemTagGenerator.MINING_LOGS);
		planksBlock(getter, "sorting", TFBlocks.SORTING_PLANKS, ItemTagGenerator.SORTING_LOGS);
		planksBlock(getter, "time", TFBlocks.TIME_PLANKS, ItemTagGenerator.TIME_LOGS);
		planksBlock(getter, "transformation", TFBlocks.TRANSFORMATION_PLANKS, ItemTagGenerator.TRANSFORMATION_LOGS);
		planksBlock(getter, "twilight_oak", TFBlocks.TWILIGHT_OAK_PLANKS, ItemTagGenerator.TWILIGHT_OAK_LOGS);

		woodBlock(getter, "canopy", TFBlocks.CANOPY_WOOD, TFBlocks.CANOPY_LOG);
		woodBlock(getter, "dark", TFBlocks.DARK_WOOD, TFBlocks.DARK_LOG);
		woodBlock(getter, "mangrove", TFBlocks.MANGROVE_WOOD, TFBlocks.MANGROVE_LOG);
		woodBlock(getter, "mining", TFBlocks.MINING_WOOD, TFBlocks.MINING_LOG);
		woodBlock(getter, "sorting", TFBlocks.SORTING_WOOD, TFBlocks.SORTING_LOG);
		woodBlock(getter, "time", TFBlocks.TIME_WOOD, TFBlocks.TIME_LOG);
		woodBlock(getter, "transformation", TFBlocks.TRANSFORMATION_WOOD, TFBlocks.TRANSFORMATION_LOG);
		woodBlock(getter, "twilight_oak", TFBlocks.TWILIGHT_OAK_WOOD, TFBlocks.TWILIGHT_OAK_LOG);

		strippedWoodBlock(getter, "canopy", TFBlocks.STRIPPED_CANOPY_WOOD, TFBlocks.STRIPPED_CANOPY_LOG);
		strippedWoodBlock(getter, "dark", TFBlocks.STRIPPED_DARK_WOOD, TFBlocks.STRIPPED_DARK_LOG);
		strippedWoodBlock(getter, "mangrove", TFBlocks.STRIPPED_MANGROVE_WOOD, TFBlocks.STRIPPED_MANGROVE_LOG);
		strippedWoodBlock(getter, "mining", TFBlocks.STRIPPED_MINING_WOOD, TFBlocks.STRIPPED_MINING_LOG);
		strippedWoodBlock(getter, "sorting", TFBlocks.STRIPPED_SORTING_WOOD, TFBlocks.STRIPPED_SORTING_LOG);
		strippedWoodBlock(getter, "time", TFBlocks.STRIPPED_TIME_WOOD, TFBlocks.STRIPPED_TIME_LOG);
		strippedWoodBlock(getter, "transformation", TFBlocks.STRIPPED_TRANSFORMATION_WOOD, TFBlocks.STRIPPED_TRANSFORMATION_LOG);
		strippedWoodBlock(getter, "twilight_oak", TFBlocks.STRIPPED_TWILIGHT_OAK_WOOD, TFBlocks.STRIPPED_TWILIGHT_OAK_LOG);

		plateBlock(getter, "canopy", TFBlocks.CANOPY_PLATE, TFBlocks.CANOPY_PLANKS);
		plateBlock(getter, "dark", TFBlocks.DARK_PLATE, TFBlocks.DARK_PLANKS);
		plateBlock(getter, "mangrove", TFBlocks.MANGROVE_PLATE, TFBlocks.MANGROVE_PLANKS);
		plateBlock(getter, "mining", TFBlocks.MINING_PLATE, TFBlocks.MINING_PLANKS);
		plateBlock(getter, "sorting", TFBlocks.SORTING_PLATE, TFBlocks.SORTING_PLANKS);
		plateBlock(getter, "time", TFBlocks.TIME_PLATE, TFBlocks.TIME_PLANKS);
		plateBlock(getter, "transformation", TFBlocks.TRANSFORMATION_PLATE, TFBlocks.TRANSFORMATION_PLANKS);
		plateBlock(getter, "twilight_oak", TFBlocks.TWILIGHT_OAK_PLATE, TFBlocks.TWILIGHT_OAK_PLANKS);

		woodenSlabBlock(getter, "canopy", TFBlocks.CANOPY_SLAB, TFBlocks.CANOPY_PLANKS);
		woodenSlabBlock(getter, "dark", TFBlocks.DARK_SLAB, TFBlocks.DARK_PLANKS);
		woodenSlabBlock(getter, "mangrove", TFBlocks.MANGROVE_SLAB, TFBlocks.MANGROVE_PLANKS);
		woodenSlabBlock(getter, "mining", TFBlocks.MINING_SLAB, TFBlocks.MINING_PLANKS);
		woodenSlabBlock(getter, "sorting", TFBlocks.SORTING_SLAB, TFBlocks.SORTING_PLANKS);
		woodenSlabBlock(getter, "time", TFBlocks.TIME_SLAB, TFBlocks.TIME_PLANKS);
		woodenSlabBlock(getter, "transformation", TFBlocks.TRANSFORMATION_SLAB, TFBlocks.TRANSFORMATION_PLANKS);
		woodenSlabBlock(getter, "twilight_oak", TFBlocks.TWILIGHT_OAK_SLAB, TFBlocks.TWILIGHT_OAK_PLANKS);

		woodenStairsBlock(getter, locWood("canopy_stairs"), TFBlocks.CANOPY_STAIRS, TFBlocks.CANOPY_PLANKS, TFBlocks.CANOPY_PLANKS.get());
		woodenStairsBlock(getter, locWood("dark_stairs"), TFBlocks.DARK_STAIRS, TFBlocks.DARK_PLANKS, TFBlocks.DARK_PLANKS.get());
		woodenStairsBlock(getter, locWood("mangrove_stairs"), TFBlocks.MANGROVE_STAIRS, TFBlocks.MANGROVE_PLANKS, TFBlocks.MANGROVE_PLANKS.get());
		woodenStairsBlock(getter, locWood("mining_stairs"), TFBlocks.MINING_STAIRS, TFBlocks.MINING_PLANKS, TFBlocks.MINING_PLANKS.get());
		woodenStairsBlock(getter, locWood("sorting_stairs"), TFBlocks.SORTING_STAIRS, TFBlocks.SORTING_PLANKS, TFBlocks.SORTING_PLANKS.get());
		woodenStairsBlock(getter, locWood("time_stairs"), TFBlocks.TIME_STAIRS, TFBlocks.TIME_PLANKS, TFBlocks.TIME_PLANKS.get());
		woodenStairsBlock(getter, locWood("transformation_stairs"), TFBlocks.TRANSFORMATION_STAIRS, TFBlocks.TRANSFORMATION_PLANKS, TFBlocks.TRANSFORMATION_PLANKS.get());
		woodenStairsBlock(getter, locWood("twilight_oak_stairs"), TFBlocks.TWILIGHT_OAK_STAIRS, TFBlocks.TWILIGHT_OAK_PLANKS, TFBlocks.TWILIGHT_OAK_PLANKS.get());

		trapdoorBlock(getter, "canopy", TFBlocks.CANOPY_TRAPDOOR, TFBlocks.CANOPY_PLANKS);
		trapdoorBlock(getter, "dark", TFBlocks.DARK_TRAPDOOR, TFBlocks.DARK_PLANKS);
		trapdoorBlock(getter, "mangrove", TFBlocks.MANGROVE_TRAPDOOR, TFBlocks.MANGROVE_PLANKS);
		trapdoorBlock(getter, "mining", TFBlocks.MINING_TRAPDOOR, TFBlocks.MINING_PLANKS);
		trapdoorBlock(getter, "sorting", TFBlocks.SORTING_TRAPDOOR, TFBlocks.SORTING_PLANKS);
		trapdoorBlock(getter, "time", TFBlocks.TIME_TRAPDOOR, TFBlocks.TIME_PLANKS);
		trapdoorBlock(getter, "transformation", TFBlocks.TRANSFORMATION_TRAPDOOR, TFBlocks.TRANSFORMATION_PLANKS);
		trapdoorBlock(getter, "twilight_oak", TFBlocks.TWILIGHT_OAK_TRAPDOOR, TFBlocks.TWILIGHT_OAK_PLANKS);

		signBlock(getter, "canopy", TFItems.CANOPY_SIGN, TFBlocks.CANOPY_PLANKS);
		signBlock(getter, "dark", TFItems.DARK_SIGN, TFBlocks.DARK_PLANKS);
		signBlock(getter, "mangrove", TFItems.MANGROVE_SIGN, TFBlocks.MANGROVE_PLANKS);
		signBlock(getter, "mining", TFItems.MINING_SIGN, TFBlocks.MINING_PLANKS);
		signBlock(getter, "sorting", TFItems.SORTING_SIGN, TFBlocks.SORTING_PLANKS);
		signBlock(getter, "time", TFItems.TIME_SIGN, TFBlocks.TIME_PLANKS);
		signBlock(getter, "transformation", TFItems.TRANSFORMATION_SIGN, TFBlocks.TRANSFORMATION_PLANKS);
		signBlock(getter, "twilight_oak", TFItems.TWILIGHT_OAK_SIGN, TFBlocks.TWILIGHT_OAK_PLANKS);

		hangingSignBlock(getter, "canopy", TFItems.CANOPY_HANGING_SIGN, TFBlocks.STRIPPED_CANOPY_LOG);
		hangingSignBlock(getter, "dark", TFItems.DARK_HANGING_SIGN, TFBlocks.STRIPPED_DARK_LOG);
		hangingSignBlock(getter, "mangrove", TFItems.MANGROVE_HANGING_SIGN, TFBlocks.STRIPPED_MANGROVE_LOG);
		hangingSignBlock(getter, "mining", TFItems.MINING_HANGING_SIGN, TFBlocks.STRIPPED_MINING_LOG);
		hangingSignBlock(getter, "sorting", TFItems.SORTING_HANGING_SIGN, TFBlocks.STRIPPED_SORTING_LOG);
		hangingSignBlock(getter, "time", TFItems.TIME_HANGING_SIGN, TFBlocks.STRIPPED_TIME_LOG);
		hangingSignBlock(getter, "transformation", TFItems.TRANSFORMATION_HANGING_SIGN, TFBlocks.STRIPPED_TRANSFORMATION_LOG);
		hangingSignBlock(getter, "twilight_oak", TFItems.TWILIGHT_OAK_HANGING_SIGN, TFBlocks.STRIPPED_TWILIGHT_OAK_LOG);

		banisterBlock(getter, "canopy", TFBlocks.CANOPY_BANISTER, TFBlocks.CANOPY_SLAB);
		banisterBlock(getter, "dark", TFBlocks.DARK_BANISTER, TFBlocks.DARK_SLAB);
		banisterBlock(getter, "mangrove", TFBlocks.MANGROVE_BANISTER, TFBlocks.MANGROVE_SLAB);
		banisterBlock(getter, "mining", TFBlocks.MINING_BANISTER, TFBlocks.MINING_SLAB);
		banisterBlock(getter, "sorting", TFBlocks.SORTING_BANISTER, TFBlocks.SORTING_SLAB);
		banisterBlock(getter, "time", TFBlocks.TIME_BANISTER, TFBlocks.TIME_SLAB);
		banisterBlock(getter, "transformation", TFBlocks.TRANSFORMATION_BANISTER, TFBlocks.TRANSFORMATION_SLAB);
		banisterBlock(getter, "twilight_oak", TFBlocks.TWILIGHT_OAK_BANISTER, TFBlocks.TWILIGHT_OAK_SLAB);

		banisterBlock(getter, "oak", TFBlocks.OAK_BANISTER, Blocks.OAK_SLAB);
		banisterBlock(getter, "spruce", TFBlocks.SPRUCE_BANISTER, Blocks.SPRUCE_SLAB);
		banisterBlock(getter, "birch", TFBlocks.BIRCH_BANISTER, Blocks.BIRCH_SLAB);
		banisterBlock(getter, "jungle", TFBlocks.JUNGLE_BANISTER, Blocks.JUNGLE_SLAB);
		banisterBlock(getter, "acacia", TFBlocks.ACACIA_BANISTER, Blocks.ACACIA_SLAB);
		banisterBlock(getter, "dark_oak", TFBlocks.DARK_OAK_BANISTER, Blocks.DARK_OAK_SLAB);
		banisterBlock(getter, "crimson", TFBlocks.CRIMSON_BANISTER, Blocks.CRIMSON_SLAB);
		banisterBlock(getter, "warped", TFBlocks.WARPED_BANISTER, Blocks.WARPED_SLAB);
		banisterBlock(getter, "vangrove", TFBlocks.VANGROVE_BANISTER, Blocks.MANGROVE_SLAB);
		banisterBlock(getter, "bamboo", TFBlocks.BAMBOO_BANISTER, Blocks.BAMBOO_SLAB);
		banisterBlock(getter, "cherry", TFBlocks.CHERRY_BANISTER, Blocks.CHERRY_SLAB);

		chestBlock(getter, "twilight_oak", TFBlocks.TWILIGHT_OAK_CHEST, TFBlocks.TWILIGHT_OAK_TRAPPED_CHEST, TFBlocks.TWILIGHT_OAK_PLANKS);
		chestBlock(getter, "canopy", TFBlocks.CANOPY_CHEST, TFBlocks.CANOPY_TRAPPED_CHEST, TFBlocks.CANOPY_PLANKS);
		chestBlock(getter, "mangrove", TFBlocks.MANGROVE_CHEST, TFBlocks.MANGROVE_TRAPPED_CHEST, TFBlocks.MANGROVE_PLANKS);
		chestBlock(getter, "dark", TFBlocks.DARK_CHEST, TFBlocks.DARK_TRAPPED_CHEST, TFBlocks.DARK_PLANKS);
		chestBlock(getter, "time", TFBlocks.TIME_CHEST, TFBlocks.TIME_TRAPPED_CHEST, TFBlocks.TIME_PLANKS);
		chestBlock(getter, "transformation", TFBlocks.TRANSFORMATION_CHEST, TFBlocks.TRANSFORMATION_TRAPPED_CHEST, TFBlocks.TRANSFORMATION_PLANKS);
		chestBlock(getter, "mining", TFBlocks.MINING_CHEST, TFBlocks.MINING_TRAPPED_CHEST, TFBlocks.MINING_PLANKS);
		chestBlock(getter, "sorting", TFBlocks.SORTING_CHEST, TFBlocks.SORTING_TRAPPED_CHEST, TFBlocks.SORTING_PLANKS);

		buildBoats(getter, TFItems.TWILIGHT_OAK_BOAT, TFItems.TWILIGHT_OAK_CHEST_BOAT, TFBlocks.TWILIGHT_OAK_PLANKS);
		buildBoats(getter, TFItems.CANOPY_BOAT, TFItems.CANOPY_CHEST_BOAT, TFBlocks.CANOPY_PLANKS);
		buildBoats(getter, TFItems.MANGROVE_BOAT, TFItems.MANGROVE_CHEST_BOAT, TFBlocks.MANGROVE_PLANKS);
		buildBoats(getter, TFItems.DARK_BOAT, TFItems.DARK_CHEST_BOAT, TFBlocks.DARK_PLANKS);
		buildBoats(getter, TFItems.TIME_BOAT, TFItems.TIME_CHEST_BOAT, TFBlocks.TIME_PLANKS);
		buildBoats(getter, TFItems.TRANSFORMATION_BOAT, TFItems.TRANSFORMATION_CHEST_BOAT, TFBlocks.TRANSFORMATION_PLANKS);
		buildBoats(getter, TFItems.MINING_BOAT, TFItems.MINING_CHEST_BOAT, TFBlocks.MINING_PLANKS);
		buildBoats(getter, TFItems.SORTING_BOAT, TFItems.SORTING_CHEST_BOAT, TFBlocks.SORTING_PLANKS);
	}

	private void nagastoneRecipes(HolderGetter<Item> getter) {
		ShapedRecipeBuilder.shaped(getter, RecipeCategory.BUILDING_BLOCKS, TFBlocks.SPIRAL_BRICKS.get(), 8)
			.pattern("BSS")
			.pattern("BSS")
			.pattern("BBB")
			.define('B', Ingredient.of(Blocks.STONE_BRICKS, Blocks.MOSSY_STONE_BRICKS, Blocks.CRACKED_STONE_BRICKS, Blocks.CHISELED_STONE_BRICKS))//Ingredient.merge(ImmutableList.of(Ingredient.fromTag(Tags.Items.STONE), Ingredient.fromItems(Blocks.STONE_BRICKS, Blocks.MOSSY_STONE_BRICKS, Blocks.CRACKED_STONE_BRICKS, Blocks.CHISELED_STONE_BRICKS))))
			.define('S', Ingredient.of(Blocks.STONE_SLAB, Blocks.STONE_BRICK_SLAB))
			.unlockedBy("has_item", has(TFBlocks.SPIRAL_BRICKS.get()))
			.save(this.output, locNaga("nagastone_spiral"));

		stairsBlock(getter, locNaga("nagastone_stairs_left"), TFBlocks.NAGASTONE_STAIRS_LEFT, TFBlocks.ETCHED_NAGASTONE, TFBlocks.ETCHED_NAGASTONE.get());
		stairsRightBlock(getter, locNaga("nagastone_stairs_right"), TFBlocks.NAGASTONE_STAIRS_RIGHT, TFBlocks.ETCHED_NAGASTONE, TFBlocks.ETCHED_NAGASTONE.get());

		stairsBlock(getter, locNaga("mossy_nagastone_stairs_left"), TFBlocks.MOSSY_NAGASTONE_STAIRS_LEFT, TFBlocks.MOSSY_ETCHED_NAGASTONE, TFBlocks.MOSSY_ETCHED_NAGASTONE.get());
		stairsRightBlock(getter, locNaga("mossy_nagastone_stairs_right"), TFBlocks.MOSSY_NAGASTONE_STAIRS_RIGHT, TFBlocks.MOSSY_ETCHED_NAGASTONE, TFBlocks.MOSSY_ETCHED_NAGASTONE.get());

		ShapelessRecipeBuilder.shapeless(getter, RecipeCategory.BUILDING_BLOCKS, TFBlocks.MOSSY_ETCHED_NAGASTONE.get(), 1)
			.requires(Ingredient.of(Blocks.VINE, Blocks.MOSS_BLOCK))
			.requires(Ingredient.of(TFBlocks.ETCHED_NAGASTONE.get()))
			.unlockedBy("has_item", has(TFBlocks.ETCHED_NAGASTONE.get()))
			.save(this.output, locNaga("mossy_etched_nagastone"));

		ShapelessRecipeBuilder.shapeless(getter, RecipeCategory.BUILDING_BLOCKS, TFBlocks.MOSSY_NAGASTONE_PILLAR.get(), 1)
			.requires(Ingredient.of(Blocks.VINE, Blocks.MOSS_BLOCK))
			.requires(Ingredient.of(TFBlocks.NAGASTONE_PILLAR.get()))
			.unlockedBy("has_item", has(TFBlocks.NAGASTONE_PILLAR.get()))
			.save(this.output, locNaga("mossy_nagastone_pillar"));

		stairsBlock(getter, locNaga("cracked_nagastone_stairs_left"), TFBlocks.CRACKED_NAGASTONE_STAIRS_LEFT, TFBlocks.CRACKED_ETCHED_NAGASTONE, TFBlocks.CRACKED_ETCHED_NAGASTONE.get());
		stairsRightBlock(getter, locNaga("cracked_nagastone_stairs_right"), TFBlocks.CRACKED_NAGASTONE_STAIRS_RIGHT, TFBlocks.CRACKED_ETCHED_NAGASTONE, TFBlocks.CRACKED_ETCHED_NAGASTONE.get());
	}

	private void castleRecipes(HolderGetter<Item> getter) {
		ShapelessRecipeBuilder.shapeless(getter, RecipeCategory.BUILDING_BLOCKS, TFBlocks.MOSSY_CASTLE_BRICK.get(), 1)
			.requires(Ingredient.of(Blocks.VINE, Blocks.MOSS_BLOCK))
			.requires(Ingredient.of(TFBlocks.CASTLE_BRICK.get()))
			.unlockedBy("has_item", has(TFBlocks.CASTLE_BRICK.get()))
			.save(this.output, locCastle("mossy_castle_brick"));

		castleBlock(getter, TFBlocks.THICK_CASTLE_BRICK, TFBlocks.CASTLE_BRICK.get(), TFBlocks.WORN_CASTLE_BRICK.get(), TFBlocks.CRACKED_CASTLE_BRICK.get(), TFBlocks.MOSSY_CASTLE_BRICK.get());
		castleBlock(getter, TFBlocks.BOLD_CASTLE_BRICK_PILLAR, TFBlocks.THICK_CASTLE_BRICK.get());
		castleBlock(getter, TFBlocks.BOLD_CASTLE_BRICK_TILE, TFBlocks.BOLD_CASTLE_BRICK_PILLAR.get());

		ShapedRecipeBuilder.shaped(getter, RecipeCategory.BUILDING_BLOCKS, TFBlocks.BOLD_CASTLE_BRICK_PILLAR.get(), 4)
			.pattern("##")
			.pattern("##")
			.define('#', Ingredient.of(TFBlocks.BOLD_CASTLE_BRICK_TILE.get()))
			.unlockedBy("has_castle_brick", has(TFBlocks.CASTLE_BRICK.get()))
			.save(this.output, locCastle("bold_castle_pillar_from_tile"));

		ShapedRecipeBuilder.shaped(getter, RecipeCategory.BUILDING_BLOCKS, TFBlocks.ENCASED_CASTLE_BRICK_PILLAR.get(), 6)
			.pattern("#H#")
			.pattern("#H#")
			.define('#', Ingredient.of(TFBlocks.CASTLE_BRICK.get(), TFBlocks.WORN_CASTLE_BRICK.get(), TFBlocks.CRACKED_CASTLE_BRICK.get(), TFBlocks.MOSSY_CASTLE_BRICK.get(), TFBlocks.THICK_CASTLE_BRICK.get()))
			.define('H', Ingredient.of(TFBlocks.ENCASED_CASTLE_BRICK_PILLAR.get(), TFBlocks.ENCASED_CASTLE_BRICK_TILE.get(), TFBlocks.BOLD_CASTLE_BRICK_PILLAR.get(), TFBlocks.BOLD_CASTLE_BRICK_TILE.get()))
			.unlockedBy("has_castle_brick", has(TFBlocks.CASTLE_BRICK.get()))
			.save(this.output, locCastle("encased_castle_pillar"));

		stairsBlock(getter, locCastle("bold_castle_brick_stairs"), TFBlocks.BOLD_CASTLE_BRICK_STAIRS, TFBlocks.BOLD_CASTLE_BRICK_PILLAR, TFBlocks.BOLD_CASTLE_BRICK_PILLAR.get(), TFBlocks.BOLD_CASTLE_BRICK_TILE.get());
		stairsBlock(getter, locCastle("castle_brick_stairs"), TFBlocks.CASTLE_BRICK_STAIRS, TFBlocks.CASTLE_BRICK, TFBlocks.CASTLE_BRICK.get());
		stairsBlock(getter, locCastle("cracked_castle_brick_stairs"), TFBlocks.CRACKED_CASTLE_BRICK_STAIRS, TFBlocks.CRACKED_CASTLE_BRICK, TFBlocks.CRACKED_CASTLE_BRICK.get());
		stairsBlock(getter, locCastle("encased_castle_brick_stairs"), TFBlocks.ENCASED_CASTLE_BRICK_STAIRS, TFBlocks.ENCASED_CASTLE_BRICK_PILLAR, TFBlocks.ENCASED_CASTLE_BRICK_PILLAR.get(), TFBlocks.ENCASED_CASTLE_BRICK_TILE.get());
		stairsBlock(getter, locCastle("mossy_castle_brick_stairs"), TFBlocks.MOSSY_CASTLE_BRICK_STAIRS, TFBlocks.MOSSY_CASTLE_BRICK, TFBlocks.MOSSY_CASTLE_BRICK.get());
		stairsBlock(getter, locCastle("worn_castle_brick_stairs"), TFBlocks.WORN_CASTLE_BRICK_STAIRS, TFBlocks.WORN_CASTLE_BRICK, TFBlocks.WORN_CASTLE_BRICK.get());
	}

	private void fieryConversions(HolderGetter<Item> getter) {
		ShapelessRecipeBuilder.shapeless(getter, RecipeCategory.MISC, TFItems.FIERY_INGOT.get())
			.requires(Ingredient.of(getter.getOrThrow(ItemTagGenerator.FIERY_VIAL)))
			.requires(Ingredient.of(getter.getOrThrow(Tags.Items.INGOTS_IRON)))
			.unlockedBy("has_item", has(ItemTagGenerator.FIERY_VIAL))
			.group("fiery_ingot")
			.save(this.output, locEquip("fiery_ingot_crafting"));

		fieryConversion(getter, TFItems.FIERY_HELMET, Items.IRON_HELMET, 5);
		fieryConversion(getter, TFItems.FIERY_CHESTPLATE, Items.IRON_CHESTPLATE, 8);
		fieryConversion(getter, TFItems.FIERY_LEGGINGS, Items.IRON_LEGGINGS, 7);
		fieryConversion(getter, TFItems.FIERY_BOOTS, Items.IRON_BOOTS, 4);

		ShapelessRecipeBuilder.shapeless(getter, RecipeCategory.COMBAT, TFItems.FIERY_SWORD.get())
			.requires(Items.IRON_SWORD)
			.requires(Ingredient.of(getter.getOrThrow(ItemTagGenerator.FIERY_VIAL)), 2)
			.requires(Ingredient.of(getter.getOrThrow(Tags.Items.RODS_BLAZE)))
			.unlockedBy("has_item", has(ItemTagGenerator.FIERY_VIAL))
			.group("fiery_sword")
			.save(this.output, locEquip("fiery_" + BuiltInRegistries.ITEM.getKey(Items.IRON_SWORD).getPath()));

		ShapelessRecipeBuilder.shapeless(getter, RecipeCategory.TOOLS, TFItems.FIERY_PICKAXE.get())
			.requires(Items.IRON_PICKAXE)
			.requires(Ingredient.of(getter.getOrThrow(ItemTagGenerator.FIERY_VIAL)), 3)
			.requires(Ingredient.of(getter.getOrThrow(Tags.Items.RODS_BLAZE)), 2)
			.unlockedBy("has_item", has(ItemTagGenerator.FIERY_VIAL))
			.group("fiery_pickaxe")
			.save(this.output, locEquip("fiery_" + BuiltInRegistries.ITEM.getKey(Items.IRON_PICKAXE).getPath()));
	}

	private <T extends AbstractCookingRecipe> void cookingRecipes(String processName, RecipeSerializer<T> process, AbstractCookingRecipe.Factory<T> factory, int smeltingTime) {
		SimpleCookingRecipeBuilder.generic(Ingredient.of(TFItems.RAW_MEEF.get()), RecipeCategory.FOOD, TFItems.COOKED_MEEF.get(), 0.35F, smeltingTime, process, factory).unlockedBy("has_food", has(TFItems.RAW_MEEF.get())).save(this.output, this.createKey("food/" + processName + "_meef").toString());
		SimpleCookingRecipeBuilder.generic(Ingredient.of(TFItems.RAW_VENISON.get()), RecipeCategory.FOOD, TFItems.COOKED_VENISON.get(), 0.35F, smeltingTime, process, factory).unlockedBy("has_food", has(TFItems.RAW_VENISON.get())).save(this.output, this.createKey("food/" + processName + "_venison").toString());
	}

	private <T extends AbstractCookingRecipe> void ingotRecipes(String processName, RecipeSerializer<T> process, AbstractCookingRecipe.Factory<T> factory, int smeltingTime) {
		SimpleCookingRecipeBuilder.generic(Ingredient.of(TFItems.ARMOR_SHARD_CLUSTER.get()), RecipeCategory.MISC, TFItems.KNIGHTMETAL_INGOT.get(), 1.0F, smeltingTime, process, factory).unlockedBy("has_item", has(TFItems.ARMOR_SHARD_CLUSTER.get())).group("knightmetal_ingot").save(this.output, this.createKey("material/" + processName + "_knightmetal_ingot").toString());
		SimpleCookingRecipeBuilder.generic(Ingredient.of(TFItems.RAW_IRONWOOD.get()), RecipeCategory.MISC, TFItems.IRONWOOD_INGOT.get(), 1.0F, smeltingTime, process, factory).unlockedBy("has_item", has(TFItems.RAW_IRONWOOD.get())).group("ironwood_ingot").save(this.output, this.createKey("material/" + processName + "_ironwood_ingot").toString());
	}

	private void crackedWoodRecipes() {
		SimpleCookingRecipeBuilder.smoking(Ingredient.of(TFBlocks.TOWERWOOD.get()), RecipeCategory.BUILDING_BLOCKS, TFBlocks.CRACKED_TOWERWOOD.get(), 0.1F, 100).unlockedBy("has_item", has(TFBlocks.TOWERWOOD.get())).save(this.output, this.createKey("wood/" + "smoked" + "_cracked_towerwood").toString());
	}

	private void crackedStoneRecipes() {
		SimpleCookingRecipeBuilder.smelting(Ingredient.of(TFBlocks.NAGASTONE_PILLAR.get()), RecipeCategory.BUILDING_BLOCKS, TFBlocks.CRACKED_NAGASTONE_PILLAR.get(), 0.1F, 200).unlockedBy("has_item", has(TFBlocks.NAGASTONE_PILLAR.get())).save(this.output, this.createKey("nagastone/" + "smelted" + "_cracked_nagastone_pillar").toString());
		SimpleCookingRecipeBuilder.smelting(Ingredient.of(TFBlocks.ETCHED_NAGASTONE.get()), RecipeCategory.BUILDING_BLOCKS, TFBlocks.CRACKED_ETCHED_NAGASTONE.get(), 0.1F, 200).unlockedBy("has_item", has(TFBlocks.ETCHED_NAGASTONE.get())).save(this.output, this.createKey("nagastone/" + "smelted" + "_cracked_etched_nagastone").toString());
		SimpleCookingRecipeBuilder.smelting(Ingredient.of(TFBlocks.MAZESTONE_BRICK.get()), RecipeCategory.BUILDING_BLOCKS, TFBlocks.CRACKED_MAZESTONE.get(), 0.1F, 200).unlockedBy("has_item", has(TFBlocks.MAZESTONE_BRICK.get())).save(this.output, this.createKey("maze_stone/" + "smelted" + "_maze_stone_cracked").toString());
		SimpleCookingRecipeBuilder.smelting(Ingredient.of(TFBlocks.CASTLE_BRICK.get()), RecipeCategory.BUILDING_BLOCKS, TFBlocks.CRACKED_CASTLE_BRICK.get(), 0.1F, 200).unlockedBy("has_item", has(TFBlocks.CASTLE_BRICK.get())).save(this.output, this.createKey("castleblock/" + "smelted" + "_cracked_castle_brick").toString());
		SimpleCookingRecipeBuilder.smelting(Ingredient.of(TFBlocks.UNDERBRICK.get()), RecipeCategory.BUILDING_BLOCKS, TFBlocks.CRACKED_UNDERBRICK.get(), 0.1F, 200).unlockedBy("has_item", has(TFBlocks.UNDERBRICK.get())).save(this.output, this.createKey("smelted" + "_cracked_underbrick").toString());
	}
}
