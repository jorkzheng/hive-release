/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.apache.hadoop.hive.metastore.messaging.json;

import org.codehaus.jackson.annotate.JsonProperty;
import org.apache.hadoop.hive.metastore.api.ColumnStatistics;
import org.apache.hadoop.hive.metastore.messaging.UpdateTableColumnStatMessage;
import org.apache.thrift.TException;
import java.util.Map;

/**
 * JSON implementation of JSONUpdateTableColumnStatMessage
 */
public class JSONUpdateTableColumnStatMessage extends UpdateTableColumnStatMessage {

  @JsonProperty
  private Long writeId, timestamp;

  @JsonProperty
  private String validWriteIds, server, servicePrincipal, db, colStatsJson;

  @JsonProperty
  private Map<String, String> parameters;

  /**
   * Default constructor, needed for Jackson.
   */
  public JSONUpdateTableColumnStatMessage() {
  }

  public JSONUpdateTableColumnStatMessage(String server, String servicePrincipal, Long timestamp,
                      ColumnStatistics colStats, Map<String, String> parameters, String validWriteIds, long writeId) {
    this.timestamp = timestamp;
    this.server = server;
    this.servicePrincipal = servicePrincipal;
    this.writeId = writeId;
    this.db = colStats.getStatsDesc().getDbName();
    this.parameters = parameters;
    this.validWriteIds = validWriteIds;
    try {
      this.colStatsJson = JSONMessageFactory.createTableColumnStatJson(colStats);
    } catch (TException e) {
      throw new IllegalArgumentException("Could not serialize JSONUpdateTableColumnStatMessage : ", e);
    }
  }

  @Override
  public Long getTimestamp() {
    return timestamp;
  }

  @Override
  public String getDB() {
    return db;
  }

  @Override
  public String getServicePrincipal() {
    return servicePrincipal;
  }

  @Override
  public String getServer() {
    return server;
  }

  @Override
  public ColumnStatistics getColumnStatistics() {
    try {
      return  (ColumnStatistics) JSONMessageFactory.getTObj(colStatsJson, ColumnStatistics.class);
    } catch (Exception e) {
      throw new RuntimeException("failed to get the ColumnStatistics object ", e);
    }
  }

  @Override
  public String getValidWriteIds() {
    return validWriteIds;
  }

  @Override
  public String getcolStatsJson() {
    return colStatsJson;
  }

  @Override
  public Long getWriteId() {
    return writeId;
  }

  @Override
  public Map<String, String> getParameters() {
    return parameters;
  }

  @Override
  public String toString() {
    try {
      return JSONMessageDeserializer.mapper.writeValueAsString(this);
    } catch (Exception exception) {
      throw new IllegalArgumentException("Could not serialize: ", exception);
    }
  }
}

