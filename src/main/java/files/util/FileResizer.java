package files.util;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class FileResizer {

    public static final int WIDTH_MAX = 1600;
    public static final int HEIGHT_MAX = 900;
    public static final String JPG = "jpg";

    public static boolean needsResize(BufferedImage image) {
        return needsResize(image.getWidth(), image.getHeight());
    }

    private static boolean needsResize(int width, int height) {
        return width > WIDTH_MAX || height > HEIGHT_MAX;
    }

    public static BufferedImage dynamicResize(BufferedImage img) throws IOException {
        int width = img.getWidth();
        int height = img.getHeight();
        double percentOfOriginal = maxResizePercent(width, height);
        int newWidth = (int) (percentOfOriginal * width);
        int newHeight = (int) (percentOfOriginal * height);
        return resize(img, newWidth, newHeight);
    }

    private static BufferedImage resize(BufferedImage img, int newWidth, int newHeight) {
        BufferedImage resizedImage = new BufferedImage(newWidth, newHeight, defineType(img));
        Graphics2D g2d = resizedImage.createGraphics();
        Image tmp = img.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);
        g2d.drawImage(tmp, 0, 0, newWidth, newHeight, null);
        g2d.dispose();
        return resizedImage;
    }

    private static int defineType(BufferedImage img) {
        return (img.getTransparency() == Transparency.OPAQUE) ? BufferedImage.TYPE_INT_RGB : BufferedImage.TYPE_INT_ARGB;
    }

    public static ByteArrayOutputStream getByteArrayOutputStream(BufferedImage img) throws IOException {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        ImageIO.write(img, JPG, os);
        return os;
    }

    private static double maxResizePercent(int width, int height) {
        return 1. - getNeededPercentChange(width, height);
    }

    private static double getNeededPercentChange(int width, int height) {
        int heightOver = height - HEIGHT_MAX;
        int widthOver = width - WIDTH_MAX;
        double heightOverPercent = (double) heightOver / (heightOver + HEIGHT_MAX);
        double widthOverPercent = (double) widthOver / (widthOver + WIDTH_MAX);
        if (heightOverPercent > widthOverPercent) return heightOverPercent;
        return widthOverPercent;
    }
}
