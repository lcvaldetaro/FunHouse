package jni
import com.funhouse.shared.common.utils.Preview
import com.funhouse.shared.common.generated.resources.Res as CommonR
import com.funhouse.shared.common.generated.resources.underconstruction
import com.funhouse.shared.common.generated.resources.backarrow
import com.funhouse.shared.common.generated.resources.ic_profile
import com.funhouse.shared.common.generated.resources.call_spk_on
import com.funhouse.shared.common.generated.resources.speaker_off
import com.funhouse.shared.common.generated.resources.eliza
import com.funhouse.shared.common.generated.resources.funhouse
import com.funhouse.shared.common.generated.resources.steamboat_willie
import com.funhouse.shared.common.generated.resources.android
import com.funhouse.shared.common.generated.resources.numberseven
import com.funhouse.shared.common.generated.resources.call_mute
import com.funhouse.shared.common.generated.resources.pause




import com.funhouse.shared.common.TABLE_COLOR_GREEN
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight

import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import club.gepetto.composeutils.GcPortraitInLandscape
import club.gepetto.composeutils.GcTheme


import jni.models.BLACK
import jni.models.BoxUsed
import jni.models.COL1
import jni.models.COL2
import jni.models.COL3
import jni.models.DOZEN1
import jni.models.DOZEN2
import jni.models.DOZEN3
import jni.models.EVEN
import jni.models.HEIGHT_CELL
import jni.models.HEIGHT_CELL_L
import jni.models.HEIGHT_NUMBER_CELL
import jni.models.HEIGHT_NUMBER_CELL_L
import jni.models.heightCell
import jni.models.heightNumberCell
import jni.models.LINES_COLOR
import jni.models.ODD
import jni.models.ONE_ZERO
import jni.models.RED
import jni.models.RouletteBet
import jni.models.TOKEN_COLOR
import jni.models.V19_36
import jni.models.V1_18
import jni.models.WIDTH_CELL
import jni.models.WIDTH_CELL_L
import jni.models.WIDTH_NUMBER_CELL
import jni.models.WIDTH_NUMBER_CELL_L
import jni.models.widthCell
import jni.models.widthNumberCell
import jni.models.heightBarAndHeaders
import club.gepetto.GcLog

@Composable
fun RouletteTable(
    modifier: Modifier = Modifier,
    landscape: Boolean = false,
    scaleRatio: Float = 1f,
    bets: List<RouletteBet> = listOf(),
    onClick: (RouletteBet) -> Unit = {}
) {
    fun adjustScreenSize() {
        heightCell = heightCell * scaleRatio
        widthCell = widthCell * scaleRatio
        widthNumberCell = widthNumberCell * scaleRatio
        heightNumberCell = heightNumberCell * scaleRatio
    }

    if (landscape) {
        heightCell = HEIGHT_CELL_L
        widthCell = WIDTH_CELL_L
        widthNumberCell = WIDTH_NUMBER_CELL_L
        heightNumberCell = HEIGHT_NUMBER_CELL_L
        adjustScreenSize()
    }
    else {
        heightCell = HEIGHT_CELL
        widthCell = WIDTH_CELL
        widthNumberCell = WIDTH_NUMBER_CELL
        heightNumberCell = HEIGHT_NUMBER_CELL
        adjustScreenSize()
    }


    Column(modifier.verticalScroll(rememberScrollState())) {
        Row {
            SingleSpacer()
            RouletteHorizontalBar(bets, id = ONE_ZERO, text ="0", landscape = landscape, onClick = onClick)
        }
        Row {
            SingleSpacer()
            RouletteColumnHeader(bets, Modifier, COL1, "C", COL2, "C", COL3, "C", landscape, onClick = onClick)
        }
        Row {
            RouletteVerticalBar(bets, id = ODD, text = "Odd", onClick = onClick)
            RouletteNumbers(bets,1, landscape = landscape, onClick = onClick)
            RouletteVerticalBar(bets, id = EVEN, text = "Even", onClick = onClick)
        }
        Row {
            RouletteVerticalBar(bets, id = RED, resource = 0, onClick = onClick)
            RouletteNumbers(bets, 2, landscape = landscape, onClick = onClick)
            RouletteVerticalBar(bets, id = BLACK, resource = 0, onClick = onClick)
        }
        Row {
            RouletteVerticalBar(bets, id = V1_18, text = "1-18", onClick = onClick)
            RouletteNumbers(bets,3, landscape = landscape, onClick = onClick)
            RouletteVerticalBar(bets, id = V19_36, text = "19-36", onClick = onClick)
        }
        Row {
            SingleSpacer()
            RouletteColumnHeader(bets, Modifier, DOZEN1, "1-12", DOZEN2, "13-24", DOZEN3, "25-36", landscape, onClick = onClick)
        }
    }
}

