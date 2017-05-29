package com.teamwizardry.librarianlib.test.container

import com.teamwizardry.librarianlib.test.testcore.TestEntryPoint
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent

/**
 * Created by TheCodeWarrior
 */
object ContainerEntryPoint : TestEntryPoint {
    override fun preInit(event: FMLPreInitializationEvent) {
        ContainerBlocks
    }

    override fun init(event: FMLInitializationEvent) {

    }

    override fun postInit(event: FMLPostInitializationEvent) {

    }
}

object ContainerBlocks {
    val test = BlockContainerTest()

    init {
        BlockPoweredMachine
        BlockFluidTank
    }
}
