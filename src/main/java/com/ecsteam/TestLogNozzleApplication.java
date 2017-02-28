package com.ecsteam;

import org.cloudfoundry.reactor.DefaultConnectionContext;
import org.cloudfoundry.reactor.TokenProvider;
import org.cloudfoundry.reactor.doppler.ReactorDopplerClient;
import org.cloudfoundry.reactor.tokenprovider.ClientCredentialsGrantTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;

@SpringBootApplication
@EnableConfigurationProperties(NozzleProperties.class)
public class TestLogNozzleApplication {

	public static void main(String[] args) {
		SpringApplication.run(TestLogNozzleApplication.class, args);
	}
	private DefaultConnectionContext connectionContext(String apiHost, Boolean skipSslValidation) {
		return DefaultConnectionContext.builder()
				.apiHost(apiHost)
				.skipSslValidation(skipSslValidation)
				.build();
	}

	private TokenProvider tokenProvider(String clientId, String clientSecret) {
		return ClientCredentialsGrantTokenProvider.builder()
				.clientId(clientId)
				.clientSecret(clientSecret)
				.build();
	}

	private ReactorDopplerClient dopplerClient(NozzleProperties properties) {
		return ReactorDopplerClient.builder()
				.connectionContext(connectionContext(properties.getApiHost(), properties.isSkipSslValidation()))
				.tokenProvider(tokenProvider(properties.getClientId(), properties.getClientSecret()))
				.build();
	}

	@Bean
	@Profile("!test")
	@Autowired
	FirehoseReader firehoseReader(NozzleProperties properties) {
		return new FirehoseReader(dopplerClient(properties), properties);
	}
}
