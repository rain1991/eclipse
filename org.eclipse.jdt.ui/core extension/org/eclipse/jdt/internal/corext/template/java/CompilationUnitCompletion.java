/*
 * (c) Copyright IBM Corp. 2000, 2001.
 * All Rights Reserved.
 */
package org.eclipse.jdt.internal.corext.template.java;

import java.util.Iterator;
import java.util.Vector;

import org.eclipse.core.resources.IMarker;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.ICompletionRequestor;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.ITypeHierarchy;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.compiler.IProblem;

import org.eclipse.jdt.internal.core.Assert;
import org.eclipse.jdt.internal.corext.util.JavaModelUtil;

/**
 * A completion requestor to collect informations on local variables.
 * This class is used for guessing variable names like arrays, collections, etc.
 */
class CompilationUnitCompletion implements ICompletionRequestor {

	static class LocalVariable {
		String name;
		String typePackageName;
		String typeName;
		
		LocalVariable(String name, String typePackageName, String typeName) {
			this.name= name;
			this.typePackageName= typePackageName;
			this.typeName= typeName;
		}
	}

	private ICompilationUnit fUnit;

	private Vector fClasses;
	private Vector fFields;
	private Vector fInterfaces;
	private Vector fKeywords;
	private Vector fLabels;
	private Vector fLocalVariables;
	private Vector fMethods;
	private Vector fMethodDeclarations;
	private Vector fModifiers;
	private Vector fPackages;
	private Vector fTypes;
	private Vector fVariableNames;

	private boolean fError;

	/**
	 * Creates a compilation unit completion.
	 * 
	 * @param unit the compilation unit, may be <code>null</code>.
	 */
	public CompilationUnitCompletion(ICompilationUnit unit) {
		reset(unit);
	}
	
	/**
	 * Resets the completion requestor.
	 * 
	 * @param unit the compilation unit, may be <code>null</code>.
	 */
	public void reset(ICompilationUnit unit) {
		fUnit= unit;
		
		fClasses= new Vector();
		fFields= new Vector();
		fInterfaces= new Vector();
		fKeywords= new Vector();
		fLabels= new Vector();
		fLocalVariables= new Vector();
		fMethods= new Vector();
		fMethodDeclarations= new Vector();
		fModifiers= new Vector();
		fPackages= new Vector();
		fTypes= new Vector();
		fVariableNames= new Vector();		
		
		fError= false;
	}

	/*
	 * @see ICodeCompletionRequestor#acceptClass(char[], char[], char[], int, int, int)
	 */
	public void acceptClass(
		char[] packageName,
		char[] className,
		char[] completionName,
		int modifiers,
		int completionStart,
		int completionEnd) {
	}

	/*
	 * @see ICompletionRequestor#acceptError(IProblem)
	 */
	public void acceptError(IProblem error) {
		fError= true;
	}

	/*
	 * @see ICodeCompletionRequestor#acceptField(char[], char[], char[], char[], char[], char[], int, int, int)
	 */
	public void acceptField(
		char[] declaringTypePackageName,
		char[] declaringTypeName,
		char[] name,
		char[] typePackageName,
		char[] typeName,
		char[] completionName,
		int modifiers,
		int completionStart,
		int completionEnd) {
	}

	/*
	 * @see ICodeCompletionRequestor#acceptInterface(char[], char[], char[], int, int, int)
	 */
	public void acceptInterface(
		char[] packageName,
		char[] interfaceName,
		char[] completionName,
		int modifiers,
		int completionStart,
		int completionEnd) {
	}

	/*
	 * @see ICodeCompletionRequestor#acceptKeyword(char[], int, int)
	 */
	public void acceptKeyword(
		char[] keywordName,
		int completionStart,
		int completionEnd) {
	}

	/*
	 * @see ICodeCompletionRequestor#acceptLabel(char[], int, int)
	 */
	public void acceptLabel(
		char[] labelName,
		int completionStart,
		int completionEnd) {
	}

	/*
	 * @see ICodeCompletionRequestor#acceptLocalVariable(char[], char[], char[], int, int, int)
	 */
	public void acceptLocalVariable(char[] name, char[] typePackageName, char[] typeName,
		int modifiers, int completionStart,	int completionEnd)
	{
		fLocalVariables.add(new LocalVariable(
			new String(name), new String(typePackageName), new String(typeName)));
	}

	/*
	 * @see ICodeCompletionRequestor#acceptMethod(char[], char[], char[], char[][], char[][], char[][], char[], char[], char[], int, int, int)
	 */
	public void acceptMethod(
		char[] declaringTypePackageName,
		char[] declaringTypeName,
		char[] selector,
		char[][] parameterPackageNames,
		char[][] parameterTypeNames,
		char[][] parameterNames,
		char[] returnTypePackageName,
		char[] returnTypeName,
		char[] completionName,
		int modifiers,
		int completionStart,
		int completionEnd) {
	}

	/*
	 * @see ICodeCompletionRequestor#acceptMethodDeclaration(char[], char[], char[], char[][], char[][], char[][], char[], char[], char[], int, int, int)
	 */
	public void acceptMethodDeclaration(
		char[] declaringTypePackageName,
		char[] declaringTypeName,
		char[] selector,
		char[][] parameterPackageNames,
		char[][] parameterTypeNames,
		char[][] parameterNames,
		char[] returnTypePackageName,
		char[] returnTypeName,
		char[] completionName,
		int modifiers,
		int completionStart,
		int completionEnd) {
	}

