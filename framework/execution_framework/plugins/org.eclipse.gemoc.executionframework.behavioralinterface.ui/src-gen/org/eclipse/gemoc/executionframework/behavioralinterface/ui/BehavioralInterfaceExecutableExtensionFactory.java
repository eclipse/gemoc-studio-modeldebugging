/*
 * generated by Xtext 2.24.0
 */
package org.eclipse.gemoc.executionframework.behavioralinterface.ui;

import com.google.inject.Injector;
import org.eclipse.gemoc.executionframework.behavioralinterface.ui.internal.BehavioralinterfaceActivator;
import org.eclipse.xtext.ui.guice.AbstractGuiceAwareExecutableExtensionFactory;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

/**
 * This class was generated. Customizations should only happen in a newly
 * introduced subclass. 
 */
public class BehavioralInterfaceExecutableExtensionFactory extends AbstractGuiceAwareExecutableExtensionFactory {

	@Override
	protected Bundle getBundle() {
		return FrameworkUtil.getBundle(BehavioralinterfaceActivator.class);
	}
	
	@Override
	protected Injector getInjector() {
		BehavioralinterfaceActivator activator = BehavioralinterfaceActivator.getInstance();
		return activator != null ? activator.getInjector(BehavioralinterfaceActivator.ORG_ECLIPSE_GEMOC_EXECUTIONFRAMEWORK_BEHAVIORALINTERFACE_BEHAVIORALINTERFACE) : null;
	}

}
