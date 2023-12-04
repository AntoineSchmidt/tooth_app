package hs_kempten.ibrush.activities;

import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;

import hs_kempten.ibrush.R;
import hs_kempten.ibrush.events.VideoListener;
import hs_kempten.ibrush.models.BrushModel;
import hs_kempten.ibrush.worker.VideoWorker;

public class VideoActivity extends AppCompatActivity implements VideoListener {
    public static final String TAG = "VIDEO";

    private EditText mBrushLength;
    private FloatingActionButton mProcessButton;
    private ProgressBar mProgressBar;
    private EditText mProgessNumber;

    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS: {
                    mProcessButton.setVisibility(View.VISIBLE);
                    break;
                }
                default: {
                    super.onManagerConnected(status);
                    break;
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        mBrushLength = (EditText) findViewById(R.id.editText);
        mProcessButton = (FloatingActionButton) findViewById(R.id.processButton);
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        mProgessNumber = (EditText) findViewById(R.id.progress);
        mProgressBar.setVisibility(View.INVISIBLE);
        mProgessNumber.setVisibility(View.INVISIBLE);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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

    /**
     * Process Button Event Handler
     *
     * @param v
     */
    public void onProcessClick(View v) {
        //Set Brush Length Value
        try {
            BrushModel.brushLength = Float.parseFloat(mBrushLength.getText().toString());
        } catch (Exception e) {
            BrushModel.brushLength = BrushModel.brushLengthDefault;
        }
        Log.d(TAG, "Brushlength " + BrushModel.brushLength);
        mBrushLength.setVisibility(View.INVISIBLE);
        mProcessButton.setEnabled(false);
        mProgressBar.setVisibility(View.VISIBLE);
        mProgessNumber.setVisibility(View.VISIBLE);
        new Thread(new VideoWorker(this, Environment.getExternalStorageDirectory().getAbsolutePath() + "/BrushGuideVideos/")).start();
    }

    @Override
    public void onProgress(int prog) {
        mProgessNumber.setText(Integer.toString(prog));
    }

    @Override
    public void onProcessingFinished() {
        mProgressBar.setVisibility(View.INVISIBLE);
        mProgessNumber.setVisibility(View.INVISIBLE);
        mBrushLength.setVisibility(View.VISIBLE);
        mProcessButton.setEnabled(true);
    }
}
