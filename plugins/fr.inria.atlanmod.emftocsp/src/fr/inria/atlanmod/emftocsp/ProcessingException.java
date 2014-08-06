/*******************************************************************************
 * Copyright (c) 2013 INRIA Rennes Bretagne-Atlantique.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     INRIA Rennes Bretagne-Atlantique - initial API and implementation
 *******************************************************************************/
package fr.inria.atlanmod.emftocsp;


/**
 * @author <a href="mailto:amine.benelallam@inria.fr">Amine Benelallam</a>
 *
 */
public class ProcessingException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public ProcessingException(Throwable e) {
		super(e);
	}

	public ProcessingException(String msg, Throwable th) {
		super(msg, th);
	}

	public ProcessingException(String string) {
		super(string);
	}

}
