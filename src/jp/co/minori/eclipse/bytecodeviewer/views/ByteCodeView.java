/* ====================================================================
   Licensed to the Apache Software Foundation (ASF) under one or more
   contributor license agreements.  See the NOTICE file distributed with
   this work for additional information regarding copyright ownership.
   The ASF licenses this file to You under the Apache License, Version 2.0
   (the "License"); you may not use this file except in compliance with
   the License.  You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
==================================================================== */
package jp.co.minori.eclipse.bytecodeviewer.views;

import org.eclipse.core.resources.IFile;
import org.eclipse.jdt.core.IClassFile;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.ToolFactory;
import org.eclipse.jdt.core.util.ClassFileBytesDisassembler;
import org.eclipse.jdt.core.util.ClassFormatException;
import org.eclipse.jdt.internal.ui.javaeditor.ClassFileEditor;
import org.eclipse.jdt.internal.ui.javaeditor.CompilationUnitEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.part.ViewPart;

/**
 * This sample class demonstrates how to plug-in a new
 * workbench view. The view shows data obtained from the
 * model. The sample creates a dummy model on the fly,
 * but a real implementation would connect to the model
 * available either in this or another plug-in (e.g. the workspace).
 * The view is connected to the model using a content provider.
 * <p>
 * The view uses a label provider to define how model
 * objects should be presented in the view. Each
 * view can present the same model objects using
 * different labels and icons, if needed. Alternatively,
 * a single label provider can be shared between views
 * in order to ensure that objects of the same type are
 * presented in the same way everywhere.
 * <p>
 */

@SuppressWarnings("restriction")
public class ByteCodeView extends ViewPart implements ILinkedWithEditorView {

	/**
	 * The ID of the view as specified by the extension.
	 */
	public static final String ID = "jp.co.minori.eclipse.bytecodeviewer.views.ByteCodeView";

	private LinkWithEditorPartListener linkWithEditorPartListener = new LinkWithEditorPartListener(this);

	private Text text;

	/**
	 * The constructor.
	 */
	public ByteCodeView() {
	}

	/**
	 * This is a callback that will allow us
	 * to create the viewer and initialize it.
	 */
	public void createPartControl(final Composite parent) {
		text = new Text(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		text.setEditable(false);

		getSite().getPage().addPartListener(linkWithEditorPartListener);
	}

	public void editorActivated(final IEditorPart activeEditor) {
		change(activeEditor);
	}

	private void change(final IEditorPart activeEditor) {
		// Javaエディタ 以外は無視
		if (!(activeEditor instanceof CompilationUnitEditor)) {
			return;
		}

		final IFileEditorInput editorInput = ((IFileEditorInput)activeEditor.getEditorInput());
		final IFile resource = editorInput.getFile();

		final IClassFile classfile = JdtUtils.getByteCodePath(resource);

		try {
			final byte[] bytes = classfile.getBytes();
			final ClassFileBytesDisassembler disassembler = ToolFactory.createDefaultClassFileBytesDisassembler();
		
			text.setText(disassembler.disassemble(bytes, "\n", ClassFileBytesDisassembler.DETAILED));
		} catch (final ClassFormatException e) {
			text.setText(e.getMessage());
		} catch (final JavaModelException e) {
			text.setText(e.getMessage());
		}
	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus() {
		text.setFocus();
	}
}