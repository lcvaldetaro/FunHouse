package com.funhouse.feature.funhouseenginekotlin.models

import kotlinx.serialization.Serializable

data class Place(
    val num: Int,
    val name: String,
    val descr: String,
    val altdescr: String,
    val message: String,
    val comments: String,
    val points: Int,
    var disc: Int,
    var status: Int,
    val obj_dep: Int,
    val m: IntArray = IntArray(12) // North, South, East, West, Up, Down, etc.
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        other as Place
        if (num != other.num) return false
        return true
    }

    override fun hashCode(): Int {
        return num
    }
}

data class GameObject(
    val num: Int,
    val name: String,
    val nickname: String,
    val location: Int,
    val status: Int,
    val description: String,
    val weight: Int,
    val pointsSaved: Int,
    val locationToDrop: Int,
    val pointsTaken: Int,
    val moveable: Boolean,
    val readable: Boolean,
    val openable: Boolean,
    val unlockable: Boolean,
    val turnable: Boolean,
    val startable: Boolean,
    val text: String,
    val dependent: Int,
    val incompatible: Int
)

data class Goal(
    val num: Int,
    val name: String,
    val type: Int,
    val finish: Int,
    val pointsOwner: Int,
    val pointsNoOwner: Int,
    val location: Int,
    val `object`: Int,
    val text: String,
    val verb: IntArray = IntArray(16),
    val locDep: Int,
    val depend: IntArray = IntArray(16),
    val incomp: IntArray = IntArray(16),
    val noOwnerText: String,
    val errorText: String,
    val descr: String
)

@Serializable
data class PlayerInstance(
    val index: Int,
    var use: Int = 0,
    var iobj: Int = 0,
    var iverb: Int = 0,
    var iconju: Int = 0,
    var ipron: Int = 0,
    var inoun: Int = 0,
    var myErr: Int = 0,
    var errpos: Int = 0,
    var numObjsCarried: Int = 0,
    var numMovements: Int = 0,
    var initialPosition: Int = 0,
    var myPosition: Int = 0,
    var myPoints: Int = 0,
    var mySavedPoints: Int = 0,
    var myPlacePoints: Int = 0,
    var can: Int = 0,
    var chest: Int = 0,
    var sand: Int = 0,
    var wordtype: String = "",
    var argv: List<String> = emptyList(),
    var argc: Int = 0,
    val placeVisited: IntArray = IntArray(2048),

    var handle: String = "",
    var description: String = "",
    var url: String = "",
    var gender: String = "",
    var infoGiven: Int = 0,
    var gameover: Int = 0,
    var online: Int = 0,
    var helped: Int = 0,
    var goal: Int = 0,
    var alertedLost: Int = 0
)
