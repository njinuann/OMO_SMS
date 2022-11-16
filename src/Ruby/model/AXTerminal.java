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
public class AXTerminal implements Serializable, Comparable<AXTerminal>
{
    private String channel;
    private String terminalId;
    private String operator;
    private String scheme;
    private String location;
    private String status;
    private String buCode;
    private String sysUser;
    private Date sysDate;
    private HashMap<String, CNAccount> accounts = new HashMap<>();

    /**
     * @return the terminalId
     */
    public String getTerminalId()
    {
        return terminalId;
    }

    /**
     * @param terminalId the terminalId to set
     */
    public void setTerminalId(String terminalId)
    {
        this.terminalId = terminalId;
    }

    /**
     * @return the operator
     */
    public String getOperator()
    {
        return operator;
    }

    /**
     * @param operator the operator to set
     */
    public void setOperator(String operator)
    {
        this.operator = operator;
    }

    /**
     * @return the location
     */
    public String getLocation()
    {
        return location;
    }

    /**
     * @param location the location to set
     */
    public void setLocation(String location)
    {
        this.location = location;
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
     * @return the buCode
     */
    public String getBuCode()
    {
        return buCode;
    }

    /**
     * @param buCode the buCode to set
     */
    public void setBuCode(String buCode)
    {
        this.buCode = buCode;
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

    public CNAccount getAccount(String currency)
    {
        return getAccounts().getOrDefault(currency, getAccounts().getOrDefault("ALL", new CNAccount()));
    }

    /**
     * @return the accounts
     */
    public HashMap<String, CNAccount> getAccounts()
    {
        return accounts;
    }

    /**
     * @param accounts the accounts to set
     */
    public void setAccounts(HashMap<String, CNAccount> accounts)
    {
        this.accounts = accounts;
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

    @Override
    public String toString()
    {
        return getTerminalId() + "~" + getLocation();
    }

    @Override
    public int compareTo(AXTerminal o)
    {
        return getTerminalId().compareTo(o.getTerminalId());
    }
}
