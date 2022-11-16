/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Ruby.model;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.ArrayList;

/**
 *
 * @author Pecherk
 */
public class MXAlert implements Serializable, Cloneable
{
    private Long recId;
    private String alertCode;
    private int priority = 3;
    private String description;
    private String alertType;
    private String alertDays;
    private String runTime;
    private String frequency;
    private ArrayList<String> filters = new ArrayList<>();
    private String filterBy;
    private Date nextDate;
    private Date previousDate;
    private Date expiryDate;
    private String status;
    private String chargeCode;
    private HashMap<String, String> templates = new HashMap<>();

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
     * @return the frequency
     */
    public String getFrequency()
    {
        return frequency;
    }

    /**
     * @param frequency the frequency to set
     */
    public void setFrequency(String frequency)
    {
        this.frequency = frequency;
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
     * @return the alertType
     */
    public String getTaskType()
    {
        return getAlertType();
    }

    /**
     * @param alertType the alertType to set
     */
    public void setTaskType(String alertType)
    {
        this.setAlertType(alertType);
    }

    /**
     * @return the priority
     */
    public int getPriority()
    {
        return priority;
    }

    /**
     * @param priority the priority to set
     */
    public void setPriority(int priority)
    {
        this.priority = priority;
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
     * @return the alertType
     */
    public String getAlertType()
    {
        return alertType;
    }

    /**
     * @param alertType the alertType to set
     */
    public void setAlertType(String alertType)
    {
        this.alertType = alertType;
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
     * @return the templates
     */
    public HashMap<String, String> getTemplates()
    {
        return templates;
    }

    /**
     * @param templates the templates to set
     */
    public void setTemplates(HashMap<String, String> templates)
    {
        this.templates = templates;
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
     * @return the alertCode
     */
    public String getAlertCode()
    {
        return alertCode;
    }

    /**
     * @param alertCode the alertCode to set
     */
    public void setAlertCode(String alertCode)
    {
        this.alertCode = alertCode;
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
     * @return the alertDays
     */
    public String getAlertDays()
    {
        return alertDays;
    }

    /**
     * @param alertDays the alertDays to set
     */
    public void setAlertDays(String alertDays)
    {
        this.alertDays = alertDays;
    }

    /**
     * @return the chargeCode
     */
    public String getChargeCode()
    {
        return chargeCode;
    }

    /**
     * @param chargeCode the chargeCode to set
     */
    public void setChargeCode(String chargeCode)
    {
        this.chargeCode = chargeCode;
    }

    @Override
    public String toString()
    {
        return getAlertCode() + "~" + getDescription();
    }

    @Override
    public MXAlert clone() throws CloneNotSupportedException
    {
        MXAlert clone = (MXAlert) super.clone();
        clone.setFilters((ArrayList) getFilters().clone());
        clone.setTemplates((HashMap) getTemplates().clone());
        return clone;
    }
}
