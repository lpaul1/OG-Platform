/**
 * Copyright (C) 2011 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.financial.depgraph.rest;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import java.util.Set;

import org.fudgemsg.FudgeMsg;
import org.fudgemsg.FudgeMsgEnvelope;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;
import org.threeten.bp.Duration;
import org.threeten.bp.Instant;

import com.opengamma.OpenGammaRuntimeException;
import com.opengamma.core.value.MarketDataRequirementNames;
import com.opengamma.engine.ComputationTarget;
import com.opengamma.engine.DefaultComputationTargetResolver;
import com.opengamma.engine.InMemorySecuritySource;
import com.opengamma.engine.function.AbstractFunction;
import com.opengamma.engine.function.CachingFunctionRepositoryCompiler;
import com.opengamma.engine.function.CompiledFunctionService;
import com.opengamma.engine.function.FunctionCompilationContext;
import com.opengamma.engine.function.FunctionInvoker;
import com.opengamma.engine.function.InMemoryFunctionRepository;
import com.opengamma.engine.function.resolver.DefaultFunctionResolver;
import com.opengamma.engine.marketdata.MarketDataListener;
import com.opengamma.engine.marketdata.MarketDataPermissionProvider;
import com.opengamma.engine.marketdata.MarketDataProvider;
import com.opengamma.engine.marketdata.MarketDataSnapshot;
import com.opengamma.engine.marketdata.availability.DefaultMarketDataAvailabilityProvider;
import com.opengamma.engine.marketdata.availability.DomainMarketDataAvailabilityFilter;
import com.opengamma.engine.marketdata.availability.MarketDataAvailabilityProvider;
import com.opengamma.engine.marketdata.resolver.SingleMarketDataProviderResolver;
import com.opengamma.engine.marketdata.spec.MarketDataSpecification;
import com.opengamma.engine.target.ComputationTargetType;
import com.opengamma.engine.value.ValueProperties;
import com.opengamma.engine.value.ValuePropertyNames;
import com.opengamma.engine.value.ValueRequirement;
import com.opengamma.engine.value.ValueRequirementNames;
import com.opengamma.engine.value.ValueSpecification;
import com.opengamma.id.ExternalScheme;
import com.opengamma.id.VersionCorrection;
import com.opengamma.util.fudgemsg.OpenGammaFudgeContext;
import com.opengamma.util.test.TestGroup;

/**
 * Tests the diagnostic REST exposure of a dependency graph builder.
 */
@Test(groups = TestGroup.UNIT)
public class DependencyGraphBuilderResourceTest {

  private static final Logger s_logger = LoggerFactory.getLogger(DependencyGraphBuilderResourceTest.class);

  private CompiledFunctionService createFunctionCompilationService() {
    final InMemoryFunctionRepository functions = new InMemoryFunctionRepository();
    functions.addFunction(new AbstractFunction.NonCompiled() {

      @Override
      public ComputationTargetType getTargetType() {
        return ComputationTargetType.PRIMITIVE;
      }

      @Override
      public boolean canApplyTo(final FunctionCompilationContext context, final ComputationTarget target) {
        return true;
      }

      @Override
      public Set<ValueRequirement> getRequirements(final FunctionCompilationContext context, final ComputationTarget target, final ValueRequirement desiredValue) {
        throw new OpenGammaRuntimeException("test");
      }

      @Override
      public Set<ValueSpecification> getResults(final FunctionCompilationContext context, final ComputationTarget target) {
        return Collections.singleton(new ValueSpecification(ValueRequirementNames.FAIR_VALUE, target.toSpecification(), ValueProperties.with(
            ValuePropertyNames.FUNCTION, "Test").get()));
      }

      @Override
      public FunctionInvoker getFunctionInvoker() {
        fail();
        return null;
      }

    });
    final FunctionCompilationContext context = new FunctionCompilationContext();
    final InMemorySecuritySource securities = new InMemorySecuritySource();
    context.setSecuritySource(securities);
    context.setRawComputationTargetResolver(new DefaultComputationTargetResolver(securities));
    context.setComputationTargetResolver(context.getRawComputationTargetResolver().atVersionCorrection(VersionCorrection.LATEST));
    return new CompiledFunctionService(functions, new CachingFunctionRepositoryCompiler(), context);
  }

  private DependencyGraphBuilderResourceContextBean createContextBean() {
    final DependencyGraphBuilderResourceContextBean bean = new DependencyGraphBuilderResourceContextBean();
    final CompiledFunctionService cfs = createFunctionCompilationService();
    cfs.initialize();
    bean.setFunctionCompilationContext(cfs.getFunctionCompilationContext());
    bean.setFunctionResolver(new DefaultFunctionResolver(cfs));
    bean.setMarketDataProviderResolver(new SingleMarketDataProviderResolver(new MarketDataProvider() {

      @Override
      public void addListener(final MarketDataListener listener) {
        fail();
      }

      @Override
      public void removeListener(final MarketDataListener listener) {
        fail();
      }

      @Override
      public void subscribe(final ValueSpecification valueSpecification) {
        fail();
      }

      @Override
      public void subscribe(final Set<ValueSpecification> valueSpecifications) {
        fail();
      }

      @Override
      public void unsubscribe(final ValueSpecification valueSpecification) {
        fail();
      }

      @Override
      public void unsubscribe(final Set<ValueSpecification> valueSpecifications) {
        fail();
      }

      @Override
      public MarketDataAvailabilityProvider getAvailabilityProvider(final MarketDataSpecification marketDataSpec) {
        return new DomainMarketDataAvailabilityFilter(Arrays.asList(ExternalScheme.of("Foo")), Arrays.asList(MarketDataRequirementNames.MARKET_VALUE))
            .withProvider(new DefaultMarketDataAvailabilityProvider());
      }

      @Override
      public MarketDataPermissionProvider getPermissionProvider() {
        fail();
        return null;
      }

      @Override
      public boolean isCompatible(final MarketDataSpecification marketDataSpec) {
        fail();
        return false;
      }

      @Override
      public MarketDataSnapshot snapshot(final MarketDataSpecification marketDataSpec) {
        fail();
        return null;
      }

      @Override
      public Duration getRealTimeDuration(final Instant fromInstant, final Instant toInstant) {
        fail();
        return null;
      }

    }));
    return bean;
  }

