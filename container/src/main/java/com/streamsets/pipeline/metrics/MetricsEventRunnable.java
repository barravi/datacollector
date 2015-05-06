/**
 * (c) 2015 StreamSets, Inc. All rights reserved. May not
 * be copied, modified, or distributed in whole or part without
 * written consent of StreamSets, Inc.
 */

package com.streamsets.pipeline.metrics;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.streamsets.pipeline.json.ObjectMapperFactory;
import com.streamsets.pipeline.main.RuntimeInfo;
import com.streamsets.pipeline.prodmanager.StandalonePipelineManagerTask;
import com.streamsets.pipeline.prodmanager.State;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MetricsEventRunnable implements Runnable {
  private final static Logger LOG = LoggerFactory.getLogger(MetricsEventRunnable.class);
  private List<MetricsEventListener> metricsEventListenerList = new ArrayList<>();

  private final StandalonePipelineManagerTask pipelineManager;
  private final RuntimeInfo runtimeInfo;

  public MetricsEventRunnable(StandalonePipelineManagerTask pipelineManager, RuntimeInfo runtimeInfo) {
    this.pipelineManager = pipelineManager;
    this.runtimeInfo = runtimeInfo;
  }

  public void addMetricsEventListener(MetricsEventListener metricsEventListener) {
    metricsEventListenerList.add(metricsEventListener);
  }

  public void removeMetricsEventListener(MetricsEventListener metricsEventListener) {
    metricsEventListenerList.remove(metricsEventListener);
  }

  public void run() {
    try {
      if (metricsEventListenerList.size() > 0 && pipelineManager.getPipelineState() != null &&
        pipelineManager.getPipelineState().getState() == State.RUNNING) {
        ObjectMapper objectMapper = ObjectMapperFactory.get();

        String metricsJSONStr;

        if(runtimeInfo.getExecutionMode().equals(RuntimeInfo.ExecutionMode.CLUSTER)) {
          //In case of cluster mode return List of Slave node details
          // TODO: Return aggregated metrics from all slave nodes along with slave node details
          metricsJSONStr = objectMapper.writer().writeValueAsString(pipelineManager.getSlaveCallbackList());
        } else {
          metricsJSONStr = objectMapper.writer().writeValueAsString(pipelineManager.getMetrics());
        }

        for(MetricsEventListener alertEventListener : metricsEventListenerList) {
          try {
            alertEventListener.notification(metricsJSONStr);
          } catch(Exception ex) {
            LOG.warn("Error while notifying metrics, {}", ex.getMessage(), ex);
          }
        }
      }
    } catch (IOException ex) {
      LOG.warn("Error while serializing metrics, {}", ex.getMessage(), ex);
    }
  }
}