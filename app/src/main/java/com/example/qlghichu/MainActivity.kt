    package com.example.qlghichu

    import android.os.Bundle
    import androidx.activity.ComponentActivity
    import androidx.activity.compose.setContent
    import com.example.qlghichu.Routes.SetupNavGraph
    import com.example.qlghichu.ui.theme.QLGhiChuTheme

    class MainActivity : ComponentActivity() {
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContent {
                QLGhiChuTheme {
                    SetupNavGraph()
                }
            }
        }
    }