# Lost Island Game Guide & Map

This document provides a comprehensive walkthrough and map layout of the **Lost Island** adventure. The details below correspond directly to the database configuration files: [islandplaces.csv](file:///Users/luizvaldetaro/valdetaro/FunHouse/feature/funhouse-engine/src/main/assets/islandplaces.csv) and [islandobjects.csv](file:///Users/luizvaldetaro/valdetaro/FunHouse/feature/funhouse-engine/src/main/assets/islandobjects.csv).

---

## Game Overview
*   **Spawn Locations**: Randomly chosen between Rooms 1, 6, 14, 16, 19, or 20.
*   **Goal**: Find items, navigate the forest, hills, cave, and house, and escape either by repair/fueling the plane or speedboat, or getting the gold.
*   **Maximum Items Allowed**: 5 items.

---

## Interactive Item Registry
| ID | Item Name | Nickname | Start Location | Action Verbs | Description / Use Case |
| :--- | :--- | :--- | :--- | :--- | :--- |
| **1** | a heavy engine | `engine` | Room 6 | `start` | Speedboat engine. |
| **2** | a golden key | `key` | Room 2 | `"get,open"` | Unlocks Room 10 (Old house). |
| **3** | an iron can | `can` | Room 10 | `get` | Fuel container. |
| **4** | some food | `food` | Room 22 | `get` | Fried chicken. Required for boat escape. |
| **5** | a shovel | `shovel` | Room 16 | `get` | Digs graves/sand. |
| **6** | a plastic cup | `cup` | Room 1 | `get` | Simple container. |
| **7** | a wooden stick | `stick` | Room 1 | `get` | Tree branch. |
| **8** | a small paper | `paper` | Room 10 | `"get,read"` | Contains password: `"DEATHMATCH."` |
| **9** | a chest | `chest` | Room 10 | `"get,open,unlock"`| Locked box. Contains points. |
| **10**| a ball | `ball` | Room 6 | `get` | Soccer ball. |
| **11**| an old house | `house` | Room 9 | None | House scenery. |
| **12**| a speed boat | `boat` | Room 5 | None | Speedboat in the water. |
| **13**| yellow sand | `sand` | Room 5 | `get` | Sandy ground. |
| **14**| some fuel | `fuel` | Room 4 | `get` | Boat fuel. |
| **15**| an iron door | `door` | Room 9 | None | Closed door scenery. |
| **16**| an airplane | `airplane`| Room 1 | `start` | Crashed YF-22 jet. Target for repair/escape. |
| **17**| a sign | `sign` | Room 1 | `read` | Warning sign. |
| **18**| an ancient grave | `grave` | Room 16 | None | Grave scenery. |
| **19**| a treasure chest | `treasure`| Room 6 | `get` | High-value treasure item. |
| **20**| some Silver bars | `silver` | Room 11 | `get` | Silver bars. |
| **21**| a lamp | `lamp` | Room 7 | `"get,turn"` | Gas lamp. Ignites to light up Room 10. |
| **22**| a liquid Container| `container`| Room 10 | `get` | 20-gallon fuel container. |
| **23**| Fuel | `fuel` | Room 9 | `get` | Jet fuel. Required to fuel the plane. |
| **24**| a bucket | `bucket` | Room 1 | `get` | Metal bucket. |
| **25**| a Knife | `knife` | Room 3 | `get` | Combat knife. |
| **26**| a Revolver | `revolver`| Room 10 | `get` | Smith & Wesson revolver. |
| **27**| a Gun | `gun` | Room 6 | `get` | Walther PPK. |
| **28**| a Toolbox | `toolbox` | Room 26 | `get` | Mechanics toolset. Required to fix the plane. |
| **29**| a large house | `house` | Room 9 | None | Scenery house. |
| **30**| a Barrel | `barrel` | Room 14 | `get` | Golden barrel full of diamonds. |
| **31**| some water | `water` | Room 19 | `get` | Drinkable water bottle. |
| **32**| a message | `message` | Room 16 | `read` | Warning: *"Beware of falling over the ledge!"* |
| **33**| ignition button| `ignition`| Room 19 | `start` | Airplane controls. |

---

## Room Map & Navigation Flowchart

The island is divided into the Forest/Shore, the Heights, and the House. 

> [!WARNING]
> Entering Room 18 (Dark Pit) or Room 29 (Ledge Chasm) results in instant death and triggers a Game Over.

| Room ID | Room Name | South | North | West | East | Up / Down / Special | Notes / Status |
| :---: | :--- | :---: | :---: | :---: | :---: | :---: | :--- |
| **1** | Large Clearing | **17** | - | **2** | - | Up: **18** / Down: **19** | Spawn point. Contains crashed plane. |
| **2** | Forest | **3** | - | - | **1** | - | Spawn point. Contains golden key. |
| **3** | Forest | **4** | **2** | - | - | - | Contains combat knife. |
| **4** | Forest | - | **3** | **5** | - | - | Contains boat fuel. |
| **5** | Sandy Shore | **6** | **7** | - | **4** | - | Contains speed boat. |
| **6** | In a boat | - | **5** | - | - | - | Spawn point. Contains engine & treasures. |
| **7** | Forest path | **5** | **8** | - | - | - | Contains gas lamp. |
| **8** | Forest path | **7** | **9** | - | - | - | - |
| **9** | In a village | **8** | **11** | **15** | **10** (Locked) | NE: **20** | Hub room. East requires **golden key**. |
| **10** | In an old house | - | - | **9** | - | - | **Dark Room**. Requires gas lamp to see. |
| **11** | Mountain side | **9** | **12** | - | - | - | Contains silver bars. |
| **12** | Mountain side | **11** | **13** | - | - | - | - |
| **13** | Cave mouth | **12** | **14** | - | - | Up: **14** | Leads into the cave. |
| **14** | In a cave | **13** | - | - | - | Down: **13** | Spawn point. Contains diamond barrel. |
| **15** | Cliff path | - | - | **16** | **9** | Down: **29** | - |
| **16** | Rocky ledge | - | - | - | **15** | - | Spawn point. Contains shovel. |
| **17** | Dead end | - | **1** | - | - | - | - |
| **18** | Dark pit | - | - | - | - | Up: **1** (with -255 pts) | **DEATH ROOM** (Game Over). |
| **19** | In an airplane | - | - | - | - | Out: **1** | Target for repairing/escaping. |
| **20** | Lobby of a House | - | **23** | **21** | **9** (from NE) | Up: **25** / NW: **22** / NE: **24** | Spawn point. Contains stairs. |
| **21** | Study room | - | - | - | **20** | - | - |
| **22** | Kitchen | - | - | - | - | SE: **20** | Contains food. |
| **23** | Living room | **20** | - | - | - | - | - |
| **24** | Dining room | - | - | **20** | - | - | - |
| **25** | Den (2nd floor) | - | **27** | - | **26** | Down: **20** / NW: **28** | Den landing area. |
| **26** | Master bedroom | - | - | **25** | - | - | Contains toolbox. |
| **27** | Guest bedroom | **25** | - | - | - | - | - |
| **28** | Small bedroom | - | - | - | - | SE: **25** | - |
| **29** | Ledge Chasm | - | - | - | - | Up: **1** (with -255 pts) | **DEATH ROOM** (Game Over). |

---

## Victory Paths

### Path A: Escape by Plane (400 Points)
1.  **Retrieve the Airplane Key**: Go to the Forest (Room 2) and get the `golden key` (2).
2.  **Unlock the Old House**: Go to the Village (Room 9). Execute `open` to unlock the Old House (Room 10) with the golden key.
3.  **Light up the Old House**: Carry the `lamp` (21) from Room 7 and execute `turn lamp` to light up Room 10.
4.  **Get the Container**: In Room 10, pick up the `liquid Container` (22) and the `iron can` (3).
5.  **Get the Jet Fuel**: In the Village (Room 9), execute `get fuel` (23) to load it into the Container.
6.  **Retrieve the Toolbox**: Go to the Lobby of the House (Room 20), climb `up` to the Den (Room 25), go `east` to the Master Bedroom (Room 26), and pick up the `toolbox` (28).
7.  **Board and Escape**: Go to the Clearing (Room 1) and go `down` into the airplane (Room 19). Execute `start airplane` (or `start ignition`) while carrying the Container, Fuel, and Toolbox to repair and take off!

### Path B: Escape by Speedboat (150 Points)
1.  **Get the Fuel**: Go to the Forest (Room 4) and pick up `some fuel` (14).
2.  **Get the Food**: Enter the House Lobby (Room 20), go `north-west` to the Kitchen (Room 22), and pick up `some food` (4).
3.  **Board and Escape**: Go to the Shore (Room 5) and go `south` onto the speedboat (Room 6). Execute `start boat` while carrying the fuel and food to start the engines and escape!

### Path C: Collect the Gold (100 Points)
1.  **Retrieve the Treasure**: Go to the Speedboat (Room 6) and pick up `treasure chest` (19).
2.  **Claim the Gold**: Bring the chest to the Clearing (Room 1) and enter the airplane (Room 19). Execute `take gold` (or `get treasure`) to claim the victory!
