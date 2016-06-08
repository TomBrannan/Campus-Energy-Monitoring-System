/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package common;

/**
 *
 * @author cmr98507
 */
public class University {
    private String universityName;
    private int universityId;

    public University() {
    }
    
    public University(int universityId, String universityName)
    {
        this.universityId = universityId;
        this.universityName = universityName;
    }
    
    public String getUniversityName() {
        return universityName;
    }

    public void setUniversityName(String universityName) {
        this.universityName = universityName;
    }

    public int getUniversityId() {
        return universityId;
    }

    public void setUniversityId(int universityId) {
        this.universityId = universityId;
    }
    
}
