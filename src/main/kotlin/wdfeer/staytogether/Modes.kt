package wdfeer.staytogether

import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.math.Vec3d
import kotlin.math.pow

typealias Ticker = ServerWorld.() -> Unit

private fun doOnAlivePlayers(
    action: List<ServerPlayerEntity>.() -> Unit
): Ticker = {
    val people = players.filter { it.isAlive }
    if (people.size > 1) people.action()
}

private const val MAX_DISTANCE = 10.0
val modes: Map<String, Ticker> = mapOf(
    "off" to {},
    "damage" to doOnAlivePlayers {
        filter { p1 -> (this - p1).none { p2 -> p2.distanceTo(p1) < MAX_DISTANCE } }.forEach {
            it.damage(
                it.world,
                it.world.damageSources.magic(),
                1f
            )
        }
    },
    "pull" to doOnAlivePlayers {
        val center = map { it.pos }.run {
            Vec3d(sumOf { it.x }, sumOf { it.y }, sumOf { it.z }).multiply(1.0 / size)
        }

        filter { it.pos.distanceTo(center) > MAX_DISTANCE / 2 }.forEach {
            val delta = center.subtract(it.pos)
            val acceleration = (delta.length() - MAX_DISTANCE / 2).pow(2) * 0.01
            it.addVelocity(delta.normalize().multiply(acceleration))
            it.velocityModified = true
        }
    },
    "jerk" to doOnAlivePlayers {
        if (this[0].world.time % 20 == 0L) {
            val center = map { it.pos }.run {
                Vec3d(sumOf { it.x }, sumOf { it.y }, sumOf { it.z }).multiply(1.0 / size)
            }

            filter { it.pos.distanceTo(center) > MAX_DISTANCE / 2 }.forEach {
                val delta = center.subtract(it.pos)
                val acceleration = (delta.length() - MAX_DISTANCE / 2).pow(1.5) * 0.1
                it.addVelocity(delta.normalize().multiply(acceleration))
                it.velocityModified = true
            }
        }
    },
    "teleport" to doOnAlivePlayers {
        val center = map { it.pos }.run {
            Vec3d(sumOf { it.x }, sumOf { it.y }, sumOf { it.z }).multiply(1.0 / size)
        }

        filter { it.pos.distanceTo(center) > MAX_DISTANCE / 2 }.forEach {
            val pos = center.add(it.pos.subtract(center).normalize().multiply(MAX_DISTANCE / 2))
            pos.run { it.teleport(x, y, z, false) }
        }
    }
)
