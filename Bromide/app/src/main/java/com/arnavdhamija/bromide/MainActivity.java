package com.arnavdhamija.bromide;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.renderscript.ScriptGroup;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity {

    final static int PICK_IMAGE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        Button bromideButton = (Button) findViewById(R.id.bromide_button);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);

        setSupportActionBar(toolbar);


        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        final String hubAddress = sharedPreferences.getString("hub_address", "");

        bromideButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("BROMIDE", "HUB_ADDR"+hubAddress);

                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "TBD", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PICK_IMAGE) {
            if (resultCode == Activity.RESULT_OK) {
                Uri result = data.getData();
                Log.d("BROMIDE", result.toString());
                ImageView displayImage = (ImageView) findViewById(R.id.displayImage);
                displayImage.setImageURI(result);

                ImageEncoder imageEncoder = new ImageEncoder(result);
                try {
                    String encodedString = imageEncoder.base64encode();
                    Log.d("ENCODE", encodedString);
                } catch (InterruptedException e) {

                }
            }
        }
    }

    class ImageEncoder implements Runnable {
        private Thread thread;
        Uri imageURI;
        String resultString;

        ImageEncoder(Uri uri) {
            imageURI = uri;
        }

        @Override
        public void run() {
            resultString = encodeImage(imageURI);
        }

        public String base64encode() throws InterruptedException {
            thread = new Thread(this);
            thread.start();
            thread.join();
            return resultString;
        }

        private String encodeImage(Uri uri) {
            try {
                InputStream imageStream = getContentResolver().openInputStream(uri);
                final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                selectedImage.compress(Bitmap.CompressFormat.JPEG,100,baos);
                byte[] b = baos.toByteArray();
                String encImage = Base64.encodeToString(b, Base64.DEFAULT);
                return encImage;
            } catch (IOException e) {
                Log.e("BROMIDE", "File not found");
            }
            return null;
        }
    }

}
