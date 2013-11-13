/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vnlocalize.ebooksreader.customControl;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import javax.imageio.ImageIO;
import javax.swing.text.Element;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.ImageView;

/**
 *
 * @author Wise-SW
 */
public class CustomImageView extends ImageView {

    private String imageUrl = "";
    private String oldImgUrl = "";
    private ZipFile zipFile;
    private Image image;
    int a = 0;

    public CustomImageView(Element elem, ZipFile zipFile) {
        super(elem);
        this.zipFile = zipFile;
    }

    @Override
    public URL getImageURL() {
        String src = (String) getElement().getAttributes().
                getAttribute(HTML.Attribute.SRC);
        imageUrl = src.substring(src.lastIndexOf("/"), src.length());
        if (src == null) {
            return null;
        }

        URL reference = ((HTMLDocument) getDocument()).getBase();
        try {
            URL u = new URL(reference, src);
            return u;
        } catch (MalformedURLException e) {
            return null;
        }

    }

    @Override
    public Image getImage() {

        if (oldImgUrl == null ? imageUrl == null : oldImgUrl.equals(imageUrl)) {
            return image;
        } else {
            BufferedImage bufferedImage = null;
            Enumeration<? extends ZipEntry> entries = zipFile.entries();
            ZipEntry entry = null;
            while (entries.hasMoreElements()) {
                entry = entries.nextElement();
                if (entry.getName().contains(imageUrl)) {
                    break;
                }
            }
            try {
                bufferedImage = ImageIO.read(zipFile.getInputStream(entry));
                image = bufferedImage;
            } catch (IOException ex) {
                Logger.getLogger(CustomImageView.class.getName()).log(Level.SEVERE, null, ex);
            }
            oldImgUrl = imageUrl;
            return bufferedImage;
        }
    }
}
