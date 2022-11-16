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
public class AXPayment implements Serializable, Comparable<AXPayment>
{
    private Long recId;
    private Date createDt;
    private String channel;
    private String type;
    private String txnCd;
    private String description;
    private Integer codeField;
    private Integer detailField;
    private String code;
    private String account;
    private String sysUser;
    private Date sysDate;
    private String status;

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

    /**
     * @return the createDt
     */
    public Date getCreateDt()
    {
        return createDt;
    }

    /**
     * @param createDt the createDt to set
     */
    public void setCreateDt(Date createDt)
    {
        this.createDt = createDt;
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
     * @return the type
     */
    public String getType()
    {
        return type;
    }

    /**
     * @param type the type to set
     */
    public void setType(String type)
    {
        this.type = type;
    }

    /**
     * @return the txnCd
     */
    public String getTxnCd()
    {
        return txnCd;
    }

    /**
     * @param txnCd the txnCd to set
     */
    public void setTxnCd(String txnCd)
    {
        this.txnCd = txnCd;
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
     * @return the codeField
     */
    public Integer getCodeField()
    {
        return codeField;
    }

    /**
     * @param codeField the codeField to set
     */
    public void setCodeField(Integer codeField)
    {
        this.codeField = codeField;
    }

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
     * @return the account
     */
    public String getAccount()
    {
        return account;
    }

    /**
     * @param account the account to set
     */
    public void setAccount(String account)
    {
        this.account = account;
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
    public int compareTo(AXPayment o)
    {
        return getTxnCd().compareTo(o.getTxnCd());
    }

    @Override
    public String toString()
    {
        return getTxnCd() + "~" + getDescription();
    }

    /**
     * @return the detailField
     */
    public Integer getDetailField()
    {
        return detailField;
    }

    /**
     * @param detailField the detailField to set
     */
    public void setDetailField(Integer detailField)
    {
        this.detailField = detailField;
    }
}
