The Wizard's Castle, aka Orb Of Zot is a classic text adventure that has been around in various forms since 1980. Originally in BASIC, it has been rewritten in C for maximum compatibility and is now available in the FunHouse game collection.

The game is turn-based and takes place in an 8 x 8 x 8 dungeon that is randomly stocked with monsters, treasure, and various other items. Goals involve fighting monsters, recovering magic items, avoiding traps, and finding the fabled Orb of Zot.

---

# GAMEPLAY GUIDE & COMMANDS

## 1. Character Setup
Before entering the dungeon, you must choose:
- **Race/Species**: **Hobbit**, **Elf**, **Human**, or **Dwarf** (each has different starting attributes and limits).
- **Sex/Gender**: **Male** or **Female**.
- **Armor**: **Leather**, **Chainmail**, **Plate**, or **None**.
- **Weapon**: **Dagger**, **Mace**, **Sword**, or **None**.
- **Attribute Points**: Allocate your remaining starting gold to increase your **Strength**, **Intelligence**, and **Dexterity**.

---

## 2. Dungeon Map Symbols
The dungeon is represented by an 8x8 grid on each of the 8 levels. The symbols are:
- `.` **Empty Room**: Nothing here.
- `E` **Entrance/Exit**: Where you start and can leave to win if you have the Orb.
- `U` **Stairs Up**: Move up one dungeon level.
- `D` **Stairs Down**: Move down one dungeon level.
- `P` **Magic Pool**: Drink to get random benefits or race transformations.
- `C` **Chest**: Open it to find gold or traps.
- `G` **Gold Pieces**: Pick up currency.
- `F` **Flares**: Pick up items to light up adjacent rooms.
- `W` **Warp**: Stepping here teleports you to a random coordinate.
- `S` **Sinkhole**: Drop down to the next level.
- `O` **Crystal Orb**: Gaze to reveal locations of items or hazards.
- `B` **Book**: Open to read for magic/curses.
- `V` **Vendor**: Trade gold for armor, weapons, or stats.
- `T` **Treasure**: Valuable artifacts with magical properties.
- `M` **Monster**: Hostile creature occupying the room.

---

## 3. General Commands
During exploration, use these single-character commands:
- **N** (North) / **E** (East) / **S** (South) / **W** (West): Move in a cardinal direction.
- **U** (Up) / **D** (Down): Ascend or descend stairs.
- **L** (Look): Display the contents of the current room again.
- **M** (Map): View the explored map of the current level.
- **H** (Help): Show the command help menu.
- **F** (Flare): Throw a flare to reveal contents of all adjacent rooms.
- **C** (Catch): Catch a frog or other small creature in the room.
- **K** (Kiss): Kiss a frog (can have wild random effects, both helpful and harmful).
- **I** (Drink): Take a drink from a magic pool.
- **R** (Release): Release a caught frog.
- **O** (Open): Open a chest or read a book in the room.
- **G** (Gaze): Gaze into a crystal orb (reveals dungeon secrets or blinds you).
- **T** (Teleport): Teleport to any level/row/column coordinate.
- **Z** (Zot): Cast a spell or interact with the Orb.
- **Q** (Quit): Exit the game and calculate your final score.

---

## 4. Combat Commands
When you run into a monster (`M`), you enter combat:
- **A** (Attack): Strike the monster with your weapon.
- **B** (Bribe): Offer gold or treasures to get the monster to let you pass.
- **C** (Cast): Cast a spell using your intelligence. Spell types:
  - **F** (Fireball): Cast a high-damage blast.
  - **W** (Web): Entangle the monster to slow it down.
- **R** (Retreat): Run away to a safe adjacent room.

---

## 5. Monsters & Foes
The dungeon is guarded by several monsters of increasing difficulty:
- **Kobold**, **Orc**, **Goblin**, **Ogre**, **Troll**, **Harpy**, **Cyclops**, **Minotaur**, **Gargoyle**, **Chimera**, **Balrog**, and **Dragon**.

---

## 6. Treasures & Magical Properties
There are 8 legendary treasures. Collecting them grants permanent passive benefits:
- **Red Ruby**: Protects you from the **Lethargy** curse.
- **Norn Stone**: No magical effect, but highly valuable.
- **Pale Pearl**: Protects you from the **Leech** curse (prevents gold draining).
- **Opal Eye**: Cures and protects against **Blindness**.
- **Green Gem**: Protects you from the **Forgetfulness** curse (prevents forgetting mapped rooms).
- **Blue Flame**: Allows you to safely **Dissolve Books** (prevents book-opening curses).
- **Palantir**: No magical effect.
- **Silmaril**: No magical effect.

---

# HISTORY

Many cycles ago, I owned a Commodore 128. The 128 was the last and greatest of the 8-bit era of computing and had many different capabilities; one of which was a full implementation of CP/M. CP/M was well on its way out, while MS-DOS was on its way in, which made locating CP/M software difficult. Fortunately, I had a friend with an Osborne with which to trade software. One of the disks had a BASIC game called ZOT.BAS, otherwise known as The Wizard's Castle.

I enjoyed this game a great deal. Although it was completely text-based and non-graphical, it was challenging with good repeat playability. I later copied it to MS-DOS and then on to AmigaBASIC.

Many cycles passed. BASIC and the systems that used it faded into obscurity. Hard drives lived and died.

Then recently, while reminiscing about the game and wishing I could play it again, I had an idea: WHY NOT DO SOMETHING ABOUT IT? DUH! And thus my current project was born.

I decided to rewrite the game in C, for maximum compatibility with just about every system I own. I also made the decision not to add improvements such as graphics and sound, but to remain as faithful as possible to the look and feel of the original. Such enhanced and updated versions already exist.

I needed a listing of the BASIC program to get started, which is when I discovered that the game I had played was not, in fact, the original Wizard's Castle! The original was written by Joseph R. Power for the Exidy Sorcerer and appeared in the July/August 1980 issue of Recreational Computing Magazine. The version I was used to playing was an Enhanced version written by Verne R. Walrafen in 1984. This was the version that I wanted to convert, but I was unable to find any listing of it on the Internet. Well, good thing I still have my Commodore 128, and my original disk is still in readable shape!

A few cycles later, and The Wizard's Castle has come back to life. Enjoy looking for the Orb Of Zot, watch what you kiss, be careful when opening books and try not to step on any frogs!

Leslie Bird
