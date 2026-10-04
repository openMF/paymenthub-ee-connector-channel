package org.mifos.connector.channel.config;

import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

/**
 * The default destination DFSP: {@code destination.*}.
 *
 * @param dfspid
 *            id of the DFSP a request is sent to when the caller does not name one
 */

@Validated
@ConfigurationProperties(prefix = "destination")
public record DestinationProperties(@NotNull String dfspid) {
}
