package twilightforest.client.model.block.forcefield;

import net.minecraft.client.renderer.block.model.BakedOverrides;
import net.minecraft.client.renderer.block.model.BlockElement;
import net.minecraft.client.renderer.block.model.ItemOverride;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelState;
import net.neoforged.neoforge.client.model.geometry.IGeometryBakingContext;
import net.neoforged.neoforge.client.model.geometry.IUnbakedGeometry;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

public record UnbakedForceFieldModel(Map<BlockElement, ForceFieldModelLoader.Condition> elementsAndConditions) implements IUnbakedGeometry<UnbakedForceFieldModel> {

	@Override
	public BakedModel bake(IGeometryBakingContext context, ModelBaker baker, Function<Material, TextureAtlasSprite> spriteGetter, ModelState modelState, List<ItemOverride> overrides) {
		return new ForceFieldModel(this.elementsAndConditions, spriteGetter, context, new BakedOverrides(baker, overrides, spriteGetter));
	}
}
