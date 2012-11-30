/*******************************************************************************
 * Copyright (c) 2012 Kieran Simpson
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.quasar.googletestgenerator.wizards;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import org.eclipse.cdt.core.CCorePlugin;
import org.eclipse.cdt.core.browser.IndexTypeInfo;
import org.eclipse.cdt.core.dom.ast.IBinding;
import org.eclipse.cdt.core.index.IIndex;
import org.eclipse.cdt.core.index.IIndexBinding;
import org.eclipse.cdt.core.index.IIndexManager;
import org.eclipse.cdt.core.index.IndexFilter;
import org.eclipse.cdt.core.model.CoreModel;
import org.eclipse.cdt.core.parser.ast.ASTAccessVisibility;
import org.eclipse.cdt.internal.ui.browser.opentype.ElementSelectionDialog;
import org.eclipse.cdt.internal.ui.wizards.dialogfields.LayoutUtil;
import org.eclipse.cdt.ui.CUIPlugin;
import org.eclipse.cdt.ui.wizards.NewClassCreationWizardPage;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.quasar.googletestgenerator.Activator;
import org.quasar.googletestgenerator.cdt.MethodStub;

/**
 * This wizard page mimics the CDTs {@link NewClassCreationWizardPage} in look
 * and feel, with behaviour geared towards generating a Google Testing
 * Frameworks test class.
 */
public class NewGoogleTestClassWizardPage extends NewClassCreationWizardPage {
  public static final String PAGE_NAME = "NewGoogleTestClassWizardPage";
  private static final IProgressMonitor NO_MONITOR = null;
  private static final Throwable NO_CAUSE = null;

  private static boolean preConstructionCalled = false;

  public NewGoogleTestClassWizardPage() {
    super();

    checkPreconstruction();

    // TODO: Move these to more translatable friendly constants.
    setTitle("Google Test Class");
    setDescription("Creates a new Google Test C++ class");
  }

  /**
   * As of CDT 8.1.1, the {@link NewClassCreationWizardPage} isn't subclass
   * friendly so this class uses reflection to perform some nasty hacks.
   */
  public static final void preConstruction() {
    try {
      changePageNameConstant();
    }
    catch (final Exception e) {
      throwException("An error occurred while preconstrucing " + NewGoogleTestClassWizardPage.class.getName(), e);
    }

    preConstructionCalled = true;
  }

  @Override
  public void createControl(final Composite parent) {
    super.createControl(parent);

    addGoogleTestMembersToNewClass();
  }

  /**
   * We override this to change the layout for this section.
   */
  @Override
  protected void createFileControls(final Composite composite, final int nColumns) {
    this.fHeaderFileDialogField.doFillIntoGrid(composite, nColumns);
    Text textControl = this.fHeaderFileDialogField.getTextControl(null);
    LayoutUtil.setWidthHint(textControl, getMaxFieldWidth());
    textControl.addFocusListener(createStatusFocusListenerFor(HEADER_FILE_ID));

    this.fSourceFileDialogField.doFillIntoGrid(composite, nColumns);
    textControl = this.fSourceFileDialogField.getTextControl(null);
    LayoutUtil.setWidthHint(textControl, getMaxFieldWidth());
    textControl.addFocusListener(createStatusFocusListenerFor(SOURCE_FILE_ID));
  }

  /**
   * This is based off {@link ElementSelectionDialog#getElementsByPrefix(char, org.eclipse.core.runtime.IProgressMonitor)}
   */
  private void addGoogleTestMembersToNewClass()  {
    addGoogleTestClassAsDefaultParentOfNewClass();
    addGoogleTestMethodsToNewClass();
  }

