package game.objects;

import engine.entity.Entity;
import engine.model.Model;
import org.joml.Vector3f;

public class Building extends InteractableObject {
    private String buildingType;
    private boolean isOpen;
    private Vector3f doorPosition;
    private Vector3f doorSize;
    
    public Building() {
        super();
        this.buildingType = "barn";
        this.isOpen = false;
    }
    
    public Building(Model model, Vector3f position, String type) {
        super(model, type);
        this.setPosition(position);
        this.buildingType = type;
        this.isOpen = false;
        this.setInteractionRange(5.0f);
        this.setActionPrompt("Press E to " + (isOpen ? "close" : "enter"));
    }
    
    @Override
    protected void onInteract() {
        isOpen = !isOpen;
        this.setActionPrompt("Press E to " + (isOpen ? "close" : "enter"));
    }
    
    public void open() {
        isOpen = true;
    }
    
    public void close() {
        isOpen = false;
    }
    
    public boolean isOpen() {
        return isOpen;
    }
    
    public String getBuildingType() {
        return buildingType;
    }
    
    public void setDoorPosition(Vector3f position) {
        this.doorPosition = position;
    }
    
    public Vector3f getDoorPosition() {
        return doorPosition;
    }
    
    public void setDoorSize(Vector3f size) {
        this.doorSize = size;
    }
    
    public Vector3f getDoorSize() {
        return doorSize;
    }
}