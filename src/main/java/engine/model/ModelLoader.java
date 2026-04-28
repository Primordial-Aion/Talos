package engine.model;

import engine.util.Logger;
import engine.util.FileUtils;
import org.joml.Vector2f;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

public class ModelLoader {
    
    public static Model loadObjModel(String path) {
        Logger.info("Loading OBJ model: " + path);
        
        String content = FileUtils.readFileAsString(path);
        String[] lines = content.split("\n");
        
        List<Vector3f> positions = new ArrayList<>();
        List<Vector3f> normals = new ArrayList<>();
        List<Vector2f> texCoords = new ArrayList<>();
        List<ModelLoader.OBJFace> faces = new ArrayList<>();
        
        for (String line : lines) {
            line = line.trim();
            if (line.isEmpty() || line.startsWith("#")) continue;
            
            String[] parts = line.split("\\s+");
            String type = parts[0];
            
            switch (type) {
                case "v" -> {
                    float x = Float.parseFloat(parts[1]);
                    float y = Float.parseFloat(parts[2]);
                    float z = Float.parseFloat(parts[3]);
                    positions.add(new Vector3f(x, y, z));
                }
                case "vn" -> {
                    float x = Float.parseFloat(parts[1]);
                    float y = Float.parseFloat(parts[2]);
                    float z = Float.parseFloat(parts[3]);
                    normals.add(new Vector3f(x, y, z));
                }
                case "vt" -> {
                    float u = Float.parseFloat(parts[1]);
                    float v = Float.parseFloat(parts[2]);
                    texCoords.add(new Vector2f(u, v));
                }
                case "f" -> {
                    int[] posIndices = new int[parts.length - 1];
                    int[] texIndices = new int[parts.length - 1];
                    int[] normIndices = new int[parts.length - 1];
                    
                    for (int i = 1; i < parts.length; i++) {
                        String[] indices = parts[i].split("/");
                        posIndices[i - 1] = Integer.parseInt(indices[0]) - 1;
                        if (indices.length > 1 && !indices[1].isEmpty()) {
                            texIndices[i - 1] = Integer.parseInt(indices[1]) - 1;
                        }
                        if (indices.length > 2) {
                            normIndices[i - 1] = Integer.parseInt(indices[2]) - 1;
                        }
                    }
                    faces.add(new OBJFace(posIndices, texIndices, normIndices));
                }
            }
        }
        
        List<Float> verticesList = new ArrayList<>();
        List<Integer> indicesList = new ArrayList<>();
        int indexOffset = 0;
        
        for (OBJFace face : faces) {
            for (int i = 0; i < face.posIndices.length; i++) {
                int posIdx = face.posIndices[i];
                int texIdx = face.texIndices[i];
                int normIdx = face.normIndices[i];
                
                Vector3f pos = positions.get(posIdx);
                verticesList.add(pos.x);
                verticesList.add(pos.y);
                verticesList.add(pos.z);
                
                if (normIdx >= 0 && normIdx < normals.size()) {
                    Vector3f norm = normals.get(normIdx);
                    verticesList.add(norm.x);
                    verticesList.add(norm.y);
                    verticesList.add(norm.z);
                } else {
                    verticesList.add(0.0f);
                    verticesList.add(1.0f);
                    verticesList.add(0.0f);
                }
                
                if (texIdx >= 0 && texIdx < texCoords.size()) {
                    Vector2f tex = texCoords.get(texIdx);
                    verticesList.add(tex.x);
                    verticesList.add(tex.y);
                } else {
                    verticesList.add(0.0f);
                    verticesList.add(0.0f);
                }
                
                indicesList.add(indexOffset++);
            }
        }
        
        float[] vertices = new float[verticesList.size()];
        for (int i = 0; i < verticesList.size(); i++) {
            vertices[i] = verticesList.get(i);
        }
        
        int[] indices = new int[indicesList.size()];
        for (int i = 0; i < indicesList.size(); i++) {
            indices[i] = indicesList.get(i);
        }
        
        Mesh mesh = Mesh.create(vertices, indices);
        Model model = new Model(mesh);
        
        Logger.info("Loaded OBJ model: " + path + " (" + vertices.length / 8 + " vertices, " + indices.length / 3 + " triangles)");
        return model;
    }
    
    public static Model createModel(Mesh mesh) {
        return new Model(mesh);
    }
    
    private static class OBJFace {
        int[] posIndices;
        int[] texIndices;
        int[] normIndices;
        
        OBJFace(int[] pos, int[] tex, int[] norm) {
            this.posIndices = pos;
            this.texIndices = tex;
            this.normIndices = norm;
        }
    }
}