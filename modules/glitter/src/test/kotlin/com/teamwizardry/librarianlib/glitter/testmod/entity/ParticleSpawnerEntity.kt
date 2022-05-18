package com.teamwizardry.librarianlib.glitter.testmod.entity

import com.teamwizardry.librarianlib.core.util.sided.SidedRunnable
import com.teamwizardry.librarianlib.glitter.testmod.init.TestEntities
import com.teamwizardry.librarianlib.glitter.testmod.systems.ParticleSystems
import net.minecraft.entity.Entity
import net.minecraft.nbt.CompoundNBT
import net.minecraft.network.IPacket
import net.minecraft.world.World
import net.minecraft.network.datasync.DataSerializers
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.network.datasync.EntityDataManager
import net.minecraftforge.fml.network.NetworkHooks

class ParticleSpawnerEntity(world: World): Entity(TestEntities.spawner, world) {
    var system: String
        get() = this.entityData[SYSTEM]
        set(value) {
            this.entityData[SYSTEM] = value
        }

    init {
        canUpdate(true)
    }

    override fun getAddEntityPacket(): IPacket<*> {
        return NetworkHooks.getEntitySpawningPacket(this)
    }

    override fun readAdditionalSaveData(compound: CompoundNBT) {
        system = compound.getString("System")
    }

    override fun addAdditionalSaveData(compound: CompoundNBT) {
        compound.putString("System", system)
    }

    override fun defineSynchedData() {
        entityData.define(SYSTEM, "")
    }

    override fun canBeCollidedWith(): Boolean {
        return true
    }

    override fun tick() {
        super.tick()
        if(level.isClientSide) {
            SidedRunnable.client {
                ParticleSystems.spawn(system, this)
            }
        }
    }

    override fun skipAttackInteraction(entity: Entity): Boolean {
        if(entity is PlayerEntity) {
            this.remove()
            return true
        }
        return false
    }

    companion object {
        val SYSTEM = EntityDataManager.defineId(ParticleSpawnerEntity::class.java, DataSerializers.STRING)
    }

}