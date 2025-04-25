package com.yonggi.customhttp

import org.junit.Before
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import java.io.ByteArrayInputStream
import java.io.OutputStream
import java.net.HttpURLConnection
import javax.net.ssl.HttpsURLConnection

open class BaseHttpTest {

    protected val baseUrl = "http://example.com"

    protected lateinit var mockConnection: HttpURLConnection
    protected lateinit var mockFactory: ConnectionFactory
    protected lateinit var mockInputStream: ByteArrayInputStream
    protected lateinit var mockOutputStream: OutputStream

    protected open fun createClient(): HttpClient {
        return HttpClient(
            mapOf("Authorization" to "Bearer token") // 기본값
        )
    }

    @Before
    fun setup() {
        mockFactory = mock(ConnectionFactory::class.java)
        mockOutputStream = mock(OutputStream::class.java)
    }

    fun setupConnection(path: String, response: String, isHttps: Boolean = false) {
        mockConnection = if (isHttps) {
            mock(HttpsURLConnection::class.java)
        } else {
            mock(HttpURLConnection::class.java)
        }

        mockInputStream = response.byteInputStream()

        `when`(mockFactory.open(baseUrl + path)).thenReturn(mockConnection)
        `when`(mockConnection.inputStream).thenReturn(mockInputStream)
        `when`(mockConnection.outputStream).thenReturn(mockOutputStream)
    }

    fun builder(): HttpCustomBuilder {
        return HttpCustomBuilder.builder()
            .baseurl(baseUrl)
            .client(createClient())
            .connectionFactory(mockFactory)
            .build()
    }

    fun verifyCommonHeaders(method: String) {
        verify(mockConnection).setRequestProperty("Authorization", "Bearer token")
        verify(mockConnection).setRequestMethod(method)
    }
}