  /**
   * This is based off {@link ElementSelectionDialog#getElementsByPrefix(char, org.eclipse.core.runtime.IProgressMonitor)}
   */
  private void addGoogleTestClassAsDefaultParentOfNewClass() {
    final IndexFilter filter= new IndexFilter() {
      @Override
      public boolean acceptBinding(final IBinding binding) throws CoreException {
        return IndexFilter.ALL_DECLARED.acceptBinding(binding);
      }
    };

    try  {
      final IIndex index = CCorePlugin.getIndexManager().getIndex(CoreModel.getDefault().getCModel().getCProjects(), IIndexManager.ADD_EXTENSION_FRAGMENTS_NAVIGATION);
      index.acquireReadLock();

      try  {
        // we want to find 'testing::Test::*'
        final IIndexBinding[] bindings = index.findBindingsForPrefix(new char[] { 'T' }, false, filter, NO_MONITOR);
        for (final IIndexBinding binding: bindings)  {
          final String[] fqns = binding.getQualifiedName();

          if (bindingRefersToTestClass(fqns))  {
            final IndexTypeInfo typeinfo = IndexTypeInfo.create(index, binding);
            addBaseClass(typeinfo, ASTAccessVisibility.PUBLIC, false);

            return;
          }
        }
      }
      finally {
        index.releaseReadLock();
      }
    }
    catch(final Exception e)  {
      throwException("Problem adding Google Test Class as default parent of new class", e);
    }
  }

  private void addGoogleTestMethodsToNewClass() {
    addMethodStub(new MethodStub("void", "SetUp", ASTAccessVisibility.PROTECTED, true, false), true);
    addMethodStub(new MethodStub("void", "TearDown", ASTAccessVisibility.PROTECTED, true, false), true);
  }

  private boolean bindingRefersToTestClass(final String[] fqns) {
    // the fqns length needs to be two to make sure we get the class type and not a method or something.
    return (fqns.length == 2) && bindingIsInTestClass(fqns);
  }

  private boolean bindingIsInTestClass(final String[] fqns)  {
    return "testing".equals(fqns[0]) && "Test".equals(fqns[1]);
  }

  /**
   * This is needed because in the parent class a protected method is using a
   * private inner class, so a reflection hack is needed.
   */
  private FocusListener createStatusFocusListenerFor(final int fileId) {
    try  {
      final Class<?>[] innerClasses = this.getClass().getSuperclass().getDeclaredClasses();
      FocusListener instantiatedListener = null;

      for(final Class<?> cls: innerClasses)  {
        if (cls.getSimpleName().equals("StatusFocusListener"))  {
          // see http://stackoverflow.com/questions/2097982/is-it-possible-to-create-an-instance-of-nested-class-using-java-reflection for arg order explanation.
          final Constructor<?> statusFocusListenerConstructor = cls.getDeclaredConstructor(NewClassCreationWizardPage.class, int.class);
          statusFocusListenerConstructor.setAccessible(true);

          instantiatedListener = (FocusListener) statusFocusListenerConstructor.newInstance(this, fileId);

          return instantiatedListener;
        }
      }
    }
    catch(final Exception e)  {
      throwException("Can't create StatusFocusListener", e);
    }

    throwException("Didn't find StatusFocusListener class to create instance of");

    // keep the compiler happy.
    return null;
  }

  private static void changePageNameConstant() throws NoSuchFieldException, IllegalAccessException {
    final Field pageName = NewClassCreationWizardPage.class.getDeclaredField("PAGE_NAME");
    pageName.setAccessible(true);

    final Field modifiersField = Field.class.getDeclaredField("modifiers");
    modifiersField.setAccessible(true);
    modifiersField.setInt(pageName, pageName.getModifiers() & ~Modifier.FINAL);

    pageName.set(null, PAGE_NAME);
  }

  private static void throwException(final String message) {
    throwException(message, NO_CAUSE);
  }

  private static void throwException(final String message, final Throwable e) {
    final IStatus status = new Status(IStatus.ERROR, Activator.PLUGIN_ID, IStatus.ERROR, message, e);
    CUIPlugin.log(status);
  }

  private void checkPreconstruction() {
    if (!preConstructionCalled) {
      throw new RuntimeException("CDT " + NewGoogleTestClassWizardPage.class.getName()
          + " didn't have it's preconstruction method called");
    }
  }
}