package com.faceDemo.currencyview;

/*
 * Copyright 2019 The TensorFlow Authors. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import android.app.Fragment;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.util.Size;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import com.faceDemo.activity.CameraActivity;
import com.faceDemo.activity.ClassifierActivity;
import com.faceDemo.utils.ImageUtils;
import com.faceDemo.R;

import java.io.IOException;
import java.util.List;

public class LegacyCameraConnectionFragment extends Fragment {
    /**
     * Conversion from screen rotation to JPEG orientation.
     */
    private static final SparseIntArray ORIENTATIONS = new SparseIntArray();

    static {
        ORIENTATIONS.append(Surface.ROTATION_0, 90);
        ORIENTATIONS.append(Surface.ROTATION_90, 0);
        ORIENTATIONS.append(Surface.ROTATION_180, 270);
        ORIENTATIONS.append(Surface.ROTATION_270, 180);
    }

    private Camera camera;
    private Camera.PreviewCallback imageListener;
    private Size desiredSize;
    private int layout;
    private AutoFitTextureView textureView;

    private final TextureView.SurfaceTextureListener surfaceTextureListener = new TextureView.SurfaceTextureListener() {
        @Override
        public void onSurfaceTextureAvailable(final SurfaceTexture texture, final int width, final int height) {
            int index = getCameraId();
            camera = Camera.open(index);

            try {
                Camera.Parameters parameters = camera.getParameters();
                List<String> focusModes = parameters.getSupportedFocusModes();
                if (focusModes != null && focusModes.contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)) {
                    parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
                }
                List<Camera.Size> cameraSizes = parameters.getSupportedPreviewSizes();
                Size[] sizes = new Size[cameraSizes.size()];
                int i = 0;
                for (Camera.Size size : cameraSizes) {
                    sizes[i++] = new Size(size.width, size.height);
                }
                Size previewSize = CameraConnectionFragment.chooseOptimalSize(sizes, desiredSize.getWidth(), desiredSize.getHeight());
                parameters.setPreviewSize(previewSize.getWidth(), previewSize.getHeight());
                camera.setDisplayOrientation(90);
                camera.setParameters(parameters);
                camera.setPreviewTexture(texture);
            } catch (IOException exception) {
                camera.release();
            }

            camera.setPreviewCallbackWithBuffer(imageListener);
            Camera.Size s = camera.getParameters().getPreviewSize();
            Log.d("getPreviewSize", "onSurfaceTextureAvailable: s.height="+s.height+" s.width"+ s.width);
            camera.addCallbackBuffer(new byte[ImageUtils.getYUVByteSize(s.height, s.width)]);

            textureView.setAspectRatio(s.height, s.width);
            camera.startPreview();
        }

        @Override
        public void onSurfaceTextureSizeChanged(final SurfaceTexture texture, final int width, final int height) {
        }

        @Override
        public boolean onSurfaceTextureDestroyed(final SurfaceTexture texture) {
            return true;
        }

        @Override
        public void onSurfaceTextureUpdated(final SurfaceTexture texture) {
        }
    };
    /**
     * An additional thread for running tasks that shouldn't block the UI.
     */
    private HandlerThread backgroundThread;

    public LegacyCameraConnectionFragment(final Camera.PreviewCallback imageListener, final int layout, final Size desiredSize) {
        this.imageListener = imageListener;
        this.layout = layout;
        this.desiredSize = desiredSize;
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
        return inflater.inflate(layout, container, false);
    }

    @Override
    public void onViewCreated(final View view, final Bundle savedInstanceState) {
        textureView = (AutoFitTextureView) view.findViewById(R.id.texture);

        view.findViewById(R.id.bt1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ClassifierActivity.FILE_NAME = "humanMatting-1.pt";
            }
        });

        view.findViewById(R.id.bt2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClassifierActivity.FILE_NAME = "humanMatting-2.pt";
            }
        });

        view.findViewById(R.id.bt3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClassifierActivity.FILE_NAME = "humanMatting-3.pt";
            }
        });

        view.findViewById(R.id.bt4).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ClassifierActivity.FILE_NAME = "humanMatting-4.pt";
            }
        });

        view.findViewById(R.id.bt5).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClassifierActivity.FILE_NAME = "humanMatting-5.pt";
            }
        });

        view.findViewById(R.id.bt6).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClassifierActivity.FILE_NAME = "humanMatting-6.pt";
            }
        });

        final TextView progress = view.findViewById(R.id.textView);
        final SeekBar seekbar = view.findViewById(R.id.seekBar);
        SeekBar.OnSeekBarChangeListener onSeekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                ClassifierActivity.threshold = seekbar.getProgress();
                progress.setText(String.valueOf(seekBar.getProgress()));
            }
        };
        seekbar.setOnSeekBarChangeListener(onSeekBarChangeListener);


