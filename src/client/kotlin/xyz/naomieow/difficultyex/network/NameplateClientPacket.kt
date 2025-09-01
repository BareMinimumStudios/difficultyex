package xyz.naomieow.difficultyex.network

import com.yungnickyoung.minecraft.travelerstitles.TravelersTitlesCommon
import io.netty.buffer.Unpooled
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking
import net.fabricmc.fabric.api.networking.v1.PacketSender
import net.minecraft.client.Minecraft
import net.minecraft.client.multiplayer.ClientPacketListener
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.network.chat.Component
import net.minecraft.network.protocol.game.ClientboundCustomPayloadPacket
import net.minecraft.network.protocol.game.ServerboundCustomPayloadPacket

object NameplateClientPacket {
    fun init() {
        ClientPlayNetworking.registerGlobalReceiver(NameplateServerPacket.TITLE_SC_COMPAT) { client: Minecraft, handler: ClientPacketListener?, buffer: FriendlyByteBuf, responseSender: PacketSender? ->
            val mobLevel = buffer.readInt()
            client.execute {
                if (TravelersTitlesCommon.titleManager.biomeTitleRenderer.displayedTitle != null) {
                    TravelersTitlesCommon.titleManager.biomeTitleRenderer.displayTitle(TravelersTitlesCommon.titleManager.biomeTitleRenderer.displayedTitle, Component.translatable("text.nameplate.level", mobLevel))
                }
            }
        }
    }

    @JvmStatic
    fun writeC2STravelerCompatPacket() {
        val buf = FriendlyByteBuf(Unpooled.buffer())
        val packet = ServerboundCustomPayloadPacket(NameplateServerPacket.TITLE_CS_COMPAT, buf)
        Minecraft.getInstance().connection?.send(packet)
    }
}