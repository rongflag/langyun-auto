package com.langyun.auto.common;

import com.langyun.auto.annotation.AutoContextInitializer;
import com.langyun.auto.annotation.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 注解类型
 *
* @author langyun
 * @date 2021年06月09日 0:31
 */
@Getter
@RequiredArgsConstructor
public enum BootAutoType {

	/**
	 * Component，组合注解，添加到 spring.factories
	 */
	COMPONENT("org.springframework.stereotype.Component", "org.springframework.boot.autoconfigure.EnableAutoConfiguration"),
	/**
	 * ApplicationContextInitializer 添加到 spring.factories
	 */
	CONTEXT_INITIALIZER(AutoContextInitializer.class.getName(), "org.springframework.context.ApplicationContextInitializer"),
	/**
	 * ApplicationListener 添加到 spring.factories
	 */
	LISTENER(AutoListener.class.getName(), "org.springframework.context.ApplicationListener"),
	/**
	 * SpringApplicationRunListener 添加到 spring.factories
	 */
	RUN_LISTENER(AutoRunListener.class.getName(), "org.springframework.boot.SpringApplicationRunListener"),
	/**
	 * EnvironmentPostProcessor 添加到 spring.factories
	 */
	ENV_POST_PROCESSOR(AutoEnvPostProcessor.class.getName(), "org.springframework.boot.env.EnvironmentPostProcessor"),
	/**
	 * FailureAnalyzer 添加到 spring.factories
	 */
	FAILURE_ANALYZER(AutoFailureAnalyzer.class.getName(), "org.springframework.boot.diagnostics.FailureAnalyzer"),
	/**
	 * AutoConfigurationImportFilter spring.factories
	 */
	AUTO_CONFIGURATION_IMPORT_FILTER(AutoConfigImportFilter.class.getName(), "org.springframework.boot.autoconfigure.AutoConfigurationImportFilter"),
	/**
	 * TemplateAvailabilityProvider 添加到 spring.factories
	 */
	TEMPLATE_AVAILABILITY_PROVIDER(AutoTemplateProvider.class.getName(), "org.springframework.boot.autoconfigure.template.TemplateAvailabilityProvider"),
	/**
	 * auto EnableCircuitBreaker
	 */
	AUTO_ENABLE_CIRCUIT_BREAKER(AutoEnableCircuitBreaker.class.getName(), "org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker"),
	/**
	 * auto ConfigDataLocationResolver
	 */
	AUTO_CONFIG_DATA_LOCATION_RESOLVER(AutoConfigDataLocationResolver.class.getName(), "org.springframework.boot.context.config.ConfigDataLocationResolver"),
	/**
	 * auto ConfigDataLoader
	 */
	AUTO_CONFIG_DATA_LOADER(AutoConfigDataLoader.class.getName(), "org.springframework.boot.context.config.ConfigDataLoader"),
	;

	private final String annotation;
	private final String configureKey;

}
