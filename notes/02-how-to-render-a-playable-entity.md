# How To Render A Playable Entity

## 1. Goal

A playable entity is an `Entity` whose position changes based on player input. In this engine, the simplest playable entity is a colored cube added to the scene and moved in `update(float deltaTime)`.

For 2D-style games, move the entity on the X/Z plane:

| Input | Movement |
| --- | --- |
| W or Up | Negative Z |
| S or Down | Positive Z |
| A or Left | Negative X |
| D or Right | Positive X |

## 2. Required Imports

```java
import engine.entity.Entity;
import engine.input.Input;
import engine.model.Mesh;
import engine.model.Model;
import engine.scene.Scene;
import org.joml.Vector2f;
import org.joml.Vector3f;
```

What each import does:

| Import | Purpose |
| --- | --- |
| `Entity` | Stores player position, scale, rotation, and render model. |
| `Input` | Reads keyboard state such as `isKeyHeld(Input.Keys.W)`. |
| `Mesh` | Creates cube or quad geometry. |
| `Model` | Sets the player's mesh and color. |
| `Scene` | Receives the player through `scene.addEntity(player)`. |
| `Vector2f` | Useful for 2D movement math on X/Z. |
| `Vector3f` | Required by engine entity positions and scale. |

JOML vectors are mutable. When you call `new Vector3f(...)`, you create a new vector. When you call `position.add(...)`, the vector itself changes.

## 3. Creating The Player Entity

```java
private static final float PLAYER_HALF_SIZE = 0.8f;

private Entity player;

private void createPlayer(Scene scene) {
    Mesh cubeMesh = Mesh.createCube();

    Model playerModel = new Model(cubeMesh);
    playerModel.setColor(0.95f, 0.28f, 0.22f);

    player = new Entity(playerModel);
    player.setName("player");
    player.setScale(new Vector3f(PLAYER_HALF_SIZE, PLAYER_HALF_SIZE, PLAYER_HALF_SIZE));
    player.setPosition(new Vector3f(0.0f, PLAYER_HALF_SIZE, 0.0f));

    scene.addEntity(player);
}
```

Important details:

| Line | Why it matters |
| --- | --- |
| `Mesh.createCube()` | Gives the player visible 3D volume, even in a 2D top-down scene. |
| `setColor(...)` | Colors the untextured model. |
| `setScale(...)` | A cube mesh goes from -1 to +1 before scale, so scale controls half-size. |
| `Y = PLAYER_HALF_SIZE` | Places the cube on top of a plane at Y = 0. |
| `scene.addEntity(player)` | The entity will be updated and rendered by `Scene`. |

## 4. Reading Movement Input

For movement, prefer `isKeyHeld`, not `isKeyPressed`.

| Method | Meaning | Good for |
| --- | --- | --- |
| `Input.isKeyPressed(key)` | True only on the frame the key went down. | Jump, pause, menu click. |
| `Input.isKeyHeld(key)` | True as long as the key is down. | Continuous movement. |
| `Input.isKeyReleased(key)` | True only on the frame the key went up. | Charge/release actions. |

```java
private Vector2f readMoveInput() {
    Vector2f input = new Vector2f();

    if (Input.isKeyHeld(Input.Keys.A) || Input.isKeyHeld(Input.Keys.LEFT)) {
        input.x -= 1.0f;
    }
    if (Input.isKeyHeld(Input.Keys.D) || Input.isKeyHeld(Input.Keys.RIGHT)) {
        input.x += 1.0f;
    }
    if (Input.isKeyHeld(Input.Keys.W) || Input.isKeyHeld(Input.Keys.UP)) {
        input.y -= 1.0f;
    }
    if (Input.isKeyHeld(Input.Keys.S) || Input.isKeyHeld(Input.Keys.DOWN)) {
        input.y += 1.0f;
    }

    return input;
}
```

This returns X movement in `input.x` and Z movement in `input.y`.

