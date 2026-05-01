#version 330 core
out vec4 fragColor;

in vec2 texCoords_out;

uniform sampler2D texture0;
uniform vec4 color;

void main() {
    vec4 texColor = texture(texture0, texCoords_out);
    fragColor = texColor * color;
}