/*******************************************************************************
 * Copyright (c) 2000, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jdt.internal.ui.search;

import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.search.IJavaSearchScope;

/**
 * Describes a search query by giving the <code>IJavaElement</code> to search for.
 * Clients must not instantiate or subclass this class.
 */
public class ElementQuerySpecification extends QuerySpecification {
	private IJavaElement fElement;

	/**
	 * A constructor.
	 * @see org.eclipse.jdt.core.search.SearchEngine#search(IWorkspace, IJavaElement, int, IJavaSearchScope, IJavaSearchResultCollector)
	 * @param javaElement The java element the query should search for.
	 * @param limitTo		  The kind of occurrence the query should search for
	 * @param scope		  The scope to search in.
	 * @param scopeDescription A human readeable description of the search scope.
	 */
	public ElementQuerySpecification(IJavaElement javaElement, int limitTo, IJavaSearchScope scope, String scopeDescription) {
		super(limitTo, scope, scopeDescription);
		fElement= javaElement;
	}
	
	public IJavaElement getElement() {
		return fElement;
	}
}
