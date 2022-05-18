package com.teamwizardry.librarianlib.glitter.testmod.item

import com.teamwizardry.librarianlib.core.util.sided.SidedRunnable
import com.teamwizardry.librarianlib.glitter.testmod.entity.ParticleSpawnerEntity
import com.teamwizardry.librarianlib.glitter.testmod.init.TestItemGroup
import com.teamwizardry.librarianlib.glitter.testmod.modid
import com.teamwizardry.librarianlib.glitter.testmod.systems.ParticleSystems
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.item.UseAction
import net.minecraft.util.ActionResult
import net.minecraft.util.ActionResultType
import net.minecraft.util.Hand
import net.minecraft.util.ResourceLocation
import net.minecraft.world.World

class ParticleSpawnerItem(val type: String): Item(
    Properties()
        .tab(TestItemGroup)
        .stacksTo(1)
) {

    init {
        this.registryName = ResourceLocation(modid, "spawn_$type")
    }

    override fun getUseAnimation(stack: ItemStack): UseAction {
        return UseAction.BOW
    }

    override fun onUsingTick(stack: ItemStack, player: LivingEntity, count: Int) {
        if(player.level.isClientSide)
            SidedRunnable.client {
                ParticleSystems.spawn(type, player)
            }
    }

    override fun use(worldIn: World, playerIn: PlayerEntity, handIn: Hand): ActionResult<ItemStack> {
        if(playerIn.isCrouching) {
            if(!worldIn.isClientSide) {
                val eye = playerIn.getEyePosition(0f)
                val spawner = ParticleSpawnerEntity(worldIn)
                spawner.system = type
                spawner.setPos(eye.x, eye.y - spawner.eyeHeight, eye.z)
                spawner.xRot = playerIn.xRot
                spawner.yRot = playerIn.yRot
                worldIn.addFreshEntity(spawner)
            }
        } else {
//            playerIn. = handIn
        }
        return ActionResult(ActionResultType.SUCCESS, playerIn.getItemInHand(handIn))
    }

    override fun getUseDuration(stack: ItemStack): Int {
        return 3600 * 20 // an hour :P
    }
}
