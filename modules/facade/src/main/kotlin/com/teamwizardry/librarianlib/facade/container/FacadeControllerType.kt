package com.teamwizardry.librarianlib.facade.container

import com.teamwizardry.librarianlib.scribe.Scribe
import com.teamwizardry.librarianlib.scribe.nbt.NbtSerializer
import dev.thecodewarrior.mirror.Mirror
import dev.thecodewarrior.mirror.member.ConstructorMirror
import dev.thecodewarrior.mirror.type.TypeMirror
import dev.thecodewarrior.prism.PrismException
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory
import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry
import net.fabricmc.fabric.api.util.NbtType
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.ListTag
import net.minecraft.network.PacketByteBuf
import net.minecraft.screen.ScreenHandler
import net.minecraft.screen.ScreenHandlerType
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.text.Text
import net.minecraft.util.Identifier
import java.lang.IllegalArgumentException
import kotlin.math.min

public interface FacadeControllerType<T : FacadeController> {
    public val id: Identifier
    public val screenHandlerType: ScreenHandlerType<T>
    public fun open(player: ServerPlayerEntity, title: Text, vararg arguments: Any?)
}

public object FacadeControllerRegistry {
    @JvmStatic
    public fun <T : FacadeController> register(id: Identifier, clazz: Class<T>): FacadeControllerType<T> {
        val screenHandlerType = ScreenHandlerRegistry.registerExtended(id, FacadeControllerClientFactoryImpl(clazz))
        return FacadeControllerTypeImpl(id, screenHandlerType, clazz)
    }
}

public class FacadeControllerFactory<T : FacadeController>(private val clazz: Class<T>) {
    private val type = Mirror.reflectClass(clazz)
    private val constructor: ConstructorMirror
    private val argumentTypes: List<TypeMirror>
    private val argumentSerializers: List<NbtSerializer<*>>

    init {
        if (type.declaredConstructors.size != 1) {
            throw IllegalArgumentException("$type must have exactly one constructor")
        }
        constructor = type.declaredConstructors[0]
        if (
            constructor.parameters.size < 2 ||
            constructor.parameterTypes[0] != Mirror.types.int ||
            constructor.parameterTypes[1] != Mirror.reflect<PlayerEntity>()
        ) {
            val actualArgs = constructor.parameters.subList(0, min(2, constructor.parameters.size))
            throw IllegalArgumentException(
                "The first two parameters of $type's constructor must be " +
                        "`int windowId, PlayerEntity player`, not `${actualArgs.joinToString(", ")}`"
            )
        }
        argumentTypes = constructor.parameterTypes.subList(2, constructor.parameterTypes.size)
        try {
            argumentSerializers = argumentTypes.map { Scribe.nbt[it].value }
        } catch (e: PrismException) {
            throw IllegalArgumentException("Unable to serialize the constructor for $type", e)
        }
    }

    public fun writeArguments(arguments: Array<out Any?>, buffer: PacketByteBuf) {
        val list = ListTag()
        argumentSerializers.mapIndexed { i, serializer ->
            val tag = CompoundTag()
            arguments[i]?.also {
                tag.put("V", serializer.write(it))
            }
            list.add(tag)
        }.toTypedArray()
        buffer.writeCompoundTag(CompoundTag().also { it.put("ll", list) })
    }

    public fun readArguments(buffer: PacketByteBuf): Array<Any?> {
        val list = buffer.readCompoundTag()!!.getList("ll", NbtType.COMPOUND)
        return argumentSerializers.mapIndexed { i, serializer ->
            list.getCompound(i).get("V")?.let {
                serializer.read(it, null)
            }
        }.toTypedArray()
    }

    public fun create(syncId: Int, player: PlayerEntity, arguments: Array<out Any?>): T {
        return constructor.call(syncId, player, *arguments)
    }
}

private class FacadeControllerClientFactoryImpl<T : FacadeController>(
    clazz: Class<T>
) : ScreenHandlerRegistry.ExtendedClientHandlerFactory<T> {
    val controllerFactory = FacadeControllerFactory(clazz)

    override fun create(syncId: Int, inventory: PlayerInventory, buf: PacketByteBuf): T {
        return controllerFactory.create(syncId, inventory.player, controllerFactory.readArguments(buf))
    }
}

private class FacadeControllerTypeImpl<T : FacadeController>(
    override val id: Identifier,
    override val screenHandlerType: ScreenHandlerType<T>,
    clazz: Class<T>
) : FacadeControllerType<T> {
    val controllerFactory = FacadeControllerFactory(clazz)

    override fun open(player: ServerPlayerEntity, title: Text, vararg arguments: Any?) {
        player.openHandledScreen(object : ExtendedScreenHandlerFactory {
            override fun createMenu(windowId: Int, playerInv: PlayerInventory, player: PlayerEntity): ScreenHandler {
                return controllerFactory.create(windowId, player, arguments)
            }

            override fun getDisplayName(): Text {
                return title
            }

            override fun writeScreenOpeningData(player: ServerPlayerEntity?, buf: PacketByteBuf) {
                controllerFactory.writeArguments(arguments, buf)
            }
        })
    }
}
