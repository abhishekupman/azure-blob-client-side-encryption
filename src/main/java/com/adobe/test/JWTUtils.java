package com.adobe.test;

import io.jsonwebtoken.Jwts;
import lombok.SneakyThrows;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueRequest;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueResponse;

import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.Date;

public class JWTUtils {

    @SneakyThrows
    public static String getIDToken() throws RuntimeException {
        String kid = "kuWdkXzuFRqSTYi0g8MFfIcIDzKNGzLGHntyeZbZ0Cc";
        return Jwts.builder().
                claim("service_id", "frame.io/transcode").
                claim("service_owner", "Adobe").
                setIssuedAt(Date.from(Instant.now())).
                setExpiration(Date.from(Instant.now().plus(5l, ChronoUnit.MINUTES))).
                setSubject("frame.io:transcode_workflow").
                setIssuer("https://token-practice-set.web.app").
                setAudience("api://AzureADTokenExchange").
                setId(kid).
                setHeaderParam("kid", kid).
                signWith(getPrivateKey()).
                compact();
    }

    private static PrivateKey getPrivateKey() throws Exception {
        java.security.Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
        String str = getSecretKey();
        str = str.replace("-----BEGIN RSA PRIVATE KEY-----", "");
        str = str.replace("-----END RSA PRIVATE KEY-----", "");
        str = str.replaceAll("\n", "");
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(Base64.getDecoder().decode(str));
        KeyFactory kf = KeyFactory.getInstance("RSA");
        return kf.generatePrivate(keySpec);
    }
    private static String getSecretKey() {
        String secretName = "oidc_private_key";

        // Create a Secrets Manager client
        SecretsManagerClient client = SecretsManagerClient.builder()
                .region(software.amazon.awssdk.regions.Region.US_EAST_1)
                .build();

        GetSecretValueRequest getSecretValueRequest = GetSecretValueRequest.builder()
                .secretId(secretName)
                .build();

        GetSecretValueResponse getSecretValueResponse = client.getSecretValue(getSecretValueRequest);;
        return getSecretValueResponse.secretString();
    }
}
