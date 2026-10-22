package org.mifos.connector.channel.zeebe;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

/**
 * send-error-to-channel logs errorInformation and then completes the job. Before PHEE-453 a value that was not the
 * Mojaloop error document threw inside the handler, so the job ran out of retries and the payer process hung as an
 * incident instead of ending as failed.
 */
class ZeebeWorkersErrorInformationTest {

    @Test
    void describesMojaloopErrorDocument() {
        String mojaloop = "{\"errorInformation\":{\"errorCode\":\"5100\",\"errorDescription\":\"Payee rejected\"}}";

        assertThat(ZeebeWorkers.describeErrorInformation(mojaloop))
                .isEqualTo("code: 5100 type: GENERIC_PAYEE_REJECTION message: Payee rejected");
    }

    @Test
    void returnsPlainTextAsIs() {
        // what ams-mifosx sets when Fineract rejects a funds hold
        String plain = "The server is currently unable to handle the request due to concurrent modification";

        assertThat(ZeebeWorkers.describeErrorInformation(plain)).isEqualTo(plain);
    }

    @Test
    void toleratesUnknownOrMissingErrorCode() {
        assertThat(ZeebeWorkers.describeErrorInformation("{\"errorInformation\":{\"errorCode\":\"9999\",\"errorDescription\":\"x\"}}"))
                .isEqualTo("code: 9999 type: UNKNOWN message: x");
        assertThat(ZeebeWorkers.describeErrorInformation("{\"errorInformation\":{\"errorDescription\":\"x\"}}"))
                .isEqualTo("code:  type: UNKNOWN message: x");
    }

    @Test
    void returnsJsonWithoutErrorInformationAsIs() {
        assertThat(ZeebeWorkers.describeErrorInformation("{\"other\":1}")).isEqualTo("{\"other\":1}");
    }
}
