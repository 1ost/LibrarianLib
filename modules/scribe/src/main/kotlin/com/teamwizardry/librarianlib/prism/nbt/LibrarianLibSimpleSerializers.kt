package com.teamwizardry.librarianlib.prism.nbt

import com.teamwizardry.librarianlib.math.Matrix3d
import com.teamwizardry.librarianlib.math.Matrix4d
import com.teamwizardry.librarianlib.math.MutableMatrix3d
import com.teamwizardry.librarianlib.math.MutableMatrix4d
import com.teamwizardry.librarianlib.math.Quaternion
import com.teamwizardry.librarianlib.math.Rect2d
import com.teamwizardry.librarianlib.math.Vec2d
import com.teamwizardry.librarianlib.math.Vec2i
import net.minecraft.nbt.CompoundNBT
import net.minecraft.nbt.DoubleNBT
import net.minecraft.nbt.INBT
import net.minecraft.nbt.IntNBT
import net.minecraft.nbt.ListNBT
import net.minecraft.nbt.NumberNBT

internal object Vec2dSerializer: NBTSerializer<Vec2d>() {
    override fun deserialize(tag: INBT, existing: Vec2d?): Vec2d {
        @Suppress("NAME_SHADOWING") val tag = tag.expectType<CompoundNBT>("tag")
        return Vec2d(
            tag.expect<NumberNBT>("X").asDouble,
            tag.expect<NumberNBT>("Y").asDouble
        )
    }

    override fun serialize(value: Vec2d): INBT {
        val tag = CompoundNBT()
        tag.put("X", DoubleNBT.valueOf(value.x))
        tag.put("Y", DoubleNBT.valueOf(value.y))
        return tag
    }
}

internal object Vec2iSerializer: NBTSerializer<Vec2i>() {
    override fun deserialize(tag: INBT, existing: Vec2i?): Vec2i {
        @Suppress("NAME_SHADOWING") val tag = tag.expectType<CompoundNBT>("tag")
        return Vec2i(
            tag.expect<NumberNBT>("X").asInt,
            tag.expect<NumberNBT>("Y").asInt
        )
    }

    override fun serialize(value: Vec2i): INBT {
        val tag = CompoundNBT()
        tag.put("X", IntNBT.valueOf(value.x))
        tag.put("Y", IntNBT.valueOf(value.y))
        return tag
    }
}

internal object Rect2dSerializer: NBTSerializer<Rect2d>() {
    override fun deserialize(tag: INBT, existing: Rect2d?): Rect2d {
        @Suppress("NAME_SHADOWING") val tag = tag.expectType<CompoundNBT>("tag")
        return Rect2d(
            tag.expect<NumberNBT>("X").asDouble,
            tag.expect<NumberNBT>("Y").asDouble,
            tag.expect<NumberNBT>("Width").asDouble,
            tag.expect<NumberNBT>("Height").asDouble
        )
    }

    override fun serialize(value: Rect2d): INBT {
        val tag = CompoundNBT()
        tag.put("X", DoubleNBT.valueOf(value.x))
        tag.put("Y", DoubleNBT.valueOf(value.y))
        tag.put("Width", DoubleNBT.valueOf(value.width))
        tag.put("Height", DoubleNBT.valueOf(value.height))
        return tag
    }
}

internal object Matrix3dSerializer: NBTSerializer<Matrix3d>() {
    override fun deserialize(tag: INBT, existing: Matrix3d?): Matrix3d {
        val list = tag.expectType<ListNBT>("tag")
        return Matrix3d(
            list[0].expectType<NumberNBT>("00").asDouble,
            list[1].expectType<NumberNBT>("01").asDouble,
            list[2].expectType<NumberNBT>("02").asDouble,
            list[3].expectType<NumberNBT>("10").asDouble,
            list[4].expectType<NumberNBT>("11").asDouble,
            list[5].expectType<NumberNBT>("12").asDouble,
            list[6].expectType<NumberNBT>("20").asDouble,
            list[7].expectType<NumberNBT>("21").asDouble,
            list[8].expectType<NumberNBT>("22").asDouble
        )
    }

