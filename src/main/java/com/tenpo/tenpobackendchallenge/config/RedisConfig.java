package com.tenpo.tenpobackendchallenge.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {

    @Bean
    public ReactiveRedisTemplate<String, Object> reactiveRedisTemplate(ReactiveRedisConnectionFactory connectionFactory) {
        // Serializador para claves
        StringRedisSerializer keySerializer = new StringRedisSerializer();

        // Serializador para valores
        GenericJackson2JsonRedisSerializer valueSerializer = new GenericJackson2JsonRedisSerializer();

        // Configuración del contexto de serialización
        RedisSerializationContext<String, Object> serializationContext = RedisSerializationContext
                .<String, Object>newSerializationContext(keySerializer) // Serializador para claves
                .key(keySerializer) // Claves como String
                .value(valueSerializer) // Valores como JSON
                .hashKey(keySerializer) // Claves en hashes como String
                .hashValue(valueSerializer) // Valores en hashes como JSON
                .build();

        // ReactiveRedisTemplate con la configuración personalizada
        return new ReactiveRedisTemplate<>(connectionFactory, serializationContext);
    }

}
