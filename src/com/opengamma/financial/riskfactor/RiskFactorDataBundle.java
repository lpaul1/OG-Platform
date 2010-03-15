/**
 * Copyright (C) 2009 - 2010 by OpenGamma Inc.
 *
 * Please see distribution for license.
 */
package com.opengamma.financial.riskfactor;

import java.util.Map;

import com.opengamma.financial.greeks.Greek;
import com.opengamma.financial.greeks.GreekResult;
import com.opengamma.financial.greeks.GreekResultCollection;
import com.opengamma.financial.pnl.Underlying;

/**
 * @author emcleod
 *
 */
public class RiskFactorDataBundle {
  private final GreekResultCollection _greekValues;
  private final Map<Greek, Map<Object, Double>> _underlyingData;

  public RiskFactorDataBundle(final GreekResultCollection greekValues, final Map<Greek, Map<Object, Double>> underlyingData) {
    if (greekValues == null)
      throw new IllegalArgumentException("GreekResultCollection was null");
    if (greekValues.isEmpty())
      throw new IllegalArgumentException("GreekResultCollection was empty");
    if (underlyingData == null)
      throw new IllegalArgumentException("Underlying data map was null");
    if (underlyingData.isEmpty())
      throw new IllegalArgumentException("Underlying data map was empty");
    _greekValues = greekValues;
    _underlyingData = underlyingData;
  }

  public GreekResultCollection getAllGreekValues() {
    return _greekValues;
  }

  public Map<Greek, Map<Object, Double>> getAllUnderlyingData() {
    return _underlyingData;
  }

  public GreekResult<?> getGreekValueForGreek(final Greek greek) {
    if (_greekValues.containsKey(greek))
      return _greekValues.get(greek);
    throw new IllegalArgumentException("Greek result collection did not contain a value for " + greek);
  }

  public Map<Object, Double> getAllUnderlyingDataForGreek(final Greek greek) {
    if (_underlyingData.containsKey(greek))
      return _underlyingData.get(greek);
    throw new IllegalArgumentException("Underlying data map did not contain data for " + greek);
  }

  public Double getUnderlyingDataForGreek(final Greek greek, final Underlying underlying) {
    final Map<Object, Double> data = getAllUnderlyingDataForGreek(greek);
    if (data.containsKey(underlying))
      return data.get(underlying);
    throw new IllegalArgumentException("Underlying data map did not contain " + underlying + " data for " + greek);
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#hashCode()
   */
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((_greekValues == null) ? 0 : _greekValues.hashCode());
    result = prime * result + ((_underlyingData == null) ? 0 : _underlyingData.hashCode());
    return result;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#equals(java.lang.Object)
   */
  @Override
  public boolean equals(final Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    final RiskFactorDataBundle other = (RiskFactorDataBundle) obj;
    if (_greekValues == null) {
      if (other._greekValues != null)
        return false;
    } else if (!_greekValues.equals(other._greekValues))
      return false;
    if (_underlyingData == null) {
      if (other._underlyingData != null)
        return false;
    } else if (!_underlyingData.equals(other._underlyingData))
      return false;
    return true;
  }
}
