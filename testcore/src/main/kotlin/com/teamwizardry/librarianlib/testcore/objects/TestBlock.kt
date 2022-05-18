package com.teamwizardry.librarianlib.testcore.objects

import com.mojang.datafixers.types.constant.EmptyPart
import com.teamwizardry.librarianlib.core.util.kotlin.threadLocal
import com.teamwizardry.librarianlib.core.util.sided.ClientMetaSupplier
import com.teamwizardry.librarianlib.core.util.sided.ClientSideFunction
import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.block.DirectionalBlock
import net.minecraft.client.renderer.tileentity.TileEntityRenderer
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher
import net.minecraft.entity.Entity
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.projectile.ProjectileEntity
import net.minecraft.item.BlockItemUseContext
import net.minecraft.item.ItemStack
import net.minecraft.loot.LootContext
import net.minecraft.state.DirectionProperty
import net.minecraft.state.StateContainer
import net.minecraft.state.properties.BlockStateProperties
import net.minecraft.tileentity.TileEntity
import net.minecraft.tileentity.TileEntityType
import net.minecraft.util.ActionResultType
import net.minecraft.util.Direction
import net.minecraft.util.Hand
import net.minecraft.util.Mirror
import net.minecraft.util.ResourceLocation
import net.minecraft.util.Rotation
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.BlockRayTraceResult
import net.minecraft.world.IBlockReader
import net.minecraft.world.IWorld
import net.minecraft.world.IWorldReader
import net.minecraft.world.World
import net.minecraft.world.server.ServerWorld
import net.minecraftforge.fml.ModLoadingContext
import java.util.Random

public open class TestBlock(public val config: TestBlockConfig): Block(config.also { configHolder = it }.properties) {
    public var tileEntityType: TileEntityType<*>? = null
        private set
    public val tileEntityRenderer: ClientMetaSupplier<TileEntityRendererFactory>?
    private var tileFactory: (() -> TileEntity)? = null

