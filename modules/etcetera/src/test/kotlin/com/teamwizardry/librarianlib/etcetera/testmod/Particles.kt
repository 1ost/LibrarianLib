package com.teamwizardry.librarianlib.etcetera.testmod

import net.minecraft.client.particle.IAnimatedSprite
import net.minecraft.client.particle.IParticleFactory
import net.minecraft.client.particle.IParticleRenderType
import net.minecraft.client.particle.Particle
import net.minecraft.client.particle.SpriteTexturedParticle
import net.minecraft.client.world.ClientWorld
import net.minecraft.particles.BasicParticleType
import net.minecraft.world.World
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.api.distmarker.OnlyIn

object Particles {
    val TARGET_RED: BasicParticleType = BasicParticleType(true).also {
        it.setRegistryName("ll-etcetera-test:target_red")
    }
    val TARGET_BLUE: BasicParticleType = BasicParticleType(true).also {
        it.setRegistryName("ll-etcetera-test:target_blue")
    }
}

@OnlyIn(Dist.CLIENT)
class HitParticle private constructor(world: ClientWorld, x: Double, y: Double, z: Double): SpriteTexturedParticle(world, x, y, z, 0.0, 0.0, 0.0) {
    init {
        age = 1
        hasPhysics = false
    }

    override fun getRenderType(): IParticleRenderType {
        return IParticleRenderType.PARTICLE_SHEET_OPAQUE
    }

    override fun getQuadSize(p_217561_1_: Float): Float {
        return 0.5f
    }

    override fun tick() {
        xo = x
        yo = y
        zo = z
        if (age++ >= age) {
            remove()
        }
    }

    @OnlyIn(Dist.CLIENT)
    class Factory(private val spriteSet: IAnimatedSprite): IParticleFactory<BasicParticleType> {
        override fun createParticle(
            typeIn: BasicParticleType,
            worldIn: ClientWorld,
            x: Double,
            y: Double,
            z: Double,
            xSpeed: Double,
            ySpeed: Double,
            zSpeed: Double
        ): Particle {
            val hitParticle = HitParticle(worldIn, x, y, z)
            hitParticle.pickSprite(spriteSet)
            return hitParticle
        }
    }

}