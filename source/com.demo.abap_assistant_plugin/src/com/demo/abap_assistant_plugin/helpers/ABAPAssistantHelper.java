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

package com.demo.abap_assistant_plugin.helpers;

import java.util.Optional;

import org.eclipse.equinox.security.storage.ISecurePreferences;
import org.eclipse.equinox.security.storage.SecurePreferencesFactory;
import org.eclipse.equinox.security.storage.StorageException;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleConstants;
import org.eclipse.ui.console.IConsoleManager;
import org.eclipse.ui.console.IConsoleView;
import org.eclipse.ui.console.MessageConsole;
import org.eclipse.ui.console.MessageConsoleStream;

import software.amazon.awssdk.auth.credentials.AwsSessionCredentials;
import software.amazon.awssdk.auth.credentials.ProfileProviderCredentialsContext;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.profiles.Profile;
import software.amazon.awssdk.profiles.ProfileFile;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.bedrockruntime.BedrockRuntimeClient;
import software.amazon.awssdk.services.sso.auth.SsoProfileCredentialsProviderFactory;

public class ABAPAssistantHelper {
	
	public static String getPreferences (String prefName) throws StorageException {
		ISecurePreferences prefs = SecurePreferencesFactory.getDefault();
		String prefvalue = "";
		prefvalue = prefs.get(prefName, "").trim();
		System.out.println(prefName+" = "+prefvalue);
		return prefvalue;	
	}
	
	public static void writeToConsole(String output, IWorkbenchPage page) throws PartInitException {
		MessageConsole messageConsole = findConsole(ABAPAssistantConstants.ECLIPSE_CONSOLE);
		messageConsole.clearConsole();
		MessageConsoleStream out = messageConsole.newMessageStream();
		out.println(output);
		
		String id = IConsoleConstants.ID_CONSOLE_VIEW;
		IConsoleView view = (IConsoleView) page.showView(id);
		view.display(messageConsole);
	}

	public static BedrockRuntimeClient getBedrockClient () throws StorageException {
		String awsRegion = ABAPAssistantHelper.getPreferences(ABAPAssistantConstants.PREFERENCES_AWS_REGION).toLowerCase();
		String awsProfile = ABAPAssistantHelper.getPreferences(ABAPAssistantConstants.PREFERENCES_AWS_PROFILE);
        
		ProfileFile profileFile = ProfileFile.defaultProfileFile();

		Optional<Profile> profile = profileFile.getSection(ProfileFile.PROFILES_SECTION_TITLE, awsProfile);

		ProfileProviderCredentialsContext profileProvider = ProfileProviderCredentialsContext.builder()
				.profile(profile.get())
				.profileFile(profileFile).build();
		
		AwsSessionCredentials awsCredentials = (AwsSessionCredentials) new SsoProfileCredentialsProviderFactory()
				.create(profileProvider)
				.resolveCredentials();
	      
        BedrockRuntimeClient client = BedrockRuntimeClient.builder()
        		.region(Region.of(awsRegion))
                .credentialsProvider(StaticCredentialsProvider.create(awsCredentials))
                .build();  
		
		return client;
	}
	
	public static boolean checkPreferences() throws StorageException {
		if(getPreferences(ABAPAssistantConstants.PREFERENCES_AWS_PROFILE).equals("")
		   || getPreferences(ABAPAssistantConstants.PREFERENCES_AWS_REGION).equals("")
		   || getPreferences(ABAPAssistantConstants.PREFERENCES_MODEL_ID).equals("")) {
			return false;
		}
		return true;
	}
	
	private static MessageConsole findConsole(String name) {
		ConsolePlugin consolePlugin = ConsolePlugin.getDefault();
		IConsoleManager consoleManager = consolePlugin.getConsoleManager();
		IConsole[] existing = consoleManager.getConsoles();
		for (int i = 0; i < existing.length; i++)
			if (name.equals(existing[i].getName()))
				return (MessageConsole) existing[i];
		// Create a new console if one doesn't exist
		MessageConsole messageConsole = new MessageConsole(name, null);
		consoleManager.addConsoles(new IConsole[] { messageConsole });
		return messageConsole;
	}
	

}