    init {
        this.registryName = ResourceLocation(ModLoadingContext.get().activeContainer.modId, config.id)
        if (config.directional) {
            this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.UP))
        }
        @Suppress("UNCHECKED_CAST")
        val tileConfig = config.tileConfig as TestTileConfig<TileEntity>?
        if(tileConfig != null) {
            tileFactory = {
                @Suppress("UNCHECKED_CAST")
                tileConfig.factory(tileEntityType as TileEntityType<TileEntity>)
            }
//            val type = TileEntityType.Builder.of(tileFactory!!, this).build(TileEntityType)
//            type.registryName = this.registryName
//            tileEntityType = type
            tileEntityRenderer = tileConfig.renderer
        } else {
            tileEntityRenderer = null
        }
    }

    public open val modelName: String
        get() = "${if (config.directional) "directional" else "normal"}/${if (config.transparent) "transparent" else "solid"}"

    override fun getDrops(state: BlockState, builder: LootContext.Builder): MutableList<ItemStack> {
        return mutableListOf()
    }

    override fun rotate(state: BlockState, rot: Rotation): BlockState {
        if (!config.directional)
            return state
        return state.setValue(DirectionalBlock.FACING, rot.rotate(state.getValue(FACING)))
    }

    override fun mirror(state: BlockState, mirrorIn: Mirror): BlockState {
        if (!config.directional)
            return state
        return state.setValue(DirectionalBlock.FACING, mirrorIn.mirror(state.getValue(FACING)))
    }

    override fun getStateForPlacement(context: BlockItemUseContext): BlockState? {
        if (!config.directional)
            return super.getStateForPlacement(context)

        val direction = context.clickedFace
        val blockstate = context.level.getBlockState(context.clickedPos.offset(direction.opposite.normal))
        return if (blockstate.block === this && blockstate.getValue(FACING) == direction) this.defaultBlockState().setValue(FACING, direction.opposite) else this.defaultBlockState().setValue(FACING, direction)
    }

    override fun createBlockStateDefinition(builder: StateContainer.Builder<Block, BlockState>) {
        if (!configHolder!!.directional)
            return
        builder.add(FACING)
    }

    // ticks ===========================================================================================================
    override fun animateTick(stateIn: BlockState, worldIn: World, pos: BlockPos, rand: Random) {
        super.animateTick(stateIn, worldIn, pos, rand)
    }

    @Suppress("DEPRECATION")
    override fun tick(state: BlockState, worldIn: ServerWorld, pos: BlockPos, rand: Random) {
        super.tick(state, worldIn, pos, rand)
    }

    @Suppress("DEPRECATION")
    override fun randomTick(state: BlockState, worldIn: ServerWorld, pos: BlockPos, random: Random) {
        super.randomTick(state, worldIn, pos, random)
    }

    // placed/broken ===================================================================================================
    @Suppress("DEPRECATION")
    override fun onPlace(p_220082_1_: BlockState, worldIn: World, pos: BlockPos, p_220082_4_: BlockState, p_220082_5_: Boolean) {
        super.onPlace(p_220082_1_, worldIn, pos, p_220082_4_, p_220082_5_)
    }

    @Suppress("DEPRECATION")
    override fun updateShape(stateIn: BlockState, facing: Direction, facingState: BlockState, worldIn: IWorld, currentPos: BlockPos, facingPos: BlockPos): BlockState {
        return super.updateShape(stateIn, facing, facingState, worldIn, currentPos, facingPos)
    }

    override fun updateEntityAfterFallOn(worldIn: IBlockReader, entityIn: Entity) {
        super.updateEntityAfterFallOn(worldIn, entityIn)
    }

    override fun playerWillDestroy(worldIn: World, pos: BlockPos, state: BlockState, player: PlayerEntity) {
        if (config.destroy.exists)
            config.destroy.run(worldIn.isClientSide, TestBlockConfig.DestroyContext(state, worldIn, pos, player))
        else
            super.playerWillDestroy(worldIn, pos, state, player)
    }

    override fun setPlacedBy(worldIn: World, pos: BlockPos, state: BlockState, placer: LivingEntity?, stack: ItemStack) {
        if (placer is PlayerEntity)
            config.place.run(worldIn.isClientSide, TestBlockConfig.PlaceContext(state, worldIn, pos, placer, stack))
        else
            super.setPlacedBy(worldIn, pos, state, placer, stack)
    }

    // interaction =====================================================================================================
    @Suppress("DEPRECATION")
    override fun use(state: BlockState, worldIn: World, pos: BlockPos, player: PlayerEntity, handIn: Hand, hit: BlockRayTraceResult): ActionResultType {
        config.rightClick.run(worldIn.isClientSide, TestBlockConfig.RightClickContext(state, worldIn, pos, player, handIn, hit))
        if (config.rightClick.exists)
            return ActionResultType.CONSUME
        else
            return super.use(state, worldIn, pos, player, handIn, hit)
    }

    @Suppress("DEPRECATION")
    override fun attack(state: BlockState, worldIn: World, pos: BlockPos, player: PlayerEntity) {
        if (config.leftClick.exists)
            config.leftClick.run(worldIn.isClientSide, TestBlockConfig.LeftClickContext(state, worldIn, pos, player))
        else
            super.attack(state, worldIn, pos, player)
    }

    // entity interaction ==============================================================================================
    @Suppress("DEPRECATION")
    override fun entityInside(state: BlockState, worldIn: World, pos: BlockPos, entityIn: Entity) {
        super.entityInside(state, worldIn, pos, entityIn)
    }

    override fun stepOn(worldIn: World, pos: BlockPos, entityIn: Entity) {
        super.stepOn(worldIn, pos, entityIn)
    }

    override fun fallOn(worldIn: World, pos: BlockPos, entityIn: Entity, fallDistance: Float) {
        super.fallOn(worldIn, pos, entityIn, fallDistance)
    }

    override fun onProjectileHit(worldIn: World, state: BlockState, hit: BlockRayTraceResult, projectile: ProjectileEntity) {
        super.onProjectileHit(worldIn, state, hit, projectile)
    }

    // misc ============================================================================================================

    override fun onNeighborChange(state: BlockState?, world: IWorldReader?, pos: BlockPos?, neighbor: BlockPos?) {
        super.onNeighborChange(state, world, pos, neighbor)
    }

    override fun skipRendering(state: BlockState, adjacentBlockState: BlockState, side: Direction): Boolean {
        return if (adjacentBlockState.block === this) true else super.skipRendering(state, adjacentBlockState, side)
    }

    override fun hasTileEntity(state: BlockState?): Boolean {
        return tileEntityType != null
    }

    override fun createTileEntity(state: BlockState?, world: IBlockReader?): TileEntity? {
        return tileFactory!!.invoke()
    }

    public companion object {
        public val FACING: DirectionProperty = BlockStateProperties.FACING

        // needed because fillStateContainer is called before we can set the config property
        private var configHolder: TestBlockConfig? by threadLocal()
    }
}

public fun interface TileEntityRendererFactory: ClientSideFunction {
    public fun create(dispatcher: TileEntityRendererDispatcher): TileEntityRenderer<*>
}
