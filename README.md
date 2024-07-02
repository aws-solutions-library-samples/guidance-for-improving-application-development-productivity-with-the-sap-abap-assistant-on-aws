# Guidance for Improving Application Development Productivity with the SAP ABAP Assistant on AWS

## Table of Content 

1. [Overview](#overview)
    - [Architecture](#architecture)
    - [Cost](#cost)
2. [Prerequisites](#prerequisites)
    - [Operating System](#operating-system)
    - [Third-party tools](#third-party-tools)
3. [Deployment Steps](#deployment-steps)
4. [Deployment Validation](#deployment-validation)
5. [Setup](#setup)
    - [Authentication](#authentication)
    - [Plugin Installation](#plugin-installation)
6. [Running the Guidance](#running-the-guidance)
7. [Next Steps](#next-steps)
8. [Cleanup](#cleanup)
9. [Notices](#notices)
10. [Authors](#authors)

## Overview

SAP customers are embarking on digital transformation projects, including RISE with SAP. To accelerate and de-risk transformation projects, customers want to understand ABAP programs in their SAP systems to assess the impact on business processes. Understanding existing custom code can be challenging for several reasons: 1/ lack of quality documentation for ABAP programs, 2/ unavailability of resources that built the custom code, and 3/ lack of resources needed to review and understand the legacy custom code. Also, ABAP developers spend significant time searching the existing codebase and forums for code snippets to incorporate into the ABAP programs. These challenges reduce  productivity and impact project deadlines.

With the SAP ABAP Assistant using Amazon Bedrock, SAP customers can accelerate delivery of new ABAP code by generating code snippets with natural language prompts, and generate documentation for custom and standard ABAP programs, improving productivity and accelerating transformation.

This sample code consists of the SAP ABAP Assistant Eclipse plugin release and its source code.

### Architecture

![Architecture of SAP ABAP Assistant](/assets/images/Architecture.png)

1. **Install AWS Command Line Interface (AWS CLI) v2:** SAP ABAP Developer installs AWS CLI v2 in Windows or Mac and configures the AWS CLI to authenticate with the AWS IAM Identity Center using `aws configure sso` command.

2. **Authentication:** SAP ABAP Developer authenticates with the AWS IAM Identity Center using the AWS CLI with `aws sso login --profile <profile-name>` command.

3. **Install SAP ABAP Assistant Plugin:** SAP ABAP developer downloads, installs and configures the SAP ABAP Assistant plugin in Eclipse Integrated Development Environment (IDE) .

4. **Invoke SAP ABAP Assistant:** SAP ABAP Developer authenticates and connects one or more SAP system hosted in AWS VPC or on-premises or SAP Business Technology Platform (BTP) ABAP environment to Eclipse IDE. 
The developer opens an ABAP program in Eclipse IDE, and selects a block of ABAP code for which the documentation has to be generated. For code generation, the developer writes prompt in simple English in the ABAP program. The developer invokes SAP ABAP Assistant plugin using “Ask Bedrock” menu in Eclipse IDE.

5. **Invoke Foundation Model:** The SAP ABAP Assistant plugin sends a request with the selected prompts or ABAP code to Amazon Bedrock to call large language models (LLMs) hosted by Amazon Bedrock, such as the Anthropic’s Claude model to produce ABAP code or documentation.

6. **Response Generation:** The SAP ABAP Assistant returns the response back to the ABAP editor in case of code generation or displays documentation in a console in Eclipse IDE. The developer will validate the generated code and if required, modify it to the specific use case. 

### Cost

_We recommend creating a [Budget](https://docs.aws.amazon.com/cost-management/latest/userguide/budgets-managing-costs.html) through [AWS Cost Explorer](https://aws.amazon.com/aws-cost-management/aws-cost-explorer/) to help manage costs. Prices are subject to change. For full details, refer to the pricing webpage for each AWS service used in this Guidance._

### Sample Cost Table

The following table provides a sample cost breakdown for deploying this Guidance with the default parameters in the US East (N. Virginia) Region


| AWS service  | Price per 1,000 input tokens [USD] | Price per 1,000 output tokens [USD] |
| ------------ | ----------------------------------| --------------------------------- |
| Amazon Bedrock - Claude 3 Sonnet | $ 0.003 | $ 0.015 |

## Prerequisites

### Operating System
You can use SAP ABAP Assistant plugin in **Eclipse IDE** on **MacOS** or **Windows** Operating System

### Third-party tools

1. Install [Eclipse IDE](https://www.eclipse.org/downloads/packages/) with version 2023-09 (4.29.0) and above
2. Install [ABAP Development tools for Eclipse](https://tools.hana.ondemand.com/#abap)
3. Create an [ABAP project in Eclipse IDE](https://help.sap.com/docs/abap-cloud/abap-development-tools-user-guide/abap-project?version=sap_btp) to connect with the ABAP system

### AWS account requirements

For this guidance , we will be using the `us-east-1` region.

1. Enable [AWS IAM Identity Center](https://docs.aws.amazon.com/singlesignon/latest/userguide/get-set-up-for-idc.html) with AWS Organizations. Make a note of the **Instance ARN** and **AWS Access portal URL** from the **Settings** page of IAM Identity Center.

![Settings page of IAM Identity Center](/assets/images/prerequisites/IAM_Identity_Center_Settings.png)

2. [Access to Amazon Bedrock foundation model](https://docs.aws.amazon.com/bedrock/latest/userguide/model-access.html): Request access to Anthropic Claude 3 Sonnet foundation model from the Amazon Bedrock console in your AWS account. Alternatively, you can use Anthropic Calude 3 Haiku, Anthropic Claude v2, Anthropic Claude v2.1, Jurassic-2 Mid or Jurassic-2 Ultra foundation models.

### AWS Command Line Interface (AWS CLI)

Install [AWS Command Line Interface v2](https://docs.aws.amazon.com/cli/latest/userguide/getting-started-install.html) . To confirm the installation, open your preferred terminal and enter `aws --version` command 

```
C:\> aws --version
aws-cli/2.15.30 Python/3.11.6 Windows/10 exe/AMD64 prompt/off
```
## Deployment Steps

1. Clone the GitHub repository to access the AWS CloudFormation deployment template.
```
git clone https://github.com/aws-solutions-library-samples/guidance-for-improving-application-development-productivity-with-the-sap-abap-assistant-on-aws.git
cd ./guidance-for-improving-application-development-productivity-with-the-sap-abap-assistant-on-aws
```

2. Deploy the AWS CloudFormation Stack
This guidance utilizes the `AdministratorAccess` role for deployment. For use in a production environment, refer to the [security best practices](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html) in the AWS Identity and Access Management (IAM) documentation and modify the IAM roles as needed.

* Sign in to the [AWS CloudFormation console](https://console.aws.amazon.com/cloudformation/home)
* Create Stack > Upload the `guidance-for-improving-application-development-productivity-with-the-sap-abap-assistant-on-aws/deployment/prereq-sap-abap-assistant-on-aws.yml` file
* Input the following values:
    * Enter `sap-abap-assistant` in the stack name
    * For **InstanceArn** parameter, enter the Instance ARN of AWS IAM Identity Center
    * The **PermissionSetName** parameter is populated with the value `ABAPAssistantAccess`
    * The **SessionDuration** parameter is populated with the value `PT8H`

* Deploy the CloudFormation stack

## Deployment Validation

* Open the AWS CloudFormation console and verify the status of the stack deployment with the name starting with `sap-abap-assistant`.
* If deployment is successful, you should see a permission set with the name `ABAPAssistantAccess` in the AWS IAM Identity Center.
* [Optional] - You can see the detailed output in the AWS CloudFormation Stack `sap-abap-assistant` using below AWS CLI command.
    ```
    aws cloudformation describe-stacks --stack-name sap-abap-assistant --query 'Stacks[0].Outputs' --output table --no-cli-pager
    ```

## Setup

1. As per the [identity source](https://docs.aws.amazon.com/singlesignon/latest/userguide/manage-your-identity-source.html) configured in your IAM Identitiy Cetner, [assign permission set](https://docs.aws.amazon.com/singlesignon/latest/userguide/set-up-single-sign-on-access-to-accounts.html) created above to the user or group in IAM Identity Center.

### Authentication
1. Open your preferred terminal in MacOS or Windows Operating System to run the AWS CLI commands

2. Enter the command `aws configure sso` to create the SSO token provider configuration.

     ```
    $ aws configure sso
    SSO session name (Recommended): abap-assistant-sso
    SSO start URL [None]: https://enter-your-sso-start-url.awsapps.com/start
     SSO region [None]: us-east-1
    SSO registration scopes [None]: sso:account:access
    ```
3. The AWS CLI attempts to open your default browser and begin the login process for your IAM Identity Center account.

4. The AWS CLI displays the AWS accounts available for you to use. Use the arrow keys to select the account you want to use. If you are authorized to use only one account, the AWS CLI selects that account for you automatically and skips the prompt.
    ```
    There are 2 AWS accounts available to you.
    > DeveloperAccount, developer-account-admin@example.com (123456789011) 
    ProductionAccount, production-account-admin@example.com (123456789022)
    ```
5. The AWS CLI confirms your account choice, and displays the IAM roles that are available to you in the selected account. Use the arrow keys to select the IAM role `ABAPAssistantAccess` and press \<ENTER\>. If the selected account lists only one role, the AWS CLI selects that role for you automatically and skips the prompt.
    ```
    Using the account ID 123456789011
    There are 2 roles available to you.
    > ReadOnly
    ABAPAssistantAccess
    ```
6. Specify the default output format, the default AWS Region to send commands to, and provide a name as `abap-assistant` for the profile so you can reference this profile from Eclipse plugin.

    ```
    CLI default client Region [None]: us-east-1
    CLI default output format [None]: json
    CLI profile name [ABAPAssistantAccess-123456789011]: abap-assistant
     ```

7. A final message describes the completed profile configuration.

    ```
    To use this profile, specify the profile name using --profile, as shown:

    aws s3 ls --profile abap-assistant
     ```
8. This results in creating the sso-session section with the name `abap-assistant-sso` and named profile with the name `abap-assistant` in the credentials file located at ~/.aws/credentials on macOS, or at C:\Users\USERNAME\.aws\credentials on Windows.

     ```
    [profile abap-assistant]
    sso_session = abap-assistant-sso
    sso_account_id = 123456789011
    sso_role_name = ABAPAssistantAccess
    region = us-east-1
    output = json
                
    [sso-session abap-assistant-sso]
    sso_start_url = https://enter-your-sso-start-url.awsapps.com/start
    sso_region = us-east-1
    so_registration_scopes = sso:account:access
    ``` 

9. To reauthenticate after the session expiry, you need to use the following command.
    ```
    aws sso login --profile abap-assistant
    ```

### Plugin Installation
1. From the main page of the GitHub repository, click **Releases** . From the latest listed releae, download the SAP ABAP Assistant plugin zip file (for e.g. sap-abap-assistant-V1_0_17.zip) from the **Assets** section. Save the plugin zip file in your local file system.

2. Open Eclipse IDE and choose **Help -> Install New Software**. Choose the **Add…** button. In the next dialog window, choose **Archive…** and select the zip file that you downloaded. Choose **Add**.

    ![Eclipse Installation Wizard](assets/images/plugin-installation/Eclipse_Installation_Wizard.png)

3. Uncheck the option **Group items by category**. You will see SAP ABAP Assistant plugin listed along with the version number Select the plugin and choose **Next**

    ![SAP ABAP Assistant in Eclipse Installation Wizard](assets/images/plugin-installation/SAP_ABAP_Assistant_in_Eclipse_Installation_Wizard.png)

4. In the subsequent windows, choose **Next**, and accept the terms of the license agreement and choose **Finish**. Eclipse will start the installation of plugin. You can see the status of installation at the bottom right corner of the Eclipse IDE. Eclipse will prompt to trust the artifacts. Choose **Trust Selected**. 

    ![Trust Artifacts in Eclipse](assets/images/plugin-installation/Trust_Artifacts_in_Eclipse.png)

5. You will then see a dialog box asking you to restart Eclipse. Choose **Restart Now**.
After the Eclipse has restarted, you can see a menu bar at the top called **Ask Bedrock** with two submenus – **ABAP Code Assistant** and **ABAP Documentation Assistant**

    ![SAP ABAP Assistant Menu in Eclipse](assets/images/plugin-installation/SAP_ABAP_Assistant_Menu_in_Eclipse.png)

### Plugin Configuration
Before using ABAP Assistant plugin , you need to set up the plugin preferences in the Eclipse IDE. Choose **Windows -> Preferences** (on Windows), or **Eclipse -> Settings** (on MacOS) to bring up the preferences dialog page of Eclipse. In the left pane , choose **SAP ABAP Assistant**. Input the following settings :

* **AWS Region** – Enter `us-east-1`. Refer this [documentation](https://docs.aws.amazon.com/AWSEC2/latest/UserGuide/using-regions-availability-zones.html#concepts-available-regions) for list of AWS region codes. 

* **Model ID** – Enter `anthropic.claude-3-sonnet-20240229-v1:0`. The plugin currently supports Anthropic Claude (v2, v2.1, Claude 3 Sonnet and Claude 3 Haiku) and Jurassic-2 (mid and ultra) models . Refer [this documentation](https://docs.aws.amazon.com/bedrock/latest/userguide/model-ids.html) for list of Amazon bedrock Model IDs. 

* **AWS Profile** – Enter the name of the AWS profile `abap-assistant` that you created

The following screenshot shows the SAP ABAP Assistant plugin preferences page.

![SAP ABAP Assistant Preferences Page](assets/images/plugin-installation/SAP_ABAP_Assistant_Preferences_Page.png)

## Running the Guidance

### ABAP Code Assistant
In the ABAP perspective of Eclipse, open an existing ABAP program or create a new ABAP program. Enter the below prompts to generate ABAP code to retreive and display the material data from table MARA present in SAP S/4HANA system.
```
* Define a structure ty_material with fields MATNR MTART BRGEW GEWEI
* Define a table lt_material from ty_material
* Select MATNR MTART BRGEW GEWEI from table MARA into lt_material
* Display data in ALV format using CL_SALV_TABLE with appropriate parameters

```
In the Eclipse editor, select the lines of written text using your cursor and then select **Ask Bedrock -> ABAP Code Assistant** 

![Selecting the ABAP Code Assistant menu in eclipse to generate the ABAP Code](assets/images/running-plugin/ABAP_Code_Generation.png)

The ABAP assistant plugin will generate ABAP code after the last line of comment. You will see the generated ABAP code resembles the example in the following screenshot. 

![ABAP Code generated by SAP ABAP Assistant](assets/images/running-plugin/ABAP_Code_Generated.png)

### ABAP Documentation Assistant
To generate the documentation, open the ABAP program `RH_GET_ADDRESS` in Eclipse and select all the lines of ABAP code. Then select **Ask Bedrock -> ABAP Documentation Assistant**. 

![Selecting the ABAP Documentation Assistant menu in eclipse to generate the ABAP documentation](assets/images/running-plugin/ABAP_Documentation_Generation.png)

You can see the ABAP documentation for the selected lines of ABAP code in a console named **BEROCK_ABAP_CONSOLE** at the bottom of the eclipse IDE. You will see the documentation that resembles the example in the following screenshot.

![ABAP Documentation generated by SAP ABAP Assistant](assets/images/running-plugin/ABAP_Documentation_Generated.png)

## Next Steps

You can modify the source code as per your requirements and generate a new version of the plugin. For example, you can add support to an additional foundation model (for e.g. Cohere Command). You can create a custom model by training a foundation model using Amazon Bedrock and then input the custom model ID in the ABAP Assistant plugin preferences in Eclipse.

1. Clone the GitHub repository to access the code. 
```
git clone https://github.com/aws-solutions-library-samples/guidance-for-improving-application-development-productivity-with-the-sap-abap-assistant-on-aws.git
cd ./guidance-for-improving-application-development-productivity-with-the-sap-abap-assistant-on-aws
```

2. Download and install **[Eclipse IDE for Eclipse Committers](https://www.eclipse.org/downloads/packages/)** with version 2023-09 and above.

3. Import the source code in Eclipse IDE for Eclipse Committers. Choose `File -> Import -> Maven -> Existing Maven projects`. 

    ![Import wizard in Eclipse IDE for Eclipse Committers](assets/images/next-steps/import-wizard.png)

4. Browser to the `source` folder which has the downloaded source code. Choose **Finish**. 

    ![Import soure code in Eclipse IDE for Eclipse Committers](assets/images/next-steps/source-code-import.png)

5. The imported projects can be seen in the Package Explorer view in Eclipse IDE for Eclipse Committers.

    ![Imported projects in Eclipse IDE for Eclipse Committers](assets/images/next-steps/maven-imported-projects.png)

6. Make the required code changes as per your requirements in the project `com.demo.abap_assistant_plugin`. 

7. Update the Plugin version in `com.demo.abap_assistant_plugin -> plugin.xml` and in `com.demo.abap_assistant_feature -> feature.xml`

    ![update version number in plugin.xml file of plugin project](assets/images/next-steps/update-version-plugin-project.png)

    ![update version number in feature.xml file of feature project](assets/images/next-steps/update-version-feature-project.png)

8. To generate a new version, right click on  `com.demo.abap_assistant_plugin.releng -> Run As -> Maven Build`. In the **Goals** enter `tycho-versions:update-pom` and Choose **Run**.

    ![Maven command to update the version](assets/images/next-steps/maven-update-pom.png)

9. Right click on `com.demo.abap_assistant_plugin.releng -> Run As -> Maven Build`. In the **Goals** enter `clean install` and  choose **Run**.

    ![Maven clean install command](assets/images/next-steps/maven-clean-install.png)

    You should see a log resembling the below screenshot in the console 

    ![Maven Build log](assets/images/next-steps/maven-log.png)

10. The plugin will be generated in `com.demo.abap_assistant_p2 -> target -> repository` folder.

    ![repository folder where plugin will be generated](assets/images/next-steps/plugin-folder.png)

11. You can install or update to the new version of the plugin in by selecting **Help -> Install New Software** in Eclipse and pointing to the `repository` folder. Alternatively, you can zip the contents under `repository` folder and point to the zip file.

    ![Installing new version of SAP ABAP Assistant plugin](assets/images/next-steps/install-new-plugin-version.png)

## Cleanup

### Uninstall SAP ABAP Assistant Plugin
If you want to unisntall the ABAP Assitant from Eclipse, proceed with the below steps:

1. In Eclipse IDE, navigate to **Help -> About Eclipse IDE** and then choose **Installation Details** to list the installed softwares.

    ![About Eclipse option in Eclipse IDE](assets/images/cleanup/about-eclipse.png)

2. Select **ABAP Assistant** from the list, and then choose **Uninstall**

    ![ABAP Assistant Uninstall option](assets/images/cleanup/uninstall-abap-assistant.png)

3. In the subsequent screen, choose **Finish** to unisntall the ABAP Asssistant Plugin. You will then see a dialog box asking you to restart Eclipse. Choose **Restart Now**. 
    ![Uninstalling ABAP Assistant plugin](assets/images/cleanup/unisntall-confirmation.png)

### AWS resources
1. Remove the permission set `ABAPAssistantAccess` from all AWS accounts that use the permission set. Refer this [link](https://docs.aws.amazon.com/singlesignon/latest/userguide/howtoremovepermissionset.html) for the steps to be followed.

2.  You can delete the permission set manually or by deleting the entire AWS CloudFormation stack.

 - If you want to delete the entire stack using the CloudFormation console:
    - Sign in to the AWS CloudFormation console
    - Select the Stack `sap-abap-assistant` and click on **delete**.


## Notices

*Customers are responsible for making their own independent assessment of the information in this Guidance. This Guidance: (a) is for informational purposes only, (b) represents AWS current product offerings and practices, which are subject to change without notice, and (c) does not create any commitments or assurances from AWS and its affiliates, suppliers or licensors. AWS products or services are provided “as is” without warranties, representations, or conditions of any kind, whether express or implied. AWS responsibilities and liabilities to its customers are controlled by AWS agreements, and this Guidance is not part of, nor does it modify, any agreement between AWS and its customers.*

## Authors

Adren D Souza