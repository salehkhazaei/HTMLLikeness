
package ieprojectchecker;

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
public class Compare {
    public LoadedFile file1;
    public LoadedFile file2;
    
    public Student s1;
    public Student s2;
    
    public boolean checked;
    public double prob;

    public Compare(LoadedFile file1, LoadedFile file2, Student s1, Student s2) {
        this.file1 = file1;
        this.file2 = file2;
        this.s1 = s1;
        this.s2 = s2;
        this.checked = false;
        this.prob = 0.0;
    }
    
    
}
