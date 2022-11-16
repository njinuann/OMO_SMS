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
public class AXActivity implements Serializable, Comparable<AXActivity>
{
    private String txnId;
    private String channel;
    private String identity;
    private String activity;
    private String name;

    /**
     * @return the txnId
     */
    public String getTxnId()
    {
        return txnId;
    }

    /**
     * @param txnId the txnId to set
     */
    public void setTxnId(String txnId)
    {
        this.txnId = txnId;
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
     * @return the identity
     */
    public String getIdentity()
    {
        return identity;
    }

    /**
     * @param identity the identity to set
     */
    public void setIdentity(String identity)
    {
        this.identity = identity;
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
     * @return the activity
     */
    public String getActivity()
    {
        return activity;
    }

    /**
     * @param activity the activity to set
     */
    public void setActivity(String activity)
    {
        this.activity = activity;
    }

    @Override
    public int compareTo(AXActivity o)
    {
        return getTxnId().compareTo(o.getTxnId());
    }
}
