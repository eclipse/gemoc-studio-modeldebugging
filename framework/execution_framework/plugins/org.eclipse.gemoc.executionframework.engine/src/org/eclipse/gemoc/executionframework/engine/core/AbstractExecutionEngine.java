/*******************************************************************************
 * Copyright (c) 2016 Inria and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Inria - initial API and implementation
 *******************************************************************************/
package org.eclipse.gemoc.executionframework.engine.core;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.transaction.RecordingCommand;
import org.eclipse.emf.transaction.RollbackException;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.emf.transaction.impl.EMFCommandTransaction;
import org.eclipse.emf.transaction.impl.InternalTransactionalEditingDomain;
import org.eclipse.gemoc.executionframework.engine.Activator;
import org.eclipse.gemoc.trace.commons.model.trace.Step;
import org.eclipse.gemoc.xdsmlframework.api.core.EngineStatus;
import org.eclipse.gemoc.xdsmlframework.api.core.EngineStatus.RunStatus;
import org.eclipse.gemoc.xdsmlframework.api.core.IDisposable;
import org.eclipse.gemoc.xdsmlframework.api.core.IExecutionContext;
import org.eclipse.gemoc.xdsmlframework.api.core.IExecutionEngine;
import org.eclipse.gemoc.xdsmlframework.api.core.IRunConfiguration;
import org.eclipse.gemoc.xdsmlframework.api.engine_addon.EngineAddonSortingRule;
import org.eclipse.gemoc.xdsmlframework.api.engine_addon.EngineAddonSortingRule.EngineEvent;
import org.eclipse.gemoc.xdsmlframework.api.engine_addon.IEngineAddon;

/**
 * Common implementation of {@link IExecutionEngine}. It provides the following
 * services:
 * <ul>
 * <li>a basic implementation of the notification for the engine addons
 * ({@link IEngineAddon}).</li>
 * <li>registration into the engine registry.</li>
 * <li>basic step service (with transaction)</li>
 * </ul>
 * This class is intended to be subclassed.
 * 
 * @author Didier Vojtisek<didier.vojtisek@inria.fr>
 *
 */
public abstract class AbstractExecutionEngine<C extends IExecutionContext<R, ?, ?>, R extends IRunConfiguration> implements IExecutionEngine<C>, IDisposable {

	private RunStatus _runningStatus = RunStatus.Initializing;

	protected EngineStatus engineStatus = new EngineStatus();

	protected C _executionContext;

	protected boolean _started = false;
	protected boolean _isStopped = false;

	protected String _name;

	public Thread thread;
	public boolean stopOnAddonError = false;
	public Throwable error = null;
	protected InternalTransactionalEditingDomain editingDomain;
	private EMFCommandTransaction currentTransaction;
	private Deque<Step<?>> currentSteps = new ArrayDeque<>();

	abstract protected void performStart();

	abstract protected void performStop();

	abstract protected void performInitialize(C executionContext);

	abstract protected void beforeStart();

	abstract protected void finishDispose();

