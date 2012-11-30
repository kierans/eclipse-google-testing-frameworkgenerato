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
import org.eclipse.cdt.ui.CUIPlugin;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.quasar.googletestgenerator.Activator;

/**
 * Specialised creation of a new C++ class that uses Google Testing Framework.
 */
public class GoogleTestClassWizard extends NewClassCreationWizard {
  private NewGoogleTestClassWizardPage mainPage;

  public GoogleTestClassWizard() {
    super();

    // TODO: Move these to more translatable friendly constants.
    setWindowTitle("New Google Test Class");
  }

  @Override
  public void addPages() {
    NewGoogleTestClassWizardPage.preConstruction();

    this.mainPage = createAndInjectPage();

    addPage(this.mainPage);

    this.mainPage.init(getSelection());
  }

  private NewGoogleTestClassWizardPage createAndInjectPage() {
    final NewGoogleTestClassWizardPage page = new NewGoogleTestClassWizardPage();

    try {
      injectPageIntoParentClassAttribute(page);
    }
    catch (final Exception e) {
      final IStatus status = new Status(IStatus.ERROR, Activator.PLUGIN_ID, IStatus.ERROR, "Error injecting page into parent", e);
      CUIPlugin.log(status);
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
}