package com.universio.humack;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapRegionDecoder;
import android.graphics.Color;
import android.graphics.Rect;
import android.net.Uri;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.io.InputStream;

/**
 * Created by Cyril Humbertclaude on 11/05/2015.
 */
public class Tools {

    /**
     * Show a Toast with a long duration
     * @param context The context
     * @param text The text
     */
    public static void toast(Context context, String text){
        Toast.makeText(context, text, Toast.LENGTH_LONG).show();
    }

    /**
     * Get the pixel color of an image placed into asset at given coordinates
     * @param imageName The image name
     * @param x The X coordinate
     * @param y The Y coordinate
     * @return The RGB number representing the color
     */
    public static int getPixelColor(String imageName, int x, int y) {
        int color = -1;
        Boolean outCoordinate = false;
        if(x < 0 || y < 0)
            outCoordinate = true;
        else {
            try {
                InputStream inputStream = MainActivity.ASSET_MANAGER.open(imageName);
                BitmapRegionDecoder decoder = BitmapRegionDecoder.newInstance(inputStream, true);
                if (decoder != null) {
                    if(x >= decoder.getWidth() || y >= decoder.getHeight())
                        outCoordinate = true;
                    else{
                        Rect region = new Rect(x, y, x + 1, y + 1);
                        Bitmap bitmap = decoder.decodeRegion(region, null);
                        color = bitmap.getPixel(0, 0);
                    }
                    decoder.recycle();
                }
            } catch (Exception e) {
                Log.e("_Tools", ".getPixelColor| Impossible to get pixel of the image '" + imageName + "'. Error: " + e);
            }
        }
        if(outCoordinate)
            Log.w("_Tools", ".getPixelColor| Coordinates [" + x + ", " + y + "] out of image '" + imageName + "'.");

        return color;
    }

    /**
     * Determine if two colors are close
     * @param color1 Color 1
     * @param color2 Color 2
     * @param tolerance Tolerance
     * @return True if colors are close
     */
    public static boolean isCloseColor(int color1, int color2, int tolerance) {
        if (Math.abs (Color.red(color1) - Color.red (color2)) > tolerance )
            return false;
        else if (Math.abs (Color.green (color1) - Color.green (color2)) > tolerance )
            return false;
        else if (Math.abs (Color.blue (color1) - Color.blue (color2)) > tolerance )
            return false;
        return true;
    }

    public static String getLineSeparator(){
        return System.getProperty("line.separator");
    }

    /**
     * Trigger the Html.fromHtml method on all TextView's child and subchilds of a ViewGroup
     * @param viewGroup The ViewGroup
     */
    public static void fromHtml(ViewGroup viewGroup){
        for(int i=0; i<viewGroup.getChildCount(); i++) {
            View view = viewGroup.getChildAt(i);
            if(view instanceof TextView) {
                TextView textView = (TextView)view;
                Spanned html = Html.fromHtml(textView.getText().toString());
                textView.setText(html);
            }else if(view instanceof ViewGroup)
                Tools.fromHtml((ViewGroup)view);
        }
    }

    /**
     * Launch a new ACTION_VIEW Intent to a parsed URL
     * @param url The url where to go, should start by "http(s)://"
     */
    public static void openUrl(Activity activity, String url){
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        activity.startActivity(browserIntent);
    }

    public static int getPixelFromDP(Context context, float dp){
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int)(dp * scale + 0.5f);
    }
}
