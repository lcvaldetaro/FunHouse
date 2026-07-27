package jni

import com.funhouse.shared.common.AppData
import com.funhouse.shared.common.jni.BaseKotlinGame
import com.funhouse.shared.common.ECHO_PREFIX
import club.gepetto.GcLog
import kotlinx.coroutines.*
import com.funhouse.shared.common.utils.GcInputQueue

class SecretForestKotlin : BaseKotlinGame() {

    private var gameJob: Job? = null
    private val commandQueue = GcInputQueue<String>()
    private var currentRoom = 0
    private var gameStatus = 0
    private val inventory = BooleanArray(23) { false }

    data class Room(
        val roomId: Int,
        val roomName: String,
        val roomDesc: String,
        val walkTo: IntArray,
        val walkDesc: Array<String?>,
        val searchDesc: String?
    )

    data class Item(
        val itemId: Int,
        val itemName: String,
        val itemAdj: String?,
        val itemDescFloor: String,
        val itemDescExam: String,
        var hidden: Boolean,
        val takeable: Boolean,
        var location: Int
    )

    data class Event(
        val eventId: Int,
        val item1: Int,
        val item2: Int,
        val roomId: Int,
        var triggerable: Boolean,
        val eventType: Int,
        val eventDir: Int,
        val eventAttr1: Int,
        val eventAttr2: Int,
        val eventLink: Int,
        val quit: Boolean,
        val eventDesc: String?
    )

    companion object {
        private const val OPEN = 0
        private const val BREAK = 1
        private const val CREATE = 2
        private const val STORY = 3
        private const val TAKE = 4

        private const val NORTH = 0
        private const val EAST = 1
        private const val SOUTH = 2
        private const val WEST = 3
    }

