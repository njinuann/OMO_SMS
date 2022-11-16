/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Ruby.model;

import java.io.Serializable;
import java.util.Date;

/**
 *
 * @author Pecherk
 */
public class TCScheme implements Serializable
{
    private String code;
    private String channel;
    private String description;
    private String sysUser;
    private String status;
    private Date sysDate = new Date();

    /**
     * @return the code
     */
    public String getCode()
    {
        return code;
    }

    /**
     * @param code the code to set
     */
    public void setCode(String code)
    {
        this.code = code;
    }

    /**
     * @return the channel
     */
    public String getChannel()
    {
        return channel;
    }

    /**
     * @param channel the channel to set
     */
    public void setChannel(String channel)
    {
        this.channel = channel;
    }

    /**
     * @return the description
     */
    public String getDescription()
    {
        return description;
    }

    /**
     * @param description the description to set
     */
    public void setDescription(String description)
    {
        this.description = description;
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
     * @param lastDate the sysDate to set
     */
    public void setSysDate(Date lastDate)
    {
        this.sysDate = lastDate;
    }

    @Override
    public String toString()
    {
        return getCode() + "~" + getDescription();
    }
}
