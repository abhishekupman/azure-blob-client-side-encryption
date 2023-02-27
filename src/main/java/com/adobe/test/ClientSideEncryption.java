package com.adobe.test;
import com.azure.core.cryptography.AsyncKeyEncryptionKey;
import com.azure.core.http.policy.HttpLogDetailLevel;
import com.azure.core.http.policy.HttpLogOptions;
import com.azure.core.util.BinaryData;
import com.azure.identity.ClientAssertionCredential;
import com.azure.identity.ClientAssertionCredentialBuilder;
import com.azure.security.keyvault.keys.cryptography.KeyEncryptionKeyClientBuilder;

import com.azure.security.keyvault.keys.cryptography.models.KeyWrapAlgorithm;
import com.azure.storage.blob.*;
import com.azure.storage.blob.models.ParallelTransferOptions;
import com.azure.storage.blob.specialized.cryptography.EncryptedBlobClient;
import com.azure.storage.blob.specialized.cryptography.EncryptedBlobClientBuilder;
import com.azure.storage.blob.specialized.cryptography.EncryptionVersion;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class ClientSideEncryption {

    public static void uploadBlobUsingClientSideEncryption(UserInput userInput) {
        ClientAssertionCredential clientAssertionCredential = new ClientAssertionCredentialBuilder().
                clientId(userInput.getClientId()).
                tenantId(userInput.getTenantId()).
                clientAssertion(JWTUtils::getIDToken).
                build();

        AsyncKeyEncryptionKey asyncKeyEncryptionKey = new KeyEncryptionKeyClientBuilder().
                credential(clientAssertionCredential).
                buildAsyncKeyEncryptionKey(userInput.getVaultKey()).
                block();

        BlobClient azureBlobClient = getAzureBlobClient(userInput, clientAssertionCredential);

        EncryptedBlobClient encryptedBlobClient = new EncryptedBlobClientBuilder(EncryptionVersion.V2).
                key(asyncKeyEncryptionKey, KeyWrapAlgorithm.RSA1_5.toString()).
                blobClient(azureBlobClient).
                buildEncryptedBlobClient();

        uploadAndValidateTextData(encryptedBlobClient);
        //uploadLargeFile(encryptedBlobClient);
    }

    private static void uploadAndValidateTextData(EncryptedBlobClient encryptedBlobClient) {
        String textData = "Some Text Data";
        InputStream is = new ByteArrayInputStream(textData.getBytes(StandardCharsets.UTF_8));
        encryptedBlobClient.upload(is, textData.length(), true);
        System.out.println("Uploaded the content");

        BinaryData binaryData = encryptedBlobClient.downloadContent();
        String result = new String(binaryData.toBytes());
        assert result.equals(textData);
    }

    private static void uploadLargeFile(EncryptedBlobClient encryptedBlobClient) {
        File f = new File("/Users/abhishekupman/Downloads/4k_video.mp4");
        ParallelTransferOptions parallelTransferOptions = new ParallelTransferOptions(10 * 1024 * 1024, 10, null);
        encryptedBlobClient.uploadFromFile(f.getPath(), parallelTransferOptions, null, null, null, null, null);
    }


    private static BlobClient getAzureBlobClient(UserInput applicationInput, ClientAssertionCredential clientAssertionCredential) {
        final String accountUrl = "https://"+ applicationInput.getStorageAccount() + ".blob.core.windows.net";

        BlobServiceClient client = new BlobServiceClientBuilder()
                .credential(clientAssertionCredential)
                .endpoint(accountUrl)
                .httpLogOptions(new HttpLogOptions().setLogLevel(HttpLogDetailLevel.BASIC))
                .buildClient();

        BlobContainerClient blobContainerClient = client.getBlobContainerClient(applicationInput.getContainerName());
        return blobContainerClient.getBlobClient(applicationInput.getBlobName());
    }
}
