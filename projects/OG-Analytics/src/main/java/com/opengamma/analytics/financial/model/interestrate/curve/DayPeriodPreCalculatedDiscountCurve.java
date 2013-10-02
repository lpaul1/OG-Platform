/**
 * Copyright (C) 2013 - present by OpenGamma Inc. and the OpenGamma group of companies
 * 
 * Please see distribution for license.
 */
package com.opengamma.analytics.financial.model.interestrate.curve;

import com.opengamma.analytics.math.curve.DoublesCurve;
import com.opengamma.util.ArgumentChecker;
import com.opengamma.util.time.DateUtils;

/**
 * Wraps a normal {@link DiscountCurve} and pre-calculates all future points
 * over a number of years, rendering all calls to obtain discount factors
 * to an array lookup. If {@link #preCalculateDiscountFactors(int)} is not called,
 * this class behaves precisely as a normal {@link DiscountCurve}.
 * <p/>
 * This is <em>not</em> a general purpose class, and is only appropriate when
 * the following constraints are met:
 * <ul>
 *   <li>Curve data changes on a slow period (on the order of hours or days);</li>
 *   <li>Forward points are only requested on a daily boundary (e.g. at midnight,
 *       rather than for discrete times during a day);</li>
 *   <li>The underlying curve is using a sufficiently slow interpolator that the
 *       pre-computation is appropriate</li>
 *   <li>Sufficiently large portfolios or numbers of calculations will happen
 *       with the same curve object to warrant the pre-computation.</li>
 *   <li>No extrapolation is required.</li>
 * </ul>
 * <p/>
 * Given these caveats, this class should only be used in cases where performance testing
 * and constraints on the problem domain warrant developers using this class.
 * <p/>
 * For more information, see <a href="http://jira.opengamma.com/browse/PLAT-4688">PLAT-4688</a>.
 */
public class DayPeriodPreCalculatedDiscountCurve extends DiscountCurve {
  private double[] _preCalculatedDiscountFactors;

  /**
   * @param name The discount curve name.
   * @param discountFactorCurve The underlying curve.
   */
  public DayPeriodPreCalculatedDiscountCurve(String name, DoublesCurve discountFactorCurve) {
    super(name, discountFactorCurve);
  }
  
  /**
   * Pre calculate all discount factors for every single discrete day
   * over the given number of years.
   * @param numYears the number of years to pre-calculate.
   */
  public void preCalculateDiscountFactors(int numYears) {
    ArgumentChecker.isTrue(numYears >= 1, "numYears must be more than 1");
    // Because of leap year and other calendar issues, we can't actually
    // accurately pre-calculate the number of forward days over that number
    // of years. Therefore, we overallocate the array and go beyond the end
    // of the year range slightly.
    
    _preCalculatedDiscountFactors = new double[numYears * 366];
    int numDays = 0;
    while (numDays < _preCalculatedDiscountFactors.length) {
      double xValue = ((double) numDays) / DateUtils.DAYS_PER_YEAR;
      double yValue = super.getDiscountFactor(xValue);
      _preCalculatedDiscountFactors[numDays] = yValue;
      numDays++;
    }
  }

  @Override
  public double getDiscountFactor(double t) {
    if (_preCalculatedDiscountFactors == null) {
      return super.getDiscountFactor(t);
    }
    long nDaysLong = Math.round(t * 365.5);
    if (nDaysLong > Integer.MAX_VALUE) {
      return super.getDiscountFactor(t);
    }
    int nDays = (int) nDaysLong;
    if (nDays > _preCalculatedDiscountFactors.length) {
      return super.getDiscountFactor(t);
    }
    return _preCalculatedDiscountFactors[nDays];
  }

}
