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
    val context = LocalContext.current
    val deviceList by viewModel.deviceList.collectAsState()
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
                    LazyColumn(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(deviceList) { device ->
                            DeviceItem(
                                device = device,
                                isConnected = device == connectedDevice,
                                onClick = { selectedDevice ->
                                    viewModel.connectToDevice(selectedDevice)
                                    onNavigateToChat(selectedDevice)
                                }
                            )
                        }
                        
                        item {
                            Spacer(modifier = Modifier.height(80.dp)) // 为FAB留出空间
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
