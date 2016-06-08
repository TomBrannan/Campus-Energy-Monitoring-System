package database;

import common.GImage;
import java.awt.Image;
import java.io.File;
import java.util.Collection;

/**
 *
 * @author cws55854
 */
public interface GImageManager {
    
    public GImage addGImage(File file, boolean forGreenTip);
    //public boolean updateGImage(GImage gi);
    public boolean deleteGImage(GImage gi);
    public GImage getGImageById(int id);
    public Collection<GImage> getGreenTipGImages();
    
}
