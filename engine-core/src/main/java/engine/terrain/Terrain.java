package engine.terrain;

import engine.model.Mesh;
import engine.texture.Texture;
import engine.util.Constants;
import engine.util.Logger;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import java.util.ArrayList;
import java.util.List;

public class Terrain {
    private static Terrain instance;
    
    private int size;
    private int segments;
    private float heightScale;
    private Mesh mesh;
    private float[][] heightMap;
    private Texture[] textures;
    private Texture blendMap;
    private float[] vertices;
    private int[] indices;
    
    private Terrain() {
        this.size = Constants.DEFAULT_TERRAIN_SIZE;
        this.segments = Constants.DEFAULT_TERRAIN_SEGMENTS;
        this.heightScale = Constants.TERRAIN_HEIGHT_SCALE;
    }
    
    public static Terrain get() {
        if (instance == null) {
            instance = new Terrain();
        }
        return instance;
    }
    
    public void init() {
        Logger.info("Initializing terrain...");
        generateFlatTerrain();
        Logger.info("Terrain initialized: " + size + "x" + size + " with " + segments + " segments");
    }
    
    public void generateFlatTerrain() {
        heightMap = new float[segments + 1][segments + 1];
        for (int z = 0; z <= segments; z++) {
            for (int x = 0; x <= segments; x++) {
                heightMap[x][z] = 0;
            }
        }
        createMesh();
    }
    
    public void generateFromHeightmap(float[] heightData) {
        if (heightData.length != (segments + 1) * (segments + 1)) {
            Logger.warn("Heightmap data size mismatch, using default");
            generateFlatTerrain();
            return;
        }
        
        heightMap = new float[segments + 1][segments + 1];
        for (int z = 0; z <= segments; z++) {
            for (int x = 0; x <= segments; x++) {
                heightMap[x][z] = heightData[z * (segments + 1) + x] * heightScale;
            }
        }
        createMesh();
    }
    
    public void generateNoiseTerrain(long seed) {
        heightMap = new float[segments + 1][segments + 1];
        
        double scale = 0.05;
        double amplitude = 1.0;
        double frequency;
        
        for (int z = 0; z <= segments; z++) {
            for (int x = 0; x <= segments; x++) {
                double height = 0;
                amplitude = heightScale;
                frequency = scale;
                
                for (int octave = 0; octave < 4; octave++) {
                    double nx = x * frequency + seed;
                    double nz = z * frequency + seed;
                    
                    double perlin = noise(nx, nz);
                    height += perlin * amplitude;
                    
                    amplitude *= 0.5;
                    frequency *= 2.0;
                }
                
                heightMap[x][z] = (float) height;
            }
        }
        createMesh();
    }
    
    private double noise(double x, double z) {
        int xi = (int) Math.floor(x);
        int zi = (int) Math.floor(z);
        double xf = x - xi;
        double zf = z - zi;
        
        double smooth = xf * xf * (3 - 2 * xf);
        double smoothZ = zf * zf * (3 - 2 * zf);
        
        int n00 = (xi * 1619 + zi * 31337);
        int n10 = ((xi + 1) * 1619 + zi * 31337);
        int n01 = (xi * 1619 + (zi + 1) * 31337);
        int n11 = ((xi + 1) * 1619 + (zi + 1) * 31337);
        
        double x00 = hash(n00) * 2 - 1;
        double x10 = hash(n10) * 2 - 1;
        double x01 = hash(n01) * 2 - 1;
        double x11 = hash(n11) * 2 - 1;
        
        double i1 = lerp(x00, x10, smooth);
        double i2 = lerp(x01, x11, smooth);
        
        return lerp(i1, i2, smoothZ);
    }
    
    private double hash(int n) {
        n = (n << 13) ^ n;
        return 1.0 - ((n * (n * n * 15731 + 789221) + 1376312589) & 0x7fffffff) / 1073741824.0;
    }
    
    private double lerp(double a, double b, double t) {
        return a + t * (b - a);
    }
    
