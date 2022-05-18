package com.teamwizardry.librarianlib.testcore.objects

import com.teamwizardry.librarianlib.core.util.Client
import com.teamwizardry.librarianlib.core.util.kotlin.threadLocal
import com.teamwizardry.librarianlib.core.util.sided.clientOnly
import net.minecraft.entity.Entity
import net.minecraft.entity.EntitySize
import net.minecraft.entity.Pose
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.nbt.CompoundNBT
import net.minecraft.network.IPacket
import net.minecraft.util.ActionResultType
import net.minecraft.util.DamageSource
import net.minecraft.util.Hand
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.RayTraceResult
import net.minecraft.util.math.vector.Vector3d
import net.minecraft.world.World
import net.minecraftforge.fml.network.NetworkHooks

public open class TestEntity(public val config: TestEntityConfig, world: World): Entity(config.also { configHolder = it }.type, world) {

    init {
        canUpdate(true)
    }

    override fun canBeCollidedWith(): Boolean {
        return true
    }

    override fun skipAttackInteraction(entity: Entity): Boolean {
        val context = TestEntityConfig.HitContext(this, entity, entity is PlayerEntity)

        config.hit.run(this.level.isClientSide, context)
        if (context.kill) {
            this.remove()
            return true
        }
        return false
    }

    override fun getPickedResult(target: RayTraceResult?): ItemStack {
        return ItemStack(config.spawnerItem)
    }

    override fun isGlowing(): Boolean {
        var heldGlow = false
        clientOnly {
            heldGlow = Client.minecraft.player?.getItemInHand(Hand.MAIN_HAND)?.item == config.spawnerItem ||
                    Client.minecraft.player?.getItemInHand(Hand.OFF_HAND)?.item == config.spawnerItem
        }
        return config.enableGlow || heldGlow || super.isGlowing()
    }

    override fun tick() {
        super.tick()
        config.tick.run(this.level.isClientSide, TestEntityConfig.TickContext(this))
    }

    override fun hurt(source: DamageSource, amount: Float): Boolean {
        config.attack.run(this.level.isClientSide, TestEntityConfig.AttackContext(this, source, amount))
        return false
    }

    override fun onRemovedFromWorld() {
        super.onRemovedFromWorld()
    }

    override fun interactAt(player: PlayerEntity, vec: Vector3d, hand: Hand): ActionResultType {
        config.rightClick.run(this.level.isClientSide, TestEntityConfig.RightClickContext(this, player, hand, vec))
        if (config.rightClick.exists)
            return ActionResultType.SUCCESS
        return super.interactAt(player, vec, hand)
    }

    // miscellaneous boilerplate =======================================================================================

    override fun getAddEntityPacket(): IPacket<*> {
        return NetworkHooks.getEntitySpawningPacket(this)
    }

    override fun readAdditionalSaveData(compound: CompoundNBT) {
    }

    override fun addAdditionalSaveData(compound: CompoundNBT) {
    }

    override fun defineSynchedData() {
        configHolder!!.entityProperties.forEach {
            @Suppress("UNCHECKED_CAST")
            it as TestEntityConfig.Property<Any>

            entityData.define(it.parameter, it.defaultValue)
        }
    }

    private inline fun <T> loadProperties(block: () -> T): T {
        config.entityProperties.forEach {
            it.entity = this
        }
        val value = block()
        config.entityProperties.forEach {
            it.entity = null
        }
        return value
    }

    override fun getEyeHeight(p_213316_1_: Pose, p_213316_2_: EntitySize): Float {
        return 0f
    }

    public val relativeBoundingBox: AxisAlignedBB = run {
        val size = 1.0 //idk
        val width = 1.0
        val height = 1.0
        AxisAlignedBB(
            -width / 2, -height / 2, -width / 2,
            +width / 2, +height / 2, +width / 2
        )
    }

    override fun getBoundingBox(): AxisAlignedBB {
        return relativeBoundingBox.move(this.x, this.y, this.z)
    }

    private companion object {
        // needed because registerData is called before we can set the config property
        private var configHolder: TestEntityConfig? by threadLocal()
    }
}
