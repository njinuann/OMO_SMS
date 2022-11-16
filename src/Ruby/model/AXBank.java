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
public class AXBank implements Comparable<AXBank>
{
    private String bin;
    private String name;
    private String transferLedger;
    private String loroLedger;
    private String suspenseLedger;
    private String sysUser;
    private String status;
    private Date sysDate = new Date();

    /**
     * @return the bin
     */
    public String getBin()
    {
        return bin;
    }

    /**
     * @param bin the bin to set
     */
    public void setBin(String bin)
    {
        this.bin = bin;
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
     * @return the interBankLedger
     */
    public String getTransferLedger()
    {
        return transferLedger;
    }

    /**
     * @param transferLedger the interBankLedger to set
     */
    public void setTransferLedger(String transferLedger)
    {
        this.transferLedger = transferLedger;
    }

    /**
     * @return the loroLedger
     */
    public String getLoroLedger()
    {
        return loroLedger;
    }

    /**
     * @param loroLedger the loroLedger to set
     */
    public void setLoroLedger(String loroLedger)
    {
        this.loroLedger = loroLedger;
    }

    /**
     * @return the suspenseLedger
     */
    public String getSuspenseLedger()
    {
        return suspenseLedger;
    }

    /**
     * @param suspenseLedger the suspenseLedger to set
     */
    public void setSuspenseLedger(String suspenseLedger)
    {
        this.suspenseLedger = suspenseLedger;
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

    @Override
    public String toString()
    {
        return getBin() + "~" + getName();
    }

    @Override
    public int compareTo(AXBank o)
    {
        return String.CASE_INSENSITIVE_ORDER.compare(getBin(), o.getBin());
    }
}
