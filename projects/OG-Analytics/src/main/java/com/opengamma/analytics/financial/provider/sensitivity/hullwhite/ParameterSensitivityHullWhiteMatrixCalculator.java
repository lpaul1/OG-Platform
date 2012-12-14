/**
 * Copyright (C) 2012 - present by OpenGamma Inc. and the OpenGamma group of companies
 * 
 * Please see distribution for license.
 */
package com.opengamma.analytics.financial.provider.sensitivity.hullwhite;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.ArrayUtils;

import com.opengamma.analytics.financial.interestrate.InstrumentDerivativeVisitor;
import com.opengamma.analytics.financial.provider.description.interestrate.HullWhiteOneFactorProviderInterface;
import com.opengamma.analytics.financial.provider.sensitivity.multicurve.ForwardSensitivity;
import com.opengamma.analytics.financial.provider.sensitivity.multicurve.MulticurveSensitivity;
import com.opengamma.analytics.financial.provider.sensitivity.multicurve.SimpleParameterSensitivity;
import com.opengamma.analytics.math.matrix.DoubleMatrix1D;
import com.opengamma.util.tuple.DoublesPair;

/**
 * For an instrument, computes the sensitivity of double value (often par spread) to the parameters used in the curve.
 * The meaning of "parameters" will depend of the way the curve is stored (interpolated yield, function parameters, etc.).
 * The return format is DoubleMatrix1D object.
 */
public class ParameterSensitivityHullWhiteMatrixCalculator extends AbstractParameterSensitivityHullWhiteMatrixCalculator {

  /**
   * Constructor
   * @param curveSensitivityCalculator The curve sensitivity calculator.
   */
  public ParameterSensitivityHullWhiteMatrixCalculator(InstrumentDerivativeVisitor<HullWhiteOneFactorProviderInterface, MulticurveSensitivity> curveSensitivityCalculator) {
    super(curveSensitivityCalculator);
  }

  @Override
  public DoubleMatrix1D pointToParameterSensitivity(final MulticurveSensitivity sensitivity, final HullWhiteOneFactorProviderInterface hullWhite, final Set<String> curvesSet) {
    SimpleParameterSensitivity ps = new SimpleParameterSensitivity();
    // YieldAndDiscount
    Map<String, List<DoublesPair>> sensitivityDsc = sensitivity.getYieldDiscountingSensitivities();
    for (final String name : sensitivityDsc.keySet()) {
      if (curvesSet.contains(name)) {
        ps = ps.plus(name, new DoubleMatrix1D(hullWhite.getMulticurveProvider().parameterSensitivity(name, sensitivityDsc.get(name))));
      }
    }
    // Forward
    Map<String, List<ForwardSensitivity>> sensitivityFwd = sensitivity.getForwardSensitivities();
    for (final String name : sensitivityFwd.keySet()) {
      if (curvesSet.contains(name)) {
        ps = ps.plus(name, new DoubleMatrix1D(hullWhite.getMulticurveProvider().parameterForwardSensitivity(name, sensitivityFwd.get(name))));
      }
    }
    // By curve name in the curves set (to have the right order)
    double[] result = new double[0];
    for (String name : curvesSet) {
      DoubleMatrix1D sensi = ps.getSensitivity(name);
      if (sensi != null) {
        result = ArrayUtils.addAll(result, sensi.getData());
      } else {
        result = ArrayUtils.addAll(result, new double[hullWhite.getMulticurveProvider().getNumberOfParameters(name)]);
      }
    }
    return new DoubleMatrix1D(result);
  }

}
