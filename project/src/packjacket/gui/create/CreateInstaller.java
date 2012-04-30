/*
 * PackJacket - GUI frontend to IzPack to make Java-based installers
 * Copyright (C) 2008 - 2009  Amandeep Grewal, Manodasan Wignarajah
 *
 * PackJacket is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * PackJacket is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with PackJacket.  If not, see <http://www.gnu.org/licenses/>.
 */
package packjacket.gui.create;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;
import java.util.logging.Level;
import javax.swing.JOptionPane;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import packjacket.RunnerClass;
import packjacket.gui.GUIUtils;
import packjacket.tasks.ExeTask;
import packjacket.tasks.JarTask;
import packjacket.tasks.Task;
import packjacket.xml.Pack;
import packjacket.xmlcreation.InstallXML;
import packjacket.xmlcreation.XMLUtils;

/**
 * Allows user a choice of what installers to create.
 * @author Amandeep Grewal
 */
public class CreateInstaller extends javax.swing.JFrame {

    public static boolean use7zip = false, useWindows = false;

    /**
     * If the user's system supports making a 7Zip EXE
     * @return true if the user's system supports making a 7Zip EXE, false otherwise
     * @throws InterruptedException
     */
    public boolean isEXEMakable() throws InterruptedException {
        //If on windows, its supported becuase of the py2exe function
        if (System.getProperty("os.name").startsWith("Windows")) {
            useWindows = true;
            return true;
        }
        //See is 7za is installed
        try {
            Runtime.getRuntime().exec("7za").waitFor();
            use7zip = true;
        } catch (java.io.IOException e) {
            use7zip = false;
        }
        RunnerClass.logger.info("use7zip: " + use7zip);
        return use7zip;
    }

