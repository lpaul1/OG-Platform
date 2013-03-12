/**
 * Copyright (C) 2011 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.financial.livedata.rest;

import java.util.Map;

import org.joda.beans.BeanBuilder;
import org.joda.beans.BeanDefinition;
import org.joda.beans.JodaBeanUtils;
import org.joda.beans.MetaProperty;
import org.joda.beans.Property;
import org.joda.beans.PropertyDefinition;
import org.joda.beans.impl.direct.DirectBean;
import org.joda.beans.impl.direct.DirectBeanBuilder;
import org.joda.beans.impl.direct.DirectMetaBean;
import org.joda.beans.impl.direct.DirectMetaProperty;
import org.joda.beans.impl.direct.DirectMetaPropertyMap;

import com.opengamma.engine.value.ValueRequirement;
import com.opengamma.engine.value.ValueSpecification;
import com.opengamma.util.PublicSPI;

/**
 *
 */
@PublicSPI
@BeanDefinition
public class AddValueRequest extends DirectBean {

  @PropertyDefinition
  private ValueRequirement _valueRequirement;

  @PropertyDefinition
  private ValueSpecification _valueSpecification;

  @PropertyDefinition
  private Object _value;

  //------------------------- AUTOGENERATED START -------------------------
  ///CLOVER:OFF
  /**
   * The meta-bean for {@code AddValueRequest}.
   * @return the meta-bean, not null
   */
  public static AddValueRequest.Meta meta() {
    return AddValueRequest.Meta.INSTANCE;
  }
  static {
    JodaBeanUtils.registerMetaBean(AddValueRequest.Meta.INSTANCE);
  }

  @Override
  public AddValueRequest.Meta metaBean() {
    return AddValueRequest.Meta.INSTANCE;
  }

  @Override
  protected Object propertyGet(final String propertyName, final boolean quiet) {
    switch (propertyName.hashCode()) {
      case -755281390:  // valueRequirement
        return getValueRequirement();
      case 7765778:  // valueSpecification
        return getValueSpecification();
      case 111972721:  // value
        return getValue();
    }
    return super.propertyGet(propertyName, quiet);
  }

  @Override
  protected void propertySet(final String propertyName, final Object newValue, final boolean quiet) {
    switch (propertyName.hashCode()) {
      case -755281390:  // valueRequirement
        setValueRequirement((ValueRequirement) newValue);
        return;
      case 7765778:  // valueSpecification
        setValueSpecification((ValueSpecification) newValue);
        return;
      case 111972721:  // value
        setValue(newValue);
        return;
    }
    super.propertySet(propertyName, newValue, quiet);
  }

  @Override
  public boolean equals(final Object obj) {
    if (obj == this) {
      return true;
    }
    if (obj != null && obj.getClass() == this.getClass()) {
      final AddValueRequest other = (AddValueRequest) obj;
      return JodaBeanUtils.equal(getValueRequirement(), other.getValueRequirement()) &&
          JodaBeanUtils.equal(getValueSpecification(), other.getValueSpecification()) &&
          JodaBeanUtils.equal(getValue(), other.getValue());
    }
    return false;
  }

