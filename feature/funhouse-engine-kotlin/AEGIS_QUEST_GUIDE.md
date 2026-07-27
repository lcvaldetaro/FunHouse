# Aegis Quest Game Guide & Map

This document provides a comprehensive walkthrough, room layout, item registry, and victory guide for the **Aegis Quest** adventure on the Aegis Prime Space Station.

---

## Game Overview
*   **Spawn Locations**: Randomly chosen from rooms: 1, 16, 31, 46, 61, 76, 86, or 96 (representing the entry lift of each deck/garrison/nest).
*   **Conflict**: Three factions (Solar Vanguard, Xenophage Swarm, and Mecha-Legion) are in a deadly battle to control Aegis Prime.
*   **Inventory Capacity**: Maximum of 5 items.
*   **Darkness Mechanic**: Several secure rooms (such as the Vault, Core Hub, and Armory) are pitch dark. You must carry the **flashlight (33)** and type `turn flashlight` (to set its status to ON) to see and interact in these rooms.

---

## Interactive Item Registry

Here is a list of key items you can find throughout Aegis Prime:

| ID | Item Name | Nickname | Start Location | Verbs | Description / Use Case |
| :--- | :--- | :--- | :--- | :--- | :--- |
| **1** | heavy reactor rod | `rod` | Room 33 | `get` | High weight reactor component. Required to trigger Reactor Self-Destruct. |
| **2** | override key | `key` | Room 12 | `"get,open"` | Security override keycard. Opens blast door to Armory Annex (Room 14). |
| **3** | laser cutter | `cutter` | Room 42 | `get` | Portable laser tool. Required to cut the seals and obtain the Alien Artifact. |
| **4** | alien artifact | `artifact`| Room 55 | `get` | Mysterious alien relic. Requires Laser Cutter (3) in inventory to pick up. |
| **5** | security keycard | `keycard` | Room 14 | `"get,open"` | Senior officer keycard. Opens blast door to the Command Bridge (Room 1). |
| **6** | emp grenade | `grenade` | Room 78 | `get` | Weapon. Disables droids and adds weight. |
| **7** | plasma blaster | `blaster` | Room 14 | `get` | High-yield weapon. Increases combat weight when held. |
| **8** | nanite paste | `paste` | Room 48 | `get` | Carbon-repair paste. Required to decontaminate Life Support. |
| **9** | bio-toxin canister| `canister`| Room 51 | `"get,drop"` | Bio-hazard flask. Target for Xenophage water infection goal. |
| **11**| filter cartridge | `cartridge`| Room 27 | `get` | Air filtration filter. Required to decontaminate Life Support. |
| **12**| decryption disk | `disk` | Room 102 | `"get,open,drop"`| Droid decryption algorithms. Opens ML Mainframe (Room 102). |
| **13**| queen egg | `egg` | Room 92 | `get` | Pulsing heavy alien egg. |
| **14**| ai drive | `drive` | Room 104 | `get` | Data stick loaded with the mainframe virus payload. |
| **15**| launch codes | `codes` | Room 8 | `"get,read"` | Emergency pod launch codes: `"7042"`. |
| **16**| escape pod key | `podkey` | Room 80 | `"get,open"` | Pod release key. Unlocks Escape Pod Bay door (Room 65). |
| **33**| flashlight | `flashlight`| Room 16 | `"get,turn"` | Portable light source. Toggle `on`/`off` to explore dark sectors. |
| **35**| carbon filter | `filter` | Room 30 | `get` | Air membrane. Used for Life Support decontamination. |
| **37**| reactor console | `console` | Room 35 | `start` | reactor control board. Target of self-destruct goal. |
| **38**| escape pod console| `pod` | Room 65 | `start` | Evacuation capsule computer. Target of escape pod goal. |
| **39**| bridge terminal | `terminal`| Room 1 | `start` | Mainframe console on Bridge. Target of AI virus goal. |
| **40**| purification grid| `purification`| Room 26 | `start` | Life Support controls. Target of decontamination goal. |
| **41**| stolen plans | `plans` | Room 90 | `get` | Vanguard troop movements. Target of military plans recovery goal. |
| **50**| mainframe core | `core` | Room 105 | `"get,drop"` | Mainframe optical hardware. Target of Vanguard victory goal. |

---

## Room Map & Navigation Grid

Aegis Prime contains 105 rooms across 8 sectors. You can move between Decks using the **Central Lifts** located at the core of Decks B, C, D, and E.

### Deck A: Bridge & Ops (Rooms 1 - 15)
*   **Room 1 (Bridge)**: Links Down to Room 16 (Deck B Lift). Commands `east` to Room 2, `west` to Room 3, and `south` to Room 8.
*   **Room 8 (Central Junction A)**: Hub linking to Bridge (north), Officers Lounge (south), Server Alpha (east), and Server Beta (west).
*   **Room 10 (Officers Quarters)**: **DARK**. Requires Flashlight. Links to Engineer's Quarters (east) and Security Chief's Quarters (west).
*   **Room 13 (Briefing Room)**: Blast door leads `south` to Room 14 (locked, requires Override Key (2)).
*   **Room 14 (Secure Armory)**: **DARK**. Requires Flashlight. Contains the Security Keycard (5) and Plasma Blaster (7).

