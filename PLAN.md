# 진행 계획: Forecourt

> 진행 상태·다음 순서를 추적하는 작업 로드맵. **설계 상세는 @DESIGN.md가 단일 출처** — 여기선 중복하지 않고 "지금 어디까지 했고 다음에 뭘 하나"만 적는다.
> 갱신: 2026-06-26 · 현재 브랜치 `feat/collector`

## 현재 위치
기획 완료. 국토부 수집·파싱이 콘솔 출력까지 동작 검증됨(강남구 1개월 파싱 확인). **다음 = DB 적재.**

## 완료
- [x] **기획·설계 확정** — 서버리스 토폴로지, 적재·수집 운영 모델, 다중유저/Auth/RLS 범위. (@DESIGN.md)
- [x] **Spring Boot 배치 뼈대** — `web-application-type=none` + `CommandLineRunner`, JDK 17, Gradle 8.12. (`a84cb96`)
- [x] **국토부 수집·파싱** — `MolitApiClient`(User-Agent·페이징·XML) → `AptTrade` 매핑 → 콘솔 출력. (`7f93821`)
- [x] **적재·수집 운영 모델 문서화** — (LAWD_CD, deal_ym) 교체 적재·`collection_job`·`cdealType` 제외. (@DESIGN.md §4.2, `ef9c4b9`)

## 다음 (순서)

### 1. DB 적재 ← 바로 다음
- **선행(차단):** Supabase 프로젝트 + 접속정보(host/DB/비번) 준비 필요. 없으면 스키마·코드는 작성 가능하나 실행·검증 불가.
- Flyway 스키마: `apt_trade`(surrogate bigserial PK, 자연 unique 없음) + `collection_job`(lawd_cd, deal_ym_range, status, updated_at).
- **(LAWD_CD, deal_ym) 교체 적재** — 단일 트랜잭션 `DELETE → bulk INSERT`. row upsert 아님, 중복 제거 안 함. (@DESIGN.md §4.2)
- 검증: 같은 (구, 월) 2회 적재 시 행 수 멱등.

### 2. 백필 / 증분 분기
- `CollectorService`에 백필(과거 3~5년 월 루프)·증분(최근 1~2개월) 모드 분리.
- `collection_job` 상태 갱신(queued→running→done/failed) + 멱등 가드(running 중복 디스패치 무시).

### 3. 지표 계산·적재 (`domain/market`)
- 평단가 정규화·기간 변동률·신고가 판정 → Supabase 지표 뷰/테이블에 미리 계산 적재.
- 지표는 `WHERE cdeal_type IS NULL`(해제거래 제외).

### 4. 자동화 (GitHub Actions)
- 증분 cron(매일, JAR 실행) — Supabase 7일 일시정지 keep-alive 겸함.
- 자동 백필 트리거 체인: watchlist 등록/'받기 승인' INSERT → DB 웹훅 → Edge Function → `repository_dispatch` → Actions. (@DESIGN.md §6.3)

### 5. 정적 프론트 (별도 프로젝트)
빌드 순서(@DESIGN.md §10): ① 인사이트 루프(워치리스트·비교·차트) → ② Auth(Google)+RLS → ③ 개인 메모 → ④ 사진 첨부(Storage).

## 대기 / 미정
- **Supabase 접속정보** — 1번의 선행. 준비 여부 확인 필요.
- **지도 API 키** — 카카오 채택 시 JS·REST 키 발급(주소→LAWD_CD 해석, @DESIGN.md §8).
- **프론트 프레임워크** — Vite+React vs Next.js static export 미정.

## 참조
- 설계 단일 출처: @DESIGN.md
- 컨텍스트 주입: @CLAUDE.md
- Git 컨벤션: docs/GIT_CONVENTION.md
