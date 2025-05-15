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
	public static final String PREFERENCES_PROMPT_CODE = "PROMPT-CODE";	
	public static final String PREFERENCES_PROMPT_DOC = "PROMPT-DOC";	
	public static final String PREFERENCES_IDENTITY_CENTER = "USE-IDENTITY-CENTER";
	public static final String PREFERENCES_ROLES_ANYWHERE = "USE-ROLES-ANYWHERE";
	public static final String PREFERENCES_PKCS_12_CERTIFICATE = "PKCS-12-CERTIFICATE";
	public static final String PREFERENCES_PKCS_12_KEY_ALIAS = "PKCS-12-KEY-ALIAS";
	public static final String PREFERENCES_PKCS_12_KEYSTORE_PASSWORD = "PKCS-12-KEYSTORE-PASSWORD";
	public static final String PREFERENCES_RLA_TRUST_ANCHOR = "RLA-TRUST-ANCHOR";
	public static final String PREFERENCES_RLA_PROFILE_ARN = "RLA-PROFILE-ARN";
	public static final String PREFERENCES_RLA_ROLE_ARN = "RLA-ROLE-ARN";
	public static final String PREFERENCES_TEMPERATURE = "TEMPERATURE";
	public static final String PREFERENCES_TOP_P = "TOP_P";
	public static final String PREFERENCES_MAX_TOKENS = "MAX-TOKENS";

	public static final String PREFERENCES_GENERAL_GROUP_NAME = "General configurations";
	public static final String PREFERENCES_AUTH_GROUP_NAME = "Authentication Options";
	public static final String PREFERENCES_AUTH_GROUP_TEXT = "Select the Authentication option and fill the corresponding fields";
	
	public static final String PREFERENCES_AWS_REGION_LINK_TEXT = "<a>Click to find AWS Region code</a>";
	public static final String PREFERENCES_AWS_REGION_LINK_URL = "https://docs.aws.amazon.com/global-infrastructure/latest/regions/aws-regions.html";

	public static final String PREFERENCES_MODEL_ID_LINK_TEXT = "<a>Click to find Bedrock Foundation Model ID</a>";
	public static final String PREFERENCES_MODEL_ID_LINK_URL = "https://docs.aws.amazon.com/bedrock/latest/userguide/models-supported.html";
	public static final String PREFERENCES_MODEL_CROSS_REGION_LINK_TEXT = "<a>Click to find cross region inference profiles</a>";
	public static final String PREFERENCES_MODEL_CROSS_REGION_LINK_URL = "https://docs.aws.amazon.com/bedrock/latest/userguide/inference-profiles-support.html#inference-profiles-support-system";
	
	public static final String PREFERENCES_AUTH_IDC_OPTION = "IAM Identity Center";
	public static final String PREFERENCES_AUTH_RLA_OPTION = "IAM Roles Anywhere";
	public static final String PREFERENCES_AWS_REGION_FIELD_NAME = "AWS Region";
	public static final String PREFERENCES_MODEL_ID_FIELD_NAME = "Model ID";
	public static final String PREFERENCES_AWS_PROFILE_FIELD_NAME = "AWS Profile";	
	public static final String PREFERENCES_PROMPT_CODE_FIELD_NAME = "Prompt for ABAP Code";	
	public static final String PREFERENCES_PROMPT_DOC_FIELD_NAME = "Prompt for Documentation";	
	public static final String PREFERENCES_PKCS_12_CERTIFICATE_FIELD_NAME = "PKCS12 Certificate		";
	public static final String PREFERENCES_PKCS_12_KEY_ALIAS_FIELD_NAME = "PKCS12 Key Alias";
	public static final String PREFERENCES_PKCS_12_KEYSTORE_PASSWORD_FIELD_NAME = "PKCS12 KeyStore Password";
	public static final String PREFERENCES_RLA_TRUST_ANCHOR_FIELD_NAME = "Trust Anchor";
	public static final String PREFERENCES_RLA_PROFILE_ANCHOR_ARN_FIELD_NAME = "Profile ARN";
	public static final String PREFERENCES_RLA_PROFILE_ROLE_ARN_FIELD_NAME = "Role ARN";
	public static final String PREFERENCES_TEMPERATURE_FIELD_NAME = "Temperature";
	public static final String PREFERENCES_TOP_P_FIELD_NAME = "Top P";
	public static final String PREFERENCES_MAX_TOKENS_FIELD_NAME = "Maximum length";


	// Eclipse preferences Default constants
	public static final String PREFERENCES_AWS_REGION_DEFAULT = "us-east-1";
	public static final String PREFERENCES_MODEL_ID_DEFAULT = "anthropic.claude-v2";
	public static final String PREFERENCES_AWS_PROFILE_DEFAULT = "abap-assistant";

	// Model ID Constants
	public static final String CLAUDE_V2_MODEL_ID = "anthropic.claude-v2";
	public static final String CLAUDE_V2_1_MODEL_ID = "anthropic.claude-v2:1";
	public static final String CLAUDE_3_SONNET_MODEL_ID = "anthropic.claude-3-sonnet-20240229-v1:0";
	public static final String CLAUDE_3_HAIKU_MODEL_ID= "anthropic.claude-3-haiku-20240307-v1:0";
	public static final String CLAUDE_3_5_SONNET_MODEL_ID = "anthropic.claude-3-5-sonnet-20240620-v1:0";
	public static final String CLAUDE_3_5_SONNET_V2_MODEL_ID = "anthropic.claude-3-5-sonnet-20241022-v2:0";
	public static final String CLAUDE_3_OPUS_MODEL_ID = "anthropic.claude-3-opus-20240229-v1:0";
	public static final String CLAUDE_3_5_HAIKU_MODEL_ID = "anthropic.claude-3-5-haiku-20241022-v1:0";
	public static final String CLAUDE_3_7_MODEL_ID = "anthropic.claude-3-7-sonnet-20250219-v1:0";
	public static final String META_LLAMA_3_1_405B = "meta.llama3-1-405b-instruct-v1:0";	

	// Custom logic - Additional Model ID constant definitions go here

}
