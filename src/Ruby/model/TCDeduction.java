/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Ruby.model;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 *
 * @author Pecherk
 */
public class TCDeduction implements Serializable, Comparable<TCDeduction>, Cloneable
{
    private Long recId;
    private String basis;
    private String description;
    private String account;
    private String valueType;
    private BigDecimal value;

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
     * @return the basis
     */
    public String getBasis()
    {
        return basis;
    }

    /**
     * @param basis the basis to set
     */
    public void setBasis(String basis)
    {
        this.basis = basis;
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
     * @return the valueType
     */
    public String getValueType()
    {
        return valueType;
    }

    /**
     * @param valueType the valueType to set
     */
    public void setValueType(String valueType)
    {
        this.valueType = valueType;
    }

    /**
     * @return the value
     */
    public BigDecimal getValue()
    {
        return value;
    }

    /**
     * @param value the value to set
     */
    public void setValue(BigDecimal value)
    {
        this.value = value;
    }

    @Override
    public int compareTo(TCDeduction o)
    {
        return getBasis().compareTo(o.getBasis());
    }

    @Override
    public String toString()
    {
        return getRecId() + "~" + getDescription();
    }

    @Override
    public TCDeduction clone() throws CloneNotSupportedException
    {
        return (TCDeduction) super.clone();
    }
}
