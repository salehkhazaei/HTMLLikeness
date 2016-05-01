package gui;

import ieprojectchecker.Compare;
import ieprojectchecker.LoadedFile;
import ieprojectchecker.Result;
import ieprojectchecker.Student;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.util.Pair;
import javax.swing.table.DefaultTableModel;
import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 *
 * @author Saleh Khazaei
 * @email saleh.khazaei@gmail.com Copyright:
 *
 * Copyright (C) Saleh Khazaei
 *
 * This code is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 3, or (at your option) any later version.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this code; see the file COPYING. If not, write to the Free Software
 * Foundation, 675 Mass Ave, Cambridge, MA 02139, USA.
 *
 * EndCopyright:
 */
public class Processing extends javax.swing.JPanel {

    public enum ThreadState {

        Waiting,
        CheckingStudent,
        Comparing,
        Completed
    }

    public class ProcessingThread extends Thread {

        ArrayList<Student> students;
        ThreadState ts;
        String f1;
        String f2;
        String ft;

        public ProcessingThread(ArrayList<Student> students) {
            this.students = students;
            ts = ThreadState.Waiting;
            f1 = f2 = ft = "";
        }

        public Pair<Integer, Integer> compareGraph(Elements d1, Elements d2) {
            Elements doc1;
            Elements doc2;

            if (d1.size() > d2.size()) {
                doc1 = d1;
                doc2 = d2;
            } else {
                doc1 = d2;
                doc2 = d1;
            }

            int match = 0;
            int mismatch = 0;
            for (int i = 0; i < doc2.size(); i++) {
                for (int j = 0; j < doc1.size(); j++) {
                    Element w2 = doc2.get(i);
                    Element w1 = doc1.get(i);

                    if (w2.tagName().equals(w1.tagName()) && w2.id().equals(w1.id())) {
                        boolean equal = true;
                        Iterator<Attribute> it = w1.attributes().iterator();
                        while (it.hasNext()) {
                            Attribute at = it.next();
                            if (w2.hasAttr(at.getKey())) {
                                if (!w2.attr(at.getKey()).equals(at.getValue())) {
                                    equal = false;
                                    break;
                                }
                            }
                        }

                        if (equal) {
                            match += compareGraph(w1.children(), w2.children()).getKey() + 1;
                        } else {
                            mismatch++;
                        }
                    }
                }
            }
            return new Pair(match, mismatch);
        }

