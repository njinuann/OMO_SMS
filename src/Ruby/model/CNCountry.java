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
public class CNCountry
{
    private Long countryId = 0L;
    private String contryCode;
    private String isoCode;
    private String contryName;
    private String status;

    /**
     * @return the countryId
     */
    public Long getCountryId()
    {
        return countryId;
    }

    /**
     * @param countryId the countryId to set
     */
    public void setCountryId(Long countryId)
    {
        this.countryId = countryId;
    }

    /**
     * @return the contryCode
     */
    public String getContryCode()
    {
        return contryCode;
    }

    /**
     * @param contryCode the contryCode to set
     */
    public void setContryCode(String contryCode)
    {
        this.contryCode = contryCode;
    }

    /**
     * @return the contryName
     */
    public String getContryName()
    {
        return contryName;
    }

    /**
     * @param contryName the contryName to set
     */
    public void setContryName(String contryName)
    {
        this.contryName = contryName;
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
     * @return the isoCode
     */
    public String getIsoCode()
    {
        return isoCode;
    }

    /**
     * @param isoCode the isoCode to set
     */
    public void setIsoCode(String isoCode)
    {
        this.isoCode = isoCode;
    }
}
