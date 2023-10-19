package programmerzamannow.redis;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.*;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class RedisTest {

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

  @Test
  void zSet() {
    ZSetOperations<String, String> operations = redisTemplate.opsForZSet();

    operations.add("score", "Eko", 100);
    operations.add("score", "Budi", 85);
    operations.add("score", "Joko", 90);

    assertEquals("Eko", operations.popMax("score").getValue());
    assertEquals("Joko", operations.popMax("score").getValue());
    assertEquals("Budi", operations.popMax("score").getValue());
  }

  @Test
  void hash() {
    HashOperations<String, Object, Object> operations = redisTemplate.opsForHash();

//    operations.put("user:1", "id", "1");
//    operations.put("user:1", "name", "Eko");
//    operations.put("user:1", "email", "eko@example.com");

    Map<Object, Object> map = new HashMap<>();
    map.put("id", "1");
    map.put("name", "Eko");
    map.put("email", "eko@example.com");

    operations.putAll("user:1", map);

    assertEquals("1", operations.get("user:1", "id"));
    assertEquals("Eko", operations.get("user:1", "name"));
    assertEquals("eko@example.com", operations.get("user:1", "email"));

    redisTemplate.delete("user:1");
  }
}

