package com.computerstorage.common.transfer;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EndpointBindingServiceTest {
    @Test
    void exposesStableValidationReasons() {
        assertEquals(EndpointBindingService.Result.INVALID, EndpointBindingService.Result.valueOf("INVALID"));
        assertEquals(EndpointBindingService.Result.TOO_FAR, EndpointBindingService.Result.valueOf("TOO_FAR"));
        assertEquals(EndpointBindingService.Result.NO_BLOCK_ENTITY, EndpointBindingService.Result.valueOf("NO_BLOCK_ENTITY"));
    }
}
