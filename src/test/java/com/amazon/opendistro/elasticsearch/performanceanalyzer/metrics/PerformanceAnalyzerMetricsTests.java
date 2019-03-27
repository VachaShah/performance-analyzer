/*
 * Copyright <2019> Amazon.com, Inc. or its affiliates. All Rights Reserved.
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


package com.amazon.opendistro.elasticsearch.performanceanalyzer.metrics;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class PerformanceAnalyzerMetricsTests {


    @Test
    public void testBasicMetric() {
        System.setProperty("performanceanalyzer.metrics.log.enabled", "False");
        PerformanceAnalyzerMetrics.emitMetric(PerformanceAnalyzerMetrics.sDevShmLocation + "/dir1/test1", "value1");
        assertEquals("value1", PerformanceAnalyzerMetrics.getMetric(PerformanceAnalyzerMetrics.sDevShmLocation + "/dir1/test1"));

        assertEquals("", PerformanceAnalyzerMetrics.getMetric(PerformanceAnalyzerMetrics.sDevShmLocation + "/dir1/test2"));

        PerformanceAnalyzerMetrics.removeMetrics(PerformanceAnalyzerMetrics.sDevShmLocation + "/dir1");
    }
}
