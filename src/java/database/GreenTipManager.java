package database;

import common.GreenTip;
import java.util.Collection;

/**
 *
 * @author cmr98507
 */
public interface GreenTipManager {
    
    public GreenTip addGreenTip(String text, int gImageId);
    public boolean updateGreenTip(GreenTip greenTip);
    public boolean deleteGreenTip(GreenTip greenTip);
    public GreenTip getGreenTipById(int greenTipId);
    public Collection<GreenTip> getAllGreenTips();
    
}
