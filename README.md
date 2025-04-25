# BleMsg - 蓝牙消息应用

BleMsg是一款基于蓝牙技术的Android消息应用，允许设备之间通过蓝牙进行通信。该项目采用现代Android开发实践和架构构建。

## 项目概述

BleMsg允许用户：
- 发现并连接附近的蓝牙设备
- 建立安全或非安全的蓝牙连接
- 通过蓝牙发送和接收消息
- 查看已连接设备列表
- 管理蓝牙连接

## 架构

项目遵循Clean Architecture原则，采用模块化方法，实现了高度解耦的代码结构：

### 模块

1. **Presentation模块**
   - 包含使用Jetpack Compose构建的UI组件
   - 实现ViewModels进行UI状态管理
   - 处理用户交互并显示数据
   - 包括设备列表和聊天界面等主要屏幕
   - 通过导航组件管理屏幕切换

2. **Domain模块**
   - 包含业务逻辑和用例实现
   - 实现Model模块中定义的网关接口
   - 管理蓝牙连接和通信
   - 处理客户端和服务端的不同角色
   - 通过协程处理异步蓝牙操作

3. **Model模块**
   - 包含数据模型和实体（如Devices、Messages）
   - 定义网关接口（如HandleClientGateway、HandleServerGateway）
   - 定义用例接口和抽象类
   - 保存状态类和常量
   - 不依赖于其他模块，确保依赖倒置原则

### 核心组件

- **Clean Architecture**：通过明确的层次分离关注点，确保代码可测试性和可维护性
- **MVVM模式**：ViewModels管理UI状态和业务逻辑，实现UI和业务逻辑的分离
- **依赖注入**：使用Hilt进行依赖管理，简化组件间的依赖关系
- **Kotlin协程和Flow**：用于异步操作和响应式编程，处理蓝牙操作和UI更新
- **Jetpack Compose**：现代声明式UI工具包，构建响应式用户界面

### 模块间依赖关系

- **Model模块**：不依赖其他模块，只包含接口和数据模型
- **Domain模块**：依赖Model模块，实现其定义的接口
- **Presentation模块**：依赖Domain和Model模块，使用它们提供的功能

## 蓝牙实现

应用通过几个关键组件实现蓝牙功能，并明确区分了客户端和服务端角色：

### 客户端/服务端区分

- **客户端组件**：
  - `HandleClientGateway` 接口和 `HandleClientGatewayImpl` 实现
  - `HandleClientUseCase` 用例
  - 负责主动连接到其他蓝牙设备

- **服务端组件**：
  - `HandleServerGateway` 接口和 `HandleServerGatewayImpl` 实现
  - `HandleServerUseCase` 用例
  - 负责接受其他设备的连接请求

- **共享组件**：
  - `HandleMessageGateway` 接口和 `HandleMessageGatewayImpl` 实现
  - `WriteMessageUseCase` 和 `ReadMessageUseCase` 用例
  - 负责消息的收发，无论是客户端还是服务端

### 关键功能

- **设备发现**：
  - `BluetoothDiscoveryGateway` 接口和实现
  - `DiscoverDevicesUseCase` 用例
  - 使用 Flow 实时提供发现的设备

- **安全/非安全连接**：
  - 支持安全和非安全的蓝牙连接模式
  - 使用不同的 UUID 和服务名称区分

- **消息处理**：
  - 专用组件用于读取和写入消息
  - 支持不同类型的消息（发送和接收）

- **连接管理**：
  - 正确处理连接状态和生命周期
  - 使用状态模式跟踪连接状态
  - 处理连接错误和异常情况

## 使用的技术

- **Kotlin**：主要编程语言
- **Jetpack Compose**：UI框架
- **Hilt**：依赖注入
- **协程和Flow**：异步编程
- **Material Design 3**：UI设计系统
- **Android蓝牙API**：用于蓝牙功能

## 权限

应用需要以下权限：
- `BLUETOOTH_CONNECT`：用于连接蓝牙设备
- `BLUETOOTH_SCAN`：用于发现蓝牙设备

## 代码组织与质量

项目代码组织清晰，具有以下特点：

- **架构清晰**：采用了三层架构，职责划分明确
- **解耦良好**：各模块之间通过接口和依赖注入实现解耦
- **功能完整**：包含了蓝牙设备发现、连接、消息收发等完整功能
- **客户端/服务端区分**：明确区分了客户端和服务端角色
- **代码利用率高**：所有代码类都有明确用途，没有冗余代码

项目结构符合 Clean Architecture 原则，各个组件之间的依赖关系清晰，代码复用性高。虽然有些功能（如实际的蓝牙连接和消息收发）目前是模拟实现，但框架已经搭建好，只需替换为实际实现即可。
