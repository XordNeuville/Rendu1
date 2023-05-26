
import java.awt.*;
import java.awt.image.*;
import java.io.*;
import javax.imageio.ImageIO;

/**
 * 2D Texture class.
 */
public class Texture {
    int width;
    int height;
    BufferedImage image;

    /**
     * Constructs a new Texture with the content of the image at @path.
     */ 
    public Texture (String path) throws Exception {
        image = ImageIO.read (new File (path));
        width = image.getWidth ();
        height = image.getHeight ();
    }

    /**
     * Samples the texture at texture coordinates (u,v), using nearest neighboor interpolation
     * u and v and wrapped around to [0,1].
     */ 
    public Color sample (double u, double v) {
        double u1 = (u * width) % width;
        double v1 = (v * height) % height;
        Color c = new Color (image.getRGB ((int)u1, (int)v1));
        
        return c;
        
    }
}
