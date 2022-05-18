package com.teamwizardry.librarianlib.foundation.testmod.customtypes.client

import com.mojang.blaze3d.matrix.MatrixStack
import com.teamwizardry.librarianlib.core.util.Client
import com.teamwizardry.librarianlib.core.util.loc
import com.teamwizardry.librarianlib.core.util.kotlin.pos
import com.teamwizardry.librarianlib.foundation.testmod.customtypes.TestTileEntity
import net.minecraft.client.renderer.IRenderTypeBuffer
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.tileentity.TileEntityRenderer
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher

class TestTileEntityRenderer(rendererDispatcherIn: TileEntityRendererDispatcher) :
    TileEntityRenderer<TestTileEntity>(rendererDispatcherIn) {

    override fun render(
        tileEntityIn: TestTileEntity, partialTicks: Float, matrixStackIn: MatrixStack,
        bufferIn: IRenderTypeBuffer, combinedLightIn: Int, combinedOverlayIn: Int
    ) {
        val texture = Client.getBlockAtlasSprite(loc("block/dirt"))
        val builder = texture.wrap(bufferIn.getBuffer(RenderType.cutout()))
        builder.pos(matrixStackIn.last().pose(), 0, tileEntityIn.lastFallDistance + 1, 0).color(1f, 1f, 1f, 1f)
        builder.uv(0f, 0f).uv2(combinedLightIn).normal(0f, 1f, 0f).endVertex()
        builder.pos(matrixStackIn.last().pose(), 1, tileEntityIn.lastFallDistance + 1, 0).color(1f, 1f, 1f, 1f)
        builder.uv(1f, 0f).uv2(combinedLightIn).normal(0f, 1f, 0f).endVertex()
        builder.pos(matrixStackIn.last().pose(), 1, tileEntityIn.lastFallDistance + 1, 1).color(1f, 1f, 1f, 1f)
        builder.uv(1f, 1f).uv2(combinedLightIn).normal(0f, 1f, 0f).endVertex()
        builder.pos(matrixStackIn.last().pose(), 0, tileEntityIn.lastFallDistance + 1, 1).color(1f, 1f, 1f, 1f)
        builder.uv(0f, 1f).uv2(combinedLightIn).normal(0f, 1f, 0f).endVertex()

        builder.pos(matrixStackIn.last().pose(), 0, tileEntityIn.lastFallDistance + 1, 1).color(1f, 1f, 1f, 1f)
        builder.uv(0f, 1f).uv2(combinedLightIn).normal(0f, -1f, 0f).endVertex()
        builder.pos(matrixStackIn.last().pose(), 1, tileEntityIn.lastFallDistance + 1, 1).color(1f, 1f, 1f, 1f)
        builder.uv(1f, 1f).uv2(combinedLightIn).normal(0f, -1f, 0f).endVertex()
        builder.pos(matrixStackIn.last().pose(), 1, tileEntityIn.lastFallDistance + 1, 0).color(1f, 1f, 1f, 1f)
        builder.uv(1f, 0f).uv2(combinedLightIn).normal(0f, -1f, 0f).endVertex()
        builder.pos(matrixStackIn.last().pose(), 0, tileEntityIn.lastFallDistance + 1, 0).color(1f, 1f, 1f, 1f)
        builder.uv(0f, 0f).uv2(combinedLightIn).normal(0f, -1f, 0f).endVertex()
    }
}