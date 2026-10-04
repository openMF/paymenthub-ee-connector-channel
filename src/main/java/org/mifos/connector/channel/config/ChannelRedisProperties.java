package org.mifos.connector.channel.config;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

/**
 * The Redis instance used for idempotency, under the connector's own {@code redis.*} prefix.
 *
 * <p>
 * This is not Spring Data's {@code spring.data.redis}: the connector has always read its own {@code redis.*} keys and
 * the deployment sets them, so the names stay as they are.
 * </p>
 *
 * <p>
 * {@code redis.idempotency.apiList} is deliberately not bound here. It is still read by {@code IdInterceptor} through
 * {@code @Value}, and it is read incorrectly - moving it would silently change behaviour, which belongs in its own
 * ticket rather than in this change.
 * </p>
 *
 * @param host
 *            Redis host
 * @param port
 *            Redis port
 * @param password
 *            Redis password; the deployment supplies it from a secret, so a missing one is a mistake
 * @param database
 *            Redis database index
 * @param cacheRetencyDuration
 *            how long, in days, an idempotency key is kept
 * @param idempotency
 *            idempotency settings, {@code redis.idempotency.*}
 */

@Validated
@ConfigurationProperties(prefix = "redis")
public record ChannelRedisProperties(@NotNull String host, @NotNull Integer port, @NotNull String password, @NotNull Integer database,
        @NotNull Long cacheRetencyDuration, @NotNull @Valid Idempotency idempotency) {

    /**
     * Idempotency settings: {@code redis.idempotency.*}.
     *
     * @param enabled
     *            whether duplicate requests are rejected at all
     * @param keyFormat
     *            the order of the parts that make up the Redis key
     */
    public record Idempotency(@NotNull Boolean enabled, @NotNull String keyFormat) {
    }
}
