/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Ruby.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import Ruby.acx.AXIgnore;

/**
 *
 * @author Pecherk
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class MXMessage implements Serializable
{

    @XmlElement(name = "alertId")
    private String alertId = String.valueOf(System.currentTimeMillis());
    @XmlElement(name = "alertDate")
    private Date alertDate;
    @XmlElement(name = "alertTime")
    private Date alertTime;
    @XmlElement(name = "alertCode")
    private String alertCode;
    @XmlElement(name = "alertType")
    private String alertType;
    @XmlElement(name = "description")
    private String description;
    @XmlElement(name = "txnId")
    private String txnId;
    @XmlElement(name = "custId")
    private long custId;
    @XmlElement(name = "custName")
    private String custName;
    @XmlElement(name = "acctNo")
    private String acctNo;
    @XmlElement(name = "contra")
    private String contra;
    @XmlElement(name = "chargeAcct")
    private String chargeAcct;
    @XmlElement(name = "txnDate")
    private Date txnDate;
    @XmlElement(name = "currency")
    private String currency;
    @XmlElement(name = "txnAmt")
    private BigDecimal txnAmt = BigDecimal.ZERO;
    @XmlElement(name = "txnChg")
    private BigDecimal txnChg = BigDecimal.ZERO;
    @XmlElement(name = "chargeAmount")
    private BigDecimal chargeAmount = BigDecimal.ZERO;
    @XmlElement(name = "chargeId")
    private Long chargeId;
    @XmlElement(name = "txnDesc")
    private String txnDesc;
    @XmlElement(name = "schemeId")
    private long schemeId;
    @XmlElement(name = "accessCode")
    private String accessCode;
    @XmlElement(name = "clrdBal")
    private BigDecimal clrdBal = BigDecimal.ZERO;
    @XmlElement(name = "msisdn")
    private String msisdn;
    @XmlElement(name = "message")
    private String message;
    @XmlElement(name = "status")
    private String status;
    @XmlElement(name = "result")
    private String result;
    @XmlElement(name = "responseid")
    private String responseId;
    @XmlElement(name = "maturity")
    private Date maturityDate;
    @XmlElement(name = "startDate")
    private Date startDate;
    @XmlElement(name = "tenor")
    private String tenor;
    @XmlElement(name = "rate")
    private BigDecimal rate;
    @XmlElement(name = "prodId")
    private Long prodId;
    @XmlElement(name = "prodDesc")
    private String prodDesc;
    @XmlTransient
    private String password;
    @XmlTransient
    private LNDetail loanDetail = new LNDetail();
    @XmlElement(name = "originator")
    private String originator;
    @XmlElement(name = "guarantor")
    private String guarantor;

    
    /**
     * @return the alertId
     */
    public String getAlertId()
    {
        return alertId;
    }

    /**
     * @param alertId the alertId to set
     */
    public void setAlertId(String alertId)
    {
        this.alertId = alertId;
    }

    /**
     * @return the alertDate
     */
    public Date getAlertDate()
    {
        return alertDate;
    }

    /**
     * @param alertDate the alertDate to set
     */
    public void setAlertDate(Date alertDate)
    {
        this.alertDate = alertDate;
    }

    /**
     * @return the alertTime
     */
    public Date getAlertTime()
    {
        return alertTime;
    }

    /**
     * @param alertTime the alertTime to set
     */
    public void setAlertTime(Date alertTime)
    {
        this.alertTime = alertTime;
    }

    /**
     * @return the alertCode
     */
    public String getAlertCode()
    {
        return alertCode;
    }

    /**
     * @param alertCode the alertCode to set
     */
    public void setAlertCode(String alertCode)
    {
        this.alertCode = alertCode;
    }

    /**
     * @return the alertType
     */
    public String getAlertType()
    {
        return alertType;
    }

    /**
     * @param alertType the alertType to set
     */
    public void setAlertType(String alertType)
    {
        this.alertType = alertType;
    }

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
     * @return the custId
     */
    public long getCustId()
    {
        return custId;
    }

    /**
     * @param custId the custId to set
     */
    public void setCustId(long custId)
    {
        this.custId = custId;
    }

    /**
     * @return the custName
     */
    public String getCustName()
    {
        return custName;
    }

    /**
     * @param custName the custName to set
     */
    public void setCustName(String custName)
    {
        this.custName = custName;
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
     * @return the chargeAcct
     */
    public String getChargeAcct()
    {
        return chargeAcct;
    }

    /**
     * @param chargeAcct the chargeAcct to set
     */
    public void setChargeAcct(String chargeAcct)
    {
        this.chargeAcct = chargeAcct;
    }

    /**
     * @return the txnDate
     */
    public Date getTxnDate()
    {
        return txnDate == null ? new Date() : txnDate;
    }

    /**
     * @param txnDate the txnDate to set
     */
    public void setTxnDate(Date txnDate)
    {
        this.txnDate = txnDate;
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
     * @return the txnAmt
     */
    public BigDecimal getTxnAmt()
    {
        return txnAmt == null ? BigDecimal.ZERO : txnAmt;
    }

    /**
     * @param txnAmt the txnAmt to set
     */
    public void setTxnAmt(BigDecimal txnAmt)
    {
        this.txnAmt = txnAmt;
    }

    /**
     * @return the chargeAmount
     */
    public BigDecimal getChargeAmount()
    {
        return chargeAmount;
    }

    /**
     * @param chargeAmount the chargeAmount to set
     */
    public void setChargeAmount(BigDecimal chargeAmount)
    {
        this.chargeAmount = chargeAmount;
    }

    /**
     * @return the txnDesc
     */
    public String getTxnDesc()
    {
        return txnDesc;
    }

    /**
     * @param txnDesc the txnDesc to set
     */
    public void setTxnDesc(String txnDesc)
    {
        this.txnDesc = txnDesc;
    }

    /**
     * @return the msisdn
     */
    public String getMsisdn()
    {
        return msisdn;
    }

    /**
     * @param msisdn the msisdn to set
     */
    public final void setMsisdn(String msisdn)
    {
        this.msisdn = msisdn;
    }

    /**
     * @return the message
     */
    public String getMessage()
    {
        return message;
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
     * @return the responseId
     */
    public String getResponseId()
    {
        return responseId;
    }

    /**
     * @param responseId the responseId to set
     */
    public void setResponseId(String responseId)
    {
        this.responseId = responseId;
    }

    /**
     * @return the clrdBal
     */
    public BigDecimal getClrdBal()
    {
        return clrdBal;
    }

    /**
     * @param clrdBal the clrdBal to set
     */
    public void setClrdBal(BigDecimal clrdBal)
    {
        this.clrdBal = clrdBal;
    }

    /**
     * @return the txnChg
     */
    public BigDecimal getTxnChg()
    {
        return txnChg;
    }

    /**
     * @param txnChg the txnChg to set
     */
    public void setTxnChg(BigDecimal txnChg)
    {
        this.txnChg = txnChg;
    }

    /**
     * @return the loanDetail
     */
    public LNDetail getLoanDetail()
    {
        return loanDetail;
    }

    /**
     * @param loanDetail the loanDetail to set
     */
    public void setLoanDetail(LNDetail loanDetail)
    {
        this.loanDetail = loanDetail;
    }

    /**
     * @param message the message to set
     */
    public final void setMessage(String message)
    {
        this.message = message;
    }

    @Override
    @AXIgnore
    public String toString()
    {
        return getAlertId() + "~" + getMsisdn();
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
     * @return the chargeId
     */
    public Long getChargeId()
    {
        return chargeId;
    }

    /**
     * @param chargeId the chargeId to set
     */
    public void setChargeId(Long chargeId)
    {
        this.chargeId = chargeId;
    }

    /**
     * @return the schemeId
     */
    public long getSchemeId()
    {
        return schemeId;
    }

    /**
     * @param schemeId the schemeId to set
     */
    public void setSchemeId(long schemeId)
    {
        this.schemeId = schemeId;
    }

    /**
     * @return the password
     */
    public String getPassword()
    {
        return password;
    }

    /**
     * @param password the password to set
     */
    public void setPassword(String password)
    {
        this.password = password;
    }

    /**
     * @return the accessCode
     */
    public String getAccessCode()
    {
        return accessCode;
    }

    /**
     * @param accessCode the accessCode to set
     */
    public void setAccessCode(String accessCode)
    {
        this.accessCode = accessCode;
    }

    /**
     * @return the maturityDate
     */
    public Date getMaturityDate()
    {
        return maturityDate;
    }

    /**
     * @param maturityDate the maturityDate to set
     */
    public void setMaturityDate(Date maturityDate)
    {
        this.maturityDate = maturityDate;
    }

    /**
     * @return the startDate
     */
    public Date getStartDate()
    {
        return startDate;
    }

    /**
     * @param startDate the startDate to set
     */
    public void setStartDate(Date startDate)
    {
        this.startDate = startDate;
    }

    /**
     * @return the tenor
     */
    public String getTenor()
    {
        return tenor;
    }

    /**
     * @param tenor the tenor to set
     */
    public void setTenor(String tenor)
    {
        this.tenor = tenor;
    }

    /**
     * @return the rate
     */
    public BigDecimal getRate()
    {
        return rate;
    }

    /**
     * @param rate the rate to set
     */
    public void setRate(BigDecimal rate)
    {
        this.rate = rate;
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
     * @return the prodDesc
     */
    public String getProdDesc()
    {
        return prodDesc;
    }

    /**
     * @param prodDesc the prodDesc to set
     */
    public void setProdDesc(String prodDesc)
    {
        this.prodDesc = prodDesc;
    }

    /**
     * @return the originator
     */
    public String getOriginator()
    {
        return originator;
    }

    /**
     * @param originator the originator to set
     */
    public void setOriginator(String originator)
    {
        this.originator = originator;
    }

    /**
     * @return the guarantor
     */
    public String getGuarantor()
    {
        return guarantor;
    }

    /**
     * @param guarantor the guarantor to set
     */
    public void setGuarantor(String guarantor)
    {
        this.guarantor = guarantor;
    }

}
