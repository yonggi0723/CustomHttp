package com.yonggi.customhttp.multipart

import com.yonggi.customhttp.BaseHttpTest
import com.yonggi.customhttp.HttpClient
import com.yonggi.customhttp.HttpCustomBuilder
import junit.framework.TestCase.assertEquals
import org.mockito.ArgumentMatchers.any
import org.mockito.ArgumentCaptor
import org.mockito.Mockito.atLeastOnce
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import java.io.File
import org.junit.Test

class MultipartCallTest: BaseHttpTest() {

    override fun createClient(): HttpClient {
        return HttpClient(
            mapOf("Authorization" to "Bearer token",
                "Content-Type" to "multipart/form-data")
        )
    }

    // 실제 String 값으로만 매핑 되있어서
    // 테스트 안했으면, 큰일 날뻔
    @Test
    fun testMultipartFileUpload() {
        val fakeFile = File.createTempFile("test", ".txt").apply {
            writeText("file_content")
        }

        setupConnection("/upload", "upload ok")

        val builder = builder()
        builder.post("/upload", body = fakeFile)

        verify(mockOutputStream, atLeastOnce()).write(any(ByteArray::class.java))
        verify(mockConnection).setRequestProperty("Content-Type", "multipart/form-data")
    }

    @Test
    fun testMultipartMapUpload() {
        val formData = mapOf("name" to "com/yonggi", "type" to "engineer")
        setupConnection("/form", "form ok")

        val builder = builder()
        builder.post("/form", body = formData)

        verify(mockOutputStream, atLeastOnce()).write(any(ByteArray::class.java))
        verify(mockConnection).setRequestProperty("Content-Type", "multipart/form-data")
    }

    @Test
    fun testMultipartStringUpload() {
        val data = "just a plain string"
        setupConnection("/string", "string ok")

        val builder = builder()
        builder.post("/string", body = data)

        verify(mockOutputStream, atLeastOnce()).write(any(ByteArray::class.java))
        verify(mockConnection).setRequestProperty("Content-Type", "multipart/form-data")
    }


    // 검증 안했으면 망할 뻔했다
    // 구멍이 존재했다.
    @Test
    fun testMultipartBoundary() {
        val fakeFile = File.createTempFile("test", ".txt").apply {
            writeText("multipart content")
        }

        setupConnection("/boundary", "ok")
        `when`(mockConnection.getRequestProperty("Content-Type")).thenReturn("multipart/form-data")

        val builder = builder()
        builder.post("/boundary", fakeFile)

        val captor = ArgumentCaptor.forClass(ByteArray::class.java)
        verify(mockOutputStream, atLeastOnce()).write(captor.capture())

        val writtenData = captor.allValues
            .joinToString("") { String(it) }

        assert(writtenData.contains("WebKitFormBoundary"))
    }

    @Test
    fun testMultipart500() {
        val fakeFile = File.createTempFile("error", ".txt").apply {
            writeText("data")
        }

        val mockErrorStream = "multipart failed".byteInputStream()

        setupConnection("/fail", response = "") // inputStream 무시
        `when`(mockConnection.inputStream).thenThrow(RuntimeException("force error"))
        `when`(mockConnection.errorStream).thenReturn(mockErrorStream)

        val builder = builder()
        val response = builder.post("/fail", fakeFile)

        assertEquals("multipart failed", response)
        verify(mockConnection).setRequestMethod("POST")
    }

    @Test
    fun testMultipartFormDataMapUpload() {
        val formData: Map<*, *> = mapOf("username" to "com/yonggi", "role" to "dev")

        setupConnection("/multipart-form", "ok")

        `when`(mockConnection.getRequestProperty("Content-Type"))
            .thenReturn("multipart/form-data")

        val client = HttpClient(
            headers = mapOf("Content-Type" to "multipart/form-data")
        )

        val builder = HttpCustomBuilder.builder()
            .baseurl(baseUrl)
            .client(client)
            .connectionFactory(mockFactory)
            .build()

        val response = builder.post("/multipart-form", formData)

        assertEquals("ok", response)

        val captor = ArgumentCaptor.forClass(ByteArray::class.java)
        verify(mockOutputStream, atLeastOnce()).write(captor.capture())

        val written = captor.allValues.joinToString("") { String(it) }

        assert(written.contains("Content-Disposition: form-data; name=\"username\""))
        assert(written.contains("com/yonggi"))
        assert(written.contains("Content-Disposition: form-data; name=\"role\""))
        assert(written.contains("dev"))
    }

    @Test
    fun testMultipartWithPlainTextBody() {
        val plainText = "simple text"
        setupConnection("/multipart/text", "ok")

        `when`(mockConnection.getRequestProperty("Content-Type"))
            .thenReturn("multipart/form-data") // 이게 핵심!!

        val client = HttpClient(
            headers = mapOf("Content-Type" to "multipart/form-data")
        )

        val builder = HttpCustomBuilder.builder()
            .baseurl(baseUrl)
            .client(client)
            .connectionFactory(mockFactory)
            .build()

        val response = builder.post("/multipart/text", plainText)

        assertEquals("ok", response)

        val captor = ArgumentCaptor.forClass(ByteArray::class.java)
        verify(mockOutputStream, atLeastOnce()).write(captor.capture())

        val written = captor.allValues.joinToString("") { String(it) }
        println("🧪 Captured Body:\n$written")

        assert(written.contains("Content-Disposition"))
        assert(written.contains("simple text"))
    }

    @Test
    fun testMultipartFormDataIgnoresNonStringKeys() {
        // key가 Int인 항목 추가
        val formData = mapOf(123 to "shouldNotBeIncluded", "valid" to "shouldBeIncluded")
        setupConnection("/multipart", "ok")

        `when`(mockConnection.getRequestProperty("Content-Type"))
            .thenReturn("multipart/form-data")

        val builder = HttpCustomBuilder.builder()
            .baseurl(baseUrl)
            .client(HttpClient(mapOf("Content-Type" to "multipart/form-data")))
            .connectionFactory(mockFactory)
            .build()

        builder.post("/multipart", formData)

        val captor = ArgumentCaptor.forClass(ByteArray::class.java)
        verify(mockOutputStream, atLeastOnce()).write(captor.capture())

        val written = captor.allValues.joinToString("") { String(it) }

        // 검증: 올바른 key만 포함되어야 함
        assert(written.contains("name=\"valid\""))
        assert(!written.contains("name=\"123\""))
    }

}