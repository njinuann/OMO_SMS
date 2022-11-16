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
public class CAEvent implements Serializable
{
    private Long custId;
    private Long acctId;
    private String shortName;
    private String custNo;
    private String accessCd;
    private String acctNo;
    private Long prodId;
    private String auditAction;
    private Long custChannelAcctId;
    private Long channelUserCustId;
    private String custCat;
    private String role;

    /**
     * @return the custChannelAcctId
     */
    public Long getCustChannelAcctId()
    {
        return custChannelAcctId;
    }

    /**
     * @param custChannelAcctId the custChannelAcctId to set
     */
    public void setCustChannelAcctId(Long custChannelAcctId)
    {
        this.custChannelAcctId = custChannelAcctId;
    }

    /**
     * @return the custId
     */
    public Long getCustId()
    {
        return custId;
    }

    /**
     * @param custId the custId to set
     */
    public void setCustId(Long custId)
    {
        this.custId = custId;
    }

    /**
     * @return the acctId
     */
    public Long getAcctId()
    {
        return acctId;
    }

    /**
     * @param acctId the acctId to set
     */
    public void setAcctId(Long acctId)
    {
        this.acctId = acctId;
    }

    /**
     * @return the shortName
     */
    public String getShortName()
    {
        return shortName;
    }

    /**
     * @param shortName the shortName to set
     */
    public void setShortName(String shortName)
    {
        this.shortName = shortName;
    }

    /**
     * @return the auditAction
     */
    public String getAuditAction()
    {
        return auditAction;
    }

    /**
     * @param auditAction the auditAction to set
     */
    public void setAuditAction(String auditAction)
    {
        this.auditAction = auditAction;
    }

    /**
     * @return the custNo
     */
    public String getCustNo()
    {
        return custNo;
    }

    /**
     * @param custNo the custNo to set
     */
    public void setCustNo(String custNo)
    {
        this.custNo = custNo;
    }

    /**
     * @return the accessCd
     */
    public String getAccessCd()
    {
        return accessCd;
    }

    /**
     * @param accessCd the accessCd to set
     */
    public void setAccessCd(String accessCd)
    {
        this.accessCd = accessCd;
    }

    /**
     * @return the acctNo
     */
    public String getAcctNo()
    {
        return acctNo;
    }

    /**
     * @param acctNo the acctNo to set
     */
    public void setAcctNo(String acctNo)
    {
        this.acctNo = acctNo;
    }

    /**
     * @return the prodId
     */
    public Long getProdId()
    {
        return prodId;
    }

    /**
     * @param prodId the prodId to set
     */
    public void setProdId(Long prodId)
    {
        this.prodId = prodId;
    }

    /**
     * @return the channelUserCustId
     */
    public Long getChannelUserCustId()
    {
        return channelUserCustId;
    }

    /**
     * @param channelUserCustId the channelUserCustId to set
     */
    public void setChannelUserCustId(Long channelUserCustId)
    {
        this.channelUserCustId = channelUserCustId;
    }

    /**
     * @return the custCat
     */
    public String getCustCat()
    {
        return custCat;
    }

    /**
     * @param custCat the custCat to set
     */
    public void setCustCat(String custCat)
    {
        this.custCat = custCat;
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
}
