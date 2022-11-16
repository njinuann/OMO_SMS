/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Ruby.model;

import Ruby.APController;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;

/**
 *
 * @author Pecherk
 */
public class TCValue implements Serializable, Comparable<TCValue>, Cloneable
{
    private Long valueId;
    private String chargeType = "Constant";
    private BigDecimal minAmount = BigDecimal.ZERO;
    private BigDecimal maxAmount = BigDecimal.ZERO;
    private BigDecimal chargeValue = BigDecimal.ZERO;
    private HashMap<BigDecimal, AXTier> tiers = new HashMap<>();
    private HashMap<Long, TCDeduction> deductions = new HashMap<>();
    private String currency = APController.getCurrency().getCurrencyCode();

    /**
     * @return the valueId
     */
    public Long getValueId()
    {
        return valueId;
    }

    /**
     * @param valueId the valueId to set
     */
    public void setValueId(Long valueId)
    {
        this.valueId = valueId;
    }

    /**
     * @return the chargeType
     */
    public String getChargeType()
    {
        return chargeType;
    }

    /**
     * @param chargeType the chargeType to set
     */
    public void setChargeType(String chargeType)
    {
        this.chargeType = chargeType;
    }

    /**
     * @return the chargeValue
     */
    public BigDecimal getValue()
    {
        return chargeValue;
    }

    /**
     * @param chargeValue the chargeValue to set
     */
    public void setValue(BigDecimal chargeValue)
    {
        this.chargeValue = chargeValue.setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * @return the minAmount
     */
    public BigDecimal getMinAmount()
    {
        return minAmount;
    }

    /**
     * @param minAmount the minAmount to set
     */
    public void setMinAmount(BigDecimal minAmount)
    {
        this.minAmount = minAmount.setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * @return the maxAmount
     */
    public BigDecimal getMaxAmount()
    {
        return maxAmount;
    }

    /**
     * @param maxAmount the maxAmount to set
     */
    public void setMaxAmount(BigDecimal maxAmount)
    {
        this.maxAmount = maxAmount.setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * @return the tiers
     */
    public HashMap<BigDecimal, AXTier> getTiers()
    {
        return tiers;
    }

    /**
     * @param tiers the tiers to set
     */
    public void setTiers(HashMap<BigDecimal, AXTier> tiers)
    {
        this.tiers = tiers;
    }

    /**
     * @return the currency
     */
    public String getCurrency()
    {
        return currency;
    }

    /**
     * @param currency the currency to set
     */
    public void setCurrency(String currency)
    {
        this.currency = currency;
    }

    /**
     * @return the deductions
     */
    public HashMap<Long, TCDeduction> getDeductions()
    {
        return deductions;
    }

    /**
     * @param deductions the deductions to set
     */
    public void setDeductions(HashMap<Long, TCDeduction> deductions)
    {
        this.deductions = deductions;
    }

    @Override
    public int compareTo(TCValue o)
    {
        return getValueId().compareTo(o.getValueId());
    }

    @Override
    public TCValue clone() throws CloneNotSupportedException
    {
        TCValue clone = (TCValue) super.clone();
        clone.setTiers((HashMap) getTiers().clone());
        clone.setDeductions((HashMap) getDeductions().clone());
        return clone;
    }
}
