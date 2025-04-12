package com.hezy.blemsg

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
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
import com.hezy.model.usecase.client.HandleClientUseCase
import com.hezy.model.usecase.discovery.DiscoverDevicesUseCase
import com.hezy.model.usecase.message.ReadMessageUseCase
import com.hezy.model.usecase.message.WriteMessageUseCase
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
    private val discoverDevicesUseCase: DiscoverDevicesUseCase,
    private val bluetoothFactory: com.hezy.domain.factory.BluetoothFactory,
    private val handleClientUseCaseFactory: (@JvmSuppressWildcards BluetoothDevice, @JvmSuppressWildcards Boolean) -> @JvmSuppressWildcards HandleClientUseCase,
    private val readMessageUseCaseFactory: com.hezy.blemsg.di.ReadMessageUseCaseFactory,
    private val writeMessageUseCaseFactory: com.hezy.blemsg.di.WriteMessageUseCaseFactory
) : ViewModel() {

    // UI状态
    private val _uiState = mutableStateOf(BleState())
    val uiState: State<BleState> = _uiState

    // 扫描到的设备列表
    private val _deviceList = MutableStateFlow<List<Devices>>(emptyList())
    val deviceList: StateFlow<List<Devices>> = _deviceList.asStateFlow()

    // 确保设备列表中没有null值
    private fun updateDeviceList(devices: List<Devices?>) {
        val safeDevices = devices.filterNotNull()
        android.util.Log.d("MainViewModel", "Updating device list: ${safeDevices.size} devices")

        // 使用viewModelScope确保状态更新在主线程上进行
        viewModelScope.launch {
            _deviceList.emit(safeDevices)
            android.util.Log.d("MainViewModel", "Device list updated with ${safeDevices.size} devices")
        }
    }

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

            // 先清空设备列表，确保不会有旧数据
            _deviceList.emit(emptyList())
            android.util.Log.d("MainViewModel", "Starting scan, cleared device list")

            _uiState.value = _uiState.value.copy(
                progressBarState = ProgressBarState.Loading
            )

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
                            // 使用安全的方式更新设备列表
                            updateDeviceList(devices)

                            // 输出日志以便调试
                            _deviceList.value.forEach { device ->
                                android.util.Log.d("MainViewModel", "Device: ${device.deviceName} (${device.deviceAddress})")
                            }
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
        // 安全检查
        if (device.deviceName.isBlank() || device.deviceAddress.isBlank()) {
            android.util.Log.e("MainViewModel", "Invalid device: $device")
            return
        }

        android.util.Log.d("MainViewModel", "Connecting to device: ${device.deviceName} (${device.deviceAddress})")

        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(
                    progressBarState = ProgressBarState.Loading,
                    bluetoothConnectionState = BluetoothConnectionState.None
                )

                // 获取实际的BluetoothDevice对象
                val deviceAddress = device.deviceAddress
                if (deviceAddress.isBlank()) {
                    android.util.Log.e("MainViewModel", "Device address is blank")
                    return@launch
                }

                val bluetoothDevice = try {
                    bluetoothAdapter.getRemoteDevice(deviceAddress)
                } catch (e: Exception) {
                    android.util.Log.e("MainViewModel", "Error getting remote device: ${e.message}")
                    return@launch
                }

                // 创建并使用HandleClientUseCase
                val handleClientUseCase = try {
                    handleClientUseCaseFactory(bluetoothDevice, true) // 使用安全连接
                } catch (e: Exception) {
                    android.util.Log.e("MainViewModel", "Error creating HandleClientUseCase: ${e.message}")
                    return@launch
                }

                // 收集连接结果
                handleClientUseCase().collect { result ->
                    when (result) {
                        is BluetoothData.Status -> {
                            _uiState.value = _uiState.value.copy(
                                progressBarState = result.progressBarState
                            )
                        }
                        is BluetoothData.Data -> {
                            if (result.connectionState == ConnectionState.Connected) {
                                // 连接成功
                                android.util.Log.d("MainViewModel", "蓝牙连接成功: ${device.deviceName}")
                                _connectedDevice.value = device
                                _uiState.value = _uiState.value.copy(
                                    progressBarState = ProgressBarState.Idel,
                                    bluetoothConnectionState = BluetoothConnectionState.Connected
                                )

                                // 启动消息监听
                                startMessageListener()
                            } else if (result.connectionState == ConnectionState.Failed) {
                                // 连接失败
                                android.util.Log.e("MainViewModel", "蓝牙连接失败: ${device.deviceName}")
                                _uiState.value = _uiState.value.copy(
                                    progressBarState = ProgressBarState.Idel,
                                    bluetoothConnectionState = BluetoothConnectionState.None
                                )
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                // 处理异常
                _uiState.value = _uiState.value.copy(
                    progressBarState = ProgressBarState.Idel,
                    bluetoothConnectionState = BluetoothConnectionState.None
                )
            }
        }
    }

    /**
     * 启动消息监听
     */
    private fun startMessageListener() {
        android.util.Log.d("MainViewModel", "Starting message listener")
        viewModelScope.launch {
            try {
                val readMessageUseCase = try {
                    readMessageUseCaseFactory.create()
                } catch (e: Exception) {
                    android.util.Log.e("MainViewModel", "Error creating ReadMessageUseCase: ${e.message}")
                    return@launch
                }

                if (readMessageUseCase == null) {
                    android.util.Log.e("MainViewModel", "ReadMessageUseCase is null")
                    return@launch
                }

                android.util.Log.d("MainViewModel", "Message listener started successfully")

                // 持续监听消息
                while (_uiState.value.bluetoothConnectionState == BluetoothConnectionState.Connected) {
                    try {
                        readMessageUseCase().collect { result ->
                            when (result) {
                                is BluetoothData.Status -> {
                                    // 状态更新
                                    android.util.Log.d("MainViewModel", "Message status update: ${result.progressBarState}")
                                }
                                is BluetoothData.Data -> {
                                    // 收到消息
                                    val message = result.data
                                    if (message != null) {
                                        android.util.Log.d("MainViewModel", "Message received: ${message.message}")
                                        val currentMessages = _messages.value.toMutableList()
                                        currentMessages.add(message)
                                        _messages.value = currentMessages
                                    } else {
                                        android.util.Log.e("MainViewModel", "Received null message")
                                    }
                                }
                            }
                        }
                    } catch (e: Exception) {
                        android.util.Log.e("MainViewModel", "Error collecting messages: ${e.message}")
                        kotlinx.coroutines.delay(1000) // 防止循环过快
                    }
                }
            } catch (e: Exception) {
                android.util.Log.e("MainViewModel", "Error in message listener: ${e.message}")
            }
        }
    }

    /**
     * 断开连接
     */
    fun disconnect() {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(
                    progressBarState = ProgressBarState.Loading
                )

                // 关闭蓝牙Socket
                bluetoothFactory.getCurrentSocket()?.close()

                // 清理资源
                bluetoothFactory.clearCurrentSocket()
                bluetoothFactory.clearCurrentDevice()

                _connectedDevice.value = null
                _uiState.value = _uiState.value.copy(
                    progressBarState = ProgressBarState.Idel,
                    bluetoothConnectionState = BluetoothConnectionState.None
                )
            } catch (e: Exception) {
                // 处理异常
                _uiState.value = _uiState.value.copy(
                    progressBarState = ProgressBarState.Idel
                )
            }
        }
    }

    /**
     * 发送消息
     */
    fun sendMessage(message: String) {
        if (message.isBlank()) return

        viewModelScope.launch {
            try {
                // 获取WriteMessageUseCase
                val writeMessageUseCase = writeMessageUseCaseFactory.create()
                if (writeMessageUseCase == null) {
                    // 如果没有可用的WriteMessageUseCase，则模拟发送
                    val newMessage = Messages(
                        message = message,
                        time = System.currentTimeMillis(),
                        type = Constants.MESSAGE_TYPE_SENT
                    )

                    val currentMessages = _messages.value.toMutableList()
                    currentMessages.add(newMessage)
                    _messages.value = currentMessages
                    return@launch
                }

                // 使用WriteMessageUseCase发送消息
                val messageBytes = message.toByteArray()
                writeMessageUseCase(messageBytes).collect { result ->
                    when (result) {
                        is BluetoothData.Status -> {
                            // 状态更新
                            _uiState.value = _uiState.value.copy(
                                progressBarState = result.progressBarState
                            )
                        }
                        is BluetoothData.Data -> {
                            // 消息发送成功
                            if (result.connectionState == ConnectionState.Connected) {
                                // 添加到消息列表
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
                    }
                }
            } catch (e: Exception) {
                // 处理异常
                _uiState.value = _uiState.value.copy(
                    progressBarState = ProgressBarState.Idel
                )
            }
        }
    }

    /**
     * 接收消息（仅用于演示）
     * 在实际实现中，消息接收应由startMessageListener处理
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