/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vnlocalize.ebooksreader;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

/**
 *
 * @author Wise-SW
 */
public class ChapterProcess {

    public static String getChapterName(String zipPath, ArrayList<Item> itemList, ArrayList<String> itemOrder, int i) {
        String chapterName = "", chapterPath = "";
        String bid = itemOrder.get(i);
        for (Item item : itemList) {
            if (item.getId().equals(bid)) {
                chapterPath = item.getHref();
                break;
            }
        }
        ZipFile zipFile = null;
        Enumeration<? extends ZipEntry> entries = null;
        ZipEntry entry = null;
        try {
            zipFile = new ZipFile(new File(zipPath));
            entries = zipFile.entries();
            while (entries.hasMoreElements()) {
                entry = entries.nextElement();
                if (entry.getName().contains("toc.ncx")) {
                    break;
                }
            }
            BufferedReader reader = new BufferedReader(new InputStreamReader(zipFile.getInputStream(entry),"UTF8"));
            String line = "", navPoint = "",src = "", startText = "<text>", startSrc = "src=\"";

            while (reader.ready()) {
                line = reader.readLine();
                if (line.contains("<navPoint")) {
                    navPoint = "";
                }
                navPoint += line;
                if(line.contains("/navPoint")){
                    navPoint += line;
                    int index = navPoint.indexOf(startSrc)+startSrc.length();
                    if(index > 0){
                        src = navPoint.substring(index, navPoint.indexOf("\"", index+1));
                        if(src.equals(chapterPath)){
                            index = navPoint.indexOf(startText)+startText.length();
                            if(index > 0){
                                chapterName = navPoint.substring(index,navPoint.indexOf("</text>", index+1));
                            }
                            break;
                        }
                    }
                }
            }
        } catch (ZipException ex) {
            Logger.getLogger(ChapterProcess.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ChapterProcess.class.getName()).log(Level.SEVERE, null, ex);
        }
        return chapterName;
    }
}
