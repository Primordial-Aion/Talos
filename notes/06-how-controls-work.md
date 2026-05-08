# How Controls Work

## 1. Goal

Controls in this engine are handled by `engine.input.Input`. GLFW sends keyboard and mouse events to `Input`, and your game reads those states inside `update(float deltaTime)`.

The engine loop does this order:

1. Poll GLFW events.
2. Call your game's `update(deltaTime)`.
3. Call your game's `render()`.
4. Swap buffers.
5. Reset one-frame input states.

This means `isKeyPressed` is valid during the same frame after a key goes down.

## 2. Required Imports

```java
import engine.input.Input;
import org.lwjgl.glfw.GLFW;
```

What each import does:

| Import | Purpose |
| --- | --- |
| `Input` | Reads keyboard keys, mouse buttons, cursor position, cursor delta, scroll, and action maps. |
| `GLFW` | Provides cursor mode constants such as `GLFW.GLFW_CURSOR_DISABLED`. |

You only need `GLFW` if you are changing cursor mode or using raw GLFW constants directly. Most keyboard constants are already exposed through `Input.Keys`.

## 3. Key State Methods

| Method | True when | Use for |
| --- | --- | --- |
| `Input.isKeyPressed(key)` | The key went down this frame. | Toggle pause, open menu, single action. |
| `Input.isKeyHeld(key)` | The key is currently down. | Movement, charging, holding aim. |
| `Input.isKeyReleased(key)` | The key went up this frame. | Release attack, end drag. |

Example:

```java
if (Input.isKeyPressed(Input.Keys.ESCAPE)) {
    stop();
}

if (Input.isKeyHeld(Input.Keys.W)) {
    moveForward(deltaTime);
}
```

Do not use `isKeyPressed` for continuous movement. It only fires for one frame.

## 4. Common Key Constants

The engine exposes keys through `Input.Keys`.

| Key | Constant |
| --- | --- |
| W | `Input.Keys.W` |
| A | `Input.Keys.A` |
| S | `Input.Keys.S` |
| D | `Input.Keys.D` |
| Arrow up | `Input.Keys.UP` |
| Arrow down | `Input.Keys.DOWN` |
| Arrow left | `Input.Keys.LEFT` |
| Arrow right | `Input.Keys.RIGHT` |
| Space | `Input.Keys.SPACE` |
| Escape | `Input.Keys.ESCAPE` |
| Left Shift | `Input.Keys.LEFT_SHIFT` |
| Left Control | `Input.Keys.LEFT_CONTROL` |

## 5. Movement Controls

For a top-down 2D game:

```java
private float inputAxisX() {
    float axis = 0.0f;

    if (Input.isKeyHeld(Input.Keys.A) || Input.isKeyHeld(Input.Keys.LEFT)) {
        axis -= 1.0f;
    }
    if (Input.isKeyHeld(Input.Keys.D) || Input.isKeyHeld(Input.Keys.RIGHT)) {
        axis += 1.0f;
    }

    return axis;
}

private float inputAxisZ() {
    float axis = 0.0f;

    if (Input.isKeyHeld(Input.Keys.W) || Input.isKeyHeld(Input.Keys.UP)) {
        axis -= 1.0f;
    }
    if (Input.isKeyHeld(Input.Keys.S) || Input.isKeyHeld(Input.Keys.DOWN)) {
        axis += 1.0f;
    }

    return axis;
}
```

Then normalize diagonal movement:

```java
Vector2f input = new Vector2f(inputAxisX(), inputAxisZ());
if (input.lengthSquared() > 1.0f) {
    input.normalize();
}
```

Required import for the vector:

```java
import org.joml.Vector2f;
```

## 6. Action Mapping

The input class can map a named action to a key:

```java
Input.mapAction("JUMP", Input.Keys.SPACE);
Input.mapAction("PAUSE", Input.Keys.ESCAPE);
```

Then read actions:

```java
if (Input.isActionPressed("PAUSE")) {
    togglePause();
}

if (Input.isActionActive("JUMP")) {
    holdJump();
}
```

Use action mapping when you want game code to say what the player is doing instead of which physical key is pressed.

Current limitation: each action maps to one key in the built-in `Input` class. If you want multiple keys for one action, check both keys manually or extend the input system.

## 7. Mouse Buttons

Mouse buttons are exposed through `Input.Mouse`.

| Mouse input | Constant |
| --- | --- |
| Left click | `Input.Mouse.LEFT` |
| Right click | `Input.Mouse.RIGHT` |
| Middle click | `Input.Mouse.MIDDLE` |

Example:

```java
if (Input.isMouseButtonPressed(Input.Mouse.LEFT)) {
    fireProjectile();
}

if (Input.isMouseButtonHeld(Input.Mouse.RIGHT)) {
    holdAim();
}
```

Mouse methods mirror keyboard methods:

| Method | Meaning |
| --- | --- |
| `isMouseButtonPressed` | Button went down this frame. |
| `isMouseButtonHeld` | Button is currently down. |
| `isMouseButtonReleased` | Button went up this frame. |

## 8. Mouse Position And UI Clicks

Mouse position is in window pixels:

```java
double mx = Input.getMouseX();
double my = Input.getMouseY();
```

Use it for UI hit tests:

```java
boolean insideButton = mx >= 20.0 && mx <= 140.0
        && my >= 120.0 && my <= 158.0;

if (insideButton && Input.isMouseButtonPressed(Input.Mouse.LEFT)) {
    resetGame();
}
```

Remember: UI Y increases downward from the top of the window.

## 9. Mouse Delta And Cursor Lock

Mouse delta is useful for camera rotation:

```java
double dx = Input.getMouseDeltaX();
double dy = Input.getMouseDeltaY();
```

For camera-look controls, lock the cursor:

```java
getWindow().setCursorMode(GLFW.GLFW_CURSOR_DISABLED);
Input.setMouseLocked(true);
```

To unlock:

```java
getWindow().setCursorMode(GLFW.GLFW_CURSOR_NORMAL);
Input.setMouseLocked(false);
```

The engine's `Window.setCursorMode(...)` already syncs `Input.setMouseLocked(...)`, so usually call only:

```java
getWindow().setCursorMode(GLFW.GLFW_CURSOR_DISABLED);
```

## 10. Scroll Input

Scroll values are one-frame values:

```java
double scrollY = Input.getScrollDY();
if (scrollY != 0.0) {
    zoomCamera(scrollY);
}
```

Like key press states, scroll resets after each frame.

## 11. Control Update Checklist

| Need | Use |
| --- | --- |
| Continuous movement | `isKeyHeld` |
| One-time toggle | `isKeyPressed` |
| Release action | `isKeyReleased` |
| UI click | Mouse X/Y plus `isMouseButtonPressed` |
| Camera look | Mouse delta plus cursor disabled mode |
| Zoom | Scroll delta |
| Rebindable concepts | `Input.mapAction` |

## 12. Common Mistakes

| Mistake | Result | Fix |
| --- | --- | --- |
| Using `isKeyPressed` for movement | Player moves one tiny step. | Use `isKeyHeld`. |
| Not multiplying by `deltaTime` | Movement speed depends on FPS. | Use `speed * deltaTime`. |
| Not normalizing diagonal input | Diagonal movement is faster. | Normalize vectors longer than 1. |
| Reading mouse delta without cursor lock | Delta may be inconsistent for camera control. | Use disabled cursor mode. |
| Checking UI Y as bottom-up | Click zones feel wrong. | UI Y starts at top and increases downward. |

