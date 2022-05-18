package com.teamwizardry.librarianlib.albedo.testmod.shaders

import com.mojang.blaze3d.matrix.MatrixStack
import com.teamwizardry.librarianlib.albedo.GLSL
import com.teamwizardry.librarianlib.albedo.Shader
import com.teamwizardry.librarianlib.albedo.testmod.ShaderTest
import com.teamwizardry.librarianlib.core.util.Client
import com.teamwizardry.librarianlib.core.util.SimpleRenderTypes
import com.teamwizardry.librarianlib.core.util.kotlin.color
import com.teamwizardry.librarianlib.core.util.kotlin.pos2d
import net.minecraft.client.renderer.IRenderTypeBuffer
import net.minecraft.util.ResourceLocation
import org.lwjgl.opengl.GL11
import java.awt.Color

internal object IntArrayUniform: ShaderTest<IntArrayUniform.Test>() {

    override fun doDraw(matrixStack: MatrixStack) {
        val minX = 0.0
        val minY = 0.0
        val maxX = 128.0
        val maxY = 128.0

        val c = Color.WHITE

        val index = (Client.time.seconds % 2).toInt()
        shader.index.set(index)
        shader.primitive[0] = 10
        shader.primitive[1] = 20
        shader.vector2.set(0, 10, 20)
        shader.vector2.set(1, 30, 40)
        shader.vector3.set(0, 10, 20, 30)
        shader.vector3.set(1, 40, 50, 60)
        shader.vector4.set(0, 10, 20, 30, 40)
        shader.vector4.set(1, 50, 60, 70, 80)

        val buffer = IRenderTypeBuffer.immediate(Client.tessellator.builder)
        val vb = buffer.getBuffer(renderType)

        vb.pos2d(minX, maxY).color(c).uv(0f, 1f).endVertex()
        vb.pos2d(maxX, maxY).color(c).uv(1f, 1f).endVertex()
        vb.pos2d(maxX, minY).color(c).uv(1f, 0f).endVertex()
        vb.pos2d(minX, minY).color(c).uv(0f, 0f).endVertex()

        shader.bind()
        buffer.endBatch()
        shader.unbind()

        val fr = Client.minecraft.font
        fr.draw(matrixStack, "$index",
            (maxX - 2 - fr.width("$index")).toInt().toFloat(),
            minY.toFloat() + 11,
            Color.WHITE.rgb
        )
    }

    private val renderType = SimpleRenderTypes.flat(ResourceLocation("minecraft:missingno"), GL11.GL_QUADS)

    class Test: Shader("int_array_tests", null, ResourceLocation("ll-albedo-test:shaders/int_array_tests.frag")) {
        val index = GLSL.glInt()
        val primitive = GLSL.glInt[2]
        val vector2 = GLSL.ivec2[2]
        val vector3 = GLSL.ivec3[2]
        val vector4 = GLSL.ivec4[2]
    }
}