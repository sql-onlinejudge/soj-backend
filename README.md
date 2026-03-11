# Querify — SQL Online Judge

SQL 쿼리 문제를 풀고 자동으로 채점하는 온라인 저지 플랫폼입니다.

## 기술 스택

| 분류 | 기술 |
|------|------|
| Language / Runtime | Kotlin 2.x, Java 21 |
| Framework | Spring Boot 3.4.1 |
| ORM / Migration | Exposed 0.47, Flyway 10.6 |
| Auth | Spring Security, OAuth2 (Google / GitHub), JWT |
| Database | MySQL 8, Redis 7, MongoDB, Elasticsearch 9 |
| Messaging | Apache Kafka |
| Container | Docker SDK (샌드박스 격리 실행) |
| Monitoring | Prometheus, Micrometer, Sentry |

## 주요 기능

- **SQL 채점** — 사용자 쿼리를 격리된 MySQL 샌드박스에서 실행하고 기댓값과 비교
- **판정** — ACCEPTED / WRONG_ANSWER / TIME_LIMIT_EXCEEDED / RUNTIME_ERROR / INVALID_QUERY
- **실시간 결과** — SSE(Server-Sent Events)로 채점 상태 스트리밍
- **문제 / 워크북 관리** — CRUD, 난이도·키워드 필터링, 페이지네이션
- **인증** — Google·GitHub OAuth, 로컬 계정, 게스트 → 정식 계정 병합
- **캐싱** — Redis 기반 (문제 30분, 유저 60분, 테스트케이스 30분)
- **관리자 대시보드** — 통계, 최근 제출 목록, 전체 제출 조회

## 도메인 구조

```
src/main/kotlin/me/suhyun/soj/
├── domain/
│   ├── auth/          # OAuth, JWT, 게스트 병합
│   ├── user/          # 유저 엔티티 (LOCAL / GITHUB / GOOGLE)
│   ├── problem/       # 문제 CRUD
│   ├── testcase/      # 테스트케이스 관리
│   ├── submission/    # 제출 & SSE
│   ├── run/           # 즉석 SQL 실행 & SSE
│   ├── grading/       # 채점 엔진 (ResultComparator, ExecutionService)
│   ├── workbook/      # 워크북 & 문제 묶음
│   └── admin/         # 대시보드
└── global/            # Security, Cache, Kafka, Logging, Infra
```