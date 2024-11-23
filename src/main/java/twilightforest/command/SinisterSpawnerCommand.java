package twilightforest.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.ParticleArgument;
import net.minecraft.commands.arguments.ResourceOrIdArgument;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.ReloadableServerRegistries;
import net.minecraft.world.level.storage.loot.LootTable;
import twilightforest.block.entity.spawner.SinisterSpawnerBlockEntity;

@twilightforest.beans.Component
public class SinisterSpawnerCommand {
	// Copied from LootCommand.SUGGEST_LOOT_TABLE
	public static final SuggestionProvider<CommandSourceStack> SUGGEST_LOOT_TABLE = (context, builder) -> {
		ReloadableServerRegistries.Holder holder = context.getSource().getServer().reloadableRegistries();
		return SharedSuggestionProvider.suggestResource(holder.getKeys(Registries.LOOT_TABLE), builder);
	};

	public LiteralArgumentBuilder<CommandSourceStack> register(CommandBuildContext buildContext) {
		return Commands.literal("sinister_spawner")
			.requires(cs -> cs.hasPermission(2))
			.then(Commands.literal("add_particle").then(Commands.argument("particle", ParticleArgument.particle(buildContext)).then(Commands.argument("pos", BlockPosArgument.blockPos()).executes(this::addParticle))))
			.then(Commands.literal("remove_particle").then(Commands.argument("particle", ParticleArgument.particle(buildContext)).then(Commands.argument("pos", BlockPosArgument.blockPos()).executes(this::removeParticle))))
			.then(Commands.literal("set_loot").then(Commands.argument("loot", ResourceOrIdArgument.lootTable(buildContext)).suggests(SUGGEST_LOOT_TABLE).then(Commands.argument("pos", BlockPosArgument.blockPos()).executes(this::setLootTable))))
			.then(Commands.literal("clear_loot").then(Commands.argument("pos", BlockPosArgument.blockPos()).executes(this::clearLootTable)))
			;
	}

	private int addParticle(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
		ParticleOptions options = ParticleArgument.getParticle(context, "particle");
		BlockPos pos = BlockPosArgument.getLoadedBlockPos(context, "pos");

		if (context.getSource().getLevel().getBlockEntity(pos) instanceof SinisterSpawnerBlockEntity entity) {
			if (entity.addParticle(options))
				return 1;
		}

		return 0;
	}

	private int removeParticle(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
		ParticleOptions options = ParticleArgument.getParticle(context, "particle");
		BlockPos pos = BlockPosArgument.getLoadedBlockPos(context, "pos");

		if (context.getSource().getLevel().getBlockEntity(pos) instanceof SinisterSpawnerBlockEntity entity) {
			if (entity.removeParticle(options))
				return 1;
		}

		return 0;
	}

	private int setLootTable(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
		BlockPos pos = BlockPosArgument.getLoadedBlockPos(context, "pos");
		Holder<LootTable> loot = ResourceOrIdArgument.getLootTable(context, "loot");

		if (context.getSource().getLevel().getBlockEntity(pos) instanceof SinisterSpawnerBlockEntity entity)
			if (entity.setLootTable(loot.getKey()))
				return 1;

		return 0;
	}

	private int clearLootTable(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
		BlockPos pos = BlockPosArgument.getLoadedBlockPos(context, "pos");

		if (context.getSource().getLevel().getBlockEntity(pos) instanceof SinisterSpawnerBlockEntity entity)
			if (entity.setLootTable(null))
				return 1;

		return 0;
	}
}