	/*
	 * @see ICodeCompletionRequestor#acceptModifier(char[], int, int)
	 */
	public void acceptModifier(
		char[] modifierName,
		int completionStart,
		int completionEnd) {
	}

	/*
	 * @see ICodeCompletionRequestor#acceptPackage(char[], char[], int, int)
	 */
	public void acceptPackage(
		char[] packageName,
		char[] completionName,
		int completionStart,
		int completionEnd) {
	}

	/*
	 * @see ICodeCompletionRequestor#acceptType(char[], char[], char[], int, int)
	 */
	public void acceptType(
		char[] packageName,
		char[] typeName,
		char[] completionName,
		int completionStart,
		int completionEnd) {
	}

	/*
	 * @see ICodeCompletionRequestor#acceptVariableName(char[], char[], char[], char[], int, int)
	 */
	public void acceptVariableName(
		char[] typePackageName,
		char[] typeName,
		char[] name,
		char[] completionName,
		int completionStart,
		int completionEnd) {
	}

	// ---

	/**
	 * Tests if the code completion process produced errors.
	 */
	public boolean hasErrors() {
		return fError;
	}
	

	boolean existsLocalName(String name) {
		for (Iterator iterator = fLocalVariables.iterator(); iterator.hasNext();) {
			LocalVariable localVariable = (LocalVariable) iterator.next();

			if (localVariable.name.equals(name))
				return true;
		}

		return false;
	}

	LocalVariable[] findLocalArrays() {
		Vector vector= new Vector();

		for (Iterator iterator= fLocalVariables.iterator(); iterator.hasNext();) {
			LocalVariable localVariable= (LocalVariable) iterator.next();

			if (isArray(localVariable.typeName))
				vector.add(localVariable);
		}

		return (LocalVariable[]) vector.toArray(new LocalVariable[vector.size()]);
	}
	
	LocalVariable[] findLocalCollections() throws JavaModelException {
		Vector vector= new Vector();

		for (Iterator iterator= fLocalVariables.iterator(); iterator.hasNext();) {
			LocalVariable localVariable= (LocalVariable) iterator.next();

			String typeName= qualify(localVariable.typeName);
			
			if (typeName == null)
				continue;
						
			if (isSubclassOf(typeName, "java.util.Collection")) //$NON-NLS-1$			
				vector.add(localVariable);
		}

		return (LocalVariable[]) vector.toArray(new LocalVariable[vector.size()]);
	}

	private LocalVariable[] findLocalIntegers() {
		Vector vector= new Vector();

		for (Iterator iterator= fLocalVariables.iterator(); iterator.hasNext();) {
			LocalVariable localVariable= (LocalVariable) iterator.next();

			if (localVariable.typeName.equals("int")) //$NON-NLS-1$
				vector.add(localVariable);
		}

		return (LocalVariable[]) vector.toArray(new LocalVariable[vector.size()]);
	}

	private LocalVariable[] findLocalIterator() throws JavaModelException {
		Vector vector= new Vector();

		for (Iterator iterator= fLocalVariables.iterator(); iterator.hasNext();) {
			LocalVariable localVariable= (LocalVariable) iterator.next();

			String typeName= qualify(localVariable.typeName);			

			if (typeName == null)
				continue;

			if (isSubclassOf(typeName, "java.util.Iterator")) //$NON-NLS-1$
				vector.add(localVariable);
		}

		return (LocalVariable[]) vector.toArray(new LocalVariable[vector.size()]);
	}	

	private static boolean isArray(String type) {
		return type.endsWith("[]"); //$NON-NLS-1$
	}
	
	// returns fully qualified name if successful
	private String qualify(String typeName) throws JavaModelException {
		if (fUnit == null)
			return null;

		IType[] types= fUnit.getTypes();

		if (types.length == 0)
			return null;
		
		String[][] resolvedTypeNames= types[0].resolveType(typeName);

		if (resolvedTypeNames == null)
			return null;
			
		return resolvedTypeNames[0][0] + '.' + resolvedTypeNames[0][1];
	}	
	
	// type names must be fully qualified
	private boolean isSubclassOf(String typeName0, String typeName1) throws JavaModelException {
		if (typeName0.equals(typeName1))
			return true;

		if (fUnit == null)
			return false;

		IJavaProject project= fUnit.getJavaProject();
		IType type0= JavaModelUtil.findType(project, typeName0);
		IType type1= JavaModelUtil.findType(project, typeName1);

		ITypeHierarchy hierarchy= type0.newSupertypeHierarchy(null);
		IType[] superTypes= hierarchy.getAllSupertypes(type0);
		
		for (int i= 0; i < superTypes.length; i++)
			if (superTypes[i].equals(type1))
				return true;			
		
		return false;
	}

	static String typeToVariable(String string) {
		Assert.isTrue(string.length() > 0);		
		char first= string.charAt(0);
		
		// base type
		if (Character.isLowerCase(first))
			return "value"; //$NON-NLS-1$

		// class or interface
		return Character.toLowerCase(first) + string.substring(1);
	}

	/*
	 * @see ICompletionRequestor#acceptAnonymousType(char[], char[], char[][], char[][], char[][], char[], int, int, int)
	 */
	public void acceptAnonymousType(
		char[] superTypePackageName,
		char[] superTypeName,
		char[][] parameterPackageNames,
		char[][] parameterTypeNames,
		char[][] parameterNames,
		char[] completionName,
		int modifiers,
		int completionStart,
		int completionEnd) {
	}

}

