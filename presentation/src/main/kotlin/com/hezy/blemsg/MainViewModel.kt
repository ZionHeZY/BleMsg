package com.hezy.blemsg

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.Context
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hezy.model.Constants
import com.hezy.model.entity.Devices
import com.hezy.model.entity.Messages
import com.hezy.model.state.BluetoothConnectionState
import com.hezy.model.state.BluetoothData
import com.hezy.model.state.ConnectionState
import com.hezy.model.state.ProgressBarState
import com.hezy.model.usecase.discovery.DiscoverDevicesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val bluetoothAdapter: BluetoothAdapter,
    private val discoverDevicesUseCase: DiscoverDevicesUseCase
) : ViewModel() {

    // UI状态
    private val _uiState = mutableStateOf(BleState())
    val uiState: State<BleState> = _uiState

    // 扫描到的设备列表
    private val _deviceList = MutableStateFlow<List<Devices>>(emptyList())
    val deviceList: StateFlow<List<Devices>> = _deviceList.asStateFlow()

    // 当前连接的设备
    private val _connectedDevice = MutableStateFlow<Devices?>(null)
    val connectedDevice: StateFlow<Devices?> = _connectedDevice.asStateFlow()

    // 消息列表
    private val _messages = MutableStateFlow<List<Messages>>(emptyList())
    val messages: StateFlow<List<Messages>> = _messages.asStateFlow()

    // 扫描状态
    private val _isScanning = MutableStateFlow(false)
    val isScanning: StateFlow<Boolean> = _isScanning.asStateFlow()

    // 蓝牙是否可用
    val isBluetoothAvailable: Boolean = true  // 通过依赖注入获取的bluetoothAdapter不会为null

    // 蓝牙是否开启
    val isBluetoothEnabled: Boolean
        get() = bluetoothAdapter.isEnabled

    /**
     * 开始扫描蓝牙设备
     */
    fun startScan() {
        if (!isBluetoothEnabled) return

        viewModelScope.launch {
            _isScanning.value = true
            _deviceList.value = emptyList() // 清空列表

            // 使用发现设备用例
            discoverDevicesUseCase().collect { result ->
                when (result) {
                    is BluetoothData.Status -> {
                        _uiState.value = _uiState.value.copy(
                            progressBarState = result.progressBarState
                        )
                    }
                    is BluetoothData.Data -> {
                        val devices = result.data
                        if (devices != null) {
                            _deviceList.value = devices as List<Devices>
                        }
                    }
                }
            }
        }
    }

    /**
     * 停止扫描
     */
    fun stopScan() {
        if (!isBluetoothEnabled) return

        viewModelScope.launch {
            _isScanning.value = false
            discoverDevicesUseCase.stopDiscovery()
        }
    }

    /**
     * 连接到设备
     */
    fun connectToDevice(device: Devices) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                progressBarState = ProgressBarState.Loading,
                bluetoothConnectionState = BluetoothConnectionState.None
            )

            // 模拟连接过程
            // 在实际实现中，这里应该调用HandleClientUseCase
            kotlinx.coroutines.delay(1000) // 模拟连接延迟

            _connectedDevice.value = device
            _uiState.value = _uiState.value.copy(
                progressBarState = ProgressBarState.Idel,
                bluetoothConnectionState = BluetoothConnectionState.Connected
            )
        }
    }

    /**
     * 断开连接
     */
    fun disconnect() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                progressBarState = ProgressBarState.Loading
            )

            // 模拟断开连接
            // 在实际实现中，这里应该调用关闭连接的UseCase
            kotlinx.coroutines.delay(500) // 模拟断开延迟

            _connectedDevice.value = null
            _uiState.value = _uiState.value.copy(
                progressBarState = ProgressBarState.Idel,
                bluetoothConnectionState = BluetoothConnectionState.None
            )
        }
    }

    /**
     * 发送消息
     */
    fun sendMessage(message: String) {
        if (message.isBlank()) return

        viewModelScope.launch {
            // 模拟发送消息
            // 在实际实现中，这里应该调用WriteMessageUseCase
            val newMessage = Messages(
                message = message,
                time = System.currentTimeMillis(),
                type = Constants.MESSAGE_TYPE_SENT
            )

            val currentMessages = _messages.value.toMutableList()
            currentMessages.add(newMessage)
            _messages.value = currentMessages
        }
    }

    /**
     * 接收消息（模拟）
     */
    fun receiveMessage(message: String) {
        viewModelScope.launch {
            val newMessage = Messages(
                message = message,
                time = System.currentTimeMillis(),
                type = Constants.MESSAGE_TYPE_RECEIVED
            )

            val currentMessages = _messages.value.toMutableList()
            currentMessages.add(newMessage)
            _messages.value = currentMessages
        }
    }

    /**
     * 清空消息列表
     */
    fun clearMessages() {
        _messages.value = emptyList()
    }

    /**
     * 处理蓝牙事件
     */
    fun onBleEvent(event: BleEvent) {
        when (event) {
            is BleEvent.ChangeProgressBarStatus -> {
                _uiState.value = _uiState.value.copy(progressBarState = event.status)
            }
            is BleEvent.NotificationReceived -> {
                receiveMessage(event.msg)
            }
        }
    }
}