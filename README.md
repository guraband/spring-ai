# Spring AI 스터디 프로젝트

이 프로젝트는 [Spring AI 유튜브 강의](https://youtube.com/playlist?list=PLJkjrxxiBSFCgcsP_pzuntmqC3AlTMWFx&si=QSrj6GQjQhKkQ2n4)를 따라하며 학습하는 Spring AI 스터디 프로젝트입니다.

## 📋 프로젝트 개요

- **프로젝트명**: Spring AI Study Project
- **기술 스택**: Spring Boot 3.5.4, Spring AI 1.0.1, Kotlin, Thymeleaf
- **Java 버전**: 17
- **학습 목적**: Spring AI 프레임워크를 활용한 AI 기능 구현 및 학습

## 🚀 주요 기능

- Spring AI를 활용한 AI 모델 연동
- OpenAI API 연동
- Google Vertex AI Gemini 연동
- 웹 기반 서버 상태 모니터링
- Thymeleaf 템플릿 엔진을 활용한 동적 웹 페이지

## 🛠 기술 스택

### Backend
- **Spring Boot**: 3.5.4
- **Spring AI**: 1.0.1
- **Kotlin**: 1.9.25
- **Java**: 17

### Frontend
- **Thymeleaf**: 템플릿 엔진
- **HTML/CSS**: 반응형 웹 디자인

### AI 서비스
- **OpenAI**: GPT 모델 연동
- **Google Vertex AI**: Gemini 모델 연동


## 🔧 환경 설정

### 필수 환경 변수

프로젝트 실행을 위해 다음 환경 변수를 설정해야 합니다:

```bash
# OpenAI API 설정
OPENAI_API_KEY=your_openai_api_key_here

# Google Vertex AI 설정
VERTEX_AI_PROJECT_ID=your_vertex_ai_project_id
VERTEX_AI_LOCATION=your_vertex_ai_location
```

### 환경 변수 설정 방법

1. **macOS/Linux**:
   ```bash
   export OPENAI_API_KEY="your_openai_api_key_here"
   export VERTEX_AI_PROJECT_ID="your_vertex_ai_project_id"
   export VERTEX_AI_LOCATION="your_vertex_ai_location"
   ```

2. **Windows**:
   ```cmd
   set OPENAI_API_KEY=your_openai_api_key_here
   set VERTEX_AI_PROJECT_ID=your_vertex_ai_project_id
   set VERTEX_AI_LOCATION=your_vertex_ai_location
   ```

3. **.env 파일 사용** (권장):
   프로젝트 루트에 `.env` 파일을 생성하고 다음 내용을 추가:
   ```
   OPENAI_API_KEY=your_openai_api_key_here
   VERTEX_AI_PROJECT_ID=your_vertex_ai_project_id
   VERTEX_AI_LOCATION=your_vertex_ai_location
   ```

## 🏃‍♂️ 실행 방법

### 1. 프로젝트 클론
```bash
git clone <repository-url>
cd spring-ai
```

### 2. 환경 변수 설정
위의 환경 설정 섹션을 참고하여 필요한 환경 변수를 설정합니다.

### 3. 애플리케이션 실행

### redisSearch 설치
```bash
brew tap redis/redis-stack
brew install redis-stack
redis-stack-server
# 또는
nohup redis-stack-server &
```

#### mariadb
```bash
brew services start mariadb
```

#### Gradle Wrapper 사용
```bash
./gradlew bootRun
```

#### IDE에서 실행
`SpringAiApplication.kt` 파일을 실행하거나 IDE의 실행 버튼을 클릭합니다.

### 4. 접속 확인
브라우저에서 `http://localhost:8080`으로 접속하여 애플리케이션이 정상적으로 실행되는지 확인합니다.

## 📚 학습 내용

이 프로젝트를 통해 학습하는 주요 내용:

1. **Spring AI 프레임워크 기초**
   - Spring AI 설정 및 구성
   - AI 모델 연동 방법

2. **OpenAI API 연동**
   - OpenAI API 키 설정
   - GPT 모델 활용

3. **Google Vertex AI 연동**
   - Vertex AI 프로젝트 설정
   - Gemini 모델 활용

4. **Spring Boot + Kotlin 개발**
   - Kotlin을 활용한 Spring Boot 개발
   - Thymeleaf 템플릿 엔진 활용

## 🎯 다음 단계

- [ ] AI 채팅 기능 구현
- [ ] 이미지 생성 기능 추가
- [ ] 텍스트 분석 기능 구현
- [ ] API 엔드포인트 확장
- [ ] 프론트엔드 UI 개선

## 📖 참고 자료

- [Spring AI 공식 문서](https://docs.spring.io/spring-ai/reference/)
- [Spring Boot 공식 문서](https://spring.io/projects/spring-boot)
- [Kotlin 공식 문서](https://kotlinlang.org/docs/home.html)
- [유튜브 강의 플레이리스트](https://youtube.com/playlist?list=PLJkjrxxiBSFCgcsP_pzuntmqC3AlTMWFx&si=QSrj6GQjQhKkQ2n4)

## 📄 라이선스

이 프로젝트는 학습 목적으로 제작되었습니다.

---

**참고**: 이 프로젝트는 [Spring AI 유튜브 강의](https://youtube.com/playlist?list=PLJkjrxxiBSFCgcsP_pzuntmqC3AlTMWFx&si=QSrj6GQjQhKkQ2n4)를 따라하며 학습한 내용을 바탕으로 제작되었습니다.
