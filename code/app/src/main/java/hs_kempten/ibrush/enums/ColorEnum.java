package hs_kempten.ibrush.enums;

import org.opencv.core.Scalar;

/**
 * Created by Antoine Schmidt
 */
public enum ColorEnum {
    /**
     * OpenCV       Paint       Gimp
     * H [0-179]    [0-239]     [0-360]
     */
    RED(new Scalar[]{new Scalar(165, 160, 160), new Scalar(0, 160, 160)}, new Scalar[]{new Scalar(179, 255, 255), new Scalar(15, 255, 255)}), //Main Color +- 15
    YELLOW(new Scalar[]{new Scalar(20, 100, 100)}, new Scalar[]{new Scalar(30, 255, 255)}), //Gap Color +-10
    GREEN(new Scalar[]{new Scalar(45, 100, 100)}, new Scalar[]{new Scalar(75, 255, 255)}), //Main Color +- 15
    CYAN(new Scalar[]{new Scalar(80, 100, 100)}, new Scalar[]{new Scalar(100, 255, 255)}), //Gap Color +-10
    BLUE(new Scalar[]{new Scalar(105, 100, 100)}, new Scalar[]{new Scalar(135, 255, 255)}), //Main Color +- 15
    VIOLET(new Scalar[]{new Scalar(140, 100, 100)}, new Scalar[]{new Scalar(160, 255, 255)}); //Gap Color +-10

    public final Scalar[] lowerLimit;
    public final Scalar[] upperLimit;

    ColorEnum(Scalar[] lowerLimit, Scalar[] upperLimit) {
        this.lowerLimit = lowerLimit;
        this.upperLimit = upperLimit;
    }
}