    private val locations = arrayOf(
        Room(0, "Village Entrance", "You stand at the entrance to a quiet village. To the north, you see a row of\nhouses. To the east is a large field next to a forest.", intArrayOf(1, 7, -1, -1), arrayOf("You walk north.", "You walk east.", null, null), null),
        Room(1, "Edge of the Village", "You are near a row of small houses on the edge of a quiet village. More houses\nare to the north, while the village entrance is to the south.", intArrayOf(2, -1, 0, -1), arrayOf("You walk north.", null, "You walk south.", null), null),
        Room(2, "Quiet Neighborhood", "You are in a quiet, residential neighborhood. You see people lurking behind\npulled curtains out of the corner of your eye - you're being watched. There\nis a row of trash bins near the street. The village stretches both north and\nsouth.", intArrayOf(3, -1, 1, -1), arrayOf("You walk north.", null, "You walk south.", null), "You dig into the trash bins."),
        Room(3, "Shopping District", "You are outside of a row of shops. You can hear the banging of a hammer on an\nanvil from an blacksmith's shop to the east. Next to the shop is a pile of old\nmetal junk. To the west is an inn called The Sleepy Woodsman.", intArrayOf(-1, 6, 2, 4), arrayOf(null, "You enter the blacksmith's shop.", "You walk south.", "You enter the inn."), "You rummage through the junk pile."),
        Room(4, "The Sleepy Woodsman", "The cozy inn has a welcome desk near the door. A staircase leads north to the\nrooms upstairs. The exit is to the east.", intArrayOf(5, 3, -1, -1), arrayOf("You walk up the stairs.", "You leave the inn.", null, null), null),
        Room(5, "Upstairs at the Inn", "You are in a hallway on the second floor of the inn. There are four rooms here.\nAll the doors are currently closed. There are tables standing next to the doors\nwith assorted object and letters. A staircase leads downstairs to the south.", intArrayOf(-1, -1, 4, -1), arrayOf(null, null, "You walk down the stairs.", null), "You stealthily inspect the items on the tables."),
        Room(6, "The Blacksmith's Shop", "You enter the blacksmith's shop. There are intricate works of shaped metal all\naround the room - high quality weapons, armor, and tools. It looks expensive!", intArrayOf(-1, -1, -1, 3), arrayOf(null, null, null, "You exit the shop."), "You browse around the shop, looking for merchandise."),
        Room(7, "The Field Near the Village", "You are in a field. There is an entrance to a village to the west. To the east\nis a path through a tall grass field that leads to a forest.", intArrayOf(-1, 8, -1, 0), arrayOf(null, "You walk east.", null, "You walk west."), null),
        Room(8, "Tall Grass Field", "You walking on a path surrounded by dense, tall grass. There might be snakes\nlurking in the grass. A village can be seen in the distance to the west. A\nforest can be seen in the distance to the east.", intArrayOf(-1, 9, -1, 7), arrayOf(null, "You walk east.", null, "You walk west."), "You crawl through the grass, carefully searching."),
        Room(9, "Field Near the Cave and Forest", "You are standing in a field. A path to the south leads into a dark cave. A path\nto the east leads to the outskirts of a forest.", intArrayOf(-1, 15, 10, 8), arrayOf(null, "You walk east.", "You walk down the path into the cave.", "You walk west."), null),
        Room(10, "Cave Entrance", "You are standing inside a dark cave. Bright light can be seen through the\nentrance to the north. The cave stretches into darkness south.", intArrayOf(9, -1, 11, -1), arrayOf("You leave the cave.", null, "You walk deeper south into the cave.", null), null),
        Room(11, "Cave", "You are deep in a cave. It is dark here, but you can see light coming from the\nnorth. The cave stretches into darkness south.", intArrayOf(10, -1, 12, -1), arrayOf("You walk north.", null, "You slowly walk deeper into the cave.", null), null),
        Room(12, "Cave-In", "You are deep in a cave. A cave-in blocks a tunnel to the south. You can hear\nrunning water coming somewhere from the west.", intArrayOf(11, -1, -1, -1), arrayOf("You walk north.", null, null, "You climb through the passage west."), null),
        Room(13, "Underground River", "You are in a dark room in the cave. You feel wind and water spraying your face\nas you stand beside an underground river. You see pale, blue lights coming from\nthe south, accessible if you follow the river. A small passage opens to the\neast.", intArrayOf(-1, 12, 14, -1), arrayOf(null, "You climb through the passage east.", "You follow the river south.", null), null),
        Room(14, "Treasure Chamber", "You are in a room lined with strange, pale blue torches. There are piles of\ntreasure, including hundreds of shiny gold coins, lining the room. The only way\nto leave is by following the river north.", intArrayOf(13, -1, -1, -1), arrayOf("You follow the river north.", null, null, null), "You search through the treasure piles."),
        Room(15, "Forest Outskirts", "You are at the edge of the forest. There is a grassy field to the west. The\nforest gets thicker to the north and to the east, but there is a path in both\ndirections.", intArrayOf(16, 21, -1, 9), arrayOf("You walk north.", "You walk east.", null, "You walk west."), null),
        Room(16, "Forest", "You are on a forest path. The path stretches both north and south, and there\nare fewer trees to the south. There is a dense thicket to the east that may be\ntraversable.", intArrayOf(17, 23, 15, -1), arrayOf("You walk north.", "You walk east into the thicket and get lost in the forest for awhile.", "You walk south.", null), null),
        Room(17, "Near the Clearing", "You are on a path at the edges of the forest. There is a clearing to the north\nwhich appears to be full of stone columns. To the south, the forest is slightly\nmore dense.", intArrayOf(18, -1, 16, -1), arrayOf("You walk north.", null, "You walk south.", null), null),
        Room(18, "Clearing in Forest", "You are in a clearing in the forest. There are many stone podiums and columns in\nthe area. The stone structures are lined with carved symbols, but you do not\nknow what they mean. There is a path south that leads back into the woods.", intArrayOf(-1, -1, 17, -1), arrayOf("You climb down the ladder and head north through the tunnel. At the end of the\ntunnel is another ladder, which emerges into another clearing in the forest.", null, "You walk south.", null), null),
        Room(19, "North Clearing", "You are in the middle of a clearing in the forest. There are several stone\npodiums in area. Each of the podiums have intricate carvings lining them. This\nmust be a truly ancient area. There is a tunnel that leads south, and a path\nthat leads back into the woods to the north.", intArrayOf(20, -1, 18, -1), arrayOf("You walk north.", null, "You climb down the ladder and head south through the tunnel. At the end of the\ntunnel is another ladder, which emerges into another clearing in the forest.", null), "You check all the podiums."),
        Room(20, "Deep Forest", "You are walking on a forest path. The path runs south into a clearing. There are\nthick, impassable thickets north and south, and a steep rocky wall to the east.", intArrayOf(-1, -1, 19, -1), arrayOf(null, "You climb the ladder up the rockslide.", "You walk south", null), null),
        Room(21, "Forest Path", "You are walking through the forest. Dense underbrush is all around, but you are\non a clear footpath that runs east and west.", intArrayOf(-1, 22, -1, 15), arrayOf(null, "You walk east.", null, "You walk west."), null),
        Room(22, "Curve in the Forest Path", "You are walking along the forest path. To the north, the forest appears to be\nmore dense. To the west, the forest is less dense.", intArrayOf(23, -1, -1, 21), arrayOf("You walk north.", null, null, "You walk west."), null),
        Room(23, "The Familiar Copse", "You are walking along a path in the forest. The path runs north and south. The\npath is split in the middle by a grassy patch with four trees growing closely\ntogether.", intArrayOf(24, -1, 22, -1), arrayOf("You walk north.", null, "You walk south.", null), null),
        Room(24, "Dark Forest", "You are in a dark portion of the forest where the trees are blocking out light\nfrom the sky. There is dense underbrush that makes it very hard to see through\nthe trees, but there is a footpath that runs south and east.", intArrayOf(-1, -1, 23, -1), arrayOf(null, "You walk east.", "You walk south.", null), null),
        Room(25, "Dark Forest", "You are on a path through the forest that runs west and north. The trees all\naround you are thick and hard to see through, and the air feels very still. You\ncan't see anything east or south, only darkness.", intArrayOf(26, -1, -1, 24), arrayOf("You walk north.", null, null, "You walk west."), null),
        Room(26, "Thicket Dead End", "You are walking in the forest. The forest path from the south seems to end here,\nwith dense overgrowth in all other directions.", intArrayOf(27, 23, 25, 17), arrayOf("You walk into the north thicket. You get lost in the forest for awhile, but the\nthicket clears and you find a the footpath again.", "You walk into the east thicket. You get lost in the forest for awhile.", "You walk south.", "You walk into the west thicket. You get lost in the forest for awhile."), null),
        Room(27, "Light Forest", "You are walking in a light section of the forest. The trees have thinned out\nhere. To the south, you see a dense thicket of trees. A path leads to the north,\nwhere you see a mountain rising above the trees.", intArrayOf(28, -1, 26, -1), arrayOf("You walk north.", null, "You walk south.", null), null),
        Room(28, "Light Forest Near the Tower", "You are walking along a forest path, which runs south and west from here. There\nis a mountain with small, carved alcoves that blocks the way to the north. The\npath leads west to a large tower to the west which leads up to a bluff at the\ntop the mountain.", intArrayOf(-1, -1, 27, 29), arrayOf(null, null, "You walk south.", "You walk west."), "You inspect the inside of the alcoves."),
        Room(29, "Base of the Tower", "You are standing at the base of a large tower that leads to the top of the\nmountain bluff, which you can enter to the west. There are small, carved alcoves\nin the mountains to the north. A path leads into the forest to the east.", intArrayOf(-1, 28, -1, -1), arrayOf(null, "You walk east.", null, "You enter the tower and ascend the steps."), "You inspect the inside of the alcoves."),
        Room(30, "Bluff Overlooking the Forest", "You are at the top of a bluff at the northmost point of the forest. Looking\naround, you can see the entire forest, and to the west, you can see the small\ntown. There is a rocky hill to the west, a twisting stone path to the north, and\nthe entrance to a tower to the east.", intArrayOf(31, -1, -1, 20), arrayOf("You walk north.", "You enter the tower and descend the steps.", null, "You try to climb down the rocky hill, but quickly lose control and slide to the\nbottom! It was a little steeper than you thought."), null),
        Room(31, "Twisting Path", "You are walking on a twisting, cobblestone path that runs north and south. To\nthe north, you see a giant stone gate. To the south is a tower and bluff that\noverlooks the forest.", intArrayOf(32, -1, 30, -1), arrayOf("You walk north.", null, "You walk south.", null), null),
        Room(32, "The Stone Gate", "You are at the northmost part of a twisting cobblestone path. There is a giant\nstone gate that leads to the north. The gate looks old, like it was built many\ncenturies ago.", intArrayOf(-1, -1, 31, -1), arrayOf("You walk through the gate.", null, "You walk south.", null), null),
        Room(33, "Hidden Temple", "You enter an ancient temple. Tall stone walls rise up around you on all sides,\nno ceiling. There is a two-story tall gold throne sitting beside the north wall.", intArrayOf(-1, -1, 32, -1), arrayOf(null, null, "You exit through the gate.", null), "You walk around the temple, look behind the throne, and even crawl between the\nstatue's legs, looking for treasure.")
    )

