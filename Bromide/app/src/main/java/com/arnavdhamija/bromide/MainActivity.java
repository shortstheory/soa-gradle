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
import android.widget.TextView;

import com.bumptech.glide.Glide;

import org.w3c.dom.Text;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import in.swifiic.plat.helper.andi.ui.SwifiicActivity;

public class MainActivity extends SwifiicActivity {

    final static int PICK_IMAGE = 1;
    String encodedString = null;
    boolean payloadReady = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        Button bromideButton = (Button) findViewById(R.id.bromide_button);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        final String hubAddress = sharedPreferences.getString("hub_address", "");

        toolbar.setTitle("Bromide");

        bromideButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("BROMIDE", "HUB_ADDR" + hubAddress);

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
                if (payloadReady) {

                }
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
//                displayImage.setImageURI(result);

                Glide.with(this).load(result).into(displayImage);
                ImageEncoder imageEncoder = new ImageEncoder(this, result);
                try {
                    imageEncoder.base64encode();
                    payloadReady = true;
                } catch (InterruptedException e) {
                }
            }
        }
    }
}