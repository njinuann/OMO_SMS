/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Ruby.model;

import Ruby.acx.AXIgnore;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 *
 * @author Pecherk
 */
public class TCSplit implements Serializable, Comparable<TCSplit>
{
    private String reference;
    private String description;
    private BigDecimal amount = BigDecimal.ZERO;
    private String account;

    /**
     * @return the recId
     */
    public String getReference()
    {
        return reference;
    }

    /**
     * @param reference the recId to set
     */
    public void setReference(String reference)
    {
        this.reference = reference;
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

    @Override
    public int compareTo(TCSplit o)
    {
        return getReference().compareTo(o.getReference());
    }

    @Override
    @AXIgnore
    public String toString()
    {
        return getReference() + "~" + getDescription();
    }
}
