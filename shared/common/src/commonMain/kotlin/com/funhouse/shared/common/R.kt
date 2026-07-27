package com.funhouse.shared.common

import androidx.compose.runtime.Composable
import com.funhouse.shared.common.generated.resources.*
import org.jetbrains.compose.resources.StringResource
import com.funhouse.shared.common.utils.getCacheMap

object R {
    object string {
        const val about = 20000
        const val about_game = 20001
        const val about_label = 20002
        const val adventure_about = 20003
        const val adventure_backup_success = 20004
        const val adventure_banner_content = 20005
        const val adventure_cant_open_external = 20006
        const val adventure_cant_open_external_path = 20007
        const val adventure_cant_open_file = 20008
        const val adventure_cant_read_text = 20009
        const val adventure_fatal_error = 20010
        const val adventure_filename_prompt = 20011
        const val adventure_init_complete = 20012
        const val adventure_init_started = 20013
        const val adventure_restart_hint = 20014
        const val adventure_restore_success = 20015
        const val adventure_title = 20016
        const val aliens_title = 20017
        const val already_five_cards = 20018
        const val app_name = 20019
        const val arcade_banner_content = 20020
        const val arcade_title = 20021
        const val back = 20022
        const val betting_liras = 20023
        const val blackjack_about = 20024
        const val blackjack_help = 20025
        const val blackjack_shout = 20026
        const val body_label = 20027
        const val button_down = 20028
        const val button_left = 20029
        const val button_pause = 20030
        const val button_reset = 20031
        const val button_right = 20032
        const val button_rotate = 20033
        const val button_sounds = 20034
        const val button_up = 20035
        const val cached = 20036
        const val chess_black = 20037
        const val chess_checkmate = 20038
        const val chess_easy = 20039
        const val chess_hard = 20040
        const val chess_moderate = 20041
        const val chess_stalemate = 20042
        const val chess_status_checkmate = 20043
        const val chess_status_level = 20044
        const val chess_status_ongoing = 20045
        const val chess_status_stalemate = 20046
        const val chess_white = 20047
        const val command_label = 20048
        const val copyright = 20049
        const val craps_game_title = 20050
        const val deal = 20051
        const val deal_label = 20052
        const val dealer_blackjack = 20053
        const val dealer_busted = 20054
        const val dealer_cards = 20055
        const val dealer_drew = 20056
        const val dealer_stood = 20057
        const val easy = 20058
        const val echo_prefix = 20059
        const val eliza_about = 20060
        const val eliza_dont_repeat = 20061
        const val eliza_goodbye = 20062
        const val eliza_hello_problem = 20063
        const val eliza_reply_prefix = 20064
        const val eliza_secrets_safe = 20065
        const val eliza_secrets_unlocked = 20066
        const val eliza_stop_instructions = 20067
        const val eliza_title = 20068
        const val eliza_you_prefix = 20069
        const val exit = 20070
        const val game_only_in_english = 20071
        const val game_over = 20072
        const val game_over_hint = 20073
        const val game_over_shout = 20074
        const val game_version_template = 20075
        const val gepetto_score_label = 20076
        const val gepetto_wins = 20077
        const val greetings_text = 20078
        const val hard = 20079
        const val hello_player = 20080
        const val help = 20081
        const val hit_label = 20082
        const val invalid_bet = 20083
        const val invalid_command = 20084
        const val it_is_draw = 20085
        const val level = 20086
        const val lines = 20087
        const val liras = 20088
        const val loading_games = 20089
        const val luck_banner_content = 20090
        const val luck_title = 20091
        const val main_banner_content = 20092
        const val meaning_not_found = 20093
        const val medium = 20094
        const val multiplayer_banner_content = 20095
        const val multiplayer_title = 20096
        const val nav_advent = 20097
        const val nav_adventure = 20098
        const val nav_arcade = 20099
        const val nav_chance = 20100
        const val nav_chatbots = 20101
        const val nav_info = 20102
        const val nav_multiplayer = 20103
        const val nav_profile = 20104
        const val nav_skills = 20105
        const val network_failed = 20106
        const val new_game = 20107
        const val next = 20108
        const val no_cards = 20109
        const val notes = 20110
        const val other_banner_content = 20111
        const val other_title = 20112
        const val paddleball_title = 20113
        const val pinball_title = 20114
        const val play = 20115
        const val play_again = 20116
        const val play_against_friend = 20117
        const val play_against_gepetto = 20118
        const val player_cards = 20119
        const val playing_against_friend = 20120
        const val playing_against_gepetto = 20121
        const val point_label = 20122
        const val points_label = 20123
        const val poker_flush = 20124
        const val poker_four_of_a_kind = 20125
        const val poker_full_house = 20126
        const val poker_jacks_or_better = 20127
        const val poker_none = 20128
        const val poker_royal_flush = 20129
        const val poker_royal_street_flush = 20130
        const val poker_straight = 20131
        const val poker_straight_flush = 20132
        const val poker_three_of_a_kind = 20133
        const val poker_two_pairs = 20134
        const val privacy_policy_title = 20135
        const val profile = 20136
        const val pull_plunger = 20137
        const val randomizing_word = 20138
        const val restart = 20139
        const val retrocircuit_title = 20140
        const val retry = 20141
        const val roll_dice = 20142
        const val roll_to_start = 20143
        const val roll_to_start_new = 20144
        const val roulette_black = 20145
        const val roulette_lost_multiple = 20146
        const val roulette_no_bets = 20147
        const val roulette_red = 20148
        const val roulette_winning_number = 20149
        const val roulette_won_multiple = 20150
        const val roulette_won_single = 20151
        const val roulette_zero = 20152
        const val score_info = 20153
        const val score_label = 20154
        const val select_difficulty = 20155
        const val show_cards = 20156
        const val skill_banner_content = 20157
        const val skill_title = 20158
        const val slot_all_elizas = 20159
        const val slot_all_funhouse = 20160
        const val slot_all_jokers = 20161
        const val slot_all_same = 20162
        const val slot_all_same_with_joker = 20163
        const val slot_all_sevens = 20164
        const val slot_all_stars = 20165
        const val slot_all_willies = 20166
        const val slot_none = 20167
        const val slot_two_of_a_kind = 20168
        const val stand_label = 20169
        const val total_in_wallet = 20170
        const val turn_off_sound = 20171
        const val turn_on_sound = 20172
        const val using_decks = 20173
        const val version_build_format = 20174
        const val version_text = 20175
        const val voice = 20176
        const val wallet_entry = 20177
        const val what_is_your_name = 20178
        const val winnings_in_game = 20179
        const val winnings_per_game = 20180
        const val word_meaning = 20181
        const val you_busted = 20182
        const val you_got_liras = 20183
        const val you_lost = 20184
        const val you_lost_poker = 20185
        const val you_rolled_craps_lose = 20186
        const val you_rolled_hit_point = 20187
        const val you_rolled_point = 20188
        const val you_rolled_seven_out = 20189
        const val you_rolled_still_point = 20190
        const val you_rolled_win = 20191
        const val you_score_label = 20192
        const val you_stood = 20193
        const val you_won = 20194
        const val you_won_arcade = 20195
        const val you_won_poker = 20196
        const val adventure_game_welcome = 20197
    }
}

