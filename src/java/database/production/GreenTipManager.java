package database.production;

import common.GreenTip;
import database.MYSQL_Helper;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.logging.Level;
import utilities.ErrorLogger;
import utilities.PropertyManager;

/**
 * Done?
 * Manager which manipulates (add, remove, update, get) <code>GreenTip</code>s 
 * stored in the database.
 * @author cmr98507 cws55854
 */
public class GreenTipManager implements database.GreenTipManager {

    public static final String GREEN_TIP_TABLE_NAME = "greenTips";
    
    private static final String VAR_ID = "tipid";
    private static final String VAR_TIP_TEXT = "tipText";
    private static final String VAR_GIMAG_ID = "picture";

    private static final String PREPARED_ADD_GREEN_TIP
            = "insert into " + GREEN_TIP_TABLE_NAME + " (" + VAR_TIP_TEXT + "," + VAR_GIMAG_ID + ") values(?,?)";

    private static final String PREPARED_DELETE_GREEN_TIP
            = "delete from " + GREEN_TIP_TABLE_NAME + " where " + VAR_ID + "=?";

    private static final String PREPARED_GET_GREEN_TIP_BY_ID
            = "select * from " + GREEN_TIP_TABLE_NAME + " where " + VAR_ID + "=?";

    private static final String PREPARED_GET_ALL_GREEN_TIPS
            = "select * from " + GREEN_TIP_TABLE_NAME;

    private static final String PREPARED_UPDATE_GREEN_TIP
            = "update " + GREEN_TIP_TABLE_NAME + " set " + VAR_TIP_TEXT + "=?," + VAR_GIMAG_ID + "=? where " + VAR_ID + "=?";

    /**
     * Attempts to add a new green tip with the given text and gimage
     *
     * @param text <code>GreenTip</code> text
     * @param gImageId <code>GreenTip</code> gImage ID.
     * @return The <code>GreenTip</code> which was added. If the addition fails,
     * null is returned.
     */
    @Override
    public GreenTip addGreenTip(String text, int gImageId) {
        // Do some quick data validating.
        if (text == null) {
            return null;
        }
        
        ResultSet results = null;
        // Create the prepared statement
        try (PreparedStatement addgt = MYSQL_Helper.getConnection().prepareStatement(PREPARED_ADD_GREEN_TIP, PreparedStatement.RETURN_GENERATED_KEYS)) {

            // Set the text parameter
            addgt.setString(1, text);
            // Set the giamge id parameter
            addgt.setInt(2, gImageId);

            // Execute and try to find it if successful
            if (addgt.executeUpdate() > 0) {
                results = addgt.getGeneratedKeys();
                if (results.next()) {
                    return getGreenTipById(results.getInt(1));
                }
            }
        } catch (SQLException ex) {
            ErrorLogger.log(Level.WARNING, "GreenTipManager.addGreenTip: Unable to prepare statment.");
        } finally {
            MYSQL_Helper.closeResultSet(results);
        }
        return null;
    }

    /**
     * Will update the <code>GreenTip</code> in the database, with the same
     * greenTipID as the given <code>GreenTip</code>, to have the same
     * text and imageID as the given <code>GreenTip</code>
     * @param tip The updated tip
     * @return A null pointer if the update is unsuccessful (will be unsuccessful
     * if no <code>GreenTip</code> with the same ID exists). Otherwise, will return
     * the updated <code>GreenTip</code> (identically, the <code>GreenTip</code> given). 
     */
    @Override
    public boolean updateGreenTip(GreenTip tip) {
        try (PreparedStatement updategt = MYSQL_Helper.prepareStatement(PREPARED_UPDATE_GREEN_TIP)) {
            updategt.setString(1, tip.getTipText());
            updategt.setInt(2, tip.getGimageId());
            updategt.setInt(3, tip.getId());
            if (updategt.executeUpdate() > 0) {
               return true;
            }
        } catch (SQLException ex) {
            ErrorLogger.log(Level.SEVERE, "GreenTipManager.updateGreenTip: Unable to prepare statment.");
        }
        return false;
    }

    /**
     * Removes the given <code>GreenTip</code> from the database.
     *
     * @param tip The GreenTip to remove
     * @return True if the given <code>GreenTip</code> was removed. Otherwise false.
     * False most likely means that the <code>GreenTip</code> given is no longer
     * in the database.
     */
    @Override
    public boolean deleteGreenTip(GreenTip tip) {
        // Make sure the tip is not null
        if (tip == null) {
            return false;
        }
        try (PreparedStatement removegt = MYSQL_Helper.prepareStatement(PREPARED_DELETE_GREEN_TIP)) {
            removegt.setInt(1, tip.getId());
            return removegt.executeUpdate() > 0; // > 0 if successful, == 0 if the record DNE and failed
        } catch (SQLException ex) {
            ErrorLogger.log(Level.SEVERE, "GreenTipManager.deleteGreenTip: Unable to prepare statement.");
        }
        return false; // False is returned if the statement is unable to be prepared.
    }

