package com.teamwizardry.librarianlib.foundation.registration

import com.teamwizardry.librarianlib.foundation.LibrarianLibFoundationModule
import com.teamwizardry.librarianlib.foundation.block.IFoundationBlock
import com.teamwizardry.librarianlib.foundation.item.IFoundationItem
import net.minecraft.block.Block
import net.minecraft.client.renderer.RenderTypeLookup
import net.minecraft.client.renderer.entity.EntityRenderer
import net.minecraft.client.renderer.tileentity.TileEntityRenderer
import net.minecraft.data.BlockTagsProvider
import net.minecraft.data.DataGenerator
import net.minecraft.data.ItemTagsProvider
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityType
import net.minecraft.item.BlockItem
import net.minecraft.item.Item
import net.minecraft.item.ItemGroup
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.tags.Tag
import net.minecraft.tileentity.TileEntity
import net.minecraft.tileentity.TileEntityType
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.api.distmarker.OnlyIn
import net.minecraftforge.client.model.generators.BlockStateProvider
import net.minecraftforge.client.model.generators.ExistingFileHelper
import net.minecraftforge.common.capabilities.CapabilityInject
import net.minecraftforge.common.capabilities.CapabilityManager
import net.minecraftforge.common.data.LanguageProvider
import net.minecraftforge.event.RegistryEvent
import net.minecraftforge.eventbus.api.IEventBus
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.client.registry.ClientRegistry
import net.minecraftforge.fml.client.registry.RenderingRegistry
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent
import net.minecraftforge.fml.event.lifecycle.FMLDedicatedServerSetupEvent
import net.minecraftforge.fml.event.lifecycle.GatherDataEvent

/**
 * The main class for registering objects in LibrarianLib Foundation. This class manages when to register them, along
 * with any secondary registrations, such as [block items][BlockItem] and block render layers.
 */
public class RegistrationManager(public val modid: String, modEventBus: IEventBus) {
    init {
        modEventBus.register(this)
    }

    /**
     * The default item group for items registered with this registration manager.
     */
    public val itemGroup: ItemGroup = object: ItemGroup(modid) {
        @OnlyIn(Dist.CLIENT)
        override fun createIcon(): ItemStack {
            return ItemStack(itemGroupIcon?.get() ?: Items.AIR)
        }
    }

    /**
     * The default item group icon for items registered with this registration manager.
     */
    public var itemGroupIcon: LazyItem? = null

    private val blocks = mutableListOf<BlockSpec>()
    private val items = mutableListOf<ItemSpec>()
    private val tileEntities = mutableListOf<TileEntitySpec<*>>()
    private val entities = mutableListOf<EntitySpec<*>>()
    private val capabilities = mutableListOf<CapabilitySpec<*>>()

    /**
     * Methods for performing data generation
     */
    public val datagen: DataGen = DataGen()

    /**
     * Adds a block to this registration manager and returns a lazy reference to it
     */
    public fun add(spec: BlockSpec): LazyBlock {
        spec.modid = modid
        @Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
        spec.itemProperties.group(spec.itemGroup.get(this))
        blocks.add(spec)
        return spec.lazy
    }

    /**
     * Adds an item to this registration manager and returns a lazy reference to it
     */
    public fun add(spec: ItemSpec): LazyItem {
        spec.modid = modid
        @Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
        spec.itemProperties.group(spec.itemGroup.get(this))
        items.add(spec)
        return spec.lazy
    }

    /**
     * Adds a tile entity type to this registration manager and returns a lazy reference to it
     */
    public fun <T: TileEntity> add(spec: TileEntitySpec<T>): LazyTileEntityType<T> {
        spec.modid = modid
        tileEntities.add(spec)
        return spec.lazy
    }

    /**
     * Adds an entity type to this registration manager and returns a lazy reference to it
     */
    public fun <T: Entity> add(spec: EntitySpec<T>): LazyEntityType<T> {
        spec.modid = modid
        entities.add(spec)
        return spec.lazy
    }

    /**
     * Adds a capability type to this registration manager. To access the resulting capability, use
     * [@CapabilityInject][CapabilityInject].
     */
    public fun <T> add(spec: CapabilitySpec<T>) {
        capabilities.add(spec)
    }

