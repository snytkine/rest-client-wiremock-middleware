/**
 * Copyright 2025 - 2026 Dmitri Snytkine. All rights reserved.
 *
 * <p>Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * <p>http://www.apache.org/licenses/LICENSE-2.0
 *
 * <p>Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied.
 *
 * <p>See the License for the specific language governing permissions and limitations under the
 * License.
 */
package net.snytkine.springboot.wm_interceptor.model;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "net.snytkine.rest-client-wiremock-interceptor")
@Component
@Data
public class WireMockProperties {
  private boolean enabled = false;
  private Integer containerThreads = 1;
  private Boolean asynchronousResponseEnabled = false;
  private Integer asynchronousResponseThreads;
  private String rootDirectory;
  private Boolean journalDisabled;
  private Integer maxRequestJournalEntries;
  private Boolean gzipDisabled;
  private Boolean disableOptimizeXmlFactories = false;
  private Boolean stubCorsEnabled = false;
  private Boolean stubRequestLoggingDisabled;
  private Long maxTemplateCacheEntries;
  private Boolean globalTemplating = false;
  private boolean templatingEnabled = false;
  private String mappingsClassPath;
  private boolean proxyPassThrough;
  private String mockResponseHeader;
  private String mockResponseHeaderValue;
}
