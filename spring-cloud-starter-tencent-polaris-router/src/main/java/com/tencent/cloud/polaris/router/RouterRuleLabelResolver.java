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

package com.tencent.cloud.polaris.router;

import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.tencent.cloud.common.util.ExpressionLabelUtils;
import com.tencent.cloud.polaris.context.ServiceRuleManager;
import com.tencent.polaris.client.pb.ModelProto;
import com.tencent.polaris.client.pb.RoutingProto;

import org.springframework.util.CollectionUtils;

/**
 * Parse label expressions from routing rules。
 * @author lepdou 2022-05-19
 */
public class RouterRuleLabelResolver {

	private final ServiceRuleManager serviceRuleManager;

	public RouterRuleLabelResolver(ServiceRuleManager serviceRuleManager) {
		this.serviceRuleManager = serviceRuleManager;
	}

	public Set<String> getExpressionLabelKeys(String namespace, String sourceService, String dstService) {
		List<RoutingProto.Routing> routings = serviceRuleManager.getServiceRouterRule(namespace, sourceService, dstService);
		if (CollectionUtils.isEmpty(routings)) {
			return Collections.emptySet();
		}

		List<RoutingProto.Route> rules = new LinkedList<>();
		for (RoutingProto.Routing routing : routings) {
			List<RoutingProto.Route> inboundRules = routing.getInboundsList();
			if (!CollectionUtils.isEmpty(inboundRules)) {
				rules.addAll(inboundRules);
			}
			List<RoutingProto.Route> outboundRules = routing.getOutboundsList();
			if (!CollectionUtils.isEmpty(outboundRules)) {
				rules.addAll(outboundRules);
			}
		}

		Set<String> expressionLabels = new HashSet<>();

		for (RoutingProto.Route rule : rules) {
			List<RoutingProto.Source> sources = rule.getSourcesList();
			if (CollectionUtils.isEmpty(sources)) {
				continue;
			}
			for (RoutingProto.Source source : sources) {
				Map<String, ModelProto.MatchString> labels = source.getMetadataMap();
				if (CollectionUtils.isEmpty(labels)) {
					continue;
				}
				for (String labelKey : labels.keySet()) {
					if (ExpressionLabelUtils.isExpressionLabel(labelKey)) {
						expressionLabels.add(labelKey);
					}
				}
			}
		}

		return expressionLabels;
	}
}
