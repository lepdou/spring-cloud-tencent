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

package com.tencent.cloud.plugin.gateway.staining.rule;

import com.tencent.polaris.configuration.api.core.ConfigFile;
import com.tencent.polaris.configuration.api.core.ConfigFileService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.Mockito.when;


/**
 * Test for {@link StainingRuleManager}.
 *@author lepdou 2022-07-12
 */
@RunWith(MockitoJUnitRunner.class)
public class StainingRuleManagerTest {

	@Mock
	private ConfigFileService configFileService;

	private final String testNamespace = "testNamespace";
	private final String testGroup = "testGroup";
	private final String testFileName = "rule.json";

	@Test
	public void testNormalRule() {
		RuleStainingProperties ruleStainingProperties = new RuleStainingProperties();
		ruleStainingProperties.setNamespace(testNamespace);
		ruleStainingProperties.setGroup(testGroup);
		ruleStainingProperties.setFileName(testFileName);

		ConfigFile configFile = Mockito.mock(ConfigFile.class);
		when(configFile.getContent()).thenReturn("{\n"
				+ "    \"rules\":[\n"
				+ "        {\n"
				+ "            \"conditions\":[\n"
				+ "                {\n"
				+ "                    \"key\":\"${http.query.uid}\",\n"
				+ "                    \"values\":[\"1000\"],\n"
				+ "                    \"operation\":\"EQUAL\"\n"
				+ "                }\n"
				+ "            ],\n"
				+ "            \"labels\":[\n"
				+ "                {\n"
				+ "                    \"key\":\"env\",\n"
				+ "                    \"value\":\"blue\"\n"
				+ "                }\n"
				+ "            ]\n"
				+ "        }\n"
				+ "    ]\n"
				+ "}");
		when(configFileService.getConfigFile(testNamespace, testGroup, testFileName)).thenReturn(configFile);

		StainingRuleManager stainingRuleManager = new StainingRuleManager(ruleStainingProperties, configFileService);

		StainingRule stainingRule = stainingRuleManager.getStainingRule();

		Assert.assertNotNull(stainingRule);
		Assert.assertEquals(1, stainingRule.getRules().size());
		StainingRule.Rule rule = stainingRule.getRules().get(0);
		Assert.assertEquals(1, rule.getConditions().size());
		Assert.assertEquals(1, rule.getLabels().size());
	}

	@Test(expected = RuntimeException.class)
	public void testWrongRule() {
		RuleStainingProperties ruleStainingProperties = new RuleStainingProperties();
		ruleStainingProperties.setNamespace(testNamespace);
		ruleStainingProperties.setGroup(testGroup);
		ruleStainingProperties.setFileName(testFileName);

		ConfigFile configFile = Mockito.mock(ConfigFile.class);
		when(configFile.getContent()).thenReturn("{\n"
				+ "    \"rules\":[\n"
				+ "        {\n"
				+ "            \"conditionsxxxx\":[\n"
				+ "                {\n"
				+ "                    \"key\":\"${http.query.uid}\",\n"
				+ "                    \"values\":[\"1000\"],\n"
				+ "                    \"operation\":\"EQUAL\"\n"
				+ "                }\n"
				+ "            ],\n"
				+ "            \"labels\":[\n"
				+ "                {\n"
				+ "                    \"key\":\"env\",\n"
				+ "                    \"value\":\"blue\"\n"
				+ "                }\n"
				+ "            ]\n"
				+ "        }\n"
				+ "    ]\n"
				+ "}");
		when(configFileService.getConfigFile(testNamespace, testGroup, testFileName)).thenReturn(configFile);

		new StainingRuleManager(ruleStainingProperties, configFileService);
	}

	@Test
	public void testEmptyRule() {
		RuleStainingProperties ruleStainingProperties = new RuleStainingProperties();
		ruleStainingProperties.setNamespace(testNamespace);
		ruleStainingProperties.setGroup(testGroup);
		ruleStainingProperties.setFileName(testFileName);

		ConfigFile configFile = Mockito.mock(ConfigFile.class);
		when(configFile.getContent()).thenReturn(null);
		when(configFileService.getConfigFile(testNamespace, testGroup, testFileName)).thenReturn(configFile);

		StainingRuleManager stainingRuleManager = new StainingRuleManager(ruleStainingProperties, configFileService);
		Assert.assertNull(stainingRuleManager.getStainingRule());
	}
}
