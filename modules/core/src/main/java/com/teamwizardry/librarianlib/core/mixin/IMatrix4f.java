package com.teamwizardry.librarianlib.core.mixin;

import net.minecraft.util.math.vector.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Matrix4f.class)
public interface IMatrix4f {
    /** Get row 0, column 0 */
    @Accessor("m00") float getM00();
    /** Set row 0, column 0 */
    @Accessor("m00")void setM00(float v);
    /** Get row 0, column 1 */
    @Accessor("m01")float getM01();
    /** Set row 0, column 1 */
    @Accessor("m01")void setM01(float v);
    /** Get row 0, column 2 */
    @Accessor("m02")float getM02();
    /** Set row 0, column 2 */
    @Accessor("m02")void setM02(float v);
    /** Get row 0, column 3 */
    @Accessor("m03")float getM03();
    /** Set row 0, column 3 */
    @Accessor("m03")void setM03(float v);
    /** Get row 1, column 0 */
    @Accessor("m10")float getM10();
    /** Set row 1, column 0 */
    @Accessor("m10")void setM10(float v);
    /** Get row 1, column 1 */
    @Accessor("m11")float getM11();
    /** Set row 1, column 1 */
    @Accessor("m11")void setM11(float v);
    /** Get row 1, column 2 */
    @Accessor("m12")float getM12();
    /** Set row 1, column 2 */
    @Accessor("m12")void setM12(float v);
    /** Get row 1, column 3 */
    @Accessor("m13")float getM13();
    /** Set row 1, column 3 */
    @Accessor("m13")void setM13(float v);
    /** Get row 2, column 0 */
    @Accessor("m20")float getM20();
    /** Set row 2, column 0 */
    @Accessor("m20")void setM20(float v);
    /** Get row 2, column 1 */
    @Accessor("m21")float getM21();
    /** Set row 2, column 1 */
    @Accessor("m21")void setM21(float v);
    /** Get row 2, column 2 */
    @Accessor("m22")float getM22();
    /** Set row 2, column 2 */
    @Accessor("m22")void setM22(float v);
    /** Get row 2, column 3 */
    @Accessor("m23")float getM23();
    /** Set row 2, column 3 */
    @Accessor("m23")void setM23(float v);
    /** Get row 3, column 0 */
    @Accessor("m30")float getM30();
    /** Set row 3, column 0 */
    @Accessor("m30")void setM30(float v);
    /** Get row 3, column 1 */
    @Accessor("m31")float getM31();
    /** Set row 3, column 1 */
    @Accessor("m31")void setM31(float v);
    /** Get row 3, column 2 */
    @Accessor("m32")float getM32();
    /** Set row 3, column 2 */
    @Accessor("m32")void setM32(float v);
    /** Get row 3, column 3 */
    @Accessor("m33")float getM33();
    /** Set row 3, column 3 */
    @Accessor("m33")void setM33(float v);
}
