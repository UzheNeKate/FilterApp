package by.cw.filterapp.filters;


import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.util.Log;

import java.util.ArrayList;

public class BarGraph {
    Bitmap srcImg;

    public BarGraph(Bitmap srcImg) {
        this.srcImg = srcImg;
    }

    public Bitmap equalizeRgb() {
        Bitmap src = Util.getGrayscaleImage(srcImg);
        int pixelCount = src.getWidth() * src.getHeight();
        int[][] histogram = new int[3][256];

        for (int x = 0; x < src.getWidth(); x++) {
            for (int y = 0; y < src.getHeight(); y++) {
                int rgb = src.getPixel(x, y);
                histogram[0][Color.red(rgb)]++;
                histogram[1][Color.green(rgb)]++;
                histogram[2][Color.blue(rgb)]++;
            }
        }

        int[][] chistogram = new int[3][256];
        chistogram[0][0] = histogram[0][0];
        chistogram[1][0] = histogram[1][0];
        chistogram[2][0] = histogram[2][0];
        for (int i = 1; i < 256; i++) {
            chistogram[0][i] = chistogram[0][i - 1] + histogram[0][i];
            chistogram[1][i] = chistogram[1][i - 1] + histogram[1][i];
            chistogram[2][i] = chistogram[2][i - 1] + histogram[2][i];
        }

        float[][] arr = new float[3][256];
        for (int i = 0; i < 256; i++) {
            arr[0][i] = (float) ((chistogram[0][i] * 255.0) / (float) pixelCount);
            arr[1][i] = (float) ((chistogram[1][i] * 255.0) / (float) pixelCount);
            arr[1][i] = (float) ((chistogram[2][i] * 255.0) / (float) pixelCount);
        }

        int[] filtered = new int[pixelCount];
        for (int k = 0; k < pixelCount; k++) {
            int i = k % src.getWidth();
            int j = k / src.getWidth();
            int rgb = src.getPixel(i, j);
            int argb = Color.rgb((int) arr[0][Color.red(rgb)], (int) arr[0][Color.green(rgb)],
                    (int) arr[0][Color.blue(rgb)]);
            float[] hsv = new float[3];
            Color.colorToHSV(argb, hsv);
            filtered[k] = Color.HSVToColor(255, hsv);
        }
        return Util.createImageFromPixels(filtered, src.getWidth(), src.getHeight());
    }
}

