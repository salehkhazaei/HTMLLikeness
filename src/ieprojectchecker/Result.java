
package ieprojectchecker;

import java.util.ArrayList;
import javafx.util.Pair;

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
public class Result {
    public Student s1;
    public Student s2;
    public double max_prob;
    
    public ArrayList<Compare> results = new ArrayList<>();

    public Result(Student s1, Student s2) {
        this.s1 = s1;
        this.s2 = s2;
        max_prob = 0.0;
    }
    
}
