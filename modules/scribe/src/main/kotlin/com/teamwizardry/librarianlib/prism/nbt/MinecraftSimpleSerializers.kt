package com.teamwizardry.librarianlib.prism.nbt

import com.mojang.authlib.GameProfile
import com.teamwizardry.librarianlib.core.util.kotlin.inconceivable
import com.teamwizardry.librarianlib.core.util.block
import dev.thecodewarrior.mirror.Mirror
import dev.thecodewarrior.mirror.type.ClassMirror
import dev.thecodewarrior.mirror.type.TypeMirror
import dev.thecodewarrior.prism.DeserializationException
import net.minecraft.block.BlockState
import net.minecraft.enchantment.Enchantment
import net.minecraft.enchantment.EnchantmentData
import net.minecraft.item.ItemStack
import net.minecraft.nbt.CompoundNBT
import net.minecraft.nbt.DoubleNBT
import net.minecraft.nbt.FloatNBT
import net.minecraft.nbt.INBT
import net.minecraft.nbt.IntNBT
import net.minecraft.nbt.NBTUtil
import net.minecraft.nbt.NumberNBT
import net.minecraft.nbt.StringNBT
import net.minecraft.potion.EffectInstance
import net.minecraft.util.ResourceLocation
import net.minecraft.util.Tuple
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.ChunkPos
import net.minecraft.util.math.ColumnPos
import net.minecraft.util.math.GlobalPos
import net.minecraft.util.math.MutableBoundingBox
import net.minecraft.util.math.Rotations
import net.minecraft.util.math.SectionPos
import net.minecraft.util.math.vector.Vector2f
import net.minecraft.util.math.vector.Vector3d
import net.minecraft.util.registry.Registry
import net.minecraft.util.text.ITextComponent
import net.minecraftforge.fluids.FluidStack
import net.minecraftforge.fluids.capability.templates.FluidTank
import net.minecraftforge.fml.common.registry.GameRegistry

internal object ResourceLocationSerializer: NBTSerializer<ResourceLocation>() {
    override fun deserialize(tag: INBT, existing: ResourceLocation?): ResourceLocation {
        return ResourceLocation(tag.expectType<StringNBT>("tag").asString)
    }

    override fun serialize(value: ResourceLocation): INBT {
        return StringNBT.valueOf(value.toString())
    }
}

//region Math stuff

internal object Vector3dSerializer: NBTSerializer<Vector3d>() {
    override fun deserialize(tag: INBT, existing: Vector3d?): Vector3d {
        @Suppress("NAME_SHADOWING") val tag = tag.expectType<CompoundNBT>("tag")
        return Vector3d(
            tag.expect<NumberNBT>("X").asDouble,
            tag.expect<NumberNBT>("Y").asDouble,
            tag.expect<NumberNBT>("Z").asDouble
        )
    }

    override fun serialize(value: Vector3d): INBT {
        val tag = CompoundNBT()
        tag.put("X", DoubleNBT.valueOf(value.x))
        tag.put("Y", DoubleNBT.valueOf(value.y))
        tag.put("Z", DoubleNBT.valueOf(value.z))
        return tag
    }
}

internal object Vector2fSerializer: NBTSerializer<Vector2f>() {
    override fun deserialize(tag: INBT, existing: Vector2f?): Vector2f {
        @Suppress("NAME_SHADOWING") val tag = tag.expectType<CompoundNBT>("tag")
        return Vector2f(
            tag.expect<NumberNBT>("X").asFloat,
            tag.expect<NumberNBT>("Y").asFloat
        )
    }

    override fun serialize(value: Vector2f): INBT {
        val tag = CompoundNBT()
        tag.put("X", FloatNBT.valueOf(value.x))
        tag.put("Y", FloatNBT.valueOf(value.y))
        return tag
    }
}

internal object BlockPosSerializer: NBTSerializer<BlockPos>() {
    override fun deserialize(tag: INBT, existing: BlockPos?): BlockPos {
        return NBTUtil.readBlockPos(tag.expectType("tag"))
    }

    override fun serialize(value: BlockPos): INBT {
        return NBTUtil.writeBlockPos(value)
    }
}


internal object ChunkPosSerializer: NBTSerializer<ChunkPos>() {
    override fun deserialize(tag: INBT, existing: ChunkPos?): ChunkPos {
        @Suppress("NAME_SHADOWING") val tag = tag.expectType<CompoundNBT>("tag")
        return ChunkPos(
            tag.expect<NumberNBT>("X").asInt,
            tag.expect<NumberNBT>("Z").asInt
        )
    }

