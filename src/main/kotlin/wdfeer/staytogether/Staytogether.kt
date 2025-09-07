package wdfeer.staytogether

import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents
import net.minecraft.server.world.ServerWorld
import org.slf4j.Logger
import org.slf4j.LoggerFactory

object Staytogether : ModInitializer {
    const val MOD_ID: String = "staytogether"

    val LOGGER: Logger = LoggerFactory.getLogger(MOD_ID)

    override fun onInitialize() {
        ServerTickEvents.END_WORLD_TICK.register(ServerTickEvents.EndWorldTick(::tick))
        LOGGER.info("Stay Together loaded!")
    }

    const val MAX_DISTANCE = 10f
    const val ACCELERATION = 0.02

    private fun tick(world: ServerWorld) {
        if (world.players.size < 2) return

        // TODO: implement center-of-mass attraction for 3+ players
        val pair = world.players.take(2)

        if (pair[0].distanceTo(pair[1]) > MAX_DISTANCE) {
            val dir = pair[1].pos.subtract(pair[0].pos).normalize()

            pair[0].addVelocity(dir.multiply(ACCELERATION))
            pair[1].addVelocity(dir.multiply(-ACCELERATION))
        }
    }
}