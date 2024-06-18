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
import org.json.JSONException;
import org.json.JSONObject;

import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.services.bedrockruntime.BedrockRuntimeClient;
import software.amazon.awssdk.services.bedrockruntime.model.InvokeModelRequest;
import software.amazon.awssdk.services.bedrockruntime.model.InvokeModelResponse;

public class ABAPAssistantModelHelper {
	
	// Invoke Anthropic Claude 2 Models
	public static String invokeClaude2Models(String prompt, String modelID) throws JSONException, StorageException {
        
        BedrockRuntimeClient client =  ABAPAssistantHelper.getBedrockClient();
        
        String payload = new JSONObject().put("prompt", prompt)
				.put("temperature", 0)
				.put("top_k", 250)
				.put("top_p", 0.2)
				.put("max_tokens_to_sample", 4000).toString();

        InvokeModelRequest request = InvokeModelRequest.builder()
                .body(SdkBytes.fromUtf8String(payload))
                .modelId(modelID)
                .contentType("application/json")
                .accept("application/json")
                .build();

        InvokeModelResponse response = client.invokeModel(request);

        JSONObject responseBody = new JSONObject(response.body().asUtf8String());

        String result = responseBody.getString("completion");
        
        return result;

    }
	
	// Invoke Anthropic Claude 3 Models
	public static String invokeClaude3Models(String prompt, String modelID) throws JSONException, StorageException {
        
        BedrockRuntimeClient client =  ABAPAssistantHelper.getBedrockClient();
        
        String payload = new JSONObject()
                .put("anthropic_version", "bedrock-2023-05-31")
                .put("max_tokens", 4000)
				.put("temperature", 0.2)
				.put("top_k", 250)
				.put("top_p", 0.2)
                .append("messages", new JSONObject()
                        .put("role", "user")
                        .append("content", new JSONObject()
                                .put("type", "text")
                                .put("text", prompt)
                        )).toString();

        InvokeModelRequest request = InvokeModelRequest.builder()
                .body(SdkBytes.fromUtf8String(payload))
                .modelId(modelID)
                .contentType("application/json")
                .accept("application/json")
                .build();

        InvokeModelResponse response = client.invokeModel(request);

        JSONObject responseBody = new JSONObject(response.body().asUtf8String());
        
        String result = responseBody.getJSONArray("content").getJSONObject(0).getString("text");
       
        return result;

    }
	
	// Invoke AI21 Jurassic Model
	public static String invokeJurassicModels(String prompt, String modelID) throws JSONException, StorageException {
		
        BedrockRuntimeClient client =  ABAPAssistantHelper.getBedrockClient();

        String payload = new JSONObject().put("prompt", prompt)
				.put("temperature", 0)
				.put("topP", 0.2)
				.put("maxTokens", 8191).toString();

        InvokeModelRequest request = InvokeModelRequest.builder()
                .body(SdkBytes.fromUtf8String(payload))
                .modelId(modelID)
                .contentType("application/json")
                .accept("application/json")
                .build();

        InvokeModelResponse response = client.invokeModel(request);

        JSONObject responseBody = new JSONObject(response.body().asUtf8String());
        
        String result =  responseBody.getJSONArray("completions").getJSONObject(0).getJSONObject("data").getString("text");
        
        return result;

    }
	
	// Custom logic - Functions for invoking other foundation models go here 
	

	
}
