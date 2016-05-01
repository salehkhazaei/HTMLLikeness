
package gui;

import ieprojectchecker.Student;
import java.awt.BorderLayout;
import java.util.ArrayList;
import javax.swing.JFrame;

/**
 *
 * @author Saleh Khazaei
 * @email saleh.khazaei@gmail.com
 * Copyright:

 Copyright (C) Saleh Khazaei

 This code is free software; you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation; either version 3, or (at your option)
 any later version.

 This code is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this code; see the file COPYING.  If not, write to
 the Free Software Foundation, 675 Mass Ave, Cambridge, MA 02139, USA.

 *EndCopyright:
 */
public class MainFrame extends JFrame {
    public PullingStudents pull = new PullingStudents();
    public Processing proc ;
    public Results resu ;
    public ArrayList<Student> students = new ArrayList<>();
    public MainFrame() {
        this.setSize(800,500);
        this.setLayout(new BorderLayout());
        this.add(pull);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setVisible(true);
        this.setTitle("By Saleh Khazaei (9131089)");
    }
}
