/*
 * Copyright 2020-2021 Amazon.com, Inc. or its affiliates. All Rights Reserved.
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

package com.amazon.opendistro.opensearch.performanceanalyzer.util;


import com.amazon.opendistro.opensearch.performanceanalyzer.OpenSearchResources;
import com.amazon.opendistro.opensearch.performanceanalyzer.collectors.AdmissionControlMetricsCollector;
import com.amazon.opendistro.opensearch.performanceanalyzer.collectors.CacheConfigMetricsCollector;
import com.amazon.opendistro.opensearch.performanceanalyzer.collectors.CircuitBreakerCollector;
import com.amazon.opendistro.opensearch.performanceanalyzer.collectors.ClusterApplierServiceStatsCollector;
import com.amazon.opendistro.opensearch.performanceanalyzer.collectors.FaultDetectionMetricsCollector;
import com.amazon.opendistro.opensearch.performanceanalyzer.collectors.MasterServiceEventMetrics;
import com.amazon.opendistro.opensearch.performanceanalyzer.collectors.MasterServiceMetrics;
import com.amazon.opendistro.opensearch.performanceanalyzer.collectors.MasterThrottlingMetricsCollector;
import com.amazon.opendistro.opensearch.performanceanalyzer.collectors.NodeDetailsCollector;
import com.amazon.opendistro.opensearch.performanceanalyzer.collectors.NodeStatsAllShardsMetricsCollector;
import com.amazon.opendistro.opensearch.performanceanalyzer.collectors.NodeStatsFixedShardsMetricsCollector;
import com.amazon.opendistro.opensearch.performanceanalyzer.collectors.ShardIndexingPressureMetricsCollector;
import com.amazon.opendistro.opensearch.performanceanalyzer.collectors.ShardStateCollector;
import com.amazon.opendistro.opensearch.performanceanalyzer.collectors.ThreadPoolMetricsCollector;
import com.amazon.opendistro.opensearch.performanceanalyzer.metrics.MetricsConfiguration;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Iterator;
import org.opensearch.action.admin.indices.stats.CommonStats;
import org.opensearch.action.admin.indices.stats.CommonStatsFlags;
import org.opensearch.action.admin.indices.stats.IndexShardStats;
import org.opensearch.action.admin.indices.stats.ShardStats;
import org.opensearch.index.IndexService;
import org.opensearch.index.shard.IndexShard;
import org.opensearch.index.shard.IndexShardState;
import org.opensearch.index.shard.ShardId;
import org.opensearch.indices.IndicesService;

public class Utils {

    public static void configureMetrics() {
        MetricsConfiguration.MetricConfig cdefault = MetricsConfiguration.cdefault;
        MetricsConfiguration.CONFIG_MAP.put(AdmissionControlMetricsCollector.class, cdefault);
        MetricsConfiguration.CONFIG_MAP.put(CacheConfigMetricsCollector.class, cdefault);
        MetricsConfiguration.CONFIG_MAP.put(CircuitBreakerCollector.class, cdefault);
        MetricsConfiguration.CONFIG_MAP.put(ThreadPoolMetricsCollector.class, cdefault);
        MetricsConfiguration.CONFIG_MAP.put(NodeDetailsCollector.class, cdefault);
        MetricsConfiguration.CONFIG_MAP.put(NodeStatsAllShardsMetricsCollector.class, cdefault);
        MetricsConfiguration.CONFIG_MAP.put(NodeStatsFixedShardsMetricsCollector.class, cdefault);
        MetricsConfiguration.CONFIG_MAP.put(
                MasterServiceEventMetrics.class, new MetricsConfiguration.MetricConfig(1000, 0, 0));
        MetricsConfiguration.CONFIG_MAP.put(MasterServiceMetrics.class, cdefault);
        MetricsConfiguration.CONFIG_MAP.put(FaultDetectionMetricsCollector.class, cdefault);
        MetricsConfiguration.CONFIG_MAP.put(ShardStateCollector.class, cdefault);
        MetricsConfiguration.CONFIG_MAP.put(MasterThrottlingMetricsCollector.class, cdefault);
        MetricsConfiguration.CONFIG_MAP.put(ClusterApplierServiceStatsCollector.class, cdefault);
        MetricsConfiguration.CONFIG_MAP.put(ShardIndexingPressureMetricsCollector.class, cdefault);
    }

    // These methods are utility functions for the Node Stat Metrics Collectors. These methods are
    // used by both the all
    // shards collector and the few shards collector.

    /**
     * This function is copied directly from IndicesService.java in elastic search as the original
     * function is not public we need to collect stats per shard based instead of calling the stat()
     * function to fetch all at once(which increases cpu usage on data nodes dramatically).
     *
     * @param indicesService Indices Services which keeps tracks of the indexes on the node
     * @param indexShard Shard to fetch the metrics for
     * @param flags The Metrics Buckets which needs to be fetched.
     * @return stats given in the flags param for the shard given in the indexShard param.
     */
    public static IndexShardStats indexShardStats(
            final IndicesService indicesService,
            final IndexShard indexShard,
            final CommonStatsFlags flags) {
        if (indexShard.routingEntry() == null) {
            return null;
        }

        return new IndexShardStats(
                indexShard.shardId(),
                new ShardStats[] {
                    new ShardStats(
                            indexShard.routingEntry(),
                            indexShard.shardPath(),
                            new CommonStats(
                                    indicesService.getIndicesQueryCache(), indexShard, flags),
                            null,
                            null,
                            null)
                });
    }

    public static HashMap<ShardId, IndexShard> getShards() {
        HashMap<ShardId, IndexShard> shards = new HashMap<>();
        Iterator<IndexService> indexServices = OpenSearchResources.INSTANCE.getIndicesService().iterator();
        while (indexServices.hasNext()) {
            Iterator<IndexShard> indexShards = indexServices.next().iterator();
            while (indexShards.hasNext()) {
                IndexShard shard = indexShards.next();
                shards.put(shard.shardId(), shard);
            }
        }
        return shards;
    }

    public static final EnumSet<IndexShardState> CAN_WRITE_INDEX_BUFFER_STATES =
            EnumSet.of(
                    IndexShardState.RECOVERING,
                    IndexShardState.POST_RECOVERY,
                    IndexShardState.STARTED);
}
