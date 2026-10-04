package org.mifos.connector.channel.config;

import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

/**
 * The authorization server this connector talks to: {@code rest.authorization.*}.
 *
 * <p>
 * {@code rest.authorization.enabled} is not bound. The key is in the shipped {@code application.yml}, but no code in
 * this connector has ever read it - there was no {@code @Value} for it before this change and nothing reads it now.
 * </p>
 *
 * @param host
 *            base URL of the authorization server
 * @param header
 *            the Authorization header value used when asking for a token
 */

@Validated
@ConfigurationProperties(prefix = "rest.authorization")
public record RestAuthorizationProperties(@NotNull String host, @NotNull String header) {
}
