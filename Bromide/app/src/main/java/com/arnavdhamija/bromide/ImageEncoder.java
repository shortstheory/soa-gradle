package com.arnavdhamija.bromide;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Base64;
import android.util.Log;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import id.zelory.compressor.Compressor;

/**
 * Created by nic on 25/10/17.
 */

class ImageEncoder implements Runnable {
    private Thread thread;
    Context context;
    Uri imageURI;
    String resultString;
    final int BITMAP_MAX_DIMENSION = 640;

    ImageEncoder(Context ctx, Uri uri) {
        imageURI = uri;
        context = ctx;
    }

    @Override
    public void run() {
        encodeImage(imageURI);
    }

    public void base64encode() throws InterruptedException {
        thread = new Thread(this);
        thread.start();
    }

    private void encodeImage(Uri uri) {
        try {
            InputStream imageStream = context.getContentResolver().openInputStream(uri);
            final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
            final float aspectRatio = (float) selectedImage.getWidth()/(float) selectedImage.getHeight();
            Bitmap resizedBitmap = null;
            if (aspectRatio > 1) {
                if (selectedImage.getWidth() > BITMAP_MAX_DIMENSION) {
                    int scaledHeight = Math.round(BITMAP_MAX_DIMENSION / aspectRatio);
                    resizedBitmap = Bitmap.createScaledBitmap(selectedImage, BITMAP_MAX_DIMENSION, scaledHeight, false);
                }
            } else {
                if (selectedImage.getHeight() > BITMAP_MAX_DIMENSION) {
                    int scaledWidth = Math.round(BITMAP_MAX_DIMENSION * aspectRatio);
                    resizedBitmap = Bitmap.createScaledBitmap(selectedImage, scaledWidth, BITMAP_MAX_DIMENSION, false);
                }
            }


            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos); //32KB
            byte[] b = baos.toByteArray();
            String encImage = Base64.encodeToString(b, Base64.DEFAULT);

            Log.i("BROMIDE", "SelectedImageBytes: " + resizedBitmap.getByteCount());
            TextView selectedSizeText = (TextView) ((Activity) context).findViewById(R.id.selectedSizeText);
            TextView compressedSizeText = (TextView)((Activity) context).findViewById(R.id.compressedSizeText);
            TextView compressionRatioText = (TextView)((Activity) context).findViewById(R.id.compressionRatioText);


            runOnUiThread(new Runnable() {
                @Override
                public void run() {

//stuff that updates ui

                }
            });
            selectedSizeText.setText("Original Size: " + String.valueOf(selectedImage.getByteCount()));
            compressedSizeText.setText("Compressed Size: " + String.valueOf(resizedBitmap.getByteCount()));
            compressionRatioText.setText("Compression Ratio: " + String.valueOf((float)selectedImage.getByteCount() / resizedBitmap.getByteCount()));

            //            Log.d("BROMIDE", "SelectedImageBytes: " + selectedImage.getByteCount() + ", Compressed Image bytes: " + compressImage.getByteCount());

            ImageSender imageSender = new ImageSender(context);
            imageSender.sendImage(encImage);
        } catch (IOException e) {
            Log.e("BROMIDE", "File not found");
        }
    }
}
