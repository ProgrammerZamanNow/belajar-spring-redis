package programmerzamannow.redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Component
public class CustomerPublisher {

  @Autowired
  private StringRedisTemplate redisTemplate;

  @Scheduled(fixedRate = 10, timeUnit = TimeUnit.SECONDS)
  public void publish(){
    redisTemplate.convertAndSend("customers", "Eko " + UUID.randomUUID());
  }
}
