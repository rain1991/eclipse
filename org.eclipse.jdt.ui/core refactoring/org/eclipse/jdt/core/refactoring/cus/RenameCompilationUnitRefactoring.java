/*
 * (c) Copyright IBM Corp. 2000, 2001.
 * All Rights Reserved.
 */
package org.eclipse.jdt.core.refactoring.cus;import org.eclipse.core.runtime.IProgressMonitor;import org.eclipse.jdt.core.Flags;import org.eclipse.jdt.core.ICompilationUnit;import org.eclipse.jdt.core.IType;import org.eclipse.jdt.core.JavaModelException;import org.eclipse.jdt.core.refactoring.IChange;import org.eclipse.jdt.core.refactoring.RefactoringStatus;import org.eclipse.jdt.core.refactoring.tagging.IRenameRefactoring;import org.eclipse.jdt.core.refactoring.text.ITextBufferChangeCreator;import org.eclipse.jdt.core.refactoring.types.RenameTypeRefactoring;import org.eclipse.jdt.internal.core.refactoring.Assert;import org.eclipse.jdt.internal.core.refactoring.Checks;import org.eclipse.jdt.internal.core.refactoring.CompositeChange;import org.eclipse.jdt.internal.core.refactoring.NullChange;import org.eclipse.jdt.internal.core.refactoring.RenameResourceChange;

public class RenameCompilationUnitRefactoring extends CompilationUnitRefactoring implements IRenameRefactoring{

	private String fNewName;
	private RenameTypeRefactoring fRenameTypeRefactoring;
	
	public RenameCompilationUnitRefactoring(ITextBufferChangeCreator changeCreator, ICompilationUnit cu){
		super(changeCreator, cu);
		computeRenameTypeRefactoring();
	}
		
	private void computeRenameTypeRefactoring(){
		IType type= getCu().getType(getSimpleCUName());
		if (type.exists())
			fRenameTypeRefactoring= new RenameTypeRefactoring(getTextBufferChangeCreator(), type);
		else
			fRenameTypeRefactoring= null;
	}

	/**
	 * @see IRenameRefactoring#setNewName(String)
	 * @param newName 'java' must be included
	 */
	public void setNewName(String newName) {
		Assert.isNotNull(newName);
		fNewName= newName;
		if (willRenameType())
			fRenameTypeRefactoring.setNewName(removeFileNameExtension(newName));
	}

	/**
	 * @see IRenameRefactoring#checkNewName()
	 */
	public RefactoringStatus checkNewName() throws JavaModelException {
		RefactoringStatus result= new RefactoringStatus();
		result.merge(Checks.checkCompilationUnitName(fNewName));
		if (willRenameType())
			result.merge(fRenameTypeRefactoring.checkNewName());
		if (Checks.isAlreadyNamed(getCu(), fNewName))
			result.addFatalError("The same name chosen");	
		return result;
	}
	
	/**
	 * @see IRenameRefactoring#getCurrentName()
	 */
	public String getCurrentName() {
		return getCu().getElementName();
	}
	
	/**
	 * @see IRefactoring#getName()
	 */
	public String getName() {
		return "Rename \"" + getCu().getElementName() + "\" to \"" + fNewName + "\"";
	}

	/**
	 * @see Refactoring#checkActivation(IProgressMonitor)
	 */
	public RefactoringStatus checkActivation(IProgressMonitor pm) throws JavaModelException {
		RefactoringStatus result= new RefactoringStatus();
		result.merge(checkAvailability(getCu()));
		if (willRenameType())
			result.merge(fRenameTypeRefactoring.checkActivation(pm));
		return result;
	}

	/**
	 * @see Refactoring#checkInput(IProgressMonitor)
	 */
	public RefactoringStatus checkInput(IProgressMonitor pm) throws JavaModelException {
		if (willRenameType())
			return fRenameTypeRefactoring.checkInput(pm);
		else{
			RefactoringStatus result= new RefactoringStatus();
			result.merge(Checks.checkCompilationUnitNewName(getCu(), fNewName));
			return result;
		}
	}
	
	/**
	 * 
	 * @see IRefactoring#createChange(IProgressMonitor)
	 */
	public IChange createChange(IProgressMonitor pm) throws JavaModelException {
		//renaming the file is taken care of in renameTypeRefactoring
		if (willRenameType())
			return fRenameTypeRefactoring.createChange(pm);
	
		CompositeChange composite= new CompositeChange();
		composite.addChange(new RenameResourceChange(getResource(getCu()), removeFileNameExtension(fNewName)));
		return composite;	
	}

	private boolean willRenameType() {
		return fRenameTypeRefactoring != null;
	}

}
