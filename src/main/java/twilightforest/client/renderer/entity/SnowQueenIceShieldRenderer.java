package twilightforest.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.data.ModelData;
import twilightforest.client.state.IceShieldRenderState;
import twilightforest.entity.boss.SnowQueenIceShield;

public class SnowQueenIceShieldRenderer extends EntityRenderer<SnowQueenIceShield, IceShieldRenderState> {
	public SnowQueenIceShieldRenderer(EntityRendererProvider.Context context) {
		super(context);
	}

	@Override
	public void render(IceShieldRenderState state, PoseStack stack, MultiBufferSource buffer, int light) {
		BlockState blockstate = Blocks.PACKED_ICE.defaultBlockState();
		if (blockstate.getRenderShape() == RenderShape.MODEL) {
			stack.pushPose();
			stack.translate(-0.5D, 0.0D, -0.5D);
			BlockRenderDispatcher dispatcher = Minecraft.getInstance().getBlockRenderer();
			var model = dispatcher.getBlockModel(blockstate);
			for (var renderType : model.getRenderTypes(blockstate, RandomSource.create(blockstate.getSeed(state.blockPos)), ModelData.EMPTY))
				dispatcher.getModelRenderer().tesselateBlock(state, model, blockstate, state.blockPos, stack, buffer.getBuffer(renderType), false, RandomSource.create(), blockstate.getSeed(state.blockPos), OverlayTexture.NO_OVERLAY, ModelData.EMPTY, renderType);
			stack.popPose();
			super.render(state, stack, buffer, light);
		}
	}

	@Override
	public IceShieldRenderState createRenderState() {
		return new IceShieldRenderState();
	}

	@Override
	public void extractRenderState(SnowQueenIceShield entity, IceShieldRenderState state, float partialTick) {
		super.extractRenderState(entity, state, partialTick);
		BlockPos blockpos = BlockPos.containing(entity.getX(), entity.getBoundingBox().maxY, entity.getZ());
		state.blockPos = blockpos;
		state.biome = entity.level().getBiome(blockpos);
		state.level = entity.level();
	}
}
