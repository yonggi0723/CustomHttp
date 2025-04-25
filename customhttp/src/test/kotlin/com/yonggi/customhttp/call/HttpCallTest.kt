package com.yonggi.customhttp.call

import com.yonggi.customhttp.BaseHttpTest
import junit.framework.TestCase.assertEquals
import org.mockito.Mockito.never
import org.mockito.Mockito.verify
import org.junit.Test

class HttpCallTest: BaseHttpTest() {

    @Test
    fun testGet() {
        setupConnection("/path", "response")
        val response = builder().get("/path")
        assertEquals("response", response)
        verifyCommonHeaders("GET")
    }

    @Test
    fun testPost() {
        setupConnection("/post", "ok")
        val response = builder().post("/post", """{"data":"test"}""")
        assertEquals("ok", response)
        verifyCommonHeaders("POST")
    }

    @Test
    fun testPut() {
        setupConnection("/put", "ok")
        val response = builder().put("/put", """{"data":"test"}""")
        assertEquals("ok", response)
        verifyCommonHeaders("PUT")
    }

    @Test
    fun testDelete() {
        setupConnection("/delete", "deleted")
        val response = builder().delete("/delete")
        assertEquals("deleted", response)
        verifyCommonHeaders("DELETE")
    }

    @Test
    fun testHttpsGet() {
        setupConnection("/secure", "https ok",isHttps = true)
        val response = builder().get("/secure")
        assertEquals("https ok", response)
        verifyCommonHeaders("GET")
    }

    @Test
    fun testHttpsPost() {
        setupConnection("/secure/post", "https ok", isHttps = true)
        val response = builder().post("/secure/post", """{"data":"test"}""")

        assertEquals("https ok", response)
        verifyCommonHeaders("POST")
    }

    @Test
    fun testHttpsPut() {
        setupConnection("/secure/put", "https ok", isHttps = true)
        val response = builder().put("/secure/put", """{"data":"test"}""")

        assertEquals("https ok", response)
        verifyCommonHeaders("PUT")
    }

    @Test
    fun testHttpsDelete() {
        setupConnection("/secure/delete", "https deleted", isHttps = true)
        val response = builder().delete("/secure/delete")
        assertEquals("https deleted", response)
        verifyCommonHeaders("DELETE")
    }

    @Test
    fun testPostWithoutBody() {
        setupConnection("/nobody", "ok")

        val builder = builder()
        val response = builder.post("/nobody", null)

        assertEquals("ok", response)
        verify(mockConnection).doOutput = true
        verify(mockConnection, never()).outputStream // 스트림은 열리지 않음
    }

}