/**
 * Copyright (C) 2009 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.component.factory.master;

import java.util.LinkedHashMap;
import java.util.Map;

import org.joda.beans.Bean;
import org.joda.beans.BeanBuilder;
import org.joda.beans.BeanDefinition;
import org.joda.beans.JodaBeanUtils;
import org.joda.beans.MetaProperty;
import org.joda.beans.Property;
import org.joda.beans.PropertyDefinition;
import org.joda.beans.impl.direct.DirectBeanBuilder;
import org.joda.beans.impl.direct.DirectMetaProperty;
import org.joda.beans.impl.direct.DirectMetaPropertyMap;

import com.opengamma.component.ComponentInfo;
import com.opengamma.component.ComponentRepository;
import com.opengamma.component.factory.ComponentInfoAttributes;
import com.opengamma.core.change.JmsChangeManager;
import com.opengamma.master.orgs.OrganizationMaster;
import com.opengamma.master.orgs.impl.DataOrganizationMasterResource;
import com.opengamma.master.orgs.impl.RemoteOrganizationMaster;
import com.opengamma.masterdb.orgs.DbOrganizationMaster;
import com.opengamma.util.jms.JmsConnector;

/**
 * Component factory for the organization master.
 */
@BeanDefinition
public class DbOrganizationMasterComponentFactory extends AbstractDbMasterComponentFactory {

  /**
   * The classifier that the factory should publish under.
   */
  @PropertyDefinition(validate = "notNull")
  private String _classifier;
  /**
   * The flag determining whether the component should be published by REST (default true).
   */
  @PropertyDefinition
  private boolean _publishRest = true;
  /**
   * The JMS connector.
   */
  @PropertyDefinition
  private JmsConnector _jmsConnector;
  /**
   * The JMS change manager topic.
   */
  @PropertyDefinition
  private String _jmsChangeManagerTopic;
  /**
   * The scheme used by the {@code UniqueId}.
   */
  @PropertyDefinition
  private String _uniqueIdScheme;
  /**
   * The maximum number of retries when updating.
   */
  @PropertyDefinition
  private Integer _maxRetries;

  //-------------------------------------------------------------------------
  @Override
  public void init(ComponentRepository repo, LinkedHashMap<String, String> configuration) {
    ComponentInfo info = new ComponentInfo(OrganizationMaster.class, getClassifier());

    // create
    DbOrganizationMaster master = new DbOrganizationMaster(getDbConnector());
    if (getUniqueIdScheme() != null) {
      master.setUniqueIdScheme(getUniqueIdScheme());
    }
    if (getMaxRetries() != null) {
      master.setMaxRetries(getMaxRetries());
    }
    if (getJmsChangeManagerTopic() != null) {
      JmsChangeManager cm = new JmsChangeManager(getJmsConnector(), getJmsChangeManagerTopic());
      master.setChangeManager(cm);
      repo.registerLifecycle(cm);
      if (getJmsConnector().getClientBrokerUri() != null) {
        info.addAttribute(ComponentInfoAttributes.JMS_BROKER_URI, getJmsConnector().getClientBrokerUri().toString());
      }
      info.addAttribute(ComponentInfoAttributes.JMS_CHANGE_MANAGER_TOPIC, getJmsChangeManagerTopic());
    }
    checkSchema(master.getSchemaVersion(), "org");

    // register
    info.addAttribute(ComponentInfoAttributes.LEVEL, 1);
    info.addAttribute(ComponentInfoAttributes.REMOTE_CLIENT_JAVA, RemoteOrganizationMaster.class);
    info.addAttribute(ComponentInfoAttributes.UNIQUE_ID_SCHEME, master.getUniqueIdScheme());
    repo.registerComponent(info, master);

    // publish
    if (isPublishRest()) {
      repo.getRestComponents().publish(info, new DataOrganizationMasterResource(master));
    }
  }

  //------------------------- AUTOGENERATED START -------------------------
  ///CLOVER:OFF
  /**
   * The meta-bean for {@code DbOrganizationMasterComponentFactory}.
   * @return the meta-bean, not null
   */
  public static DbOrganizationMasterComponentFactory.Meta meta() {
    return DbOrganizationMasterComponentFactory.Meta.INSTANCE;
  }

  static {
    JodaBeanUtils.registerMetaBean(DbOrganizationMasterComponentFactory.Meta.INSTANCE);
  }

