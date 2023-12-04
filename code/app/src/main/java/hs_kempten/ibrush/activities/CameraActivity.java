package hs_kempten.ibrush.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.SurfaceView;
import android.view.WindowManager;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import hs_kempten.ibrush.R;
import hs_kempten.ibrush.database.CleanTable;
import hs_kempten.ibrush.objects.BrushObject;
import hs_kempten.ibrush.objects.FaceObject;

/**
 * Created by Antoine Schmidt
 */
public class CameraActivity extends AppCompatActivity implements CameraBridgeViewBase.CvCameraViewListener2 {
    public static final String TAG = "Camera";

    // MEMBERS
    private static final int MAX_FRAME_SIZE_WIDTH = 800;
    private static final int MAX_FRAME_SIZE_HEIGHT = 480;

    // CONSTANTS
    private CameraBridgeViewBase mOpenCvCameraView;
    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS: {
                    mOpenCvCameraView.enableView();
                    break;
                }
                default: {
                    super.onManagerConnected(status);
                    break;
                }
            }
        }
    };

    //Objects
    private FaceObject face;
    private BrushObject brush;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Objects
        face = new FaceObject();
        brush = new BrushObject(face);

        // keep the screen on and don't dim it
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        // set the GUI
        setContentView(R.layout.activity_camera);

        // get the camera-view within the GUI
        mOpenCvCameraView = (CameraBridgeViewBase) findViewById(R.id.cameraPreView);

        // set a maximum frame-size to not have massive resolutions to calculate with
        mOpenCvCameraView.setMaxFrameSize(MAX_FRAME_SIZE_WIDTH, MAX_FRAME_SIZE_HEIGHT);
        mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);

        // listen to cameraview-specific events
        mOpenCvCameraView.setCvCameraViewListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!OpenCVLoader.initDebug()) {
            Log.d(TAG, "Internal OpenCV library not found. Using OpenCV Manager for initialization");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_1_0, this, mLoaderCallback);
        } else {
            Log.d(TAG, "OpenCV library found inside package. Using it!");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mOpenCvCameraView != null){
            mOpenCvCameraView.disableView();}

        // Finish Cleaning Process
        CleanTable.finishClean();
    }

    // OPENCV CALLBACKS

    @Override
    public void onCameraViewStopped() {
        // empty!
    }

    @Override
    public void onCameraViewStarted(int width, int height) {
        // empty!
    }

    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        // get the cameraframe as matrix with rgba values
        Mat rgba = inputFrame.rgba();

        // face-detection
        face.update(rgba);

        // brush-detection
        brush.update(rgba);

        // face-draw
        face.draw(rgba);

        // brush-draw
        brush.draw(rgba);

        //Mirror only if Front Camera
        if (getString(R.string.camera_number).equals(getString(R.string.camera_number_front))) {
            Mat mRgbaF = new Mat(rgba.height(), rgba.width(), CvType.CV_8UC4);
            Imgproc.resize(rgba, mRgbaF, mRgbaF.size(), 0, 0, 0);
            Core.flip(mRgbaF, rgba, 1);
        }

        // face-write
        face.write(rgba);

        // brush-write
        brush.write(rgba);

        // return frame data
        return rgba;
    }
}
