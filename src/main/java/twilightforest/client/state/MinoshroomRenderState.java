package twilightforest.client.state;

import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.util.Mth;

public class MinoshroomRenderState extends HumanoidRenderState {
	public float chargeAnimO;
	public float chargeAnim;

	public float getChargeAnimationScale() {
		return Mth.lerp(this.partialTick, this.chargeAnimO, this.chargeAnim) / 6.0F;
	}
}
