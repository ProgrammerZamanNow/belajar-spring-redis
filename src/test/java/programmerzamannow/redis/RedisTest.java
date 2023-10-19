package programmerzamannow.redis;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataAccessException;
import org.springframework.data.geo.*;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.data.redis.core.*;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
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

  @Test
  void geo() {
    GeoOperations<String, String> operations = redisTemplate.opsForGeo();

    operations.add("sellers", new Point(106.822695, -6.177456), "Toko A");
    operations.add("sellers", new Point(106.821016, -6.174598), "Toko B");

    Distance distance = operations.distance("sellers", "Toko A", "Toko B", Metrics.KILOMETERS);
    assertEquals(0.3682, distance.getValue());

    GeoResults<RedisGeoCommands.GeoLocation<String>> sellers =
        operations.search("sellers", new Circle(
            new Point(106.821922, -6.175491),
            new Distance(5, Metrics.KILOMETERS)
        ));

    assertEquals(2, sellers.getContent().size());
    assertEquals("Toko A", sellers.getContent().get(0).getContent().getName());
    assertEquals("Toko B", sellers.getContent().get(1).getContent().getName());
  }

  @Test
  void hyperLogLog() {
    HyperLogLogOperations<String, String> operations = redisTemplate.opsForHyperLogLog();

    operations.add("traffics", "eko", "kurniawan", "khannedy");
    operations.add("traffics", "eko", "budi", "joko");
    operations.add("traffics", "budi", "joko", "rully");

    assertEquals(6L, operations.size("traffics"));
  }

  @Test
  void transaction() {
    redisTemplate.execute(new SessionCallback<Object>() {
      @Override
      public Object execute(RedisOperations operations) throws DataAccessException {
        operations.multi();

        operations.opsForValue().set("test1", "Eko", Duration.ofSeconds(2));
        operations.opsForValue().set("test2", "Budi", Duration.ofSeconds(2));

        operations.exec();
        return null;
      }
    });

    assertEquals("Eko", redisTemplate.opsForValue().get("test1"));
    assertEquals("Budi", redisTemplate.opsForValue().get("test2"));
  }

  @Test
  void pipeline() {
    List<Object> statuses = redisTemplate.executePipelined(new SessionCallback<Object>() {
      @Override
      public Object execute(RedisOperations operations) throws DataAccessException {
        operations.opsForValue().set("test1", "Eko", Duration.ofSeconds(2));
        operations.opsForValue().set("test2", "Eko", Duration.ofSeconds(2));
        operations.opsForValue().set("test3", "Eko", Duration.ofSeconds(2));
        operations.opsForValue().set("test4", "Eko", Duration.ofSeconds(2));
        return null;
      }
    });

    assertThat(statuses, hasSize(4));
    assertThat(statuses, hasItem(true));
    assertThat(statuses, not(hasItem(false)));
  }
}

