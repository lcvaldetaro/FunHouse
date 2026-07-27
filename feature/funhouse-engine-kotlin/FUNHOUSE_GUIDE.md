# Fun House Game Guide & Map

This document provides a comprehensive walkthrough and map layout of the **Fun House** adventure. The details below correspond directly to the database configuration files: [funhouseplaces.csv](file:///Users/luizvaldetaro/valdetaro/FunHouse/feature/funhouse-engine/src/main/assets/funhouseplaces.csv) and [funhouseobjects.csv](file:///Users/luizvaldetaro/valdetaro/FunHouse/feature/funhouse-engine/src/main/assets/funhouseobjects.csv).

---

## Game Overview
*   **Starting Location**: Room 21 (Abandoned Ticket Booth).
*   **Goal**: Find items, solve puzzles, bypass the maze, and unlock the exit gate to escape.
*   **Maximum Items Allowed**: 5 items.

---

## Interactive Item Registry
| ID | Item Name | Nickname | Start Location | Action Verbs | Description / Use Case |
| :--- | :--- | :--- | :--- | :--- | :--- |
| **1** | a heavy engine | `engine` | Room 6 | `get` | A MERCURY 720 engine. Required to start the generator. |
| **2** | a golden key | `key` | Room 2 | `"get,open"` | Opens the locked door to Room 8. |
| **3** | an iron can | `can` | Room 18 | `get` | Fuel container. Required to start the generator. |
| **4** | some food | `food` | Room 10 | `get` | Utility item. |
| **5** | a sign | `sign` | Room 21 | `read` | Warning sign: *"The ticket booth is boarded up..."* |
| **6** | a lamp | `lamp` | Room 1 | `"get,turn"` | Electric lamp. Ignite it (`turn lamp`) to see in Room 3. |
| **7** | an iron door | `door` | Room 5 | None | Locked scenery door. |
| **8** | a ticket | `ticket` | Room 21 | `"get,open"` | Ticket used to unlock the entrance and consult Zoltar. |
| **9** | a mirror | `mirror` | Room 12 | None | Maze mirror scenery. |
| **10**| a string | `string` | Room 13 | `get` | Puppet string. |
| **11**| a music box | `box` | Room 13 | `open` | Locked puppet music box. |
| **12**| a teller | `teller` | Room 17 | `start` | Zoltar fortune teller machine. Requires `ticket` to start. |
| **13**| a copper coin | `coin` | Room 17 | `get` | Shiny coin received from Zoltar. Unlocks the safe. |
| **14**| a generator switch | `switch` | Room 14 | `turn` | Switch to restore funhouse power. Requires engine and fuel. |
| **15**| a safe | `safe` | Room 19 | `open` | Metal safe containing the Ringmaster's key. |
| **16**| a ringmaster key | `key` | Room 19 | `"get,open"` | Silver exit key. Unlocks the exit gate (Room 22). |
| **17**| a wooden horse | `horse` | Room 16 | None | Carousel scenery. |
| **18**| an exit gate | `gate` | Room 16 | None | Locked exit gate. |

---

## Room Map & Navigation Flowchart

The funhouse contains 22 rooms. Exits with a negative prefix (e.g. `-21` or `-8`) are locked. Use the `open` command while carrying the correct key/item to unlock them.

| Room ID | Room Name | South | North | West | East | Up / Down / Special | Notes / Status |
| :---: | :--- | :---: | :---: | :---: | :---: | :---: | :--- |
| **1** | Hall of Optical Illusions | **21** (Locked) | **3** | **12** | **2** | - | Starts here. South requires **ticket**. |
| **2** | Makeshift Kart Chamber | - | - | **1** | - | - | Contains golden key. |
| **3** | Hall of Ghostly Frivolity | **1** | **5** | **4** | **13** | - | **Dark Room** (requires lit lamp). |
| **4** | Rickety Stairs | - | - | **3** | - | Down: **18** | Leads to mechanical shaft. |
| **5** | Blood-shock Hall 1 | **3** | **6** | **7** | **8** (Locked) | - | East exit requires **golden key**. |
| **6** | Blood-shock Hall 2 | **5** | **9** | **10** | **11** | - | Main intersection hub. |
| **7** | Musical Room | - | - | - | **5** | - | - |
| **8** | Hellhound's Enclosure | - | - | **5** | **16** | - | Leads to Carousel clearing. |
| **9** | Upstairs Landing | **6** | - | **20** | **19** | Down: **15** (Slide) | Leads to Den and Private Office. |
| **10** | Conspicious Trap Door | - | - | **6** | - | Down: **18** | Trapdoor into basement shaft. |
| **11** | The Scorching Cell | - | - | **6** | - | - | - |
| **12** | Mirror Maze | **17** | **12** (Loops) | **12** (Loops) | **1** | - | Looping layout. Exit is South. |
| **13** | Chamber of Puppets | - | - | **3** | - | - | Contains music box & string. |
| **14** | The Old Control Room | - | - | - | **18** | - | Contains power switch. |
| **15** | The Spiral Giant Slide | - | - | - | - | Down: **4** | One-way shortcut back to basement. |
| **16** | Haunted Carousel Clearing | - | - | **8** | **22** (Locked) | - | East gate requires **ringmaster key**. |
| **17** | Fortune Teller's Booth | - | **12** | - | - | - | Contains Zoltar teller. |
| **18** | Secret Mechanical Shaft | - | - | **14** | - | Up: **10** / Down: **4** | Connects trapdoor and stairs. |
| **19** | Ringmaster's Private Den | - | - | **9** | - | - | Contains safe with ringmaster key. |
| **20** | The Hall of Vertigo | - | - | - | **9** | - | Disorienting exit. |
| **21** | Abandoned Ticket Booth | - | **1** (Locked) | - | - | - | Starting spawn point. |
| **22** | Exit Gate | - | - | - | - | Out: Win | Escape clearing. Victory spot. |

---

## Step-by-Step Walkthrough

1.  **Enter the Funhouse**:
    *   Start at the **Abandoned Ticket Booth** (Room 21).
    *   Command: `get ticket`.
    *   Command: `open` (unlocks the door to Room 1).
    *   Command: `north`.
2.  **Get the Lamp & Main Key**:
    *   Now in the **Hall of Optical Illusions** (Room 1).
    *   Command: `get lamp`.
    *   Command: `east` (to Room 2).
    *   Command: `get key`.
    *   Command: `west`.
3.  **Light up the Gallery**:
    *   Command: `turn lamp`.
    *   Command: `north` (to the **Hall of Ghostly Frivolity**, Room 3).
4.  **Get the Ticket to Zoltar**:
    *   Command: `west` (to Room 12, **Mirror Maze**).
    *   Command: `south` (to the **Fortune Teller's Booth**, Room 17).
    *   Command: `start teller` (inserts the ticket).
    *   Command: `get coin` (collects the copper coin from the tray).
    *   Command: `north` (back to Mirror Maze), then `east` (back to Room 1).
5.  **Gather the Generator Parts**:
    *   From Room 1, go `north` (Room 3), then `north` (Room 5), then `north` (Room 6).
    *   Go `west` to Room 10.
    *   Command: `down` (enters **Secret Mechanical Shaft**, Room 18).
    *   Command: `get can` (gets fuel can).
    *   Go `up` (Room 10), then `east` (Room 6).
    *   Command: `get engine` (gets heavy engine).
6.  **Power Up the Carousel**:
    *   Go `west` (Room 10), then `down` (Room 18).
    *   Command: `west` (enters **Control Room**, Room 14).
    *   Command: `start switch` (uses engine & fuel to power generator).
    *   Go `east` (Room 18), then `up` (Room 10), then `east` (Room 6).
7.  **Obtain the Exit Key**:
    *   Command: `north` (enters **Upstairs Landing**, Room 9).
    *   Command: `east` (enters **Ringmaster's Private Den**, Room 19).
    *   Command: `start safe` (drops the copper coin in slot, opens safe).
    *   Command: `get key` (gets the ringmaster key).
    *   Command: `west` (back to Room 9), then `south` (Room 6), then `south` (Room 5).
8.  **Unlock the Hellhound Gate**:
    *   Now in Room 5.
    *   Command: `open` (uses the golden key to unlock the door to Room 8).
    *   Command: `east` (enters Room 8).
    *   Command: `east` (enters **Haunted Carousel Clearing**, Room 16).
9.  **Escape the Funhouse**:
    *   Now in Room 16.
    *   Command: `open` (uses the ringmaster key to unlock the exit gate to Room 22).
    *   Command: `east` (or `start gate` to trigger victory). You have escaped!
