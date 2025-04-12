package com.hezy.blemsg

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.hezy.blemsg.ui.screens.ChatScreen
import com.hezy.blemsg.ui.screens.DeviceListScreen
import com.hezy.blemsg.ui.theme.BleMsgTheme
import com.hezy.model.entity.Devices
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BleMsgTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    BleMsgApp(viewModel = viewModel)
                }
            }
        }
    }
}

/**
 * 应用主界面
 */
@Composable
fun BleMsgApp(viewModel: MainViewModel) {
    val navController = rememberNavController()
    var selectedDevice by remember { mutableStateOf<Devices?>(null) }

    NavHost(navController = navController, startDestination = "device_list") {
        // 设备列表屏幕
        composable("device_list") {
            DeviceListScreen(
                viewModel = viewModel,
                onNavigateToChat = { device ->
                    selectedDevice = device
                    navController.navigate("chat")
                }
            )
        }

        // 聊天屏幕
        composable("chat") {
            selectedDevice?.let { device ->
                ChatScreen(
                    viewModel = viewModel,
                    device = device,
                    onNavigateBack = {
                        navController.popBackStack()
                    }
                )
            }
        }
    }
}