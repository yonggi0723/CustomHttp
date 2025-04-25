package com.yonggi.customhttp.call

import com.yonggi.customhttp.HttpClient
import com.yonggi.customhttp.HttpCustomBuilder
import org.junit.Test

class HttpLiveTest {

    private val builder = HttpCustomBuilder.builder()
        .baseurl("https://en.wikipedia.org/api/rest_v1/page")
        .client(HttpClient())
        .build()

    @Test
    fun testSummaryGet() {
        val response = builder.get("/summary/Android")
        println("📦 response = $response")
        assert(response.contains("Android")) // 간단 검증
    }

    @Test
    fun testMediaList() {
        val response = builder.get("/media-list/Android")
        println("📦 response = $response")
        assert(response.contains("revision")) // 간단 검증
    }
}