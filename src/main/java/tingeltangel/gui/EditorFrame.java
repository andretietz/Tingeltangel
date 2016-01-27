/*
    Copyright (C) 2015   Martin Dames <martin@bastionbytes.de>
  
    This program is free software; you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation; either version 2 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License along
    with this program; if not, write to the Free Software Foundation, Inc.,
    51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
  
*/

package tingeltangel.gui;

import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.MenuItem;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;
import tingeltangel.Tingeltangel;
import tingeltangel.core.Book;
import tingeltangel.core.Codes;
import tingeltangel.core.Entry;
import tingeltangel.core.Importer;
import tingeltangel.core.ReadYamlFile;
import tingeltangel.core.Repository;
import tingeltangel.core.Translator;
import tingeltangel.core.scripting.SyntaxError;
import tingeltangel.tools.Callback;
import tingeltangel.tools.FileEnvironment;
import tingeltangel.tools.Progress;
import tingeltangel.tools.ProgressDialog;
import tingeltangel.tools.ZipHelper;
import tiptoi_reveng.lexer.LexerException;
import tiptoi_reveng.parser.ParserException;

public class EditorFrame extends JFrame implements Callback<String> {

    private Book book = new Book(15000);
    
    private final EditorPanel indexPanel;
    private final InfoFrame contactFrame = new InfoFrame("Kontakt", "html/contact.html");
    private final InfoFrame licenseFrame = new InfoFrame("Lizenz", "html/license.html");
    
    private final LinkedList<EntryListener> listeners = new LinkedList<EntryListener>();
    
