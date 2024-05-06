# Spring Data JDBC 연습
- 공식 블로그 글을 번역하며 실습합니다.

## 목차
1. [스프링 데이터 JDBC 소개](https://github.com/sharpie1330/data_jdbc_practice/wiki/%EC%8A%A4%ED%94%84%EB%A7%81-%EB%8D%B0%EC%9D%B4%ED%84%B0-JDBC-%EC%86%8C%EA%B0%9C)
2. [Spring Data JDBC, References, and Aggregates](https://github.com/sharpie1330/data_jdbc_practice/wiki/Spring-Data-JDBC,-References,-and-Aggregates)


이제 ID에 대해 시작해보자. 특히 엔티티의 ID를 데이터베이스에 맡기지 않고 직접 제어하고 싶을 때의 옵션에 대해 살펴보자.
그 전에 먼저 Spring Data JDBC의 기본 전략을 다시 한 번 짚어보자.

기본적으로 Spring Data JDBC는 ID가 SERIAL, IDENTITY 또는 AUTOINCREMENT과 같은 종류의 칼럼에 의해 생성된다고 가정한다.
또 애그리거트 루트의 ID가 null인지, 원시타입인 경우 0인지 확인한다. 만약 그렇다면 애그리거트는 새로운 것으로 가정되어 애그리거트 루트에 대한 삽입이 수행된다. 
데이터베이스가 ID를 생성하고, Spring Data JDBC에 의해 애그리거트 루트에 ID가 설정된다.
ID가 null이 아니라면 애그리거트가 기존에 존재하던 것으로 가정되어 애그리거트 루트에 대해 업데이트가 수행된다.

하나의 간단한 클래스로 구성된 애그리거트를 생각해 보자.

```java
@Getter
@Setter
public class Minion {
    @Id
    @Column("minion_id")
    private Long id;
    private String name;

    Minion(String name) {
        this.name = name;
    }
}
```

나아가 기본 CrudRepository를 생각해 보자.

```java
@Repository
public interface MinionRepository extends CrudRepository<Minion, Long> {
}
```

리포지토리는 다음과 같이 autowired된다.
```java
@Autowired
private MinionRepository minionRepository;
```

다음 작업은 잘 동작한다.
```java
@Test
void minionTest() {
    Minion before = new Minion("Bob");
    assertThat(before.getId()).isNull();

    Minion after = minionRepository.save(before);

    assertThat(after.getId()).isNotNull();
}
```

하지만 이 작업은 동작하지 않는다.
```java
Minion newBefore = new Minion("Stuart");
newBefore.setId(42L);
minionRepository.save(newBefore);
```

앞서 설명한 것과 같이, Spring Data JDBC는 ID가 이미 설정되어 있기에 업데이트를 수행하려 하지만, 실제로는 애그리거트가 새 애그리거트이기 때문에 업데이트 문은 0개의 행에 영향을 미치고 예외를 발생시킨다.

이 문제를 해결할 수 있는 몇 가지 방법이 있다.
이에 대한 네 가지 접근 방식을 찾았고, 가장 쉬운 방법부터 나열했으므로 자신에게 맞는 해결책을 찾은 후에는 읽기를 중단할 수 있다.
나중에 다시 돌아와 다른 옵션에 대해 읽고 Spring Data 기술을 향상시킬 수 있다.

