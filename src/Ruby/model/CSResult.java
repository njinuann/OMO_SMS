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
public class CSResult implements Serializable
{
    private Integer code = -1;
    private String msisdn;
    private String message;
    private Integer score;
    private String grade;
    private BigDecimal limit = BigDecimal.ZERO;
    private Integer reason;

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
}
