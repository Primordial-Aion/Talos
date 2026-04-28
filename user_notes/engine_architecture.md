java-game-engine/
├── java_game_engine_module_requirements_full.md
├── pom.xml
├── upgrade.md
├── USER_MANUAL.md
├── user_notes/
│   └── engine_architecture.md
├── src/
│   └── main/
│       ├── java/
│       │   └── engine/
│       │       ├── core/
│       │       │   ├── Engine.java
│       │       │   └── Window.java
│       │       ├── input/
│       │       │   └── Input.java
│       │       ├── math/
│       │       │   └── MathUtils.java
│       │       ├── render/
│       │       │   └── Renderer.java
│       │       ├── shader/
│       │       │   ├── Shader.java
│       │       │   └── ShaderProgram.java
│       │       ├── texture/
│       │       │   └── Texture.java
│       │       ├── model/
│       │       │   ├── Model.java
│       │       │   ├── Mesh.java
│       │       │   └── ModelLoader.java
│       │       ├── camera/
│       │       │   └── Camera.java
│       │       ├── entity/
│       │       │   └── Entity.java
│       │       ├── terrain/
│       │       │   └── Terrain.java
│       │       ├── lighting/
│       │       │   ├── Light.java
│       │       │   └── LightManager.java
│       │       ├── physics/
│       │       │   └── Physics.java
│       │       ├── scene/
│       │       │   └── Scene.java
│       │       ├── ui/
│       │       │   ├── UIElement.java
│       │       │   ├── UIPanel.java
│       │       │   └── UIManager.java
│       │       ├── audio/
│       │       │   └── AudioManager.java
│       │       ├── animation/
│       │       │   ├── Animation.java
│       │       │   ├── Animator.java
│       │       │   └── Keyframe.java
│       │       ├── debug/
│       │       │   └── DebugOverlay.java
│       │       ├── util/
│       │       │   ├── Config.java
│       │       │   ├── Constants.java
│       │       │   ├── FileUtils.java
│       │       │   └── Logger.java
│       │       ├── behavior/
│       │       │   ├── Actor.java
│       │       │   ├── Event.java
│       │       │   ├── GameObject.java
│       │       │   ├── TransformNode.java
│       │       │   ├── IAnimatable.java
│       │       │   ├── ICollidable.java
│       │       │   ├── IInteractable.java
│       │       │   ├── IRenderable.java
│       │       │   ├── ISavable.java
│       │       │   ├── ISelectable.java
│       │       │   └── IUpdateable.java
│       │       └── asset/
│       │           ├── AssetFactory.java
│       │           ├── AssetLoader.java
│       │           ├── AssetRegistry.java
│       │           ├── PrefabManager.java
│       │           └── SocketSystem.java
│       └── game/
│           ├── Game.java
│           ├── player/
│           │   └── Player.java
│           ├── objects/
│           │   ├── Building.java
│           │   ├── Crop.java
│           │   ├── InteractableManager.java
│           │   └── InteractableObject.java
│           └── save/
│               └── SaveManager.java
│       └── resources/
│           └── shaders/
│               ├── default.vert
│               ├── default.frag
│               ├── terrain.vert
│               ├── terrain.frag
│               ├── ui.vert
│               └── ui.frag
└── target/
    └── (build output)