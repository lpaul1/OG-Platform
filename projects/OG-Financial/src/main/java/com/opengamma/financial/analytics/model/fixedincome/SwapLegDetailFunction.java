/**
 * Copyright (C) 2013 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.financial.analytics.model.fixedincome;

import static com.opengamma.engine.value.ValueRequirementNames.SWAP_PAY_LEG_DETAILS;
import static com.opengamma.engine.value.ValueRequirementNames.SWAP_RECEIVE_LEG_DETAILS;

import java.util.Collections;
import java.util.Set;

import org.threeten.bp.Clock;
import org.threeten.bp.LocalDate;
import org.threeten.bp.ZonedDateTime;

import com.google.common.collect.Iterables;
import com.opengamma.OpenGammaRuntimeException;
import com.opengamma.analytics.financial.instrument.annuity.AnnuityDefinition;
import com.opengamma.analytics.financial.instrument.payment.PaymentDefinition;
import com.opengamma.analytics.financial.instrument.swap.SwapDefinition;
import com.opengamma.analytics.financial.interestrate.AnnuityAccrualDatesVisitor;
import com.opengamma.analytics.financial.interestrate.AnnuityDiscountFactorsVisitor;
import com.opengamma.analytics.financial.interestrate.AnnuityFixedRatesVisitor;
import com.opengamma.analytics.financial.interestrate.AnnuityNotionalsVisitor;
import com.opengamma.analytics.financial.interestrate.AnnuityPaymentFractionsVisitor;
import com.opengamma.analytics.financial.interestrate.AnnuityPaymentTimesVisitor;
import com.opengamma.analytics.financial.interestrate.InstrumentDerivative;
import com.opengamma.analytics.financial.interestrate.YieldCurveBundle;
import com.opengamma.analytics.financial.interestrate.annuity.derivative.Annuity;
import com.opengamma.analytics.financial.interestrate.payments.derivative.Payment;
import com.opengamma.analytics.financial.interestrate.swap.derivative.Swap;
import com.opengamma.core.config.ConfigSource;
import com.opengamma.engine.ComputationTarget;
import com.opengamma.engine.function.FunctionExecutionContext;
import com.opengamma.engine.function.FunctionInputs;
import com.opengamma.engine.target.ComputationTargetType;
import com.opengamma.engine.value.ComputedValue;
import com.opengamma.engine.value.ValuePropertyNames;
import com.opengamma.engine.value.ValueRequirement;
import com.opengamma.engine.value.ValueRequirementNames;
import com.opengamma.engine.value.ValueSpecification;
import com.opengamma.financial.OpenGammaExecutionContext;
import com.opengamma.financial.analytics.fixedincome.FixedIncomeInstrumentCurveExposureHelper;
import com.opengamma.financial.analytics.ircurve.calcconfig.ConfigDBCurveCalculationConfigSource;
import com.opengamma.financial.analytics.ircurve.calcconfig.MultiCurveCalculationConfig;
import com.opengamma.financial.analytics.model.YieldCurveFunctionUtils;
import com.opengamma.financial.analytics.timeseries.HistoricalTimeSeriesBundle;
import com.opengamma.financial.analytics.timeseries.HistoricalTimeSeriesFunctionUtils;
import com.opengamma.financial.security.FinancialSecurity;
import com.opengamma.financial.security.FinancialSecurityTypes;
import com.opengamma.financial.security.FinancialSecurityUtils;
import com.opengamma.financial.security.swap.FixedInterestRateLeg;
import com.opengamma.financial.security.swap.SwapSecurity;
import com.opengamma.util.money.Currency;
import com.opengamma.util.money.CurrencyAmount;
import com.opengamma.util.tuple.Pair;

/**
 * @deprecated The parent class of this function is deprecated.
 */
@Deprecated
public class SwapLegDetailFunction extends InterestRateInstrumentFunction {
  /** Whether this function returns details for the pay leg or the receive leg */
  private final boolean _payLeg;

  /**
   * Sets the value requirement name to {@link ValueRequirementNames#SWAP_PAY_LEG_DETAILS} or {@link ValueRequirementNames#SWAP_RECEIVE_LEG_DETAILS}
   * @param payLeg True if the details to be returned are for the pay leg; false returns details for receive legs.
   */
  public SwapLegDetailFunction(final String payLeg) {
    this(Boolean.parseBoolean(payLeg));
  }

  /**
   * Sets the value requirement name to {@link ValueRequirementNames#SWAP_PAY_LEG_DETAILS} or {@link ValueRequirementNames#SWAP_RECEIVE_LEG_DETAILS}
   * @param payLeg True if the details to be returned are for the pay leg; false returns details for receive legs.
   */
  public SwapLegDetailFunction(final boolean payLeg) {
    super(payLeg ? SWAP_PAY_LEG_DETAILS : SWAP_RECEIVE_LEG_DETAILS);
    _payLeg = payLeg;
  }

  @Override
  public ComputationTargetType getTargetType() {
    return FinancialSecurityTypes.SWAP_SECURITY;
  }

