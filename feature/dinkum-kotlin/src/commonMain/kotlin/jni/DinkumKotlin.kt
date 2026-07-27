package jni

import com.funhouse.shared.common.AppData
import com.funhouse.shared.common.jni.*
import com.funhouse.shared.common.GAMES_FOLDER
import club.gepetto.GcLog
import java.io.File
import java.io.BufferedReader
import java.io.BufferedWriter
import kotlinx.coroutines.*
import com.funhouse.shared.common.utils.GcInputQueue

class GameEndedException : RuntimeException()

class DinkumKotlin : BaseKotlinGame() {

    override fun start() {
        GcLog.d("DinkumKotlin.start() called")
        gameJob?.cancel()
        gameJob = CoroutineScope(Dispatchers.Default).launch {
            try {
                runGame()
            } catch (e: CancellationException) {
                // game cancelled
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun stop() {
        super.stop()
        gameJob?.cancel()
    }

    override fun start(gameNickName: String) {
        start()
    }

    override fun sendCommand(command: String): Int {
        if (command.trim().equals("about", ignoreCase = true)) {
            myPrintf("--- Dinkum ---\n$about\n")
            return 0
        }
        inputQueue.put(command + "\n")
        return 0
    }

    private fun rand(): Int {
        return Math.abs(random.nextInt())
    }

    // Structure for monsters
    data class MonsterStruct(
        var type: Int,
        var location: Int,
        var status: Int,
        var hits: Int
    )

    // Structure for objects
    data class ObjectStruct(
        var location: Int,
        var value: Int,
        var type: Int,
        var id: Int,
        var weight: Int,
        var status: Int,
        var text: String
    )

    // Structure for objects which can be examined or read
    data class ReadStruct(
        val id: Int,
        val readable: Boolean
    )

    // Structure for adjectives
    data class AdjectiveStruct(
        val adjective: Int,
        val modifiedNoun: Int,
        val generatedNoun: Int,
        val command: Int
    )

    // Structure for adverbs
    data class AdverbStruct(
        val adverb: Int,
        val modifiedVerb: Int,
        val generatedVerb: Int
    )

    companion object {
        const val Failed = 0
        const val Request = 1
        const val Logic_error = 2

        // Room code numbers
        const val R_WALL = -1
        const val R_meadow = 0
        const val R_dike = 3
        const val R_river_edge = 4
        const val R_river = 5
        const val R_stream = 6
        const val R_slime = 7
        const val R_bunyip = 8
        const val R_river_exit = 9
        const val R_forest = 11
        const val R_billabong = 14
        const val R_taipan = 18
        const val R_mine_head = 22
        const val R_lift_entr = 27
        const val R_bitumen = 29
        const val R_office_entr = 36
        const val R_office_hall = 37
        const val R_office_mang = 38
        const val R_geo_w = 39
        const val R_store_room = 40
        const val R_geo_e = 41
        const val R_lift_inside = 43
        const val R_L49_entr = 44
        const val R_L67_entr = 45
        const val R_L82_entr = 46
        const val R_hideout_entr = 144
        const val R_hideout = 146
        const val R_bend = 147
        const val R_no_treasure = 159
        const val R_manhole_1 = 166
        const val R_manhole_2 = 170
        const val R_foreman = 177
        const val R_blast_point = 185
        const val R_ufo_w = 191
        const val R_ufo_nw = 192
        const val R_ufo_n = 193
        const val R_ufo_ne = 194
        const val R_ufo_e = 195
        const val R_ufo_se = 196
        const val R_ufo_s = 197
        const val R_ufo_sw = 198
        const val R_air_lock = 199
        const val R_ship_passage = 200
        const val R_flight_deck = 201
        const val R_panel = 202
        const val R_transporter = 203
        const val R_gleep_tank = 206
        const val R_warning = 217
        const val R_gong = 238
        const val R_closet = 240
        const val R_guard = 241
        const val R_prayer = 242
        const val R_road_kill_s = 245
        const val R_road_kill_n = 248

        // Room category indicator
        const val M_rm_type = 10
        const val M_obj_cnt = 11
        const val M_unmov_obj = 12
        const val M_monster = 13
        const val M_gleep = 14
        const val M_descp = 15

        // Room type
        const val T_lethal = -1
        const val T_short_descp = 0
        const val T_long_descp = 1
        const val T_was_long = 3
        const val T_looping = 5

        // Unmovable object status
        const val S_closed = 0
        const val S_revealed = 1
        const val S_flashing = 1
        const val S_dialed = 2
        const val S_unlocked = 2
        const val S_open = 3
        const val S_kicked = 8
        const val S_recorder = 4
        const val S_told = 5
        const val S_fair_game = 6
        const val S_recording = 7
        const val S_playing = 9
        const val S_inactive = 10

        const val L0 = 0
        const val L49 = 49
        const val L67 = 67
        const val L82 = 82

        // Vocabulary code numbers
        const val V_LINE_END = -1
        const val V_NULL = 0
        const val V_MOVE = 1
        const val V_PLURAL = 2
        const val V_VERB_ONLY = 3
        const val V_DIRECTION = 4
        const val V_NUMBER = 5

        // real word symbols
        const val V_east = 3
        const val V_west = 4
        const val V_up = 5
        const val V_down = 6
        const val V_u = 15
        const val V_quit = 21
        const val V_unlock = 22
        const val V_lock = 23
        const val V_take = 24
        const val V_leave = 25
        const val V_drop = 26
        const val V_can = 27
        const val V_bottle = 28
        const val V_mat = 29
        const val V_key = 30
        const val V_butt = 31
        const val V_look = 32
        const val V_door = 33
        const val V_open = 34
        const val V_get = 35
        const val V_throw = 36
        const val V_turn = 37
        const val V_on = 38
        const val V_off = 39
        const val V_push = 40
        const val V_button = 41
        const val V_enter = 42
        const val V_switch = 43
        const val V_zero = 44
        const val V_forty_nine = 45
        const val V_sixty_seven = 46
        const val V_eighty_two = 47
        const val V_0 = 48
        const val V_49 = 49
        const val V_67 = 50
        const val V_82 = 51
        const val V_beer = 52
        const val V_lager = 53
        const val V_doormat = 54
        const val V_lift = 55
        const val V_on_q = 56
        const val V_off_q = 57
        const val V_svc = 58
        const val V_map = 59
        const val V_picture = 60
        const val V_safe = 61
        const val V_dial = 62
        const val V_paper = 63
        const val V_put = 64
        const val V_close = 65
        const val V_read = 66
        const val V_gold = 67
        const val V_bar = 68
        const val V_dynamite = 69
        const val V_rifle = 70
        const val V_M16 = 71
        const val V_m16 = 72
        const val V_cap = 73
        const val V_matches = 74
        const val V_diamond = 75
        const val V_ring = 76
        const val V_ruby = 77
        const val V_silver = 78
        const val V_coin = 79
        const val V_bill = 80
        const val V_money = 81
        const val V_teapot = 82
        const val V_clip = 83
        const val V_ammo = 84
        const val V_box = 85
        const val V_stick = 86
        const val V_pills = 87
        const val V_orange = 88
        const val V_glowing = 89
        const val V_saphire = 90
        const val V_emerald = 91
        const val V_score = 92
        const val V_inventory = 93
        const val V_examine = 94
        const val V_describe = 95
        const val V_drink = 96
        const val V_fill = 97
        const val V_water = 98
        const val V_fourex = 99
        const val V_invent = 100
        const val V_view = 101
        const val V_press = 102
        const val V_exit = 103
        const val V_all = 104
        const val V_Fourex = 105
        const val V_everything = 106
        const val V_shoot = 107
        const val V_kill = 108
        const val V_ned = 109
        const val V_Ned = 110
        const val V_kelly = 111
        const val V_Kelly = 112
        const val V_pick = 113
        const val V_grab = 114
        const val V_combination = 115
        const val V_select = 116
        const val V_safety = 117
        const val V_SAFE = 118
        const val V_triple = 119
        const val V_III = 120
        const val V_single = 121
        const val V_I = 122
        const val V_auto = 123
        const val V_AUTO = 124
        const val V_automatic = 125
        const val V_set = 126
        const val V_insert = 127
        const val V_attach = 128
        const val V_plan = 129
        const val V_gun = 130
        const val V_magazine = 131
        const val V_hoop = 132
        const val V_snake = 133
        const val V_bear = 134
        const val V_wombat = 135
        const val V_remove = 136
        const val V_eject = 137
        const val V_org_clip = 138
        const val V_shut = 139
        const val V_gleep = 140
        const val V_gleeps = 141
        const val V_letter = 142
        const val V_envelope = 143
        const val V_torch = 144
        const val V_mail = 145
        const val V_tank = 146
        const val V_carefully = 147
        const val V_gently = 148
        const val V_softly = 149
        const val V_slow_drop = 150
        const val V_light = 151
        const val V_ignite = 152
        const val V_fuse = 153
        const val V_slowly = 154
        const val V_match = 155
        const val V_cube = 156
        const val V_blue = 157
        const val V_blue_button = 158
        const val V_gray = 159
        const val V_gray_button = 160
        const val V_clapper = 161
        const val V_sound = 162
        const val V_bang = 163
        const val V_hit = 164
        const val V_strike = 165
        const val V_gong = 166
        const val V_detector = 167
        const val V_give = 168
        const val V_stats = 169
        const val V_jsys = 170
        const val V_su = 171
        const val V_yellow = 172
        const val V_yellow_button = 173
        const val V_poster = 174
        const val V_eat = 175
        const val V_swallow = 176
        const val V_pill = 177
        const val V_atropine = 178
        const val V_packet = 179
        const val V_wrapper = 180
        const val V_tire = 181
        const val V_shell = 182
        const val V_brick = 183
        const val V_rope = 184
        const val V_package = 185
        const val V_belt = 186
        const val V_filter = 187
        const val V_string = 188
        const val V_cabinet = 189
        const val V_calendar = 190
        const val V_tackle = 191
        const val V_bit = 192
        const val V_jack = 193
        const val V_pen = 194
        const val V_pencil = 195
        const val V_wire = 196
        const val V_pipe = 197
        const val V_panel = 198
        const val V_opener = 199
        const val V_cord = 200
        const val V_photo = 201
        const val V_chair = 202
        const val V_bulb = 203
        const val V_rag = 204
        const val V_tube = 205
        const val V_carpet = 206
        const val V_branch = 207
        const val V_cork = 208
        const val V_trap = 209
        const val V_lighter = 210
        const val V_lace = 211
        const val V_comb = 212
        const val V_umbrella = 213
        const val V_meadow = 214
        const val V_billabong = 215
        const val V_river = 216
        const val V_road = 217
        const val V_forest = 218
        const val V_grass = 219
        const val V_stream = 220
        const val V_desert = 221
        const val V_building = 222
        const val V_office = 223
        const val V_hideout = 224
        const val V_dust = 225
        const val V_mine = 226
        const val V_tunnel = 227
        const val V_tree = 228
        const val V_spinifex = 229
        const val V_hexagon = 230
        const val V_spacecaft = 231
        const val V_airlock = 232
        const val V_wing = 233
        const val V_hole = 234
        const val V_tray = 235
        const val V_liquid = 236
        const val V_cockroach = 237
        const val V_go = 238
        const val V_scream = 239
        const val V_yell = 240
        const val V_bring = 241
        const val V_help = 242
        const val V_dump = 243
        const val V_attack = 244
        const val V_check = 245
        const val V_inspect = 246
        const val V_place = 247
        const val V_touch = 248
        const val V_pull = 249
        const val V_extinguish = 250
        const val V_ask = 251
        const val V_talk = 252
        const val V_tell = 253
        const val V_find = 254
        const val V_move = 255
        const val V_break = 256
        const val V_kick = 257
        const val V_smash = 258
        const val V_feed = 259
        const val V_taste = 260
        const val V_smell = 261
        const val V_slam = 262
        const val V_desk = 263
        const val V_in = 264
        const val V_into = 265
        const val V_under = 266
        const val V_out = 267
        const val V_from = 268
        const val V_by = 269
        const val V_with = 270
        const val V_fling = 271
        const val V_what = 272
        const val V_where = 273
        const val V_are = 274
        const val V_am = 275
        const val V_have = 276
        const val V_QUESTION = 277
        const val V_how = 278
        const val V_why = 279
        const val V_who = 280
        const val V_when = 281
        const val V_leap = 282
        const val V_walk = 283
        const val V_run = 284
        const val V_jump = 285
        const val V_hop = 286
        const val V_stroll = 287
        const val V_saunter = 288
        const val V_swagger = 289
        const val V_swing = 290
        const val V_dig = 291
        const val V_swim = 292
        const val V_depart = 293
        const val V_info = 294
        const val V_back = 295
        const val V_hold = 296
        const val V_fire = 297
        const val V_stand = 298
        const val V_painting = 299
        const val V_doors = 300
        const val V_treasure = 301
        const val V_trigger = 302
        const val V_load = 303
        const val V_unload = 304
        const val V_sand = 305
        const val V_cockroaches = 306
        const val V_kangaroo = 307
        const val V_climb = 308
        const val V_bulldust = 309
        const val V_kangaroos = 310
        const val V_spinifexes = 311
        const val V_fuze = 312
        const val V_message = 313
        const val V_wall = 314
        const val V_recorder = 315
        const val V_red = 316
        const val V_white = 317
        const val V_green = 318
        const val V_red_button = 319
        const val V_white_button = 320
        const val V_green_button = 321
        const val V_grey = 322
        const val V_matchbox = 323
        const val V_map_frag = 324
        const val V_circuit = 325
        const val V_lift_door = 326
        const val V_transporter = 327
        const val V_warning = 328
        const val V_explosive = 329
        const val V_self = 330
        const val V_myself = 331
        const val V_yourself = 332
        const val V_one = 333
        const val V_two = 334
        const val V_three = 335
        const val V_four = 336
        const val V_five = 337
        const val V_toss = 338
        const val V_chart = 339
        const val V_charts = 340
        const val V_schedule = 341
        const val V_schedules = 342
        const val V_orange_button = 343
        const val V_pants = 344
        const val V_clothes = 345
        const val V_clothing = 346

        const val Vocab_cnt = 346
        const val DM_tag = 347
        const val DM_room = 249
        const val Enemy_cnt = 15
        const val Objcnt = 82
        const val Read_objcnt = 15
        const val Quest_max = 9
        const val Verb_max = 81
        const val Adj_max = 20
        const val Adv_max = 30
        const val Obj_init_max = 51

        const val S_number = 1

        const val FALSE = 0
        const val TRUE = 1
        const val F_asleep = 8
        const val F_stealing = 9
        const val F_passive = 10
        const val F_aggressive = 11
        const val F_killing = 12
        const val F_no_monster = 13
        const val F_monster_active = 14
        const val F_no_argument = 15
        const val F_safety = 16
        const val F_single = 17
        const val F_triple = 18
        const val F_auto = 19
        const val F_no_clip = 20
        const val F_normal_clip = 21
        const val F_org_clip = 22
        const val F_wounded = 23
        const val F_replace = 24
        const val F_ignore = 25
        const val F_died = 26
        const val F_quit = 27

        const val N_ned = 0
        const val N_wombat = 1
        const val N_drop_bear = 2
        const val N_hoop_snake = 6
        const val N_guards = 14
        const val N_mullah = 15

        const val B_have = -1
        const val B_unmade = -2
        const val B_destroyed = -3
        const val B_in_safe = -5

        const val Z_normal = 0
        const val Z_transform = 1
        const val Z_alias = 2
        const val Z_unmovable = 3

        const val about = """
            --- The Dinkum Program ---

            Software by Gary A. Allen, Jr. 23 February 1994 Version: Mk 2.14
            (c) Copyright 1994 by Gary A. Allen, Jr.
            Permission granted for personal (non-commercial) use only.
            Converted to Kotlin by Valdetaro Consulting, LLC in 2026.
        """
    }

    // --- GAME STATE VARIABLES ---
    private var score = 0
    private var monster_flag = F_no_monster
    private var carry_count = 0
    private var carry_weight = 0
    private var clock_explode: Long = 0
    private var sw_warned = false
    private var flag_clock = 0
    private var sw_clock = false
    private var i_poison = 0
    private var l_time: Long = 0
    private var start_time: Long = 0
    private var sw_wizard = false
    private var sw_standard = false
    private var gleep_count = 0
    private var gleep_safe = 0
    private var gleep_drop = false
    private var sw_snaked = false
    private var sw_wombat = false
    private var sw_active = false
    private var pill_count = 7
    private var clip_flag = F_no_clip
    private var rifle_flag = F_safety
    private var sw_valuable = false
    private var sw_help = false
    private var sw_null = false
    private var sw_number = false
    private var serial = S_number
    private var verb = 0
    private val max_score = 1100

    private var describe_old_n = 0
    private var describe_dark_count = 0
    private var describe_sw_hint = false

    private var longdsc_sw_bend = true
    private var longdsc_sw_mob = true

    private var kelly_ned_look = 0
    private var kelly_sw_fiddle = true

    private var killer_sw_hurt = false
    private var actor_sw_door_kick = false

    private var monster_sw_hoop = false
    private var monster_bear_flag = 0
    private var monster_sw_replaced = false
    private var monster_sw_nogo = false
    private var monster_sw_guarded = true
    private var monster_sw_letter = false
    private var monster_count_down = 0

    private var gleeper_sw_glp_st = false
    private var gleeper_cnt_down = 0

    // JNI playback/recording variables
    private var recorderStatus = S_inactive
    private var fpReader: BufferedReader? = null
    private var fpWriter: BufferedWriter? = null
    private var fileName: String = ""

    private val inputQueue = GcInputQueue<String>()
    private var gameJob: Job? = null
    private val random = java.util.Random()

    private val packageFolderFile get() = File(AppData.packageFolder ?: "").apply { if (!exists()) mkdirs() }
    private val localFilesDirectory get() = File(packageFolderFile, "files").apply { if (!exists()) mkdirs() }
    private val backupDirectory get() = File(packageFolderFile, AppData.gameFolder ?: GAMES_FOLDER).apply { if (!exists()) mkdirs() }

    private val sent = IntArray(20)
    private var number_word = 0
    private val tag = BooleanArray(DM_tag)
    private val gleep_spot = IntArray(10)

    private lateinit var Rifle: ObjectStruct
    private lateinit var Teapot: ObjectStruct
    private lateinit var Can: ObjectStruct
    private lateinit var Clip: ObjectStruct
    private lateinit var Org_clip: ObjectStruct
    private lateinit var Recorder: ObjectStruct
    private lateinit var Cap: ObjectStruct
    private lateinit var Dynamite: ObjectStruct
    private lateinit var Letter: ObjectStruct
    private lateinit var Clapper: ObjectStruct
    private lateinit var Detector: ObjectStruct
    private lateinit var Key: ObjectStruct
    private lateinit var Umbrella: ObjectStruct
    private lateinit var Matches: ObjectStruct
    private lateinit var Pills: ObjectStruct
    private lateinit var Cube: ObjectStruct
    private lateinit var Torch: ObjectStruct
    private lateinit var Mat: ObjectStruct
    private lateinit var Map: ObjectStruct
    private lateinit var Map_frag: ObjectStruct
    private lateinit var Bottle: ObjectStruct
    private lateinit var Circuit_breaker: ObjectStruct
    private lateinit var Lift: ObjectStruct
    private lateinit var Door: ObjectStruct
    private lateinit var Safe: ObjectStruct
    private lateinit var Lift_door: ObjectStruct
    private lateinit var Picture: ObjectStruct
    private lateinit var Tank: ObjectStruct
    private lateinit var Gong: ObjectStruct
    private lateinit var Transporter: ObjectStruct

    // Monsters references
    private lateinit var monster_start: Array<MonsterStruct>
    private lateinit var Ned: MonsterStruct
    private lateinit var Wombat: MonsterStruct
    private lateinit var Guards: MonsterStruct
    private lateinit var Mullah: MonsterStruct

    private val room = arrayOf(
        intArrayOf(3, 2, 1, 15, -1, -1, 1, 15, 3, 2, 0, 0, 0, 0, 0, 0),
        intArrayOf(6, 2, 20, 0, -1, -1, 0, 3, 0, 2, 0, 0, 0, 0, 0, 1),
        intArrayOf(28, 11, 29, 148, -1, -1, 29, 11, 28, 148, 0, 0, 0, 0, 0, 2),
        intArrayOf(4, 0, 6, 4, -1, -1, 2, 1, 4, 2, 0, 0, 0, 0, 0, 3),
        intArrayOf(5, 9, 4, 4, -1, -1, 5, 5, 9, 9, 5, 0, 0, 0, 0, 4),
        intArrayOf(-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 0, 0, 0, 0, 5),
        intArrayOf(3, 14, 47, 3, -1, -1, 2, 3, 14, 3, 1, 0, 0, 0, 0, 6),
        intArrayOf(14, 8, 14, 8, -1, -1, 14, 14, 8, 8, 0, 0, 0, 0, 0, 7),
        intArrayOf(-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 0, 0, 0, 0, 8),
        intArrayOf(4, 1, 6, 4, -1, -1, 0, 2, 0, 1, 0, 0, 0, 0, 0, 9),
        intArrayOf(14, 6, 14, 3, -1, -1, 6, 14, 6, 14, 0, 0, 0, 0, 0, 10),
        intArrayOf(28, 12, 28, 12, -1, -1, 12, 28, 3, 6, 1, 0, 0, 0, 0, 11),
        intArrayOf(11, 13, 12, 13, -1, -1, 13, 12, 11, 13, 5, 0, 0, 0, 0, 12),
        intArrayOf(13, 13, 13, 13, -1, -1, 13, 13, 13, 12, 5, 0, 0, 0, 0, 13),
        intArrayOf(6, 7, 7, 10, -1, -1, 6, 10, 7, 7, 0, 0, 0, 0, 0, 14),
        intArrayOf(147, 148, 0, 16, -1, -1, 0, 147, 0, 148, 0, 0, 0, 0, 0, 15),
        intArrayOf(19, 19, 16, 17, -1, -1, 19, 19, 17, 17, 5, 0, 0, 0, 0, 16),
        intArrayOf(16, 16, 16, 18, -1, -1, 16, 16, 18, 16, 0, 0, 0, 0, 0, 17),
        intArrayOf(-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 0, 0, 0, 0, 18),
        intArrayOf(3, 2, 0, 16, -1, -1, 0, 16, 2, 16, 0, 0, 0, 0, 0, 19),
        intArrayOf(21, 142, 22, 1, -1, -1, 23, 2, 141, 1, 0, 0, 0, 0, 0, 20),
        intArrayOf(42, 20, 145, 47, -1, -1, 145, 1, 145, 6, 0, 0, 0, 0, 0, 21),
        intArrayOf(36, 27, -1, 20, -1, -1, -1, 20, -1, 20, 1, 0, 0, 0, 0, 22),
        intArrayOf(25, 24, 25, 145, -1, -1, 25, 145, 25, 34, 4, 0, 0, 0, 0, 23),
        intArrayOf(23, 26, 25, 33, -1, -1, 25, 33, 25, 32, 0, 0, 0, 0, 0, 24),
        intArrayOf(25, 25, 25, 25, -1, -1, 143, 23, 25, 26, 5, 0, 0, 0, 0, 25),
        intArrayOf(24, 25, 25, 141, -1, -1, 25, 31, 25, 141, 4, 0, 0, 0, 0, 26),
        intArrayOf(22, 30, -1, 20, -1, -1, -1, 20, -1, 20, 2, 0, 0, 0, 0, 27),
        intArrayOf(0, 2, 29, 148, -1, -1, 29, 11, 0, 2, 0, 0, 0, 0, 0, 28),
        intArrayOf(142, 243, 141, 2, -1, -1, 141, 2, 141, 2, 1, 0, 0, 0, 0, 29),
        intArrayOf(27, 141, 31, 20, -1, -1, -1, 20, 141, 141, 0, 0, 0, 0, 0, 30),
        intArrayOf(32, 141, 24, 30, -1, -1, 24, -1, 26, 141, 0, 0, 0, 0, 0, 31),
        intArrayOf(33, 31, 24, -1, -1, -1, 24, -1, 24, -1, 0, 0, 0, 0, 0, 32),
        intArrayOf(34, 32, 24, -1, -1, -1, 24, -1, 24, -1, 0, 0, 0, 0, 0, 33),
        intArrayOf(145, 33, 24, 35, -1, -1, 23, 145, 24, -1, 0, 0, 0, 0, 0, 34),
        intArrayOf(145, 36, 34, 20, -1, -1, 145, 145, -1, 20, 0, 0, 0, 0, 0, 35),
        intArrayOf(35, 22, -1, 20, -1, -1, 35, 20, -1, 20, 2, 0, 0, 0, 0, 36),
        intArrayOf(38, 39, -1, 36, -1, -1, 38, 36, 39, 36, 0, 0, 0, 0, 0, 37),
        intArrayOf(-1, 37, -1, -1, -1, -1, -1, -1, -1, 37, 1, 0, 0, 0, 0, 38),
        intArrayOf(37, -1, 41, -1, -1, -1, -1, -1, -1, -1, 1, 0, 0, 0, 0, 39),
        intArrayOf(-1, 41, -1, -1, -1, -1, -1, -1, -1, 41, 2, 0, 0, 0, 0, 40),
        intArrayOf(40, -1, -1, 39, -1, -1, 40, -1, -1, -1, 4, 0, 0, 0, 0, 41),
        intArrayOf(246, 21, 145, 6, -1, -1, 145, 1, 145, 6, 0, 0, 0, 0, 0, 42),
        intArrayOf(-1, -1, -1, 27, -1, -1, -1, 27, -1, -1, 1, 0, 0, 0, 0, 43),
        intArrayOf(-1, -1, 43, 48, -1, -1, -1, -1, 43, -1, 0, 0, 0, 0, 0, 44),
        intArrayOf(-1, 204, 43, 149, -1, -1, -1, -1, 43, -1, 0, 0, 0, 0, 0, 45),
        intArrayOf(239, -1, 43, -1, -1, -1, -1, -1, 43, -1, 0, 0, 0, 0, 0, 46),
        intArrayOf(3, 20, 21, 6, -1, -1, 21, 3, 20, 6, 0, 0, 0, 0, 0, 47),
        intArrayOf(49, 50, 44, -1, -1, -1, -1, -1, -1, -1, 0, 0, 0, 0, 0, 48),
        intArrayOf(51, 48, -1, -1, -1, -1, -1, -1, -1, -1, 0, 0, 0, 0, 0, 55),
        intArrayOf(48, 56, -1, -1, -1, -1, -1, -1, -1, -1, 0, 0, 0, 0, 0, 55),
        intArrayOf(-1, 49, 52, 53, -1, -1, -1, -1, -1, -1, 0, 0, 0, 0, 0, 51),
        intArrayOf(-1, -1, 54, 51, -1, 88, -1, -1, -1, -1, 0, 0, 0, 0, 0, 59),
        intArrayOf(-1, -1, 51, 55, -1, -1, -1, -1, -1, -1, 0, 0, 0, 0, 0, 56),
        intArrayOf(-1, -1, -1, 52, -1, -1, -1, -1, -1, -1, 0, 0, 0, 0, 0, 91),
        intArrayOf(-1, -1, 53, -1, 57, -1, -1, -1, -1, -1, 0, 0, 0, 0, 0, 62),
        intArrayOf(50, -1, -1, -1, -1, -1, -1, -1, 74, 72, 0, 0, 0, 0, 0, 64),
        intArrayOf(-1, -1, -1, -1, -1, 55, -1, 58, -1, 59, 0, 0, 0, 0, 0, 76),
        intArrayOf(-1, -1, -1, -1, -1, -1, -1, 60, 57, -1, 0, 0, 0, 0, 0, 57),
        intArrayOf(-1, -1, -1, -1, -1, -1, 57, -1, -1, 61, 0, 0, 0, 0, 0, 58),
        intArrayOf(-1, -1, -1, -1, -1, -1, -1, -1, 58, 62, 0, 0, 0, 0, 0, 84),
        intArrayOf(-1, -1, -1, -1, -1, -1, 59, 63, -1, -1, 0, 0, 0, 0, 0, 87),
        intArrayOf(-1, -1, -1, -1, -1, -1, 60, -1, -1, 64, 0, 0, 0, 0, 0, 58),
        intArrayOf(-1, -1, -1, -1, -1, -1, -1, 64, 61, -1, 0, 0, 0, 0, 0, 57),
        intArrayOf(-1, -1, -1, -1, -1, 65, 62, -1, 63, -1, 0, 0, 0, 0, 0, 77),
        intArrayOf(-1, 66, -1, -1, 64, -1, -1, -1, -1, -1, 0, 0, 0, 0, 0, 61),
        intArrayOf(65, 67, -1, -1, -1, -1, -1, -1, -1, -1, 0, 0, 0, 0, 0, 55),
        intArrayOf(66, 69, 68, 70, -1, -1, -1, -1, -1, -1, 0, 0, 0, 0, 0, 78),
        intArrayOf(-1, -1, 71, 67, -1, -1, -1, -1, -1, -1, 0, 0, 0, 0, 0, 56),
        intArrayOf(67, -1, -1, -1, -1, -1, -1, -1, -1, -1, 0, 0, 0, 0, 0, 92),
        intArrayOf(121, 128, 67, -1, -1, -1, -1, -1, -1, -1, 0, 0, 0, 0, 0, 48),
        intArrayOf(-1, -1, -1, 68, -1, -1, 72, -1, 73, -1, 0, 0, 0, 0, 0, 66),
        intArrayOf(-1, -1, -1, -1, -1, -1, 56, -1, -1, 71, 0, 0, 0, 0, 0, 58),
        intArrayOf(-1, -1, -1, -1, -1, -1, -1, 71, 75, -1, 0, 0, 0, 0, 0, 57),
        intArrayOf(-1, -1, -1, -1, -1, -1, -1, 56, 77, -1, 0, 0, 0, 0, 0, 57),
        intArrayOf(-1, -1, -1, -1, -1, -1, 76, 73, -1, 80, 0, 0, 0, 0, 0, 52),
        intArrayOf(-1, -1, -1, -1, -1, -1, 77, -1, -1, 75, 0, 0, 0, 0, 0, 58),
        intArrayOf(-1, -1, -1, -1, -1, -1, 79, 74, 78, 76, 0, 0, 0, 0, 0, 79),
        intArrayOf(-1, -1, -1, -1, -1, -1, -1, 77, -1, -1, 0, 0, 0, 0, 0, 90),
        intArrayOf(-1, -1, -1, -1, -1, -1, 108, -1, -1, 77, 0, 0, 0, 0, 0, 58),
        intArrayOf(-1, -1, 81, 82, -1, -1, 75, -1, -1, -1, 0, 0, 0, 0, 0, 74),
        intArrayOf(-1, -1, -1, 80, -1, -1, -1, -1, -1, -1, 0, 0, 0, 0, 0, 93),
        intArrayOf(-1, -1, 80, 83, -1, -1, -1, -1, -1, -1, 0, 0, 0, 0, 0, 56),
        intArrayOf(-1, 84, 82, -1, -1, -1, -1, -1, -1, -1, 0, 0, 0, 0, 0, 81),
        intArrayOf(83, -1, 85, 86, -1, -1, -1, -1, -1, -1, 0, 0, 0, 0, 0, 50),
        intArrayOf(-1, -1, -1, 84, -1, -1, -1, -1, -1, -1, 0, 0, 0, 0, 0, 90),
        intArrayOf(87, -1, 84, -1, -1, -1, -1, -1, -1, -1, 0, 0, 0, 0, 0, 83),
        intArrayOf(-1, 86, -1, 109, -1, -1, -1, -1, -1, -1, 0, 0, 0, 0, 0, 94),
        intArrayOf(-1, -1, -1, -1, 52, -1, 92, -1, -1, 89, 0, 0, 0, 0, 0, 96),
        intArrayOf(-1, -1, -1, -1, -1, -1, 88, -1, -1, 90, 0, 0, 0, 0, 0, 58),
        intArrayOf(-1, -1, -1, -1, -1, -1, 89, -1, -1, 91, 0, 0, 0, 0, 0, 58),
        intArrayOf(-1, -1, -1, -1, -1, -1, 90, -1, -1, -1, 0, 0, 0, 0, 0, 90),
        intArrayOf(-1, -1, -1, -1, -1, -1, 93, -1, -1, 88, 0, 0, 0, 0, 0, 58),
        intArrayOf(-1, -1, -1, -1, -1, -1, -1, 94, 95, 92, 0, 0, 0, 0, 0, 95),
        intArrayOf(-1, -1, -1, -1, 96, -1, -1, -1, 93, -1, 0, 0, 0, 0, 0, 60),
        intArrayOf(-1, -1, -1, -1, -1, -1, -1, 93, 104, -1, 0, 0, 0, 0, 0, 57),
        intArrayOf(97, -1, 103, -1, -1, 94, -1, -1, -1, -1, 0, 0, 0, 0, 0, 97),
        intArrayOf(98, 96, -1, -1, -1, -1, -1, -1, -1, -1, 0, 0, 0, 0, 0, 55),
        intArrayOf(-1, 97, 99, -1, -1, -1, -1, -1, -1, -1, 0, 0, 0, 0, 0, 81),
        intArrayOf(-1, -1, 100, 98, -1, -1, -1, -1, -1, -1, 0, 0, 0, 0, 0, 56),
        intArrayOf(-1, 101, -1, 99, -1, -1, -1, -1, -1, -1, 0, 0, 0, 0, 0, 80),
        intArrayOf(100, 102, -1, -1, -1, -1, -1, -1, -1, -1, 0, 0, 0, 0, 0, 55),
        intArrayOf(101, -1, -1, 103, -1, -1, -1, -1, -1, -1, 0, 0, 0, 0, 0, 82),
        intArrayOf(-1, -1, 102, 96, -1, -1, -1, -1, -1, -1, 0, 0, 0, 0, 0, 56),
        intArrayOf(-1, -1, -1, -1, -1, -1, -1, 95, 105, -1, 0, 0, 0, 0, 0, 57),
        intArrayOf(-1, -1, -1, -1, -1, -1, -1, 104, -1, 106, 0, 0, 0, 0, 0, 86),
        intArrayOf(-1, -1, -1, -1, -1, -1, 105, -1, -1, 107, 0, 0, 0, 0, 0, 58),
        intArrayOf(-1, -1, -1, -1, 79, -1, 106, -1, -1, -1, 0, 0, 0, 0, 0, 89),
        intArrayOf(-1, -1, -1, -1, -1, 106, -1, -1, -1, 79, 0, 0, 0, 0, 0, 88),
        intArrayOf(-1, -1, 87, 110, -1, -1, -1, -1, -1, -1, 0, 0, 0, 0, 0, 56),
        intArrayOf(-1, -1, 109, -1, -1, 140, -1, -1, -1, -1, 0, 0, 0, 0, 0, 99),
        intArrayOf(-1, -1, -1, 140, 109, -1, -1, -1, -1, -1, 0, 0, 0, 0, 0, 98),
        intArrayOf(-1, -1, 140, -1, 113, -1, -1, -1, -1, -1, 0, 0, 0, 0, 0, 62),
        intArrayOf(114, -1, -1, -1, -1, 112, -1, -1, -1, -1, 0, 0, 0, 0, 0, 100),
        intArrayOf(115, 113, -1, -1, -1, -1, -1, -1, -1, -1, 0, 0, 0, 0, 0, 55),
        intArrayOf(-1, 114, 118, 116, -1, -1, -1, -1, -1, -1, 0, 0, 0, 0, 0, 51),
        intArrayOf(-1, 117, 115, -1, -1, -1, -1, -1, -1, -1, 0, 0, 0, 0, 0, 81),
        intArrayOf(116, -1, -1, -1, -1, -1, -1, -1, -1, -1, 0, 0, 0, 0, 0, 101),
        intArrayOf(-1, -1, 119, 115, -1, -1, -1, -1, -1, -1, 0, 0, 0, 0, 0, 56),
        intArrayOf(-1, 125, 120, 118, -1, -1, -1, -1, -1, -1, 0, 0, 0, 0, 0, 51),
        intArrayOf(-1, 122, -1, 119, -1, -1, -1, -1, -1, -1, 0, 0, 0, 0, 0, 80),
        intArrayOf(-1, 123, -1, 122, -1, -1, -1, -1, -1, -1, 0, 0, 0, 0, 0, 80),
        intArrayOf(120, 123, 121, -1, -1, -1, -1, -1, -1, -1, 0, 0, 0, 0, 0, 48),
        intArrayOf(122, -1, -1, 124, -1, -1, -1, -1, -1, -1, 0, 0, 0, 0, 0, 82),
        intArrayOf(-1, -1, 123, -1, -1, -1, -1, -1, -1, -1, 0, 0, 0, 0, 0, 90),
        intArrayOf(119, -1, -1, 126, -1, -1, -1, -1, -1, -1, 0, 0, 0, 0, 0, 82),
        intArrayOf(-1, 127, 125, -1, -1, -1, -1, -1, -1, -1, 0, 0, 0, 0, 0, 81),
        intArrayOf(126, -1, -1, -1, -1, -1, -1, -1, -1, -1, 0, 0, 0, 0, 0, 90),
        intArrayOf(70, -1, -1, 129, -1, -1, -1, -1, -1, -1, 0, 0, 0, 0, 0, 82),
        intArrayOf(130, 136, 128, -1, -1, -1, -1, -1, -1, -1, 0, 0, 0, 0, 0, 48),
        intArrayOf(-1, 129, -1, 131, -1, -1, -1, -1, -1, -1, 0, 0, 0, 0, 0, 80),
        intArrayOf(134, 132, 130, -1, -1, -1, -1, -1, -1, -1, 0, 0, 0, 0, 0, 48),
        intArrayOf(131, -1, -1, 133, -1, -1, -1, -1, -1, -1, 0, 0, 0, 0, 0, 82),
        intArrayOf(-1, -1, 132, -1, -1, -1, -1, -1, -1, -1, 0, 0, 0, 0, 0, 90),
        intArrayOf(-1, 131, -1, 135, -1, -1, -1, -1, -1, -1, 0, 0, 0, 0, 0, 80),
        intArrayOf(-1, -1, 134, -1, -1, -1, -1, -1, -1, -1, 0, 0, 0, 0, 0, 90),
        intArrayOf(129, -1, -1, 137, -1, -1, -1, -1, -1, -1, 0, 0, 0, 0, 0, 82),
        intArrayOf(-1, -1, 136, 138, -1, -1, -1, -1, -1, -1, 0, 0, 0, 0, 0, 56),
        intArrayOf(139, -1, 137, -1, -1, -1, -1, -1, -1, -1, 0, 0, 0, 0, 0, 83),
        intArrayOf(-1, 138, -1, -1, -1, -1, -1, -1, -1, -1, 0, 0, 0, 0, 0, 90),
        intArrayOf(-1, -1, 111, 112, -1, -1, -1, -1, -1, -1, 0, 0, 0, 0, 0, 56),
        intArrayOf(30, 26, 26, 142, -1, -1, 24, 20, 26, 142, 0, 0, 0, 0, 0, 102),
        intArrayOf(20, 29, 141, 2, -1, -1, 141, 2, 141, 2, 0, 0, 0, 0, 0, 103),
        intArrayOf(25, 25, 25, 25, -1, -1, 25, 25, 144, 25, 0, 0, 0, 0, 0, 104),
        intArrayOf(146, 146, 146, 146, -1, -1, 146, 146, 146, 146, 1, 0, 0, 0, 0, 105),
        intArrayOf(23, 35, 23, 21, -1, -1, 23, 21, 35, 34, 0, 0, 0, 0, 0, 106),
        intArrayOf(-1, -1, -1, 144, -1, -1, -1, -1, -1, -1, 0, 0, 0, 0, 0, 107),
        intArrayOf(3, 15, 3, 3, -1, -1, 3, 3, 15, 15, 1, 0, 0, 0, 0, 108),
        intArrayOf(15, 2, 2, 15, -1, -1, 15, 15, 2, 2, 0, 0, 0, 0, 0, 109),
        intArrayOf(-1, -1, 45, -1, -1, 150, -1, -1, -1, -1, 0, 0, 0, 0, 0, 110),
        intArrayOf(-1, 151, -1, -1, 149, -1, -1, -1, -1, -1, 0, 0, 0, 0, 0, 61),
        intArrayOf(150, 152, -1, -1, -1, -1, -1, -1, -1, -1, 0, 0, 0, 0, 0, 55),
        intArrayOf(151, 153, -1, 159, -1, -1, -1, -1, -1, -1, 0, 0, 0, 0, 0, 49),
        intArrayOf(152, 154, -1, -1, -1, -1, -1, -1, -1, -1, 0, 0, 0, 0, 0, 55),
        intArrayOf(153, -1, -1, 155, -1, -1, -1, -1, -1, -1, 0, 0, 0, 0, 0, 82),
        intArrayOf(-1, -1, 154, 156, -1, -1, -1, -1, -1, -1, 0, 0, 0, 0, 0, 56),
        intArrayOf(157, -1, 155, -1, -1, -1, -1, -1, -1, -1, 0, 0, 0, 0, 0, 83),
        intArrayOf(158, 156, -1, -1, -1, -1, -1, -1, -1, -1, 0, 0, 0, 0, 0, 55),
        intArrayOf(-1, 157, 159, 160, -1, -1, -1, -1, -1, -1, 0, 0, 0, 0, 0, 51),
        intArrayOf(-1, -1, 152, 158, -1, -1, -1, -1, -1, -1, 1, 0, 0, 0, 0, 132),
        intArrayOf(-1, -1, 158, 161, -1, -1, -1, -1, -1, -1, 0, 0, 0, 0, 0, 56),
        intArrayOf(-1, -1, 160, 162, -1, -1, -1, -1, -1, -1, 0, 0, 0, 0, 0, 56),
        intArrayOf(-1, -1, 161, 163, -1, -1, -1, -1, -1, -1, 0, 0, 0, 0, 0, 56),
        intArrayOf(-1, -1, 162, 164, -1, -1, -1, -1, -1, -1, 0, 0, 0, 0, 0, 56),
        intArrayOf(-1, -1, 163, 165, -1, -1, -1, -1, -1, -1, 0, 0, 0, 0, 0, 56),
        intArrayOf(-1, -1, 164, -1, -1, 166, -1, -1, -1, -1, 0, 0, 0, 0, 0, 110),
        intArrayOf(-1, -1, 167, 178, 165, -1, -1, -1, -1, -1, 0, 0, 0, 0, 0, 111),
        intArrayOf(168, -1, -1, 166, -1, -1, -1, -1, -1, -1, 0, 0, 0, 0, 0, 82),
        intArrayOf(169, 167, -1, -1, -1, -1, -1, -1, -1, -1, 0, 0, 0, 0, 0, 55),
        intArrayOf(170, 168, -1, -1, -1, -1, -1, -1, -1, -1, 0, 0, 0, 0, 0, 55),
        intArrayOf(-1, 169, -1, -1, 171, -1, -1, -1, -1, -1, 0, 0, 0, 0, 0, 61),
        intArrayOf(-1, -1, 172, -1, -1, 170, -1, -1, -1, -1, 0, 0, 0, 0, 0, 110),
        intArrayOf(-1, -1, 173, 171, -1, -1, -1, -1, -1, -1, 0, 0, 0, 0, 0, 56),
        intArrayOf(-1, -1, 174, 172, -1, -1, -1, -1, -1, -1, 0, 0, 0, 0, 0, 56),
        intArrayOf(-1, -1, 175, 173, -1, -1, -1, -1, -1, -1, 0, 0, 0, 0, 0, 56),
        intArrayOf(-1, -1, 176, 174, -1, -1, -1, -1, -1, -1, 0, 0, 0, 0, 0, 56),
        intArrayOf(-1, -1, 177, 175, -1, -1, -1, -1, -1, -1, 0, 0, 0, 0, 0, 56),
        intArrayOf(-1, -1, -1, 176, -1, -1, -1, -1, -1, -1, 1, 0, 0, 0, 0, 112),
        intArrayOf(-1, -1, 166, 179, -1, -1, -1, -1, -1, -1, 0, 0, 0, 0, 0, 56),
        intArrayOf(-1, -1, 178, 180, -1, -1, -1, -1, -1, -1, 0, 0, 0, 0, 0, 56),
        intArrayOf(-1, 181, 179, -1, -1, -1, -1, -1, -1, -1, 0, 0, 0, 0, 0, 81),
        intArrayOf(180, 182, -1, -1, -1, -1, -1, -1, -1, -1, 0, 0, 0, 0, 0, 55),
        intArrayOf(181, 185, 184, 183, -1, -1, -1, -1, -1, -1, 0, 0, 0, 0, 0, 78),
        intArrayOf(-1, -1, 182, -1, -1, -1, -1, -1, -1, -1, 0, 0, 0, 0, 0, 90),
        intArrayOf(-1, -1, -1, 182, -1, -1, -1, -1, -1, -1, 0, 0, 0, 0, 0, 90),
        intArrayOf(182, -1, -1, -1, -1, -1, -1, -1, -1, -1, 0, 0, 0, 0, 0, 90),
        intArrayOf(185, 187, -1, -1, -1, -1, -1, -1, -1, -1, 0, 0, 0, 0, 0, 55),
        intArrayOf(186, 189, 190, 188, -1, -1, -1, -1, -1, -1, 0, 0, 0, 0, 0, 78),
        intArrayOf(-1, -1, 187, -1, -1, -1, -1, -1, -1, -1, 0, 0, 0, 0, 0, 90),
        intArrayOf(187, -1, -1, -1, -1, -1, -1, -1, -1, -1, 0, 0, 0, 0, 0, 90),
        intArrayOf(-1, -1, 191, 187, -1, -1, -1, -1, -1, -1, 0, 0, 0, 0, 0, 56),
        intArrayOf(192, 198, -1, 190, -1, -1, -1, -1, -1, -1, 1, 0, 0, 0, 0, 113),
        intArrayOf(-1, 191, -1, -1, -1, -1, 193, -1, -1, -1, 1, 0, 0, 0, 0, 114),
        intArrayOf(-1, -1, -1, -1, -1, -1, -1, -1, 194, 192, 1, 0, 0, 0, 0, 115),
        intArrayOf(-1, 195, -1, -1, -1, -1, -1, 193, -1, -1, 1, 0, 0, 0, 0, 116),
        intArrayOf(194, 196, -1, -1, -1, -1, -1, -1, -1, -1, 1, 0, 0, 0, 0, 117),
        intArrayOf(195, -1, -1, -1, 199, -1, -1, -1, -1, 197, 1, 0, 0, 0, 0, 118),
        intArrayOf(-1, -1, -1, -1, -1, -1, 196, 198, -1, -1, 1, 0, 0, 0, 0, 119),
        intArrayOf(191, -1, -1, -1, -1, -1, -1, -1, 197, -1, 1, 0, 0, 0, 0, 120),
        intArrayOf(200, -1, -1, -1, -1, 196, -1, -1, -1, -1, 1, 0, 0, 0, 0, 121),
        intArrayOf(-1, 199, 201, 202, -1, -1, -1, -1, -1, -1, 1, 0, 0, 0, 0, 122),
        intArrayOf(-1, -1, -1, 200, -1, -1, -1, -1, -1, -1, 1, 0, 0, 0, 0, 123),
        intArrayOf(-1, -1, 200, 203, -1, -1, -1, -1, -1, -1, 1, 0, 0, 0, 0, 124),
        intArrayOf(-1, -1, 202, -1, -1, -1, -1, -1, -1, -1, 1, 0, 0, 0, 0, 125),
        intArrayOf(45, 205, -1, -1, -1, -1, -1, -1, -1, -1, 0, 0, 0, 0, 0, 55),
        intArrayOf(204, -1, 206, -1, -1, -1, -1, -1, -1, -1, 0, 0, 0, 0, 0, 83),
        intArrayOf(-1, -1, -1, 205, -1, -1, -1, -1, -1, -1, 1, 0, 0, 0, 0, 126),
        intArrayOf(-1, 208, 227, -1, -1, -1, -1, -1, -1, -1, 0, 0, 0, 0, 0, 81),
        intArrayOf(207, 209, 226, -1, -1, -1, -1, -1, -1, -1, 0, 0, 0, 0, 0, 48),
        intArrayOf(208, 210, 225, -1, -1, -1, -1, -1, -1, -1, 0, 0, 0, 0, 0, 48),
        intArrayOf(209, 211, 224, -1, -1, -1, -1, -1, -1, -1, 0, 0, 0, 0, 0, 48),
        intArrayOf(210, 217, 223, -1, -1, -1, -1, -1, -1, -1, 0, 0, 0, 0, 0, 48),
        intArrayOf(217, 213, 222, -1, -1, -1, -1, -1, -1, -1, 0, 0, 0, 0, 0, 48),
        intArrayOf(212, 214, 221, -1, -1, -1, -1, -1, -1, -1, 0, 0, 0, 0, 0, 48),
        intArrayOf(213, 215, 220, -1, -1, -1, -1, -1, -1, -1, 0, 0, 0, 0, 0, 48),
        intArrayOf(214, 216, 219, -1, -1, -1, -1, -1, -1, -1, 0, 0, 0, 0, 0, 48),
        intArrayOf(215, -1, 218, -1, -1, -1, -1, -1, -1, -1, 0, 0, 0, 0, 0, 83),
        intArrayOf(211, 212, -1, 239, -1, -1, -1, -1, -1, -1, 1, 0, 0, 0, 0, 128),
        intArrayOf(-1, -1, 237, 216, -1, -1, -1, -1, -1, -1, -1, 0, 0, 0, 0, 56),
        intArrayOf(-1, -1, 236, 215, -1, -1, -1, -1, -1, -1, -1, 0, 0, 0, 0, 56),
        intArrayOf(-1, -1, 235, 214, -1, -1, -1, -1, -1, -1, -1, 0, 0, 0, 0, 56),
        intArrayOf(-1, -1, 234, 213, -1, -1, -1, -1, -1, -1, -1, 0, 0, 0, 0, 56),
        intArrayOf(-1, -1, 233, 212, -1, -1, -1, -1, -1, -1, -1, 0, 0, 0, 0, 56),
        intArrayOf(-1, -1, 232, 211, -1, -1, -1, -1, -1, -1, -1, 0, 0, 0, 0, 56),
        intArrayOf(-1, -1, 231, 210, -1, -1, -1, -1, -1, -1, -1, 0, 0, 0, 0, 56),
        intArrayOf(-1, -1, 230, 209, -1, -1, -1, -1, -1, -1, -1, 0, 0, 0, 0, 56),
        intArrayOf(-1, -1, 229, 208, -1, -1, -1, -1, -1, -1, -1, 0, 0, 0, 0, 56),
        intArrayOf(-1, -1, 228, 207, -1, -1, -1, -1, -1, -1, -1, 0, 0, 0, 0, 56),
        intArrayOf(-1, 229, -1, 227, -1, -1, -1, -1, -1, -1, 0, 0, 0, 0, 0, 80),
        intArrayOf(228, 230, -1, 226, -1, -1, -1, -1, -1, -1, 0, 0, 0, 0, 0, 49),
        intArrayOf(229, 231, -1, 225, -1, -1, -1, -1, -1, -1, 0, 0, 0, 0, 0, 49),
        intArrayOf(230, 232, -1, 224, -1, -1, -1, -1, -1, -1, 0, 0, 0, 0, 0, 49),
        intArrayOf(231, 233, -1, 223, -1, -1, -1, -1, -1, -1, 0, 0, 0, 0, 0, 49),
        intArrayOf(232, 234, -1, 222, -1, -1, -1, -1, -1, -1, 0, 0, 0, 0, 0, 49),
        intArrayOf(233, 235, -1, 221, -1, -1, -1, -1, -1, -1, 0, 0, 0, 0, 0, 49),
        intArrayOf(234, 236, -1, 220, -1, -1, -1, -1, -1, -1, 0, 0, 0, 0, 0, 49),
        intArrayOf(235, 237, -1, 219, -1, -1, -1, -1, -1, -1, 0, 0, 0, 0, 0, 49),
        intArrayOf(236, 238, -1, 218, -1, -1, -1, -1, -1, -1, 0, 0, 0, 0, 0, 49),
        intArrayOf(237, -1, -1, -1, -1, -1, -1, -1, -1, -1, 1, 0, 0, 0, 0, 127),
        intArrayOf(-1, 46, 217, -1, -1, -1, -1, -1, -1, -1, 0, 0, 0, 0, 0, 81),
        intArrayOf(241, -1, -1, -1, -1, -1, -1, -1, -1, -1, 1, 0, 0, 0, 0, 129),
        intArrayOf(-1, 240, 242, -1, -1, -1, -1, -1, -1, -1, 1, 0, 0, 0, 0, 130),
        intArrayOf(-1, -1, -1, 241, -1, -1, -1, -1, -1, -1, 1, 0, 0, 0, 0, 131),
        intArrayOf(29, 244, 141, 2, -1, -1, 141, 2, 141, 2, 4, 0, 0, 0, 0, 133),
        intArrayOf(243, 245, 141, 2, -1, -1, 141, 2, 141, 2, 0, 0, 0, 0, 0, 134),
        intArrayOf(244, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 0, 0, 0, 0, 135),
        intArrayOf(247, 42, 145, 6, -1, -1, 145, 1, 145, 6, 4, 0, 0, 0, 0, 136),
        intArrayOf(248, 246, 145, 6, -1, -1, 145, 1, 145, 6, 0, 0, 0, 0, 0, 134),
        intArrayOf(-1, 247, -1, -1, -1, -1, -1, -1, -1, -1, -1, 0, 0, 0, 0, 135)
    )

    private val descript = arrayOf(
        "are in a grassy meadow that stretches out in all directions",
        "are on a path next to the meadow.  To the east is a road.",
        "are approaching a thickly wooded forest full of gum trees.",
        "have hiked a short distance and then climbed up onto a river dike.",
        "are next to a wide murky river that looks dangerous.",
        "--- marker for drowing in river",
        "are next to a billabong with a stream feeding into it.",
        "feel something slimey under the water wraping itself around your leg.",
        "--- marker for the bunyip",
        "are back on top of the dike next to a great meandering river.",
        "are waist deep in the stream, the water is cool and refreshing.",
        "are in a thickly wooded forest. There is a rich eucalyptus smell",
        "are deeper into the gum tree forest and getting disoriented.",
        "are deep in a gum tree forest and appear to be ***lost***",
        "are wading in the billabong.  The water is stagnant and smelly.",
        "approach an area with high grass. You can only see in a few metres.",
        "are deep in the grass, which is waist high.",
        "see snakes slither by in the grass.  Some are quite big.",
        "--- marker for the Taipan",
        "leave the grassy area and head towards the meadow.",
        "are on a road.  Far to the east are some deserted buildings.",
        "are on a narrow one lane road stretching out to the horizon.",
        "are west from the head gear of the abandoned ACME Gold Mine.",
        "are in an orange desert with bulldust swirling about you.",
        "are in a desert full of high sand dunes.",
        "are in between two sand dunes and can see only sand.",
        "are in a sandy desert with large thorny spinifexes rolling by you.",
        "are west of the mine shaft lift.  The mine tower looms over head.",
        "are walking away from a forest of sweet smelling gum trees. ",
        "find yourself on a narrow potholed bitumen road with soft shoulders.",
        "are southwest of the mine lift.  To the south is desert.",
        "are southeast of the mine lift.  To the south is desert.",
        "are east of the mine tower between the lift and the office.",
        "are east of the mine office building.  There is no door here.",
        "are at the north eastern corner of the office building.",
        "are at the north western corner of the office building.",
        "are west of the ACME Gold Mine office building entrance.",
        "are in the entry hall.  There are doors to the north, south and west.",
        "are in the Site Manager's office.  The only exit is to the south.",
        "are in the western section of the Geologist's Office.",
        "are in the store room. The only exit is to the south.",
        "are in the eastern section of the Geologist's Office.",
        "continue walking on the narrow road which has only one lane.",
        "are inside the mine lift.  The doorway is to the west.",
        "are in a dimly lit tunnel going west.  To the east is the lift.",
        "are in a poorly lit tunnel going east, west and south.",
        "are in a dimly lit tunnel going north.  To the east is the lift.",
        "are in a pasture.  To the east is a road and west is a billabong.",
        "are in a T-section going north, south or east.",
        "are in a T-section going north, south or west.",
        "are in a T-section going north, west or east.",
        "are in a T-section going east, west or south.",
        "are in a T-section going northeast, northwest, or southwest.",
        "are in a T-section going northeast, northwest, or southeast.",
        "are in a T-section going northeast, southeast, or southwest.",
        "are in a tunnel going north and south.",
        "are in a tunnel going east and west.",
        "are in a tunnel going northwest and southeast.",
        "are in a tunnel going northeast and southwest.",
        "are in a tunnel that has a hole in the ground and goes east/west.",
        "are in an L-section going southeast or up through a hole in the roof.",
        "are in an L-section that goes south or up through a hole in the roof.",
        "are in an L-section that goes east or up through a hole in the roof.",
        "are in an L-section that goes west or up through a hole in the roof.",
        "are in a Y-section that goes north, southeast, or southwest.",
        "are in a Y-section that goes south, northeast, or northwest.",
        "are in a Y-section that goes west, northeast, or southeast.",
        "are in a Y-section that goes east, northwest, or southwest.",
        "are in a Y-section that goes north, south, or southwest.",
        "are in a Y-section that goes south, north, or northwest.",
        "are in a Y-section that goes west, east, or southeast.",
        "are in a Y-section that goes east, west, or southwest.",
        "are in a Y-section that goes north, south, or southeast.",
        "are in a Y-section that goes south, north, or northeast.",
        "are in a Y-section that goes west, east, or northeast.",
        "are in a Y-section that goes east, west, or northwest.",
        "are in a Y-section that goes down a manhole, northwest, or southwest.",
        "are in a Y-section that goes down a manhole, northeast, or southeast.",
        "are at a tunnel crossing.  You can go north, east, south, or west.",
        "are at a tunnel crossing going nw, ne, sw, and se.",
        "are in a tunnel corner that goes south and west.",
        "are in a tunnel corner that goes south and east.",
        "are in a tunnel corner that goes north and west.",
        "are in a tunnel corner that goes north and east.",
        "are in a tunnel corner that goes southeast and southwest.",
        "are in a tunnel corner that goes southeast and northeast.",
        "are in a tunnel corner that goes northwest and southwest.",
        "are in a tunnel corner that goes northwest and northeast.",
        "are in a tunnel going southwest with a stairway going down.",
        "are in a tunnel going northeast with a stairway going up.",
        "are at a deadend.",
        "are in the ventilation equipment room. The exit is to the west.",
        "are in the worker's lunch room. The exit is to the north.",
        "are at the stope face with the hydraulic jacks still in place.",
        "are in an ore storage area with exits to the south and west.",
        "are in a T-section going northwest, southeast, or southwest.",
        "are in a tunnel going northeast/southwest, with a hole in the roof.",
        "are in a corner going north and east, with a manhole going down.",
        "are in a tunnel going west with a stairway going up.",
        "are in a tunnel going east with a stairway going down.",
        "can go north or climb down a man hole.",
        "are at a caved in section of tunnel which is now a dead end.",
        "are in a bleak and forbidding desert of bare flat stone.",
        "are on a narrow one lane road with gum trees on either side.",
        "are in a flat and boring desert.",
        "are in front of Ned Kelly's desert hide out.",
        "are in a vast desert of dry salt lakes shimmering with mirages.",
        "are inside Ned Kelly's hide out.  The only way out is to the west.",
        "are north of a grassy area and within the bend of a great river.",
        "are south of a grassy area and on the edge of a gum tree forest.",
        "can go east or climb down a man hole.",
        "can go east, west or climb up through a hole in the roof.",
        "are at the Level #67 Shift Foreman's office. You can go west.",
        "are at the spacecraft's end.  You can go north, south and west.",
        "are near the spacecraft's fin. You can go south or north-east.",
        "are next to the ship's wing.  You can go south-west or south-east.",
        "are beside the ship's window.  You can go north-west or south.",
        "are next to the spacecraft's Mach probe.  You can north or south.",
        "are facing the access hatch.  You can go up, north, or south-west.",
        "are beside the landing gear.  You can go north-east or north-west.",
        "are beside the wing's edge.  You can go north or south-east.",
        "are in the spacecraft's airlock.  You can go down or north.",
        "are in the central access way.  You can east, west, or south.",
        "are on the flight deck of an ancient spacecraft.  You can go west.",
        "are at a mysterious control panel.  You can east or west.",
        "are in the chamber with glowing hexagons. The only exit is east.",
        "are in a room with a tank full of dark blue liquid.  You can go west.",
        "are in a cavernous room with a huge silver gong in the middle.",
        "are in a room with a warning on the wall going north, south and west.",
        "are in a closet stinking of moth balls. The only exit is north.",
        "are in the guard room. The closet is south, the main doorway is east.",
        "are in the prayer room of the Iranian Parliament. The guard room is west.",
        "are in a tunnel going east and west with a message on the wall.",
        "are on a narrow country road.  There is a dead kangaroo by the road.",
        "are on a road with a low rise ahead of you.  You hear a distant rumbling.",
        "--- marker for getting hit by a road train.",
        "are on a narrow country road.  There is a dead red kangaroo by the road."
    )

    private val vocab = arrayOf(
        "north", "south", "east", "west", "up",
        "down", "northeast", "northwest", "southeast", "southwest",
        "n", "s", "e", "w", "u",
        "d", "ne", "nw", "se", "sw",
        "quit", "unlock", "lock", "take", "leave",
        "drop", "can", "bottle", "mat", "key",
        "butt", "look", "door", "open", "get",
        "throw", "turn", "on", "off", "push",
        "button", "enter", "switch", "zero", "forty-nine",
        "sixty-seven", "eighty-two", "0", "49", "67",
        "82", "beer", "lager", "doormat", "lift",
        "'on'", "'off'", "SVC", "map", "picture",
        "safe", "dial", "paper", "put", "close",
        "read", "gold", "bar", "dynamite", "rifle",
        "M16", "m16", "cap", "matches", "diamond",
        "ring", "ruby", "silver", "coin", "bill",
        "money", "teapot", "clip", "ammo", "box",
        "stick", "pills", "orange", "glowing", "saphire",
        "emerald", "score", "inventory", "examine", "describe",
        "drink", "fill", "water", "fourex", "invent",
        "view", "press", "exit", "all", "Fourex",
        "everything", "shoot", "kill", "ned", "Ned",
        "kelly", "Kelly", "pick", "grab", "combination",
        "select", "safety", "SAFE", "triple", "III",
        "single", "I", "auto", "AUTO", "automatic",
        "set", "insert", "attach", "plan", "gun",
        "magazine", "hoop", "snake", "bear", "wombat",
        "remove", "eject", "org_clip", "shut", "gleep",
        "gleeps", "letter", "envelope", "torch", "mail",
        "tank", "carefully", "gently", "softly", "slow_drop",
        "light", "ignite", "fuse", "slowly", "match",
        "cube", "blue", "blue-button", "gray", "gray-button",
        "clapper", "sound", "bang", "hit", "strike",
        "gong", "detector", "give", "STATS", "JSYS",
        "SU", "yellow", "yellow-button", "poster", "eat",
        "swallow", "pill", "atropine", "packet", "wrapper",
        "tyre", "shell", "brick", "rope", "package",
        "belt", "filter", "string", "cabinet", "calendar",
        "tackle", "bit", "jack", "pen", "pencil",
        "wire", "pipe", "panel", "opener", "cord",
        "photo", "chair", "bulb", "rag", "tube",
        "carpet", "branch", "cork", "trap", "lighter",
        "lace", "comb", "umbrella", "meadow", "billabong",
        "river", "road", "forest", "grass", "stream",
        "desert", "building", "office", "hideout", "dust",
        "mine", "tunnel", "tree", "spinifex", "hexagon",
        "spacecaft", "airlock", "wing", "hole", "tray",
        "liquid", "cockroach", "go", "scream", "yell",
        "bring", "help", "dump", "attack", "check",
        "inspect", "place", "touch", "pull", "extinguish",
        "ask", "talk", "tell", "find", "move",
        "break", "kick", "smash", "feed", "taste",
        "smell", "slam", "desk", "in", "into",
        "under", "out", "from", "by", "with",
        "fling", "what", "where", "are", "am",
        "have", "qUeStIoN", "how", "why", "who",
        "when", "leap", "walk", "run", "jump",
        "hop", "stroll", "saunter", "swagger", "swing",
        "dig", "swim", "depart", "info", "back",
        "hold", "fire", "stand", "painting", "doors",
        "treasure", "trigger", "load", "unload", "sand",
        "cockroaches", "kangaroo", "climb", "bulldust", "kangaroos",
        "spinifexes", "fuze", "message", "wall", "recorder",
        "red", "white", "green", "red-button", "white-button",
        "green-button", "grey", "matchbox", "map", "circuit",
        "lift-door", "transporter", "warning", "explosive", "self",
        "myself", "yourself", "one", "two", "three",
        "four", "five", "toss", "chart", "charts",
        "schedule", "schedules", "orange-button", "pants", "clothes",
        "clothing"
    )

    private val adjective = arrayOf(
        AdjectiveStruct(V_drop, V_bear, F_ignore, F_ignore),
        AdjectiveStruct(V_gold, V_bar, F_ignore, F_ignore),
        AdjectiveStruct(V_orange, V_clip, V_org_clip, F_replace),
        AdjectiveStruct(V_glowing, V_clip, V_org_clip, F_replace),
        AdjectiveStruct(V_blue, V_switch, V_blue_button, F_replace),
        AdjectiveStruct(V_blue, V_button, V_blue_button, F_replace),
        AdjectiveStruct(V_gray, V_switch, V_gray_button, F_replace),
        AdjectiveStruct(V_gray, V_button, V_gray_button, F_replace),
        AdjectiveStruct(V_yellow, V_switch, V_yellow_button, F_replace),
        AdjectiveStruct(V_yellow, V_button, V_yellow_button, F_replace),
        AdjectiveStruct(V_red, V_switch, V_red_button, F_replace),
        AdjectiveStruct(V_red, V_button, V_red_button, F_replace),
        AdjectiveStruct(V_white, V_switch, V_white_button, F_replace),
        AdjectiveStruct(V_white, V_button, V_white_button, F_replace),
        AdjectiveStruct(V_green, V_switch, V_green_button, F_replace),
        AdjectiveStruct(V_green, V_button, V_green_button, F_replace),
        AdjectiveStruct(V_grey, V_switch, V_gray_button, F_replace),
        AdjectiveStruct(V_grey, V_button, V_gray_button, F_replace),
        AdjectiveStruct(V_orange, V_switch, V_orange_button, F_replace),
        AdjectiveStruct(V_orange, V_button, V_orange_button, F_replace)
    )

    private val adverb = arrayOf(
        AdverbStruct(V_down, V_put, V_drop),
        AdverbStruct(V_down, V_set, V_drop),
        AdverbStruct(V_carefully, V_drop, V_slow_drop),
        AdverbStruct(V_gently, V_drop, V_slow_drop),
        AdverbStruct(V_softly, V_drop, V_slow_drop),
        AdverbStruct(V_slowly, V_drop, V_slow_drop),
        AdverbStruct(V_carefully, V_put, V_slow_drop),
        AdverbStruct(V_gently, V_put, V_slow_drop),
        AdverbStruct(V_softly, V_put, V_slow_drop),
        AdverbStruct(V_slowly, V_put, V_slow_drop),
        AdverbStruct(V_in, V_go, V_enter),
        AdverbStruct(V_in, V_move, V_enter),
        AdverbStruct(V_in, V_leap, V_enter),
        AdverbStruct(V_in, V_walk, V_enter),
        AdverbStruct(V_in, V_run, V_enter),
        AdverbStruct(V_in, V_jump, V_enter),
        AdverbStruct(V_in, V_hop, V_enter),
        AdverbStruct(V_in, V_stroll, V_enter),
        AdverbStruct(V_in, V_saunter, V_enter),
        AdverbStruct(V_in, V_swagger, V_enter),
        AdverbStruct(V_out, V_go, V_exit),
        AdverbStruct(V_out, V_move, V_exit),
        AdverbStruct(V_out, V_leap, V_exit),
        AdverbStruct(V_out, V_walk, V_exit),
        AdverbStruct(V_out, V_run, V_exit),
        AdverbStruct(V_out, V_jump, V_exit),
        AdverbStruct(V_out, V_hop, V_exit),
        AdverbStruct(V_out, V_stroll, V_exit),
        AdverbStruct(V_out, V_saunter, V_exit),
        AdverbStruct(V_out, V_swagger, V_exit)
    )

    private val read_object = arrayOf(
        ReadStruct(V_map_frag, true),
        ReadStruct(V_can, false),
        ReadStruct(V_recorder, false),
        ReadStruct(V_pills, false),
        ReadStruct(V_letter, true),
        ReadStruct(V_paper, true),
        ReadStruct(V_cube, false),
        ReadStruct(V_clip, false),
        ReadStruct(V_org_clip, false),
        ReadStruct(V_rifle, false),
        ReadStruct(V_detector, false),
        ReadStruct(V_safe, false),
        ReadStruct(V_gong, false),
        ReadStruct(V_message, true),
        ReadStruct(V_warning, true)
    )

    private val gleep_init = arrayOf(
        intArrayOf(97, 98, 101, 102),
        intArrayOf(98, 99, 102, 103),
        intArrayOf(99, 100, 103, 96),
        intArrayOf(100, 101, 96, 97),
        intArrayOf(101, 102, 97, 98),
        intArrayOf(102, 103, 98, 99),
        intArrayOf(103, 96, 99, 100),
        intArrayOf(134, 132, 136, 137),
        intArrayOf(69, 66, 90, 89),
        intArrayOf(74, 72, 68, 92)
    )

    private val mon_init = arrayOf(
        intArrayOf(139, 67, 86, 115),
        intArrayOf(77, 75, 78, 79),
        intArrayOf(103, 97, 98, 71),
        intArrayOf(64, 60, 62, 93),
        intArrayOf(67, 81, 66, 75),
        intArrayOf(93, 95, 94, 64),
        intArrayOf(75, 73, 76, 67),
        intArrayOf(71, 128, 67, 77),
        intArrayOf(52, 53, 51, 103),
        intArrayOf(115, 114, 113, 129),
        intArrayOf(131, 130, 129, 80),
        intArrayOf(119, 120, 118, 84),
        intArrayOf(109, 87, 83, 52),
        intArrayOf(241, 241, 241, 241),
        intArrayOf(242, 242, 242, 242)
    )

    private val obj_init = arrayOf(
        intArrayOf(V_can, 1, 1, 1, 1),
        intArrayOf(V_butt, 165, 166, 161, 152),
        intArrayOf(V_bottle, 2, 2, 2, 2),
        intArrayOf(V_gold, 54, 53, 55, 56),
        intArrayOf(V_dynamite, 78, 91, 69, 117),
        intArrayOf(V_clip, 48, 52, 52, 48),
        intArrayOf(V_org_clip, 80, 68, 113, 99),
        intArrayOf(V_rifle, 48, 56, 48, 52),
        intArrayOf(V_cap, 183, 184, 183, 184),
        intArrayOf(V_matches, 69, 85, 78, 121),
        intArrayOf(V_ring, 87, 135, 127, 105),
        intArrayOf(V_ruby, 99, 106, 114, 74),
        intArrayOf(V_coin, 127, 127, 87, 87),
        intArrayOf(V_bill, 135, 87, 135, 127),
        intArrayOf(V_pills, 85, 83, 91, 78),
        intArrayOf(V_saphire, 91, 78, 85, 69),
        intArrayOf(V_emerald, 68, 74, 117, 66),
        intArrayOf(V_letter, 177, 177, 177, 177),
        intArrayOf(V_torch, 92, 114, 66, 53),
        intArrayOf(V_cube, 202, 202, 202, 202),
        intArrayOf(V_wrapper, 171, 216, 239, 101),
        intArrayOf(V_tire, 47, 47, 47, 47),
        intArrayOf(V_shell, 82, 107, 213, 175),
        intArrayOf(V_brick, 33, 33, 33, 33),
        intArrayOf(V_rope, 209, 179, 181, 155),
        intArrayOf(V_package, 156, 137, 95, 113),
        intArrayOf(V_belt, 21, 21, 21, 21),
        intArrayOf(V_filter, 142, 142, 142, 142),
        intArrayOf(V_string, 204, 88, 118, 167),
        intArrayOf(V_umbrella, 37, 37, 37, 37),
        intArrayOf(V_tackle, 9, 9, 9, 9),
        intArrayOf(V_bit, 105, 117, 90, 184),
        intArrayOf(V_jack, 81, 81, 81, 81),
        intArrayOf(V_pen, 177, 177, 177, 177),
        intArrayOf(V_pencil, 202, 202, 202, 202),
        intArrayOf(V_wire, 214, 154, 159, 151),
        intArrayOf(V_pipe, 206, 239, 166, 151),
        intArrayOf(V_opener, 63, 62, 61, 60),
        intArrayOf(V_cord, 80, 131, 119, 79),
        intArrayOf(V_photo, 206, 160, 155, 180),
        intArrayOf(V_chair, 177, 177, 177, 177),
        intArrayOf(V_bulb, 154, 162, 164, 181),
        intArrayOf(V_rag, 92, 89, 73, 132),
        intArrayOf(V_tube, 139, 121, 129, 132),
        intArrayOf(V_carpet, 41, 41, 41, 41),
        intArrayOf(V_branch, 28, 28, 28, 28),
        intArrayOf(V_cork, 130, 122, 115, 109),
        intArrayOf(V_trap, 146, 146, 146, 146),
        intArrayOf(V_lighter, 210, 213, 46, 79),
        intArrayOf(V_lace, 126, 136, 67, 72),
        intArrayOf(V_comb, 164, 157, 156, 172)
    )

    private val verb_table = intArrayOf(
        V_quit, V_unlock, V_lock, V_take, V_leave, V_drop, V_look, V_open,
        V_get, V_throw, V_turn, V_on, V_off, V_push, V_enter, V_switch,
        V_put, V_close, V_read, V_drink, V_invent, V_view, V_press, V_exit,
        V_shoot, V_kill, V_pick, V_grab, V_combination, V_select, V_safety,
        V_triple, V_single, V_auto, V_set, V_insert, V_attach, V_remove,
        V_eject, V_shut, V_carefully, V_gently, V_softly, V_slow_drop,
        V_light, V_ignite, V_slowly, V_sound, V_bang, V_hit, V_strike,
        V_svc, V_give, V_stats, V_jsys, V_su, V_eat, V_swallow, V_fill, V_go,
        V_scream, V_yell, V_bring, V_help, V_dump, V_attack, V_check,
        V_inspect, V_place, V_touch, V_pull, V_extinguish, V_ask, V_talk,
        V_tell, V_find, V_move, V_break, V_kick, V_smash, V_feed, V_taste,
        V_smell, V_slam, V_toss, V_climb, V_swim, V_back, V_stand, V_load,
        V_unload
    )

    private val quest = intArrayOf(
        V_QUESTION, V_what, V_where, V_are, V_am, V_have, V_how, V_why,
        V_who, V_when
    )

    private val objectList = arrayOf(
        ObjectStruct(0, 0, 0, V_can, 10, 0, "an empty can of Fourex beer"),
        ObjectStruct(0, 0, 0, V_butt, 1, 0, "a cigarette butt"),
        ObjectStruct(0, 0, 0, V_bottle, 20, 0, "an empty bottle of Black Swan Lager"),
        ObjectStruct(36, 0, Z_transform, V_mat, 300, 0, "an old doormat with \"ACME Gold Mines Ltd.\" written on it"),
        ObjectStruct(B_unmade, 0, 0, V_key, 5, 0, "a large brass key"),
        ObjectStruct(0, 0, 0, V_umbrella, 100, 0, "a ripped and bent up umbrella"),
        ObjectStruct(0, 0, 0, V_tackle, 50, 0, "a two metre length of fishing tackle without a hook"),
        ObjectStruct(0, 0, 0, V_bit, 10, 0, "a broken drill bit"),
        ObjectStruct(38, 0, Z_transform, V_map, 10, 0, "a very old but detailed map of the ACME Mine"),
        ObjectStruct(B_unmade, 0, 0, V_map_frag, 10, 0, "a fragment of a map showing the ACME Mine"),
        ObjectStruct(B_in_safe, 0, 0, V_paper, 10, 0, "a sheet of paper with some writing on it"),
        ObjectStruct(0, 200, 0, V_gold, 500, 0, "a gold bar weighing ten kilograms"),
        ObjectStruct(0, 0, 0, V_jack, 400, 0, "a hydraulic jack which is rusted solid"),
        ObjectStruct(0, 0, 0, V_dynamite, 100, 0, "a large stick of dynamite"),
        ObjectStruct(0, 0, 0, V_clip, 50, 150, "an M16 ammo clip designed to hold up to 200 rounds"),
        ObjectStruct(0, 0, 0, V_pen, 5, 0, "a fountain pen which is dried up and useless"),
        ObjectStruct(0, 0, 0, V_org_clip, 50, 5, "an orange M16 ammo clip which is glowing a faint pale blue"),
        ObjectStruct(0, 0, 0, V_pencil, 5, 0, "a mechanical pencil without any leads"),
        ObjectStruct(0, 0, 0, V_rifle, 300, 0, "an unloaded, fully automatic M16 infantry rifle"),
        ObjectStruct(0, 0, 0, V_wire, 100, 0, "a piece of copper wire"),
        ObjectStruct(0, 0, 0, V_pipe, 200, 0, "a short length of galvanized iron pipe"),
        ObjectStruct(0, 0, 0, V_cap, 5, 0, "a blasting cap with 20 cm. of fuse attached"),
        ObjectStruct(0, 0, 0, V_matches, 2, 0, "a box of \"Red Head\" matches"),
        ObjectStruct(0, 100, 0, V_ring, 5, 0, "a diamond ring with a three carat flawless blue diamond"),
        ObjectStruct(0, 0, 0, V_opener, 10, 0, "a can opener"),
        ObjectStruct(0, 100, 0, V_ruby, 30, 0, "a bright red ruby the size of an egg"),
        ObjectStruct(B_unmade, 200, 0, V_teapot, 50, 0, "an antique sterling silver teapot with \"N.K.\" engraved on it"),
        ObjectStruct(0, 150, 0, V_coin, 7, 0, "a silver Syracusian Dekadrachma coin"),
        ObjectStruct(0, 0, 0, V_cord, 70, 0, "an electrical extension cord"),
        ObjectStruct(0, 100, 0, V_bill, 5, 0, "a $100 dollar bill"),
        ObjectStruct(0, 0, 0, V_photo, 5, 0, "an autographed photo of Sir Joh"),
        ObjectStruct(0, 0, 0, V_chair, 500, 0, "an old folding chair"),
        ObjectStruct(0, 0, 0, V_bulb, 10, 0, "a burned out light bulb"),
        ObjectStruct(0, 0, 0, V_pills, 10, 0, "a packet containing atropine pills"),
        ObjectStruct(0, 150, 0, V_saphire, 30, 0, "a high quality artificial saphire"),
        ObjectStruct(0, 100, 0, V_emerald, 30, 0, "a huge uncut emerald"),
        ObjectStruct(0, 0, 0, V_rag, 50, 0, "an oily rag"),
        ObjectStruct(0, 0, 0, V_tube, 50, 0, "a burned out fluorescent tube"),
        ObjectStruct(0, 0, 0, V_carpet, 400, 0, "a filthy worn out carpet"),
        ObjectStruct(0, 0, 0, V_branch, 400, 0, "a branch from a gum tree"),
        ObjectStruct(0, 0, 0, V_letter, 10, 0, "a thick airmail letter, marked \"Postage Due\""),
        ObjectStruct(0, 0, 0, V_cork, 5, 0, "a cork from a wine bottle"),
        ObjectStruct(0, 0, 0, V_torch, 100, 0, "a high intensity battery powered torch"),
        ObjectStruct(0, 0, 0, V_trap, 5, 0, "an old cockroach trap"),
        ObjectStruct(0, 0, 0, V_lighter, 5, 0, "an empty butane cigarette lighter"),
        ObjectStruct(0, 0, 0, V_cube, 50, 0, "a small black cube with a blue push button on one side"),
        ObjectStruct(B_unmade, 700, 0, V_clapper, 100, 0, "an ancient and priceless gong clapper made of ivory and gold"),
        ObjectStruct(B_unmade, 0, 0, V_detector, 100, 0, "a Semtex explosive detector with a push button switch"),
        ObjectStruct(0, 0, 0, V_lace, 2, 0, "a broken shoe lace"),
        ObjectStruct(0, 0, 0, V_comb, 5, 0, "a dirty old comb"),
        ObjectStruct(0, 0, 0, V_wrapper, 3, 0, "a Cornetto ice cream cone wrapper"),
        ObjectStruct(0, 0, 0, V_tire, 400, 0, "a bald automobile tyre"),
        ObjectStruct(0, 0, 0, V_shell, 3, 0, "an empty broken egg shell"),
        ObjectStruct(0, 0, 0, V_brick, 100, 0, "a broken piece of brick"),
        ObjectStruct(0, 0, 0, V_rope, 100, 0, "a one metre length of rope"),
        ObjectStruct(0, 0, 0, V_package, 5, 0, "an empty package of Stradbroke cigarettes"),
        ObjectStruct(0, 0, 0, V_belt, 10, 0, "a broken automobile fan belt"),
        ObjectStruct(0, 0, 0, V_filter, 50, 0, "a used automobile oil filter"),
        ObjectStruct(0, 0, 0, V_string, 5, 0, "a short piece of string"),
        ObjectStruct(B_unmade, 0, 0, V_recorder, 0, S_inactive, "a data recorder having four coloured buttons"),
        ObjectStruct(39, 0, Z_unmovable, V_poster, 0, 0, "Error"),
        ObjectStruct(41, 0, Z_unmovable, V_poster, 0, 0, "Error"),
        ObjectStruct(39, 0, Z_unmovable, V_cockroach, 0, 0, "Error"),
        ObjectStruct(41, 0, Z_unmovable, V_cockroach, 0, 0, "Error"),
        ObjectStruct(243, 0, Z_unmovable, V_kangaroo, 0, 0, "Error"),
        ObjectStruct(246, 0, Z_unmovable, V_kangaroo, 0, 0, "Error"),
        ObjectStruct(26, 0, Z_unmovable, V_spinifex, 0, 0, "Error"),
        ObjectStruct(R_gong, 0, Z_unmovable, V_gong, 0, S_fair_game, "Error"),
        ObjectStruct(23, 0, Z_unmovable, V_bulldust, 0, 0, "Error"),
        ObjectStruct(159, 0, Z_unmovable, V_message, 0, 0, "Error"),
        ObjectStruct(R_store_room, 0, Z_unmovable, V_circuit, 0, 0, "Error"),
        ObjectStruct(R_lift_inside, 0, Z_unmovable, V_lift, 0, L0, "Error"),
        ObjectStruct(R_office_entr, 0, Z_unmovable, V_door, 0, S_closed, "Error"),
        ObjectStruct(R_office_mang, 0, Z_unmovable, V_safe, 0, S_closed, "Error"),
        ObjectStruct(R_lift_entr, 0, Z_unmovable, V_lift_door, 0, S_closed, "Error"),
        ObjectStruct(R_office_mang, 0, Z_unmovable, V_picture, 0, S_closed, "Error"),
        ObjectStruct(R_gleep_tank, 0, Z_unmovable, V_tank, 0, 0, "Error"),
        ObjectStruct(R_transporter, 0, Z_unmovable, V_transporter, 0, 0, "Error"),
        ObjectStruct(R_warning, 0, Z_unmovable, V_warning, 0, 0, "Error"),
        ObjectStruct(R_office_mang, 0, Z_unmovable, V_chart, 0, 0, "Error"),
        ObjectStruct(R_office_mang, 0, Z_unmovable, V_desk, 0, 0, "Error"),
        ObjectStruct(R_foreman, 0, Z_unmovable, V_desk, 0, 0, "Error")
    )

    private fun pointToObject(find_id: Int): ObjectStruct {
        for (obj in objectList) {
            if (obj.id == find_id) return obj
        }
        myPrintf("Run time error detected in \"point_to_object\" function.\n")
        myPrintf("\"find_id\" value was %d.\n", find_id)
        bugs(Logic_error)
        return objectList[0]
    }

    private suspend fun myGetchar(): Char {
        val line = inputQueue.take()
        return if (line.isNotEmpty()) line[0] else '\n'
    }

    private suspend fun yesNo(): Boolean {
        while (true) {
            val chr = myGetchar()
            if (chr == 'y' || chr == 'Y') return true
            if (chr == 'n' || chr == 'N') return false
            myPrintf("(Answer:  Yes or No) ")
        }
    }

    private suspend fun hold_it() {
        myPrintf(" --- press return to continue --- \n")
        inputQueue.take()
    }

    private fun myExit(status: Int) {
        myPrintf("\nDinkum adventure will restart in 2 seconds...\n")
        try {
            Thread.sleep(2000)
        } catch (e: InterruptedException) {
            // ignore
        }
        inputQueue.clear()
        start()
        throw InterruptedException("Game Restarted")
    }

    private fun bugs(i_type: Int) {
        if (i_type == Request) {
            myPrintf("\nSend bug reports to Gary Allen.\n")
            myPrintf("My Email address is:  allen@me.chalmers.se\n")
            return
        }
        myPrintf("Bugs!!  There is a logic error in this program!\n")
        myPrintf("Please write down what you just did and inform the author.\n")
        myPrintf("My Email address is:  allen@me.chalmers.se\n")
        myExit(Failed)
    }

    private fun newScore() {
        var acc = 0
        for (obj in objectList) {
            if (obj.location == B_in_safe) {
                acc += obj.value
            }
        }
        if (gleep_safe > 0) {
            acc += gleep_safe * 10
            if (acc > max_score - 100) {
                acc = max_score - 100
            }
        }
        if (sw_wizard) {
            acc = max_score
        }
        score = acc
    }

    private fun ender(i_quit: Int) {
        newScore()
        stopRecordingOrPlayback()
        
        if (i_quit == F_died) {
            myPrintf("\nMy statistics indicate that you are dead. ")
            myPrintf("Better luck next time!\n")
        }
        if (i_quit == F_ignore) {
            myPrintf("\nCongratulations!!  You finished the game.\n")
            if (score >= max_score) {
                myPrintf("You scored %d points which makes you a \"fair dinkum\" master!\n", score)
            } else {
                myPrintf("You scored %d points. Only \"fair dinkum\" masters score %d points.\n", score, max_score)
            }
        }
        if (i_quit == F_quit) {
            myPrintf("\nToo bad.  I thought you had more guts!\n")
        }
        
        myPrintf("\nIn this adventure you scored %d points out of a possible %d.\n", score, max_score)
        
        throw GameEndedException()
    }

    private fun boom() {
        myPrintf("               B O O M !!!\n\n")
        myPrintf("The dynamite went off! ")
        destroy_all()
    }



    private fun purge(k_start: Int, k_finish: Int) {
        for (k in k_start until k_finish - 1) {
            sent[k] = sent[k + 1]
        }
        sent[k_finish - 1] = 0
    }

    private fun synonym(jaccParam: Int): Boolean {
        var jacc = jaccParam
        var sw_movement = false
        when (sent[0]) {
            V_go, V_move, V_walk, V_run, V_jump, V_leap, V_hop, V_stroll, V_saunter, V_swagger -> {
                sw_movement = true
            }
        }

        var sw_adverb = false
        var sw_adverb_fnd = false
        var j_point = -1

        for (i in 0 until Adv_max) {
            val adv = adverb[i]
            for (j in 1 until jacc) {
                if (sent[j] == adv.adverb) {
                    j_point = j
                    sw_adverb = true
                    if (adv.modifiedVerb == sent[0]) {
                        sent[0] = adv.generatedVerb
                        sw_adverb = false
                        sw_adverb_fnd = true
                        purge(j, jacc)
                        jacc--
                    }
                }
            }
        }

        if (sw_adverb && !sw_movement) {
            purge(j_point, jacc)
            jacc--
        }

        if (!sw_adverb_fnd && sw_movement) {
            for (j in 0 until jacc) {
                if (sent[j] <= 10) {
                    tag[V_MOVE] = true
                    tag[V_DIRECTION] = true
                    verb = sent[j]
                    return false
                }
                if (sent[j] == V_LINE_END) break
            }
            myPrintf("Where to?  In what direction?\n")
            return true
        }

        tag[sent[0]] = true

        if (jacc == 1) {
            tag[V_VERB_ONLY] = true
            verb = sent[0]
            return false
        }

        for (j in 1 until jacc) {
            if (sent[j] == V_LINE_END) break
            if (sent[j] <= 10) tag[V_DIRECTION] = true
            if (sent[j] <= 4) continue
            tag[sent[j]] = true

            // synonym nouns
            when (sent[j]) {
                V_automatic, V_AUTO -> tag[V_auto] = true
                V_bar -> tag[V_gold] = true
                V_clothes, V_clothing -> tag[V_pants] = true
                V_beer, V_fourex, V_Fourex -> tag[V_can] = true
                V_cockroaches -> {
                    tag[V_PLURAL] = true
                    tag[V_cockroach] = true
                }
                V_diamond -> tag[V_ring] = true
                V_doormat -> tag[V_mat] = true
                V_drop -> tag[V_bear] = true
                V_everything -> tag[V_all] = true
                V_wall -> {
                    tag[V_warning] = true
                    tag[V_message] = true
                }
                V_money -> tag[V_bill] = true
                V_lager -> tag[V_bottle] = true
                V_fuze, V_fuse -> tag[V_cap] = true
                V_gleeps -> {
                    tag[V_PLURAL] = true
                    tag[V_gleep] = true
                }
                V_hoop -> tag[V_snake] = true
                V_M16, V_m16, V_gun -> tag[V_rifle] = true
                V_kangaroos -> {
                    tag[V_PLURAL] = true
                    tag[V_kangaroo] = true
                }
                V_magazine, V_ammo -> tag[V_clip] = true
                V_mail, V_envelope -> tag[V_letter] = true
                V_charts, V_schedule, V_schedules -> tag[V_chart] = true
                V_matchbox -> {
                    tag[V_matches] = true
                    tag[V_PLURAL] = true
                    tag[V_match] = true
                }
                V_matches -> {
                    tag[V_PLURAL] = true
                    tag[V_match] = true
                }
                V_Ned, V_kelly, V_Kelly -> tag[V_ned] = true
                V_off_q -> tag[V_off] = true
                V_on_q -> tag[V_on] = true
                V_plan -> {
                    tag[V_map] = true
                    tag[V_map_frag] = true
                }
                V_map -> tag[V_map_frag] = true
                V_painting -> tag[V_picture] = true
                V_picture -> tag[V_photo] = true
                V_doors -> {
                    tag[V_PLURAL] = true
                    tag[V_door] = true
                }
                V_pills, V_packet -> {
                    tag[V_PLURAL] = true
                    tag[V_pill] = true
                }
                V_atropine -> tag[V_pill] = true
                V_safety, V_SAFE -> tag[V_safe] = true
                V_silver -> tag[V_coin] = true
                V_spinifexes -> {
                    tag[V_PLURAL] = true
                    tag[V_spinifex] = true
                }
                V_yourself, V_myself -> tag[V_self] = true
                V_stick, V_explosive -> tag[V_dynamite] = true
                V_switch -> tag[V_button] = true
                V_treasure -> tag[V_all] = true
                V_I -> tag[V_single] = true
                V_III -> tag[V_triple] = true
                V_zero -> {
                    tag[V_0] = true
                    tag[V_NUMBER] = true
                    number_word = 0
                }
                V_one -> {
                    tag[V_NUMBER] = true
                    number_word = 1
                }
                V_two -> {
                    tag[V_NUMBER] = true
                    number_word = 2
                }
                V_three -> {
                    tag[V_NUMBER] = true
                    number_word = 3
                }
                V_four -> {
                    tag[V_NUMBER] = true
                    number_word = 4
                }
                V_five -> {
                    tag[V_NUMBER] = true
                    number_word = 5
                }
                V_forty_nine -> tag[V_49] = true
                V_sixty_seven -> tag[V_67] = true
                V_eighty_two -> tag[V_82] = true
            }
        }
        verb = sent[0]
        return false
    }

    private var jacc = 0

    private suspend fun parse() {
        var line = ""
        if (recorderStatus == S_playing) {
            val nextLine = fpReader?.readLine()
            if (nextLine == null) {
                recorderStatus = S_inactive
                myPrintf("Data recorder playback stopped.\n")
                line = inputQueue.take()
            } else {
                line = nextLine
                myPrintf(line + "\n")
            }
        } else {
            line = inputQueue.take()
            if (recorderStatus == S_recording) {
                try {
                    fpWriter?.write(line)
                    fpWriter?.newLine()
                    fpWriter?.flush()
                } catch (e: Exception) {
                    GcLog.e("Error writing to recording file", e)
                }
            }
        }

        val cleanLine = line.lowercase().trim()
        val words = ArrayList<String>()
        var currentWord = StringBuilder()
        for (char in cleanLine) {
            if (char in "\",.;:?!&{}()[])'` \t\n\r") {
                if (currentWord.isNotEmpty()) {
                    words.add(currentWord.toString())
                    currentWord = StringBuilder()
                }
            } else {
                if (currentWord.length < 14) {
                    currentWord.append(char)
                }
            }
        }
        if (currentWord.isNotEmpty()) {
            words.add(currentWord.toString())
        }

        sent.fill(0)
        tag.fill(false)
        number_word = 0
        jacc = 0

        for (i in 0 until words.size) {
            val word = words[i]
            var matched = false
            for (j in 0 until Vocab_cnt) {
                if (word == vocab[j]) {
                    var wordId = j
                    if (wordId in 10..19) {
                        wordId -= 10
                    }
                    if (jacc < 19) {
                        sent[jacc++] = wordId + 1
                    }
                    matched = true
                    break
                }
            }
            if (!matched) {
                if (word.isNotEmpty() && word.all { it.isDigit() }) {
                    tag[V_NUMBER] = true
                    try {
                        number_word = word.toInt()
                    } catch (e: Exception) {}
                }
            }
        }

        if (jacc == 0) {
            myPrintf("Huh? Nothing you said was understandable! Try again. \n")
            return
        }

        sent[jacc] = V_LINE_END

        if (sent[0] <= 10) {
            tag[V_MOVE] = true
            tag[V_DIRECTION] = true
            verb = sent[0]
            return
        }

        var sw_purge = false
        var j_purge = -1
        for (j in 0 until jacc) {
            for (i in 0 until Adj_max) {
                val adj = adjective[i]
                if (sent[j] == adj.adjective) {
                    if (adj.command == F_ignore && (j + 1 >= jacc || adj.modifiedNoun != sent[j + 1])) {
                        break
                    }
                    if (adj.command == F_replace && j + 1 < jacc && adj.modifiedNoun == sent[j + 1]) {
                        sent[j + 1] = adj.generatedNoun
                    }
                    sw_purge = true
                    j_purge = j
                }
            }
        }
        if (sw_purge && j_purge != -1) {
            purge(j_purge, jacc)
            jacc--
        }

        for (i in 0 until Verb_max) {
            if (sent[0] == verb_table[i]) {
                if (synonym(jacc)) {
                    jacc = 0
                    return
                }
                return
            }
        }

        for (i in 0 until Quest_max) {
            if (sent[0] == quest[i]) {
                for (j in jacc downTo 0) {
                    sent[j + 1] = sent[j]
                }
                sent[0] = V_QUESTION
                tag[V_QUESTION] = true
                jacc++
                if (jacc == 2) {
                    tag[V_VERB_ONLY] = true
                    tag[sent[1]] = true
                    verb = V_QUESTION
                    return
                }
                if (synonym(jacc)) {
                    jacc = 0
                    return
                }
                return
            }
        }

        if (jacc != 1) {
            for (j in 1 until jacc) {
                if (sent[j] <= 10) {
                    tag[V_MOVE] = true
                    val tmp = sent[0]
                    sent[0] = sent[j]
                    sent[j] = tmp
                    if (synonym(jacc)) {
                        jacc = 0
                        return
                    }
                    return
                }
                for (i in 0 until Verb_max) {
                    if (sent[j] == verb_table[i]) {
                        val tmp = sent[0]
                        sent[0] = sent[j]
                        sent[j] = tmp
                        if (synonym(jacc)) {
                            jacc = 0
                            return
                        }
                        return
                    }
                }
            }
        }

        myPrintf("If there was a verb in that sentence, I didn't understand it.\n")
        jacc = 0
    }

    private fun startPlayback(name: String): Boolean {
        val cleanName = name.trim().lowercase()
        val backupFile = File(backupDirectory, "$cleanName.dinkumsav")
        if (!backupFile.exists()) {
            myPrintf("Can't open file\n${backupFile.absolutePath}.\n")
            return false
        }
        val localFile = File(localFilesDirectory, cleanName)
        try {
            backupFile.copyTo(localFile, overwrite = true)
            fpReader = localFile.bufferedReader()
            fileName = cleanName
            recorderStatus = S_playing
            myPrintf("Playback starting.\n")
            return true
        } catch (e: Exception) {
            myPrintf("Can't open game file\n$cleanName\n")
            GcLog.e("Error starting playback", e)
            return false
        }
    }

    private fun startRecording(name: String): Boolean {
        val cleanName = name.trim().lowercase()
        val localFile = File(localFilesDirectory, cleanName)
        try {
            fpWriter = localFile.bufferedWriter()
            fileName = cleanName
            recorderStatus = S_recording
            myPrintf("Recording starting.\n")
            return true
        } catch (e: Exception) {
            myPrintf("Unable to open the output file!\n")
            GcLog.e("Error starting recording", e)
            return false
        }
    }

    private fun stopRecordingOrPlayback() {
        if (recorderStatus == S_playing) {
            try {
                fpReader?.close()
            } catch (e: Exception) {}
            fpReader = null
            recorderStatus = S_inactive
            myPrintf("Playback stopped.\n")
        } else if (recorderStatus == S_recording) {
            try {
                fpWriter?.close()
            } catch (e: Exception) {}
            fpWriter = null
            recorderStatus = S_inactive
            myPrintf("Recording stopped.\n")

            val localFile = File(localFilesDirectory, fileName)
            val backupFile = File(backupDirectory, "$fileName.dinkumsav")
            try {
                if (localFile.exists()) {
                    localFile.copyTo(backupFile, overwrite = true)
                }
            } catch (e: Exception) {
                myPrintf("Can't open game file\n${backupFile.absolutePath}\nin the external storage.\n")
                GcLog.e("Error copying recorded file to backup directory", e)
            }
        }
    }

    private suspend fun runGame() {
        try {
            // produce two random numbers ranging from 0-9 based on system clock
            l_time = System.currentTimeMillis() / 1000L
            start_time = l_time
            val l_base = l_time / 10L * 10L
            val l_pass = l_time - l_base
            val dig_2 = l_pass.toInt()
            val dig_1 = random.nextInt(10)

        val i_rand1: Int
        val i_rand2: Int
        val i_rand3: Int
        if (sw_standard) {
            i_rand1 = 2
            i_rand2 = 3
            i_rand3 = 1
        } else {
            i_rand3 = (dig_1 + dig_2) / 2
            i_rand2 = dig_2 / 3
            i_rand1 = (dig_1 / 3) + 1
        }

        // Initialize monsters data structure (Enemy_cnt + 1 elements, indices 0 to 15)
        monster_start = Array(Enemy_cnt + 1) { i ->
            val loc = if (i == 0) B_unmade else mon_init[i - 1][i_rand2]
            val type = when (i) {
                2, 3, 4, 5 -> N_drop_bear
                6, 7, 8, 9, 10, 11, 12, 13 -> N_hoop_snake
                else -> i
            }
            MonsterStruct(
                type = type,
                location = loc,
                status = F_asleep,
                hits = 0
            )
        }
        Ned = monster_start[N_ned]
        Wombat = monster_start[N_wombat]
        Guards = monster_start[N_guards]
        Mullah = monster_start[N_mullah]

        // Initialize specific object references
        Gong = pointToObject(V_gong)
        Recorder = pointToObject(V_recorder)

        Gong.status = S_recorder
        Recorder.location = R_meadow

        // Load objects into rooms
        for (obj in objectList) {
            for (i in 0 until Obj_init_max) {
                if (obj.id == obj_init[i][0]) {
                    obj.location = obj_init[i][i_rand1]
                    break
                }
            }
            val loc = obj.location
            if (obj.type == Z_unmovable) {
                if (loc >= 0 && loc < room.size) {
                    room[loc][M_unmov_obj] = 1
                }
            } else if (loc >= 0 && loc < room.size) {
                room[loc][M_obj_cnt]++
            }
        }

        // Initialize other specific object references
        Rifle = pointToObject(V_rifle)
        Rifle.status = obj_init[7][i_rand1] * 20

        Org_clip = pointToObject(V_org_clip)
        Org_clip.status = obj_init[6][i_rand1]

        // load the gleep reproductive locations and seed gleep
        for (i in 0..9) {
            gleep_spot[i] = gleep_init[i][i_rand2]
        }
        room[gleep_spot[0] - 1][M_gleep] = 1

        // load safe passage for end-game killing passages
        room[218 + i_rand3][M_rm_type] = T_short_descp

        // Initialize other object references
        Teapot = pointToObject(V_teapot)
        Can = pointToObject(V_can)
        Clip = pointToObject(V_clip)
        Cap = pointToObject(V_cap)
        Dynamite = pointToObject(V_dynamite)
        Letter = pointToObject(V_letter)
        Clapper = pointToObject(V_clapper)
        Detector = pointToObject(V_detector)
        Key = pointToObject(V_key)
        Umbrella = pointToObject(V_umbrella)
        Matches = pointToObject(V_matches)
        Pills = pointToObject(V_pills)
        Cube = pointToObject(V_cube)
        Torch = pointToObject(V_torch)
        Mat = pointToObject(V_mat)
        Map = pointToObject(V_map)
        Map_frag = pointToObject(V_map_frag)
        Bottle = pointToObject(V_bottle)
        Circuit_breaker = pointToObject(V_circuit)
        Lift = pointToObject(V_lift)
        Door = pointToObject(V_door)
        Safe = pointToObject(V_safe)
        Lift_door = pointToObject(V_lift_door)
        Picture = pointToObject(V_picture)
        Tank = pointToObject(V_tank)
        Transporter = pointToObject(V_transporter)

        myPrintf(" \n--- Dinkum --- \n                         \n                    Version 2.14, 23 February 1994\n\n")
        myPrintf("Would you like some initial help with Dinkum?\n")
        if (yesNo()) {
            myPrintf("\nG'day Mate!  Welcome to \"Dinkum\" the Australian adventure\n")
            myPrintf("game.  In Dinkum you'll search for treasure in the\n")
            myPrintf("Australian Outback.  You give the commands and I'll do the\n")
            myPrintf("dangerous work. I understand most plain English sentences.\n")
            myPrintf("If you want me to go east then type \"Will you please go\n")
            myPrintf("east?\" and press the RETURN or ENTER key.  If you don't\n")
            myPrintf("enjoy typing then just type \"e\" and press RETURN.  I'll\n")
            myPrintf("still go east.  After you have gathered up some things\n")
            myPrintf("you can see what you have by typing \"inventory\".\n")
            myPrintf("Sometimes you can learn more about an object by typing\n")
            myPrintf("\"examine NAME-OF-OBJECT\".  I understand many other\n")
            myPrintf("commands and words.  However I'll let you have the fun of\n")
            myPrintf("finding out what I can and can not understand. Good luck!\n\n")
        }
        myPrintf("\nIt's a hot summer day (in January) in Queensland, Australia.\n")

        start_time = System.currentTimeMillis() / 1000L
        val loc = intArrayOf(R_meadow, -999, 0)

        while (true) {
            newScore()
            l_time = System.currentTimeMillis() / 1000L

            if (!sw_warned && (l_time > start_time + 3300L)) {
                sw_warned = true
                myPrintf("\nAttention:  Only five minutes left until my clock runs out!\n")
            }

            if (l_time > start_time + 3600L) {
                myPrintf("\nTime's up!  I've got to run to catch my bus!\n")
                ender(F_died)
            }

            kelly(loc[0])

            val shouldDescribe = (!((room[R_hideout][M_monster] > 0) && (loc[0] == R_hideout))) &&
                    (!((monster_flag == F_monster_active) && (loc[2] == loc[0])))

            if (shouldDescribe) {
                loc[2] = 0
                if (room[loc[0]][M_rm_type] == T_long_descp) {
                    long_descp(loc[0])
                } else {
                    describe(loc[0])
                    if (room[loc[0]][M_unmov_obj] != 0) {
                        actor(loc[0])
                    }
                }
                objlooker(loc[0])
                gleeper(loc[0])

                // spaceship transporter logic
                if (Transporter.status != 0) {
                    if (loc[0] == R_ship_passage) {
                        Transporter.status = 0
                    } else if (loc[0] == R_transporter) {
                        loc[0] = R_closet
                        Transporter.status = 0
                        myPrintf("\nSuddenly radiant energy seems to surge from the hexagons\n")
                        myPrintf("around you.  Then there is a bright flash and you find\n")
                        myPrintf("yourself in.....\n\n")
                        myPrintf("A closet stinking of moth balls full of nondescript junk.\n")
                        myPrintf("The only exit is to the north through a curtain hanging\n")
                        myPrintf("over the entrance.\n\n")
                        loc[2] = loc[0]
                        continue
                    }
                }
            }

            loc[1] = loc[0]

            while (true) {
                var sw_loop = false

                // monster check
                if (room[loc[0]][M_monster] > 0 || monster_flag != F_no_monster) {
                    if (monster_flag == F_wounded) {
                        monster_flag = F_monster_active
                    } else {
                        monster(loc)
                        if (loc[0] != loc[1]) {
                            sw_loop = true
                        }
                    }
                }
                if (sw_loop) break

                myPrintf("> ")
                parse()

                // poison checks for hypertoxic ammunition clip
                if (i_poison > 0) {
                    i_poison--
                } else {
                    if (Org_clip.location == B_have) {
                        i_poison--
                        when (i_poison) {
                            -20 -> myPrintf("You've developed a nervous tick near your eye.\n")
                            -30 -> myPrintf("You're hands are starting to shake and you're sweating like a pig.\n")
                            -35 -> myPrintf("You're shaking so bad you can barely stand and you can only see in black and white.\n")
                            -40 -> {
                                myPrintf("Your chest has developed a terrible pain.  Your breathing\n")
                                myPrintf("is labored and you can hear your heart pounding like a drum.\n")
                            }
                            -45 -> {
                                myPrintf("You have collapsed onto your back with your legs and arms\n")
                                myPrintf("flailing in the air.  You begin to twitch spasmodically.\n")
                                myPrintf("Slowly your spasms reduce in frequency.  With the coming of\n")
                                myPrintf("death you grind down to a halt.\n\n")
                                ender(F_died)
                            }
                        }
                    }
                }

                // clock explosion check (dynamite fuse)
                if (sw_clock) {
                    val current_time = System.currentTimeMillis() / 1000L
                    if (current_time > clock_explode) {
                        sw_clock = false
                        if (flag_clock == loc[0]) {
                            boom()
                            ender(F_died)
                        } else {
                            if ((loc[0] >= 43 && loc[0] <= 101) || (loc[0] >= 166 && loc[0] <= 185) || loc[0] == 206 || loc[0] == 217) {
                                myPrintf("\nYou hear a muffled explosion in the distance.\n")
                            } else {
                                myPrintf("\nYou hear a faint rumble in the distance.\n")
                            }
                            if (flag_clock == R_blast_point) {
                                room[R_store_room][3] = R_geo_w
                                room[R_geo_w][0] = R_store_room
                                room[R_geo_w][M_rm_type] = T_long_descp
                            }
                            // destroy objects in the room
                            for (obj in objectList) {
                                if (obj.location == flag_clock) {
                                    obj.location = B_destroyed
                                }
                            }
                        }
                    }
                }

                if (tag[V_MOVE]) {
                    if (mover(verb, loc)) {
                        sw_loop = true
                    }
                } else {
                    // Verb switch
                    when (verb) {
                        V_help -> help()
                        V_info -> {
                            myPrintf("Dinkum adventure game version: Mk 2.1  (c) Copyright 1993\n")
                            myPrintf("by Gary A. Allen, Jr.  All rights reserved.\n")
                        }
                        V_quit -> ender(F_quit)
                        V_QUESTION -> {
                            if (tag[V_VERB_ONLY]) {
                                myPrintf("Try to be more specific. What do you want to know?\n")
                            } else {
                                cheater()
                            }
                        }
                        V_inventory, V_invent -> inventer()
                        V_close, V_shut -> closer(loc[0])
                        V_open, V_swing -> opener(loc[0])
                        V_dial, V_combination -> dialer(loc[0])
                        V_drink -> drinker()
                        V_ignite, V_light -> igniter(loc[0])
                        V_read -> reader(loc[0])
                        V_push, V_press -> {
                            pusher(loc)
                            if (loc[0] != loc[1]) {
                                sw_loop = true
                            }
                        }
                        V_switch, V_turn, V_select, V_set -> switcher(loc[0])
                        V_shoot, V_fire, V_kill -> killer(loc[0])
                        V_unlock -> unlocker(loc[0])
                        V_lock -> locker(loc[0])
                        V_eat, V_swallow -> eater()
                        V_fill -> filler()
                        V_sound, V_bang, V_hit, V_strike -> sounder(loc[0])
                        V_svc -> {
                            if (sw_wizard) {
                                sw_standard = !sw_standard
                                myPrintf("sw_standard set to:  %s\n", sw_standard.toString())
                            } else {
                                myPrintf("SVC is not available.  You're not a wizard!\n")
                            }
                        }
                        V_stats -> {
                            if (sw_wizard) {
                                myPrintf("\nGame Statistics:\n")
                                myPrintf("    Time:     %d seconds\n", System.currentTimeMillis() / 1000L - start_time)
                                myPrintf("    Score:    %d points\n", score)
                                myPrintf("    Carry:    %d items, weighing %d decigrams\n", carry_count, carry_weight)
                                myPrintf("    Location: Room %d\n", loc[0])
                                myPrintf("    Ned:      Room %d\n", Ned.location)
                            } else {
                                myPrintf("You'll have to find another way of doing that!\n")
                            }
                        }
                        V_su -> {
                            myPrintf("Password: ")
                            val passLine = inputQueue.take().trim()
                            if (passLine == "dinkum") {
                                sw_wizard = true
                                myPrintf("Wizard mode active.\n")
                            } else {
                                myPrintf("Access denied.\n")
                            }
                        }
                        V_jsys -> {
                            if (sw_wizard) {
                                myPrintf("Enter room destination code: ")
                                try {
                                    val rDest = inputQueue.take().trim().toInt()
                                    if (rDest in 0..248) {
                                        loc[0] = rDest
                                        sw_loop = true
                                    } else {
                                        myPrintf("Invalid room code.\n")
                                    }
                                } catch (e: Exception) {
                                    myPrintf("Invalid input.\n")
                                }
                            } else {
                                myPrintf("If there was a verb in that sentence, I didn't understand it.\n")
                            }
                        }
                        V_score -> {
                            newScore()
                            myPrintf("Your current score is %d points out of %d.\n", score, max_score)
                        }
                        V_enter -> {
                            myPrintf("I need a direction. Where do you want to enter?\n")
                        }
                        V_exit, V_leave, V_depart -> {
                            myPrintf("In what direction? Which way?\n")
                        }
                        V_dig -> {
                            myPrintf("I don't think you can find anything by digging here.\n")
                        }
                        V_stand -> {
                            myPrintf("You're already standing up!\n")
                        }
                        V_load -> loader()
                        V_unload -> unloader(loc[0])
                        V_pull -> {
                            myPrintf("I don't think pulling anything here will help us!\n")
                        }
                        V_climb -> {
                            if (tag[V_lift]) {
                                myPrintf("You can enter the lift or push its buttons.\n")
                            } else if (tag[V_mat]) {
                                myPrintf("You're standing on the doormat already.\n")
                            } else {
                                myPrintf("Try using a direction command:  UP or DOWN.\n")
                            }
                        }
                        V_swim -> {
                            if (loc[0] == R_stream || loc[0] == R_billabong || loc[0] == R_river_edge) {
                                myPrintf("You can swim in the stream or billabong, but the river is too dangerous!\n")
                            } else {
                                myPrintf("There's no water here for me to swim in!\n")
                            }
                        }
                        V_back -> {
                            if (loc[0] == loc[2]) {
                                myPrintf("You haven't moved yet!\n")
                            } else {
                                val tmp = loc[0]
                                loc[0] = loc[2]
                                loc[2] = tmp
                                sw_loop = true
                            }
                        }
                        V_take, V_get, V_lift, V_pick, V_grab, V_remove, V_attach, V_hold -> taker(loc[0])
                        V_drop, V_throw, V_put, V_fling, V_toss, V_insert, V_give, V_eject, V_slow_drop -> dropper(loc[0])
                        V_look, V_view, V_examine, V_inspect, V_describe -> looker(loc[0])
                        else -> {
                            myPrintf("Huh?  You will have to make yourself clearer! \n")
                        }
                    }
                }

                if (sw_loop) break
            }

            loc[2] = loc[0]
        }
        } catch (e: GameEndedException) {
            myPrintf("\nWould you like to play again? ")
            if (yesNo()) {
                myPrintf("\nDinkum adventure will restart in 2 seconds...\n")
                delay(2000)
                inputQueue.clear()
                start()
            } else {
                myPrintf("\nGood bye!\n")
                inputQueue.clear()
            }
        }
    }

    private fun help() {
        myPrintf("There is no help available.  You're on your own!\n")
    }

    private fun cheater() {
        if (tag[V_what] && tag[V_am]) {
            myPrintf("You are a treasure hunter looking for gold!\n")
            return
        }
        if (tag[V_what] && tag[V_have]) {
            inventer()
            return
        }
        if (tag[V_where] && tag[V_am]) {
            myPrintf("Look at the room description. It tells you where you are.\n")
            return
        }
        myPrintf("I don't know the answer to that question.\n")
    }

    private suspend fun mover(direction: Int, loc: IntArray): Boolean {
        loc[2] = loc[0]
        val next_room = room[loc[0]][direction - 1]

        if (next_room == R_WALL) {
            myPrintf("You can't go in that direction!\n")
            return false
        }

        if (next_room == R_river) {
            loc[0] = R_river
            long_descp(R_river)
            ender(F_died)
            return false
        }

        if (next_room == R_bunyip) {
            loc[0] = R_bunyip
            long_descp(R_bunyip)
            ender(F_died)
            return false
        }

        if (next_room == R_taipan) {
            loc[0] = R_taipan
            long_descp(R_taipan)
            ender(F_died)
            return false
        }

        if (next_room == R_road_kill_n || next_room == R_road_kill_s) {
            loc[0] = next_room
            long_descp(next_room)
            ender(F_died)
            return false
        }

        loc[0] = next_room
        return true
    }

    private suspend fun describe(n: Int) {
        if (((Torch.location == B_have) && Torch.status != 0) ||
            (n < 186) || (n > 202)) {
            if (n == R_office_mang) {
                if (!sw_active && Rifle.status != 0 &&
                    Ned.location == B_destroyed && !describe_sw_hint) {
                    describe_sw_hint = true
                    myPrintf("For half of the bullets in your M-16 rifle I will\n")
                    myPrintf("give you a hint on how to get into the safe.\n")
                    myPrintf("             Are you interested? \n")

                    while (true) {
                        myPrintf("(Answer:  Yes or No) ")
                        val chr = myGetchar()
                        if (chr == 'n' || chr == 'N' || chr == 'y' || chr == 'Y') {
                            if (chr == 'y' || chr == 'Y') {
                                Rifle.status /= 2
                                myPrintf("\nTake the missing word in the sentence:\n")
                                myPrintf("                      \"The chook **** an egg.\"\n")
                                myPrintf("and spell it backwards.  Then look very carefully at the\n")
                                myPrintf("map which you found in the manager's office.  Also, you\n")
                                myPrintf("now have %d bullets in your rifle.\n", Rifle.status)
                            }
                            break
                        }
                    }
                    myPrintf("\n")
                }
            }
            myPrintf("You %s \n", descript[room[n][M_descp]])
            describe_old_n = n
            describe_dark_count = 0
        } else {
            if (n == 186) {
                myPrintf("You have entered a north/south passage which does not have\n")
                myPrintf("electric lighting.  There is some light coming in from the\n")
                myPrintf("north, but the tunnel to the south is completely dark.\n")
                return
            }
            if (n == 187) {
                myPrintf("The tunnel you are in is pitch black.  There is a little\n")
                myPrintf("light coming in from the north.  If you keep going you will\n")
                myPrintf("probably fall down a hole.\n")
                return
            }
            if (n in 188..202) {
                if (describe_dark_count == 0 || describe_old_n == n) {
                    myPrintf("You are in a mine tunnel in total darkness, which is \n")
                    myPrintf("extremely dangerous.  I have no clue where we are going.\n")
                    myPrintf("Let's go back to where there is some light!\n")
                    describe_dark_count++
                    describe_old_n = n
                } else {
                    myPrintf("Twit!!  You have fallen down a hole and broken your neck!\n")
                    ender(F_died)
                }
                return
            }
        }
    }

    private suspend fun rdtxt(k: Int) {
        when (k) {
            V_map_frag -> {
                myPrintf("+----------------------------------------------------- \n")
                myPrintf("|..................................................... \n")
                myPrintf("|...+-----------------------+......................... \n")
                myPrintf("|...|  Current Operational  |........................ \n")
                myPrintf("|...|  Levels are:          |......D--W--W--W--W--W- \n")
                myPrintf("|...|     0, 49, 67, 82     |......|................ \n")
                myPrintf("|...| Level #67 is depicted |......S................ \n")
                myPrintf("|...+-----------------------+......|................ \n")
                myPrintf("|..................................S............... \n")
                myPrintf("|..................................|............... \n")
                myPrintf("|......................S--W--W--D--W.............. \n")
                myPrintf("|......................|........|................. \n")
                myPrintf("|......................S........E--E--E--E--E--E- \n")
                myPrintf("|......................|.......................... \n")
                myPrintf("|.......[dead end]--W--*--E--[dead end].......... \n")
                myPrintf("|......................|........................ \n")
                myPrintf("|......................S......................")
                myPrintf("       The rest of the map\n")
                myPrintf("|......................|.....................")
                myPrintf("        has turned into dust.\n")
                myPrintf("|..+--->..[false dead end (partition)]......\n")
                myPrintf("|..|...................|................. \n")
                myPrintf("|.{use an explosive}...S.............. \n")
                myPrintf("|......................|.... \n")
                myPrintf("|.............. \n")
            }
            V_can -> {
                myPrintf("You see an aluminum beer can with a hole in the bottom. The\n")
                myPrintf("can is coloured yellow-orange.  Written in red letters\n")
                myPrintf("across the can't front is the following:\n\n")
                myPrintf("                  \"CASTLEMAINE\"\n")
                myPrintf("                      XXXX\n")
                myPrintf("                   BITTER ALE\n\n")
                myPrintf("There's a picture of Fourex's Milton brewery in the middle.\n")
            }
            V_pills -> {
                myPrintf("You're holding a packet which once contained 30 pills but now\n")
                myPrintf("holds only %d pills.  On the back of the packet is written:\n\n", pill_count)
                myPrintf("                       ATROPINE PILLS\n")
                myPrintf("The pills in this packet will protect the user against poisoning\n")
                myPrintf("by organo-phosphorous compounds, i.e. VX nerve gas.\n")
                myPrintf("    Dosage:  Take ONE pill when poisoning symptons occur.\n")
                myPrintf("    Warning:  Atropine is itself a poison.  An overdose can\n")
                myPrintf("              be lethal!\n\n")
            }
            V_letter -> {
                myPrintf("+---------------------------------------------------------+\n")
                myPrintf("|                                               +-----+   |\n")
                myPrintf("| Col. M. Gaddafi                               |Libya|   |\n")
                myPrintf("| Azizya Barracks                Postage Due    |5 zl.|   |\n")
                myPrintf("| Tripoli, Libya                   $1.50        | -o- |   |\n")
                myPrintf("|                                               +-----+   |\n")
                myPrintf("|                 Sam Cohen                               |\n")
                myPrintf("|                 Level #67 Shift Boss                    |\n")
                myPrintf("|                 ACME Mine Ltd.                          |\n")
                myPrintf("|                 Birdsville, Queensland 4482             |\n")
                myPrintf("|  AIR MAIL                   Australia                   |\n")
                myPrintf("|                                                         |\n")
                myPrintf("+---------------------------------------------------------+\n")
                myPrintf("\n\nThe envelope must be opened before the letter can be read.\n\n")
            }
            V_paper -> {
                myPrintf("                             Congratulations!!! \n")
                myPrintf("You got the safe open!  Now you can start scoring points for treasure.\n")
                myPrintf("Credit is awarded **only** for treasure put into the safe. Beware of fell\n")
                myPrintf("bush rangers who steal treasure (and sometimes kill adventurers).\n\n")
                myPrintf("                        ---  Concerning Gleeps ---\n")
                myPrintf("Gleeps are potent adjuncts which are credited separately from treasure.  Gleeps\n")
                myPrintf("will reproduce if left undisturbed in special locations underground. However\n")
                myPrintf("in most locations they will remain dormant. Credit for gleeps is awarded \n")
                myPrintf("only for those placed in a \"gleep tank\". While anyone can gather treasure, the\n")
                myPrintf("ability to cultivate and collect gleeps is the mark of a fair dinkum master.\n")
                myPrintf("                               Have Fun !!\n")
            }
            V_rifle -> {
                myPrintf("--- You are looking at a Colt M16 infantry rifle. --- \n")
                myPrintf("This fully automatic weapon is supplied bullets from a \n")
                myPrintf("detachable magazine which can hold up to 200 rounds. \n")
                myPrintf("The rifle has a selector knob that can place the weapon \n")
                myPrintf("into one of four possible firing modes: \n\n")
                myPrintf("     SAFE = Rifle can't fire, even if dropped \n")
                myPrintf("     I    = Single fire mode. Shoots one bullet at a time \n")
                myPrintf("     III  = Triple fire mode. Shoots three bullets rapidly \n")
                myPrintf("            every time the trigger is pulled. \n")
                myPrintf("     AUTO = Fully automatic, firing bullets at a rate of \n")
                myPrintf("            660 rounds/min. when trigger is pulled. \n\n")
                if (rifle_flag == F_safety)
                    myPrintf("The rifle is currently set in the SAFE mode. \n")
                if (rifle_flag == F_single)
                    myPrintf("The rifle is currently set in the \"I\" or single fire mode.\n")
                if (rifle_flag == F_triple)
                    myPrintf("The rifle is now set in the \"III\" or triple fire mode.\n")
                if (rifle_flag == F_auto)
                    myPrintf("The rifle is currently set in the AUTO mode. \n")
                if (clip_flag == F_no_clip)
                    myPrintf("The rifle does not have an ammunition clip attached to it.\n")
                if (clip_flag == F_normal_clip) {
                    myPrintf("The rifle has a standard ammunition clip attached which contains %d rounds.\n", Rifle.status)
                }
                if (clip_flag == F_org_clip) {
                    myPrintf("The rifle has an orange ammunition clip attached which contains %d rounds.\n", Rifle.status)
                }
            }
            V_safe -> {
                if (Picture.status >= S_open) {
                    myPrintf("You see a conventional combination dial type wall safe \n")
                    myPrintf("fixed immovably into the wall.  The dial is black with \n")
                    myPrintf("white numbers written onto it.  The numbers range from \n")
                    myPrintf("one to one hundred. Attached to the upper right hand \n")
                    myPrintf("corner of the safe is a manufacturer's label which reads:\n\n")
                    myPrintf("                    +-----------------------------+ \n")
                    myPrintf("                    |     Kryptonite Safe Co.     | \n")
                    myPrintf("                    |  Model Number:  C-3283      | \n")
                    myPrintf("                    |  Serial Number: 10149167182 | \n")
                    myPrintf("                    |      Made in Australia      | \n")
                    myPrintf("                    +-----------------------------+ \n")
                } else myPrintf("I don't see a safe here for me to describe.\n")
            }
            V_wall, V_message -> {
                myPrintf("You've read this message before.  It says:\n\n")
                myPrintf("Dear Treasure Hunter:\n")
                myPrintf("There was lots of treasure on this level when I first\n")
                myPrintf("discovered this mine.  However I have since carted it all\n")
                myPrintf("away.  You might try looking on another level.")
                myPrintf("\n\n                            Good Luck!\n")
                myPrintf("                         Dr. I. Jones, Ph.D.\n\n")
            }
            V_recorder -> {
                myPrintf("  --- You are holding a Fair Dinkum data recorder. ---\n")
                myPrintf("This device is a metal box with four coloured buttons.  The\n")
                myPrintf("buttons have the following functions:\n\n")
                myPrintf("Green Button:\n")
                myPrintf("When the green button is pressed the recorder will query the user\n")
                myPrintf("for a data file name.  Once the name has been provided, every\n")
                myPrintf("move made after will be recorded in the named file.  The recording\n")
                myPrintf("session will end when the red button is pressed.\n\n")
                myPrintf("White Button:\n")
                myPrintf("When the white button is pressed the recorder will query the user\n")
                myPrintf("for a data file name.  Once the name for an existing data file has\n")
                myPrintf("been provided, then all the moves listed in that file will be\n")
                myPrintf("played back with the player acting accordingly.\n\n")
                myPrintf("Red Button:\n")
                myPrintf("When the red button is pressed a recording or play back session\n")
                myPrintf("is ended.  Otherwise nothing happens.\n\n")
                hold_it()
                myPrintf("Orange Button:\n")
                myPrintf("When the orange button is pressed then play is suspended and\n")
                myPrintf("Dinkum's clock stopped.  You are then asked if you want to\n")
                myPrintf("start the clock running again.\n\n")
                myPrintf("                          -------------\n\n")
                myPrintf("Warning:  Things Change!  A recorder file which worked for one\n")
                myPrintf("situation could become lethal for another.\n")
            }
            V_warning -> {
                myPrintf("+---------------------------------------------------------+\n")
                myPrintf("|                                                         |\n")
                myPrintf("|              ACME MINE IS UNFAIR TO WORKERS!!           |\n")
                myPrintf("|                                                         |\n")
                myPrintf("|                       ON STRIKE!!                       |\n");
                myPrintf("|                                                         |\n")
                myPrintf("| The Loyal Brotherhood of Australian Miners and Under-   |\n")
                myPrintf("| ground Workers is on strike against Acme Mines Ltd. due |\n")
                myPrintf("| to unfair treatment of its workers.  Our Union demands  |\n")
                myPrintf("| the following:  Worker's wages be increased by 30%%, the |\n")
                myPrintf("| week shall have only 4 working days, the working day    |\n")
                myPrintf("| shall be reduced to 5 hours, triple wages for overtime  |\n")
                myPrintf("| work and immunity from sacking for all Union members.   |\n")
                myPrintf("| To discourage scabs and other strike breakers from      |\n")
                myPrintf("| working during the strike, we have planted booby traps  |\n")
                myPrintf("| through out the tunnels on this level.  We do hope you  |\n")
                myPrintf("| don't mind the mine being mined so it won't be mined.   |\n")
                myPrintf("+---------------------------------------------------------+\n")
            }
            V_detector -> {
                myPrintf("You see an olive green box with a yellow button in its\n")
                myPrintf("centre, a yellow light in its upper right hand corner and\n")
                myPrintf("Cryllic writing all over it.  Below the Cryllic letters\n")
                myPrintf("is a translation in English which reads:\n\n")
                myPrintf("              Mk 5.12 Semtex Explosive Detector\n")
                myPrintf("This device is used in detecting explosive weapons such as\n")
                myPrintf("antipersonal mines which utilize the high velocity plastic\n")
                myPrintf("explosive Semtex.  To operate this device merely press the\n")
                myPrintf("yellow button and an air sample will be taken. If trace\n")
                myPrintf("amounts of Semtex are detected then a warning beeper will\n")
                myPrintf("be activated.   If there is no detectable Semtex then a\n")
                myPrintf("yellow light will flash.\n\n")
                myPrintf("               Made in the Czeckoslovak Socialist Republic\n")
            }
            V_cube -> {
                myPrintf("You're holding a cube made of dull black metal which is about\n")
                myPrintf("6 cm across each face.  There is a strange alien writing much\n")
                myPrintf("like Sanskrit stamped into the metal.  On one cube face is a\n")
                myPrintf("large iridescent blue push button projecting out from the surface.\n")
            }
            V_clip -> {
                myPrintf("You see a conventional M16 ammunition clip.  It is olive green\n")
                myPrintf("in colour and about 30 centimetres long.\n")
                if (Clip.status == 0)
                    myPrintf("\nThe clip is EMPTY with NO bullets.\n")
                else {
                    if (Clip.status == 1)
                        myPrintf("\nThe clip contains one bullet.\n")
                    else
                        myPrintf("\nThe clip contains %d bullets.\n", Clip.status)
                }
            }
            V_gong -> {
                myPrintf("You see an enormous silver gong weighing at least a ton.  The\n")
                myPrintf("huge gong is suspended from a finely polished red cedar frame,\n")
                myPrintf("the top of which is about four metres above the floor.\n")
            }
            V_org_clip -> {
                myPrintf("You see an M16 ammunition clip which has been painted bright orange.\n")
                myPrintf("A strange blue glow seems to be radiating from the clip. There is\n")
                myPrintf("a placard attached to the side of the clip which reads:\n\n")
                myPrintf("    +----------------------------------------------------+\n")
                myPrintf("    |    Hypertoxic 0.223 Caliber Ammunition Magazine    |\n")
                myPrintf("    |                                                    |\n")
                myPrintf("    | The ammunition contained in this magazine is       |\n")
                myPrintf("    | manufactured from spent nuclear fuel rods composed |\n")
                myPrintf("    | of uranium and plutonium metal.  The bullets have  |\n")
                myPrintf("    | been hollowed out and filled with mercury metal.   |\n")
                myPrintf("    | The exterior of each bullet has been striated and  |\n")
                myPrintf("    | impregnated with VX nerve gas and anthrax bacillus.|\n")
                myPrintf("    | The use of this ammunition violates the Geneva     |\n")
                myPrintf("    | Convention on the Rules of War, the International  |\n")
                myPrintf("    | Moratorium on Biological Weapons, and the United   |\n")
                myPrintf("    | Nations Charter.  Close proximity to this          |\n")
                myPrintf("    | ammunition can be harmful to your health.          |\n")
                myPrintf("    |                                                    |\n")
                myPrintf("    |    Developed under DOE Contract: LLNL 89-5632/Z    |\n")
                myPrintf("    |                  Patent Pending                    |\n")
                myPrintf("    +----------------------------------------------------+\n")
                if (Org_clip.status == 0)
                    myPrintf("\nThe orange clip is EMPTY with NO bullets.\n")
                else {
                    if (Org_clip.status == 1)
                        myPrintf("\nThe orange clip contains one bullet.\n")
                    else
                        myPrintf("\nThe orange clip contains %d bullets.\n", Org_clip.status)
                }
            }
        }
    }

    private suspend fun long_descp(n: Int) {
        if (((Torch.location == B_have) && Torch.status != 0) ||
            (n < 186) || (n > 202)) {

            if (n in 218..227) {
                myPrintf("As you're walking down the passage you feel the slight\n")
                myPrintf("tug of a trip wire on your leg.  Out the corner of your\n")
                myPrintf("eye you see the trip wire is connected to an olive green\n")
                myPrintf("cylinder attached to the wall of the tunnel....\n\n")
                boom()
                myPrintf("\nIn case you're wondering that was a booby trap which you\n")
                myPrintf("set off!\n")
                ender(F_died)
                return
            }

            when (n) {
                R_river -> {
                    myPrintf("As you approach the edge of the dike, there is a small\n")
                    myPrintf("earth slide which pitches you into the river.  You\n")
                    myPrintf("flounder around in the water for a short while.  Luckily\n")
                    myPrintf("before you have the chance to drown, a crocodile pulls\n")
                    myPrintf("you under, bringing you to a rather quick though painful\n")
                    myPrintf("end. \n")
                    return
                }
                R_bunyip -> {
                    myPrintf("The slimey tentacle suddenly tightens around your leg, and\n")
                    myPrintf("the water in the billabong starts to churn furiously. Then\n")
                    myPrintf("surfaces the one thing that all Australians fear the most...\n\n")
                    myPrintf("Before you stands an enormous sickly green Queensland\n")
                    myPrintf("Bunyip in all its awesome horror. It does not make a quick\n")
                    myPrintf("end of you....  Such is not the way of Bunyips.  First it \n")
                    myPrintf("peels off your skin as if you were a banana.  After much\n")
                    myPrintf("fearful agony, comes release as you pass into death.\n")
                    return
                }
                R_road_kill_n, R_road_kill_s -> {
                    myPrintf("The distant rumble becomes a loud roar.  Suddenly a huge Mack\n")
                    myPrintf("truck pulling three carriages packed with cattle leaps over the\n")
                    myPrintf("low rise, filling the narrow country road.  You stand terrified\n")
                    myPrintf("in the middle of the road with the metal monster bearing down\n")
                    myPrintf("at 120 km/hr.  The last thing you see before being flattened\n")
                    myPrintf("like a cockroach is a sign on the truck's roo bar saying:\n")
                    myPrintf("                 \"CAUTION:  ROAD TRAIN\"\n\n")
                    return
                }
                R_forest -> {
                    room[n][M_rm_type] = T_was_long
                    myPrintf("You are in a forest thickly wooded with gum trees.  The forest\n")
                    myPrintf("floor is covered with long thin leaves from gum trees mixed\n")
                    myPrintf("with their seed pods.  There is a strong eucalytus smell in the\n")
                    myPrintf("air.  Up in the forest canopy you can hear kookaburras engaged\n")
                    myPrintf("in their raucous cackling.\n")
                    return
                }
                R_bend -> {
                    room[n][M_rm_type] = T_was_long
                    myPrintf("You are north of the grassy area in the bend of a great meandering\n")
                    myPrintf("river.  All around you are the tall black fronds of grass trees.\n")
                    if (longdsc_sw_bend) {
                        longdsc_sw_bend = false
                        myPrintf("Off in the distance you can see a mob of emus striding out of\n")
                        myPrintf("sight.\n")
                    }
                    return
                }
                R_bitumen -> {
                    room[n][M_rm_type] = T_was_long
                    myPrintf("You are on a narrow country road made out of potholed bitumen.\n")
                    myPrintf("The road's shoulders are of soft red earth which could cause\n")
                    myPrintf("a driver to lose control of his car if he took a wheel off the\n")
                    myPrintf("bitumen.  ")
                    if (longdsc_sw_mob) {
                        longdsc_sw_mob = false
                        myPrintf("A few metres away is a mob of grey kangroos grazing\n")
                        myPrintf("on grass by the road.  A huge buck looks up at you and doesn't\n")
                        myPrintf("like what he sees.  He bounds off with the rest of the mob\n")
                        myPrintf("following.  You can see the heads of joeys protruding from\n")
                        myPrintf("their mother's pouches as the mob leaps off into the bush and\n")
                        myPrintf("out of sight.\n")
                    } else myPrintf("\n")
                    return
                }
                R_stream -> {
                    room[n][M_rm_type] = T_was_long
                    myPrintf("You approach a billabong with a small stream flowing into it.\n")
                    myPrintf("Cane toads can be heard croaking nearby and you see countless\n")
                    myPrintf("numbers of their tadpoles swimming in the stream.\n")
                    return
                }
                R_taipan -> {
                    myPrintf("As you wander aimlessly in the grass, you accidently step\n")
                    myPrintf("upon a taipan which is perhaps the most poisonous of Oz's \n")
                    myPrintf("snakes. The snake strikes upwards and bites you in the\n")
                    myPrintf("worst possible place.  You thrash around for a short while\n")
                    myPrintf("and then the venom takes hold. Your body begins to decompose\n")
                    myPrintf("before your very eyes.  Death comes only after an hour \n")
                    myPrintf("of the worst possible agony.  \n")
                    return
                }
                R_mine_head -> {
                    room[n][M_rm_type] = T_was_long
                    myPrintf("East of you is a large iron open truss tower, topped with \n")
                    myPrintf("two rusting wheels holding a pair of thick steel cables.  \n")
                    myPrintf("This is the head gear of the once highly successful ACME \n")
                    myPrintf("Gold Mine, which now is completely mined out.  Where \n")
                    myPrintf("there were once rich veins of gold is now a honeycomb of \n")
                    myPrintf("tunnels and caverns.  There are stories about strange \n")
                    myPrintf("events in the old tunnels of the ACME mine and only fools \n")
                    myPrintf("or reckless adventurers would dare explore them. \n")
                    myPrintf("To the north is the old office building.  Rumor has it \n")
                    myPrintf("the safe in this building once had millions of dollars in \n")
                    myPrintf("gold processed from the rich ore of the ACME mine.  To \n")
                    myPrintf("the south is the mine lift, which once transported up to \n")
                    myPrintf("twenty miners at a time down the long mine shaft to the \n")
                    myPrintf("working tunnels deep under the earth below.\n")
                    return
                }
                R_office_mang -> {
                    room[n][M_rm_type] = T_short_descp
                    myPrintf("You are in the Manager's office of the ACME Mine.  In \n")
                    myPrintf("former times this room saw many lively meetings between \n")
                    myPrintf("the Site Manager and Shift Bosses.  Fixed to the wall are\n")
                    myPrintf("old production schedules and organizational charts. In\n")
                    myPrintf("the middle of the room is a beautiful silky oak desk.\n")
                    myPrintf("Unfortunately it is too heavy to move.  Attached to\n")
                    myPrintf("the far wall is a curious picture of a platypus wearing a \n")
                    myPrintf("hat with old wine corks dangling from the hat's brim. The\n")
                    myPrintf("only exit is to the south.\n\n")
                    return
                }
                R_geo_w -> {
                    room[n][M_rm_type] = T_short_descp
                    myPrintf("As you entered this room, huge cockroaches scuttled off in every \n")
                    myPrintf("direction. On the far wall is a rather tasteless poster depicting \n")
                    myPrintf("an extremely well endowed (unclad) young woman holding a mining \n")
                    myPrintf("drill.  This office was once occupied by the mine geologist.  One \n")
                    myPrintf("can still see traces of the office's former occupant.  There are only \n")
                    myPrintf("two doors to this room.  Both go to the north. You entered through \n")
                    myPrintf("the west door.  However there is another door in the eastern section \nof the office. \n")
                    return
                }
                R_lift_inside -> {
                    room[n][M_rm_type] = T_was_long
                    myPrintf("You are inside the lift, which has only one entrance doorway to the west. \n")
                    myPrintf("To the left of the door is a panel of buttons.  Most of the buttons are \ninoperative with tape over them.  However there are four buttons \nthat still work. Each of the four buttons have a number stamped on them.  \nThe numbers are zero, forty-nine, sixty-seven, and eighty-two. \n \n")
                    return
                }
                R_hideout_entr -> {
                    room[n][M_rm_type] = T_was_long
                    if (monster_flag == F_no_monster) {
                        myPrintf("Before you is a Queensland house with large verandas. The front of\nthe house is to the east. The house is painted fluorescent orange.\nOn the roof of the house is a large, bright flashing neon sign which reads: \n\n")
                        myPrintf("+---------------------------------------------------------+\n")
                        myPrintf("|                                                         |\n")
                        myPrintf("|            Ned Kelly's ---SECRET--- Hide Out            |\n")
                        myPrintf("| Policemen, troopers, law enforcement officers and all   |\n")
                        myPrintf("| other minions of the law and public decency are advised |\n")
                        myPrintf("| to look else where for bush rangers, thieves, and other |\n")
                        myPrintf("| larrikins, since ***only*** honest, law abiding         |\n")
                        myPrintf("| Australians (loyal to Queen and Country) live here.     |\n")
                        myPrintf("|                                                         |\n")
                        myPrintf("+---------------------------------------------------------+\n")
                    } else describe(n)
                    return
                }
                R_foreman -> {
                    room[n][M_rm_type] = T_was_long
                    myPrintf("You have entered the office of the Level #67 Foreman. In the middle of the\nroom is the Foreman's large and immovable desk.  To one side can be seen an\nempty file cabinet and a series of pidgeon holes for holding mine worker's\nmail. The only exit is to the west.\n\n")
                    return
                }
                R_no_treasure -> {
                    room[n][M_rm_type] = T_was_long
                    myPrintf("You are in a tunnel going east and west. Crude letters have\nbeen scratched into the wall of the tunnel.  The following\nwas written:\n\n")
                    myPrintf("Dear Treasure Hunter:\n")
                    myPrintf("There was lots of treasure on this level when I first\n")
                    myPrintf("discovered this mine.  However I have since carted it all\n")
                    myPrintf("away.  You might try looking on another level.")
                    myPrintf("\n\n                            Good Luck!\n")
                    myPrintf("                         Dr. I. Jones, Ph.D.\n\n")
                    return
                }
                R_gleep_tank -> {
                    room[n][M_rm_type] = T_was_long
                    myPrintf("You have walked into a room with a large plastic tank set in the middle.\nThe tank is about two metres deep and full of dark blue liquid which\nsmells strongly of chlorine. The words \"Gleep Tank\" have been crudely\nspray painted on the wall.\n")
                    return
                }
                R_ufo_e -> {
                    room[n][M_rm_type] = T_was_long
                    myPrintf("You crawl under a long thin sting that projects from the front of the\nspacecraft.  It's made from a different metal (stainless steel?), and appears\nto have been retractable. This was probably a Mach probe for measuring flight\nspeed. You can go north or south.\n")
                    return
                }
                R_ufo_w -> {
                    room[n][M_rm_type] = T_was_long
                    myPrintf("Before you is perhaps the real reason why the ACME Mine was closed. You see\nthe bent and twisted remains of what could only be an ancient spacecraft. It\nsits embedded within a gold bearing reef of conglomerate stone from the\nPrecambrain Period (hundreds of millions of years old).  The old spacecraft\nwas based on a delta wing design which the miners had chiped free from\nthe rock. You can see clearly the interior of the engine exhaust ducts.  The\nvehicle has air inlets so it must have been air breathing when it flew in\nthe atmosphere. The edges of the ducts appear to be made of titanium and are\nabout six centimetres thick. No wonder it could survive millions of years\nunder tons of stone. This spacecraft is literally built like a battleship!\nYou can climb over the vehicle to the north or go south.\n\n")
                    return
                }
                R_ufo_n -> {
                    room[n][M_rm_type] = T_was_long
                    myPrintf("You're now doing a tight squeeze between the tunnel wall and the leading\nedge of the wing.  You can see the titanium surface is covered with many\nregularly placed little holes, each about half a millimetre in diameter.\nThis was probably part of the wing's cooling system for atmospheric entry\nfrom orbit. You can go south-east or south-west.\n\n")
                    return
                }
                R_ufo_s -> {
                    room[n][M_rm_type] = T_was_long
                    myPrintf("You're crawling under the wing and next to the spacecraft's landing gear.\nThe landing gear strut is almost snapped off.  The tyre has long ago turned\nto dust, but the tyre rim is still in place.  The braking system used is\nquite odd.  The brake rotor is basicly a ring being gripped by a caliper with\nconcave brake pads.  You can go north-west or north-east.\n")
                    return
                }
                R_ufo_ne -> {
                    room[n][M_rm_type] = T_was_long
                    myPrintf("You are now near the nose of the derelict spacecraft.  You see a quartz\nwindow in the side of the vehicle.  It has been so scratched by the\ncenturies that it is now opaque.  You can go north-west or south.\n")
                    return
                }
                R_ufo_nw -> {
                    room[n][M_rm_type] = T_was_long
                    myPrintf("As you scrambled over the old derelict, you passed under the vertical\nstabilizer which centuries of geological forces had bent into what\nlooks like a sagging shark's fin. You can go south or do a tight squeeze\nto the north-east.\n\n")
                    return
                }
                R_ufo_se -> {
                    room[n][M_rm_type] = T_was_long
                    myPrintf("You are under the forward section of the spacecraft. Above you is an\nopened access hatch which leads into the dark interior of the derelict.\nYou can go north, up or crawl under the wing to the south-west\n")
                    return
                }
                R_ufo_sw -> {
                    room[n][M_rm_type] = T_was_long
                    myPrintf("You are hunching over as you scramble underneath the spacecraft's wing.\nNear the trailing edge of the wing you can see what is left of the\nwing flaps.  The flap was actuated by a very conventional hydraulic system.\nSome of the metal tubing for the actuaters are still in place, though\nthe hydraulic fluid has long since fossilized into stone. You can\ngo north or south-east.\n")
                    return
                }
                R_air_lock -> {
                    room[n][M_rm_type] = T_was_long
                    myPrintf("You are inside what is obviously an airlock.  There are two hatchways\ninto this airlock.  One leads to the exterior while the other goes into\nthe spacecraft's interior.  Both hatches appear to have originally been\nleft open and not forced by the miners (the hinges are immovable). It\nappears that the ship's crew left in a hurry. You can go north or down.\n")
                    return
                }
                R_ship_passage -> {
                    room[n][M_rm_type] = T_was_long
                    myPrintf("You've entered the intercompartment access passage.  To the south is an\nopen air tight door leading to the airlock. The access door to the east\nis open and recessed into the wall. The access door to the west has been\nforced open with a cutting torch and folded back with a pneumatic\nhammer (obviously the work of the miners). The passage itself has about\nten centimetres of silt on the bottom and has the appearance of ancient\ndilapidation.\n")
                    return
                }
                R_flight_deck -> {
                    room[n][M_rm_type] = T_was_long
                    myPrintf("You have made it to the flight deck. Unfortunately it's not much to\nwrite home about. Since the access door was open, this area has been full\nof water and silt for millennia.  The miners have shoveled out most of it.\nOnce the wall panels were covered with some kind of plastic which has long\nsince turned to dust.  There are three corroded frames which were once crew\nseats. The beings that flew this machine were taller and wider than humans\nbut they had anthropoid form. The flight controls are still here but they\nare so heavily corroded that they can no longer be moved.  The only way out\nis to the west.\n\n")
                    return
                }
                R_panel -> {
                    room[n][M_rm_type] = T_was_long
                    myPrintf("You are in a compartment that seems brand new, which is unbelievable.  Perhaps\nthe machinery in this ship was once self repairing.  Before you is a control\npanel with CRT type displays and buttons that still work.  Beneath each button\nis a description written in a strange alien script vaguely like Sanskrit.\nI wouldn't dare press any button except for a gray button.  Beneath\nthis button is some masking tape that was obviously placed there by the miners.\nSomeone has written \"TEHR.\" on the masking tape with a pencil.\n\n")
                    return
                }
                R_prayer -> {
                    room[n][M_rm_type] = T_short_descp
                    myPrintf("You are in a large and very grand room with walls made of\npolished mahogany, and crystal chandeliers hanging from an\nornate ceiling.  There is a mihrab incongruously set in the\nwestern wall of the building.  Also there are quotations\nfrom the Quram written in golden letters along the top of\nthe walls near the ceiling.  On the floor are sumptuous\ncushions made of silk and fine green velvet. Sitting on the\ncushions are old men wearing black pajamas with white\ntowels wrapped around their heads. In the middle of the\nfloor is a raised dais, upon which is sitting a rather\nstately old man wearing similar clothing. In the far corner\nof the room is standing a wax dummy of the Ayatolla\nKhomeini. His right arm is upright much like the Statue of\nLiberty and in his right hand is a glowing 100 watt light\nbub.  I think this is the prayer room for the Iranian\nParliament!  You'll have to go west if you wish to leave.\n\n")
                    return
                }
                R_guard -> {
                    room[n][M_rm_type] = T_was_long
                    myPrintf("You are in a rather small room with a table in the middle that is covered\nwith half filled cups of tea, dirty ash trays, and a newspaper written in\nwhat appears to be Arabic.  There is a brightly coloured poster on the\nwall depicting the Kaba Stone of Mecca with a calendar below it.  The only\nexits are through an open doorway to the east and through a curtain covered\ndoorway to the south.\n\n")
                    return
                }
                R_closet -> {
                    room[n][M_rm_type] = T_was_long
                    myPrintf("You are in a closet full of grubby, nondescript things not\nworth taking. The closet stinks of moth balls. The only way\nout is through a curtain covered doorway to the north.\n\n")
                    return
                }
                R_gong -> {
                    room[n][M_rm_type] = T_short_descp
                    myPrintf("You have entered an enormous room which is walled with\npolished black granite supported by vast curving arches\nmuch like a gothic cathedral.  The cavernous room is a seven\nsided polygon in layout with huge flaming torches set in\nbrass holders in every corner of the room.  In the flickering\nlight of the torches you see an enormous silver gong\nweighing at least a ton.  The huge gong is suspended from\na finely polished red cedar frame, the top of which is\nabout four metres above the floor.  The room is deathly\nquiet like an ancient tomb. The only sound you can hear is\nyour own heart beating.\n")
                    return
                }
                R_warning -> {
                    room[n][M_rm_type] = T_was_long
                    myPrintf("You have entered a passage with exits to the north, south,\nand west.  There is a poster glued to the wall which reads:\n")
                    myPrintf("+---------------------------------------------------------+\n")
                    myPrintf("|                                                         |\n")
                    myPrintf("|              ACME MINE IS UNFAIR TO WORKERS!!           |\n")
                    myPrintf("|                                                         |\n")
                    myPrintf("|                       ON STRIKE!!                       |\n")
                    myPrintf("|                                                         |\n")
                    myPrintf("| The Loyal Brotherhood of Australian Miners and Under-   |\n")
                    myPrintf("| ground Workers is on strike against Acme Mines Ltd. due |\n")
                    myPrintf("| to unfair treatment of its workers.  Our Union demands  |\n")
                    myPrintf("| the following:  Worker's wages be increased by 30%%, the |\n")
                    myPrintf("| week shall have only 4 working days, the working day    |\n")
                    myPrintf("| shall be reduced to 5 hours, triple wages for overtime  |\n")
                    myPrintf("| work and immunity from sacking for all Union members.   |\n")
                    myPrintf("| To discourage scabs and other strike breakers from      |\n")
                    myPrintf("| working during the strike, we have planted booby traps  |\n")
                    myPrintf("| through out the tunnels on this level.  We do hope you  |\n")
                    myPrintf("| don't mind the mine being mined so it won't be mined.   |\n")
                    myPrintf("+---------------------------------------------------------+\n")
                    return
                }
            }
        } else describe(n)
    }



    private fun which_button() {
        myPrintf("Which button do you want me to push? \n")
    }

    private suspend fun lift_moves() {
        myPrintf("The steel doors slam shut and you feel the vibration of the lift \n")
        myPrintf("moving in its shaft. Finally the lift jerks to a halt and the doors whirr open. \n")
    }

    private fun nothing_happens() {
        myPrintf("Nothing happens!\n")
    }

    private fun cubic() {
        myPrintf("You press the button on the cube but nothing happens.\n")
    }

    private fun no_gray() {
        myPrintf("I see no gray button to push!\n")
    }

    private fun no_switch() {
        myPrintf("There is nothing here with a push button switch.\n")
    }

    private fun no_cube() {
        myPrintf("You don't have the black cube with the blue button in your possession!\n")
    }

    private suspend fun pusher(loc: IntArray) {
        val n = loc[0]

        if (tag[V_VERB_ONLY]) {
            myPrintf("What exactly do you want me to push? \n")
            return
        }

        if (tag[V_red_button] || tag[V_white_button] || tag[V_green_button] || tag[V_orange_button]) {
            if (Gong.status == S_recorder) {
                if (Recorder.location == B_have) {
                    if (tag[V_red_button]) {
                        if (recorderStatus != S_inactive) {
                            myPrintf("--- The data recorder has stopped ")
                            if (recorderStatus == S_playing) {
                                myPrintf("playing. ---\n")
                            } else {
                                myPrintf("recording. ---\n")
                            }
                            stopRecordingOrPlayback()
                        } else {
                            myPrintf("Nothing happened!\n")
                        }
                        return
                    }
                    if (tag[V_white_button]) {
                        myPrintf("Type in the file name for reading from:  ")
                        val name = inputQueue.take().trim()
                        startPlayback(name)
                        return
                    }
                    if (tag[V_green_button]) {
                        myPrintf("Type in the file name for writing to:  ")
                        val name = inputQueue.take().trim()
                        startRecording(name)
                        return
                    }
                    if (tag[V_orange_button]) {
                        myPrintf("Dinkum's clock has been stopped.\n\n")
                        val delta_time = (System.currentTimeMillis() / 1000L) - (start_time + 3300L)

                        while (true) {
                            myPrintf("Do you wish to resume playing Dinkum?\n")
                            if (yesNo()) {
                                start_time = (System.currentTimeMillis() / 1000L) - (delta_time + 3300L)
                                myPrintf("\n+----------------------------------------------------------+\n")
                                myPrintf("| Dinkum's clock is restarted with the same time remaining |\n")
                                myPrintf("| as when it was halted.                                   |\n")
                                myPrintf("+----------------------------------------------------------+\n\n")
                                describe(n)
                                return
                            } else {
                                myPrintf("Do you wish to quit Dinkum?\n")
                                if (yesNo()) ender(F_quit)
                            }
                        }
                    }
                } else {
                    myPrintf("You don't have the recorder in your possession!\n")
                }
            } else {
                myPrintf("I have never seen a button of that color.\n")
            }
            return
        }

        when (n) {
            R_lift_entr -> {
                if (tag[V_gray_button]) {
                    no_gray()
                    return
                }
                if (!tag[V_button]) {
                    if (!tag[V_blue_button]) {
                        myPrintf("I don't see why I should push that! \n")
                    } else {
                        nothing_happens()
                    }
                    return
                }
                if (Lift_door.status == S_flashing) {
                    myPrintf("You push the call button and there is a loud \"whirr\" from ")
                    myPrintf("an electric motor. \nThe massive steel doors slide open ")
                    myPrintf("revealing a huge lift that could hold \ntwenty men at once. ")
                    myPrintf("The way is now open for you to enter. \n")
                    Lift_door.status = S_open
                    room[R_lift_entr][2] = R_lift_inside
                } else {
                    if (Lift_door.status == S_open) {
                        myPrintf("You push the call button and there is a loud \"whirr\" from ")
                        myPrintf("an electric motor. \nThe massive steel doors slide shut, ")
                        myPrintf("closing off access to the lift.\n")
                        Lift_door.status = S_flashing
                        room[R_lift_entr][2] = R_WALL
                    } else {
                        myPrintf("You pushed the lift call button, but nothing happened.\n")
                        myPrintf("I think the electrical power has been turned off at the\n")
                        myPrintf("circuit breaker.\n")
                    }
                }
            }

            R_lift_inside -> {
                if (tag[V_gray_button]) {
                    no_gray()
                    return
                }

                if (tag[V_0]) {
                    if (Lift.status == L0) {
                        myPrintf("You pushed the flashing button, but nothing happened.\n")
                        return
                    }
                    myPrintf("The steel doors slam shut and you feel heavy as the lift ")
                    myPrintf("accelerates \nupwards.  Finally the lift jerks to a halt ")
                    myPrintf("and the doors whirr open.  \nSunlight is streaming in. ")
                    myPrintf("Once again you are breathing the fresh air.\n")
                    Lift.status = L0
                    room[R_lift_inside][3] = R_lift_entr
                    room[R_lift_inside][7] = R_lift_entr
                    return
                }

                if (tag[V_49]) {
                    if (Lift.status == L49) {
                        myPrintf("You pushed the forty-nine button, but nothing happened.\n")
                        return
                    }
                    Lift.status = L49
                    room[R_lift_inside][3] = R_L49_entr
                    room[R_lift_inside][7] = R_L49_entr
                    lift_moves()
                    return
                }

                if (tag[V_67]) {
                    if (Lift.status == L67) {
                        myPrintf("You pushed the sixty-seven button, but nothing happened.\n")
                        return
                    }
                    Lift.status = L67
                    room[R_lift_inside][3] = R_L67_entr
                    room[R_lift_inside][7] = R_L67_entr
                    lift_moves()
                    return
                }

                if (tag[V_82]) {
                    if (Lift.status == L82) {
                        myPrintf("You pushed the eighty-two button, but nothing happened.\n")
                        return
                    }
                    Lift.status = L82
                    room[R_lift_inside][3] = R_L82_entr
                    room[R_lift_inside][7] = R_L82_entr
                    myPrintf("The steel doors slam shut and you feel a sense of ")
                    myPrintf("weightlessness as \nthe lift plummets down the mine shaft. ")
                    myPrintf("Finally the lift comes to a halt \n")
                    myPrintf("and the doors whirr open. \n")
                    return
                }
                if (tag[V_blue_button]) {
                    nothing_happens()
                    return
                }
                myPrintf("This lift can go only to levels 0, 49, 67, or 82 \n")
            }

            R_closet -> {
                if (tag[V_gray_button]) {
                    no_gray()
                    return
                }
                if (tag[V_blue_button] || tag[V_button]) {
                    if (Cube.location != B_have) {
                        no_switch()
                        return
                    }
                    myPrintf("There is a bright flash of light! Then suddenly you are\n")
                    myPrintf("back in the hexagon chamber of the ancient spaceship.\n\n")
                    loc[0] = R_transporter
                } else {
                    no_switch()
                }
            }

            R_panel -> {
                if (tag[V_button]) {
                    which_button()
                    return
                }
                if (tag[V_blue_button]) {
                    if (Cube.location == B_have) {
                        cubic()
                    } else {
                        no_cube()
                    }
                    return
                }
                if (tag[V_gray_button]) {
                    if (Transporter.status == 0) {
                        myPrintf("Lots of the \"Sanskrit\" text is flashing by on the display\n")
                        myPrintf("panel's CRTs.  There is a whirring noise coming out of the\n")
                        myPrintf("equipment around you which is increasing in both loudness\n")
                        myPrintf("and pitch. You've turned something on, that's for sure!\n")
                        Transporter.status = 1
                    } else {
                        nothing_happens()
                    }
                    return
                }
            }

            else -> {
                if (tag[V_detector] || tag[V_yellow_button] || ((Cube.location != B_have) && (Detector.location == B_have))) {
                    if (Detector.location == B_have) {
                        myPrintf("You press the button on the Semtex explosive detector and\n")
                        for (i in 0..9) {
                            if ((n == i + 207) || (n == i + 228)) {
                                if (room[227 - i][M_rm_type] == T_lethal) {
                                    myPrintf("hear a \"beep, beep, beep\".\n")
                                    return
                                }
                            }
                        }
                        myPrintf("the yellow light flashed.\n")
                    } else {
                        myPrintf("You don't have the detector in your possession.\n")
                    }
                    return
                }
                if (Cube.location == B_have) {
                    if ((Detector.location == B_have) && (!tag[V_blue_button])) {
                        which_button()
                        return
                    }
                    cubic()
                    return
                }
                if (tag[V_blue_button]) {
                    no_cube()
                    return
                }
                if (tag[V_gray_button]) {
                    no_gray()
                    return
                }
                myPrintf("You can push as much as you like, but nothing will happen.\n")
            }
        }
    }

    private fun switcher(n: Int) {
        if (tag[V_VERB_ONLY]) {
            if (tag[V_turn]) myPrintf("Turn what?\n")
            if (tag[V_set]) myPrintf("Set what?\n")
            if (tag[V_select]) myPrintf("Select what?\n")
            if (tag[V_switch]) myPrintf("Switch what?\n")
            return
        }

        if (tag[V_safety]) {
            if (Rifle.location == B_have) {
                if (tag[V_off]) {
                    rifle_flag = F_single
                    myPrintf("The M16 rifle is set to single shot mode with the safety off.\n")
                    return
                }
                if (tag[V_on]) {
                    rifle_flag = F_safety
                    myPrintf("The M16 rifle's safety has been turned on.\n")
                    return
                }
                myPrintf("How do you want the safety set?\n")
            } else {
                myPrintf("You're not holding the rifle!\n")
            }
            return
        }

        if (tag[V_turn] && tag[V_dial] && (!tag[V_rifle])) {
            dialer(n)
            return
        }

        if (tag[V_turn] && (!tag[V_dial]) && tag[V_safe]) {
            if (n != R_office_mang) {
                myPrintf("There is no safe here to turn!\n")
                return
            }
            myPrintf("Since the safe is set in a concrete wall, turning it would\n")
            myPrintf("be rather difficult!  Perhaps I should turn the safe's\n")
            myPrintf("dial instead?\n")
            return
        }

        var flag_switch = F_no_argument
        if (tag[V_on]) flag_switch = 1
        if (tag[V_off]) flag_switch = 0
        if (verb == V_light) flag_switch = 1

        var flag_rifle_md = F_no_argument
        if (tag[V_safe]) flag_rifle_md = F_safety
        if (tag[V_triple]) flag_rifle_md = F_triple
        if (tag[V_single]) flag_rifle_md = F_single
        if (tag[V_auto]) flag_rifle_md = F_auto

        if ((flag_switch == F_no_argument) && (flag_rifle_md == F_no_argument)) {
            myPrintf("You will have to be a little more specific than that. \n")
            return
        }

        if ((!tag[V_torch]) && (!tag[V_rifle]) && (flag_rifle_md == F_no_argument)) {
            if (n == R_store_room) {
                if (flag_switch == 0) {
                    if (Circuit_breaker.status != 0) {
                        myPrintf("As you pull the switch lever, there is a bright blue \n")
                        myPrintf("arc and then all of the lights go out. \n")
                        Circuit_breaker.status = 0
                        Lift_door.status = S_closed
                        room[R_lift_entr][2] = R_WALL
                    } else {
                        myPrintf("The circuit breaker is already turned off! \n")
                    }
                    return
                }
                if (flag_switch == 1) {
                    if (Circuit_breaker.status != 0) {
                        myPrintf("The circuit breaker is already turned on! \n")
                    } else {
                        myPrintf("As you pull the switch lever, there is a loud \"CLUNK\" \n")
                        myPrintf("and you see a POWER ON light glowing. \n")
                        Circuit_breaker.status = 1
                        Lift_door.status = S_flashing
                    }
                    return
                }
            }
            myPrintf("I don't understand what you want me to switch. \n")
            return
        }

        if (tag[V_rifle]) {
            if (Rifle.location == B_have) {
                if ((flag_switch == 1) || ((flag_rifle_md == F_safety) && (flag_switch == 0))) {
                    if (rifle_flag != F_safety) {
                        myPrintf("The rifle's safety is already off! \n")
                        return
                    }
                    rifle_flag = F_single
                    myPrintf("The M16 rifle has been set to single shot mode. \n")
                    return
                }
                if ((flag_switch == 0) || ((flag_rifle_md == F_safety) && ((flag_switch == 1) || (flag_switch == F_no_argument)))) {
                    if (rifle_flag == F_safety) {
                        myPrintf("The rifle's safety is already on! \n")
                        return
                    }
                    rifle_flag = F_safety
                    myPrintf("The M16 rifle's safety has now been turned on. \n")
                    return
                }
                if (flag_rifle_md == F_single) {
                    if (rifle_flag == F_single) {
                        myPrintf("The rifle has already been set to single shot mode.\n")
                        return
                    }
                    rifle_flag = F_single
                    myPrintf("The M16 rifle has been set to single shot mode. \n")
                    return
                }
                if (flag_rifle_md == F_triple) {
                    if (rifle_flag == F_triple) {
                        myPrintf("The rifle has already been set to triple fire mode.\n")
                        return
                    }
                    rifle_flag = F_triple
                    myPrintf("The M16 rifle has been set to triple fire mode. \n")
                    return
                }
                if (flag_rifle_md == F_auto) {
                    if (rifle_flag == F_auto) {
                        myPrintf("The rifle has already been set to fully automatic.\n")
                        return
                    }
                    rifle_flag = F_auto
                    myPrintf("The M16 rifle has been set to fully automatic. \n")
                    return
                }
            } else {
                myPrintf("You don't have a rifle in your possession! \n")
            }
            return
        }

        if (tag[V_torch]) {
            if (Torch.location == B_have) {
                if (flag_switch == 1) {
                    if (Torch.status != 0) {
                        myPrintf("The torch is already turned on.\n")
                        return
                    }
                    myPrintf("An intense beam of light is projected from the torch.\n")
                    Torch.status = 1
                    return
                }
                if (flag_switch == 0) {
                    if (Torch.status == 0) {
                        myPrintf("The torch isn't switched on.\n")
                        return
                    }
                    myPrintf("The torch switches off with a \"click\".\n")
                    Torch.status = 0
                    return
                }
            }
            myPrintf("You don't have a torch in your possession to switch! \n")
        }
    }

    private fun dialer(n: Int) {
        if (tag[V_what]) {
            myPrintf("I really don't know.  You might try looking around for a\n")
            myPrintf("series of numbers. Sometimes that can be a clue to the\n")
            myPrintf("actual combination.\n")
            return
        }

        if ((n == R_office_mang) && (Picture.status >= S_open)) {
            if (tag[V_0] && tag[V_49] && tag[V_67] && tag[V_82]) {
                myPrintf("Click!! \n")
                Safe.status = S_dialed
                return
            }
            myPrintf("You turned the dial but nothing happened.  I think you \n")
            myPrintf("dialed the wrong thing. \n")
            return
        }
        myPrintf("How am I supposed to dial that? \n")
    }

    private fun inventer() {
        var j_ws = 0
        var point: ObjectStruct? = null
        for (obj in objectList) {
            if (obj.location == B_have) {
                j_ws++
                point = obj
            }
        }
        if ((j_ws == 0) && (gleep_count == 0)) {
            myPrintf("You've got NOTHING!! \n")
            return
        }
        if (j_ws >= 1) {
            if (j_ws == 1 && point != null) {
                myPrintf("You are carrying ")
                objector(point)
                myPrintf("\n")
            } else {
                myPrintf("You are carrying the following: \n")
                for (obj in objectList) {
                    if (obj.location == B_have) {
                        myPrintf("        ")
                        objector(obj)
                        myPrintf("\n")
                    }
                }
            }
        }
        if (gleep_count > 0) {
            if (gleep_count == 1) {
                myPrintf("You have one gleep.\n")
                return
            }
            myPrintf("You are carrying %d gleeps. \n", gleep_count)
        }
    }

    private fun drinker() {
        if (tag[V_can]) {
            if (Can.location == B_have) {
                myPrintf("The XXXX keeps on coming on ? \n")
                myPrintf("I think not, for there is no Fourex left for me to drink!\n")
            } else {
                myPrintf("You don't have it in your possession to drink!\n")
            }
            return
        }

        if (tag[V_bottle]) {
            if (Bottle.location == B_have) {
                myPrintf("Some greedy larrikin has drunk up all of the Black Swan! \n")
                myPrintf("Bad on Him! There is none left for me! \n")
                return
            }
            myPrintf("You don't have it in your possession to drink!\n")
            return
        }

        if (tag[V_water]) {
            myPrintf("There is no water here that I am willing to drink.\n")
            return
        }

        myPrintf("What exactly do you want me to drink? \n")
    }

    private suspend fun reader(n: Int) {
        if (tag[V_VERB_ONLY]) {
            myPrintf("What exactly do you want me to read? \n")
            return
        }

        var sw_no_see = false
        for (i in 0 until Read_objcnt) {
            val rObj = read_object[i]
            val m = rObj.id
            if (!rObj.readable) continue
            if (tag[m]) {
                val point = pointToObject(m)
                if (((point.location == n) && (point.type == Z_unmovable)) || (point.location == B_have)) {
                    rdtxt(m)
                } else {
                    if (point.type == Z_unmovable) {
                        sw_no_see = true
                        continue
                    } else {
                        myPrintf("I can't read it because it isn't in my possession. \n")
                    }
                }
                return
            }
        }

        if (sw_no_see) {
            myPrintf("I don't see how I can read that.\n")
        } else {
            myPrintf("There is no way in which I can read that. \n")
        }
    }

    private fun closer(n: Int) {
        if (room[n][M_unmov_obj] == 0) {
            myPrintf("There is nothing here that can be closed! \n")
            return
        }

        if (tag[V_VERB_ONLY]) {
            myPrintf("What exactly do you want me to close? \n")
            return
        }

        when (n) {
            R_office_entr -> {
                if (!tag[V_door]) return

                when (Door.status) {
                    S_kicked -> {
                        myPrintf("The door has been kicked in!  It'll never be closed again.\n")
                    }

                    S_open -> {
                        if (tag[V_key]) {
                            if (Key.location != B_have) {
                                myPrintf("You don't have the key to this door in your possession! \n")
                            } else {
                                myPrintf("You close the office door and lock it with the key.\n")
                                room[R_office_entr][2] = R_WALL
                                Door.status = S_closed
                            }
                            return
                        }
                        myPrintf("The office door closes with a slam. \n")
                        Door.status = S_unlocked
                    }

                    else -> {
                        myPrintf("The office door is already closed! \n")
                    }
                }
            }

            R_office_mang -> {
                if ((!tag[V_picture]) && (!tag[V_safe])) return
                if (tag[V_safe]) {
                    if (Safe.status != S_open) {
                        myPrintf("The safe is already closed. \n")
                    } else {
                        myPrintf("The safe door closes and locks with a \"click\". \n")
                        Safe.status = S_closed
                    }
                }
                if (tag[V_picture]) {
                    if (Picture.status == S_closed) {
                        myPrintf("The platypus picture is already closed. \n")
                    } else {
                        myPrintf("The picture swings flush against the wall, hiding the safe.\n")
                        Picture.status = S_closed
                    }
                }
            }
        }
    }

    private suspend fun opener(n: Int) {
        if (tag[V_VERB_ONLY]) {
            myPrintf("What exactly do you want me to open? \n")
            return
        }

        if (tag[V_can]) {
            if (Can.location == B_have) {
                myPrintf("Some bugger has already drunk it up. The stubee is empty!\n")
            } else {
                myPrintf("You don't have a can to open! \n")
            }
            return
        }

        if (tag[V_letter]) {
            if (Letter.location == B_have) {
                boom()
                myPrintf("It's not polite to read other people's mail!!  As you lay\n")
                myPrintf("bleeding on the floor with your hands and arms blown away,\n")
                myPrintf("you realize that envelope actually contained a letter bomb!\n")
                myPrintf("(A trick widely used by terrorists in the 1970s).  However\n")
                myPrintf("the knowledge does you little good since your spirit soon\n")
                myPrintf("departs this mortal coil.\n")
                ender(F_died)
            } else {
                myPrintf("You don't have a letter to open!\n")
            }
            return
        }

        if (tag[V_bottle]) {
            if (Bottle.location == B_have) {
                myPrintf("Some bugger has already drunk it up. The bottle is Empty! \n")
            } else {
                myPrintf("You don't have a bottle to open! \n")
            }
            return
        }

        if (tag[V_umbrella]) {
            if (Umbrella.location == B_have) {
                myPrintf("You open the umbrella but find it's full of holes and\n")
                myPrintf("its clasp is broken.  As soon as you release it, the\n")
                myPrintf("umbrella snaps shut.\n")
            } else {
                myPrintf("You don't have an umbrella to open! \n")
            }
            return
        }

        if (room[n][M_unmov_obj] == 0) {
            myPrintf("I don't understand what you want me to open. \n")
            return
        }

        when (n) {
            R_office_entr -> {
                if (tag[V_door]) {
                    when (Door.status) {
                        S_kicked -> {
                            myPrintf("The door has been kicked in!  You don't need to open it.\n")
                        }

                        S_open -> {
                            myPrintf("The door is already open! \n")
                        }

                        S_unlocked -> {
                            myPrintf("As you push open the office door, the rusty hinges creak \n")
                            myPrintf("from long disuse.  Darkness and stale air seem to exude \n")
                            myPrintf("from the vacant office.  The way is free for you to enter. \n")
                            room[R_office_entr][2] = R_office_hall
                            Door.status = S_open
                        }

                        S_closed -> {
                            if (tag[V_key]) {
                                if (Key.location != B_have) {
                                    myPrintf("You don't have the key to this door in your possession! \n")
                                } else {
                                    myPrintf("You turn the key in the lock and hear a satisfying \"click\".\n")
                                    myPrintf("Then you push open the office door, the rusty hinges creak \n")
                                    myPrintf("from long disuse.  Darkness and stale air seem to exude \n")
                                    myPrintf("from the vacant office.  The way is free for you to enter. \n")
                                    room[R_office_entr][2] = R_office_hall
                                    Door.status = S_open
                                }
                            } else {
                                myPrintf("The office door is locked tight.  You need to unlock it with a key. \n")
                            }
                        }
                    }
                }
            }

            R_office_mang -> {
                if ((!tag[V_picture]) && (!tag[V_safe])) {
                    myPrintf("I don't understand how I would open that.\n")
                    return
                }

                if (tag[V_picture]) {
                    if (Picture.status == S_closed) {
                        myPrintf("You swing open the platypus picture revealing a large wall \n")
                        myPrintf("safe with a combination dial. \n")
                        Picture.status = S_open
                    } else {
                        if (Safe.status == S_closed) {
                            myPrintf("The picture is already swung out.  The problem is to open \n")
                            myPrintf("the safe!!  Hint:  Dial in a combination. \n")
                        } else {
                            myPrintf("The picture is already swung out.\n")
                        }
                    }
                    return
                }

                if (tag[V_safe]) {
                    if (Picture.status == S_closed) {
                        myPrintf("I do not see a safe in this office for me to open.  Perhaps\n")
                        myPrintf("there is a safe hidden in this office somewhere.\n")
                        return
                    }

                    when (Safe.status) {
                        S_closed -> {
                            myPrintf("The safe is locked.  You must first dial the combination\n")
                            myPrintf("in order to open it.  See if you can find the combination. \n")
                        }

                        S_dialed -> {
                            myPrintf("The safe door swings open smoothly and easily. \n")
                            Safe.status = S_open
                            sw_active = true
                            var sw_found = false
                            for (obj in objectList) {
                                if (obj.location == B_in_safe) {
                                    myPrintf("Inside the safe, you see the following: \n")
                                    sw_found = true
                                    for (o2 in objectList) {
                                        if (o2.location == B_in_safe) {
                                            myPrintf("        ")
                                            objector(o2)
                                            myPrintf("\n")
                                        }
                                    }
                                    break
                                }
                            }
                            if (gleep_safe != 0) {
                                if (sw_found) {
                                    if (gleep_safe == 1) {
                                        myPrintf("        one gleep\n")
                                    } else {
                                        myPrintf("        %d gleeps\n", gleep_safe)
                                    }
                                } else {
                                    if (gleep_safe == 1) {
                                        myPrintf("Inside the safe is one gleep.\n")
                                    } else {
                                        myPrintf("Inside the safe are %d gleeps.\n", gleep_safe)
                                    }
                                    sw_found = true
                                }
                            }
                            if (!sw_found) {
                                myPrintf("---The safe is empty.--- \n")
                            }

                            kelly(R_office_mang)
                        }

                        S_open -> {
                            myPrintf("The safe is already open! \n")
                        }
                    }
                }
            }

            R_lift_entr -> {
                if (tag[V_door] || tag[V_lift]) {
                    myPrintf("It is not exactly clear how I would open these doors.\n")
                    myPrintf("However I see a push button next to the lift doors.\n")
                    myPrintf("Perhaps if I pressed the button something would happen.\n")
                } else {
                    myPrintf("I'm confused about what I should open.\n")
                }
            }

            else -> {
                myPrintf("I see nothing here that I can open. \n")
            }
        }
    }

    private fun taker(n: Int) {
        var j = 0
        if (tag[V_VERB_ONLY]) {
            for (obj in objectList) {
                if (n == obj.location) {
                    j++
                    tag[obj.id] = true
                }
            }
            if (room[n][M_gleep] != 0) {
                j++
                tag[V_gleep] = true
                if (room[n][M_gleep] > 1) tag[V_PLURAL] = true
            }
            if (j > 1) {
                myPrintf("What exactly do you want me to take? \n")
                return
            }
        }

        var ammo_flag = F_no_clip
        if (tag[V_clip]) ammo_flag = F_normal_clip
        if (tag[V_org_clip]) ammo_flag = F_org_clip

        if (tag[V_clip] && (Clip.location != n) && (Org_clip.location == n)) {
            tag[V_org_clip] = true
            tag[V_clip] = false
            if (V_clip == sent[1]) sent[1] = V_org_clip
        }

        if (verb == V_attach) {
            if (tag[V_rifle] && (ammo_flag != F_no_clip)) {
                clip_in(ammo_flag)
                return
            }
            if ((n != R_office_mang) && (room[n][M_obj_cnt] <= 0)) {
                myPrintf("There is nothing here that I can attach!\n")
                return
            }
        }

        for (obj in objectList) {
            if (obj.id == sent[1]) {
                if (obj.location == B_have) {
                    myPrintf("You already have a %s in your possession.\n", vocab[sent[1] - 1])
                    return
                } else break
            }
        }

        var sw_done = false

        if ((tag[V_pill]) && (Pills.location == B_have)) {
            eater()
            return
        }

        if (tag[V_pants]) {
            if ((verb == V_remove) || tag[V_off]) {
                myPrintf("This is not R rated.  I can't remove my clothing!\n")
            } else {
                myPrintf("I don't understand what you want me to do with the clothing.\n")
            }
            return
        }

        if ((n != R_office_mang) && (verb != V_attach) && (verb != V_remove) &&
            (room[n][M_obj_cnt] <= 0) && (room[n][M_gleep] == 0) &&
            (!((n == R_gleep_tank) && (Tank.status > 0))) &&
            (room[n][M_unmov_obj] == 0)
        ) {
            myPrintf("There is nothing here that you can take! \n")
            return
        }

        if (tag[V_tank]) {
            if (n != R_gleep_tank) {
                myPrintf("I see no gleep tank to take anything from. \n")
                return
            }
            myPrintf("\nYou climb up onto the edge of the gleep tank and reach\n")
            myPrintf("into the dark blue fluid.  Suddenly the fluid begins a\n")
            myPrintf("furious boiling that instantly reduces your hand into\n")
            myPrintf("bleached white bones.  The shock and pain disturbs your\n")
            myPrintf("balance and you fall into the horrible stuff!!  There is\n")
            myPrintf("once again a furious boiling.  Eventually the fluid stills\n")
            myPrintf("and becomes clear again.  On the bottom of the tank can\n")
            myPrintf("be seen a white, clean, \"medical school quality\" human\n")
            myPrintf("skeleton.\n")
            ender(F_died)
        }

        if ((tag[V_safe]) && (n != R_office_mang)) {
            myPrintf("There is no safe here.\n")
            return
        }

        if (tag[V_gleep]) {
            if (!tag[V_safe]) {
                var gleep_local = 0
                if ((n == R_office_mang) && (Safe.status == S_open)) {
                    gleep_local = gleep_safe
                }
                gleep_local += room[n][M_gleep]
                if (n == R_gleep_tank) gleep_local += Tank.status

                if (gleep_local == 0) {
                    myPrintf("There are no gleeps here to take!\n")
                    return
                }

                if ((n == R_gleep_tank) && (room[n][M_gleep] == 0)) {
                    myPrintf("There are no gleeps on the floor but I see ")
                    if (Tank.status == 1) {
                        myPrintf("a gleep in the gleep tank.\n")
                    } else {
                        myPrintf("%d gleeps in the gleep tank.\n", Tank.status)
                    }
                    return
                }

                if ((n == R_office_mang) && (Safe.status == S_open) && (Picture.status == S_open)) {
                    if ((!tag[V_PLURAL]) || (gleep_local == 1)) {
                        myPrintf("Gleep taken. \n")
                        gleep_count++
                        if (gleep_safe == 0) room[n][M_gleep]-- else gleep_safe--
                    } else {
                        if (tag[V_NUMBER]) {
                            if (number_word == 0) {
                                myPrintf("Don't be silly.\n")
                                return
                            }
                            if (number_word > gleep_local) {
                                myPrintf("There aren't that many gleeps here!\n")
                                return
                            } else {
                                myPrintf("%d gleeps taken.\n", number_word)
                                gleep_count += number_word
                                gleep_local -= number_word
                                gleep_safe = gleep_local
                                room[n][M_gleep] = 0
                            }
                        } else {
                            myPrintf("Gleeps taken. \n")
                            gleep_count += gleep_local
                            room[n][M_gleep] = 0
                            gleep_safe = 0
                        }
                    }
                    return
                }

                if ((!tag[V_PLURAL]) || (room[n][M_gleep] == 1)) {
                    myPrintf("Gleep taken. \n")
                    gleep_count++
                    room[n][M_gleep]--
                } else {
                    if (tag[V_NUMBER]) {
                        if (number_word == 0) {
                            myPrintf("Don't be silly.\n")
                            return
                        }
                        if (number_word > room[n][M_gleep]) {
                            myPrintf("There aren't that many gleeps here!\n")
                            return
                        } else {
                            myPrintf("%d gleeps taken.\n", number_word)
                            gleep_count += number_word
                            room[n][M_gleep] -= number_word
                        }
                    } else {
                        myPrintf("Gleeps taken. \n")
                        gleep_count += room[n][M_gleep]
                        room[n][M_gleep] = 0
                    }
                }
                return
            } else {
                if ((Picture.status == S_open) && (Safe.status == S_open)) {
                    if (gleep_safe != 0) {
                        if (tag[V_PLURAL] && (gleep_safe > 1)) {
                            if (tag[V_NUMBER]) {
                                if (number_word == 0) {
                                    myPrintf("Don't be silly.\n")
                                    return
                                }
                                if (number_word > gleep_safe) {
                                    myPrintf("There aren't that many gleeps in the safe!\n")
                                    return
                                } else {
                                    myPrintf("%d gleeps taken.\n", number_word)
                                    gleep_count += number_word
                                    gleep_safe -= number_word
                                }
                            } else {
                                myPrintf("Gleeps taken from safe. \n")
                                gleep_count += gleep_safe
                                gleep_safe = 0
                            }
                        } else {
                            myPrintf("Gleep taken from safe. \n")
                            gleep_count++
                            gleep_safe--
                        }
                    } else {
                        myPrintf("There are no gleeps in the safe!\n")
                    }
                } else {
                    if (tag[V_PLURAL]) {
                        myPrintf("There is no open safe to take gleeps from.\n")
                    } else {
                        myPrintf("There is no open safe to take the gleep from.\n")
                    }
                }
                return
            }
        }

        if (verb == V_remove) {
            if ((!tag[V_safe]) && (ammo_flag != F_no_clip)) {
                clip_out(n)
                return
            }
        }

        if (carry_count > 5) {
            myPrintf("I can't do it!\n")
            myPrintf("I'm holding so many things that I can't take anymore!\n")
            return
        }

        if (carry_weight >= 800) {
            myPrintf("I can't do it!\n")
            myPrintf("This junk I'm carrying is too heavy! I can't carry anymore!\n")
            return
        }

        for (pnt in objectList) {
            if ((carry_count > 5) || (carry_weight >= 800)) break

            if ((n == R_office_mang) && (pnt.location == B_in_safe) &&
                (tag[pnt.id] || tag[V_all]) &&
                ((!tag[V_treasure]) || (pnt.value > 0))
            ) {
                if (Picture.status != S_open) {
                    if (tag[V_all] || tag[V_treasure]) {
                        myPrintf("I don't see a safe.\n")
                    } else {
                        myPrintf("You need to open the picture first.\n")
                    }
                    return
                }
                if (Safe.status == S_open) {
                    pnt.location = B_have
                    carry_count++
                    carry_weight += pnt.weight
                    sw_done = true
                } else {
                    myPrintf("I can't do it because the safe is closed. \n")
                    return
                }
            }

            if ((pnt.location == n) && (tag[pnt.id] || tag[V_all]) &&
                ((!tag[V_treasure]) || (pnt.value > 0))
            ) {
                if (!tag[V_safe]) {
                    if (pnt.type == Z_transform) {
                        when (pnt.id) {
                            V_mat -> {
                                myPrintf("As you lift up the dirty old doormat, you find half \n")
                                myPrintf("hidden in the dust....  a large brass key. \n")
                                Mat.type = Z_normal
                                carry_count++
                                carry_weight += Mat.weight
                                Mat.location = B_have
                                Key.location = R_office_entr
                                return
                            }

                            V_map -> {
                                myPrintf("As you picked up the old map from the floor, most of it crumbled into \ndust leaving only one small piece. \n")
                                Map.location = B_unmade
                                Map_frag.location = B_have
                                carry_count++
                                carry_weight += Map_frag.weight
                                room[n][M_obj_cnt]--
                                return
                            }
                        }
                    }

                    if ((pnt.type == Z_normal) || (pnt.type == Z_alias)) {
                        room[n][M_obj_cnt]--
                        carry_count++
                        carry_weight += pnt.weight
                        pnt.location = B_have
                        sw_done = true
                    }
                }

                if ((pnt.type == Z_unmovable) && (pnt.location == n) && (!tag[V_all])) {
                    when (pnt.id) {
                        V_bulldust -> {
                            myPrintf("The bulldust is so fine that it wafts away with a touch.\n")
                            return
                        }

                        V_gong -> {
                            myPrintf("The siver gong is about three metres in diameter and weighs at\n")
                            myPrintf("least a ton!  There is no way I could move it, let alone get\n")
                            myPrintf("it through the tunnel!\n")
                            return
                        }

                        V_cockroach -> {
                            myPrintf("I will not touch the filthy things!\n")
                            return
                        }

                        V_kangaroo -> {
                            myPrintf("I will not touch a dead maggoty kangaroo!  The smell is bad enough!\n")
                            return
                        }

                        V_desk -> {
                            myPrintf("The desk is of very solid construction and can not be moved.\n")
                            return
                        }

                        V_chart -> {
                            myPrintf("The charts and production schedules are so old and brittle that\n")
                            myPrintf("they'd fall apart the moment you touched them.  Besides they're\n")
                            myPrintf("useless and not worth taking.\n")
                            return
                        }

                        V_poster -> {
                            myPrintf("I think this sort of poster is best left on the wall.\n")
                            return
                        }

                        V_spinifex -> {
                            myPrintf("The thorns on the spinifex are large and nasty.\n")
                            myPrintf("I'll just leave them to roll about.\n")
                            return
                        }

                        V_picture -> {
                            myPrintf("The picture can not be removed.  It appears to be hinged to the wall.\n")
                            return
                        }

                        V_safe -> {
                            if (sw_done) return
                            myPrintf("The safe is set in a concrete wall.  I can see no way\n")
                            myPrintf("the safe can be removed without destroying the wall.\n")
                            return
                        }

                        V_door -> {
                            if (Door.status == S_kicked) {
                                myPrintf("The door is too heavy and bulky to move.\n")
                            } else {
                                myPrintf("The door is attached to the building with some heavy duty\n")
                                myPrintf("hinges which I can't remove.\n")
                            }
                            return
                        }

                        else -> {
                            myPrintf("I don't think I can move that.\n")
                            return
                        }
                    }
                }
            }
        }

        if (tag[V_all] && (!tag[V_treasure])) {
            if (room[n][M_gleep] > 0) {
                gleep_count += room[n][M_gleep]
                room[n][M_gleep] = 0
                sw_done = true
            }
            if ((gleep_safe != 0) && (n == R_office_mang) && (Safe.status == S_open)) {
                gleep_count += gleep_safe
                gleep_safe = 0
                sw_done = true
            }
        }

        if (sw_done) {
            if (((carry_count > 5) || (carry_weight >= 800)) && (tag[V_all])) {
                myPrintf("You've taken as much as you can.\n")
            } else {
                myPrintf("Done \n")
            }
        } else {
            if (tag[V_all]) {
                myPrintf("I see nothing which I can take.\n")
            } else {
                if (tag[V_VERB_ONLY]) {
                    myPrintf("What exactly should I take?\n")
                } else {
                    myPrintf("I don't see a")
                    if (tag[V_PLURAL]) myPrintf("ny")
                    myPrintf(" %s around here. \n", vocab[sent[1] - 1])
                }
            }
        }
    }

    private fun dropper(n: Int) {
        if (tag[V_VERB_ONLY]) {
            myPrintf("You'll have to be more specific. \n")
            return
        }

        if (tag[V_clip] && (Clip.location != B_have) && (clip_flag != F_normal_clip) && ((Org_clip.location == B_have) || (clip_flag == F_org_clip))) {
            tag[V_org_clip] = true
            tag[V_clip] = false
            if (V_clip == sent[1]) sent[1] = V_org_clip
        }

        var ammo_flag = F_no_clip
        if (tag[V_clip]) ammo_flag = F_normal_clip
        if (tag[V_org_clip]) ammo_flag = F_org_clip

        when (verb) {
            V_put -> {
                if (tag[V_tank] || tag[V_gleep] || tag[V_safe] || tag[V_river] || tag[V_billabong] || tag[V_stream]) {
                    // break to normal drop flow
                } else if (tag[V_key] && tag[V_door]) {
                    unlocker(n)
                    return
                } else {
                    if (!tag[V_cap]) {
                        if ((!tag[V_rifle]) && (ammo_flag != F_no_clip)) {
                            myPrintf("What am I to put the clip into? \n")
                            return
                        }
                        if (tag[V_rifle] && (ammo_flag != F_no_clip)) {
                            clip_in(ammo_flag)
                        } else {
                            myPrintf("I don't understand what this is to be put into. \n")
                        }
                        return
                    }
                }
            }

            V_insert -> {
                if (tag[V_safe]) {
                    // break to normal safe flow
                } else if (ammo_flag != F_no_clip) {
                    if (!tag[V_rifle]) {
                        myPrintf("What am I to insert the clip into? \n")
                    } else {
                        clip_in(ammo_flag)
                    }
                    return
                } else if (tag[V_cap]) {
                    if (!tag[V_dynamite]) {
                        myPrintf("I see no reason why I should put a blasting cap into that.\n")
                        return
                    } else {
                        if ((Cap.location != B_have) && (Dynamite.location != B_have)) {
                            myPrintf("You bloody dill!  You have neither the dynamite or a blasting cap.\n")
                            return
                        }
                        if (Cap.location != B_have) {
                            myPrintf("You've got the dynamite but you need a blasing cap.\n")
                            return
                        }
                        if (Dynamite.location != B_have) {
                            myPrintf("You've got the blasting cap but you need some dynamite.\n")
                            return
                        }
                        myPrintf("With some trepidation, you slide the blasting cap into\n")
                        myPrintf("the dynamite.  What you are now holding is VERY dangerous.\n")
                        Cap.location = B_unmade
                        Dynamite.type = Z_alias
                        carry_weight -= Cap.weight
                        carry_count--
                        return
                    }
                } else {
                    myPrintf("I can think of some interesing places to insert this. \n")
                    myPrintf("However I shall not reduce myself to such vulgarity.\n")
                    return
                }
            }

            V_eject -> {
                if ((ammo_flag == F_normal_clip) && (clip_flag == F_org_clip)) ammo_flag = F_org_clip
                if (((ammo_flag == F_org_clip) && (clip_flag == F_org_clip)) || ((ammo_flag == F_normal_clip) && (clip_flag == F_normal_clip))) {
                    clip_out(n)
                    return
                }
            }

            V_drop -> {
                if (((ammo_flag == F_org_clip) && (clip_flag == F_org_clip)) || ((ammo_flag == F_normal_clip) && (clip_flag == F_normal_clip))) {
                    clip_out(n)
                    return
                }
            }
        }

        if (tag[V_all] && (carry_count == 0) && (gleep_count == 0)) {
            myPrintf("You dill!  You have nothing to drop!\n")
            return
        }

        if (tag[V_safe]) {
            if (n != R_office_mang) {
                myPrintf("There is no safe here! \n")
                return
            }
            if ((Picture.status != S_open) || (Safe.status != S_open)) {
                myPrintf("I don't see an open safe to put anything into.\n")
                return
            }
        }

        if (tag[V_gleep] || tag[V_tank]) {
            if (!tag[V_tank]) {
                if (gleep_count == 0) {
                    myPrintf("You have no gleeps to drop! \n")
                    return
                }
                if ((!tag[V_PLURAL]) || (gleep_count == 1)) {
                    if (tag[V_safe]) {
                        myPrintf("Gleep put into safe.\n")
                        gleep_safe++
                        gleep_count--
                    } else {
                        myPrintf("Gleep dropped. \n")
                        gleep_count--
                        room[n][M_gleep]++
                    }
                } else {
                    if (tag[V_safe]) {
                        if (tag[V_NUMBER]) {
                            if (number_word == 0) {
                                myPrintf("Don't be silly.\n")
                                return
                            }
                            if (number_word > gleep_count) {
                                myPrintf("I don't have that many gleeps!\n")
                                return
                            } else {
                                myPrintf("You put %d gleeps into the safe.\n", number_word)
                                gleep_safe += number_word
                                gleep_count -= number_word
                            }
                        } else {
                            myPrintf("Gleeps put into safe.\n")
                            gleep_safe += gleep_count
                            gleep_count = 0
                        }
                    } else {
                        if (tag[V_NUMBER]) {
                            if (number_word == 0) {
                                myPrintf("Don't be silly.\n")
                                return
                            }
                            if (number_word > gleep_count) {
                                myPrintf("I don't have that many gleeps!\n")
                                return
                            } else {
                                myPrintf("You drop %d gleeps.\n", number_word)
                                room[n][M_gleep] += number_word
                                gleep_count -= number_word
                            }
                        } else {
                            myPrintf("Gleeps dropped. \n")
                            room[n][M_gleep] += gleep_count
                            gleep_count = 0
                        }
                    }
                }
                gleep_drop = true
                return
            } else {
                if (n != R_gleep_tank) {
                    myPrintf("I don't see a gleep tank here. \n")
                    return
                }
                if (tag[V_gleep]) {
                    if (gleep_count <= 0) {
                        myPrintf("You have no gleeps to put in the tank. \n")
                        return
                    }
                    if ((!tag[V_PLURAL]) || (gleep_count == 1)) {
                        myPrintf("Your gleep falls into the tank with a \"plonk\".\n")
                        Tank.status++
                        gleep_count--
                    }
                    if (tag[V_PLURAL]) {
                        if (tag[V_NUMBER]) {
                            if (number_word == 0) {
                                myPrintf("Don't be silly.\n")
                                return
                            }
                            if (number_word > gleep_count) {
                                myPrintf("I don't have that many gleeps!\n")
                                return
                            } else {
                                myPrintf("You drop %d gleeps into the gleep tank.\n", number_word)
                                Tank.status += number_word
                                gleep_count -= number_word
                            }
                        } else {
                            myPrintf("Your gleeps fall into the tank causing a splash.\n")
                            Tank.status += gleep_count
                            gleep_count = 0
                        }
                    }
                }

                if (tag[V_all]) {
                    var sw_possess = false
                    for (obj in objectList) {
                        if ((obj.location == B_have) && ((!tag[V_treasure]) || (obj.value > 0))) {
                            sw_possess = true
                            obj.location = B_destroyed
                            carry_count--
                            carry_weight -= obj.weight
                        }
                    }
                    if (gleep_count == 0) {
                        if (sw_possess) {
                            myPrintf("You dump everything into the gleep tank. There is a furious\n")
                            myPrintf("bubbling as the corrosive fluid of the tank turns the\n")
                            myPrintf("objects into NOTHING.\n")
                        } else {
                            myPrintf("You've got nothing to throw into the tank.\n")
                        }
                    } else {
                        if (sw_possess) {
                            myPrintf("You fling everything into the gleep tank.  The gleep")
                            if (gleep_count > 1) {
                                myPrintf("s\nsplash into the tank and settle to the bottom of the\n")
                            } else {
                                myPrintf("\nplonks into the tank and settles to the bottom of the\n")
                            }
                            myPrintf("tank.  However the other objects begin to dissolve the\n")
                            myPrintf("moment the tank's fluid touches them.  Time passes and\n")
                            myPrintf("the objects dissolve into NOTHING.\n")
                        } else {
                            if (gleep_count == 1) {
                                myPrintf("Your gleep falls into the tank with a \"plonk\".\n")
                            } else {
                                myPrintf("Your gleeps fall into the tank causing a splash.\n")
                            }
                        }
                        Tank.status += gleep_count
                        gleep_count = 0
                    }
                    return
                }

                for (pnt in objectList) {
                    if (tag[pnt.id] && (pnt.id != V_tank)) {
                        if (pnt.location == B_have) {
                            myPrintf("You fling the %s into the gleep tank.  As soon as it\n", vocab[pnt.id - 1])
                            myPrintf("touched the tank's fluid there was a furious effervescence\n")
                            myPrintf("as it began to dissolve.  With the passage of time, the\n")
                            myPrintf("fluid stills and there is NOTHING left.\n")
                            carry_count--
                            carry_weight -= pnt.weight
                            pnt.location = B_destroyed
                        } else {
                            myPrintf("You don't have a %s to toss into the gleep tank.\n", vocab[pnt.id - 1])
                        }
                        return
                    }
                }
                myPrintf("I don't understand what you want me to drop.\n")
                return
            }
        }

        var sw_done = false

        if (tag[V_all]) {
            if (tag[V_stream]) {
                if (n == R_stream) {
                    myPrintf("You fling everything into the stream.\n")
                    destroy_all()
                } else {
                    myPrintf("I see no stream to toss stuff into.\n")
                }
                return
            }
            if (tag[V_billabong]) {
                if ((n == R_stream) || (n == R_slime) || (n == R_billabong)) {
                    myPrintf("You fling everything into the billabong.\n")
                    destroy_all()
                } else {
                    myPrintf("I see no billabong to toss stuff into.\n")
                }
                return
            }
            if (tag[V_river]) {
                if ((n == R_dike) || (n == R_river_edge) || (n == R_river_exit)) {
                    myPrintf("You fling everything into the river.\n")
                    destroy_all()
                } else {
                    myPrintf("I see no river to toss stuff into.\n")
                }
                return
            }

            if (!tag[V_safe]) {
                if (room[n][M_rm_type] != T_looping) {
                    for (pnt in objectList) {
                        if ((pnt.location == B_have) && ((!tag[V_treasure]) || (pnt.value > 0))) {
                            sw_done = true
                            if ((pnt.id == V_cap) && (verb != V_slow_drop)) {
                                cap_drop()
                                return
                            }
                            if ((pnt.id == V_dynamite) && (Dynamite.type == Z_alias) && (verb != V_slow_drop)) {
                                dynamite_drop()
                                return
                            }
                            room[n][M_obj_cnt]++
                            carry_count--
                            carry_weight -= pnt.weight
                            pnt.location = n
                            if (pnt.value > 0) {
                                if ((n in 22..41) || (n in 144..146)) {
                                    sw_valuable = true
                                }
                            }
                        }
                    }
                    if ((gleep_count > 0) && (!tag[V_treasure])) {
                        room[n][M_gleep] += gleep_count
                        gleep_count = 0
                        sw_done = true
                        gleep_drop = true
                    }

                    if (sw_done) {
                        myPrintf("Done\n")
                        objlooker(n)
                        gleeper(n)
                    } else {
                        myPrintf("Don't have it to drop.\n")
                    }
                    return
                } else {
                    myPrintf("You dropped everything you had in a heap, which \n")
                    myPrintf("promptly vaporized into a bright blue flash followed \n")
                    myPrintf("by a low \"BOOM\". \n")
                    myPrintf("       --- You've blown it Bozo!! ---\n")
                    destroy_all()
                    return
                }
            } else {
                for (pnt in objectList) {
                    if ((pnt.location == B_have) && ((!tag[V_treasure]) || (pnt.value > 0))) {
                        pnt.location = B_in_safe
                        carry_count--
                        carry_weight -= pnt.weight
                        sw_done = true
                    }
                }
                if (!tag[V_treasure]) {
                    gleep_safe += gleep_count
                    gleep_count = 0
                    sw_done = true
                }
                if (sw_done) {
                    myPrintf("Done \n")
                } else {
                    myPrintf("You don't have it to put in the safe!\n")
                }
                return
            }
        }

        for (pnt in objectList) {
            if (tag[pnt.id]) {
                if (pnt.id == V_rifle) continue
                if (pnt.location == B_have) {
                    if (tag[V_safe]) {
                        pnt.location = B_in_safe
                        myPrintf("The %s is now inside the safe. \n", vocab[pnt.id - 1])
                        sw_done = true
                        carry_count--
                        carry_weight -= pnt.weight
                        continue
                    }

                    if (tag[V_river] || tag[V_billabong] || tag[V_stream]) {
                        if (tag[V_stream]) {
                            if (n == R_stream) {
                                myPrintf("You fling the %s into the stream.\n", vocab[pnt.id - 1])
                                carry_count--
                                carry_weight -= pnt.weight
                                pnt.location = B_destroyed
                            } else {
                                myPrintf("I see no stream to toss it into.\n")
                            }
                            return
                        }
                        if (tag[V_billabong]) {
                            if ((n == R_stream) || (n == R_slime) || (n == R_billabong)) {
                                myPrintf("You fling the %s into the billabong.\n", vocab[pnt.id - 1])
                                carry_count--
                                carry_weight -= pnt.weight
                                pnt.location = B_destroyed
                            } else {
                                myPrintf("I see no billabong to toss it into.\n")
                            }
                            return
                        }
                        if (tag[V_river]) {
                            if ((n == R_dike) || (n == R_river_edge) || (n == R_river_exit)) {
                                myPrintf("You fling the %s into the river.\n", vocab[pnt.id - 1])
                                carry_count--
                                carry_weight -= pnt.weight
                                pnt.location = B_destroyed
                            } else {
                                myPrintf("I see no river to toss it into.\n")
                            }
                            return
                        }
                    } else if (room[n][M_rm_type] != T_looping) {
                        if ((pnt.id == V_cap) && (verb != V_slow_drop)) {
                            cap_drop()
                            return
                        }
                        if ((pnt.id == V_dynamite) && (verb != V_slow_drop) && (Dynamite.type == Z_alias)) {
                            dynamite_drop()
                            return
                        }

                        if (pnt.id == V_rifle) {
                            if ((clip_flag != F_no_clip) && (Rifle.status > 0)) {
                                when (rifle_flag) {
                                    F_safety -> {}
                                    F_single -> {
                                        myPrintf("\nBam!\n")
                                        dropped_gun()
                                    }

                                    F_triple -> {
                                        myPrintf("\n")
                                        if (Rifle.status >= 3) {
                                            myPrintf("Bam! Bam! Bam! \n\n")
                                        } else {
                                            for (k in 1..Rifle.status) myPrintf("Bam! ")
                                        }
                                        dropped_gun()
                                    }

                                    F_auto -> {
                                        myPrintf("\n")
                                        if (Rifle.status >= 30) {
                                            for (k in 1..3) {
                                                myPrintf("Bam! Bam! Bam! Bam! Bam! Bam! Bam! Bam! Bam! Bam! \n")
                                            }
                                        } else {
                                            val i_10 = Rifle.status / 10
                                            val i_fract = Rifle.status - (i_10 * 10)
                                            for (k in 1..i_10) {
                                                myPrintf("Bam! Bam! Bam! Bam! Bam! Bam! Bam! Bam! Bam! Bam! \n")
                                            }
                                            for (k in 1..i_fract) myPrintf("Bam! ")
                                        }
                                        dropped_gun()
                                    }
                                }
                            }
                        }

                        carry_count--
                        carry_weight -= pnt.weight
                        room[n][M_obj_cnt]++
                        pnt.location = n
                        sw_done = true
                        if (pnt.value > 0) {
                            if ((n in 22..41) || (n in 144..146)) {
                                sw_valuable = true
                            }
                        }
                        continue
                    } else {
                        myPrintf("As the %s left your possession there was a bright \n", vocab[pnt.id - 1])
                        myPrintf("blue flash, followed by a low \"BOOM\" as it vaporized \n")
                        myPrintf("into nonexistence. \n")
                        carry_count--
                        carry_weight -= pnt.weight
                        pnt.location = B_destroyed
                        return
                    }
                }
                if (pnt.location == B_unmade) continue
            }
        }

        if (sw_done) {
            myPrintf("Done \n")
            objlooker(n)
            gleeper(n)
        } else {
            myPrintf("I don't understand what it is I'm supposed to drop.\n")
        }
    }

    private fun objlooker(n: Int) {
        if (((Torch.location == B_have) && Torch.status != 0) || (n < 186) || (n > 202)) {
            if (room[n][M_obj_cnt] == 1) {
                for (pnt in objectList) {
                    if ((pnt.location == n) && (pnt.type != Z_unmovable)) {
                        if (n == R_office_mang) {
                            myPrintf("On the floor you see ")
                            objector(pnt)
                            myPrintf(".\n")
                        } else {
                            myPrintf("You see ")
                            objector(pnt)
                            myPrintf(".\n")
                        }
                    }
                }
            }
            if (room[n][M_obj_cnt] > 1) {
                if (n == R_office_mang) {
                    myPrintf("You see the following on the floor: \n")
                } else {
                    myPrintf("You see the following: \n")
                }
                for (pnt in objectList) {
                    if ((pnt.location == n) && (pnt.type != Z_unmovable)) {
                        myPrintf("        ")
                        objector(pnt)
                        myPrintf("\n")
                    }
                }
            }
        }
    }

    private suspend fun kelly(n: Int) {
        if (Ned.location == B_destroyed) return

        var sw_ned_look = false
        var sw_ned_set = false

        when (n) {
            R_office_mang -> {
                if (Safe.status != S_open) return

                var score_in_office = 0
                for (pnt in objectList) {
                    if ((pnt.location == B_in_safe) || (pnt.location == B_have) || (pnt.location == R_office_mang)) {
                        score_in_office += pnt.value
                    }
                }

                if (score_in_office >= 1600) {
                    myPrintf("\nYou hear behind you the sound of someone walking into the\n")
                    myPrintf("manager's office.  You then hear the ominous \"click\" of a hammer\n")
                    myPrintf("on a double barreled shootgun being cocked into firing position.\n")
                    hold_it()
                    myPrintf("You turn around slowly and your mouth falls open at the sight\n")
                    myPrintf("of what you see.  Before you is a strange looking character wearing\n")
                    myPrintf("an iron helmet with thin eye slits which completely covers his\n")
                    myPrintf("head.  He has body armour made of thin sheets of iron that covers\n")
                    myPrintf("his chest and abdomen.  He is pointing a double barreled shootgun\n")
                    myPrintf("straight at your face.\n\n")
                    myPrintf("As you stand frozen in terror, he proceeds to stuff the muzzle of\n")
                    myPrintf("his shotgun into your open mouth and cocks the other hammer.\n\n")
                    myPrintf("At this point you do what any typical Australian would do:\n")
                    myPrintf("You whimper softly, pee in your pants and tremble uncontrollably.\n\n")
                    if (Rifle.location == B_have) {
                        myPrintf("Chortling with derision, the strange looking character snatches the\n")
                        myPrintf("M-16 rifle from your trembling hands, sets the safety and throws\n")
                        myPrintf("your rifle into the far corner of the room.  Next he grabs as much\n")
                        myPrintf("treasure as he can with his free hand and then bolts out of the\n")
                        myPrintf("room as quickly as he came.\n")
                        Rifle.location = R_office_mang
                        rifle_flag = F_safety
                        room[R_office_mang][M_obj_cnt]++
                        carry_count--
                        carry_weight -= Rifle.weight
                    } else {
                        myPrintf("Chortling with derision, the strange looking character grabs as\n")
                        myPrintf("much treasure as he can with his free hand and then bolts out of\n")
                        myPrintf("the room as quickly as he came.\n")
                    }
                    hold_it()

                    var stolen_count = 0
                    for (pnt in objectList) {
                        if ((pnt.id == V_gold) || (pnt.value <= 0)) continue
                        if (pnt.location == B_have) {
                            carry_count--
                            carry_weight -= pnt.weight
                        }
                        if (pnt.location == R_office_mang) {
                            room[R_office_mang][M_obj_cnt]--
                        }
                        pnt.location = R_hideout
                        room[R_hideout][M_obj_cnt]++
                        stolen_count++
                        if (stolen_count >= 3) break
                    }
                    Ned.status = F_stealing
                    Ned.location = R_hideout
                    room[R_hideout][M_monster] = 1
                }
            }

            R_lift_inside -> {
                if ((score > 0) && (Safe.status != S_closed)) {
                    if ((kelly_ned_look > 3) || (Door.status == S_open) || (Door.status == S_kicked) || (Door.status == S_unlocked)) {
                        if (kelly_ned_look > 3) {
                            Door.status = S_kicked
                            room[R_office_entr][2] = R_office_hall
                        }
                        for (pnt in objectList) {
                            if ((pnt.location == B_in_safe) && (pnt.value > 0)) {
                                room[R_hideout][M_obj_cnt]++
                                pnt.location = R_hideout
                                sw_ned_set = true
                                Picture.status = S_open
                            }
                        }
                    } else {
                        kelly_ned_look++
                        sw_ned_look = true
                    }
                }

                if (sw_valuable) {
                    for (pnt in objectList) {
                        if (pnt.value > 0) {
                            for (k in 22..36) {
                                if (pnt.location == k) {
                                    room[R_hideout][M_obj_cnt]++
                                    room[k][M_obj_cnt]--
                                    pnt.location = R_hideout
                                    sw_ned_set = true
                                    sw_valuable = false
                                    break
                                }
                            }
                            for (k in 144..145) {
                                if (pnt.location == k) {
                                    room[R_hideout][M_obj_cnt]++
                                    room[k][M_obj_cnt]--
                                    pnt.location = R_hideout
                                    sw_valuable = false
                                    sw_ned_set = true
                                    break
                                }
                            }
                            for (k in 37..41) {
                                if (pnt.location == k) {
                                    if ((Door.status == S_open) || (Door.status == S_unlocked) || (Door.status == S_kicked)) {
                                        room[R_hideout][M_obj_cnt]++
                                        room[k][M_obj_cnt]--
                                        pnt.location = R_hideout
                                        sw_valuable = false
                                        sw_ned_set = true
                                        break
                                    }
                                    if (kelly_ned_look > 3) {
                                        Door.status = S_kicked
                                        room[R_office_entr][2] = R_office_hall
                                        room[R_hideout][M_obj_cnt]++
                                        room[k][M_obj_cnt]--
                                        pnt.location = R_hideout
                                        sw_valuable = false
                                        sw_ned_set = true
                                    } else {
                                        if (!sw_ned_look) kelly_ned_look++
                                        sw_ned_look = true
                                        break
                                    }
                                    break
                                }
                            }
                        }
                    }
                }

                if (sw_ned_set) {
                    Ned.status = F_stealing
                    Ned.location = R_hideout
                    room[R_hideout][M_monster] = 1
                    sw_ned_set = false
                }
            }

            R_office_entr -> {
                if (Ned.status == F_stealing) {
                    Ned.status = F_asleep
                    myPrintf("There is an odd looking character with an iron bucket on his head and a swag\n")
                    myPrintf("bag over his shoulder running away from the office building towards\nthe open desert. \n\n")
                    return
                }
                if ((kelly_ned_look == 2) && kelly_sw_fiddle && (Door.status != S_open) && (Door.status != S_kicked) && (Door.status != S_unlocked)) {
                    kelly_sw_fiddle = false
                    myPrintf("As you approach the office building's door, you see a\n")
                    myPrintf("strange looking character with a bucket on his head and\n")
                    myPrintf("wearing an iron breast plate.  He is on his knees before\n")
                    myPrintf("the door trying to pick the lock with a bit of coat hanger\n")
                    myPrintf("wire. Suddenly, he turns and sees you coming. With amazing\n")
                    myPrintf("speed, he throws away his wire and runs off towards the\n")
                    myPrintf("open desert.\n\n")
                }
            }
        }
    }

    private fun objector(pnt: ObjectStruct) {
        if (pnt.type == Z_alias) {
            if (pnt.id == V_rifle) {
                myPrintf("an M16 infantry rifle with an ammunition clip inserted")
                return
            }
            if (pnt.id == V_dynamite) {
                myPrintf("a large stick of dynamite with a fuse type blasting cap")
                return
            }
        }
        myPrintf("%s", pnt.text)
    }

    private fun clip_in(ammo_flag: Int) {
        if (Rifle.location != B_have) {
            myPrintf("Galah!!  You don't have a rifle to put an ammo clip into.\n")
            return
        }
        if (clip_flag != F_no_clip) {
            myPrintf("There is already an ammo clip in the rifle. \n")
            return
        }
        if (((Clip.location != B_have) && (ammo_flag == F_normal_clip)) || ((Org_clip.location != B_have) && (ammo_flag == F_org_clip))) {
            myPrintf("You don't possess an ammo clip to put into the rifle.\n")
            return
        }
        Rifle.type = Z_alias
        carry_count--
        carry_weight -= Clip.weight
        if (ammo_flag == F_normal_clip) {
            Clip.location = B_unmade
            clip_flag = F_normal_clip
            Rifle.status = Clip.status
        } else {
            Org_clip.location = B_unmade
            clip_flag = F_org_clip
            Rifle.status = Org_clip.status
        }
        myPrintf("The ammunition clip slides into the rifle with a \"click\".\n")
        myPrintf("You have %d bullets in the clip \n", Rifle.status)
        if (Rifle.status > 0) {
            myPrintf("You cycle the M16's bolt once to chamber a round.\n")
        }
    }

    private fun clip_out(n: Int) {
        if (Rifle.location != B_have) {
            myPrintf("You Bloody Galah!  You don't have a rifle in your possession!\n")
            return
        }
        if (clip_flag == F_no_clip) {
            myPrintf("There is no clip in the rifle. \n")
            return
        }
        Rifle.type = Z_normal
        val ammo_flag = clip_flag
        if (clip_flag == F_normal_clip) {
            Clip.location = n
        } else {
            Org_clip.location = n
        }
        room[n][M_obj_cnt]++
        clip_flag = F_no_clip
        myPrintf("You press the \"eject\" button on the rifle and the magazine falls to the ground.\n")

        if (Rifle.status > 0) {
            myPrintf("You then cycle the M16's bolt once to clear the chamber. The unfired round\n")
            myPrintf("is ejected from the breech and rolls out of sight. \n")
            Rifle.status--
            if (ammo_flag == F_normal_clip) Clip.status = Rifle.status else Org_clip.status = Rifle.status
        } else {
            if (ammo_flag == F_normal_clip) Clip.status = 0 else Org_clip.status = 0
        }
        Rifle.status = 0
    }

    private fun igniter(n: Int) {
        if (tag[V_VERB_ONLY]) {
            myPrintf("What exactly do you want me to set alight?\n")
            return
        }

        if (tag[V_torch]) {
            switcher(n)
            return
        }

        if (!tag[V_match]) {
            myPrintf("What am I to light it with?\n")
            myPrintf("(I am not prepared to rub two sticks together.)\n")
            return
        }
        if (Matches.location != B_have) {
            myPrintf("I have no matches!\n")
            return
        } else {
            if ((!tag[V_cap]) && (!tag[V_dynamite]) && (sent[2] != V_LINE_END)) {
                myPrintf("It would be a fire hazard to set that alight.\n")
                return
            }

            if (tag[V_dynamite] && (Dynamite.location != B_have)) {
                myPrintf("You have no dynamite to ignite.\n")
                return
            }

            if (tag[V_cap] && (Dynamite.location != B_have) && (Cap.location != B_have)) {
                myPrintf("You don't have one to ignite.\n")
                return
            }

            if (tag[V_PLURAL]) {
                myPrintf("You open up the match box and strike ALL of the matches.\n")
                Matches.location = B_destroyed
            } else {
                myPrintf("You take one match from the match box and ignite it.\n")
            }
        }

        if (tag[V_cap] && (Cap.location == B_have)) {
            myPrintf("You then ignite the fuse of the blasting cap.\n\n")
            myPrintf("Ssssssssssssssssssssssss........\n")
            clock_explode = (System.currentTimeMillis() / 1000L) + 11
            sw_clock = true
            flag_clock = V_cap
            return
        }

        if ((tag[V_cap] || tag[V_dynamite]) && (Dynamite.location == B_have)) {
            if (Dynamite.type == Z_alias) {
                myPrintf("You then ignite the fuse of the dynamite's blasting cap....\n")
                myPrintf("You need to very quickly get very far away from this thing!!")
                myPrintf("\n\nSsssssssssssssssssssssss........\n")
                clock_explode = (System.currentTimeMillis() / 1000L) + 11
                sw_clock = true
                flag_clock = V_dynamite
            } else {
                myPrintf("You then ignite the dynamite. Which is an odd thing\n")
                myPrintf("to do since it doesn't have a blasting cap in it.  The\n")
                myPrintf("\"dynamite\" (which is actually a very safe mining explosive)\n")
                myPrintf("burns for a few minutes, billowing out smelly black smoke\n")
                myPrintf("and eventually goes out leaving a few scant ashes behind.\n")
                Dynamite.location = B_destroyed
            }
        }
    }

    private fun unlocker(n: Int) {
        if (tag[V_VERB_ONLY]) {
            myPrintf("What do you want me to unlock? \n")
            return
        }

        if (room[n][M_unmov_obj] != 0) {
            when (n) {
                R_office_mang -> {
                    if (tag[V_safe]) {
                        if (Picture.status == S_open) {
                            myPrintf("This is a combination type safe (no key hole).  You must\n")
                            myPrintf("dial a combination in order to open this safe. \n")
                        } else {
                            myPrintf("I don't see a safe to unlock.\n")
                        }
                    } else {
                        myPrintf("You're wasting your time.  You can't unlock that! \n")
                    }
                }

                R_office_entr -> {
                    if (tag[V_door]) {
                        when (Door.status) {
                            S_open -> {
                                myPrintf("The door is already open! \n")
                                return
                            }

                            S_kicked -> {
                                myPrintf("The door has been kicked in! \n")
                                return
                            }

                            S_unlocked -> {
                                myPrintf("The door is already unlocked.  However it is still closed! \n")
                                return
                            }
                        }
                        if (tag[V_key]) {
                            if (Key.location != B_have) {
                                myPrintf("You don't have the key to this door in your possession! \n")
                            } else {
                                myPrintf("You turn the key in the lock and hear a satisfying \"click\".\n")
                                Door.status = S_unlocked
                            }
                        } else {
                            myPrintf("With what shall I unlock the door? \n")
                        }
                    }
                }

                else -> {
                    myPrintf("I don't think that is the sort of thing one unlocks.\n")
                }
            }
        } else {
            myPrintf("There is nothing here that can be unlocked! \n")
        }
    }

    private fun locker(n: Int) {
        if (tag[V_VERB_ONLY]) {
            myPrintf("What do you want me to lock? \n")
            return
        }

        if (room[n][M_unmov_obj] != 0) {
            when (n) {
                R_office_mang -> {
                    if (tag[V_safe]) {
                        if (Picture.status == S_open) {
                            if (tag[V_key]) {
                                myPrintf("You can't lock a combination safe with a key (no keyhole)!\n")
                                return
                            }
                            when (Safe.status) {
                                S_open -> {
                                    myPrintf("You close the safe and it locks with a \"click\". \n")
                                }

                                S_dialed -> {
                                    myPrintf("You give the safe's dial a twist, locking the safe. \n")
                                }

                                else -> {
                                    myPrintf("The safe is already locked!\n")
                                    return
                                }
                            }
                            Safe.status = S_closed
                        } else {
                            myPrintf("I don't see a safe to lock.\n")
                        }
                    } else {
                        myPrintf("You're wasting your time.  You can't lock that! \n")
                    }
                }

                R_office_entr -> {
                    if (tag[V_door]) {
                        if (tag[V_key]) {
                            if (Key.location != B_have) {
                                myPrintf("You don't have the key to this door in your possession! \n")
                                return
                            }
                        } else {
                            myPrintf("With what shall I lock the door? \n")
                            return
                        }

                        when (Door.status) {
                            S_open -> {
                                myPrintf("You close the door and turn the key in the lock, locking the door.\n")
                            }

                            S_kicked -> {
                                myPrintf("The door has been kicked in! It'll never be locked again.\n")
                                return
                            }

                            S_unlocked -> {
                                myPrintf("You turn the key in the lock, locking the door.\n")
                            }
                        }
                        room[R_office_entr][2] = R_WALL
                        Door.status = S_closed
                    }
                }

                else -> {
                    myPrintf("I don't think that is the sort of thing one locks.\n")
                }
            }
        } else {
            myPrintf("There is nothing here that can be locked! \n")
        }
    }

    private fun loader() {
        if (tag[V_VERB_ONLY]) {
            myPrintf("What exactly do you want me to load?\n")
            return
        }
        if (!tag[V_rifle]) {
            myPrintf("I don't know how I could load that.\n")
            return
        }
        var ammo_flag = F_no_clip
        if (!tag[V_clip]) {
            if ((Clip.location != B_have) && (Org_clip.location == B_have)) ammo_flag = F_org_clip
            if ((Clip.location == B_have) && (Org_clip.location != B_have)) ammo_flag = F_normal_clip
        } else {
            if (tag[V_orange]) {
                if (Org_clip.location == B_have) {
                    ammo_flag = F_org_clip
                } else {
                    myPrintf("You don't possess the orange ammunition clip.\n")
                    return
                }
            } else {
                if (Clip.location == B_have) {
                    ammo_flag = F_normal_clip
                } else {
                    if (Org_clip.location == B_have) {
                        ammo_flag = F_org_clip
                    } else {
                        myPrintf("You don't possess an ammunition clip to load.\n")
                        return
                    }
                }
            }
        }
        clip_in(ammo_flag)
    }

    private fun unloader(n: Int) {
        if (tag[V_VERB_ONLY]) {
            myPrintf("What exactly do you want me to unload?\n")
            return
        }
        if (!tag[V_rifle]) {
            myPrintf("I don't know how I could unload that.\n")
            return
        }
        clip_out(n)
    }

    private fun eater() {
        if (tag[V_VERB_ONLY]) {
            myPrintf("What exactly do you want me to swallow?\n")
            return
        }
        if (tag[V_pill]) {
            if (Pills.location != B_have) {
                myPrintf("You don't have the atropine pills in your possesion!\n")
                return
            }
            if (tag[V_PLURAL]) {
                myPrintf("You remove ALL of the pills from the packet and swallow\n")
                myPrintf("the lot.  For a couple of minutes nothing happens, then you\n")
                myPrintf("start seeing double and have nervous twitches. You then\n")
                myPrintf("start shaking violently and collapse to the ground.\n")
                myPrintf("Eventually you pass out and expire from atropine poisoning.\n")
                ender(F_died)
            }
            pill_count--
            if (pill_count <= 0) {
                Pills.location = B_destroyed
                carry_count--
                carry_weight -= Pills.weight
                myPrintf("You swallow the last atropine pill and throw away the empty\n")
                myPrintf("packet which disappears from sight.  ")
            } else {
                myPrintf("You swallow one atropine pill.  ")
            }
            i_poison += 10
            if (i_poison >= 30) {
                myPrintf("\nAfter a moment you have a violent seizure and die.\n")
                ender(F_died)
            }
            if (i_poison >= 20) {
                myPrintf("\nYou begin to shake violently and can barely stand.\n")
                return
            }
            if (i_poison >= 15) {
                myPrintf("\nYour hands become a bit jittery and your vision blurred.\n")
                return
            }
            myPrintf("The drug has no ill effect.\n")
        } else {
            myPrintf("I am not inclined towards putting that into my mouth!\n")
        }
    }

    private fun filler() {
        if (tag[V_VERB_ONLY]) {
            myPrintf("Fill what?\n")
            return
        }
        if (tag[V_can]) {
            if (Can.location != B_have) {
                myPrintf("You don't have the Fourex can in your possession!\n")
            } else {
                myPrintf("I can't do it!  There's a hole in the can't bottom.\n")
            }
            return
        }
        if (tag[V_bottle]) {
            if (Bottle.location != B_have) {
                myPrintf("You don't have the bottle in your possession!\n")
            } else {
                myPrintf("I can't do it!  The bottle has a crack in it's bottom.\n")
            }
            return
        }
        myPrintf("I can't fill that!\n")
    }

    private suspend fun looker(n: Int) {
        if (tag[V_VERB_ONLY]) {
            if ((room[n][M_rm_type] == T_was_long) || (n == R_lift_inside)) {
                long_descp(n)
            } else {
                describe(n)
            }
            objlooker(n)
            gleeper(n)
            if (room[n][M_unmov_obj] != 0) {
                actor(n)
            }
            return
        }

        if (tag[V_tank]) {
            if (n == R_gleep_tank) {
                myPrintf("You look inside the gleep tank and see a blue fluid which\n")
                myPrintf("smells of chlorine")
                if (Tank.status == 0) {
                    myPrintf(".\n")
                    return
                }
                if (Tank.status == 1) {
                    myPrintf(" and a single gleep submerged in the fluid.\n")
                    return
                }
                myPrintf(" and %d gleeps submerged in the fluid.\n", Tank.status)
                return
            } else {
                myPrintf("There is no gleep tank here!\n")
                return
            }
        }

        var sw_no_see = false
        for (i in 0 until Read_objcnt) {
            val point = read_object[i]
            val m = point.id
            if (tag[m]) {
                val pnt = pointToObject(m)
                if (pnt.type == Z_unmovable) {
                    if (pnt.location == n) {
                        rdtxt(m)
                        return
                    } else {
                        sw_no_see = true
                    }
                } else {
                    if (pnt.location == B_have) {
                        rdtxt(m)
                    } else {
                        myPrintf("I can examine an object only if it is in my possession.\n")
                    }
                    return
                }
            }
        }
        if (sw_no_see) {
            myPrintf("I don't see it here.\n")
            return
        }
        myPrintf("There is nothing more that I can describe about it.\n")
    }

    private suspend fun actor(n: Int) {
        when (n) {
            R_lift_entr -> {
                when (Lift_door.status) {
                    S_closed -> {
                        myPrintf("The lift doors are closed and the lift call button is not glowing. It \nseems the electrical power has been turned off at the main switch. \n")
                    }

                    S_flashing -> {
                        myPrintf("The lift doors are closed.  However the call button is flashing. There \nis the sound of electrical equipment humming within the mine lift. \n")
                    }

                    S_open -> {
                        myPrintf("The lift doors are standing open. \n")
                    }
                }
            }

            R_office_entr -> {
                when (Door.status) {
                    S_open -> {
                        myPrintf("      --- The office front door is open. --- \n")
                    }

                    S_kicked -> {
                        if (!actor_sw_door_kick) {
                            myPrintf("The lock of the front door has been shot at several times.\n")
                            myPrintf("The door itself has been kicked in and is broken off at\n")
                            myPrintf("the hinges.  The passage way is free to enter.\n")
                            actor_sw_door_kick = true
                        } else {
                            myPrintf("The front door of the office has been kicked in. \n")
                        }
                    }

                    else -> {
                        myPrintf("The office has a front door with a sun faded sign \nupon which is written:  \"Sorry, We are CLOSED\". \n")
                        myPrintf("      --- The office door is shut. --- \n")
                    }
                }
            }

            R_office_mang -> {
                if (Picture.status == S_closed) {
                    myPrintf("On the wall is a picture of a platypus wearing a hat with corks \ndangling from the hat's brim.\n")
                } else if ((Safe.status != S_open) && (Picture.status == S_open)) {
                    myPrintf("Before you is a picture hinged to the wall that has been swung \nopen revealing a closed combination dial wall safe.\n")
                } else if ((Safe.status == S_open) && (Picture.status == S_open)) {
                    myPrintf("Before you is a picture hinged to the wall that has been swung \nout revealing a wall safe with an open door.\n")
                    var sw_found = false
                    for (obj in objectList) {
                        if (obj.location == B_in_safe) {
                            sw_found = true
                            myPrintf("You see the following inside the safe: \n")
                            for (o2 in objectList) {
                                if (o2.location == B_in_safe) {
                                    myPrintf("        ")
                                    objector(o2)
                                    myPrintf("\n")
                                }
                            }
                            if (gleep_safe != 0) {
                                myPrintf("        ")
                                if (gleep_safe == 1) {
                                    myPrintf("one gleep\n")
                                } else {
                                    myPrintf("%d gleeps\n", gleep_safe)
                                }
                            }
                            break
                        }
                    }
                    if (!sw_found) {
                        if (gleep_safe != 0) {
                            myPrintf("There is nothing in the safe except ")
                            if (gleep_safe == 1) {
                                myPrintf("a single gleep.\n")
                            } else {
                                myPrintf("%d gleeps.\n", gleep_safe)
                            }
                        } else {
                            myPrintf("---The safe is empty.--- \n")
                        }
                    }
                }
            }

            R_store_room -> {
                myPrintf("There is a 1500 Volt circuit breaker box on the wall")
                if (Circuit_breaker.status != 0) {
                    myPrintf(" which has \na switch set in the \"on\" position. \n")
                } else {
                    myPrintf(" which has \na switch set in the \"off\" position. \n")
                }
            }

            R_lift_inside -> {
                myPrintf("The level button with the number ")
                when (Lift.status) {
                    L0 -> myPrintf("zero")
                    L49 -> myPrintf("forty-nine")
                    L67 -> myPrintf("sixty-seven")
                    L82 -> myPrintf("eighty-two")
                }
                myPrintf(" is flashing. \n")
            }
        }
    }

    private suspend fun sounder(n: Int) {
        if (n != R_gong) {
            if (tag[V_gong]) {
                myPrintf("I don't see a gong here.\n")
            } else {
                myPrintf("I don't see how you can do that.\n")
            }
            return
        }

        if (tag[V_clapper]) {
            if (Clapper.location == B_have) {
                if (!tag[V_gong]) {
                    myPrintf("With what are you going to do that with the clapper?\n")
                    return
                }
                myPrintf("\n  GGGGGGGG       OOOOOOOO     NN       NN     GGGGGGGG     !!!\n")
                myPrintf("GG        GG   OO        OO   NNN      NN   GG        GG   !!!\n")
                myPrintf("GG             OO        OO   NN N     NN   GG             !!!\n")
                myPrintf("GG             OO        OO   NN  N    NN   GG             !!!\n")
                myPrintf("GG   GGGGGGG   OO        OO   NN   N   NN   GG    GGGGGG   !!!\n")
                myPrintf("GG        GG   OO        OO   NN    N  NN   GG        GG   !!!\n")
                myPrintf("GG        GG   OO        OO   NN     N NN   GG        GG\n")
                myPrintf("GG        GG   OO        OO   NN      NNN   GG        GG   000\n")
                myPrintf("  GGGGGGGG       OOOOOOOO     NN       NN     GGGGGGGG     000\n\n")

                if (Gong.status == S_recorder) {
                    myPrintf("You hear an ethereal voice which says:\n\n")
                    myPrintf("Sorry Mate!  You used the bloody data recorder which gave you\n")
                    myPrintf("an unfair advantage.  If you want to be admitted into the final\n")
                    myPrintf("part of Dinkum and have a chance at winning then you must find\n")
                    myPrintf("all the treasure, come back here and sound the gong WITHOUT having\n")
                    myPrintf("started Dinkum with the command switch \"-S\" (NO data recorder).\n\n")
                    myPrintf("You did however put in a good effort.  Better luck next time!\n")
                    Gong.status = S_told
                    return
                }
                if (Gong.status == S_told) {
                    myPrintf("Nothing happened.\n")
                    return
                }

                if ((score < max_score) || (Tank.status == 0)) {
                    myPrintf("You hear an ethereal voice which says:\n\n")
                    if (score < max_score) {
                        myPrintf("Sorry Mate!  You haven't put all of the treasure in the\n")
                        myPrintf("safe yet.  Find the rest, and then the secret of the gong\n")
                        myPrintf("will be revealed.\n")
                        return
                    }
                    if (Tank.status == 0) {
                        myPrintf("Good on you Mate!  You've found all of the treasure.\n")
                        myPrintf("However you haven't put a single gleep in the gleep tank!\n")
                        myPrintf("You're not a Fair Dinkum Adventurer until you've put at\n")
                        myPrintf("least one gleep in the tank.\n")
                        return
                    }
                }

                myPrintf("There is a loud \"Fromp!\" as a portcullis crashes down and\n")
                myPrintf("seals the northern (and only) exit.  The lights go dim and\n")
                myPrintf("a hazy fog appears in the room.  Slowly this fog coalesces\n")
                if (sw_warned) {
                    myPrintf("into the now familiar form of Banjo Patterson.  For some\n")
                    myPrintf("odd reason Banjo is holding in his right hand a sword of\n")
                    myPrintf("fire such as one reads about in Genesis. The deathly quiet\n")
                    myPrintf("is broken when Banjo begins to speak:\n")
                } else {
                    myPrintf("into a ghostly human form.  This ethereal being is wearing\n")
                    myPrintf("a grey flannel suit, a bowler hat, and has a gold pocket\n")
                    myPrintf("watch chain looped across the front of his waist coat.  He\n")
                    myPrintf("is holding in his right hand a sword of fire such as one\n")
                    myPrintf("reads about in Genesis.  You can hear \"Waltzing Matilda\"\n")
                    myPrintf("being played softly in the background.  You know what?\n")
                    myPrintf("I think this ethereal being is none other than the ghost\n")
                    myPrintf("of Banjo Patterson!  With this realization Banjo begins to\n")
                    myPrintf("speak:\n")
                }
                hold_it()
                myPrintf("\"Good on you, Fair Dinkum Adventurer!  You have braved\n")
                myPrintf("the perils of hoop snakes, drop bears, and mutant wombats.\n")
                myPrintf("You have solved many riddles, collected much treasure and\n")
                myPrintf("encountered true evil and dealt with it appropriately.\n")
                myPrintf("However it has occured to us that you might not be one of\n")
                myPrintf("the chosen few who can be admitted into the Land of the\n")
                myPrintf("Blessed.  The possibility remains that you could be a\n")
                myPrintf("whinging Pom or a bleeding Yank.  Therefore you will be\n")
                myPrintf("tested with three questions.  If you answer correctly, you\n")
                myPrintf("will be admitted into paradise. However should you answer\n");
                myPrintf("falsely, you will be sent to another place, an abode of\n")
                myPrintf("darkness and dread.  So answer wisely, for you will not be\n")
                myPrintf("given a second chance!\"\n\n")

                myPrintf("What is the capital of Australia?\n\n")
                myPrintf("     A) Sydney\n")
                myPrintf("     B) Canberra\n")
                myPrintf("     C) Melbourne\n\n")
                myPrintf("Answer A, B, or C:  ")

                var letter = ' '
                while (letter == '\n' || letter == ' ') {
                    letter = myGetchar()
                }
                if (letter == 'b' || letter == 'B') {
                    myPrintf("\nCorrect!\n\n")
                } else {
                    los_angeles()
                    return
                }

                myPrintf("In the Australian emblem there appears two animals on\n")
                myPrintf("either side of a shield.  One of the two animals is a\n")
                myPrintf("kangaroo.  What species is the other animal?\n\n")
                myPrintf("     A) Koala Bear\n")
                myPrintf("     B) Platypus\n")
                myPrintf("     C) Emu\n\n")
                myPrintf("Answer A, B, or C:  ")

                letter = ' '
                while (letter == '\n' || letter == ' ') {
                    letter = myGetchar()
                }
                if (letter == 'c' || letter == 'C') {
                    myPrintf("\nCorrect!\n\n")
                } else {
                    los_angeles()
                    return
                }

                myPrintf("Which place is a state in Australia?\n\n")
                myPrintf("     A) Arcadia\n")
                myPrintf("     B) Patagonia\n")
                myPrintf("     C) Tasmania\n\n")
                myPrintf("Answer A, B, or C:  ")

                letter = ' '
                while (letter == '\n' || letter == ' ') {
                    letter = myGetchar()
                }
                if (letter == 'c' || letter == 'C') {
                    myPrintf("\nCorrect!\n\n")
                } else {
                    los_angeles()
                    return
                }

                myPrintf("You see Banjo Patterson with a beatific smile.  He waves\n")
                myPrintf("his firey sword with a florish and the scene around you\n")
                myPrintf("dissolves into a million tiny motes of light.  You now\n")
                myPrintf("find yourself wearing swimming togs.  You are being carried\n")
                myPrintf("by a guard of honour made up of four sumptuous shielas\n")
                myPrintf("dressed in string bikinis and four handsome lifeguards.\n")
                myPrintf("They carry you to a perfect beach on South Stradbroke\n")
                myPrintf("Island.  Off in the distance on the Queensland mainland\n")
                myPrintf("you see the Gold Coast which vaguely resembles the Emerald\n")
                myPrintf("City of the classic movie.  Your guard of honour sets you\n")
                myPrintf("down and hands you a can of Power's Bitter.  Three\n")
                myPrintf("beautiful white pelicans float over head in a clear blue\n")
                myPrintf("sky completing a scene of exquisite beauty.\n\n")
                myPrintf("You're in paradise Mate and you've also won the game!\n\n")

                if (Tank.status == 1) {
                    myPrintf("---- A hint for those who want to go on playing Dinkum ----\n")
                    myPrintf("In this game you only put the minimum single gleep in the\n")
                    myPrintf("gleep tank.  In future games try seeding fertile tunnels\n")
                    myPrintf("with gleeps and reproducing them.  Build up as many gleeps\n")
                    myPrintf("as you can and then hit the gong before Dinkum times out.\n")
                } else {
                    myPrintf("You won this game with %d gleeps in the gleep tank.\n\n", Tank.status)
                }
                if (sw_wizard) {
                    return
                } else {
                    inputQueue.clear()
                }
            } else {
                myPrintf("I don't have a clapper to hit it with! \n")
            }
        } else {
            myPrintf("\nThud!\n\n")
            myPrintf("Nothing happened.\n")
            myPrintf("I think you'd have more success if you used a gong clapper.\n")
        }
    }

    private fun los_angeles() {
        myPrintf("\n        -!-!-!-!-!-     Wrong!    -!-!-!-!-!-\n\n")
        myPrintf("Banjo Patterson brings down the sword of fire onto your\n")
        myPrintf("head. The scene around you disappears in a flash and you\n")
        myPrintf("find yourself transported to.....\n\n")
        myPrintf("             Pico Blvd., West Los Angeles\n\n")
        myPrintf("L.A. is having a stage three smog alert.  The air is so\n")
        myPrintf("foul you can taste it.  A Los Angeles municipal bus roars\n")
        myPrintf("by covering you in soot.  Across the street you see a\n")
        myPrintf("modern day Neanderthal selling vials of Crack to children.\n")
        myPrintf("The person next to you has taken a definite physical\n")
        myPrintf("interest in you.  She/he is wearing a short skirt and has a\n")
        myPrintf("beehive hairdo.  He/she also has a five o'clock shadow, and\n")
        myPrintf("bicepts so thick that he could collapse your skull with a\n")
        myPrintf("single thump.\n\n")
        myPrintf("We now leave this unhappy scene.  Since you were sent to\n")
        myPrintf("Los Angeles your score has been zeroed.\n\n")
        myPrintf("So ends yet another unsuccessful session at Dinkum!\n")

        if (sw_wizard) {
            return
        } else {
            inputQueue.clear()
        }
    }

    private fun cap_drop() {
        myPrintf("Bang!! The blasting cap you were carrying detonated when\n")
        myPrintf("it hit the ground.  Fortunately no damage was caused.\n")
        Cap.location = B_destroyed
        carry_count--
        carry_weight -= Cap.weight
    }

    private fun dynamite_drop() {
        boom()
        myPrintf("Dropping a stick of dynamite with a blasting cap in it\n")
        myPrintf("ranks high as one of the dumbest things a person can do.\n")
        myPrintf("Needless to say you were blown to bits. Next time drop\n")
        myPrintf("the dynamite slowly or gently.\n")
        ender(F_died)
    }

    private fun destroy_all() {
        gleep_count = 0
        carry_count = 0
        carry_weight = 0
        for (obj in objectList) {
            if (obj.location == B_have) obj.location = B_destroyed
        }
    }

    private fun dropped_gun() {
        myPrintf("\n")
        myPrintf("Dropping a loaded and armed automatic rifle is a stupid way\n")
        if ((Rifle.status == 1) || (rifle_flag == F_single)) {
            myPrintf("to commit suicide.  Needless to say you were hit by the\n")
            myPrintf("bullet after the rifle went off.\n")
        } else {
            myPrintf("to commit suicide.  The bullets shot from the rifle fly\n")
            myPrintf("around.  One of them bounces back and hits you!\n")
        }
        ender(F_died)
    }

    private fun the_fork() {
        myPrintf("If you could see me now then you'd see me giving you a well\n")
        myPrintf("known Australian finger sign called \"The Fork\".  This is\n")
        myPrintf("similar to the \"V\" for victory sign but has an entirely\n")
        myPrintf("different meaning which I trust you understand.\n")
    }

    private fun shoot_it(): Int {
        if (Rifle.location != B_have) {
            myPrintf("I would do that.  Only there is one small problem.... \n")
            myPrintf("I don't have a rifle in my possession! \n")
            return 0
        }

        myPrintf("You pull the trigger... \n\n")

        if (clip_flag == F_no_clip) {
            myPrintf("Nothing happens!  \n")
            myPrintf("Your rifle doesn't have an ammunition clip in it.\n")
            return 0
        }

        if (Rifle.status <= 0) {
            myPrintf("Nothing happens!  The ammo clip is out of bullets.\n")
            return 0
        }

        var hits = 0
        when (rifle_flag) {
            F_safety -> {
                myPrintf("Nothing happens!  The rifle's safety is still on.\n")
                return 0
            }

            F_single -> {
                myPrintf("Bam! \n\n")
                Rifle.status--
                hits = if (clip_flag == F_normal_clip) 1 else 100
            }

            F_triple -> {
                if (Rifle.status >= 3) {
                    myPrintf("Bam! Bam! Bam! \n\n")
                    Rifle.status -= 3
                    hits = if (clip_flag == F_normal_clip) 3 else 300
                } else {
                    for (i in 1..Rifle.status) myPrintf("Bam! ")
                    myPrintf("\n\nYou've run out of bullets. \n\n")
                    hits = if (clip_flag == F_normal_clip) Rifle.status else Rifle.status * 100
                    Rifle.status = 0
                }
            }

            F_auto -> {
                if (Rifle.status >= 30) {
                    for (i in 1..3) {
                        myPrintf("Bam! Bam! Bam! Bam! Bam! Bam! Bam! Bam! Bam! Bam! \n")
                    }
                    Rifle.status -= 30;
                    hits = 30
                } else {
                    val i_10 = Rifle.status / 10
                    val i_fract = Rifle.status - (i_10 * 10)
                    for (i in 1..i_10) {
                        myPrintf("Bam! Bam! Bam! Bam! Bam! Bam! Bam! Bam! Bam! Bam! \n")
                    }
                    for (i in 1..i_fract) myPrintf("Bam! ")
                    myPrintf("\n\nYou've run out of bullets. \n")
                    hits = if (clip_flag == F_normal_clip) Rifle.status else Rifle.status * 100
                    Rifle.status = 0
                }
                myPrintf("\n")
            }
        }
        return hits
    }

    private fun killer(n: Int) {
        if (tag[V_self]) {
            the_fork()
            return
        }

        if (verb == V_kill) {
            if (tag[V_VERB_ONLY]) {
                myPrintf("It may be obvious to you, but you are going to have to tell\n")
                myPrintf("me exactly what it is you want to have killed, and \n")
                myPrintf("with what sort of weapon. \n")
                return
            }

            if (!tag[V_rifle]) {
                if (tag[V_dynamite]) {
                    if (Dynamite.location == B_have) {
                        myPrintf("You'll find the dynamite is not all that formidable of a weapon.\n")
                        myPrintf("Try lighting some with a match and you'll see why.\n")
                    } else {
                        myPrintf("You silly gallah! You don't have any dynamite in your possession!\n")
                    }
                } else {
                    myPrintf("What weapon am I suppose to use? \n")
                }
                return
            }
        }

        var sw_other_object = false

        if (tag[V_ned]) {
            if (Ned.location == B_destroyed) {
                myPrintf("Forget it, you drongo!! Ned Kelly is already dead!\n")
                return
            }
            if (Ned.status == F_asleep) {
                myPrintf("There is no Ned Kelly here for me to kill! \n")
                return
            }

            val hits = shoot_it()
            if (hits == 0) return

            myPrintf("Ned Kelly keels over dead.  His body suddenly glows a bright orange like it\nis being consummed by a heatless flame and then the late Ned Kelly disappears\ninto a puff of blue smoke. \n\n")
            myPrintf("By the way, the recently deceased bushranger ***was*** wearing iron body\narmour.  However thin sheets of iron would just barely work against nineteenth\ncentury firearms at long range.  Against a modern infantry rifle, Ned's\narmour offered about as much protection as a dunny paper bandage. \n")
            Ned.location = B_destroyed
            Ned.status = F_asleep
            monster_flag = F_no_monster
            room[R_hideout][M_monster] = 0

            room[R_hideout][M_obj_cnt]++
            Teapot.location = R_hideout
            return
        }

        if (tag[V_bear]) {
            var mnstr: MonsterStruct? = null
            for (j in 2..5) {
                val m = monster_start[j]
                if (m.status != F_asleep) {
                    mnstr = m
                    break
                }
            }
            if (mnstr == null) {
                myPrintf("I see no drop bear for me to kill! \n")
                return
            }

            val hits = shoot_it()
            if (hits == 0) return

            if (mnstr.hits == 0) killer_sw_hurt = false
            mnstr.hits += hits

            if (mnstr.hits < 30) {
                if (killer_sw_hurt && (mnstr.hits >= 10)) {
                    myPrintf("The drop bear has been shot at so many times, that it has gone mad\nwith rage.  It leaps three metres and rips out your throat with a\nsingle swish of its claws.  You die instantly. \n")
                    ender(F_died)
                }

                if (mnstr.hits == 1) {
                    myPrintf("Your bullet hit the drop bear causing it to howl in anguish.  However drop\nbears are pretty tough and just one bullet isn't going to kill it.\n")
                } else {
                    if (!killer_sw_hurt) {
                        myPrintf("You've hit the drop bear and it is bleeding fairly heavily.\n")
                        myPrintf("Unfortunately it is still alive and kicking and wants your blood \nin payment for its.\n")
                        killer_sw_hurt = true
                    } else {
                        myPrintf("You've hit the drop bear again.  It is weakening but still alive.\n")
                    }
                }
                monster_flag = F_wounded
                return
            }

            if (rifle_flag == F_single) {
                myPrintf("That last bullet was the straw which broke the drop bear's back.\nThe blasted thing is finally dead! \n")
            } else {
                myPrintf("The bullets riddled the drop bear with holes, killing it instantly.\n")
            }
            myPrintf("Suddenly the drop bear's corpse turns into a cloud of greasy blue smoke,\nwhich floats away without a trace.\n")
            killer_sw_hurt = false
            mnstr.location = B_destroyed
            mnstr.status = F_asleep
            monster_flag = F_no_monster
            room[n][M_monster] = 0
            return
        }

        if (tag[V_wombat]) {
            if (Wombat.status == F_asleep) {
                myPrintf("I see no wombat for me to kill!\n")
                return
            }

            val hits = shoot_it()
            if (hits == 0) return

            if (Wombat.hits == 0) killer_sw_hurt = false
            Wombat.hits += hits

            if (Wombat.hits < 300) {
                if (Wombat.hits == 1) {
                    myPrintf("Your bullet hit the wombat causing it some minor discomfort.  Judging from its \nbehavior, killing this beast is going to be tough! \n")
                } else {
                    if (!killer_sw_hurt) {
                        myPrintf("You've hit the wombat and got its attention but you've not\nseriously wounded it.  In fact, you've made it more fierce than before.\n")
                        killer_sw_hurt = true
                    } else {
                        myPrintf("You've hit the wombat again, but it is still going strong.\n")
                    }
                }
                monster_flag = F_wounded
                return
            }

            if (rifle_flag == F_single) {
                myPrintf("That last bullet was the straw which broke the wombat's back.\nThe blasted thing is finally dead!\n\n")
            } else {
                myPrintf("The bullets did the trick on the wretched thing.  It's dead as a doornail.\n\n")
            }
            myPrintf("Suddenly the wombat's corpse starts to glow with an intense white light.\nThere is then a crackling sound as its body starts to burn.  You can\nsmell the stench of burning hair.  Then the white light begins to dim,\nleaving no trace left of the once formidable monster.\n")
            killer_sw_hurt = false
            Wombat.location = B_destroyed
            Wombat.status = F_asleep
            monster_flag = F_no_monster
            room[n][M_monster] = 0
            return
        }

        if (tag[V_snake]) {
            var mnstr: MonsterStruct? = null
            for (j in 6..13) {
                val m = monster_start[j]
                if (m.status != F_asleep) {
                    mnstr = m
                    break
                }
            }
            if (mnstr == null) {
                myPrintf("I see no hoop snake for me to kill! \n")
                return
            }

            val hits = shoot_it()
            if (hits == 0) return

            if (mnstr.hits == 0) killer_sw_hurt = false
            mnstr.hits += hits

            if ((rifle_flag == F_single) && (mnstr.hits < 3)) {
                if (mnstr.hits == 1) {
                    myPrintf("Your bullet hit the hoop snake.  However the hoop snake's thick scales \nslowed the bullet down.  The hoop snake is now hissing furiously. \n")
                } else {
                    myPrintf("You've hit the hoop snake again, and it's hurting.  However it is \nalive and full of venom. \n")
                }
                monster_flag = F_wounded
                return
            }

            if (rifle_flag == F_single) {
                myPrintf("The bullet hit the hoop snake finishing the horrible creature off. \n")
            } else {
                myPrintf("The bullets hit the hoop snake splatting it into a mass of mince meat. \n")
            }
            myPrintf("Suddenly the bullet holed snake glows red and whooshes into a cloud \nsteam, leaving no traces behind.\n\n")
            mnstr.location = B_destroyed
            mnstr.status = F_asleep
            monster_flag = F_no_monster
            room[n][M_monster] = 0
            return
        }

        var shot_obj: ObjectStruct? = null
        for (obj in objectList) {
            if (tag[obj.id]) {
                if (obj.id == V_rifle) continue
                sw_other_object = true
                if (obj.location == n) {
                    shot_obj = obj
                    break
                }
                if (obj.location == B_have) {
                    myPrintf("I will ***NOT*** shoot at something that I'm holding!!\n")
                    return
                }
            }
        }

        if (shot_obj != null) {
            val hits = shoot_it()
            if (hits == 0) return

            when (shot_obj.id) {
                V_can -> {
                    myPrintf("The can is hit by a bullet and flies off out of sight.\n")
                    Can.location = B_destroyed
                }

                V_bottle -> {
                    myPrintf("It is hit by a bullet and shatters into a million pieces.\n")
                    Bottle.location = B_destroyed
                }

                V_dynamite -> {
                    myPrintf("Nothing happens!  The \"dynamite\" is actually a very safe\nmining explosive which won't detonate even if impacted by a high\nspeed bullet (TRUE story!).  However this sort of explosive will\nalways detonate with a blasting cap.\n")
                }

                V_cap -> {
                    myPrintf("POP!!  The blasting cap explodes but causes no damage.\n")
                    Cap.location = B_destroyed
                }

                V_gong -> {
                    myPrintf("The bullet")
                    if (hits > 1) myPrintf("s")
                    myPrintf(" went cleanly through the soft silver metal of\nthe gong making a weird humming noise in the process.  It's\na pointless waste of ammunition vandalizing this beautiful\ngong.\n")
                }

                V_safe -> {
                    if (Picture.status == S_open) {
                        myPrintf("The safe is made out of harden steel.  You'll only waste ammunition \nshooting at it.  Try to unlock it instead. \n")
                    } else {
                        myPrintf("I don't see a safe to shoot at.\n")
                    }
                }

                V_letter -> {
                    boom()
                    myPrintf("That was clever of you to realize the letter was really\na letter bomb.  However shooting a letter bomb while you're\nright next to it was less than clever. Too bad you got blown\nto pieces discovering that bit of wisdom.\n\n")
                    ender(F_died)
                }

                else -> {
                    myPrintf("Except for wasting ammunition nothing much happened.\n")
                }
            }
            return
        }

        if (sw_other_object) {
            myPrintf("I don't see the target.\n")
            return
        }

        if (tag[V_DIRECTION]) {
            val hits = shoot_it()
            if (hits == 0) return
            myPrintf("You shoot in that direction but didn't accomplish anything\nexcept waste ammunition.\n")
        } else {
            myPrintf("It may be obvious to you, but I don't understand what you\nwant to shoot at.\n")
        }
    }

    private fun gleeper(n: Int) {
        val count = room[n][M_gleep]

        if (((Torch.location == B_have) && Torch.status != 0) || (n < 186) || (n > 202)) {
            if (count != 0) {
                monster_sw_hoop = true
                if (count != 1) {
                    myPrintf("There are %d gleeps here. \n", count)
                } else {
                    myPrintf("There is a gleep here. \n")
                }
                return
            }
        }

        if (monster_sw_hoop) {
            monster_count_down++
            if (monster_count_down > 5) {
                monster_count_down = 0
                for (i in 0..9) {
                    val spot = gleep_spot[i]
                    if (room[spot][M_gleep] == 0) continue
                    if (room[spot][M_gleep] > 10000) {
                        room[spot][M_gleep]++
                    } else {
                        room[spot][M_gleep] = room[spot][M_gleep] shl 1
                    }
                }
            }
        }
    }

    private suspend fun monster(loc_pnt: IntArray) {
        if (loc_pnt[0] == R_closet) {
            monster_flag = F_no_monster
            if (monster_sw_letter) {
                monster_sw_letter = false
                monster_sw_nogo = true
                myPrintf("\nYou hear a loud \"BOOM!\" from the Prayer Room followed\nby the sounds of people shouting and cursing in Farsi. I\nthink it is definitely time that we leave.\n")
            }
            return
        }

        var sw_chasing = false
        var sw_local = false
        var mnstr_local: MonsterStruct? = null
        var mnstr_chase: MonsterStruct? = null

        for (j in 0 until Enemy_cnt) {
            val mnstr = monster_start[j]
            if (mnstr.location == loc_pnt[0]) {
                sw_local = true
                mnstr_local = mnstr
            } else if (mnstr.status != F_asleep) {
                if ((N_mullah == j) || (N_guards == j) || ((N_ned == j) && (mnstr.status == F_stealing))) {
                    continue
                }
                sw_chasing = true
                mnstr_chase = mnstr
            }
        }

        if (sw_local && mnstr_local != null) {
            if (sw_chasing && mnstr_chase != null) {
                when (mnstr_chase.type) {
                    N_wombat -> {
                        mnstr_local.status = F_asleep
                        room[mnstr_local.location][M_monster]--
                        mnstr_local.location = Wombat.location
                        room[Wombat.location][M_monster]++
                        chaser(mnstr_chase, loc_pnt)
                        return
                    }

                    N_hoop_snake -> {
                        myPrintf("                     ---- What a relief!! ---- \n")
                        myPrintf("           The dreaded hoop snake has broken off pursuit.\n")
                        myPrintf("                         But what is this!!\n\n")
                        myPrintf("You see yet another terrible creature!\n")
                        monster_sw_replaced = true
                    }
                }
                monster_flag = F_no_monster
                mnstr_chase.status = F_asleep
                sw_chasing = false
            }

            when (mnstr_local.type) {
                N_guards -> {
                    monster_flag = F_monster_active

                    if (monster_sw_nogo) {
                        myPrintf("\nWith you are three men dressed in ripped and shredded clothes which have\nbeen blackened by an explosion.  They are armed to the teeth and insane\nwith rage.  They take one look at you and proceed to cut you to pieces with\ntheir automatic weapons!\n\n")
                        ender(F_died)
                    }

                    if (Rifle.location == B_have) {
                        myPrintf("With you are four men dressed in rather shabby clothes. Three of them\nare clutching AK-47 assault rifles while the fourth one is holding an Uzi\nmachine gun. They take one look at the M-16 rifle which you are clutching,\npoint their own weapons at you and proceed to hose you with a hail of\nlead!  Five seconds later you look like a piece of Swiss cheese.\n\n")
                        ender(F_died)
                    }

                    when (Guards.status) {
                        F_asleep -> {
                            Guards.status = F_passive
                            myPrintf("With you are four men dressed in rather shabby clothes. Three of them\nare clutching AK-47 assault rifles while the fourth one is holding an Uzi\nmachine gun. They search you but find nothing that is obviously a\nweapon.  They are in a quandary on what to do with you, since you\nliterally appeared out of thin air.\n")
                            return
                        }

                        F_passive -> {
                            if (Mullah.status == F_asleep) {
                                myPrintf("\nThe four men have concluded that the best thing to do\nwith you is to take you to their superiors.  Two of the men\nroughly grab hold of you and fling you bodily through the\ndoorway to the east.\n\n")
                                loc_pnt[0] = R_prayer
                                long_descp(R_prayer)
                                chief_mullah()
                                return
                            }
                            if (monster_sw_guarded) {
                                myPrintf("\nAll of the guards are currently in the prayer room.\n")
                                monster_sw_guarded = false
                            }
                            return
                        }

                        F_aggressive -> {
                            Guards.status = F_killing
                            myPrintf("\nThe four guards grab you by the arms and legs, and frog\nmarch you back into the prayer room.\n")
                            loc_pnt[0] = R_prayer
                            return
                        }

                        F_killing -> {
                            myPrintf("\nThe guards are tired of fooling with you.  One of them\nhits you on the head with the butt of his rifle.  Then the\nother two guards drag you outside where you are executed\nbefore a jeering mob of 30,000 people!\n\n")
                            ender(F_died)
                        }
                    }
                }

                N_mullah -> {
                    when (Mullah.status) {
                        F_asleep -> {
                            chief_mullah()
                            return
                        }

                        F_aggressive -> {
                            if (bribe_mullah()) return
                            if (Letter.location == R_prayer) {
                                see_letter()
                                return
                            }
                            monster_count_down++
                            when (monster_count_down) {
                                1 -> {
                                    myPrintf("\nThe Mullah is waiting for you to give him something.\n")
                                    return
                                }

                                2 -> {
                                    myPrintf("\nThe Mullah is becoming quite impatient.  You had better\ndo something soon!\n")
                                    Mullah.status = F_killing
                                }
                            }
                            return
                        }

                        F_passive -> {
                            monster_count_down++
                            when (monster_count_down) {
                                1 -> {
                                    myPrintf("\nThe Mullah is examining the envelope of Muammar's letter.\n")
                                    return
                                }

                                2 -> {
                                    myPrintf("\nThe Mullah has turned the envelope over and is just\nabout to open it.\n")
                                    return
                                }

                                3 -> {
                                    boom()
                                    myPrintf("The Mullah opened the letter.  Needless to say, the letter\nwas a bomb.  The good news is the nasty old Mullah died.\nThe bad news is you died in the explosion as well!\n\n")
                                    ender(F_died)
                                }
                            }
                        }

                        F_killing -> {
                            if (bribe_mullah()) {
                                Mullah.status = F_aggressive
                                return
                            }
                            if (Letter.location == R_prayer) {
                                see_letter()
                                return
                            }
                            myPrintf("\n\"You are a brainless idiot!\" screams the Mullah!\n")
                            mullah_kills()
                        }
                    }
                    see_letter()
                    return
                }

                N_ned -> {
                    if (monster_flag == F_no_monster) {
                        myPrintf("\nAs you approached the entrance of Ned Kelly's hide out, a man walked out\nthe front door and prevented you from entering.  He is wearing an iron\nhelmet with thin eye slits which completely covers his head.  He has\nbody armour made of thin sheets of iron that covers his chest and abdomen.\nHe is pointing a 12 guage double barrel shotgun and has a pistol on\nhis hip.  I could be wrong but I think this is Ned Kelly!\n\n")
                        loc_pnt[0] = R_hideout_entr
                        monster_flag = F_monster_active
                        Ned.status = F_passive
                        return
                    } else {
                        if (Ned.status == F_killing) ned_kills()
                        myPrintf("\nNed is not about to allow you to enter his hide out.  He is now expressing\nhis displeasure with your continued presence by cocking both of the hammers on\nhis shotgun and pointing it at your head.  I believe it is time to go!!\n\n")
                        loc_pnt[0] = R_hideout_entr
                        Ned.status = F_killing
                        return
                    }
                }

                N_wombat -> {
                    monster_sw_replaced = false
                    if (monster_flag == F_no_monster) {
                        myPrintf("\n                        ----- OH NO!! -----\n")
                        myPrintf("                  You are in ***SERIOUS TROUBLE*** !!!\n")
                        myPrintf("Forget about hoop snakes, and forget about drop bears.  This is the WORST\nthing that can be found in the ACME Mine.  You see before you the awful\nspawn of the Pommy nuclear weapon's tests. Its ancestors were inoffensive\ncreatures, but gamma radiation has transformed this into...\n                      The Dreadful Mutant Wombat!! \n")
                        myPrintf("Normally I would advise you to run for your life.  However there's really\nno point.  You can not out run this thing, and it's almost impossible to\nkill.  You might as well just stand here and let it finish you off as\nquickly and painlessly as possible.\n\n")
                        monster_flag = F_monster_active
                        Wombat.status = F_aggressive
                        loc_pnt[1] = loc_pnt[0]
                        loc_pnt[2] = 0
                    } else {
                        if (Wombat.status == F_aggressive) {
                            myPrintf("\nThe wombat is approaching you.  Its mouth is wide open showing its\nenormous canines.  Its claws are fully extended.  The wombat sees\nyou as an easy meal and is preparing to feast.\n")
                            Wombat.status = F_killing
                            return
                        }
                        if (Wombat.status == F_killing) {
                            myPrintf("\nThe obscene creature has grasped you with its terrible claws!  First the\nwombat rips off your right arm with a single jerk and tosses it down its\nthroat like it was an appetizer (which it was!).  Next the monster studies\nyou for a moment and then twists off your left leg and chews on it like\na turkey drumstick.  After savoring your left leg, it opens its mouth wide\nand stuffs you in head first!  Your last memory was hearing the crunching\nof your own bones as the wombat's jaws clamped down!\n")
                            ender(F_died)
                        }
                    }
                    return
                }

                N_drop_bear -> {
                    monster_sw_replaced = false
                    if (monster_flag == F_no_monster) {
                        when (monster_bear_flag) {
                            0 -> {
                                myPrintf("\nAs you walk in, you see something that looks vaguely like a koala bear\nsitting in the middle of the floor.  However this \"koala\" has vampire\nteeth and blood drooling down the sides of its mouth. The bear takes\none look at you and climbs up the wall onto the ceiling. It \nclings to the ceiling much like a fly and seems to be positioning\nitself to be directly over you.  I could be wrong but I think this is\nthe deadly DROP BEAR! \n\n")
                                monster_bear_flag = 1
                            }

                            1 -> {
                                myPrintf("\nJust as you walk in, a drop bear flashes by and hits the ground with\na THUNK.  That was close!  Had it hit you, and caught hold with its\nclaws, you would have been finished.  The dreaded beast is now\nrunning up the wall towards the ceiling to give it another go. I\nthink we had better leave and soon!\n\n")
                                monster_bear_flag = 2
                            }

                            2 -> {
                                myPrintf("\nAs you walk in, you see a drop bear lounging lazily in the middle of\nthe floor. It rolls over and takes one look at you, runs towards\nthe wall and scampers up to the ceiling.  It is now positioning\nitself to be directly over you.\n\n")
                                monster_bear_flag = 1
                            }
                        }
                        monster_flag = F_monster_active
                        mnstr_local.status = F_aggressive
                    } else {
                        if (mnstr_local.status == F_aggressive) {
                            myPrintf("\nThe drop bear is now positioned directly above you.  It is hanging batlike\nby its rear paws, with its front arms reaching out with claws fully \nextended.  Its mouth is wide open with its vampire teeth clearly visible.\nI think this thing means business!  Let's make a hasty departure!!\n")
                            mnstr_local.status = F_killing
                            return
                        }
                        if (mnstr_local.status == F_killing) {
                            myPrintf("\nThe drop bear drops on top of you!  First it grabs hold of you with its \nsharp claws that sink deep into your flesh.  Then it bites into your neck \nat the jugular vein and begins sucking your blood.  You try desperately \nto pull the horrible monster off, but it only clamps on harder and sucks \nvigorously.  Soon you grow weak from lack of blood, and lapse into death. \n")
                            ender(F_died)
                        }
                    }
                    return
                }

                N_hoop_snake -> {
                    if (monster_flag == F_no_monster) {
                        if (monster_sw_replaced) {
                            monster_sw_replaced = false
                            myPrintf("\nA new and rested hoop snake rolls into view.  The snake sees you, lets go of\nits tail and starts slithering towards you with fangs at the ready. \n\n")
                        } else {
                            if (!sw_snaked) {
                                myPrintf("\nSomething that looks vaguely like a barrel hoop rolls into the passage. You \nsuddenly realize to your horror that this is no hoop but a snake biting its \nown tail.  The snake lets go of its tail and starts to slither towards you \nlike a regular, highly aggressive snake. \n\n")
                                sw_snaked = true
                            } else {
                                myPrintf("\nA hoop snake rolls towards you.  It lets go of its tail and starts slithering\nin your direction. \n\n")
                            }
                        }
                        monster_flag = F_monster_active
                        mnstr_local.status = F_aggressive
                        loc_pnt[1] = loc_pnt[0]
                        loc_pnt[2] = 0
                        return
                    } else {
                        if (mnstr_local.status == F_aggressive) {
                            myPrintf("\nThe hoop snake is coiling up in front of you and hissing very aggressively. \nVenom is dripping from its sharp fangs and seems to be burning holes into\nthe stone floor.\n\nI think it would be wise for us to leave....  Quickly!!\n")
                            mnstr_local.status = F_killing
                            return
                        }
                        if (mnstr_local.status == F_killing) {
                            myPrintf("\nThe hoop snake strikes and bites you right on the nose!\n\nYou begin to thrash around like a Baygon sprayed cocky.  The nerve poison \nmakes you jerk around onto your back with your arms and legs flailing about\nin the air.  With time your spasmodic twitching reduces in frequency.  You\nslowly grind down to a halt with the coming of death. \n")
                            ender(F_died)
                        }
                    }
                }
            }
        }

        if (sw_chasing && mnstr_chase != null) {
            chaser(mnstr_chase, loc_pnt)
        }
    }

    private fun chaser(mnstr: MonsterStruct, loc_pnt: IntArray) {
        when (mnstr.type) {
            N_ned -> {
                if (loc_pnt[0] == R_hideout_entr) {
                    if (Ned.status == F_passive) {
                        myPrintf("\nNed Kelly is standing in front of you and is holding a 12 guage shotgun\nin a rather menacing manner.  This guy is really game!\n\nI suggest we leave.....   and quickly!!\n\n")
                        Ned.status = F_aggressive
                        return
                    }
                    if (Ned.status == F_aggressive) {
                        myPrintf("\nNed seems to be getting impatient with you.  He is now expressing his\ndispleasure with your continued presence by cocking both of the hammers on\nhis shotgun and pointing it at your head.  I really think we should be\nmaking a hasty departure!!\n\n")
                        Ned.status = F_killing
                        return
                    }
                    if (Ned.status != F_killing) {
                        monster_flag = F_no_monster
                        Ned.status = F_asleep
                        return
                    } else {
                        ned_kills()
                    }
                } else {
                    monster_flag = F_no_monster
                    Ned.status = F_asleep
                    return
                }
            }

            N_wombat -> {
                if (loc_pnt[0] == R_lift_inside) {
                    myPrintf("\nYou run as fast as you can into the lift.  You've made it!  You're in the\nlift! However as you turn around, you realize to your horror that the\nwombat has also made it inside the lift, and the door is closing!!\n\n                     Ah, Stuff of Nightmares!!!\n          ---Trapped in a Lift with a Mutant Wombat!!!---\n\nThis is just too horrible.  Let it suffice... You died. \n")
                    ender(F_died)
                }
                if (loc_pnt[0] == loc_pnt[2]) {
                    myPrintf("The wombat is in that direction.  You can't go that way!\n")
                    loc_pnt[0] = loc_pnt[1]
                    return
                }
                if (!sw_wombat) {
                    myPrintf("\nAs you flee down the passage, you hear the \"THUD, THUD, THUD\" of the wombat\ntrudging down the passage.  The horrible thing is after you!  Give up\nall hope!  The wombat is driven by nuclear energy and will never stop.\n\n")
                    sw_wombat = true
                } else {
                    myPrintf("\nThe wombat is still chasing you and not tiring. \n")
                }
                monster_flag = F_monster_active
                mnstr.status = F_aggressive
                room[mnstr.location][M_monster]--
                room[loc_pnt[0]][M_monster]++
                mnstr.location = loc_pnt[0]
                loc_pnt[2] = loc_pnt[1]
                loc_pnt[1] = loc_pnt[0]
                return
            }

            N_hoop_snake -> {
                if (loc_pnt[0] == R_lift_inside) {
                    myPrintf("\nYou seek refuge from the hoop snake by fleeing into the lift.  Just as you\nenter the lift, its door begins to close.  Unfortunately the hoop snake\nrolls in the instant before the door is fully closed.\n\nWe will not describe the unpleasant events that occur behind the closed door\nof the lift. However one can hear screams, curses, futile banging on the\nlift door and loud snake hissing.  After a few minutes there is again silence.\nThe lift door opens and the hoop snake rolls out and away. A peek inside the\nlift reveals your corpse which is in the early phases of rigor mortis.\n")
                    ender(F_died)
                }
                if (loc_pnt[0] == loc_pnt[2]) {
                    if (mnstr.status == F_killing) {
                        myPrintf("The hoop snake is in that direction.  You can't go that way!\n")
                        loc_pnt[0] = loc_pnt[1]
                        return
                    } else {
                        myPrintf("You have run back the way you came and passed the hoop snake, which\nis still biting its tail and rolling like a hoop. The vile creature\nhisses in frustration and does a U-turn to continue the chase.\n\n")
                    }
                } else {
                    if (!sw_snaked) {
                        myPrintf("\nAs you flee down the passage, you hear the characteristic sound of reptilian\nscales rubbing the stone floor.  You look over your shoulder and see the\nhoop snake is rolling along right behind you! The dreaded thing is chasing you!!\n\n")
                        sw_snaked = true
                    } else {
                        myPrintf("\nThe hoop snake is rolling along behind you, and still in hot pursuit!\n\n")
                    }
                }
                monster_flag = F_monster_active
                mnstr.status = F_aggressive
                room[mnstr.location][M_monster]--
                room[loc_pnt[0]][M_monster]++
                mnstr.location = loc_pnt[0]
                loc_pnt[2] = loc_pnt[1]
                loc_pnt[1] = loc_pnt[0]
                return
            }

            N_drop_bear, N_mullah, N_guards -> {
                monster_flag = F_no_monster
                mnstr.status = F_asleep
                return
            }
        }
        myPrintf("Run time error detected in \"chaser\" subroutine.\n")
        myPrintf("\"mnstr->Type\" value was %d.\n", mnstr.type)
        bugs(Logic_error)
    }

    private fun ned_kills() {
        myPrintf("\nWith an air of professional detachment, Ned Kelly pulls both triggers\non his shotgun and blows your head clean off your shoulders. \n\n")
        ender(F_died)
    }

    private fun mullah_kills() {
        myPrintf("\nWith that pronouncement, the chief Mullah claps his hands\ntwice.  Two Revolutionary Guards drag you outside where you\nare executed before a jeering mob of 30,000 people!\n")
        ender(F_died)
    }

    private fun see_letter() {
        myPrintf("\nThe Mullah gives the letter a casual glance, then\nsuddenly his face lights up and he claps his hands with\nglee.\n\n")
        myPrintf("\"It's a letter from my old friend Muammar!\", he exclaims!\n")
        myPrintf("\"This is an occasion for much jubulation.  As a token of\nmy esteem take this ancient gong clapper made of gold and\nivory.  It was recovered from the personal collection of\nthe deposed Shah. It once belonged to the ancient Persian\nKing Cyrus and is priceless!\"\n\n")
        myPrintf("With this glad tiding the Mullah hands you the ancient\nclapper and then reaches down to pick up the letter from\nhis old friend.\n")

        Mullah.status = F_passive
        Guards.status = F_passive
        Letter.location = B_destroyed
        room[R_prayer][M_obj_cnt]--
        Clapper.location = B_have
        Detector.location = R_guard
        room[R_guard][M_obj_cnt]++
        carry_count++
        carry_weight += Clapper.weight
        monster_sw_letter = true
        monster_count_down = 0
    }

    private suspend fun chief_mullah() {
        hold_it()
        Mullah.status = F_aggressive
        Guards.status = F_aggressive

        myPrintf("The chief Mullah (or whatever he is) who is sitting on the\ndias glares in your direction and begins expounding in a\nlong monologue in Farsi. One of his Revolutionary Guards\nstarts translating the Mullah's words into fairly good\nEnglish (with an American accent!). Here is what he said:\n")
        myPrintf("\n\"I can see by your slouch hat, singlet and short pants\nthat you are a good-for-nothing satanic Australian. I am\ncompelled to point out that your sort is most unwelcomed in\n")

        for (pnt in objectList) {
            if ((pnt.location == B_have) && ((V_letter == pnt.id) || (pnt.value > 0))) {
                myPrintf("this holy precinct.  Unless you can provide some good\nreasons (preferably financial ones) to the contrary, I\nshall order you to be executed immediately for the\namusement and edification of the local populace!\"\n")
                monster_count_down = 0
                return
            }
        }

        myPrintf("this holy precinct.  Since you have nothing of value, our\nusual practice would be to take you hostage and extract a\nransom from your government.  However the Ozzie Dollar\ndoesn't buy all that much anymore, so we'll just execute\nyou and leave it at that.\"\n")
        mullah_kills()
    }

    private fun bribe_mullah(): Boolean {
        var sw_bribe = false
        for (pnt in objectList) {
            if ((pnt.location == R_prayer) && (pnt.value > 0)) {
                pnt.location = B_destroyed
                room[R_prayer][M_obj_cnt]--
                sw_bribe = true
            }
        }
        if (sw_bribe) {
            myPrintf("\nOne of the guards picks up the treasure and hands it over to\nthe chief Mullah who discretely pockets it within his robe.  The\nMullah then looks expectantly towards you...  I think he wants\nmore treasure.\n")
            monster_count_down = 0
            return true
        }
        return false
    }
}




