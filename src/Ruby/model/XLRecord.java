/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Ruby.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 *
 * @author Pecherk
 */
public class XLRecord implements Serializable
{
    private Long recId;
    private Date txnDate;
    private String sessionId;
    private String txnType;
    private String refTxt;
    private String account;
    private String contra;
    private String currency;
    private BigDecimal amount;
    private String description;
    private String reversal;
    private String jrnlId;
    private String result;

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
     * @return the txnDate
     */
    public Date getTxnDate()
    {
        return txnDate;
    }

    /**
     * @param txnDate the txnDate to set
     */
    public void setTxnDate(Date txnDate)
    {
        this.txnDate = txnDate;
    }

    /**
     * @return the sessionId
     */
    public String getSessionId()
    {
        return sessionId;
    }

    /**
     * @param sessionId the sessionId to set
     */
    public void setSessionId(String sessionId)
    {
        this.sessionId = sessionId;
    }

    /**
     * @return the txnType
     */
    public String getTxnType()
    {
        return txnType;
    }

    /**
     * @param txnType the txnType to set
     */
    public void setTxnType(String txnType)
    {
        this.txnType = txnType;
    }

    /**
     * @return the refTxt
     */
    public String getRefTxt()
    {
        return refTxt;
    }

    /**
     * @param refTxt the refTxt to set
     */
    public void setRefTxt(String refTxt)
    {
        this.refTxt = refTxt;
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
     * @return the contra
     */
    public String getContra()
    {
        return contra;
    }

    /**
     * @param contra the contra to set
     */
    public void setContra(String contra)
    {
        this.contra = contra;
    }

    /**
     * @return the currency
     */
    public String getCurrency()
    {
        return currency;
    }

    /**
     * @param currency the currency to set
     */
    public void setCurrency(String currency)
    {
        this.currency = currency;
    }

    /**
     * @return the amount
     */
    public BigDecimal getAmount()
    {
        return amount;
    }

    /**
     * @param amount the amount to set
     */
    public void setAmount(BigDecimal amount)
    {
        this.amount = amount;
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
     * @return the reversal
     */
    public String getReversal()
    {
        return reversal;
    }

    /**
     * @param reversal the reversal to set
     */
    public void setReversal(String reversal)
    {
        this.reversal = reversal;
    }

    /**
     * @return the jrnlId
     */
    public String getJrnlId()
    {
        return jrnlId;
    }

    /**
     * @param jrnlId the jrnlId to set
     */
    public void setJrnlId(String jrnlId)
    {
        this.jrnlId = jrnlId;
    }

    /**
     * @return the result
     */
    public String getResult()
    {
        return result;
    }

    /**
     * @param result the result to set
     */
    public void setResult(String result)
    {
        this.result = result;
    }
}
