package com.teamwizardry.librarianlib.client.fx.particle.functions

import com.teamwizardry.librarianlib.client.fx.particle.ParticleBase
import com.teamwizardry.librarianlib.client.fx.particle.ParticleRenderLayer
import net.minecraft.client.renderer.VertexBuffer
import net.minecraft.entity.Entity
import net.minecraft.util.math.Vec3d
import java.awt.Color

/**
 * Created by TheCodeWarrior
 */
abstract class RenderFunction(private val theLayer: ParticleRenderLayer) {

    open fun getLayer(): ParticleRenderLayer {
        return theLayer
    }

    abstract fun render(i: Float, particle: ParticleBase, color: Color,
                        worldRendererIn: VertexBuffer, entityIn: Entity?, partialTicks: Float, rotationX: Float, rotationZ: Float, rotationYZ: Float, rotationXY: Float, rotationXZ: Float,
                        scale: Float, pos: Vec3d, skyLight: Int, blockLight: Int)
}