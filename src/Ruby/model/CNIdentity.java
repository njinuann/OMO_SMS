/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Ruby.model;

/**
 *
 * @author Pecherk
 */
public class CNIdentity
{
    private Long custId;
    private Long custIdentId;
    private Long identityId;
    private String identityNumber;
    private String description;
    private String status;

    /**
     * @return the custId
     */
    public Long getCustId()
    {
        return custId;
    }

    /**
     * @param custId the custId to set
     */
    public void setCustId(Long custId)
    {
        this.custId = custId;
    }

    /**
     * @return the custIdentId
     */
    public Long getCustIdentId()
    {
        return custIdentId;
    }

    /**
     * @param custIdentId the custIdentId to set
     */
    public void setCustIdentId(Long custIdentId)
    {
        this.custIdentId = custIdentId;
    }

    /**
     * @return the identityId
     */
    public Long getIdentityId()
    {
        return identityId;
    }

    /**
     * @param identityId the identityId to set
     */
    public void setIdentityId(Long identityId)
    {
        this.identityId = identityId;
    }

    /**
     * @return the identityNumber
     */
    public String getIdentityNumber()
    {
        return identityNumber;
    }

    /**
     * @param identityNumber the identityNumber to set
     */
    public void setIdentityNumber(String identityNumber)
    {
        this.identityNumber = identityNumber;
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
