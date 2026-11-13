package com.swagcoders.khataguard

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.swagcoders.khataguard.navigation.KhataGuardNavHost
import com.swagcoders.khataguard.ui.theme.KhataGuardTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            KhataGuardTheme {
                KhataGuardNavHost()
            }
        }
    }
}
