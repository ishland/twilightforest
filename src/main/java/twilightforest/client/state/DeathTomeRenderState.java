package twilightforest.client.state;

import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.util.Mth;

public class DeathTomeRenderState extends LivingEntityRenderState {
	public boolean onLectern;
	private float oFlip;
	private float flip;

	public float getFlip() {
		return Mth.lerp(this.partialTick, this.oFlip, this.flip);
	}
}
