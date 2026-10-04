package org.mifos.connector.channel.config;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

/**
 * How this connector reaches the operations service: {@code operations.*}.
 *
 * @param url
 *            base URL of the operations API
 * @param authEnabled
 *            whether calls to the operations API carry an authorization header
 * @param endpoint
 *            the paths under the base URL, {@code operations.endpoint.*}
 */

@Validated
@ConfigurationProperties(prefix = "operations")
public record OperationsProperties(@NotNull String url, @NotNull Boolean authEnabled, @NotNull @Valid Endpoint endpoint) {

    /**
     * Paths under the operations base URL: {@code operations.endpoint.*}.
     *
     * @param transfers
     *            the transfers query path
     * @param transactionReq
     *            the transaction requests query path
     */
    public record Endpoint(@NotNull String transfers, @NotNull String transactionReq) {
    }
}