private var cacheInitialized = false

val stringCache: MutableMap<StringResource, String> = getCacheMap()

suspend fun initStringCache() {
    if (cacheInitialized) return
    val resources = legacyIdMap.values
    resources.forEach { res ->
        try {
            stringCache[res] = org.jetbrains.compose.resources.getString(res)
        } catch (e: Exception) {
            club.gepetto.GcLog.e("Failed to load string resource: $res", e)
        }
    }
    cacheInitialized = true
}

val legacyIdMap: Map<Int, StringResource> = mapOf(
    20000 to Res.string.about,
    20001 to Res.string.about_game,
    20002 to Res.string.about_label,
    20003 to Res.string.adventure_about,
    20004 to Res.string.adventure_backup_success,
    20005 to Res.string.adventure_banner_content,
    20006 to Res.string.adventure_cant_open_external,
    20007 to Res.string.adventure_cant_open_external_path,
    20008 to Res.string.adventure_cant_open_file,
    20009 to Res.string.adventure_cant_read_text,
    20010 to Res.string.adventure_fatal_error,
    20011 to Res.string.adventure_filename_prompt,
    20012 to Res.string.adventure_init_complete,
    20013 to Res.string.adventure_init_started,
    20014 to Res.string.adventure_restart_hint,
    20015 to Res.string.adventure_restore_success,
    20016 to Res.string.adventure_title,
    20017 to Res.string.aliens_title,
    20018 to Res.string.already_five_cards,
    20019 to Res.string.app_name,
    20020 to Res.string.arcade_banner_content,
    20021 to Res.string.arcade_title,
    20022 to Res.string.back,
    20023 to Res.string.betting_liras,
    20024 to Res.string.blackjack_about,
    20025 to Res.string.blackjack_help,
    20026 to Res.string.blackjack_shout,
    20027 to Res.string.body_label,
    20028 to Res.string.button_down,
    20029 to Res.string.button_left,
    20030 to Res.string.button_pause,
    20031 to Res.string.button_reset,
    20032 to Res.string.button_right,
    20033 to Res.string.button_rotate,
    20034 to Res.string.button_sounds,
    20035 to Res.string.button_up,
    20036 to Res.string.cached,
    20037 to Res.string.chess_black,
    20038 to Res.string.chess_checkmate,
    20039 to Res.string.chess_easy,
    20040 to Res.string.chess_hard,
    20041 to Res.string.chess_moderate,
    20042 to Res.string.chess_stalemate,
    20043 to Res.string.chess_status_checkmate,
    20044 to Res.string.chess_status_level,
    20045 to Res.string.chess_status_ongoing,
    20046 to Res.string.chess_status_stalemate,
    20047 to Res.string.chess_white,
    20048 to Res.string.command_label,
    20049 to Res.string.copyright,
    20050 to Res.string.craps_game_title,
    20051 to Res.string.deal,
    20052 to Res.string.deal_label,
    20053 to Res.string.dealer_blackjack,
    20054 to Res.string.dealer_busted,
    20055 to Res.string.dealer_cards,
    20056 to Res.string.dealer_drew,
    20057 to Res.string.dealer_stood,
    20058 to Res.string.easy,
    20059 to Res.string.echo_prefix,
    20060 to Res.string.eliza_about,
    20061 to Res.string.eliza_dont_repeat,
    20062 to Res.string.eliza_goodbye,
    20063 to Res.string.eliza_hello_problem,
    20064 to Res.string.eliza_reply_prefix,
    20065 to Res.string.eliza_secrets_safe,
    20066 to Res.string.eliza_secrets_unlocked,
    20067 to Res.string.eliza_stop_instructions,
    20068 to Res.string.eliza_title,
    20069 to Res.string.eliza_you_prefix,
    20070 to Res.string.exit,
    20071 to Res.string.game_only_in_english,
    20072 to Res.string.game_over,
    20073 to Res.string.game_over_hint,
    20074 to Res.string.game_over_shout,
    20075 to Res.string.game_version_template,
    20076 to Res.string.gepetto_score_label,
    20077 to Res.string.gepetto_wins,
    20078 to Res.string.greetings_text,
    20079 to Res.string.hard,
    20080 to Res.string.hello_player,
    20081 to Res.string.help,
    20082 to Res.string.hit_label,
    20083 to Res.string.invalid_bet,
    20084 to Res.string.invalid_command,
    20085 to Res.string.it_is_draw,
    20086 to Res.string.level,
    20087 to Res.string.lines,
    20088 to Res.string.liras,
    20089 to Res.string.loading_games,
    20090 to Res.string.luck_banner_content,
    20091 to Res.string.luck_title,
    20092 to Res.string.main_banner_content,
    20093 to Res.string.meaning_not_found,
    20094 to Res.string.medium,
    20095 to Res.string.multiplayer_banner_content,
    20096 to Res.string.multiplayer_title,
    20097 to Res.string.nav_advent,
    20098 to Res.string.nav_adventure,
    20099 to Res.string.nav_arcade,
    20100 to Res.string.nav_chance,
    20101 to Res.string.nav_chatbots,
    20102 to Res.string.nav_info,
    20103 to Res.string.nav_multiplayer,
    20104 to Res.string.nav_profile,
    20105 to Res.string.nav_skills,
    20106 to Res.string.network_failed,
    20107 to Res.string.new_game,
    20108 to Res.string.next,
    20109 to Res.string.no_cards,
    20110 to Res.string.notes,
    20111 to Res.string.other_banner_content,
    20112 to Res.string.other_title,
    20113 to Res.string.paddleball_title,
    20114 to Res.string.pinball_title,
    20115 to Res.string.play,
    20116 to Res.string.play_again,
    20117 to Res.string.play_against_friend,
    20118 to Res.string.play_against_gepetto,
    20119 to Res.string.player_cards,
    20120 to Res.string.playing_against_friend,
    20121 to Res.string.playing_against_gepetto,
    20122 to Res.string.point_label,
    20123 to Res.string.points_label,
    20124 to Res.string.poker_flush,
    20125 to Res.string.poker_four_of_a_kind,
    20126 to Res.string.poker_full_house,
    20127 to Res.string.poker_jacks_or_better,
    20128 to Res.string.poker_none,
    20129 to Res.string.poker_royal_flush,
    20130 to Res.string.poker_royal_street_flush,
    20131 to Res.string.poker_straight,
    20132 to Res.string.poker_straight_flush,
    20133 to Res.string.poker_three_of_a_kind,
    20134 to Res.string.poker_two_pairs,
    20135 to Res.string.privacy_policy_title,
    20136 to Res.string.profile,
    20137 to Res.string.pull_plunger,
    20138 to Res.string.randomizing_word,
    20139 to Res.string.restart,
    20140 to Res.string.retrocircuit_title,
    20141 to Res.string.retry,
    20142 to Res.string.roll_dice,
    20143 to Res.string.roll_to_start,
    20144 to Res.string.roll_to_start_new,
    20145 to Res.string.roulette_black,
    20146 to Res.string.roulette_lost_multiple,
    20147 to Res.string.roulette_no_bets,
    20148 to Res.string.roulette_red,
    20149 to Res.string.roulette_winning_number,
    20150 to Res.string.roulette_won_multiple,
    20151 to Res.string.roulette_won_single,
    20152 to Res.string.roulette_zero,
    20153 to Res.string.score_info,
    20154 to Res.string.score_label,
    20155 to Res.string.select_difficulty,
    20156 to Res.string.show_cards,
    20157 to Res.string.skill_banner_content,
    20158 to Res.string.skill_title,
    20159 to Res.string.slot_all_elizas,
    20160 to Res.string.slot_all_funhouse,
    20161 to Res.string.slot_all_jokers,
    20162 to Res.string.slot_all_same,
    20163 to Res.string.slot_all_same_with_joker,
    20164 to Res.string.slot_all_sevens,
    20165 to Res.string.slot_all_stars,
    20166 to Res.string.slot_all_willies,
    20167 to Res.string.slot_none,
    20168 to Res.string.slot_two_of_a_kind,
    20169 to Res.string.stand_label,
    20170 to Res.string.total_in_wallet,
    20171 to Res.string.turn_off_sound,
    20172 to Res.string.turn_on_sound,
    20173 to Res.string.using_decks,
    20174 to Res.string.version_build_format,
    20175 to Res.string.version_text,
    20176 to Res.string.voice,
    20177 to Res.string.wallet_entry,
    20178 to Res.string.what_is_your_name,
    20179 to Res.string.winnings_in_game,
    20180 to Res.string.winnings_per_game,
    20181 to Res.string.word_meaning,
    20182 to Res.string.you_busted,
    20183 to Res.string.you_got_liras,
    20184 to Res.string.you_lost,
    20185 to Res.string.you_lost_poker,
    20186 to Res.string.you_rolled_craps_lose,
    20187 to Res.string.you_rolled_hit_point,
    20188 to Res.string.you_rolled_point,
    20189 to Res.string.you_rolled_seven_out,
    20190 to Res.string.you_rolled_still_point,
    20191 to Res.string.you_rolled_win,
    20192 to Res.string.you_score_label,
    20193 to Res.string.you_stood,
    20194 to Res.string.you_won,
    20195 to Res.string.you_won_arcade,
    20196 to Res.string.you_won_poker,
    20197 to Res.string.adventure_game_welcome
)

