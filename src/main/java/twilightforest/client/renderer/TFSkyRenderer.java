package twilightforest.client.renderer;

import com.mojang.blaze3d.buffers.BufferUsage;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.*;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import org.joml.*;

import java.lang.Math;

public class TFSkyRenderer {

	private static final VertexBuffer starBuffer = createStarBuffer();

	// [VanillaCopy] LevelRenderer.renderSky's overworld branch, without sun/moon/sunrise/sunset, using our own stars at full brightness, and lowering void horizon threshold height from getHorizonHeight (63) to 0
	public static boolean renderSky(ClientLevel level, float partialTicks, Camera camera, Runnable setupFog) {
		LevelRenderer levelRenderer = Minecraft.getInstance().levelRenderer;
		setupFog.run();
		RenderStateShard.MAIN_TARGET.setupRenderState();
		PoseStack posestack = new PoseStack();

		//TF: all unused
//		Tesselator tesselator = Tesselator.getInstance();
//		float f = level.getSunAngle(partialTicks);
//		float f1 = level.getTimeOfDay(partialTicks);
//		float f2 = 1.0F - level.getRainLevel(partialTicks);
//		float f3 = level.getStarBrightness(partialTicks) * f2;
//		int i = dimensionspecialeffects.getSunriseOrSunsetColor(f1);
//		int j = level.getMoonPhase();
		int k = level.getSkyColor(camera.getPosition(), partialTicks);
		float f4 = ARGB.from8BitChannel(ARGB.red(k));
		float f5 = ARGB.from8BitChannel(ARGB.green(k));
		float f6 = ARGB.from8BitChannel(ARGB.blue(k));
		levelRenderer.skyRenderer.renderSkyDisc(f4, f5, f6);
		//TF: snip out sunrise and sunset coloring
//		if (dimensionspecialeffects.isSunriseOrSunset(f1)) {
//			levelRenderer.skyRenderer.renderSunriseAndSunset(posestack, tesselator, f, i);
//		}

		//TF: replace sun, moon, and star rendering method with our own star renderer
		renderStars(setupFog, posestack);
		//TF: use custom height checks for the void sky as vanilla hardcodes to 63
		if (shouldDarkenSky(level, camera, partialTicks)) {
			levelRenderer.skyRenderer.renderDarkDisc(posestack);
		}

		return true;
	}

	private static boolean shouldDarkenSky(ClientLevel level, Camera camera, float partialTicks) {
		return camera.getEntity().getEyePosition(partialTicks).y - level.getMinY() < 0.0;
	}

	private static VertexBuffer createStarBuffer() {
		VertexBuffer vertexbuffer = new VertexBuffer(BufferUsage.STATIC_WRITE);
		vertexbuffer.bind();
		vertexbuffer.upload(drawStars(Tesselator.getInstance()));
		VertexBuffer.unbind();
		return vertexbuffer;
	}

	//[VanillaCopy] of SkyRenderer.renderStars, using our own buffer instead. Coloring was also removed as the stars are always fully bright
	private static void renderStars(Runnable setupFog, PoseStack stack) {
		Matrix4fStack matrix = RenderSystem.getModelViewStack();
		matrix.pushMatrix();
		matrix.mul(stack.last().pose());
		RenderSystem.depthMask(false);
		RenderSystem.overlayBlendFunc();
		RenderSystem.setShader(CoreShaders.POSITION);
		RenderSystem.enableBlend();
		RenderSystem.setShaderFog(FogParameters.NO_FOG);
		starBuffer.bind();
		starBuffer.drawWithShader(matrix, RenderSystem.getProjectionMatrix(), RenderSystem.getShader());
		VertexBuffer.unbind();
		setupFog.run();
		RenderSystem.disableBlend();
		RenderSystem.defaultBlendFunc();
		RenderSystem.depthMask(true);
		matrix.popMatrix();
	}

	// [VanillaCopy] of LevelRenderer.drawStars but with double the number of them
	private static MeshData drawStars(Tesselator tesselator) {
		RandomSource random = RandomSource.create(10842L);
		BufferBuilder bufferbuilder = tesselator.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION);

		// TF - 1500 -> 3000
		for (int i = 0; i < 3000; ++i) {
			float f1 = random.nextFloat() * 2.0F - 1.0F;
			float f2 = random.nextFloat() * 2.0F - 1.0F;
			float f3 = random.nextFloat() * 2.0F - 1.0F;
			float f4 = 0.15F + random.nextFloat() * 0.1F;
			float f5 = Mth.lengthSquared(f1, f2, f3);
			if (!(f5 <= 0.010000001F) && !(f5 >= 1.0F)) {
				Vector3f vector3f = new Vector3f(f1, f2, f3).normalize(100.0F);
				float f6 = (float)(random.nextDouble() * (float) Math.PI * 2.0);
				Matrix3f matrix3f = new Matrix3f().rotateTowards(new Vector3f(vector3f).negate(), new Vector3f(0.0F, 1.0F, 0.0F)).rotateZ(-f6);
				bufferbuilder.addVertex(vector3f.add(new Vector3f(f4, -f4, 0.0F).mul(matrix3f).add(vector3f)));
				bufferbuilder.addVertex(vector3f.add(new Vector3f(f4, f4, 0.0F).mul(matrix3f).add(vector3f)));
				bufferbuilder.addVertex(vector3f.add(new Vector3f(-f4, f4, 0.0F).mul(matrix3f).add(vector3f)));
				bufferbuilder.addVertex(vector3f.add(new Vector3f(-f4, -f4, 0.0F).mul(matrix3f).add(vector3f)));
			}
		}

		return bufferbuilder.buildOrThrow();
	}
}
