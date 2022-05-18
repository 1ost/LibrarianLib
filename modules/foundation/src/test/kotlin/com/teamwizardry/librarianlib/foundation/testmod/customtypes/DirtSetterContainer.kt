package com.teamwizardry.librarianlib.foundation.testmod.customtypes

import net.minecraft.entity.player.PlayerEntity
import net.minecraft.util.math.BlockPos
import com.teamwizardry.librarianlib.facade.container.FacadeContainer
import com.teamwizardry.librarianlib.facade.container.messaging.Message
import com.teamwizardry.librarianlib.foundation.testmod.ModContainers
import com.teamwizardry.librarianlib.math.clamp
import net.minecraft.block.Blocks

class DirtSetterContainer(
    windowId: Int, player: PlayerEntity,
    private val pos: BlockPos
): FacadeContainer(ModContainers.dirtSetter.get(), windowId, player) {
    @Message
    private fun setToDirt(offset: Int) {
        if(isClientContainer)
            return

        player.level.setBlockAndUpdate(
            pos.offset(0, offset.clamp(-1, 1), 0),
            Blocks.DIRT.defaultBlockState()
        )
    }

    override fun stillValid(playerIn: PlayerEntity): Boolean {
        return true
    }
}