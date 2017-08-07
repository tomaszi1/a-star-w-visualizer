package pl.edu.agh.idziak.asw.visualizer.gui.drawing;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.awt.*;

public class DrawingUtils {
    public static void clipRect(GraphicsContext gc, int x, int y, int width, int height) {
        gc.beginPath();
        gc.rect(x, y, x + width, y + height);
        gc.closePath();
        gc.clip();
    }

    public static void clipRect(Graphics2D g, int x, int y, int width, int height) {
        g.setClip(x, y, width, height);
    }

    public static java.awt.Color toAwtColor(Color c) {
        int alpha = (int) (c.getOpacity() * 255);
        int red = (int) (c.getRed() * 255);
        int green = (int) (c.getGreen() * 255);
        int blue = (int) (c.getBlue() * 255);
        return new java.awt.Color(red, green, blue, alpha);
    }

    public static int getMaxFittingFontSize(Graphics2D g, Font font, String string, int width, int height){
        int minSize = 0;
        int maxSize = 100;
        int curSize = font.getSize();

        while (maxSize - minSize > 2){
            FontMetrics fm = g.getFontMetrics(new Font(font.getName(), font.getStyle(), curSize));
            int fontWidth = fm.stringWidth(string);
            int fontHeight = fm.getLeading() + fm.getMaxAscent() + fm.getMaxDescent();

            if ((fontWidth > width) || (fontHeight > height)){
                maxSize = curSize;
                curSize = (maxSize + minSize) / 2;
            }
            else{
                minSize = curSize;
                curSize = (minSize + maxSize) / 2;
            }
        }

        return curSize;
    }

}
