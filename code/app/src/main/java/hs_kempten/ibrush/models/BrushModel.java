package hs_kempten.ibrush.models;

import android.graphics.PointF;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import java.util.List;

import hs_kempten.ibrush.enums.ColorEnum;
import hs_kempten.ibrush.enums.ToothSurfaceEnum;
import hs_kempten.ibrush.utils.Utils;

/**
 * Created by Antoine on 17.10.2016.
 */

/**
 * Represents the RL Brush Parameters
 */
public class BrushModel {

    //Brush Length overwritten with Profile Data or User Input
    public static final float brushLengthDefault = 170;

    //Marking Colors
    public static final ColorEnum colorEndTop = ColorEnum.BLUE;
    public static final ColorEnum colorEndBottom = ColorEnum.YELLOW;
    public static final ColorEnum colorBeginTop = ColorEnum.RED;
    public static final ColorEnum colorBeginBottom = ColorEnum.GREEN;

    //Length
    public static float brushLength = brushLengthDefault;
    public static float brushLengthFirstMarkDistance = 20;

    //Results
    public final int brushQuad;
    public final int brushTooth;
    public final ToothSurfaceEnum brushSurface;

    //Visual Debug Values
    private List<PointF> schemaPointsBegin;
    private List<PointF> schemaPointsEnd;
    private PointF brushPointBegin;
    private PointF brushPointEnd;

    /**
     * Konstruktor
     *
     * @param brushQuad
     * @param brushTooth
     */
    public BrushModel(int brushQuad, int brushTooth, ToothSurfaceEnum brushSurface) {
        this.brushQuad = brushQuad;
        this.brushTooth = brushTooth;
        this.brushSurface = brushSurface;
    }

    /**
     * Sets Debug Points
     *
     * @param schemaPointsBegin
     * @param PointBegin
     */
    public void setSchemaPointsBegin(final List<PointF> schemaPointsBegin, final PointF PointBegin) {
        this.schemaPointsBegin = schemaPointsBegin;
        this.brushPointBegin = PointBegin;
    }

    /**
     * Sets Debug Points
     *
     * @param schemaPointsEnd
     * @param PointEnd
     */
    public void setSchemaPointsEnd(final List<PointF> schemaPointsEnd, final PointF PointEnd) {
        this.schemaPointsEnd = schemaPointsEnd;
        this.brushPointEnd = PointEnd;
    }

    /**
     * Draws Brush to Mat
     *
     * @param rgba
     */
    public void draw(Mat rgba) {
        //Begin Values
        if (schemaPointsBegin != null) {
            for (PointF p : schemaPointsBegin) {
                Imgproc.circle(rgba, Utils.getAsPoint(p), 5, new Scalar(0, 0, 255, 255), 4);
            }
        }
        if (brushPointBegin != null) {
            Imgproc.circle(rgba, Utils.getAsPoint(brushPointBegin), 5, new Scalar(255, 0, 0, 255), 5);
        }
        //End Values
        if (schemaPointsEnd != null) {
            for (PointF p : schemaPointsEnd) {
                Imgproc.circle(rgba, Utils.getAsPoint(p), 5, new Scalar(0, 255, 255, 255), 4);
            }
        }
        if (brushPointEnd != null) {
            Imgproc.circle(rgba, Utils.getAsPoint(brushPointEnd), 5, new Scalar(255, 0, 0, 255), 5);
        }
    }

    /**
     * Write Brush to Mat
     *
     * @param rgba
     */
    public void write(Mat rgba) {
        Imgproc.putText(rgba, toString(), new Point(5, 40), Core.FONT_ITALIC, 0.7, new Scalar(0, 255, 0, 255), 2);
    }

    /**
     * Returns Brush Position
     *
     * @return
     */
    public String getPosition() {
        String answer = "";
        if (brushQuad == 0) {
            answer += "-";
        } else {
            answer += brushQuad;
        }
        if (brushTooth == 0) {
            answer += "-";
        } else {
            answer += brushTooth;
        }
        answer += " ";
        answer += brushSurface.toString();
        return answer;
    }

    /**
     * Returns Video Analyse Log Line
     *
     * @return
     */
    public String getLogText() {
        return " " + getPosition();
    }

    @Override
    public String toString() {
        return "BRUSH " + getPosition();
    }
}
