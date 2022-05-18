package com.teamwizardry.librarianlib.glitter.testmod.systems

import com.teamwizardry.librarianlib.core.util.loc
import com.teamwizardry.librarianlib.glitter.ParticleSystem
import com.teamwizardry.librarianlib.glitter.bindings.ConstantBinding
import com.teamwizardry.librarianlib.glitter.modules.BasicPhysicsUpdateModule
import com.teamwizardry.librarianlib.glitter.modules.GlLineBeamRenderModule
import com.teamwizardry.librarianlib.glitter.modules.SpriteRenderModule
import com.teamwizardry.librarianlib.glitter.modules.SpriteRenderOptions
import net.minecraft.entity.Entity
import net.minecraft.util.ResourceLocation

object WorldLitSystem : TestSystem("world_lit") {
    override fun configure() {
        val position = bind(3)
        val previousPosition = bind(3)
        val velocity = bind(3)

        updateModules.add(
            BasicPhysicsUpdateModule(
                position = position,
                previousPosition = previousPosition,
                velocity = velocity,
                enableCollision = true,
                gravity = ConstantBinding(0.02),
                bounciness = ConstantBinding(0.8),
                friction = ConstantBinding(0.02),
                damping = ConstantBinding(0.01)
            )
        )

        renderModules.add(
            SpriteRenderModule.build(
                SpriteRenderOptions.build(loc("minecraft", "textures/item/snowball.png"))
                    .worldLight(true)
                    .build(),
                position,
            )
                .previousPosition(previousPosition)
                .size(0.25)
                .build()
        )
    }

    override fun spawn(player: Entity) {
        val eyePos = player.getEyePosition(0f)
        val look = player.lookAngle

        val spawnDistance = 2
        val spawnVelocity = 0.2

        this.addParticle(
            200,
            // position
            eyePos.x + look.x * spawnDistance,
            eyePos.y + look.y * spawnDistance,
            eyePos.z + look.z * spawnDistance,
            // previous position
            eyePos.x + look.x * spawnDistance,
            eyePos.y + look.y * spawnDistance,
            eyePos.z + look.z * spawnDistance,
            // velocity
            look.x * spawnVelocity,
            look.y * spawnVelocity,
            look.z * spawnVelocity,
        )
    }
}