/*******************************************************************************
 * Copyright (c) 2012 Kieran Simpson
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.quasar.googletestgenerator.cdt;

import org.eclipse.cdt.core.model.ITranslationUnit;
import org.eclipse.cdt.core.parser.ast.ASTAccessVisibility;
import org.eclipse.cdt.internal.ui.wizards.classwizard.AbstractMethodStub;
import org.eclipse.cdt.internal.ui.wizards.classwizard.IBaseClassInfo;
import org.eclipse.cdt.ui.CodeGeneration;
import org.eclipse.core.runtime.CoreException;

public class MethodStub extends AbstractMethodStub {
  private final String returnType;

  public MethodStub(final String returnType, final String name, final ASTAccessVisibility access, final boolean isVirtual, final boolean isInline) {
    super(name, access, isVirtual, isInline);

    this.returnType = returnType;
  }

  @Override
  public String createMethodDeclaration(final ITranslationUnit tu, final String className, final IBaseClassInfo[] baseClasses,
      final String lineDelimiter) throws CoreException {
    final StringBuffer buf = new StringBuffer();

    if (isVirtual())  {
      buf.append("virtual ");
    }

    buf.append(this.returnType);
    buf.append(" ");
    buf.append(this.fName);
    buf.append("()");

    if (this.fIsInline) {
      buf.append('{');
      buf.append(lineDelimiter);

      final String body = CodeGeneration.getMethodBodyContent(tu, className, this.fName, null, lineDelimiter);

      if (body != null) {
        buf.append(body);
        buf.append(lineDelimiter);
      }

      buf.append('}');
    }
    else {
      buf.append(";");
    }

    return buf.toString();
  }

  @Override
  public String createMethodImplementation(final ITranslationUnit tu, final String className, final IBaseClassInfo[] baseClasses,
      final String lineDelimiter) throws CoreException {
    if (this.fIsInline) {
      return "";
    }

    final StringBuffer buf = new StringBuffer();
    buf.append(this.returnType);
    buf.append(" ");
    buf.append(className);
    buf.append("::");
    buf.append(this.fName);
    buf.append("()");
    buf.append(lineDelimiter);
    buf.append('{');
    buf.append(lineDelimiter);

    final String body = CodeGeneration.getMethodBodyContent(tu, className, this.fName, null, lineDelimiter);

    if (body != null) {
      buf.append(body);
      buf.append(lineDelimiter);
    }

    buf.append('}');

    return buf.toString();
  }
}