    private val items = arrayOf(
        Item(0, "blacksmith", "old", "There is an old blacksmith here, shaping metal on an anvil with his hammer.", "The blacksmith watches you as you browse around his shop, but he keeps working.", false, false, 6),
        Item(1, "blacksmith", "old", "There is an old blacksmith here, carefully inspecting a gold owl statuette.", "The blacksmith mutters to himself happily as he looks at his treasure.", false, false, -1),
        Item(2, "sign", "wooden", "There is a wooden sign near the cave entrance.", "The sign says 'DO NOT ENTER. THIS CAVE IS EMPTY.'", false, false, 9),
        Item(3, "statue", "stone", "There is an enormous stone statue of a warrior blocking the path to the east.", "The statue of the warrior is an elongated sphere, giving the impression that the\nancient warrior is a giant egg. He is holding a spear in his left hand and a\nsmall, round shield in his right hand. There is an inscription that says:\nON Y TH  BR VE MA   ASS.", false, false, 24),
        Item(4, "kiosk", "stone", "There is a stone kiosk with a blue stone on top in the middle of the clearing.", "The kiosk has a pyramid-shaped blue stone on top. There is a small flat hole near\nthe stone.", false, false, 18),
        Item(5, "kiosk", "stone", "There is a stone kiosk with a green stone on top in the middle of the clearing.", "The kiosk has a sphere-shaped green stone on top. There is a small flat hole\nnear the stone.", false, false, -1),
        Item(6, "kiosk", "stone", "There is a stone kiosk with a red stone on top in the middle of the clearing.", "The kiosk has a cube-shaped red stone on top. There is a small flat hole near\nthe stone.", false, false, -1),
        Item(7, "path", "tunnel", "There is an underground tunnel path that leads to the north.", "You can enter the path by climbing down a ladder.", false, false, -1),
        Item(8, "ladder", "extended", "There is an extended ladder propped up against the rockslide.", "The ladder is secure enough to climb.", false, false, -1),
        Item(9, "gate", "locked", "There is a locked gate blocking the lower entrance to tower.", "The gate is impassable, but there is a large keyhole in it.", false, false, 29),
        Item(10, "gate", "locked", "There is a locked gate blocking the upper entrance to the tower.", "The gate is impassible, but there is a large keyhole in it.", false, false, 30),
        Item(11, "lever", "stone", "There is a stone lever near the gate.", "The stone lever is large, heavy, and old.", false, false, 32),
        Item(12, "statue", "golden", "There is a golden statue of a bird-man sitting on the throne. He looks like an\nancient god, with large golden wings and a feathered headdress. His right hand\nholds an egg-shaped ruby, and his grasping left hand is empty.", "The giant bird-man statue sits silently on the throne.", false, false, 33),
        Item(13, "coin", "blue", "There is a blue coin here.", "The coin is carved out of stone and painted blue. There is a triangle-shaped\nhole in the center of it.", false, true, 5),
        Item(14, "coin", "green", "There is a green coin here.", "The coin is carved out of stone and painted green. There is a circle-shaped hole\nin the center of it.", true, true, 28),
        Item(15, "coin", "red", "There is a red coin here.", "The coin is carved out of stone and painted red. There is a square-shaped hole\nin the center of it.", true, true, 8),
        Item(16, "statuette", "gold", "There is a gold statuette here.", "The gleaming statuette looks like a regal owl.", true, true, 14),
        Item(17, "sword", null, "There is a sword here.", "The sword is finely crafted and extra sharp. You have never seen its equal.", false, true, -1),
        Item(18, "hammer", "old", "There is an old hammer here.", "It is an old blacksmith's hammer that has seen better days.", true, true, 3),
        Item(19, "ladder", "collapsible", "There is a collapsible ladder here.", "The ladder can be folded to make it easier to carry.", false, true, 2),
        Item(20, "key", "ancient", "There is an ancient key here.", "The ancient skeleton key is carved with intricate patterns.", true, true, 19),
        Item(21, "hamburger", "gross", "There's a gross hamburger lying on the ground.", "Old and dirty, it's probably best to not eat that.", true, true, 2),
        Item(22, "scepter", "valuable", "There is a valuable scepter here.", "The scepter is heavy and ornate, lined with gold and silver, and topped with a\nshiny gem on top that is as big as your fist.", true, true, 14)
    )

