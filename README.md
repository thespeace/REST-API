# What is a REST API?
RESTful API는 Representational State Transfer Application Programming Interface의 약자로, 웹 애플리케이션에서 리소스(Resource)에 접근하고 조작하는 표준 방식입니다.  
REST는 HTTP의 요청 메서드(GET, POST, PUT, DELETE 등)를 사용하여 시스템 간 데이터를 교환하며, 클라이언트와 서버가 독립적으로 운영될 수 있도록 합니다.

REST API는 웹에서 리소스를 효율적이고 일관성 있게 다루기 위해 Roy Fielding의 REST 아키텍처 스타일을 기반으로 설계되었습니다. 이를 통해, 클라이언트는 필요한 리소스를 일관된 방식으로 접근하고 조작할 수 있으며, 시스템의 확장성과 유연성을 보장받을 수 있습니다.

<br>

## 5 Core Principles of REST API
1. Stateless (무상태성)
   * 각 요청은 독립적으로 처리되며, 서버는 클라이언트의 상태를 저장하지 않습니다.
   * 모든 필요한 정보는 요청에 포함되어야 합니다. 
2. Client-Server (클라이언트-서버 구조)
   * 클라이언트와 서버의 역할이 분리되어 있어, 서로 독립적으로 발전할 수 있습니다.
   * 클라이언트는 요청을 보내고, 서버는 리소스를 제공하는 역할을 수행합니다.
3. Cacheable (캐시 가능성)
   * 서버 응답은 캐시될 수 있어야 하며, 이를 통해 네트워크 트래픽과 응답 시간을 줄일 수 있습니다.
   * HTTP 헤더를 통해 응답의 캐시 가능 여부를 설정합니다.
4. Uniform Interface (일관된 인터페이스)
   * 모든 리소스는 동일한 URL 형식으로 접근하며, HTTP 메서드 (GET, POST, PUT, DELETE)를 사용해 조작할 수 있습니다.
   * 예시: GET /users/123는 ID가 123인 사용자를 조회합니다.
5. Layered System (계층화된 시스템)
   * 요청과 응답은 여러 계층을 거칠 수 있으며, 중간 서버는 보안, 로드 밸런싱 등의 기능을 수행할 수 있습니다.
   * 클라이언트는 서버가 직접 응답하는지 중간 서버를 거쳐 응답하는지 알 필요가 없습니다.

이 원칙들을 준수함으로써 RESTful API는 확장성, 성능, 유지보수성을 확보할 수 있습니다.