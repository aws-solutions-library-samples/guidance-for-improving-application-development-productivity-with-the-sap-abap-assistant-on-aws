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
import org.eclipse.jface.preference.FileFieldEditor;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import com.demo.abap_assistant_plugin.helpers.ABAPAssistantConstants;

public class ABAPAssistantPreferences extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {
	
	StringFieldEditor awsRegion, modelId, awsProfile, promptCode, promptDoc, awsBedrockKBId, keyStorePassword, keyAlias = null;
    Button enableKB, useIdentityCenter, useRolesAnywhere;
    FileFieldEditor  rlaCertificate, awsBedrockKBConfigFile ;
    StringFieldEditor rlaTrustAnchor, rlaProfileArn, rlaRoleArn ;
    StringFieldEditor temperature, maxTokens, topP = null;
	
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
		
		Group authGroup = createGroup(ABAPAssistantConstants.PREFERENCES_AUTH_GROUP_NAME) ;
		
		GridData authGroupGridData = new GridData(GridData.FILL_BOTH);
		authGroup.setLayoutData(authGroupGridData);
		
		Label textLabel = new Label(authGroup, SWT.NONE);
        textLabel.setText(ABAPAssistantConstants.PREFERENCES_AUTH_GROUP_TEXT);
        GridData headerData = new GridData(GridData.FILL_HORIZONTAL);
        headerData.horizontalSpan = 2;
        textLabel.setLayoutData(headerData);

		useIdentityCenter = new Button(authGroup, SWT.RADIO);
        useIdentityCenter.setText(ABAPAssistantConstants.PREFERENCES_AUTH_IDC_OPTION);
        
        Composite profileComposite  = getComposite(authGroup, 1, 0, 0);
        
		awsProfile = new StringFieldEditor(ABAPAssistantConstants.PREFERENCES_AWS_PROFILE, ABAPAssistantConstants.PREFERENCES_AWS_PROFILE_FIELD_NAME , 35, profileComposite);
		addField((FieldEditor)awsProfile);
        
        useRolesAnywhere = new Button(authGroup, SWT.RADIO);
        useRolesAnywhere.setText(ABAPAssistantConstants.PREFERENCES_AUTH_RLA_OPTION);
        
        Composite rlaFieldEditorComposite  = getComposite(authGroup, 2, 0, 0);
        
        rlaCertificate = new FileFieldEditor(ABAPAssistantConstants.PREFERENCES_PKCS_12_CERTIFICATE, ABAPAssistantConstants.PREFERENCES_PKCS_12_CERTIFICATE_FIELD_NAME, true, rlaFieldEditorComposite );
        rlaCertificate.setFileExtensions(new String[] {"*.p12"});
        Text textControl1 = rlaCertificate.getTextControl(rlaFieldEditorComposite);
        GridData gridData = new GridData();
        gridData.widthHint = 320;  
        textControl1.setLayoutData(gridData);
        addField(rlaCertificate);        

        Composite rlaStringFieldEditorComposite  = getComposite(authGroup, 1, 0, 0);
        
        keyAlias = new StringFieldEditor(ABAPAssistantConstants.PREFERENCES_PKCS_12_KEY_ALIAS, ABAPAssistantConstants.PREFERENCES_PKCS_12_KEY_ALIAS_FIELD_NAME, 35, rlaStringFieldEditorComposite);
  		addField((FieldEditor)keyAlias);
        
        keyStorePassword = new StringFieldEditor(ABAPAssistantConstants.PREFERENCES_PKCS_12_KEYSTORE_PASSWORD, ABAPAssistantConstants.PREFERENCES_PKCS_12_KEYSTORE_PASSWORD_FIELD_NAME, 35, rlaStringFieldEditorComposite);
        Text textControl = keyStorePassword.getTextControl(rlaStringFieldEditorComposite);
        textControl.setEchoChar('*');
		addField((FieldEditor)keyStorePassword);
		
        rlaTrustAnchor = new StringFieldEditor(ABAPAssistantConstants.PREFERENCES_RLA_TRUST_ANCHOR, ABAPAssistantConstants.PREFERENCES_RLA_TRUST_ANCHOR_FIELD_NAME, 35, rlaStringFieldEditorComposite);
		addField((FieldEditor)rlaTrustAnchor);
		
        rlaProfileArn = new StringFieldEditor(ABAPAssistantConstants.PREFERENCES_RLA_PROFILE_ARN, ABAPAssistantConstants.PREFERENCES_RLA_PROFILE_ANCHOR_ARN_FIELD_NAME, 35, rlaStringFieldEditorComposite);
		addField((FieldEditor)rlaProfileArn);
		
