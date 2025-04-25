package com.yonggi.customhttp

import java.io.File
import java.io.OutputStream
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLConnection
import javax.net.ssl.HttpsURLConnection
import javax.net.ssl.SSLSocketFactory

class HttpCustomBuilder  private constructor(
    val baseUrl: String,
    val client : HttpClient,
    val connectionFactory: ConnectionFactory) {

    companion object {
        // 빌더 메서드
        fun builder(): Builder {
            return Builder()
        }

        class Builder {
            private var baseUrl: String = ""
            private var client: HttpClient = HttpClient(emptyMap())
            private var connectionFactory: ConnectionFactory = ConnectionFactoryImpl()

            fun baseurl(url: String) = apply { this.baseUrl = url }
            fun client(client: HttpClient) = apply { this.client = client }
            fun connectionFactory(factory: ConnectionFactory) = apply { this.connectionFactory = factory }

            fun build(): HttpCustomBuilder {
                return HttpCustomBuilder(baseUrl, client, connectionFactory)
            }
        }
    }


    // GET 요청
    fun get(path: String): String {
        return request(path, "GET")
    }

    // POST 요청
    fun post(path: String, body: Any?): String {
        return request(path, "POST", body)
    }

    // PUT 요청
    fun put(path: String, body: Any?): String {
        return request(path, "PUT", body)
    }

    // DELETE 요청
    fun delete(path: String): String {
        return request(path, "DELETE")
    }

    // 실제 요청을 처리하는 함수
    private fun request(path: String, method: String, body: Any? = null): String {
        val connection =  connectionFactory.open(baseUrl + path).apply {
            requestMethod = method
            connectTimeout = client.connectTimeOut
            readTimeout = client.readTimeOut
            doInput = true
            setRequestProperty("Content-Type", "application/json") // 기본 Content-Type은 JSON으로 설정
        }

        if (connection is HttpsURLConnection) {
            connection.sslSocketFactory = getSSLSocketFactory()
        }

        // 헤더 설정
        for ((key, value) in client.headers) {
            connection.setRequestProperty(key, value)
        }

        // POST, PUT 요청일 경우 body 전송
        if (method == "POST" || method == "PUT") {
            connection.doOutput = true
            body?.let {
                connection.outputStream.use { os ->
                    val contentType = connection.getRequestProperty("Content-Type") ?: "application/json"
                    when (contentType) {
                        "application/x-www-form-urlencoded" -> {
                            // Form 데이터 처리
                            os.write(encodeFormData(it as Map<String, String>).toByteArray())
                        }

                        "multipart/form-data" -> {
                            writeMultipartData(os, it)
                        }

                        else -> {
                            os.write(it.toString().toByteArray())
                        }
                    }
                    os.flush()
                }
            }
        }

        if (method == "DELETE") {
            connection.doOutput = false
        }

        // 에러 응답 처리
        return try {
            connection.inputStream.bufferedReader().use { it.readText() }
        } catch (e: Exception) {
            val errorStream = connection.errorStream
            if (errorStream != null) {
                return errorStream.bufferedReader().use { it.readText() }
            } else {
                throw e
            }
        } finally {
            connection.disconnect()
        }
    }


    private fun getSSLSocketFactory(): SSLSocketFactory {
        return SSLSocketFactory.getDefault() as SSLSocketFactory
    }

    private fun encodeFormData(params: Map<String, String>): String {
        return params.entries.joinToString("&") { "${it.key}=${it.value}" }
    }

    private fun writeMultipartData(os: OutputStream, body: Any) {
        val boundary = "----WebKitFormBoundary7MA4YWxkTrZu0gW"
        val lineEnd = "\r\n"
        val twoHyphens = "--"

        os.write("$twoHyphens$boundary$lineEnd".toByteArray())

        when (body) {
            is File -> {
                // 파일 전송 처리
                os.write("Content-Disposition: form-data; name=\"file\"; filename=\"${body.name}\"$lineEnd".toByteArray())
                os.write("Content-Type: ${URLConnection.guessContentTypeFromName(body.name)}$lineEnd".toByteArray())
                os.write(lineEnd.toByteArray())

                // 파일 내용 전송
                body.inputStream().use { fileStream ->
                    fileStream.copyTo(os)  // 파일을 OutputStream으로 복사
                }

                os.write(lineEnd.toByteArray())
            }
            is Map<*, *> -> {
                val stringMap = body.entries
                    .filter { it.key is String && it.value is String }
                    .associate { it.key as String to it.value as String }

                for ((key, value) in stringMap) {
                    os.write("$twoHyphens$boundary$lineEnd".toByteArray())
                    os.write("Content-Disposition: form-data; name=\"$key\"$lineEnd".toByteArray())
                    os.write(lineEnd.toByteArray())
                    os.write(value.toByteArray())
                    os.write(lineEnd.toByteArray())
                }
            }
            else -> {
                // 기본 텍스트 데이터 처리
                os.write("Content-Disposition: form-data; name=\"data\"$lineEnd".toByteArray())
                os.write(lineEnd.toByteArray())
                os.write(body.toString().toByteArray())  // 일반 텍스트 데이터
                os.write(lineEnd.toByteArray())
            }
        }
        os.write("$twoHyphens$boundary$twoHyphens$lineEnd".toByteArray())
    }

}

