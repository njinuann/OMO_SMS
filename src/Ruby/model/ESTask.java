/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Ruby.model;

import Ruby.acx.AXIgnore;
import java.io.Serializable;
import java.util.Date;
import java.util.ArrayList;

/**
 *
 * @author Pecherk
 */
public class ESTask implements Serializable, Cloneable
{
    private Long recId;
    private String code;
    private String description;
    private String document;
    private String range;
    private String runTime;
    private String cycle;
    private ArrayList<String> filters = new ArrayList<>();
    private String filterBy;
    private Date nextDate;
    private Date previousDate;
    private Date expiryDate;
    private String status;
    private String charge;

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
     * @return the cycle
     */
    public String getCycle()
    {
        return cycle;
    }

    /**
     * @param cycle the cycle to set
     */
    public void setCycle(String cycle)
    {
        this.cycle = cycle;
    }

    /**
     * @return the expiryDate
     */
    public Date getExpiryDate()
    {
        return expiryDate;
    }

    /**
     * @param expiryDate the expiryDate to set
     */
    public void setExpiryDate(Date expiryDate)
    {
        this.expiryDate = expiryDate;
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
     * @return the document
     */
    public String getDocument()
    {
        return document;
    }

    /**
     * @param document the document to set
     */
    public void setDocument(String document)
    {
        this.document = document;
    }

    /**
     * @return the filters
     */
    public ArrayList<String> getFilters()
    {
        return filters;
    }

    /**
     * @param filters the filters to set
     */
    public void setFilters(ArrayList<String> filters)
    {
        this.filters = filters;
    }

    /**
     * @return the filterBy
     */
    public String getFilterBy()
    {
        return filterBy;
    }

    /**
     * @param filterBy the filterBy to set
     */
    public void setFilterBy(String filterBy)
    {
        this.filterBy = filterBy;
    }

    /**
     * @return the nextDate
     */
    public Date getNextDate()
    {
        return nextDate;
    }

    /**
     * @param nextDate the nextDate to set
     */
    public void setNextDate(Date nextDate)
    {
        this.nextDate = nextDate;
    }

    /**
     * @return the previousDate
     */
    public Date getPreviousDate()
    {
        return previousDate;
    }

    /**
     * @param previousDate the previousDate to set
     */
    public void setPreviousDate(Date previousDate)
    {
        this.previousDate = previousDate;
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
     * @return the runTime
     */
    public String getRunTime()
    {
        return runTime;
    }

    /**
     * @param runTime the runTime to set
     */
    public void setRunTime(String runTime)
    {
        this.runTime = runTime;
    }

    /**
     * @return the charge
     */
    public String getCharge()
    {
        return charge;
    }

    /**
     * @param charge the charge to set
     */
    public void setCharge(String charge)
    {
        this.charge = charge;
    }

    /**
     * @return the range
     */
    public String getRange()
    {
        return range;
    }

    /**
     * @param range the range to set
     */
    public void setRange(String range)
    {
        this.range = range;
    }

    @Override
    @AXIgnore
    public String toString()
    {
        return getCode() + "~" + getDescription();
    }

    @Override
    public ESTask clone() throws CloneNotSupportedException
    {
        ESTask clone = (ESTask) super.clone();
        clone.setFilters((ArrayList) getFilters().clone());
        return clone;
    }
}
