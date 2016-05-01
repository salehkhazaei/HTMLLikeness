
package ieprojectchecker;

import java.io.File;
import java.util.ArrayList;

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
public class Student {
    public String name;
    public ArrayList<LoadedFile> html = new ArrayList<>();
    public ArrayList<LoadedFile> css = new ArrayList<>();
    public ArrayList<LoadedFile> js = new ArrayList<>();
    public ArrayList<LoadedFile> php = new ArrayList<>();
    public int checked; // 0-free, 1-checking, 2-checked
    public Student() {
        checked = 0;
    }
}
