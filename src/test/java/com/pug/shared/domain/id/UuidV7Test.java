package com.pug.shared.domain.id;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class UuidV7Test {

    @Test
    public void testNextUuidGeneration() {
        UUID uuid = UuidV7.next();
        assertNotNull(uuid, "UUID should not be null");
    }

    @Test
    public void testUuidUniqueness() {
        UUID uuid1 = UuidV7.next();
        UUID uuid2 = UuidV7.next();

        assertNotEquals(uuid1, uuid2, "Generated UUIDs should be unique");
    }

    @Test
    public void testUuidFormat() {
        UUID uuid = UuidV7.next();

        assertTrue(isValidUuidFormat(uuid), "Generated UUID should have a valid format");
    }

    private boolean isValidUuidFormat(UUID uuid) {
        String uuidString = uuid.toString();
        return uuidString.matches("^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$");
    }

    @Test
    public void testMonotonicCounter() {
        UUID firstUuid = UuidV7.next();
        UUID secondUuid = UuidV7.next();

        long firstCounter = getCounterFromUuid(firstUuid);
        long secondCounter = getCounterFromUuid(secondUuid);

        assertTrue(secondCounter > firstCounter, "The counter should be monotonically increasing");
    }

    private long getCounterFromUuid(UUID uuid) {
        return uuid.getMostSignificantBits() & 0x0FFF;
    }
}