    private val interactions = arrayOf(
        Event(0, 11, -1, 32, true, OPEN, NORTH, 32, 33, 1, true, "You put all your strength into moving the lever. It finally moves and the large\ngate clanks and rumbles loudly as it slides opens. You can now travel north!"),
        Event(1, 11, -1, 32, false, OPEN, NORTH, 32, -1, 0, true, "The lever is surprisingly easy to move. As soon as you touch it, the large gate\nquickly crashes down with a clatter. The way north is blocked!"),
        Event(2, 18, -1, 12, true, BREAK, WEST, 18, -1, 3, false, "You smash the hammer against the west wall. It opened up a path you can crawl\nthrough! But the hammer is now broken and useless. You throw it away."),
        Event(3, 18, -1, 12, false, OPEN, WEST, 12, 13, -1, true, null),
        Event(4, 19, -1, 20, true, BREAK, EAST, 19, -1, 5, false, "You unfold the ladder and place it on the steep rockslide to the east. It looks\nlike you can climb up it, but you give it a shake to make sure it is secure\nenough to climb. You can now travel east!"),
        Event(5, 19, -1, 20, false, CREATE, EAST, 8, 20, 6, false, null),
        Event(6, 19, -1, 20, false, OPEN, EAST, 20, 30, -1, true, null),
        Event(7, 20, 9, 29, true, BREAK, WEST, 20, -1, 8, false, "You slide the key into the keyhole in the gate. As you turn it, it clicks\nloudly. The gate swings open, allowing you to ascend the tower to the west!"),
        Event(8, 20, 9, 29, false, BREAK, WEST, 9, -1, 9, false, null),
        Event(9, 20, 9, 29, false, OPEN, WEST, 29, 30, 10, false, null),
        Event(10, 20, 9, 29, false, OPEN, EAST, 30, 29, 11, false, null),
        Event(11, 20, 9, 29, false, BREAK, WEST, 10, -1, -1, true, null),
        Event(12, 20, 10, 30, true, BREAK, WEST, 20, -1, 13, false, "You slide the key into the keyhole in the gate. As you turn it, it clicks\nloudly. The gate swings open, allowing you to ascend the tower to the east!"),
        Event(13, 20, 10, 30, false, BREAK, WEST, 10, -1, 14, false, null),
        Event(14, 20, 10, 30, false, OPEN, WEST, 29, 30, 15, false, null),
        Event(15, 20, 10, 30, false, OPEN, EAST, 30, 29, 16, false, null),
        Event(16, 20, 10, 30, false, BREAK, WEST, 9, -1, -1, true, null),
        Event(17, 17, 3, 24, true, BREAK, EAST, 3, -1, 18, false, "You swing the giant sword at the statue. As you slice the stone shield, the\nstatue begins to collapse. You keep attacking it until it completely falls\napart. As the statue lies in ruin, the path to the east is now open."),
        Event(18, 17, 3, 24, false, OPEN, EAST, 24, 25, -1, true, null),
        Event(19, 13, 4, 18, true, BREAK, NORTH, 13, 0, 20, false, "You slide the blue coin into the slot at the top of the kiosk. The kiosk slowly\nrumbles, and its inner stone workings begin to shift around. The blue pyramid\nslides away beneath a hidden compartment and a stone, green sphere shifts into\nthe same position."),
        Event(20, 13, 4, 18, false, BREAK, NORTH, 4, 0, 21, false, null),
        Event(21, 13, 4, 18, false, CREATE, NORTH, 5, 18, -1, true, null),
        Event(22, 14, 5, 18, true, BREAK, NORTH, 14, 1, 23, false, "You slip the green coin into the slot in the kiosk. Once again, the kiosk starts\nrumbling loudly as its inner workings come to life. The green sphere disappears\ninto a hidden panel and a rectangular red stone takes its place."),
        Event(23, 14, 5, 18, false, BREAK, NORTH, 5, 1, 24, false, null),
        Event(24, 14, 5, 18, false, CREATE, NORTH, 6, 18, -1, true, null),
        Event(25, 15, 6, 18, true, BREAK, NORTH, 15, 0, 26, false, "Once again, the kiosk comes to life as soon as you slide the red coin into the\nslot. As the red stone slides into the inside, you hear a haunting melody come\nfrom within the kiosk. As the song slowly fades away, the kiosk slides into a\nhidden panel in the stone on which it sat. The hole in the stone slab remains,\nrevealing a ladder down into an underground tunnel that leads north."),
        Event(26, 15, 6, 18, false, BREAK, NORTH, 6, 0, 27, false, null),
        Event(27, 15, 6, 18, false, OPEN, NORTH, 18, 19, 28, false, null),
        Event(28, 15, 6, 18, false, CREATE, NORTH, 7, 18, -1, true, null),
        Event(29, 16, 0, 6, true, BREAK, EAST, 16, -1, 30, false, "The blacksmith sees the statuette and his eyes get wide.\n\n\"Please! I must have that! Take this sword! I crafted it myself! It is the most\nfinely made sword you will ever see!\"\n"),
        Event(30, 16, 0, 6, false, TAKE, EAST, 17, 6, 31, false, "You are impressed with the quality of the sword and think it is an even trade.\nThe blacksmith takes the statuette and hands you the sword carefully."),
        Event(31, 16, 0, 6, false, BREAK, EAST, 0, 6, 32, false, null),
        Event(32, 16, 0, 6, false, CREATE, EAST, 1, 6, -1, true, null),
        Event(33, 14, 4, 18, true, STORY, NORTH, 0, -1, 33, true, "The green coin does not fit into the blue slot."),
        Event(34, 15, 4, 18, true, STORY, NORTH, 0, -1, 34, true, "The red coin does not fit into the blue slot."),
        Event(35, 15, 5, 18, true, STORY, NORTH, 0, -1, 35, true, "The red coin does not fit into the green slot."),
        Event(36, 17, 1, 6, true, STORY, EAST, 0, -1, 36, true, "You shouldn't do that."),
        Event(37, 18, 0, 6, true, STORY, EAST, 0, -1, 37, true, "You shouldn't do that."),
        Event(38, 22, 12, 33, true, OPEN, SOUTH, 33, -1, 40, true, "As you approach the bird-man statue with the scepter, you see his eyes gleam red\nfor a moment - a warning? A loud crashing is heard behind you and the gate has\nclosed! You are still holding the scepter... are you sure this is a good idea?"),
        Event(39, 22, 12, 33, true, STORY, SOUTH, -2, -1, -1, true, "You place the scepter into the empty hand of the statue. As soon as you do, his\neyes and the gem in the scepter gleam bright red. Supernaturally fast, he stands\nup, knocking you back.\n\n\"AT LAST, I HAVE BEEN RELEASED FROM MY SLUMBER. THE WORLD WILL NOW SUFFER. THE\nWORLD WILL NOW BE MINE!\"\n\nHe walks towards the western wall and climbs over it in a quick motion, leaving\nyou behind in the Hidden Temple."),
        Event(40, 17, 12, 33, true, OPEN, SOUTH, 33, -1, 38, true, "You approach the bird-man statue, brandishing the sword. His eyes gleam red for\na moment - a warning? A loud crashing is heard behind you and the gate has\nclosed! You are still holding the sword... Are you sure this is a good idea?"),
        Event(41, 17, 12, 33, true, STORY, SOUTH, -3, -1, -1, true, "You raise the sword and lower it with a crash onto the statue. As you are about\nto strike, the statue supernaturally moves to defend itself, resulting in you\naccidentally striking the ruby in its right hand. Both the sword and the ruby\nexplode into a million pieces!\n\nAs soon as the ruby is destroyed, the statue crumbles to the floor and melts\naway before your eyes. A hidden chamber behind the throne slowly opens to reveal\nmountains of gold and treasure, and the large gate to the temple opens!"),
        Event(42, 21, 0, 6, true, STORY, NORTH, 0, -1, 42, true, "He doesn't want to eat that."),
        Event(43, 21, 1, 6, true, STORY, NORTH, 0, -1, 43, true, "He doesn't want to eat that."),
        Event(44, 21, 12, 33, true, STORY, NORTH, 0, -1, 44, true, "He doesn't want to eat that."),
        Event(45, 21, 3, 24, true, STORY, NORTH, 0, -1, 45, true, "He doesn't want to eat that.")
    )

