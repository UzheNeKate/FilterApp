package by.cw.filterapp.filters;

import android.graphics.Bitmap;
import android.graphics.Color;

import java.util.ArrayList;

public class LinearContrast {
    Bitmap srcImg;

    float min;
    float max;

    public LinearContrast(Bitmap srcImg) {
        this.srcImg = srcImg;
    }

    public Bitmap filterImage() {
        assert (srcImg != null);

        Bitmap src = Util.getGrayscaleImage(srcImg);

        int pixelCount = src.getHeight() * src.getWidth();
        int[] filtered = new int[pixelCount];
        ArrayList<ArrayList<Float>> hsb = Util.getHsb(src);

        min = getMinBrightness(hsb.get(2));
        max = getMaxBrightness(hsb.get(2));


        for (int k = 0; k < pixelCount; k++) {
            float b = hsb.get(2).get(k);
            hsb.get(2).set(k, 1 / (max - min) * (b - min));
        }

        for (int i = 0; i < pixelCount; i++) {
            filtered[i] = Color.HSVToColor(255, new float[]{
                    hsb.get(0).get(i),
                    hsb.get(1).get(i),
                    hsb.get(2).get(i)
            });
        }
        return Util.createImageFromPixels(filtered, src.getWidth(), src.getHeight());
    }

    private float getMinBrightness(ArrayList<Float> brightnesses){
        Float min = brightnesses.get(0);
        for (int i = 1; i < brightnesses.size(); i++) {
            if(min > brightnesses.get(i))
                min = brightnesses.get(i);
        }
        return min;
    }

    private float getMaxBrightness(ArrayList<Float> brightnesses){
        Float max = brightnesses.get(0);
        for (int i = 1; i < brightnesses.size(); i++) {
            if(max < brightnesses.get(i))
                max = brightnesses.get(i);
        }
        return max;
    }
}