  @Override
  public Set<ComputedValue> execute(final FunctionExecutionContext executionContext, final FunctionInputs inputs, final ComputationTarget target,
      final Set<ValueRequirement> desiredValues) {
    final SwapSecurity security = (SwapSecurity) target.getSecurity();
    final Currency currency = FinancialSecurityUtils.getCurrency(security);
    final Clock snapshotClock = executionContext.getValuationClock();
    final ZonedDateTime now = ZonedDateTime.now(snapshotClock);
    final HistoricalTimeSeriesBundle timeSeries = HistoricalTimeSeriesFunctionUtils.getHistoricalTimeSeriesInputs(executionContext, inputs);
    final ValueRequirement desiredValue = Iterables.getOnlyElement(desiredValues);
    final String curveCalculationConfigName = desiredValue.getConstraint(ValuePropertyNames.CURVE_CALCULATION_CONFIG);
    final ConfigSource configSource = OpenGammaExecutionContext.getConfigSource(executionContext);
    final ConfigDBCurveCalculationConfigSource curveCalculationConfigSource = new ConfigDBCurveCalculationConfigSource(configSource);
    final MultiCurveCalculationConfig curveCalculationConfig = curveCalculationConfigSource.getConfig(curveCalculationConfigName);
    if (curveCalculationConfig == null) {
      throw new OpenGammaRuntimeException("Could not find curve calculation configuration named " + curveCalculationConfigName);
    }
    final String[] curveNames = curveCalculationConfig.getYieldCurveNames();
    final int numCurveNames = curveNames.length;
    final String[] fullCurveNames = new String[numCurveNames];
    for (int i = 0; i < numCurveNames; i++) {
      fullCurveNames[i] = curveNames[i] + "_" + currency.getCode();
    }
    final String[] yieldCurveNames = numCurveNames == 1 ? new String[] {fullCurveNames[0], fullCurveNames[0] } : fullCurveNames;
    final String[] curveNamesForSecurity = FixedIncomeInstrumentCurveExposureHelper.getCurveNamesForSecurity(security, yieldCurveNames[0], yieldCurveNames[1]);
    final YieldCurveBundle bundle = YieldCurveFunctionUtils.getAllYieldCurves(inputs, curveCalculationConfig, curveCalculationConfigSource);
    final SwapDefinition definition = (SwapDefinition) security.accept(getVisitor());
    if (definition == null) {
      throw new OpenGammaRuntimeException("Definition for security " + security + " was null");
    }
    final Swap<? extends Payment, ? extends Payment> derivative =
        (Swap<? extends Payment, ? extends Payment>) getDerivative(security, now, timeSeries, curveNamesForSecurity, definition, getConverter());
    final AnnuityDefinition<? extends PaymentDefinition> legDefinition;
    final Annuity<? extends Payment> legDerivative;
    final boolean isFixed;
    if (_payLeg) {
      isFixed = security.getPayLeg() instanceof FixedInterestRateLeg;
      final boolean payFirstLeg = definition.getFirstLeg().isPayer();
      legDefinition = payFirstLeg ? definition.getFirstLeg() : definition.getSecondLeg();
      legDerivative = payFirstLeg ? derivative.getFirstLeg() : derivative.getSecondLeg();
    } else {
      isFixed = security.getReceiveLeg() instanceof FixedInterestRateLeg;
      final boolean payFirstLeg = definition.getFirstLeg().isPayer();
      legDefinition = payFirstLeg ? definition.getSecondLeg() : definition.getFirstLeg();
      legDerivative = payFirstLeg ? derivative.getSecondLeg() : derivative.getFirstLeg();
    }
    if (isFixed) {
      final Pair<LocalDate[], LocalDate[]> accrualDates = legDefinition.accept(AnnuityAccrualDatesVisitor.getInstance(), now.toLocalDate());
      final double[] discountFactors = legDerivative.accept(AnnuityDiscountFactorsVisitor.getInstance(), bundle);
      final double[] paymentTimes = legDerivative.accept(AnnuityPaymentTimesVisitor.getInstance());
      final double[] paymentFractions = legDerivative.accept(AnnuityPaymentFractionsVisitor.getInstance());
      final CurrencyAmount[] notionals = legDefinition.accept(AnnuityNotionalsVisitor.getInstance(), now.toLocalDate());
      final double[] fixedRates = legDerivative.accept(AnnuityFixedRatesVisitor.getInstance());
      final FixedSwapLegDetails details = new FixedSwapLegDetails(accrualDates.getFirst(), accrualDates.getSecond(), discountFactors, paymentTimes,
          paymentFractions, notionals, fixedRates);
      final ValueSpecification spec = new ValueSpecification(getValueRequirementName(), target.toSpecification(), desiredValue.getConstraints());
      return Collections.singleton(new ComputedValue(spec, details));
    }
    return null;
  }

  @Override
  protected Set<ComputedValue> getComputedValues(final InstrumentDerivative derivative, final YieldCurveBundle bundle, final FinancialSecurity security,
      final ComputationTarget target, final String curveCalculationConfigName, final String currency) {
    throw new UnsupportedOperationException();
  }
}
