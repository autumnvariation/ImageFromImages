import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;

public class ImageFromImagesFrame {
    public static int WIDTH = 60;
    public static int HEIGHT = 60;

    public ImageFromImagesFrame(File imagesDirectory, File image) throws IOException {
        HashMap<Color, BufferedImage> rgbToImage = new HashMap<>();
        addImagesToMap(rgbToImage, imagesDirectory);
        BufferedImage bufferedImage = ImageIO.read(image.listFiles()[0]);
        BufferedImage outputImage = new BufferedImage(1920, 1080, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2 = (Graphics2D) outputImage.getGraphics();
        for (int x = 0; x < WIDTH; x++) {
            for (int y = 0; y < HEIGHT; y++) {
                System.out.println(x + " " + y);
                Color color = getAverageColor(bufferedImage, (1920 / WIDTH) * x, (1080 / HEIGHT) * y, (1920 / WIDTH), (1080 / HEIGHT));
                BufferedImage innerImage = getClosestImage(color, rgbToImage);

                assert innerImage != null;
                g2.drawImage(innerImage.getScaledInstance(1920 / WIDTH, (1080 / HEIGHT), Image.SCALE_DEFAULT), (1920 / WIDTH) * x, (1080 / HEIGHT) * y, null);
            }
        }

        File imageFile = new File("output/output.png");
        int i = 1;
        while(imageFile.exists()){
            imageFile = new File("output/output" + ++i + ".png");
        }

        ImageIO.write(outputImage, "png", imageFile);
    }

    private BufferedImage getClosestImage(Color color, HashMap<Color, BufferedImage> rgbToImage) {
        int minDiff = Integer.MAX_VALUE;
        Color closestColor = null;
        for (Color key : rgbToImage.keySet()) {
            int currentDif = 0;
            currentDif += Math.abs(key.getRed() - color.getRed());
            currentDif += Math.abs(key.getGreen() - color.getGreen());
            currentDif += Math.abs(key.getBlue() - color.getBlue());
            if (minDiff > currentDif) {
                closestColor = key;
                minDiff = currentDif;
            }
        }

        return rgbToImage.get(closestColor);
    }

    private Color getAverageColor(BufferedImage image, int x1, int y1, int width, int height) {
        long sumR = 0, sumG = 0, sumB = 0;
        for (int x = x1; x < x1 + width; x++) {
            for (int y = y1; y < y1 + height; y++) {
                Color pixel = new Color(image.getRGB(x, y));
                sumR += pixel.getRed();
                sumG += pixel.getGreen();
                sumB += pixel.getBlue();
            }
        }
        int totalPxs = width * height;
        return new Color((int) (sumR / totalPxs), (int) (sumG / totalPxs), (int) (sumB / totalPxs));
    }

    private void addImagesToMap(HashMap<Color, BufferedImage> rgbToImage, File imagesDirectory) throws IOException {
        for (File file : imagesDirectory.listFiles()) {
            if (file.isDirectory()){
                for (File listFile : file.listFiles()) {
                    BufferedImage image2 = ImageIO.read(listFile);
                    rgbToImage.put(getAverageColor(image2), image2);
                }
            }
            else{
                BufferedImage image = ImageIO.read(file);
                rgbToImage.put(getAverageColor(image), image);
            }
        }
    }

    private Color getAverageColor(BufferedImage image) {
        long sumR = 0, sumG = 0, sumB = 0;
        for (int x = 0; x < image.getWidth(); x++) {
            for (int y = 0; y < image.getHeight(); y++) {
                Color pixel = new Color(image.getRGB(x, y));
                sumR += pixel.getRed();
                sumG += pixel.getGreen();
                sumB += pixel.getBlue();
            }
        }
        int totalPxs = image.getWidth() * image.getHeight();

        return new Color((int) (sumR / totalPxs), (int) (sumG / totalPxs), (int) (sumB / totalPxs));
    }

    public static void main(String[] args) throws IOException {
        File directory = new File("res/images");
        File image = new File("res/image");
        new ImageFromImagesFrame(directory, image);
    }
}
