# IdleRSC Patcher

> **Bytecode patching module for IdleRSC**
> Adds hooks and features to the OpenRSC client using ASM.

## Overview

The **patcher** module is responsible for modifying the OpenRSC client JAR to inject hooks, interfaces, and other features required by IdleRSC.
It uses the [ASM](https://asm.ow2.io/) bytecode manipulation library to patch both classes and methods, enabling advanced botting and scripting capabilities.

## Features

- **Injects hooks** for botting, scripting, and UI overlays
- Adds new fields, methods, and interfaces to the client
- Supports both class-level and method-level bytecode adapters
- Modular, testable, and easy to extend

## Directory Structure

```
patcher/
  ├── src/
  │   └── main/java/patcher/
  │        ├── adapters/
  │        │    ├── base/         # Base adapter classes (class/method)
  │        │    ├── classlevel/   # Class-level adapters (e.g. Render3DAdapter)
  │        │    └── methodlevel/  # Method-level adapters (e.g. CommandAdapter)
  │        ├── core/              # Core patching logic (PatchService, ClassManipulator, etc)
  │        ├── config/            # AdapterConfig and related config
  │        └── utils/             # Logging, constants, file utils
  ├── build.gradle
  └── README.md
```

## How It Works

The patcher transforms a vanilla OpenRSC client JAR into a bot-ready client by injecting hooks and features at the bytecode level.

### 1. **Decompression**
- Extracts all class files from the input JAR into a temporary workspace for manipulation.

### 2. **Adapter Application**
- **Class-level adapters** modify entire classes (adding fields, interfaces, methods)
- **Method-level adapters** modify specific methods (injecting hooks, callbacks, logging)
- Adapters are applied based on configuration in `AdapterConfig.java`

### 3. **Bytecode Manipulation**
- Uses the [ASM](https://asm.ow2.io/) library to read and modify Java bytecode
- Inserts, removes, or alters fields and methods while maintaining class validity

### 4. **Repackaging**
- Repackages all modified classes into a new output JAR ready for IdleRSC

### 5. **Logging & Verification**
- Logs all patching operations and provides detailed output in debug mode
- Verifies that all expected modifications were applied successfully

**In summary:** The patcher automates bytecode modification to inject new features and hooks into the OpenRSC client using a modular adapter system.

## Usage

You can run the patcher as a standalone tool or as part of the build process. But generally, this module is used as part of the full build process for IdleRSC, as a Gradle task. Going from core OpenRSC client, using the patcher to add code to client, and then IdleRSC uses reflections to control that client code while it is running.

### Standalone

```sh
java -cp patcher.jar patcher.Main <input-client.jar> <output-patched.jar>
```

### Gradle Task Example

```gradle
task buildPatchedClient(type: JavaExec) {
    dependsOn ':client:build', ':patcher:build'
    mainClass = 'patcher.Main'
    args clientJar, patchedClientJar
}
```

## Configuration

Adapters are registered in `AdapterConfig.java`:

```java
// Class-level adapter example
CLASS_TO_ADAPTER.put("orsc/mudclient", "patcher.adapters.classlevel.Render3DAdapter");

// Method-level adapter example
addMethod("orsc/mudclient", "draw", "()V", "patcher.adapters.methodlevel.GraphicsAdapter");
```

Constants for field/method/class names are in `utils/HookConstants.java`.

## Debugging

Enable debug logging for detailed output:

```java
// In patcher/config/PatcherConfig.java
public static final boolean PATCHER_DEBUG_MODE = true;
```

## Logging Philosophy

The patcher uses a custom `PatchLogger` for all output:

- **INFO/WARNING/ERROR** messages always print. These cover main patching steps, warnings, and errors.
- **DEBUG** messages only print if debug logging is enabled (via `PatchLogger.setShowDebugLogs(true)`). These are for detailed developer output (e.g., skipped methods, adapter creation, etc).

This allows normal users to see only the important output, while developers can enable debug logs for more granular details.

### Example Usage

```java
PatchLogger.logInfo("Patching started");
PatchLogger.logWarning("Some classes were not found");
PatchLogger.logError("Failed to patch class: ...");
// Turn on debug messages on program launch, via PatcherConfig PATCHER_DEBUG_MODE
PatchLogger.logDebug("Adapter created for ...");
```

## Troubleshooting

- **Missing hooks/fields?**
  Check your adapter registration and class/method names in `AdapterConfig`.

- **Build failures?**
  Ensure all adapters and dependencies are up to date and correctly referenced.

- **Unexpected behavior?**
  Use debug logging to trace patching steps and verify adapter application.

## Contributing

- Keep adapters focused and single-purpose
- Add new adapters in the appropriate `adapters/classlevel` or `adapters/methodlevel` directory
- Update `AdapterConfig` to register new adapters
- Write clear commit messages and document new features

## License

See the main project for license details.
