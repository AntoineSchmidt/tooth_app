package hs_kempten.ibrush.worker;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;

import java.io.File;
import java.io.FileWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import hs_kempten.ibrush.enums.ColorEnum;
import hs_kempten.ibrush.enums.ToothSurfaceEnum;
import hs_kempten.ibrush.events.VideoListener;
import hs_kempten.ibrush.filters.ExtFilter;
import hs_kempten.ibrush.models.BrushModel;
import hs_kempten.ibrush.models.FaceModel;

import static org.opencv.imgcodecs.Imgcodecs.imread;
import static org.opencv.imgcodecs.Imgcodecs.imwrite;

/**
 * Created by Antoine on 31.10.2016.
 */

public class VideoWorker implements Runnable {
    public static final String TAG = "VIDEO WORKER";

    //Output Flags
    public static boolean output_processed = true;
    public static boolean output_processed_color = false;

    //Extracted Frames from Video if Analyzed as Single Images
    public static int fps = 30;

    private final VideoListener me;

    //Path Values
    private final String path;
    private final String path_output;
    private final String path_output_color;

    private Map<ColorEnum, Mat> colorsDetected;
    private FaceDetectionWorker faceWorker;
    private ColorDetectionWorker colorBeginTopWorker;
    private ColorDetectionWorker colorBeginBottomWorker;
    private ColorDetectionWorker colorEndTopWorker;
    private ColorDetectionWorker colorEndBottomWorker;
    private BrushDetectionWorker brushWorker;

    public VideoWorker(VideoListener me, String path) {
        //Setting Values
        this.me = me;
        this.path = path;
        this.path_output = path + "output/";
        this.path_output_color = path_output + "color/";

        //Creates Folders
        if (!(new File(path_output_color)).mkdirs()) {
            Log.d(TAG, "Error Creating " + path_output_color);
        }

        //Instanziating
        colorsDetected = new HashMap<>();
        faceWorker = new FaceDetectionWorker(null);
        colorBeginTopWorker = new ColorDetectionWorker(null, BrushModel.colorBeginTop, Imgproc.COLOR_BGR2HSV);
        colorBeginBottomWorker = new ColorDetectionWorker(null, BrushModel.colorBeginBottom, Imgproc.COLOR_BGR2HSV);
        colorEndTopWorker = new ColorDetectionWorker(null, BrushModel.colorEndTop, Imgproc.COLOR_BGR2HSV);
        colorEndBottomWorker = new ColorDetectionWorker(null, BrushModel.colorEndBottom, Imgproc.COLOR_BGR2HSV);
        brushWorker = new BrushDetectionWorker(null);
    }

    @Override
    public void run() {
        Log.d(TAG, "Path " + path);
        File directory = new File(path);
        if (directory.exists()) {
            File[] files = directory.listFiles(new ExtFilter(".jpg"));
            if (files != null && files.length > 0) {
                Log.d(TAG, "Count " + files.length);
                Arrays.sort(files);

                //Process Images
                processImages(files);

                /*
                //Process Video
                for (int i = 0; i < files.length; i++) {
                    processVideo(files[i]);
                }
                */
            } else {
                Log.d(TAG, "Folder is Empty");
            }
        } else {
            Log.d(TAG, "Folder doesnt Exist");
        }
        Log.d(TAG, "Finished");
        answer();
    }

    /**
     * Processes a given mp4 File
     * Not Working Codec Problems ffmpeg not supported with Android ... but in work
     *
     * @param file
     */
    private void processVideo(File file) {
        String filePath = file.getPath();
        Log.d(TAG, "File " + filePath);
        VideoCapture cap = new VideoCapture(filePath);
        if (cap.isOpened()) {
            int frameCount = 0;
            Mat frame = new Mat();
            try (FileWriter logFile = new FileWriter(filePath + ".txt")) {
                while (cap.read(frame)) {
                    logFile.write(processMat(frame, Integer.toString(++frameCount)));
                }
                logFile.flush();
                logFile.close();
            } catch (Exception e) {
                Log.d(TAG, "Exception Writing Log File " + e.getMessage());
            }
            cap.release();
        } else {
            Log.d(TAG, "Unable Opening " + filePath);
        }
    }