  @Override
  public DbOrganizationMasterComponentFactory.Meta metaBean() {
    return DbOrganizationMasterComponentFactory.Meta.INSTANCE;
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the classifier that the factory should publish under.
   * @return the value of the property, not null
   */
  public String getClassifier() {
    return _classifier;
  }

  /**
   * Sets the classifier that the factory should publish under.
   * @param classifier  the new value of the property, not null
   */
  public void setClassifier(String classifier) {
    JodaBeanUtils.notNull(classifier, "classifier");
    this._classifier = classifier;
  }

  /**
   * Gets the the {@code classifier} property.
   * @return the property, not null
   */
  public final Property<String> classifier() {
    return metaBean().classifier().createProperty(this);
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the flag determining whether the component should be published by REST (default true).
   * @return the value of the property
   */
  public boolean isPublishRest() {
    return _publishRest;
  }

  /**
   * Sets the flag determining whether the component should be published by REST (default true).
   * @param publishRest  the new value of the property
   */
  public void setPublishRest(boolean publishRest) {
    this._publishRest = publishRest;
  }

  /**
   * Gets the the {@code publishRest} property.
   * @return the property, not null
   */
  public final Property<Boolean> publishRest() {
    return metaBean().publishRest().createProperty(this);
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the JMS connector.
   * @return the value of the property
   */
  public JmsConnector getJmsConnector() {
    return _jmsConnector;
  }

  /**
   * Sets the JMS connector.
   * @param jmsConnector  the new value of the property
   */
  public void setJmsConnector(JmsConnector jmsConnector) {
    this._jmsConnector = jmsConnector;
  }

  /**
   * Gets the the {@code jmsConnector} property.
   * @return the property, not null
   */
  public final Property<JmsConnector> jmsConnector() {
    return metaBean().jmsConnector().createProperty(this);
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the JMS change manager topic.
   * @return the value of the property
   */
  public String getJmsChangeManagerTopic() {
    return _jmsChangeManagerTopic;
  }

  /**
   * Sets the JMS change manager topic.
   * @param jmsChangeManagerTopic  the new value of the property
   */
  public void setJmsChangeManagerTopic(String jmsChangeManagerTopic) {
    this._jmsChangeManagerTopic = jmsChangeManagerTopic;
  }

  /**
   * Gets the the {@code jmsChangeManagerTopic} property.
   * @return the property, not null
   */
  public final Property<String> jmsChangeManagerTopic() {
    return metaBean().jmsChangeManagerTopic().createProperty(this);
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the scheme used by the {@code UniqueId}.
   * @return the value of the property
   */
  public String getUniqueIdScheme() {
    return _uniqueIdScheme;
  }

  /**
   * Sets the scheme used by the {@code UniqueId}.
   * @param uniqueIdScheme  the new value of the property
   */
  public void setUniqueIdScheme(String uniqueIdScheme) {
    this._uniqueIdScheme = uniqueIdScheme;
  }

  /**
   * Gets the the {@code uniqueIdScheme} property.
   * @return the property, not null
   */
  public final Property<String> uniqueIdScheme() {
    return metaBean().uniqueIdScheme().createProperty(this);
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the maximum number of retries when updating.
   * @return the value of the property
   */
  public Integer getMaxRetries() {
    return _maxRetries;
  }

  /**
   * Sets the maximum number of retries when updating.
   * @param maxRetries  the new value of the property
   */
  public void setMaxRetries(Integer maxRetries) {
    this._maxRetries = maxRetries;
  }

  /**
   * Gets the the {@code maxRetries} property.
   * @return the property, not null
   */
  public final Property<Integer> maxRetries() {
    return metaBean().maxRetries().createProperty(this);
  }

  //-----------------------------------------------------------------------
  @Override
  public DbOrganizationMasterComponentFactory clone() {
    return (DbOrganizationMasterComponentFactory) super.clone();
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this) {
      return true;
    }
    if (obj != null && obj.getClass() == this.getClass()) {
      DbOrganizationMasterComponentFactory other = (DbOrganizationMasterComponentFactory) obj;
      return JodaBeanUtils.equal(getClassifier(), other.getClassifier()) &&
          (isPublishRest() == other.isPublishRest()) &&
          JodaBeanUtils.equal(getJmsConnector(), other.getJmsConnector()) &&
          JodaBeanUtils.equal(getJmsChangeManagerTopic(), other.getJmsChangeManagerTopic()) &&
          JodaBeanUtils.equal(getUniqueIdScheme(), other.getUniqueIdScheme()) &&
          JodaBeanUtils.equal(getMaxRetries(), other.getMaxRetries()) &&
          super.equals(obj);
    }
    return false;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash += hash * 31 + JodaBeanUtils.hashCode(getClassifier());
    hash += hash * 31 + JodaBeanUtils.hashCode(isPublishRest());
    hash += hash * 31 + JodaBeanUtils.hashCode(getJmsConnector());
    hash += hash * 31 + JodaBeanUtils.hashCode(getJmsChangeManagerTopic());
    hash += hash * 31 + JodaBeanUtils.hashCode(getUniqueIdScheme());
    hash += hash * 31 + JodaBeanUtils.hashCode(getMaxRetries());
    return hash ^ super.hashCode();
  }

  @Override
  public String toString() {
    StringBuilder buf = new StringBuilder(224);
    buf.append("DbOrganizationMasterComponentFactory{");
    int len = buf.length();
    toString(buf);
    if (buf.length() > len) {
      buf.setLength(buf.length() - 2);
    }
    buf.append('}');
    return buf.toString();
  }

  @Override
  protected void toString(StringBuilder buf) {
    super.toString(buf);
    buf.append("classifier").append('=').append(getClassifier()).append(',').append(' ');
    buf.append("publishRest").append('=').append(isPublishRest()).append(',').append(' ');
    buf.append("jmsConnector").append('=').append(getJmsConnector()).append(',').append(' ');
    buf.append("jmsChangeManagerTopic").append('=').append(getJmsChangeManagerTopic()).append(',').append(' ');
    buf.append("uniqueIdScheme").append('=').append(getUniqueIdScheme()).append(',').append(' ');
    buf.append("maxRetries").append('=').append(getMaxRetries()).append(',').append(' ');
  }

  //-----------------------------------------------------------------------
  /**
   * The meta-bean for {@code DbOrganizationMasterComponentFactory}.
   */
  public static class Meta extends AbstractDbMasterComponentFactory.Meta {
    /**
     * The singleton instance of the meta-bean.
     */
    static final Meta INSTANCE = new Meta();

    /**
     * The meta-property for the {@code classifier} property.
     */
    private final MetaProperty<String> _classifier = DirectMetaProperty.ofReadWrite(
        this, "classifier", DbOrganizationMasterComponentFactory.class, String.class);
    /**
     * The meta-property for the {@code publishRest} property.
     */
    private final MetaProperty<Boolean> _publishRest = DirectMetaProperty.ofReadWrite(
        this, "publishRest", DbOrganizationMasterComponentFactory.class, Boolean.TYPE);
    /**
     * The meta-property for the {@code jmsConnector} property.
     */
    private final MetaProperty<JmsConnector> _jmsConnector = DirectMetaProperty.ofReadWrite(
        this, "jmsConnector", DbOrganizationMasterComponentFactory.class, JmsConnector.class);
    /**
     * The meta-property for the {@code jmsChangeManagerTopic} property.
     */
    private final MetaProperty<String> _jmsChangeManagerTopic = DirectMetaProperty.ofReadWrite(
        this, "jmsChangeManagerTopic", DbOrganizationMasterComponentFactory.class, String.class);
    /**
     * The meta-property for the {@code uniqueIdScheme} property.
     */
    private final MetaProperty<String> _uniqueIdScheme = DirectMetaProperty.ofReadWrite(
        this, "uniqueIdScheme", DbOrganizationMasterComponentFactory.class, String.class);
    /**
     * The meta-property for the {@code maxRetries} property.
     */
    private final MetaProperty<Integer> _maxRetries = DirectMetaProperty.ofReadWrite(
        this, "maxRetries", DbOrganizationMasterComponentFactory.class, Integer.class);
    /**
     * The meta-properties.
     */
    private final Map<String, MetaProperty<?>> _metaPropertyMap$ = new DirectMetaPropertyMap(
        this, (DirectMetaPropertyMap) super.metaPropertyMap(),
        "classifier",
        "publishRest",
        "jmsConnector",
        "jmsChangeManagerTopic",
        "uniqueIdScheme",
        "maxRetries");

    /**
     * Restricted constructor.
     */
    protected Meta() {
    }

    @Override
    protected MetaProperty<?> metaPropertyGet(String propertyName) {
      switch (propertyName.hashCode()) {
        case -281470431:  // classifier
          return _classifier;
        case -614707837:  // publishRest
          return _publishRest;
        case -1495762275:  // jmsConnector
          return _jmsConnector;
        case -758086398:  // jmsChangeManagerTopic
          return _jmsChangeManagerTopic;
        case -1737146991:  // uniqueIdScheme
          return _uniqueIdScheme;
        case -2022653118:  // maxRetries
          return _maxRetries;
      }
      return super.metaPropertyGet(propertyName);
    }

    @Override
    public BeanBuilder<? extends DbOrganizationMasterComponentFactory> builder() {
      return new DirectBeanBuilder<DbOrganizationMasterComponentFactory>(new DbOrganizationMasterComponentFactory());
    }

    @Override
    public Class<? extends DbOrganizationMasterComponentFactory> beanType() {
      return DbOrganizationMasterComponentFactory.class;
    }

    @Override
    public Map<String, MetaProperty<?>> metaPropertyMap() {
      return _metaPropertyMap$;
    }

    //-----------------------------------------------------------------------
    /**
     * The meta-property for the {@code classifier} property.
     * @return the meta-property, not null
     */
    public final MetaProperty<String> classifier() {
      return _classifier;
    }

    /**
     * The meta-property for the {@code publishRest} property.
     * @return the meta-property, not null
     */
    public final MetaProperty<Boolean> publishRest() {
      return _publishRest;
    }

    /**
     * The meta-property for the {@code jmsConnector} property.
     * @return the meta-property, not null
     */
    public final MetaProperty<JmsConnector> jmsConnector() {
      return _jmsConnector;
    }

    /**
     * The meta-property for the {@code jmsChangeManagerTopic} property.
     * @return the meta-property, not null
     */
    public final MetaProperty<String> jmsChangeManagerTopic() {
      return _jmsChangeManagerTopic;
    }

    /**
     * The meta-property for the {@code uniqueIdScheme} property.
     * @return the meta-property, not null
     */
    public final MetaProperty<String> uniqueIdScheme() {
      return _uniqueIdScheme;
    }

    /**
     * The meta-property for the {@code maxRetries} property.
     * @return the meta-property, not null
     */
    public final MetaProperty<Integer> maxRetries() {
      return _maxRetries;
    }

    //-----------------------------------------------------------------------
    @Override
    protected Object propertyGet(Bean bean, String propertyName, boolean quiet) {
      switch (propertyName.hashCode()) {
        case -281470431:  // classifier
          return ((DbOrganizationMasterComponentFactory) bean).getClassifier();
        case -614707837:  // publishRest
          return ((DbOrganizationMasterComponentFactory) bean).isPublishRest();
        case -1495762275:  // jmsConnector
          return ((DbOrganizationMasterComponentFactory) bean).getJmsConnector();
        case -758086398:  // jmsChangeManagerTopic
          return ((DbOrganizationMasterComponentFactory) bean).getJmsChangeManagerTopic();
        case -1737146991:  // uniqueIdScheme
          return ((DbOrganizationMasterComponentFactory) bean).getUniqueIdScheme();
        case -2022653118:  // maxRetries
          return ((DbOrganizationMasterComponentFactory) bean).getMaxRetries();
      }
      return super.propertyGet(bean, propertyName, quiet);
    }

    @Override
    protected void propertySet(Bean bean, String propertyName, Object newValue, boolean quiet) {
      switch (propertyName.hashCode()) {
        case -281470431:  // classifier
          ((DbOrganizationMasterComponentFactory) bean).setClassifier((String) newValue);
          return;
        case -614707837:  // publishRest
          ((DbOrganizationMasterComponentFactory) bean).setPublishRest((Boolean) newValue);
          return;
        case -1495762275:  // jmsConnector
          ((DbOrganizationMasterComponentFactory) bean).setJmsConnector((JmsConnector) newValue);
          return;
        case -758086398:  // jmsChangeManagerTopic
          ((DbOrganizationMasterComponentFactory) bean).setJmsChangeManagerTopic((String) newValue);
          return;
        case -1737146991:  // uniqueIdScheme
          ((DbOrganizationMasterComponentFactory) bean).setUniqueIdScheme((String) newValue);
          return;
        case -2022653118:  // maxRetries
          ((DbOrganizationMasterComponentFactory) bean).setMaxRetries((Integer) newValue);
          return;
      }
      super.propertySet(bean, propertyName, newValue, quiet);
    }

    @Override
    protected void validate(Bean bean) {
      JodaBeanUtils.notNull(((DbOrganizationMasterComponentFactory) bean)._classifier, "classifier");
      super.validate(bean);
    }

  }

  ///CLOVER:ON
  //-------------------------- AUTOGENERATED END --------------------------
}