    override fun start() {
        GcLog.d("SecretForestKotlin.start() called")
        gameJob?.cancel()
        commandQueue.clear()
        gameJob = CoroutineScope(Dispatchers.Default).launch {
            greetings()
            runGameLoop()
        }
    }

    override fun start(gameNickName: String) {
        start()
    }

    override fun stop() {
        super.stop()
        gameJob?.cancel()
    }

    override fun sendCommand(command: String): Int {
        if (command.trim().equals("about", ignoreCase = true)) {
            myPrintf("${secretforestkotlin.utils.secretForestGame.about}\n")
            return 0
        }

        myPrintf("${ECHO_PREFIX}${command}\n")
        commandQueue.put(command)
        return 0
    }

    private suspend fun getLine(): String {
        return commandQueue.take()
    }

    private suspend fun readCleanLine(): String {
        val raw = getLine()
        return raw.filter { it.isLetter() || it.isWhitespace() }.uppercase()
    }

    private suspend fun isOkayQuit(): Boolean {
        myPrintf("\nReally quit? (y/n) ")
        val resp = getLine().trim().lowercase()
        return resp.startsWith("y")
    }

    private suspend fun runGameLoop() {
        // Welcome to the game
        myPrintf("Welcome to SecretForest in the Forest (game engine nag-20180703)\n\n")
        myPrintf("What lies in the mysterious forest on the edge of a small village? Myths and\n")
        myPrintf("legends have been told for centuries, but no-one has yet figured it out. Will\n")
        myPrintf("you be the one to finally solve this riddle? Good luck!\n\n")
        myPrintf("To learn how to play, type: help\n")

        myPrintf("\n%s\n", locations[currentRoom].roomName)
        lookRoom()
        gameStatus = 0

        while (true) {
            val line = getLine()
            if (line.isNotEmpty()) {
                parseInput(line)
                if (gameStatus == -1) {
                    myPrintf("\n\nGAME OVER: The mystery of the forest remains unsolved!\n\n")
                    break
                } else if (gameStatus == -2) {
                    myPrintf("\n\nYOU GOT THE BAD ENDING\n\n")
                    myPrintf("How could you have known you were going to release an ancient horror upon the\n")
                    myPrintf("people of the world? Nobody can really blame you, but as the giant statue\n")
                    myPrintf("rampages across the world, killing all who oppose him, subjugating everyone\n")
                    myPrintf("else, you are trapped in the Hidden Temple with no way out.\n\n")
                    myPrintf("GAME OVER: BETTER LUCK NEXT TIME!\n\n")
                    break
                } else if (gameStatus == -3) {
                    myPrintf("\n\nYOU GOT THE GOOD ENDING\n\n")
                    myPrintf("You have solved the secret of the Hidden Temple! By destroying the giant statue,\n")
                    myPrintf("you have uncovered a cache of riches beyond your wildest dreams! What will you\n")
                    myPrintf("do with your newfound wealth? Become a king? Become a god? The choice is up to\n")
                    myPrintf("you! But first, you need to find a cart...\n\n")
                    myPrintf("CONGRATULATIONS!\n\n")
                    break
                }
            }
        }
    }

