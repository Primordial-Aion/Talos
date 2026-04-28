package engine.asset;

import org.joml.Vector3f;
import org.joml.Matrix4f;
import engine.behavior.GameObject;
import engine.util.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SocketSystem {
    private static SocketSystem instance;
    
    private Map<Long, Map<String, Socket>> sockets;
    private Map<Long, GameObject> socketOwners;
    
    private SocketSystem() {
        this.sockets = new HashMap<>();
        this.socketOwners = new HashMap<>();
    }
    
    public static SocketSystem get() {
        if (instance == null) {
            instance = new SocketSystem();
        }
        return instance;
    }
    
    public void registerSocket(GameObject owner, String socketName, Vector3f localPosition, Vector3f localRotation) {
        long ownerId = owner.getId();
        
        if (!sockets.containsKey(ownerId)) {
            sockets.put(ownerId, new HashMap<>());
        }
        
        Socket socket = new Socket(socketName, localPosition, localRotation);
        sockets.get(ownerId).put(socketName, socket);
        socketOwners.put(ownerId, owner);
        
        Logger.debug("Registered socket: " + socketName + " on " + owner.getName());
    }
    
    public Socket getSocket(GameObject owner, String socketName) {
        Map<String, Socket> ownerSockets = sockets.get(owner.getId());
        if (ownerSockets != null) {
            return ownerSockets.get(socketName);
        }
        return null;
    }
    
    public List<String> getSocketNames(GameObject owner) {
        Map<String, Socket> ownerSockets = sockets.get(owner.getId());
        if (ownerSockets != null) {
            return new ArrayList<>(ownerSockets.keySet());
        }
        return new ArrayList<>();
    }
    
    public boolean hasSocket(GameObject owner, String socketName) {
        Map<String, Socket> ownerSockets = sockets.get(owner.getId());
        return ownerSockets != null && ownerSockets.containsKey(socketName);
    }
    
    public GameObject attachToSocket(GameObject parent, String socketName, GameObject child) {
        Socket socket = getSocket(parent, socketName);
        if (socket == null) {
            Logger.warn("Socket not found: " + socketName + " on " + parent.getName());
            return null;
        }
        
        Vector3f worldPos = socket.getWorldPosition(parent.getWorldPosition());
        child.setPosition(worldPos);
        child.setTransformParent(parent);
        
        Logger.info("Attached " + child.getName() + " to socket " + socketName + " on " + parent.getName());
        return child;
    }
    
    public GameObject detachFromSocket(GameObject parent, String socketName, GameObject child) {
        child.setTransformParent(null);
        return child;
    }
    
    public static class Socket {
        public String name;
        public Vector3f localPosition;
        public Vector3f localRotation;
        
        public Socket(String name, Vector3f position, Vector3f rotation) {
            this.name = name;
            this.localPosition = position;
            this.localRotation = rotation;
        }
        
        public Vector3f getWorldPosition(Vector3f parentWorldPos) {
            return new Vector3f(parentWorldPos).add(localPosition);
        }
        
        public Vector3f getWorldRotation(Vector3f parentWorldRot) {
            return new Vector3f(parentWorldRot).add(localRotation);
        }
    }
}