package com.teamwizardry.librarianlib.foundation.testmod.customtypes

import com.teamwizardry.librarianlib.foundation.testmod.LibrarianLibFoundationTestMod
import com.teamwizardry.librarianlib.foundation.testmod.ModTiles
import com.teamwizardry.librarianlib.foundation.tileentity.BaseTileEntity
import com.teamwizardry.librarianlib.prism.Save
import com.teamwizardry.librarianlib.prism.Sync
import net.minecraft.entity.Entity
import net.minecraft.particles.ParticleTypes
import net.minecraft.world.server.ServerWorld
import net.minecraftforge.common.util.Constants

class TestTileEntity: BaseTileEntity(ModTiles.testTile) {
    @Save
    var totalFallDistance: Float = 0f
    @Sync
    var lastFallDistance: Float = 0f

    init {
        logger.info("Test tile created")
    }

    fun onFallenUpon(entity: Entity, fallDistance: Float) {
        val world = level ?: return

        totalFallDistance += fallDistance
        lastFallDistance = fallDistance
        setChanged()
        notifyStateChange()

        (world as? ServerWorld)?.sendParticles(
            ParticleTypes.POOF,
            entity.x, entity.y, entity.z,
            5,
            0.1, 0.0, 0.1,
            0.0
        )
    }

    companion object {
        private val logger = LibrarianLibFoundationTestMod.makeLogger<TestTileEntity>()
    }
}