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

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.security.MessageDigest;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.cert.X509Certificate;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

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
import org.json.JSONObject;

import com.demo.abap_assistant_plugin.helpers.SecureKeyManager.CertificateKeyPair;

import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.AwsSessionCredentials;
import software.amazon.awssdk.auth.credentials.ProfileProviderCredentialsContext;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.profiles.Profile;
import software.amazon.awssdk.profiles.ProfileFile;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.bedrock.BedrockClient;
import software.amazon.awssdk.services.bedrock.model.InferenceProfileSummary;
import software.amazon.awssdk.services.bedrock.model.ListInferenceProfilesRequest;
import software.amazon.awssdk.services.bedrock.model.ListInferenceProfilesResponse;
import software.amazon.awssdk.services.bedrockruntime.BedrockRuntimeAsyncClient;
import software.amazon.awssdk.services.sso.auth.SsoProfileCredentialsProviderFactory;

public class ABAPAssistantHelper {
	
	private static final String IAM_IDENTITY_CENTER = "IDC";
	private static final String IAM_ROLES_ANYWHERE = "RLA";

	
	public static String getPreferences (String prefName) throws StorageException {		
		ISecurePreferences prefs = SecurePreferencesFactory.getDefault();
		String prefvalue = "";
		prefvalue = prefs.get(prefName, "").trim(); 
		return prefvalue;	
	}
	
