package twilightforest.client.model.block.giantblock;

import com.google.common.base.Preconditions;
import com.google.gson.JsonObject;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.client.model.generators.template.CustomLoaderBuilder;
import twilightforest.TwilightForestMod;

public class GiantBlockBuilder extends CustomLoaderBuilder {

	public static GiantBlockBuilder begin() {
		return new GiantBlockBuilder();
	}

	private ResourceLocation parentBlock;

	protected GiantBlockBuilder() {
		super(TwilightForestMod.prefix("giant_block"), false);
	}

	public GiantBlockBuilder parentBlock(Block block) {
		Preconditions.checkNotNull(block, "parent block must not be null");
		this.parentBlock = BuiltInRegistries.BLOCK.getKey(block);
		return this;
	}

	@Override
	protected CustomLoaderBuilder copyInternal() {
		GiantBlockBuilder builder = new GiantBlockBuilder();
		builder.parentBlock = parentBlock;
		return builder;
	}

	@Override
	public JsonObject toJson(JsonObject json) {
		json = super.toJson(json);
		json.addProperty("parent_block", this.parentBlock.toString());
		return json;
	}
}
