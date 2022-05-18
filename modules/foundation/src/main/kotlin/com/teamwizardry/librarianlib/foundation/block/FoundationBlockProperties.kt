package com.teamwizardry.librarianlib.foundation.block

import net.minecraft.block.*
import net.minecraft.block.AbstractBlock.IExtendedPositionPredicate
import net.minecraft.block.AbstractBlock.IPositionPredicate
import net.minecraft.block.material.Material
import net.minecraft.block.material.MaterialColor
import net.minecraft.block.material.PushReaction
import net.minecraft.entity.EntityType
import net.minecraft.util.Direction
import net.minecraft.util.math.BlockPos
import net.minecraft.world.IBlockReader
import net.minecraftforge.common.ToolType
import kotlin.math.max
import java.util.function.Function
import java.util.function.ToIntFunction

/**
 * Similar to [Block.Properties], but adds flammability and the ability to compose properties using [applyFrom].
 */
public class FoundationBlockProperties: BlockPropertiesBuilder<FoundationBlockProperties> {
    /**
     * Backing data for the [BlockPropertiesBuilder] builder methods. Just a reference to `this`
     */
    override val blockProperties: FoundationBlockProperties
        get() = this

    // Vanilla properties
    public var material: Material? = null
    public var mapColor: Function<BlockState, MaterialColor>? = null
    public var blocksMovement: Boolean? = null // default: true
    public var soundType: SoundType? = null // default: SoundType.STONE
    public var lightLevel: ToIntFunction<BlockState>? = null // default: 0
    public var hardnessAndResistance: Pair<Float, Float>? = null // default: 0f, 0f
    public var requiresTool: Boolean? = null // default: false
    public var ticksRandomly: Boolean? = null // default: false
    public var slipperiness: Float? = null // default: 0.6f
    public var speedFactor: Float? = null // default: 1.0f
    public var jumpFactor: Float? = null // default: 1.0f

    /** Sets loot table information  */
    public var noDrops: Boolean? = null // default: false
    public var isSolid: Boolean? = null // default: true
    public var isAir: Boolean? = null // default: false
    public var variableOpacity: Boolean? = null // default: false
    public var harvestLevel: Int? = null // default: -1
    public var harvestTool: ToolType? = null
    public var lootFrom: Block? = null

    public var allowsSpawn: IExtendedPositionPredicate<EntityType<*>>? = null
    public var isOpaque: IPositionPredicate? = null
    public var suffocates: IPositionPredicate? = null

    /** If it blocks vision on the client side.  */
    public var blocksVision: IPositionPredicate? = null
    public var needsPostProcessing: IPositionPredicate? = null
    public var emissiveRendering: IPositionPredicate? = null

    // Foundation properties
    public var flammability: Int? = null
    public var fireSpreadSpeed: Int? = null

    //region Builders

    /**
     * Creates a vanilla [Block.Properties] object based on this one
     */
    public val vanillaProperties: AbstractBlock.Properties
        get() {
            val properties = AbstractBlock.Properties.of(
                material ?: GENERIC_MATERIAL,
                mapColor ?: Function { MaterialColor.NONE }
            )
            if (blocksMovement == false)
                properties.noCollission()
            soundType?.also { properties.sound(it) }
            lightLevel?.also { properties.lightLevel(it) }
            hardnessAndResistance?.also { properties.strength(it.first, it.second) }
            if (ticksRandomly == true)
                properties.randomTicks()
            slipperiness?.also { properties.friction(it) }
            speedFactor?.also { properties.speedFactor(it) }
            jumpFactor?.also { properties.jumpFactor(it) }
            if (noDrops == true)
                properties.noDrops()
            lootFrom?.also { properties.dropsLike(it) }
            if (isSolid == false)
                properties.noOcclusion()
            if (isAir == true)
                properties.air()
            if (variableOpacity == true)
                properties.dynamicShape()
            harvestLevel?.also { properties.harvestLevel(it) }
            harvestTool?.also { properties.harvestTool(it) }
            requiresTool?.also { properties.requiresCorrectToolForDrops() }
            allowsSpawn?.also { properties.isValidSpawn(it) }
            isOpaque?.also { properties.isRedstoneConductor(it) }
            suffocates?.also { properties.isSuffocating(it) }
            blocksVision?.also { properties.isViewBlocking(it) }
            needsPostProcessing?.also { properties.hasPostProcess(it) }
            emissiveRendering?.also { properties.emissiveRendering(it) }

            return properties
        }

    public fun getFlammabilityImpl(state: BlockState?, world: IBlockReader?, pos: BlockPos?, face: Direction?): Int {
        return flammability ?: 0
    }

    public fun getFireSpreadSpeedImpl(state: BlockState?, world: IBlockReader?, pos: BlockPos?, face: Direction?): Int {
        return fireSpreadSpeed ?: 0
    }

    /**
     * True if this block is sourcing its properties from another block's table
     */
    public val usesExternalLoot: Boolean
        get() = lootFrom != null

    private inline fun build(block: () -> Unit): FoundationBlockProperties {
        block()
        return this
    }

    private companion object {
        private val GENERIC_MATERIAL = Material(
            MaterialColor.NONE, // materialMapColor
            false, // liquid
            true, // solid
            true, // doesBlockMovement
            true, // opaque
            false, // canBurn
            false, // replaceable
            PushReaction.NORMAL // mobilityFlag
        )
    }
}