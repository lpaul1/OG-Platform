/**
 * Copyright (C) 2013 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.financial.analytics.ircurve.strips;

import java.util.Map;

import org.joda.beans.BeanBuilder;
import org.joda.beans.BeanDefinition;
import org.joda.beans.JodaBeanUtils;
import org.joda.beans.MetaProperty;
import org.joda.beans.Property;
import org.joda.beans.PropertyDefinition;
import org.joda.beans.impl.direct.DirectBeanBuilder;
import org.joda.beans.impl.direct.DirectMetaProperty;
import org.joda.beans.impl.direct.DirectMetaPropertyMap;

import com.opengamma.financial.security.cash.CashSecurity;
import com.opengamma.id.ExternalId;
import com.opengamma.util.ArgumentChecker;
import com.opengamma.util.time.Tenor;

/**
 * Cash node for use in curves containing sufficient information to construct a {@link CashSecurity}
 */
@BeanDefinition
public class CashNode extends CurveNode {

  /** Serialization version */
  private static final long serialVersionUID = 1L;

  /**
   * The start tenor.
   */
  @PropertyDefinition(validate = "notNull")
  private Tenor _startTenor;

  /**
   * The maturity tenor.
   */
  @PropertyDefinition(validate = "notNull")
  private Tenor _maturityTenor;

  /**
   * The convention.
   */
  @PropertyDefinition(validate = "notNull")
  private ExternalId _convention;

  /* package */ CashNode() {
    super();
  }

  /**
   * @param startTenor The start tenor, not null
   * @param maturityTenor The maturity tenor, not null
   * @param convention The convention, not null
   * @param curveNodeIdMapperName The name of the curve specification, not null
   */
  public CashNode(final Tenor startTenor, final Tenor maturityTenor, final ExternalId convention, final String curveNodeIdMapperName) {
    super(curveNodeIdMapperName);
    setStartTenor(startTenor);
    setMaturityTenor(maturityTenor);
    setConvention(convention);
  }

  @Override
  public Tenor getResolvedMaturity() {
    return _maturityTenor;
  }

  @Override
  public <T> T accept(final CurveNodeVisitor<T> visitor) {
    ArgumentChecker.notNull(visitor, "visitor");
    return visitor.visitCashNode(this);
  }

  //------------------------- AUTOGENERATED START -------------------------
  ///CLOVER:OFF
  /**
   * The meta-bean for {@code CashNode}.
   * @return the meta-bean, not null
   */
  public static CashNode.Meta meta() {
    return CashNode.Meta.INSTANCE;
  }
  static {
    JodaBeanUtils.registerMetaBean(CashNode.Meta.INSTANCE);
  }

  @Override
  public CashNode.Meta metaBean() {
    return CashNode.Meta.INSTANCE;
  }

  @Override
  protected Object propertyGet(String propertyName, boolean quiet) {
    switch (propertyName.hashCode()) {
      case -1583746178:  // startTenor
        return getStartTenor();
      case 45907375:  // maturityTenor
        return getMaturityTenor();
      case 2039569265:  // convention
        return getConvention();
    }
    return super.propertyGet(propertyName, quiet);
  }

  @Override
  protected void propertySet(String propertyName, Object newValue, boolean quiet) {
    switch (propertyName.hashCode()) {
      case -1583746178:  // startTenor
        setStartTenor((Tenor) newValue);
        return;
      case 45907375:  // maturityTenor
        setMaturityTenor((Tenor) newValue);
        return;
      case 2039569265:  // convention
        setConvention((ExternalId) newValue);
        return;
    }
    super.propertySet(propertyName, newValue, quiet);
  }

  @Override
  protected void validate() {
    JodaBeanUtils.notNull(_startTenor, "startTenor");
    JodaBeanUtils.notNull(_maturityTenor, "maturityTenor");
    JodaBeanUtils.notNull(_convention, "convention");
    super.validate();
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this) {
      return true;
    }
    if (obj != null && obj.getClass() == this.getClass()) {
      CashNode other = (CashNode) obj;
      return JodaBeanUtils.equal(getStartTenor(), other.getStartTenor()) &&
          JodaBeanUtils.equal(getMaturityTenor(), other.getMaturityTenor()) &&
          JodaBeanUtils.equal(getConvention(), other.getConvention()) &&
          super.equals(obj);
    }
    return false;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash += hash * 31 + JodaBeanUtils.hashCode(getStartTenor());
    hash += hash * 31 + JodaBeanUtils.hashCode(getMaturityTenor());
    hash += hash * 31 + JodaBeanUtils.hashCode(getConvention());
    return hash ^ super.hashCode();
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the start tenor.
   * @return the value of the property, not null
   */
  public Tenor getStartTenor() {
    return _startTenor;
  }

