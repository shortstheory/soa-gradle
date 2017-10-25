package com.arnavdhamija.bromide;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Base64;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by nic on 25/10/17.
 */

class ImageEncoder implements Runnable {
    private Thread thread;
    Context context;
    Uri imageURI;
    String resultString;

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
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            selectedImage.compress(Bitmap.CompressFormat.JPEG, 100, baos); //32KB
            byte[] b = baos.toByteArray();
            String encImage = Base64.encodeToString(b, Base64.DEFAULT);

            ImageSender imageSender = new ImageSender(context);
            imageSender.sendImage(encImage);
        } catch (IOException e) {
            Log.e("BROMIDE", "File not found");
        }
    }
}
