#version 330 core
out vec4 fragColor;

in vec3 fragPos;
in vec3 normal_out;
in vec2 texCoords_out;

uniform vec3 lightDir;
uniform vec3 lightColor;
uniform vec3 ambientColor;
uniform sampler2D texture0;
uniform bool useTexture;

void main() {
    vec3 normal = normalize(normal_out);
    vec3 lightDirection = normalize(lightDir);
    
    float diff = max(dot(normal, lightDirection), 0.0);
    vec3 diffuse = diff * lightColor;
    
    vec3 result = (ambientColor + diffuse);
    
    if (useTexture) {
        vec4 texColor = texture(texture0, texCoords_out);
        fragColor = vec4(result, 1.0) * texColor;
    } else {
        fragColor = vec4(result, 1.0);
    }
}