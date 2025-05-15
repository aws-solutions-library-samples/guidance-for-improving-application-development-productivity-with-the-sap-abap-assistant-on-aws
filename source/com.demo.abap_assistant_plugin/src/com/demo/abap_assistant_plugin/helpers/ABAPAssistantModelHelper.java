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

import org.eclipse.equinox.security.storage.StorageException;
import software.amazon.awssdk.services.bedrockruntime.BedrockRuntimeAsyncClient;
import software.amazon.awssdk.services.bedrockruntime.model.ContentBlock;
import software.amazon.awssdk.services.bedrockruntime.model.ConversationRole;
import software.amazon.awssdk.services.bedrockruntime.model.ConverseStreamResponseHandler;
import software.amazon.awssdk.services.bedrockruntime.model.Message;

public class ABAPAssistantModelHelper {
	
	
	// Invoke Amazon Bedrock Foundation Models	
	public static String invokeBedrockModels(String prompt, String modelID) throws Exception {
		
		BedrockRuntimeAsyncClient client =  ABAPAssistantHelper.getBedrockRuntimeAsyncClient();
		
		 Message message = Message.builder()
	             .content(ContentBlock.fromText(prompt))
	             .role(ConversationRole.USER)
	             .build();
	     
	     // Prepare a buffer to accumulate the generated response text.
	     StringBuilder completeResponseTextBuffer = new StringBuilder();

	     // Handler to extract the response text
	     ConverseStreamResponseHandler responseStreamHandler = ConverseStreamResponseHandler.builder()
	             .subscriber(ConverseStreamResponseHandler.Visitor.builder()
	                     .onContentBlockDelta(chunk -> {
	                         String responseText = chunk.delta().text();
	                         completeResponseTextBuffer.append(responseText);
	                     }).build()
	             ).build();
	     
         // Send the message with inference configuration and attach the handler.
         client.converseStream(request -> request
                 .modelId(modelID)
                 .messages(message)
                 .inferenceConfig(config -> {
					try {
						config
						         .maxTokens(Integer.parseInt(ABAPAssistantHelper.getPreferences(ABAPAssistantConstants.PREFERENCES_MAX_TOKENS).trim()))
						         .temperature(Float.parseFloat(ABAPAssistantHelper.getPreferences(ABAPAssistantConstants.PREFERENCES_TEMPERATURE).trim()))
						         .topP(Float.parseFloat(ABAPAssistantHelper.getPreferences(ABAPAssistantConstants.PREFERENCES_TOP_P).trim()));
					} catch (NumberFormatException | StorageException e) {
					}
				}

                 ), responseStreamHandler).get();
         
         // Return the complete response text.
         return completeResponseTextBuffer.toString();
		
	}
	
	// Custom logic - Functions for invoking other foundation models if required go here 
	
}
