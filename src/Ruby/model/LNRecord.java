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
public class LNRecord
{
    private Long recId;
    private Long applId;
    private String reqId;
    private Date createDt;
    private String vmtNumber;
    private String msisdn;
    private String acctNo;
    private BigDecimal amount;
    private Long chrgId;
    private Integer code;
    private String message;
    private Integer score;
    private String grade;
    private BigDecimal limit;
    private Integer reason;
    private String repayDt;
    private BigDecimal paid;
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
     * @return the applId
     */
    public Long getApplId()
    {
        return applId;
    }

    /**
     * @param applId the applId to set
     */
    public void setApplId(Long applId)
    {
        this.applId = applId;
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
     * @return the chrgId
     */
    public Long getChrgId()
    {
        return chrgId;
    }

    /**
     * @param chrgId the chrgId to set
     */
    public void setChrgId(Long chrgId)
    {
        this.chrgId = chrgId;
    }

    /**
     * @return the code
     */
    public Integer getCode()
    {
        return code;
    }

    /**
     * @param code the code to set
     */
    public void setCode(Integer code)
    {
        this.code = code;
    }

    /**
     * @return the message
     */
    public String getMessage()
    {
        return message;
    }

    /**
     * @param message the message to set
     */
    public void setMessage(String message)
    {
        this.message = message;
    }

    /**
     * @return the score
     */
    public Integer getScore()
    {
        return score;
    }

    /**
     * @param score the score to set
     */
    public void setScore(Integer score)
    {
        this.score = score;
    }

    /**
     * @return the grade
     */
    public String getGrade()
    {
        return grade;
    }

    /**
     * @param grade the grade to set
     */
    public void setGrade(String grade)
    {
        this.grade = grade;
    }

    /**
     * @return the limit
     */
    public BigDecimal getLimit()
    {
        return limit;
    }

    /**
     * @param limit the limit to set
     */
    public void setLimit(BigDecimal limit)
    {
        this.limit = limit;
    }

    /**
     * @return the reason
     */
    public Integer getReason()
    {
        return reason;
    }

    /**
     * @param reason the reason to set
     */
    public void setReason(Integer reason)
    {
        this.reason = reason;
    }

    /**
     * @return the repayDt
     */
    public String getRepayDt()
    {
        return repayDt;
    }

    /**
     * @param repayDt the repayDt to set
     */
    public void setRepayDt(String repayDt)
    {
        this.repayDt = repayDt;
    }

    /**
     * @return the paid
     */
    public BigDecimal getPaid()
    {
        return paid;
    }

    /**
     * @param paid the paid to set
     */
    public void setPaid(BigDecimal paid)
    {
        this.paid = paid;
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
}