    @SubscribeEvent
    @JvmSynthetic
    internal fun registerBlocks(e: RegistryEvent.Register<Block>) {
        blocks.forEach { block ->
            logger.debug("Manager for $modid: Registering block ${block.registryName}")
            e.registry.register(block.blockInstance)
        }
    }

    @SubscribeEvent
    @JvmSynthetic
    internal fun registerItems(e: RegistryEvent.Register<Item>) {
        blocks.forEach { block ->
            if (block.hasItem) {
                logger.debug("Manager for $modid: Registering blockitem ${block.registryName}")
                e.registry.register(block.itemInstance)
            }
        }
        items.forEach { item ->
            logger.debug("Manager for $modid: Registering item ${item.registryName}")
            e.registry.register(item.itemInstance)
        }
    }

    @SubscribeEvent
    @JvmSynthetic
    internal fun registerTileEntities(e: RegistryEvent.Register<TileEntityType<*>>) {
        tileEntities.forEach { te ->
            logger.debug("Manager for $modid: Registering TileEntityType ${te.registryName}")
            e.registry.register(te.typeInstance)
        }
    }

    @SubscribeEvent
    @JvmSynthetic
    internal fun registerEntities(e: RegistryEvent.Register<EntityType<*>>) {
        entities.forEach { te ->
            logger.debug("Manager for $modid: Registering EntityType ${te.registryName}")
            e.registry.register(te.typeInstance)
        }
    }

    @SubscribeEvent
    @JvmSynthetic
    internal fun commonSetup(e: FMLCommonSetupEvent) {
        capabilities.forEach { cap ->
            @Suppress("UNCHECKED_CAST")
            cap as CapabilitySpec<Any> // appease the type checking gods
            CapabilityManager.INSTANCE.register(cap.type, cap.storage, cap.defaultImpl)
        }
    }

    @SubscribeEvent
    @JvmSynthetic
    internal fun clientSetup(e: FMLClientSetupEvent) {
        blocks.forEach { block ->
            RenderTypeLookup.setRenderLayer(block.blockInstance, block.renderLayer.getRenderType())
        }
        tileEntities.forEach { spec ->
            val renderer = spec.renderer ?: return@forEach
            logger.debug("Manager for $modid: Registering TileEntityRenderer for ${spec.registryName}")
            @Suppress("UNCHECKED_CAST")
            ClientRegistry.bindTileEntityRenderer(spec.typeInstance as TileEntityType<TileEntity>) {
                renderer.applyClient(it) as TileEntityRenderer<TileEntity>
            }
        }
        for(spec in entities) {
            val factory = spec.renderFactory ?: continue
                logger.debug("Manager for $modid: Registering EntityRenderer for ${spec.registryName}")
                @Suppress("UNCHECKED_CAST")
                RenderingRegistry.registerEntityRenderingHandler(spec.typeInstance as EntityType<Entity>) {
                    factory.applyClient(it) as EntityRenderer<Entity>
            }
        }
    }

    @SubscribeEvent
    @JvmSynthetic
    internal fun dedicatedServerSetup(e: FMLDedicatedServerSetupEvent) {
    }

    @SubscribeEvent
    @JvmSynthetic
    internal fun gatherData(e: GatherDataEvent) {
        e.generator.addProvider(BlockStateGeneration(e.generator, e.existingFileHelper))

        val locales = mutableSetOf<String>()
        for(spec in blocks) {
            locales.addAll(spec.datagen.names.keys)
        }
        for(spec in items) {
            locales.addAll(spec.datagen.names.keys)
        }
        for(locale in locales) {
            e.generator.addProvider(LanguageGeneration(e.generator, locale))
        }

        e.generator.addProvider(BlockTagsGeneration(e.generator))
        e.generator.addProvider(ItemTagsGeneration(e.generator))
    }

    public class DataGen {
        public val blockTags: TagGen<Block> = TagGen()
        public val itemTags: TagGen<Item> = TagGen()

        public class TagGen<T> {
            @get:JvmSynthetic
            internal val metaTags = mutableMapOf<Tag<T>, MutableList<Tag<T>>>()

            @get:JvmSynthetic
            internal val valueTags = mutableMapOf<Tag<T>, MutableList<T>>()

            /**
             * Add values to the given tag
             */
            public fun add(tag: Tag<T>, vararg values: T): TagGen<T> {
                valueTags.getOrPut(tag) { mutableListOf() }.addAll(values)
                return this
            }

