#version 330 core

out vec4 FragColor;

in vec3 vertexColor;
in vec2 fragCoord;

void main() {
    if (distance(fragCoord, vec2(0.0, 0.0)) > 1.0) {
        discard;
    }
    FragColor = vec4(0.9, 0.9, 0.8, 1.0);
}