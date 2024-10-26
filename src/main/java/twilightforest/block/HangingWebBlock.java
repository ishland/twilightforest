package twilightforest.block;

import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Spider;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.common.IShearable;

import javax.annotation.Nullable;
import java.util.Iterator;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class HangingWebBlock extends Block implements IShearable {
	public static final MapCodec<HangingWebBlock> CODEC = simpleCodec(HangingWebBlock::new);
	public static final BooleanProperty UP = PipeBlock.UP;
	public static final BooleanProperty DOWN = PipeBlock.DOWN;
	public static final BooleanProperty NORTH = PipeBlock.NORTH;
	public static final BooleanProperty EAST = PipeBlock.EAST;
	public static final BooleanProperty SOUTH = PipeBlock.SOUTH;
	public static final BooleanProperty WEST = PipeBlock.WEST;
	public static final Map<Direction, BooleanProperty> PROPERTY_BY_DIRECTION = PipeBlock.PROPERTY_BY_DIRECTION;
	private static final VoxelShape UP_AABB = Block.box(0.0, 15.0, 0.0, 16.0, 16.0, 16.0);
	private static final VoxelShape DOWN_AABB = Block.box(0.0, 0.0, 0.0, 16.0, 1.0, 16.0);
	private static final VoxelShape WEST_AABB = Block.box(0.0, 0.0, 0.0, 1.0, 16.0, 16.0);
	private static final VoxelShape EAST_AABB = Block.box(15.0, 0.0, 0.0, 16.0, 16.0, 16.0);
	private static final VoxelShape NORTH_AABB = Block.box(0.0, 0.0, 0.0, 16.0, 16.0, 1.0);
	private static final VoxelShape SOUTH_AABB = Block.box(0.0, 0.0, 15.0, 16.0, 16.0, 16.0);
	private final Map<BlockState, VoxelShape> shapesCache;

	public HangingWebBlock(BlockBehaviour.Properties properties) {
		super(properties);
		this.registerDefaultState(this.stateDefinition.any().setValue(UP, false).setValue(DOWN, false).setValue(NORTH, false).setValue(EAST, false).setValue(SOUTH, false).setValue(WEST, false));
		this.shapesCache = ImmutableMap.copyOf(this.stateDefinition.getPossibleStates().stream().collect(Collectors.toMap(Function.identity(), HangingWebBlock::calculateShape)));
	}

	@Override
	public MapCodec<HangingWebBlock> codec() {
		return CODEC;
	}

	@Override
	protected void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
		if (entity instanceof Spider) return;
		if (entity instanceof LivingEntity livingentity && (livingentity.hasEffect(MobEffects.WEAVING) || livingentity.isShiftKeyDown())) return;
		entity.makeStuckInBlock(state, new Vec3(0.95, 0.95, 0.95));

		// dissolve
		if (!level.isClientSide() && ((entity instanceof LivingEntity living && living.getRandom().nextInt(20) == 1) || entity instanceof Projectile)) {
			level.destroyBlock(pos, false);
		}
	}

	private static VoxelShape calculateShape(BlockState state) {
		VoxelShape voxelshape = Shapes.empty();
		if (state.getValue(UP)) {
			voxelshape = UP_AABB;
		}

		if (state.getValue(DOWN)) {
			voxelshape = Shapes.or(voxelshape, DOWN_AABB);
		}

		if (state.getValue(NORTH)) {
			voxelshape = Shapes.or(voxelshape, NORTH_AABB);
		}

		if (state.getValue(SOUTH)) {
			voxelshape = Shapes.or(voxelshape, SOUTH_AABB);
		}

		if (state.getValue(EAST)) {
			voxelshape = Shapes.or(voxelshape, EAST_AABB);
		}

		if (state.getValue(WEST)) {
			voxelshape = Shapes.or(voxelshape, WEST_AABB);
		}

		return voxelshape.isEmpty() ? Shapes.block() : voxelshape;
	}

	@Override
	protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		return this.shapesCache.get(state);
	}

	@Override
	protected boolean propagatesSkylightDown(BlockState state, BlockGetter level, BlockPos pos) {
		return true;
	}

	@Override
	protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
		return this.hasFaces(this.getUpdatedState(state, level, pos));
	}

	private boolean hasFaces(BlockState state) {
		return this.countFaces(state) > 0;
	}

	private int countFaces(BlockState state) {
		int i = 0;

		for (BooleanProperty booleanproperty : PROPERTY_BY_DIRECTION.values()) {
			if (state.getValue(booleanproperty)) {
				++i;
			}
		}

		return i;
	}

	private boolean canSupportAtFace(BlockGetter level, BlockPos pos, Direction direction) {
		BlockPos blockpos = pos.relative(direction);
		if (isAcceptableNeighbour(level, blockpos, direction)) {
			return true;
		} else if (direction.getAxis() == Direction.Axis.Y) {
			return false;
		} else {
			BooleanProperty booleanproperty = PROPERTY_BY_DIRECTION.get(direction);
			BlockState blockstate = level.getBlockState(pos.above());
			return blockstate.is(this) && blockstate.getValue(booleanproperty);
		}
	}

	public static boolean isAcceptableNeighbour(BlockGetter blockReader, BlockPos neighborPos, Direction attachedFace) {
		return MultifaceBlock.canAttachTo(blockReader, attachedFace, neighborPos, blockReader.getBlockState(neighborPos));
	}

	private BlockState getUpdatedState(BlockState state, BlockGetter level, BlockPos pos) {
		BlockPos blockpos = pos.above();
		if (state.getValue(UP)) {
			state = state.setValue(UP, isAcceptableNeighbour(level, blockpos, Direction.DOWN));
		}

		BlockState blockstate = null;
		Iterator<Direction> iterator = Direction.Plane.HORIZONTAL.iterator();

		while(true) {
			Direction direction;
			BooleanProperty booleanproperty;
			do {
				if (!iterator.hasNext()) {
					return state;
				}

				direction = iterator.next();
				booleanproperty = getPropertyForFace(direction);
			} while(!state.getValue(booleanproperty));

			boolean flag = this.canSupportAtFace(level, pos, direction);
			if (!flag) {
				if (blockstate == null) {
					blockstate = level.getBlockState(blockpos);
				}

				flag = blockstate.is(this) && blockstate.getValue(booleanproperty);
			}

			state = state.setValue(booleanproperty, flag);
		}
	}

	@Override
	protected BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor level, BlockPos currentPos, BlockPos facingPos) {
		BlockState blockstate = this.getUpdatedState(state, level, currentPos);
		return !this.hasFaces(blockstate) ? Blocks.AIR.defaultBlockState() : blockstate;
	}

	@Override
	protected boolean canBeReplaced(BlockState state, BlockPlaceContext useContext) {
		BlockState blockstate = useContext.getLevel().getBlockState(useContext.getClickedPos());
		return blockstate.is(this) ? this.countFaces(blockstate) < PROPERTY_BY_DIRECTION.size() : super.canBeReplaced(state, useContext);
	}

	@Nullable
	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		BlockState blockstate = context.getLevel().getBlockState(context.getClickedPos());
		boolean flag = blockstate.is(this);
		BlockState blockstate1 = flag ? blockstate : this.defaultBlockState();

		for (Direction direction : context.getNearestLookingDirections()) {
			BooleanProperty booleanproperty = getPropertyForFace(direction);
			boolean flag1 = flag && blockstate.getValue(booleanproperty);
			if (!flag1 && this.canSupportAtFace(context.getLevel(), context.getClickedPos(), direction)) {
				return blockstate1.setValue(booleanproperty, true);
			}

		}

		return flag ? blockstate1 : null;
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(UP, DOWN, NORTH, EAST, SOUTH, WEST);
	}

	@Override
	@SuppressWarnings("deprecation")
	protected BlockState rotate(BlockState state, Rotation rotate) {
		return switch (rotate) {
			case CLOCKWISE_180 ->
				state.setValue(NORTH, state.getValue(SOUTH)).setValue(EAST, state.getValue(WEST)).setValue(SOUTH, state.getValue(NORTH)).setValue(WEST, state.getValue(EAST));
			case COUNTERCLOCKWISE_90 ->
				state.setValue(NORTH, state.getValue(EAST)).setValue(EAST, state.getValue(SOUTH)).setValue(SOUTH, state.getValue(WEST)).setValue(WEST, state.getValue(NORTH));
			case CLOCKWISE_90 ->
				state.setValue(NORTH, state.getValue(WEST)).setValue(EAST, state.getValue(NORTH)).setValue(SOUTH, state.getValue(EAST)).setValue(WEST, state.getValue(SOUTH));
			default -> state;
		};
	}

	@Override
	@SuppressWarnings("deprecation")
	protected BlockState mirror(BlockState state, Mirror mirror) {
		return switch (mirror) {
			case LEFT_RIGHT -> state.setValue(NORTH, state.getValue(SOUTH)).setValue(SOUTH, state.getValue(NORTH));
			case FRONT_BACK -> state.setValue(EAST, state.getValue(WEST)).setValue(WEST, state.getValue(EAST));
			default -> super.mirror(state, mirror);
		};
	}

	public static BooleanProperty getPropertyForFace(Direction face) {
		return PROPERTY_BY_DIRECTION.get(face);
	}
}
