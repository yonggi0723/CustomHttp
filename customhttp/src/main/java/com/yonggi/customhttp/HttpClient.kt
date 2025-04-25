package com.yonggi.customhttp

import java.net.HttpURLConnection
import java.net.URL

class HttpClient(
    val headers: Map<String, String> = emptyMap(),
    val connectTimeOut: Int = 15000,
    val readTimeOut: Int = 15000,
    val writeTimeOut: Int = 15000)