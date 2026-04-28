# 🎮 Talos - Java 3D Game Engine

<p align="center">
  <img src="https://img.shields.io/badge/Java-17+-red.svg?style=for-the-badge&logo=java" alt="Java 17+" />
  <img src="https://img.shields.io/badge/LWJGL-3.3.1-orange.svg?style=for-the-badge" alt="LWJGL 3.3.1" />
  <img src="https://img.shields.io/badge/OpenGL-3.3+-blue.svg?style=for-the-badge&logo=opengl" alt="OpenGL 3.3+" />
  <img src="https://img.shields.io/badge/Maven-3.8+-green.svg?style=for-the-badge&logo=apachemaven" alt="Maven 3.8+" />
</p>

<p align="center">
  <strong>A modern, modular 3D game engine built in Java with OpenGL</strong>
</p>

---

## 📋 Table of Contents

- [Overview](#overview)
- [Features](#features)
- [Architecture](#architecture)
- [Getting Started](#getting-started)
- [Building & Running](#building--running)
- [Project Structure](#project-structure)
- [Modules](#modules)
- [Contributing](#contributing)
- [License](#license)

---

## 🎯 Overview

**Talos** is a 3D game engine written in Java, designed with modularity and performance in mind. Built on top of LWJGL (Lightweight Java Game Library) and OpenGL, it provides a robust foundation for developing 3D games and interactive applications.

The engine features a component-based architecture, making it easy to extend and customize for various game types - from first-person adventures to simulation games.

---

## ✨ Features

### 🎨 Rendering
- **OpenGL 3.3+** based rendering pipeline
- **Shader-based** rendering with GLSL support
- **Mesh & Model** loading and management
- **Texture** loading and binding
- **Terrain** rendering system
- **UI System** with panels and elements

### 🎭 Animation & Assets
- **Animation system** with keyframes and animators
- **Asset management** with registry and prefabs
- **Socket system** for equipment attachment
- **Asset factory** for dynamic object creation

### 🎮 Game Logic
- **Component-based** game object system
- **Behavior interfaces**: `IUpdateable`, `IRenderable`, `ICollidable`, `IInteractable`
- **Actor system** with transform hierarchy
- **Event system** for decoupled communication
- **Physics** engine integration

### 🖥️ Input & Interaction
- **Input handling** for keyboard and mouse
- **Interactive objects** with selection and interaction
- **Camera system** with movement controls

### 💡 Lighting & Effects
- **Light management** system
- **Multiple light types** support
- **Debug overlay** for development

### 🎵 Audio
- **Audio manager** for sound effects and music

### 💾 Save System
- **Save/Load** functionality for game state

---

## 🏗️ Architecture

```
┌─────────────────────────────────────────────────────────────┐
│                        Game Layer                           │
│  (Game, Player, Objects, SaveManager, InteractableManager)  │
└─────────────────────────────────────────────────────────────┘
                              ▼
┌─────────────────────────────────────────────────────────────┐
│                       Engine Core                           │
│  (Engine, Window, Scene, Renderer, Physics, Input)         │
└─────────────────────────────────────────────────────────────┘
                              ▼
┌─────────────────────────────────────────────────────────────┐
│                     Systems & Assets                        │
│  (Assets, Animation, Audio, Lighting, UI, Camera)          │
└─────────────────────────────────────────────────────────────┘
                              ▼
┌─────────────────────────────────────────────────────────────┐
│                      Behavior Layer                         │
│  (GameObject, Actor, TransformNode, Interfaces)             │
└─────────────────────────────────────────────────────────────┘
                              ▼
┌─────────────────────────────────────────────────────────────┐
│                   LWJGL / OpenGL Layer                      │
└─────────────────────────────────────────────────────────────┘
```

---

## 🚀 Getting Started

### Prerequisites

- **Java JDK 17+** (Java Development Kit)
- **Maven 3.8+** (Build tool)
- **Git** (Version control)

### Clone the Repository

```bash
git clone https://github.com/Primordial-Aion/Talos.git
cd Talos
```

---

## 🔨 Building & Running

### Build the Project

```bash
mvn clean compile
```

### Run the Game

```bash
mvn exec:java -Dexec.mainClass="game.Game"
```

### Build JAR

```bash
mvn clean package
```

The JAR will be generated in the `target/` directory.

---

## 📁 Project Structure

```
Talos/
├── src/
│   └── main/
│       ├── java/
│       │   ├── engine/           # Core engine code
│       │   │   ├── animation/    # Animation system
│       │   │   ├── asset/        # Asset management
│       │   │   ├── audio/        # Audio system
│       │   │   ├── behavior/     # Game object behaviors
│       │   │   ├── camera/       # Camera system
│       │   │   ├── core/         # Engine core (Engine, Window)
│       │   │   ├── debug/        # Debug tools
│       │   │   ├── entity/       # Entity system
│       │   │   ├── input/        # Input handling
│       │   │   ├── lighting/     # Lighting system
│       │   │   ├── math/         # Math utilities
│       │   │   ├── model/        # Model/Mesh loading
│       │   │   ├── physics/      # Physics engine
│       │   │   ├── render/       # Rendering system
│       │   │   ├── scene/        # Scene management
│       │   │   ├── shader/       # Shader system
│       │   │   ├── terrain/      # Terrain rendering
│       │   │   ├── texture/      # Texture management
│       │   │   ├── ui/           # UI system
│       │   │   └── util/         # Utilities
│       │   └── game/             # Game implementation
│       │       ├── objects/      # Game objects
│       │       ├── player/       # Player system
│       │       └── save/         # Save system
│       └── resources/
│           └── shaders/          # GLSL shaders
├── target/                       # Build output (ignored)
├── user_notes/                   # Documentation
├── pom.xml                       # Maven configuration
└── README.md                     # This file
```

---

## 🧩 Modules

### Engine Core (`engine.core`)
- **Engine**: Main game loop and initialization
- **Window**: OpenGL window management
- **Scene**: Scene graph and object management

### Asset System (`engine.asset`)
- **AssetLoader**: Loads models, textures, and metadata
- **AssetRegistry**: Manages registered assets
- **PrefabManager**: Handles prefabricated objects
- **SocketSystem**: Equipment attachment system
- **AssetFactory**: Dynamic asset creation

### Behavior System (`engine.behavior`)
- **GameObject**: Base game object class
- **Actor**: Interactive game entity
- **TransformNode**: Hierarchical transformations
- **Interfaces**: `IUpdateable`, `IRenderable`, `ICollidable`, `IInteractable`, `IAnimatable`, `ISelectable`, `ISavable`

### Rendering (`engine.render`)
- **Renderer**: Main rendering pipeline
- **Shader & ShaderProgram**: GLSL shader management
- **Mesh & Model**: 3D model handling

### Utilities (`engine.util`)
- **Config**: Configuration management
- **Constants**: Game constants
- **Logger**: Logging system
- **FileUtils**: File operations

---

## 🎮 Game Implementation

The `game/` package contains a sample game implementation:

- **Game**: Main game class
- **Player**: Player character with movement and interaction
- **Objects**: `Building`, `Crop`, `InteractableObject`
- **InteractableManager**: Manages interactive objects
- **SaveManager**: Handles game state persistence

---

## 🤝 Contributing

Contributions are welcome! Here's how you can help:

1. **Fork** the repository
2. **Create** a feature branch (`git checkout -b feature/AmazingFeature`)
3. **Commit** your changes (`git commit -m 'Add some AmazingFeature'`)
4. **Push** to the branch (`git push origin feature/AmazingFeature`)
5. **Open** a Pull Request

### Development Guidelines

- Follow Java naming conventions
- Add comments for complex logic
- Test your changes before submitting
- Update documentation as needed

---

## 📝 License

This project is licensed under the MIT License - see the LICENSE file for details.

---

## 🙏 Acknowledgments

- **LWJGL Team** for the excellent Java game library
- **OpenGL** for the rendering API
- **Maven** for dependency management

---

## 📧 Contact

- **Repository**: [github.com/Primordial-Aion/Talos](https://github.com/Primordial-Aion/Talos)
- **Issues**: [Report bugs here](https://github.com/Primordial-Aion/Talos/issues)

---

<p align="center">
  <strong>Built with ❤️ using Java and OpenGL</strong>
</p>
