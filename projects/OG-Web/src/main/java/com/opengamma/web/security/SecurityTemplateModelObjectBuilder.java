/**
 * Copyright (C) 2013 - present by OpenGamma Inc. and the OpenGamma group of companies
 * 
 * Please see distribution for license.
 */
package com.opengamma.web.security;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.joda.beans.impl.flexi.FlexiBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.opengamma.core.id.ExternalSchemes;
import com.opengamma.core.security.Security;
import com.opengamma.financial.security.FinancialSecurityVisitorSameValueAdapter;
import com.opengamma.financial.security.capfloor.CapFloorCMSSpreadSecurity;
import com.opengamma.financial.security.capfloor.CapFloorSecurity;
import com.opengamma.financial.security.cds.CreditDefaultSwapIndexComponent;
import com.opengamma.financial.security.cds.CreditDefaultSwapIndexDefinitionSecurity;
import com.opengamma.financial.security.fra.FRASecurity;
import com.opengamma.financial.security.future.AgricultureFutureSecurity;
import com.opengamma.financial.security.future.BondFutureDeliverable;
import com.opengamma.financial.security.future.BondFutureSecurity;
import com.opengamma.financial.security.future.DeliverableSwapFutureSecurity;
import com.opengamma.financial.security.future.EnergyFutureSecurity;
import com.opengamma.financial.security.future.EquityFutureSecurity;
import com.opengamma.financial.security.future.EquityIndexDividendFutureSecurity;
import com.opengamma.financial.security.future.FXFutureSecurity;
import com.opengamma.financial.security.future.FederalFundsFutureSecurity;
import com.opengamma.financial.security.future.IndexFutureSecurity;
import com.opengamma.financial.security.future.InterestRateFutureSecurity;
import com.opengamma.financial.security.future.MetalFutureSecurity;
import com.opengamma.financial.security.future.StockFutureSecurity;
import com.opengamma.financial.security.option.CreditDefaultSwapOptionSecurity;
import com.opengamma.financial.security.option.EquityBarrierOptionSecurity;
import com.opengamma.financial.security.option.EquityIndexOptionSecurity;
import com.opengamma.financial.security.option.EquityOptionSecurity;
import com.opengamma.financial.security.option.IRFutureOptionSecurity;
import com.opengamma.financial.security.option.SwaptionSecurity;
import com.opengamma.financial.security.swap.FixedInflationSwapLeg;
import com.opengamma.financial.security.swap.FixedInterestRateLeg;
import com.opengamma.financial.security.swap.FixedVarianceSwapLeg;
import com.opengamma.financial.security.swap.FloatingGearingIRLeg;
import com.opengamma.financial.security.swap.FloatingInterestRateLeg;
import com.opengamma.financial.security.swap.FloatingSpreadIRLeg;
import com.opengamma.financial.security.swap.FloatingVarianceSwapLeg;
import com.opengamma.financial.security.swap.InflationIndexSwapLeg;
import com.opengamma.financial.security.swap.SwapLegVisitor;
import com.opengamma.financial.security.swap.SwapSecurity;
import com.opengamma.financial.security.swap.YearOnYearInflationSwapSecurity;
import com.opengamma.financial.security.swap.ZeroCouponInflationSwapSecurity;
import com.opengamma.id.ExternalId;
import com.opengamma.master.orgs.ManageableOrganization;
import com.opengamma.master.orgs.OrganizationMaster;
import com.opengamma.master.orgs.OrganizationSearchRequest;
import com.opengamma.master.orgs.OrganizationSearchResult;
import com.opengamma.master.security.ManageableSecurity;
import com.opengamma.master.security.SecurityMaster;
import com.opengamma.util.time.Tenor;

/**
 * Builds the model object used in the security freemarker templates
 */
