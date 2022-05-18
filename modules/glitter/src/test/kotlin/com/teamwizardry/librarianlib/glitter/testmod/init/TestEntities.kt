package com.teamwizardry.librarianlib.glitter.testmod.init

import com.teamwizardry.librarianlib.glitter.testmod.entity.ParticleSpawnerEntity
import com.teamwizardry.librarianlib.glitter.testmod.modid
import net.minecraft.entity.EntityClassification
import net.minecraft.entity.AreaEffectCloudEntity
import net.minecraft.entity.EntityType
import net.minecraft.util.ResourceLocation
import net.minecraftforge.registries.ForgeRegistries

object TestEntities {
    val spawner = EntityType.Builder.of<ParticleSpawnerEntity>({ _, world ->
        ParticleSpawnerEntity(world)
    }, EntityClassification.MISC)
        .setCustomClientFactory { _, world ->
            ParticleSpawnerEntity(world)
        }
        .sized(0.5f, 0.5f).build("particle_spawner")
        .setRegistryName(modid, "particle_spawner")

    fun register() {
        ForgeRegistries.ENTITIES.register(spawner)
    }
}