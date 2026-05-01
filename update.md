# Engine Modularization and JAR Output Instructions

## Goal
Turn the Java game engine into a reusable module that can be imported into other projects, instead of copying the whole project every time.

The current project already separates reusable engine code from game-specific code, with engine packages under `src/main/java/engine/` and game code under `src/main/java/game/`. That is a good starting point for extracting a reusable engine module. fileciteturn0file0

---

# 1. What you should do conceptually

Do not copy the engine into every new game project.

Instead:

- keep the engine in its own project or module
- build it as a JAR
- place the JAR in a known output folder
- import that JAR into your game project as a dependency

This gives you one engine that can power many games.

---

# 2. Recommended project layout

## Option A: Single repository, multi-module Maven
```text
java-game-engine/
  pom.xml
  engine-core/
    pom.xml
    src/main/java/engine/...
    src/main/resources/...
  my-game/
    pom.xml
    src/main/java/game/...
    src/main/resources/...
```

## Option B: Separate repositories
```text
engine-core/
  pom.xml
  src/main/java/engine/...
  src/main/resources/...

my-game/
  pom.xml
  src/main/java/game/...
  src/main/resources/...
```

For long-term maintainability, Option A is easier while you are still developing the engine.

---

# 3. What belongs in the engine module

Move these into `engine-core`:

- `engine/core`
- `engine/input`
- `engine/math`
- `engine/render`
- `engine/shader`
- `engine/texture`
- `engine/model`
- `engine/camera`
- `engine/entity`
- `engine/terrain`
- `engine/lighting`
- `engine/physics`
- `engine/scene`
- `engine/ui`
- `engine/audio`
- `engine/animation`
- `engine/debug`
- `engine/util`
- `engine/behavior`
- `engine/asset`

These are reusable engine systems.

---

# 4. What belongs in the game module

Keep these in your actual game project:

- player class
- crops
- buildings
- interactable objects
- save/load for your specific game state
- farming logic
- inventory rules
- progression rules
- game-specific UI
- game-specific scenes and levels

These are not engine code. They are game code.

---

# 5. Best practice: treat the engine like a library

Your game should import the engine like this:

```java
import engine.core.Engine;
import engine.scene.Scene;
```

and not copy the engine source into the game project.

---

# 6. How to create the JAR in a targeted folder

## The cleanest Maven approach
In the engine project’s `pom.xml`, define the final JAR output folder using the Maven build directory.

Example:

```xml
<build>
    <directory>build-output</directory>
    <finalName>engine-core-1.0</finalName>
</build>
```

This makes Maven write build artifacts to:

```text
build-output/
```

Then the JAR will typically appear as:

```text
build-output/engine-core-1.0.jar
```

---

# 7. If you want the JAR to go into a specific subfolder

You can use a Maven plugin to copy the built JAR after packaging.

## Example using `maven-dependency-plugin`
```xml
<build>
    <plugins>
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-dependency-plugin</artifactId>
            <version>3.6.1</version>
            <executions>
                <execution>
                    <id>copy-jar</id>
                    <phase>package</phase>
                    <goals>
                        <goal>copy</goal>
                    </goals>
                    <configuration>
                        <artifactItems>
                            <artifactItem>
                                <groupId>your.group</groupId>
                                <artifactId>engine-core</artifactId>
                                <version>1.0</version>
                                <type>jar</type>
                                <outputDirectory>${project.basedir}/dist/libs</outputDirectory>
                                <destFileName>engine-core.jar</destFileName>
                            </artifactItem>
                        </artifactItems>
                    </configuration>
                </execution>
            </executions>
        </plugin>
    </plugins>
</build>
```

That will place the JAR in:

```text
dist/libs/engine-core.jar
```

---

# 8. Simple recommended folder choice

Use one of these output folders:

- `dist/`
- `dist/libs/`
- `build-output/`
- `target/engine/`

The cleanest and most readable choice is usually:

```text
dist/libs/
```

because it makes it obvious that the folder contains distributable JARs.

---

# 9. Build commands

From the engine module:

```bash
mvn clean package
```

or if it is a multi-module project:

```bash
mvn clean package -pl engine-core
```

This will compile the engine and create the JAR.

---

# 10. How your game project should use it

In the game project’s `pom.xml`, add the engine as a dependency.

## If installed locally to your Maven repository
```xml
<dependency>
    <groupId>your.group</groupId>
    <artifactId>engine-core</artifactId>
    <version>1.0</version>
</dependency>
```

## If you are using a local file JAR temporarily
```xml
<dependency>
    <groupId>local.engine</groupId>
    <artifactId>engine-core</artifactId>
    <version>1.0</version>
    <scope>system</scope>
    <systemPath>${project.basedir}/libs/engine-core.jar</systemPath>
</dependency>
```

The first option is better. The second is only for quick testing.

---

# 11. Recommended development workflow

## Step 1
Finish engine features in the engine module.

## Step 2
Run:

```bash
mvn clean package
```

## Step 3
Copy or publish the JAR into your target subfolder.

## Step 4
Start the game project and depend on the JAR.

## Step 5
Whenever the engine changes, rebuild the engine JAR and update the game project.

This keeps the architecture clean.

---

# 12. Important design rule

Do not let the game project depend on engine source files.

It should depend on the engine artifact:

- `engine-core.jar`
- or `engine-core` Maven dependency

That is what makes the engine reusable.

---

# 13. Recommended next improvement

After this, the best upgrade is to split the engine API into:

- `engine-core` → base engine runtime
- `engine-editor` → optional tools
- `engine-assets` → import and asset pipeline helpers

That way you can keep the runtime small and only include extra tooling when needed.

---

# 14. Summary

To make the engine modular:

- separate engine code from game code
- build the engine as a JAR
- output the JAR into a chosen folder like `dist/libs/`
- import the JAR into your game project as a dependency
- never copy the engine source into each new game

That gives you a reusable engine that you can plug into any future project.
