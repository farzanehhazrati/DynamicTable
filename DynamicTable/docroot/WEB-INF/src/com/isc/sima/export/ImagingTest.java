package com.isc.sima.export;

import java.awt.*;
import java.awt.image.BufferedImage;
        import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
        import java.io.IOException;
        import java.net.URL;
        import javax.imageio.ImageIO;

public class ImagingTest{

    public static void main(String[] args) throws IOException {
        mergeImageAndText( "d:\\babakhani\\icon\\isc.jpg", "سلام", new Point(400, 30),"d:\\babakhani\\icon\\so2.jpg");

    }

    public static File mergeImageAndText(String imageFilePath,
                                           String text, Point textPosition,String outFilePath) throws IOException {
        BufferedImage im = ImageIO.read(new File(imageFilePath));
        Graphics2D g2 = im.createGraphics();
        g2.setFont(g2.getFont().deriveFont(20f));
        g2.setColor(Color.decode("#4e8fff"));
        g2.drawString(text, textPosition.x, textPosition.y);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(im, "png", baos);
        FileOutputStream fos = new FileOutputStream(outFilePath);
        fos.write(baos.toByteArray());
        fos.close();
        return  new File(outFilePath);
    }
}