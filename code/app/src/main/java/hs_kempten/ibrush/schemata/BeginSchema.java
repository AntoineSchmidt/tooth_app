package hs_kempten.ibrush.schemata;

import android.graphics.PointF;

import hs_kempten.ibrush.enums.ToothSurfaceEnum;
import hs_kempten.ibrush.models.BrushModel;

/**
 * Created by Antoine on 16.12.2016.
 */

/**
 * Schema for Brushbegin Marks
 */
public class BeginSchema extends Schema {
    public static final String TAG = "BEGIN SCHEMA";

    //Erfahrungswert Distanz der Zahnbürstenbeklebung zum Mundmittelpunkt beim Putzen des 1ten Zahns
    private int firstToothDistance;
    //Erfahrungswert Distanz der Zahnbürstenbeklebung zum Mundmittelpunkt beim Putzen des 8ten Zahns
    private int lastToothDistance;

    /**
     * Konstruktor
     */
    public BeginSchema() {
        firstToothDistance = 30;
        lastToothDistance = 30;

        upperSchemaRootMidTop = new PointF(0, -15);
        upperSchemaRelativePoints = new PointF[]{
                new PointF(-30, 0),
                new PointF(-30, 15),
                new PointF(30, 15),
                new PointF(30, 0)
        };

        lowerSchemaRootMidTop = new PointF(0, 0);
        lowerSchemaRelativePoints = new PointF[]{
                new PointF(-30, 0),
                new PointF(-30, 20),
                new PointF(30, 20),
                new PointF(30, 0)
        };

        colorTop = BrushModel.colorBeginTop;
        colorBottom = BrushModel.colorBeginBottom;
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
        return calculatePoint(base, factorise, (quad == 1 || quad == 4) ? false : true);
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
            float x = (firstToothDistance + lastToothDistance) * factor;
            float a = 8 / (x * x);
            float dist = firstToothDistance * factor;
            float distX;
            if (quadrant < 3) {
                distX = colorsMidPoint.x - upperMidPoint.x; //Utils.getDistance(colorsMidPoint, upperMidPoint);
            } else {
                distX = colorsMidPoint.x - lowerMidPoint.x; //Utils.getDistance(colorsMidPoint, lowerMidPoint);
            }
            if (quadrant == 1 || quadrant == 4) {
                dist -= distX;
            } else {
                dist += distX;
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
        return estimateSurface_Mass(1.3f, 0.7f);
    }
}
