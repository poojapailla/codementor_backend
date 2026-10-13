package com.codementor.backend.config;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;

public final class CaCertBootstrap {

	public static final String TRUSTSTORE_PASSWORD = "codementor-ca";
	private static final String RESOURCE = "certs/aiven-ca.pem";
	private static final String PEM_TARGET = "certs/aiven-ca.pem";
	private static final String TRUSTSTORE_TARGET = "certs/aiven-truststore.p12";

	private CaCertBootstrap() {
	}

	public static void ensureAivenCaCert() {
		try (InputStream in = CaCertBootstrap.class.getResourceAsStream("/" + RESOURCE)) {
			if (in == null) {
				return;
			}
			Path pemTarget = Path.of(PEM_TARGET);
			Files.createDirectories(pemTarget.getParent());
			Files.copy(in, pemTarget, StandardCopyOption.REPLACE_EXISTING);

			Path storeTarget = Path.of(TRUSTSTORE_TARGET);
			try (InputStream pem = Files.newInputStream(pemTarget)) {
				CertificateFactory factory = CertificateFactory.getInstance("X.509");
				Certificate cert = factory.generateCertificate(pem);

				KeyStore keyStore = KeyStore.getInstance("PKCS12");
				keyStore.load(null, null);
				keyStore.setCertificateEntry("aiven-ca", cert);
				try (OutputStream out = Files.newOutputStream(storeTarget)) {
					keyStore.store(out, TRUSTSTORE_PASSWORD.toCharArray());
				}
			}
		} catch (Exception ignored) {
		}
	}
}