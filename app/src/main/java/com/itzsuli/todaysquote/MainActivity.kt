package com.itzsuli.todaysquote

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.itzsuli.todaysquote.ui.AppRoot
import com.itzsuli.todaysquote.ui.TodaysQuoteTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            TodaysQuoteTheme(darkTheme = true) {
                AppRoot()
            }
        }
    }
}
