import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class PGMReader {

    public static BufferedImage readPGM(String filename) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(filename));

        // Read the magic number
        String line = readNonCommentLine(br);
        if (!line.equals("P2")) {
            br.close();
            throw new IOException("Invalid PGM file: incorrect magic number");
        }

        // Read the width and height of the image
        line = readNonCommentLine(br);
        String[] dimensions = line.trim().split("\\s+");
        while (dimensions.length < 2) {
            // If width and height are not on the same line
            String nextLine = readNonCommentLine(br);
            line += " " + nextLine;
            dimensions = line.trim().split("\\s+");
        }
        int width = Integer.parseInt(dimensions[0]);
        int height = Integer.parseInt(dimensions[1]);

        // Read the maximum gray value
        line = readNonCommentLine(br);
        int maxGray = Integer.parseInt(line.trim());

        // Read pixel data
        List<Integer> pixelValues = new ArrayList<>();
        while ((line = br.readLine()) != null) {
            line = line.trim();
            if (line.startsWith("#") || line.isEmpty()) {
                continue; // Skip comments and empty lines
            }
            String[] tokens = line.split("\\s+");
            for (String token : tokens) {
                if (!token.isEmpty()) {
                    pixelValues.add(Integer.parseInt(token));
                }
            }
        }
        br.close();

        if (pixelValues.size() != width * height) {
            throw new IOException("Invalid PGM file: pixel count does not match width and height");
        }

        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
        int idx = 0;
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int gray = pixelValues.get(idx++);
                // Scale gray value to 0-255
                int scaledGray = (gray * 255) / maxGray;
                //Bitshit the gray value into each color channel, in order to greate a grayscaled image, which can display RGB...
                int rgb = (scaledGray << 16) | (scaledGray << 8) | scaledGray;
                image.setRGB(x, y, rgb);
            }
        }
        return image;
    }

    // Helper method to read non-commented lines
    private static String readNonCommentLine(BufferedReader br) throws IOException {
        String line;
        do {
            line = br.readLine();
            if (line == null) {
                throw new IOException("Unexpected end of file");
            }
            line = line.trim();
        } while (line.startsWith("#") || line.isEmpty());
        return line;
    }
}
