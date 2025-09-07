package wdfeer.staytogether

import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.server.world.ServerWorld
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.function.Consumer


object Staytogether : ModInitializer {
    const val MOD_ID: String = "staytogether"

    val LOGGER: Logger = LoggerFactory.getLogger(MOD_ID)

    override fun onInitialize() {
        val maxDistance = 10f
        ServerTickEvents.END_WORLD_TICK.register(ServerTickEvents.EndWorldTick { world: ServerWorld? ->
            world!!.getPlayers().forEach(
                Consumer { p1: ServerPlayerEntity? ->
                    if (world.getPlayers().stream().filter { p2: ServerPlayerEntity? -> p1 !== p2 }
                            .noneMatch { p2: ServerPlayerEntity? -> p1!!.distanceTo(p2) < maxDistance }) {
                        p1!!.damage(world, world.damageSources.magic(), 1f)
                    }
                })
        })

        LOGGER.info("Stay Together loaded!")

    }
}