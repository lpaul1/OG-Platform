/**
 * Copyright (C) 2012 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.web.analytics.blotter;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertNotNull;

import java.math.BigDecimal;

import org.apache.commons.lang.ArrayUtils;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.threeten.bp.LocalDate;
import org.threeten.bp.LocalTime;
import org.threeten.bp.OffsetTime;
import org.threeten.bp.ZoneOffset;

import com.google.common.collect.ImmutableMap;
import com.opengamma.financial.security.fx.FXForwardSecurity;
import com.opengamma.id.ExternalId;
import com.opengamma.id.UniqueId;
import com.opengamma.id.VersionCorrection;
import com.opengamma.master.portfolio.ManageablePortfolio;
import com.opengamma.master.portfolio.ManageablePortfolioNode;
import com.opengamma.master.portfolio.PortfolioDocument;
import com.opengamma.master.portfolio.PortfolioMaster;
import com.opengamma.master.portfolio.impl.InMemoryPortfolioMaster;
import com.opengamma.master.position.ManageablePosition;
import com.opengamma.master.position.ManageableTrade;
import com.opengamma.master.position.PositionDocument;
import com.opengamma.master.position.PositionMaster;
import com.opengamma.master.position.impl.InMemoryPositionMaster;
import com.opengamma.master.security.ManageableSecurity;
import com.opengamma.master.security.SecurityMaster;
import com.opengamma.master.security.impl.InMemorySecurityMaster;
import com.opengamma.util.money.Currency;

/**
 *
 */
public class OtcTradeBuilderTest {

  private static final ImmutableMap<String,String> ATTRIBUTES = ImmutableMap.of("attr1", "val1", "attr2", "val2");
  private static final OffsetTime PREMIUM_TIME = LocalTime.of(13, 0).atOffset(ZoneOffset.UTC);
  private static final OffsetTime TRADE_TIME = LocalTime.of(10, 0).atOffset(ZoneOffset.UTC);
  private static final LocalDate PREMIUM_DATE = LocalDate.of(2012, 12, 25);
  private static final LocalDate TRADE_DATE = LocalDate.of(2012, 12, 21);
  private static final double PREMIUM = 1234d;
  private static final ExternalId COUNTERPARTY_ID = ExternalId.of("Cpty", "testCpty");

  private SecurityMaster _securityMaster;
  private PositionMaster _positionMaster;
  private PortfolioMaster _portfolioMaster;
  private OtcTradeBuilder _builder;
  private ManageablePortfolio _savedPortfolio;
  private UniqueId _nodeId;

  // TODO test that the URL ID is always unversioned and the trade ID is always versioned
  // TODO what happens if an existing trade's security is changed?

  // TODO create trade with various fields missing (especially attributes)

  // TODO move to BlotterTestUtils?
  private static BeanDataSource createTradeData(Object... valuePairs) {
    Object[] basicData = {
        "type", "OtcTrade",
        "counterparty", "testCpty",
        "tradeDate", "2012-12-21",
        "tradeTime", "10:00+00:00",
        "premium", "1234",
        "premiumCurrency", "GBP",
        "premiumDate", "2012-12-25",
        "premiumTime", "13:00+00:00",
        "attributes", ATTRIBUTES};
    Object[] tradeData = ArrayUtils.addAll(basicData, valuePairs);
    return BlotterTestUtils.beanData(tradeData);
  }

  @BeforeMethod
  public void setUp() throws Exception {
    _securityMaster = new InMemorySecurityMaster();
    _positionMaster = new InMemoryPositionMaster();
    _portfolioMaster = new InMemoryPortfolioMaster();
    _builder = new OtcTradeBuilder(_positionMaster,
                                   _portfolioMaster,
                                   _securityMaster,
                                   BlotterResource.s_metaBeans,
                                   BlotterResource.getStringConvert());
    ManageablePortfolio portfolio = new ManageablePortfolio();
    ManageablePortfolioNode root = new ManageablePortfolioNode();
    ManageablePortfolioNode node = new ManageablePortfolioNode();
    root.addChildNode(node);
    portfolio.setRootNode(root);
    _savedPortfolio = _portfolioMaster.add(new PortfolioDocument(portfolio)).getPortfolio();
    _nodeId = _savedPortfolio.getRootNode().getChildNodes().get(0).getUniqueId();
  }

  @Test
  public void newSecurityWithNoUnderlying() {
    UniqueId tradeId = _builder.addTrade(createTradeData(), BlotterTestUtils.FX_FORWARD_DATA_SOURCE, null, _nodeId);
    ManageableTrade trade = _positionMaster.getTrade(tradeId);
    UniqueId positionId = trade.getParentPositionId();
    ManageablePosition position = _positionMaster.get(positionId).getPosition();
    assertEquals(BigDecimal.ONE, trade.getQuantity());
    assertEquals(BigDecimal.ONE, position.getQuantity());
    ManageableSecurity security = _securityMaster.get(trade.getSecurityLink().getObjectId(),
                                                      VersionCorrection.LATEST).getSecurity();
    assertNotNull(security);
    security.setUniqueId(null); // so it can be tested for equality against the unsaved version
    assertEquals(BlotterTestUtils.FX_FORWARD, security);
    assertEquals(COUNTERPARTY_ID, trade.getCounterpartyExternalId());
    assertEquals(PREMIUM, trade.getPremium());
    assertEquals(Currency.GBP, trade.getPremiumCurrency());
    assertEquals(PREMIUM_DATE, trade.getPremiumDate());
    assertEquals(TRADE_DATE, trade.getTradeDate());
    assertEquals(PREMIUM_TIME, trade.getPremiumTime());
    assertEquals(TRADE_TIME, trade.getTradeTime());
    assertEquals(ATTRIBUTES, trade.getAttributes());

    // can't check the node ID as nodes are completely replaced
    ManageablePortfolioNode loadedRoot = _portfolioMaster.get(_savedPortfolio.getUniqueId()).getPortfolio().getRootNode();
    ManageablePortfolioNode loadedNode = loadedRoot.getChildNodes().get(0);
    assertEquals(1, loadedNode.getPositionIds().size());
    assertEquals(positionId.getObjectId(), loadedNode.getPositionIds().get(0));
  }