## 5. Applying Movement

Always multiply by `deltaTime`. Without `deltaTime`, faster computers move the player faster.

```java
private static final float PLAYER_SPEED = 12.0f;

private void updatePlayer(float deltaTime) {
    Vector2f input = readMoveInput();

    if (input.lengthSquared() > 1.0f) {
        input.normalize();
    }

    Vector3f position = player.getPosition();
    position.x += input.x * PLAYER_SPEED * deltaTime;
    position.z += input.y * PLAYER_SPEED * deltaTime;

    position = clampToBoard(position);
    player.setPosition(position);
}
```

Why normalize diagonal movement?

If the player holds W and D, the raw movement vector is `(1, -1)`. Its length is about `1.414`, so the player would move 41 percent faster diagonally. Normalizing makes diagonal movement the same speed as straight movement.

## 6. Board Boundaries

```java
private static final float PLANE_HALF_EXTENT = 24.0f;
private static final float PLAYER_HALF_SIZE = 0.8f;

private Vector3f clampToBoard(Vector3f position) {
    float max = PLANE_HALF_EXTENT - PLAYER_HALF_SIZE;
    position.x = clamp(position.x, -max, max);
    position.z = clamp(position.z, -max, max);
    return position;
}

private float clamp(float value, float min, float max) {
    return Math.max(min, Math.min(max, value));
}
```

The player center must stay inside the board by at least the player's half-size. If the plane ends at X = 24 and the player half-size is 0.8, the player center should not go past X = 23.2.

## 7. Component-Based Movement

The engine includes `engine.entity.Component`. Components are useful when you want movement behavior attached directly to an entity.

Additional import:

```java
import engine.entity.Component;
```

Example:

```java
public class PlayerMovementComponent extends Component {
    private final float speed;
    private final float boardHalfExtent;
    private final float playerHalfSize;

    public PlayerMovementComponent(float speed, float boardHalfExtent, float playerHalfSize) {
        this.speed = speed;
        this.boardHalfExtent = boardHalfExtent;
        this.playerHalfSize = playerHalfSize;
    }

    @Override
    public void start() {
    }

    @Override
    public void update(float deltaTime) {
        Vector2f input = new Vector2f();

        if (Input.isKeyHeld(Input.Keys.A) || Input.isKeyHeld(Input.Keys.LEFT)) input.x -= 1.0f;
        if (Input.isKeyHeld(Input.Keys.D) || Input.isKeyHeld(Input.Keys.RIGHT)) input.x += 1.0f;
        if (Input.isKeyHeld(Input.Keys.W) || Input.isKeyHeld(Input.Keys.UP)) input.y -= 1.0f;
        if (Input.isKeyHeld(Input.Keys.S) || Input.isKeyHeld(Input.Keys.DOWN)) input.y += 1.0f;

        if (input.lengthSquared() > 1.0f) {
            input.normalize();
        }

        Vector3f position = entity.getPosition();
        position.x += input.x * speed * deltaTime;
        position.z += input.y * speed * deltaTime;

        float max = boardHalfExtent - playerHalfSize;
        position.x = Math.max(-max, Math.min(max, position.x));
        position.z = Math.max(-max, Math.min(max, position.z));

        entity.setPosition(position);
    }
}
```

Attach it like this:

```java
player.addComponent(new PlayerMovementComponent(12.0f, 24.0f, 0.8f));
```

Then call `scene.update(deltaTime)` from your game update. The scene calls each entity's `update`, and the entity updates its components.

## 8. Recommended Pattern

For small games:

1. Store the player as a field in your game class.
2. Read input in `update`.
3. Move the player with `deltaTime`.
4. Clamp the player to the board.
5. Call `scene.update(deltaTime)`.

For larger games:

1. Put player input and movement in a `Component`.
2. Keep collision and scoring in a separate game state or system class.
3. Keep rendering entities synchronized with game state.