    override fun serialize(value: ChunkPos): INBT {
        val tag = CompoundNBT()
        tag.put("X", IntNBT.valueOf(value.x))
        tag.put("Z", IntNBT.valueOf(value.z))
        return tag
    }
}

internal object ColumnPosSerializer: NBTSerializer<ColumnPos>() {
    override fun deserialize(tag: INBT, existing: ColumnPos?): ColumnPos {
        @Suppress("NAME_SHADOWING") val tag = tag.expectType<CompoundNBT>("tag")
        return ColumnPos(
            tag.expect<NumberNBT>("X").asInt,
            tag.expect<NumberNBT>("Z").asInt
        )
    }

    override fun serialize(value: ColumnPos): INBT {
        val tag = CompoundNBT()
        tag.put("X", IntNBT.valueOf(value.x))
        tag.put("Z", IntNBT.valueOf(value.z))
        return tag
    }
}

internal object SectionPosSerializer: NBTSerializer<SectionPos>() {
    override fun deserialize(tag: INBT, existing: SectionPos?): SectionPos {
        @Suppress("NAME_SHADOWING") val tag = tag.expectType<CompoundNBT>("tag")
        return SectionPos.of(
            ChunkPos(
                tag.expect<NumberNBT>("X").asInt,
                tag.expect<NumberNBT>("Z").asInt
            ),
            tag.expect<NumberNBT>("Y").asInt
        )
    }

    override fun serialize(value: SectionPos): INBT {
        val tag = CompoundNBT()
        tag.put("X", IntNBT.valueOf(value.x))
        tag.put("Y", IntNBT.valueOf(value.y))
        tag.put("Z", IntNBT.valueOf(value.z))
        return tag
    }
}

// Dimension types seem to be "dynamic registry entries" now, which means you need to get them from a World instance.
//internal object GlobalPosSerializer: NBTSerializer<GlobalPos>() {
//    override fun deserialize(tag: INBT, existing: GlobalPos?): GlobalPos {
//        @Suppress("NAME_SHADOWING") val tag = tag.expectType<CompoundNBT>("tag")
//        val dimensionName = ResourceLocation(tag.expect<StringNBT>("Dimension").string)
//        return GlobalPos.of(
//            DimensionType.byName(dimensionName)!!, // `!!` because the dimension type registry has a default value
//            block(
//                tag.expect<NumberNBT>("X").int,
//                tag.expect<NumberNBT>("Y").int,
//                tag.expect<NumberNBT>("Z").int
//            )
//        )
//    }
//
//    override fun serialize(value: GlobalPos): INBT {
//        val tag = CompoundNBT()
//        tag.put("Dimension", StringNBT.valueOf(value.dimension.registryName.toString()))
//        tag.put("X", IntNBT.valueOf(value.pos.x))
//        tag.put("Y", IntNBT.valueOf(value.pos.y))
//        tag.put("Z", IntNBT.valueOf(value.pos.z))
//        return tag
//    }
//}

internal object RotationsSerializer: NBTSerializer<Rotations>() {
    override fun deserialize(tag: INBT, existing: Rotations?): Rotations {
        @Suppress("NAME_SHADOWING") val tag = tag.expectType<CompoundNBT>("tag")
        return Rotations(
            tag.expect<NumberNBT>("X").asFloat,
            tag.expect<NumberNBT>("Y").asFloat,
            tag.expect<NumberNBT>("Z").asFloat
        )
    }

    override fun serialize(value: Rotations): INBT {
        val tag = CompoundNBT()
        tag.put("X", FloatNBT.valueOf(value.x))
        tag.put("Y", FloatNBT.valueOf(value.y))
        tag.put("Z", FloatNBT.valueOf(value.z))
        return tag
    }
}

internal object AxisAlignedBBSerializer: NBTSerializer<AxisAlignedBB>() {
    override fun deserialize(tag: INBT, existing: AxisAlignedBB?): AxisAlignedBB {
        @Suppress("NAME_SHADOWING") val tag = tag.expectType<CompoundNBT>("tag")
        return AxisAlignedBB(
            tag.expect<NumberNBT>("MinX").asDouble,
            tag.expect<NumberNBT>("MinY").asDouble,
            tag.expect<NumberNBT>("MinZ").asDouble,
            tag.expect<NumberNBT>("MaxX").asDouble,
            tag.expect<NumberNBT>("MaxY").asDouble,
            tag.expect<NumberNBT>("MaxZ").asDouble
        )
    }

