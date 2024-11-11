package twilightforest.util.woods;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.WoodType;
import twilightforest.TwilightForestMod;

public class TFWoodTypes {

	public static final BlockSetType TWILIGHT_OAK_SET = BlockSetType.register(new BlockSetType(TwilightForestMod.prefix("twilight_oak").toString()));
	public static final BlockSetType CANOPY_WOOD_SET = BlockSetType.register(new BlockSetType(TwilightForestMod.prefix("canopy").toString()));
	public static final BlockSetType MANGROVE_WOOD_SET = BlockSetType.register(new BlockSetType(TwilightForestMod.prefix("mangrove").toString()));
	public static final BlockSetType DARK_WOOD_SET = BlockSetType.register(new BlockSetType(TwilightForestMod.prefix("dark").toString()));
	public static final BlockSetType TIME_WOOD_SET = BlockSetType.register(new BlockSetType(TwilightForestMod.prefix("time").toString()));
	public static final BlockSetType TRANSFORMATION_WOOD_SET = BlockSetType.register(new BlockSetType(TwilightForestMod.prefix("transformation").toString()));
	public static final BlockSetType MINING_WOOD_SET = BlockSetType.register(new BlockSetType(TwilightForestMod.prefix("mining").toString()));
	public static final BlockSetType SORTING_WOOD_SET = BlockSetType.register(new BlockSetType(TwilightForestMod.prefix("sorting").toString()));
	public static final BlockSetType VEILWOOD_WOOD_SET = BlockSetType.register(new BlockSetType(TwilightForestMod.prefix("veilwood").toString(), true, true, true, BlockSetType.PressurePlateSensitivity.EVERYTHING, SoundType.CHERRY_WOOD, SoundEvents.CHERRY_WOOD_DOOR_CLOSE, SoundEvents.CHERRY_WOOD_DOOR_OPEN, SoundEvents.CHERRY_WOOD_TRAPDOOR_CLOSE, SoundEvents.CHERRY_WOOD_TRAPDOOR_OPEN, SoundEvents.CHERRY_WOOD_PRESSURE_PLATE_CLICK_OFF, SoundEvents.CHERRY_WOOD_PRESSURE_PLATE_CLICK_ON, SoundEvents.CHERRY_WOOD_BUTTON_CLICK_OFF, SoundEvents.CHERRY_WOOD_BUTTON_CLICK_ON));

	public static final WoodType TWILIGHT_OAK_WOOD_TYPE = WoodType.register(new WoodType(TwilightForestMod.prefix("twilight_oak").toString(), TWILIGHT_OAK_SET));
	public static final WoodType CANOPY_WOOD_TYPE = WoodType.register(new WoodType(TwilightForestMod.prefix("canopy").toString(), CANOPY_WOOD_SET));
	public static final WoodType MANGROVE_WOOD_TYPE = WoodType.register(new WoodType(TwilightForestMod.prefix("mangrove").toString(), MANGROVE_WOOD_SET));
	public static final WoodType DARK_WOOD_TYPE = WoodType.register(new WoodType(TwilightForestMod.prefix("dark").toString(), DARK_WOOD_SET));
	public static final WoodType TIME_WOOD_TYPE = WoodType.register(new WoodType(TwilightForestMod.prefix("time").toString(), TIME_WOOD_SET));
	public static final WoodType TRANSFORMATION_WOOD_TYPE = WoodType.register(new WoodType(TwilightForestMod.prefix("transformation").toString(), TRANSFORMATION_WOOD_SET));
	public static final WoodType MINING_WOOD_TYPE = WoodType.register(new WoodType(TwilightForestMod.prefix("mining").toString(), MINING_WOOD_SET));
	public static final WoodType SORTING_WOOD_TYPE = WoodType.register(new WoodType(TwilightForestMod.prefix("sorting").toString(), SORTING_WOOD_SET));
	public static final WoodType VEILWOOD_WOOD_TYPE = WoodType.register(new WoodType(TwilightForestMod.prefix("veilwood").toString(), VEILWOOD_WOOD_SET));
}
