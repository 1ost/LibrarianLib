package com.teamwizardry.librarianlib.client.fx.shader;

import com.teamwizardry.librarianlib.api.Const;
import com.teamwizardry.librarianlib.api.LibrarianLog;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.resources.IReloadableResourceManager;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingEvent.LivingJumpEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.ARBFragmentShader;
import org.lwjgl.opengl.ARBShaderObjects;
import org.lwjgl.opengl.ARBVertexShader;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Credit to Vazkii (https://github.com/Vazkii/Botania/blob/master/src/main/java/vazkii/botania/client/core/helper/ShaderHelper.java)
 */

public final class ShaderHelper implements IResourceManagerReloadListener {
    private static final int VERT = ARBVertexShader.GL_VERTEX_SHADER_ARB;
    private static final int FRAG = ARBFragmentShader.GL_FRAGMENT_SHADER_ARB;
    private static final List<Shader> shaders = new ArrayList<Shader>();
    private static boolean hasLoaded = false;
    private static ShaderHelper INSTANCE = new ShaderHelper();
    
    private ShaderHelper() {
    }

    public static <T extends Shader> T addShader(T shader) {
    	shaders.add(shader);
    	if(hasLoaded && !useShaders())
    		createProgram(shader);
    	return shader;
    }
    
    public static void initShaders() {
        if (!useShaders())
            return;

        //burst = new BurstShader(null, "/assets/wizardry/shader/burstNew.frag");

        //createProgram(burst);

        for (Shader shader : shaders) {
			createProgram(shader);
		}
        
        if (!hasLoaded) {
            hasLoaded = true;

            MinecraftForge.EVENT_BUS.register(INSTANCE);
            if (Const.isDev && Minecraft.getMinecraft().getResourceManager() instanceof IReloadableResourceManager)
                ((IReloadableResourceManager) Minecraft.getMinecraft().getResourceManager()).registerReloadListener(INSTANCE);
        }
    }

    public static <T extends Shader> void useShader(T shader, ShaderCallback<T> callback) {
        if (shader == null) {
            ARBShaderObjects.glUseProgramObjectARB(0);
            return;
        }
        if (!useShaders())
            return;


        ARBShaderObjects.glUseProgramObjectARB(shader.getGlName());

//    	if(shader.time != null)
//    		shader.time.set(System.nanoTime()/1000000f);

        if (callback != null)
            callback.call(shader);
    }

    public static <T extends Shader> void useShader(T shader) {
        useShader(shader, null);
    }

    public static void releaseShader() {
        useShader(null);
    }

    public static boolean useShaders() {
        return /*ConfigHandler.useShaders && */OpenGlHelper.shadersSupported;
    }

    private static int createProgram(Shader shader) {
        String vert = shader.getVert();
        String frag = shader.getFrag();

        int vertId = 0, fragId = 0, program = 0;
        String vertText = "[[NONE]]", fragText = "[[NONE]]";
        if (vert != null) {
            try {
                vertText = readFileAsString(vert);
                vertId = createShader(vertText, VERT);
            } catch (Exception e) {
                vertText = "ERROR: \n" + e.toString();
                for (StackTraceElement elem : e.getStackTrace()) {
                    vertText += "\n" + elem.toString();
                }
            }
        }
        if (frag != null) {
            try {
                fragText = readFileAsString(frag);
                fragId = createShader(fragText, FRAG);
            } catch (Exception e) {
                vertText = "ERROR: \n" + e.toString();
                for (StackTraceElement elem : e.getStackTrace()) {
                    vertText += "\n" + elem.toString();
                }
            }
        }

        String logText = ">> VERT: \n```" + vertText + "```\n>> FRAG: \n```" + fragText + "```";

        if(shader.getGlName() != 0)
        	GL20.glDeleteProgram(shader.getGlName()); // Don't know if this works... but uploading it with the same id doesn't.
        program = ARBShaderObjects.glCreateProgramObjectARB();
        if (program == 0)
            return 0;

        if (vert != null)
            ARBShaderObjects.glAttachObjectARB(program, vertId);
        if (frag != null)
            ARBShaderObjects.glAttachObjectARB(program, fragId);

        ARBShaderObjects.glLinkProgramARB(program);
        if (ARBShaderObjects.glGetObjectParameteriARB(program, ARBShaderObjects.GL_OBJECT_LINK_STATUS_ARB) == GL11.GL_FALSE) {
        	LibrarianLog.I.error(getLogInfo(program, logText));
            return 0;
        }

        ARBShaderObjects.glValidateProgramARB(program);
        if (ARBShaderObjects.glGetObjectParameteriARB(program, ARBShaderObjects.GL_OBJECT_VALIDATE_STATUS_ARB) == GL11.GL_FALSE) {
        	LibrarianLog.I.error(getLogInfo(program, logText));
            return 0;
        }
        LibrarianLog.I.info("Created program %d - VERT:'%s' FRAG:'%s'", program, vert, frag);

        shader.init(program);

        return program;
    }

    private static int createShader(String fileText, int shaderType) {
        int shader = 0;
        try {
            shader = ARBShaderObjects.glCreateShaderObjectARB(shaderType);

            if (shader == 0)
                return 0;

            ARBShaderObjects.glShaderSourceARB(shader, fileText);
            ARBShaderObjects.glCompileShaderARB(shader);

            if (ARBShaderObjects.glGetObjectParameteriARB(shader, ARBShaderObjects.GL_OBJECT_COMPILE_STATUS_ARB) == GL11.GL_FALSE) {
                throw new RuntimeException("Error creating shader: " + getLogInfo(shader, ">> CREATING:\n```" + fileText + "```"));
            }

            return shader;
        } catch (Exception e) {
            ARBShaderObjects.glDeleteObjectARB(shader);
            e.printStackTrace();
            return -1;
        }
    }

    // Most of the code taken from the LWJGL wiki
    // http://lwjgl.org/wiki/index.php?title=GLSL_Shaders_with_LWJGL

    private static String getLogInfo(int obj, String fileText) {
        return ARBShaderObjects.glGetInfoLogARB(obj, ARBShaderObjects.glGetObjectParameteriARB(obj, ARBShaderObjects.GL_OBJECT_INFO_LOG_LENGTH_ARB));// + "\n" + fileText;
    }

    private static String readFileAsString(String filename) throws Exception {
        StringBuilder source = new StringBuilder();
        InputStream in = ShaderHelper.class.getResourceAsStream(filename);
        Exception exception = null;
        BufferedReader reader;

        if (in == null)
            return "";

        try {
            reader = new BufferedReader(new InputStreamReader(in, "UTF-8"));

            Exception innerExc = null;
            try {
                String line;
                while ((line = reader.readLine()) != null)
                    source.append(line).append('\n');
            } catch (Exception exc) {
                exception = exc;
            } finally {
                try {
                    reader.close();
                } catch (Exception exc) {
                    if (innerExc == null)
                        innerExc = exc;
                    else exc.printStackTrace();
                }
            }

            if (innerExc != null)
                throw innerExc;
        } catch (Exception exc) {
            exception = exc;
        } finally {
            try {
                in.close();
            } catch (Exception exc) {
                if (exception == null)
                    exception = exc;
                else exc.printStackTrace();
            }

            if (exception != null)
                throw exception;
        }

        return source.toString();
    }

    @SubscribeEvent
    public void reloadShaders(LivingJumpEvent event) {
        if (!event.getEntity().worldObj.isRemote)
            return;
    }

    @Override
    public void onResourceManagerReload(IResourceManager resourceManager) {
        if(Const.isDev)
        	initShaders();
    }

}