	@Override
	public final void initialize(C executionContext) {
		if (executionContext == null)
			throw new IllegalArgumentException("executionContext");
		_executionContext = executionContext;
		this.editingDomain = getEditingDomain(executionContext.getResourceModel().getResourceSet());
		setEngineStatus(EngineStatus.RunStatus.Initializing);
		performInitialize(executionContext);
	};

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.gemoc.executionframework.engine.core.IExecutionEngine#
	 * getExecutionContext()
	 */
	@Override
	public final C getExecutionContext() {
		return _executionContext;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.gemoc.executionframework.engine.core.IExecutionEngine#
	 * getEngineStatus ()
	 */
	@Override
	public final EngineStatus getEngineStatus() {
		return engineStatus;
	}

	@Override
	public final void dispose() {
		try {
			stop();
			notifyEngineAboutToDispose();
			getExecutionContext().dispose();
			finishDispose();
		} finally {
			Activator.getDefault().gemocRunningEngineRegistry.unregisterEngine(getName());
		}
	}

	public String getName() {
		return _name == null ? engineKindName() + " " + _executionContext.getRunConfiguration().getExecutedModelURI() : _name;
	}

	private void addonError(IEngineAddon addon, Exception e) {
		StringWriter sw = new StringWriter();
		e.printStackTrace(new PrintWriter(sw));
		String exceptionAsString = sw.toString();
		Activator.getDefault().error("Exception in Addon (" + addon + "), " + exceptionAsString, e);
		if (stopOnAddonError) {
			throw new RuntimeException(e);
		}
	}

	protected void notifyEngineAboutToStart() {
		for (IEngineAddon addon : getExecutionContext().getExecutionPlatform().getSortedEngineAddons(EngineEvent.engineAboutToStart)) {
			try {
				addon.engineAboutToStart(this);
			} catch (Exception e) {
				addonError(addon, e);
			}
		}
	}

	protected void notifyEngineStarted() {
		for (IEngineAddon addon : getExecutionContext().getExecutionPlatform().getSortedEngineAddons(EngineEvent.engineStarted)) {
			try {
				addon.engineStarted(this);
			} catch (Exception e) {
				addonError(addon, e);
			}
		}
	}

	protected void notifyEngineInitialized() {
		for (IEngineAddon addon : getExecutionContext().getExecutionPlatform().getSortedEngineAddons(EngineEvent.engineInitialized)) {
			try {
				addon.engineInitialized(this);
			} catch (Exception e) {
				addonError(addon, e);
			}
		}
	}

	protected final void notifyAboutToStop() {
		for (IEngineAddon addon : getExecutionContext().getExecutionPlatform().getSortedEngineAddons(EngineEvent.engineAboutToStop)) {
			try {
				addon.engineAboutToStop(this);
			} catch (Exception e) {
				addonError(addon, e);
			}
		}
	}

	protected final void notifyEngineStopped() {
		for (IEngineAddon addon : getExecutionContext().getExecutionPlatform().getSortedEngineAddons(EngineEvent.engineStopped)) {
			try {
				addon.engineStopped(this);
			} catch (Exception e) {
				addonError(addon, e);
			}
		}
	}

	protected final void notifyEngineAboutToDispose() {
		for (IEngineAddon addon : getExecutionContext().getExecutionPlatform().getSortedEngineAddons(EngineEvent.engineAboutToDispose)) {
			try {
				addon.engineAboutToDispose(this);
			} catch (Exception e) {
				addonError(addon, e);
			}
		}
		sortedAddonMapCache.clear();
	}

	protected void notifyEngineStatusChanged(RunStatus newStatus) {
		for (IEngineAddon addon : getCachedSortedEngineAddons(EngineEvent.engineStatusChanged)) {
			try {
				addon.engineStatusChanged(this, newStatus);
			} catch (Exception e) {
				addonError(addon, e);
			}
		}
	}

	protected void notifyAboutToExecuteLogicalStep(Step<?> l) {
		for (IEngineAddon addon : getCachedSortedEngineAddons(EngineEvent.aboutToExecuteStep)) {
			try {
				addon.aboutToExecuteStep(this, l);
			} catch (EngineStoppedException ese) {
				Activator.getDefault().debug("Addon (" + addon.getClass().getSimpleName() + "@" + addon.hashCode() + ") has received stop command  with message : " + ese.getMessage());
				stop();
				throw ese; // do not continue to execute anything, forward exception
			} catch (Exception e) {
				addonError(addon, e);
			}
		}
	}

	protected void notifyLogicalStepExecuted(Step<?> l) {
		for (IEngineAddon addon : getCachedSortedEngineAddons(EngineEvent.stepExecuted)) {
			try {
				addon.stepExecuted(this, l);
			} catch (EngineStoppedException ese) {
				Activator.getDefault().debug("Addon (" + addon.getClass().getSimpleName() + "@" + addon.hashCode() + ") has received stop command  with message : " + ese.getMessage());
				stop();
			} catch (Exception e) {
				addonError(addon, e);
			}
		}
	}

	protected Map<EngineAddonSortingRule.EngineEvent, List<IEngineAddon>> sortedAddonMapCache = new HashMap<EngineAddonSortingRule.EngineEvent, List<IEngineAddon>>();

	/**
	 * a cache for the sort algorithm applies only to most relevant event such as
	 * stepExecuted aboutToExecuteLogicalStep should not be used for
	 * engineStatusChanged because engines may add new implicit addon
	 */
	protected List<IEngineAddon> getCachedSortedEngineAddons(EngineAddonSortingRule.EngineEvent engineEvent) {
		List<IEngineAddon> cachedList = sortedAddonMapCache.get(engineEvent);
		if (((cachedList != null))) {
			return cachedList;
		} else {
			List<IEngineAddon> result = getExecutionContext().getExecutionPlatform().getSortedEngineAddons(engineEvent);
			sortedAddonMapCache.put(engineEvent, result);
			return result;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.gemoc.executionframework.engine.core.IExecutionEngine#hasAddon(
	 * java. lang.Class)
	 */
	@Override
	public final <T extends IEngineAddon> boolean hasAddon(Class<T> type) {
		for (IEngineAddon c : getExecutionContext().getExecutionPlatform().getEngineAddons()) {
			if (c.getClass().equals(type))
				return true;
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.gemoc.executionframework.engine.core.IExecutionEngine#getAddon(
	 * java. lang.Class)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public final <T extends IEngineAddon> T getAddon(Class<T> type) {
		for (IEngineAddon c : getExecutionContext().getExecutionPlatform().getEngineAddons()) {
			if (c.getClass().equals(type))
				return (T) c;
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.gemoc.executionframework.engine.core.IExecutionEngine#
	 * getAddonsTypedBy(java.lang.Class)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public final <T> Set<T> getAddonsTypedBy(Class<T> type) {
		Set<T> result = new HashSet<T>();
		for (IEngineAddon c : getExecutionContext().getExecutionPlatform().getEngineAddons()) {
			if (type.isAssignableFrom(c.getClass()))
				result.add((T) c);
		}
		return result;
	}

	public final void setEngineStatus(RunStatus newStatus) {
		_runningStatus = newStatus;
		notifyEngineStatusChanged(newStatus);
	}

	@Override
	public final RunStatus getRunningStatus() {
		return _runningStatus;
	}

	public final void joinThread() {
		try {
			thread.join();
		} catch (InterruptedException e) {
			Activator.getDefault().warn("InterruptedException received", e);
		}
	}

	@Override
	public final void start() {
		if (!_started) {
			_started = true;
			Runnable r = new Runnable() {

				@Override
				public void run() {
					startSynchronous();
				}
			};
			thread = new Thread(r, engineKindName() + " " + _executionContext.getRunConfiguration().getExecutedModelURI());
			thread.start();
		}
	}

	@Override
	public final void startSynchronous() {
		try {
			notifyEngineAboutToStart();
			_name = Activator.getDefault().gemocRunningEngineRegistry.registerEngine(getName(), AbstractExecutionEngine.this);
			setEngineStatus(EngineStatus.RunStatus.Running);
			beforeStart();
			notifyEngineStarted();
			try {
				performStart();
			} finally {
				// We always try to commit the last remaining transaction
				commitCurrentTransaction();
			}
		} catch (EngineStoppedException stopException) {
			// not really an error, simply print the stop exception message
			Activator.getDefault().info("Engine stopped by the user : " + stopException.getMessage());
		} catch (Throwable e) {
			error = e;
			e.printStackTrace();
			Activator.getDefault().error("Exception received " + e.getMessage() + ", stopping engine.", e);
		} finally {
			// make sure to notify the stop if this wasn't an
			// external call to stop() that lead us here.
			// ie. normal end of the mode execution
			stop();
			Activator.getDefault().info("*** " + AbstractExecutionEngine.this.getName() + " stopped ***");
		}
	}

	@Override
	public final void stop() {
		if (!_isStopped) {
			notifyAboutToStop();
			_isStopped = true;
			performStop();
			setEngineStatus(RunStatus.Stopped);
			notifyEngineStopped();
		}
	}

	private void cleanCurrentTransactionCommand() {
		if (currentTransaction != null && currentTransaction.getCommand() != null)
			currentTransaction.getCommand().dispose();
	}

	private synchronized void commitCurrentTransaction() {
		if (currentTransaction != null) {
			try {
				currentTransaction.commit();
			} catch (RollbackException t) {

				cleanCurrentTransactionCommand();

				// Extracting the real error from the RollbackException
				Throwable realT = t.getStatus().getException();

				// And we put it inside our own sort of exception, as a cause
				SequentialExecutionException enclosingException = new SequentialExecutionException(getCurrentStep(), realT);
				enclosingException.initCause(realT);
				throw enclosingException;
			}
			currentTransaction = null;
		}
	}

	@Override
	public final Deque<Step<?>> getCurrentStack() {
		return currentSteps;
	}

	private EMFCommandTransaction createTransaction(InternalTransactionalEditingDomain editingDomain, RecordingCommand command) {
		return new EMFCommandTransaction(command, editingDomain, null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.gemoc.xdsmlframework.api.core.IExecutionEngine#
	 * getCurrentStep()
	 */
	@Override
	public final Step<?> getCurrentStep() {
		if (currentSteps.size() > 0)
			return currentSteps.getFirst();
		else
			return null;
	}

	private void startNewTransaction(InternalTransactionalEditingDomain editingDomain, RecordingCommand command) {
		currentTransaction = createTransaction(editingDomain, command);
		try {
			currentTransaction.start();
		} catch (InterruptedException e) {
			cleanCurrentTransactionCommand();
			command.dispose();
			SequentialExecutionException enclosingException = new SequentialExecutionException(getCurrentStep(), e);
			enclosingException.initCause(e);
			throw enclosingException;
		}
	}

	protected final void stopExecutionIfAsked() {
		// If the engine is stopped, we use this call to stop the execution
		if (_isStopped) {
			// notification occurs only if not already stopped
			notifyAboutToStop();
			throw new EngineStoppedException("Execution stopped.");
		}
	}

	protected final void beforeExecutionStep(Step<?> step) {

		// We will trick the transaction with an empty command. This most
		// probably make rollbacks impossible, but at least we can manage
		// transactions the way we want.
		RecordingCommand rc = new RecordingCommand(editingDomain) {
			@Override
			protected void doExecute() {
			}
		};

		beforeExecutionStep(step, rc);
		rc.execute();
	}

	/**
	 * To be called just after each execution step by an implementing engine. If the
	 * step was done through a RecordingCommand, it can be given.
	 */
	protected final void beforeExecutionStep(Step<?> step, RecordingCommand rc) {
		engineStatus.incrementNbLogicalStepCalled();
		try {

			currentSteps.push(step);

			stopExecutionIfAsked();

			// We end any running transaction
			commitCurrentTransaction();

			// We notify addons
			notifyAboutToExecuteLogicalStep(step);

			// We start a new transaction
			startNewTransaction(editingDomain, rc);

		}

		// In case of error, we dispose recording commands to be sure to remove
		// notifiers
		catch (Throwable t) {
			cleanCurrentTransactionCommand();
			rc.dispose();
			throw t;
		}

	}

	private boolean isInStep() {

		boolean containsNotNull = false;

		for (Step<?> ls : currentSteps) {
			if (ls != null && ls.getMseoccurrence() != null) {
				containsNotNull = true;
				break;
			}
		}

		return !currentSteps.isEmpty() && containsNotNull;

	}

	/**
	 * To be called just after each execution step by an implementing engine.
	 */
	protected void afterExecutionStep(List<Object> returnValue) {

		RecordingCommand emptyrc = null;

		try {

			Step<?> step = currentSteps.pop();
			if (!returnValue.isEmpty()) {
				step.getMseoccurrence().getResult().add(returnValue.get(0));
			}

			// We commit the transaction (which might be a different one
			// than the one created earlier, or null if two operations
			// end successively)
			commitCurrentTransaction();

			// We notify addons that the step ended.
			notifyLogicalStepExecuted(step);

			// If we are still in the middle of a step, we start a new
			// transaction with an empty command (since we can't have command
			// containing the remainder of the previous step),
			if (isInStep()) {
				emptyrc = new RecordingCommand(editingDomain) {
					@Override
					protected void doExecute() {
					}
				};
				startNewTransaction(editingDomain, emptyrc);
				emptyrc.execute();
			}
			engineStatus.incrementNbLogicalStepRun();

			stopExecutionIfAsked();
		}

		// In case of error, we dispose recording commands to be sure to remove
		// notifiers
		catch (Throwable t) {
			cleanCurrentTransactionCommand();
			if (emptyrc != null)
				emptyrc.dispose();
			throw t;
		}

	}

	private static InternalTransactionalEditingDomain getEditingDomain(ResourceSet rs) {
		TransactionalEditingDomain edomain = org.eclipse.emf.transaction.TransactionalEditingDomain.Factory.INSTANCE.getEditingDomain(rs);
		if (edomain instanceof InternalTransactionalEditingDomain)
			return (InternalTransactionalEditingDomain) edomain;
		else
			return null;
	}

}
