package com.buschmais;

import com.vaadin.collaborationengine.CollaborationEngineConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class VaadinConfiguration {

	@Bean
	public CollaborationEngineConfiguration collaborationEngineConfiguration() {
		CollaborationEngineConfiguration configuration = new CollaborationEngineConfiguration(licenseEvent -> {});
		configuration.setDataDir("./vaadin-collaboration-engine/");
		return configuration;
	}

}
