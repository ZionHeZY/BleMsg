package com.hezy.blemsg.ui.screens

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.hezy.blemsg.MainViewModel
import com.hezy.blemsg.R
import com.hezy.blemsg.ui.components.DeviceItem
import com.hezy.blemsg.utils.PermissionUtils
import com.hezy.model.entity.Devices
import com.hezy.model.state.ProgressBarState

/**
 * 设备列表屏幕
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeviceListScreen(
    viewModel: MainViewModel,
    onNavigateToChat: (Devices) -> Unit
) {
    // 安全包装onNavigateToChat回调
    val safeNavigateToChat: (Devices) -> Unit = { device ->
        try {
            if (device.deviceName.isNotBlank() && device.deviceAddress.isNotBlank()) {
                android.util.Log.d("DeviceListScreen", "Navigating to chat with: ${device.deviceName}")
                onNavigateToChat(device)
            } else {
                android.util.Log.e("DeviceListScreen", "Cannot navigate to chat with invalid device: $device")
            }
        } catch (e: Exception) {
            android.util.Log.e("DeviceListScreen", "Error navigating to chat: ${e.message}")
        }
    }
    val context = LocalContext.current
    val deviceList by viewModel.deviceList.collectAsState(initial = emptyList())
    val isScanning by viewModel.isScanning.collectAsState()
    val connectedDevice by viewModel.connectedDevice.collectAsState()
    val uiState by viewModel.uiState

    var showBluetoothDialog by remember { mutableStateOf(false) }
    var showPermissionDialog by remember { mutableStateOf(false) }

    // 蓝牙启用结果
    val bluetoothEnableLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == android.app.Activity.RESULT_OK) {
            // 蓝牙已启用，检查权限
            if (PermissionUtils.hasBluetoothPermissions(context)) {
                viewModel.startScan()
            } else {
                showPermissionDialog = true
            }
        }
    }

    // 权限请求结果
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val allGranted = permissions.values.all { it }
        if (allGranted && viewModel.isBluetoothEnabled) {
            viewModel.startScan()
        }
    }

    // 检查蓝牙和权限
    LaunchedEffect(key1 = Unit) {
        if (!viewModel.isBluetoothAvailable) {
            // 设备不支持蓝牙
            return@LaunchedEffect
        }

        if (!viewModel.isBluetoothEnabled) {
            showBluetoothDialog = true
        } else if (!PermissionUtils.hasBluetoothPermissions(context)) {
            showPermissionDialog = true
        } else {
            viewModel.startScan()
        }
    }

    // 监听设备列表变化
    LaunchedEffect(deviceList) {
        android.util.Log.d("DeviceListScreen", "LaunchedEffect: Device list changed, size: ${deviceList.size}")
    }

    // 蓝牙未启用对话框
    if (showBluetoothDialog) {
        AlertDialog(
            onDismissRequest = { showBluetoothDialog = false },
            title = { Text(stringResource(R.string.bluetooth_not_enabled)) },
            text = { Text(stringResource(R.string.enable_bluetooth)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        showBluetoothDialog = false
                        val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                        bluetoothEnableLauncher.launch(enableBtIntent)
                    }
                ) {
                    Text(stringResource(R.string.ok))
                }
            },
            dismissButton = {
                TextButton(onClick = { showBluetoothDialog = false }) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }

    // 权限请求对话框
    if (showPermissionDialog) {
        AlertDialog(
            onDismissRequest = { showPermissionDialog = false },
            title = { Text(stringResource(R.string.permission_required)) },
            text = { Text(stringResource(R.string.permission_rationale)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        showPermissionDialog = false
                        val permissions = PermissionUtils.getRequiredBluetoothPermissions(context)
                        permissionLauncher.launch(permissions.toTypedArray())
                    }
                ) {
                    Text(stringResource(R.string.grant_permission))
                }
            },
            dismissButton = {
                TextButton(onClick = { showPermissionDialog = false }) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.device_list)) }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    if (viewModel.isBluetoothEnabled && PermissionUtils.hasBluetoothPermissions(context)) {
                        if (isScanning) {
                            viewModel.stopScan()
                        } else {
                            viewModel.startScan()
                        }
                    } else if (!viewModel.isBluetoothEnabled) {
                        showBluetoothDialog = true
                    } else {
                        showPermissionDialog = true
                    }
                }
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = if (isScanning) stringResource(R.string.stop_scan) else stringResource(R.string.scan_devices)
                )
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // 扫描状态
                if (isScanning) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = stringResource(R.string.scanning),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }

                // 设备列表
                if (deviceList.isEmpty() && !isScanning) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = stringResource(R.string.no_devices_found),
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                } else {
                    // 安全地处理设备列表
                    val safeDeviceList = deviceList.filterNotNull()

                    // 输出日志以便调试
                    android.util.Log.d("DeviceListScreen", "Device list size: ${deviceList.size}, Safe device list size: ${safeDeviceList.size}")
                    safeDeviceList.forEach { device ->
                        android.util.Log.d("DeviceListScreen", "Device in UI: ${device.deviceName} (${device.deviceAddress})")
                    }

                    // 如果设备列表为空，显示加载中消息
                    if (safeDeviceList.isEmpty()) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "\u52a0\u8f7d\u8bbe\u5907\u5217\u8868...",
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    } else {
                        // 使用Column而不是LazyColumn，避免可能的Compose问题
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(bottom = 80.dp) // 为FAB留出空间
                        ) {
                            safeDeviceList.forEach { device ->
                                // 对每个设备进行空值检查
                                if (device.deviceName.isNotBlank() && device.deviceAddress.isNotBlank()) {
                                    DeviceItem(
                                        device = device,
                                        isConnected = device == connectedDevice,
                                        onClick = { selectedDevice ->
                                            try {
                                                // 安全检查
                                                if (selectedDevice.deviceName.isNotBlank() && selectedDevice.deviceAddress.isNotBlank()) {
                                                    android.util.Log.d("DeviceListScreen", "Connecting to device: ${selectedDevice.deviceName}")
                                                    viewModel.connectToDevice(selectedDevice)
                                                    safeNavigateToChat(selectedDevice)
                                                } else {
                                                    android.util.Log.e("DeviceListScreen", "Invalid device selected: $selectedDevice")
                                                }
                                            } catch (e: Exception) {
                                                android.util.Log.e("DeviceListScreen", "Error in onClick: ${e.message}")
                                            }
                                        }
                                    )
                                }
                            }
                        }
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
