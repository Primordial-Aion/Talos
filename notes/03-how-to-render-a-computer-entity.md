# How To Render A Computer Entity

## 1. Goal

A computer entity is an entity whose behavior is controlled by an algorithm instead of the keyboard. Examples:

| Entity type | Algorithm |
| --- | --- |
| Enemy | Chase the player. |
| Patrol guard | Move between waypoints. |
| Food spawner | Move or respawn at timed intervals. |
| Neutral animal | Wander randomly. |
| Projectile | Move in a fixed direction until it hits something. |

Rendering a computer entity is the same as rendering a player entity. The difference is where its movement vector comes from.

## 2. Required Imports

```java
import engine.entity.Component;
import engine.entity.Entity;
import engine.model.Mesh;
import engine.model.Model;
import engine.scene.Scene;
import org.joml.Vector3f;
import java.util.List;
import java.util.Random;
```

What each import does:

| Import | Purpose |
| --- | --- |
| `Component` | Lets you attach AI behavior to an entity. |
| `Entity` | Stores the computer-controlled object's transform and model. |
| `Mesh` | Creates cube or quad geometry. |
| `Model` | Gives the entity a mesh and color. |
| `Scene` | Adds and updates the entity. |
| `Vector3f` | Stores positions, directions, and scales. |
| `List` | Useful for patrol waypoints. |
| `Random` | Useful for wandering behavior and respawn positions. |

## 3. Creating A Computer Entity

```java
private Entity createEnemy(Scene scene) {
    Model enemyModel = new Model(Mesh.createCube());
    enemyModel.setColor(0.45f, 0.14f, 0.78f);

    Entity enemy = new Entity(enemyModel);
    enemy.setName("enemy");
    enemy.setScale(new Vector3f(0.7f, 0.7f, 0.7f));
    enemy.setPosition(new Vector3f(10.0f, 0.7f, 10.0f));

    scene.addEntity(enemy);
    return enemy;
}
```

This renders the enemy. It does not move yet.

## 4. Chase Behavior

The most common computer behavior is "move toward the player."

```java
public class ChaseTargetComponent extends Component {
    private final Entity target;
    private final float speed;
    private final float stopDistance;

    public ChaseTargetComponent(Entity target, float speed, float stopDistance) {
        this.target = target;
        this.speed = speed;
        this.stopDistance = stopDistance;
    }

    @Override
    public void start() {
    }

    @Override
    public void update(float deltaTime) {
        Vector3f position = entity.getPosition();
        Vector3f targetPosition = target.getPosition();

        Vector3f direction = new Vector3f(
                targetPosition.x - position.x,
                0.0f,
                targetPosition.z - position.z
        );

        if (direction.length() <= stopDistance) {
            return;
        }

        direction.normalize();
        position.x += direction.x * speed * deltaTime;
        position.z += direction.z * speed * deltaTime;

        entity.setPosition(position);
    }
}
```

Attach it:

```java
enemy.addComponent(new ChaseTargetComponent(player, 5.0f, 1.2f));
```

Call `scene.update(deltaTime)` in the game update loop. The scene updates the enemy component.

## 5. Patrol Behavior

Use patrol behavior when an entity should follow fixed points.

```java
public class PatrolComponent extends Component {
    private final List<Vector3f> waypoints;
    private final float speed;
    private int targetIndex = 0;

    public PatrolComponent(List<Vector3f> waypoints, float speed) {
        this.waypoints = waypoints;
        this.speed = speed;
    }

    @Override
    public void start() {
    }

    @Override
    public void update(float deltaTime) {
        if (waypoints.isEmpty()) {
            return;
        }

        Vector3f position = entity.getPosition();
        Vector3f target = waypoints.get(targetIndex);
        Vector3f direction = new Vector3f(target).sub(position);
        direction.y = 0.0f;

        if (direction.length() < 0.25f) {
            targetIndex = (targetIndex + 1) % waypoints.size();
            return;
        }

        direction.normalize();
        position.x += direction.x * speed * deltaTime;
        position.z += direction.z * speed * deltaTime;
        entity.setPosition(position);
    }
}
```

Attach it:

```java
enemy.addComponent(new PatrolComponent(List.of(
        new Vector3f(-10.0f, 0.7f, -10.0f),
        new Vector3f(10.0f, 0.7f, -10.0f),
        new Vector3f(10.0f, 0.7f, 10.0f),
        new Vector3f(-10.0f, 0.7f, 10.0f)
), 4.0f));
```

## 6. Random Wander Behavior

Random wandering uses a timer and a random direction.

```java
public class WanderComponent extends Component {
    private final Random random = new Random();
    private final float speed;
    private final float boardHalfExtent;
    private final float entityHalfSize;
    private Vector3f direction = new Vector3f(1.0f, 0.0f, 0.0f);
    private float timeUntilTurn = 0.0f;

    public WanderComponent(float speed, float boardHalfExtent, float entityHalfSize) {
        this.speed = speed;
        this.boardHalfExtent = boardHalfExtent;
        this.entityHalfSize = entityHalfSize;
    }

    @Override
    public void start() {
        chooseNewDirection();
    }

    @Override
    public void update(float deltaTime) {
        timeUntilTurn -= deltaTime;
        if (timeUntilTurn <= 0.0f) {
            chooseNewDirection();
        }

        Vector3f position = entity.getPosition();
        position.x += direction.x * speed * deltaTime;
        position.z += direction.z * speed * deltaTime;

        float max = boardHalfExtent - entityHalfSize;
        if (position.x < -max || position.x > max || position.z < -max || position.z > max) {
            chooseNewDirection();
        }

        position.x = Math.max(-max, Math.min(max, position.x));
        position.z = Math.max(-max, Math.min(max, position.z));
        entity.setPosition(position);
    }

    private void chooseNewDirection() {
        float angle = random.nextFloat() * (float) Math.PI * 2.0f;
        direction.set((float) Math.cos(angle), 0.0f, (float) Math.sin(angle));
        timeUntilTurn = 0.5f + random.nextFloat() * 1.5f;
    }
}
```

## 7. Algorithm Design Checklist

Computer entities should usually answer these questions:

| Question | Example answer |
| --- | --- |
| What is the target? | Player, waypoint, coin, random point. |
| How fast can it move? | `5.0f` world units per second. |
| Can it leave the board? | Usually no, so clamp X/Z. |
| Does it stop near the target? | Enemies may stop at attack range. |
| Does it need timers? | Patrol wait time, attack cooldown, random turn time. |
| Does it interact with the player? | Damage, block, collect, talk, trigger event. |

## 8. Keep AI And Rendering Separate

The entity's model and color are rendering concerns. The movement algorithm is behavior. Keep them separated:

```java
Entity enemy = createEnemy(scene);
enemy.addComponent(new ChaseTargetComponent(player, 5.0f, 1.2f));
```

This lets you reuse the same algorithm with different rendered models.

