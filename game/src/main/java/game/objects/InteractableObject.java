package game.objects;

import engine.entity.Entity;
import engine.model.Model;
import engine.physics.Physics;
import org.joml.Vector3f;

public class InteractableObject extends Entity {
    private String interactType;
    private float interactionRange = 3.0f;
    private String actionPrompt = "";
    private boolean canInteract = true;
    
    public InteractableObject() {
        super();
        this.interactType = "default";
    }
    
    public InteractableObject(Model model, String interactType) {
        super(model);
        this.interactType = interactType;
        this.actionPrompt = "Press E to interact";
    }
    
    public InteractableObject(Model model, Vector3f position, String interactType) {
        super(model, position);
        this.interactType = interactType;
        this.actionPrompt = "Press E to interact";
    }
    
    public boolean isInRange(Vector3f playerPos) {
        float dist = getPosition().distance(playerPos);
        return dist <= interactionRange;
    }
    
    public void interact() {
        if (canInteract) {
            onInteract();
        }
    }
    
    protected void onInteract() {
    }
    
    public void setInteractType(String type) {
        this.interactType = type;
    }
    
    public String getInteractType() {
        return interactType;
    }
    
    public void setInteractionRange(float range) {
        this.interactionRange = range;
    }
    
    public float getInteractionRange() {
        return interactionRange;
    }
    
    public void setActionPrompt(String prompt) {
        this.actionPrompt = prompt;
    }
    
    public String getActionPrompt() {
        return actionPrompt;
    }
    
    public void setCanInteract(boolean can) {
        this.canInteract = can;
    }
    
    public boolean canInteract() {
        return canInteract;
    }
}