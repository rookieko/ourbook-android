# OurBook — Android

웹소설을 읽고, 쓰고, 작품별 오픈 채팅방에서 이야기하는 Android 앱입니다.

**서버 저장소:** [ourbook-server](https://github.com/rookieko/ourbook-server) — PHP REST API + Java Raw TCP 채팅 서버

| | |
|---|---|
| 역할 | 개인 프로젝트 (서버·클라이언트 전부 직접 구현) |
| 최초 개발 | 2023-12 ~ 2024-02 |
| 현재 상태 | **복구·정리 완료 (2026-09).** 실제 서버에 붙여 동작을 재확인함 |
| 성격 | production-ready 앱이 아니라 **복구·학습 프로젝트**입니다. 미구현 화면과 알려진 결함을 아래에 그대로 적었습니다 |

---

## 화면

| 로그인 | 홈 | 작품 상세 |
|---|---|---|
| ![로그인](docs/screenshots/01_login.png) | ![홈](docs/screenshots/03_home.png) | ![작품 상세](docs/screenshots/08_book_detail.png) |

| 오픈 채팅방 목록 | 채팅 (Raw TCP) | 프로필 |
|---|---|---|
| ![채팅방](docs/screenshots/05_chat_rooms.png) | ![실시간 채팅](docs/screenshots/09_chat_realtime.png) | ![프로필](docs/screenshots/07_profile.png) |

채팅 화면의 아래 세 메시지는 **2026-09-04 에 실제로 주고받은 것**입니다. 그 위는 2024년 이력이고,
가운데 날짜 구분선이 둘을 나눕니다.

> 2026-09-04, Android API 36 에뮬레이터에서 실제 서버에 접속해 촬영했습니다.
> 프로필 화면의 이메일은 가렸습니다.

---

## 기술 스택

```text
언어/빌드   Java · Gradle 8.13 · AGP 8.13.2 · JDK 21 toolchain
SDK        minSdk 31 · targetSdk 34 · compileSdk 34
네트워크    Retrofit 2.9 + OkHttp 4.10 (Moshi / Gson 컨버터)
이미지      Glide 4.16
UI         Material 3 · ViewBinding · RecyclerView · Navigation
푸시        Firebase Cloud Messaging (BOM 32.7)
실시간      Raw TCP 소켓 (포트 6080) — 라이브러리 없이 직접 구현
```

화면(Activity) **39개** — `AppCompatActivity`/`Activity` 를 상속한 클래스 기준입니다.

---

## 기술적으로 볼 만한 것

### 1. Raw TCP 채팅 클라이언트

서버와 **개행 구분 JSON** 을 주고받습니다. Socket.IO 같은 라이브러리를 쓰지 않았으므로, 클라이언트 쪽에서도 직접 다뤄야 하는 것들이 있습니다.

- 소켓 수명과 Activity 생명주기의 분리
- `Ojwt-Token` 헤더로 받은 JWT를 소켓 접속 시에도 다시 제시
- 읽음 위치(`update_chat_id` / `refresh_chat_id`) 동기화
- 소켓이 끊긴 동안 온 메시지는 FCM 푸시로 수신

프로토콜 설계 자체는 서버 저장소 README에 자세히 적었습니다.

### 2. 읽기 위치 복원

회차를 읽다 나가면 다음에 그 위치부터 이어 봅니다. 스크롤 위치와 진행률을 서버에 올려 두고, 다시 들어올 때 모달로 확인한 뒤 복원합니다.

`ReadNovelActivity` 에서 세 가지를 맞춰야 했습니다.

- `onScrollChanged` 에서 스크롤 위치와 퍼센트를 갱신 (전체 높이가 0인 초기 프레임 방어)
- 복원은 `webNovelReadScroll.post { scrollTo(...) }` — **레이아웃이 끝난 뒤라야 스크롤이 먹습니다**
- `onPause()` 에서 저장 — 사용자가 뒤로 가기로 나가도 위치가 남습니다

### 3. 알림 권한 (API 33+)

`POST_NOTIFICATIONS` 를 선언하고 `ActivityResultContracts.RequestPermission` 으로 런타임 요청합니다. `Build.VERSION_CODES.TIRAMISU` 이상에서만 요청하고, 이미 허용돼 있으면 묻지 않습니다.

![알림 권한](docs/screenshots/02_notification_permission.png)

---

## 실행 방법

**서버가 필요합니다.** [ourbook-server](https://github.com/rookieko/ourbook-server) 를 먼저 띄우세요.

### 1. 서버 주소 설정

저장소에는 실제 주소가 들어 있지 않습니다. 두 곳을 자신의 서버로 바꾸세요.

```text
app/src/main/java/com/example/ourbook/DataTool/BaseUrl.java   BASE_URL
app/src/main/java/com/example/ourbook/Constants.java          SERVER_IP · SERVER_PORT
```

### 2. 빌드

```bash
./gradlew assembleDebug
```

**`google-services.json` 없이도 빌드됩니다.** FCM 플러그인은 해당 파일이 있을 때만 적용되도록 해 두었습니다 (`app/build.gradle.kts`). 푸시 알림을 쓰려면 Firebase 콘솔에서 받은 파일을 `app/google-services.json` 에 두세요.

---

## 알려진 한계 — "구현 완료"가 아닌 것들

**과장하지 않기 위해 명시합니다.**

### 라이브러리 탭은 동작하지 않습니다

하단 네비게이션에 항목은 있으나 `MainLoginActivity` 의 `setOnItemSelectedListener` 에 `R.id.tab_library` 분기가 없습니다. 탭해도 아무 일도 일어나지 않고 홈이 유지됩니다.

### 작품 상세의 조회수·평점이 채워지지 않습니다

바인딩 실수가 아니라 **구현하지 않은 것**입니다. `BookInfoActivity.java:208` 에
`// TODO 조회수 , 점수` 주석이 있고 해당 TextView 를 설정하는 코드가 없어,
레이아웃의 정적 라벨이 그대로 보입니다. 목록 화면에서는 정상 표시됩니다.

### `ChatListenService` 는 프로토타입입니다

백그라운드 메시지 수신 서비스를 만들다 멈춘 상태입니다. **시작을 호출하는 곳이 없고**, 내부에서 `startListeningForMessages(null)` 을 호출합니다.

**채팅방 화면이 떠 있는 동안의 실시간 송수신은 동작합니다** (2026-09-04 실측). 그 외 상황에서는 FCM 푸시가 대신합니다. 백그라운드 소켓 유지는 미구현입니다.

### 자기가 보낸 메시지는 화면이 즉시 갱신되지 않습니다

전송은 성공하고 서버에 저장되지만(FCM 응답으로 확인), 보낸 사람의 RecyclerView 가 바로 갱신되지 않습니다. 방을 다시 들어가면 보입니다.

### 서버 주소가 소스에 하드코딩돼 있습니다

`BuildConfig` 주입으로 빼는 것이 맞습니다. 이번 정리에서는 우선순위를 낮게 두어 placeholder 로만 바꿨습니다.

---

## 2026년 정리에서 고친 것

| 문제 | 처리 |
|---|---|
| API 33+ 에서 푸시 알림이 표시되지 않음 (`POST_NOTIFICATIONS` 미선언) | 선언 + 런타임 요청 추가 |
| 읽기 위치가 저장되지 않음 (`onScrollChanged` 가 값을 갱신하지 않음) | 갱신 로직 + `onPause()` 저장 + `post{}` 복원 |
| 릴리스 빌드에서도 요청/응답 본문 전체가 Logcat 에 남음 | `BuildConfig.DEBUG` 로 로깅 레벨 분기 |
| 로그인 시 응답 키를 전부 Logcat 에 출력하는 루프 | 제거 |
| `ChatListenService` 가 `exported="true"` | `false` 로 변경 |
