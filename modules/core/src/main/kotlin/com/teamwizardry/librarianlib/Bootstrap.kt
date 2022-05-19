package com.teamwizardry.librarianlib

import com.teamwizardry.librarianlib.core.LibrarianLibCoreModule
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.eventbus.api.IEventBus
import net.minecraftforge.fml.common.Mod
import thedarkcolour.kotlinforforge.forge.MOD_BUS

@Mod("ll-core")
internal object Bootstrap {
    private val modEventBus: IEventBus = MOD_BUS
    init {
        MinecraftForge.EVENT_BUS.register(LibrarianLibCoreModule)
        modEventBus.register(LibrarianLibCoreModule)
    }
}