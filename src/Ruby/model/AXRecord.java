/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Ruby.model;

import java.math.BigDecimal;
import java.util.Date;

/**
 *
 * @author Pecherk
 */
public class AXRecord
{
    private Long recId;
    private Date txnDate;
    private String reqId;
    private Long channelId;
    private String channel;
    private String application;
    private String operator;
    private String txnCode;
    private String tranType;
    private String bankCode;
    private String vmtNumber;
    private String msisdn;
    private String vmtAcct;
    private String groupId;
    private String shortCode;
    private Date timeStamp;
    private Long tranId;
    private String linkType;
    private Long appTranId;
    private String account;
    private String contra;
    private String currency;
    private BigDecimal amount;
    private String narration;
    private BigDecimal charge;
    private Date dueDate;
    private BigDecimal maxAmt;
    private String state;
    private String receipt;
    private String vmtRs;
    private BigDecimal balance;
    private String xapiCode;
    private String xapiMsg;
    private String result;
    private Long txnId;
    private Long chgId;
    private String response;
    private String exception;
    private String recSt;

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
     * @return the reqId
     */
    public String getReqId()
    {
        return reqId;
    }

    /**
     * @param reqId the reqId to set
     */
    public void setReqId(String reqId)
    {
        this.reqId = reqId;
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
     * @return the application
     */
    public String getApplication()
    {
        return application;
    }

    /**
     * @param application the application to set
     */
    public void setApplication(String application)
    {
        this.application = application;
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
     * @return the txnCode
     */
    public String getTxnCode()
    {
        return txnCode;
    }

    /**
     * @param txnCode the txnCode to set
     */
    public void setTxnCode(String txnCode)
    {
        this.txnCode = txnCode;
    }

    /**
     * @return the tranType
     */
    public String getTranType()
    {
        return tranType;
    }

    /**
     * @param tranType the tranType to set
     */
    public void setTranType(String tranType)
    {
        this.tranType = tranType;
    }

    /**
     * @return the bankCode
     */
    public String getBankCode()
    {
        return bankCode;
    }

    /**
     * @param bankCode the bankCode to set
     */
    public void setBankCode(String bankCode)
    {
        this.bankCode = bankCode;
    }

    /**
     * @return the vmtNumber
     */
    public String getVmtNumber()
    {
        return vmtNumber;
    }

    /**
     * @param vmtNumber the vmtNumber to set
     */
    public void setVmtNumber(String vmtNumber)
    {
        this.vmtNumber = vmtNumber;
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
    public void setMsisdn(String msisdn)
    {
        this.msisdn = msisdn;
    }

    /**
     * @return the vmtAcct
     */
    public String getVmtAcct()
    {
        return vmtAcct;
    }

    /**
     * @param vmtAcct the vmtAcct to set
     */
    public void setVmtAcct(String vmtAcct)
    {
        this.vmtAcct = vmtAcct;
    }

    /**
     * @return the groupId
     */
    public String getGroupId()
    {
        return groupId;
    }

    /**
     * @param groupId the groupId to set
     */
    public void setGroupId(String groupId)
    {
        this.groupId = groupId;
    }

    /**
     * @return the shortCode
     */
    public String getShortCode()
    {
        return shortCode;
    }

    /**
     * @param shortCode the shortCode to set
     */
    public void setShortCode(String shortCode)
    {
        this.shortCode = shortCode;
    }

    /**
     * @return the timeStamp
     */
    public Date getTimeStamp()
    {
        return timeStamp;
    }

    /**
     * @param timeStamp the timeStamp to set
     */
    public void setTimeStamp(Date timeStamp)
    {
        this.timeStamp = timeStamp;
    }

    /**
     * @return the tranId
     */
    public Long getTranId()
    {
        return tranId;
    }

    /**
     * @param tranId the tranId to set
     */
    public void setTranId(Long tranId)
    {
        this.tranId = tranId;
    }

    /**
     * @return the linkType
     */
    public String getLinkType()
    {
        return linkType;
    }

    /**
     * @param linkType the linkType to set
     */
    public void setLinkType(String linkType)
    {
        this.linkType = linkType;
    }

    /**
     * @return the appTranId
     */
    public Long getAppTranId()
    {
        return appTranId;
    }

    /**
     * @param appTranId the appTranId to set
     */
    public void setAppTranId(Long appTranId)
    {
        this.appTranId = appTranId;
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
     * @return the narration
     */
    public String getNarration()
    {
        return narration;
    }

    /**
     * @param narration the narration to set
     */
    public void setNarration(String narration)
    {
        this.narration = narration;
    }

    /**
     * @return the charge
     */
    public BigDecimal getCharge()
    {
        return charge;
    }

    /**
     * @param charge the charge to set
     */
    public void setCharge(BigDecimal charge)
    {
        this.charge = charge;
    }

    /**
     * @return the dueDate
     */
    public Date getDueDate()
    {
        return dueDate;
    }

    /**
     * @param dueDate the dueDate to set
     */
    public void setDueDate(Date dueDate)
    {
        this.dueDate = dueDate;
    }

    /**
     * @return the maxAmt
     */
    public BigDecimal getMaxAmt()
    {
        return maxAmt;
    }

    /**
     * @param maxAmt the maxAmt to set
     */
    public void setMaxAmt(BigDecimal maxAmt)
    {
        this.maxAmt = maxAmt;
    }

    /**
     * @return the state
     */
    public String getState()
    {
        return state;
    }

    /**
     * @param state the state to set
     */
    public void setState(String state)
    {
        this.state = state;
    }

    /**
     * @return the receipt
     */
    public String getReceipt()
    {
        return receipt;
    }

    /**
     * @param receipt the receipt to set
     */
    public void setReceipt(String receipt)
    {
        this.receipt = receipt;
    }

    /**
     * @return the balance
     */
    public BigDecimal getBalance()
    {
        return balance;
    }

    /**
     * @param balance the balance to set
     */
    public void setBalance(BigDecimal balance)
    {
        this.balance = balance;
    }

    /**
     * @return the xapiCode
     */
    public String getXapiCode()
    {
        return xapiCode;
    }

    /**
     * @param xapiCode the xapiCode to set
     */
    public void setXapiCode(String xapiCode)
    {
        this.xapiCode = xapiCode;
    }

    /**
     * @return the xapiMsg
     */
    public String getXapiMsg()
    {
        return xapiMsg;
    }

    /**
     * @param xapiMsg the xapiMsg to set
     */
    public void setXapiMsg(String xapiMsg)
    {
        this.xapiMsg = xapiMsg;
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
     * @return the txnId
     */
    public Long getTxnId()
    {
        return txnId;
    }

    /**
     * @param txnId the txnId to set
     */
    public void setTxnId(Long txnId)
    {
        this.txnId = txnId;
    }

    /**
     * @return the chgId
     */
    public Long getChgId()
    {
        return chgId;
    }

    /**
     * @param chgId the chgId to set
     */
    public void setChgId(Long chgId)
    {
        this.chgId = chgId;
    }

    /**
     * @return the response
     */
    public String getResponse()
    {
        return response;
    }

    /**
     * @param response the response to set
     */
    public void setResponse(String response)
    {
        this.response = response;
    }

    /**
     * @return the exception
     */
    public String getException()
    {
        return exception;
    }

    /**
     * @param exception the exception to set
     */
    public void setException(String exception)
    {
        this.exception = exception;
    }

    /**
     * @return the recSt
     */
    public String getRecSt()
    {
        return recSt;
    }

    /**
     * @param recSt the recSt to set
     */
    public void setRecSt(String recSt)
    {
        this.recSt = recSt;
    }

    /**
     * @return the vmtRs
     */
    public String getVmtRs()
    {
        return vmtRs;
    }

    /**
     * @param vmtRs the vmtRs to set
     */
    public void setVmtRs(String vmtRs)
    {
        this.vmtRs = vmtRs;
    }


}
