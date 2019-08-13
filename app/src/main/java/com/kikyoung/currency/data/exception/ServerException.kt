package com.kikyoung.currency.data.exception

import com.squareup.moshi.Json
import java.io.IOException

data class ServerException(
    @field:Json(name = "error")
    val error: String?
) : IOException(error)