        @Override
        public void run() {
            super.run();
            int targetindex = 0;
            int studentindex = 0;
            while (true) {
                switch (ts) {
                    case Waiting:
                        break;
                    case CheckingStudent:
                        f1 = "Looking for a student ...";
                        f2 = "";
                        ft = "";
                        Student s = null;
                        for (; studentindex < students.size(); studentindex++) {
                            Student temp = students.get(studentindex);
                            synchronized (temp) {
                                if (temp.checked == 0) {
                                    s = temp;
                                    s.checked = 1;
                                    break;
                                }
                            }
                        }

                        if (s != null) {
                            f1 = s.name;
                            ft = "Php";
                            for (int i = 0; i < s.php.size(); i++) {
                                LoadedFile lf = s.php.get(i);
                                f2 = lf.address;

                                // extracting non-php
                                String php_content = "";
                                String non_php_content = "";

                                boolean is_php_content = false;

                                int index = 0;
                                String content = lf.content;
                                while (index < content.length()) {
                                    if (is_php_content) {
                                        // looking for ?>
                                        int q = content.indexOf("?>");
                                        if (q == -1) {
                                            // not found 
                                            // rest of string is php
                                            php_content += content;
                                            break;
                                        } else {
                                            php_content += content.substring(0, q);
                                            content = content.substring(q + 2);
                                            is_php_content = false;
                                        }
                                    } else {
                                        // looking for <?
                                        int q = content.indexOf("<?");
                                        if (q == -1) {
                                            // not found 
                                            // rest of string is non-php
                                            non_php_content += content;
                                            break;
                                        } else {
                                            non_php_content += content.substring(0, q);
                                            content = content.substring(q + 2);
                                            if (content.startsWith("php")) {
                                                content = content.substring(3);
                                            }
                                            is_php_content = true;
                                        }
                                    }
                                }

                                lf.content = php_content;
                                s.html.add(new LoadedFile(lf.address, non_php_content));
                            }

                            String phps = "";
                            for (int i = 0; i < s.php.size(); i++) {
                                LoadedFile lf = s.php.get(i);
                                f2 = lf.address;

                                phps += lf.content;
                            }
/*                            String jss = "";
                            for (int i = 0; i < s.js.size(); i++) {
                                LoadedFile lf = s.js.get(i);
                                f2 = lf.address;

                                jss += lf.content;
                            }
                            String csss = "";
                            for (int i = 0; i < s.css.size(); i++) {
                                LoadedFile lf = s.css.get(i);
                                f2 = lf.address;

                                csss += lf.content;
                            }*/

                            try {
                                FileOutputStream file = new FileOutputStream(new File(s.name + "_phps.php"));
                                file.write(phps.getBytes());
                            } catch (FileNotFoundException ex) {
                                Logger.getLogger(Processing.class.getName()).log(Level.SEVERE, null, ex);
                            } catch (IOException ex) {
                                Logger.getLogger(Processing.class.getName()).log(Level.SEVERE, null, ex);
                            }
/*                            try {
                                FileOutputStream file = new FileOutputStream(new File(s.name + "_jss.php"));
                                file.write(jss.getBytes());
                            } catch (FileNotFoundException ex) {
                                Logger.getLogger(Processing.class.getName()).log(Level.SEVERE, null, ex);
                            } catch (IOException ex) {
                                Logger.getLogger(Processing.class.getName()).log(Level.SEVERE, null, ex);
                            }
                            try {
                                FileOutputStream file = new FileOutputStream(new File(s.name + "_csss.php"));
                                file.write(csss.getBytes());
                            } catch (FileNotFoundException ex) {
                                Logger.getLogger(Processing.class.getName()).log(Level.SEVERE, null, ex);
                            } catch (IOException ex) {
                                Logger.getLogger(Processing.class.getName()).log(Level.SEVERE, null, ex);
                            }*/
                        } else {
                            ts = ThreadState.Completed;
                        }
                        break;
                    case Comparing:
                        f1 = "Looking for a compare ...";
                        f2 = "";
                        ft = "";
                        Compare target = null;
                        for (; targetindex < compares.size(); targetindex++) {
                            Compare c = compares.get(targetindex);
                            synchronized (c) {
                                if (!c.checked) {
                                    c.checked = true;
                                    target = c;
                                    break;
                                }
                            }
                        }
                        if (target == null) {
                            ts = ThreadState.Completed;
                            f1 = "Completed";
                            f2 = "";
                            ft = "";
                        } else {
                            // we have a compare :D
                            Document doc1, doc2;
                            long doc1size, doc2size;
                            synchronized (target.file1) {
                                doc1 = target.file1.getRoot();
                                doc1size = target.file1.getSize();
                            }
                            synchronized (target.file2) {
                                doc2 = target.file2.getRoot();
                                doc2size = target.file2.getSize();
                            }

                            f1 = target.file1.address;
                            f2 = target.file2.address;
                            ft = "HTML";

                            int matches = compareGraph(doc1.children(), doc2.children()).getKey();
                            target.prob = ((double) matches / (double) (doc1size > doc2size ? doc1size : doc2size));
                        }
                        break;
                }
                try {
                    Thread.sleep(50);
                } catch (InterruptedException ex) {
                    Logger.getLogger(Processing.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }
    /**
     * Creates new form Processing
     */
    private ProcessingThread threads[];
    public ArrayList<Compare> compares = new ArrayList<>();
    public ArrayList<Result> results = new ArrayList<>();

    public Processing() {
        initComponents();
        new Thread() {
            @Override
            public void run() {
                while (!Processing.this.isVisible()) {
                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(Processing.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }

                MainFrame parent = ((MainFrame) Processing.this.getParent().getParent().getParent().getParent());
                int n = ((PullingStudents) parent.pull).numberOfThreads;
                threads = new ProcessingThread[n];
                for (int i = 0; i < n; i++) {
                    threads[i] = new ProcessingThread(parent.students);
                    threads[i].start();
                    threads[i].ts = ThreadState.CheckingStudent;
                }

                int n_of_completed = 0;
                String[][] data = new String[threads.length][5];
                String[] header = new String[]{
                    "Thread #", "State", "File 1", "File 2", "File Type"
                };
                DefaultTableModel model = new DefaultTableModel(data, header);
                Processing.this.jTable1.setModel(model);
                boolean go_for_compare = false;
                do {
                    n_of_completed = 0;
                    for (int i = 0; i < threads.length; i++) {
                        if (threads[i].ts == ThreadState.Completed) {
                            n_of_completed++;
                        }
                        data[i][0] = i + "";
                        data[i][1] = threads[i].ts.toString();
                        data[i][2] = threads[i].f1;
                        data[i][3] = threads[i].f2;
                        data[i][4] = threads[i].ft;
                    }

                    synchronized (model) {
                        model.setDataVector(data, header);
                    }
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(Processing.class.getName()).log(Level.SEVERE, null, ex);
                    }

                    if (n_of_completed == threads.length && !go_for_compare) {
                        go_for_compare = true;
                        Processing.this.jLabel1.setText("Please wait... Creating compare graph! (Clustring)");
                        // create compares 
                        long max_size = 0;
                        long min_size = 0;
                        for (int i = 0; i < parent.students.size(); i++) {
                            Student stu = parent.students.get(i);

                            for (int k = 0; k < stu.html.size(); k++) {
                                long len = stu.html.get(k).content.length();
                                if (max_size < len) {
                                    max_size = len;
                                }
                                if (min_size > len) {
                                    min_size = len;
                                }
                            }
                        }

                        long size = (max_size - min_size) / parent.pull.numberOfClusters;

                        FileOutputStream fos = null;
                        try {
                            fos = new FileOutputStream(new File("compares.txt"));
                        } catch (FileNotFoundException ex) {
                            Logger.getLogger(Processing.class.getName()).log(Level.SEVERE, null, ex);
                        }

                        Processing.this.jLabel1.setText("Please wait... Creating compare graph! (Creating compares)");
                        for (int i = 0; i < parent.students.size(); i++) {
                            for (int j = 0; j < parent.students.size(); j++) {
                                if (i == j) {
                                    continue;
                                }

                                Student stui = parent.students.get(i);
                                Student stuj = parent.students.get(j);

                                for (int k = 0; k < stui.html.size(); k++) {
                                    for (int l = k; l < stuj.html.size(); l++) {
                                        LoadedFile fil1 = stui.html.get(k);
                                        LoadedFile fil2 = stuj.html.get(l);

                                        int cluster1 = -1;
                                        int cluster2 = -1;
                                        for (int y = 0; y < parent.pull.numberOfClusters; y++) {
                                            if (min_size + y * size <= fil1.content.length() && fil1.content.length() <= min_size + (y + 1) * size) {
                                                cluster1 = y + 1;
                                            }
                                            if (min_size + y * size <= fil2.content.length() && fil2.content.length() <= min_size + (y + 1) * size) {
                                                cluster2 = y + 1;
                                            }
                                        }

                                        if (cluster1 == cluster2 || true) {
                                            compares.add(new Compare(stui.html.get(k), stuj.html.get(l), stui, stuj));
                                            try {
                                                fos.write((stui.html.get(k).address + "\t\t\t\t" + stuj.html.get(l).address + "\n").getBytes());
                                            } catch (IOException ex) {
                                                Logger.getLogger(Processing.class.getName()).log(Level.SEVERE, null, ex);
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        Processing.this.jLabel1.setText("Please wait... Comparing! (0/" + compares.size() + ")");
                        for (int i = 0; i < threads.length; i++) {
                            threads[i].ts = ThreadState.Comparing;
                        }
                        n_of_completed = 0;
                    }
                    if (go_for_compare) {
                        int count = 0;
                        for (int i = 0; i < compares.size(); i++) {
                            Compare c = compares.get(i);
                            synchronized (c) {
                                if (compares.get(i).checked) {
                                    count++;
                                }
                            }
                        }
                        Processing.this.jLabel1.setText("Please wait... Comparing! (" + count + "/" + compares.size() + ")");
                        jProgressBar1.setValue((int) ((double) count * 100.0 / (double) compares.size()));
                    }
                } while (n_of_completed != threads.length);

                Processing.this.jLabel1.setText("Please wait... Preparing results!");

                compares.sort(new Comparator<Compare>() {

                    @Override
                    public int compare(Compare o1, Compare o2) {
                        if (o1.prob < o2.prob) {
                            return 1;
                        } else if (o1.prob == o2.prob) {
                            return 0;
                        } else {
                            return -1;
                        }
                    }
                });
                for (int i = 0; i < compares.size(); i++) {
                    Compare c = compares.get(i);

                    boolean found = false;
                    for (int j = 0; j < results.size(); j++) {
                        Result r = results.get(j);
                        if ((c.s1 == r.s1 && c.s2 == r.s2) || (c.s2 == r.s1 && c.s1 == r.s2)) {
                            r.results.add(c);
                            if (r.max_prob < c.prob) {
                                r.max_prob = c.prob;
                            }
                            found = true;
                        }
                    }
                    if (!found) {
                        Result r = new Result(c.s1, c.s2);
                        r.results.add(c);
                        if (r.max_prob < c.prob) {
                            r.max_prob = c.prob;
                        }
                        results.add(r);
                    }
                }
                // completed
                if (parent.resu == null) {
                    parent.resu = new Results();
                    parent.add(parent.resu);
                }
                parent.resu.setVisible(true);
                parent.proc.setVisible(false);
            }

        }.start();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jProgressBar1 = new javax.swing.JProgressBar();
        jLabel1 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jButton1 = new javax.swing.JButton();

        setFont(new java.awt.Font("Calibri Light", 0, 18)); // NOI18N

        jLabel1.setFont(new java.awt.Font("Calibri Light", 0, 24)); // NOI18N
        jLabel1.setText("Please wait ...Loading files");

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {},
            new String [] {
                "Thread #","State", "File 1", "File 2", "File Type"
            }
        ));
        jScrollPane1.setViewportView(jTable1);

        jButton1.setText("Cancel");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 661, Short.MAX_VALUE)
                    .addComponent(jProgressBar1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jButton1)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jProgressBar1, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 263, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton1)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        System.exit(0);
    }//GEN-LAST:event_jButton1ActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JProgressBar jProgressBar1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    // End of variables declaration//GEN-END:variables

}
