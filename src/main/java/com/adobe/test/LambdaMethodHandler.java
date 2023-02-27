package com.adobe.test;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import lombok.SneakyThrows;

import java.util.Map;

public class LambdaMethodHandler implements RequestHandler<Map<String,String>, String> {

    @Override
    @SneakyThrows
    public String handleRequest(Map<String, String> eventMap, Context context) {
        UserInput userInput = getUserInput(eventMap);
        ClientSideEncryption.uploadBlobUsingClientSideEncryption(userInput);
        return "200 OK";
    }

    private UserInput getUserInput(Map<String, String> eventMap) {
        return UserInput.builder().
                clientId(eventMap.get("client-id")).
                tenantId(eventMap.get("tenant-id")).
                blobName(eventMap.get("blob-name")).
                containerName(eventMap.get("container-name")).
                storageAccount(eventMap.get("storage-account")).
                vaultKey(eventMap.get("vault-key-id")).
                build();
    }
}
