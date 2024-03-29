/**
 */
package org.eclipse.gemoc.trace.commons.model.launchconfiguration.provider;

import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.Notifier;

import org.eclipse.emf.edit.provider.ChangeNotifier;
import org.eclipse.emf.edit.provider.ComposeableAdapterFactory;
import org.eclipse.emf.edit.provider.ComposedAdapterFactory;
import org.eclipse.emf.edit.provider.IChangeNotifier;
import org.eclipse.emf.edit.provider.IDisposable;
import org.eclipse.emf.edit.provider.IEditingDomainItemProvider;
import org.eclipse.emf.edit.provider.IItemLabelProvider;
import org.eclipse.emf.edit.provider.IItemPropertySource;
import org.eclipse.emf.edit.provider.INotifyChangedListener;
import org.eclipse.emf.edit.provider.IStructuredItemContentProvider;
import org.eclipse.emf.edit.provider.ITreeItemContentProvider;

import org.eclipse.gemoc.trace.commons.model.launchconfiguration.util.LaunchconfigurationAdapterFactory;

/**
 * This is the factory that is used to provide the interfaces needed to support Viewers.
 * The adapters generated by this factory convert EMF adapter notifications into calls to {@link #fireNotifyChanged fireNotifyChanged}.
 * The adapters also support Eclipse property sheets.
 * Note that most of the adapters are shared among multiple instances.
 * <!-- begin-user-doc -->
 * <!-- end-user-doc -->
 * @generated
 */
