/*
 * Tencent is pleased to support the open source community by making Spring Cloud Tencent available.
 *
 * Copyright (C) 2019 THL A29 Limited, a Tencent company. All rights reserved.
 *
 * Licensed under the BSD 3-Clause License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://opensource.org/licenses/BSD-3-Clause
 *
 * Unless required by applicable law or agreed to in writing, software distributed
 * under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 *
 */
package com.tencent.cloud.polaris.config;

import com.tencent.cloud.polaris.config.adapter.PolarisConfigFileLocator;
import com.tencent.cloud.polaris.config.adapter.PolarisPropertySourceManager;
import com.tencent.cloud.polaris.config.config.PolarisConfigProperties;
import com.tencent.cloud.polaris.context.ConditionalOnPolarisEnabled;
import com.tencent.cloud.polaris.context.PolarisContextAutoConfiguration;
import com.tencent.cloud.polaris.context.PolarisContextProperties;
import com.tencent.polaris.client.api.SDKContext;
import com.tencent.polaris.configuration.api.core.ConfigFileService;
import com.tencent.polaris.configuration.factory.ConfigFileServiceFactory;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * polaris config module auto configuration at bootstrap phase.
 *
 * @author lepdou 2022-03-10
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnPolarisEnabled
@ConditionalOnProperty(value = "spring.cloud.polaris.config.enabled",
		matchIfMissing = true)
@Import(PolarisContextAutoConfiguration.class)
public class PolarisConfigBootstrapAutoConfiguration {

	@Bean
	public PolarisConfigProperties polarisProperties() {
		return new PolarisConfigProperties();
	}

	@Bean
	public ConfigFileService configFileService(SDKContext sdkContext) {
		return ConfigFileServiceFactory.createConfigFileService(sdkContext);
	}

	@Bean
	public PolarisPropertySourceManager polarisPropertySourceManager() {
		return new PolarisPropertySourceManager();
	}

	@Bean
	public PolarisConfigFileLocator polarisConfigFileLocator(
			PolarisConfigProperties polarisConfigProperties,
			PolarisContextProperties polarisContextProperties,
			ConfigFileService configFileService,
			PolarisPropertySourceManager polarisPropertySourceManager) {
		return new PolarisConfigFileLocator(polarisConfigProperties,
				polarisContextProperties, configFileService,
				polarisPropertySourceManager);
	}

	@Bean
	public ConfigurationModifier configurationModifier(PolarisConfigProperties polarisConfigProperties,
			PolarisContextProperties polarisContextProperties) {
		return new ConfigurationModifier(polarisConfigProperties, polarisContextProperties);
	}

}
