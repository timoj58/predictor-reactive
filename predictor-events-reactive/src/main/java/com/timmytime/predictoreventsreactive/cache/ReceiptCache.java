package com.timmytime.predictoreventsreactive.cache;

import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Component
public class ReceiptCache {

    private final Set<UUID> receipts = new HashSet<>();

    public void addReceipt(UUID receipt){
        this.receipts.add(receipt);
    }

    public void processReceipt(UUID receipt){
        this.receipts.remove(receipt);
    }

    public Boolean isEmpty(UUID receipt){ //param used for test mocks to stop recursive loop
        return this.receipts.isEmpty();
    }
}