	public static Boolean getPreferencesBoolean (String prefName) throws StorageException {
		ISecurePreferences prefs = SecurePreferencesFactory.getDefault();
		Boolean prefvalue = false;
		prefvalue = prefs.getBoolean(prefName, false);
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

	public static BedrockRuntimeAsyncClient getBedrockRuntimeAsyncClient () throws Exception  {
	    AwsCredentialsProvider credentialsProvider = getCredentialsProvider();
	    return BedrockRuntimeAsyncClient.builder()
	            .region(Region.of(getAwsRegion()))
	            .credentialsProvider(credentialsProvider)
	            .build();
	}
	
	
	public static BedrockClient getBedrockClient () throws Exception {
	    AwsCredentialsProvider credentialsProvider = getCredentialsProvider();
	    return BedrockClient.builder()
        		.region(Region.of(getAwsRegion()))
                .credentialsProvider(credentialsProvider)
                .build();  
	}

	private static String getAwsRegion() throws StorageException {
	    return ABAPAssistantHelper.getPreferences(ABAPAssistantConstants.PREFERENCES_AWS_REGION)
	            .toLowerCase();
	}
	
	public static String getAuthenticationOption() throws StorageException {
	    return Boolean.TRUE.equals(ABAPAssistantHelper.getPreferencesBoolean(ABAPAssistantConstants.PREFERENCES_IDENTITY_CENTER)) ? IAM_IDENTITY_CENTER : 
	           Boolean.TRUE.equals(ABAPAssistantHelper.getPreferencesBoolean(ABAPAssistantConstants.PREFERENCES_ROLES_ANYWHERE)) ? IAM_ROLES_ANYWHERE : IAM_IDENTITY_CENTER;
	}
	
	private static AwsCredentialsProvider getCredentialsProvider() throws Exception  {
	    String authenticationOption = getAuthenticationOption();
	    
	    switch (authenticationOption.toUpperCase()) {
	        case IAM_IDENTITY_CENTER:
	            String awsProfile = ABAPAssistantHelper.getPreferences(ABAPAssistantConstants.PREFERENCES_AWS_PROFILE);
	            return StaticCredentialsProvider.create(getSSOCredentials(awsProfile));
	        case IAM_ROLES_ANYWHERE :
	            return StaticCredentialsProvider.create(getIAMRolesAnywhereCredentials());
	        default:
	            throw new IllegalArgumentException("Unsupported authentication option: " + authenticationOption);
	    }
	}
	
	private static AwsSessionCredentials getSSOCredentials (String awsProfile) throws StorageException {
		ProfileFile profileFile = ProfileFile.defaultProfileFile();
		Optional<Profile> profile = profileFile.getSection(ProfileFile.PROFILES_SECTION_TITLE, awsProfile);
		ProfileProviderCredentialsContext profileProvider = ProfileProviderCredentialsContext.builder()
					.profile(profile.get())
					.profileFile(profileFile).build();
		AwsSessionCredentials awsCredentials = (AwsSessionCredentials) new SsoProfileCredentialsProviderFactory()
					.create(profileProvider)
					.resolveCredentials();
		return awsCredentials;
	}
	
	public static AwsSessionCredentials getIAMRolesAnywhereCredentials() throws Exception  {
				
    	String rlaTrustAnchor = ABAPAssistantHelper.getPreferences(ABAPAssistantConstants.PREFERENCES_RLA_TRUST_ANCHOR).trim();
    	String rlaProfileArn = ABAPAssistantHelper.getPreferences(ABAPAssistantConstants.PREFERENCES_RLA_PROFILE_ARN).trim();
    	String rlaRoleArn = ABAPAssistantHelper.getPreferences(ABAPAssistantConstants.PREFERENCES_RLA_ROLE_ARN).trim();
 
		String awsRegion = ABAPAssistantHelper.getPreferences(ABAPAssistantConstants.PREFERENCES_AWS_REGION).toLowerCase();
        String sessionName = "assume_role_session";
        int durationSeconds = 900;
        
        // Request values
        String method = "POST";
        String service = "rolesanywhere";
        String host = service + "." + awsRegion + ".amazonaws.com";
        String endpoint = "https://" + host;
        String contentType = "application/json";
        
        // Create date for headers
        ZonedDateTime now = ZonedDateTime.now(ZoneOffset.UTC);
        String amzDate = now.format(DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss'Z'"));
        String dateStamp = now.format(DateTimeFormatter.ofPattern("yyyyMMdd"));

        // Generate payload
        JSONObject payloadJson = new JSONObject();
        payloadJson.put("durationSeconds", durationSeconds);
        payloadJson.put("profileArn", rlaProfileArn);
        payloadJson.put("roleArn", rlaRoleArn);
        payloadJson.put("sessionName", sessionName);
        payloadJson.put("trustAnchorArn", rlaTrustAnchor);
        String payload = payloadJson.toString();
        
        // Retrieve the certificate and private key from .p12
        SecureKeyManager keyManager = new SecureKeyManager();
        CertificateKeyPair certKeyPair = keyManager.getCertificateAndKey();
        
        X509Certificate cert = certKeyPair.getCertificate();
        PrivateKey privateKey = certKeyPair.getPrivateKey();

        String amzX509 = Base64.getEncoder().encodeToString(cert.getEncoded());
        
        // Get certificate serial number
        String caSerialNumber = cert.getSerialNumber().toString();

        // Create canonical request
        String canonicalUri = "/sessions";
        String canonicalQuerystring = "";
        String canonicalHeaders = "content-type:" + contentType + "\n" +
                                "host:" + host + "\n" +
                                "x-amz-date:" + amzDate + "\n" +
                                "x-amz-x509:" + amzX509 + "\n";

        String signedHeaders = "content-type;host;x-amz-date;x-amz-x509";

        // Create payload hash
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        String payloadHash = bytesToHex(md.digest(payload.getBytes()));

        // Combine elements to create canonical request
        String canonicalRequest = method + "\n" + canonicalUri + "\n" + canonicalQuerystring + "\n" +
                                canonicalHeaders + "\n" + signedHeaders + "\n" + payloadHash;

        // Create string to sign
        String algorithm = "AWS4-X509-RSA-SHA256";
        String credentialScope = dateStamp + "/" + awsRegion + "/" + service + "/aws4_request";
        String stringToSign = algorithm + "\n" + amzDate + "\n" + credentialScope + "\n" +
                            bytesToHex(md.digest(canonicalRequest.getBytes()));

        // Calculate signature
        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initSign(privateKey);
        signature.update(stringToSign.getBytes());
        String signatureHex = bytesToHex(signature.sign());

        // Create authorization header
        String authorizationHeader = algorithm + " Credential=" + caSerialNumber + "/" + credentialScope +
                                   ", SignedHeaders=" + signedHeaders + ", Signature=" + signatureHex;

        // Create HTTP client and send request
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(endpoint + canonicalUri))
            .header("Content-Type", contentType)
            .header("X-Amz-Date", amzDate)
            .header("X-Amz-X509", amzX509)
            .header("Authorization", authorizationHeader)
            .POST(HttpRequest.BodyPublishers.ofString(payload))
            .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                
        JSONObject responseJson = new JSONObject(response.body());

        String accessKey = responseJson.getJSONArray("credentialSet")
            .getJSONObject(0)
            .getJSONObject("credentials")
            .getString("accessKeyId");
        String secretKey = responseJson.getJSONArray("credentialSet")
            .getJSONObject(0)
            .getJSONObject("credentials")
            .getString("secretAccessKey");
        String sessionToken = responseJson.getJSONArray("credentialSet")
            .getJSONObject(0)
            .getJSONObject("credentials")
            .getString("sessionToken");
        
        AwsSessionCredentials awsCredentials = AwsSessionCredentials.create(
        		accessKey,
        		secretKey,
            sessionToken
        );
		
        return awsCredentials;
    	
	}
	
    private static String bytesToHex(byte[] bytes) {
        StringBuilder result = new StringBuilder();
        for (byte b : bytes) {
            result.append(String.format("%02x", b));
        }
        return result.toString();
    }

	
	public static boolean checkPreferences() throws StorageException {
	    String authenticationOption = getAuthenticationOption();
	    
	    // Check authentication-specific preferences
	    if (!checkAuthenticationPreferences(authenticationOption)) {
	        return false;
	    }

	    return true;
	}

	private static boolean checkAuthenticationPreferences(String authOption) throws StorageException {
	    // Common preferences required for all authentication methods
	    Set<String> commonPreferences = new HashSet<>(Arrays.asList(
	        ABAPAssistantConstants.PREFERENCES_AWS_REGION,
	        ABAPAssistantConstants.PREFERENCES_MODEL_ID,
	        ABAPAssistantConstants.PREFERENCES_PROMPT_CODE,
	        ABAPAssistantConstants.PREFERENCES_PROMPT_DOC,
	        ABAPAssistantConstants.PREFERENCES_TEMPERATURE,
	        ABAPAssistantConstants.PREFERENCES_TOP_P,
	        ABAPAssistantConstants.PREFERENCES_MAX_TOKENS
	    ));

	    // Check authentication-specific preferences
	    if (authOption.equalsIgnoreCase(IAM_IDENTITY_CENTER)) {
	        commonPreferences.add(ABAPAssistantConstants.PREFERENCES_AWS_PROFILE);
	    } else if (authOption.equalsIgnoreCase(IAM_ROLES_ANYWHERE)) {
	        commonPreferences.addAll(Arrays.asList(
	        	ABAPAssistantConstants.PREFERENCES_PKCS_12_CERTIFICATE,
	        	ABAPAssistantConstants.PREFERENCES_PKCS_12_KEY_ALIAS,
	        	ABAPAssistantConstants.PREFERENCES_PKCS_12_KEYSTORE_PASSWORD,
	        	ABAPAssistantConstants.PREFERENCES_RLA_TRUST_ANCHOR,
	        	ABAPAssistantConstants.PREFERENCES_RLA_PROFILE_ARN,
	        	ABAPAssistantConstants.PREFERENCES_RLA_ROLE_ARN
	        ));
	    }

	    return arePreferencesNotEmpty(commonPreferences);
	}


	private static boolean arePreferencesNotEmpty(Set<String> preferences) throws StorageException {
	    for (String preference : preferences) {
	        if (getPreferences(preference).equals("")) {
	            return false;
	        }
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
	
	public static boolean isModelSupported(String modelID) throws Exception {
	    List<String> modelsList = List.of(
	    		ABAPAssistantConstants.CLAUDE_V2_MODEL_ID,
	    		ABAPAssistantConstants.CLAUDE_V2_1_MODEL_ID,
	    		ABAPAssistantConstants.CLAUDE_3_SONNET_MODEL_ID,
	    		ABAPAssistantConstants.CLAUDE_3_HAIKU_MODEL_ID,
	    		ABAPAssistantConstants.CLAUDE_3_5_SONNET_MODEL_ID,
	    		ABAPAssistantConstants.CLAUDE_3_5_SONNET_V2_MODEL_ID,
	    		ABAPAssistantConstants.CLAUDE_3_OPUS_MODEL_ID,
	    		ABAPAssistantConstants.CLAUDE_3_5_HAIKU_MODEL_ID,
	    		ABAPAssistantConstants.CLAUDE_3_7_MODEL_ID,
	    		ABAPAssistantConstants.META_LLAMA_3_1_405B
	     );
	    
	    if(modelsList.contains(modelID)) {
			return true;
		} else {
			//case of inference profile model ID
		    String modelToCheck = modelID.substring(modelID.indexOf('.') + 1);
	        if (modelsList.contains(modelToCheck)) {
	        	BedrockClient bedrockClient =  ABAPAssistantHelper.getBedrockClient();

	    		ListInferenceProfilesRequest listRequest = ListInferenceProfilesRequest.builder().build();
	    		ListInferenceProfilesResponse listResponse = bedrockClient.listInferenceProfiles(listRequest);
	            List<InferenceProfileSummary> profileSummaries = listResponse.inferenceProfileSummaries();
	            
	            for (InferenceProfileSummary summary : profileSummaries) {
	                if (summary.models().stream().anyMatch(model -> model.modelArn().contains(modelToCheck))) {
	                	String inferenceModelProfileId = summary.inferenceProfileId();
	                	if(inferenceModelProfileId.equals(modelID)) {
	                		return true;
	                	} else {
	                		return false;
	                	}
	                }
	            }
	        } else {
	        	return false;
	        }
	        
		}
		return false;
	}	
	

}
