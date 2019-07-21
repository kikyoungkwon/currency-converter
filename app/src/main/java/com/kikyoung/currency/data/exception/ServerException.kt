package com.kikyoung.currency.data.exception

import java.io.IOException

data class ServerException(val error: String?) : IOException(error)