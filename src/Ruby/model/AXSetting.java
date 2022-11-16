/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Ruby.model;

import java.util.Date;

/**
 *
 * @author Pecherk
 */
public class AXSetting implements Comparable<AXSetting>
{
    private Long recId;
    private String code;
    private String value;
    private String channel;
    private Long channelId;
    private String description;
    private String sysUser;
    private boolean encrypted = false;
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
     * @return the value
     */
    public String getValue()
    {
        return value;
    }

    /**
     * @param value the value to set
     */
    public void setValue(String value)
    {
        this.value = value;
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
     * @return the user
     */
    public String getSysUser()
    {
        return sysUser;
    }

    /**
     * @param sysUser the user to set
     */
    public void setSysUser(String sysUser)
    {
        this.sysUser = sysUser;
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
     * @return the encrypted
     */
    public boolean isEncrypted()
    {
        return encrypted;
    }

    /**
     * @param encrypted the encrypted to set
     */
    public void setEncrypted(boolean encrypted)
    {
        this.encrypted = encrypted;
    }

    @Override
    public String toString()
    {
        return getCode() + "~" + getDescription();
    }

    @Override
    public int compareTo(AXSetting o)
    {
        return String.CASE_INSENSITIVE_ORDER.compare(getCode(), o.getCode());
    }

    /**
     * @return the channelId
     */
    public Long getChannelId()
    {
        return channelId;
    }

    /**
     * @param channelId the channelId to set
     */
    public void setChannelId(Long channelId)
    {
        this.channelId = channelId;
    }

    /**
     * @return the recId
     */
    public Long getRecId()
    {
        return recId;
    }

    /**
     * @param recId the recId to set
     */
    public void setRecId(Long recId)
    {
        this.recId = recId;
    }
}