    override fun serialize(value: Matrix3d): INBT {
        return ListNBT().also {
            it.add(DoubleNBT.valueOf(value.m00))
            it.add(DoubleNBT.valueOf(value.m01))
            it.add(DoubleNBT.valueOf(value.m02))
            it.add(DoubleNBT.valueOf(value.m10))
            it.add(DoubleNBT.valueOf(value.m11))
            it.add(DoubleNBT.valueOf(value.m12))
            it.add(DoubleNBT.valueOf(value.m20))
            it.add(DoubleNBT.valueOf(value.m21))
            it.add(DoubleNBT.valueOf(value.m22))
        }
    }
}

internal object MutableMatrix3dSerializer: NBTSerializer<MutableMatrix3d>() {
    override fun deserialize(tag: INBT, existing: MutableMatrix3d?): MutableMatrix3d {
        val list = tag.expectType<ListNBT>("tag")
        return (existing ?: MutableMatrix3d()).set(
            list[0].expectType<NumberNBT>("00").asDouble,
            list[1].expectType<NumberNBT>("01").asDouble,
            list[2].expectType<NumberNBT>("02").asDouble,
            list[3].expectType<NumberNBT>("10").asDouble,
            list[4].expectType<NumberNBT>("11").asDouble,
            list[5].expectType<NumberNBT>("12").asDouble,
            list[6].expectType<NumberNBT>("20").asDouble,
            list[7].expectType<NumberNBT>("21").asDouble,
            list[8].expectType<NumberNBT>("22").asDouble
        )
    }

    override fun serialize(value: MutableMatrix3d): INBT {
        return ListNBT().also {
            it.add(DoubleNBT.valueOf(value.m00))
            it.add(DoubleNBT.valueOf(value.m01))
            it.add(DoubleNBT.valueOf(value.m02))
            it.add(DoubleNBT.valueOf(value.m10))
            it.add(DoubleNBT.valueOf(value.m11))
            it.add(DoubleNBT.valueOf(value.m12))
            it.add(DoubleNBT.valueOf(value.m20))
            it.add(DoubleNBT.valueOf(value.m21))
            it.add(DoubleNBT.valueOf(value.m22))
        }
    }
}

internal object Matrix4dSerializer: NBTSerializer<Matrix4d>() {
    override fun deserialize(tag: INBT, existing: Matrix4d?): Matrix4d {
        val list = tag.expectType<ListNBT>("tag")
        return Matrix4d(
            list[0].expectType<NumberNBT>("00").asDouble,
            list[1].expectType<NumberNBT>("01").asDouble,
            list[2].expectType<NumberNBT>("02").asDouble,
            list[3].expectType<NumberNBT>("03").asDouble,
            list[4].expectType<NumberNBT>("10").asDouble,
            list[5].expectType<NumberNBT>("11").asDouble,
            list[6].expectType<NumberNBT>("12").asDouble,
            list[7].expectType<NumberNBT>("13").asDouble,
            list[8].expectType<NumberNBT>("20").asDouble,
            list[9].expectType<NumberNBT>("21").asDouble,
            list[10].expectType<NumberNBT>("22").asDouble,
            list[11].expectType<NumberNBT>("23").asDouble,
            list[12].expectType<NumberNBT>("30").asDouble,
            list[13].expectType<NumberNBT>("31").asDouble,
            list[14].expectType<NumberNBT>("32").asDouble,
            list[15].expectType<NumberNBT>("33").asDouble
        )
    }