fun Any.getString(id: Int): String {
    return com.funhouse.shared.common.getString(id)
}

fun Any.getString(id: Int, vararg args: Any?): String {
    return com.funhouse.shared.common.getString(id, *args)
}

fun getString(id: Int): String {
    val res = legacyIdMap[id] ?: return ""
    return stringCache[res] ?: ""
}

fun getString(id: Int, vararg args: Any?): String {
    val pattern = getString(id)
    var result = pattern
    for (i in args.indices) {
        val arg = args[i]
        val str = arg?.toString() ?: "null"
        val indexRegex = Regex("%${i + 1}\\$(?:\\d+)?(?:\\.\\d+)?[a-zA-Z]")
        val match = indexRegex.find(result)
        if (match != null) {
            result = result.replace(match.value, str)
        } else {
            val regex = Regex("%[a-zA-Z]")
            val sequentialMatch = regex.find(result)
            if (sequentialMatch != null) {
                result = result.replaceFirst(sequentialMatch.value, str)
            }
        }
    }
    return result
}

@Composable
fun stringResource(id: Int): String {
    val res = legacyIdMap[id]
    return if (res != null) {
        org.jetbrains.compose.resources.stringResource(res)
    } else {
        ""
    }
}

@Composable
fun stringResource(id: Int, vararg formatArgs: Any): String {
    val res = legacyIdMap[id]
    return if (res != null) {
        org.jetbrains.compose.resources.stringResource(res, *formatArgs)
    } else {
        ""
    }
}
