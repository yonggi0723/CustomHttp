package com.yonggi.customhttp.factory

import com.yonggi.customhttp.ConnectionFactoryImpl
import junit.framework.TestCase.assertTrue
import java.net.HttpURLConnection
import org.junit.Test

class ConnectionFactory {
    @Test
    fun testOpenReturnsHttpURLConnection() {
        val factory = ConnectionFactoryImpl()
        val url = "http://example.com"

        val connection = factory.open(url)

        assertTrue(connection is HttpURLConnection)
    }
}