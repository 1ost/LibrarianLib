package com.teamwizardry.librarianlib.courier

import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.eventbus.api.IEventBus
import net.minecraftforge.fml.common.Mod
import thedarkcolour.kotlinforforge.forge.MOD_BUS

@Mod("ll-courier")
internal object Boostrap {
    private val modEventBus: IEventBus = MOD_BUS
    init {
        MinecraftForge.EVENT_BUS.register(LibrarianLibCourierModule)
        modEventBus.register(LibrarianLibCourierModule)
    }
}