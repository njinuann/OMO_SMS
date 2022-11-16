/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Ruby.model;

import java.util.ArrayList;

/**
 *
 * @author Pecherk
 */
public class BNUser
{
    private Long userId;
    private String staffNumber;
    private String staffName;
    private String userName;
    private Long roleId;
    private String role;
    private Long branchId;
    private String branchName;
    private ArrayList<USRole> roles = new ArrayList<>();

    /**
     * @return the userId
     */
    public Long getUserId()
    {
        return userId;
    }

    /**
     * @param userId the userId to set
     */
    public void setUserId(Long userId)
    {
        this.userId = userId;
    }

    /**
     * @return the staffNumber
     */
    public String getStaffNumber()
    {
        return staffNumber;
    }

    /**
     * @param staffNumber the staffNumber to set
     */
    public void setStaffNumber(String staffNumber)
    {
        this.staffNumber = staffNumber;
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
     * @return the roleId
     */
    public Long getRoleId()
    {
        return roleId;
    }

    /**
     * @param roleId the roleId to set
     */
    public void setRoleId(Long roleId)
    {
        this.roleId = roleId;
    }

    /**
     * @return the role
     */
    public String getRole()
    {
        return role;
    }

    /**
     * @param role the role to set
     */
    public void setRole(String role)
    {
        this.role = role;
    }

    /**
     * @return the branchId
     */
    public Long getBranchId()
    {
        return branchId;
    }

    /**
     * @param branchId the branchId to set
     */
    public void setBranchId(Long branchId)
    {
        this.branchId = branchId;
    }

    /**
     * @return the branchName
     */
    public String getBranchName()
    {
        return branchName;
    }

    /**
     * @param branchName the branchName to set
     */
    public void setBranchName(String branchName)
    {
        this.branchName = branchName;
    }

    /**
     * @return the roles
     */
    public ArrayList<USRole> getRoles()
    {
        return roles;
    }

    /**
     * @param roles the roles to set
     */
    public void setRoles(ArrayList<USRole> roles)
    {
        this.roles = roles;
    }
}
