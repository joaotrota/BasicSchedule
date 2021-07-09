package com.example.basicschedule

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import java.time.DayOfWeek
import java.time.YearMonth

const val WEIGHT_7DAY_WEEK = 1 / 7f

@Preview
@Composable
fun WeekDaysList(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        DayOfWeek.values().toSet().forEach {
            DayOfWeek(
                modifier.weight(WEIGHT_7DAY_WEEK),
                it.name.first().toString()
            )
        }
    }
}

@Preview
@Composable
fun DayOfWeek(
    modifier: Modifier = Modifier,
    weekDay: String = "M"
) {
    Box(
        modifier.background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        Text(modifier = modifier.padding(4.dp), text = weekDay, style = TextStyle(color = Color.Gray, fontWeight = FontWeight.Bold))
    }
}

@Preview
@Composable
fun CurrentMonthDay(
    modifier: Modifier = Modifier,
    weekDay: Int = 1
) {
    Box(
        modifier
            .background(Color.White), contentAlignment = Alignment.Center
    ) {
        Text(modifier = modifier.padding(4.dp), text = weekDay.toString(), style = TextStyle(color = Color.Black, fontWeight = FontWeight.Bold))
    }
}

@Preview
@Composable
fun PreviousMonthDay(
    modifier: Modifier = Modifier,
    weekDay: Int = 1
) {
    Box(
        modifier
            .background(Color.White), contentAlignment = Alignment.Center
    ) {
        Text(modifier = modifier.padding(4.dp), text = weekDay.toString(), style = TextStyle(color = Color.LightGray, fontWeight = FontWeight.Bold))
    }
}

@Preview
@Composable
fun NextMonthDay(
    modifier: Modifier = Modifier,
    weekDay: Int = 1
) {
    Box(
        modifier
            .background(Color.White), contentAlignment = Alignment.Center
    ) {
        Text(modifier = modifier.padding(4.dp), text = weekDay.toString(), style = TextStyle(color = Color.LightGray, fontWeight = FontWeight.Bold))
    }
}

@Composable
fun Week(
    modifier: Modifier = Modifier,
    startDayOffSet: Int,
    endDayCount: Int,
    monthWeekNumber: Int,
    weekCount: Int,
    priorMonthLength: Int
) {
    Row(modifier = modifier
        .fillMaxWidth()
    ) {
        if (monthWeekNumber == 0) {
            for (i in 0 until startDayOffSet) {
                val priorDay = (priorMonthLength - (startDayOffSet - i - 1))
                PreviousMonthDay(
                    modifier = Modifier.weight(WEIGHT_7DAY_WEEK),
                    weekDay = priorDay
                )
            }
        }

        val endDay = when (monthWeekNumber) {
            0 -> 7 - startDayOffSet
            weekCount -> endDayCount
            else -> 7
        }

        for (i in 1..endDay) {
            val day = if (monthWeekNumber == 0) i else (i + (7 * monthWeekNumber) - startDayOffSet)
            CurrentMonthDay(
                modifier = Modifier.weight(WEIGHT_7DAY_WEEK),
                weekDay = day
            )
        }

        if (monthWeekNumber == weekCount && endDayCount > 0) {
            for (i in 0 until (7 - endDayCount)) {
                val nextMonthDay = i + 1
                NextMonthDay(
                    modifier = Modifier.weight(WEIGHT_7DAY_WEEK),
                    weekDay = nextMonthDay
                )
            }
        }
    }
}

@ExperimentalMaterialApi
@Preview
@Composable
fun CalendarWithHeader(
    modifier: Modifier = Modifier
) {
    val minHeight: Dp = 80.dp
    val maxHeight = 180.dp
    val minSizePx = with(LocalDensity.current) { minHeight.toPx() }
    val maxSizePx = with(LocalDensity.current) { maxHeight.toPx() }
    val swipeableState = rememberSwipeableState(1f)
    val anchors = mapOf(minSizePx to 0f, maxSizePx to 1f)

    Column(
        modifier = Modifier
            .swipeable(
                state = swipeableState,
                anchors = anchors,
                thresholds = { _, _ -> FractionalThreshold(0.3f) },
                orientation = Orientation.Vertical
            )
    ) {
        Surface(elevation = 10.dp) {
            Column(modifier = Modifier
                .padding(bottom = 5.dp)
                .background(Color.White)
            ) {
                Column(modifier = Modifier
                    .height(with(LocalDensity.current) { swipeableState.offset.value.toDp() })
                    .background(Color.White)
                ) {
                    val animationPercent = if (swipeableState.progress.to == 1f) {
                        swipeableState.progress.fraction
                    } else 1f - swipeableState.progress.fraction

                    WeekDaysList()
                    Month(animationPercent = animationPercent)
                }
                Box(modifier = Modifier.align(Alignment.CenterHorizontally), contentAlignment = Alignment.Center) {
                    Box(
                        modifier = Modifier
                            .width(64.dp)
                            .height(2.dp)
                            .background(Color.Gray)
                            .padding(bottom = 10.dp)
                            .clip(RoundedCornerShape(50))
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun Month(month: YearMonth = YearMonth.now(), animationPercent: Float = 1f) {
    val firstDayOffset = month.atDay(1).dayOfWeek.ordinal
    val monthLength = month.lengthOfMonth()
    val priorMonthLength = month.minusMonths(1).lengthOfMonth()
    val lastDayCount = (monthLength + firstDayOffset) % 7
    val weekCount = (firstDayOffset + monthLength) / 7

    for (i in 0..weekCount) {
        if (i == 1) {
            Week(
                modifier = Modifier.height(30.dp),
                startDayOffSet = firstDayOffset,
                endDayCount = lastDayCount,
                monthWeekNumber = i,
                weekCount = weekCount,
                priorMonthLength = priorMonthLength
            )
        } else {
            Week(
                modifier = Modifier
                    .height((animationPercent * 30).dp)
                    .alpha(animationPercent),
                startDayOffSet = firstDayOffset,
                endDayCount = lastDayCount,
                monthWeekNumber = i,
                weekCount = weekCount,
                priorMonthLength = priorMonthLength
            )
        }

    }
}

@Preview
@Composable
fun CurrentWeek(month: YearMonth = YearMonth.now()) {
    val firstDayOffset = month.atDay(1).dayOfWeek.ordinal
    val monthLength = month.lengthOfMonth()
    val priorMonthLength = month.minusMonths(1).lengthOfMonth()
    val lastDayCount = (monthLength + firstDayOffset) % 7
    val weekCount = (firstDayOffset + monthLength) / 7

    Week(
        startDayOffSet = firstDayOffset,
        endDayCount = lastDayCount,
        monthWeekNumber = 1,
        weekCount = weekCount,
        priorMonthLength = priorMonthLength
    )
}
