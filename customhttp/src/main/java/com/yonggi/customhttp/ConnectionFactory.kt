package com.yonggi.customhttp

import java.net.HttpURLConnection
import java.net.URL

interface ConnectionFactory {
    fun open(url: String): HttpURLConnection
}

class ConnectionFactoryImpl: ConnectionFactory {

    override fun open(url: String): HttpURLConnection {
        return URL(url).openConnection() as HttpURLConnection
    }

}