/**
 * Copyright (C) 2012 - present by OpenGamma Inc. and the OpenGamma group of companies
 * 
 * Please see distribution for license.
 */
package com.opengamma.analytics.financial.interestrate.market.description;

import com.opengamma.util.ArgumentChecker;

/**
 * Object representing the sensitivity to a forward curve at a reference point (start time, end time, accrual factor).
 */
public class MarketForwardSensitivity {
  private final double _startTime;
  private final double _endTime;
  private final double _accrualFactor;
  private final double _value;

  /**
   * Constructor
   * @param startTime The start time
   * @param endTime The end time, must be after the start time
   * @param accrualFactor The accrual factor
   * @param value The sensitivity value.
   */
  public MarketForwardSensitivity(final double startTime, final double endTime, final double accrualFactor, final double value) {
    ArgumentChecker.isTrue(startTime < endTime, "Start time {} must be before the end time {}", startTime, endTime);
    _startTime = startTime;
    _endTime = endTime;
    _accrualFactor = accrualFactor;
    _value = value;
  }

  /**
   * Gets the start time
   * @return The start time
   */
  public double getStartTime() {
    return _startTime;
  }

  /**
   * Gets the end time
   * @return The end time
   */
  public double getEndTime() {
    return _endTime;
  }

  /**
   * Gets the accrual factor
   * @return The accrual factor
   */
  public double getAccrualFactor() {
    return _accrualFactor;
  }

  /**
   * Gets the value
   * @return The value
   */
  public double getValue() {
    return _value;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    long temp;
    temp = Double.doubleToLongBits(_accrualFactor);
    result = prime * result + (int) (temp ^ (temp >>> 32));
    temp = Double.doubleToLongBits(_endTime);
    result = prime * result + (int) (temp ^ (temp >>> 32));
    temp = Double.doubleToLongBits(_startTime);
    result = prime * result + (int) (temp ^ (temp >>> 32));
    temp = Double.doubleToLongBits(_value);
    result = prime * result + (int) (temp ^ (temp >>> 32));
    return result;
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (!(obj instanceof MarketForwardSensitivity)) {
      return false;
    }
    final MarketForwardSensitivity other = (MarketForwardSensitivity) obj;
    if (Double.compare(_startTime, other._startTime) != 0) {
      return false;
    }
    if (Double.compare(_endTime, other._endTime) != 0) {
      return false;
    }
    if (Double.compare(_accrualFactor, other._accrualFactor) != 0) {
      return false;
    }
    if (Double.compare(_value, other._value) != 0) {
      return false;
    }
    return true;
  }

}
