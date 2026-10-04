package org.mifos.connector.channel.config;

import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

/**
 * The BPMN process ids this connector starts: {@code bpmn.flows.*}.
 *
 * <p>
 * The property names are the ones the deployment sets as environment variables and must not be renamed.
 * </p>
 *
 * @param paymentTransfer
 *            the payer fund transfer flow
 * @param specialPaymentTransfer
 *            the special payer fund transfer flow
 * @param transactionRequest
 *            the payee transaction request flow
 * @param partyRegistration
 *            the party registration flow
 * @param inboundTransactionReqFlow
 *            the inbound transaction request flow
 * @param gsmaBaseTransaction
 *            the GSMA base transaction flow
 * @param gsmaIntTransfer
 *            the GSMA international transfer flow
 * @param gsmaPayeeProcess
 *            the GSMA payee process flow
 * @param gsmaBillPayment
 *            the GSMA bill payment flow
 * @param gsmaLinkBasedPayment
 *            the GSMA link based transfer flow
 * @param internationalRemittancePayee
 *            the international remittance payee flow
 * @param internationalRemittancePayer
 *            the international remittance payer flow
 */

@Validated
@ConfigurationProperties(prefix = "bpmn.flows")
public record BpmnFlowProperties(@NotNull String paymentTransfer, @NotNull String specialPaymentTransfer,
        @NotNull String transactionRequest, @NotNull String partyRegistration, @NotNull String inboundTransactionReqFlow,
        @NotNull String gsmaBaseTransaction, @NotNull String gsmaIntTransfer, @NotNull String gsmaPayeeProcess,
        @NotNull String gsmaBillPayment, @NotNull String gsmaLinkBasedPayment, @NotNull String internationalRemittancePayee,
        @NotNull String internationalRemittancePayer) {
}
