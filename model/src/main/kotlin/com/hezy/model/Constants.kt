package com.hezy.model

import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream

object Constants {
    val DEFAULT_OUTPUT_STREAM = ByteArrayOutputStream(1024)
    val DEFAULT_INPUT_STREAM = ByteArrayInputStream(ByteArray(1024))
}