        rlaRoleArn = new StringFieldEditor(ABAPAssistantConstants.PREFERENCES_RLA_ROLE_ARN, ABAPAssistantConstants.PREFERENCES_RLA_PROFILE_ROLE_ARN_FIELD_NAME, 35, rlaStringFieldEditorComposite);
		addField((FieldEditor)rlaRoleArn);
		
        
		Group generalGroup = createGroup(ABAPAssistantConstants.PREFERENCES_GENERAL_GROUP_NAME) ;
		GridData generalGroupgridData = new GridData(GridData.FILL_HORIZONTAL);
		generalGroup.setLayoutData(generalGroupgridData);
		
        Composite generalGroupComposite  = getComposite(generalGroup, 1, 0, 0);

		awsRegion = new StringFieldEditor(ABAPAssistantConstants.PREFERENCES_AWS_REGION, ABAPAssistantConstants.PREFERENCES_AWS_REGION_FIELD_NAME, 35, generalGroupComposite);
		addField((FieldEditor)awsRegion);
		createLink(generalGroupComposite, ABAPAssistantConstants.PREFERENCES_AWS_REGION_LINK_TEXT, ABAPAssistantConstants.PREFERENCES_AWS_REGION_LINK_URL);

		modelId = new StringFieldEditor(ABAPAssistantConstants.PREFERENCES_MODEL_ID, ABAPAssistantConstants.PREFERENCES_MODEL_ID_FIELD_NAME, 35, generalGroupComposite);
		addField((FieldEditor)modelId);
		createLink(generalGroupComposite, ABAPAssistantConstants.PREFERENCES_MODEL_ID_LINK_TEXT, ABAPAssistantConstants.PREFERENCES_MODEL_ID_LINK_URL);
		createLink(generalGroupComposite, ABAPAssistantConstants.PREFERENCES_MODEL_CROSS_REGION_LINK_TEXT, ABAPAssistantConstants.PREFERENCES_MODEL_CROSS_REGION_LINK_URL);

		promptCode = new StringFieldEditor(ABAPAssistantConstants.PREFERENCES_PROMPT_CODE, ABAPAssistantConstants.PREFERENCES_PROMPT_CODE_FIELD_NAME, 55, generalGroupComposite);
		addField((FieldEditor)promptCode);
		
		promptDoc = new StringFieldEditor(ABAPAssistantConstants.PREFERENCES_PROMPT_DOC, ABAPAssistantConstants.PREFERENCES_PROMPT_DOC_FIELD_NAME, 55, generalGroupComposite);
		addField((FieldEditor)promptDoc);

		Composite generalGroupOptionalComposite = new Composite(generalGroup, SWT.NONE);
		GridLayout layout = new GridLayout(3, false);
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		layout.horizontalSpacing = 15;
		generalGroupOptionalComposite.setLayout(layout);
		generalGroupOptionalComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		// Create sub-composites for each field
		Composite tempComposite = new Composite(generalGroupOptionalComposite, SWT.NONE);
		Composite topPComposite = new Composite(generalGroupOptionalComposite, SWT.NONE);
		Composite maxTokensComposite = new Composite(generalGroupOptionalComposite, SWT.NONE);

		// Set layout for each composite
		GridLayout fieldLayout = new GridLayout();
		fieldLayout.marginWidth = 0;
		fieldLayout.marginHeight = 0;
		tempComposite.setLayout(fieldLayout);
		topPComposite.setLayout(fieldLayout);
		maxTokensComposite.setLayout(fieldLayout);

		GridData fieldGridData = new GridData(GridData.BEGINNING, GridData.CENTER, false, false);
		tempComposite.setLayoutData(fieldGridData);
		topPComposite.setLayoutData(fieldGridData);
		maxTokensComposite.setLayoutData(fieldGridData);

