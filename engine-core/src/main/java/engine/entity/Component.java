package engine.entity;

public abstract class Component {
    protected Entity entity;
    private boolean initialized = false;

    public void setEntity(Entity entity) {
        this.entity = entity;
    }

    public Entity getEntity() {
        return entity;
    }

    public void init() {
        if (!initialized) {
            start();
            initialized = true;
        }
    }

    public abstract void start();

    public abstract void update(float deltaTime);

    public void cleanup() {
    }
}
