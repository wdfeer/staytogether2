package wdfeer.staytogether

import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents
import net.minecraft.server.network.ServerPlayerEntity
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
    private fun tick(world: ServerWorld) {
        world.players.forEach { p1: ServerPlayerEntity ->
            if ((world.players - p1).none { it!!.distanceTo(p1) < MAX_DISTANCE })
                p1.damage(world, world.damageSources.magic(), 1f)
        }
    }
}