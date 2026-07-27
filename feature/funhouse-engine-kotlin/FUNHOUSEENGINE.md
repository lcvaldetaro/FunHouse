# FunHouse Engine (gepetto.gensrv) Module Documentation

The `funhouse-engine` module is a core feature of the **FunHouse** application. It implements a fully-featured, multiplayer-capable, scriptable text adventure game engine written in C/C++, wrapped using Java Native Interface (JNI) to be run inside the Android app. 

The engine currently powers two games:
1. **Fun House** (`funhouse`): A thrilling adventure game with entertainment, traps, and puzzles where the goal is to escape alive.
2. **Lost Island** (`island`): An adventure game where the player survives a plane crash on a lost island and seeks to escape with the maximum amount of treasure.

---

## Architectural Overview

The engine acts as a bridge between the Android application code (Kotlin) and the high-performance native parser and multiplayer host (C/C++). Below is the system flow and interaction diagram:

```mermaid
graph TD
    %% Kotlin Layer
    subgraph Kotlin Layer (Frontend)
        A[Android UI / Terminal View] <--> B[com.funhouse.shared.common.jni.BaseNativeGame]
        B <--> C["jni.Gengame (Gengame.kt)"]
        D["funhouseengine.utils.Utils (Utils.kt)"] -.->|Installs assets & setups Defaults| C
    end

    %% JNI Bridge
    subgraph JNI Bridge
        C <-->|JNI Calls / JVM Callbacks| E["srvgenandroid.c (JNI Bindings)"]
    end

    %% C Engine Layer
    subgraph Native Game Engine
        E <-->|Controls Loop / Commands| F["gengame.c (Game Mechanics & Parser)"]
        F <-->|Reads Data| G["architecture.c (CSV Data Loader)"]
        F <-->|Network Messages| H["srvgencomms.c (Multiplayer Connection Host)"]
        H <-->|Sockets / Cryptography| I["tcpip.c (TCP & Security Layer)"]
        
        %% Helpers & DB
        F -.->|Abstracted OS / Mutex / Lists| J["iolib.c (OS Abstraction Library)"]
        I -.->|Abstracted OS / Mutex / Lists| J
        J -.->|Persistence / Multi-user Tracking| K["sqlite3.h (SQLite Core)"]
    end
    
    classDef kotlin fill:#A7F3D0,stroke:#047857,stroke-width:2px;
    classDef native fill:#FED7AA,stroke:#C2410C,stroke-width:2px;
    classDef bridge fill:#F3E8FF,stroke:#7E22CE,stroke-width:2px;
    
    class A,B,C,D kotlin;
    class E bridge;
    class F,G,H,I,J,K native;
```

---

## Source File Directory & Descriptions

### 1. Kotlin API & Wrappers (`/src/main/java`)

