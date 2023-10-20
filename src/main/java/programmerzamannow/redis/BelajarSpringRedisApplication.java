package programmerzamannow.redis;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.stream.Consumer;
import org.springframework.data.redis.connection.stream.ObjectRecord;
import org.springframework.data.redis.connection.stream.ReadOffset;
import org.springframework.data.redis.connection.stream.StreamOffset;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.data.redis.stream.StreamListener;
import org.springframework.data.redis.stream.StreamMessageListenerContainer;
import org.springframework.data.redis.stream.Subscription;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.time.Duration;

@SpringBootApplication
@Slf4j
@EnableScheduling
@EnableRedisRepositories
@EnableCaching
public class BelajarSpringRedisApplication {

	@Autowired
	private StringRedisTemplate redisTemplate;

	@Bean
	public RedisMessageListenerContainer messageListenerContainer(RedisConnectionFactory connectionFactory,
																																CustomerListener customerListener){
		var container = new RedisMessageListenerContainer();
		container.setConnectionFactory(connectionFactory);
		container.addMessageListener(customerListener, new ChannelTopic("customers"));
		return container;
	}

	@Bean
	public Subscription orderSubscription(StreamMessageListenerContainer<String, ObjectRecord<String, Order>> orderContainer,
																				OrderListener orderListener){
		try{
			redisTemplate.opsForStream().createGroup("orders", "my-group");
		}catch (Throwable throwable){

		}

		var offset = StreamOffset.create("orders", ReadOffset.lastConsumed());
		var consumer = Consumer.from("my-group", "consumer-1");
		var readRequest = StreamMessageListenerContainer.StreamReadRequest
				.builder(offset)
				.consumer(consumer)
				.autoAcknowledge(true)
				.cancelOnError(throwable -> false)
				.errorHandler(throwable -> log.warn(throwable.getMessage()))
				.build();

		return orderContainer.register(readRequest, orderListener);
	}

	@Bean(destroyMethod = "stop", initMethod = "start")
	public StreamMessageListenerContainer<String, ObjectRecord<String, Order>> orderContainer(RedisConnectionFactory connectionFactory){
		var options = StreamMessageListenerContainer.StreamMessageListenerContainerOptions
				.builder()
				.pollTimeout(Duration.ofSeconds(5))
				.targetType(Order.class)
				.build();

		return StreamMessageListenerContainer.create(connectionFactory, options);
	}

	public static void main(String[] args) {
		SpringApplication.run(BelajarSpringRedisApplication.class, args);
	}

}
