package twilightforest.world.components.chunkgenerators;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.SectionPos;
import net.minecraft.resources.RegistryFileCodec;
import net.minecraft.util.KeyDispatchDataCodec;
import net.minecraft.world.level.levelgen.DensityFunction;
import org.jetbrains.annotations.NotNull;
import twilightforest.TFRegistries;
import twilightforest.world.components.layer.BiomeDensitySource;

/**
 * A DensityFunction implementation that enables Biomes to influence terrain formulations, if in the noise chunk generator.
 */
public class NoiseDensityRouter implements DensityFunction.SimpleFunction {
	public static final MapCodec<NoiseDensityRouter> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
		RegistryFileCodec.create(TFRegistries.Keys.BIOME_TERRAIN_DATA, BiomeDensitySource.CODEC, false).fieldOf("terrain_source").forGetter(NoiseDensityRouter::biomeDensitySourceHolder),
		NoiseHolder.CODEC.fieldOf("noise").forGetter(NoiseDensityRouter::noise),
		Codec.doubleRange(-64, 0).fieldOf("lower_density_bound").forGetter(NoiseDensityRouter::lowerDensityBound),
		Codec.doubleRange(0, 64).fieldOf("upper_density_bound").forGetter(NoiseDensityRouter::upperDensityBound),
		Codec.doubleRange(0, 32).orElse(8.0).fieldOf("depth_scalar").forGetter(NoiseDensityRouter::depthScalar),
		DensityFunction.HOLDER_HELPER_CODEC.fieldOf("base_factor").forGetter(NoiseDensityRouter::baseFactor),
		DensityFunction.HOLDER_HELPER_CODEC.fieldOf("base_offset").forGetter(NoiseDensityRouter::baseOffset)
	).apply(inst, NoiseDensityRouter::new));
	public static final KeyDispatchDataCodec<NoiseDensityRouter> KEY_CODEC = KeyDispatchDataCodec.of(CODEC);

	private final Holder<BiomeDensitySource> biomeDensitySourceHolder;
	private final NoiseHolder noise;
	private final double lowerDensityBound;
	private final double upperDensityBound;
	private final double depthScalar;
	private final DensityFunction baseFactor;
	private final DensityFunction baseOffset;

	/**
	 * @param biomeDensitySource A BiomeDensitySource containing TerrainColumns, providing per-biome scaling and depth behavior that allows biomes to distinguish their landscapes.
	 * @param lowerDensityBound  Lower clamp bound
	 * @param upperDensityBound  Upper clamp bound
	 * @param baseFactor         Density function (can be constant) for the height of the vertical y-gradient at a given X-Z position. A biome speeds or slows this vertical rate of change.
	 * @param baseOffset         Density function (can be constant) for the elevation of the vertical y-gradient at a given X-Z position. A biome moves it up and down.
	 */
	public NoiseDensityRouter(Holder<BiomeDensitySource> biomeDensitySource, NoiseHolder noise, double lowerDensityBound, double upperDensityBound, double depthScalar, DensityFunction baseFactor, DensityFunction baseOffset) {
		this.biomeDensitySourceHolder = biomeDensitySource;
		this.noise = noise;
		this.lowerDensityBound = lowerDensityBound;
		this.upperDensityBound = upperDensityBound;
		this.depthScalar = depthScalar;
		this.baseFactor = baseFactor;
		this.baseOffset = baseOffset;
	}

	@Override
	public double compute(FunctionContext context) {
		return this.computeTerrain(context).scale;
	}

	// Our default method for obtaining column samples of the biome source.
	// This method is overridden by ChunkCachedNoiseDensityRouter, operating that subclass's cache.
	@NotNull
	public BiomeDensitySource.DensityData computeTerrain(FunctionContext context) {
		return this.biomeDensitySourceHolder.value().sampleTerrain(context.blockX(), context.blockZ(), context);
	}

	@Override
	public double minValue() {
		return this.lowerDensityBound;
	}

	@Override
	public double maxValue() {
		return this.upperDensityBound;
	}

	@Override
	public KeyDispatchDataCodec<? extends DensityFunction> codec() {
		return KEY_CODEC;
	}

	public Holder<BiomeDensitySource> biomeDensitySourceHolder() {
		return this.biomeDensitySourceHolder;
	}

	private NoiseHolder noise() {
		return this.noise;
	}

	public double lowerDensityBound() {
		return this.lowerDensityBound;
	}

	public double upperDensityBound() {
		return this.upperDensityBound;
	}

	public double depthScalar() {
		return this.depthScalar;
	}

	public DensityFunction baseFactor() {
		return this.baseFactor;
	}

	public DensityFunction baseOffset() {
		return this.baseOffset;
	}

	/**
	 * NoiseDensityRouter is at best, a configuration class with DensityFunction capabilities.
	 * ChunkCachedNoiseDensityRouter is the actual DensityFunction used in worldgen.
	 * This cache is made once per Chunk in noisegen, and caches first density value obtained from each unique X-Z coordinate, ambiguating the Y value in coordinate.
	 * Plan your biome density functions accordingly! Don't use anything that's vertically sensitive
	 */
	@Override // NoiseChunk is the only class to ever call this, and it's typically a new chunk each time
	public DensityFunction mapAll(Visitor visitor) {
		return visitor.apply(new ChunkCachedNoiseDensityRouter(
			this.biomeDensitySourceHolder,
			visitor.visitNoise(this.noise),
			this.lowerDensityBound,
			this.upperDensityBound,
			this.depthScalar,
			this.baseFactor,
			this.baseOffset
		));
	}

	public static class ChunkCachedNoiseDensityRouter extends NoiseDensityRouter {
		private final BiomeDensitySource biomeDensitySource;

		private final BiomeDensitySource.DensityData[] horizontalCache = new BiomeDensitySource.DensityData[16 * 16];

		public ChunkCachedNoiseDensityRouter(Holder<BiomeDensitySource> biomeDensitySource, DensityFunction.NoiseHolder noise, double lowerDensityBound, double upperDensityBound, double depthScalar, DensityFunction baseFactor, DensityFunction baseOffset) {
			super(biomeDensitySource, noise, lowerDensityBound, upperDensityBound, depthScalar, baseFactor, baseOffset);
			this.biomeDensitySource = biomeDensitySource.value();
		}

		@NotNull
		@Override
		public BiomeDensitySource.DensityData computeTerrain(FunctionContext context) {
			int xInChunk = SectionPos.sectionRelative(context.blockX());
			int zInChunk = SectionPos.sectionRelative(context.blockZ());

			int arrayCoord = zInChunk + (xInChunk << 4);

			BiomeDensitySource.DensityData dataColumn = this.horizontalCache[arrayCoord];

			if (dataColumn == null) {
				dataColumn = this.biomeDensitySource.sampleTerrain(context.blockX(), context.blockZ(), context);
				this.horizontalCache[arrayCoord] = dataColumn;
			}

			return dataColumn;
		}
	}
}