@Composable
fun RouletteHorizontalBar(
    bets: List<RouletteBet>,
    id: Int,
    modifier: Modifier = Modifier,
    text: String? = null,
    landscape: Boolean = false,
    onClick: (RouletteBet) -> Unit
) {
    var clicked by remember { mutableStateOf(false)}
    val hasBet = bets.count{ it.id == id } > 0
    if (clicked) { if (!hasBet) clicked = false } else if (hasBet) clicked = true

    GcLog.d("horz bets: ${bets}")
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .size(widthNumberCell * 3, heightCell)
            .border(1.dp, LINES_COLOR)
            .clickable {
                clicked = !clicked
                GcLog.d("horz clicked = ${clicked}")
                onClick(RouletteBet(BoxUsed.ZERO_BAR, id, clicked))
            }
    ) {
        if (text != null) {
            if (landscape) GcPortraitInLandscape {
                Text(text, fontSize = 14.sp, color = LINES_COLOR, modifier = Modifier.align(Alignment.Center))
            }
            else {
                Text(text, fontSize = 14.sp, color = LINES_COLOR, modifier = Modifier.align(Alignment.Center))
            }
        }
        if (clicked)
            RouletteToken()
    }
}

@Composable
fun RouletteColumnHeader(
    bets: List<RouletteBet>,
    modifier: Modifier = Modifier,
    id1: Int? = null,
    text1: String? = null,
    id2: Int? = null,
    text2: String? = null,
    id3: Int? = null,
    text3: String? = null,
    landscape: Boolean = false,
    onClick: (RouletteBet) -> Unit,
) {
    Row (modifier
        .size(widthNumberCell * 3, heightBarAndHeaders)
        .border(1.dp, LINES_COLOR)) {
        SingleColumnHeader(bets, id = id1, text = text1, landscape = landscape, onClick = onClick)
        SingleColumnHeader(bets, id = id2, text = text2, landscape = landscape, onClick = onClick)
        SingleColumnHeader(bets, id = id3, text = text3, landscape = landscape, onClick = onClick)
    }
}

@Composable
fun SingleColumnHeader(
    bets: List<RouletteBet>,
    id: Int?,
    modifier: Modifier = Modifier,
    text: String? = null,
    landscape: Boolean = false,
    onClick: (RouletteBet) -> Unit
) {
    var clicked by remember { mutableStateOf(false)}
    val hasBet = bets.count { it.id == id } > 0
    if (clicked) { if (!hasBet) clicked = false } else if (hasBet) clicked = true

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .width(widthNumberCell)
            .height(heightBarAndHeaders)
            .border(0.dp, LINES_COLOR)
            .clickable {
                clicked = !clicked
                if (id != null)
                    onClick(RouletteBet(type = BoxUsed.HEADER_COLUMN, id = id, clicked))
            }
    ) {
        if (text != null) {
            if (landscape)
                GcPortraitInLandscape { Text(text, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = LINES_COLOR) }
            else
                Text(text, fontSize = 14.sp, color = LINES_COLOR)
        }
        if (clicked)
            RouletteToken()
    }
}

@Composable
fun RouletteNumbers(
    bets: List<RouletteBet>,
    dozen: Int,
    modifier: Modifier = Modifier,
    landscape: Boolean = false,
    onClick: (RouletteBet) -> Unit
) {
    val begin = when (dozen) {
        1 -> 0
        2 -> 4
        3 -> 8
        else -> 0
    }
    val end = when (dozen) {
        1 -> 3
        2 -> 7
        3 -> 11
        else -> 0
    }
    Column (modifier.border(1.dp, LINES_COLOR)) {
        for (l in begin..end) {
            Row {
                for (r in 0..2) {
                    val i = l * 3 + r + 1
                    SingleNumber(bets,i, landscape = landscape, onClick = onClick)
                }
            }
        }
    }
}

