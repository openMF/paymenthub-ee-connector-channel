package org.mifos.connector.channel.config;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

/**
 * Whether the connector notifies the mpesa notification service: {@code mpesa.notification.*}.
 *
 * <p>
 * Neither group has a default. These two flags decide whether a payment result is announced at all, and they are read
 * into the workflow in {@code ChannelRouteBuilder}, so a group that quietly went missing would turn notifications off
 * without anyone noticing. The deployment sets both, so a missing one means something went wrong and startup should say
 * so.
 * </p>
 *
 * @param success
 *            settings for the success notification
 * @param failure
 *            settings for the failure notification
 */

@Validated
@ConfigurationProperties(prefix = "mpesa.notification")
public record MpesaNotificationProperties(@NotNull @Valid Success success, @NotNull @Valid Failure failure) {

    /**
     * Success notification: {@code mpesa.notification.success.*}.
     *
     * @param enabled
     *            whether the success notification is sent
     */
    public record Success(@NotNull Boolean enabled) {
    }

    /**
     * Failure notification: {@code mpesa.notification.failure.*}.
     *
     * @param enabled
     *            whether the failure notification is sent
     */
    public record Failure(@NotNull Boolean enabled) {
    }
}
