package twilightforest.world.components.structures.fallentrunk;

import net.minecraft.util.RandomSource;
import twilightforest.TwilightForestMod;

public class Hole {
	private final boolean[][] hole;
	private final int maxAttemptAmount = 1000;
	public Hole(FallenTrunkPiece piece, RandomSource random) {
		this.hole = buildHoleArray(piece.length, piece.getSideLength() * 4, random, piece);
	}

	private boolean[][] buildHoleArray(int length, int height, RandomSource random, FallenTrunkPiece piece) {
		int arrayLength = length - (FallenTrunkPiece.ERODED_LENGTH + 1) * 2;
		boolean[][] arr = new boolean[height][arrayLength];

		int X_MIN_SIZE = piece.length / 6;
		int X_MAX_SIZE = piece.length / 4 + 1;
		int Y_MIN_SIZE = piece.getSideLength();
		int Y_MAX_SIZE = piece.getSideLength() * 2;


		int xSize;
		int ySize = random.nextInt(Y_MIN_SIZE, Y_MAX_SIZE);

		int yOffset = random.nextInt(1, Y_MAX_SIZE - ySize + 1);

		int previousX1 = Integer.MIN_VALUE, previousX2 = Integer.MAX_VALUE;
		for (int dy = yOffset; dy < yOffset + ySize; dy++) {
			xSize = random.nextInt(X_MIN_SIZE, X_MAX_SIZE + 1);
			int x1 = random.nextInt(arrayLength - xSize - 1);
			int x2 = x1 + xSize - 1;

			int tries = 0;
			while (tries < maxAttemptAmount && (x1 == previousX1 || x2 == previousX2 && dy % Y_MIN_SIZE != 0) || x2 < previousX1 || x1 > previousX2
				|| (float) (Math.min(x2, previousX2) - Math.max(previousX1, x1) + 1) / (Math.max(x2, previousX2) - Math.min(x1, previousX1) + 1) < 1 / 3f) {
				xSize = random.nextInt(X_MIN_SIZE, X_MAX_SIZE + 1);
				x1 = random.nextInt(Math.max(0, previousX1 - xSize), Math.min(arrayLength - xSize - 1, previousX2 + xSize));
				x2 = x1 + xSize - 1;
				tries++;
			}
			System.out.println(tries);

			if (tries >= maxAttemptAmount)
				TwilightForestMod.LOGGER.error("Too many tries during generation of the hole in Fallen Trunk! Please contact TF devs with seed and {}", piece.getBoundingBox().getCenter().toString());

			previousX1 = x1;
			previousX2 = x2;

			for (int x = x1; x <= x2; x++) {
				arr[dy][x] = true;
			}
		}

		return arr;
	}

	public boolean isInHole(int x, int y) {
		return hole[x][y];
	}
}
