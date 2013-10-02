/**
 * Copyright (C) 2013 - present by OpenGamma Inc. and the OpenGamma group of companies
 * 
 * Please see distribution for license.
 */
package com.opengamma.integration.tool.config;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.fudgemsg.MutableFudgeMsg;
import org.fudgemsg.mapping.FudgeSerializer;
import org.fudgemsg.wire.FudgeMsgWriter;
import org.fudgemsg.wire.xml.FudgeXMLStreamWriter;
import org.joda.beans.Bean;
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

import com.opengamma.id.VersionCorrection;
import com.opengamma.master.config.ConfigDocument;
import com.opengamma.master.config.ConfigMaster;
import com.opengamma.master.config.ConfigMetaDataRequest;
import com.opengamma.master.config.ConfigMetaDataResult;
import com.opengamma.master.config.ConfigSearchRequest;
import com.opengamma.master.config.ConfigSearchResult;
import com.opengamma.util.ArgumentChecker;
import com.opengamma.util.fudgemsg.OpenGammaFudgeContext;

/**
 * Class that will save the entire current contents of a configuration store
 * to a set of XML encoded files on disk.
 * This differs from {@link ConfigSaver} primarily in that it saves all items
 * as individual files.
 */
@BeanDefinition
public class MultiFileConfigSaver extends DirectBean {
  @PropertyDefinition
  private File _destinationDirectory;
  @PropertyDefinition
  private ConfigMaster _configMaster;
  
  public void setDestinationDirectory(String directory) {
    setDestinationDirectory(new File(directory));
  }
  
  public void run() throws IOException {
    ArgumentChecker.notNullInjected(getDestinationDirectory(), "destinationDirectory");
    ArgumentChecker.notNullInjected(getConfigMaster(), "configMaster");
    
    if (!getDestinationDirectory().exists() || !getDestinationDirectory().isDirectory()) {
      throw new IllegalArgumentException("Destination directory " + getDestinationDirectory() + " must exist and be a directory.");
    }
    
    ConfigMetaDataRequest request = new ConfigMetaDataRequest();
    request.setConfigTypes(true);
    ConfigMetaDataResult result = getConfigMaster().metaData(request);
    
    for (Class<?> clazz : result.getConfigTypes()) {
      outputFilesForConfigClass(clazz);
    }
  }
  
  @SuppressWarnings({"rawtypes", "unchecked" })
  protected void outputFilesForConfigClass(Class<?> clazz) throws IOException {
    System.out.println("Outputting files for " + clazz);
    ConfigSearchRequest searchRequest = new ConfigSearchRequest(clazz);
    ConfigSearchResult searchResult = getConfigMaster().search(searchRequest);
    Set<ConfigDocument> latest = new HashSet<ConfigDocument>();
    for (Object documentObject : searchResult.getDocuments()) {
      ConfigDocument document = (ConfigDocument) documentObject;
      latest.add(getConfigMaster().get(document.getObjectId(), VersionCorrection.LATEST));
    }
    
    FudgeSerializer serializer = new FudgeSerializer(OpenGammaFudgeContext.getInstance());
    File directory = new File(getDestinationDirectory(), clazz.getName());
    directory.mkdir();
    for (ConfigDocument document : latest) {
      File documentFile = new File(directory, document.getName() + ".xml");
      System.out.println("-- Creating file " + documentFile.getAbsolutePath());
      FileOutputStream fos = new FileOutputStream(documentFile);
      FudgeXMLStreamWriter xmlStreamWriter = new FudgeXMLStreamWriter(OpenGammaFudgeContext.getInstance(), new OutputStreamWriter(fos));
      MutableFudgeMsg msg = serializer.objectToFudgeMsg(document.getConfig().getValue());
      FudgeMsgWriter fudgeMsgWriter = new FudgeMsgWriter(xmlStreamWriter);
      fudgeMsgWriter.writeMessage(msg);
      fudgeMsgWriter.close();
      fos.close();
    }
  }
  

  //------------------------- AUTOGENERATED START -------------------------
  ///CLOVER:OFF
  /**
   * The meta-bean for {@code MultiFileConfigSaver}.
   * @return the meta-bean, not null
   */
  public static MultiFileConfigSaver.Meta meta() {
    return MultiFileConfigSaver.Meta.INSTANCE;
  }

  static {
    JodaBeanUtils.registerMetaBean(MultiFileConfigSaver.Meta.INSTANCE);
  }

