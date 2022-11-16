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
public class CMChannel implements Serializable
{
    private Long custChannelId;
    private String status;

    /**
     * @return the custChannelId
     */
    public Long getCustChannelId()
    {
        return custChannelId;
    }

    /**
     * @param custChannelId the custChannelId to set
     */
    public void setCustChannelId(Long custChannelId)
    {
        this.custChannelId = custChannelId;
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
}
