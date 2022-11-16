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
public class GPSplit implements Serializable
{
    private Date txnDate;
    private String channel;
    private String currency;
    private BigDecimal amount;
    private String description;
    private String debitAccount;
    private String creditAccount;
    private String reversal;

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
     * @return the debitAccount
     */
    public String getDebitAccount()
    {
        return debitAccount;
    }

    /**
     * @param debitAccount the debitAccount to set
     */
    public void setDebitAccount(String debitAccount)
    {
        this.debitAccount = debitAccount;
    }

    /**
     * @return the creditAccount
     */
    public String getCreditAccount()
    {
        return creditAccount;
    }

    /**
     * @param creditAccount the creditAccount to set
     */
    public void setCreditAccount(String creditAccount)
    {
        this.creditAccount = creditAccount;
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
}
