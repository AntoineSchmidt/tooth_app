package hs_kempten.ibrush.schemata;

/**
 * Created by Antoine on 16.12.2016.
 */

import android.graphics.PointF;

import hs_kempten.ibrush.enums.ToothSurfaceEnum;
import hs_kempten.ibrush.models.BrushModel;
import hs_kempten.ibrush.utils.Utils;

/**
 * The Big Schema for Brushend Marks
 */
public class EndSchema extends Schema {
    public static final String TAG = "END SCHEMA";

    //Erfahrungswert Distanz der Zahnbürstenbeklebung zum Mundmittelpunkt beim Putzen des 8ten Zahns
    private float lastToothDistance;

    /**
     * Konstruktor
     */
    public EndSchema() {
        lastToothDistance = 50 * (BrushModel.brushLength / BrushModel.brushLengthDefault);

        upperSchemaRootMidTop = new PointF(0, -15 * (BrushModel.brushLength / BrushModel.brushLengthDefault));
        upperSchemaRelativePoints = new PointF[]{
                new PointF(0, 0),
                new PointF(0, 60 * (BrushModel.brushLength / BrushModel.brushLengthDefault)),
                new PointF(BrushModel.brushLength, 105 * (BrushModel.brushLength / BrushModel.brushLengthDefault)),
                new PointF(BrushModel.brushLength + 5 * (BrushModel.brushLength / BrushModel.brushLengthDefault), 0)
        };

        lowerSchemaRootMidTop = new PointF(0, 45 * (BrushModel.brushLength / BrushModel.brushLengthDefault));
        lowerSchemaRelativePoints = new PointF[]{
                new PointF(0, 0),
                new PointF(0, 70 * (BrushModel.brushLength / BrushModel.brushLengthDefault)),
                new PointF(BrushModel.brushLength - (50 * (BrushModel.brushLength / BrushModel.brushLengthDefault)), 100 * (BrushModel.brushLength / BrushModel.brushLengthDefault)),
                new PointF(BrushModel.brushLength, 45 * (BrushModel.brushLength / BrushModel.brushLengthDefault))
        };

        colorTop = BrushModel.colorEndTop;
        colorBottom = BrushModel.colorEndBottom;
    }

    /**
     * Returns Quad Point in Image
     *
     * @param base
     * @param factorise
     * @param quad
     * @return
     */
    public PointF getPoint(final PointF base, final PointF factorise, final int quad) {
        return calculatePoint(base, factorise, (quad == 1 || quad == 4) ? true : false);
    }

    /**
     * Estimates Brushed Tooth
     *
     * @param quadrant
     * @return
     */
    public int estimateTooth(int quadrant) {
        int answer = 0;
        if (colorsMidPoint != null) {
            float x = (BrushModel.brushLength - lastToothDistance) * factor;
            float a = 8 / (x * x);
            float dist = BrushModel.brushLength * factor;
            if (quadrant < 3) {
                dist -= Math.abs(colorsMidPoint.x - upperMidPoint.x); //Utils.getDistance(colorsMidPoint, upperMidPoint);
            } else {
                dist -= Math.abs(colorsMidPoint.x - lowerMidPoint.x); //Utils.getDistance(colorsMidPoint, lowerMidPoint);
            }
            if (dist >= 0) {
                float resu = a * dist * dist;
                if ((answer = (int) Math.ceil(resu)) > 8) {
                    //Happens when Color Mid Point is in LastToothDistance
                    answer = 8;
                }
            }
        }
        return answer;
    }

    /**
     * Estimates Brushed Surface
     *
     * @param quadrant
     * @param tooth
     * @return
     */
    public ToothSurfaceEnum estimateSurface(int quadrant, int tooth) {
        if(tooth > 5){
            return estimateSurface_Degree(quadrant, 45f, 45f);
        } else {
            return estimateSurface_Mass(1.3f, 0.7f);
        }
    }

    /**
     * Estimates Brushed Surface By Degree
     *
     * @param quadrant
     * @return
     */
    private ToothSurfaceEnum estimateSurface_Degree(int quadrant, float thresholdTop, float thresholdBottom) {
        if (colorTopMidPoint == null && colorBottomMidPoint == null) {
            return ToothSurfaceEnum.NONE;
        }
        if (colorTopMidPoint == null) {
            return ToothSurfaceEnum.OUTSIDE;
        }
        if (colorBottomMidPoint == null) {
            return ToothSurfaceEnum.INSIDE;
        }
        float degree = Utils.getDegree(colorTopMidPoint, colorBottomMidPoint);
        if (degree < 180) {
            switch (quadrant) {
                case 3:
                    if (degree < thresholdBottom) return ToothSurfaceEnum.OUTSIDE;
                    if (degree > (180 - thresholdTop)) return ToothSurfaceEnum.INSIDE;
                    return ToothSurfaceEnum.TOP;

                case 4:
                    if (degree < thresholdBottom) return ToothSurfaceEnum.INSIDE;
                    if (degree > (180 - thresholdTop)) return ToothSurfaceEnum.OUTSIDE;
                    return ToothSurfaceEnum.TOP;
            }
        } else {
            switch (quadrant) {
                case 1:
                    if (degree < (180 + thresholdBottom)) return ToothSurfaceEnum.OUTSIDE;
                    if (degree > (380 - thresholdTop)) return ToothSurfaceEnum.INSIDE;
                    return ToothSurfaceEnum.TOP;

                case 2:
                    if (degree < (180 + thresholdBottom)) return ToothSurfaceEnum.INSIDE;
                    if (degree > (380 - thresholdTop)) return ToothSurfaceEnum.OUTSIDE;
                    return ToothSurfaceEnum.TOP;
            }
        }
        return ToothSurfaceEnum.NONE;
    }
}
