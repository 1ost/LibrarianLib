@file:Suppress("DEPRECATION")

package com.teamwizardry.librarianlib.common.core

import com.teamwizardry.librarianlib.client.book.Book
import com.teamwizardry.librarianlib.common.util.EasyConfigHandler
import com.teamwizardry.librarianlib.common.util.autoregister.AutoRegisterHandler
import com.teamwizardry.librarianlib.common.util.bitsaving.BitwiseStorageManager
import net.minecraft.util.text.translation.I18n
import net.minecraftforge.fml.common.Loader
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import net.minecraftforge.fml.common.event.FMLInterModComms
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent
import java.io.InputStream

/**
 * @author WireSegal
 * Created at 5:07 PM on 4/12/16.
 */
open class LibCommonProxy {

    open fun pre(e: FMLPreInitializationEvent) {
        BitwiseStorageManager
        EasyConfigHandler.loadAsm(e.asmData)
        EasyConfigHandler.init(e.suggestedConfigurationFile)
    }

    open fun latePre(e: FMLPreInitializationEvent) {
        AutoRegisterHandler.handle(e)
    }

    open fun init(e: FMLInitializationEvent) {
        // NO-OP
    }

    open fun lateInit(e: FMLInitializationEvent) {
        // NO-OP
    }

    open fun post(e: FMLPostInitializationEvent) {
        // NO-OP
    }

    open fun latePost(e: FMLPostInitializationEvent) {
        // NO-OP
    }

    /**
     * Translates a string. Works server-side or client-side.
     * [s] is the localization key, and [format] is any objects you want to fill into `%s`.
     */
    open fun translate(s: String, vararg format: Any?): String {
        return I18n.translateToLocalFormatted(s, *format)
    }

    /**
     * Gets a resource for a given modid.
     * [modId] must be the name of an existing mod server-side. Otherwise, it'll return null.
     * Client-side, it can be any domain provided by resource packs.
     * [path] should be of the format `path/to/file.extension`. Leading slashes will be ignored. Do not use backslashes.
     */
    open fun getResource(modId: String, path: String): InputStream? {
        val fixPath = path.removePrefix("/")
        val mods = Loader.instance().indexedModList
        val mod = mods[modId] ?: return null
        return mod.mod.javaClass.getResourceAsStream("/assets/$modId/$fixPath")
    }

    open val bookInstance: Book?
        get() = null

}