@Composable
fun SingleNumber(
    bets: List<RouletteBet>,
    number: Int,
    modifier: Modifier = Modifier,
    landscape: Boolean = false,
    onClick: (RouletteBet) -> Unit
) {
    var clicked by remember { mutableStateOf(false)}
    val hasBet = bets.count{ it.type == BoxUsed.NUMBER && it.id == number } > 0
    if (clicked) { if (!hasBet) clicked = false } else if (hasBet) clicked = true

    Box(modifier
        .width(widthNumberCell)
        .height(heightNumberCell)
        .border(0.dp, LINES_COLOR)) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .padding(6.dp)
                .size(widthNumberCell, heightNumberCell)
                .background(getRouletteColor(number), CircleShape)
                .clickable {
                    clicked = !clicked
                    onClick(RouletteBet(BoxUsed.NUMBER, number, clicked))
                }
        ) {
            val textModifier = if (landscape)
                Modifier.align(Alignment.Center).graphicsLayer( rotationZ = 90f)
                    else
                Modifier.align(Alignment.TopCenter)
            Text(
                modifier = textModifier,
                text = "${number}",
                fontSize = 14.sp,
                color = Color.White
            )
            if (clicked)
                RouletteToken()
        }
    }
}

@Composable
fun RouletteVerticalBar(
    bets: List<RouletteBet>,
    id: Int,
    modifier: Modifier = Modifier,
    text: String? = null,
    color: Color? = null,
    resource: Int? = null,
    onClick: (RouletteBet) -> Unit
) {
    var clicked by remember { mutableStateOf(false)}
    val hasBet = bets.count{ it.id == id } > 0
    if (clicked) { if (!hasBet) clicked = false } else if (hasBet) clicked = true

    Column (modifier .size(widthCell, heightCell * 4)){
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxSize()
                .border(1.dp, LINES_COLOR)
                .clickable {
                    clicked = !clicked
                    onClick(RouletteBet(type = BoxUsed.SIDEBAR, id = id, clicked))
                }
        ) {
            if (text != null) {
                Text(
                    text = text,
                    modifier = Modifier.graphicsLayer { rotationZ = 90f },
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = color ?: LINES_COLOR
                )
            }
            if (resource != null) {
                GcImage(
                    imageResource = resource,
                    modifier = Modifier.size(14.dp, 24.dp),
                    onClick = {
                        clicked = !clicked
                        onClick(RouletteBet(type = BoxUsed.SIDEBAR, id = id, clicked))
                    }
                )
            }
            if (hasBet)
                RouletteToken()
        }
    }
}

@Composable
fun RouletteToken(
    modifier: Modifier = Modifier,
) {
    Canvas(modifier.size(heightCell / 2)) {
        drawCircle(
            color = TOKEN_COLOR,
            radius = (size.minDimension / 7) * 2,
        )
    }
}

@Composable
fun SingleSpacer(
    modifier: Modifier = Modifier,
) {
    Spacer(modifier.size(widthCell, 10.dp))
}

@Preview
@Composable
private fun PreviewFunc() {
    GcTheme {
        Surface {
            RouletteTable(Modifier.background(TABLE_COLOR_GREEN))
        }
    }
}

@Composable
private fun GcImage(
    imageResource: org.jetbrains.compose.resources.DrawableResource,
    modifier: Modifier = Modifier,
    contentScale: androidx.compose.ui.layout.ContentScale = androidx.compose.ui.layout.ContentScale.Fit,
    onClick: (() -> Unit)? = null
) {
    androidx.compose.foundation.Image(
        painter = org.jetbrains.compose.resources.painterResource(imageResource),
        contentDescription = null,
        modifier = if (onClick != null) modifier.clickable { onClick() } else modifier,
        contentScale = contentScale
    )
}

@Composable
private fun GcImage(
    imageResource: Int?,
    modifier: Modifier = Modifier,
    contentScale: androidx.compose.ui.layout.ContentScale = androidx.compose.ui.layout.ContentScale.Fit,
    onClick: (() -> Unit)? = null
) {
    // No-op box for dummy / layout placeholder integer resources
    androidx.compose.foundation.layout.Box(modifier)
}
