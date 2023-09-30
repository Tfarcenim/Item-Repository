package tfar.nabba.util;

public enum BarrelType {
    BETTER("stacks",0xff00ff00),ANTI("items",0xffffff00),FLUID("buckets",0xff0000ff);
    public final String s;
    public final int color;

    BarrelType(String s,int color) {
        this.s = s;
        this.color = color;
    }
}
