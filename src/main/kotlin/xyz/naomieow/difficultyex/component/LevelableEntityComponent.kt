package xyz.naomieow.difficultyex.component

import com.bibireden.data_attributes.endec.nbt.NbtDeserializer
import com.bibireden.data_attributes.endec.nbt.NbtSerializer
import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent
import io.wispforest.endec.Endec
import io.wispforest.endec.impl.StructEndecBuilder
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.world.entity.Mob
import xyz.naomieow.difficultyex.event.EntityLevelingEvents

class LevelableEntityComponent(private var entity: Mob, private var _level: Int = 0) : ILevelableEntityComponent, AutoSyncedComponent {
    override val level: Int
        get() = this._level

    override fun set(value: Int) {
        this._level = value
        EntityLevelingEvents.CHANGED.invoker().onEntityLevelChanged(entity, _level)
    }

    override fun readFromNbt(tag: CompoundTag) {
        ENDEC.decodeFully(NbtDeserializer::of, tag.get("DATA")).also {
            this.set(it.level)
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