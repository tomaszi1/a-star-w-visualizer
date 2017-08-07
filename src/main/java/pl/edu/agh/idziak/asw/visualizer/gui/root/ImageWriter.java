package pl.edu.agh.idziak.asw.visualizer.gui.root;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.WritableImage;

import javax.imageio.ImageIO;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.String.format;
import static java.util.Comparator.naturalOrder;

/**
 * Created by Tomasz on 04.03.2017.
 */
public class ImageWriter {

    public static final Path DEFAULT_SNAPSHOT_DIR_PATH = Paths.get("SNAPSHOTS");

    public static final String DEFAULT_FILENAME_BASE = "SNAPSHOT";
    public static final Pattern DEFAULT_FILENAME_PNG_PATTERN =
            Pattern.compile("^" + DEFAULT_FILENAME_BASE + "([\\d]{3})\\.png$");
    public static final Pattern DEFAULT_FILENAME_SVG_PATTERN =
            Pattern.compile("^" + DEFAULT_FILENAME_BASE + "([\\d]{3})\\.svg$");


    public void writeImageToDefaultLocation(WritableImage writableImage) {
        RenderedImage renderedImage = SwingFXUtils.fromFXImage(writableImage, null);

        if (!Files.isDirectory(DEFAULT_SNAPSHOT_DIR_PATH)) {
            createDirectory();
        }

        try {
            int newFileOrdinal = getNewFileOrdinal(false);
            Path newFilePath = Paths.get(DEFAULT_SNAPSHOT_DIR_PATH.toString(),
                    format("%s%03d.png", DEFAULT_FILENAME_BASE, newFileOrdinal));
            ImageIO.write(renderedImage, "png", newFilePath.toFile());
        } catch (IOException e) {
            throw new RuntimeException("Could not save image to file", e);
        }
    }

    private static int getNewFileOrdinal(boolean isSvg) throws IOException {
        return Files.list(DEFAULT_SNAPSHOT_DIR_PATH)
                .map(path -> path.getFileName().toString())
                .map(input -> isSvg ?
                        DEFAULT_FILENAME_SVG_PATTERN.matcher(input)
                        : DEFAULT_FILENAME_PNG_PATTERN.matcher(input))
                .filter(Matcher::matches)
                .map(matcher -> matcher.group(1))
                .map(Integer::parseInt)
                .max(naturalOrder())
                .map(maxExistingOrdinal -> maxExistingOrdinal + 1)
                .orElse(0);
    }

    private static void createDirectory() {
        try {
            Files.createDirectory(DEFAULT_SNAPSHOT_DIR_PATH);
        } catch (IOException e) {
            throw new RuntimeException("Could not create directory: " + e.getMessage(), e);
        }
    }

    public void writeImageToFile(WritableImage writableImage, File file) {
        RenderedImage renderedImage = SwingFXUtils.fromFXImage(writableImage, null);
        try {
            ImageIO.write(renderedImage, "png", file);
        } catch (IOException e) {
            throw new RuntimeException(format("Could not save image to file %s.", file.getPath()), e);
        }
    }

    public void writeSvgToDefaultLocation(byte[] svgBytes) {
        if (!Files.isDirectory(DEFAULT_SNAPSHOT_DIR_PATH)) {
            createDirectory();
        }

        try {
            int newFileOrdinal = getNewFileOrdinal(true);
            Path newFilePath = Paths.get(DEFAULT_SNAPSHOT_DIR_PATH.toString(),
                    format("%s%03d.svg", DEFAULT_FILENAME_BASE, newFileOrdinal));
            Files.write(newFilePath, svgBytes);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
