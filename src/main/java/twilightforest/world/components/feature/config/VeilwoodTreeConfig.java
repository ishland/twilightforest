package twilightforest.world.components.feature.config;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecorator;

import java.util.List;

/**
 * Follows similar structure to HugeTreeFeatureConfig
 */
public class VeilwoodTreeConfig implements FeatureConfiguration, DecoratedTree {
	public static final Codec<VeilwoodTreeConfig> codecVeilwoodTreeConfig = RecordCodecBuilder.create(instance -> instance.group(
		BlockStateProvider.CODEC.fieldOf("trunk_provider").forGetter(obj -> obj.trunkProvider),
		BlockStateProvider.CODEC.fieldOf("branch_provider").forGetter(obj -> obj.branchProvider),
		BlockStateProvider.CODEC.fieldOf("roots_provider").forGetter(obj -> obj.rootsProvider),
		Codec.DOUBLE.fieldOf("scale").orElse(0.75D).forGetter(obj -> obj.scale),
		Codec.INT.fieldOf("min_size").orElse(10).forGetter(obj -> obj.minSize),
		Codec.INT.fieldOf("max_size").orElse(17).forGetter(obj -> obj.maxSize),
		Codec.INT.fieldOf("min_branch_length").orElse(5).forGetter(obj -> obj.minBranchLength),
		Codec.INT.fieldOf("max_branch_length").orElse(7).forGetter(obj -> obj.maxBranchLength),
		Codec.DOUBLE.fieldOf("branch_off_count").orElse(1.0D).forGetter(obj -> obj.branchOffCount),
		TreeDecorator.CODEC.listOf().fieldOf("decorators").orElseGet(ImmutableList::of).forGetter(obj -> obj.decorators)
	).apply(instance, VeilwoodTreeConfig::new));

	public final BlockStateProvider trunkProvider;
	public final BlockStateProvider branchProvider;
	public final BlockStateProvider rootsProvider;
	public final double scale;
	public final int minSize;
	public final int maxSize;
	public final int minBranchLength;
	public final int maxBranchLength;
	public final double branchOffCount;
	public transient boolean forcePlacement;
	public final List<TreeDecorator> decorators;

	public VeilwoodTreeConfig(BlockStateProvider trunk, BlockStateProvider branch, BlockStateProvider roots, double scale, int minSize, int maxSize, int minBranchLength, int maxBranchLength, double branchOffCount, List<TreeDecorator> decorators) {
		this.trunkProvider = trunk;
		this.branchProvider = branch;
		this.rootsProvider = roots;
		this.scale = scale;
		this.minSize = minSize;
		this.maxSize = Math.max(minSize + 1, maxSize);
		this.minBranchLength = minBranchLength;
		this.maxBranchLength = maxBranchLength;
		this.branchOffCount = branchOffCount;
		this.decorators = decorators;
	}

	public void forcePlacement() {
		this.forcePlacement = true;
	}

	@Override
	public List<TreeDecorator> getDecorators() {
		return this.decorators;
	}

	public static class Builder {
		private final BlockStateProvider trunkProvider;
		private final BlockStateProvider branchProvider;
		private final BlockStateProvider rootsProvider;
		private double scale = 0.75D;
		private int minSize = 10;
		private int maxSize = 17;
		private int minBranchLength = 5;
		private int maxBranchLength = 7;
		private double branchOffCount = 1.0D;
		private final List<TreeDecorator> decorators = Lists.newArrayList();

		public Builder(BlockStateProvider trunk, BlockStateProvider branch, BlockStateProvider roots) {
			this.trunkProvider = trunk;
			this.branchProvider = branch;
			this.rootsProvider = roots;
		}

		public VeilwoodTreeConfig.Builder scale(double scale) {
			this.scale = scale;
			return this;
		}

		public VeilwoodTreeConfig.Builder minSize(int minSize) {
			this.minSize = minSize;
			return this;
		}

		public VeilwoodTreeConfig.Builder maxSize(int maxSize) {
			this.maxSize = maxSize;
			return this;
		}

		public VeilwoodTreeConfig.Builder minBranchLength(int minBranchLength) {
			this.minBranchLength = minBranchLength;
			return this;
		}

		public VeilwoodTreeConfig.Builder maxBranchLength(int maxBranchLength) {
			this.maxBranchLength = maxBranchLength;
			return this;
		}

		public VeilwoodTreeConfig.Builder branchOffCount(double branchOffCount) {
			this.branchOffCount = branchOffCount;
			return this;
		}

		public VeilwoodTreeConfig.Builder addDecorator(TreeDecorator deco) {
			decorators.add(deco);
			return this;
		}

		public VeilwoodTreeConfig build() {
			return new VeilwoodTreeConfig(trunkProvider, branchProvider, rootsProvider, scale, minSize, maxSize, minBranchLength, maxBranchLength, branchOffCount, decorators);
		}
	}
}