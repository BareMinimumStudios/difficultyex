package xyz.naomieow.difficultyex

import net.fabricmc.api.ClientModInitializer
import xyz.naomieow.difficultyex.network.NameplateClientPacket

object DifficultyEXClient : ClientModInitializer {
	override fun onInitializeClient() {
		NameplateClientPacket.init()
	}
}