    /**
     * Processes Images
     *
     * @param files
     */
    private void processImages(File[] files) {
        try (FileWriter logFile = new FileWriter(path + "output.txt")) {

            //Write Header
            logFile.write("  Time  --   Image    -QT-Surface / Length: " + (int)BrushModel.brushLength + "mm" + System.lineSeparator());

            //Timestamp, fra is just the Frame Index for the Second
            int min = 0, sec = 0, fra = 0;

            for (int frame = 0; frame < files.length; frame++) {

                //Update Counter View
                progress(files.length - frame);

                //Time increase
                fra++;
                sec += fra / fps;
                fra %= fps;
                min += sec / 60;
                sec %= 60;

                //Timestring
                String timeString = String.format("%02d", min) + ":" + String.format("%02d", sec) + "-" + String.format("%02d", fra);

                //Processing
                String filePath = files[frame].getPath();
                Log.d(TAG, filePath);
                String result = timeString + "  " + processMat(imread(filePath), files[frame].getName());
                logFile.write(result + System.lineSeparator());
                Log.d(TAG, result);
            }
            logFile.flush();
            logFile.close();
        } catch (Exception e) {
            Log.d(TAG, "Exception Writing Log File " + e.getMessage());
        }
    }

    /**
     * Processes Mat
     *
     * @param frame
     * @return
     */
    private String processMat(Mat frame, String name) {
        //Set Data
        faceWorker.setData(frame);
        colorBeginTopWorker.setData(frame);
        colorBeginBottomWorker.setData(frame);
        colorEndTopWorker.setData(frame);
        colorEndBottomWorker.setData(frame);

        //Search Face
        FaceModel faceResult = faceWorker.process();
        if (faceResult != null) {
            //Search for Colors
            colorsDetected.put(BrushModel.colorBeginTop, colorBeginTopWorker.process());
            colorsDetected.put(BrushModel.colorBeginBottom, colorBeginBottomWorker.process());
            colorsDetected.put(BrushModel.colorEndTop, colorEndTopWorker.process());
            colorsDetected.put(BrushModel.colorEndBottom, colorEndBottomWorker.process());

            //Execute Brush Detection
            brushWorker.setData(colorsDetected, faceResult);
            BrushModel brushResult = brushWorker.process();

            if (output_processed_color) {
                //Write out Detected Colors
                imwrite(path_output_color + name + "-beginTop.jpg", colorsDetected.get(BrushModel.colorBeginTop));
                imwrite(path_output_color + name + "-beginBottom.jpg", colorsDetected.get(BrushModel.colorBeginBottom));
                imwrite(path_output_color + name + "-endTop.jpg", colorsDetected.get(BrushModel.colorEndTop));
                imwrite(path_output_color + name + "-endBottom.jpg", colorsDetected.get(BrushModel.colorEndBottom));
            }

            if (brushResult != null) {
                if (output_processed) {
                    //Write out for further Analysing
                    faceResult.draw(frame);
                    brushResult.draw(frame);
                    faceResult.write(frame);
                    brushResult.write(frame);
                    imwrite(path_output + name + "-processed.jpg", frame);
                }
                //Returns Log Content
                return name + brushResult.getLogText();
            }
        }
        //Returns Log Content
        return name + " -- " + ToothSurfaceEnum.NONE.toString() + " No Result" + ((faceResult == null) ? "/No Face" : "");
    }

    /**
     * Messages Progress
     *
     * @param prog
     */
    private void progress(final int prog) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                me.onProgress(prog);
            }
        });
    }

    /**
     * Returns Answer over Message Loop
     */
    private void answer() {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                me.onProcessingFinished();
            }
        });
    }
}