public class LaunchconfigurationItemProviderAdapterFactory extends LaunchconfigurationAdapterFactory implements ComposeableAdapterFactory, IChangeNotifier, IDisposable {
	/**
	 * This keeps track of the root adapter factory that delegates to this adapter factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected ComposedAdapterFactory parentAdapterFactory;

	/**
	 * This is used to implement {@link org.eclipse.emf.edit.provider.IChangeNotifier}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected IChangeNotifier changeNotifier = new ChangeNotifier();

	/**
	 * This keeps track of all the supported types checked by {@link #isFactoryForType isFactoryForType}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected Collection<Object> supportedTypes = new ArrayList<Object>();

	/**
	 * This constructs an instance.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public LaunchconfigurationItemProviderAdapterFactory() {
		supportedTypes.add(IEditingDomainItemProvider.class);
		supportedTypes.add(IStructuredItemContentProvider.class);
		supportedTypes.add(ITreeItemContentProvider.class);
		supportedTypes.add(IItemLabelProvider.class);
		supportedTypes.add(IItemPropertySource.class);
	}

	/**
	 * This keeps track of the one adapter used for all {@link org.eclipse.gemoc.trace.commons.model.launchconfiguration.LaunchConfiguration} instances.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected LaunchConfigurationItemProvider launchConfigurationItemProvider;

	/**
	 * This creates an adapter for a {@link org.eclipse.gemoc.trace.commons.model.launchconfiguration.LaunchConfiguration}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Adapter createLaunchConfigurationAdapter() {
		if (launchConfigurationItemProvider == null) {
			launchConfigurationItemProvider = new LaunchConfigurationItemProvider(this);
		}

		return launchConfigurationItemProvider;
	}

	/**
	 * This keeps track of the one adapter used for all {@link org.eclipse.gemoc.trace.commons.model.launchconfiguration.LanguageNameParameter} instances.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected LanguageNameParameterItemProvider languageNameParameterItemProvider;

	/**
	 * This creates an adapter for a {@link org.eclipse.gemoc.trace.commons.model.launchconfiguration.LanguageNameParameter}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Adapter createLanguageNameParameterAdapter() {
		if (languageNameParameterItemProvider == null) {
			languageNameParameterItemProvider = new LanguageNameParameterItemProvider(this);
		}

		return languageNameParameterItemProvider;
	}

	/**
	 * This keeps track of the one adapter used for all {@link org.eclipse.gemoc.trace.commons.model.launchconfiguration.AddonExtensionParameter} instances.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected AddonExtensionParameterItemProvider addonExtensionParameterItemProvider;

	/**
	 * This creates an adapter for a {@link org.eclipse.gemoc.trace.commons.model.launchconfiguration.AddonExtensionParameter}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Adapter createAddonExtensionParameterAdapter() {
		if (addonExtensionParameterItemProvider == null) {
			addonExtensionParameterItemProvider = new AddonExtensionParameterItemProvider(this);
		}

		return addonExtensionParameterItemProvider;
	}

	/**
	 * This keeps track of the one adapter used for all {@link org.eclipse.gemoc.trace.commons.model.launchconfiguration.ModelURIParameter} instances.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected ModelURIParameterItemProvider modelURIParameterItemProvider;

	/**
	 * This creates an adapter for a {@link org.eclipse.gemoc.trace.commons.model.launchconfiguration.ModelURIParameter}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Adapter createModelURIParameterAdapter() {
		if (modelURIParameterItemProvider == null) {
			modelURIParameterItemProvider = new ModelURIParameterItemProvider(this);
		}

		return modelURIParameterItemProvider;
	}

	/**
	 * This keeps track of the one adapter used for all {@link org.eclipse.gemoc.trace.commons.model.launchconfiguration.AnimatorURIParameter} instances.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected AnimatorURIParameterItemProvider animatorURIParameterItemProvider;

	/**
	 * This creates an adapter for a {@link org.eclipse.gemoc.trace.commons.model.launchconfiguration.AnimatorURIParameter}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Adapter createAnimatorURIParameterAdapter() {
		if (animatorURIParameterItemProvider == null) {
			animatorURIParameterItemProvider = new AnimatorURIParameterItemProvider(this);
		}

		return animatorURIParameterItemProvider;
	}

	/**
	 * This keeps track of the one adapter used for all {@link org.eclipse.gemoc.trace.commons.model.launchconfiguration.EntryPointParameter} instances.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected EntryPointParameterItemProvider entryPointParameterItemProvider;

	/**
	 * This creates an adapter for a {@link org.eclipse.gemoc.trace.commons.model.launchconfiguration.EntryPointParameter}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Adapter createEntryPointParameterAdapter() {
		if (entryPointParameterItemProvider == null) {
			entryPointParameterItemProvider = new EntryPointParameterItemProvider(this);
		}

		return entryPointParameterItemProvider;
	}

	/**
	 * This keeps track of the one adapter used for all {@link org.eclipse.gemoc.trace.commons.model.launchconfiguration.InitializationArgumentsParameter} instances.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected InitializationArgumentsParameterItemProvider initializationArgumentsParameterItemProvider;

	/**
	 * This creates an adapter for a {@link org.eclipse.gemoc.trace.commons.model.launchconfiguration.InitializationArgumentsParameter}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Adapter createInitializationArgumentsParameterAdapter() {
		if (initializationArgumentsParameterItemProvider == null) {
			initializationArgumentsParameterItemProvider = new InitializationArgumentsParameterItemProvider(this);
		}

		return initializationArgumentsParameterItemProvider;
	}

	/**
	 * This keeps track of the one adapter used for all {@link org.eclipse.gemoc.trace.commons.model.launchconfiguration.ModelRootParameter} instances.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected ModelRootParameterItemProvider modelRootParameterItemProvider;

	/**
	 * This creates an adapter for a {@link org.eclipse.gemoc.trace.commons.model.launchconfiguration.ModelRootParameter}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Adapter createModelRootParameterAdapter() {
		if (modelRootParameterItemProvider == null) {
			modelRootParameterItemProvider = new ModelRootParameterItemProvider(this);
		}

		return modelRootParameterItemProvider;
	}

	/**
	 * This keeps track of the one adapter used for all {@link org.eclipse.gemoc.trace.commons.model.launchconfiguration.InitializationMethodParameter} instances.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected InitializationMethodParameterItemProvider initializationMethodParameterItemProvider;

	/**
	 * This creates an adapter for a {@link org.eclipse.gemoc.trace.commons.model.launchconfiguration.InitializationMethodParameter}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Adapter createInitializationMethodParameterAdapter() {
		if (initializationMethodParameterItemProvider == null) {
			initializationMethodParameterItemProvider = new InitializationMethodParameterItemProvider(this);
		}

		return initializationMethodParameterItemProvider;
	}

	/**
	 * This returns the root adapter factory that contains this factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public ComposeableAdapterFactory getRootAdapterFactory() {
		return parentAdapterFactory == null ? this : parentAdapterFactory.getRootAdapterFactory();
	}

	/**
	 * This sets the composed adapter factory that contains this factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setParentAdapterFactory(ComposedAdapterFactory parentAdapterFactory) {
		this.parentAdapterFactory = parentAdapterFactory;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public boolean isFactoryForType(Object type) {
		return supportedTypes.contains(type) || super.isFactoryForType(type);
	}

	/**
	 * This implementation substitutes the factory itself as the key for the adapter.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Adapter adapt(Notifier notifier, Object type) {
		return super.adapt(notifier, this);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object adapt(Object object, Object type) {
		if (isFactoryForType(type)) {
			Object adapter = super.adapt(object, type);
			if (!(type instanceof Class<?>) || (((Class<?>)type).isInstance(adapter))) {
				return adapter;
			}
		}

		return null;
	}

	/**
	 * This adds a listener.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void addListener(INotifyChangedListener notifyChangedListener) {
		changeNotifier.addListener(notifyChangedListener);
	}

	/**
	 * This removes a listener.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void removeListener(INotifyChangedListener notifyChangedListener) {
		changeNotifier.removeListener(notifyChangedListener);
	}

	/**
	 * This delegates to {@link #changeNotifier} and to {@link #parentAdapterFactory}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void fireNotifyChanged(Notification notification) {
		changeNotifier.fireNotifyChanged(notification);

		if (parentAdapterFactory != null) {
			parentAdapterFactory.fireNotifyChanged(notification);
		}
	}

	/**
	 * This disposes all of the item providers created by this factory. 
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void dispose() {
		if (launchConfigurationItemProvider != null) launchConfigurationItemProvider.dispose();
		if (languageNameParameterItemProvider != null) languageNameParameterItemProvider.dispose();
		if (addonExtensionParameterItemProvider != null) addonExtensionParameterItemProvider.dispose();
		if (modelURIParameterItemProvider != null) modelURIParameterItemProvider.dispose();
		if (animatorURIParameterItemProvider != null) animatorURIParameterItemProvider.dispose();
		if (entryPointParameterItemProvider != null) entryPointParameterItemProvider.dispose();
		if (initializationArgumentsParameterItemProvider != null) initializationArgumentsParameterItemProvider.dispose();
		if (modelRootParameterItemProvider != null) modelRootParameterItemProvider.dispose();
		if (initializationMethodParameterItemProvider != null) initializationMethodParameterItemProvider.dispose();
	}

}
