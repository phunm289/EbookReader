package com.vnlocalize.ebooksreader;

import com.vnlocalize.ebooksreader.customControl.MyAccordionTabRenderer;
import com.vnlocalize.ebooksreader.customControl.WrapLayout;
import com.vnlocalize.ebooksreader.customControl.MyLabel;
import com.vnlocalize.ebooksreader.customControl.CustomHyperlinkListener;
import com.vnlocalize.ebooksreader.customControl.CustomHTMLEditorKit;
import com.vnlocalize.ebooksreader.customControl.MyScrollBarUI;
import com.vnlocalize.ebooksreader.customControl.MySliderUI;
import com.vnlocalize.ebooksreader.db.Database;
import com.javaswingcomponents.accordion.JSCAccordion;
import com.javaswingcomponents.accordion.TabOrientation;
import com.javaswingcomponents.accordion.listener.AccordionEvent;
import com.javaswingcomponents.accordion.listener.AccordionListener;
import com.javaswingcomponents.accordion.plaf.steel.SteelAccordionUI;
import com.javaswingcomponents.framework.painters.configurationbound.GradientColorPainter;
import com.vnlocalize.ebooksreader.customControl.SpecialThumbIcon;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.HeadlessException;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;
import javax.imageio.ImageIO;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultSingleSelectionModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.plaf.IconUIResource;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter.DefaultHighlightPainter;
import javax.swing.text.Document;
import javax.swing.text.html.StyleSheet;

public class MainFrame extends javax.swing.JFrame {

    private JFileChooser jFileChooer;
    private Enumeration<? extends ZipEntry> zipEntries;
    private JSCAccordion accordion;
    private JPanel pnAllBooks, pnRecentAdd, pnUnread, pnRecentRead, pnCataloge, pnBook, pnContenRight;
    private JScrollPane scrAll, scrRAdd, scrUnread, scrReRead, scrContentLeft, scrEditorPane;
    private Database database;
    private int selected_book = 0, selected_tab = -1;
    private ArrayList<Item> itemList;
    private ArrayList<String> itemOrder;
    private int readingPage = 0;
    public JEditorPane editorPane;
    private JToolBar control;
    private JButton btnPrev, btnNext, btnBack;
    private JToggleButton btnShowHide;
    private JSlider slider;
    private JFrame frame_fs;
    private JComboBox cbbFont;
    private String readingPageUrl = "";
    private JTextField txtSearch;
    private String path;
    private CustomHTMLEditorKit kit;
    protected static int searchIndex = 0;

    public Database getDatabase() {
        return database;
    }

    public int getSelected_book() {
        return selected_book;
    }

    public String getReadingPageUrl() {
        return readingPageUrl;
    }

