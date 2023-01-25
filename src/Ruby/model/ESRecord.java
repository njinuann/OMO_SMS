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
public class ESRecord
{
    private Long recId;
    private Date createDt;
    private String task;
    private String account;
    private Date startDt;
    private Date endDt;
    private String address;
    private BigDecimal charge;
    private Long chgId;
    private String status = "F";
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
     * @return the task
     */
    public String getTask()
    {
        return task;
    }

    /**
     * @param task the task to set
     */
    public void setTask(String task)
    {
        this.task = task;
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
     * @return the startDt
     */
    public Date getStartDt()
    {
        return startDt;
    }

    /**
     * @param startDt the startDt to set
     */
    public void setStartDt(Date startDt)
    {
        this.startDt = startDt;
    }

    /**
     * @return the endDt
     */
    public Date getEndDt()
    {
        return endDt;
    }

    /**
     * @param endDt the endDt to set
     */
    public void setEndDt(Date endDt)
    {
        this.endDt = endDt;
    }

    /**
     * @return the address
     */
    public String getAddress()
    {
        return address;
    }

    /**
     * @param address the address to set
     */
    public void setAddress(String address)
    {
        this.address = address;
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
