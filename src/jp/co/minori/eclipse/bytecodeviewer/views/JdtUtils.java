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

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IClassFile;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;

public class JdtUtils {

	public static ICompilationUnit getCompilationUnit(final IResource resource) {
		IJavaElement element = JavaCore.create(resource);
		if (element instanceof ICompilationUnit) {
			return (ICompilationUnit) element;
		}
		return null;
	}

	public static IClasspathEntry findClasspathEntry(final IJavaProject project, final IFile file) throws JavaModelException {
		final IClasspathEntry[] entries = project.getResolvedClasspath(true);
		final IPath filePath = file.getFullPath();

		for (final IClasspathEntry entry : entries) {
			if (entry.getPath().isPrefixOf(filePath)) {
				return entry;
			}
		}
		return null;
	}

	public static IClassFile getByteCodePath(final IFile file) {
		if (file == null) {
			return null;
		}

		IPath path = null;
		try {
			path = getOutputPackagePath(file);
		} catch (JavaModelException e) {
			return null;
		}

		final IProject project = file.getProject();
		final IJavaElement javaElement = JavaCore.create(file);

		path = path.addTrailingSeparator().append(getClassName(javaElement));
		path = path.removeFirstSegments(1);
		
		final IFile classFile = project.getFile(path);
		
		return JavaCore.createClassFileFrom(classFile);
	}

	public static IPath getOutputPackagePath(final IFile file) throws JavaModelException {
		final IProject project = file.getProject();
		final IJavaProject javaProject = JavaCore.create(project);

		final IClasspathEntry target = findClasspathEntry(javaProject, file);

		IPath outputPath = target.getOutputLocation();
		if (outputPath == null) {
			outputPath = javaProject.getOutputLocation();
		}

		final IContainer parent = file.getParent();
		final IPath parentPath = parent.getFullPath();
		final IPath packagePath = parentPath.removeFirstSegments(parentPath.matchingFirstSegments(target.getPath()));
		final String packageName = packagePath.toString().replace('/', '.');
		
		return outputPath.append(packageName);
	}

	public static String getClassName(final IJavaElement element) {
		final String name = JavaCore.removeJavaLikeExtension(element.getElementName());
		return name + ".class";
	}

	private JdtUtils() {
		// do nothing.
	}
}
