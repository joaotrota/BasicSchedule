package com.example.basicschedule

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.lifecycleScope
import com.example.basicschedule.ui.theme.BasicScheduleTheme

class MainActivity : ComponentActivity() {
    @ExperimentalMaterialApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BasicScheduleTheme {
                val presenter = SchedulePresenter(lifecycleScope)
                Column {
                    TopAppBar(title = { Text(text = "Schedule")} )
                    Surface(color = MaterialTheme.colors.background) {
                        Column {
                            CalendarWithHeader(Modifier.weight(1f))
                            Schedule(schedulePresenter = presenter, modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        }
    }
}