    private suspend fun parseInput(line: String) {
        val allWords = line.lowercase().trim()
        val words = allWords.split(Regex("\\s+")).filter { it.isNotEmpty() }
        if (words.isEmpty()) return

        val cmd = words[0]

        when (cmd) {
            "quit", "exit", "q" -> {
                if (isOkayQuit()) {
                    gameStatus = -1
                }
            }
            "help", "h" -> {
                displayHelp()
            }
            "look" -> {
                if (words.size == 1) {
                    lookRoom()
                } else {
                    val word1 = words.getOrNull(1)
                    if (word1 == "at") {
                        val word2 = words.getOrNull(2)
                        if (word2 == "the") {
                            lookItem(words.getOrNull(3), words.getOrNull(4))
                        } else {
                            lookItem(words.getOrNull(2), words.getOrNull(3))
                        }
                    } else {
                        lookItem(words.getOrNull(1), words.getOrNull(2))
                    }
                }
            }
            "go", "walk", "move" -> {
                move(words.getOrNull(1))
            }
            "north", "east", "south", "west" -> {
                move(cmd)
            }
            "n" -> move("north")
            "e" -> move("east")
            "s" -> move("south")
            "w" -> move("west")
            "take", "get" -> {
                val word1 = words.getOrNull(1)
                if (word1 == "the") {
                    takeItem(words.getOrNull(2), words.getOrNull(3))
                } else {
                    takeItem(words.getOrNull(1), words.getOrNull(2))
                }
            }
            "drop" -> {
                val word1 = words.getOrNull(1)
                if (word1 == "the") {
                    dropItem(words.getOrNull(2), words.getOrNull(3))
                } else {
                    dropItem(words.getOrNull(1), words.getOrNull(2))
                }
            }
            "search", "find" -> {
                search()
            }
            "i", "inventory", "inv" -> {
                listInv()
            }
            "use" -> {
                useItems(words)
            }
            else -> {
                myPrintf("\nUnknown command '%s'", cmd)
                for (i in 1 until minOf(words.size, 4)) {
                    myPrintf(" %s", words[i])
                }
                myPrintf("'.\n")
            }
        }
    }

