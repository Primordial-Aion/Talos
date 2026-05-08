# How To Render UI Elements

## 1. Goal

UI elements are 2D rectangles drawn over the 3D/2D world. Use them for:

| UI use | Example |
| --- | --- |
| Score | A top-left score panel. |
| Health | Hearts or bars. |
| Buttons | Reset, pause, inventory slot. |
| Crosshair | Small center marker. |
| Debug panel | FPS or position background. |

The engine's UI system currently renders colored or textured rectangles through `UIElement`, `UIPanel`, and `UIManager`.

## 2. Required Imports

```java
import engine.shader.ShaderProgram;
import engine.ui.UIElement;
import engine.ui.UIManager;
import engine.ui.UIPanel;
import org.joml.Vector2f;
import org.lwjgl.opengl.GL11;
```

What each import does:

| Import | Purpose |
| --- | --- |
| `ShaderProgram` | Provides `ShaderProgram.getUI()` for UI rendering. |
| `UIElement` | A colored or textured rectangle in screen pixels. |
| `UIManager` | Optional manager for named elements and panels. |
| `UIPanel` | Groups multiple UI elements. |
| `Vector2f` | Stores pixel position and size. |
| `GL11` | Useful if you need to manually control depth testing around UI rendering. |

## 3. UI Coordinate System

UI positions are in pixels.

| Coordinate | Meaning |
| --- | --- |
| X = 0 | Left edge of the window. |
| Y = 0 | Top edge of the window. |
| Positive X | Moves right. |
| Positive Y | Moves down. |

Example:

```java
UIElement panel = new UIElement(
        new Vector2f(20.0f, 20.0f),
        new Vector2f(180.0f, 54.0f)
);
```

This creates a rectangle 20 pixels from the left, 20 pixels from the top, 180 pixels wide, and 54 pixels tall.

## 4. Creating A Basic HUD Rectangle

```java
private UIElement scorePanel;

private void createHud() {
    scorePanel = new UIElement(new Vector2f(18.0f, 18.0f), new Vector2f(210.0f, 58.0f));
    scorePanel.setColor(0.02f, 0.03f, 0.04f, 0.78f);
}
```

Color values are RGBA floats:

| Channel | Meaning |
| --- | --- |
| R | Red, `0.0f` to `1.0f` |
| G | Green, `0.0f` to `1.0f` |
| B | Blue, `0.0f` to `1.0f` |
| A | Alpha/opacity, `0.0f` transparent to `1.0f` solid |

## 5. Rendering UI

Render UI after rendering the scene:

```java
@Override
protected void render() {
    Renderer.get().beginFrame();

    scene.render(ShaderProgram.getDefault(), view, projection);

    ShaderProgram uiShader = ShaderProgram.getUI();
    if (uiShader != null) {
        scorePanel.render(uiShader);
    }

    Renderer.get().endFrame();
}
```

The `UIElement.render` method sets an orthographic projection internally using the current window size, so you do not need to provide your own UI projection matrix.

## 6. Using UIManager

`UIManager` stores named elements:

```java
UIManager.get().init();

UIElement healthBar = new UIElement(new Vector2f(20.0f, 84.0f), new Vector2f(160.0f, 18.0f));
healthBar.setColor(0.9f, 0.1f, 0.1f, 1.0f);

UIManager.get().addElement("healthBar", healthBar);
```

Render all manager UI:

```java
ShaderProgram uiShader = ShaderProgram.getUI();
UIManager.get().render(uiShader);
```

Use `UIManager` when many systems need to find UI by name. For small games, direct fields are also fine.

## 7. Health Bar Example

Use two rectangles: background and fill.

```java
private UIElement healthBackground;
private UIElement healthFill;
private int maxHealth = 5;
private int currentHealth = 5;

private void createHealthBar() {
    healthBackground = new UIElement(new Vector2f(20.0f, 84.0f), new Vector2f(170.0f, 22.0f));
    healthBackground.setColor(0.05f, 0.05f, 0.06f, 0.85f);

    healthFill = new UIElement(new Vector2f(24.0f, 88.0f), new Vector2f(162.0f, 14.0f));
    healthFill.setColor(0.9f, 0.12f, 0.12f, 1.0f);
}

private void updateHealthBar() {
    float ratio = (float) currentHealth / Math.max(1, maxHealth);
    healthFill.setSize(162.0f * ratio, 14.0f);
}

private void renderHealthBar(ShaderProgram uiShader) {
    healthBackground.render(uiShader);
    healthFill.render(uiShader);
}
```

Call `updateHealthBar()` when health changes, not necessarily every frame.

## 8. Score Digits Without A Font

The current engine has rectangle UI, not a text renderer. You can still display numbers by composing seven-segment digits from small rectangles.

Each digit has seven bars:

```text
  0
5   1
  6
4   2
  3
```

For example, digit `8` shows all seven segments. Digit `1` shows only segments 1 and 2.

Create one segment:

```java
private UIElement segment(float x, float y, float width, float height) {
    UIElement element = new UIElement(new Vector2f(x, y), new Vector2f(width, height));
    element.setColor(0.95f, 0.98f, 1.0f, 1.0f);
    return element;
}
```

Then create seven segments per digit and set each segment visible or hidden depending on the score. This is how a simple score HUD can be implemented without adding a font library.

## 9. Buttons And Mouse Hit Tests

The UI system draws rectangles. Button behavior is handled manually with input.

Required additional import:

```java
import engine.input.Input;
```

Example:

```java
private UIElement resetButton;

private void createResetButton() {
    resetButton = new UIElement(new Vector2f(20.0f, 120.0f), new Vector2f(120.0f, 38.0f));
    resetButton.setColor(0.2f, 0.6f, 1.0f, 0.9f);
}

private void updateResetButton() {
    if (!Input.isMouseButtonPressed(Input.Mouse.LEFT)) {
        return;
    }

    double mx = Input.getMouseX();
    double my = Input.getMouseY();

    boolean inside = mx >= 20.0 && mx <= 140.0 && my >= 120.0 && my <= 158.0;
    if (inside) {
        resetGame();
    }
}
```

## 10. UI Checklist

| Question | Recommendation |
| --- | --- |
| Should UI be behind the game? | No. Render it after the scene. |
| Should UI use world units? | No. UI uses pixels. |
| How do I show text? | Use rectangle-based digits or add a font/text system later. |
| How do I update bars? | Change `UIElement.setSize(...)`. |
| How do I hide UI? | Use `UIElement.setVisible(false)`. |
| How do I click UI? | Compare mouse X/Y to the rectangle bounds. |

