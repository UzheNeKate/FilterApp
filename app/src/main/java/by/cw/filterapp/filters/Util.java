package by.cw.filterapp.filters;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;

import java.util.ArrayList;

public class Util {

    static float[][] GaussMatrix = new float[][]{
            {1, 3, 1},
            {3, 16, 3},
            {1, 3, 1}
    };

    public static Bitmap getGrayscaleImage(Bitmap srcImg) {
        int width, height;
        height = srcImg.getHeight();
        width = srcImg.getWidth();

        Bitmap bmpGrayscale = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bmpGrayscale);
        Paint paint = new Paint();
        ColorMatrix cm = new ColorMatrix();
        cm.setSaturation(0);
        ColorMatrixColorFilter f = new ColorMatrixColorFilter(cm);
        paint.setColorFilter(f);
        c.drawBitmap(srcImg, 0, 0, paint);
        return bmpGrayscale;
    }

    public static ArrayList<ArrayList<Float>> getHsb(Bitmap srcImg) {
        ArrayList<Float> hues = new ArrayList<>();
        ArrayList<Float> saturations = new ArrayList<>();
        ArrayList<Float> brightnesses = new ArrayList<>();

        for (int i = 0; i < srcImg.getHeight(); i++) {
            for (int j = 0; j < srcImg.getWidth(); j++) {
                float[] hsv = new float[3];
                Color.colorToHSV(srcImg.getPixel(j, i), hsv);
                hues.add(hsv[0]);
                saturations.add(hsv[1]);
                brightnesses.add(hsv[2]);
            }
        }

        ArrayList<ArrayList<Float>> hsv = new ArrayList<>();
        hsv.add(hues);
        hsv.add(saturations);
        hsv.add(brightnesses);

        return hsv;
    }

    public static int getMedian(Bitmap srcImg, int x, int y, int radius) {
        ArrayList<Float> hues = new ArrayList<>();
        ArrayList<Float> saturations = new ArrayList<>();
        ArrayList<Float> brightnesses = new ArrayList<>();

        for (int i = -radius; i <= radius; ++i) {
            for (int j = -radius; j <= radius; ++j) {
                if (x + i >= 0 && x + i < srcImg.getWidth() && y + j >= 0 && y + j < srcImg.getHeight()) {
                    float[] hsv = new float[3];
                    Color.colorToHSV(srcImg.getPixel(x + i, y + j), hsv);
                    hues.add(hsv[0]);
                    saturations.add(hsv[1]);
                    brightnesses.add(hsv[2]);
                }
            }
        }
        insertSort(hues);
        insertSort(saturations);
        insertSort(brightnesses);


        int mid = hues.size() / 2;
        return Color.HSVToColor(255, new float[]{
                hues.get(mid),
                saturations.get(mid),
                brightnesses.get(mid)
        });
    }

    public static int getMean(Bitmap srcImg, int x, int y) {
        ArrayList<Float> hues = new ArrayList<>();
        ArrayList<Float> saturations = new ArrayList<>();
        ArrayList<Float> brightnesses = new ArrayList<>();

        for (int i = -1; i <= 1; ++i) {
            for (int j = -1; j <= 1; ++j) {
                if (x + i >= 0 && x + i < srcImg.getWidth() && y + j >= 0 && y + j < srcImg.getHeight()) {
                    float[] hsv = new float[3];
                    Color.colorToHSV(srcImg.getPixel(x + i, y + j), hsv);
                    hues.add(hsv[0]);
                    saturations.add(hsv[1]);
                    brightnesses.add(hsv[2]);
                } else {
                    return srcImg.getPixel(x, y);
                }
            }
        }

        float meanH = 0, meanS = 0, meanB = 0;
        for (int i = 0; i < 9; i++) {
            meanH += GaussMatrix[i % 3][i / 3] * hues.get(i);
            meanS += GaussMatrix[i % 3][i / 3] * saturations.get(i);
            meanB += GaussMatrix[i % 3][i / 3] * brightnesses.get(i);
        }

        meanH /= 32;
        meanS /= 32;
        meanB /= 32;

        float[] hsv = new float[3];
        Color.colorToHSV(srcImg.getPixel(x, y), hsv);
        return Color.HSVToColor(255, new float[]{
                //hsv[0], hsv[1], meanB
                meanH, meanS, meanB
        });
    }

    public static void insertSort(ArrayList<Float> list) {
        for (int i = 1; i < list.size(); i++) {
            float current = list.get(i);
            int j = i - 1;
            while (j >= 0 && current < list.get(j)) {
                list.set(j + 1, list.get(j));
                j--;
            }
            list.set(j + 1, current);
        }
    }

    public static Bitmap createImageFromPixels(int[] pixels, int width, int height) {
        Bitmap newImg = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        newImg.setPixels(pixels, 0, width, 0, 0, width, height);
        return newImg;
    }
}
