/**
 * Copyright (C) 2009 - 2009 by OpenGamma Inc.
 *
 * Please see distribution for license.
 */
package com.opengamma.engine.view;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import com.opengamma.id.UniqueIdentifier;
import com.opengamma.livedata.msg.UserPrincipal;
import com.opengamma.util.ArgumentChecker;

/**
 * The encapsulated logic that controls how precisely a view is to be constructed
 * and computed.
 */
public class ViewDefinition implements Serializable {
  private final String _name;
  private final UniqueIdentifier _portfolioId;
  private final UserPrincipal _liveDataUser;
  
  // NOTE: jim 14-June-2010 -- put these back in as we're going to use them now.
  private boolean _computePortfolioNodeCalculations = true;
  private boolean _computePositionNodeCalculations = true;
  private boolean _computeSecurityNodeCalculations /*= false*/;
  private boolean _computePrimitiveNodeCalculations /*= false*/;
  
  /** 
   * A delta recomputation of the view should be performed at this interval.
   * Milliseconds.
   * 0 = can be performed as often as there is CPU resources for.
   * Null = delta recomputation only needs to be performed if underlying
   * market data changes.  
   */
  private Long _deltaRecalculationPeriod;
  
  /** 
   * A full recomputation of the view should be performed at this interval 
   * (i.e., no delta vs. previous result should be used).
   * Milliseconds.
   * 0 = each computation should be a full recomputation.
   * Null = no full recomputation needs to be performed - previous result can always be used
   */ 
  private Long _fullRecalculationPeriod;
  
  private final Map<String, ViewCalculationConfiguration> _calculationConfigurationsByName =
    new TreeMap<String, ViewCalculationConfiguration>();
  
  public ViewDefinition(String name, UniqueIdentifier portfolioId, String userName) {
    ArgumentChecker.notNull(name, "View name");
    ArgumentChecker.notNull(portfolioId, "Portfolio id");
    ArgumentChecker.notNull(userName, "User name");
    
    _name = name;
    _portfolioId = portfolioId;
    _liveDataUser = UserPrincipal.getLocalUser(userName);
  }
  
  public ViewDefinition(String name, UniqueIdentifier portfolioId, UserPrincipal liveDataUser) {
    ArgumentChecker.notNull(name, "View name");
    ArgumentChecker.notNull(portfolioId, "Portfolio id");
    ArgumentChecker.notNull(liveDataUser, "User name");
    
    _name = name;
    _portfolioId = portfolioId;
    _liveDataUser = liveDataUser;
  }
  
  public Set<String> getAllValueRequirements() {
    Set<String> requirements = new TreeSet<String>();
    for (ViewCalculationConfiguration calcConfig : _calculationConfigurationsByName.values()) {
      requirements.addAll(calcConfig.getAllValueRequirements());
    }
    return requirements;
  }

  public String getName() {
    return _name;
  }
  
  public UniqueIdentifier getPortfolioId() {
    return _portfolioId;
  }
  
  /**
   * @return The LiveData user should be used to create 
   * LiveData subscriptions. It is thus a kind of 'super-user'
   * and ensures that the View can be materialized even without
   * any end user trying to use it.
   * <p>
   * Authenticating the end users of the View (of which there can be many) 
   * is a separate matter entirely and has nothing to do with this user.  
   */
  public UserPrincipal getLiveDataUser() {
    return _liveDataUser;
  }
  
  public Collection<ViewCalculationConfiguration> getAllCalculationConfigurations() {
    return new ArrayList<ViewCalculationConfiguration>(_calculationConfigurationsByName.values());
  }
  
  public Set<String> getAllCalculationConfigurationNames() {
    return Collections.unmodifiableSet(_calculationConfigurationsByName.keySet());
  }
  
  public Map<String, ViewCalculationConfiguration> getAllCalculationConfigurationsByName() {
    return Collections.unmodifiableMap(_calculationConfigurationsByName);
  }
  
  public ViewCalculationConfiguration getCalculationConfiguration(String configurationName) {
    return _calculationConfigurationsByName.get(configurationName);
  }
  
