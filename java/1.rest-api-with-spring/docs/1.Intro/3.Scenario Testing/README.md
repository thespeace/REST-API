# Events API 시나리오 테스트

1. (토큰 없이) 이벤트 목록 조회 (http://localhost:8080/api/events)
   * create 안 보임 
2. access token 발급 받기 (A 사용자 로그인) (http://localhost:8080/oauth/token)
   * +Authorization: username, password
   * +Body: username, password
3. (유효한 A 토큰 가지고) 이벤트 목록 조회 (http://localhost:8080/api/events) 
   * create event 보임
4. (유효한 A 토큰 가지고) 이벤트 만들기 (http://localhost:8080/api/events)
   * +Authorization: Token
   * +Body: Sample JSON
5. (토큰 없이) 이벤트 조회 (http://localhost:8080/api/events/?)
   * update 링크 안 보임
6. (유효한 A 토큰 가지고) 이벤트 조회 (http://localhost:8080/api/events/?)
   * +Authorization: Token
   * update 링크 보임
7. access token 발급 받기 (B 사용자 로그인) (http://localhost:8080/oauth/token)
   * +Authorization: username, password
8. (유효한 B 토큰 가지고) 이벤트 조회 (http://localhost:8080/api/events/?)
   * update 안 보임


## 테스트 클라이언트 애플리케이션
* 크롬 플러그인
  * Restlet
* 애플리케이션
  * Postman
