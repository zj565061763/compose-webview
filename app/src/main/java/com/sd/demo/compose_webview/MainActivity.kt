package com.sd.demo.compose_webview

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.sd.demo.compose_webview.ui.theme.AppTheme
import com.sd.lib.compose.webview.FWebView
import com.sd.lib.compose.webview.LoadingState
import com.sd.lib.compose.webview.rememberFWebViewState

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    Content()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Content() {
    val state = rememberFWebViewState()

    LaunchedEffect(state) {
        state.loadUrl("https://github.com/")
    }

    Scaffold(
        topBar = {
            Column {
                CenterAlignedTopAppBar(
                    title = { Text(text = state.pageTitle ?: "......") },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = Color.Red,
                        titleContentColor = Color.White,
                    )
                )

                state.loadingState.let {
                    if (it is LoadingState.Loading) {
                        LinearProgressIndicator(
                            progress = it.progress,
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }
                }
            }
        }
    ) {
        FWebView(
            state = state,
            modifier = Modifier.padding(it),
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun DefaultPreview() {
    AppTheme {
        Content()
    }
}

inline fun logMsg(block: () -> String) {
    Log.i("FWebView-demo", block())
}