		// Create the field editors in their respective composites
		temperature = new StringFieldEditor(ABAPAssistantConstants.PREFERENCES_TEMPERATURE, ABAPAssistantConstants.PREFERENCES_TEMPERATURE_FIELD_NAME, 5, tempComposite) {
		    @Override
		    protected boolean checkState() {
		        Text text = getTextControl();
		        if (text == null) {
		            return false;
		        }
		        
		        String value = text.getText();
		        if (value.isEmpty()) {
		            return true;
		        }
		        
		        try {
		            double number = Double.parseDouble(value);
		            if (number >= 0 && number <= 1) {
		                clearErrorMessage();
		                return true;
		            } else {
		                showErrorMessage("Please enter a number between 0 and 1");
		                return false;
		            }
		        } catch (NumberFormatException e) {
		        	showErrorMessage("Please enter a valid decimal number");
		            return false;
		        }
		    }
		    
		    @Override
		    protected void doLoad() {
		        super.doLoad();
		        Text text = getTextControl();
		        if (text != null) {
		            text.addVerifyListener(e -> {
		                // Allow digits and one decimal point
		                String newText = text.getText().substring(0, e.start) + e.text + text.getText().substring(e.end);
		                e.doit = newText.matches("^[0-9]*\\.?[0-9]*$");
		            });
		        }
		    }
		};
		
		topP = new StringFieldEditor(ABAPAssistantConstants.PREFERENCES_TOP_P, ABAPAssistantConstants.PREFERENCES_TOP_P_FIELD_NAME, 5, topPComposite) {
	    @Override
	    protected boolean checkState() {
	        Text text = getTextControl();
	        if (text == null) {
	            return false;
	        }
	        
	        String value = text.getText();
	        if (value.isEmpty()) {
	            return true;
	        }
	        
	        try {
	            double number = Double.parseDouble(value);
	            if (number >= 0 && number <= 1) {
	                clearErrorMessage();
	                return true;
	            } else {
	                showErrorMessage("Please enter a number between 0 and 1");
	                return false;
	            }
	        } catch (NumberFormatException e) {
	        	showErrorMessage("Please enter a valid decimal number");
	            return false;
	        }
	    }
	    
	    @Override
	    protected void doLoad() {
	        super.doLoad();
	        Text text = getTextControl();
	        if (text != null) {
	            text.addVerifyListener(e -> {
	                // Allow digits and one decimal point
	                String newText = text.getText().substring(0, e.start) + e.text + text.getText().substring(e.end);
	                e.doit = newText.matches("^[0-9]*\\.?[0-9]*$");
		            });
		        }
		    }
		};
		
		maxTokens = new StringFieldEditor(ABAPAssistantConstants.PREFERENCES_MAX_TOKENS, ABAPAssistantConstants.PREFERENCES_MAX_TOKENS_FIELD_NAME, 5, maxTokensComposite) {
		    @Override
		    protected boolean checkState() {
		        Text text = getTextControl();
		        if (text == null) {
		            return false;
		        }
		        
		        String value = text.getText();
		        if (value.isEmpty()) {
		            return true;
		        }
		        
		        try {
		            Integer.parseInt(value);
		            clearErrorMessage();
		            return true;
		        } catch (NumberFormatException e) {
		            return false;
		        }
		    }
		    @Override
		    protected void doLoad() {
		        super.doLoad();
		        Text text = getTextControl();
		        if (text != null) {
		            text.addVerifyListener(e -> {
		                // Only allow digits
		                e.doit = e.text.matches("[0-9]*");
		            });
		        }
		    }
		};
		

		temperature.setTextLimit(3);
		topP.setTextLimit(5);
		maxTokens.setTextLimit(5);
		
		addField(temperature);
		addField(topP);
		addField(maxTokens);
  
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
			pref.putBoolean(ABAPAssistantConstants.PREFERENCES_IDENTITY_CENTER, useIdentityCenter.getSelection(), true);
			pref.putBoolean(ABAPAssistantConstants.PREFERENCES_ROLES_ANYWHERE, useRolesAnywhere.getSelection(), true);
			pref.put(ABAPAssistantConstants.PREFERENCES_PKCS_12_CERTIFICATE, rlaCertificate.getStringValue(), true);
			pref.put(ABAPAssistantConstants.PREFERENCES_PKCS_12_KEY_ALIAS, keyAlias.getStringValue(), true);
			pref.put(ABAPAssistantConstants.PREFERENCES_PKCS_12_KEYSTORE_PASSWORD, keyStorePassword.getStringValue(), true);
			pref.put(ABAPAssistantConstants.PREFERENCES_RLA_TRUST_ANCHOR, rlaTrustAnchor.getStringValue(), true);
			pref.put(ABAPAssistantConstants.PREFERENCES_RLA_PROFILE_ARN, rlaProfileArn.getStringValue(), true);
			pref.put(ABAPAssistantConstants.PREFERENCES_RLA_ROLE_ARN, rlaRoleArn.getStringValue(), true);
			pref.put(ABAPAssistantConstants.PREFERENCES_TEMPERATURE, temperature.getStringValue(), true);
			pref.put(ABAPAssistantConstants.PREFERENCES_TOP_P, topP.getStringValue(), true);
			pref.put(ABAPAssistantConstants.PREFERENCES_MAX_TOKENS, maxTokens.getStringValue(), true);	

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