    private fun lookRoom() {
        myPrintf("\n%s\n", locations[currentRoom].roomDesc)
        roomItems()
        roomExits()
    }

    private fun roomItems() {
        for (item in items) {
            if (item.location == currentRoom && !item.hidden) {
                myPrintf("%s\n", item.itemDescFloor)
            }
        }
    }

    private fun roomExits() {
        val dirs = arrayOf("north", "east", "south", "west")
        var count = 0
        myPrintf("Exits:  ")
        for (i in 0 until 4) {
            if (locations[currentRoom].walkTo[i] != -1) {
                myPrintf("%s  ", dirs[i])
                count++
            }
        }
        if (count == 0) {
            myPrintf("none")
        }
        myPrintf("\n")
    }

    private fun move(direction: String?) {
        myPrintf("\n")
        if (direction == null) {
            myPrintf("Walk where?\n")
            return
        }

        val dir = when (direction) {
            "north" -> 0
            "east" -> 1
            "south" -> 2
            "west" -> 3
            else -> {
                myPrintf("Walk where?\n")
                return
            }
        }

        val nextRoom = locations[currentRoom].walkTo[dir]
        if (nextRoom != -1) {
            myPrintf("%s\n", locations[currentRoom].walkDesc[dir] ?: "")
            currentRoom = nextRoom
            myPrintf("\n%s\n", locations[currentRoom].roomName)
            lookRoom()
        } else {
            myPrintf("You cannot walk %s.\n", direction)
        }
    }

    private fun search() {
        myPrintf("\n")
        val hasDesc = locations[currentRoom].searchDesc != null
        if (hasDesc) {
            myPrintf("%s ", locations[currentRoom].searchDesc ?: "")
        } else {
            myPrintf("You carefully search the area. ")
        }

        var count = 0
        for (i in items.indices) {
            val item = items[i]
            if (item.location == currentRoom && item.hidden) {
                if (count > 0) {
                    myPrintf("\nYou also found the ")
                } else {
                    myPrintf("You found the ")
                }
                count++
                if (item.itemAdj != null) {
                    myPrintf("%s ", item.itemAdj)
                }
                myPrintf("%s! ", item.itemName)
                item.hidden = false
                item.location = -1
                inventory[i] = true
            }
        }
        if (count == 0) {
            myPrintf("You didn't find anything.\n")
        } else {
            myPrintf("\n")
        }
    }

    private fun listInv() {
        myPrintf("\nInventory:\n")
        var count = 0
        for (i in items.indices) {
            if (inventory[i]) {
                count++
                myPrintf("\t")
                if (items[i].itemAdj != null) {
                    myPrintf("%s ", items[i].itemAdj ?: "")
                }
                myPrintf("%s\n", items[i].itemName)
            }
        }
        if (count == 0) {
            myPrintf("\tnothing.\n")
        }
    }

    private fun assignName(word1: String?, word2: String?): Pair<String?, String?> {
        return if (word2 == null) {
            Pair(null, word1)
        } else {
            Pair(word1, word2)
        }
    }

    private fun inRoom(adj: String?, name: String?): Int {
        if (name == null) return -1
        var count = 0
        var itemNum = -1
        for (i in items.indices) {
            val item = items[i]
            if (item.itemName == name && item.location == currentRoom && !item.hidden) {
                if (adj == null) {
                    count++
                    itemNum = i
                } else if (item.itemAdj != null && adj == item.itemAdj) {
                    count = 1
                    itemNum = i
                    break
                }
            }
        }
        return when {
            count == 0 -> -1
            count > 1 -> -2
            else -> itemNum
        }
    }

    private fun inInv(adj: String?, name: String?): Int {
        if (name == null) return -1
        var count = 0
        var itemNum = -1
        for (i in items.indices) {
            val item = items[i]
            if (item.itemName == name && inventory[i]) {
                if (adj == null) {
                    count++
                    itemNum = i
                } else if (item.itemAdj != null && adj == item.itemAdj) {
                    count = 1
                    itemNum = i
                    break
                }
            }
        }
        return when {
            count == 0 -> -1
            count > 1 -> -2
            else -> itemNum
        }
    }

    private fun uniqueItem(adj: String?, name: String?): Int {
        if (name == null) return -1
        val room = inRoom(adj, name)
        val inv = inInv(adj, name)
        return when {
            (room >= 0 && inv >= 0) || room == -2 || inv == -2 -> -2
            room == -1 && inv == -1 -> -1
            else -> if (inv == -1) room else inv
        }
    }

