package hs_kempten.ibrush.schemata;

import android.graphics.PointF;
import android.util.Log;

import org.opencv.core.Core;
import org.opencv.core.Mat;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import hs_kempten.ibrush.enums.ColorEnum;
import hs_kempten.ibrush.enums.ToothSurfaceEnum;
import hs_kempten.ibrush.utils.InsideChecker;
import hs_kempten.ibrush.utils.Utils;

/**
 * Created by Antoine on 15.12.2016.
 */

/**
 * Position Schema for Quad and Tooth
 */
public abstract class Schema {
    public static final String TAG = "SCHEMA";

    //Calculated Schema Points
    private List<PointF> calculatedPoints;
    //Schema Points Quad 1 and 2
    public PointF upperSchemaRootMidTop;
    public PointF[] upperSchemaRelativePoints;
    //Schema Points Quad 3 and 4
    public PointF lowerSchemaRootMidTop;
    public PointF[] lowerSchemaRelativePoints;
    //Analyzed Colors in this Schema
    protected ColorEnum colorTop;
    protected ColorEnum colorBottom;
    //The Point Distance Factor
    protected float factor;
    //The Colors Mid Point
    protected PointF colorsMidPoint;
    protected PointF colorTopMidPoint;
    protected PointF colorBottomMidPoint;
    //Calculated Schema Mid Points
    protected PointF upperMidPoint;
    protected PointF lowerMidPoint;
    //The Detected Colors
    protected Map<ColorEnum, Mat> colors;
    //The Root Mouth Point
    private PointF root;

    /**
     * Konstruktor
     */
    protected Schema() {
        factor = 0;
        colorsMidPoint = null;
    }

    /**
     * Returns Colors Mid Point
     *
     * @return
     */
    public PointF getColorsMidPoint() {
        return colorsMidPoint;
    }

    /**
     * Returns Calculated Points Collection
     *
     * @return
     */
    public List<PointF> getCalculatedPoints() {
        return calculatedPoints;
    }

    /**
     * Sets Factor for Point Calculation
     *
     * @param factor
     */
    public void setFactor(final float factor) {
        this.factor = factor;
    }

    /**
     * Sets Detected Colors
     *
     * @param colors
     */
    public void setColors(final Map<ColorEnum, Mat> colors) {
        this.colors = colors;
        colorsMidPoint = null;
    }

    /**
     * Sets Root Point
     *
     * @param root
     */
    public void setRootPoint(final PointF root) {
        this.root = root;
        upperMidPoint = null;
        lowerMidPoint = null;
    }

    /**
     * Returns Quad Point in Image
     *
     * @param base
     * @param factorise
     * @param quad
     * @return
     */
    protected abstract PointF getPoint(final PointF base, final PointF factorise, final int quad);

    /**
     * Returns Calculated Schema Point
     *
     * @param base
     * @param factorise
     * @param add
     * @return
     */
    protected PointF calculatePoint(final PointF base, final PointF factorise, final boolean add) {
        float x = base.x;
        float y = base.y + (factorise.y * factor);
        if (add) {
            x += (factorise.x * factor);
        } else {
            x -= (factorise.x * factor);
        }
        PointF calculated = new PointF(x, y);
        //Add Point for Visual Debugging
        calculatedPoints.add(calculated);
        return calculated;
    }

    /**
     * Prepares Schema
     *
     * @return
     */
    public void prepare() {
        //New Point List
        calculatedPoints = new ArrayList<>();

        //Calculate Complete Color Mid
        Mat joined = new Mat();
        Core.add(colors.get(colorTop), colors.get(colorBottom), joined);
        colorsMidPoint = Utils.getMid(joined);

        //Calculate Single Color Mid
        colorTopMidPoint = Utils.getMid(colors.get(colorTop));
        colorBottomMidPoint = Utils.getMid(colors.get(colorBottom));

        //Calculate Schema Root Points
        upperMidPoint = getPoint(root, upperSchemaRootMidTop, 2);
        lowerMidPoint = getPoint(root, lowerSchemaRootMidTop, 3);
    }

