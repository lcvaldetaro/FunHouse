package com.funhouse.feature.funhouseenginekotlin.util

import com.funhouse.feature.funhouseenginekotlin.models.Place
import com.funhouse.feature.funhouseenginekotlin.models.GameObject
import com.funhouse.feature.funhouseenginekotlin.models.Goal
import club.gepetto.GcLog

object DatabaseLoader {
    private fun parseCsvLine(line: String): List<String> {
        val result = mutableListOf<String>()
        var current = StringBuilder()
        var inQuotes = false
        var i = 0
        while (i < line.length) {
            val c = line[i]
            if (c == '"') {
                if (inQuotes && i + 1 < line.length && line[i + 1] == '"') {
                    current.append('"')
                    i++ // Skip second quote
                } else {
                    inQuotes = !inQuotes
                }
            } else if (c == ',' && !inQuotes) {
                result.add(current.toString().trim())
                current = StringBuilder()
            } else {
                current.append(c)
            }
            i++
        }
        result.add(current.toString().trim())
        return result
    }

    private fun cleanDblQuotes(s: String): String {
        return s.replace("\"\"", "\"")
    }

    fun loadCsv(content: String): List<List<String>> {
        val result = mutableListOf<List<String>>()
        if (content.isBlank()) return result
        val lines = content.replace("\r\n", "\n").split('\n')
        lines.forEach { line ->
            val trimmedLine = line.trim()
            if (trimmedLine.isNotBlank()) {
                result.add(parseCsvLine(trimmedLine).map { cleanDblQuotes(it) })
            }
        }
        return result
    }

    fun loadMaster(content: String): Map<String, String> {
        val rows = loadCsv(content)
        val map = mutableMapOf<String, String>()
        for (row in rows) {
            if (row.size >= 2) {
                map[row[0].lowercase()] = row[1]
            }
        }
        return map
    }

    fun loadPlaces(content: String): List<Place> {
        val rows = loadCsv(content)
        val list = mutableListOf<Place>()
        if (rows.isEmpty()) return list
        // Skip header row 0
        for (p in 1 until rows.size) {
            val row = rows[p]
            if (row.size < 20) continue
            try {
                val num = row[0].toIntOrNull() ?: continue
                val name = row[1]
                val exits = IntArray(12)
                for (d in 0 until 12) {
                    exits[d] = row[d + 2].toIntOrNull() ?: 0
                }
                val points = row[14].toIntOrNull() ?: 0
                val statusStr = row[15].lowercase()
                val status = if (statusStr.contains("dark")) 1 else 0 // 1 = dark, 0 = lit
                val objDep = row[16].toIntOrNull() ?: 0
                val message = row[17]
                val altDescr = row[18]
                val descr = row[19]

                list.add(
                    Place(
                        num = num,
                        name = name,
                        descr = descr,
                        altdescr = altDescr,
                        message = message,
                        comments = "",
                        points = points,
                        disc = 0,
                        status = status,
                        obj_dep = objDep,
                        m = exits
                    )
                )
            } catch (e: Exception) {
                GcLog.e("Error parsing place row: $row", e)
            }
        }
        return list
    }