/*package*/ class SecurityTemplateModelObjectBuilder extends FinancialSecurityVisitorSameValueAdapter<Void> {

  private static final Logger s_logger = LoggerFactory.getLogger(SecurityTemplateModelObjectBuilder.class);
  
  private final FlexiBean _out;
  private final SecurityMaster _securityMaster;
  private final OrganizationMaster _organizationMaster;
  
  SecurityTemplateModelObjectBuilder(final FlexiBean out, final SecurityMaster securityMaster, final OrganizationMaster organizationMaster) {
    super(null);
    _out = out;
    _securityMaster = securityMaster;
    _organizationMaster = organizationMaster;
  }
  
  private void addFutureSecurityType(final String futureType) {
    _out.put("futureSecurityType", futureType);
  }
  
  private void addUnderlyingSecurity(ExternalId underlyingId) {
    ManageableSecurity security = getSecurity(underlyingId);
    if (security != null) {
      _out.put("underlyingSecurity", security);
    }
  }
  
  private ManageableSecurity getSecurity(ExternalId underlyingIdentifier) {
    return AbstractWebSecurityResource.getSecurity(underlyingIdentifier, _securityMaster);
  }

  @Override
  public Void visitSwapSecurity(SwapSecurity security) {
    _out.put("payLegType", security.getPayLeg().accept(new SwapLegClassifierVisitor()));
    _out.put("receiveLegType", security.getReceiveLeg().accept(new SwapLegClassifierVisitor()));
    return null;
  }
    
  @Override
  public Void visitInterestRateFutureSecurity(InterestRateFutureSecurity security) {
    addFutureSecurityType("InterestRate");
    addUnderlyingSecurity(security.getUnderlyingId());
    return null;
  }

  @Override
  public Void visitBondFutureSecurity(BondFutureSecurity security) {
    addFutureSecurityType("BondFuture");
    Map<String, String> basket = new TreeMap<String, String>();
    for (BondFutureDeliverable bondFutureDeliverable : security.getBasket()) {
      String identifierValue = bondFutureDeliverable.getIdentifiers().getValue(ExternalSchemes.BLOOMBERG_BUID);
      basket.put(ExternalSchemes.BLOOMBERG_BUID.getName() + "-" + identifierValue, String.valueOf(bondFutureDeliverable.getConversionFactor()));
    }
    _out.put("basket", basket);
    return null;
  }
  
  @Override
  public Void visitCapFloorSecurity(CapFloorSecurity security) {
    addUnderlyingSecurity(security.getUnderlyingId());
    return null;
  }
  
  @Override
  public Void visitCapFloorCMSSpreadSecurity(CapFloorCMSSpreadSecurity security) {
    Security shortUnderlying = getSecurity(security.getShortId());
    Security longUnderlying = getSecurity(security.getLongId());
    if (shortUnderlying != null) {
      _out.put("shortSecurity", shortUnderlying);
    }
    if (longUnderlying != null) {
      _out.put("longSecurity", longUnderlying);
    }
    return null;
  }

  @Override
  public Void visitEnergyFutureSecurity(EnergyFutureSecurity security) {
    addFutureSecurityType("EnergyFuture");
    addUnderlyingSecurity(security.getUnderlyingId());
    return null;
  }

  @Override
  public Void visitEquityBarrierOptionSecurity(EquityBarrierOptionSecurity security) {
    addUnderlyingSecurity(security.getUnderlyingId());
    return null;
  }

  @Override
  public Void visitEquityFutureSecurity(EquityFutureSecurity security) {
    addFutureSecurityType("EquityFuture");
    addUnderlyingSecurity(security.getUnderlyingId());
    return null;
  }

  @Override
  public Void visitEquityIndexDividendFutureSecurity(EquityIndexDividendFutureSecurity security) {
    addFutureSecurityType("EquityIndexDividendFuture");
    addUnderlyingSecurity(security.getUnderlyingId());
    return null;
  }
  
  @Override
  public Void visitEquityIndexOptionSecurity(EquityIndexOptionSecurity security) {
    addUnderlyingSecurity(security.getUnderlyingId());
    return null;
  }

  @Override
  public Void visitEquityOptionSecurity(EquityOptionSecurity security) {
    addUnderlyingSecurity(security.getUnderlyingId());
    return null;
  }
  
  @Override
  public Void visitFRASecurity(FRASecurity security) {
    addUnderlyingSecurity(security.getUnderlyingId());
    return null;
  }

  @Override
  public Void visitFXFutureSecurity(FXFutureSecurity security) {
    addFutureSecurityType("FxFuture");
    return null;
  }

  @Override
  public Void visitIndexFutureSecurity(IndexFutureSecurity security) {
    addFutureSecurityType("IndexFuture");
    addUnderlyingSecurity(security.getUnderlyingId());
    return null;
  }
  
  @Override
  public Void visitIRFutureOptionSecurity(IRFutureOptionSecurity security) {
    addUnderlyingSecurity(security.getUnderlyingId());
    return null;
  }

  @Override
  public Void visitMetalFutureSecurity(MetalFutureSecurity security) {
    addFutureSecurityType("MetalFuture");
    addUnderlyingSecurity(security.getUnderlyingId());
    return null;
  }

  @Override
  public Void visitStockFutureSecurity(StockFutureSecurity security) {
    addFutureSecurityType("StockFuture");
    addUnderlyingSecurity(security.getUnderlyingId());
    return null;
  }
  
  @Override
  public Void visitSwaptionSecurity(SwaptionSecurity security) {
    addUnderlyingSecurity(security.getUnderlyingId());
    return null;
  }

  @Override
  public Void visitAgricultureFutureSecurity(AgricultureFutureSecurity security) {
    addFutureSecurityType("AgricultureFuture");
    return null;
  }

  @Override
  public Void visitDeliverableSwapFutureSecurity(DeliverableSwapFutureSecurity security) {
    addFutureSecurityType("DeliverableSwapFuture");
    return null;
  }
  
  @Override
  public Void visitCreditDefaultSwapIndexDefinitionSecurity(CreditDefaultSwapIndexDefinitionSecurity security) {
    List<String> tenors = Lists.newArrayList();
    for (Tenor tenor : security.getTerms()) {
      tenors.add(tenor.getPeriod().toString());
    }
    _out.put("terms", ImmutableList.copyOf(tenors));
    Set<CreditDefaultSwapIndexComponent> components = new TreeSet<>(Collections.reverseOrder());
    for (CreditDefaultSwapIndexComponent component : security.getComponents()) {
      components.add(component);
    }
    _out.put("components", ImmutableList.copyOf(components));
    return null;
  }
  
  @Override
  public Void visitCreditDefaultSwapOptionSecurity(CreditDefaultSwapOptionSecurity security) {
    ExternalId underlyingId = security.getUnderlyingId();
    if (underlyingId != null) {
      OrganizationSearchRequest request = new OrganizationSearchRequest();
      if (underlyingId.getScheme().equals(ExternalSchemes.MARKIT_RED_CODE)) {
        request.setObligorREDCode(underlyingId.getValue());
        OrganizationSearchResult searchResult = _organizationMaster.search(request);
        ManageableOrganization organization = searchResult.getSingleOrganization();
        if (organization != null) {
          _out.put("underlyingOrganization", organization);
        }
      } else {
        s_logger.warn("{} does not currently support CDSOption underlying lookup based on {}", WebSecuritiesResource.class, underlyingId.getScheme().getName());
      }
    }
    return null;
  }
  
  @Override
  public Void visitFederalFundsFutureSecurity(FederalFundsFutureSecurity security) {
    addFutureSecurityType("FederalFundsFutureSecurity");
    addUnderlyingSecurity(security.getUnderlyingId());
    return null;
  }
  
  @Override
  public Void visitZeroCouponInflationSwapSecurity(ZeroCouponInflationSwapSecurity security) {
    _out.put("payLegType", security.getPayLeg().accept(new SwapLegClassifierVisitor()));
    _out.put("receiveLegType", security.getReceiveLeg().accept(new SwapLegClassifierVisitor()));
    return null;
  }
  
  @Override
  public Void visitYearOnYearInflationSwapSecurity(YearOnYearInflationSwapSecurity security) {
    _out.put("payLegType", security.getPayLeg().accept(new SwapLegClassifierVisitor()));
    _out.put("receiveLegType", security.getReceiveLeg().accept(new SwapLegClassifierVisitor()));
    return null;
  }

  /**
   * SwapLegClassifierVisitor
   */
  private static class SwapLegClassifierVisitor implements SwapLegVisitor<String> {
    @Override
    public String visitFixedInterestRateLeg(FixedInterestRateLeg swapLeg) {
      return "FixedInterestRateLeg";
    }

    @Override
    public String visitFloatingInterestRateLeg(FloatingInterestRateLeg swapLeg) {
      return "FloatingInterestRateLeg";
    }

    @Override
    public String visitFloatingSpreadIRLeg(FloatingSpreadIRLeg swapLeg) {
      return "FloatingSpreadInterestRateLeg";
    }

    @Override
    public String visitFloatingGearingIRLeg(FloatingGearingIRLeg swapLeg) {
      return "FloatingGearingInterestRateLeg";
    }

    @Override
    public String visitFixedVarianceSwapLeg(FixedVarianceSwapLeg swapLeg) {
      return "FixedVarianceLeg";
    }

    @Override
    public String visitFloatingVarianceSwapLeg(FloatingVarianceSwapLeg swapLeg) {
      return "FloatingVarianceLeg";
    }

    @Override
    public String visitFixedInflationSwapLeg(FixedInflationSwapLeg swapLeg) {
      return "FixedInflationLeg";
    }

    @Override
    public String visitInflationIndexSwapLeg(InflationIndexSwapLeg swapLeg) {
      return "InflationIndexLeg";
    }
  }
}
