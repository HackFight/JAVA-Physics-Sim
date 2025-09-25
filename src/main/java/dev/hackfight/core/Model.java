package dev.hackfight.core;

import org.joml.*;
import org.lwjgl.*;

import static org.lwjgl.opengl.GL30C.*;

public class Model {

    private int vao_;
    private int vbo_;
    private int ebo_;
    private int vertexCount_;
    private int indexCount_;

    public class Vertex {
        Vector3f position;
        Vector2f texCoord;
        Vector3f color;
    }

    public Model(Vertex[] vertices, long[] indices, int indexCount) {
        this.indexCount_ = indexCount;
        createBuffer(vertices, indices);
    }

    public void bind() {
        glBindVertexArray(vao_);
    }

    public void draw() {
        glDrawElements(GL_TRIANGLES, indexCount_, GL_UNSIGNED_INT, 0);
    }

    private void createBuffer(Vertex[] vertices, long[] indices) {
        vertexCount_ = vertices.length;

        vao_ = glGenVertexArrays();
        vbo_ = glGenBuffers();
        ebo_ = glGenBuffers();

        glBindVertexArray(vao_);

        glBindBuffer(GL_ARRAY_BUFFER, vbo_);
        glBufferData(GL_ARRAY_BUFFER, vertices.length * sizeOf(Vertex), vertices.data(), GL_STATIC_DRAW);

        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, m_EBO);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, m_indexCount * sizeof(uint32_t), indices, GL_STATIC_DRAW);

        glVertexAttribPointer(0, 3, GL_FLOAT, GL_FALSE, 8 * sizeof(float), (void*)0);
        glEnableVertexAttribArray(0);
        glVertexAttribPointer(1, 2, GL_FLOAT, GL_FALSE, 8 * sizeof(float), (void*)(3 * sizeof(float)));
        glEnableVertexAttribArray(1);
        glVertexAttribPointer(2, 3, GL_FLOAT, GL_FALSE, 8 * sizeof(float), (void*)(5 * sizeof(float)));
        glEnableVertexAttribArray(2);
    }
}
