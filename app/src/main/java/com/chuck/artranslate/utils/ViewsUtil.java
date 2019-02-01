package com.chuck.artranslate.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.AnimRes;
import android.support.annotation.ColorRes;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;

import com.chuck.artranslate.R;

public abstract class ViewsUtil {

    public static View removeParentView(View view) {
        ViewGroup parent = (ViewGroup) view.getParent();
        if (parent != null) {
            parent.removeView(view);
        }
        return view;
    }

    public static void makeVisible(View view) {
        if (view != null && !isVisible(view)) {
            view.setVisibility(View.VISIBLE);
        }
    }

    public static void makeVisible(View view, Context context, @AnimRes int anim) {
        if (view != null && !isVisible(view)) {
            view.startAnimation(AnimationUtils.loadAnimation(context, anim));
            makeVisible(view);
        }
    }

    public static void makeInvisible(View view) {
        if (view != null && !isInvisible(view)) {
            view.setVisibility(View.INVISIBLE);
        }
    }

    public static void makeInvisible(View view, Context context, @AnimRes int anim) {
        if (view != null && !isInvisible(view)) {
            view.startAnimation(AnimationUtils.loadAnimation(context, anim));
            makeInvisible(view);
        }
    }

    public static void makeGone(View view) {
        if (view != null && !isGone(view)) {
            view.setVisibility(View.GONE);
        }
    }

    public static void makeGone(View view, Context context, @AnimRes int anim) {
        if (view != null && !isGone(view)) {
            view.startAnimation(AnimationUtils.loadAnimation(context, anim));
            makeGone(view);
        }
    }

    public static boolean isVisible(View view) {
        return view != null && view.getVisibility() == View.VISIBLE;
    }

    public static boolean isInvisible(View view) {
        return view != null && view.getVisibility() == View.INVISIBLE;
    }

    private static boolean isGone(View view) {
        return view != null && view.getVisibility() == View.GONE;
    }

    public static int getColor(Context context, @ColorRes int color) {
        return ContextCompat.getColor(context, color);
    }

    public static AlertDialog progressDialog(Activity activity) {
        AlertDialog alertDialog = new AlertDialog.Builder(activity)
                .setCancelable(false)
                .setView(activity.getLayoutInflater().inflate(R.layout.progress_dialog, null))
                .create();
        removeWhiteBg(alertDialog.getWindow());
        return alertDialog;

    }

    public static AlertDialog.Builder customDialog(Activity activity) {
        return new AlertDialog.Builder(activity);
    }

    private static void removeWhiteBg(Window window) {
        if(window != null) {
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
    }


}
