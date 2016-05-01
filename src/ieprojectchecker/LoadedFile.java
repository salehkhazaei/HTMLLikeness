package ieprojectchecker;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

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
public class LoadedFile {

    public String address;
    public String content;

    private Document doc;
    private long size;
    
    private boolean doc_loaded ;
    private boolean size_loaded;

    public LoadedFile(File f) {
        FileInputStream fis = null;
        try {
            address = f.getAbsolutePath();
            content = "";
            fis = new FileInputStream(f);
            byte[] data = new byte[(int) f.length()];
            fis.read(data);
            fis.close();
            content = new String(data, "UTF-8");
        } catch (FileNotFoundException ex) {
            Logger.getLogger(LoadedFile.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(LoadedFile.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                fis.close();
            } catch (IOException ex) {
                Logger.getLogger(LoadedFile.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public LoadedFile(String address, String content) {
        this.address = address;
        this.content = content;
    }

    public Document getRoot() {
        if ( ! doc_loaded )
        {
            doc = Jsoup.parse(content);
            doc_loaded = true ;
        }
        return doc ;
    }

    public int graphSize(Elements d) {
        int size = 0;
        for (int i = 0; i < d.size(); i++) {
            size += graphSize(d.get(i).children());
        }
        return size + 1;
    }

    public long getSize() {
        if ( ! size_loaded )
        {
            size_loaded = true ;
            size = graphSize(doc.children());
        }
        return size;
    }
}
