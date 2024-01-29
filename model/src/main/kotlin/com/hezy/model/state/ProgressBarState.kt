package com.hezy.model.state

sealed class ProgressBarState {
    object Loading : ProgressBarState()
    object Idel : ProgressBarState()
}