  @Override
  public int hashCode() {
    int hash = getClass().hashCode();
    hash += hash * 31 + JodaBeanUtils.hashCode(getValueRequirement());
    hash += hash * 31 + JodaBeanUtils.hashCode(getValueSpecification());
    hash += hash * 31 + JodaBeanUtils.hashCode(getValue());
    return hash;
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the valueRequirement.
   * @return the value of the property
   */
  public ValueRequirement getValueRequirement() {
    return _valueRequirement;
  }

  /**
   * Sets the valueRequirement.
   * @param valueRequirement  the new value of the property
   */
  public void setValueRequirement(final ValueRequirement valueRequirement) {
    this._valueRequirement = valueRequirement;
  }

  /**
   * Gets the the {@code valueRequirement} property.
   * @return the property, not null
   */
  public final Property<ValueRequirement> valueRequirement() {
    return metaBean().valueRequirement().createProperty(this);
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the valueSpecification.
   * @return the value of the property
   */
  public ValueSpecification getValueSpecification() {
    return _valueSpecification;
  }

  /**
   * Sets the valueSpecification.
   * @param valueSpecification  the new value of the property
   */
  public void setValueSpecification(final ValueSpecification valueSpecification) {
    this._valueSpecification = valueSpecification;
  }

  /**
   * Gets the the {@code valueSpecification} property.
   * @return the property, not null
   */
  public final Property<ValueSpecification> valueSpecification() {
    return metaBean().valueSpecification().createProperty(this);
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the value.
   * @return the value of the property
   */
  public Object getValue() {
    return _value;
  }

  /**
   * Sets the value.
   * @param value  the new value of the property
   */
  public void setValue(final Object value) {
    this._value = value;
  }

  /**
   * Gets the the {@code value} property.
   * @return the property, not null
   */
  public final Property<Object> value() {
    return metaBean().value().createProperty(this);
  }

  //-----------------------------------------------------------------------
  /**
   * The meta-bean for {@code AddValueRequest}.
   */
  public static class Meta extends DirectMetaBean {
    /**
     * The singleton instance of the meta-bean.
     */
    static final Meta INSTANCE = new Meta();

    /**
     * The meta-property for the {@code valueRequirement} property.
     */
    private final MetaProperty<ValueRequirement> _valueRequirement = DirectMetaProperty.ofReadWrite(
        this, "valueRequirement", AddValueRequest.class, ValueRequirement.class);
    /**
     * The meta-property for the {@code valueSpecification} property.
     */
    private final MetaProperty<ValueSpecification> _valueSpecification = DirectMetaProperty.ofReadWrite(
        this, "valueSpecification", AddValueRequest.class, ValueSpecification.class);
    /**
     * The meta-property for the {@code value} property.
     */
    private final MetaProperty<Object> _value = DirectMetaProperty.ofReadWrite(
        this, "value", AddValueRequest.class, Object.class);
    /**
     * The meta-properties.
     */
    private final Map<String, MetaProperty<?>> _metaPropertyMap$ = new DirectMetaPropertyMap(
        this, null,
        "valueRequirement",
        "valueSpecification",
        "value");

    /**
     * Restricted constructor.
     */
    protected Meta() {
    }

    @Override
    protected MetaProperty<?> metaPropertyGet(final String propertyName) {
      switch (propertyName.hashCode()) {
        case -755281390:  // valueRequirement
          return _valueRequirement;
        case 7765778:  // valueSpecification
          return _valueSpecification;
        case 111972721:  // value
          return _value;
      }
      return super.metaPropertyGet(propertyName);
    }

    @Override
    public BeanBuilder<? extends AddValueRequest> builder() {
      return new DirectBeanBuilder<AddValueRequest>(new AddValueRequest());
    }

    @Override
    public Class<? extends AddValueRequest> beanType() {
      return AddValueRequest.class;
    }

    @Override
    public Map<String, MetaProperty<?>> metaPropertyMap() {
      return _metaPropertyMap$;
    }

    //-----------------------------------------------------------------------
    /**
     * The meta-property for the {@code valueRequirement} property.
     * @return the meta-property, not null
     */
    public final MetaProperty<ValueRequirement> valueRequirement() {
      return _valueRequirement;
    }

    /**
     * The meta-property for the {@code valueSpecification} property.
     * @return the meta-property, not null
     */
    public final MetaProperty<ValueSpecification> valueSpecification() {
      return _valueSpecification;
    }

    /**
     * The meta-property for the {@code value} property.
     * @return the meta-property, not null
     */
    public final MetaProperty<Object> value() {
      return _value;
    }

  }

  ///CLOVER:ON
  //-------------------------- AUTOGENERATED END --------------------------
}
