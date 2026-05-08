# How To Add Entity-Entity Interaction

## 1. Goal

Entity-entity interaction means something happens when two game objects relate to each other. The most common interactions are:

| Interaction | Example |
| --- | --- |
| Collection | Player touches coin; score increases; coin disappears or respawns. |
| Damage | Enemy touches player; player health decreases. |
| Blocking | Player touches wall; movement is stopped. |
| Trigger | Player enters area; door opens or level changes. |
| Pickup | Player touches item; inventory changes. |

The engine has an `ICollidable` interface, but the current practical path for small 2D-style games is to calculate distances between entity positions yourself. This is simple, predictable, and easy to test.

## 2. Required Imports

```java
import engine.entity.Entity;
import org.joml.Vector3f;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
```

What each import does:

| Import | Purpose |
| --- | --- |
| `Entity` | Gives access to object positions with `getPosition()` and lets you move/show/hide objects. |
| `Vector3f` | Stores world positions. Use X/Z for 2D distance. |
| `ArrayList` | Stores coins, enemies, projectiles, or pickups. |
| `Iterator` | Safely removes items while looping. |
| `List` | Lets systems accept any list implementation. |

## 3. Distance-Based Collision

For a top-down 2D game, ignore Y and compare X/Z distance.

```java
private boolean touches(Entity a, float radiusA, Entity b, float radiusB) {
    Vector3f pa = a.getPosition();
    Vector3f pb = b.getPosition();

    float dx = pa.x - pb.x;
    float dz = pa.z - pb.z;
    float radius = radiusA + radiusB;

    return dx * dx + dz * dz <= radius * radius;
}
```

Why use squared distance?

The normal distance formula uses a square root. You do not need that square root to compare distances. Squared distance is faster and exact enough for collision checks.

## 4. Player Collecting Coins

Start with data fields:

```java
private Entity player;
private final List<Entity> coins = new ArrayList<>();
private int score = 0;

private static final float PLAYER_RADIUS = 0.8f;
private static final float COIN_RADIUS = 0.35f;
```

Check collection in your game `update` method:

```java
private void updateCoinCollection() {
    Iterator<Entity> iterator = coins.iterator();
    while (iterator.hasNext()) {
        Entity coin = iterator.next();

        if (touches(player, PLAYER_RADIUS, coin, COIN_RADIUS)) {
            score++;
            coin.setVisible(false);
            iterator.remove();
        }
    }
}
```

Call it after movement:

```java
@Override
protected void update(float deltaTime) {
    updatePlayer(deltaTime);
    updateCoinCollection();
    scene.update(deltaTime);
}
```

Important order:

1. Move the player.
2. Resolve interactions.
3. Update/render the scene.

If you check collection before movement, a coin touched this frame may not be collected until next frame.

## 5. Player Collecting Respawning Food

If you want one food item to respawn instead of disappearing:

```java
private Entity food;
private int score = 0;

private void updateFoodCollection() {
    if (!touches(player, 0.8f, food, 0.4f)) {
        return;
    }

    score++;
    food.setPosition(randomFoodPositionAwayFromPlayer());
}
```

Example respawn:

```java
private Vector3f randomFoodPositionAwayFromPlayer() {
    float max = 23.6f;

    for (int attempt = 0; attempt < 100; attempt++) {
        float x = randomRange(-max, max);
        float z = randomRange(-max, max);
        Vector3f candidate = new Vector3f(x, 0.4f, z);

        float dx = candidate.x - player.getPosition().x;
        float dz = candidate.z - player.getPosition().z;
        if (dx * dx + dz * dz > 3.0f * 3.0f) {
            return candidate;
        }
    }

    return new Vector3f(-max, 0.4f, -max);
}

private float randomRange(float min, float max) {
    return min + (float) Math.random() * (max - min);
}
```

The "away from player" rule prevents the food from respawning already collected.

## 6. Enemy Hurting Player

Use health and a damage cooldown. Without a cooldown, the enemy may damage the player every frame.

```java
private int playerHealth = 3;
private float damageCooldown = 0.0f;
private final List<Entity> enemies = new ArrayList<>();

private void updateEnemyDamage(float deltaTime) {
    damageCooldown = Math.max(0.0f, damageCooldown - deltaTime);

    if (damageCooldown > 0.0f) {
        return;
    }

    for (Entity enemy : enemies) {
        if (touches(player, 0.8f, enemy, 0.8f)) {
            playerHealth--;
            damageCooldown = 1.0f;
            break;
        }
    }
}
```

Suggested damage cooldowns:

| Game feel | Cooldown |
| --- | --- |
| Very forgiving | `1.5f` seconds |
| Standard arcade | `1.0f` seconds |
| Harsh | `0.35f` seconds |

## 7. Blocking Walls

Blocking is different from collection. For blocking, calculate the intended next position first, reject it if it touches a wall, and only then set the entity position.

```java
private boolean canMoveTo(Vector3f nextPosition, List<Entity> walls) {
    Vector3f original = player.getPosition();
    player.setPosition(nextPosition);

    boolean blocked = false;
    for (Entity wall : walls) {
        if (touches(player, 0.8f, wall, 1.0f)) {
            blocked = true;
            break;
        }
    }

    player.setPosition(original);
    return !blocked;
}
```

Then use:

```java
Vector3f next = player.getPosition();
next.x += input.x * speed * deltaTime;
next.z += input.y * speed * deltaTime;

if (canMoveTo(next, walls)) {
    player.setPosition(next);
}
```

For more precise collision, use rectangle or axis-aligned bounding box checks instead of circular radius checks.

## 8. Interaction System Pattern

As games grow, do not spread interaction code everywhere. Make a small system class:

```java
public class InteractionSystem {
    public boolean touches(Entity a, float radiusA, Entity b, float radiusB) {
        Vector3f pa = a.getPosition();
        Vector3f pb = b.getPosition();
        float dx = pa.x - pb.x;
        float dz = pa.z - pb.z;
        float radius = radiusA + radiusB;
        return dx * dx + dz * dz <= radius * radius;
    }
}
```

Use it from your game:

```java
private final InteractionSystem interactions = new InteractionSystem();

if (interactions.touches(player, 0.8f, coin, 0.35f)) {
    score++;
}
```

This makes your game easier to test because interaction rules are no longer hidden inside rendering code.

## 9. Checklist

Before adding an interaction, decide:

| Decision | Example |
| --- | --- |
| Who initiates it? | Player touches coin. |
| What shape is used? | Circular radius on X/Z. |
| What changes? | Score increments and coin hides. |
| Can it happen repeatedly? | Coins: no. Enemy damage: yes, but cooldown. |
| When is it checked? | After movement, before scene update/render. |
| Does it need UI feedback? | Score display, health display, sound, flash. |

