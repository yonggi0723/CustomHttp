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

