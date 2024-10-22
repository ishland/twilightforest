package twilightforest.client.model.entity;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.entity.state.WolfRenderState;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

import java.util.function.Function;

public class HostileWolfModel extends EntityModel<WolfRenderState> {

	private final ModelPart head;
	private final ModelPart body;
	private final ModelPart rightHindLeg;
	private final ModelPart leftHindLeg;
	private final ModelPart rightFrontLeg;
	private final ModelPart leftFrontLeg;
	private final ModelPart tail;
	private final ModelPart upperBody;

	public HostileWolfModel(ModelPart root) {
		this(RenderType::entityCutoutNoCull, root);
	}

	public HostileWolfModel(Function<ResourceLocation, RenderType> type, ModelPart root) {
		super(root, type);
		this.head = root.getChild("head");
		this.body = root.getChild("body");
		this.upperBody = root.getChild("upper_body");
		this.rightHindLeg = root.getChild("right_hind_leg");
		this.leftHindLeg = root.getChild("left_hind_leg");
		this.rightFrontLeg = root.getChild("right_front_leg");
		this.leftFrontLeg = root.getChild("left_front_leg");
		this.tail = root.getChild("tail");
	}

	@Override
	public void setupAnim(WolfRenderState state) {
		super.setupAnim(state);
		this.head.xRot = state.xRot * Mth.DEG_TO_RAD;
		this.head.yRot = state.yRot * Mth.DEG_TO_RAD;
		this.tail.xRot = state.ageInTicks;

		if (state.isAngry) {
			this.tail.yRot = 0.0F;
		} else {
			this.tail.yRot = Mth.cos(state.walkAnimationPos * 0.6662F) * 1.4F * state.walkAnimationSpeed;
		}

		this.body.setPos(0.0F, 14.0F, 2.0F);
		this.body.xRot = Mth.HALF_PI;
		this.upperBody.setPos(-1.0F, 14.0F, -3.0F);
		this.upperBody.xRot = this.body.xRot;
		this.tail.setPos(-1.0F, 12.0F, 8.0F);
		this.rightHindLeg.setPos(-2.5F, 16.0F, 7.0F);
		this.leftHindLeg.setPos(0.5F, 16.0F, 7.0F);
		this.rightFrontLeg.setPos(-2.5F, 16.0F, -4.0F);
		this.leftFrontLeg.setPos(0.5F, 16.0F, -4.0F);
		this.rightHindLeg.xRot = Mth.cos(state.walkAnimationPos * 0.6662F) * 1.4F * state.walkAnimationSpeed;
		this.leftHindLeg.xRot = Mth.cos(state.walkAnimationPos * 0.6662F + Mth.PI) * 1.4F * state.walkAnimationSpeed;
		this.rightFrontLeg.xRot = Mth.cos(state.walkAnimationPos * 0.6662F + Mth.PI) * 1.4F * state.walkAnimationSpeed;
		this.leftFrontLeg.xRot = Mth.cos(state.walkAnimationPos * 0.6662F) * 1.4F * state.walkAnimationSpeed;
	}
}
