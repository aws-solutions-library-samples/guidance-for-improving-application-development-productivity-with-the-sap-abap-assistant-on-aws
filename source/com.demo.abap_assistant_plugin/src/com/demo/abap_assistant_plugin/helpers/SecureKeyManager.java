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

import java.io.FileInputStream;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.UnrecoverableKeyException;
import java.security.cert.X509Certificate;
import java.util.Arrays;

import org.eclipse.equinox.security.storage.StorageException;

public class SecureKeyManager {
	private KeyStore keyStore;

	public SecureKeyManager() {
		try {
			this.keyStore = loadKeyStore();
		} catch (Exception e) {
		}
	}

	private KeyStore loadKeyStore() throws Exception {
		String keystorePath = ABAPAssistantHelper.getPreferences(ABAPAssistantConstants.PREFERENCES_PKCS_12_CERTIFICATE).trim();
		char[] keystorePassword = ABAPAssistantHelper.getPreferences(ABAPAssistantConstants.PREFERENCES_PKCS_12_KEYSTORE_PASSWORD).trim().toCharArray();

		try {
			KeyStore keyStore = KeyStore.getInstance("PKCS12");
			try (FileInputStream fis = new FileInputStream(keystorePath)) {
				keyStore.load(fis, keystorePassword);
			} finally {
				clearPassword(keystorePassword);
			}
			return keyStore;
		} catch (Exception e) {
			throw e;
		}
	}

	public static class CertificateKeyPair {
		private final X509Certificate certificate;
		private final PrivateKey privateKey;

		public CertificateKeyPair(X509Certificate certificate, PrivateKey privateKey) {
			this.certificate = certificate;
			this.privateKey = privateKey;
		}

		public X509Certificate getCertificate() {
			return certificate;
		}

		public PrivateKey getPrivateKey() {
			return privateKey;
		}
	}

	public CertificateKeyPair getCertificateAndKey() throws StorageException, UnrecoverableKeyException, KeyStoreException, NoSuchAlgorithmException  {
		String keyAlias = ABAPAssistantHelper.getPreferences(ABAPAssistantConstants.PREFERENCES_PKCS_12_KEY_ALIAS).trim();
		char[] keyPassword = ABAPAssistantHelper.getPreferences(ABAPAssistantConstants.PREFERENCES_PKCS_12_KEYSTORE_PASSWORD).trim().toCharArray();

		try {
			// Get the private key
			PrivateKey privateKey = (PrivateKey) keyStore.getKey(keyAlias, keyPassword);

			// Get the certificate
			X509Certificate certificate = (X509Certificate) keyStore.getCertificate(keyAlias);

			if (certificate == null) {
				throw new IllegalStateException("Failed to retrieve certificate  for alias: " + keyAlias);
			}

			if (privateKey == null) {
				throw new IllegalStateException("Failed to retrieve private key for alias: " + keyAlias);
			}

			return new CertificateKeyPair(certificate, privateKey);
		} finally {
			clearPassword(keyPassword);
		}
	}

	private void clearPassword(char[] password) {
		if (password != null) {
			Arrays.fill(password, '\0');
		}
	}

}