/*
        view.findViewById(R.id.bt4).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ClassifierActivity.FILE_NAME = "douyin-7.pt";
            }
        });

        view.findViewById(R.id.bt5).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClassifierActivity.FILE_NAME = "douyin-8.pt";
            }
        });

        view.findViewById(R.id.bt6).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClassifierActivity.FILE_NAME = "douyin-9.pt";
            }
        });

        view.findViewById(R.id.bt7).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ClassifierActivity.FILE_NAME = "douyin-10.pt";
            }
        });

        view.findViewById(R.id.bt8).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClassifierActivity.FILE_NAME = "douyin-11.pt";
            }
        });

        view.findViewById(R.id.bt9).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClassifierActivity.FILE_NAME = "douyin-12.pt";
            }
        });

        view.findViewById(R.id.bt10).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClassifierActivity.FILE_NAME = "douyin-13.pt";
            }
        });

        view.findViewById(R.id.bt11).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClassifierActivity.FILE_NAME = "douyin-14.pt";
            }
        });

        view.findViewById(R.id.bt12).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClassifierActivity.FILE_NAME = "douyin-15.pt";
            }
        });

        view.findViewById(R.id.bt13).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClassifierActivity.FILE_NAME = "douyin-16.pt";
            }
        });

        view.findViewById(R.id.bt14).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClassifierActivity.FILE_NAME = "snapchat-16.pt";
            }
        });*/
    }

    @Override
    public void onActivityCreated(final Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }


    @Override
    public void onResume() {
        super.onResume();
        startBackgroundThread();
        // When the screen is turned off and turned back on, the SurfaceTexture is already
        // available, and "onSurfaceTextureAvailable" will not be called. In that case, we can open
        // a camera and start preview from here (otherwise, we wait until the surface is ready in
        // the SurfaceTextureListener).

        if (textureView.isAvailable() && camera != null) {
            camera.startPreview();
        } else {
            textureView.setSurfaceTextureListener(surfaceTextureListener);
        }
    }

    @Override
    public void onPause() {
//        stopCamera();
        stopBackgroundThread();
        super.onPause();
    }

    /**
     * Starts a background thread and its {@link Handler}.
     */
    private void startBackgroundThread() {
        backgroundThread = new HandlerThread("CameraBackground");
        backgroundThread.start();
    }

    /**
     * Stops the background thread and its {@link Handler}.
     */
    private void stopBackgroundThread() {
        if (backgroundThread != null) {
            backgroundThread.quitSafely();
            try {
                backgroundThread.join();
                backgroundThread = null;
            } catch (final InterruptedException e) {
            }
        }
    }

    protected void stopCamera() {
        if (camera != null) {
            camera.stopPreview();
            camera.setPreviewCallback(null);
            camera.release();
            camera = null;
            textureView.setSurfaceTextureListener(null);
        }
    }

    public int getCameraId() {
        CameraInfo ci = new CameraInfo();
        for (int i = 0; i < Camera.getNumberOfCameras(); i++) {
            Camera.getCameraInfo(i, ci);
            if (CameraActivity.is_front_camera) {
                if (ci.facing == CameraInfo.CAMERA_FACING_FRONT) return i;
            }
            else {
                if (ci.facing == CameraInfo.CAMERA_FACING_BACK) return i;
            }
        }
        return -1; // No camera found
    }
}
