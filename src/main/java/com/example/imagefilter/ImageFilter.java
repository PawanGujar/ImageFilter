package com.example.imagefilter;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * ImageFilter — provides simple image filters: grayscale, sepia, and blur.
 *
 * <p>All methods operate on and return a new {@link BufferedImage} so original image is not mutated.</p>
 *
 * @author Pawan Gujar
 * @version 1.0
 */
public final class ImageFilter {

    private ImageFilter() { /* utility */ }

    /**
     * Returns a new BufferedImage converted to grayscale using luminance method.
     *
     * @param src source image (must not be null)
     * @return new grayscale image
     */
    public static BufferedImage applyGrayscale(BufferedImage src) {
        if (src == null) throw new IllegalArgumentException("Source image cannot be null");
        BufferedImage out = new BufferedImage(src.getWidth(), src.getHeight(), BufferedImage.TYPE_INT_ARGB);
        for (int y = 0; y < src.getHeight(); y++) {
            for (int x = 0; x < src.getWidth(); x++) {
                int rgba = src.getRGB(x, y);
                Color c = new Color(rgba, true);
                int gray = (int) Math.round(0.299 * c.getRed() + 0.587 * c.getGreen() + 0.114 * c.getBlue());
                Color g = new Color(gray, gray, gray, c.getAlpha());
                out.setRGB(x, y, g.getRGB());
            }
        }
        return out;
    }

    /**
     * Returns a new BufferedImage with sepia tone applied.
     *
     * @param src source image (must not be null)
     * @return new sepia image
     */
    public static BufferedImage applySepia(BufferedImage src) {
        if (src == null) throw new IllegalArgumentException("Source image cannot be null");
        BufferedImage out = new BufferedImage(src.getWidth(), src.getHeight(), BufferedImage.TYPE_INT_ARGB);
        for (int y = 0; y < src.getHeight(); y++) {
            for (int x = 0; x < src.getWidth(); x++) {
                int rgba = src.getRGB(x, y);
                Color c = new Color(rgba, true);
                int r = c.getRed();
                int g = c.getGreen();
                int b = c.getBlue();

                int tr = clamp((int) Math.round(0.393 * r + 0.769 * g + 0.189 * b));
                int tg = clamp((int) Math.round(0.349 * r + 0.686 * g + 0.168 * b));
                int tb = clamp((int) Math.round(0.272 * r + 0.534 * g + 0.131 * b));

                Color s = new Color(tr, tg, tb, c.getAlpha());
                out.setRGB(x, y, s.getRGB());
            }
        }
        return out;
    }

    /**
     * Returns a new BufferedImage blurred using a simple box blur (kernel size 3).
     *
     * @param src source image (must not be null)
     * @return new blurred image
     */
    public static BufferedImage applyBlur(BufferedImage src) {
        if (src == null) throw new IllegalArgumentException("Source image cannot be null");
        int w = src.getWidth();
        int h = src.getHeight();
        BufferedImage out = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);

        int[][] kernel = {
                {1,1,1},
                {1,1,1},
                {1,1,1}
        };
        int kSum = 9;

        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                long rSum=0, gSum=0, bSum=0, aSum=0;
                for (int ky = -1; ky <= 1; ky++) {
                    for (int kx = -1; kx <= 1; kx++) {
                        int nx = x + kx;
                        int ny = y + ky;
                        if (nx < 0 || nx >= w || ny < 0 || ny >= h) continue;
                        int rgba = src.getRGB(nx, ny);
                        Color c = new Color(rgba, true);
                        rSum += c.getRed() * kernel[ky+1][kx+1];
                        gSum += c.getGreen() * kernel[ky+1][kx+1];
                        bSum += c.getBlue() * kernel[ky+1][kx+1];
                        aSum += c.getAlpha() * kernel[ky+1][kx+1];
                    }
                }
                int r = clamp((int) Math.round((double) rSum / kSum));
                int g = clamp((int) Math.round((double) gSum / kSum));
                int b = clamp((int) Math.round((double) bSum / kSum));
                int a = clamp((int) Math.round((double) aSum / kSum));
                Color c = new Color(r,g,b,a);
                out.setRGB(x,y,c.getRGB());
            }
        }
        return out;
    }

    private static int clamp(int v) {
        if (v < 0) return 0;
        if (v > 255) return 255;
        return v;
    }
}

