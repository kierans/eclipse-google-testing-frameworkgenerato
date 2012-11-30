/*******************************************************************************
 * Copyright (c) 2012 Kieran Simpson
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.quasar.googletestgenerator.wizards;

import java.lang.reflect.Field;

import org.eclipse.cdt.internal.ui.wizards.NewClassCreationWizard;

/**
 * Specialised creation of a new C++ class that uses Google Testing Framework.
 */
public class GoogleTestClassWizard extends NewClassCreationWizard {
  private NewGoogleTestClassWizardPage mainPage;

  public GoogleTestClassWizard() {
    super();

    // TODO: Refactor me to constant.
    setWindowTitle("New Google Test Class");
  }

  @Override
  public void addPages() {
    NewGoogleTestClassWizardPage.preConstruction();

    this.mainPage = createAndInjectPage();

    addPage(this.mainPage);

    this.mainPage.init(this.getSelection());
  }

  private NewGoogleTestClassWizardPage createAndInjectPage() {
    final NewGoogleTestClassWizardPage page = new NewGoogleTestClassWizardPage();

    try {
      injectPageIntoParentClassAttribute(page);
    }
    catch (final Exception e) {
      // TODO: Something with the exception.
    }

    return page;
  }

  /**
   * Again due to protected methods using private data, we need to use a
   * reflection hack to get the behaviour properly working
   * 
   * @throws NoSuchFieldException
   * @throws SecurityException
   * @throws IllegalAccessException
   * @throws IllegalArgumentException
   */
  private void injectPageIntoParentClassAttribute(final NewGoogleTestClassWizardPage page) throws SecurityException,
  NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
    final Field superPage = this.getClass().getSuperclass().getDeclaredField("fPage");

    superPage.setAccessible(true);
    superPage.set(this, page);
  }

  // if (!resource.exists() || !(resource instanceof IContainer)) {
  // throwCoreException("Container \"" + containerName + "\" does not exist.");
  // }
  // private void throwCoreException(String message) throws CoreException {
  // IStatus status = new Status(IStatus.ERROR, "GoogleTestGenerator",
  // IStatus.OK, message, null);
  // throw new CoreException(status);
  // }

  // public void init(IWorkbench workbench, IStructuredSelection selection) {
  // this.selection = selection;
  // }
}