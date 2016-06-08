package database.production;

import common.GImage;
import database.MYSQL_Helper;
import java.awt.Image;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.logging.Level;
import javax.imageio.ImageIO;
import utilities.ErrorLogger;
import utilities.PropertyManager;

/**
 * Done? May add update method later.
 * @author cws55854
 */
public class GImageManager implements database.GImageManager {

    public static final String GIMAGES_TABLE_NAME = "images";
    
    private static final String VAR_GIMAGE_ID = "imageId";
    private static final String VAR_IMAGE = "image";
    private static final String VAR_IMAGE_FOR_TIP = "forTip";

    private static final String PREPARED_GET_IMAGE_BY_ID
            = "select * from " + GIMAGES_TABLE_NAME + " where " + VAR_GIMAGE_ID + "=?";
    
    private static final String PREPARED_GET_ALL_TIP_GIMAGES
            = "select * from " + GIMAGES_TABLE_NAME + " where " + VAR_IMAGE_FOR_TIP + "=true";
    
    private static final String PREPARED_ADD_IMAGE
            = "insert into " + GIMAGES_TABLE_NAME + "(" + VAR_IMAGE + "," + VAR_IMAGE_FOR_TIP + ") values(?,?)";
    
    private static final String PREPARED_REMOVE_GIMAGE
            = "delete from " + GIMAGES_TABLE_NAME + " where " + VAR_GIMAGE_ID + "=?";
    
    private static final String PREPARED_UPDATE_GIMAGE
            = "update " + GIMAGES_TABLE_NAME + " set " + VAR_IMAGE + "=?," + VAR_IMAGE_FOR_TIP + "=? where " + VAR_GIMAGE_ID + "=?";
            
    public static final String[] SUPPORTED_IMAGES = {
        ".png"
    };
    
    /**
     * DOESN'T WORK 100% RIGHT NOW
     * TODO: Collin must finish this one. It will be a pain. May need change the
     * GImage table.
     * @param file
     * @param forGreenTip
     * @return ALWAYS NULL FOR NOW
     */
    @Override
    public GImage addGImage(File file, boolean forGreenTip) {
        Connection conn = MYSQL_Helper.getConnection();
        // return null if the given file is null
        if (file == null) {
            return null;
        }

        // Check to make sure that the file is of a supported type
        String fileName = file.getName();
        boolean fileSupported = false;
        for (String fileType : SUPPORTED_IMAGES) {
            if (fileName.endsWith(fileType)) {
                fileSupported = true;
                break;
            }
        }
        if (!fileSupported) {
            // File was not inserted
            return null;
        }

        PreparedStatement addImage = null;
        ResultSet results = null;
        // Try to open the File
        try (FileInputStream inputStream = new FileInputStream(file)) {
            // Try to prepare the statement
            try  {
                addImage = conn.prepareStatement(PREPARED_ADD_IMAGE, PreparedStatement.RETURN_GENERATED_KEYS);
                addImage.setBlob(1, inputStream);
                addImage.setBoolean(2, forGreenTip);
                if (addImage.executeUpdate() > 0)
                {
                    results = addImage.getGeneratedKeys();
                    if (results.first()) {
                       // int gimageid = results.getInt(1);
                        //System.out.println("gimageid(" + file.getName() + "): " + gimageid);
                        return getGImageById(results.getInt(1));
                    }
                }
                
            } catch (SQLException ex) {
                ErrorLogger.log(Level.WARNING, "GImageManager.addGImage: Unable to prepare statment.", ex);
            }
        } catch (FileNotFoundException ex) {
            ErrorLogger.log(Level.WARNING, "GImageManager.addGImage: File(" + file.toString() + "). File can not be found.");
        } catch (IOException ex) {
            ErrorLogger.log(Level.WARNING, "GImageManager.addGImage: File(" + file.toString() + "). Ran into an issue closing the file.");
        } finally {
            MYSQL_Helper.closeResultSet(results);
            MYSQL_Helper.closeStatement(addImage);
            MYSQL_Helper.returnConnection(conn);
        }

        // If an issue occurs, the image will not be inserted
        return null;
    }

    @Override
    public boolean deleteGImage(GImage gi) {
        Connection conn = MYSQL_Helper.getConnection();
        try (PreparedStatement removeImage = conn.prepareStatement(PREPARED_REMOVE_GIMAGE)) {
            removeImage.setInt(1, gi.getId());
            return removeImage.executeUpdate() > 0;
        } catch (SQLException ex) {
            ErrorLogger.log(Level.WARNING, "GImageManager.removeImage: Unable to prepare statement.");
        } finally {
            MYSQL_Helper.returnConnection(conn);
        }

        return false;
    }
    
