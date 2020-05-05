@file:JvmName("NBTPrism")
package com.teamwizardry.librarianlib.prism

import com.teamwizardry.librarianlib.prism.nbt.*
import dev.thecodewarrior.prism.Prism

@get:JvmSynthetic
inline val NBTPrism: NBTPrism get() = instance

val instance: NBTPrism = Prism<NBTSerializer<*>>().also { prism ->
    prism.register(
        // java types
        ArraySerializerFactory(prism),
        ListSerializerFactory(prism),
        ObjectSerializerFactory(prism),

        // kotlin types
        PairSerializerFactory(prism),
        TripleSerializerFactory(prism),

        // minecraft types
        IForgeRegistryEntrySerializerFactory(prism),
        INBTSerializableSerializerFactory(prism),
        INBTPassthroughSerializerFactory(prism),
        TupleSerializerFactory(prism)
    )

    prism.register(
        // primitives
        PrimitiveLongSerializer,
        PrimitiveIntSerializer,
        PrimitiveShortSerializer,
        PrimitiveByteSerializer,
        PrimitiveCharSerializer,
        PrimitiveDoubleSerializer,
        PrimitiveFloatSerializer,
        PrimitiveBooleanSerializer,

        // boxed
        LongSerializer,
        IntSerializer,
        ShortSerializer,
        ByteSerializer,
        CharSerializer,
        DoubleSerializer,
        FloatSerializer,
        BooleanSerializer,
        NumberSerializer,

        // primitive arrays
        PrimitiveLongArraySerializer,
        PrimitiveIntArraySerializer,
        PrimitiveShortArraySerializer,
        PrimitiveByteArraySerializer,
        PrimitiveCharArraySerializer,
        PrimitiveDoubleArraySerializer,
        PrimitiveFloatArraySerializer,
        PrimitiveBooleanArraySerializer,

        // java types
        BigIntegerSerializer,
        BigDecimalSerializer,
        StringSerializer,
        BitSetSerializer,
        UUIDSerializer,

        // minecraft types
        BlockPosSerializer,
        Vec3dSerializer,
        Vec2fSerializer,
        ChunkPosSerializer,
        ColumnPosSerializer,
        SectionPosSerializer,
        GlobalPosSerializer,
        RotationsSerializer,
        AxisAlignedBBSerializer,
        MutableBoundingBoxSerializer,
        ResourceLocationSerializer,
        BlockStateSerializer,
        GameProfileSerializer,
        ITextComponentSerializer,
        ItemStackSerializer,
        FluidStackSerializer,
        EffectInstanceSerializer,
        EnchantmentDataSerializer,

        // liblib types
        Vec2dSerializer,
        Vec2iSerializer,
        Ray2dSerializer,
        Rect2dSerializer,
        Matrix3dSerializer,
        MutableMatrix3dSerializer,
        Matrix4dSerializer,
        MutableMatrix4dSerializer,
        QuaternionSerializer
    )
}