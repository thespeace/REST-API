# Event API 비즈니스 로직

## Event 생성 API
* 다음의 입력 값을 받는다.
  * name
  * description
  * beginEnrollmentDateTime
  * closeEnrollmentDateTime
  * beginEventDateTime
  * endEventDateTime
  * location (optional) 장소가 없으면 온라인 모임
  * basePrice (optional)
  * maxPrice (optional)
  * limitOfEnrollment

## basePrice와 maxPrice 경우의 수와 각각의 로직

| basePrice | maxPrice |                                                                                              |
|-----------|----------|----------------------------------------------------------------------------------------------|
| 0         | 100      | 선착순 등록                                                                                       |
| 0         | 0        | 무료                                                                                           |
| 100       | 0        | 무제한 경매(높은 금액 낸 사람이 등록)                                                                       |
| 100       | 200      | 제한가 선착순 등록<br/><br/>처음 부터 200을 낸 사람은 선 등록<br/><br/>100을 내고 등록할 수 있으나 더 많이 낸 사람에 의해 밀려날 수 있다. |

* 결과값
  * id
  * name
  * ...
  * **eventStatus: DRAFT**, PUBLISHED, ENROLLMENT_STARTED, ...
  * offline
  * free
  * _links
    * profile (for the self-descriptive message)
    * self
    * publish
    * ...