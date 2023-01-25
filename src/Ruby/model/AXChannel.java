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
public class AXChannel implements Serializable
{
    private Long Id;
    private String code;
    private String name;
    private String debitContra;
    private String creditContra;
    private BigDecimal debitLimit;
    private BigDecimal creditLimit;
    private CNBranch branch = new CNBranch();
    private String status;

    /**
     * @return the Id
     */
    public Long getId()
    {
        return Id;
    }

    /**
     * @param Id the Id to set
     */
    public void setId(Long Id)
    {
        this.Id = Id;
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
     * @return the name
     */
    public String getName()
    {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name)
    {
        this.name = name;
    }

    /**
     * @return the debitContra
     */
    public String getDebitContra()
    {
        return debitContra;
    }

    /**
     * @param debitContra the debitContra to set
     */
    public void setDebitContra(String debitContra)
    {
        this.debitContra = debitContra;
    }

    /**
     * @return the creditContra
     */
    public String getCreditContra()
    {
        return creditContra;
    }

    /**
     * @param creditContra the creditContra to set
     */
    public void setCreditContra(String creditContra)
    {
        this.creditContra = creditContra;
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
     * @return the branch
     */
    public CNBranch getBranch()
    {
        return branch;
    }

    /**
     * @param branch the branch to set
     */
    public void setBranch(CNBranch branch)
    {
        this.branch = branch;
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
}
