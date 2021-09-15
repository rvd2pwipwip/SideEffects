package com.hdesrosiers.sideeffects

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.activity.OnBackPressedDispatcher
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.hdesrosiers.sideeffects.ui.theme.SideEffectsTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContent {
      val scaffoldState = rememberScaffoldState()

      Scaffold(scaffoldState = scaffoldState) {
        val counter = produceState(initialValue = 0) {
          kotlinx.coroutines.delay(3000L)
          value = 4
        }

        if (counter.value % 5 == 0 && counter.value > 0) {
          LaunchedEffect(key1 = scaffoldState.snackbarHostState) {
            scaffoldState.snackbarHostState.showSnackbar(message = "Multiple of 5")
          }
        }

        Button(onClick = { }) {
          Text(text = "${counter.value}")
        }
      }
    }
  }
}

var i = 0

@Composable
fun SideEffectComposable(backPressedDispatcher: OnBackPressedDispatcher) {
  // share Compose state with objects not managed by compose
  SideEffect {
    i++
  }

  Button(onClick = { /*TODO*/ }) {
    Text(text = "Click me")
  }
}

@Composable
fun DisposableEffectComposable(backPressedDispatcher: OnBackPressedDispatcher) {
  val callback = remember {
    object : OnBackPressedCallback(true) {
      override fun handleOnBackPressed() {
        // Do something
      }
    }
  }
  // if the DisposableEffect keys change, the composable needs to dispose
  // (do the cleanup for) its current effect, and reset by calling the effect again
  DisposableEffect(key1 = backPressedDispatcher) {
    backPressedDispatcher.addCallback(callback)
    onDispose {
      callback.remove()
    }
  }

  Button(onClick = { /*TODO*/ }) {
    Text(text = "Click me")
  }
}

@Composable
fun LaunchedEffectComposable() {
  val scaffoldState = rememberScaffoldState()
  Scaffold(scaffoldState = scaffoldState) {
    var counter by remember { mutableStateOf(0) }

    if (counter % 5 == 0 && counter > 0) {
      // call suspend functions safely from inside a composable
      // will cancel whenever the counter conditions are false,
      // and only start when they are true or if `scaffoldState.snackbarHostState` changes.
      LaunchedEffect(key1 = scaffoldState.snackbarHostState) {
        scaffoldState.snackbarHostState.showSnackbar(message = "Multiple of 5")
      }
    }

    Button(onClick = { counter++ }) {
      Text(text = "Increment $counter")
    }
  }
}