    private fun takeItem(word1: String?, word2: String?) {
        myPrintf("\n")
        if (word1 == null) {
            myPrintf("Take what?\n")
            return
        }

        val (adj, name) = assignName(word1, word2)
        val itemNum = inRoom(adj, name)
        if (itemNum == -1) {
            myPrintf("There is no ")
            if (adj != null) myPrintf("%s ", adj)
            myPrintf("%s here.\n", name ?: "")
            return
        } else if (itemNum == -2) {
            myPrintf("Which %s do you want to take?\n", name ?: "")
            return
        }

        val item = items[itemNum]
        if (item.takeable) {
            item.location = -1
            inventory[itemNum] = true
            myPrintf("You took the ")
            if (item.itemAdj != null) {
                myPrintf("%s ", item.itemAdj)
            }
            myPrintf("%s.\n", name ?: "")
        } else {
            myPrintf("You cannot take the %s!\n", name ?: "")
        }
    }

    private fun dropItem(word1: String?, word2: String?) {
        myPrintf("\n")
        if (word1 == null) {
            myPrintf("Drop what?\n")
            return
        }

        val (adj, name) = assignName(word1, word2)
        val itemNum = inInv(adj, name)
        if (itemNum == -1) {
            myPrintf("You do not have a ")
            if (adj != null) myPrintf("%s ", adj)
            myPrintf("%s.\n", name ?: "")
            return
        } else if (itemNum == -2) {
            myPrintf("Which %s do you want to drop?\n", name ?: "")
            return
        }

        val item = items[itemNum]
        item.location = currentRoom
        inventory[itemNum] = false
        myPrintf("You dropped the ")
        if (item.itemAdj != null) {
            myPrintf("%s ", item.itemAdj)
        }
        myPrintf("%s.\n", name ?: "")
    }

    private fun lookItem(word1: String?, word2: String?) {
        myPrintf("\n")
        if (word1 == null) {
            myPrintf("Look at what?\n")
            return
        }

        val (adj, name) = assignName(word1, word2)
        val itemNum = uniqueItem(adj, name)
        if (itemNum == -2) {
            myPrintf("Which %s?\n", name ?: "")
            return
        } else if (itemNum == -1) {
            myPrintf("There is no ")
            if (adj != null) myPrintf("%s ", adj)
            myPrintf("%s here.\n", name ?: "")
            return
        }

        myPrintf("%s\n", items[itemNum].itemDescExam)
    }

    private fun useItems(input: List<String>) {
        myPrintf("\n")
        val words = mutableListOf<String>()
        for (i in 1 until input.size) {
            val w = input[i]
            if (w == "on" || w == "the") {
                continue
            }
            words.add(w)
        }

        if (words.isEmpty()) {
            myPrintf("Use what?\n")
            return
        }

        val usedItems = IntArray(2) { -1 }
        var j = 0
        var i = 0
        while (i < words.size && j < 2) {
            val w = words[i]
            var curPhrase = uniqueItem(null, w)
            if (curPhrase == -2) {
                myPrintf("Which %s?\n", w)
                return
            } else if (curPhrase == -1) {
                val nextW = words.getOrNull(i + 1)
                curPhrase = uniqueItem(w, nextW)
                if (curPhrase == -1) {
                    myPrintf("There is no %s here.\n", w)
                    return
                }
                i++
            }
            usedItems[j++] = curPhrase
            i++
        }

        interact(currentRoom, usedItems[0], usedItems[1])
    }

    private fun interact(room: Int, item1: Int, item2: Int) {
        for (event in interactions) {
            if (event.roomId == room && event.item1 == item1 && event.item2 == item2 && event.triggerable) {
                when (event.eventType) {
                    OPEN -> {
                        locations[event.eventAttr1].walkTo[event.eventDir] = event.eventAttr2
                    }
                    BREAK -> {
                        inventory[event.eventAttr1] = false
                        items[event.eventAttr1].location = -1
                    }
                    CREATE -> {
                        items[event.eventAttr1].location = event.eventAttr2
                    }
                    STORY -> {
                        gameStatus = event.eventAttr1
                    }
                    TAKE -> {
                        inventory[event.eventAttr1] = true
                    }
                }

                if (event.eventDesc != null) {
                    myPrintf("%s\n", event.eventDesc)
                }

                event.triggerable = false
                if (event.eventLink != -1) {
                    interactions[event.eventLink].triggerable = !interactions[event.eventLink].triggerable
                }

                if (event.quit) return
            }
        }
        myPrintf("You can't do that.\n")
    }

    private fun displayHelp() {
        myPrintf("HOW TO PLAY\n")
        myPrintf("\tExplore the world and solve the mystery of the forest!\n")
        myPrintf("\tTo perform an action, use the following commands:\n\n")

        myPrintf("NAVIGATING THE WORLD\n")
        myPrintf("\twalk direction (or just direction) - move in the direction specified\n")
        myPrintf("\tlook - examine your surroundings\n\n")

        myPrintf("MANAGING ITEMS\n")
        myPrintf("\tinventory - list your belongings\n")
        myPrintf("\ttake - pick up an item\n")
        myPrintf("\tdrop - drop an item\n\n")

        myPrintf("INTERACTING WITH THE WORLD\n")
        myPrintf("\tsearch - look for hidden objects\n")
        myPrintf("\tuse - use an item or items\n\n")

        myPrintf("TO QUIT\n")
        myPrintf("\tquit - give up in your search and go home\n\n")
    }
}
