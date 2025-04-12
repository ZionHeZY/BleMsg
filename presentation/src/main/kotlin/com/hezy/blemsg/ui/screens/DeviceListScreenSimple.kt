package com.hezy.blemsg.ui.screens

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Divider
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
import com.hezy.model.entity.DeviceType
import com.hezy.model.entity.Devices
import com.hezy.model.state.ProgressBarState

/**
 * 简化版设备列表屏幕
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeviceListScreenSimple(
    viewModel: MainViewModel,
    onNavigateToChat: (Devices) -> Unit
) {
    val context = LocalContext.current
    // 确保设备列表类型一致性
    val deviceList by viewModel.deviceList.collectAsState(initial = emptyList())
    // 安全处理设备列表，过滤掉可能的null值
    val safeDeviceList = deviceList.filterNotNull()
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
        android.util.Log.d("DeviceListScreenSimple", "Device list changed, size: ${deviceList.size}, safe size: ${safeDeviceList.size}")
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
            // 主内容
            if (deviceList.isEmpty() && !isScanning) {
                // 没有设备时显示提示
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
                // 将设备列表分组
                val pairedDevices = safeDeviceList.filter { it.deviceType == DeviceType.PAIRED }
                val scannedDevices = safeDeviceList.filter { it.deviceType == DeviceType.SCANNED }

                // 使用Column布局，分为上下两栏
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = 80.dp) // 为FAB留出空间
                ) {
                    // 上方显示已配对设备，固定高度
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .background(MaterialTheme.colorScheme.surface)
                    ) {
                        // 标题栏
                        Text(
                            text = "已配对设备",
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                        )

                        // 已配对设备列表
                        if (pairedDevices.isNotEmpty()) {
                            // 使用LazyColumn显示已配对设备
                            LazyColumn(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f)
                            ) {
                                items(
                                    items = pairedDevices.filter { it != null && it.deviceName.isNotBlank() && it.deviceAddress.isNotBlank() },
                                    key = { it.deviceAddress }
                                ) { device ->
                                    // 在每个设备渲染前输出日志
                                    android.util.Log.d("DeviceListScreenSimple", "Rendering paired device: ${device.deviceName} (${device.deviceAddress})")

                                    DeviceItem(
                                        device = device,
                                        isConnected = device == connectedDevice,
                                        onClick = { selectedDevice ->
                                            // 在点击回调中输出日志
                                            android.util.Log.d("DeviceListScreenSimple", "Navigating to chat with device: ${selectedDevice.deviceName}")
                                            onNavigateToChat(selectedDevice)
                                        }
                                    )
                                }
                            }
                        } else {
                            // 没有已配对设备时显示提示
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "没有已配对的设备",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    // 分隔线
                    Divider(color = MaterialTheme.colorScheme.outlineVariant)

                    // 下方显示扫描到的设备
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .background(MaterialTheme.colorScheme.surface)
                    ) {
                        // 标题栏和扫描状态
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "扫描到的设备",
                                style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier.weight(1f)
                            )

                            // 扫描状态指示器
                            if (isScanning) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(20.dp),
                                        strokeWidth = 2.dp,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = stringResource(R.string.scanning),
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                            } else {
                                // 当不在扫描时，显示“开始扫描”按钮
                                Text(
                                    text = "点击FAB开始扫描",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        // 扫描到的设备列表
                        if (scannedDevices.isNotEmpty()) {
                            // 使用LazyColumn显示扫描到的设备
                            LazyColumn(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f)
                            ) {
                                items(
                                    items = scannedDevices.filter { it != null && it.deviceName.isNotBlank() && it.deviceAddress.isNotBlank() },
                                    key = { it.deviceAddress }
                                ) { device ->
                                    // 在每个设备渲染前输出日志
                                    android.util.Log.d("DeviceListScreenSimple", "Rendering scanned device: ${device.deviceName} (${device.deviceAddress})")

                                    DeviceItem(
                                        device = device,
                                        isConnected = device == connectedDevice,
                                        onClick = { selectedDevice ->
                                            // 在点击回调中输出日志
                                            android.util.Log.d("DeviceListScreenSimple", "Navigating to chat with device: ${selectedDevice.deviceName}")
                                            onNavigateToChat(selectedDevice)
                                        }
                                    )
                                }
                            }
                        } else {
                            // 没有扫描到设备时显示提示
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    if (isScanning) {
                                        CircularProgressIndicator(
                                            modifier = Modifier.size(36.dp),
                                            strokeWidth = 3.dp
                                        )
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text(
                                            text = "正在扫描设备...",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    } else {
                                        Text(
                                            text = "没有扫描到新设备",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // 不再使用全屏加载指示器，因为我们已经在扫描到的设备部分显示了加载状态
        }
    }
}
