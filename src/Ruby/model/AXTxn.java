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
public class AXTxn
{
    private Long recId;
    private String txnRef;
    private Date txnDate;
    private Long channelId;
    private String channel;
    private String client;
    private String txnCode;
    private String txnType;
    private Long buId;
    private String acquirer;
    private String terminal;
    private String advice;
    private String onus;
    private String accessCd;
    private String account;
    private String contra;
    private String currency;
    private BigDecimal amount;
    private String description;
    private String detail;
    private String chargeLedger;
    private BigDecimal charge;
    private Long txnId;
    private Long chgId;
    private BigDecimal balance;
    private String xapiCode;
    private String result;
    private String respCode;
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
     * @return the txnRef
     */
    public String getTxnRef()
    {
        return txnRef;
    }

    /**
     * @param txnRef the txnRef to set
     */
    public void setTxnRef(String txnRef)
    {
        this.txnRef = txnRef;
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
     * @return the client
     */
    public String getClient()
    {
        return client;
    }

    /**
     * @param client the client to set
     */
    public void setClient(String client)
    {
        this.client = client;
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
     * @return the buId
     */
    public Long getBuId()
    {
        return buId;
    }

    /**
     * @param buId the buId to set
     */
    public void setBuId(Long buId)
    {
        this.buId = buId;
    }

    /**
     * @return the acquirer
     */
    public String getAcquirer()
    {
        return acquirer;
    }

    /**
     * @param acquirer the acquirer to set
     */
    public void setAcquirer(String acquirer)
    {
        this.acquirer = acquirer;
    }

    /**
     * @return the terminal
     */
    public String getTerminal()
    {
        return terminal;
    }

    /**
     * @param terminal the terminal to set
     */
    public void setTerminal(String terminal)
    {
        this.terminal = terminal;
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
     * @return the detail
     */
    public String getDetail()
    {
        return detail;
    }

    /**
     * @param detail the detail to set
     */
    public void setDetail(String detail)
    {
        this.detail = detail;
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
     * @param chqId the chgId to set
     */
    public void setChgId(Long chqId)
    {
        this.chgId = chqId;
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
     * @return the respCode
     */
    public String getRespCode()
    {
        return respCode;
    }

    /**
     * @param respCode the respCode to set
     */
    public void setRespCode(String respCode)
    {
        this.respCode = respCode;
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
     * @return the advice
     */
    public String getAdvice()
    {
        return advice;
    }

    /**
     * @param advice the advice to set
     */
    public void setAdvice(String advice)
    {
        this.advice = advice;
    }

    /**
     * @return the onus
     */
    public String getOnus()
    {
        return onus;
    }

    /**
     * @param onus the onus to set
     */
    public void setOnus(String onus)
    {
        this.onus = onus;
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
}
