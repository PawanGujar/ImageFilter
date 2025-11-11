import com.example.imagefilter.ImageFilter;
import org.junit.jupiter.api.Test;

import java.awt.*;
import java.awt.image.BufferedImage;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for ImageFilter methods.
 */
public class ImageFilterTest {

    /** Creates a small test image (2x2) with known colors. */
    private BufferedImage createTestImage() {
        BufferedImage img = new BufferedImage(2,2, BufferedImage.TYPE_INT_ARGB);
        img.setRGB(0,0, new Color(255,0,0).getRGB()); // red
        img.setRGB(1,0, new Color(0,255,0).getRGB()); // green
        img.setRGB(0,1, new Color(0,0,255).getRGB()); // blue
        img.setRGB(1,1, new Color(255,255,255).getRGB()); // white
        return img;
    }

    @Test
    void testGrayscaleMakesColorsGray() {
        BufferedImage src = createTestImage();
        BufferedImage g = ImageFilter.applyGrayscale(src);
        // check that each pixel has R==G==B
        for (int y=0;y<2;y++) for (int x=0;x<2;x++) {
            Color c = new Color(g.getRGB(x,y), true);
            assertEquals(c.getRed(), c.getGreen());
            assertEquals(c.getGreen(), c.getBlue());
        }
    }

    @Test
    void testSepiaChangesColors() {
        BufferedImage src = createTestImage();
        BufferedImage s = ImageFilter.applySepia(src);
        // At least one pixel must differ from original
        boolean changed = false;
        for (int y=0;y<2;y++) for (int x=0;x<2;x++) {
            if (s.getRGB(x,y) != src.getRGB(x,y)) { changed = true; break; }
        }
        assertTrue(changed);
    }

    @Test
    void testBlurSmoothsColors() {
        BufferedImage src = createTestImage();
        BufferedImage b = ImageFilter.applyBlur(src);
        // blurred pixel should not be identical to at least one original pixel
        boolean different = false;
        for (int y=0;y<2;y++) for (int x=0;x<2;x++) {
            if (b.getRGB(x,y) != src.getRGB(x,y)) { different = true; break; }
        }
        assertTrue(different);
    }
}
