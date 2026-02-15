# CLAUDE.md

Behavioral guidelines to reduce common LLM coding mistakes. Merge with project-specific instructions as needed.

**Tradeoff:** These guidelines bias toward caution over speed. For trivial tasks, use judgment.

## 1. Think Before Coding

**Don't assume. Don't hide confusion. Surface tradeoffs.**

Before implementing:
- State your assumptions explicitly. If uncertain, ask.
- If multiple interpretations exist, present them - don't pick silently.
- If a simpler approach exists, say so. Push back when warranted.
- If something is unclear, stop. Name what's confusing. Ask.

## 2. Simplicity First

**Minimum code that solves the problem. Nothing speculative.**

- No features beyond what was asked.
- No abstractions for single-use code.
- No "flexibility" or "configurability" that wasn't requested.
- No error handling for impossible scenarios.
- If you write 200 lines and it could be 50, rewrite it.

Ask yourself: "Would a senior engineer say this is overcomplicated?" If yes, simplify.

## 3. Surgical Changes

**Touch only what you must. Clean up only your own mess.**

When editing existing code:
- Don't "improve" adjacent code, comments, or formatting.
- Don't refactor things that aren't broken.
- Match existing style, even if you'd do it differently.
- If you notice unrelated dead code, mention it - don't delete it.

When your changes create orphans:
- Remove imports/variables/functions that YOUR changes made unused.
- Don't remove pre-existing dead code unless asked.

The test: Every changed line should trace directly to the user's request.

## 4. Goal-Driven Execution

**Define success criteria. Loop until verified.**

Transform tasks into verifiable goals:
- "Add validation" → "Write tests for invalid inputs, then make them pass"
- "Fix the bug" → "Write a test that reproduces it, then make it pass"
- "Refactor X" → "Ensure tests pass before and after"

For multi-step tasks, state a brief plan:
```
1. [Step] → verify: [check]
2. [Step] → verify: [check]
3. [Step] → verify: [check]
```

Strong success criteria let you loop independently. Weak criteria ("make it work") require constant clarification.

---

**These guidelines are working if:** fewer unnecessary changes in diffs, fewer rewrites due to overcomplication, and clarifying questions come before implementation rather than after mistakes.
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
- API 문서: springdoc-openapi 3.0.1 (Scalar UI)
- 테스트: Kotest (DescribeSpec) + MockK, 컨트롤러 테스트는 JUnit 5 + @WebFluxTest
- 베이스 패키지: `com.ddingmin.jjalb`

## 아키텍처 패턴

- 값 객체: `OriginalUrl`, `ShortCode` — 도메인 불변식 보장
  - `OriginalUrl`: 프로토콜 화이트리스트(http/https), 내부 IP 차단, 길이 제한(2048자), URI 형식 검증
- 전략 패턴: `CodeGenerator` 인터페이스 → `SqidsCodeGenerator` 구현 (ID + salt 기반 Sqids 인코딩)
- 이벤트 기반: 클릭 기록은 `ClickEvent` → `@EventListener` + `SupervisorJob` 코루틴 스코프로 비동기 처리
- 캐시 추상화: `LinkCacheRepository` 인터페이스 → `RedisLinkCacheRepository` 구현
- 글로벌 예외 핸들러: `@RestControllerAdvice`로 일관된 에러 응답 (`ErrorResponse`)
- API 문서: springdoc-openapi + Scalar UI (`/api/docs`), OpenAPI JSON (`/v3/api-docs`)

## 설정

- `application.yml`의 민감 정보는 환경 변수로 주입 (기본값은 로컬 개발용)
- 환경 변수: `DB_URL`, `DB_USERNAME`, `DB_PASSWORD`, `REDIS_HOST`, `REDIS_PORT`, `APP_BASE_URL`, `SQIDS_ALPHABET`, `SQIDS_MIN_LENGTH`

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
