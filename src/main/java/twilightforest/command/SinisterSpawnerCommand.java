package twilightforest.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.ParticleArgument;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import twilightforest.block.entity.spawner.SinisterSpawnerBlockEntity;

@twilightforest.beans.Component
public class SinisterSpawnerCommand {
	public LiteralArgumentBuilder<CommandSourceStack> register(CommandBuildContext buildContext) {
		return Commands.literal("sinister_spawner")
			.requires(cs -> cs.hasPermission(2))
			.then(Commands.literal("add_particle").then(Commands.argument("particle", ParticleArgument.particle(buildContext)).then(Commands.argument("pos", BlockPosArgument.blockPos()).executes(this::addParticle))))
			.then(Commands.literal("remove_particle").then(Commands.argument("particle", ParticleArgument.particle(buildContext)).then(Commands.argument("pos", BlockPosArgument.blockPos()).executes(this::removeParticle))))
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
}
