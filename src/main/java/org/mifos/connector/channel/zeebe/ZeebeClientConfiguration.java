package org.mifos.connector.channel.zeebe;

import io.camunda.zeebe.client.ZeebeClient;
import java.time.Duration;
import org.mifos.connector.channel.config.ZeebeProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ZeebeClientConfiguration {

    private final String zeebeBrokerContactpoint;

    private final int zeebeClientMaxThreads;

    public ZeebeClientConfiguration(ZeebeProperties zeebeProperties) {
        this.zeebeBrokerContactpoint = zeebeProperties.broker().contactpoint();
        this.zeebeClientMaxThreads = zeebeProperties.client().maxExecutionThreads();
    }

    @Bean
    public ZeebeClient setup() {
        return ZeebeClient.newClientBuilder().gatewayAddress(zeebeBrokerContactpoint).usePlaintext()
                .defaultJobPollInterval(Duration.ofMillis(1)).defaultJobWorkerMaxJobsActive(2000)
                .numJobWorkerExecutionThreads(zeebeClientMaxThreads).build();
    }
}