    override fun serialize(value: AxisAlignedBB): INBT {
        val tag = CompoundNBT()
        tag.put("MinX", DoubleNBT.valueOf(value.minX))
        tag.put("MinY", DoubleNBT.valueOf(value.minY))
        tag.put("MinZ", DoubleNBT.valueOf(value.minZ))
        tag.put("MaxX", DoubleNBT.valueOf(value.maxX))
        tag.put("MaxY", DoubleNBT.valueOf(value.maxY))
        tag.put("MaxZ", DoubleNBT.valueOf(value.maxZ))
        return tag
    }
}

internal object MutableBoundingBoxSerializer: NBTSerializer<MutableBoundingBox>() {
    override fun deserialize(tag: INBT, existing: MutableBoundingBox?): MutableBoundingBox {
        @Suppress("NAME_SHADOWING") val tag = tag.expectType<CompoundNBT>("tag")
        return MutableBoundingBox(
            tag.expect<NumberNBT>("MinX").asInt,
            tag.expect<NumberNBT>("MinY").asInt,
            tag.expect<NumberNBT>("MinZ").asInt,
            tag.expect<NumberNBT>("MaxX").asInt,
            tag.expect<NumberNBT>("MaxY").asInt,
            tag.expect<NumberNBT>("MaxZ").asInt
        )
    }

    override fun serialize(value: MutableBoundingBox): INBT {
        val tag = CompoundNBT()
        tag.put("MinX", IntNBT.valueOf(value.x0))
        tag.put("MinY", IntNBT.valueOf(value.y0))
        tag.put("MinZ", IntNBT.valueOf(value.z0))
        tag.put("MaxX", IntNBT.valueOf(value.x1))
        tag.put("MaxY", IntNBT.valueOf(value.y1))
        tag.put("MaxZ", IntNBT.valueOf(value.z1))
        return tag
    }
}

//endregion

internal class TupleSerializerFactory(prism: NBTPrism): NBTSerializerFactory(prism, Mirror.reflect<Tuple<*, *>>()) {
    override fun create(mirror: TypeMirror): NBTSerializer<*> {
        return TupleSerializer(prism, mirror as ClassMirror)
    }

    class TupleSerializer(prism: NBTPrism, type: ClassMirror): NBTSerializer<Tuple<Any?, Any?>>(type) {
        private val firstSerializer by prism[type.typeParameters[0]]
        private val secondSerializer by prism[type.typeParameters[1]]

        override fun deserialize(tag: INBT, existing: Tuple<Any?, Any?>?): Tuple<Any?, Any?> {
            @Suppress("NAME_SHADOWING") val tag = tag.expectType<CompoundNBT>("tag")
            @Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS") // stupid @MethodsReturnNonnullByDefault
            return Tuple(
                (if (tag.contains("A")) firstSerializer.read(tag.expect("A"), existing?.a) else null) as Any,
                (if (tag.contains("B")) secondSerializer.read(tag.expect("B"), existing?.b) else null) as Any
            )
        }

        override fun serialize(value: Tuple<Any?, Any?>): INBT {
            val tag = CompoundNBT()
            @Suppress("UNNECESSARY_SAFE_CALL") // stupid @MethodsReturnNonnullByDefault
            value.a?.also { tag.put("A", firstSerializer.write(it)) }
            @Suppress("UNNECESSARY_SAFE_CALL") // stupid @MethodsReturnNonnullByDefault
            value.b?.also { tag.put("B", secondSerializer.write(it)) }
            return tag
        }
    }
}

internal class INBTPassthroughSerializerFactory(prism: NBTPrism): NBTSerializerFactory(prism, Mirror.reflect<INBT>()) {
    override fun create(mirror: TypeMirror): NBTSerializer<*> {
        return INBTPassthroughSerializer(mirror as ClassMirror)
    }

    class INBTPassthroughSerializer(type: ClassMirror): NBTSerializer<INBT>(type) {
        @Suppress("UNCHECKED_CAST")
        private val nbtClass = type.erasure as Class<INBT>

        override fun deserialize(tag: INBT, existing: INBT?): INBT {
            return expectType(tag, nbtClass, "tag").copy()
        }

        override fun serialize(value: INBT): INBT {
            return value.copy()
        }
    }
}