*   #### [Gengame.kt](file:///Users/luizvaldetaro/valdetaro/FunHouse/feature/funhouse-engine/src/main/java/jni/Gengame.kt)
    *   **Purpose**: The main Kotlin wrapper that handles lifecycle actions for the native library. It extends `BaseNativeGame()`.
    *   **Key Responsibilities**:
        *   Loads the native library `libgepetto.gensrv.so` via `System.loadLibrary()`.
        *   Launches and feeds user command sequences into the game using [sendCommand](file:///Users/luizvaldetaro/valdetaro/FunHouse/feature/funhouse-engine/src/main/java/jni/Gengame.kt#L40) and the native `sendLibCommand()`.
        *   Declares JNI bindings such as `loadLibGame()`, `setPlayerInfo()`, and `vocabulary()`.
        *   Maintains the callback method `writeTerminalData()` which redirects engine stdout/messages back to the UI terminal.

*   #### [Utils.kt](file:///Users/luizvaldetaro/valdetaro/FunHouse/feature/funhouse-engine/src/main/java/utils/Utils.kt)
    *   **Purpose**: Bootstrapping and setup utility class.
    *   **Key Responsibilities**:
        *   Defines [installFiles](file:///Users/luizvaldetaro/valdetaro/FunHouse/feature/funhouse-engine/src/main/java/utils/Utils.kt#L8), which extracts and copies room definitions, items, graphics, licenses, and HTML help templates from app assets to local filesystem directories.
        *   Exposes definitions for `defaultFunhouseGame` and `defaultIslandGame` containing structural game configs (start rooms, maximum items allowed, directory file paths).

---

### 2. Native JNI & Platform Integration (`/src/main/cpp/Gengame`)

*   #### [srvgenandroid.c](file:///Users/luizvaldetaro/valdetaro/FunHouse/feature/funhouse-engine/src/main/cpp/Gengame/srvgenandroid.c)
    *   **Purpose**: The concrete JNI implementation linking `Gengame.kt` calls to the underlying C game engine.
    *   **Key Responsibilities**:
        *   Implements native JNI functions like `Java_jni_Gengame_loadLibGame()`, `Java_jni_Gengame_sendLibCommand()`, `Java_jni_Gengame_setPlayerInfo()`, and `Java_jni_Gengame_vocabulary()`.
        *   Manages thread spawning for the C game loop using `pthread_create()` targeting the [lib_main](file:///Users/luizvaldetaro/valdetaro/FunHouse/feature/funhouse-engine/src/main/cpp/Gengame/srvgenandroid.c#L181) runner thread.
        *   Saves references to the Java Virtual Machine (`g_VM`) to invoke callbacks in `Gengame.kt`.
        *   Provides filesystem utilities like `lib_backup_file()` and `lib_restore_file()` for cross-directory backups.

*   #### [jni_Gengame.h](file:///Users/luizvaldetaro/valdetaro/FunHouse/feature/funhouse-engine/src/main/cpp/Gengame/jni_Gengame.h)
    *   **Purpose**: Header placeholder for JNI exports. Currently empty since modern JNI function names are resolved dynamically or declared inline in `srvgenandroid.c`.

---

### 3. Core Text Adventure Engine (`/src/main/cpp/Gengame`)

*   #### [architecture.h](file:///Users/luizvaldetaro/valdetaro/FunHouse/feature/funhouse-engine/src/main/cpp/Gengame/architecture.h)
    *   **Purpose**: Defines the data structures modeling the text adventure environment.
    *   **Key Definitions**:
        *   [`struct tagPlace`](file:///Users/luizvaldetaro/valdetaro/FunHouse/feature/funhouse-engine/src/main/cpp/Gengame/architecture.h#L36): Represents rooms/locations including available exits, point values, lighting conditions (dark/lit), names, descriptions, messages, and comments.
        *   [`struct tagObj`](file:///Users/luizvaldetaro/valdetaro/FunHouse/feature/funhouse-engine/src/main/cpp/Gengame/architecture.h#L50): Represents items/objects including points earned, drop locations, and state properties (moveable, readable, openable, unlockable, turnable, startable).
        *   [`struct tagGoal`](file:///Users/luizvaldetaro/valdetaro/FunHouse/feature/funhouse-engine/src/main/cpp/Gengame/architecture.h#L68): Models game tasks, mapping objectives to room locations, interactive verbs, dependencies, and completion conditions.
        *   [`struct tagInstance`](file:///Users/luizvaldetaro/valdetaro/FunHouse/feature/funhouse-engine/src/main/cpp/Gengame/architecture.h#L90): Encapsulates a player's interactive session (inventory list, total score, moves taken, current room, parsing buffers, and networking states).

*   #### [architecture.c](file:///Users/luizvaldetaro/valdetaro/FunHouse/feature/funhouse-engine/src/main/cpp/Gengame/architecture.c)
    *   **Purpose**: Responsible for parsing CSV game configurations and managing vocabulary synonyms.
    *   **Key Responsibilities**:
        *   Defines hardcoded tables for engine vocabulary: `verb` (movement directions and interactive commands), `conju` (conjunctions/articles), `pron` (adjectives/item descriptions), and `noun` (exit/object designations).
        *   Implements [load_master](file:///Users/luizvaldetaro/valdetaro/FunHouse/feature/funhouse-engine/src/main/cpp/Gengame/architecture.c#L149) which reads general settings, along with room loaders (`load_game_architecture`), item loaders (`load_game_objects`), and task builders (`load_game_goals`).
        *   Provides verb-synonym matching in [check_verb_synonym](file:///Users/luizvaldetaro/valdetaro/FunHouse/feature/funhouse-engine/src/main/cpp/Gengame/architecture.c#L132) (e.g. mapping "take" and "grab" to "get").

*   #### [externs.h](file:///Users/luizvaldetaro/valdetaro/FunHouse/feature/funhouse-engine/src/main/cpp/Gengame/externs.h)
    *   **Purpose**: Declares global variables and structures defined in `architecture.c` as externs, granting files like `gengame.c` access to places, objects, goals, active players, and general configurations.

*   #### [gengame.h](file:///Users/luizvaldetaro/valdetaro/FunHouse/feature/funhouse-engine/src/main/cpp/Gengame/gengame.h)
    *   **Purpose**: The central header for the core engine. Defines limits (e.g., maximum places, objects, and game instances), direction indices (e.g. `_NORTH`, `_SOUTH`), and exports global function definitions for game execution.

*   #### [gengame.c](file:///Users/luizvaldetaro/valdetaro/FunHouse/feature/funhouse-engine/src/main/cpp/Gengame/gengame.c)
    *   **Purpose**: The core mechanic execution hub. Contains the main input parser, state machine, and command handlers.
    *   **Key Responsibilities**:
        *   Implements the two-pass command parsers `parse()` and `recgn()`, converting text strings into interactive actions.
        *   Executes parsed directives in `execute()`, routing requests to action handlers (e.g. `get_obj()`, `drop_obj()`, `Open()`, `Close()`, `Read()`, `turn()`, `dig()`, `look()`).
        *   Manages player traversal in `move()` and maps directions through functions like `go_north()`, `go_south()`, `go_up()`, and `go_down()`.
        *   Implements inventory listings (`inventory`), scoring systems (`score`), game termination (`endgame`), and state serialization/deserialization (`save_game()`, `restore_game()`).

---

### 4. Networking & Multiplayer Coordinator (`/src/main/cpp/Gengame`)

*   #### [srvgencomms.c](file:///Users/luizvaldetaro/valdetaro/FunHouse/feature/funhouse-engine/src/main/cpp/Gengame/srvgencomms.c)
    *   **Purpose**: Manages multi-player sockets and delegates incoming client sessions.
    *   **Key Responsibilities**:
        *   Runs a listener thread (`accept_thread()`) that awaits connections on a specified port (e.g. 8082).
        *   Maps distinct client sockets to player instances (`inst` array matching `tagInstance is` data).
        *   Spawns player threads (`main_thread()`) that feed individual client socket streams into isolated instances of `game_main_loop()`.
        *   Processes remote commands (e.g., `playerinformation`, `ping`, and direct messaging between players `msg`).

*   #### [tcpip.h](file:///Users/luizvaldetaro/valdetaro/FunHouse/feature/funhouse-engine/src/main/cpp/Gengame/tcpip.h)
    *   **Purpose**: Defines constants for encryption protocols (AES, Blowfish, TwoFish, OpenSSL), compression types, and maps POSIX network sockets.

*   #### [tcpip.c](file:///Users/luizvaldetaro/valdetaro/FunHouse/feature/funhouse-engine/src/main/cpp/Gengame/tcpip.c)
    *   **Purpose**: Implementation of the network wrappers and cryptographic filters.
    *   **Key Responsibilities**:
        *   Handles socket creation, binding, listening, reading, and writing.
        *   Encodes and decodes network streams using dynamic compression (gzip/deflate) and encryption (e.g., AES-128/256 CBC, TwoFish, Blowfish, Base64).

---

### 5. Operating System Abstraction (`/src/main/cpp/Gengame`)

*   #### [iolib.h](file:///Users/luizvaldetaro/valdetaro/FunHouse/feature/funhouse-engine/src/main/cpp/Gengame/iolib.h)
    *   **Purpose**: Header declaration for platform-agnostic OS APIs, double-linked list types, JSON parsers, and SQLite bindings.

*   #### [iolib.c](file:///Users/luizvaldetaro/valdetaro/FunHouse/feature/funhouse-engine/src/main/cpp/Gengame/iolib.c)
    *   **Purpose**: A library bridging system calls across Linux/Android and Windows platforms.
    *   **Key Responsibilities**:
        *   Abstracts threading (`io_begin_thread`, `io_endthread`) and thread safety (`io_create_mutex_semaphore`, `io_request_mutex_semaphore`).
        *   Provides generic double-linked lists (`io_ll_t`) and management utilities (adding, inserting, updating, searching, and deleting nodes).
        *   Implements helper logic to parse, sanitize, and read values from JSON formatted text.
        *   Hosts SQL helpers (`sql_statement`, `open_specific_sqlite_db`, transactions) to communicate with the SQLite client database.

*   #### [bgi.h](file:///Users/luizvaldetaro/valdetaro/FunHouse/feature/funhouse-engine/src/main/cpp/Gengame/bgi.h)
    *   **Purpose**: Defines an HTTP `ENVIRONMENT` struct with CGI-like headers. Indicates CGI gateway web extension capability.

*   #### [sqlite3.h](file:///Users/luizvaldetaro/valdetaro/FunHouse/feature/funhouse-engine/src/main/cpp/Gengame/sqlite3.h)
    *   **Purpose**: Standard header for the embedded SQLite database engine.

---

### 6. Build Configurations

*   #### [CMakeLists.txt](file:///Users/luizvaldetaro/valdetaro/FunHouse/feature/funhouse-engine/CMakeLists.txt)
    *   **Purpose**: The native compilation script. Sets up dynamic library configurations, compiler flags (`-DSHARED_LIBRARY`, `-DANDROID`), and aggregates native C source files (`srvgenandroid.c`, `srvgencomms.c`, `gengame.c`, `architecture.c`, `iolib.c`, `tcpip.c`) into the shared binary `libgepetto.gensrv.so`.

*   #### [build.gradle](file:///Users/luizvaldetaro/valdetaro/FunHouse/feature/funhouse-engine/build.gradle)
    *   **Purpose**: Configures the Android Gradle target, setting target/min SDK values, declaring CMake location, setting up Java 17 toolchain, and declaring dependencies.

---

### 7. Game Assets & Definitions (`/src/main/assets`)

The game layout, item definitions, and objectives are externalized in CSV/JSON databases.
*   **Fun House Game Definition**:
    *   `funhouse.json` / `funhouse.csv`: Main game settings.
    *   `funhouseplaces.csv`: Map layout and room descriptions.
    *   `funhouseobjects.csv`: Interactive item listings and attributes.
    *   `funhousegoals.csv`: List of objectives needed to complete the game.
    *   `funhouse.html`: Instructions and help text format.
*   **Lost Island Game Definition**:
    *   `island.json` / `island.csv`: Main game settings.
    *   `islandplaces.csv`: Location definitions and description strings.
    *   `islandobjects.csv`: Object configurations.
    *   `islandgoals.csv`: Escape criteria and score parameters.
    *   `island.html`: Gameplay guide documentation.
