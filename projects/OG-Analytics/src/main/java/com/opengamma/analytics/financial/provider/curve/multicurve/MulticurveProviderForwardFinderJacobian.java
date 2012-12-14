/**
 * Copyright (C) 2012 - present by OpenGamma Inc. and the OpenGamma group of companies
 * 
 * Please see distribution for license.
 */
package com.opengamma.analytics.financial.provider.curve.multicurve;

import java.util.Set;

import com.opengamma.analytics.financial.interestrate.InstrumentDerivative;
import com.opengamma.analytics.financial.provider.description.interestrate.MulticurveProviderForward;
import com.opengamma.analytics.financial.provider.sensitivity.multicurve.AbstractParameterSensitivityMatrixMulticurveProviderCalculator;
import com.opengamma.analytics.math.function.Function1D;
import com.opengamma.analytics.math.matrix.DoubleMatrix1D;
import com.opengamma.analytics.math.matrix.DoubleMatrix2D;

/**
 * Function computing the Jacobian of the error of valuation produce by a array representing the curve parameters. 
 * @author marc
 */
public class MulticurveProviderForwardFinderJacobian extends Function1D<DoubleMatrix1D, DoubleMatrix2D> {

  /**
   * The instrument parameter sensitivity calculator.
   */
  private final AbstractParameterSensitivityMatrixMulticurveProviderCalculator _parameterSensitivityCalculator;
  /**
   * The data required for curve building.
   */
  private final MulticurveProviderForwardBuildingData _data;

  /**
   * Constructor.
   * @param parameterSensitivityCalculator The instrument parameter sensitivity calculator.
   * @param data The data required for curve building.
   */
  public MulticurveProviderForwardFinderJacobian(final AbstractParameterSensitivityMatrixMulticurveProviderCalculator parameterSensitivityCalculator, 
      final MulticurveProviderForwardBuildingData data) {
    _parameterSensitivityCalculator = parameterSensitivityCalculator;
    _data = data;
  }

  @Override
  public DoubleMatrix2D evaluate(DoubleMatrix1D x) {
    final MulticurveProviderForward bundle = _data.getKnownData().copy();
    MulticurveProviderForward newCurves = _data.getGeneratorMarket().evaluate(x);
    Set<String> curvesSet = _data.getGeneratorMarket().getCurvesList();
    bundle.setAll(newCurves);
    final int nbParameters = _data.getNumberOfInstruments();
    final double[][] res = new double[nbParameters][nbParameters];
    for (int loopinstrument = 0; loopinstrument < _data.getNumberOfInstruments(); loopinstrument++) {
      InstrumentDerivative deriv = _data.getInstrument(loopinstrument);
      res[loopinstrument] = _parameterSensitivityCalculator.calculateSensitivity(deriv, bundle, curvesSet).getData();
    }
    return new DoubleMatrix2D(res);
  }

}
