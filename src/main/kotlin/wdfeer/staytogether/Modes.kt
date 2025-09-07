package wdfeer.staytogether

import net.minecraft.server.world.ServerWorld
import net.minecraft.util.math.Vec3d
import kotlin.math.pow

const val MAX_DISTANCE = 5f

val modes: Map<String, (ServerWorld) -> Unit> = mapOf(
    "tether" to { world ->
        val people = world.players.filter { it.isAlive }
        if (people.size > 1) {
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
)
