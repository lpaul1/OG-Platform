/**
 * Copyright (C) 2012 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.analytics.financial.interestrate.payments.provider;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.opengamma.analytics.financial.interestrate.payments.derivative.CouponIborCompounding;
import com.opengamma.analytics.financial.provider.description.interestrate.MulticurveProviderInterface;
import com.opengamma.analytics.financial.provider.sensitivity.multicurve.ForwardSensitivity;
import com.opengamma.analytics.financial.provider.sensitivity.multicurve.MulticurveSensitivity;
import com.opengamma.analytics.financial.provider.sensitivity.multicurve.MultipleCurrencyMulticurveSensitivity;
import com.opengamma.util.ArgumentChecker;
import com.opengamma.util.money.MultipleCurrencyAmount;
import com.opengamma.util.tuple.DoublesPair;

/**
 * Method to compute present value and present value sensitivity for Ibor compounded coupon.
 */
public final class CouponIborCompoundingDiscountingMethod {

  /**
   * The method unique instance.
   */
  private static final CouponIborCompoundingDiscountingMethod INSTANCE = new CouponIborCompoundingDiscountingMethod();

  /**
   * Return the unique instance of the class.
   * @return The instance.
   */
  public static CouponIborCompoundingDiscountingMethod getInstance() {
    return INSTANCE;
  }

  /**
   * Private constructor.
   */
  private CouponIborCompoundingDiscountingMethod() {
  }

  /**
   * Compute the present value of a Ibor compounded coupon by discounting.
   * @param coupon The coupon.
   * @param multicurve The multi-curve provider.
   * @return The present value.
   */
  public MultipleCurrencyAmount presentValue(final CouponIborCompounding coupon, final MulticurveProviderInterface multicurve) {
    ArgumentChecker.notNull(coupon, "Coupon");
    ArgumentChecker.notNull(multicurve, "Multi-curves provider");
    final int nbSubPeriod = coupon.getFixingTimes().length;
    double notionalAccrued = coupon.getNotionalAccrued();
    for (int loopsub = 0; loopsub < nbSubPeriod; loopsub++) {
      final double ratioForward = (1.0 + coupon.getPaymentAccrualFactors()[loopsub]
          * multicurve.getForwardRate(coupon.getIndex(), coupon.getFixingPeriodStartTimes()[loopsub], coupon.getFixingPeriodEndTimes()[loopsub], coupon.getFixingPeriodAccrualFactors()[loopsub]));
      notionalAccrued *= ratioForward;
    }
    final double df = multicurve.getDiscountFactor(coupon.getCurrency(), coupon.getPaymentTime());
    final double pv = (notionalAccrued - coupon.getNotional()) * df;
    return MultipleCurrencyAmount.of(coupon.getCurrency(), pv);
  }

  /**
   * Compute the present value sensitivity to rates of a Ibor compounded coupon by discounting.
   * @param coupon The coupon.
   * @param multicurve The multi-curve provider.
   * @return The present value sensitivity.
   */
  public MultipleCurrencyMulticurveSensitivity presentValueCurveSensitivity(final CouponIborCompounding coupon, final MulticurveProviderInterface multicurve) {
    ArgumentChecker.notNull(coupon, "Coupon");
    ArgumentChecker.notNull(multicurve, "Multi-curves provider");
    final int nbSubPeriod = coupon.getFixingTimes().length;
    double notionalAccrued = coupon.getNotionalAccrued();
    final double[] forward = new double[nbSubPeriod];
    final double[] ratioForward = new double[nbSubPeriod];
    for (int loopsub = 0; loopsub < nbSubPeriod; loopsub++) {
      forward[loopsub] = multicurve.getForwardRate(coupon.getIndex(), coupon.getFixingPeriodStartTimes()[loopsub], coupon.getFixingPeriodEndTimes()[loopsub],
          coupon.getFixingPeriodAccrualFactors()[loopsub]);
      ratioForward[loopsub] = 1.0 + coupon.getPaymentAccrualFactors()[loopsub] * forward[loopsub];
      notionalAccrued *= ratioForward[loopsub];
    }
    final double dfPayment = multicurve.getDiscountFactor(coupon.getCurrency(), coupon.getPaymentTime());
    // Backward sweep
    final double pvBar = 1.0;
    final double dfPaymentBar = (notionalAccrued - coupon.getNotional()) * pvBar;
    final double notionalAccruedBar = dfPayment * pvBar;
    final double[] ratioForwardBar = new double[nbSubPeriod];
    final double[] forwardBar = new double[nbSubPeriod];
    for (int loopsub = 0; loopsub < nbSubPeriod; loopsub++) {
      ratioForwardBar[loopsub] = notionalAccrued / ratioForward[loopsub] * notionalAccruedBar;
      forwardBar[loopsub] = coupon.getPaymentAccrualFactors()[loopsub] * ratioForwardBar[loopsub];
    }
    final Map<String, List<DoublesPair>> mapDsc = new HashMap<>();
    final List<DoublesPair> listDiscounting = new ArrayList<>();
    listDiscounting.add(new DoublesPair(coupon.getPaymentTime(), -coupon.getPaymentTime() * dfPayment * dfPaymentBar));
    mapDsc.put(multicurve.getName(coupon.getCurrency()), listDiscounting);
    final Map<String, List<ForwardSensitivity>> mapFwd = new HashMap<>();
    final List<ForwardSensitivity> listForward = new ArrayList<>();
    for (int loopsub = 0; loopsub < nbSubPeriod; loopsub++) {
      listForward.add(new ForwardSensitivity(coupon.getFixingPeriodStartTimes()[loopsub], coupon.getFixingPeriodEndTimes()[loopsub], coupon.getFixingPeriodAccrualFactors()[loopsub],
          forwardBar[loopsub]));
    }
    mapFwd.put(multicurve.getName(coupon.getIndex()), listForward);
    return MultipleCurrencyMulticurveSensitivity.of(coupon.getCurrency(), MulticurveSensitivity.of(mapDsc, mapFwd));
  }
}
