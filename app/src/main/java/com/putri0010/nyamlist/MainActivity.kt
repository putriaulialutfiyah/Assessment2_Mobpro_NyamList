package com.putri0010.nyamlist

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.putri0010.nyamlist.navigation.SetupNavGraph
import com.putri0010.nyamlist.ui.theme.NyamListTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NyamListTheme {
                SetupNavGraph()
            }
        }
    }
}

