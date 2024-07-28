/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: MIT-0
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this
 * software and associated documentation files (the "Software"), to deal in the Software
 * without restriction, including without limitation the rights to use, copy, modify,
 * merge, publish, distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A
 * PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 * HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
 * SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.demo.abap_assistant_plugin.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.equinox.security.storage.StorageException;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.part.MultiPageEditorPart;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.ui.texteditor.ITextEditor;
import org.json.JSONException;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

import com.demo.abap_assistant_plugin.helpers.ABAPAssistantConstants;
import com.demo.abap_assistant_plugin.helpers.ABAPAssistantHelper;
import com.demo.abap_assistant_plugin.helpers.ABAPAssistantModelHelper;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionProvider;

public class ABAPCodeHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {

		final Bundle bundle = FrameworkUtil.getBundle(getClass());
		final ILog logger = Platform.getLog(bundle);
		
		IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindowChecked(event);

		try {
			if (ABAPAssistantHelper.checkPreferences()) {

				IWorkbenchPage activePage = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
				IEditorPart activeEditor = activePage.getActiveEditor();

				if (activeEditor instanceof MultiPageEditorPart) {
					ITextEditor texteditor = (ITextEditor) ((MultiPageEditorPart) activeEditor).getSelectedPage();

					if (texteditor != null) {
						IDocumentProvider dp = texteditor.getDocumentProvider();
						IDocument doc = dp.getDocument(texteditor.getEditorInput());
						IEditorSite editorSite = activeEditor.getEditorSite();

						if (editorSite != null) {
							ISelectionProvider selectionProvider = editorSite.getSelectionProvider();

							if (selectionProvider != null) {
								ISelection selection = selectionProvider.getSelection();
								ITextSelection textSelection = (ITextSelection) selection;

								try {

									String selectedText = textSelection.getText().replaceAll("\\*", "").trim();
									//System.out.println("promptText = " + selectedText);
									
									if (!selectedText.equalsIgnoreCase("")) {

										String result = "";
										String modelID = ABAPAssistantHelper
												.getPreferences(ABAPAssistantConstants.PREFERENCES_MODEL_ID);

										// Anthropic Claude 2 Models
										if (modelID.equalsIgnoreCase(ABAPAssistantConstants.CLAUDE_MODEL_ID_V2)
												|| modelID.equalsIgnoreCase(ABAPAssistantConstants.CLAUDE_MODEL_ID_V2_1)) {
											String prompt = "\n\nHuman: " + ABAPAssistantHelper.getPreferences(ABAPAssistantConstants.PREFERENCES_PROMPT_CODE)
													+ selectedText + "\nAssistant:";
											result = ABAPAssistantModelHelper.invokeClaude2Models(prompt, modelID);
											doc.replace(doc.getLength(), 0, "\n" + result + "\n");
										}
										// Anthropic Claude 3 Models
										else if (modelID
												.equalsIgnoreCase(ABAPAssistantConstants.CLAUDE3_MODEL_ID_SONNET)
												|| modelID.equalsIgnoreCase(ABAPAssistantConstants.CLAUDE3_MODEL_ID_HAIKU)) {
											String prompt = ABAPAssistantHelper.getPreferences(ABAPAssistantConstants.PREFERENCES_PROMPT_CODE) + selectedText;
											result = ABAPAssistantModelHelper.invokeClaude3Models(prompt, modelID);
											doc.replace(doc.getLength(), 0, "\n" + result + "\n");
										}
										// AI21 Jurassic Model
										else if (modelID.equalsIgnoreCase(ABAPAssistantConstants.JURASSIC_MODEL_ID_MID)
												|| modelID.equalsIgnoreCase(ABAPAssistantConstants.JURASSIC_MODEL_ID_ULTRA)) {
											String prompt = ABAPAssistantHelper.getPreferences(ABAPAssistantConstants.PREFERENCES_PROMPT_CODE) + selectedText;
											result = ABAPAssistantModelHelper.invokeJurassicModels(prompt, modelID);
											doc.replace(doc.getLength(), 0, "\n" + result + "\n");
										}
										// Custom logic - Additional foundation model implementations go here within else if

										else {
											MessageDialog.openError(window.getShell(),
													ABAPAssistantConstants.ECLIPSE_DIALOG_TITLE,
													"Invalid model. Please check your settings.");
										}
									} else {
										MessageDialog.openError(window.getShell(), ABAPAssistantConstants.ECLIPSE_DIALOG_TITLE,
												"Text not selected");
									}

								} catch (BadLocationException e) {
									e.printStackTrace();
									logger.log(new Status(Status.ERROR, bundle.getSymbolicName(), ABAPAssistantConstants.ECLIPSE_DIALOG_TITLE+ ":" +e.getLocalizedMessage(),e));
									MessageDialog.openError(window.getShell(), ABAPAssistantConstants.ECLIPSE_DIALOG_TITLE, "Error: " + e.getLocalizedMessage());
								} catch (JSONException e) {
									e.printStackTrace();
									logger.log(new Status(Status.ERROR, bundle.getSymbolicName(), ABAPAssistantConstants.ECLIPSE_DIALOG_TITLE+ ":" +e.getLocalizedMessage(),e));
									MessageDialog.openError(window.getShell(), ABAPAssistantConstants.ECLIPSE_DIALOG_TITLE, "Error: " + e.getLocalizedMessage());
								} catch (StorageException e) {
									e.printStackTrace();
									logger.log(new Status(Status.ERROR, bundle.getSymbolicName(), ABAPAssistantConstants.ECLIPSE_DIALOG_TITLE+ ":" +e.getLocalizedMessage(),e));
									MessageDialog.openError(window.getShell(), ABAPAssistantConstants.ECLIPSE_DIALOG_TITLE, "Error: " + e.getLocalizedMessage());
								} catch (Exception e) {
									e.printStackTrace();
									logger.log(new Status(Status.ERROR, bundle.getSymbolicName(), ABAPAssistantConstants.ECLIPSE_DIALOG_TITLE+ ":" +e.getLocalizedMessage(),e));
									MessageDialog.openError(window.getShell(), ABAPAssistantConstants.ECLIPSE_DIALOG_TITLE, "Error: " + e.getLocalizedMessage());
								}

							}
						}
					}

				} else {
					MessageDialog.openError(window.getShell(), ABAPAssistantConstants.ECLIPSE_DIALOG_TITLE,
							"ABAP Assistant plugin is not supported in this editor");
				}
			} else {
				MessageDialog.openError(window.getShell(), ABAPAssistantConstants.ECLIPSE_DIALOG_TITLE,
						"ABAP Assistant preferences are not set in eclipse");
			}
		} catch (StorageException e) {
			e.printStackTrace();
			logger.log(new Status(Status.ERROR, bundle.getSymbolicName(), ABAPAssistantConstants.ECLIPSE_DIALOG_TITLE+ ":" +e.getLocalizedMessage(),e));
			MessageDialog.openError(window.getShell(), ABAPAssistantConstants.ECLIPSE_DIALOG_TITLE,
					"Error: " + e.getLocalizedMessage());
		} catch (Exception e) {
			e.printStackTrace();
			logger.log(new Status(Status.ERROR, bundle.getSymbolicName(), ABAPAssistantConstants.ECLIPSE_DIALOG_TITLE+ ":" +e.getLocalizedMessage(),e));
			MessageDialog.openError(window.getShell(), ABAPAssistantConstants.ECLIPSE_DIALOG_TITLE,
					"Error: " + e.getLocalizedMessage());
		}
		return null;
		
	}
}