  private DependencyGraphBuilderResource createResource() {
    return new DependencyGraphBuilderResource(createContextBean(), OpenGammaFudgeContext.getInstance());
  }

  public void testSetValuationTime() {
    final DependencyGraphBuilderResource resource = createResource();
    final Instant i1 = resource.getValuationTime();
    final DependencyGraphBuilderResource prime = resource.setValuationTime("2007-12-03T10:15:30+01:00[Europe/Paris]");
    final Instant i2 = prime.getValuationTime();
    assertEquals(i1, resource.getValuationTime()); // original unchanged
    assertFalse(Objects.equals(i1, i2));
  }

  // TODO: testSetResolutionTime method

  public void testSetCalculationConfigurationName() {
    final DependencyGraphBuilderResource resource = createResource();
    final String c1 = resource.getCalculationConfigurationName();
    final DependencyGraphBuilderResource prime = resource.setCalculationConfigurationName("Foo");
    final String c2 = prime.getCalculationConfigurationName();
    assertEquals(c1, resource.getCalculationConfigurationName()); // original unchanged
    assertFalse(c1.equals(c2));
  }

  public void testSetDefaultProperties() {
    final DependencyGraphBuilderResource resource = createResource();
    final ValueProperties p1 = resource.getDefaultProperties();
    final DependencyGraphBuilderResource prime = resource.setDefaultProperties("A=[foo,bar],B=*");
    final ValueProperties p2 = prime.getDefaultProperties();
    assertEquals(p1, resource.getDefaultProperties()); // original unchanged
    assertFalse(p1.equals(p2));
  }

  public void testAddValue() {
    final DependencyGraphBuilderResource resource = createResource();
    final Collection<ValueRequirement> r1 = resource.getRequirements();
    final DependencyGraphBuilderResource prime = resource.addValueRequirementByUniqueId("Foo", "PRIMITIVE", "Test~1");
    final Collection<ValueRequirement> r2 = prime.getRequirements();
    final DependencyGraphBuilderResource prime2 = prime.addValueRequirementByUniqueId("Bar", "PRIMITIVE", "Test~2");
    final Collection<ValueRequirement> r3 = prime2.getRequirements();
    assertEquals(r1, resource.getRequirements()); // original unchanged
    assertEquals(r2, prime.getRequirements()); // unchanged
    assertEquals(r1.size(), 0);
    assertEquals(r2.size(), 1);
    assertEquals(r3.size(), 2);
  }

  public void testBuild_ok() {
    final DependencyGraphBuilderResource resource = createResource();
    final FudgeMsgEnvelope env = resource.addValueRequirementByExternalId(MarketDataRequirementNames.MARKET_VALUE, "PRIMITIVE", "Foo~1")
        .addValueRequirementByExternalId(MarketDataRequirementNames.MARKET_VALUE, "PRIMITIVE", "Foo~2").build();
    final FudgeMsg msg = env.getMessage();
    s_logger.debug("testBuild_ok = {}", msg);
    assertTrue(msg.hasField("dependencyGraph"));
    assertFalse(msg.hasField("exception"));
    assertFalse(msg.hasField("failure"));
  }

  public void testBuild_exceptions() {
    final DependencyGraphBuilderResource resource = createResource();
    final FudgeMsgEnvelope env = resource.addValueRequirementByExternalId(MarketDataRequirementNames.MARKET_VALUE, "PRIMITIVE", "Foo~1")
        .addValueRequirementByUniqueId(ValueRequirementNames.FAIR_VALUE, "PRIMITIVE", "Foo~Bar").build();
    final FudgeMsg msg = env.getMessage();
    s_logger.debug("testBuild_exceptions = {}", msg);
    assertTrue(msg.hasField("dependencyGraph"));
    assertEquals(msg.getAllByName("exception").size(), 2); // one from the exception, and one from not resolving
    assertEquals(msg.getAllByName("failure").size(), 1);
  }

  public void testBuild_failures() {
    final DependencyGraphBuilderResource resource = createResource();
    final FudgeMsgEnvelope env = resource.addValueRequirementByExternalId(MarketDataRequirementNames.MARKET_VALUE, "PRIMITIVE", "Bar~1")
        .addValueRequirementByUniqueId(ValueRequirementNames.PRESENT_VALUE, "PRIMITIVE", "Bar~2").build();
    final FudgeMsg msg = env.getMessage();
    s_logger.debug("testBuild_failures = {}", msg);
    assertTrue(msg.hasField("dependencyGraph"));
    assertEquals(msg.getAllByName("exception").size(), 2);
    assertEquals(msg.getAllByName("failure").size(), 2);
  }

}