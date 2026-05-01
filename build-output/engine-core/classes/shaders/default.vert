#version 330 core
layout(location = 0) in vec3 position;
layout(location = 1) in vec3 normal;
layout(location = 2) in vec2 texCoords;

uniform mat4 model;
uniform mat4 view;
uniform mat4 projection;

out vec3 fragPos;
out vec3 normal_out;
out vec2 texCoords_out;

void main() {
    fragPos = vec3(model * vec4(position, 1.0));
    normal_out = mat3(transpose(inverse(model))) * normal;
    texCoords_out = texCoords;
    gl_Position = projection * view * vec4(fragPos, 1.0);
}