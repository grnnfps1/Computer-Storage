package com.computerstorage.common.compat;

public record CompatibilityResult(boolean supported, Kind kind, String detail) {
    public enum Kind { ITEM_HANDLER, ENERGY, UNSUPPORTED, INVALID }
    public static CompatibilityResult items(String detail) { return new CompatibilityResult(true, Kind.ITEM_HANDLER, detail); }
    public static CompatibilityResult energy(String detail) { return new CompatibilityResult(true, Kind.ENERGY, detail); }
    public static CompatibilityResult unsupported(String detail) { return new CompatibilityResult(false, Kind.UNSUPPORTED, detail); }
    public static CompatibilityResult invalid(String detail) { return new CompatibilityResult(false, Kind.INVALID, detail); }
}