    public MainFrame() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            IconUIResource specialThumbIcon = new IconUIResource(new SpecialThumbIcon());
            UIManager.put("Slider.horizontalThumbIcon", specialThumbIcon);
            UIManager.put("Slider.verticalThumbIcon", specialThumbIcon);
            UIManager.put("Slider.background", new Color(237, 237, 237));
            UIManager.put("Slider.trackWidth", 1);
            UIManager.put("Slider.majorTickLength", 1);

        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
            System.out.println(ex.getMessage());
        }
        initComponents();
        initContent();
        scrContentRight.getVerticalScrollBar().setUI(new MyScrollBarUI());
        scrContentRight.getVerticalScrollBar().setPreferredSize(new Dimension(10, 0));
        scrContentRight.getHorizontalScrollBar().setPreferredSize(new Dimension(0, 10));
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation((screenSize.width - this.getWidth()) / 3, (screenSize.height - this.getHeight()) / 3);

    }

    private void formLoad() {
        try {
            //<editor-fold defaultstate="collapsed" desc="add to allbooks panel">
            pnAllBooks.removeAll();
            final ResultSet rs = database.getAllBook();
            while (rs.next()) {
                final MyLabel lblTitle = new MyLabel(rs.getString("name"));
                lblTitle.setFont(new Font("arial", Font.BOLD, 14));
                lblTitle.setId(rs.getInt("id"));
                if (lblTitle.getId() == selected_book) {
                    lblTitle.setForeground(new Color(255, 255, 255));
                } else {
                    lblTitle.setForeground(new Color(100, 100, 100));
                }
                lblTitle.setCursor(new Cursor(Cursor.HAND_CURSOR));

                lblTitle.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        selected_book = lblTitle.getId();
                        if (e.getClickCount() == 1 && SwingUtilities.isLeftMouseButton(e)) {
                            lblTitle.setForeground(new Color(255, 255, 255));
                            formLoad();
                            loadBookInfo(selected_tab, false);
                        } else if (e.getClickCount() >= 2 && SwingUtilities.isLeftMouseButton(e)) {
                            loadBookInfo(selected_tab, true);
                            bookModifying(selected_book);
                            pnTopLeft.setPreferredSize(new Dimension(0, 220));
                            readBook(selected_book);
                        }
                    }

                    @Override
                    public void mouseEntered(MouseEvent e) {
                        if (lblTitle.getId() != selected_book) {
                            lblTitle.setForeground(new Color(20, 20, 20));
                        }
                    }

                    @Override
                    public void mouseExited(MouseEvent e) {
                        if (lblTitle.getId() != selected_book) {
                            lblTitle.setForeground(new Color(100, 100, 100));
                        }
                    }
                });
                pnAllBooks.add(lblTitle);

            }
            pnAllBooks.revalidate();
            pnAllBooks.repaint();
            //</editor-fold>
            //<editor-fold defaultstate="collapsed" desc="add to recent add panel">
            pnRecentAdd.removeAll();
            final ResultSet rsRecentAdd = database.getRecentAdd();
            while (rsRecentAdd.next()) {
                final MyLabel lblTitle = new MyLabel(rsRecentAdd.getString("name"));
                lblTitle.setFont(new Font("arial", Font.BOLD, 14));
                lblTitle.setId(rsRecentAdd.getInt("id"));
                if (lblTitle.getId() == selected_book) {
                    lblTitle.setForeground(new Color(255, 255, 255));
                } else {
                    lblTitle.setForeground(new Color(100, 100, 100));
                }
                lblTitle.setCursor(new Cursor(Cursor.HAND_CURSOR));

                lblTitle.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        selected_book = lblTitle.getId();
                        if (e.getClickCount() == 1 && SwingUtilities.isLeftMouseButton(e)) {
                            lblTitle.setForeground(new Color(255, 255, 255));
                            formLoad();
                            loadBookInfo(selected_tab, false);
                        } else if (e.getClickCount() >= 2 && SwingUtilities.isLeftMouseButton(e)) {
                            loadBookInfo(selected_tab, true);
                            bookModifying(selected_book);
                            pnTopLeft.setPreferredSize(new Dimension(0, 220));
                            readBook(selected_book);
                        }
                    }

                    @Override
                    public void mouseEntered(MouseEvent e) {
                        if (lblTitle.getId() != selected_book) {
                            lblTitle.setForeground(new Color(20, 20, 20));
                        }
                    }

                    @Override
                    public void mouseExited(MouseEvent e) {
                        if (lblTitle.getId() != selected_book) {
                            lblTitle.setForeground(new Color(100, 100, 100));
                        }
                    }
                });
                pnRecentAdd.add(lblTitle);
            }
            pnRecentAdd.revalidate();
            pnRecentAdd.repaint();
            //</editor-fold>
            //<editor-fold defaultstate="collapsed" desc="add to unread panel">
            pnUnread.removeAll();
            final ResultSet rsUnread = database.getUnreadBook();
            while (rsUnread.next()) {
                final MyLabel lblTitle = new MyLabel(rsUnread.getString("name"));
                lblTitle.setFont(new Font("arial", Font.BOLD, 14));
                lblTitle.setId(rsUnread.getInt("id"));
                if (lblTitle.getId() == selected_book) {
                    lblTitle.setForeground(new Color(255, 255, 255));
                } else {
                    lblTitle.setForeground(new Color(100, 100, 100));
                }
                lblTitle.setCursor(new Cursor(Cursor.HAND_CURSOR));

                lblTitle.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        selected_book = lblTitle.getId();
                        if (e.getClickCount() == 1 && SwingUtilities.isLeftMouseButton(e)) {
                            lblTitle.setForeground(new Color(255, 255, 255));
                            formLoad();
                            loadBookInfo(selected_tab, false);
                        } else if (e.getClickCount() >= 2 && SwingUtilities.isLeftMouseButton(e)) {
                            loadBookInfo(selected_tab, true);
                            bookModifying(selected_book);
                            pnTopLeft.setPreferredSize(new Dimension(0, 220));
                            readBook(selected_book);
                        }
                    }

                    @Override
                    public void mouseEntered(MouseEvent e) {
                        if (lblTitle.getId() != selected_book) {
                            lblTitle.setForeground(new Color(20, 20, 20));
                        }
                    }

                    @Override
                    public void mouseExited(MouseEvent e) {
                        if (lblTitle.getId() != selected_book) {
                            lblTitle.setForeground(new Color(100, 100, 100));
                        }
                    }
                });
                pnUnread.add(lblTitle);
            }
            pnUnread.revalidate();
            pnUnread.repaint();
            //</editor-fold>
            //<editor-fold defaultstate="collapsed" desc="add to recent read">
            pnRecentRead.removeAll();
            final ResultSet rsRecentRead = database.getRecentRead();
            while (rsRecentRead.next()) {
                final MyLabel lblTitle = new MyLabel(rsRecentRead.getString("name"));
                lblTitle.setFont(new Font("arial", Font.BOLD, 14));
                lblTitle.setId(rsRecentRead.getInt("id"));
                if (lblTitle.getId() == selected_book) {
                    lblTitle.setForeground(new Color(255, 255, 255));
                } else {
                    lblTitle.setForeground(new Color(100, 100, 100));
                }
                lblTitle.setCursor(new Cursor(Cursor.HAND_CURSOR));

                lblTitle.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        selected_book = lblTitle.getId();
                        if (e.getClickCount() == 1 && SwingUtilities.isLeftMouseButton(e)) {
                            lblTitle.setForeground(new Color(255, 255, 255));
                            formLoad();
                            loadBookInfo(selected_tab, false);
                        } else if (e.getClickCount() >= 2 && SwingUtilities.isLeftMouseButton(e)) {
                            loadBookInfo(selected_tab, true);
                            bookModifying(selected_book);
                            pnTopLeft.setPreferredSize(new Dimension(0, 220));
                            readBook(selected_book);
                        }
                    }

                    @Override
                    public void mouseEntered(MouseEvent e) {
                        if (lblTitle.getId() != selected_book) {
                            lblTitle.setForeground(new Color(20, 20, 20));
                        }
                    }

                    @Override
                    public void mouseExited(MouseEvent e) {
                        if (lblTitle.getId() != selected_book) {
                            lblTitle.setForeground(new Color(100, 100, 100));
                        }
                    }
                });
                pnRecentRead.add(lblTitle);
            }
            pnRecentRead.revalidate();
            pnRecentRead.repaint();
            //</editor-fold>
        } catch (SQLException ex) {
            Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void loadBookInfo(final int tabIndex, final boolean check) {

        try {
            pnContenRight.removeAll();
            ResultSet rs = null;
            switch (tabIndex) {
                case 0: {
                    rs = database.getAllBook();
                    break;
                }
                case 1: {
                    rs = database.getUnreadBook();
                    break;
                }
                case 2: {
                    rs = database.getRecentRead();
                    break;
                }
                case 3: {
                    rs = database.getRecentAdd();
                    break;
                }
            }
            if (rs != null) {
                while (rs.next()) {
                    String filePath = rs.getString("path");
                    String name = rs.getString("name");
                    String author = rs.getString("author");
                    String lastRead = rs.getString("lastRead");

                    if (lastRead == null) {
                        lastRead = "Unread";
                    }
                    final int bid = rs.getInt("id");
                    File file = new File(filePath);
                    if (!file.isFile()) {
                        continue;
                    }
                    ZipFile zipFile = new ZipFile(file);
                    zipEntries = zipFile.entries();
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
                    Image resize_cover = null;
                    ImageIcon icon = null;
                    if (cover != null) {
                        resize_cover = cover.getScaledInstance(160, 180, 5);
                        icon = new ImageIcon(resize_cover);
                        final MyLabel labelInfo = new MyLabel() {
                            @Override
                            public JPopupMenu getComponentPopupMenu() {
                                MyLabel mylabel = ((MyLabel) pnContenRight.getComponentAt(pnContenRight.getMousePosition()) != null)
                                        ? ((MyLabel) pnContenRight.getComponentAt(pnContenRight.getMousePosition())) : null;
                                if (mylabel != null) {
                                    selected_book = mylabel.getId();
                                    mylabel.setBackground(new Color(180, 180, 180));
                                }
                                return super.getComponentPopupMenu();
                            }
                        };
                        labelInfo.setComponentPopupMenu(createPopupMenu(labelInfo));
                        labelInfo.setOpaque(true);
                        labelInfo.setId(bid);
                        labelInfo.setCursor(new Cursor(Cursor.HAND_CURSOR));
                        labelInfo.setText("<html><body><h3>    " + name + "</h3>"
                                + "Author: <i>" + author + "</i>"
                                + "<br/> Last Read: <i>" + lastRead + "</i> </body></html>");
                        labelInfo.setPreferredSize(new Dimension(350, 180));
                        labelInfo.setIcon(icon);
                        if (selected_book == bid) {
                            labelInfo.setBackground(new Color(180, 180, 180));
                            if (check) {
                                lblLogo.setIcon(labelInfo.getIcon());
                                lblLogo.setText(labelInfo.getText());
                            }
                        }
                        labelInfo.addMouseListener(new MouseAdapter() {
                            @Override
                            public void mouseEntered(MouseEvent e) {
                                if (selected_book != labelInfo.getId()) {
                                    labelInfo.setOpaque(true);
                                    labelInfo.setBackground(new Color(200, 200, 200));
                                }
                            }

                            @Override
                            public void mouseExited(MouseEvent e) {
                                if (selected_book != labelInfo.getId()) {
                                    labelInfo.setBackground(new Color(240, 240, 240));
                                }
                            }

                            @Override
                            public void mouseClicked(MouseEvent e) {
                                if (SwingUtilities.isLeftMouseButton(e)) {
                                    selected_book = labelInfo.getId();
                                    labelInfo.setBackground(new Color(180, 180, 180));
                                    pnTopLeft.setPreferredSize(new Dimension(0, 220));
                                    lblLogo.setIcon(labelInfo.getIcon());
                                    lblLogo.setText(labelInfo.getText());
                                    readBook(bid);
                                } else if (SwingUtilities.isMiddleMouseButton(e)) {
                                    database.removeBook(labelInfo.getId());
                                    formLoad();
                                    loadBookInfo(tabIndex, check);
                                }
                            }
                        });
                        labelInfo.setBorder(new EmptyBorder(10, 10, 10, 10));

                        labelInfo.setIconTextGap(10);
                        pnContenRight.add(labelInfo);
                    }
                }
            }
            pnContenRight.revalidate();
            pnContenRight.repaint();
        } catch (IOException | SQLException ex) {
            Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private JPopupMenu createPopupMenu(final MyLabel parent) {
        JPopupMenu popupMenu = new JPopupMenu();
        popupMenu.addPopupMenuListener(new PopupMenuListener() {
            @Override
            public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
            }

            @Override
            public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
            }

            @Override
            public void popupMenuCanceled(PopupMenuEvent e) {
                selected_book = -1;
                parent.setBackground(new Color(240, 240, 240));
            }
        });
        popupMenu.setBorderPainted(false);
        popupMenu.setSelectionModel(new DefaultSingleSelectionModel());
        JMenuItem miRead = new JMenuItem("Read");
        miRead.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                selected_book = parent.getId();
                parent.setBackground(new Color(180, 180, 180));
                pnTopLeft.setPreferredSize(new Dimension(0, 220));
                lblLogo.setIcon(parent.getIcon());
                lblLogo.setText(parent.getText());
                readBook(selected_book);
            }
        });

        JMenuItem miInfo = new JMenuItem("Show book infomation");
        miInfo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                DialogBookInfo info = new DialogBookInfo(MainFrame.this, true, parent);
                Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
                info.setLocation((int) (screenSize.getWidth() - info.getWidth()) / 2, ((int) screenSize.getHeight() - info.getHeight()) / 3);
                info.setVisible(true);

            }
        });


        JMenuItem miPath = new JMenuItem("Copy book path");
        miPath.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                try {
                    ResultSet rs = database.getBook(parent.getId());
                    String path = "";
                    while (rs.next()) {
                        path = rs.getString("path");
                    }
                    StringSelection selection = new StringSelection(path);
                    clipboard.setContents(selection, null);
                } catch (SQLException ex) {
                    Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });

        JMenuItem miRemoveBook = new JMenuItem("Remove from library");
        miRemoveBook.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                database.removeBook(parent.getId());
                formLoad();
                loadBookInfo(selected_tab, true);
            }
        });

        popupMenu.add(miRead);
        popupMenu.add(miInfo);
        popupMenu.add(miPath);
        popupMenu.add(miRemoveBook);
        return popupMenu;
    }

    public void bookModifying(int bid) {
        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        Date date = new Date();
        database.modifiBook(bid, dateFormat.format(date));
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        splitMain = new javax.swing.JSplitPane();
        panelLeft = new JPanel();
        pnTopLeft = new javax.swing.JPanel();
        pnLogo = new javax.swing.JPanel();
        lblLogo = new javax.swing.JLabel();
        pnTools = new javax.swing.JPanel();
        jSeparator1 = new javax.swing.JSeparator();
        toolBar = new javax.swing.JToolBar();
        lblOpen = new javax.swing.JLabel();
        lblSpace1 = new javax.swing.JLabel();
        lblClose = new javax.swing.JLabel();
        lblSpace2 = new javax.swing.JLabel();
        toggleBtnFS = new javax.swing.JToggleButton();
        jSeparator2 = new javax.swing.JSeparator();
        pnContentLeft = new javax.swing.JPanel();
        panelRight = new javax.swing.JPanel();
        scrContentRight = new javax.swing.JScrollPane();
        menuBar = new javax.swing.JMenuBar();
        mFile = new javax.swing.JMenu();
        miAddBook = new javax.swing.JMenuItem();
        miReadBook = new javax.swing.JMenuItem();
        miRemove = new javax.swing.JMenuItem();
        jSeparator3 = new javax.swing.JPopupMenu.Separator();
        miExit = new javax.swing.JMenuItem();
        mEdit = new javax.swing.JMenu();
        miBack = new javax.swing.JMenuItem();
        cbmnNavi = new javax.swing.JCheckBoxMenuItem();
        cbmiFullScreen = new javax.swing.JCheckBoxMenuItem();
        mRead = new javax.swing.JMenu();
        miNext = new javax.swing.JMenuItem();
        miPrev = new javax.swing.JMenuItem();
        jSeparator4 = new javax.swing.JPopupMenu.Separator();
        miSearch = new javax.swing.JMenuItem();
        mHelp = new javax.swing.JMenu();
        jMenuItem1 = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("eBook Library");
        setMinimumSize(new java.awt.Dimension(1480, 720));

        splitMain.setBorder(null);
        splitMain.setDividerLocation(300);
        splitMain.setToolTipText("");
        splitMain.setContinuousLayout(true);
        splitMain.setMinimumSize(new java.awt.Dimension(5, 5));

        panelLeft.setBackground(new java.awt.Color(255, 255, 255));
        panelLeft.setMaximumSize(new java.awt.Dimension(350, 32767));
        panelLeft.setMinimumSize(new java.awt.Dimension(270, 479));
        panelLeft.setLayout(new java.awt.BorderLayout());

        pnTopLeft.setBackground(new java.awt.Color(255, 255, 255));
        pnTopLeft.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 10, 1, 10));
        pnTopLeft.setMaximumSize(new java.awt.Dimension(32767, 300));
        pnTopLeft.setMinimumSize(new java.awt.Dimension(0, 168));
        pnTopLeft.setOpaque(false);
        pnTopLeft.setPreferredSize(new java.awt.Dimension(0, 168));
        pnTopLeft.setLayout(new java.awt.BorderLayout());

        pnLogo.setOpaque(false);
        pnLogo.setLayout(new java.awt.BorderLayout());

        lblLogo.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        lblLogo.setText("<html><h1>eBooks Reader</h1></html>");
        lblLogo.setToolTipText("");
        lblLogo.setIconTextGap(10);
        pnLogo.add(lblLogo, java.awt.BorderLayout.CENTER);

        pnTopLeft.add(pnLogo, java.awt.BorderLayout.NORTH);

        pnTools.setBackground(new java.awt.Color(255, 255, 255));
        pnTools.setMaximumSize(new java.awt.Dimension(32767, 30));
        pnTools.setMinimumSize(new java.awt.Dimension(200, 30));
        pnTools.setOpaque(false);
        pnTools.setPreferredSize(new java.awt.Dimension(200, 30));
        pnTools.setLayout(new java.awt.BorderLayout());

        jSeparator1.setForeground(new java.awt.Color(200, 200, 200));
        jSeparator1.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        jSeparator1.setRequestFocusEnabled(false);
        jSeparator1.setVerifyInputWhenFocusTarget(false);
        pnTools.add(jSeparator1, java.awt.BorderLayout.PAGE_START);

        toolBar.setBackground(new java.awt.Color(255, 255, 255));
        toolBar.setBorder(null);
        toolBar.setFloatable(false);
        toolBar.setBorderPainted(false);
        toolBar.setName(""); // NOI18N
        toolBar.setOpaque(false);

        lblOpen.setBackground(new java.awt.Color(255, 255, 255));
        lblOpen.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblOpen.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblOpen.setToolTipText("Add eBooks to library");
        lblOpen.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        lblOpen.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        lblOpen.setMaximumSize(new java.awt.Dimension(40, 36));
        lblOpen.setMinimumSize(new java.awt.Dimension(40, 36));
        lblOpen.setOpaque(true);
        lblOpen.setPreferredSize(new java.awt.Dimension(40, 36));
        lblOpen.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lblOpenMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                lblOpenMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                lblOpenMouseExited(evt);
            }
        });
        toolBar.add(lblOpen);

        lblSpace1.setBackground(new java.awt.Color(255, 255, 255));
        lblSpace1.setMaximumSize(new java.awt.Dimension(20, 14));
        lblSpace1.setMinimumSize(new java.awt.Dimension(20, 14));
        lblSpace1.setPreferredSize(new java.awt.Dimension(20, 14));
        toolBar.add(lblSpace1);

        lblClose.setBackground(new java.awt.Color(255, 255, 255));
        lblClose.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblClose.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblClose.setToolTipText("Remove book from library");
        lblClose.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        lblClose.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        lblClose.setMaximumSize(new java.awt.Dimension(40, 36));
        lblClose.setMinimumSize(new java.awt.Dimension(40, 36));
        lblClose.setOpaque(true);
        lblClose.setPreferredSize(new java.awt.Dimension(40, 36));
        lblClose.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lblCloseMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                lblCloseMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                lblCloseMouseExited(evt);
            }
        });
        toolBar.add(lblClose);

        lblSpace2.setBackground(new java.awt.Color(255, 255, 255));
        lblSpace2.setMaximumSize(new java.awt.Dimension(20, 14));
        lblSpace2.setMinimumSize(new java.awt.Dimension(20, 14));
        lblSpace2.setPreferredSize(new java.awt.Dimension(20, 14));
        toolBar.add(lblSpace2);

        toggleBtnFS.setBackground(new java.awt.Color(255, 255, 255));
        toggleBtnFS.setToolTipText("Full Screen");
        toggleBtnFS.setContentAreaFilled(false);
        toggleBtnFS.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        toggleBtnFS.setFocusable(false);
        toggleBtnFS.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        toggleBtnFS.setMaximumSize(new java.awt.Dimension(43, 36));
        toggleBtnFS.setMinimumSize(new java.awt.Dimension(43, 36));
        toggleBtnFS.setOpaque(true);
        toggleBtnFS.setPreferredSize(new java.awt.Dimension(43, 36));
        toggleBtnFS.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        toggleBtnFS.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                toggleBtnFSMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                toggleBtnFSMouseExited(evt);
            }
        });
        toggleBtnFS.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                toggleBtnFSActionPerformed(evt);
            }
        });
        toolBar.add(toggleBtnFS);

        pnTools.add(toolBar, java.awt.BorderLayout.CENTER);

        jSeparator2.setForeground(new java.awt.Color(200, 200, 200));
        pnTools.add(jSeparator2, java.awt.BorderLayout.PAGE_END);

        pnTopLeft.add(pnTools, java.awt.BorderLayout.CENTER);

        panelLeft.add(pnTopLeft, java.awt.BorderLayout.NORTH);

        pnContentLeft.setBackground(new java.awt.Color(255, 255, 255));
        pnContentLeft.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        pnContentLeft.setOpaque(false);
        pnContentLeft.setLayout(new java.awt.BorderLayout());
        panelLeft.add(pnContentLeft, java.awt.BorderLayout.CENTER);

        splitMain.setLeftComponent(panelLeft);

        panelRight.setBackground(new java.awt.Color(255, 255, 255));
        panelRight.setMinimumSize(new java.awt.Dimension(545, 479));
        panelRight.setLayout(new java.awt.BorderLayout());

        scrContentRight.setBorder(null);
        scrContentRight.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrContentRight.setAutoscrolls(true);
        panelRight.add(scrContentRight, java.awt.BorderLayout.CENTER);

        splitMain.setRightComponent(panelRight);

        getContentPane().add(splitMain, java.awt.BorderLayout.CENTER);

        menuBar.setBorder(null);

        mFile.setMnemonic('F');
        mFile.setText("File");
        mFile.setMargin(new java.awt.Insets(0, 0, 0, 10));

        miAddBook.setMnemonic('A');
        miAddBook.setText("Add New Book");
        miAddBook.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                miAddBookActionPerformed(evt);
            }
        });
        mFile.add(miAddBook);

        miReadBook.setMnemonic('R');
        miReadBook.setText("Read");
        miReadBook.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                miReadBookActionPerformed(evt);
            }
        });
        mFile.add(miReadBook);

        miRemove.setMnemonic('e');
        miRemove.setText("Remove Book");
        miRemove.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                miRemoveActionPerformed(evt);
            }
        });
        mFile.add(miRemove);
        mFile.add(jSeparator3);

        miExit.setMnemonic('x');
        miExit.setText("Exit");
        miExit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                miExitActionPerformed(evt);
            }
        });
        mFile.add(miExit);

        menuBar.add(mFile);

        mEdit.setMnemonic('V');
        mEdit.setText("View");
        mEdit.setMargin(new java.awt.Insets(0, 0, 0, 10));

        miBack.setMnemonic('B');
        miBack.setText("Back To Library");
        miBack.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                miBackActionPerformed(evt);
            }
        });
        mEdit.add(miBack);

        cbmnNavi.setMnemonic('S');
        cbmnNavi.setSelected(true);
        cbmnNavi.setText("Show Navigate");
        cbmnNavi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbmnNaviActionPerformed(evt);
            }
        });
        mEdit.add(cbmnNavi);

        cbmiFullScreen.setMnemonic('F');
        cbmiFullScreen.setText("Full Screen");
        cbmiFullScreen.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbmiFullScreenActionPerformed(evt);
            }
        });
        mEdit.add(cbmiFullScreen);

        menuBar.add(mEdit);

        mRead.setMnemonic('R');
        mRead.setText("Reading");
        mRead.setFocusable(false);
        mRead.setMargin(new java.awt.Insets(0, 0, 0, 10));

        miNext.setMnemonic('N');
        miNext.setText("Next page");
        miNext.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                miNextActionPerformed(evt);
            }
        });
        mRead.add(miNext);

        miPrev.setMnemonic('r');
        miPrev.setText("Previous Page");
        miPrev.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                miPrevActionPerformed(evt);
            }
        });
        mRead.add(miPrev);
        mRead.add(jSeparator4);

        miSearch.setMnemonic('r');
        miSearch.setText("Search");
        miSearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                miSearchActionPerformed(evt);
            }
        });
        mRead.add(miSearch);

        menuBar.add(mRead);

        mHelp.setMnemonic('H');
        mHelp.setText("Help");
        mHelp.setMargin(new java.awt.Insets(0, 0, 0, 10));

        jMenuItem1.setText("About");
        jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem1ActionPerformed(evt);
            }
        });
        mHelp.add(jMenuItem1);

        menuBar.add(mHelp);

        setJMenuBar(menuBar);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void lblCloseMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblCloseMouseExited
        // TODO add your handling code here:
        lblClose.setBackground(new Color(255, 255, 255));
    }//GEN-LAST:event_lblCloseMouseExited

    private void lblCloseMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblCloseMouseEntered
        // TODO add your handling code here:
        lblClose.setBackground(new Color(240, 240, 240));
    }//GEN-LAST:event_lblCloseMouseEntered

    private void lblOpenMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblOpenMouseExited
        // TODO add your handling code here:
        lblOpen.setBackground(new Color(255, 255, 255));
    }//GEN-LAST:event_lblOpenMouseExited

    private void lblOpenMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblOpenMouseEntered
        // TODO add your handling code here:
        lblOpen.setBackground(new Color(240, 240, 240));
    }//GEN-LAST:event_lblOpenMouseEntered

    private void lblOpenMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblOpenMouseClicked
        openAction();
    }//GEN-LAST:event_lblOpenMouseClicked

    public int getReadingPage() {
        return readingPage;
    }

    public void setReadingPage(int readingPage) {
        this.readingPage = readingPage;
    }

    private void lblCloseMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblCloseMouseClicked
        removeAction();

    }//GEN-LAST:event_lblCloseMouseClicked

    private void toggleBtnFSActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_toggleBtnFSActionPerformed
        // TODO add your handling code here:
        doFullscreen(toggleBtnFS.isSelected());
        toggleBtnFS.setBackground(new Color(255, 255, 255));
    }//GEN-LAST:event_toggleBtnFSActionPerformed

    private void toggleBtnFSMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_toggleBtnFSMouseEntered
        // TODO add your handling code here:
        toggleBtnFS.setBackground(new Color(240, 240, 240));
    }//GEN-LAST:event_toggleBtnFSMouseEntered

    private void toggleBtnFSMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_toggleBtnFSMouseExited
        // TODO add your handling code here:
        toggleBtnFS.setBackground(new Color(255, 255, 255));
    }//GEN-LAST:event_toggleBtnFSMouseExited

    private void miAddBookActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_miAddBookActionPerformed
        // TODO add your handling code here:\
        openAction();
    }//GEN-LAST:event_miAddBookActionPerformed

    private void miReadBookActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_miReadBookActionPerformed
        // TODO add your handling code here:
        if (selected_book > 0) {
            readBook(selected_book);
        }
    }//GEN-LAST:event_miReadBookActionPerformed

    private void miRemoveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_miRemoveActionPerformed
        // TODO add your handling code here:
        removeAction();
    }//GEN-LAST:event_miRemoveActionPerformed

    private void miExitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_miExitActionPerformed
        System.exit(0);
    }//GEN-LAST:event_miExitActionPerformed

    private void cbmiFullScreenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbmiFullScreenActionPerformed
        // TODO add your handling code here:
        doFullscreen(cbmiFullScreen.isSelected());
    }//GEN-LAST:event_cbmiFullScreenActionPerformed

    private void miNextActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_miNextActionPerformed
        // TODO add your handling code here:
        nextPage();
    }//GEN-LAST:event_miNextActionPerformed

    private void miBackActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_miBackActionPerformed
        // TODO add your handling code here:
        backHome();
    }//GEN-LAST:event_miBackActionPerformed

    private void cbmnNaviActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbmnNaviActionPerformed
        // TODO add your handling code here:
        showHideNav(cbmnNavi.isSelected());
    }//GEN-LAST:event_cbmnNaviActionPerformed

    private void miPrevActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_miPrevActionPerformed
        // TODO add your handling code here:
        prevPage();
    }//GEN-LAST:event_miPrevActionPerformed

    private void miSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_miSearchActionPerformed
        // TODO add your handling code here:
        txtSearch.requestFocus();
    }//GEN-LAST:event_miSearchActionPerformed

    private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem1ActionPerformed
        // TODO add your handling code here:
        AboutDialog ad = new AboutDialog(this, true);
        ad.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        ad.setLocation((screenSize.width - ad.getPreferredSize().width) / 2, (screenSize.height - ad.getPreferredSize().height) / 3);
        ad.setVisible(true);
    }//GEN-LAST:event_jMenuItem1ActionPerformed

    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                final MainFrame mf = new MainFrame();
                mf.addWindowListener(new WindowAdapter() {
                    @Override
                    public void windowClosing(WindowEvent e) {
                        super.windowClosing(e); //To change body of generated methods, choose Tools | Templates.
                        if (mf.getDatabase() != null && mf.getSelected_book() > -1) {
                            mf.getDatabase().setMarkPage(mf.getReadingPageUrl(), mf.getSelected_book());
                        }
                    }
                });
                mf.setVisible(true);
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBoxMenuItem cbmiFullScreen;
    private javax.swing.JCheckBoxMenuItem cbmnNavi;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JPopupMenu.Separator jSeparator3;
    private javax.swing.JPopupMenu.Separator jSeparator4;
    private javax.swing.JLabel lblClose;
    private javax.swing.JLabel lblLogo;
    private javax.swing.JLabel lblOpen;
    private javax.swing.JLabel lblSpace1;
    private javax.swing.JLabel lblSpace2;
    private javax.swing.JMenu mEdit;
    private javax.swing.JMenu mFile;
    private javax.swing.JMenu mHelp;
    private javax.swing.JMenu mRead;
    private javax.swing.JMenuBar menuBar;
    private javax.swing.JMenuItem miAddBook;
    private javax.swing.JMenuItem miBack;
    private javax.swing.JMenuItem miExit;
    private javax.swing.JMenuItem miNext;
    private javax.swing.JMenuItem miPrev;
    private javax.swing.JMenuItem miReadBook;
    private javax.swing.JMenuItem miRemove;
    private javax.swing.JMenuItem miSearch;
    private javax.swing.JPanel panelLeft;
    private javax.swing.JPanel panelRight;
    private javax.swing.JPanel pnContentLeft;
    private javax.swing.JPanel pnLogo;
    private javax.swing.JPanel pnTools;
    private javax.swing.JPanel pnTopLeft;
    private javax.swing.JScrollPane scrContentRight;
    private javax.swing.JSplitPane splitMain;
    private javax.swing.JToggleButton toggleBtnFS;
    private javax.swing.JToolBar toolBar;
    // End of variables declaration//GEN-END:variables

    private void customizeAccLAF(JSCAccordion accordion) {
        GradientColorPainter backgroundPainter = (GradientColorPainter) accordion.getBackgroundPainter();
        backgroundPainter = (GradientColorPainter) accordion.getBackgroundPainter();
        backgroundPainter.setStartColor(new Color(180, 180, 180));
        backgroundPainter.setEndColor(new Color(240, 240, 240));

        SteelAccordionUI steelAccordionUI = (SteelAccordionUI) accordion.getUI();
        steelAccordionUI.setHorizontalBackgroundPadding(5);
        steelAccordionUI.setVerticalBackgroundPadding(5);
        steelAccordionUI.setHorizontalTabPadding(5);
        steelAccordionUI.setVerticalTabPadding(15);
        steelAccordionUI.setTabPadding(0);
    }

    private void fileProcessing(File file) {
        try {
            String bookPath = file.getAbsolutePath();
            String author = "", title = "";
            ZipFile zipFile = new ZipFile(file);
            zipEntries = zipFile.entries();
            ZipEntry entry = null;
            while (zipEntries.hasMoreElements()) {
                if ((entry = zipEntries.nextElement()).getName().contains("content.opf")) {
                    break;
                }
            }
            BufferedReader reader = new BufferedReader(new InputStreamReader(zipFile.getInputStream(entry), "UTF8"));
            String line = "";
            while (reader.ready()) {
                line = reader.readLine();
                if (line.contains("<dc:creator")) {
                    author = line.substring(line.indexOf(">") + 1, line.indexOf("</dc"));
                } else if (line.contains("<dc:title")) {
                    title = line.substring(line.indexOf(">") + 1, line.indexOf("</dc"));
                }
            }
            DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
            Date date = new Date();
            String currentDate = dateFormat.format(date);
            eBooks book = new eBooks(title, bookPath, author, currentDate);
            book.setReadTime(0);
            database.insertBook(book);
        } catch (ZipException ex) {
            JOptionPane.showMessageDialog(this, "File invalid!");
        } catch (IOException ex) {
            Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void backHome() {
        MainFrame.this.setTitle("EBook Library");
        setVisibleReadingItem(false);
        panelRight.removeAll();
        panelRight.add(scrContentRight);
        pnContentLeft.removeAll();
        pnContentLeft.add(accordion, BorderLayout.CENTER);
        pnContentLeft.revalidate();
        pnContentLeft.repaint();
        panelRight.revalidate();
        panelRight.repaint();
        btnBack.setVisible(false);
        lblOpen.setVisible(true);
        lblClose.setVisible(true);
        lblSpace1.setVisible(true);
        if (!panelLeft.isVisible()) {
            panelLeft.setVisible(true);
            splitMain.setDividerLocation(300);
        }
        toolBar.add(toggleBtnFS);
        loadBookInfo(selected_tab, true);
        database.setMarkPage(readingPageUrl, selected_book);
    }

    private void initContent() {
        lblLogo.setIcon(new ImageIcon(getClass().getResource("/ebooksreader/books.png")));
        lblOpen.setIcon(new ImageIcon(getClass().getResource("/ebooksreader/add.png")));
        lblClose.setIcon(new ImageIcon(getClass().getResource("/ebooksreader/close.png")));
        toggleBtnFS.setIcon(new ImageIcon(getClass().getResource("/ebooksreader/fullscreen.png")));
        setVisibleReadingItem(false);
        btnBack = new JButton();
        btnBack.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                backHome();
            }
        });

        btnBack.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnBack.setPreferredSize(new Dimension(0, 30));
        btnBack.setToolTipText("Back to library");
        btnBack.setText("<html><b>Library<b></html>");
        btnBack.setIcon(new ImageIcon(getClass().getResource("/ebooksreader/backToLib.png")));
        btnBack.setBackground(Color.WHITE);
        btnBack.setFocusable(false);
        btnBack.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        btnBack.setVerticalTextPosition(javax.swing.SwingConstants.CENTER);
        btnBack.setVisible(false);
        this.setIconImage(new ImageIcon(getClass().getResource("/ebooksreader/books.png")).getImage());
        pnContenRight = new JPanel();
        pnContenRight.setBorder(new EmptyBorder(10, 10, 10, 10));
        pnContenRight.setLayout(new WrapLayout(WrapLayout.LEFT, 10, 20));
        scrContentRight.getVerticalScrollBar().setUnitIncrement(20);
        scrContentRight.setViewportView(pnContenRight);
        pnAllBooks = new JPanel();
        pnAllBooks.setLayout(new BoxLayout(pnAllBooks, BoxLayout.PAGE_AXIS));
        pnAllBooks.setOpaque(false);
        scrAll = new JScrollPane();
        scrAll.getVerticalScrollBar().setUI(new MyScrollBarUI());
        scrAll.getHorizontalScrollBar().setUI(new MyScrollBarUI());
        scrAll.getVerticalScrollBar().setPreferredSize(new Dimension(10, 0));
        scrAll.getHorizontalScrollBar().setPreferredSize(new Dimension(0, 10));
        scrAll.setBorder(new EmptyBorder(15, 15, 15, 15));
        scrAll.getViewport().setOpaque(false);
        scrAll.setOpaque(false);
        scrAll.setViewportView(pnAllBooks);

        pnRecentAdd = new JPanel();
        pnRecentAdd.setLayout(new BoxLayout(pnRecentAdd, BoxLayout.PAGE_AXIS));
        pnRecentAdd.setOpaque(false);
        scrRAdd = new JScrollPane();
        scrRAdd.getVerticalScrollBar().setUI(new MyScrollBarUI());
        scrRAdd.getHorizontalScrollBar().setUI(new MyScrollBarUI());
        scrRAdd.getVerticalScrollBar().setPreferredSize(new Dimension(10, 0));
        scrRAdd.getHorizontalScrollBar().setPreferredSize(new Dimension(0, 10));
        scrRAdd.setBorder(new EmptyBorder(15, 15, 15, 15));
        scrRAdd.getViewport().setOpaque(false);
        scrRAdd.setOpaque(false);
        scrRAdd.setViewportView(pnRecentAdd);

        pnUnread = new JPanel();
        pnUnread.setLayout(new BoxLayout(pnUnread, BoxLayout.PAGE_AXIS));
        pnUnread.setOpaque(false);
        scrUnread = new JScrollPane();
        scrUnread.getVerticalScrollBar().setUI(new MyScrollBarUI());
        scrUnread.getHorizontalScrollBar().setUI(new MyScrollBarUI());
        scrUnread.getVerticalScrollBar().setPreferredSize(new Dimension(10, 0));
        scrUnread.getHorizontalScrollBar().setPreferredSize(new Dimension(0, 10));
        scrUnread.setBorder(new EmptyBorder(15, 15, 15, 15));
        scrUnread.getViewport().setOpaque(false);
        scrUnread.setOpaque(false);
        scrUnread.setViewportView(pnUnread);

        pnRecentRead = new JPanel();
        pnRecentRead.setLayout(new BoxLayout(pnRecentRead, BoxLayout.PAGE_AXIS));
        pnRecentRead.setOpaque(false);
        scrReRead = new JScrollPane();
        scrReRead.getVerticalScrollBar().setUI(new MyScrollBarUI());
        scrReRead.getHorizontalScrollBar().setUI(new MyScrollBarUI());
        scrReRead.getVerticalScrollBar().setPreferredSize(new Dimension(10, 0));
        scrReRead.getHorizontalScrollBar().setPreferredSize(new Dimension(0, 10));
        scrReRead.setBorder(new EmptyBorder(15, 15, 15, 15));
        scrReRead.getViewport().setOpaque(false);
        scrReRead.setOpaque(false);
        scrReRead.setViewportView(pnRecentRead);


        accordion = new JSCAccordion();
        accordion.setOpaque(false);
        accordion.setTabOrientation(TabOrientation.VERTICAL);
        accordion.setPreferredSize(new Dimension(0, 0));
        accordion.setDrawShadow(false);
        accordion.setVerticalAccordionTabRenderer(new MyAccordionTabRenderer());
        accordion.addTab("All Books", scrAll);
        accordion.addTab("Unread Books", scrUnread);
        accordion.addTab("Recent read", scrReRead);
        accordion.addTab("Recent Added", scrRAdd);
        accordion.addAccordionListener(new AccordionListener() {
            @Override
            public void accordionChanged(AccordionEvent accordionEvent) {
                final int tabIndex = accordionEvent.getTabIndex();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        selected_tab = tabIndex;
                        loadBookInfo(selected_tab, false);
                    }
                }).start();
            }
        });
        customizeAccLAF(accordion);
        pnContentLeft.add(accordion, BorderLayout.CENTER);
        database = new Database();
        File db = new File("eBooksReader.db");
        if (!db.isFile()) {
            database.createDatabase();
        } else {
            formLoad();
        }
    }

    public void readBook(final int bid) {

        new Thread(new Runnable() {
            String cssContent = "";

            @Override
            public void run() {
                try {
                    MainFrame.this.setCursor(new Cursor(Cursor.WAIT_CURSOR));
                    setVisibleReadingItem(true);
                    readingPage = 0;
                    bookModifying(bid);
                    ResultSet rs = database.getBook(bid);

                    while (rs.next()) {
                        MainFrame.this.setTitle(MainFrame.this.getTitle() + " - " + rs.getString("name"));
                        path = rs.getString("path");

                        itemList = new ArrayList<>();
                        itemOrder = new ArrayList<>();
                        final ZipFile zipFile = new ZipFile(new File(path));
                        Enumeration<? extends ZipEntry> entries = zipFile.entries();
                        ZipEntry contentopf = null;
                        ZipEntry stylesheet = null;
                        ZipEntry tempEntry = null;
                        while (entries.hasMoreElements()) {
                            tempEntry = entries.nextElement();
                            if (tempEntry.getName().contains("content.opf")) {
                                contentopf = tempEntry;
                            } else if (tempEntry.getName().contains("stylesheet.css")) {
                                stylesheet = tempEntry;
                            }
                            if (contentopf != null && stylesheet != null) {
                                break;
                            }
                        }
                        BufferedReader reader = new BufferedReader(new InputStreamReader(zipFile.getInputStream(contentopf), "UTF8"));
                        String line = "";
                        String href = "", id = "", idref = "";
                        String starthref = "href=\"", startid = "id=\"", startidhref = "idref=\"";
                        int index = 0;

                        while (reader.ready()) {
                            line = reader.readLine();
                            if (line.contains("<item") && line.contains("media-type=\"application/xhtml+xml\"") && !line.contains("titlepage")) {
                                index = line.indexOf(starthref, 0);
                                if (index < 0) {
                                    break;
                                }
                                href = line.substring(index + starthref.length(), line.indexOf("\"", index + starthref.length()));
                                index = line.indexOf(startid);
                                if (index < 0) {
                                    break;
                                }
                                id = line.substring(index + startid.length(), line.indexOf("\"", index + startid.length()));
                                if (!"".equals(href) && !"".equals(id)) {
                                    Item item = new Item(href, id);
                                    itemList.add(item);
                                }
                            } else if (line.contains("<itemref") && !line.contains("titlepage")) {
                                index = line.indexOf(startidhref);
                                if (index < 0) {
                                    break;
                                }
                                idref = line.substring(index + startidhref.length(), line.indexOf("\"", index + startidhref.length()));
                                if (!"".equals(idref)) {
                                    itemOrder.add(idref);
                                }
                            }
                        }

                        if (pnCataloge != null) {
                            pnCataloge = null;
                        }
                        if (pnCataloge == null) {
                            pnCataloge = new JPanel();
                            pnCataloge.setOpaque(false);
                            pnCataloge.setBorder(new EmptyBorder(20, 20, 20, 20));
                            pnCataloge.setLayout(new BoxLayout(pnCataloge, BoxLayout.PAGE_AXIS));
                        }


                        pnCataloge.revalidate();

                        if (scrContentLeft != null) {
                            scrContentLeft = null;
                        }
                        if (scrContentLeft == null) {
                            scrContentLeft = new JScrollPane();
                            scrContentLeft.getVerticalScrollBar().setUI(new MyScrollBarUI());
                            scrContentLeft.getHorizontalScrollBar().setUI(new MyScrollBarUI());
                            scrContentLeft.setBorder(new EmptyBorder(10, 10, 10, 10));
                            scrContentLeft.getVerticalScrollBar().setPreferredSize(new Dimension(10, 0));
                            scrContentLeft.getHorizontalScrollBar().setPreferredSize(new Dimension(0, 10));
                            scrContentLeft.getVerticalScrollBar().setUnitIncrement(20);
                            scrContentLeft.getHorizontalScrollBar().setUnitIncrement(20);
                            scrContentLeft.setOpaque(false);
                            scrContentLeft.getViewport().setOpaque(false);
                            scrContentLeft.setViewportView(pnCataloge);
                        }
                        pnContentLeft.removeAll();
                        lblOpen.setVisible(false);
                        lblClose.setVisible(false);
                        btnBack.setVisible(true);
                        pnContentLeft.add(scrContentLeft, BorderLayout.CENTER);
                        pnContentLeft.revalidate();
                        pnContentLeft.repaint();

                        if (pnBook != null) {
                            pnBook = null;
                        }
                        pnBook = new JPanel(new BorderLayout());
                        pnBook.setBackground(Color.WHITE);

                        if (editorPane != null) {
                            editorPane = null;
                        }
                        if (editorPane == null) {
                            editorPane = new JEditorPane();
                            editorPane.setEditable(false);
                            editorPane.setBorder(new EmptyBorder(0, 0, 0, 0));

                            editorPane.addHyperlinkListener(new CustomHyperlinkListener(itemList, itemOrder, MainFrame.this) {
                                @Override
                                public void hyperlinkUpdate(HyperlinkEvent e) {
                                    super.hyperlinkUpdate(e); //To change body of generated methods, choose Tools | Templates.
                                    if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
                                        loadPage();
                                    }
                                }
                            });

                            editorPane.addKeyListener(new KeyAdapter() {
                                @Override
                                public void keyPressed(KeyEvent e) {
                                    if (e.getKeyCode() == KeyEvent.VK_LEFT) {
                                        prevPage();
                                    } else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
                                        nextPage();
                                    } else if (e.getKeyCode() == KeyEvent.VK_ESCAPE && toggleBtnFS.isSelected()) {
                                        toggleBtnFS.setSelected(false);
                                        doFullscreen(toggleBtnFS.isSelected());
                                    } else if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
                                        backHome();
                                    } else if (e.getKeyCode() == KeyEvent.VK_ENTER && !toggleBtnFS.isSelected() && e.isAltDown()) {
                                        toggleBtnFS.setSelected(true);
                                        doFullscreen(toggleBtnFS.isSelected());
                                    } else if (e.getKeyCode() == KeyEvent.VK_F && e.isControlDown()) {
                                        txtSearch.requestFocus();
                                    } else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
                                        scrEditorPane.getVerticalScrollBar().setValue(scrEditorPane.getVerticalScrollBar().getValue() + 1);
                                    } else if (e.getKeyCode() == KeyEvent.VK_UP) {
                                        scrEditorPane.getVerticalScrollBar().setValue(scrEditorPane.getVerticalScrollBar().getValue() - 1);
                                    } else {
                                        super.keyPressed(e);
                                    }
                                }
                            });



                            if (stylesheet != null) {
                                reader = new BufferedReader(new InputStreamReader(zipFile.getInputStream(stylesheet), "UTF8"));
                                String oldKeyColor = "";
                                String newKeyColor = "";
                                while (reader.ready()) {
                                    line = reader.readLine();
                                    if (!line.contains("@")) {
                                        int indexOfSharp = line.indexOf("#");
                                        if (indexOfSharp > 0) {
                                            int endIndex = line.indexOf(" ", indexOfSharp + 1);
                                            if (endIndex < 0) {
                                                endIndex = line.indexOf(";", indexOfSharp);
                                            }
                                            if (endIndex < 0) {
                                                endIndex = line.length();
                                            }
                                            oldKeyColor = line.substring(indexOfSharp, endIndex);

                                            if (oldKeyColor.length() == 4) {
                                                newKeyColor = "#" + oldKeyColor.charAt(1) + oldKeyColor.charAt(1) + oldKeyColor.charAt(2) + oldKeyColor.charAt(2) + oldKeyColor.charAt(3) + oldKeyColor.charAt(3);
                                                line = line.replace(oldKeyColor, newKeyColor);
                                            }

                                        }
                                        cssContent += line;
                                    }
                                }
                            } else {
                            }
                            if (kit != null) {
                                kit.setStyleSheet(null);
                            }
                            kit = new CustomHTMLEditorKit(zipFile);
                            editorPane.setEditorKit(kit);
                            StyleSheet css = kit.getStyleSheet();
                            css.addRule(cssContent);
                            css.addRule(".calibre {font-size:13px;text-align:justify}");
                            css.addRule("* {font-size:13px;text-align:justify}");
                            css.addRule("body {font-size:13px;text-align:justify}");
                            kit.setStyleSheet(css);



                        }
                        if (scrEditorPane != null) {
                            scrEditorPane = null;
                        }
                        if (scrEditorPane == null) {
                            scrEditorPane = new JScrollPane();
                            scrEditorPane.getVerticalScrollBar().setUI(new MyScrollBarUI());
                            scrEditorPane.getHorizontalScrollBar().setUI(new MyScrollBarUI());
                            scrEditorPane.getVerticalScrollBar().setPreferredSize(new Dimension(10, 0));
                            scrEditorPane.getHorizontalScrollBar().setPreferredSize(new Dimension(0, 10));
                            scrEditorPane.setBorder(new EmptyBorder(0, 0, 0, 0));
                            scrEditorPane.setOpaque(false);
                            scrEditorPane.getViewport().setOpaque(false);
                            scrEditorPane.getVerticalScrollBar().setUnitIncrement(20);
                            scrEditorPane.getHorizontalScrollBar().setUnitIncrement(20);
                            scrEditorPane.setViewportView(editorPane);
                        }


                        if (slider != null) {
                            slider = null;
                        }
                        if (slider == null) {
                            slider = new JSlider();
                            slider.setUI(new MySliderUI());
                            slider.setFocusable(false);
                            slider.setBackground(Color.WHITE);
                            slider.setForeground(Color.WHITE);
                            slider.addChangeListener(new ChangeListener() {
                                @Override
                                public void stateChanged(ChangeEvent e) {
                                    if (slider.getValueIsAdjusting()) {
                                        readingPage = slider.getValue();
                                        loadPage();
                                    }
                                }
                            });
                        }
                        slider.setMinimum(0);
                        slider.setValue(0);
                        slider.setMaximum(itemOrder.size() - 1);
                        if (btnPrev != null) {
                            btnPrev = null;
                        }

                        if (btnPrev == null) {
                            btnPrev = new JButton();
                            btnPrev.setBackground(Color.WHITE);
                            btnPrev.setToolTipText("Prevous Chapter");
                            btnPrev.setFocusable(false);
                            btnPrev.setIcon(new ImageIcon(getClass().getResource("/ebooksreader/prev.png")));

                        }
                        btnPrev.addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                prevPage();
                            }
                        });
                        if (btnNext != null) {
                            btnNext = null;
                        }
                        if (btnNext == null) {
                            btnNext = new JButton();
                            btnNext.setBackground(Color.WHITE);
                            btnNext.setToolTipText("Next Chapter");
                            btnNext.setFocusable(false);
                            btnNext.setIcon(new ImageIcon(getClass().getResource("/ebooksreader/next.png")));
                        }
                        btnNext.addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                nextPage();
                            }
                        });

                        if (control != null) {
                            control = null;
                        }
                        if (control == null) {
                            control = new JToolBar(JToolBar.HORIZONTAL);
                            control.setBackground(new Color(255, 255, 255));
                            control.setFloatable(false);
                            control.add(btnPrev);
                            control.add(slider);
                            control.add(btnNext);
                        }
                        String markPage = rs.getString("markPage");
                        if (markPage != null && !"".equals(markPage)) {
                            for (Item item : itemList) {
                                if (markPage.equals(item.getHref()
                                        .substring(item.getHref().lastIndexOf("/") + 1, item.getHref().length()))
                                        || markPage.contains(item.getHref().substring(item.getHref().lastIndexOf("/") + 1, item.getHref().length()))) {
                                    for (String string : itemOrder) {
                                        if (string.equals(item.getId())) {
                                            int i = itemOrder.indexOf(string);
                                            readingPage = i;
                                            break;
                                        }
                                    }
                                    break;
                                } else {
                                }
                            }
                        }
                        loadPage();
                        scrContentLeft.setVisible(true);

                        JToolBar menuBar = new JToolBar(JToolBar.HORIZONTAL);
                        menuBar.setOpaque(true);
                        menuBar.setBackground(new Color(100, 100, 100));
                        menuBar.setPreferredSize(new Dimension(0, 30));
                        menuBar.setFloatable(false);
                        menuBar.setMargin(new Insets(0, 10, 0, 10));

                        btnBack.setMaximumSize(new Dimension(150, 30));
                        btnBack.setMinimumSize(new Dimension(50, 30));
                        btnBack.setPreferredSize(new Dimension(150, 30));
                        btnBack.setOpaque(false);
                        btnBack.setForeground(Color.WHITE);
                        menuBar.add(btnBack);
                        btnShowHide = new JToggleButton(new ImageIcon(getClass().getResource("/ebooksreader/list.png")));
                        btnShowHide.setFocusable(false);
                        btnShowHide.setMinimumSize(new Dimension(24, 28));
                        btnShowHide.setMaximumSize(new Dimension(30, 30));
                        btnShowHide.setPreferredSize(new Dimension(26, 28));
                        btnShowHide.setCursor(new Cursor(Cursor.HAND_CURSOR));
                        btnShowHide.addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                showHideNav(!btnShowHide.isSelected());
                            }
                        });


                        menuBar.add(btnShowHide);
                        JLabel lblSpace6 = new JLabel();
                        lblSpace6.setPreferredSize(new Dimension(20, 14));
                        menuBar.add(lblSpace6);
                        menuBar.add(Box.createHorizontalGlue());



                        cbbFont = new JComboBox(new String[]{"<html><span style=\"font-size:11px\">ABC</span></html>",
                            "<html><span style=\"font-size:13px\">ABC</span></html>",
                            "<html><span style=\"font-size:15px\">ABC</span></html>", "<html><span style=\"font-size:17px\">ABC</span></html>"});
                        cbbFont.setFocusable(false);
                        cbbFont.setSelectedIndex(1);
                        cbbFont.setOpaque(false);
                        cbbFont.setCursor(new Cursor(Cursor.HAND_CURSOR));
                        cbbFont.setMaximumSize(new Dimension(75, 24));
                        cbbFont.setPreferredSize(new Dimension(75, 0));
                        cbbFont.addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                switch (cbbFont.getSelectedIndex()) {
                                    case 0: {
                                        ((CustomHTMLEditorKit) editorPane.getEditorKit()).getStyleSheet().addRule(".calibre {font-size:11px;text-align:justify}");
                                        break;
                                    }
                                    case 1: {
                                        ((CustomHTMLEditorKit) editorPane.getEditorKit()).getStyleSheet().addRule(".calibre {font-size:13px;text-align:justify}");
                                        break;
                                    }
                                    case 2: {
                                        ((CustomHTMLEditorKit) editorPane.getEditorKit()).getStyleSheet().addRule(".calibre {font-size:15px;text-align:justify}");
                                        break;
                                    }
                                    case 3: {
                                        ((CustomHTMLEditorKit) editorPane.getEditorKit()).getStyleSheet().addRule(".calibre {font-size:17px;text-align:justify}");
                                        break;
                                    }
                                }
                                loadPage();
                            }
                        });
                        //search text
                        txtSearch = new JTextField();
                        JLabel lblZoom = new JLabel(new ImageIcon(getClass().getResource("/ebooksreader/search.png")));
                        txtSearch.setPreferredSize(new Dimension(120, 20));
                        txtSearch.setMaximumSize(new Dimension(120, 20));
                        txtSearch.setBorder(new EmptyBorder(3, 5, 3, 5));
                        txtSearch.addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                searchText(true, true);
                            }
                        });
                        txtSearch.addKeyListener(new KeyAdapter() {
                            @Override
                            public void keyPressed(KeyEvent e) {
                                if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                                    editorPane.getHighlighter().removeAllHighlights();
                                    txtSearch.setText("");
                                    editorPane.requestFocus();
                                } else if (e.getKeyCode() == KeyEvent.VK_ENTER && e.isControlDown()) {
                                    searchText(false, true);
                                } else if (e.getKeyCode() == KeyEvent.VK_ENTER && e.isShiftDown()) {
                                    searchText(false, false);
                                } else {
                                    super.keyPressed(e); //To change body of generated methods, choose Tools | Templates.
                                }
                            }
                        });
                        //end search text
                        JLabel lblSpace7 = new JLabel();
                        lblSpace7.setPreferredSize(new Dimension(20, 14));
                        JLabel lblSpace8 = new JLabel();
                        lblSpace8.setPreferredSize(new Dimension(6, 14));

                        JLabel lblSpace2 = new JLabel();
                        lblSpace2.setPreferredSize(new Dimension(20, 14));
                        menuBar.add(cbbFont);
                        menuBar.add(lblSpace2);

                        JLabel lblSpace4 = new JLabel();
                        lblSpace4.setPreferredSize(new Dimension(20, 14));

                        JLabel lblFindNext = new JLabel(new ImageIcon(getClass().getResource("/ebooksreader/findnext.png")));
                        lblFindNext.setCursor(new Cursor(Cursor.HAND_CURSOR));
                        lblFindNext.addMouseListener(new MouseAdapter() {
                            @Override
                            public void mouseClicked(MouseEvent e) {
                                searchText(false, true);
                            }
                        });

                        JLabel lblFindBack = new JLabel(new ImageIcon(getClass().getResource("/ebooksreader/findback.png")));
                        lblFindBack.setCursor(new Cursor(Cursor.HAND_CURSOR));
                        lblFindBack.addMouseListener(new MouseAdapter() {
                            @Override
                            public void mouseClicked(MouseEvent e) {
                                searchText(false, false);
                            }
                        });


                        menuBar.add(toggleBtnFS);
                        menuBar.add(lblSpace4);

                        menuBar.add(lblZoom);
                        menuBar.add(lblSpace8);
                        menuBar.add(txtSearch);
                        menuBar.add(lblFindBack);
                        menuBar.add(lblFindNext);
                        menuBar.add(lblSpace7);
                        toolBar.repaint();

                        pnBook.removeAll();
                        pnBook.add(menuBar, BorderLayout.NORTH);
                        pnBook.add(scrEditorPane, BorderLayout.CENTER);
                        pnBook.add(control, BorderLayout.SOUTH);
                        panelRight.removeAll();
                        panelRight.add(pnBook, BorderLayout.CENTER);
                        panelRight.revalidate();
                    }
                    MainFrame.this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                } catch (IOException | SQLException ex) {
                    Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }).start();
    }

    public void nextPage() {
        if (readingPage < itemOrder.size() - 1) {
            readingPage++;
            loadPage();
            if (slider != null) {
                slider.setValue(readingPage);
            }
        }
    }

    public void prevPage() {
        if (readingPage > 0) {
            readingPage--;
            loadPage();
            if (slider != null) {
                slider.setValue(readingPage);
            }
        }
    }

    public void showContentText(String id) {
        String href = "";
        for (Item item : itemList) {
            if (id.equals(item.getId())) {
                try {
                    Document doc = kit.createDefaultDocument();
                    editorPane.setDocument(doc);
                    editorPane.getDocument().putProperty("IgnoreCharsetDirective", Boolean.TRUE);

                    href = item.getHref();
                    readingPageUrl = href;
                    final ZipFile zipFile = new ZipFile(new File(path));
                    final Enumeration<? extends ZipEntry> entries = zipFile.entries();
                    ZipEntry entry = null;
                    while (entries.hasMoreElements()) {
                        entry = entries.nextElement();
                        if (entry.getName().contains(href)) {
                            break;
                        }
                    }
                    BufferedReader reader = new BufferedReader(new InputStreamReader(zipFile.getInputStream(entry), "UTF8"));
                    String body = "", line = "";
                    while (reader.ready()) {

                        line = reader.readLine();
                        if (line.contains("<?xml")) {
                            int index = line.indexOf("<?xml");
                            String subS = line.substring(index, line.indexOf(">", index) + 1);
                            line = line.replace(subS, "");
                        }
                        if (line.contains("?xml")) {
                            int index = line.indexOf("?xml");
                            String subS = line.substring(index, line.indexOf(">", index) + 1);
                            line = line.replace(subS, "");
                        }
                        String imgTag = "<img";
                        if (line.contains(imgTag) && line.contains("src=\"")) {
                            line = line.replaceAll("src=\"", "src=\"file:///D:/");
                        }
                        if (line.contains("<strong")) {
                            line = line.replace("<strong", "<b").replace("</strong", "</b");
                        }

                        line = line.replaceAll("[\\s]+@[a-z]+[\\s]+[{]{1}[a-zA-Z0-9~@#\\^\\$&\\*\\(\\)-_\\+=\\[\\]\\{\\}\\|\\\\,\\.\\?\\s]*[}]{1}", "");
                        body += (line + "\n");

                    }
                    editorPane.setText(body);
                    editorPane.setSelectionStart(0);
                    editorPane.setSelectionEnd(0);
                    editorPane.requestFocus();
                    break;
                } catch (ZipException ex) {
                    Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException ex) {
                    Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
                }

            }
        }
    }

    public void loadPage() {
        pnCataloge.removeAll();
        JLabel label = new JLabel();
        label.setText("<html><h2>Cataloge</h2></html>");
        pnCataloge.add(label);
        for (int i = 0; i < itemOrder.size(); i++) {
            String chapterName = ChapterProcess.getChapterName(path, itemList, itemOrder, i);
            final MyLabel lblCat = new MyLabel("<html>" + (("".equals(chapterName)) ? ("Chapter " + (i + 1)) : chapterName) + "</html>");
            lblCat.setToolTipText(itemOrder.get(i));
            lblCat.setId(i);
            lblCat.setCursor(new Cursor(Cursor.HAND_CURSOR));
            if (lblCat.getId() == readingPage) {
                lblCat.setFont(new Font("arial", Font.BOLD, 14));
                showContentText(lblCat.getToolTipText());
                slider.setValue(readingPage);
            } else {
                lblCat.setFont(new Font("arial", Font.PLAIN, 14));
            }
            lblCat.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    readingPage = lblCat.getId();
                    loadPage();
                    if (slider != null) {
                        slider.setValue(readingPage);
                    }
                }

                @Override
                public void mouseEntered(MouseEvent e) {
                    if (lblCat.getId() != readingPage) {
                        lblCat.setFont(new Font("arial", Font.BOLD, 14));
                    }
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    if (lblCat.getId() != readingPage) {
                        lblCat.setFont(new Font("arial", Font.PLAIN, 14));
                    }
                }
            });
            pnCataloge.add(lblCat);
            pnCataloge.add(Box.createRigidArea(new Dimension(0, 10)));
        }
        pnCataloge.revalidate();

    }

    public void doFullscreen(boolean selected) {
        toggleBtnFS.setSelected(selected);
        if (selected) {
            Dimension screen_size = Toolkit.getDefaultToolkit().getScreenSize();
            if (frame_fs == null) {
                frame_fs = new JFrame();
            }
            frame_fs.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame_fs.setUndecorated(true);
            frame_fs.setSize(screen_size);
            frame_fs.getContentPane().add(splitMain);
            MainFrame.this.setVisible(false);
            frame_fs.setVisible(true);

        } else {
            cbmiFullScreen.setSelected(false);
            MainFrame.this.getContentPane().add(splitMain);
            MainFrame.this.setVisible(true);
            frame_fs.setVisible(false);
            frame_fs = null;
        }
        if (editorPane != null && editorPane.isVisible()) {
            editorPane.requestFocus();
        }
    }

    public void openAction() throws HeadlessException {
        jFileChooer = new JFileChooser(System.getenv("USERPROFILE") + "/Desktop");
        jFileChooer.setFileFilter(new FileNameExtensionFilter("Electric Publishing File", "epub"));
        jFileChooer.setMultiSelectionEnabled(true);
        if (jFileChooer.showOpenDialog(this) == JFileChooser.OPEN_DIALOG) {
            for (File file : jFileChooer.getSelectedFiles()) {
                fileProcessing(file);
            }
            formLoad();
            loadBookInfo(selected_tab, false);
        }
    }

    public void removeAction() {
        if (selected_book > -1) {
            database.removeBook(selected_book);
            selected_book = -1;
            formLoad();
            loadBookInfo(selected_tab, false);
        }
    }

    public void setVisibleReadingItem(boolean value) {
        mRead.setVisible(value);
        miBack.setEnabled(value);
        cbmnNavi.setEnabled(value);
    }

    public void showHideNav(boolean value) {
        panelLeft.setVisible(value);
        if (value) {
            splitMain.setDividerLocation(300);
        }
        splitMain.revalidate();
    }

    public void searchText(boolean isNewText, boolean isNext) {
        try {
            if (isNewText) {
                MainFrame.searchIndex = 0;
            }

            editorPane.getHighlighter().removeAllHighlights();
            String find = txtSearch.getText().toLowerCase();
            Document document = editorPane.getDocument();
            if (isNext) {
                MainFrame.searchIndex += find.length();
            } else {
                MainFrame.searchIndex -= find.length();
            }
            if (!"".equals(find)) {
                if (isNext) {
                    for (int index = MainFrame.searchIndex; index + find.length() < document.getLength(); index++) {
                        String match = document.getText(index, find.length());
                        if (find.equals(match.toLowerCase())) {
                            DefaultHighlightPainter highlightPainter = new DefaultHighlightPainter(new Color(180, 180, 180));
                            editorPane.getHighlighter().addHighlight(index, index + find.length(), highlightPainter);
                            editorPane.setSelectionStart(index);
                            editorPane.setSelectionEnd(index);
                            MainFrame.searchIndex = index + 1;
                            break;
                        }
                    }
                    
                } else {
                    for (int index = MainFrame.searchIndex; index + find.length() > find.length(); index--) {
                        String match = document.getText(index, find.length());
                        if (find.equals(match.toLowerCase())) {
                            DefaultHighlightPainter highlightPainter = new DefaultHighlightPainter(new Color(180, 180, 180));
                            editorPane.getHighlighter().addHighlight(index, index + find.length(), highlightPainter);
                            editorPane.setSelectionStart(index);
                            editorPane.setSelectionEnd(index);
                            MainFrame.searchIndex = index - 1;
                            break;
                        }
                    }
                    
                }

            } else {
                editorPane.getHighlighter().removeAllHighlights();
            }
        } catch (BadLocationException ex) {
            System.out.println(ex.getMessage());
        }
    }
}
