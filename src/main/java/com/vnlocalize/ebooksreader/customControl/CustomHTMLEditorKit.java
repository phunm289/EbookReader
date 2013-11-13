/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vnlocalize.ebooksreader.customControl;

import java.util.zip.ZipFile;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BoxView;
import javax.swing.text.ComponentView;
import javax.swing.text.Element;
import javax.swing.text.IconView;
import javax.swing.text.LabelView;
import javax.swing.text.StyleConstants;
import javax.swing.text.View;
import javax.swing.text.ViewFactory;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.ParagraphView;

/**
 *
 * @author Wise-SW
 */
public class CustomHTMLEditorKit extends HTMLEditorKit {

    private ZipFile zipFile;

    public CustomHTMLEditorKit(ZipFile zipFile) {
        this.zipFile = zipFile;
    }

    @Override
    public ViewFactory getViewFactory() {
        return new CustomHTMLFactory(zipFile);
    }

    public static class CustomHTMLFactory extends HTMLEditorKit.HTMLFactory {

        /**
         * Creates a view from an element.
         *
         * @param elem the element
         * @return the view
         */
        private ZipFile zipFile;

        public CustomHTMLFactory(ZipFile zipFile) {
            this.zipFile = zipFile;
        }

        @Override
        public View create(Element elem) {
            AttributeSet attrs = elem.getAttributes();
            Object elementName =
                    attrs.getAttribute(AbstractDocument.ElementNameAttribute);
            Object o = (elementName != null)
                    ? null : attrs.getAttribute(StyleConstants.NameAttribute);
            if (o instanceof HTML.Tag) {
                HTML.Tag kind = (HTML.Tag) o;
                if (kind == HTML.Tag.IMG) {
                    return new CustomImageView(elem, zipFile);
                } else {
                    return super.create(elem);
                }
            }
            // If we get here, it's either an element we don't know about
            // or something from StyledDocument that doesn't have a mapping to HTML.
            String nm = (elementName != null) ? (String) elementName
                    : elem.getName();
            if (nm != null) {
                if (nm.equals(AbstractDocument.ContentElementName)) {
                    return new LabelView(elem);
                } else if (nm.equals(AbstractDocument.ParagraphElementName)) {
                    return new ParagraphView(elem);
                } else if (nm.equals(AbstractDocument.SectionElementName)) {
                    return new BoxView(elem, View.Y_AXIS);
                } else if (nm.equals(StyleConstants.ComponentElementName)) {
                    return new ComponentView(elem);
                } else if (nm.equals(StyleConstants.IconElementName)) {
                    return new IconView(elem);
                }
            }

            // default to text display
            return new LabelView(elem);
        }
    }

}
