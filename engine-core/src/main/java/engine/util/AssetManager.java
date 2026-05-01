package engine.util;

import engine.model.Mesh;
import engine.model.Model;
import engine.texture.Texture;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import org.joml.Vector2f;
import org.joml.Vector3f;

public class AssetManager {
    public static Model loadModel(String objPath) {
        try {
            List<Vector3f> positions = new ArrayList<>();
            List<Vector2f> texCoords = new ArrayList<>();
            List<Vector3f> normals = new ArrayList<>();
            List<Float> verticesList = new ArrayList<>();
            List<Integer> indicesList = new ArrayList<>();

            InputStream in = AssetManager.class.getClassLoader().getResourceAsStream(objPath);
            BufferedReader reader;
            if (in != null) {
                reader = new BufferedReader(new InputStreamReader(in));
            } else {
                reader = new BufferedReader(new FileReader(objPath));
            }
            
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("v ")) {
                    String[] tokens = line.split("\\s+");
                    positions.add(new Vector3f(Float.parseFloat(tokens[1]), Float.parseFloat(tokens[2]), Float.parseFloat(tokens[3])));
                } else if (line.startsWith("vt ")) {
                    String[] tokens = line.split("\\s+");
                    texCoords.add(new Vector2f(Float.parseFloat(tokens[1]), Float.parseFloat(tokens[2])));
                } else if (line.startsWith("vn ")) {
                    String[] tokens = line.split("\\s+");
                    normals.add(new Vector3f(Float.parseFloat(tokens[1]), Float.parseFloat(tokens[2]), Float.parseFloat(tokens[3])));
                } else if (line.startsWith("f ")) {
                    String[] tokens = line.split("\\s+");
                    for (int i = 1; i <= 3; i++) {
                        String[] vertexTokens = tokens[i].split("/");
                        int posIndex = Integer.parseInt(vertexTokens[0]) - 1;
                        int texIndex = vertexTokens.length > 1 && !vertexTokens[1].isEmpty() ? Integer.parseInt(vertexTokens[1]) - 1 : -1;
                        int normIndex = vertexTokens.length > 2 ? Integer.parseInt(vertexTokens[2]) - 1 : -1;
                        
                        Vector3f pos = positions.get(posIndex);
                        verticesList.add(pos.x);
                        verticesList.add(pos.y);
                        verticesList.add(pos.z);
                        
                        if (normIndex >= 0) {
                            Vector3f norm = normals.get(normIndex);
                            verticesList.add(norm.x);
                            verticesList.add(norm.y);
                            verticesList.add(norm.z);
                        } else {
                            verticesList.add(0f); verticesList.add(1f); verticesList.add(0f);
                        }
                        
                        if (texIndex >= 0) {
                            Vector2f tex = texCoords.get(texIndex);
                            verticesList.add(tex.x);
                            verticesList.add(tex.y);
                        } else {
                            verticesList.add(0f); verticesList.add(0f);
                        }
                        
                        indicesList.add(indicesList.size());
                    }
                }
            }
            reader.close();
            
            float[] vertices = new float[verticesList.size()];
            for (int i = 0; i < verticesList.size(); i++) {
                vertices[i] = verticesList.get(i);
            }
            
            int[] indices = new int[indicesList.size()];
            for (int i = 0; i < indicesList.size(); i++) {
                indices[i] = indicesList.get(i);
            }
            
            Mesh mesh = Mesh.create(vertices, indices);
            return new Model(mesh);
        } catch (Exception e) {
            Logger.error("Failed to load model " + objPath + ": " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
}
