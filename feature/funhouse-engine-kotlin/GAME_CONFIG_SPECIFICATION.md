# FunHouse Engine Game Configuration Specification

The game layouts, room architectures, interactive objects, and objective goals for the FunHouse engine are fully data-driven. They are defined using standard Comma-Separated Values (CSV) files located in the module's assets directory: `/src/main/assets`.

This document specifies the schema, columns, and behaviors of the four primary CSV file types:
1.  [Master Configuration CSV](#1-master-configuration-csv)
2.  [Places (Rooms) CSV](#2-places-rooms-csv)
3.  [Objects (Items) CSV](#3-objects-items-csv)
4.  [Goals (Objectives) CSV](#4-goals-objectives-csv)

---

## 1. Master Configuration CSV
*   **Examples**: [funhouse.csv](file:///Users/luizvaldetaro/valdetaro/FunHouse/feature/funhouse-engine/src/main/assets/funhouse.csv), [island.csv](file:///Users/luizvaldetaro/valdetaro/FunHouse/feature/funhouse-engine/src/main/assets/island.csv)
*   **Purpose**: Acts as the main entry point specifying metadata and referencing secondary asset files.

### Structure (Key-Value)
The file is structured as a row-based key-value store. Each line contains:
1.  **Setting Key** (Column 0): Name of the configuration variable.
2.  **Value** (Column 1): The variable value.
3.  **Comments** (Column 2): Optional description of the configuration key.

### Supported Keys
*   `Title`: The visible title of the adventure.
*   `Places file`: The basename of the CSV file containing room definitions (e.g., `funhouseplaces`).
*   `Objects file`: The basename of the CSV file containing interactive object definitions (e.g., `funhouseobjects`).
*   `Goals file`: The basename of the CSV file containing game goals (e.g., `funhousegoals`).
*   `savefile`: Prefix string for save state files.
*   `maximum objects carried`: Integer limit of items a player's inventory can hold at once.
*   `Greeting`: String printed to the terminal when the game starts.
*   `Description`: Summary details of the game.
*   `helpfile`: The HTML document displayed when the user executes the `help` or `?` command.
*   `Start Places`: Comma-separated room IDs where the player can spawn.

---

## 2. Places (Rooms) CSV
*   **Examples**: [funhouseplaces.csv](file:///Users/luizvaldetaro/valdetaro/FunHouse/feature/funhouse-engine/src/main/assets/funhouseplaces.csv), [islandplaces.csv](file:///Users/luizvaldetaro/valdetaro/FunHouse/feature/funhouse-engine/src/main/assets/islandplaces.csv)
*   **Purpose**: Models the rooms, caves, outdoors, and connection pathways of the virtual world.

### Column Specification (TOT_FLDP = 20 columns)
| Index | Column Name | Type | Description |
| :--- | :--- | :--- | :--- |
| **0** | `Number` | Integer | Unique identifier for the room. |
| **1** | `Place` | String | Short name or title of the room. |
| **2** | `South` | Integer | Room ID connected to the South exit (0 if blocked). |
| **3** | `North` | Integer | Room ID connected to the North exit. |
| **4** | `West` | Integer | Room ID connected to the West exit. |
| **5** | `East` | Integer | Room ID connected to the East exit. |
| **6** | `SW` | Integer | Room ID connected to the Southwest exit. |
| **7** | `SE` | Integer | Room ID connected to the Southeast exit. |
| **8** | `NW` | Integer | Room ID connected to the Northwest exit. |
| **9** | `NE` | Integer | Room ID connected to the Northeast exit. |
| **10**| `Up` | Integer | Room ID connected upwards. |
| **11**| `Down` | Integer | Room ID connected downwards. |
| **12**| `In` | Integer | Room ID entered by going inside. |
| **13**| `Out` | Integer | Room ID exited by going outside. |
| **14**| `Points` | Integer | Score points awarded upon discovering/entering this room. |
| **15**| `Status` | String | Initial room lighting state. Set to `"dark"` to require a light source. |
| **16**| `Object dependency` | Integer | Object ID dependency required to unlock or access the room. |
| **17**| `Message` | String | Custom greeting printed when entering the room. |
| **18**| `Alternate description`| String | Alternative description shown under special states. |
| **19**| `Description` | String | Detailed room environment description. |

### Access Rules
*   **Directions**: If a direction movement column evaluates to a positive integer, going that direction transitions the player to that room.
*   **Locked Exits**: A negative room integer (e.g. `-8`) denotes a locked pathway. To pass, the player must hold or use the key item specified by the `Object dependency` parameter.

---

## 3. Objects (Items) CSV
*   **Examples**: [funhouseobjects.csv](file:///Users/luizvaldetaro/valdetaro/FunHouse/feature/funhouse-engine/src/main/assets/funhouseobjects.csv), [islandobjects.csv](file:///Users/luizvaldetaro/valdetaro/FunHouse/feature/funhouse-engine/src/main/assets/islandobjects.csv)
*   **Purpose**: Models interactive items, items to carry, clues, locks, and mechanisms.

### Column Specification (TOT_FLDO = 14 columns)
| Index | Column Name | Type | Description |
| :--- | :--- | :--- | :--- |
| **0** | `Number` | Integer | Unique identifier for the object. |
| **1** | `Object Name` | String | Visible string representation of the item (e.g., `"a golden key"`). |
| **2** | `Nickname` | String | Single-word lowercase noun parsed by the engine (e.g., `"key"`). |
| **3** | `Location` | Integer | Room ID where this object initially spawns. |
| **4** | `Weight` | Integer | Optional item weight (unimplemented/defaults to 0). |
| **5** | `Points dropping` | Integer | Score points awarded when dropping this item. |
| **6** | `Place to drop/open`| Integer | Specific Room ID where the item must be dropped or operated. |
| **7** | `Points Taking` | Integer | Score points awarded when the player successfully picks up the item. |
| **8** | `Status` | String | Initial status matching: `"on"`, `"off"`, `"locked"`, `"unlocked"`, `"closed"`, `"open"`. |
| **9** | `Verbs` | String | Comma-separated actions permitted: `"get"`, `"read"`, `"open"`, `"turn"`, `"start"`, `"unlock"`. |
| **10**| `Text` | String | Associated text messages shown when reading or using the object. |
| **11**| `Dependent` | Integer | Dependent Object ID required to operate this item. |
| **12**| `Incompatible` | Integer | Object ID that invalidates this item if carried at the same time. |
| **13**| `Description` | String | Detailed description shown when inspect-reading the item. |

---

## 4. Goals (Objectives) CSV
*   **Examples**: [funhousegoals.csv](file:///Users/luizvaldetaro/valdetaro/FunHouse/feature/funhouse-engine/src/main/assets/funhousegoals.csv), [islandgoals.csv](file:///Users/luizvaldetaro/valdetaro/FunHouse/feature/funhouse-engine/src/main/assets/islandgoals.csv)
*   **Purpose**: Maps game progression conditions, winning mechanics, and multiplayer scoring rules.

### Column Specification (TOT_FLDGL = 15 columns)
| Index | Column Name | Type | Description |
| :--- | :--- | :--- | :--- |
| **0** | `Number` | Integer | Unique identifier for the goal. |
| **1** | `Obectivename` | String | Name of the goal. |
| **2** | `Finish Game` | Character | Set to `"y"` if completing this goal wins the game and triggers victory. |
| **3** | `Points Awarded to owner`| Integer| Score points awarded to the completing player. |
| **4** | `Ponts awared to non-owner`| Integer| Multiplayer score awarded to other concurrent players. |
| **5** | `Verbs` | String | Trigger verbs: `"start"`, `"kill"`, `"get"` (or `"take"`, `"grab"`), `"drop"`. |
| **6** | `Location` | Integer | Target Room ID where this goal action must be executed. |
| **7** | `Object` | Integer | Target Object ID associated with the goal verb. |
| **8** | `text` | String | Completion message displayed to the player. |
| **9** | `Loc Dep` | Integer | Room ID dependency (player must be here to do the action). |
| **10**| `Obj Dependency` | Int List | Space or comma-separated Object IDs that the player *must carry* to complete this goal. |
| **11**| `Obj Incompatible` | Int List | Space or comma-separated Object IDs that *must not be carried* to complete this goal. |
| **12**| `Error text` | String | Error string printed if dependencies fail. |
| **13**| `Non owner text` | String | Text broadcast to other multiplayer sessions when this goal is met. |
| **14**| `Description` | String | Description of the goal parameters. |

---

## Game Engine Logical Behaviors

*   **Synonyms**: The engine normalizes user inputs using the vocabulary declared in [architecture.c](file:///Users/luizvaldetaro/valdetaro/FunHouse/feature/funhouse-engine/src/main/cpp/Gengame/architecture.c). For example, typing `"take key"` translates the verb to `"get"` and looks for the object nicknamed `"key"`.
*   **Darkness**: If a place's status is `"dark"`, room descriptions will not be displayed, and player movements or look commands will notify the player that they cannot see, unless they carry an activated light source object (an object with status `"on"`).
*   **Goal Dependencies**: When a player invokes a verb on an object, the engine scans the loaded Goals. If a matching goal is found:
    1.  It checks if the player is in the correct `Location` / matches `Loc Dep`.
    2.  It verifies the player carries the items specified in `Obj Dependency`.
    3.  It verifies the player does *not* carry items listed in `Obj Incompatible`.
    4.  If criteria are satisfied, the goal completes: points are added, victory triggers if marked `"y"`, and `text` is printed. If checks fail, `Error text` is printed instead.
