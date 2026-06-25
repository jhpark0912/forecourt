# Git 컨벤션 (브랜치 · 커밋 · PR)

> 저장소 자급자족 규칙. 특정 도구·스킬에 의존하지 않는다.
> 참고: 형식은 gitmoji + type 혼합의 하우스 스타일. Conventional Commits 표준(`type(scope): msg`)과
> 다르므로, 추후 changelog 자동생성(semantic-release 등)을 도입하면 표준형으로 전환 필요.

## 브랜치 네이밍
- `<type>/<간단설명>` 케밥케이스. 예: `feat/watchlist-add`, `fix/molit-retry`.
- `type`은 아래 커밋 type 어휘와 동일.
- **main 직접 작업 금지** — 브랜치 따고 PR로 머지.

## 커밋

### 형식
```
<emoji> [type](scope) 메시지
```
- **커밋엔 실제 이모지(✨)를 입력한다.** `:sparkles:` 같은 코드는 참조용 (일부 git 클라이언트는 제목에서 미렌더).
- `scope` 선택: `collector`·`market`·`watchlist`·`api` 등 변경 범위 명확할 때만.
- 메시지는 한국어, 명사형/동사형 종결(`~추가`, `~수정`), **제목 ~50자 권장.**
- **하나의 커밋에 하나의 목적.** 성격 다르면 분리.

### Type & Emoji
| Type | Emoji | 코드 | 설명 |
|------|-------|------|------|
| `feat` | ✨ | `:sparkles:` | 새로운 기능 |
| `fix` | 🐛 | `:bug:` | 버그 수정 |
| `docs` | 📝 | `:memo:` | 문서 변경 |
| `style` | 🎨 | `:art:` | 코드 스타일(로직 X) |
| `refactor` | ♻️ | `:recycle:` | 기능 변경 없는 구조 개선 |
| `test` | ✅ | `:white_check_mark:` | 테스트 추가/수정 |
| `chore` | 🔧 | `:wrench:` | 설정·빌드 스크립트 |
| `perf` | ⚡ | `:zap:` | 성능 개선 |
| `ci` | 👷 | `:construction_worker:` | CI/CD |
| `build` | 📦 | `:package:` | 빌드/의존성 |
| `revert` | ⏪ | `:rewind:` | 롤백 |

추가: 🔥`:fire:` 코드/파일 삭제 · 🗃️`:card_file_box:` DB · 🚚`:truck:` 이동·이름변경 · 🔒`:lock:` 보안 · 🚧`:construction:` WIP

### 멀티라인 & 푸터
제목 한 줄로 부족하면 빈 줄 후 body(불릿). 필요 시 푸터:
- **호환 깨짐:** `BREAKING CHANGE: <설명>`
- **이슈 연결:** `Closes #12` (해결) / `Refs #12` (참조)

예시:
```
✨ [feat](watchlist) 관심단지 등록 시 3년치 백필 추가

- MolitCollector.backfill() 구현
- 등록 직후 비동기 수집 트리거

Closes #5
```

### 하지 말 것
- 모호한 메시지(`기능 추가`, `수정`) 금지
- 한 커밋에 `feat` + `fix` 혼합 금지
- `git add .`로 전체 추가 금지 (관련 파일만)
- **push 자동 금지** — 사용자가 직접

## PR (Pull Request)

### 제목
- 커밋과 **동일 형식**: `<emoji> [type](scope) 제목`.
- squash 머지 시 PR 제목이 그대로 커밋이 되므로 위 커밋 규칙을 그대로 따른다.

### 본문 템플릿
```markdown
## 요약
무엇을, 왜 바꿨는지 1~3줄.

## 변경사항
-

## 검증
- [ ] 빌드 통과
- [ ] 테스트 통과 (명령: ____)
- [ ] (UI 변경 시) 스크린샷 첨부

## 영향 범위 / 리스크
-

## 관련
- Closes #
```

### 규칙
- **PR 하나에 하나의 목적.** 거대 PR 금지 — 리뷰 가능한 단위로.
- 리뷰 요청 전 **self-review**: diff를 다시 읽고 의도와 무관한 변경이 섞이지 않았는지 확인.
- main 머지는 **squash 권장**(히스토리 정리). 머지 커밋 메시지 = PR 제목.
- 머지 후 브랜치 삭제.
