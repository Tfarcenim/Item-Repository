package tfar.nabba.util;

public enum BarrelType {
    BETTER("stacks"),ANTI("items"),FLUID("buckets");
    public final String s;

    BarrelType(String s) {
        this.s = s;
    }
}
