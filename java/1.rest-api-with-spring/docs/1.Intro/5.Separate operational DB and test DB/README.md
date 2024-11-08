# 운영 DB와 테스트 DB 분리
테스트 할 때는 계속 H2를 사용해도 좋지만 애플리케이션 서버를 실행할 때 PostgreSQL을 사용하도록 변경하자.

## PostgreSQL 드라이버(운영), H2 드라이버(테스트) 의존성 추가
```
runtimeOnly 'org.postgresql:postgresql'
testImplementation 'com.h2database:h2'
```

## Docker로 Postgres 생성 및 설정 Scripts

### 도커로 PostgreSQL 컨테이너 실행

```
docker run --name rest -p 5432:5432 -e POSTGRES_PASSWORD=pass -d postgres
```

다음을 사용하여 데이터베이스에 연결할 수 있도록 Postgres 인스턴스를 생성한다:
* database: postgres
* username: postgres
* password: pass
* post: 5432

### 도커 컨테이너에 들어가기

```
docker exec -i -t rest bash
```

컨테이너 bash가 루트 사용자로 접근.

### 데이터베이스에 연결

```
su - postgres
psql -d postgres -U postgres
```

### 데이터베이스 테이블 확인

```
\l
```

### Quit

```
\q
```

## Datasource(application.properties) 설정

```
# 운영 DB 설정
spring.datasource.username=postgres
spring.datasource.password=pass
spring.datasource.url=jdbc:postgresql://localhost:5432/postgres
spring.datasource.driver-class-name=org.postgresql.Driver

# Hibernate 관련 설정
spring.jpa.hibernate.ddl-auto=create-drop
spring.jpa.properties.hibernate.jdbc.lob.non_contextual_creation=true
spring.jpa.properties.hibernate.format_sql=true

logging.level.org.hibernate.SQL=DEBUG
logging.level.org.hibernate.type.descriptor.sql.BasicBinder=TRACE
```

## Test Database(application-test.properties) 설정

```
spring.datasource.username=sa
spring.datasource.password=
spring.datasource.url=jdbc:h2:mem:testdb
spring.datasource.driver-class-name=org.h2.Driver

spring.datasource.hikari.jdbc-url=jdbc:h2:mem:testdb

spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.H2Dialect
```

## 테스트 코드에 추가
* ```@ActiveProfiles("test")```