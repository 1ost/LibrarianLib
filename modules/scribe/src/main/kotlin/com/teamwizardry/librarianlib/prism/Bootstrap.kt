package com.teamwizardry.librarianlib.prism

import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.eventbus.api.IEventBus
import net.minecraftforge.fml.common.Mod
import thedarkcolour.kotlinforforge.forge.MOD_BUS

@Mod("ll-scribe")
internal object Bootstrap {
    private val modEventBus: IEventBus = MOD_BUS
    init {
        MinecraftForge.EVENT_BUS.register(LibrarianLibPrismModule)
        modEventBus.register(LibrarianLibPrismModule)
    }
}