            /**
             * Add tags to the given tag
             */
            public fun meta(tag: Tag<T>, vararg tags: Tag<T>): TagGen<T> {
                metaTags.getOrPut(tag) { mutableListOf() }.addAll(tags)
                return this
            }
        }
    }

    private inner class BlockStateGeneration(gen: DataGenerator, exFileHelper: ExistingFileHelper):
        BlockStateProvider(gen, modid, TextureExistsExistingFileHelper(exFileHelper)) {
        override fun registerStatesAndModels() {
            logger.debug("Manager for $modid: Generating blockstates/models")
            for(spec in blocks) {
                val manualGen = spec.datagen.model
                val instance = spec.blockInstance
                if (manualGen != null) {
                    logger.debug("Manager for $modid: Calling manual blockstate generator for block ${spec.registryName}")
                    manualGen.accept(this)
                } else if(instance is IFoundationBlock) {
                    logger.debug("Manager for $modid: Calling IFoundationBlock blockstate generator for block ${spec.registryName}")
                    instance.generateBlockState(this)
                }

                val manualItemGen = spec.datagen.itemModel
                val itemInstance = spec.itemInstance
                if (manualItemGen != null) {
                    logger.debug("Manager for $modid: Calling manual item model generator for block ${spec.registryName}")
                    manualItemGen.accept(this.itemModels())
                } else if(itemInstance is IFoundationItem) {
                    logger.debug("Manager for $modid: Calling IFoundationItem model generator for block item ${spec.registryName}")
                    itemInstance.generateItemModel(this.itemModels())
                }
            }

            for(spec in items) {
                val manualGen = spec.datagen.model
                val instance = spec.itemInstance
                if (manualGen != null) {
                    logger.debug("Manager for $modid: Calling manual model generator for item ${spec.registryName}")
                    manualGen.accept(this.itemModels())
                } else if(instance is IFoundationItem) {
                    logger.debug("Manager for $modid: Calling IFoundationItem model generator for item ${spec.registryName}")
                    instance.generateItemModel(this.itemModels())
                }
            }
        }
    }

    private inner class LanguageGeneration(gen: DataGenerator, val locale: String):
        LanguageProvider(gen, modid, locale) {
        override fun addTranslations() {
            logger.debug("Manager for $modid: Generating $locale language")

            for(spec in blocks) {
                val name = spec.datagen.names[locale] ?: continue
                this.add(spec.blockInstance, name)
                spec.itemInstance?.also { item ->
                    if (item.translationKey != spec.blockInstance.translationKey)
                        this.add(spec.itemInstance, name)
                }
            }

            for(spec in items) {
                spec.datagen.names[locale]?.also { name ->
                    this.add(spec.itemInstance, name)
                }
            }
        }
    }

    private inner class BlockTagsGeneration(gen: DataGenerator): BlockTagsProvider(gen) {
        override fun registerTags() {
            logger.debug("Manager for $modid: Generating tags")
            for(spec in blocks) {
                spec.datagen.tags.forEach { tag ->
                    getBuilder(tag).add(spec.blockInstance)
                }
            }

            datagen.blockTags.valueTags.forEach { (tag, values) ->
                getBuilder(tag).add(*values.toTypedArray())
            }

            datagen.blockTags.metaTags.forEach { (tag, values) ->
                getBuilder(tag).add(*values.toTypedArray())
            }
        }
    }

    private inner class ItemTagsGeneration(gen: DataGenerator): ItemTagsProvider(gen) {
        override fun registerTags() {
            for(spec in blocks) {
                val item = spec.itemInstance ?: continue
                spec.datagen.itemTags.forEach { tag ->
                    getBuilder(tag).add(item)
                }
            }

            for(spec in items) {
                spec.datagen.tags.forEach { tag ->
                    getBuilder(tag).add(spec.itemInstance)
                }
            }

            datagen.itemTags.valueTags.forEach { (tag, values) ->
                getBuilder(tag).add(*values.toTypedArray())
            }

            datagen.itemTags.metaTags.forEach { (tag, values) ->
                getBuilder(tag).add(*values.toTypedArray())
            }
        }
    }

    private companion object {
        val logger = LibrarianLibFoundationModule.makeLogger<RegistrationManager>()
    }
}
