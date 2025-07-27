package xyz.naomieow.difficultyex.entity.player

import com.bibireden.playerex.ext.level
import net.minecraft.server.level.ServerPlayer

class PlayerLevelHandler {
    private val playerLevelContainers: MutableMap<ServerPlayer, PlayerLevelContainer> = mutableMapOf()

    fun insert(player: ServerPlayer) {
        playerLevelContainers[player] = PlayerLevelContainer(player.level)
    }

//    fun insert(players: Collection<ServerPlayer>) {
//        players.forEach { player -> playerLevelContainers[player] = PlayerLevelContainer(player.level) }
//    }

    fun get(player: ServerPlayer): PlayerLevelContainer? {
        return playerLevelContainers[player]
    }

    fun get(players: Collection<ServerPlayer>): Collection<PlayerLevelContainer> {
        return players.mapNotNull(playerLevelContainers::get)
    }
}