/*******************************************************************************
 * Copyright (c) 2020 Inria and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Inria - initial API and implementation
 *******************************************************************************/
package org.eclipse.gemoc.ws.server;

import javax.servlet.ServletContext;
import javax.websocket.DeploymentException;
import javax.websocket.server.ServerContainer;
import javax.websocket.server.ServerEndpoint;
import javax.websocket.server.ServerEndpointConfig;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.gemoc.commons.eclipse.messagingsystem.api.MessagingSystem;
import org.eclipse.gemoc.commons.eclipse.messagingsystem.api.MessagingSystemManager;
import org.eclipse.gemoc.ws.server.endpoint.EndPointExtensionPointHelper;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.websocket.javax.server.config.JavaxWebSocketServletContainerInitializer;
import org.osgi.framework.BundleContext;

public class Activator extends Plugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "org.eclipse.gemoc.ws.server"; //$NON-NLS-1$
	public static final String CONSOLE_NAME = "GEMOC websocket server"; //$NON-NLS-1$

	// The shared instance
	private static Activator plugin;
	private BundleContext context;

	public static BundleContext getBundleContext() {
		return getDefault().context;
	}

	public static Activator getDefault() {
		return plugin;
	}

	public static synchronized void logError(String message, Throwable ex) {
		if (message == null) {
			message = ""; //$NON-NLS-1$
		}
		Status errorStatus = new Status(IStatus.ERROR, PLUGIN_ID, IStatus.OK, message, ex);
		Activator.getDefault().getLog().log(errorStatus);
	}

	public static synchronized void logStatus(IStatus errorStatus) {
		Activator.getDefault().getLog().log(errorStatus);
	}

	protected Server server;
	protected ServerContainer wsContainer;
	protected int assignedPort;

	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
		this.context = context;

		// starts the server later in order to be able to load the classes of the
		// contributing EndPoints
		Job job = new Job("Start GEMOC WebSocket Server") {
			protected IStatus run(IProgressMonitor monitor) {
				try {
					Activator.getDefault().startWSServer();
				} catch (Exception e) {
					logError(e.getMessage(), e);
				}
				return Status.OK_STATUS;
			}
		};
		job.setPriority(Job.SHORT);
		job.schedule();
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		stopWSServer();
		this.context = null;
		plugin = null;
		super.stop(context);
	}

	/**
	 * Starts a standard API websocket server using jetty 10
	 * 
	 * https://www.eclipse.org/jetty/documentation/jetty-10/programming-guide/index.html#pg-server-websocket-standard
	 * 
	 * @throws Exception
	 */
	public synchronized void startWSServer() throws Exception {
		// Create a Server with a ServerConnector listening on random port.
		server = new Server(0);

		// Create a ServletContextHandler with the given context path.
		ServletContextHandler handler = new ServletContextHandler(server, "/");
		server.setHandler(handler);

		// Setup the ServerContainer and the WebSocket endpoints for this web
		

		JavaxWebSocketServletContainerInitializer.configure(handler, (servletContext, container) -> {
			// Configure the ServerContainer.
			container.setDefaultMaxTextMessageBufferSize(128 * 1024);
			// Simple registration of your WebSocket endpoints.
			for (Class<?> endPointClass : EndPointExtensionPointHelper.getAllEndPointClasses()) {
				try {
					ServerEndpoint endPointAnnotation = endPointClass
							.getDeclaredAnnotation(javax.websocket.server.ServerEndpoint.class);
					if (endPointAnnotation != null) {
						String path = endPointAnnotation.value();
						info("Adding Endpoint class: " + endPointClass.getCanonicalName() + " on " + path);
						// Simple registration of WebSocket endpoints.
						container.addEndpoint(endPointClass);
						// doesn't work yet, -> Cannot load platform configurator, probably a classpath/osgi issue
						//container.addEndpoint(ServerEndpointConfig.Builder.create(endPointClass, path).build());
					}
				} catch (Exception e) {
					error("Failed to add Enpoint class: " + endPointClass.getCanonicalName(), e);
				}
			}

		});

		server.start();
		assignedPort = server.getURI().getPort();
		info("Assigned port: " + server.getURI().getPort());
		server.dump(System.err);
	}

	public synchronized void stopWSServer() throws Exception {
		if (server != null)
			server.stop();
	}

	public Server getServer() {
		return server;
	}

	public int getAssignedPort() {
		return assignedPort;
	}

	protected MessagingSystem messaggingSystem = null;

	public MessagingSystem getMessaggingSystem() {
		if (messaggingSystem == null) {
			MessagingSystemManager msm = new MessagingSystemManager();
			messaggingSystem = msm.createBestPlatformMessagingSystem(PLUGIN_ID, CONSOLE_NAME);
		}
		return messaggingSystem;
	}

	public static void info(String msg) {
		Activator.getDefault().getMessaggingSystem().info(msg, PLUGIN_ID);
	}

	public static void warn(String msg, Throwable e) {
		// Activator.getDefault().getLog().log(new Status(Status.WARNING, PLUGIN_ID,
		// Status.OK, msg, e));
		Activator.getDefault().getMessaggingSystem().warn(msg, PLUGIN_ID, e);
	}

	public static void error(String msg, Throwable e) {
		// Activator.getDefault().getLog().log(new Status(Status.ERROR, PLUGIN_ID,
		// Status.OK, msg, e));
		Activator.getDefault().getMessaggingSystem().error(msg, PLUGIN_ID, e);
	}

}
