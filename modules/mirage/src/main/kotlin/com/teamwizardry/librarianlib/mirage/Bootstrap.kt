package com.teamwizardry.librarianlib.mirage

import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.eventbus.api.IEventBus
import net.minecraftforge.fml.common.Mod
import thedarkcolour.kotlinforforge.forge.MOD_BUS

@Mod("ll-mirage")
internal object Bootstrap {
    private val modEventBus: IEventBus = MOD_BUS
    init {
        MinecraftForge.EVENT_BUS.register(LibrarianLibMirageModule)
        modEventBus.register(LibrarianLibMirageModule)
    }
}