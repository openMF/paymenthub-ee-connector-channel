package org.mifos.connector.channel.config;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.autoconfigure.context.ConfigurationPropertiesAutoConfiguration;
import org.springframework.boot.autoconfigure.validation.ValidationAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.ConfigDataApplicationContextInitializer;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Configuration;

/**
 * Every property these records ask for has to be there, or the connector must refuse to start and say which one is
 * missing. That is what the plain {@code @Value} declarations did before they were replaced, so these tests hold the
 * replacement to the same promise, and they read the real application.yml rather than a copy of it.
 */
class ShippedConfigBindsTest {

    @Configuration(proxyBeanMethods = false)
    @EnableConfigurationProperties({ BpmnFlowProperties.class, ChannelRedisProperties.class, ConnectorCamelProperties.class,
            DestinationProperties.class, MpesaNotificationProperties.class, OperationsProperties.class, RestAuthorizationProperties.class,
            ZeebeProperties.class })
    static class AllRecords {}

    @Configuration(proxyBeanMethods = false)
    @EnableConfigurationProperties(BpmnFlowProperties.class)
    static class OnlyBpmnFlows {}

    @Configuration(proxyBeanMethods = false)
    @EnableConfigurationProperties(ChannelRedisProperties.class)
    static class OnlyRedis {}

    @Configuration(proxyBeanMethods = false)
    @EnableConfigurationProperties(ConnectorCamelProperties.class)
    static class OnlyCamel {}

    @Configuration(proxyBeanMethods = false)
    @EnableConfigurationProperties(DestinationProperties.class)
    static class OnlyDestination {}

    @Configuration(proxyBeanMethods = false)
    @EnableConfigurationProperties(MpesaNotificationProperties.class)
    static class OnlyMpesaNotification {}

    @Configuration(proxyBeanMethods = false)
    @EnableConfigurationProperties(OperationsProperties.class)
    static class OnlyOperations {}

    @Configuration(proxyBeanMethods = false)
    @EnableConfigurationProperties(RestAuthorizationProperties.class)
    static class OnlyRestAuthorization {}

    @Configuration(proxyBeanMethods = false)
    @EnableConfigurationProperties(ZeebeProperties.class)
    static class OnlyZeebe {}

    /** One configuration class per record, keyed by the prefix that record binds. */
    private static final Map<String, Class<?>> ONE_RECORD_EACH = Map.of("bpmn.flows", OnlyBpmnFlows.class, "redis", OnlyRedis.class,
            "camel", OnlyCamel.class, "destination", OnlyDestination.class, "mpesa.notification", OnlyMpesaNotification.class, "operations",
            OnlyOperations.class, "rest.authorization", OnlyRestAuthorization.class, "zeebe", OnlyZeebe.class);

    private ApplicationContextRunner runner() {
        return new ApplicationContextRunner().withConfiguration(
                AutoConfigurations.of(ConfigurationPropertiesAutoConfiguration.class, ValidationAutoConfiguration.class));
    }

    private ApplicationContextRunner withShippedYaml() {
        return runner().withInitializer(new ConfigDataApplicationContextInitializer());
    }

    @Test
    void theShippedApplicationYamlFillsEveryField() {
        withShippedYaml().withUserConfiguration(AllRecords.class).run(context -> {
            assertThat(context).hasNotFailed();
            assertThat(context.getBean(BpmnFlowProperties.class).paymentTransfer()).isEqualTo("PayerFundTransfer-{dfspid}");
            assertThat(context.getBean(ConnectorCamelProperties.class).serverPort()).isEqualTo(5000);
            assertThat(context.getBean(DestinationProperties.class).dfspid()).isEqualTo("greenbank");
            assertThat(context.getBean(MpesaNotificationProperties.class).success().enabled()).isFalse();
            assertThat(context.getBean(MpesaNotificationProperties.class).failure().enabled()).isTrue();
            assertThat(context.getBean(OperationsProperties.class).endpoint().transactionReq()).isEqualTo("/transactionRequests/?");
            assertThat(context.getBean(RestAuthorizationProperties.class).host()).isEqualTo("http://localhost:8080");
            assertThat(context.getBean(ZeebeProperties.class).client().evenlyAllocatedMaxJobs()).isEqualTo(1000);
            assertThat(context.getBean(ChannelRedisProperties.class).idempotency().keyFormat()).isEqualTo("clientCorrelationId_tenant_api");
            assertThat(context.getBean(ChannelRedisProperties.class).cacheRetencyDuration()).isEqualTo(30L);
        });
    }

    @Test
    void everyRecordRefusesToStartWhenItsSectionIsMissing() {
        ONE_RECORD_EACH.forEach((prefix, configuration) -> runner().withUserConfiguration(configuration).run(context -> {
            assertThat(context).as("context with nothing configured under '%s'", prefix).hasFailed();
            assertThat(context.getStartupFailure()).as("failure for '%s'", prefix).hasStackTraceContaining("BindValidationException")
                    .hasStackTraceContaining("Binding validation errors on " + prefix);
        }));
    }

    @Test
    void aValueSetToNothingOnABooleanFieldStopsStartup() {
        withShippedYaml().withUserConfiguration(OnlyMpesaNotification.class).withPropertyValues("mpesa.notification.success.enabled=")
                .run(context -> {
                    assertThat(context).hasFailed();
                    assertThat(context.getStartupFailure()).hasStackTraceContaining("Binding validation errors on mpesa.notification");
                });
    }

    @Test
    void aValueSetToNothingOnAStringFieldIsAcceptedJustAsItWasBefore() {
        withShippedYaml().withUserConfiguration(OnlyDestination.class).withPropertyValues("destination.dfspid=").run(context -> {
            assertThat(context).hasNotFailed();
            assertThat(context.getBean(DestinationProperties.class).dfspid()).isEmpty();
        });
    }
}