    public EditorFrame() {
        super(Tingeltangel.MAIN_FRAME_TITLE + Tingeltangel.MAIN_FRAME_VERSION);
        
        
        indexPanel = new EditorPanel(this);
        
        
        JFrame.setDefaultLookAndFeelDecorated(true);

        setBounds(
                    Tingeltangel.MAIN_FRAME_POS_X,
                    Tingeltangel.MAIN_FRAME_POS_Y,
                    Tingeltangel.MAIN_FRAME_WIDTH + getInsets().left + getInsets().right,
                    Tingeltangel.MAIN_FRAME_HEIGHT + getInsets().top + getInsets().bottom
        );

        MasterFrameMenu.setMenuCallback(this);
        setMenuBar(MasterFrameMenu.getMenuBar());
        
        
        
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                closeMasterFrame();
            }
        });
        setVisible(true);
        
        setContentPane(indexPanel);
        
        book.resetChangeMade();
        indexPanel.setVisible(false);
    }
    
    public void setBookOpened() {
        indexPanel.setVisible(true);
        MenuBar bar = getMenuBar();
        for(int i = 0; i < bar.getMenuCount(); i++) {
            enableMenu(bar.getMenu(i));
        }
    }
    
    private void enableMenu(Menu menu) {
        menu.setEnabled(true);
        for(int i = 0; i < menu.getItemCount(); i++) {
            MenuItem item = menu.getItem(i);
            if(item instanceof Menu) {
                enableMenu((Menu)item);
            } else {
                item.setEnabled(true);
            }
        }
    }
    
    public Book getBook() {
        return(book);
    }
    
    
    void addEntryListener(EntryListener listener) {
        listeners.add(listener);
    }
    
    void entrySelected(int i) {
        Entry entry = book.getEntry(i);
        Iterator<EntryListener> it = listeners.iterator();
        while(it.hasNext()) {
            it.next().entrySelected(entry);
        }
    }
    
    private void closeMasterFrame() {
        if(book.unsaved()) {
            int value =  JOptionPane.showConfirmDialog(this, "Das aktuelle Buch ist nicht gespeichert. wollen sie trotzdem das Programm beenden?", "Frage...", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (value == JOptionPane.YES_OPTION) {
                System.exit(0);
            }
        } else {
            System.exit(0);
        }
    }

    @Override
    public void callback(String id) {
        if(id.equals("buch.exit")) {
            closeMasterFrame();
        } else if(id.equals("buch.new")) {
            boolean newBook = false;
            if(book.unsaved()) {
                int value =  JOptionPane.showConfirmDialog(this, "Das aktuelle Buch ist nicht gespeichert. wollen sie trotzdem ein neues Buch erstellen?", "Frage...", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                if (value == JOptionPane.YES_OPTION) {
                    newBook = true;
                }
            } else {
                newBook = true;
            }
            if(newBook) {
                
                IDChooser ic = new IDChooser(this, new Callback<Integer>() {

                    @Override
                    public void callback(Integer id) {
                        String _id = Integer.toString(id);
                        while(_id.length() < 5) {
                            _id = "0" + _id;
                        }
                        
                        // check if there is already a book with this id
                        if(new File(FileEnvironment.getBooksDirectory(), _id).exists()) {
                            JOptionPane.showMessageDialog(EditorFrame.this, "Dieses Buch existiert schon");
                            return;
                        }
                        
                        book.clear();
                        book.setID(id);
                        indexPanel.updateList();
                        indexPanel.refresh();
                        setBookOpened();
                    }
                });
                
            }
                
        } else if(id.equals("buch.import.repo")) {
            boolean loadBook = false;
            if(book.unsaved()) {
                int value =  JOptionPane.showConfirmDialog(this, "Das aktuelle Buch ist nicht gespeichert. wollen sie trotzdem ein Buch importieren?", "Frage...", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                if (value == JOptionPane.YES_OPTION) {
                    loadBook = true;
                }
            } else {
                loadBook = true;
            }
            if(loadBook) {

                BookIDChooser bidc = new BookIDChooser(this, new Callback<Integer>() {
                    @Override
                    public void callback(final Integer id) {
                        // check repository
                        if(!Repository.exists(id)) {
                            new Progress(EditorFrame.this, "Buch wird heruntergeladen") {
                                @Override
                                public void action(ProgressDialog progressDialog) {
                                    try {
                                        Repository.download(id, progressDialog);
                                        new Progress(EditorFrame.this, "Buch wird importiert") {
                                            @Override
                                            public void action(ProgressDialog progressDialog) {
                                                try {
                                                    book = new Book(id);
                                                    File ouf = Repository.getBookOuf(id);
                                                    Map<String, String> txt = Repository.getBookTxt(id);
                                                    File src = Repository.getBookSrc(id);
                                                    File png = Repository.getBookPng(id);
                                                    Importer.importBook(ouf, txt, src, png, book, progressDialog);
                                                    indexPanel.updateList();
                                                    indexPanel.refresh();
                                                    setBookOpened();
                                                } catch (SyntaxError ex) {
                                                    JOptionPane.showMessageDialog(EditorFrame.this, "Fehler beim Importieren des Buches");
                                                    ex.printStackTrace(System.out);
                                                } catch (IOException ex) {
                                                    JOptionPane.showMessageDialog(EditorFrame.this, "Fehler beim Importieren des Buches");
                                                    ex.printStackTrace(System.out);
                                                }
                                            }
                                        };
                                    } catch (IOException ex) {
                                        JOptionPane.showMessageDialog(EditorFrame.this, "Fehler beim Herunterladen des Buches");
                                        ex.printStackTrace(System.out);
                                    }
                                }
                            };
                        } else {
                            new Progress(EditorFrame.this, "Buch wird importiert") {
                                @Override
                                public void action(ProgressDialog progressDialog) {
                                    try {
                                        book = new Book(id);
                                        File ouf = Repository.getBookOuf(id);
                                        Map<String, String> txt = Repository.getBookTxt(id);
                                        File src = Repository.getBookSrc(id);
                                        File png = Repository.getBookPng(id);
                                        Importer.importBook(ouf, txt, src, png, book, progressDialog);
                                        indexPanel.updateList();
                                        indexPanel.refresh();
                                        setBookOpened();
                                    } catch (SyntaxError ex) {
                                        JOptionPane.showMessageDialog(EditorFrame.this, "Fehler beim Importieren des Buches");
                                        ex.printStackTrace(System.out);
                                    } catch (IOException ex) {
                                        JOptionPane.showMessageDialog(EditorFrame.this, "Fehler beim Importieren des Buches");
                                        ex.printStackTrace(System.out);
                                    }
                                }
                            };
                        }
                    }
                });

            }
        } else if(id.equals("buch.import.yaml")) {
            boolean loadBook = false;
            if(book.unsaved()) {
                int value =  JOptionPane.showConfirmDialog(this, "Das aktuelle Buch ist nicht gespeichert. wollen sie trotzdem ein Buch importieren?", "Frage...", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                if (value == JOptionPane.YES_OPTION) {
                    loadBook = true;
                }
            } else {
                loadBook = true;
            }
            if(loadBook) {
                JFileChooser fc = new JFileChooser();
                fc.setFileFilter(new FileNameExtensionFilter("tiptoi Buch (*.yaml)", "yaml"));
                if(fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                    try {
                        new ReadYamlFile().read(fc.getSelectedFile()).save();
                        indexPanel.updateList();
                        indexPanel.refresh();
                        setBookOpened();
                    } catch(ParserException e) {
                        JOptionPane.showMessageDialog(this, "Die yaml Datei konnte nicht importiert werden");
                        e.printStackTrace(System.out);
                    } catch (IOException e) {
                        JOptionPane.showMessageDialog(this, "Die yaml Datei konnte nicht importiert werden");
                        e.printStackTrace(System.out);
                    } catch (LexerException e) {
                        JOptionPane.showMessageDialog(this, "Die yaml Datei konnte nicht importiert werden");
                        e.printStackTrace(System.out);
                    }
                }
            }
        } else if(id.equals("buch.import.ouf")) {
            boolean loadBook = false;
            if(book.unsaved()) {
                int value =  JOptionPane.showConfirmDialog(this, "Das aktuelle Buch ist nicht gespeichert. wollen sie trotzdem ein Buch importieren?", "Frage...", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                if (value == JOptionPane.YES_OPTION) {
                    loadBook = true;
                }
            } else {
                loadBook = true;
            }
            if(loadBook) {
                
                
                final Callback<Map> callback = new Callback<Map>() {
                    @Override
                    public void callback(final Map data) {
                        int id = -1;
                        if(data.get("id") != null) {
                            id = (Integer)data.get("id");
                        }
                        if(id < 0) {
                            try {
                                // prefetch id
                                DataInputStream in = new DataInputStream(new FileInputStream((File)data.get("ouf")));
                                in.skipBytes(20);
                                id = in.readInt();
                                in.close();
                            } catch(IOException e) {
                                JOptionPane.showMessageDialog(EditorFrame.this, "Fehler beim lesen der ouf Datei");
                                e.printStackTrace(System.out);
                                return;
                            }
                        }
                        
                        final int _id = id;
                        EditorFrame.this.setEnabled(false);
                        
                        
                        Progress pr = new Progress(EditorFrame.this, "importiere Buch") {
                            @Override
                            public void action(ProgressDialog progressDialog) {
                                try {
                                    book = new Book(_id);
                                    Importer.importBook((File)data.get("ouf"), Repository.getBook((File)data.get("txt")), (File)data.get("src"), (File)data.get("png"), book, progressDialog);
                                    
                                    setBookOpened();
                                } catch(IOException e) {
                                    JOptionPane.showMessageDialog(EditorFrame.this, "Import ist fehlgeschlagen");
                                    e.printStackTrace(System.out);
                                } catch(SyntaxError se) {
                                    JOptionPane.showMessageDialog(EditorFrame.this, "Import ist fehlgeschlagen");
                                    se.printStackTrace(System.out);
                                }
                                indexPanel.updateList();
                                indexPanel.refresh();
                            }
                        };
                        
                    }
                };
                new ImportDialog(EditorFrame.this, true, callback).setVisible(true);
                
                
            }
        } else if(id.equals("buch.load")) {
            boolean loadBook = false;
            if(book.unsaved()) {
                int value =  JOptionPane.showConfirmDialog(this, "Das aktuelle Buch ist nicht gespeichert. wollen sie trotzdem ein Buch laden?", "Frage...", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                if (value == JOptionPane.YES_OPTION) {
                    loadBook = true;
                }
            } else {
                loadBook = true;
            }
            if(loadBook) {
                
                ChooseBook cb = new ChooseBook(this, new Callback<Integer>() {
                    @Override
                    public void callback(Integer _id) {
                        try {
                            book.clear();
                            book.setID(_id);
                            Book.loadXML(FileEnvironment.getXML(_id), book);
                            indexPanel.refresh();
                            indexPanel.updateList();
                            book.resetChangeMade();
                            setBookOpened();
                        } catch (IOException ex) {
                            ex.printStackTrace(System.err);
                        }
                    }
                });
                
                
            }
            
        } else if(id.equals("buch.save")) {
            
            
            try {
                book.save();
            } catch(Exception e) {
                JOptionPane.showMessageDialog(this, "Das Buch konnte nicht gespeichert werden");
                e.printStackTrace(System.out);
            }
            
        } else if(id.equals("buch.generate")) {
            
            
            JFileChooser fc = new JFileChooser();
            fc.setFileFilter(new FileNameExtensionFilter("Ting Archiv (*.zip)", "zip"));
            if(fc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                try {
                    String file = fc.getSelectedFile().getCanonicalPath();
                    if(!file.toLowerCase().endsWith(".zip")) {
                        file = file + ".zip";
                    }
                    final File output = new File(file);
                    new Progress(EditorFrame.this, "erzeuge Buch") {
                        @Override
                        public void action(ProgressDialog progressDialog) {
                            try {
                                book.generateTTS(progressDialog);

                                new Progress(EditorFrame.this, "erzeuge Buch") {
                                    @Override
                                    public void action(ProgressDialog progressDialog) {
                                        try {
                                            book.export(FileEnvironment.getDistDirectory(book.getID()), progressDialog);

                                            // create zip to output
                                            final FileOutputStream fos = new FileOutputStream(output);
                                            final ZipOutputStream out = new ZipOutputStream(fos);

                                            new Progress(EditorFrame.this, "erzeuge zip") {
                                                @Override
                                                public void action(ProgressDialog progressDialog) {
                                                    File[] entries = FileEnvironment.getDistDirectory(book.getID()).listFiles(new FilenameFilter() {
                                                        @Override
                                                        public boolean accept(File dir, String name) {
                                                            return(
                                                                    name.toLowerCase().endsWith(".ouf") ||
                                                                    name.toLowerCase().endsWith(".png") ||
                                                                    name.toLowerCase().endsWith(".txt") ||
                                                                    name.toLowerCase().endsWith(".src")
                                                            );
                                                        }
                                                    });
                                                    byte[] buffer = new byte[4096];
                                                    progressDialog.setMax(entries.length);
                                                    try {
                                                        for(int i = 0; i < entries.length; i++) {

                                                            FileInputStream in = new FileInputStream(entries[i]);
                                                            ZipEntry zipEntry = new ZipEntry(entries[i].getName());
                                                            out.putNextEntry(zipEntry);

                                                            int length;
                                                            while((length = in.read(buffer)) >= 0) {
                                                                out.write(buffer, 0, length);
                                                            }

                                                            out.closeEntry();
                                                            in.close();

                                                            progressDialog.setVal(i);
                                                        }
                                                        out.close();
                                                        fos.close();
                                                    } catch(IOException ioe) {
                                                        JOptionPane.showMessageDialog(EditorFrame.this, "Ting Archiv konnte nicht erstellt werden: " + ioe.getMessage());
                                                    }
                                                    progressDialog.done();
                                                }

                                            };
                                        } catch(IOException e) {
                                            e.printStackTrace(System.out);
                                            JOptionPane.showMessageDialog(EditorFrame.this, "Buchgenerierung fehlgeschlagen");
                                        } catch(IllegalArgumentException e) {
                                            e.printStackTrace(System.out);
                                            JOptionPane.showMessageDialog(EditorFrame.this, "Buchgenerierung fehlgeschlagen: " + e.getMessage());
                                        } catch(SyntaxError e) {
                                            e.printStackTrace(System.out);
                                            JOptionPane.showMessageDialog(EditorFrame.this, "Buchgenerierung fehlgeschlagen: Syntax Error in Skript " + e.getTingID() + " in Zeile " + e.getRow() + " (" + e.getMessage() + ")");
                                        }
                                    }

                                };
                            } catch (IOException ex) {
                                JOptionPane.showMessageDialog(EditorFrame.this, "TTS Generierung fehlgeschlagen");
                            }
                        }
                    };
                } catch(IOException ioe) {
                    JOptionPane.showMessageDialog(EditorFrame.this, "Buchgenerierung fehlgeschlagen: " + ioe.getMessage());
                }
            }
        } else if(id.equals("buch.generateMp3")) {
            JFileChooser fc = new JFileChooser();
            fc.setFileFilter(new FileNameExtensionFilter("MP3 archiv (*.zip)", "zip"));
            if(fc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                try {
                    String file = fc.getSelectedFile().getCanonicalPath();
                    if(!file.toLowerCase().endsWith(".zip")) {
                        file = file + ".zip";
                    }
                    final String _file = file;
                    new Progress(EditorFrame.this, "erzeuge Buch") {
                        @Override
                        public void action(ProgressDialog progressDialog) {
                            try {
                                book.generateTTS(progressDialog);
                    
                                final FileOutputStream fos = new FileOutputStream(_file);
                                final ZipOutputStream out = new ZipOutputStream(fos);

                                new Progress(EditorFrame.this, "erzeuge Buch") {
                                    @Override
                                    public void action(ProgressDialog progressDialog) {
                                        File[] entries = FileEnvironment.getAudioDirectory(book.getID()).listFiles(new FilenameFilter() {
                                            @Override
                                            public boolean accept(File dir, String name) {
                                                return(name.toLowerCase().endsWith(".mp3"));
                                            }
                                        });
                                        byte[] buffer = new byte[4096];
                                        progressDialog.setMax(entries.length);
                                        try {
                                            for(int i = 0; i < entries.length; i++) {

                                                FileInputStream in = new FileInputStream(entries[i]);
                                                ZipEntry zipEntry = new ZipEntry(entries[i].getName());
                                                out.putNextEntry(zipEntry);

                                                int length;
                                                while((length = in.read(buffer)) >= 0) {
                                                    out.write(buffer, 0, length);
                                                }

                                                out.closeEntry();
                                                in.close();

                                                progressDialog.setVal(i);
                                            }
                                            out.close();
                                            fos.close();
                                        } catch(IOException ioe) {
                                            JOptionPane.showMessageDialog(EditorFrame.this, "MP3 Archiv konnte nicht erstellt werden: " + ioe.getMessage());
                                        }
                                        progressDialog.done();
                                    }

                                };
                            } catch(IOException e) {
                                JOptionPane.showMessageDialog(EditorFrame.this, "MP3 Archiv konnte nicht erstellt werden: " + e.getMessage());
                            }
                        }
                    };
                } catch(Exception e) {
                    JOptionPane.showMessageDialog(this, "MP3 Archiv konnte nicht gespeichert werden");
                    e.printStackTrace(System.out);
                }
            }
        } else if(id.startsWith("buch.generateEpsCodes.") || id.startsWith("buch.generatePngCodes.")) {
            if(id.endsWith(".600")) {
                Codes.setResolution(Codes.DPI600);
            } else {
                Codes.setResolution(Codes.DPI1200);
            }
            final boolean png = id.startsWith("buch.generatePngCodes.");
            JFileChooser fc = new JFileChooser();
            if(png) {
                fc.setFileFilter(new FileNameExtensionFilter("PNG Codes (*.zip)", "zip"));
            } else {
                fc.setFileFilter(new FileNameExtensionFilter("EPS Codes (*.zip)", "zip"));
            }
            if(fc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                try {
                    String file = fc.getSelectedFile().getCanonicalPath();
                    if(!file.toLowerCase().endsWith(".zip")) {
                        file = file + ".zip";
                    }
                    final File output = new File(file);


                    new Progress(EditorFrame.this, "erzeuge Codes") {
                        @Override
                        public void action(ProgressDialog progressDialog) {
                            try {
                                if(png) {
                                    book.pngExport(FileEnvironment.getCodesDirectory(book.getID()), progressDialog);
                                } else {
                                    book.epsExport(FileEnvironment.getCodesDirectory(book.getID()), progressDialog);
                                }
                                File[] input = FileEnvironment.getCodesDirectory(book.getID()).listFiles(new FilenameFilter() {
                                    @Override
                                    public boolean accept(File dir, String name) {
                                        if(png) {
                                            return(name.toLowerCase().endsWith(".png"));
                                        } else {
                                            return(name.toLowerCase().endsWith(".eps"));
                                        }
                                    }
                                });
                                ZipHelper.zip(output, input, progressDialog, EditorFrame.this, book, "erzeuge ZIP", "ZIP konnte nicht erstellt werden");
                            } catch(IOException e) {
                                JOptionPane.showMessageDialog(EditorFrame.this, "Code-Generierung fehlgeschlagen");
                                e.printStackTrace(System.out);
                            }
                        }
                    };
                } catch(IOException ioe) {
                    JOptionPane.showMessageDialog(EditorFrame.this, "Code-Generierung fehlgeschlagen");
                    ioe.printStackTrace(System.out);
                }
            }
        
        } else if(id.startsWith("buch.booklet")) {
            JFileChooser fc = new JFileChooser();
            fc.setFileFilter(new FileNameExtensionFilter("Code Tabelle (*.ps)", "ps"));
            if(fc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                try {
                    String file = fc.getSelectedFile().getCanonicalPath();
                    if(!file.toLowerCase().endsWith(".ps")) {
                        file = file + ".ps";
                    }
                    PrintWriter out = new PrintWriter(new FileWriter(file));
                    book.generateTestBooklet(out);
                    out.close();
                } catch(Exception e) {
                    JOptionPane.showMessageDialog(this, "Die Codetabelle konnte nicht gespeichert werden");
                    e.printStackTrace(System.out);
                }
            }
        } else if(id.equals("prefs.binary")) {
            new BinaryLocationsDialog(this, true).setVisible(true);
        } else if(id.equals("prefs.tts")) {
            new TTSPreferences().setVisible(true);
    /*    } else if(id.equals("windows.stick")) {
            stickFrame.setVisible(true); */
        } else if(id.equals("windows.index")) {
            indexPanel.setVisible(true);
    /*    } else if(id.equals("windows.reference")) {
            referenceFrame.setVisible(true);
        } else if(id.equals("windows.translator")) {
            translatorFrame.setVisible(true);
        } else if(id.equals("windows.repository")) {
            repositoryFrame.setVisible(true);
        } else if(id.equals("windows.gfx")) {
            gfxEditFrame.setVisible(true);
            gfxEditFrame.update(); */
        } else if(id.startsWith("codes.raw.")) {
            id = id.substring("codes.raw.".length());
            int start = Integer.parseInt(id.substring(0, 1)) * 10000 + Integer.parseInt(id.substring(2)) * 1000;
            JFileChooser fc = new JFileChooser();
            fc.setFileFilter(new FileNameExtensionFilter("Code Tabelle (*.ps)", "ps"));
            if(fc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                try {
                    String file = fc.getSelectedFile().getCanonicalPath();
                    if(!file.toLowerCase().endsWith(".ps")) {
                        file = file + ".ps";
                    }
                    PrintWriter out = new PrintWriter(new FileWriter(file));
                    Codes.drawPage(start, out);
                    out.close();
                } catch(Exception e) {
                    JOptionPane.showMessageDialog(this, "Die Codetabelle konnte nicht gespeichert werden");
                    e.printStackTrace(System.out);
                }
            }
        } else if(id.startsWith("codes.ting.")) {
            if(!genTingCodes(Integer.parseInt(id.substring(id.lastIndexOf(".") + 1)))) {
                JOptionPane.showMessageDialog(this, "Die Ting-Codetabelle konnte nicht erstellt werden, da die gewählten Codes noch unbekannt sind.");
            }
        } else if(id.equals("codes.tabular.ting2code")) {
            generateTabular(true);
        } else if(id.equals("codes.tabular.code2ting")) {
            generateTabular(false);
        } else if(id.equals("actions.searchForNewBooks")) {
            new Progress(this, "Buchliste aktualisieren") {
                @Override
                public void action(ProgressDialog progressDialog) {
                    Repository.search(progressDialog);
                }
            };
        } else if(id.equals("actions.updateBooks")) {
            new Progress(this, "Bücher aktualisieren") {
                @Override
                public void action(ProgressDialog progressDialog) {
                    try {
                        Repository.update(progressDialog);
                    } catch(IOException ioe) {
                        ioe.printStackTrace(System.out);
                        JOptionPane.showMessageDialog(EditorFrame.this, "Update der bekannten Bücher fehlgeschlagen: " + ioe.getMessage());
                    }
                }
            };
        } else if(id.equals("actions.deleteBook")) {
            ChooseBook cb = new ChooseBook(this, new Callback<Integer>() {
                @Override
                public void callback(Integer _id) {
                    if(_id == book.getID()) {
                        JOptionPane.showMessageDialog(EditorFrame.this, "Das Buch wird gerade bearbeitet und kann nicht gelöscht werden.");
                    } else if(!book.deleteBook(_id)) {
                        JOptionPane.showMessageDialog(EditorFrame.this, "Das Buch konnte nicht gelöscht werden.");
                    } else {
                        JOptionPane.showMessageDialog(EditorFrame.this, "Das Buch wurde gelöscht.");
                    }
                }
            });
        } else if(id.equals("actions.cleanupRepository")) {
            Repository.cleanup();
            JOptionPane.showMessageDialog(EditorFrame.this, "Die Inhalte der Bücherliste wurde gelöscht.");
        } else if(id.equals("about.contact")) {
            contactFrame.setVisible(true);
        } else if(id.equals("about.license")) {
            licenseFrame.setVisible(true);
        }
    }
    
    private void generateTabular(boolean ting2code) {
        JFileChooser fc = new JFileChooser();
        fc.setFileFilter(new FileNameExtensionFilter("Tabelle (*.txt)", "txt"));
        if(fc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                String file = fc.getSelectedFile().getCanonicalPath();
                if(!file.toLowerCase().endsWith(".txt")) {
                    file = file + ".txt";
                }
                PrintWriter out = new PrintWriter(new FileWriter(file));
                for(int i = 0; i < 0x10000; i++) {
                    int t = Translator.code2ting(i);
                    if(ting2code) {
                        t = Translator.ting2code(i);
                    }
                    if(t != -1) {
                        out.println(i + "\t" + t);
                    }
                }
                out.close();
            } catch(Exception e) {
                JOptionPane.showMessageDialog(this, "Die Tabelle konnte nicht gespeichert werden");
                e.printStackTrace(System.out);
            }
        }
    }
    
    private boolean genTingCodes(int start) {
        boolean found = false;
        for(int i = start; i < start + 1000; i++) {
            if(Translator.ting2code(i) != -1) {
                found = true;
                break;
            }
        }
        if(!found) {
            return(false);
        }
        JFileChooser fc = new JFileChooser();
        fc.setFileFilter(new FileNameExtensionFilter("PostScript Datei (*.ps)", "ps"));
        fc.setDialogTitle("Zieldatei auswählen");
        if(fc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                String file = fc.getSelectedFile().getCanonicalPath();
                if(!file.toLowerCase().endsWith(".ps")) {
                    file = file + ".ps";
                }
                
                
                int[] idx = new int[1000];
                
                
                String[] lbs = new String[idx.length];
                for(int i = start; i < start + 1000; i++) {
                    int code = Translator.ting2code(i);
                    idx[i - start] = code;
                    lbs[i - start] = "" + i;
                }
                PrintWriter out = new PrintWriter(new FileWriter(file));
                Codes.drawPage(idx, lbs, out);
                out.close();
            } catch(IOException ioe) {
                JOptionPane.showMessageDialog(this, "Codegenerierung fehlgeschlagen");
                ioe.printStackTrace(System.out);
            }
        }
        return(true);
    }
    
}