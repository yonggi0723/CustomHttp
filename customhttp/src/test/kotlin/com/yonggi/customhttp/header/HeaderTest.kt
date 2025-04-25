package com.yonggi.customhttp.header

import com.yonggi.customhttp.BaseHttpTest
import com.yonggi.customhttp.HttpClient
import com.yonggi.customhttp.HttpCustomBuilder
import junit.framework.TestCase.assertEquals
import org.mockito.ArgumentCaptor
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.atLeastOnce
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import java.io.File
import org.junit.Test

class HeaderTest: BaseHttpTest() {

    private fun createBuilder(client: HttpClient): HttpCustomBuilder {
        return HttpCustomBuilder.builder()
            .baseurl(baseUrl)
            .client(client)
            .connectionFactory(mockFactory)
            .build()
    }

    private fun clientWithContentType(type: String): HttpClient {
        return HttpClient(
            headers = mapOf(
                "Authorization" to "Bearer token",
                "Content-Type" to type
            )
        )
    }

    @Test
    fun testTimeOutInfo() {
        val customClient = HttpClient(
            headers = mapOf("Authorization" to "Bearer token"),
            connectTimeOut = 5000,
            readTimeOut = 8000,
            writeTimeOut = 10000
        )

        setupConnection("/timeout", "ok")

        val builder = createBuilder(customClient)
        builder.get("/timeout")

        verify(mockConnection).connectTimeout = 5000
        verify(mockConnection).readTimeout = 8000
    }

    @Test
    fun testContentTypeJson() {
        val client = clientWithContentType("application/json")
        setupConnection("/json", "ok")
        `when`(mockConnection.getRequestProperty("Content-Type"))
            .thenReturn("application/json")

        val builder = createBuilder(client)
        val response = builder.post("/json", """{"hello":"world"}""")

        assertEquals("ok", response)
        verify(mockOutputStream, atLeastOnce()).write(any(ByteArray::class.java))
    }

    @Test
    fun testContentTypeForm() {
        val client = clientWithContentType("application/x-www-form-urlencoded")
        setupConnection("/form", "form ok")
        `when`(mockConnection.getRequestProperty("Content-Type"))
            .thenReturn("application/x-www-form-urlencoded")

        val builder = createBuilder(client)
        val formBody = mapOf("name" to "yonggi", "role" to "dev")
        val response = builder.post("/form", formBody)

        assertEquals("form ok", response)

        val captor = ArgumentCaptor.forClass(ByteArray::class.java)
        verify(mockOutputStream, atLeastOnce()).write(captor.capture())
        val written = captor.allValues.joinToString("") { String(it) }

        assert(written.contains("name=yonggi") && written.contains("role=dev"))
    }

    @Test
    fun testMultiPartWrite() {
        val fakeFile = File.createTempFile("test", ".txt").apply {
            writeText("multipart content")
        }

        setupConnection("/multipart", "multipart ok")
        `when`(mockConnection.getRequestProperty("Content-Type"))
            .thenReturn("multipart/form-data")

        val client = clientWithContentType("multipart/form-data")
        val builder = createBuilder(client)
        val response = builder.post("/multipart", fakeFile)

        assertEquals("multipart ok", response)

        val captor = ArgumentCaptor.forClass(ByteArray::class.java)
        verify(mockOutputStream, atLeastOnce()).write(captor.capture())
        val writtenBody = captor.allValues.joinToString("") { String(it) }

        assert(writtenBody.contains("Content-Disposition"))
        assert(writtenBody.contains("Content-Type:"))
        assert(writtenBody.contains("filename="))
        assert(writtenBody.contains("WebKitFormBoundary") || writtenBody.contains("------"))
    }

}