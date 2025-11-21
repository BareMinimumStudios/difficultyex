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
import xyz.naomieow.difficultyex.DifficultyEX

object NameplateServerPacket {
    val TITLE_CS_COMPAT: ResourceLocation = ResourceLocation.tryBuild("nameplate", "title_cs_compat")!!
    val TITLE_SC_COMPAT: ResourceLocation = ResourceLocation.tryBuild("nameplate", "title_sc_compat")!!

    fun init() {
        ServerPlayNetworking.registerGlobalReceiver(TITLE_CS_COMPAT) { server: MinecraftServer, player: ServerPlayer, handler: ServerGamePacketListenerImpl?, buffer: FriendlyByteBuf?, sender: PacketSender? ->
            server.execute {
                val entity: Skeleton = EntityType.SKELETON.create(player.level())!!
                entity.moveTo(player.x, player.y, player.z, 0.0f, 0.0f)

                val biome = player.level().getBiome(entity.blockPosition()).unwrapKey().get().location()

                writeS2TravelerCompatPacket(player, DifficultyEX.CONFIG.biomeScalingSettings.startingLevels[biome] ?: DifficultyEX.CONFIG.dimensionSettings.startingLevels.getOrDefault(player.level().dimension().location(), 0))
                entity.discard()
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