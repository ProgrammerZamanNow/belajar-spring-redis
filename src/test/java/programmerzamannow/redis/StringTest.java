package programmerzamannow.redis;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;

@SpringBootTest
public class StringTest {

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
}
