package com.hezy.blemsg.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.hezy.blemsg.MainViewModel
import com.hezy.blemsg.R
import com.hezy.blemsg.ui.components.MessageBubble
import com.hezy.model.entity.Devices
import com.hezy.model.state.BluetoothConnectionState
import com.hezy.model.state.ProgressBarState
import kotlinx.coroutines.delay

/**
 * 聊天屏幕
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    viewModel: MainViewModel,
    device: Devices,
    onNavigateBack: () -> Unit
) {
    val messages by viewModel.messages.collectAsState()
    val uiState by viewModel.uiState
    var messageText by remember { mutableStateOf("") }
    val listState = rememberLazyListState()

    // 自动滚动到底部
    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    // 监听蓝牙连接状态
    LaunchedEffect(Unit) {
        // 清空之前的消息
        viewModel.clearMessages()
    }

    // 监听蓝牙连接状态变化
    LaunchedEffect(uiState.bluetoothConnectionState) {
        when (uiState.bluetoothConnectionState) {
            BluetoothConnectionState.Connected -> {
                // 连接成功，显示欢迎消息
                android.util.Log.d("ChatScreen", "蓝牙连接成功")
                viewModel.receiveMessage("你好，我是${device.deviceName}！")
                delay(1000)
                viewModel.receiveMessage("我们现在已经通过蓝牙连接。")
            }
            BluetoothConnectionState.None -> {
                // 连接失败或断开
                android.util.Log.d("ChatScreen", "蓝牙连接失败或断开")
                viewModel.receiveMessage("连接失败或断开，请返回重试。")
            }
            else -> {
                // 其他状态
                android.util.Log.d("ChatScreen", "蓝牙状态: ${uiState.bluetoothConnectionState}")
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(device.deviceName) },
                navigationIcon = {
                    IconButton(onClick = {
                        viewModel.disconnect()
                        onNavigateBack()
                    }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "返回"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                // 连接状态提示
                when (uiState.bluetoothConnectionState) {
                    BluetoothConnectionState.Connected -> {
                        // 连接成功时显示绿色状态栏
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp)
                                .height(24.dp)
                                .background(MaterialTheme.colorScheme.primaryContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "已连接到 ${device.deviceName}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }
                    BluetoothConnectionState.None -> {
                        // 未连接时显示红色状态栏
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp)
                                .height(24.dp)
                                .background(MaterialTheme.colorScheme.errorContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "连接失败或断开",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onErrorContainer
                            )
                        }
                    }
                    else -> {
                        // 连接中显示黄色状态栏
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp)
                                .height(24.dp)
                                .background(MaterialTheme.colorScheme.tertiaryContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "正在连接...",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onTertiaryContainer
                            )
                        }
                    }
                }

                // 消息列表
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    state = listState
                ) {
                    items(messages) { message ->
                        MessageBubble(message = message)
                    }

                    item {
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }

                // 输入框
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = messageText,
                        onValueChange = { messageText = it },
                        modifier = Modifier.weight(1f),
                        placeholder = { Text(stringResource(R.string.type_message)) },
                        maxLines = 3
                    )

                    IconButton(
                        onClick = {
                            if (messageText.isNotBlank()) {
                                viewModel.sendMessage(messageText)
                                messageText = ""
                            }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Send,
                            contentDescription = stringResource(R.string.send),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            // 加载指示器
            if (uiState.progressBarState == ProgressBarState.Loading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
        }
    }
}
