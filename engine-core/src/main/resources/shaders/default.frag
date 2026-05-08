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
uniform vec3 objectColor;

void main() {
    vec3 normal = normalize(normal_out);
    vec3 lightDirection = normalize(-lightDir);

    float diff = max(dot(normal, lightDirection), 0.0);
    vec3 diffuse = diff * lightColor;

    if (useTexture) {
        vec4 texColor = texture(texture0, texCoords_out);
        vec3 ambient = ambientColor * texColor.rgb;
        vec3 lit = ambient + diffuse * texColor.rgb;
        fragColor = vec4(lit, texColor.a);
    } else {
        vec3 ambient = ambientColor * objectColor;
        vec3 lit = ambient + diffuse * objectColor;
        fragColor = vec4(lit, 1.0);
    }
}
