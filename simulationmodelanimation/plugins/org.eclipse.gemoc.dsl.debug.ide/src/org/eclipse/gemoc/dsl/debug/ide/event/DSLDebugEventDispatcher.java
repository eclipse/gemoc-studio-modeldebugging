/*******************************************************************************
 * Copyright (c) 2015, 2017 Obeo.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Obeo - initial API and implementation
 *******************************************************************************/
package org.eclipse.gemoc.dsl.debug.ide.event;

import org.eclipse.gemoc.dsl.debug.ide.IDSLDebugger;
import org.eclipse.gemoc.dsl.debug.ide.adapter.DSLDebugTargetAdapter;
import org.eclipse.gemoc.dsl.debug.ide.event.debugger.IDSLDebuggerReply;
import org.eclipse.gemoc.dsl.debug.ide.event.model.IDSLModelRequest;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

/**
 * Dispatches {@link IDSLDebugEvent} between the {@link IDSLDebugger debugger} and the
 * {@link DSLDebugTargetAdapter model}.
 * 
 * @author <a href="mailto:yvan.lussaud@obeo.fr">Yvan Lussaud</a>
 */
public class DSLDebugEventDispatcher extends Job implements IDSLDebugEventProcessor {

	/**
	 * The {@link Job#getName() job name}.
	 */
	public static final String NAME = "DSL debugger event dispatcher";

	/**
	 * {@link List} of events to dispatch.
	 */
	private final List<IDSLDebugEvent> events = new ArrayList<IDSLDebugEvent>();

	/**
	 * Tells that our job is over.
	 */
	private boolean terminated;

	/**
	 * The {@link IDSLDebugEvent debugger}.
	 */
	private IDSLDebugger debugger;

	/**
	 * The {@link DSLDebugTargetAdapter model}.
	 */
	private DSLDebugTargetAdapter model;

	/**
	 * Constructor.
	 */
	public DSLDebugEventDispatcher() {
		super(NAME);
		setSystem(true);
	}

	@Override
	protected IStatus run(IProgressMonitor monitor) {
		while (!terminated) {
			// wait for new events
			if (events.isEmpty()) {
				try {
					synchronized(this) {
						wait();
					}
				} catch (InterruptedException e) {
					// nothing to do here
				}
			}

			if (!monitor.isCanceled()) {
				IDSLDebugEvent event = null;
				synchronized(events) {
					if (!events.isEmpty()) {
						event = events.remove(0);
					}
				}

				if (event != null) {
					handleEvent(event);
				}
			} else {
				terminate();
			}
		}

		return Status.OK_STATUS;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.gemoc.dsl.debug.ide.event.IDSLDebugEventProcessor#handleEvent(org.eclipse.gemoc.dsl.debug.ide.event.IDSLDebugEvent)
	 */
	public Object handleEvent(final IDSLDebugEvent event) {
		final Object res;
		// forward event handling to target
		if (event instanceof IDSLDebuggerReply) {
			res = model.handleEvent(event);
		} else if (event instanceof IDSLModelRequest) {
			res = debugger.handleEvent(event);
		} else {
			throw new RuntimeException("Unknown event detected: " + event);
		}

		return res;
	}

	/**
	 * Add the given {@link IDSLDebugEvent} for future dispatch.
	 * 
	 * @param event
	 *            the {@link IDSLDebugEvent}
	 */
	public void addEvent(final IDSLDebugEvent event) {
		synchronized(events) {
			events.add(event);
		}

		synchronized(this) {
			notifyAll();
		}
	}

	/**
	 * Terminates the dispatcher.
	 */
	public void terminate() {
		terminated = true;

		// wake up job
		synchronized(this) {
			notifyAll();
		}
	}

	public void setDebugger(IDSLDebugger debugger) {
		this.debugger = debugger;
	}

	public void setModel(DSLDebugTargetAdapter model) {
		this.model = model;
	}

}