			useIdentityCenter.setSelection(prefs.getBoolean(ABAPAssistantConstants.PREFERENCES_IDENTITY_CENTER, true));
			useRolesAnywhere.setSelection(prefs.getBoolean(ABAPAssistantConstants.PREFERENCES_ROLES_ANYWHERE, false));
			rlaCertificate.setStringValue(prefs.get(ABAPAssistantConstants.PREFERENCES_PKCS_12_CERTIFICATE, ""));
			keyAlias.setStringValue(prefs.get(ABAPAssistantConstants.PREFERENCES_PKCS_12_KEY_ALIAS, ""));
			keyStorePassword.setStringValue(prefs.get(ABAPAssistantConstants.PREFERENCES_PKCS_12_KEYSTORE_PASSWORD, ""));
			rlaTrustAnchor.setStringValue(prefs.get(ABAPAssistantConstants.PREFERENCES_RLA_TRUST_ANCHOR, ""));
			rlaProfileArn.setStringValue(prefs.get(ABAPAssistantConstants.PREFERENCES_RLA_PROFILE_ARN, ""));
			rlaRoleArn.setStringValue(prefs.get(ABAPAssistantConstants.PREFERENCES_RLA_ROLE_ARN, ""));
			temperature.setStringValue(prefs.get(ABAPAssistantConstants.PREFERENCES_TEMPERATURE, "0.2"));
			topP.setStringValue(prefs.get(ABAPAssistantConstants.PREFERENCES_TOP_P, "0.2"));
			maxTokens.setStringValue(prefs.get(ABAPAssistantConstants.PREFERENCES_MAX_TOKENS, "4096"));


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
	    useIdentityCenter.setSelection(true);
	    useRolesAnywhere.setSelection(false);
	    rlaCertificate.setStringValue("");
	    keyAlias.setStringValue("");
	    keyStorePassword.setStringValue("");
	    rlaTrustAnchor.setStringValue("");
	    rlaProfileArn.setStringValue("");
	    rlaRoleArn.setStringValue("");
	    temperature.setStringValue("0.2");
	    topP.setStringValue("0.2");
	    maxTokens.setStringValue("4096");

	}
	
	private Group createGroup(String name) {
		 // Create the group
        Group group = new Group(getFieldEditorParent(), SWT.NONE);
        group.setText(name);
        
        // Set group layout
        GridLayout groupLayout = new GridLayout(1, false);
        groupLayout.marginHeight = 10;
        groupLayout.marginWidth = 10;
        group.setLayout(groupLayout);
		        
		// Set group layout data
		//        GridData groupData = new GridData(GridData.FILL_HORIZONTAL);
		//        groupData.horizontalSpan = 2;
		//        groupData.verticalIndent = 15;
		//        group.setLayoutData(groupData);
        
        return group;
	}
	
	private Link createLink(Composite group, String name, String url) {
		Link link = new Link(group, SWT.NONE);
	    link.setText(name);
	    GridData linkData = new GridData();
	    linkData.horizontalAlignment = GridData.CENTER;
	    linkData.verticalAlignment = GridData.CENTER;
	    linkData.horizontalSpan = 2;
	    link.setLayoutData(linkData);        
	    link.addSelectionListener(new SelectionAdapter() {
		    @Override
		    public void widgetSelected(SelectionEvent e) {
		       Program.launch(url);
		    }
	    });
	    return link;
	}
    
    private Composite getComposite(Group groupName, int numColumns, int marginWidth, int marginHeight) {
        Composite composite = new Composite(groupName, SWT.NONE);
        GridLayout compositeLayout = new GridLayout();
        compositeLayout.numColumns = numColumns;  
        compositeLayout.marginWidth = marginWidth;
        compositeLayout.marginHeight = marginHeight;
        composite.setLayout(compositeLayout);
        
        GridData compositeData = new GridData(GridData.FILL_HORIZONTAL);
        composite.setLayoutData(compositeData);
        
        return composite;
    }
    
    @Override
    protected void adjustGridLayout() {
        // Adjust the layout to accommodate the groups
        ((GridLayout) getFieldEditorParent().getLayout()).numColumns = 1;
    }

}
