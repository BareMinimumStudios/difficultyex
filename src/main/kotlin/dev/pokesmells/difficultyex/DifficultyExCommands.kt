package dev.pokesmells.difficultyex

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.IntegerArgumentType
import com.mojang.brigadier.context.CommandContext
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands
import net.minecraft.commands.arguments.EntityArgument
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.core.registries.Registries
import net.minecraft.network.chat.Component
import net.minecraft.world.entity.Mob
import net.minecraft.world.entity.ai.attributes.Attributes

/**
 * Server-only inspection and testing tools. Never changes player progression.
 * Permission level 2 is required for every subcommand.
 */
object DifficultyExCommands {
    @JvmStatic
    fun register(dispatcher: CommandDispatcher<CommandSourceStack>) {
        dispatcher.register(
            Commands.literal("difficultyex").requires { it.hasPermission(2) }
                .then(Commands.literal("validate").executes { validate(it.source) })
                .then(
                    Commands.literal("inspect")
                        .then(Commands.argument("target", EntityArgument.entity()).executes { context ->
                            val mob = targetMob(context) ?: return@executes 0
                            val level = (mob as MobLevelAccess).difficultyExGetLevel()
                            context.source.sendSuccess({
                                Component.literal(
                                    "${mob.name.string}: level $level, " +
                                        "health ${mob.health}/${mob.maxHealth}, " +
                                        "armor ${mob.armorValue} (attribute ${mob.getAttributeValue(Attributes.ARMOR)}), " +
                                        "attack ${mob.attributes.getInstance(Attributes.ATTACK_DAMAGE)?.value ?: "n/a"}"
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

    private fun validate(source: CommandSourceStack): Int {
        val access = source.server.registryAccess()
        val issues = DifficultyConfigDiagnostics.inspect(
            DifficultyEx.settings,
            source.server.levelKeys().map { it.location().toString() }.toSet(),
            access.registryOrThrow(Registries.BIOME).keySet().map { it.toString() }.toSet(),
            access.registryOrThrow(Registries.STRUCTURE).keySet().map { it.toString() }.toSet(),
            BuiltInRegistries.ENTITY_TYPE.keySet().map { it.toString() }.toSet()
        )
        if (issues.isEmpty()) {
            source.sendSuccess({ Component.literal("Active DifficultyEx config check passed. No inactive rules or formula errors found.") }, false)
            return 1
        }
        source.sendFailure(Component.literal("Active DifficultyEx config: ${issues.size} issue(s). No settings changed."))
        issues.take(20).forEach { source.sendFailure(Component.literal(it)) }
        if (issues.size > 20) source.sendFailure(Component.literal("${issues.size - 20} more issue(s); all issues were written to the server log."))
        val logger = org.slf4j.LoggerFactory.getLogger("difficultyex/config-check")
        issues.forEach { logger.warn("{}", it) }
        return 0
    }

    private fun targetMob(context: CommandContext<CommandSourceStack>): Mob? {
        val mob = EntityArgument.getEntity(context, "target") as? Mob
        if (mob == null) context.source.sendFailure(Component.literal("Target must be a mob."))
        return mob
    }
}
