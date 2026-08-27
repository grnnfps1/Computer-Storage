package com.computerstorage.common.transfer;

import java.util.Objects;

public final class TransferProgramValidator {
    public static final int MAX_ID_LENGTH = 64;
    public static final int MAX_PROGRAMS = 256;
    public static final int MAX_PRIORITY = 1_000_000;
    public static final int MAX_ITEMS_PER_OPERATION = 4096;
    public static final int MAX_INTERVAL_TICKS = 20 * 60 * 60;
    private TransferProgramValidator() {}
    public static Validation validate(TransferProgram program, WorldTransferEndpointRegistry endpoints) {
        if (program == null || endpoints == null) return Validation.invalid("Program or endpoint registry is null");
        if (!validId(program.id()) || !validId(program.sourceId()) || !validId(program.destinationId())) return Validation.invalid("Identifier is invalid or too long");
        if (program.sourceId().equals(program.destinationId())) return Validation.invalid("Source and destination must differ");
        if (!endpoints.contains(program.sourceId()) || !endpoints.contains(program.destinationId())) return Validation.invalid("Source and destination endpoints must exist");
        if (program.priority() < 0 || program.priority() > MAX_PRIORITY) return Validation.invalid("Priority is outside server limits");
        if (program.maxItemsPerOperation() < 1 || program.maxItemsPerOperation() > MAX_ITEMS_PER_OPERATION) return Validation.invalid("Transfer amount is outside server limits");
        if (program.minSourceAmount() < 0 || program.minSourceAmount() > program.maxItemsPerOperation()) return Validation.invalid("Minimum source amount is invalid");
        if (program.maxDestinationAmount() < 0) return Validation.invalid("Destination limit cannot be negative");
        if (program.schedule().intervalTicks() < 1 || program.schedule().intervalTicks() > MAX_INTERVAL_TICKS) return Validation.invalid("Invalid transfer interval");
        if (program.schedule().offsetTicks() < 0) return Validation.invalid("Invalid transfer offset");
        return Validation.ok();
    }
    private static boolean validId(String value) { return value != null && !value.isBlank() && value.length() <= MAX_ID_LENGTH && value.chars().allMatch(c -> Character.isLetterOrDigit(c) || c == '_' || c == '-' || c == '.'); }
    public record Validation(boolean valid, String reason) {
        public static Validation ok() { return new Validation(true, ""); }
        public static Validation invalid(String reason) { return new Validation(false, Objects.requireNonNull(reason)); }
    }
}
