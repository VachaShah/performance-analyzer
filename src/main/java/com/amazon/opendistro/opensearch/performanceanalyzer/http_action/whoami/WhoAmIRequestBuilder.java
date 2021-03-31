/*
 * Copyright 2019-2021 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * A copy of the License is located at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * or in the "license" file accompanying this file. This file is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */

package com.amazon.opendistro.opensearch.performanceanalyzer.http_action.whoami;


import org.opensearch.action.ActionRequestBuilder;
import org.opensearch.client.ClusterAdminClient;
import org.opensearch.client.ElasticsearchClient;

public class WhoAmIRequestBuilder extends ActionRequestBuilder<WhoAmIRequest, WhoAmIResponse> {
    public WhoAmIRequestBuilder(final ClusterAdminClient client) {
        this(client, WhoAmIAction.INSTANCE);
    }

    public WhoAmIRequestBuilder(final ElasticsearchClient client, final WhoAmIAction action) {
        super(client, action, new WhoAmIRequest());
    }
}
