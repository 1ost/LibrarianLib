package com.teamwizardry.librarianlib.prism.nbt

import net.minecraft.nbt.ByteArrayNBT
import net.minecraft.nbt.DoubleNBT
import net.minecraft.nbt.FloatNBT
import net.minecraft.nbt.INBT
import net.minecraft.nbt.IntArrayNBT
import net.minecraft.nbt.ListNBT
import net.minecraft.nbt.LongArrayNBT
import net.minecraft.nbt.NumberNBT

internal object PrimitiveDoubleArraySerializer: NBTSerializer<DoubleArray>() {
    override fun deserialize(tag: INBT, existing: DoubleArray?): DoubleArray {
        @Suppress("NAME_SHADOWING") val tag = tag.expectType<ListNBT>("tag")
        val array = if(tag.size == existing?.size) existing else DoubleArray(tag.size)
        tag.forEachIndexed { index, inbt ->
            array[index] = inbt.expectType<NumberNBT>("index $index").asDouble
        }
        return array
    }

    override fun serialize(value: DoubleArray): INBT {
        return value.mapTo(ListNBT()) { DoubleNBT.valueOf(it) }
    }
}

internal object PrimitiveFloatArraySerializer: NBTSerializer<FloatArray>() {
    override fun deserialize(tag: INBT, existing: FloatArray?): FloatArray {
        @Suppress("NAME_SHADOWING") val tag = tag.expectType<ListNBT>("tag")
        val array = if(tag.size == existing?.size) existing else FloatArray(tag.size)
        tag.forEachIndexed { index, inbt ->
            array[index] = inbt.expectType<NumberNBT>("index $index").asFloat
        }
        return array
    }

    override fun serialize(value: FloatArray): INBT {
        return value.mapTo(ListNBT()) { FloatNBT.valueOf(it) }
    }
}

internal object PrimitiveLongArraySerializer: NBTSerializer<LongArray>() {
    override fun deserialize(tag: INBT, existing: LongArray?): LongArray {
        @Suppress("NAME_SHADOWING") val tag = tag.expectType<LongArrayNBT>("tag")
        if(tag.asLongArray.size == existing?.size) {
            tag.asLongArray.copyInto(existing)
            return existing
        } else {
            return tag.asLongArray
        }
    }

    override fun serialize(value: LongArray): INBT {
        return LongArrayNBT(value)
    }
}

internal object PrimitiveIntArraySerializer: NBTSerializer<IntArray>() {
    override fun deserialize(tag: INBT, existing: IntArray?): IntArray {
        @Suppress("NAME_SHADOWING") val tag = tag.expectType<IntArrayNBT>("tag")
        if(tag.asIntArray.size == existing?.size) {
            tag.asIntArray.copyInto(existing)
            return existing
        } else {
            return tag.asIntArray
        }
    }

    override fun serialize(value: IntArray): INBT {
        return IntArrayNBT(value)
    }
}

internal object PrimitiveShortArraySerializer: NBTSerializer<ShortArray>() {
    override fun deserialize(tag: INBT, existing: ShortArray?): ShortArray {
        @Suppress("NAME_SHADOWING") val tag = tag.expectType<IntArrayNBT>("tag")
        if(tag.asIntArray.size == existing?.size) {
            tag.asIntArray.forEachIndexed { index, value ->
                existing[index] = value.toShort()
            }
            return existing
        } else {
            return ShortArray(tag.asIntArray.size) { tag.asIntArray[it].toShort() }
        }
    }

    override fun serialize(value: ShortArray): INBT {
        return IntArrayNBT(IntArray(value.size) { value[it].toInt() })
    }
}

internal object PrimitiveCharArraySerializer: NBTSerializer<CharArray>() {
    override fun deserialize(tag: INBT, existing: CharArray?): CharArray {
        @Suppress("NAME_SHADOWING") val tag = tag.expectType<IntArrayNBT>("tag")
        if(tag.asIntArray.size == existing?.size) {
            tag.asIntArray.forEachIndexed { index, value ->
                existing[index] = value.toChar()
            }
            return existing
        } else {
            return CharArray(tag.asIntArray.size) { tag.asIntArray[it].toChar() }
        }
    }

    override fun serialize(value: CharArray): INBT {
        return IntArrayNBT(IntArray(value.size) { value[it].toInt() })
    }
}

internal object PrimitiveByteArraySerializer: NBTSerializer<ByteArray>() {
    override fun deserialize(tag: INBT, existing: ByteArray?): ByteArray {
        @Suppress("NAME_SHADOWING") val tag = tag.expectType<ByteArrayNBT>("tag")
        if(tag.asByteArray.size == existing?.size) {
            tag.asByteArray.copyInto(existing)
            return existing
        } else {
            return tag.asByteArray
        }
    }

    override fun serialize(value: ByteArray): INBT {
        return ByteArrayNBT(value)
    }
}

internal object PrimitiveBooleanArraySerializer: NBTSerializer<BooleanArray>() {
    override fun deserialize(tag: INBT, existing: BooleanArray?): BooleanArray {
        @Suppress("NAME_SHADOWING") val tag = tag.expectType<ByteArrayNBT>("tag")
        if(tag.asByteArray.size == existing?.size) {
            tag.asByteArray.forEachIndexed { index, value ->
                existing[index] = value != 0.toByte()
            }
            return existing
        } else {
            return BooleanArray(tag.asByteArray.size) { tag.asByteArray[it] != 0.toByte() }
        }
    }

    override fun serialize(value: BooleanArray): INBT {
        return ByteArrayNBT(ByteArray(value.size) { if(value[it]) 1.toByte() else 0.toByte() })
    }
}
