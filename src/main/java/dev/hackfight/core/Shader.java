package dev.hackfight.core;

import org.joml.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.lwjgl.opengl.GL30C.*;

public class Shader {
    public int ID;

    public Shader(Path vertexPath, Path fragmentPath) throws IOException {
        // retrieve source code
        String vertexSrc = Files.readString(vertexPath, StandardCharsets.UTF_8);
        String fragSrc = Files.readString(fragmentPath, StandardCharsets.UTF_8);

        // compile shaders
        int vertexShader = glCreateShader(GL_VERTEX_SHADER);
        glShaderSource(vertexShader, vertexSrc);
        glCompileShader(vertexShader);

        int fragmentShader = glCreateShader(GL_FRAGMENT_SHADER);
        glShaderSource(fragmentShader, fragSrc);
        glCompileShader(fragmentShader);

        int status = glGetShaderi(ID, GL_COMPILE_STATUS);
        if (status != GL_TRUE) {
            throw new RuntimeException(glGetShaderInfoLog(ID));
        }

        // shader program
        int shaderProgram = glCreateProgram();
        glAttachShader(shaderProgram, vertexShader);
        glAttachShader(shaderProgram, fragmentShader);
        glLinkProgram(shaderProgram);

        status = glGetProgrami(shaderProgram, GL_LINK_STATUS);
        if (status != GL_TRUE) {
            throw new RuntimeException(glGetProgramInfoLog(shaderProgram));
        }

        glDeleteShader(vertexShader);
        glDeleteShader(fragmentShader);
    }

    public void Bind() {
        glUseProgram(ID);
    }

    public void set3f(String name, float v1, float v2, float v3) {
        glUniform3f(glGetUniformLocation(ID, name), v1, v2, v3);
    }

    public void setMat4(String name, Matrix4f mat4) {
        glUniformMatrix4fv(glGetUniformLocation(ID, name), false, mat4.getBuffer());
    }
}
