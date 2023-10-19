package programmerzamannow.redis;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.time.Duration;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;

@SpringBootTest
public class RedisTest {

  @Autowired
  private StringRedisTemplate redisTemplate;

  @Test
  void redisTemplate() {
    assertNotNull(redisTemplate);
  }

  @Test
  void string() throws InterruptedException {
    ValueOperations<String, String> operations = redisTemplate.opsForValue();

    operations.set("name", "Eko", Duration.ofSeconds(2));
    assertEquals("Eko", operations.get("name"));

    Thread.sleep(Duration.ofSeconds(3));
    assertNull(operations.get("name"));
  }

  @Test
  void list() {
    ListOperations<String, String> operations = redisTemplate.opsForList();

    operations.rightPush("names", "Eko");
    operations.rightPush("names", "Kurniawan");
    operations.rightPush("names", "Khannedy");

    assertEquals("Eko", operations.leftPop("names"));
    assertEquals("Kurniawan", operations.leftPop("names"));
    assertEquals("Khannedy", operations.leftPop("names"));
  }

  @Test
  void set() {
    SetOperations<String, String> operations = redisTemplate.opsForSet();

    operations.add("students", "Eko");
    operations.add("students", "Eko");
    operations.add("students", "Kurniawan");
    operations.add("students", "Kurniawan");
    operations.add("students", "Khannedy");
    operations.add("students", "Khannedy");

    Set<String> students = operations.members("students");
    assertEquals(3, students.size());
    assertThat(students, hasItems("Eko", "Kurniawan", "Khannedy"));
  }
}