  public void addViewCalculationConfiguration(ViewCalculationConfiguration calcConfig) {
    ArgumentChecker.notNull(calcConfig, "calculation configuration");
    ArgumentChecker.notNull(calcConfig.getName(), "Configuration name");
    _calculationConfigurationsByName.put(calcConfig.getName(), calcConfig);
  }
  
  public void addValueDefinition(String calculationConfigurationName, String securityType, String requirementName) {
    ViewCalculationConfiguration calcConfig = _calculationConfigurationsByName.get(calculationConfigurationName);
    if (calcConfig == null) {
      calcConfig = new ViewCalculationConfiguration(this, calculationConfigurationName);
      _calculationConfigurationsByName.put(calculationConfigurationName, calcConfig);
    }
    calcConfig.addValueRequirement(securityType, requirementName);
  }

  /**
   * @return A delta recomputation of the view should be performed at this interval.
   * Milliseconds.
   * 0 = can be performed as often as there is CPU resources for.
   * Null = delta recomputation only needs to be performed if underlying
   * market data changes.
   */
  public Long getDeltaRecalculationPeriod() {
    return _deltaRecalculationPeriod;
  }

  /**
   * @param minimumRecalculationPeriod the minimumRecalculationPeriod to set, milliseconds
   */
  public void setDeltaRecalculationPeriod(Long minimumRecalculationPeriod) {
    _deltaRecalculationPeriod = minimumRecalculationPeriod;
  }

  /**
   * @return A full recomputation of the view should be performed at this interval 
   * (i.e., no delta vs. previous result should be used).
   * Milliseconds.
   * 0 = each computation should be a full recomputation.
   * Null = no full recomputation needs to be performed - previous result can always be used
   */
  public Long getFullRecalculationPeriod() {
    return _fullRecalculationPeriod;
  }

  /**
   * @param fullRecalculationPeriod the fullRecalculationPeriod to set, milliseconds
   */
  public void setFullRecalculationPeriod(Long fullRecalculationPeriod) {
    _fullRecalculationPeriod = fullRecalculationPeriod;
  }

  /**
   * @return whether or not to compute all portfolio nodes, rather than just those required
   */
  public boolean isComputePortfolioNodeCalculations() {
    return _computePortfolioNodeCalculations;
  }

  /**
   * @param computePortfolioNodeCalculations whether or not to compute all portfolio nodes, rather than just those required
   */
  public void setComputePortfolioNodeCalculations(boolean computePortfolioNodeCalculations) {
    _computePortfolioNodeCalculations = computePortfolioNodeCalculations;
  }

  /**
   * @return whether or not to compute all position nodes, rather than just those required
   */
  public boolean isComputePositionNodeCalculations() {
    return _computePositionNodeCalculations;
  }

  /**
   * @param computePositionNodeCalculations whether or not to compute all position nodes, rather than just those required
   */
  public void setComputePositionNodeCalculations(boolean computePositionNodeCalculations) {
    _computePositionNodeCalculations = computePositionNodeCalculations;
  }

  /**
   * @return whether or not to compute all security nodes, rather than just those required
   */
  public boolean isComputeSecurityNodeCalculations() {
    return _computeSecurityNodeCalculations;
  }

  /**
   * @param computeSecurityNodeCalculations whether or not to compute all security nodes, rather than just those required
   */
  public void setComputeSecurityNodeCalculations(boolean computeSecurityNodeCalculations) {
    _computeSecurityNodeCalculations = computeSecurityNodeCalculations;
  }

  /**
   * @return whether or not to compute all primitive nodes, rather than just those required
   */
  public boolean isComputePrimitiveNodeCalculations() {
    return _computePrimitiveNodeCalculations;
  }

  /**
   * @param computePrimitiveNodeCalculations whether or not to compute all primitive nodes, rather than just those required
   */
  public void setComputePrimitiveNodeCalculations(boolean computePrimitiveNodeCalculations) {
    _computePrimitiveNodeCalculations = computePrimitiveNodeCalculations;
  }


}