  @Override
  public MultiFileConfigSaver.Meta metaBean() {
    return MultiFileConfigSaver.Meta.INSTANCE;
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the destinationDirectory.
   * @return the value of the property
   */
  public File getDestinationDirectory() {
    return _destinationDirectory;
  }

  /**
   * Sets the destinationDirectory.
   * @param destinationDirectory  the new value of the property
   */
  public void setDestinationDirectory(File destinationDirectory) {
    this._destinationDirectory = destinationDirectory;
  }

  /**
   * Gets the the {@code destinationDirectory} property.
   * @return the property, not null
   */
  public final Property<File> destinationDirectory() {
    return metaBean().destinationDirectory().createProperty(this);
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the configMaster.
   * @return the value of the property
   */
  public ConfigMaster getConfigMaster() {
    return _configMaster;
  }

  /**
   * Sets the configMaster.
   * @param configMaster  the new value of the property
   */
  public void setConfigMaster(ConfigMaster configMaster) {
    this._configMaster = configMaster;
  }

  /**
   * Gets the the {@code configMaster} property.
   * @return the property, not null
   */
  public final Property<ConfigMaster> configMaster() {
    return metaBean().configMaster().createProperty(this);
  }

  //-----------------------------------------------------------------------
  @Override
  public MultiFileConfigSaver clone() {
    BeanBuilder<? extends MultiFileConfigSaver> builder = metaBean().builder();
    for (MetaProperty<?> mp : metaBean().metaPropertyIterable()) {
      if (mp.style().isBuildable()) {
        Object value = mp.get(this);
        if (value instanceof Bean) {
          value = ((Bean) value).clone();
        }
        builder.set(mp.name(), value);
      }
    }
    return builder.build();
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this) {
      return true;
    }
    if (obj != null && obj.getClass() == this.getClass()) {
      MultiFileConfigSaver other = (MultiFileConfigSaver) obj;
      return JodaBeanUtils.equal(getDestinationDirectory(), other.getDestinationDirectory()) &&
          JodaBeanUtils.equal(getConfigMaster(), other.getConfigMaster());
    }
    return false;
  }

  @Override
  public int hashCode() {
    int hash = getClass().hashCode();
    hash += hash * 31 + JodaBeanUtils.hashCode(getDestinationDirectory());
    hash += hash * 31 + JodaBeanUtils.hashCode(getConfigMaster());
    return hash;
  }

  @Override
  public String toString() {
    StringBuilder buf = new StringBuilder(96);
    buf.append("MultiFileConfigSaver{");
    int len = buf.length();
    toString(buf);
    if (buf.length() > len) {
      buf.setLength(buf.length() - 2);
    }
    buf.append('}');
    return buf.toString();
  }

  protected void toString(StringBuilder buf) {
    buf.append("destinationDirectory").append('=').append(getDestinationDirectory()).append(',').append(' ');
    buf.append("configMaster").append('=').append(getConfigMaster()).append(',').append(' ');
  }

  //-----------------------------------------------------------------------
  /**
   * The meta-bean for {@code MultiFileConfigSaver}.
   */
  public static class Meta extends DirectMetaBean {
    /**
     * The singleton instance of the meta-bean.
     */
    static final Meta INSTANCE = new Meta();

    /**
     * The meta-property for the {@code destinationDirectory} property.
     */
    private final MetaProperty<File> _destinationDirectory = DirectMetaProperty.ofReadWrite(
        this, "destinationDirectory", MultiFileConfigSaver.class, File.class);
    /**
     * The meta-property for the {@code configMaster} property.
     */
    private final MetaProperty<ConfigMaster> _configMaster = DirectMetaProperty.ofReadWrite(
        this, "configMaster", MultiFileConfigSaver.class, ConfigMaster.class);
    /**
     * The meta-properties.
     */
    private final Map<String, MetaProperty<?>> _metaPropertyMap$ = new DirectMetaPropertyMap(
        this, null,
        "destinationDirectory",
        "configMaster");

    /**
     * Restricted constructor.
     */
    protected Meta() {
    }

    @Override
    protected MetaProperty<?> metaPropertyGet(String propertyName) {
      switch (propertyName.hashCode()) {
        case 807184895:  // destinationDirectory
          return _destinationDirectory;
        case 10395716:  // configMaster
          return _configMaster;
      }
      return super.metaPropertyGet(propertyName);
    }

    @Override
    public BeanBuilder<? extends MultiFileConfigSaver> builder() {
      return new DirectBeanBuilder<MultiFileConfigSaver>(new MultiFileConfigSaver());
    }

    @Override
    public Class<? extends MultiFileConfigSaver> beanType() {
      return MultiFileConfigSaver.class;
    }

    @Override
    public Map<String, MetaProperty<?>> metaPropertyMap() {
      return _metaPropertyMap$;
    }

    //-----------------------------------------------------------------------
    /**
     * The meta-property for the {@code destinationDirectory} property.
     * @return the meta-property, not null
     */
    public final MetaProperty<File> destinationDirectory() {
      return _destinationDirectory;
    }

    /**
     * The meta-property for the {@code configMaster} property.
     * @return the meta-property, not null
     */
    public final MetaProperty<ConfigMaster> configMaster() {
      return _configMaster;
    }

    //-----------------------------------------------------------------------
    @Override
    protected Object propertyGet(Bean bean, String propertyName, boolean quiet) {
      switch (propertyName.hashCode()) {
        case 807184895:  // destinationDirectory
          return ((MultiFileConfigSaver) bean).getDestinationDirectory();
        case 10395716:  // configMaster
          return ((MultiFileConfigSaver) bean).getConfigMaster();
      }
      return super.propertyGet(bean, propertyName, quiet);
    }

    @Override
    protected void propertySet(Bean bean, String propertyName, Object newValue, boolean quiet) {
      switch (propertyName.hashCode()) {
        case 807184895:  // destinationDirectory
          ((MultiFileConfigSaver) bean).setDestinationDirectory((File) newValue);
          return;
        case 10395716:  // configMaster
          ((MultiFileConfigSaver) bean).setConfigMaster((ConfigMaster) newValue);
          return;
      }
      super.propertySet(bean, propertyName, newValue, quiet);
    }

  }

  ///CLOVER:ON
  //-------------------------- AUTOGENERATED END --------------------------
}