    /**
     * Returns a <code>Collection</code> containing all <code>GreenTip</code>'s.
     *
     * @return A <code>Collection</code> containing all of the
     * <code>GreenTip</code>'s.
     */
    @Override
    public Collection<GreenTip> getAllGreenTips() {
        Collection<GreenTip> greenTips = new LinkedList<>();
        try (PreparedStatement getallgt = MYSQL_Helper.prepareStatement(PREPARED_GET_ALL_GREEN_TIPS)) {
            ResultSet results = getallgt.executeQuery();
            while (results.next()) {
                // Get the ID, text, and image
                int id = results.getInt(VAR_ID);
                String text = results.getString(VAR_TIP_TEXT);
                int gimgid = results.getInt(VAR_GIMAG_ID);

                // Add the image to the collection being returned
                greenTips.add(new GreenTip(id, text, gimgid));
            }
        } catch (SQLException ex) {
            ErrorLogger.log(Level.SEVERE, "GreenTipManager.getAllGreenTips: Unable to prepare statement.");
        }
        return greenTips;
    }

    /**
     * Finds and returns the first <code>GreenTip</code> with the given text.
     *
     * @param greenTipId The id of the green tip to retrieve 
     * @return The first <code>GreenTip</code> with the given text
     */
    @Override
    public GreenTip getGreenTipById(int greenTipId) {
        try (PreparedStatement getgt = MYSQL_Helper.prepareStatement(PREPARED_GET_GREEN_TIP_BY_ID)) {
            getgt.setInt(1, greenTipId);
            ResultSet results = getgt.executeQuery();
            if (results.next()) {
                // Has at least 1 result
                int id = results.getInt(VAR_ID);
                int imgID = results.getInt(VAR_GIMAG_ID);
                String tip_text = results.getString(VAR_TIP_TEXT);
                return new GreenTip(id, tip_text, imgID);
            }
        } catch (SQLException ex) {
            ErrorLogger.log(Level.SEVERE, "GreenTipManager.getGreenTipByText: Unable to prepare statement.");
        }
        return null;
    }

    public static void main(String[] args) {
        PropertyManager.configure("P:/CompSci480/Lucid/web/WEB-INF/config/General.properties");
        GreenTipManager gtm = new GreenTipManager();
        
//        for(GreenTip gt : gtm.getAllGreenTips()) {
//            gt.setGImageId(114);
//            gtm.updateGreenTip(gt);
//        }

//         Test getAllGreenTips()
//        Collection<GreenTip> greenTips = gtm.getAllGreenTips();
//        System.out.println("Total green tip count: " + greenTips.size());
//        for (GreenTip gt : greenTips) {
//            System.out.println(gt);
//        }
//
//         Test addGreenTip
//        System.out.println("Add greenTip(null, 1): " + gtm.addGreenTip(null, 1));
//        System.out.println("Add greenTip(Test, -1), -1 is invalid: " + gtm.addGreenTip("test", -1));
//        System.out.println("Add greenTip(Green tip!, 1): " + gtm.addGreenTip("Green tip!", 1));
//
//         Test GetGreenTipByText
//        System.out.println("GetByText(Green tip!): " + gtm.getGreenTipByText("Green tip!"));
//        System.out.println("GetByText(null): " + gtm.getGreenTipByText(null));
//        System.out.println("GetByText(asdf): " + gtm.getGreenTipByText("asdf"));
//
//         Test deleteGreenTip with null
//        System.out.println("deleteGreenTip(null): " + gtm.deleteGreenTip(null));
//        GreenTip greenTip1 = gtm.getGreenTipByText("Green tip!");
//        System.out.println("AddGreenTip(" + greenTip1 + "): " + gtm.addGreenTip(greenTip1.getTipText(), greenTip1.getGimageId()));
//        System.out.println("deleteGreenTip(" + greenTip1 + "): " + gtm.deleteGreenTip(greenTip1));
//        System.out.println("deleteGreenTip(" + greenTip1 + "): " + gtm.deleteGreenTip(greenTip1));
//        System.out.println("deleteGreenTip(" + greenTip1 + "): " + gtm.deleteGreenTip(greenTip1));
//        
//        greenTip1.setTipText("NEW TEXT");
//        System.out.println("updateGreenTip(" + greenTip1 + "): " + gtm.updateGreenTip(greenTip1));
//
//         Test deleteGreenTip and getAllGreenTips()
//        greenTips = gtm.getAllGreenTips();
//        System.out.println("Total green tip count: " + greenTips.size());
//        for (GreenTip gt : greenTips) {
//            System.out.println(gt);
//            gtm.deleteGreenTip(gt);
//        }
    }

}
