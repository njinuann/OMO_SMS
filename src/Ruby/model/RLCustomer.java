/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Ruby.model;

import java.io.Serializable;

/**
 *
 * @author Pecherk
 */
public class RLCustomer implements Serializable
{
    private Long mainCustId;
    private String mainCustNo;
    private Long relCustId;
    private String relCustNo;    
    private String relCustName;
    private Long xshipTypeIId;
    private String status;

    /**
     * @return the mainCustId
     */
    public Long getMainCustId()
    {
        return mainCustId;
    }

    /**
     * @param mainCustId the mainCustId to set
     */
    public void setMainCustId(Long mainCustId)
    {
        this.mainCustId = mainCustId;
    }

    /**
     * @return the mainCustNo
     */
    public String getMainCustNo()
    {
        return mainCustNo;
    }

    /**
     * @param mainCustNo the mainCustNo to set
     */
    public void setMainCustNo(String mainCustNo)
    {
        this.mainCustNo = mainCustNo;
    }

    /**
     * @return the relCustId
     */
    public Long getRelCustId()
    {
        return relCustId;
    }

    /**
     * @param relCustId the relCustId to set
     */
    public void setRelCustId(Long relCustId)
    {
        this.relCustId = relCustId;
    }

    /**
     * @return the relCustName
     */
    public String getRelCustName()
    {
        return relCustName;
    }

    /**
     * @param relCustName the relCustName to set
     */
    public void setRelCustName(String relCustName)
    {
        this.relCustName = relCustName;
    }

    /**
     * @return the xshipTypeIId
     */
    public Long getXshipTypeIId()
    {
        return xshipTypeIId;
    }

    /**
     * @param xshipTypeIId the xshipTypeIId to set
     */
    public void setXshipTypeIId(Long xshipTypeIId)
    {
        this.xshipTypeIId = xshipTypeIId;
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
     * @return the relCustNo
     */
    public String getRelCustNo()
    {
        return relCustNo;
    }

    /**
     * @param relCustNo the relCustNo to set
     */
    public void setRelCustNo(String relCustNo)
    {
        this.relCustNo = relCustNo;
    }
}
