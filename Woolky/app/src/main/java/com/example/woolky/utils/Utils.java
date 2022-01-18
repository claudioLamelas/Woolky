package com.example.woolky.utils;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.example.woolky.R;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;

public class Utils {

    public static boolean askForPermission(Activity context, String[] permissions, int code) {
        boolean allGranted = true;
        for (String s : permissions) {
            if (ContextCompat.checkSelfPermission(context, s) == PackageManager.PERMISSION_DENIED) {
                allGranted = false;
                break;
            }
        }
        if (!allGranted)
            ActivityCompat.requestPermissions(context, permissions, code);

        return allGranted;
    }

    public static boolean checkPermission(Activity context, String permission) {
        return ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED;
    }

    public static BitmapDescriptor BitmapFromVector(Drawable vectorDrawable, int color) {
        // below line is use to set bounds to our vector drawable.
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        vectorDrawable.setTint(color);

        // below line is use to create a bitmap for our drawable which we have added.
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);

        // below line is use to add bitmap in our canvas.
        Canvas canvas = new Canvas(bitmap);

        // below line is use to draw our vector drawable in canvas.
        vectorDrawable.draw(canvas);

        // after generating our bitmap we are returning our bitmap.
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    public static Drawable getUserDrawable(FragmentActivity activity) {
        return ContextCompat.getDrawable(activity, R.drawable.ic_android_24dp);
    }
}
