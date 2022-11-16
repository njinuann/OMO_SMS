/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Ruby.model;

import java.math.BigDecimal;

/**
 *
 * @author Pecherk
 */
public class URLimit
{
    private Long roleId;
    private String role;
    private String currency;
    private BigDecimal creditLimit;
    private BigDecimal debitLimit;

    /**
     * @return the roleId
     */
    public Long getRoleId()
    {
        return roleId;
    }

    /**
     * @param roleId the roleId to set
     */
    public void setRoleId(Long roleId)
    {
        this.roleId = roleId;
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
     * @return the creditLimit
     */
    public BigDecimal getCreditLimit()
    {
        return creditLimit;
    }

    /**
     * @param creditLimit the creditLimit to set
     */
    public void setCreditLimit(BigDecimal creditLimit)
    {
        this.creditLimit = creditLimit;
    }

    /**
     * @return the debitLimit
     */
    public BigDecimal getDebitLimit()
    {
        return debitLimit;
    }

    /**
     * @param debitLimit the debitLimit to set
     */
    public void setDebitLimit(BigDecimal debitLimit)
    {
        this.debitLimit = debitLimit;
    }
}
