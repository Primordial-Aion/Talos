package game.objects;

import engine.util.Logger;
import engine.model.Model;
import org.joml.Vector3f;

public class Crop extends InteractableObject {
    public enum CropType {
        WHEAT, CORN, TOMATO, CARROT, POTATO
    }
    
    private CropType cropType;
    private int growthStage;
    private int maxGrowthStage;
    private float growthTimer;
    private float growthTime;
    private boolean fullyGrown;
    private int harvestAmount;
    
    public Crop() {
        super();
        this.cropType = CropType.WHEAT;
        this.growthStage = 0;
        this.maxGrowthStage = 4;
        this.growthTime = 60.0f;
        this.fullyGrown = false;
        this.harvestAmount = 1;
    }
    
    public Crop(Vector3f position, CropType type) {
        super();
        this.setPosition(position);
        this.cropType = type;
        this.growthStage = 0;
        this.maxGrowthStage = 4;
        this.growthTime = getGrowthTimeForType(type);
        this.fullyGrown = false;
        this.harvestAmount = 1;
    }
    
    @Override
    protected void onInteract() {
        if (fullyGrown) {
            harvest();
        }
    }
    
    public void update(float deltaTime) {
        super.update(deltaTime);
        if (!fullyGrown) {
            growthTimer += deltaTime;
            if (growthTimer >= growthTime) {
                growthTimer = 0;
                growthStage++;
                if (growthStage >= maxGrowthStage) {
                    fullyGrown = true;
                    Logger.debug("Crop fully grown: " + cropType);
                }
            }
        }
    }
    
    public void harvest() {
        Logger.info("Harvested " + harvestAmount + " " + cropType);
        reset();
    }
    
    public void reset() {
        growthStage = 0;
        growthTimer = 0;
        fullyGrown = false;
    }
    
    private float getGrowthTimeForType(CropType type) {
        return switch (type) {
            case WHEAT -> 30.0f;
            case CORN -> 45.0f;
            case TOMATO -> 40.0f;
            case CARROT -> 25.0f;
            case POTATO -> 35.0f;
        };
    }
    
    public CropType getCropType() {
        return cropType;
    }
    
    public int getGrowthStage() {
        return growthStage;
    }
    
    public int getMaxGrowthStage() {
        return maxGrowthStage;
    }
    
    public boolean isFullyGrown() {
        return fullyGrown;
    }
    
    public float getGrowthProgress() {
        return (float) growthStage / maxGrowthStage;
    }
}