  /**
   * Sets the start tenor.
   * @param startTenor  the new value of the property, not null
   */
  public void setStartTenor(Tenor startTenor) {
    JodaBeanUtils.notNull(startTenor, "startTenor");
    this._startTenor = startTenor;
  }

  /**
   * Gets the the {@code startTenor} property.
   * @return the property, not null
   */
  public final Property<Tenor> startTenor() {
    return metaBean().startTenor().createProperty(this);
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the maturity tenor.
   * @return the value of the property, not null
   */
  public Tenor getMaturityTenor() {
    return _maturityTenor;
  }

  /**
   * Sets the maturity tenor.
   * @param maturityTenor  the new value of the property, not null
   */
  public void setMaturityTenor(Tenor maturityTenor) {
    JodaBeanUtils.notNull(maturityTenor, "maturityTenor");
    this._maturityTenor = maturityTenor;
  }

  /**
   * Gets the the {@code maturityTenor} property.
   * @return the property, not null
   */
  public final Property<Tenor> maturityTenor() {
    return metaBean().maturityTenor().createProperty(this);
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the convention.
   * @return the value of the property, not null
   */
  public ExternalId getConvention() {
    return _convention;
  }

  /**
   * Sets the convention.
   * @param convention  the new value of the property, not null
   */
  public void setConvention(ExternalId convention) {
    JodaBeanUtils.notNull(convention, "convention");
    this._convention = convention;
  }

  /**
   * Gets the the {@code convention} property.
   * @return the property, not null
   */
  public final Property<ExternalId> convention() {
    return metaBean().convention().createProperty(this);
  }

  //-----------------------------------------------------------------------
  /**
   * The meta-bean for {@code CashNode}.
   */
  public static class Meta extends CurveNode.Meta {
    /**
     * The singleton instance of the meta-bean.
     */
    static final Meta INSTANCE = new Meta();

    /**
     * The meta-property for the {@code startTenor} property.
     */
    private final MetaProperty<Tenor> _startTenor = DirectMetaProperty.ofReadWrite(
        this, "startTenor", CashNode.class, Tenor.class);
    /**
     * The meta-property for the {@code maturityTenor} property.
     */
    private final MetaProperty<Tenor> _maturityTenor = DirectMetaProperty.ofReadWrite(
        this, "maturityTenor", CashNode.class, Tenor.class);
    /**
     * The meta-property for the {@code convention} property.
     */
    private final MetaProperty<ExternalId> _convention = DirectMetaProperty.ofReadWrite(
        this, "convention", CashNode.class, ExternalId.class);
    /**
     * The meta-properties.
     */
    private final Map<String, MetaProperty<?>> _metaPropertyMap$ = new DirectMetaPropertyMap(
        this, (DirectMetaPropertyMap) super.metaPropertyMap(),
        "startTenor",
        "maturityTenor",
        "convention");

    /**
     * Restricted constructor.
     */
    protected Meta() {
    }

    @Override
    protected MetaProperty<?> metaPropertyGet(String propertyName) {
      switch (propertyName.hashCode()) {
        case -1583746178:  // startTenor
          return _startTenor;
        case 45907375:  // maturityTenor
          return _maturityTenor;
        case 2039569265:  // convention
          return _convention;
      }
      return super.metaPropertyGet(propertyName);
    }

    @Override
    public BeanBuilder<? extends CashNode> builder() {
      return new DirectBeanBuilder<CashNode>(new CashNode());
    }

    @Override
    public Class<? extends CashNode> beanType() {
      return CashNode.class;
    }

    @Override
    public Map<String, MetaProperty<?>> metaPropertyMap() {
      return _metaPropertyMap$;
    }

    //-----------------------------------------------------------------------
    /**
     * The meta-property for the {@code startTenor} property.
     * @return the meta-property, not null
     */
    public final MetaProperty<Tenor> startTenor() {
      return _startTenor;
    }

    /**
     * The meta-property for the {@code maturityTenor} property.
     * @return the meta-property, not null
     */
    public final MetaProperty<Tenor> maturityTenor() {
      return _maturityTenor;
    }

    /**
     * The meta-property for the {@code convention} property.
     * @return the meta-property, not null
     */
    public final MetaProperty<ExternalId> convention() {
      return _convention;
    }

  }

  ///CLOVER:ON
  //-------------------------- AUTOGENERATED END --------------------------

}
