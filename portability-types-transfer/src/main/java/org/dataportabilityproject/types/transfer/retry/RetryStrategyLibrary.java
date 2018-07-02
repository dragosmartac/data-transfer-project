/*
 * Copyright 2018 The Data Transfer Project Authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.dataportabilityproject.types.transfer.retry;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

/**
 * Class used by {@link RetryingCallable} to determine which {@link RetryStrategy} to use given a
 * particular error.
 */
public class RetryStrategyLibrary {

  @JsonProperty("strategyMappings")
  private final List<RetryMapping> retryMappings;
  @JsonProperty("defaultRetryStrategy")
  private final RetryStrategy defaultRetryStrategy;

  public RetryStrategyLibrary(@JsonProperty("strategyMappings") List<RetryMapping> retryMappings,
      @JsonProperty("defaultRetryStrategy") RetryStrategy defaultRetryStrategy) {
    this.retryMappings = retryMappings;
    this.defaultRetryStrategy = defaultRetryStrategy;
  }

  /**
   * Returns the best {@link RetryStrategy} for a given Throwable.  If there are no matches, returns
   * the default RetryStrategy.
   *
   * Right now it just looks at the message in the Throwable and tries to find a matching regex in
   * its internal library.  Later on it will use more and more of the Throwable to make a decision.
   */
  public RetryStrategy checkoutRetryStrategy(Throwable throwable) {
    // TODO: determine retry strategy based on full information in Throwable
    // TODO: better logic (v2)
    return getMatchingRetryStrategy(throwable.toString());
  }

  private RetryStrategy getMatchingRetryStrategy(String input) {
    for (RetryMapping mapping : retryMappings) {
      for (String regex : mapping.getRegexes()) {
        if (input.matches(regex)) {
          return mapping.getStrategy();
        }
      }
    }
    return defaultRetryStrategy;
  }

  public RetryStrategy getDefaultRetryStrategy() {
    return defaultRetryStrategy;
  }
}
