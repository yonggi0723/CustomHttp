package com.yonggi.customhttp.error

import com.yonggi.customhttp.BaseHttpTest
import org.mockito.ArgumentMatchers.any
import junit.framework.TestCase.assertEquals
import org.junit.Test
import org.mockito.Mockito.never
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import java.io.IOException

class ErrorTest: BaseHttpTest() {

    @Test
    fun testGetErrorResponse() {
        val mockErrorStream = "error from server".byteInputStream()
        setupConnection("/fail", "")

        `when`(mockConnection.inputStream).thenThrow(RuntimeException("force error"))
        `when`(mockConnection.errorStream).thenReturn(mockErrorStream)

        val response = builder().get("/fail")
        assertEquals("error from server", response)
        verifyCommonHeaders("GET")
    }

    @Test
    fun testReturnsErrorResponseFromErrorStream() {
        val errorMsg = "Server Error!"
        setupConnection("/fail", errorMsg)

        `when`(mockConnection.inputStream).thenThrow(IOException("fail"))
        `when`(mockConnection.errorStream).thenReturn(errorMsg.byteInputStream())

        val response = builder().get("/fail")

        assertEquals(errorMsg, response)
        verify(mockConnection).disconnect()
    }

    @Test(expected = IOException::class)
    fun testThrowsWhenNoErrorStream() {
        setupConnection("/fail", "")
        `when`(mockConnection.inputStream).thenThrow(IOException("fail"))
        `when`(mockConnection.errorStream).thenReturn(null)

        builder().get("/fail") // 여기서 예외 발생
    }

    @Test
    fun testPostJsonRequest() {
        setupConnection("/post", "post ok")
        val response = builder().post("/post", """{"name":"test"}""")
        assertEquals("post ok", response)
        verifyCommonHeaders("POST")
        verify(mockOutputStream).write(any<ByteArray?>())
    }

    @Test
    fun testPutJsonRequest() {
        setupConnection("/put", "put ok")
        val response = builder().put("/put", """{"id":1,"value":"update"}""")
        assertEquals("put ok", response)
        verifyCommonHeaders("PUT")
        verify(mockOutputStream).write(any<ByteArray>())
    }

    @Test
    fun testDeleteRequest() {
        setupConnection("/delete", "delete ok")
        val response = builder().delete("/delete")
        assertEquals("delete ok", response)
        verifyCommonHeaders("DELETE")
        verify(mockConnection, never()).outputStream
    }

    @Test
    fun testErrorStreamBufferedReaderNotNull() {
        setupConnection("/some", "")

        `when`(mockConnection.inputStream).thenThrow(IOException("fail"))

        // errorStream 은 정상 스트림 제공
        `when`(mockConnection.errorStream).thenReturn("error".byteInputStream())

        val builder = builder()
        val result = builder.get("/some")

        assertEquals("error", result)  // 이 분기를 타면 조건은 true

        verify(mockConnection).errorStream
    }
}