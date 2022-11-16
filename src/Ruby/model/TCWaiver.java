/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Ruby.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 *
 * @author Pecherk
 */
public class TCWaiver implements Serializable, Comparable<TCWaiver>, Cloneable
{
    private Long waiverId;
    private Long productId;
    private String matchAccount;
    private BigDecimal thresholdValue;
    private BigDecimal waivedPercentage;
    private String waiverCondition = "ALL";

    /**
     * @return the productId
     */
    public Long getProductId()
    {
        return productId;
    }

    /**
     * @param productId the productId to set
     */
    public void setProductId(Long productId)
    {
        this.productId = productId;
    }

    /**
     * @return the waivedPercentage
     */
    public BigDecimal getWaivedPercentage()
    {
        return waivedPercentage;
    }

    /**
     * @param waivedPercentage the waivedPercentage to set
     */
    public void setWaivedPercentage(BigDecimal waivedPercentage)
    {
        this.waivedPercentage = waivedPercentage.setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * @return the waiveCondition
     */
    public String getWaiverCondition()
    {
        return waiverCondition;
    }

    /**
     * @param waiverCondition the waiverCondition to set
     */
    public void setWaiverCondition(String waiverCondition)
    {
        this.waiverCondition = waiverCondition;
    }

    /**
     * @return the thresholdValue
     */
    public BigDecimal getThresholdValue()
    {
        return thresholdValue;
    }

    /**
     * @param thresholdValue the thresholdValue to set
     */
    public void setThresholdValue(BigDecimal thresholdValue)
    {
        this.thresholdValue = thresholdValue.setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * @return the matchAccount
     */
    public String getMatchAccount()
    {
        return matchAccount;
    }

    /**
     * @param matchAccount the matchAccount to set
     */
    public void setMatchAccount(String matchAccount)
    {
        this.matchAccount = matchAccount;
    }

    /**
     * @return the waiverId
     */
    public Long getWaiverId()
    {
        return waiverId;
    }

    /**
     * @param waiverId the waiverId to set
     */
    public void setWaiverId(Long waiverId)
    {
        this.waiverId = waiverId;
    }

    @Override
    public int compareTo(TCWaiver o)
    {
        return getProductId().compareTo(o.getProductId());
    }

    @Override
    public String toString()
    {
        return getProductId() + "~[" + getWaivedPercentage().toPlainString() + "%]";
    }

    @Override
    public TCWaiver clone() throws CloneNotSupportedException
    {
        return (TCWaiver) super.clone();
    }
}
