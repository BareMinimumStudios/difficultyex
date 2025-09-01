package xyz.naomieow.difficultyex.network

import io.netty.buffer.Unpooled
import net.fabricmc.fabric.api.networking.v1.PacketSender
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.network.protocol.game.ClientboundCustomPayloadPacket
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.MinecraftServer
import net.minecraft.server.level.ServerPlayer
import net.minecraft.server.network.ServerGamePacketListenerImpl
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.monster.Skeleton
import xyz.naomieow.difficultyex.ext.difficultyExLevel

object NameplateServerPacket {
    val TITLE_CS_COMPAT: ResourceLocation = ResourceLocation.tryBuild("nameplate", "title_cs_compat")!!
    val TITLE_SC_COMPAT: ResourceLocation = ResourceLocation.tryBuild("nameplate", "title_sc_compat")!!

    fun init() {
        ServerPlayNetworking.registerGlobalReceiver(TITLE_CS_COMPAT) { server: MinecraftServer, player: ServerPlayer, handler: ServerGamePacketListenerImpl?, buffer: FriendlyByteBuf?, sender: PacketSender? ->
            server.execute {
                val skeletonEntity: Skeleton = EntityType.SKELETON.create(player.level())!!
                skeletonEntity.moveTo(player.x, player.y, player.z, 0.0f, 0.0f)
                writeS2TravelerCompatPacket(player, skeletonEntity.difficultyExLevel)
                skeletonEntity.discard()
            }
        }
    }

    @JvmStatic
    fun writeS2TravelerCompatPacket(serverPlayerEntity: ServerPlayer, level: Int) {
        val buf = FriendlyByteBuf(Unpooled.buffer())
        buf.writeInt(level)
        val packet = ClientboundCustomPayloadPacket(TITLE_SC_COMPAT, buf)
        serverPlayerEntity.connection.send(packet)
    }
}