package com.pug.shared.domain.time;

import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class SystemTimeProviderTest {

    @Test
    public void testNow() {
        SystemTimeProvider timeProvider = new SystemTimeProvider();
        Instant now = timeProvider.now();

        assertNotNull(now, "now() should return a non-null Instant");
    }

    @Test
    public void testClock() {
        SystemTimeProvider timeProvider = new SystemTimeProvider();
        Clock clock = timeProvider.clock();

        assertNotNull(clock, "clock() should return a non-null Clock");
        assertEquals(Clock.systemUTC(), clock, "The clock should be the system UTC clock");
    }
}
