package com.teamwizardry.librarianlib.testcore

import com.teamwizardry.librarianlib.core.util.ModLogManager
import com.teamwizardry.librarianlib.testcore.content.TestConfig
import net.minecraft.util.Identifier
import org.apache.logging.log4j.LogManager
import java.lang.IllegalStateException
import kotlin.reflect.KClass
import kotlin.reflect.full.primaryConstructor

/**
 * A DSL for creating test objects. Loosely based on gradle's Kotlin DSL.
 */
public class TestModContentManager(public val modid: String, logManager: ModLogManager) {
    private val logger = logManager.makeLogger("TestModContentManager")
    private val objects = mutableMapOf<String, TestConfig>()

    public fun registerCommon() {
        logger.info("Performing common registration")
        for(config in objects.values) {
            logger.info("Registering ${config.id}")
            config.registerCommon()
        }
    }

    public fun registerClient() {
        logger.info("Performing client registration")
        for(config in objects.values) {
            logger.info("Registering ${config.id}")
            config.registerClient()
        }
    }

    public fun registerServer() {
        logger.info("Performing server registration")
        for(config in objects.values) {
            logger.info("Registering ${config.id}")
            config.registerServer()
        }
    }

    public fun id(name: String): Identifier = Identifier(modid, name)

    public fun hasObject(name: String): Boolean {
        return objects.contains(name)
    }

    public fun <T : TestConfig> create(type: KClass<T>, name: String): T {
        objects[name]?.also {
            throw IllegalArgumentException(
                "An object named $name already exists (existing object is ${it.javaClass.canonicalName})"
            )
        }
        val value = type.primaryConstructor!!.call(this, Identifier(modid, name))
        objects[name] = value
        return value
    }

    public inline fun <reified T : TestConfig> create(name: String): T {
        return create(T::class, name)
    }

    public fun <T : TestConfig> create(type: KClass<T>, name: String, config: T.() -> Unit): T {
        return create(type, name).apply(config)
    }

    public inline fun <reified T : TestConfig> create(name: String, config: T.() -> Unit): T {
        return create(T::class, name).apply(config)
    }

    public fun <T : TestConfig> named(name: String): T {
        val value = objects[name] ?: throw IllegalStateException("No objects named $name exist")
        @Suppress("UNCHECKED_CAST")
        return value as T
    }

    public inline fun <T : TestConfig> named(name: String, config: T.() -> Unit): T {
        return named<T>(name).apply(config)
    }
}