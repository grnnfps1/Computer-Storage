package com.computerstorage.common.transfer;

import java.util.Objects;

/** Server-side validation for player-authored logistics programs. */
public final class TransferProgramValidator {
    public static final int MAX_ID_LENGTH = 64;
    public static final int MAX_PROGRAMS = 256;
    public static final int MAX_PRIORITY = 1_000_000;
    public static final int MAX_ITEMS_PER_OPERATION = 4096;
    public static final int MAX_INTERVAL_TICKS = 20 * 60 * 60;

    private TransferProgramValidator() {}

    public static Validation validate(TransferProgram program, TransferEndpointRegistry endpoints) {
        if (program == null || endpoints == null) return Validation.invalid("Program or endpoint registry is null");
        if (!validId(program.id()) || !validId(program.sourceId()) || !validId(program.destinationId()))
            return Validation.invalid("Identifier is invalid or too long");
        if (program.sourceId().equals(program.destinationId()))
            return Validation.invalid("Source and destination must differ");
        if (endpoints.get(program.sourceId()) == null || endpoints.get(program.destinationId()) == null)
            return Validation.invalid("Source and destination endpoints must exist");
        if (program.priority() > MAX_PRIORITY)
            return Validation.invalid("Priority exceeds server limit");
        if (program.maxItemsPerOperation() > MAX_ITEMS_PER_OPERATION)
            return Validation.invalid("Transfer amount exceeds server limit");
        if (program.schedule().intervalTicks() < 1 || program.schedule().intervalTicks() > MAX_INTERVAL_TICKS)
            return Validation.invalid("Invalid transfer interval");
        if (program.minSourceAmount() > program.maxItemsPerOperation())
            return Validation.invalid("Minimum source amount exceeds operation limit");
        if (program.maxDestinationAmount() > 0 && program.maxDestinationAmount() < program.minSourceAmount())
            return Validation.invalid("Destination limit is below source minimum");
        return Validation.valid();
    }

    private static boolean validId(String value) {
        return value != null && !value.isBlank() && value.length() <= MAX_ID_LENGTH
                && value.chars().allMatch(c -> Character.isLetterOrDigit(c) || c == '_' || c == '-' || c == '.');
    }

    public record Validation(boolean valid, String reason) {
        public static Validation valid() { return new Validation(true, ""); }
        public static Validation invalid(String reason) { return new Validation(false, Objects.requireNonNull(reason)); }
    }
}
