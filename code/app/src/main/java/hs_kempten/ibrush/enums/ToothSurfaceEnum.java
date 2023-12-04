package hs_kempten.ibrush.enums;

/**
 * Created by Antoine Schmidt
 */
public enum ToothSurfaceEnum {

    INSIDE("Inside"),
    OUTSIDE("Outside"),
    TOP("Top"),
    NONE("------");

    private final String value;

    ToothSurfaceEnum(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }
}
