/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Ruby.model;

import java.util.ArrayList;
import java.util.Date;

/**
 *
 * @author Pecherk
 */
public class AXUser implements Comparable<AXUser>
{
    private String userName;
    private String staffName;
    private String userNumber;
    private String sysUser;
    private String status;
    private Date sysDate = new Date();
    private ArrayList<String> roles = new ArrayList<>();

    /**
     * @return the userName
     */
    public String getUserName()
    {
        return userName;
    }

    /**
     * @param userName the userName to set
     */
    public void setUserName(String userName)
    {
        this.userName = userName;
    }

    /**
     * @return the staffName
     */
    public String getStaffName()
    {
        return staffName;
    }

    /**
     * @param staffName the staffName to set
     */
    public void setStaffName(String staffName)
    {
        this.staffName = staffName;
    }

    /**
     * @return the userNumber
     */
    public String getUserNumber()
    {
        return userNumber;
    }

    /**
     * @param userNumber the userNumber to set
     */
    public void setUserNumber(String userNumber)
    {
        this.userNumber = userNumber;
    }

    /**
     * @return the sysUser
     */
    public String getSysUser()
    {
        return sysUser;
    }

    /**
     * @param sysUser the sysUser to set
     */
    public void setSysUser(String sysUser)
    {
        this.sysUser = sysUser;
    }

    /**
     * @return the status
     */
    public String getStatus()
    {
        return status;
    }

    /**
     * @param status the status to set
     */
    public void setStatus(String status)
    {
        this.status = status;
    }

    /**
     * @return the sysDate
     */
    public Date getSysDate()
    {
        return sysDate;
    }

    /**
     * @param sysDate the sysDate to set
     */
    public void setSysDate(Date sysDate)
    {
        this.sysDate = sysDate;
    }

    /**
     * @return the roles
     */
    public ArrayList<String> getRoles()
    {
        return roles;
    }

    /**
     * @param roles the roles to set
     */
    public void setRoles(ArrayList<String> roles)
    {
        this.roles = roles;
    }

    @Override
    public String toString()
    {
        return getUserNumber() + "~" + getStaffName();
    }

    @Override
    public int compareTo(AXUser o)
    {
        return String.CASE_INSENSITIVE_ORDER.compare(getUserNumber(), o.getUserNumber());
    }
}
