/**
 * Copyright (C) 2012 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.financial;

import java.util.List;

import com.opengamma.engine.function.config.AbstractRepositoryConfigurationBean;
import com.opengamma.engine.function.config.CombiningRepositoryConfigurationSource;
import com.opengamma.engine.function.config.FunctionConfiguration;
import com.opengamma.engine.function.config.RepositoryConfigurationSource;
import com.opengamma.financial.aggregation.AggregationFunctions;
import com.opengamma.financial.analytics.AnalyticsFunctions;
import com.opengamma.financial.currency.CurrencyFunctions;
import com.opengamma.financial.property.PropertyFunctions;
import com.opengamma.financial.value.ValueFunctions;
import com.opengamma.financial.view.ViewFunctions;

/**
 * Function repository configuration source for the functions contained in this package and sub-packages.
 */
public class FinancialFunctions extends AbstractRepositoryConfigurationBean {

  /**
   * Default instance of a repository configuration source exposing the functions from this package.
   */
  public static final RepositoryConfigurationSource DEFAULT = (new FinancialFunctions()).getObjectCreating();

  @Override
  protected void addAllConfigurations(final List<FunctionConfiguration> functions) {
    // Nothing in this package, just the sub-packages
  }

  protected RepositoryConfigurationSource aggregationFunctionConfiguration() {
    return AggregationFunctions.DEFAULT;
  }

  protected RepositoryConfigurationSource analyticsFunctionConfiguration() {
    return AnalyticsFunctions.DEFAULT;
  }

  protected RepositoryConfigurationSource currencyFunctionConfiguration() {
    return CurrencyFunctions.DEFAULT;
  }

  protected RepositoryConfigurationSource propertyFunctionConfiguration() {
    return PropertyFunctions.DEFAULT;
  }

  protected RepositoryConfigurationSource valueFunctionConfiguration() {
    return ValueFunctions.DEFAULT;
  }

  protected RepositoryConfigurationSource viewFunctionConfiguration() {
    return ViewFunctions.DEFAULT;
  }

  @Override
  protected RepositoryConfigurationSource createObject() {
    return new CombiningRepositoryConfigurationSource(super.createObject(), aggregationFunctionConfiguration(), analyticsFunctionConfiguration(), currencyFunctionConfiguration(),
        propertyFunctionConfiguration(), valueFunctionConfiguration(), viewFunctionConfiguration());
  }

}
