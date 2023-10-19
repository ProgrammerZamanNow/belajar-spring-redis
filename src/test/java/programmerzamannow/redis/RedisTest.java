package programmerzamannow.redis;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

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
}
