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

public class ABAPAssistantConstants {
	
	//Eclipse constants
	public static final String ECLIPSE_DIALOG_TITLE = "ABAP Assistant";
	public static final String ECLIPSE_CONSOLE = "ABAP_ASSISTANT_CONSOLE";
	
	// Prompts
	public static final String PROMPT_ABAP_CODE = "You are an ABAP Developer writing ABAP code in SAP S/4HANA system.Ensure the code is syntactically correct, bug-free, and optimized. In your response provide only the ABAP code with no additional comments. Do not provide any summary at the beginning and at the end.\n";
	public static final String PROMPT_ABAP_DOC = "Provide a step by step documentation for the following ABAP code.\n";

	// Eclipse preferences constants
	public static final String PREFERENCES_DESCCRIPTION = "Settings used when running SAP ABAP Assistant powered by Amazon Bedrock.\nChoose \"Restore Defaults\" to load default values";
	public static final String PREFERENCES_AWS_REGION = "AWS-REGION";
	public static final String PREFERENCES_MODEL_ID = "MODEL-ID";
	public static final String PREFERENCES_AWS_PROFILE = "AWS-PROFILE";	

	// Eclipse preferences Default constants
	public static final String PREFERENCES_AWS_REGION_DEFAULT = "us-east-1";
	public static final String PREFERENCES_MODEL_ID_DEFAULT = "anthropic.claude-v2";
	public static final String PREFERENCES_AWS_PROFILE_DEFAULT = "abap-assistant";
	
	// Model ID Constants
	public static final String CLAUDE_MODEL_ID_V2 = "anthropic.claude-v2";
	public static final String CLAUDE_MODEL_ID_V2_1 = "anthropic.claude-v2:1";
	public static final String CLAUDE3_MODEL_ID_SONNET = "anthropic.claude-3-sonnet-20240229-v1:0";
	public static final String CLAUDE3_MODEL_ID_HAIKU= "anthropic.claude-3-haiku-20240307-v1:0";
	public static final String JURASSIC_MODEL_ID_MID = "ai21.j2-mid-v1";
	public static final String JURASSIC_MODEL_ID_ULTRA = "ai21.j2-ultra-v1";
	
	// Custom logic - Additional Model ID constant definitions go here
	
	

}
