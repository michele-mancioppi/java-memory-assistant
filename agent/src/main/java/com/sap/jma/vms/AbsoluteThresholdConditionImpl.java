/*
 * Copyright (c) 2017 SAP SE or an SAP affiliate company. All rights reserved.
 * This file is licensed under the Apache Software License, v. 2 except as noted
 * otherwise in the LICENSE file at the root of the repository.
 */

package com.sap.jma.vms;

import com.sap.jma.Configuration;
import com.sap.jma.logging.Logger;

public abstract class AbsoluteThresholdConditionImpl extends
    AbstractUsageThresholdConditionImpl<Configuration.AbsoluteThresholdConfiguration> {

  // VisibleForTesting
  private final Logger logger;

  public AbsoluteThresholdConditionImpl() {
    this(Logger.Factory.get(JavaVirtualMachine.UsageThresholdCondition.class));
  }

  //VisibleForTesting
  AbsoluteThresholdConditionImpl(final Logger logger) {
    this.logger = logger;
  }

  protected abstract double getCurrentUsageInBytes();

  public final void evaluate() throws JavaVirtualMachine.UsageThresholdConditionViolatedException {
    final Configuration.AbsoluteThresholdConfiguration usageThreshold = getUsageThreshold();
    final double currentUsageInBytes = getCurrentUsageInBytes();
    final double targetUsageInBytes = usageThreshold.getTargetValueInBytes();
    final Configuration.MemorySizeUnit memorySizeUnit = usageThreshold.getMemorySizeUnit();
    final Configuration.Comparison comparison = usageThreshold.getComparison();

    if (comparison.compare(targetUsageInBytes, currentUsageInBytes)) {
      throw new JavaVirtualMachine.UsageThresholdConditionViolatedException(
          getDescription(currentUsageInBytes, targetUsageInBytes, memorySizeUnit, comparison));
    }

    logger.debug(getDescription(currentUsageInBytes, targetUsageInBytes, memorySizeUnit,
        comparison));
  }

  private String getDescription(final double actualUsage,
                                final double targetUsage,
                                final Configuration.MemorySizeUnit memorySize,
                                final Configuration.Comparison comparisonOperator) {
    return String.format("Memory pool '%s' at %s%s usage, configured threshold is %s %s%s",
        getMemoryPoolName(), DECIMAL_FORMAT.format(actualUsage), memorySize.getLiteral(),
        comparisonOperator, DECIMAL_FORMAT.format(targetUsage), memorySize.getLiteral());
  }

  @Override
  public String toString() {
    final Configuration.AbsoluteThresholdConfiguration usageThreshold = getUsageThreshold();

    return String.format("Memory pool '%s' used %s %s%s", getMemoryPoolName(),
        usageThreshold.getComparison(),
        DECIMAL_FORMAT.format(usageThreshold.getTargetValueInBytes()),
        usageThreshold.getMemorySizeUnit().getLiteral());
  }

}
