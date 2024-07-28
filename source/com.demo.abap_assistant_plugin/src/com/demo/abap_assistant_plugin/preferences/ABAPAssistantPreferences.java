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

package com.demo.abap_assistant_plugin.preferences;

import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.equinox.security.storage.ISecurePreferences;
import org.eclipse.equinox.security.storage.SecurePreferencesFactory;
import org.eclipse.equinox.security.storage.StorageException;
import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import com.demo.abap_assistant_plugin.helpers.ABAPAssistantConstants;

public class ABAPAssistantPreferences extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	
	StringFieldEditor awsRegion, modelId, awsProfile, promptCode, promptDoc = null;
	
	
	public ABAPAssistantPreferences() {
		super(FieldEditorPreferencePage.GRID);
	}
	
	@Override
	public void init(IWorkbench workbench) {
			setPreferenceStore((IPreferenceStore) new ABAPAssistantPreferencesStore(InstanceScope.INSTANCE, "com.demo.abap_assistant.plugin.preferences"));
			setDescription(ABAPAssistantConstants.PREFERENCES_DESCCRIPTION);
	}

	@Override
	protected void createFieldEditors() {	
		awsRegion = new StringFieldEditor(ABAPAssistantConstants.PREFERENCES_AWS_REGION, "AWS Region", 35, getFieldEditorParent());
		addField((FieldEditor)awsRegion);
		modelId = new StringFieldEditor(ABAPAssistantConstants.PREFERENCES_MODEL_ID, "Model ID", 35, getFieldEditorParent());
		addField((FieldEditor)modelId);
		awsProfile = new StringFieldEditor(ABAPAssistantConstants.PREFERENCES_AWS_PROFILE, "AWS Profile", 35, getFieldEditorParent());
		addField((FieldEditor)awsProfile);
		promptCode = new StringFieldEditor(ABAPAssistantConstants.PREFERENCES_PROMPT_CODE, "Prompt for ABAP Code", 55, getFieldEditorParent());
		addField((FieldEditor)promptCode);
		promptDoc = new StringFieldEditor(ABAPAssistantConstants.PREFERENCES_PROMPT_DOC, "Prompt for Documentation", 55, getFieldEditorParent());
		addField((FieldEditor)promptDoc);
		
	}
	
	@Override
	public boolean performOk() {
		ISecurePreferences pref = SecurePreferencesFactory.getDefault();
		try {
			pref.put(ABAPAssistantConstants.PREFERENCES_AWS_REGION, awsRegion.getStringValue(), true);
			pref.put(ABAPAssistantConstants.PREFERENCES_MODEL_ID, modelId.getStringValue(), true);
			pref.put(ABAPAssistantConstants.PREFERENCES_AWS_PROFILE, awsProfile.getStringValue(), true);
			pref.put(ABAPAssistantConstants.PREFERENCES_PROMPT_CODE, promptCode.getStringValue(), true);
			pref.put(ABAPAssistantConstants.PREFERENCES_PROMPT_DOC, promptDoc.getStringValue(), true);
			pref.flush();
		} catch (StorageException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return true;
	}
	
	@Override
	protected Control createContents(Composite parent) {
		super.createContents(parent);
		ISecurePreferences prefs = SecurePreferencesFactory.getDefault();
		try {
			awsRegion.setStringValue(prefs.get(ABAPAssistantConstants.PREFERENCES_AWS_REGION, ""));
			modelId.setStringValue(prefs.get(ABAPAssistantConstants.PREFERENCES_MODEL_ID, ""));
			awsProfile.setStringValue(prefs.get(ABAPAssistantConstants.PREFERENCES_AWS_PROFILE, ""));
			promptCode.setStringValue(prefs.get(ABAPAssistantConstants.PREFERENCES_PROMPT_CODE, ABAPAssistantConstants.PROMPT_ABAP_CODE));
			promptDoc.setStringValue(prefs.get(ABAPAssistantConstants.PREFERENCES_PROMPT_DOC, ABAPAssistantConstants.PROMPT_ABAP_DOC));
		} catch (StorageException e) {
			e.printStackTrace();
		}
		 return (Control)parent;
	}
	
	@Override
	protected void performDefaults() {
	    SecurePreferencesFactory.getDefault();
	    awsRegion.setStringValue(ABAPAssistantConstants.PREFERENCES_AWS_REGION_DEFAULT);
	    modelId.setStringValue(ABAPAssistantConstants.PREFERENCES_MODEL_ID_DEFAULT);
	    awsProfile.setStringValue(ABAPAssistantConstants.PREFERENCES_AWS_PROFILE_DEFAULT);
	    promptCode.setStringValue(ABAPAssistantConstants.PROMPT_ABAP_CODE);
	    promptDoc.setStringValue(ABAPAssistantConstants.PROMPT_ABAP_DOC);
	}
	


}
