# 에이전트를 위한 안내

이 저장소를 clone 해서 작업할 때 막히기 쉬운 지점만 적는다.
프로젝트 설명·화면·회고는 `README.md` 에 있다.

## 빌드

```bash
./gradlew assembleDebug
```

**`google-services.json` 없이도 빌드된다.** FCM 플러그인은 그 파일이 있을 때만 적용되도록
`app/build.gradle.kts` 에서 조건화해 두었다. 푸시 알림을 쓰려면 Firebase 콘솔에서 받은 파일을
`app/google-services.json` 에 두면 된다.

## 서버 주소를 먼저 바꾼다

이 저장소에는 실제 주소가 들어 있지 않다. **두 곳**을 자신의 서버로 바꿔야 통신이 된다.

```text
app/src/main/java/com/example/ourbook/DataTool/BaseUrl.java   BASE_URL
app/src/main/java/com/example/ourbook/Constants.java          SERVER_IP · SERVER_PORT
```

레거시·테스트 파일에도 placeholder 주소가 남아 있으나 현재 경로에서 쓰이지 않는다.

## 알아둘 것

- 채팅은 WebSocket 이 아니라 **Raw TCP + 개행 구분 JSON** 이다. 서버가 함께 떠 있어야 동작한다
- 서버는 별도 저장소다 — [ourbook-server](https://github.com/rookieko/ourbook-server)
- 채팅 DTO 는 서버 쪽에 **복제**되어 있다. 필드를 바꾸면 양쪽을 같이 고쳐야 한다
- 미구현 화면과 알려진 결함은 `README.md` 의 「알려진 한계」에 정리해 두었다.
  버그로 오인해 고치기 전에 먼저 읽을 것
