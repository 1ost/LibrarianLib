package com.teamwizardry.librarianlib.foundation

import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.eventbus.api.IEventBus
import net.minecraftforge.fml.common.Mod
import thedarkcolour.kotlinforforge.forge.MOD_BUS

@Mod("ll-foundation")
internal object Bootstrap {
    private val modEventBus: IEventBus = MOD_BUS
    init {
        MinecraftForge.EVENT_BUS.register(LibrarianLibFoundationModule)
        modEventBus.register(LibrarianLibFoundationModule)
    }
}