#version 330 core
out vec4 fragColor;

in vec2 texCoords_out;

uniform sampler2D texture0;
uniform vec4 color;
uniform bool useTexture;

void main() {
    vec4 texColor = useTexture ? texture(texture0, texCoords_out) : vec4(1.0);
    fragColor = texColor * color;
}
