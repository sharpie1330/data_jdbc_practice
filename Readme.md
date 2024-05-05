# Spring Data JDBC 연습
- 공식 블로그 글을 번역하며 실습합니다.

## 스프링 데이터 JDBC 소개
> [Introducing Spring Data JDBC](https://spring.io/blog/2018/09/17/introducing-spring-data-jdbc)

Spring Data JDBC의 기본 개념은 복잡한 JPA에 종속되지 않고 관계형 데이터베이스에 대한 액세스를 제공하는 것이다.
JPA는 지연 로딩, 캐싱, 더티 체킹과 같은 기능을 제공하고, 이런 기능들은 필요한 경우 유용하지만 실제로는 JPA와 그 동작에 대해 생각하기 어렵게 만들 수 있다.

지연 로딩은 예상치 못한 상황에서 비용이 많이 드는 구문을 트리거하거나 예외로 인해 실패할 수 있다.
캐싱은 실제로 엔티티의 두 버전을 비교하려는 경우 방해가 될 수 있으며, 더럽기 때문에(dirty) 모든 영속성 작업이 통과하는 단일 지점을 찾기 어렵다.

Spring Data JDBC는 훨씬 더 간단한 모델을 목표로 한다. 캐싱, 더티 체킹 또는 지연 로딩이 없다. 대신 SQL문은 리포지토리 메서드를 호출할 때만 발행된다.
해당 메서드의 결과로 반환되는 객체는 메서드가 반환되기 전에 완전히 로드된다. 
엔티티에 대한 세션과 프록시가 없고, 이 모든 것이 Spring Data JDBC를 더 쉽게 추론할 수 있게 해 준다.

물론 이 간단하 접근 방식에는 제약 조건이 따르며, 향후 게시글에서 다룰 예정이라고.
빨리 제공하기 위해 연기해야 했던 기능들도 존재한다고 한다.

### 단순한 예제
먼저 엔티티가 필요하다.
```java
public class Customer {
    @Id
    private Long id;
    private String firstName;
    private LocalDate dob;  // date of birth
}
```
Getter나 Setter는 필요하지 않다. 원하는 경우 사용해도 좋다.
유일한 요구사항은 엔티티에 Id로 어노테이션된 속성(@org.springframework.data.annotation.Id)이 있어야 한다는 것.

※ 예제와 다르게 나의 경우 아래와 같이 작성했다. 실제 db에 추가한 table과 같은 속성을 갖게 하기 위함과 기타의 이유 때문.
```java
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "customer")
public class Customer {
    @Id
    @Column(value = "customer_id")
    private Long id;
    private String firstName;
    @Column(value = "date_of_birth")
    private LocalDate dob;

    public void updateFirstName(String firstName) {
        this.firstName = firstName;
    }
}
```

다음으로는 리포지토리를 선언해야 한다.
이를 수행하는 가장 쉬운 방법은 CrudRepository를 확장하는 것.

```java
public interface CustomerRepository extends CrudRepository<Customer, Long> {}
```

마지막으로 리포지토리를 생성할 수 있도록 ApplicationContext를 구성해야 한다.

```java
@Configuration
@EnableJdbcRepositories
@RequiredArgsConstructor
public class ApplicationConfig extends AbstractJdbcConfiguration {
    private final DataSource dataSource;

    @Bean
    public NamedParameterJdbcOperations namedParameterJdbcOperations() {
        return new NamedParameterJdbcTemplate(dataSource);
    }

    @Bean
    public TransactionManager transactionManager() {
        return new DataSourceTransactionManager(dataSource);
    }
}
```
※ 예제에 제공되는 것과 다르게 application.yml에 작성한 내용을 바탕으로 datasource를 생성자 주입받아 사용했다.

EnableJdbcRepositories는 레포지토리 생성을 가능하게 해 준다.

AbstractJdbcConfiguration을 확장하면 ApplicationContext에 몇 가지 기본 빈이 추가된다.
그 메서드를 오버라이딩하면 Spring Data Jdbc의 일부 동작을 사용자 정의할 수 있다. 지금은 기본 구현을 사용한다.

정말 중요한 부분은 NamedParameterJdbcOperations다. 내부적으로 데이터베이스에 SQL문을 제출하는 데 사용된다.

TransactionManager는 엄밀히 말하면 필요하지 않다. 하지만 단일 구문 이상의 트랜잭션에 대한 지원 없이 작업하게 될 것이고, 아무도 그런 것을 원하지 않을 것이다.

이것으로 작업을 시작하는 데 필요한 모든 것이 끝났다.
이제 테스트해보자.

```java
@SpringBootTest
class CustomerRepositoryTest {

    @Autowired
    private CustomerRepository customerRepository;

    @Test
    public void createSimpleCustomer() {
        Customer customer = Customer.builder()
                .dob(LocalDate.of(1904, 5, 14))
                .firstName("Albert")
                .build();

        Customer saved = customerRepository.save(customer);
        assertThat(saved.getId()).isNotNull();

        saved.updateFirstName("Hans Albert");
        customerRepository.save(saved);

        Optional<Customer> reloaded = customerRepository.findById(saved.getId());

        assertThat(reloaded).isNotEmpty();
        assertThat(reloaded.get().getFirstName()).isEqualTo("Hans Albert");
    }
}
```

### @Query 어노테이션
CrudRepository의 기본 CRUD 메서드만으로는 부족할 것이다. 
Spring Data에서 메서드 이름으로 사용할 쿼리를 파생하는 인기 있는 기능은 다음 버저으로 연기하기로 결정했다고 한다. (게시글 발행일 2018년 기준이므로 실제 사용에서는 다를 수 있음)
그 버전이 출시될 때까지는 간단한 @Query 어노테이션으로 리포지토리 메서드에 쿼리 지정이 가능하다.

```
@Query("select customer_id, first_name, date_of_birth from customer where first_name like concat('%', :name, '%')")
    List<Customer> findByName(@Param("name") String name);
```
※ 원본 게시글의 쿼리가 제대로 동작하지 않아 concat을 사용하도록 수정했다. (mysql에서는 대소문자를 구분하지 않아 upper()과 같은 메서드 사용이 필요하지 않아 생략했다.)

파라미터 플래그를 사용해 컴파일하는 경우 @Param 어노테이션은 필요하지 않다.

업데이트 또는 삭제 문을 실행하려면 메서드에 @Modifying 어노테이션을 추가하면 된다.

새 메서드를 테스트하기 위해 다른 테스트를 만들어 보자.

```
@Test
public void findByName() {
    Customer customer = Customer.builder()
            .dob(LocalDate.of(1904, 5, 14))
            .firstName("Albert")
            .build();

    Customer saved = customerRepository.save(customer);
    assertThat(saved.getId()).isNotNull();

    Customer customerA = Customer.builder()
            .dob(LocalDate.of(1904, 5, 14))
            .firstName("Bertram")
            .build();
    customerRepository.save(customerA);

    Customer customerB = Customer.builder()
            .dob(LocalDate.of(1904, 5, 14))
            .firstName("Beth")
            .build();
    customerRepository.save(customerB);

    assertThat(customerRepository.findByName("bert")).hasSize(2);
}
```
java 객체와 해당 행 사이의 연결은 객체의 Id와 해당 타입 뿐이므로, Id를 null로 설정하고 다시 저장하면 데이터베이스에 다른 행이 생성된다.
※ 하지만 나는 칼럼을 private로 설정했고, Setter 메서드가 존재하지 않아 Id를 null로 설정할 수 없다. 따라서 Builder를 사용해 새로운 객체를 만들어 저장했다.

'bert'라는 문자열을 포함하는 like 검색을 수행하므로 Albert와 Bertram은 찾지만, Beth는 찾지 못한다.