### Deck B: Crew & Life Support (Rooms 16 - 30)
*   **Room 16 (Central Lift B)**: Links Up to Room 1 (Bridge), Down to Room 31 (Deck C Lift), East to Crew Lounge (17), and West to Mess Hall (18).
*   **Room 26 (Water Reclamation)**: Hub linking to Air Filtration (east) and Life Support (west).

### Deck C: Engineering & Reactor (Rooms 31 - 45)
*   **Room 31 (Central Lift C)**: Links Up to Room 16, Down to Room 46 (Deck D Lift), East to Engineering (32), and West to Generator (33).
*   **Room 35 (Reactor Control Room)**: **DARK**. Requires Flashlight. Contains the Reactor Console (37).

### Deck D: Science Labs (Rooms 46 - 60)
*   **Room 46 (Central Lift D)**: Links Up to Room 31, Down to Room 61 (Deck E Lift), East to Biology Lab (47), and West to Cybernetics Lab (48).
*   **Room 56 (Security Post D)**: Vault door to the west is locked (requires Cryo Key (49)). Leads to Room 55.
*   **Room 55 (Research Vault)**: **DARK**. Requires Flashlight. Contains the Alien Artifact (4).

### Deck E: Hangar & Cargo (Rooms 61 - 75)
*   **Room 61 (Central Lift E)**: Links Up to Room 46, East to Cargo Alpha (62), and West to Cargo Beta (63).
*   **Room 63 (Cargo Beta)**: Hangar door to the southeast is locked (requires Escape Pod Key (16)). Leads to Room 65.
*   **Room 65 (Hangar Pod Bay)**: Contains the Escape Pod Console (38).

### Deck F: Solar Vanguard Garrison (Rooms 76 - 85)
*   **Room 76 (SV Gatehouse)**: Main entry point for SV territory.
*   **Room 84 (SV Command Post)**: Garrison headquarters. Target room for dropping Mainframe Core (50).

### Deck G: Xenophage Nest (Rooms 86 - 95)
*   **Room 86 (Infested Hatchery)**: Main entry point for the swarm nest.
*   **Room 95 (Queen Chamber)**: **DARK**. Requires Flashlight. Contains the Hive Queen.

### Deck H: Mecha-Legion Sector (Rooms 96 - 105)
*   **Room 96 (ML Gate Control)**: Main entry point for the droid sector.
*   **Room 101 (ML Logic Processing)**: Mainframe Hub door to the west is locked (requires Decryption Disk (12)). Leads to Room 102.
*   **Room 102 (ML Mainframe Hub)**: **DARK**. Requires Flashlight. Contains the ML main terminal.
*   **Room 105 (ML Core)**: Contains the Mainframe Core (50).

---

## Faction Victory Walkthroughs

### 1. Solar Vanguard Victory (350 Points)
1.  **Retrieve Keycard**: Go to the Briefing Room (13). Unlock the blast door `south` to the Secure Armory Annex (14) using the Override Key (2) (found in Room 12).
2.  **Light the Armory**: Turn ON your flashlight (33) to light Room 14 and pick up the `security keycard` (5).
3.  **Unlock the Bridge**: Go to Central Junction A (8) and unlock the Bridge door `north` using the security keycard.
4.  **Enter the Droid Sector**: Go down to Deck E, enter the ML Gate Control (96), make your way to ML Logic Processing (101). Unlock the Hub `west` to Room 102 using the `decryption disk` (12) (found in Room 102).
5.  **Enter the Core**: Go east/south to the ML Core (105) and pick up the `mainframe core` (50).
6.  **Secure Victory**: Carry the mainframe core to the Solar Vanguard Command Post (Room 84) and type `drop core` to deliver victory to the Alliance!

### 2. Mecha-Legion Takeover (350 Points)
1.  **Acquire the Disk**: Go to ML Logic Processing (101). Unlock the door west using the `decryption disk` (12).
2.  **Light up the Hub**: Enter ML Mainframe Hub (102). Turn ON the flashlight (33) to light the room. Pick up the decryption disk (12) again.
3.  **Secure Takeover**: Type `drop disk` inside the ML Mainframe Hub (102) to upload the rogue override algorithms, giving the machine mind complete station control!

### 3. Xenophage Domination (350 Points)
1.  **Get the Bio-Toxin**: Go to the Chemistry Lab (51) on Deck D and pick up the `bio-toxin canister` (9).
2.  **Infect the Purifier**: Go to the Water Reclamation Facility (Room 26) on Deck B. Type `drop canister` in the room to dump the toxins into the purification unit, spreading the hive biomass across the entire station!

### 4. Evacuation Escape (400 Points)
1.  **Get the Pod Key**: Go to the SV Officers Quarters (Room 80) in the garrison and pick up the `escape pod key` (16).
2.  **Get the Codes**: Go to Central Junction A (Room 8) on Deck A and pick up the `launch codes` (15). Execute `read codes` to view the sequence.
3.  **Unlock the Hangar**: Go to Cargo Bay Beta (Room 63) on Deck E and unlock the hangar door `se` to Room 65 using the escape pod key (16).
4.  **Launch and Escape**: Enter Hangar Pod Bay (Room 65). While carrying the launch codes (15) and escape pod key (16), type `start pod` (or `start escape pod`) to launch the pod and successfully escape!
