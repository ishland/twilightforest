package twilightforest.client.model.block.connected;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.client.model.generators.template.CustomLoaderBuilder;
import twilightforest.TwilightForestMod;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
public class ConnectedTextureBuilder extends CustomLoaderBuilder {

	private List<Direction> enabledFaces = new ArrayList<>();
	private List<Block> connectableBlocks = new ArrayList<>();
	private List<TagKey<Block>> connectableTags = new ArrayList<>();
	private boolean renderOnDisabledFaces = true;

	private int baseTintIndex = -1;
	private int baseEmissivity = 0;
	private int tintIndex = -1;
	private int emissivity = 0;

	protected ConnectedTextureBuilder() {
		super(TwilightForestMod.prefix("connected_texture_block"), false);
	}

	public static ConnectedTextureBuilder begin() {
		return new ConnectedTextureBuilder();
	}

	public ConnectedTextureBuilder addConnectionFaces(Direction... faces) {
		this.enabledFaces.addAll(List.of(faces));
		return this;
	}

	public ConnectedTextureBuilder disableRenderingOnDisabledFaces() {
		this.renderOnDisabledFaces = false;
		return this;
	}

	public ConnectedTextureBuilder setBaseTintIndex(int index) {
		this.baseTintIndex = index;
		return this;
	}

	public ConnectedTextureBuilder setOverlayTintIndex(int index) {
		this.tintIndex = index;
		return this;
	}

	public ConnectedTextureBuilder setBaseEmissivity(int value) {
		this.baseEmissivity = value;
		return this;
	}

	public ConnectedTextureBuilder setOverlayEmissivity(int value) {
		this.emissivity = value;
		return this;
	}

	public final ConnectedTextureBuilder connectsTo(Block... blocks) {
		this.connectableBlocks.addAll(List.of(blocks));
		return this;
	}

	@SuppressWarnings("varargs")
	@SafeVarargs
	public final ConnectedTextureBuilder connectsTo(TagKey<Block>... blocks) {
		this.connectableTags.addAll(List.of(blocks));
		return this;
	}

	@Override
	protected ConnectedTextureBuilder copyInternal() {
		ConnectedTextureBuilder builder = new ConnectedTextureBuilder();
		builder.enabledFaces = List.copyOf(this.enabledFaces);
		builder.connectableBlocks = List.copyOf(this.connectableBlocks);
		builder.connectableTags = List.copyOf(this.connectableTags);
		builder.renderOnDisabledFaces = this.renderOnDisabledFaces;
		builder.baseTintIndex = this.baseTintIndex;
		builder.baseEmissivity = this.baseEmissivity;
		builder.tintIndex = this.tintIndex;
		builder.emissivity = this.emissivity;
		return builder;
	}

	@Override
	public JsonObject toJson(JsonObject json) {
		json = super.toJson(json);
		if (this.baseTintIndex > -1 || this.baseEmissivity != 0) {
			JsonObject baseInfo = new JsonObject();
			if (this.baseTintIndex > -1) {
				baseInfo.addProperty("tint_index", this.baseTintIndex);
			}
			if (this.baseEmissivity != 0) {
				baseInfo.addProperty("emissivity", this.baseEmissivity);
			}
			json.add("base", baseInfo);
		}
		if (!this.enabledFaces.isEmpty() || this.tintIndex > -1 || this.emissivity != 0) {
			JsonObject overlayInfo = new JsonObject();
			if (!this.enabledFaces.isEmpty()) {
				JsonArray array = new JsonArray();
				this.enabledFaces.forEach(face -> array.add(face.getName()));
				overlayInfo.add("faces", array);
			}
			if (this.tintIndex > -1) {
				overlayInfo.addProperty("tint_index", this.tintIndex);
			}
			if (this.emissivity != 0) {
				overlayInfo.addProperty("emissivity", this.emissivity);
			}
			overlayInfo.addProperty("render_disabled_faces", this.renderOnDisabledFaces);
			json.add("connected_texture", overlayInfo);
		}

		if (!this.connectableTags.isEmpty() || !this.connectableBlocks.isEmpty()) {
			JsonArray connectables = new JsonArray();
			if (!this.connectableBlocks.isEmpty()) {
				this.connectableBlocks.forEach(block -> connectables.add(BuiltInRegistries.BLOCK.getKey(block).toString()));
			}

			if (!this.connectableTags.isEmpty()) {
				this.connectableTags.forEach(tag -> connectables.add("#" + tag.location()));
			}

			json.add("connectable_blocks", connectables);
		}

		return json;
	}
}