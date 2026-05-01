package engine.math;

import org.joml.*;

public class MathUtils {
    
    public static Vector3f lerp(Vector3f a, Vector3f b, float t) {
        return new Vector3f(
            a.x + (b.x - a.x) * t,
            a.y + (b.y - a.y) * t,
            a.z + (b.z - a.z) * t
        );
    }
    
    public static float lerp(float a, float b, float t) {
        return a + (b - a) * t;
    }
    
    public static float distance(Vector3f a, Vector3f b) {
        return a.distance(b);
    }
    
    public static float distanceSquared(Vector3f a, Vector3f b) {
        return a.distanceSquared(b);
    }
    
    public static float clamp(float value, float min, float max) {
        return java.lang.Math.max(min, java.lang.Math.min(max, value));
    }
    
    public static float radians(float degrees) {
        return (float) java.lang.Math.toRadians(degrees);
    }
    
    public static float degrees(float radians) {
        return (float) java.lang.Math.toDegrees(radians);
    }
    
    public static Vector3f normalize(Vector3f v) {
        Vector3f result = new Vector3f();
        v.normalize(result);
        return result;
    }
    
    public static float dot(Vector3f a, Vector3f b) {
        return a.x * b.x + a.y * b.y + a.z * b.z;
    }
    
    public static Vector3f cross(Vector3f a, Vector3f b) {
        return a.cross(b, new Vector3f());
    }
    
    public static float magnitude(Vector3f v) {
        return (float) java.lang.Math.sqrt(v.x * v.x + v.y * v.y + v.z * v.z);
    }
    
    public static Vector3f scale(Vector3f v, float scalar) {
        return new Vector3f(v.x * scalar, v.y * scalar, v.z * scalar);
    }
    
    public static Matrix4f createTransformationMatrix(Vector3f translation, Vector3f rotation, Vector3f scale) {
        Matrix4f matrix = new Matrix4f();
        matrix.translate(translation);
        matrix.rotateXYZ(rotation);
        matrix.scale(scale);
        return matrix;
    }
    
    public static Matrix4f createViewMatrix(Vector3f position, Vector3f rotation) {
        Matrix4f viewMatrix = new Matrix4f();
        viewMatrix.identity();
        
        viewMatrix.rotateXYZ(new Vector3f(-rotation.x, -rotation.y, 0));
        
        float cosY = (float) java.lang.Math.cos(java.lang.Math.toRadians(rotation.y));
        float sinY = (float) java.lang.Math.sin(java.lang.Math.toRadians(rotation.y));
        
        viewMatrix.m30(viewMatrix.m30() - (position.x * cosY + position.z * sinY) * -1);
        viewMatrix.m31(viewMatrix.m31() - position.y * -1);
        viewMatrix.m32(viewMatrix.m32() - (position.x * sinY - position.z * cosY) * -1);
        
        return viewMatrix;
    }
    
    public static Matrix4f createPerspectiveProjectionMatrix(float fov, float aspect, float near, float far) {
        Matrix4f matrix = new Matrix4f();
        matrix.perspective(radians(fov), aspect, near, far);
        return matrix;
    }
    
    public static Matrix4f createOrthographicProjectionMatrix(float left, float right, float bottom, float top, float near, float far) {
        Matrix4f matrix = new Matrix4f();
        matrix.ortho(left, right, bottom, top, near, far);
        return matrix;
    }
}