  @Test
  public void newSecurityWithFungibleUnderlying() {
    UniqueId tradeId = _builder.addTrade(createTradeData(), BlotterTestUtils.EQUITY_VARIANCE_SWAP_DATA_SOURCE, null, _nodeId);
    ManageableTrade trade = _positionMaster.getTrade(tradeId);
    UniqueId positionId = trade.getParentPositionId();
    ManageablePosition position = _positionMaster.get(positionId).getPosition();
    assertEquals(BigDecimal.ONE, position.getQuantity());
    ManageableSecurity security =
        _securityMaster.get(trade.getSecurityLink().getObjectId(), VersionCorrection.LATEST).getSecurity();
    assertNotNull(security);
    security.setUniqueId(null); // so it can be tested for equality against the unsaved version
    assertEquals(BlotterTestUtils.EQUITY_VARIANCE_SWAP, security);

    assertEquals(COUNTERPARTY_ID, trade.getCounterpartyExternalId());
    assertEquals(PREMIUM, trade.getPremium());
    assertEquals(Currency.GBP, trade.getPremiumCurrency());
    assertEquals(PREMIUM_DATE, trade.getPremiumDate());
    assertEquals(TRADE_DATE, trade.getTradeDate());
    assertEquals(PREMIUM_TIME, trade.getPremiumTime());
    assertEquals(TRADE_TIME, trade.getTradeTime());
    assertEquals(ATTRIBUTES, trade.getAttributes());
    assertEquals(position.getUniqueId(), trade.getParentPositionId());

    // can't check the node ID as nodes are completely replaced
    ManageablePortfolioNode loadedRoot = _portfolioMaster.get(_savedPortfolio.getUniqueId()).getPortfolio().getRootNode();
    ManageablePortfolioNode loadedNode = loadedRoot.getChildNodes().get(0);
    assertEquals(1, loadedNode.getPositionIds().size());
    assertEquals(positionId.getObjectId(), loadedNode.getPositionIds().get(0));
  }

  @Test
  public void newSecurityWithOtcUnderlying() {
    // i.e. a swaption
  }

  @Test
  public void existingSecurityWithNoUnderlying() {
    UniqueId tradeId = _builder.addTrade(createTradeData(), BlotterTestUtils.FX_FORWARD_DATA_SOURCE, null, _nodeId);
    BeanDataSource updatedTradeData = createTradeData("uniqueId", tradeId.toString(),
                                                      "counterparty", "updatedCounterparty",
                                                      "tradeDate", "2012-12-22",
                                                      "premium", "4321");
    BeanDataSource updatedSecurityData = BlotterTestUtils.overrideBeanData(BlotterTestUtils.FX_FORWARD_DATA_SOURCE,
                                                                           "payCurrency", "AUD",
                                                                           "payAmount", "200",
                                                                           "regionId", "Reg~234");
    UniqueId updatedTradeId = _builder.updateTrade(updatedTradeData, updatedSecurityData, null);
    ManageableTrade updatedTrade = _positionMaster.getTrade(updatedTradeId);
    assertEquals("updatedCounterparty", updatedTrade.getCounterpartyExternalId().getValue());
    assertEquals(LocalDate.of(2012, 12, 22), updatedTrade.getTradeDate());
    assertEquals(4321d, updatedTrade.getPremium());
    PositionDocument positionDocument = _positionMaster.get(updatedTrade.getParentPositionId());
    ManageablePosition updatedPosition = positionDocument.getPosition();
    assertEquals(updatedTrade, updatedPosition.getTrade(updatedTradeId));
    VersionCorrection versionCorrection = VersionCorrection.of(positionDocument.getVersionFromInstant(),
                                                               positionDocument.getCorrectionFromInstant());
    FXForwardSecurity updatedSecurity = (FXForwardSecurity) _securityMaster.get(updatedTrade.getSecurityLink().getObjectId(),
                                                                                versionCorrection).getSecurity();
    assertEquals(Currency.AUD, updatedSecurity.getPayCurrency());
    assertEquals(200d, updatedSecurity.getPayAmount());
    assertEquals(ExternalId.of("Reg", "234"), updatedSecurity.getRegionId());
  }

  @Test
  public void existingSecurityWithFungibleUnderlying() {

  }

  @Test
  public void existingSecurityWithOtcUnderlying() {
    // i.e. a swaption

  }
}
