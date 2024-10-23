package twilightforest.client.state;

import net.minecraft.client.renderer.entity.state.HumanoidRenderState;

public class UpperGoblinKnightRenderState extends HumanoidRenderState {
	public boolean hasArmor;
	public boolean hasShield;
	public boolean isShieldDisabled;
	public float spearTimer;

	public float getArmRotationDuringSwing() {
		float timer = 60.0F - this.spearTimer;
		if (timer <= 10.0F) {
			// rock back
			return timer;
		}
		if (timer > 10.0F && timer <= 30.0F) {
			// hang back
			return 10.0F;
		}
		if (timer > 30.0F && timer <= 33.0F) {
			// slam forward
			return (timer - 30.0F) * -8.0F + 10.0F;
		}
		if (timer > 33.0F && timer <= 50.0F) {
			// stay forward
			return -15.0F;
		}
		if (timer > 50.0F && timer <= 60.0F) {
			// back to normal
			return (10.0F - (timer - 50.0F)) * -1.5F;
		}

		return 0.0F;
	}
}
