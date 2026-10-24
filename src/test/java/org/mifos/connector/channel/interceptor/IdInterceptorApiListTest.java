package org.mifos.connector.channel.interceptor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.mifos.connector.channel.camel.routes.ChannelRouteBuilder;
import org.mifos.connector.channel.config.ChannelRedisProperties;
import org.springframework.boot.convert.ApplicationConversionService;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

/**
 * Before PHEE-420 redis.idempotency.apiList was read with "${redis.idempotency.apiList}.split(',')", which appended the
 * literal ".split(',')" to the value, so the last API in the list was never checked for idempotency.
 */
class IdInterceptorApiListTest {

    @SuppressWarnings("unchecked")
    private final RedisTemplate<String, String> redisTemplate = mock(RedisTemplate.class);

    // the application gets Spring Boot's conversion service (comma-separated String -> Set, trimmed); so does this test
    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withInitializer(context -> context.getBeanFactory().setConversionService(ApplicationConversionService.getSharedInstance()))
            .withPropertyValues("redis.idempotency.apiList=/channel/transfer, /channel/transactionRequest")
            .withBean(ChannelRedisProperties.class,
                    () -> new ChannelRedisProperties("localhost", 6379, "", 0, 1L,
                            new ChannelRedisProperties.Idempotency(true, "clientCorrelationId_tenant_api")))
            // ready-made singletons: a bean definition would autowire ChannelRouteBuilder's own fields into the mock
            .withInitializer(context -> {
                context.getBeanFactory().registerSingleton("channelRouteBuilder", mock(ChannelRouteBuilder.class));
                context.getBeanFactory().registerSingleton("redisTemplate", redisTemplate);
            }).withBean(IdInterceptor.class);

    @Test
    @SuppressWarnings("unchecked")
    void everyListedApiIsCheckedIncludingTheLast() {
        ValueOperations<String, String> values = mock(ValueOperations.class);
        when(redisTemplate.opsForValue()).thenReturn(values);
        when(redisTemplate.hasKey(anyString())).thenReturn(true); // every request is a duplicate

        contextRunner.run(context -> {
            IdInterceptor interceptor = context.getBean(IdInterceptor.class);

            assertThat(interceptor.preHandle(request("/channel/transfer"), new MockHttpServletResponse(), null)).isFalse();
            assertThat(interceptor.preHandle(request("/channel/transactionRequest"), new MockHttpServletResponse(), null)).isFalse();
            assertThat(interceptor.preHandle(request("/channel/other"), new MockHttpServletResponse(), null)).isTrue();
        });
    }

    @Test
    void missingCorrelationHeaderIsABadRequestNotAServerError() {
        contextRunner.run(context -> {
            IdInterceptor interceptor = context.getBean(IdInterceptor.class);
            MockHttpServletRequest request = new MockHttpServletRequest("POST", "/channel/transfer");
            MockHttpServletResponse response = new MockHttpServletResponse();

            assertThat(interceptor.preHandle(request, response, null)).isFalse();
            assertThat(response.getStatus()).isEqualTo(400);
        });
    }

    private static MockHttpServletRequest request(String uri) {
        MockHttpServletRequest request = new MockHttpServletRequest("POST", uri);
        request.addHeader("X-CorrelationID", "c1");
        request.addHeader("Platform-TenantId", "greenbank");
        return request;
    }
}
