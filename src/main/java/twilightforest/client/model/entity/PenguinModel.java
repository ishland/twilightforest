// Date: 3/3/2012 11:56:45 PM
// Template version 1.1
// Java generated by Techne
// Keep in mind that you still need to fill in some blanks
// - ZeuX

package twilightforest.client.model.entity;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.util.Mth;
import twilightforest.client.state.BirdRenderState;

public class PenguinModel extends EntityModel<BirdRenderState> {

	public final ModelPart head;
	public final ModelPart rightWing;
	public final ModelPart leftWing;
	public final ModelPart rightFoot;
	public final ModelPart leftFoot;

	public PenguinModel(ModelPart root) {
		super(root);
		this.head = root.getChild("head");
		this.rightWing = root.getChild("right_wing");
		this.leftWing = root.getChild("left_wing");
		this.rightFoot = root.getChild("right_foot");
		this.leftFoot = root.getChild("left_foot");
	}

	public static LayerDefinition create() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		partdefinition.addOrReplaceChild("body", CubeListBuilder.create()
				.texOffs(32, 0)
				.addBox(-4.0F, 0.0F, -4.0F, 8.0F, 9.0F, 8.0F),
			PartPose.offset(0.0F, 14.0F, 0.0F));

		var head = partdefinition.addOrReplaceChild("head", CubeListBuilder.create()
				.texOffs(0, 0)
				.addBox(-3.5F, -4.0F, -3.5F, 7.0F, 5.0F, 7.0F),
			PartPose.offset(0.0F, 13.0F, 0.0F));

		head.addOrReplaceChild("beak", CubeListBuilder.create()
				.texOffs(0, 13)
				.addBox(-1.0F, 0.0F, -1.0F, 2.0F, 1.0F, 2.0F),
			PartPose.offset(0.0F, -1.0F, -4.0F));

		partdefinition.addOrReplaceChild("right_wing", CubeListBuilder.create()
				.texOffs(34, 18)
				.addBox(-1.0F, -1.0F, -2.0F, 1.0F, 8.0F, 4.0F),
			PartPose.offset(-4.0F, 15.0F, 0.0F));

		partdefinition.addOrReplaceChild("left_wing", CubeListBuilder.create()
				.texOffs(24, 18)
				.addBox(0.0F, -1.0F, -2.0F, 1.0F, 8.0F, 4.0F),
			PartPose.offset(4.0F, 15.0F, 0.0F));

		partdefinition.addOrReplaceChild("right_foot", CubeListBuilder.create()
				.texOffs(0, 16)
				.addBox(-2.0F, 0.0F, -5.0F, 4.0F, 1.0F, 8.0F),
			PartPose.offset(-2.0F, 23.0F, 0.0F));

		partdefinition.addOrReplaceChild("left_foot", CubeListBuilder.create()
				.texOffs(0, 16)
				.addBox(-2.0F, 0.0F, -5.0F, 4.0F, 1.0F, 8.0F),
			PartPose.offset(2.0F, 23.0F, 0.0F));

		return LayerDefinition.create(meshdefinition, 64, 32);
	}

	@Override
	public void setupAnim(BirdRenderState state) {
		float f = (Mth.sin(state.flap) + 1.0F) * state.flapSpeed;
		this.head.xRot = state.xRot * Mth.DEG_TO_RAD;
		this.head.yRot = state.yRot * Mth.DEG_TO_RAD;

		this.rightFoot.xRot = Mth.cos(state.walkAnimationPos) * 0.7F * state.walkAnimationSpeed;
		this.leftFoot.xRot = Mth.cos(state.walkAnimationPos + Mth.PI) * 0.7F * state.walkAnimationSpeed;

		this.rightWing.zRot = f;
		this.leftWing.zRot = -f;
	}
}
