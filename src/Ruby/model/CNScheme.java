/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Ruby.model;

import java.io.Serializable;

/**
 *
 * @author Pecherk
 */
public class CNScheme implements Serializable, Comparable<CNScheme>
{
    private Long schemeId;
    private Long channelId;
    private String schemeCode;
    private String name;
    private String status;

    /**
     * @return the schemeId
     */
    public Long getSchemeId()
    {
        return schemeId;
    }

    /**
     * @param schemeId the schemeId to set
     */
    public void setSchemeId(Long schemeId)
    {
        this.schemeId = schemeId;
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
     * @return the schemeCode
     */
    public String getSchemeCode()
    {
        return schemeCode;
    }

    /**
     * @param schemeCode the schemeCode to set
     */
    public void setSchemeCode(String schemeCode)
    {
        this.schemeCode = schemeCode;
    }

    /**
     * @return the name
     */
    public String getName()
    {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name)
    {
        this.name = name;
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

    @Override
    public int compareTo(CNScheme o)
    {
        return getSchemeId().compareTo(o.getSchemeId());
    }
}
