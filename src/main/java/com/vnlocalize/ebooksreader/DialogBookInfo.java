/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vnlocalize.ebooksreader;

import com.vnlocalize.ebooksreader.customControl.MyLabel;
import com.vnlocalize.ebooksreader.db.Database;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;
import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.KeyStroke;

/**
 *
 * @author Wise-SW
 */
public class DialogBookInfo extends javax.swing.JDialog {
    MyLabel book;
    /**
     * A return status code - returned if Cancel button has been pressed
     */
    public static final int RET_CANCEL = 0;
    /**
     * A return status code - returned if OK button has been pressed
     */
    public static final int RET_OK = 1;

    /**
     * Creates new form DialogBookInfo
     */
    public DialogBookInfo(java.awt.Frame parent, boolean modal,MyLabel book) {
        super(parent, modal);
        initComponents();
        setSize(500,350);
        this.book = book;
        try {
            ResultSet rs = new Database().getBook(book.getId());
            String path = "";
            while(rs.next()){
                this.setTitle(rs.getString("name"));
                lblBookName.setText("<html><h2>"+rs.getString("name")+"</h2></html>");
                lblAuthor.setText(lblAuthor.getText()+rs.getString("author"));
                lblAddedDate.setText("<html>"+lblAddedDate.getText()+rs.getString("addedDate")+"</html>");
                lblLastRead.setText("<html>"+lblLastRead.getText()+rs.getString("lastRead")+"</html>");
                lblPath.setText("<html>"+lblPath.getText()+rs.getString("path")+"</html>");
                lblReadTime.setText("<html>"+lblReadTime.getText()+""+rs.getInt("readTime")+"</html>");
                path = rs.getString("path");
            }
            File file = new File(path);
            ZipFile zipFile = new ZipFile(file);
            Enumeration<? extends ZipEntry> zipEntries = zipFile.entries();
            ZipEntry entry = null;
            while (zipEntries.hasMoreElements()) {
                if ((entry = zipEntries.nextElement()).getName().contains("content.opf")) {
                    break;
                }
            }

            BufferedReader reader = new BufferedReader(new InputStreamReader(zipFile.getInputStream(entry), "UTF8"));
            String line = "";
            String imgPath = "";
            while (reader.ready()) {
                line = reader.readLine();
                if (line.contains("<item") && line.contains("media-type=\"image/jpeg\"") && (line.contains("id=\"cover\"") || line.contains("id=\"cover_image\""))) {
                    imgPath = line.substring(line.indexOf("href=\"") + 6, line.indexOf("\"", line.indexOf("href=\"") + 10));
                    break;
                }
            }
            while (zipEntries.hasMoreElements()) {
                if ((entry = zipEntries.nextElement()).getName().contains(imgPath)) {
                    break;
                }
            }
            Image cover = ImageIO.read(zipFile.getInputStream(entry));
            Image resize_cover = cover.getScaledInstance(160, 180, 5);
            lblImage.setIcon(new ImageIcon(resize_cover));
        } catch (SQLException ex) {
            Logger.getLogger(DialogBookInfo.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ZipException ex) {
            Logger.getLogger(DialogBookInfo.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(DialogBookInfo.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
        String cancelName = "cancel";
        InputMap inputMap = getRootPane().getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), cancelName);
        ActionMap actionMap = getRootPane().getActionMap();
        actionMap.put(cancelName, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                doClose(RET_CANCEL);
            }
        });
    }

    /**
     * @return the return status of this dialog - one of RET_OK or RET_CANCEL
     */
    public int getReturnStatus() {
        return returnStatus;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pnCenter = new javax.swing.JPanel();
        lblImage = new javax.swing.JLabel();
        pnInfo = new javax.swing.JPanel();
        lblBookName = new javax.swing.JLabel();
        lblAuthor = new javax.swing.JLabel();
        lblAddedDate = new javax.swing.JLabel();
        lblLastRead = new javax.swing.JLabel();
        lblReadTime = new javax.swing.JLabel();
        lblPath = new javax.swing.JLabel();
        pnBottom = new javax.swing.JPanel();
        okButton = new javax.swing.JButton();

        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                closeDialog(evt);
            }
        });

        pnCenter.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 20, 0, 20));
        pnCenter.setLayout(new java.awt.BorderLayout());

        lblImage.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblImage.setText("IMAGE");
        lblImage.setFocusable(false);
        lblImage.setMaximumSize(new java.awt.Dimension(160, 180));
        lblImage.setMinimumSize(new java.awt.Dimension(160, 180));
        lblImage.setPreferredSize(new java.awt.Dimension(160, 180));
        pnCenter.add(lblImage, java.awt.BorderLayout.WEST);

        pnInfo.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 20, 20, 20));
        pnInfo.setMinimumSize(new java.awt.Dimension(0, 0));
        pnInfo.setPreferredSize(new java.awt.Dimension(0, 0));
        pnInfo.setLayout(new javax.swing.BoxLayout(pnInfo, javax.swing.BoxLayout.PAGE_AXIS));

        lblBookName.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblBookName.setText("Book Name");
        pnInfo.add(lblBookName);

        lblAuthor.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblAuthor.setText("Author: ");
        pnInfo.add(Box.createVerticalStrut(20));
        pnInfo.add(lblAuthor);

        lblAddedDate.setText("Added Date: ");
        pnInfo.add(Box.createVerticalStrut(20));
        pnInfo.add(lblAddedDate);

        lblLastRead.setText("Last Read: ");
        pnInfo.add(Box.createVerticalStrut(20));
        pnInfo.add(lblLastRead);

        lblReadTime.setText("Read Time: ");
        pnInfo.add(Box.createVerticalStrut(20));
        pnInfo.add(lblReadTime);

        lblPath.setText("Book Path: ");
        pnInfo.add(Box.createVerticalStrut(20));
        pnInfo.add(lblPath);

        pnCenter.add(pnInfo, java.awt.BorderLayout.CENTER);

        getContentPane().add(pnCenter, java.awt.BorderLayout.CENTER);

        okButton.setText("OK");
        okButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                okButtonActionPerformed(evt);
            }
        });
        pnBottom.add(okButton);
        getRootPane().setDefaultButton(okButton);

        getContentPane().add(pnBottom, java.awt.BorderLayout.SOUTH);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void okButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_okButtonActionPerformed
        doClose(RET_OK);
    }//GEN-LAST:event_okButtonActionPerformed
    
    /**
     * Closes the dialog
     */
    private void closeDialog(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_closeDialog
        doClose(RET_CANCEL);
    }//GEN-LAST:event_closeDialog
    
    private void doClose(int retStatus) {
        returnStatus = retStatus;
        setVisible(false);
        dispose();
    }

    /**
     * @param args the command line arguments
     */
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel lblAddedDate;
    private javax.swing.JLabel lblAuthor;
    private javax.swing.JLabel lblBookName;
    private javax.swing.JLabel lblImage;
    private javax.swing.JLabel lblLastRead;
    private javax.swing.JLabel lblPath;
    private javax.swing.JLabel lblReadTime;
    private javax.swing.JButton okButton;
    private javax.swing.JPanel pnBottom;
    private javax.swing.JPanel pnCenter;
    private javax.swing.JPanel pnInfo;
    // End of variables declaration//GEN-END:variables
    private int returnStatus = RET_CANCEL;
}