    /** Creates new form CreateInstaller */
    public CreateInstaller() {
        initComponents();
        //Sets tooltips, if two EXEs are avaible to differentiate between the two
        try {
            //Disable 7zip EXE if its not supported
            if (!isEXEMakable())
                exe.setVisible(false);
            else {
                exe.setToolTipText("<html>"
                        + "This will create an installer that will compress the entire installer with LZMA 7-Zip compression.<br>"
                        + "When the user runs the installer, a progress bar will be decompressing the installer, while the above EXE<br>"
                        + "will decompress when it is installing all the packs.<br>"
                        + "LZMA may be able to compress it slightly further than the bzip2 compression used on the above EXE."
                        + "</html>");
                exel4j.setToolTipText("<html>"
                        + "This will create an installer that will compress the packs using bzip2 compression.<br>"
                        + "The packs will be decompressed when the packs are being installed (InstallPanel)."
                        + "</html>");
            }
        } catch (Exception ex) {
            RunnerClass.logger.log(Level.SEVERE, null, ex);
        }
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jar = new javax.swing.JCheckBox();
        exe = new javax.swing.JCheckBox();
        okBtn = new javax.swing.JButton();
        exel4j = new javax.swing.JCheckBox();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle("Create Installer");
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        jar.setText(".jar (Executable Java Archive)");

        exe.setText(".exe (7-Zip SFX Windows Native Executable)");

        okBtn.setText("OK");
        okBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                okBtnActionPerformed(evt);
            }
        });

        exel4j.setText(".exe (Windows Native Executable)");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jar)
                .addContainerGap(165, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(344, Short.MAX_VALUE)
                .addComponent(okBtn)
                .addContainerGap())
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(exel4j)
                .addContainerGap(136, Short.MAX_VALUE))
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(exe)
                .addContainerGap(70, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jar)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(exel4j)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(exe)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(okBtn)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void okBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_okBtnActionPerformed
        StringBuffer errormsg = new StringBuffer();
        if (!jar.isSelected() && !exel4j.isSelected() && !exe.isSelected())
            errormsg.append("You have not selected an installer to create.\n");
        if (errormsg.length() != 0) {
            String msg = errormsg.substring(0, errormsg.length() - 1);
            JOptionPane.showMessageDialog(RunnerClass.mf, msg, "Incomplete Data", JOptionPane.ERROR_MESSAGE);
            return;
        }
        fixRelativeValues();
        //To count the number of operations to be done
        int countOps = 0;
        //Queue for the tasks to be run, in order of the checkboxes
        final Queue<Task> tasks = new LinkedList<Task>();
        //The frame to show progress
        final ProgressFrame pf = new ProgressFrame(tasks);

        if (jar.isSelected()) {
            //Asks suer where to save the JAR file
            File f = GUIUtils.save(new javax.swing.filechooser.FileNameExtensionFilter("JAR (.jar)", new String[]{"jar"}));
            if (f != null) {
                if (!f.getName().endsWith(".jar"))
                    f = new File(f.getPath() + ".jar");
                countOps += 1;
                //Bzip2, because this is final compression
                JarTask jt = new JarTask(f, "bzip2", pf);
                tasks.offer(jt);
            }
        }
        if (exel4j.isSelected()) {
            //Asks suer where to save the EXE file
            File f = GUIUtils.save(new javax.swing.filechooser.FileNameExtensionFilter("EXE (.exe)", new String[]{"exe"}));
            if (f != null) {
                if (!f.getName().endsWith(".exe"))
                    f = new File(f.getPath() + ".exe");
                countOps += 2;
                //Bzip2, becasue l4j wont compress any furthur
                JarTask jt = new JarTask(new File(RunnerClass.homedir + "jar.jar"), "bzip2", pf);
                tasks.offer(jt);
                ExeTask et = new ExeTask(new File(RunnerClass.homedir + "jar.jar"), f, pf, false);
                tasks.offer(et);
            }
        }
        if (exe.isSelected()) {
            //Asks suer where to save the EXE file
            File f = GUIUtils.save(new javax.swing.filechooser.FileNameExtensionFilter("EXE (.exe)", new String[]{"exe"}));
            if (f != null) {
                if (!f.getName().endsWith(".exe"))
                    f = new File(f.getPath() + ".exe");
                countOps += 2;
                //Raw because 7zip will use LZMA which is better than bzip2
                JarTask jt = new JarTask(new File(RunnerClass.homedir + "jar.jar"), "raw", pf);
                tasks.offer(jt);
                ExeTask et = new ExeTask(new File(RunnerClass.homedir + "jar.jar"), f, pf, true);
                tasks.offer(et);
            }
        }
        //Create the xml file (comment out the try catch and you can force PackJacket to not create an XML file, rahter just put your own XML file in teh homedir
        try {
            InstallXML.generateXML(RunnerClass.mf.getXML(), RunnerClass.homedir + "xml.xml");
        } catch (IOException ex) {
            RunnerClass.logger.log(Level.SEVERE, null, ex);
        }
        //Close this window
        dispose();
        RunnerClass.mf.setEnabled(true);
        //Shows progress frame
        pf.setTitle("Creating Installer");
        pf.setIconImages(RunnerClass.mf.getIconImages());
        pf.tasksNum = countOps;
        pf.setLocationRelativeTo(null);
        pf.setVisible(true);
    }//GEN-LAST:event_okBtnActionPerformed

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        //Closes and enables main window
        dispose();
        RunnerClass.mf.setEnabled(true);
    }//GEN-LAST:event_formWindowClosing
    /**
     * Fixes relative values used for refpack in resources
     */
    private void fixRelativeValues() {
        for (Pack p : RunnerClass.mf.packsPanel.packs.list)
            if (!XMLUtils.isPack(p)) {
                String file = p.xmlFile;
                try {
                    DocumentBuilderFactory docF = DocumentBuilderFactory.newInstance();
                    DocumentBuilder docBuilder = docF.newDocumentBuilder();
                    Document doc = docBuilder.parse(file);
                    //   doc.getDocumentElement().n.normalize();
                    Element list1 = doc.getDocumentElement();
                    NodeList list = list1.getElementsByTagName("resources");
                    for (int a = 0; a < list.getLength(); a++) {
                        NodeList list2 = list.item(a).getChildNodes();
                        for (int b = 0; b < list2.getLength(); b++) {
                            if (!list2.item(b).getNodeName().equals("res"))
                                continue;
                            String file2 = list2.item(b).getAttributes().getNamedItem("src").getNodeValue();
                            System.out.println("file: " + file2);
                            System.out.println(new File(file2).isAbsolute());
                            if (!new File(file2).isAbsolute()) {
                                File in = new File(new File(file).getParent(), file2);
                                File out = new File(RunnerClass.homedir, in.getName());
                                out.deleteOnExit();
                                writeFile(new FileInputStream(in), new FileOutputStream(out));
                            }
                        }
                    }
                } catch (Exception e) {
                    RunnerClass.logger.log(Level.SEVERE, null, e);
                }
            }
    }

    /**
     * This method is almost a duplicate from Izpack2Exe and should be merged to one in future releases
     * Gets data from input file, and appends it to output
     * @param inFile the input file to read from
     * @param outFile the output file to write to
     * @throws IOException When an IOException occurs, it is thrown
     */
    private void writeFile(FileInputStream inFile, FileOutputStream outFile) throws IOException {
        //Buffer is 2 kilobytes
        byte[] buf = new byte[2048];
        int read;
        //Reads buffering, and stops if should
        while ((read = inFile.read(buf)) > 0)
            outFile.write(buf, 0, read);
        inFile.close();
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox exe;
    private javax.swing.JCheckBox exel4j;
    private javax.swing.JCheckBox jar;
    private javax.swing.JButton okBtn;
    // End of variables declaration//GEN-END:variables
}