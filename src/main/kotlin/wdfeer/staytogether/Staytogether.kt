package wdfeer.staytogether

import com.mojang.brigadier.builder.LiteralArgumentBuilder
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents
import net.minecraft.server.command.CommandManager.literal
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.server.world.ServerWorld
import org.slf4j.Logger
import org.slf4j.LoggerFactory

object Staytogether : ModInitializer {
    const val MOD_ID: String = "staytogether"

    val LOGGER: Logger = LoggerFactory.getLogger(MOD_ID)

    // TODO: make persistent per world
    var mode: String = modes.entries.first().key

    override fun onInitialize() {
        CommandRegistrationCallback.EVENT.register(commandRegistration)

        // Once per dimension
        ServerTickEvents.END_WORLD_TICK.register(ServerTickEvents.EndWorldTick(::tick))

        LOGGER.info("Stay Together loaded!")
    }

    private val commandRegistration = CommandRegistrationCallback { dispatcher, _, _ ->
        val addSubcommands: LiteralArgumentBuilder<ServerCommandSource>.() -> LiteralArgumentBuilder<ServerCommandSource> = {
            modes.forEach { entry ->
                then(literal(entry.key)).executes {
                    mode = entry.key
                    1
                }
            }
            this
        }
        val command = literal("staytogether").requires { it.hasPermissionLevel(2) }.addSubcommands()
        dispatcher.register(command)
    }

    private fun tick(world: ServerWorld) = modes[mode]!!(world)
}