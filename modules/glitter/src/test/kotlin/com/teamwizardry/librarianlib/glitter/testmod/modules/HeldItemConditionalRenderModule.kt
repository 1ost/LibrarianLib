package com.teamwizardry.librarianlib.glitter.testmod.modules

import com.mojang.blaze3d.matrix.MatrixStack
import com.teamwizardry.librarianlib.core.util.Client
import com.teamwizardry.librarianlib.glitter.ParticleRenderModule
import com.teamwizardry.librarianlib.glitter.ParticleUpdateModule
import net.minecraft.util.math.vector.Matrix4f
import net.minecraft.util.ResourceLocation

/**
 * Only runs the passed render module when the player is holding a specific item
 */
class HeldItemConditionalRenderModule(
    /**
     * What item ID the player must be holding in order for the particles to render
     */
    @JvmField val filter: ResourceLocation,
    /**
     * The render module to run when the player is holding the item
     */
    @JvmField val wrapped: ParticleRenderModule,
): ParticleRenderModule {

    @Suppress("LocalVariableName")
    override fun render(matrixStack: MatrixStack, projectionMatrix: Matrix4f, particles: List<DoubleArray>, prepModules: List<ParticleUpdateModule>) {
        val isHoldingItem = Client.player!!.let {
            it.mainHandItem.item.registryName == filter || it.offhandItem.item.registryName == filter
        }
        if(isHoldingItem)
            wrapped.render(matrixStack, projectionMatrix, particles, prepModules)
    }
}
