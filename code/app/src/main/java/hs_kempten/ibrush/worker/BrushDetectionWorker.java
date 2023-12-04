package hs_kempten.ibrush.worker;

import android.graphics.PointF;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import org.opencv.core.Mat;

import java.util.Map;

import hs_kempten.ibrush.enums.ColorEnum;
import hs_kempten.ibrush.enums.ToothSurfaceEnum;
import hs_kempten.ibrush.events.BrushDetectionListener;
import hs_kempten.ibrush.models.BrushModel;
import hs_kempten.ibrush.models.FaceModel;
import hs_kempten.ibrush.schemata.BeginSchema;
import hs_kempten.ibrush.schemata.EndSchema;
import hs_kempten.ibrush.utils.Utils;

/**
 * Created by Antoine on 17.10.2016.
 */

public class BrushDetectionWorker implements Runnable {
    public static final String TAG = "BRUSH DETECTION";

    private final BrushDetectionListener ear;
    private final BeginSchema beginSchema;
    private final EndSchema endSchema;
    private FaceModel me;

    /**
     * Konstruktor
     *
     * @param ear
     */
    public BrushDetectionWorker(final BrushDetectionListener ear) {
        this.ear = ear;
        beginSchema = new BeginSchema();
        endSchema = new EndSchema();
    }

    /**
     * Sets Image Data
     *
     * @param mDetectedColors
     */
    public void setData(final Map<ColorEnum, Mat> mDetectedColors, final FaceModel me) {
        this.me = me;
        if (me != null) {
            beginSchema.setFactor(me.getToImageFactor());
            beginSchema.setColors(mDetectedColors);
            beginSchema.setRootPoint(me.getMouthMidPoint());
            endSchema.setFactor(me.getToImageFactor());
            endSchema.setColors(mDetectedColors);
            endSchema.setRootPoint(me.getMouthMidPoint());
        }
    }

    public BrushModel process() {
        Log.d(TAG, "Started");
        if (me != null) {
            //Prepare Schema
            beginSchema.prepare();
            endSchema.prepare();

            int quadrant = estimateQuadrant();
            //Analyze Tooth
            if (quadrant != 0) {
                int tooth = estimateTooth(quadrant);
                ToothSurfaceEnum surface = estimateSurface(quadrant, tooth);

                //Build Result
                final BrushModel result = new BrushModel(quadrant, tooth, surface);

                //Set Debug Values
                result.setSchemaPointsBegin(beginSchema.getCalculatedPoints(), beginSchema.getColorsMidPoint());
                result.setSchemaPointsEnd(endSchema.getCalculatedPoints(), endSchema.getColorsMidPoint());

                //Return Result
                return result;
            }
        }
        return null;
    }

