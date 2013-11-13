/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vnlocalize.ebooksreader.customControl;

import com.vnlocalize.ebooksreader.Item;
import com.vnlocalize.ebooksreader.MainFrame;
import java.util.ArrayList;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

/**
 *
 * @author Wise-SW
 */
public class CustomHyperlinkListener implements HyperlinkListener {

    ArrayList<Item> itemList;
    ArrayList<String> itemOrder;
    MainFrame f;
    
    public CustomHyperlinkListener(ArrayList<Item> itemList, ArrayList<String> itemOrder, MainFrame f) {
        this.itemList = itemList;
        this.itemOrder = itemOrder;
        this.f = f;
    }

    @Override
    public void hyperlinkUpdate(HyperlinkEvent e) {
        String url = e.getDescription();
        if(url.contains("#")){
            url = url.substring(0, url.indexOf("#"));
        }
        if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
            for (Item item : itemList) {
                if (url.equals(item.getHref()
                        .substring(item.getHref().lastIndexOf("/")+1, item.getHref().length()))) {
                    String id = item.getId();
                    for (String string : itemOrder) {
                        if (string.equals(id)) {
                            int i = itemOrder.indexOf(string);
                            f.setReadingPage(i);
                            break;
                        }
                    }
                    break;
                } else {
                }
            }
        }
    }
}
