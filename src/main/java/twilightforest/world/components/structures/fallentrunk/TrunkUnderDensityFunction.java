package twilightforest.world.components.structures.fallentrunk;

import it.unimi.dsi.fastutil.objects.ObjectIterators;
import it.unimi.dsi.fastutil.objects.ObjectListIterator;
import net.minecraft.core.Vec3i;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.Beardifier;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.pools.JigsawJunction;
import twilightforest.world.components.chunkgenerators.HollowHillFunction;

import java.util.Arrays;
import java.util.Comparator;

public class TrunkUnderDensityFunction extends Beardifier {
	private final boolean isBigTree;
	private final boolean isXOriented;
	private final RandomSource random;  // used to create dirt mounds
	private final BoundingBox boundingBox;
	private final HollowHillFunction[] hollowHillFunctions;
	private final int moundRadius = 4;
	protected final BoundingBox moundApex;

	public TrunkUnderDensityFunction(ObjectListIterator<Rigid> pieceIterator, boolean isBigTree, int minMounds, int maxMounds) {
		super(pieceIterator, (ObjectListIterator<JigsawJunction>) ObjectIterators.<JigsawJunction>emptyIterator());
		this.isBigTree = isBigTree;
		boundingBox = getFallenTrunkPiece().box();
		random = RandomSource.create(boundingBox.minX() * 14413411L + boundingBox.minZ() * 43387781L);
		isXOriented = boundingBox.maxX() - boundingBox.minX() > boundingBox.maxZ() - boundingBox.minZ();
		int length = isXOriented ? boundingBox.getXSpan() : boundingBox.getZSpan();
		Vec3i moundApexCorner = new Vec3i(
			isXOriented ?  length / 2 : 0,
			1,
			!isXOriented ? length / 2 : 0);
		this.moundApex = BoundingBox.fromCorners(moundApexCorner, moundApexCorner);
		this.hollowHillFunctions = new HollowHillFunction[random.nextInt(minMounds, maxMounds + 1)];
		for(int i = 0; i < hollowHillFunctions.length; i++) {
			hollowHillFunctions[i] = getHollowHillFunction();
		}
	}

	@Override
	public double compute(FunctionContext context) {  // modified copy of vanilla thin_beardifier
		int x = context.blockX();
		int y = context.blockY();
		int z = context.blockZ();

		int groundLevelDelta = getFallenTrunkPiece().groundLevelDelta();
		int horizontalDistanceX = Math.max(0, Math.max(boundingBox.minX() - x, x - boundingBox.maxX()));
		int horizontalDistanceZ = Math.max(0, Math.max(boundingBox.minZ() - z, z - boundingBox.maxZ()));
		int adjustedGroundLevel = boundingBox.minY() + groundLevelDelta + (isBigTree ? 2 : 1);
		int verticalDistance = y - adjustedGroundLevel;

		return Math.max(getBeardContribution(horizontalDistanceX, verticalDistance, horizontalDistanceZ, verticalDistance) * 5, computeMoundsContribution(context));
	}

	protected double computeMoundsContribution(FunctionContext context) {
		int x = context.blockX() - boundingBox.minX();
		int y = context.blockY() - boundingBox.minY();
		int z = context.blockZ() - boundingBox.minZ();
		int radius = getRadius(boundingBox);
		double ax = Math.abs((isXOriented ? z : x) - radius + 1);
		double az = Math.abs(y - radius + 1);
		if (radius == 2D) {  // This case is generated differently
			if (Math.abs((isXOriented ? z : x) - 1.5) + Math.abs(y - 1.5) <= 2)
				return -0.4;
		} else {
			if ((int) (Math.max(ax, az) + (Math.min(ax, az) * 0.5)) < radius)
				return -0.4;
		}

		return Arrays.stream(hollowHillFunctions)
			.max(Comparator.comparing(hollowHillFunction -> hollowHillFunction.compute(context)))
			.map(hollowHillFunction -> hollowHillFunction.compute(context))
			.orElse(Double.NEGATIVE_INFINITY);
//		return getBeardContribution(horizontalDistanceX, verticalDistance, horizontalDistanceZ, verticalDistance) * 2.5;
	}

	protected HollowHillFunction getHollowHillFunction() {
		int length = isXOriented ? boundingBox.getXSpan() : boundingBox.getZSpan();
		int coordinateOffset = random.nextInt(-length / 4, length / 4);
		BoundingBox absouluteMoundApex = moundApex.moved(boundingBox.minX(), boundingBox.minY(), boundingBox.minZ());
		int radius = getRadius(boundingBox);
		return new HollowHillFunction(
			absouluteMoundApex.getCenter().getX() + (isXOriented ? coordinateOffset : random.nextBoolean() ? radius * 1.5f : 0),
			absouluteMoundApex.getCenter().getY() + radius / 3f,
			absouluteMoundApex.getCenter().getZ() + (!isXOriented ? coordinateOffset : random.nextBoolean() ? radius * 1.5f : 0),
			moundRadius, 1);
	}

	protected Beardifier.Rigid getFallenTrunkPiece() {
		Beardifier.Rigid piece = pieceIterator.next();
		this.pieceIterator.back(Integer.MAX_VALUE);
		return piece;
	}

	private static int getRadius(BoundingBox box) {
		return getRadius(box.getYSpan());
	}

	private static int getRadius(int diameter) {
		return (int) Math.ceil(diameter / 2D);
	}
}