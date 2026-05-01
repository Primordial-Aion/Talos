package game.objects;

import engine.entity.Entity;
import engine.scene.Scene;
import engine.util.Logger;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

public class InteractableManager {
    private static InteractableManager instance;
    
    private List<InteractableObject> interactables;
    private InteractableObject currentTarget;
    
    private InteractableManager() {
        this.interactables = new ArrayList<>();
        this.currentTarget = null;
    }
    
    public static InteractableManager get() {
        if (instance == null) {
            instance = new InteractableManager();
        }
        return instance;
    }
    
    public void init() {
        Logger.info("Initializing interactable manager...");
        interactables.clear();
        Logger.info("Interactable manager initialized");
    }
    
    public void addInteractable(InteractableObject interactable) {
        interactables.add(interactable);
    }
    
    public void removeInteractable(InteractableObject interactable) {
        interactables.remove(interactable);
    }
    
    public void update(Vector3f playerPos) {
        currentTarget = null;
        float closestDist = Float.MAX_VALUE;
        
        for (InteractableObject obj : interactables) {
            if (!obj.canInteract()) continue;
            
            float dist = obj.getPosition().distance(playerPos);
            if (dist <= obj.getInteractionRange() && dist < closestDist) {
                closestDist = dist;
                currentTarget = obj;
            }
        }
    }
    
    public void interact() {
        if (currentTarget != null) {
            currentTarget.interact();
        }
    }
    
    public InteractableObject getCurrentTarget() {
        return currentTarget;
    }
    
    public List<InteractableObject> getInteractables() {
        return interactables;
    }
    
    public List<InteractableObject> getInteractablesInRange(Vector3f pos, float range) {
        List<InteractableObject> result = new ArrayList<>();
        for (InteractableObject obj : interactables) {
            if (obj.getPosition().distance(pos) <= range) {
                result.add(obj);
            }
        }
        return result;
    }
    
    public void cleanup() {
        interactables.clear();
        currentTarget = null;
    }
}