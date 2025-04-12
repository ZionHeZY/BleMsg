package com.hezy.model.usecase.discovery

import android.bluetooth.BluetoothAdapter
import com.hezy.model.entity.Devices
import com.hezy.model.gateway.BluetoothDiscoveryGateway
import com.hezy.model.state.BluetoothData
import com.hezy.model.state.ConnectionState
import com.hezy.model.state.ProgressBarState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import javax.inject.Inject

/**
 * 发现蓝牙设备用例
 */
class DiscoverDevicesUseCase @Inject constructor(
    private val bluetoothDiscoveryGateway: BluetoothDiscoveryGateway,
    private val bluetoothAdapter: BluetoothAdapter
) {
    /**
     * 开始扫描设备
     * @return 设备数据流
     */
    operator fun invoke(): Flow<BluetoothData<List<Devices>>> {
        return flow {
            emit(BluetoothData.Status(progressBarState = ProgressBarState.Loading))

            // 获取已配对设备
            val pairedDevices = bluetoothDiscoveryGateway.getPairedDevices(bluetoothAdapter)
            // 确保列表不包含null值
            val safePairedDevices = pairedDevices.filterNotNull()
            emit(BluetoothData.Data(ConnectionState.Inited, safePairedDevices))

            // 开始扫描新设备
            val discoveredDevices = mutableListOf<Devices>()
            discoveredDevices.addAll(safePairedDevices)

            bluetoothDiscoveryGateway.startDiscovery(bluetoothAdapter)
                .onStart {
                    emit(BluetoothData.Status(progressBarState = ProgressBarState.Loading))
                }
                .onCompletion {
                    emit(BluetoothData.Status(progressBarState = ProgressBarState.Idel))
                }
                .map { device ->
                    if (device != null && !discoveredDevices.contains(device)) {
                        discoveredDevices.add(device)
                    }
                    // 确保返回的列表不包含null值
                    BluetoothData.Data(ConnectionState.Connected, discoveredDevices.filterNotNull())
                }
                .collect { emit(it) }
        }
    }

    /**
     * 停止扫描
     */
    suspend fun stopDiscovery() {
        bluetoothDiscoveryGateway.stopDiscovery(bluetoothAdapter)
    }
}
