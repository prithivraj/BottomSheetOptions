@file:OptIn(ExperimentalMaterialApi::class, ExperimentalAnimationApi::class)

package com.example.bottomsheetoptions

import android.content.Intent
import android.os.Bundle
import android.view.ViewGroup
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Button
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.Text
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {

        }
    }

    override fun onStart() {
        super.onStart()
        addContentView(
            ComposeView(context = this).apply {
                setContent {

                    val navController = rememberAnimatedNavController()
                    AnimatedNavHost(
                        navController = navController,
                        startDestination = "home"
                    ) {
                        composable("home") {
                            val modalBottomSheetState =
                                rememberModalBottomSheetState(initialValue = ModalBottomSheetValue.Expanded)
                            val coroutineScope = rememberCoroutineScope()
                            BackHandler(
                                enabled = modalBottomSheetState.isVisible,
                                onBack = {
                                    coroutineScope.launch {
                                        modalBottomSheetState.hide()
                                    }
                                }
                            )
                            val context = LocalContext.current
                            ModalBottomSheetLayout(
                                sheetState = modalBottomSheetState,
                                content = {

                                },
                                sheetContent = {
                                    Column {
                                        Button(onClick = {
                                            context.startActivity(Intent(context, ChatActivity::class.java))
                                            //overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
                                        }) {
                                            Text(text = "Chat")
                                        }
                                        repeat(5) {
                                            Text(text = "hub")
                                        }
                                    }
                                }
                            )
                        }
                        composable(route = "chat", enterTransition = {
                            slideIntoContainer(
                                AnimatedContentScope.SlideDirection.Left,
                                animationSpec = tween(200)
                            )
                        },
                            exitTransition = {
                                slideOutOfContainer(
                                    AnimatedContentScope.SlideDirection.Right,
                                    animationSpec = tween(200)
                                )
                            }
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(Color.Yellow)
                            ) {
                                repeat(5) {
                                    Text(text = "chat")
                                }
                            }
                        }
                    }
                }
            },
            ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        )
    }
}

@OptIn(ExperimentalMaterialApi::class)
fun modalBackNavElement(
    state: ModalBottomSheetState,
    coroutineScope: CoroutineScope,
    callback: (() -> Unit)? = null
) = BackNavElement.needsProcessing {
    if (state.isVisible) {
        state.hideAnd(coroutineScope = coroutineScope, thenCallback = { callback?.invoke() })
        BackNavElement.Result.CANNOT_GO_BACK
    } else {
        BackNavElement.Result.CAN_GO_BACK
    }
}

@OptIn(ExperimentalMaterialApi::class)
fun ModalBottomSheetState.hideAnd(
    coroutineScope: CoroutineScope,
    thenCallback: () -> Unit
) {
    coroutineScope.launch {
        if (isVisible) {
            hide()
            thenCallback()
        }
    }
}