    /**
     * Estimates Brushed Quadrant
     *
     * @return
     */
    public boolean[] estimateQuadrant() {
        //Initialize Result, Element 0 true if empty Result
        boolean[] result = new boolean[]{true, false, false, false, false};

        if (colorsMidPoint != null) {
            //Estimate Brushed Quad
            InsideChecker QuadChecker;

            //Check Quad 1 and 2
            for (int quad = 1; quad < 3; quad++) {
                QuadChecker = new InsideChecker(colorsMidPoint);
                for (int i = 0; i < upperSchemaRelativePoints.length; i++) {
                    QuadChecker.addEdge(getPoint(upperMidPoint, upperSchemaRelativePoints[i], quad));
                }
                if (QuadChecker.checkInside()) {
                    result[0] = false;
                    result[quad] = true;
                }
            }

            //Check Quad 3 and 4
            for (int quad = 3; quad < 5; quad++) {
                QuadChecker = new InsideChecker(colorsMidPoint);
                for (int i = 0; i < lowerSchemaRelativePoints.length; i++) {
                    QuadChecker.addEdge(getPoint(lowerMidPoint, lowerSchemaRelativePoints[i], quad));
                }
                if (QuadChecker.checkInside()) {
                    result[0] = false;
                    result[quad] = true;
                }
            }
        }

        //Return Result as Array
        return result;
    }

    /**
     * Validates Quadrant
     *
     * @param quadrant
     * @return
     */
    public int validateQuadrant(int quadrant) {
        if (colorTopMidPoint != null && colorBottomMidPoint != null) {
            float degree = Utils.getDegree(colorTopMidPoint, colorBottomMidPoint);
            if (degree < 180) {
                switch (quadrant) {
                    case 1:
                        Log.d(TAG, "Quadrant Korrektur 1 -> 4");
                        return 4;

                    case 2:
                        Log.d(TAG, "Quadrant Korrektur 2 -> 3");
                        return 3;
                }
            } else {
                switch (quadrant) {
                    case 3:
                        Log.d(TAG, "Quadrant Korrektur 3 -> 2");
                        return 2;

                    case 4:
                        Log.d(TAG, "Quadrant Korrektur 4 -> 1");
                        return 1;
                }
            }
        }
        return quadrant;
    }

    /**
     * Estimates Brushed Tooth
     *
     * @param quadrant
     * @return
     */
    public abstract int estimateTooth(int quadrant);

    /**
     * Estimates Brushed Surface
     *
     * @param quadrant
     * @param tooth
     * @return
     */
    public abstract ToothSurfaceEnum estimateSurface(int quadrant, int tooth);

    /**
     * Estimates Brushed Surface By Color Mass
     *
     * @param thresholdTop
     * @param thresholdBottom
     * @return
     */
    protected ToothSurfaceEnum estimateSurface_Mass(float thresholdTop, float thresholdBottom) {
        double colorTopMass = Core.countNonZero(colors.get(colorTop));
        double colorBottomMass = Core.countNonZero(colors.get(colorBottom));
        if (colorBottomMass == 0 && colorTopMass == 0) {
            return ToothSurfaceEnum.NONE;
        }
        if (colorTopMass == 0) {
            return ToothSurfaceEnum.OUTSIDE;
        }
        if (colorBottomMass == 0) {
            return ToothSurfaceEnum.INSIDE;
        } else {
            double colorMass = colorTopMass / colorBottomMass;
            if (colorMass > thresholdBottom && colorMass < thresholdTop) {
                return ToothSurfaceEnum.TOP;
            } else {
                if (colorMass > thresholdTop) return ToothSurfaceEnum.INSIDE;
                if (colorMass < thresholdBottom) return ToothSurfaceEnum.OUTSIDE;
            }
        }
        return ToothSurfaceEnum.NONE;
    }
}
