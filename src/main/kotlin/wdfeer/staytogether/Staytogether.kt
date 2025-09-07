package wdfeer.staytogether

import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents
import net.minecraft.server.world.ServerWorld
import org.slf4j.Logger
import org.slf4j.LoggerFactory

object Staytogether : ModInitializer {
    const val MOD_ID: String = "staytogether"

    val LOGGER: Logger = LoggerFactory.getLogger(MOD_ID)

    // TODO: make persistent per world
    var mode: String = modes.entries.first().key

    override fun onInitialize() {
        // TODO: add command to switch modes

        // Once per dimension
        ServerTickEvents.END_WORLD_TICK.register(ServerTickEvents.EndWorldTick(::tick))

        LOGGER.info("Stay Together loaded!")
    }

    private fun tick(world: ServerWorld) = modes[mode]!!(world)
}