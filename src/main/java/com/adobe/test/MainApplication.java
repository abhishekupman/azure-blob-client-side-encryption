package com.adobe.test;

public class MainApplication {

    // test it to run it locally
    public static void main(String[] args) {
        ClientSideEncryption.uploadBlobUsingClientSideEncryption(getUserInput());
    }

    private static UserInput getUserInput() {
        return UserInput.builder().
                clientId("bcd88079-0756-4f59-81e1-6718cd62356f").
                tenantId("fa7b1b5a-7b34-4387-94ae-d2c178decee1").
                vaultKey("https://az-aws-cred.vault.azure.net/keys/blob-data-encryption-key/92fb1e6188a3411bb766c020ec2ec8ac").
                storageAccount("byostest12").
                containerName("test-data").
                blobName("encrypted_text.txt").
                build();
    }

}