internal class ITextComponentSerializerFactory(prism: NBTPrism): NBTSerializerFactory(prism, Mirror.reflect<ITextComponent>()) {
    override fun create(mirror: TypeMirror): NBTSerializer<*> {
        return ITextComponentSerializer(mirror as ClassMirror)
    }

    class ITextComponentSerializer(type: ClassMirror): NBTSerializer<ITextComponent>(type) {
        private val componentClass = type.erasure

        override fun deserialize(tag: INBT, existing: ITextComponent?): ITextComponent {
            val component = ITextComponent.Serializer.fromJson(tag.expectType<StringNBT>("tag").asString)
                ?: inconceivable("ITextComponent.Serializer.fromJson doesn't seem to ever return null")
            if(!componentClass.isAssignableFrom(component.javaClass))
                throw DeserializationException("Wrong ITextComponent type. Expected ${componentClass.simpleName}, " +
                    "found ${component.javaClass.simpleName}.")
            return component
        }

        override fun serialize(value: ITextComponent): INBT {
            return StringNBT.valueOf(ITextComponent.Serializer.toJson(value))
        }
    }
}

internal object GameProfileSerializer: NBTSerializer<GameProfile>() {
    override fun deserialize(tag: INBT, existing: GameProfile?): GameProfile {
        return NBTUtil.readGameProfile(tag.expectType("tag"))
            ?: throw DeserializationException("Reading GameProfile") // it only returns null if an error occurs
    }

    override fun serialize(value: GameProfile): INBT {
        val tag = CompoundNBT()
        NBTUtil.writeGameProfile(tag, value)
        return tag
    }
}

internal object BlockStateSerializer: NBTSerializer<BlockState>() {
    override fun deserialize(tag: INBT, existing: BlockState?): BlockState {
        return NBTUtil.readBlockState(tag.expectType("tag"))
    }

    override fun serialize(value: BlockState): INBT {
        return NBTUtil.writeBlockState(value)
    }
}

internal object ItemStackSerializer: NBTSerializer<ItemStack>() {
    override fun deserialize(tag: INBT, existing: ItemStack?): ItemStack {
        return ItemStack.of(tag.expectType("tag"))
    }

    override fun serialize(value: ItemStack): INBT {
        return value.save(CompoundNBT())
    }
}

internal object FluidStackSerializer: NBTSerializer<FluidStack>() {
    override fun deserialize(tag: INBT, existing: FluidStack?): FluidStack {
        return FluidStack.loadFluidStackFromNBT(tag.expectType("tag"))
    }

    override fun serialize(value: FluidStack): INBT {
        return value.writeToNBT(CompoundNBT())
    }
}

internal object EffectInstanceSerializer: NBTSerializer<EffectInstance>() {
    override fun deserialize(tag: INBT, existing: EffectInstance?): EffectInstance {
        return EffectInstance.load(tag.expectType("tag"))
    }

    override fun serialize(value: EffectInstance): INBT {
        return value.save(CompoundNBT())
    }
}

internal object EnchantmentDataSerializer: NBTSerializer<EnchantmentData>() {
    private val registry by lazy {
        GameRegistry.findRegistry(Enchantment::class.java)
    }

    override fun deserialize(tag: INBT, existing: EnchantmentData?): EnchantmentData {
        @Suppress("NAME_SHADOWING") val tag = tag.expectType<CompoundNBT>("tag")
        val dimensionName = ResourceLocation(tag.expect<StringNBT>("Enchantment").asString)
        val enchantment = registry.getValue(dimensionName)
            ?: throw DeserializationException("Unknown enchantment type $dimensionName")
        return EnchantmentData(
            enchantment,
            tag.expect<NumberNBT>("Level").asInt
        )
    }

    override fun serialize(value: EnchantmentData): INBT {
        val tag = CompoundNBT()
        tag.put("Enchantment", StringNBT.valueOf(value.enchantment.registryName.toString()))
        tag.put("Level", IntNBT.valueOf(value.level))
        return tag
    }
}

internal object FluidTankSerializer: NBTSerializer<FluidTank>() {
    override fun deserialize(tag: INBT, existing: FluidTank?): FluidTank {
        if(existing == null)
            throw DeserializationException("FluidTank requires an existing value to deserialize")
        @Suppress("NAME_SHADOWING") val tag = tag.expectType<CompoundNBT>("tag")
        existing.readFromNBT(tag)
        return existing
    }

    override fun serialize(value: FluidTank): INBT {
        val tag = CompoundNBT()
        value.writeToNBT(tag)
        return tag
    }
}
