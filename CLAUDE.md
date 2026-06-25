# CLAUDE.md — Forecourt (부동산 앞마당)

> 매 세션 자동 로드되는 **컨텍스트 주입 파일**(문서가 아님). 짧게 유지 — 목표 <100줄.
> 전역 규칙(`~/.claude/CLAUDE.md`)은 그대로 적용. 여기엔 **이 프로젝트에서 Claude가 모르면 틀릴 것**만.
> 설계·결정·데이터 소스 상세는 @DESIGN.md (단일 출처). 지금은 기획 단계 — 이 파일은 코드가 생기며 자란다.

## 명령어  ⚠️ 코드 착수 시 **가장 먼저 채울 것** (최고 ROI)
- 빌드: _TBD_
- 단일 테스트: _TBD_
- 로컬 실행: _TBD_

## 스택 (서버리스 토폴로지 — 상주 서버 0개)
**수집기** Java 배치 JAR(국토부 수집·aptSeq매칭·평단가정규화·지표계산) → **GitHub Actions cron**(무료·증분), 백필은 로컬/Actions 수동 1회 · **DB+Auth+Storage** Supabase(PostgREST 직접 읽기·RLS·Google OAuth) · **프론트** 정적 SPA(Vercel/Cloudflare Pages, Supabase 직접 접근, 폰 OK) · 데이터 국토부 실거래가 API(@DESIGN.md §4.1)
- ⚠️ 상주 백엔드 없음. Fly/Railway 폐기. 프론트는 서버 경유 없이 Supabase 직접. 상세 @DESIGN.md §6.

## 코드 위치
**Java 수집기**: `domain/collector` 수집(백필/증분) · `domain/market` 분석→지표 적재 · `global/` 설정·예외·API클라이언트
**정적 프론트**(별도 프로젝트): 로그인·워치리스트·비교·차트·메모·사진. 워치리스트/메모/사진은 Supabase 테이블+RLS(공용 거래데이터 vs 개인데이터 분리, @DESIGN.md §7)

## 용어 (프로젝트 전용 jargon)
- **LAWD_CD** — 법정동 시군구 코드 5자리. 국토부 API 핵심 파라미터.
- **백필 / 증분** — 등록 시 과거 3~5년 일괄수집 / 이후 최신 1~2개월만.
- **평단가 정규화** — 면적이 달라도 평당 단가로 환산. 모든 비교의 기본 단위.

## 함정 (Claude가 기본값으로 틀리는 것들)
- **IMPORTANT: 호가 시세 스크래핑 금지.** 실거래가 API만 사용(약관 리스크).
- **단지 매칭** — 응답에 단지 ID `aptSeq`(예 `11680-364`)가 **있다**(검증됨, 월 무관 고정). 1차 키로 `aptSeq` 사용, 단지명 정규화는 보조. 한 단지가 여러 seq일 수 있음(단지1↔aptSeqN). 상세 @DESIGN.md §4.1·§8.
- **API 호출** — User-Agent 없으면 게이트웨이가 `Request Blocked` 400. Encoding 인증키 그대로. `numOfRows` 기본 10건 함정 → 크게+페이징. `dealAmount`는 `"550,000"` 만원·콤마 문자열, `excluUseAr`=전용면적(공급면적 없음).
- **수집 단위** — 국토부 API는 시군구×월. 단지 직접 조회 불가 → 응답 필터링 필수.
- **스케줄러** — 상주 백엔드 없음 → `@Scheduled` 쓰지 말 것. 증분은 **GitHub Actions cron**이 JAR 실행. 매일 도는 적재가 Supabase 7일 일시정지 keep-alive 겸함.
- **인증/보안** — 정적 프론트가 Supabase를 직접 접근(public anon 키 노출) → 사적 데이터(메모·사진·워치리스트)는 **RLS 필수**(`auth.uid()`). Google OAuth. 공용 거래데이터만 익명 읽기 허용.
- **시크릿** — serviceKey·DB 비번은 환경변수. `.env` 커밋 금지.

## 작업 규칙 (프로젝트 한정)
- 설계 바뀌면 @DESIGN.md 먼저 갱신 후 코드.
- 브랜치·커밋·PR 컨벤션 → `docs/GIT_CONVENTION.md`. push는 사용자가 직접.

<!-- 이 파일은 투기적으로 늘리지 말 것. Claude가 실제로 틀린 것만 함정/규칙에 추가. -->
<!-- 코드 착수 후: 명령어 채우기 → .env.example 키 목록 → 테스트 규칙 순으로 보강. -->
