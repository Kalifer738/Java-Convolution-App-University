import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class PGMWriter {
    public static void writePGM(BufferedImage image, File filename) throws IOException {
        int width = image.getWidth();
        int height = image.getHeight();

        int maxGray = 255; // 8-bit grayscale?

        // Open the file for writing
        BufferedWriter bw = new BufferedWriter(new FileWriter(filename));

        try {
            // Write the header
            bw.write("P2");
            bw.newLine();
            bw.write("# Created by Kostadinov");
            bw.newLine();
            bw.write(width + " " + height);
            bw.newLine();
            bw.write(String.valueOf(maxGray));
            bw.newLine();

            // Write pixel data
            for (int y = 0; y < height; y++) {
                StringBuilder sb = new StringBuilder();
                for (int x = 0; x < width; x++) {
                    // Get the grayscale value
                    int rgb = image.getRGB(x, y);

                    // The image might be colored...
                    int red = (rgb >> 16) & 0xFF;
                    int green = (rgb >> 8) & 0xFF;
                    int blue = rgb & 0xFF;

                    // https://goodcalculators.com/rgb-to-grayscale-conversion-calculator/
                    // Y = 0.299 R + 0.587 G + 0.114 B
                    int gray = (int)(0.299 * red + 0.587 * green + 0.114 * blue);

                    sb.append(gray).append(' ');
                }
                bw.write(sb.toString().trim());
                bw.newLine();
            }
        }
        catch(Exception e) {
            throw e;
        }
        finally {
            bw.close();
        }
    }
}
