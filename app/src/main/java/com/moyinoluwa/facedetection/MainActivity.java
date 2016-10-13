package com.moyinoluwa.facedetection;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.SparseArray;
import android.view.View;
import android.widget.ImageView;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;
import com.google.android.gms.vision.face.Landmark;

public class MainActivity extends AppCompatActivity {
    ImageView imageView;
    Paint rectPaint;
    Bitmap defaultBitmap;
    Bitmap temporaryBitmap;
    Bitmap eyePatchBitmap;
    Bitmap leftToothBitmap;
    Bitmap rightToothBitmap;
    Canvas canvas;
    int viewID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = (ImageView) findViewById(R.id.image_view);
    }

    public void processImage(View view) {

        viewID = view.getId();

        BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
        bitmapOptions.inMutable = true;

        initializeBitmap(bitmapOptions);
        createRectanglePaint();

        canvas = new Canvas(temporaryBitmap);
        canvas.drawBitmap(defaultBitmap, 0, 0, null);

        FaceDetector faceDetector = new FaceDetector.Builder(this)
                .setTrackingEnabled(false)
                .setLandmarkType(FaceDetector.ALL_LANDMARKS)
                .build();

        if (!faceDetector.isOperational()) {
            new AlertDialog.Builder(this)
                    .setMessage("Face Detector could not be set up on your device :(")
                    .show();
        } else {
            Frame frame = new Frame.Builder().setBitmap(defaultBitmap).build();
            SparseArray<Face> sparseArray = faceDetector.detect(frame);

            detectFaces(sparseArray);

            imageView.setImageDrawable(new BitmapDrawable(getResources(), temporaryBitmap));

            faceDetector.release();
        }
    }

    private void initializeBitmap(BitmapFactory.Options bitmapOptions) {
        defaultBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.image,
                bitmapOptions);
        temporaryBitmap = Bitmap.createBitmap(defaultBitmap.getWidth(), defaultBitmap
                .getHeight(), Bitmap.Config.RGB_565);
        eyePatchBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.eye_patch,
                bitmapOptions);
        leftToothBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.left_tooth,
                bitmapOptions);
        rightToothBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.right_tooth,
                bitmapOptions);
    }

    private void createRectanglePaint() {
        rectPaint = new Paint();
        rectPaint.setStrokeWidth(5);
        rectPaint.setColor(Color.CYAN);
        rectPaint.setStyle(Paint.Style.STROKE);
    }

    private void detectFaces(SparseArray<Face> sparseArray) {

        for (int i = 0; i < sparseArray.size(); i++) {
            Face face = sparseArray.valueAt(i);

            //float left = face.getPosition().x;
            //float top = face.getPosition().y;
            //float right = left + face.getWidth();
            //float bottom = right + face.getHeight();
            //float cornerRadius = 2.0f;

            //RectF rectF = new RectF(left, top, right, bottom);
            //canvas.drawRoundRect(rectF, cornerRadius, cornerRadius, rectPaint);

            detectLandmarks(face);
        }
    }

    private void detectLandmarks(Face face) {
        for (Landmark landmark : face.getLandmarks()) {

            int cx = (int) (landmark.getPosition().x);
            int cy = (int) (landmark.getPosition().y);

            //canvas.drawCircle(cx, cy, 10, rectPaint);

            //drawLandmarkType(landmark.getType(), cx, cy);

            if(viewID == R.id.pirate_button)
                drawEyePatchBitmap(landmark.getType(), cx, cy);
            else if(viewID == R.id.dracula_button)
                drawVampTeethBitmap(landmark.getType(), cx, cy);
        }
    }

    private void drawLandmarkType(int landmarkType, float cx, float cy) {
        String type = String.valueOf(landmarkType);
        rectPaint.setTextSize(50);
        canvas.drawText(type, cx, cy, rectPaint);
    }

    private void drawEyePatchBitmap(int landmarkType, float cx, float cy) {
        if (landmarkType == 4) {
            canvas.drawBitmap(eyePatchBitmap, cx - 270, cy - 250, null);
        }
    }

    private void drawVampTeethBitmap(int landmarkType, float cx, float cy) {
        if (landmarkType == 11) { //left (mirror) tooth
            canvas.drawBitmap(leftToothBitmap, cx - 10, cy - 10, null);
        }
        else if (landmarkType == 5) { //right (mirror) tooth
            canvas.drawBitmap(rightToothBitmap, cx - 2, cy - 2, null); //landmark 5 seems more accurate than 11
        }
    }
}
