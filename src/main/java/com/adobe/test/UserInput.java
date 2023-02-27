package com.adobe.test;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

@Value
@Builder
public class UserInput {
    @NonNull String storageAccount;
    @NonNull String containerName;
    @NonNull String vaultKey;
    @NonNull String blobName;
    @NonNull String clientId;
    @NonNull String tenantId;
}
