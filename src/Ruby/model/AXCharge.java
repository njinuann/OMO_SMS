/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Ruby.model;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;

/**
 *
 * @author Pecherk
 */
public class AXCharge implements Serializable, Comparable<AXCharge>, Cloneable
{
    private Long recId;
    private String code;
    private String scheme;
    private String description;
    private String chargeAccount;
    private String chargeLedger;
    private String sysUser;
    private String status;
    private Date lastDate = new Date();
    private HashMap<String, TCValue> values = new HashMap<>();
    private HashMap<Long, TCWaiver> waivers = new HashMap<>();

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
     * @return the chargeAccount
     */
    public String getChargeAccount()
    {
        return chargeAccount;
    }

    /**
     * @param chargeAccount the chargeAccount to set
     */
    public void setChargeAccount(String chargeAccount)
    {
        this.chargeAccount = chargeAccount;
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
     * @return the lastDate
     */
    public Date getSysDate()
    {
        return lastDate;
    }

    /**
     * @param lastDate the lastDate to set
     */
    public void setSysDate(Date lastDate)
    {
        this.lastDate = lastDate;
    }

    /**
     * @return the waivers
     */
    public HashMap<Long, TCWaiver> getWaivers()
    {
        return waivers;
    }

    /**
     * @param waivers the waivers to set
     */
    public void setWaivers(HashMap<Long, TCWaiver> waivers)
    {
        this.waivers = waivers;
    }

    /**
     * @return the values
     */
    public HashMap<String, TCValue> getValues()
    {
        return values;
    }

    /**
     * @param values the values to set
     */
    public void setValues(HashMap<String, TCValue> values)
    {
        this.values = values;
    }

    /**
     * @return the chargeLedger
     */
    public String getChargeLedger()
    {
        return chargeLedger;
    }

    /**
     * @param chargeLedger the chargeLedger to set
     */
    public void setChargeLedger(String chargeLedger)
    {
        this.chargeLedger = chargeLedger;
    }

    /**
     * @return the scheme
     */
    public String getScheme()
    {
        return scheme;
    }

    /**
     * @param scheme the scheme to set
     */
    public void setScheme(String scheme)
    {
        this.scheme = scheme;
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

    @Override
    public int compareTo(AXCharge o)
    {
        return getCode().compareTo(o.getCode());
    }

    @Override
    public String toString()
    {
        return getCode() + "~" + getDescription();
    }

    @Override
    public AXCharge clone() throws CloneNotSupportedException
    {
        AXCharge clone = (AXCharge) super.clone();
        clone.setValues((HashMap) getValues().clone());
        clone.setWaivers((HashMap) getWaivers().clone());
        return clone;
    }
}
