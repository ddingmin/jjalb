# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## 프로젝트 개요

URL 단축 + 클릭 통계 서비스 (숏링크). Spring WebFlux 리액티브 패턴 학습 프로젝트.

## 빌드 & 개발 명령어

- `./gradlew build` — 빌드
- `./gradlew clean build` — 클린 빌드
- `./gradlew bootRun` — 애플리케이션 실행 (PostgreSQL + Redis 필요)
- `docker compose up -d` — 로컬 인프라 실행
- `./gradlew test` — 전체 테스트
- `./gradlew test --tests "com.ddingmin.jjalb.SomeTest"` — 단일 테스트 클래스
- `./gradlew test --tests "com.ddingmin.jjalb.SomeTest.someMethod"` — 단일 테스트 메서드

## 기술 스택 & 아키텍처

- Spring Boot 4.0.2, Kotlin 2.2.21, Java 21, Gradle 9.3.0
- 리액티브 스택: WebFlux + R2DBC(PostgreSQL) + Redis Reactive
- 코루틴: kotlinx-coroutines-reactor
- R2DBC 엔티티: data class + @Table/@Id (spring-data-relational)
- 테스트: Kotest (DescribeSpec) + MockK
- 베이스 패키지: `com.ddingmin.jjalb`

## 아키텍처 패턴

- 값 객체: `OriginalUrl`, `ShortCode` — 도메인 불변식 보장
- 전략 패턴: `CodeGenerator` 인터페이스 → `Base62CodeGenerator` 구현
- 이벤트 기반: 클릭 기록은 `ClickEvent` → `@EventListener`로 비동기 처리
- 캐시 추상화: `LinkCacheRepository` 인터페이스 → `RedisLinkCacheRepository` 구현

## 설정

- `application.yml`의 민감 정보는 환경 변수로 주입 (기본값은 로컬 개발용)
- 환경 변수: `DB_URL`, `DB_USERNAME`, `DB_PASSWORD`, `REDIS_HOST`, `REDIS_PORT`, `APP_BASE_URL`

## Git 컨벤션

### 브랜치 전략: Trunk-based Development
- `main` 브랜치에 직접 커밋 또는 매우 짧은 feature 브랜치 사용
- feature 브랜치 네이밍: `feat/설명`, `fix/설명`, `chore/설명`

### 커밋 메시지
- 영어 접두사 + 한국어 본문
- 형식: `타입: 한국어 설명`
- 타입: feat, fix, refactor, test, chore, docs, style, perf
- 예: `feat: URL 단축 API 추가`, `fix: 리다이렉트 캐시 버그 수정`

### PR 규칙
- CI(테스트) 통과 필수
- 스쿼시 머지 사용

## CI/CD

- GitHub Actions: PR → main 시 `./gradlew test` 실행
- 배포: ArgoCD GitOps 전략 (별도 관리)