    fun loadObjects(content: String): List<GameObject> {
        val rows = loadCsv(content)
        val list = mutableListOf<GameObject>()
        if (rows.isEmpty()) return list
        // Skip header
        for (p in 1 until rows.size) {
            val row = rows[p]
            if (row.size < 14) continue
            try {
                val num = row[0].toIntOrNull() ?: continue
                val name = row[1]
                val nickname = row[2]
                val location = row[3].toIntOrNull() ?: 0
                val weight = row[4].toIntOrNull() ?: 0
                val pointsSaved = row[5].toIntOrNull() ?: 0
                val locationToDrop = row[6].toIntOrNull() ?: 0
                val pointsTaken = row[7].toIntOrNull() ?: 0

                val statusStr = row[8].lowercase()
                // status mapping: closed=4, open=5, locked=2, unlocked=3, on=0, off=1
                val status = when {
                    statusStr.contains("locked") -> 2
                    statusStr.contains("unlocked") -> 3
                    statusStr.contains("closed") -> 4
                    statusStr.contains("open") -> 5
                    statusStr.contains("off") -> 1
                    else -> 0 // on
                }

                val verbsStr = row[9].lowercase()
                val moveable = verbsStr.contains("get")
                val readable = verbsStr.contains("read")
                val openable = verbsStr.contains("open")
                val turnable = verbsStr.contains("turn")
                val startable = verbsStr.contains("start")
                val unlockable = verbsStr.contains("unlock")

                val text = row[10]
                val dependent = row[11].toIntOrNull() ?: 0
                val incompatible = row[12].toIntOrNull() ?: 0
                val description = row[13]

                list.add(
                    GameObject(
                        num = num,
                        name = name,
                        nickname = nickname,
                        location = location,
                        status = status,
                        description = description,
                        weight = weight,
                        pointsSaved = pointsSaved,
                        locationToDrop = locationToDrop,
                        pointsTaken = pointsTaken,
                        moveable = moveable,
                        readable = readable,
                        openable = openable,
                        unlockable = unlockable,
                        turnable = turnable,
                        startable = startable,
                        text = text,
                        dependent = dependent,
                        incompatible = incompatible
                    )
                )
            } catch (e: Exception) {
                GcLog.e("Error parsing object row: $row", e)
            }
        }
        return list
    }

    fun loadGoals(content: String): List<Goal> {
        val rows = loadCsv(content)
        val list = mutableListOf<Goal>()
        if (rows.isEmpty()) return list
        // Skip header
        for (p in 1 until rows.size) {
            val row = rows[p]
            if (row.size < 15) continue
            try {
                val num = row[0].toIntOrNull() ?: continue
                val name = row[1]
                val finish = if (row[2].trim().lowercase() == "y") 1 else 0
                val pointsOwner = row[3].toIntOrNull() ?: 0
                val pointsNoOwner = row[4].toIntOrNull() ?: 0

                val verbsStr = row[5].lowercase()
                val verbs = IntArray(16)
                var vb = 0
                // verbs mapping: start=1, kill=2, get/grab/take=3, drop=4
                if (verbsStr.contains("start")) verbs[vb++] = 1
                if (verbsStr.contains("kill")) verbs[vb++] = 2
                if (verbsStr.contains("take") || verbsStr.contains("grab") || verbsStr.contains("get")) verbs[vb++] = 3
                if (verbsStr.contains("drop")) verbs[vb++] = 4

                val location = row[6].toIntOrNull() ?: 0
                val `object` = row[7].toIntOrNull() ?: 0
                val text = row[8]
                val locDep = row[9].toIntOrNull() ?: 0

                val depend = parseList(row[10])
                val incomp = parseList(row[11])

                val errorText = row[12]
                val noOwnerText = row[13]
                val descr = row[14]

                list.add(
                    Goal(
                        num = num,
                        name = name,
                        type = 0, // dynamic
                        finish = finish,
                        pointsOwner = pointsOwner,
                        pointsNoOwner = pointsNoOwner,
                        location = location,
                        `object` = `object`,
                        text = text,
                        verb = verbs,
                        locDep = locDep,
                        depend = depend,
                        incomp = incomp,
                        errorText = errorText,
                        noOwnerText = noOwnerText,
                        descr = descr
                    )
                )
            } catch (e: Exception) {
                GcLog.e("Error parsing goal row: $row", e)
            }
        }
        return list
    }

    private fun parseList(s: String): IntArray {
        val arr = IntArray(16)
        if (s.isBlank()) return arr
        val cleaned = s.replace(",", " ").trim()
        val tokens = cleaned.split(Regex("\\s+")).filter { it.isNotEmpty() }
        for (i in 0 until minOf(tokens.size, 16)) {
            arr[i] = tokens[i].toIntOrNull() ?: 0
        }
        return arr
    }
}
