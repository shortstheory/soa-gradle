package com.arnavdhamija.bromide;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.Image;
import android.preference.PreferenceManager;

import in.swifiic.plat.helper.andi.AppEndpointContext;
import in.swifiic.plat.helper.andi.Constants;
import in.swifiic.plat.helper.andi.Helper;
import in.swifiic.plat.helper.andi.ui.SwifiicActivity;
import in.swifiic.plat.helper.andi.xml.Action;

import java.util.Date;

/**
 * Created by nic on 25/10/17.
 */

public class ImageSender {
    private Context context;

    ImageSender(Context ctx) {
        context = ctx;
    }

    void sendImage(String encodedImage) {
        if (null != encodedImage) {
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
            String hubAddress = sharedPreferences.getString("hub_address", "");

            Date date = new Date();
            String epochDelta = String.valueOf(date.getTime());

            Action action = new Action("SendBromideImage", new AppEndpointContext("Bromide", "0.1", "55"));
            action.addArgument("encodedImage", encodedImage);
            action.addArgument("fromUser", "motog4+");
            action.addArgument("toUser", hubAddress);
            action.addArgument("sentAt", epochDelta);

            Helper.sendAction(action, hubAddress + "/Bromide", context);
        }
    }
}
