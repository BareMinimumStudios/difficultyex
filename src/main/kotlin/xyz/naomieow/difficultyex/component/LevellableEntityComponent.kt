package xyz.naomieow.difficultyex.component

import com.bibireden.data_attributes.endec.nbt.NbtDeserializer
import com.bibireden.data_attributes.endec.nbt.NbtSerializer
import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent
import io.wispforest.endec.Endec
import io.wispforest.endec.impl.StructEndecBuilder
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.FriendlyByteBuf

class LevellableEntityComponent(
    private var _level: Int = 0
) : ILevellableEntityComponent, AutoSyncedComponent {
    override val level: Int
        get() = this._level

    override fun readFromNbt(tag: CompoundTag) {
        ENDEC.decodeFully(NbtDeserializer::of, tag.get("DATA")).also {
            this._level = it.level
        }
    }

    override fun writeToNbt(tag: CompoundTag) {
        tag.put("DATA", ENDEC.encodeFully(
            NbtSerializer::of,
            Packet(this._level)
        ))
    }

    data class Packet(val level: Int)

    companion object {
        val ENDEC: Endec<Packet> = StructEndecBuilder.of(
            Endec.INT.fieldOf("level") {it.level},
            ::Packet
        )
    }

    override fun applySyncPacket(buf: FriendlyByteBuf) {
        this.readFromNbt(buf.readNbt() ?: return)
    }
}