# 📡 CustomHttp

Android에서 기본으로 제공하는 `HttpURLConnection`을 기반으로 구현된 경량 HTTP 통신 라이브러리입니다.  
외부 라이브러리 없이 GET, POST, PUT, DELETE 메서드를 지원하며, 헤더 및 바디 설정, 타임아웃 설정, JAR 패키징 및 테스트 코드가 포함되어 있습니다.

---

## ✨ 주요 기능

- `HttpURLConnection` 및 `HttpsURLConnection` 기반 구현
- HTTP 메서드 지원: GET, POST, PUT, DELETE
- 요청 헤더 및 바디 설정 가능
- 응답 데이터 형식에 관계없이 처리 가능
- API 접속 타임아웃 시간 설정 가능
- Gradle을 통한 JAR 패키징 지원
- JUnit을 활용한 테스트 케이스 포함

---

## 🛠️ 설치 방법

### Gradle 설정

`build.gradle.kts` 파일에 다음과 같이 설정합니다:

```kotlin
plugins {
    kotlin("jvm") version "1.8.0"
    `java-library`
}

group = "com.yourdomain"
version = "1.0.0"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
}
```
---
JAR 빌드
```bash
./gradlew clean build
```
---
## 📦 사용 예제

다음은 `CustomHttpClient`를 사용하여 GET 요청을 보내는 예제입니다:

```kotlin
val client = CustomHttpClient()
val request = HttpRequest(
    url = "https://api.example.com/data",
    method = HttpMethod.GET,
    headers = mapOf("Authorization" to "Bearer your_token"),
    timeout = 5000
)

val response = client.execute(request)
println("Status Code: ${response.statusCode}")
println("Response Body: ${response.body}")
```
---

## ✅ 테스트

`CustomHttpClientTest.kt` 파일에 JUnit 기반 테스트가 포함되어 있습니다.  
테스트를 실행하려면 다음 명령어를 사용하세요:

```bash
./gradlew test
```
테스트 결과는 아래 경로에서 확인할 수 있습니다:
```bash
build/reports/tests/test/index.html
```
---

## 🙌 기여

Pull Request, 이슈 등록 모두 환영합니다!  
기여 전에는 프로젝트 코드 스타일과 구조를 확인해 주세요.  
의미 있는 개선이나 버그 수정 제안은 언제나 감사히 받습니다.