    override fun serialize(value: Matrix4d): INBT {
        return ListNBT().also {
            it.add(DoubleNBT.valueOf(value.m00))
            it.add(DoubleNBT.valueOf(value.m01))
            it.add(DoubleNBT.valueOf(value.m02))
            it.add(DoubleNBT.valueOf(value.m03))
            it.add(DoubleNBT.valueOf(value.m10))
            it.add(DoubleNBT.valueOf(value.m11))
            it.add(DoubleNBT.valueOf(value.m12))
            it.add(DoubleNBT.valueOf(value.m13))
            it.add(DoubleNBT.valueOf(value.m20))
            it.add(DoubleNBT.valueOf(value.m21))
            it.add(DoubleNBT.valueOf(value.m22))
            it.add(DoubleNBT.valueOf(value.m23))
            it.add(DoubleNBT.valueOf(value.m30))
            it.add(DoubleNBT.valueOf(value.m31))
            it.add(DoubleNBT.valueOf(value.m32))
            it.add(DoubleNBT.valueOf(value.m33))
        }
    }
}

internal object MutableMatrix4dSerializer: NBTSerializer<MutableMatrix4d>() {
    override fun deserialize(tag: INBT, existing: MutableMatrix4d?): MutableMatrix4d {
        val list = tag.expectType<ListNBT>("tag")
        return (existing ?: MutableMatrix4d()).set(
            list[0].expectType<NumberNBT>("00").asDouble,
            list[1].expectType<NumberNBT>("01").asDouble,
            list[2].expectType<NumberNBT>("02").asDouble,
            list[3].expectType<NumberNBT>("03").asDouble,
            list[4].expectType<NumberNBT>("10").asDouble,
            list[5].expectType<NumberNBT>("11").asDouble,
            list[6].expectType<NumberNBT>("12").asDouble,
            list[7].expectType<NumberNBT>("13").asDouble,
            list[8].expectType<NumberNBT>("20").asDouble,
            list[9].expectType<NumberNBT>("21").asDouble,
            list[10].expectType<NumberNBT>("22").asDouble,
            list[11].expectType<NumberNBT>("23").asDouble,
            list[12].expectType<NumberNBT>("30").asDouble,
            list[13].expectType<NumberNBT>("31").asDouble,
            list[14].expectType<NumberNBT>("32").asDouble,
            list[15].expectType<NumberNBT>("33").asDouble
        )
    }

    override fun serialize(value: MutableMatrix4d): INBT {
        return ListNBT().also {
            it.add(DoubleNBT.valueOf(value.m00))
            it.add(DoubleNBT.valueOf(value.m01))
            it.add(DoubleNBT.valueOf(value.m02))
            it.add(DoubleNBT.valueOf(value.m03))
            it.add(DoubleNBT.valueOf(value.m10))
            it.add(DoubleNBT.valueOf(value.m11))
            it.add(DoubleNBT.valueOf(value.m12))
            it.add(DoubleNBT.valueOf(value.m13))
            it.add(DoubleNBT.valueOf(value.m20))
            it.add(DoubleNBT.valueOf(value.m21))
            it.add(DoubleNBT.valueOf(value.m22))
            it.add(DoubleNBT.valueOf(value.m23))
            it.add(DoubleNBT.valueOf(value.m30))
            it.add(DoubleNBT.valueOf(value.m31))
            it.add(DoubleNBT.valueOf(value.m32))
            it.add(DoubleNBT.valueOf(value.m33))
        }
    }
}

internal object QuaternionSerializer: NBTSerializer<Quaternion>() {
    override fun deserialize(tag: INBT, existing: Quaternion?): Quaternion {
        @Suppress("NAME_SHADOWING") val tag = tag.expectType<CompoundNBT>("tag")
        return Quaternion(
            tag.expect<NumberNBT>("X").asDouble,
            tag.expect<NumberNBT>("Y").asDouble,
            tag.expect<NumberNBT>("Z").asDouble,
            tag.expect<NumberNBT>("W").asDouble
        )
    }

    override fun serialize(value: Quaternion): INBT {
        val tag = CompoundNBT()
        tag.put("X", DoubleNBT.valueOf(value.x))
        tag.put("Y", DoubleNBT.valueOf(value.y))
        tag.put("Z", DoubleNBT.valueOf(value.z))
        tag.put("W", DoubleNBT.valueOf(value.w))
        return tag
    }
}
