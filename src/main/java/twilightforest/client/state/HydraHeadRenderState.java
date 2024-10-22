package twilightforest.client.state;

import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.util.Mth;
import twilightforest.entity.boss.HydraPart;

public class HydraHeadRenderState extends LivingEntityRenderState {
	private float mouthAngleO;
	private float mouthAngle;

	public float getMouthAngle() {
		return Mth.lerp(this.partialTick, this.mouthAngleO, this.mouthAngle);
	}

	public float getRotationX(HydraPart whichHead, float time) {
		return (whichHead.xRotO + (whichHead.getXRot() - whichHead.xRotO) * time) * Mth.DEG_TO_RAD;
	}

}
