package dev.hackfight;

import dev.hackfight.core.*;
import dev.hackfight.physics2d.Particle;
import dev.hackfight.physics2d.ParticlePhysicsWorld;
import org.joml.*;
import org.lwjgl.*;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import org.lwjgl.system.*;

import java.io.IOException;
import java.lang.Math;
import java.nio.*;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Timer;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.system.MemoryUtil.*;

public class Bounce {

    // The window handle
    private long window;

    private Timer timer;

    private Shader defaultShader;
    private Shader billboardShader;
    private Model triangle;

    private ParticlePhysicsWorld physWorld;

    public void run() {
        System.out.println("Hello LWJGL " + Version.getVersion() + "!");

        init();
        try {
            loadAssets();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        loop();

        // Free the window callbacks and destroy the window
        glfwFreeCallbacks(window);
        glfwDestroyWindow(window);

        // Terminate GLFW and free the error callback
        glfwTerminate();
        glfwSetErrorCallback(null).free();
    }

    private void init() {
        // Setup an error callback. The default implementation
        // will print the error message in System.err.
        GLFWErrorCallback.createPrint(System.err).set();

        // Initialize GLFW. Most GLFW functions will not work before doing this.
        if ( !glfwInit() )
            throw new IllegalStateException("Unable to initialize GLFW");

        // Configure GLFW
        glfwDefaultWindowHints(); // optional, the current window hints are already the default
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE); // the window will stay hidden after creation
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE); // the window will be resizable
        glfwWindowHint(GLFW_OPENGL_DEBUG_CONTEXT, GLFW_TRUE);

        // Create the window
        window = glfwCreateWindow(300, 300, "Hello World!", NULL, NULL);
        if ( window == NULL )
            throw new RuntimeException("Failed to create the GLFW window");

        // Setup a frame buffer resize callback
        glfwSetFramebufferSizeCallback(window, this::framebuffer_size_callback);

        // Setup a key callback. It will be called every time a key is pressed, repeated or released.
        glfwSetKeyCallback(window, (window, key, scancode, action, mods) -> {
            if ( key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE )
                glfwSetWindowShouldClose(window, true); // We will detect this in the rendering loop
        });

        // Get the thread stack and push a new frame
        try ( MemoryStack stack = stackPush() ) {
            IntBuffer pWidth = stack.mallocInt(1); // int*
            IntBuffer pHeight = stack.mallocInt(1); // int*

            // Get the window size passed to glfwCreateWindow
            glfwGetWindowSize(window, pWidth, pHeight);

            // Get the resolution of the primary monitor
            GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());

            if (OsValidator.isWindows()) {
                // Center the window
                glfwSetWindowPos(
                        window,
                        (vidmode.width() - pWidth.get(0)) / 2,
                        (vidmode.height() - pHeight.get(0)) / 2
                );
            }
        } // the stack frame is popped automatically

        // Make the OpenGL context current
        glfwMakeContextCurrent(window);

        GL.createCapabilities();
        Callback debugProc = GLUtil.setupDebugMessageCallback(); // may return null if the debug mode is not available
        // Enable v-sync
        glfwSwapInterval(1);

        // Make the window visible
        glfwShowWindow(window);
    }

    private void loop() {
        GL.createCapabilities();

        glClearColor(0.270588235f, 0.2823529411764706f, 0.4235294117647059f, 0.0f); //#45486C

        defaultShader.bind();
        triangle.bind();

        physWorld = new ParticlePhysicsWorld();

        double lastLoopTime = glfwGetTime();
        double timeCount = 0.0;
        float secondTicker = 0f;

        ArrayList<ParticleObject> objects = new ArrayList<>();
        objects.add(new ParticleObject(triangle, billboardShader));
        for (ParticleObject object : objects) {
            physWorld.addParticle(object.particle);
        }

        while ( !glfwWindowShouldClose(window) ) {
            double time = glfwGetTime();
            float delta = (float) (time - lastLoopTime);
            float fps = 1f/delta;
            lastLoopTime = time;
            timeCount += delta;
            secondTicker += delta;
            if (secondTicker >= 1f) {
                secondTicker = 0f;
                System.out.println(fps);
            }
            delta = Math.clamp(delta, 0f, 1f/30f); //Clamp delta to 30FPS to avoid glitches in case of lag spike, or `delta=0.0` at start.


            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear the framebuffer

            physWorld.step(delta);

            for (ParticleObject object : objects) {
                object.render();
            }

            glfwSwapBuffers(window); // swap the color buffers
            glfwPollEvents();
        }
    }

    private void loadAssets() throws IOException {
        // shaders
        defaultShader = new Shader(Path.of("src/main/resources/shaders/default.vert"), Path.of("src/main/resources/shaders/circle.frag"));
        billboardShader = new Shader(Path.of("src/main/resources/shaders/billboard.vert"), Path.of("src/main/resources/shaders/circle.frag"));

        // models
        Model.Vertex[] vertices = {
                new Model.Vertex(new Vector3f(-1.73205f, -1f, 0f), new Vector2f(0f, 0f), new Vector3f(1f, 0f, 0f)),
                new Model.Vertex(new Vector3f(1.73205f, -1f, 0f), new Vector2f(0.5f, 1f), new Vector3f(0f, 1f, 0f)),
                new Model.Vertex(new Vector3f(0f, 2f, 0f), new Vector2f(1f, 0f), new Vector3f(0f, 0f, 1f))
        };

        int[] indices = {
                0, 1, 2
        };

        triangle = new Model(vertices, indices);
    }

    private void framebuffer_size_callback(long window, int width, int height)
    {
        // make sure the viewport matches the new window dimensions; note that width and
        // height will be significantly larger than specified on retina displays.
        glViewport(0, 0, width, height);
        float ratio = (float) width/ (float) height;
        if (width > height) {
            Camera.getInstance().setCameraSize(-25f, 25f, -25f*(1/ratio), 25f*(1/ratio), 0.1f, 10f);
        } else {
            Camera.getInstance().setCameraSize(-25f*ratio, 25f*ratio, -25f, 25f, 0.1f, 10f);
        }
    }

    public static void main(String[] args) {
        //System.loadLibrary("renderdoc");

        try {
            new Bounce().run();
        } catch (RuntimeException e) {
            System.out.println(e.getMessage());
        }
    }

}