    private void createMesh() {
        List<Float> verticesList = new ArrayList<>();
        List<Integer> indicesList = new ArrayList<>();
        
        float segmentSize = (float) size / segments;
        float halfSize = size / 2.0f;
        
        int vertexIndex = 0;
        
        for (int z = 0; z <= segments; z++) {
            for (int x = 0; x <= segments; x++) {
                float xPos = x * segmentSize - halfSize;
                float zPos = z * segmentSize - halfSize;
                float yPos = getHeightAt(x, z);
                
                float u = (float) x / segments;
                float v = (float) z / segments;
                
                verticesList.add(xPos);
                verticesList.add(yPos);
                verticesList.add(zPos);
                
                Vector3f normal = calculateNormal(x, z);
                verticesList.add(normal.x);
                verticesList.add(normal.y);
                verticesList.add(normal.z);
                
                verticesList.add(u);
                verticesList.add(v);
                
                if (x < segments && z < segments) {
                    // Triangle 1: (x, z), (x+1, z), (x, z+1) - Counter-clockwise
                    indicesList.add(vertexIndex);
                    indicesList.add(vertexIndex + 1);
                    indicesList.add(vertexIndex + segments + 1);
                    
                    // Triangle 2: (x+1, z), (x+1, z+1), (x, z+1) - Counter-clockwise
                    indicesList.add(vertexIndex + 1);
                    indicesList.add(vertexIndex + segments + 2);
                    indicesList.add(vertexIndex + segments + 1);
                }
                
                vertexIndex++;
            }
        }
        
        vertices = new float[verticesList.size()];
        for (int i = 0; i < verticesList.size(); i++) {
            vertices[i] = verticesList.get(i);
        }
        
        indices = new int[indicesList.size()];
        for (int i = 0; i < indicesList.size(); i++) {
            indices[i] = indicesList.get(i);
        }
        
        if (mesh != null) {
            mesh.cleanup();
        }
        
        mesh = Mesh.create(vertices, indices);
    }
    
    private Vector3f calculateNormal(int x, int z) {
        float height = getHeightAt(x, z);
        float heightLeft = x > 0 ? getHeightAt(x - 1, z) : height;
        float heightRight = x < segments ? getHeightAt(x + 1, z) : height;
        float heightUp = z > 0 ? getHeightAt(x, z - 1) : height;
        float heightDown = z < segments ? getHeightAt(x, z + 1) : height;
        
        float stepSize = (float) size / segments;
        
        float nx = (heightLeft - heightRight) / (2 * stepSize);
        float ny = 1.0f;
        float nz = (heightUp - heightDown) / (2 * stepSize);
        
        Vector3f normal = new Vector3f(nx, ny, nz);
        normal.normalize();
        return normal;
    }
    
    public float getHeightAt(float x, float z) {
        if (heightMap == null) return 0;
        
        float halfSize = size / 2.0f;
        x += halfSize;
        z += halfSize;
        
        float segmentSize = (float) size / segments;
        
        float gridX = x / segmentSize;
        float gridZ = z / segmentSize;
        
        int x0 = (int) gridX;
        int z0 = (int) gridZ;
        int x1 = Math.min(x0 + 1, segments);
        int z1 = Math.min(z0 + 1, segments);
        
        if (x0 < 0 || x0 > segments || z0 < 0 || z0 > segments) {
            return 0;
        }
        
        float xf = gridX - x0;
        float zf = gridZ - z0;
        
        float h00 = heightMap[x0][z0];
        float h10 = heightMap[x1][z0];
        float h01 = heightMap[x0][z1];
        float h11 = heightMap[x1][z1];
        
        float h0 = lerp(h00, h10, xf);
        float h1 = lerp(h01, h11, xf);
        
        return lerp(h0, h1, zf);
    }
    
    public float getTerrainHeight(float worldX, float worldZ) {
        return getHeightAt(worldX, worldZ);
    }
    
    public Vector2f worldToTerrainCoords(float worldX, float worldZ) {
        float halfSize = size / 2.0f;
        float segmentSize = (float) size / segments;
        
        float terrainX = (worldX + halfSize) / segmentSize;
        float terrainZ = (worldZ + halfSize) / segmentSize;
        
        return new Vector2f(terrainX, terrainZ);
    }
    
    public void setTextures(Texture[] textures) {
        this.textures = textures;
    }
    
    public void setBlendMap(Texture blendMap) {
        this.blendMap = blendMap;
    }
    
    public void render() {
        if (mesh != null) {
            if (textures != null && blendMap != null) {
                for (int i = 0; i < Math.min(textures.length, 3); i++) {
                    if (textures[i] != null) {
                        textures[i].bind(i);
                    }
                }
                blendMap.bind(3);
            }
            mesh.render();
        }
    }
    
    public Mesh getMesh() {
        return mesh;
    }
    
    public int getSize() {
        return size;
    }
    
    public int getSegments() {
        return segments;
    }
    
    public float getHeightScale() {
        return heightScale;
    }
    
    public float[][] getHeightMap() {
        return heightMap;
    }
    
    private float lerp(float a, float b, float t) {
        return a + t * (b - a);
    }
}