    /**
     * Search the database for a <code>GImage</code> with the given unique ID.
     * @param searchID The unique ID of a particular <code>GImage</code> to search for
     * @return return an instance of the <code>GImage</code> with the given unique ID. Null if
     * no <code>GImage</code> with the given ID exists.
     */
    @Override
    public GImage getGImageById(int searchID) {
        Connection conn = MYSQL_Helper.getConnection();
        try (PreparedStatement getbyid = conn.prepareStatement(PREPARED_GET_IMAGE_BY_ID)) {
            getbyid.setInt(1, searchID);
            ResultSet results = getbyid.executeQuery();
            if (results.next()) {
                int id = results.getInt(VAR_GIMAGE_ID);
                boolean forTip = results.getBoolean(VAR_IMAGE_FOR_TIP);
                Blob imageBlob = results.getBlob(VAR_IMAGE);
                if (imageBlob != null) {
                    Image image = ImageIO.read(imageBlob.getBinaryStream());
                    return new GImage(id, image, forTip);
                }
            }
            MYSQL_Helper.closeResultSet(results);
        } catch (SQLException ex) {
            ErrorLogger.log(Level.SEVERE, "GreenTipManager.getGreenTipByText: Unable to prepare statement.");
        } catch (IOException ex) {
            ErrorLogger.log(Level.SEVERE, "GreenTipManager.getGreenTipByText: Unable to read image for GImageID(" + searchID + ").", ex);
        } finally {
            MYSQL_Helper.returnConnection(conn);
        }

        return null;
    }

    @Override
    public Collection<GImage> getGreenTipGImages() {
        Connection conn = MYSQL_Helper.getConnection();
        Collection<GImage> images = new LinkedList<>();
        int gimageID = -1; // so I can use most recent ID in the exception log
        try (PreparedStatement getImages = conn.prepareStatement(PREPARED_GET_ALL_TIP_GIMAGES)) {
            ResultSet results = getImages.executeQuery();
            while (results.next()) {
                // Get the values for this row
                gimageID = results.getInt(VAR_GIMAGE_ID);
                Image image = null;
                Blob imageBlob = results.getBlob(VAR_IMAGE);
                if (imageBlob != null) {
                    image = ImageIO.read(imageBlob.getBinaryStream());
                } else {
                    continue;
                }
                
                // query dictates that forGrenTip be 'true, so just use 'true.
                images.add(new GImage(gimageID, image, true));
            }
        } catch (SQLException ex) {
            ErrorLogger.log(Level.SEVERE, "GreenTipManager.getGreenTipGImages: Unable to prepare statement.");
        } catch (IOException ex) {
            ErrorLogger.log(Level.WARNING, "GreenTipManager.getGreenTipGImages: Unable to load image for GImageID(" + gimageID + ").");
        } finally {
            MYSQL_Helper.returnConnection(conn);
        }
        return images;
    }

    public static void main(String[] args) {
        // PLEASE DON'T RUN THIS.
        
        PropertyManager.configure("P:/CompSci480/Lucid/web/WEB-INF/config/General.properties");
        GImageManager gim = new GImageManager();
        
        /*
        Methods tested: 
        addGImage
        getGImageById
        getGreenTupGImages
        */
        
        System.out.println("Getting all gimages...");
        Collection<GImage> gimages = gim.getGreenTipGImages();
        for(GImage g : gimages) { 
            System.out.println("GImage: " + g);
            gim.deleteGImage(g);
        }
        
        System.out.println("\nAdding more images...\n");
        
        GImage a = gim.addGImage(new File("P:\\CompSci480\\Lucid\\web\\Images\\GreeneTipsBulb.png"), true);
        GImage b = gim.addGImage(new File("P:\\CompSci480\\Lucid\\web\\Images\\GreeneTipsRecycle.png"), true);
        GImage c = gim.addGImage(new File("P:\\CompSci480\\Lucid\\web\\Images\\GreeneTipsWater.png"), true);
        GImage d = gim.addGImage(new File("P:\\CompSci480\\Lucid\\web\\Images\\GreeneTipsTree.png"), true);

        
        System.out.println("GImage A: " + a);
        System.out.println("GImage B: " + b);
        System.out.println("GImage C: " + c);
        System.out.println("GImage D: " + d);
        
        System.out.println("");
        
        // Test getGImageById
        System.out.println("Get gimage by id(a.id): " + gim.getGImageById(a.getId()));
        System.out.println("Get gimage by id(-1): " + gim.getGImageById(-1));
        
        System.out.println("Remove GImage A: " + gim.deleteGImage(a));
        System.out.println("Remove GImage B: " + gim.deleteGImage(b));
//        
        System.out.println("Get gimage by id(a.id): " + gim.getGImageById(a.getId()));
//        
//        
//        //Add GreenTipImages
        gim.addGImage(new File("P:\\CompSci480\\Lucid\\web\\Images\\GreeneTipsBulb.png"), true);
        gim.addGImage(new File("P:\\CompSci480\\Lucid\\web\\Images\\GreeneTipsRecycle.png"), true);
//        gim.addGImage(new File("P:\\CompSci480\\Lucid\\web\\Images\\GreeneTipsWater.png"), true);
//        gim.addGImage(new File("P:\\CompSci480\\Lucid\\web\\Images\\GreeneTipsTree.png"), true);
        
    }


}
