package dev.pokesmells.difficultyex

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.IntegerArgumentType
import com.mojang.brigadier.context.CommandContext
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands
import net.minecraft.commands.arguments.EntityArgument
import net.minecraft.network.chat.Component
import net.minecraft.world.entity.Mob

/**
 * Server-only inspection and testing tools. Never changes player progression.
 * Permission level 2 is required for every subcommand.
 */
object DifficultyExCommands {
    @JvmStatic
    fun register(dispatcher: CommandDispatcher<CommandSourceStack>) {
        dispatcher.register(
            Commands.literal("difficultyex").requires { it.hasPermission(2) }
                .then(
                    Commands.literal("inspect")
                        .then(Commands.argument("target", EntityArgument.entity()).executes { context ->
                            val mob = targetMob(context) ?: return@executes 0
                            val level = (mob as MobLevelAccess).difficultyExGetLevel()
                            context.source.sendSuccess({
                                Component.literal(
                                    "${mob.name.string}: level $level, " +
                                        "health ${mob.health.toInt()}/${mob.maxHealth.toInt()}, " +
                                        "armor ${mob.armorValue}"
                                )
                            }, false)
                            level
                        })
                )
                .then(
                    Commands.literal("set")
                        .then(
                            Commands.argument("target", EntityArgument.entity())
                                .then(
                                    Commands.argument("level", IntegerArgumentType.integer(1))
                                        .executes { context ->
                                            val mob = targetMob(context) ?: return@executes 0
                                            val desired = IntegerArgumentType.getInteger(context, "level")
                                            val max = DifficultyEx.settings.maximumLevel.coerceAtLeast(1)
                                            if (desired > max) {
                                                context.source.sendFailure(
                                                    Component.literal("Level must be between 1 and $max.")
                                                )
                                                return@executes 0
                                            }
                                            DifficultyEx.setMobLevel(mob, desired)
                                            context.source.sendSuccess({
                                                Component.literal("Set ${mob.name.string} to level $desired.")
                                            }, true)
                                            desired
                                        }
                                )
                        )
                )
        )
    }

    private fun targetMob(context: CommandContext<CommandSourceStack>): Mob? {
        val mob = EntityArgument.getEntity(context, "target") as? Mob
        if (mob == null) context.source.sendFailure(Component.literal("Target must be a mob."))
        return mob
    }
}
