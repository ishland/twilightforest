package twilightforest.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.Model;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import twilightforest.TwilightForestMod;
import twilightforest.client.model.TFModelLayers;
import twilightforest.client.model.entity.ChainModel;
import twilightforest.client.model.entity.SpikeBlockModel;
import twilightforest.client.state.ChainBlockRenderState;
import twilightforest.entity.projectile.ChainBlock;

public class BlockChainRenderer extends EntityRenderer<ChainBlock, ChainBlockRenderState> {

	private static final ResourceLocation TEXTURE = TwilightForestMod.getModelTexture("block_and_chain.png");
	private final Model model;
	private final Model chainModel;

	public BlockChainRenderer(EntityRendererProvider.Context context) {
		super(context);
		this.model = new SpikeBlockModel(context.bakeLayer(TFModelLayers.CHAIN_BLOCK));
		this.chainModel = new ChainModel(context.bakeLayer(TFModelLayers.CHAIN));
	}

	@Override
	public void render(ChainBlockRenderState state, PoseStack stack, MultiBufferSource buffer, int light) {
		super.render(state, stack, buffer, light);

		stack.pushPose();
		VertexConsumer consumer = ItemRenderer.getFoilBuffer(buffer, this.model.renderType(TEXTURE), false, state.isFoil);

		stack.mulPose(Axis.YP.rotationDegrees(state.yRot - 90.0F));
		stack.mulPose(Axis.ZP.rotationDegrees(state.xRot));

		stack.scale(-1.0F, -1.0F, 1.0F);
		this.model.renderToBuffer(stack, consumer, light, OverlayTexture.NO_OVERLAY);
		stack.popPose();

		if (state.chainStartPos != null) {
			Vec3 xyz = state.chainStartPos;
			double links = xyz.length();
			xyz = xyz.normalize();
			for (int i = 1; i < links; i++) {
				renderChain(state.isFoil, xyz, links - i, stack, buffer, Math.max(light, state.ownerLight), this.chainModel);
			}
		}
	}

	public static void renderChain(boolean renderFoil, Vec3 xyz, double scale, PoseStack stack, MultiBufferSource buffer, int light, Model chainModel) {
		Vec3 pos = xyz.scale(scale);

		stack.pushPose();
		VertexConsumer vertexConsumer = ItemRenderer.getFoilBuffer(buffer, chainModel.renderType(TEXTURE), false, renderFoil);

		stack.translate(pos.x(), pos.y(), pos.z());

		stack.scale(-1.0F, -1.0F, 1.0F);
		chainModel.renderToBuffer(stack, vertexConsumer, light, OverlayTexture.NO_OVERLAY);
		stack.popPose();
	}

	@Override
	public ChainBlockRenderState createRenderState() {
		return new ChainBlockRenderState();
	}

	@Override
	public void extractRenderState(ChainBlock entity, ChainBlockRenderState state, float partialTick) {
		super.extractRenderState(entity, state, partialTick);
		state.yRot = entity.getYRot(partialTick);
		state.xRot = entity.getXRot(partialTick);
		state.isFoil = entity.isFoil();
		state.chainStartPos = entity.getOwner() != null ? entity.getOwner().getEyePosition(partialTick).subtract(entity.getEyePosition(partialTick)) : null;
		state.ownerLight = entity.getOwner() != null ? Minecraft.getInstance().getEntityRenderDispatcher().getPackedLightCoords(entity.getOwner(), partialTick) : 0;
	}
}
