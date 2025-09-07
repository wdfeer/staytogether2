package wdfeer.staytogether

import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.math.Vec3d
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import kotlin.math.pow

object Staytogether : ModInitializer {
    const val MOD_ID: String = "staytogether"

    val LOGGER: Logger = LoggerFactory.getLogger(MOD_ID)

    override fun onInitialize() {
        // Once per dimension
        ServerTickEvents.END_WORLD_TICK.register(ServerTickEvents.EndWorldTick(::tick))

        LOGGER.info("Stay Together loaded!")
    }

    const val MAX_DISTANCE = 5f

    private fun tick(world: ServerWorld) {
        if (world.players.size < 2) return

        val people = world.players.filter { it.isAlive }

        val center = people.map { it.pos }.run {
            Vec3d(sumOf { it.x }, sumOf { it.y }, sumOf { it.z }).multiply(1.0 / size)
        }

        people.filter { it.pos.distanceTo(center) > MAX_DISTANCE }.forEach {
            val delta = center.subtract(it.pos)
            val acceleration = (delta.length() - MAX_DISTANCE).pow(2) * 0.01
            it.addVelocity(delta.normalize().multiply(acceleration))
        }
    }
}