    /**
     * Estimates Quadrant from the estimates Result from the Schemas
     *
     * @return
     */
    private int estimateQuadrant() {
        int result = 0;

        //Analyze Quadrant
        boolean[] beginSchemaQuad = beginSchema.estimateQuadrant();
        boolean[] endSchemaQuad = endSchema.estimateQuadrant();

        //Debug
        Log.d(TAG, "Begin Schema " + beginSchemaQuad[1] + "/" + beginSchemaQuad[2] + "/" + beginSchemaQuad[3] + "/" + beginSchemaQuad[4]);
        Log.d(TAG, "End   Schema " + endSchemaQuad[1] + "/" + endSchemaQuad[2] + "/" + endSchemaQuad[3] + "/" + endSchemaQuad[4]);

        //Analyze Result
        for (int quad = 0; quad < 5; quad++) {
            if ((beginSchemaQuad[quad] || beginSchemaQuad[0]) && (endSchemaQuad[quad] || endSchemaQuad[0])) {
                result = quad;
                break;
            }
        }
        //If no Match take endSchema first
        if(result == 0){
            for (int quad = 0; quad < 5; quad++) {
                if (endSchemaQuad[quad]) {
                    result = quad;
                    break;
                }
            }
        }
        //Then beginSchema
        if(result == 0){
            for (int quad = 0; quad < 5; quad++) {
                if (endSchemaQuad[quad]) {
                    result = quad;
                    break;
                }
            }
        }

        //Validate Result
        result = endSchema.validateQuadrant(result);

        //Compose Quadrant
        PointF beginSchemaColorPoint = beginSchema.getColorsMidPoint();
        PointF endSchemaColorPoint = endSchema.getColorsMidPoint();
        if (beginSchemaColorPoint != null && endSchemaColorPoint != null) {
            //Correct unpossible Positions, this can worsen the result if face is turned
            if (beginSchemaColorPoint.x < endSchemaColorPoint.x) {
                if (result == 2) {
                    Log.d(TAG, "Quadrant Korrektur 2 -> 1");
                    return 1;
                }
                if (result == 3) {
                    Log.d(TAG, "Quadrant Korrektur 3 -> 4");
                    return 4;
                }
            } else {
                if (result == 1) {
                    Log.d(TAG, "Quadrant Korrektur 1 -> 2");
                    return 2;
                }
                if (result == 4) {
                    Log.d(TAG, "Quadrant Korrektur 3 -> 4");
                    return 3;
                }
            }
        }
        return result;
    }

    /**
     * Estimates Tooth
     *
     * @param quadrant
     * @return
     */
    private int estimateTooth(int quadrant) {

        int answerDivider = 0;
        int answerSumm = 0;
        int answerTemp;

        answerTemp = endSchema.estimateTooth(quadrant);
        if (answerTemp > 0) {
            answerSumm += answerTemp;
            answerDivider++;
        }
        answerTemp = beginSchema.estimateTooth(quadrant);
        if (answerTemp > 0) {
            answerSumm += answerTemp;
            answerDivider++;
        }

        PointF beginSchemaColorPoint = beginSchema.getColorsMidPoint();
        PointF endSchemaColorPoint = endSchema.getColorsMidPoint();
        if (beginSchemaColorPoint != null && endSchemaColorPoint != null) {
            float distance = Utils.getDistance(beginSchemaColorPoint, endSchemaColorPoint);
            float x = (BrushModel.brushLength - BrushModel.brushLengthFirstMarkDistance) * me.getToImageFactor();
            float a = 8 / (x * x);
            if (distance >= 0) {
                float resu = a * distance * distance;
                if ((answerTemp = (int) Math.ceil(resu)) > 8) {
                    answerTemp = 8;
                }
                if (answerTemp > 0) {
                    answerSumm += answerTemp;
                    answerDivider++;
                }
            }
        }
        if (answerDivider != 0) {
            return (int) Math.ceil(answerSumm / answerDivider);
        }
        return 0;
    }

    /**
     * Estimates Surface
     *
     * @param quadrant
     * @return
     */
    private ToothSurfaceEnum estimateSurface(int quadrant, int tooth) {
        ToothSurfaceEnum beginSurface = beginSchema.estimateSurface(quadrant, tooth);
        ToothSurfaceEnum endSurface = endSchema.estimateSurface(quadrant, tooth);
        if (beginSurface == ToothSurfaceEnum.NONE) {
            return endSurface;
        }
        if (endSurface == ToothSurfaceEnum.NONE) {
            return beginSurface;
        }
        if ((beginSurface == ToothSurfaceEnum.INSIDE && endSurface == ToothSurfaceEnum.OUTSIDE) || (endSurface == ToothSurfaceEnum.INSIDE && beginSurface == ToothSurfaceEnum.OUTSIDE)) {
            return ToothSurfaceEnum.TOP;
        }
        return endSurface;
    }

    @Override
    public void run() {
        answer(process());
    }

    /**
     * Returns Answer over Message Loop
     */
    private void answer(final BrushModel me) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                ear.onBrushDetection(me);
            }
        });
    }
}