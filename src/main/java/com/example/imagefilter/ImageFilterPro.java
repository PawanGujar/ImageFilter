package com.example.imagefilter;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * ImageFilterPro — advanced image filters (grayscale, sepia, blur, sharpen, edge detect, brightness/contrast).
 *
 * @author Pawan Gujar
 * @version 2.0
 */
public final class ImageFilterPro {

    private ImageFilterPro() {}

    public static BufferedImage grayscale(BufferedImage src) {
        BufferedImage out = new BufferedImage(src.getWidth(), src.getHeight(), BufferedImage.TYPE_INT_ARGB);
        for (int y = 0; y < src.getHeight(); y++) {
            for (int x = 0; x < src.getWidth(); x++) {
                Color c = new Color(src.getRGB(x, y), true);
                int gray = (int) (0.3 * c.getRed() + 0.59 * c.getGreen() + 0.11 * c.getBlue());
                Color g = new Color(gray, gray, gray, c.getAlpha());
                out.setRGB(x, y, g.getRGB());
            }
        }
        return out;
    }

    public static BufferedImage sepia(BufferedImage src) {
        BufferedImage out = new BufferedImage(src.getWidth(), src.getHeight(), BufferedImage.TYPE_INT_ARGB);
        for (int y = 0; y < src.getHeight(); y++) {
            for (int x = 0; x < src.getWidth(); x++) {
                Color c = new Color(src.getRGB(x, y), true);
                int r = c.getRed(), g = c.getGreen(), b = c.getBlue();
                int tr = clamp((int)(0.393*r + 0.769*g + 0.189*b));
                int tg = clamp((int)(0.349*r + 0.686*g + 0.168*b));
                int tb = clamp((int)(0.272*r + 0.534*g + 0.131*b));
                out.setRGB(x, y, new Color(tr, tg, tb, c.getAlpha()).getRGB());
            }
        }
        return out;
    }

    public static BufferedImage adjustBrightnessContrast(BufferedImage src, int brightness, double contrast) {
        BufferedImage out = new BufferedImage(src.getWidth(), src.getHeight(), BufferedImage.TYPE_INT_ARGB);
        for (int y = 0; y < src.getHeight(); y++) {
            for (int x = 0; x < src.getWidth(); x++) {
                Color c = new Color(src.getRGB(x, y), true);
                int r = clamp((int)((((c.getRed() - 128) * contrast) + 128) + brightness));
                int g = clamp((int)((((c.getGreen() - 128) * contrast) + 128) + brightness));
                int b = clamp((int)((((c.getBlue() - 128) * contrast) + 128) + brightness));
                out.setRGB(x, y, new Color(r, g, b, c.getAlpha()).getRGB());
            }
        }
        return out;
    }

    public static BufferedImage blur(BufferedImage src, int radius) {
        int size = radius * 2 + 1;
        int kernelSum = size * size;
        int w = src.getWidth(), h = src.getHeight();
        BufferedImage out = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);

        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                long r=0,g=0,b=0,a=0;
                for (int ky=-radius; ky<=radius; ky++) {
                    for (int kx=-radius; kx<=radius; kx++) {
                        int nx=x+kx, ny=y+ky;
                        if(nx<0||nx>=w||ny<0||ny>=h) continue;
                        Color c = new Color(src.getRGB(nx, ny), true);
                        r+=c.getRed(); g+=c.getGreen(); b+=c.getBlue(); a+=c.getAlpha();
                    }
                }
                out.setRGB(x,y,new Color((int)(r/kernelSum),(int)(g/kernelSum),(int)(b/kernelSum),(int)(a/kernelSum)).getRGB());
            }
        }
        return out;
    }

    public static BufferedImage sharpen(BufferedImage src) {
        float[][] kernel = {
                {0, -1, 0},
                {-1, 5, -1},
                {0, -1, 0}
        };
        return applyKernel(src, kernel);
    }

    public static BufferedImage edgeDetect(BufferedImage src) {
        float[][] kernel = {
                {-1, -1, -1},
                {-1, 8, -1},
                {-1, -1, -1}
        };
        return applyKernel(src, kernel);
    }

    private static BufferedImage applyKernel(BufferedImage src, float[][] kernel) {
        int w = src.getWidth(), h = src.getHeight();
        BufferedImage out = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        int kSize = kernel.length, kHalf = kSize/2;

        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                float r=0,g=0,b=0;
                for (int ky = -kHalf; ky <= kHalf; ky++) {
                    for (int kx = -kHalf; kx <= kHalf; kx++) {
                        int nx=x+kx, ny=y+ky;
                        if(nx<0||nx>=w||ny<0||ny>=h) continue;
                        Color c = new Color(src.getRGB(nx, ny), true);
                        float kval = kernel[ky+kHalf][kx+kHalf];
                        r += c.getRed() * kval;
                        g += c.getGreen() * kval;
                        b += c.getBlue() * kval;
                    }
                }
                out.setRGB(x, y, new Color(clamp((int)r), clamp((int)g), clamp((int)b)).getRGB());
            }
        }
        return out;
    }

    private static int clamp(int v){return v<0?0:Math.min(255,v);}
}

