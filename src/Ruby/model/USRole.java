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
public class USRole
{
    private Long roleId;
    private Long userId;
    private String role;
    private String userName;
    private Long branchId;
    private Long buRoleId;
    private Long userRoleId;
    private String supervisor;
    private String branchCode;
    private String branchName;
    private ArrayList<URLimit> limits = new ArrayList<>();
    private ArrayList<TLDrawer> drawers = new ArrayList<>();

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
     * @return the supervisor
     */
    public String getSupervisor()
    {
        return supervisor;
    }

    /**
     * @param supervisor the supervisor to set
     */
    public void setSupervisor(String supervisor)
    {
        this.supervisor = supervisor;
    }

    /**
     * @return the branchCode
     */
    public String getBranchCode()
    {
        return branchCode;
    }

    /**
     * @param branchCode the branchCode to set
     */
    public void setBranchCode(String branchCode)
    {
        this.branchCode = branchCode;
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
     * @return the limits
     */
    public ArrayList<URLimit> getLimits()
    {
        return limits;
    }

    /**
     * @param limits the limits to set
     */
    public void setLimits(ArrayList<URLimit> limits)
    {
        this.limits = limits;
    }

    /**
     * @return the buRoleId
     */
    public Long getBuRoleId()
    {
        return buRoleId;
    }

    /**
     * @param buRoleId the buRoleId to set
     */
    public void setBuRoleId(Long buRoleId)
    {
        this.buRoleId = buRoleId;
    }

    /**
     * @return the userRoleId
     */
    public Long getUserRoleId()
    {
        return userRoleId;
    }

    /**
     * @param userRoleId the userRoleId to set
     */
    public void setUserRoleId(Long userRoleId)
    {
        this.userRoleId = userRoleId;
    }

    /**
     * @return the drawers
     */
    public ArrayList<TLDrawer> getDrawers()
    {
        return drawers;
    }

    /**
     * @param drawers the drawers to set
     */
    public void setDrawers(ArrayList<TLDrawer> drawers)
    {
        this.drawers = drawers;
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
}
