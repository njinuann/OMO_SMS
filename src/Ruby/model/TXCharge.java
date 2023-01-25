/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Ruby.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;

/**
 *
 * @author Pecherk
 */
public class TXCharge implements Serializable
{
    private String channelLedger;
    private String chargeNarration;
    private CNAccount chargeAccount = new CNAccount();
    private BigDecimal chargeAmount = BigDecimal.ZERO;
    private BigDecimal taxAmount = BigDecimal.ZERO;
    private ArrayList<TCSplit> splitList = new ArrayList<>();
    private String chargeLedger;
    private String scheme;
    private String code;

    /**
     * @return the chargeLedger
     */
    public String getChargeLedger()
    {
        return chargeLedger;
    }

    /**
     * @param chargeLedger the chargeLedger to set
     */
    public void setChargeLedger(String chargeLedger)
    {
        this.chargeLedger = chargeLedger;
    }

    /**
     * @return the channelLedger
     */
    public String getChannelLedger()
    {
        return channelLedger;
    }

    /**
     * @param channelLedger the channelLedger to set
     */
    public void setChannelLedger(String channelLedger)
    {
        this.channelLedger = channelLedger;
    }

    /**
     * @return the chargeNarration
     */
    public String getChargeNarration()
    {
        return chargeNarration;
    }

    /**
     * @param chargeNarration the chargeNarration to set
     */
    public void setChargeNarration(String chargeNarration)
    {
        this.chargeNarration = chargeNarration;
    }

    /**
     * @return the chargeAccount
     */
    public CNAccount getChargeAccount()
    {
        return chargeAccount;
    }

    /**
     * @param chargeAccount the chargeAccount to set
     */
    public void setChargeAccount(CNAccount chargeAccount)
    {
        this.chargeAccount = chargeAccount;
    }

    /**
     * @return the chargeAmount
     */
    public BigDecimal getChargeAmount()
    {
        return chargeAmount;
    }

    /**
     * @param chargeAmount the chargeAmount to set
     */
    public void setChargeAmount(BigDecimal chargeAmount)
    {
        this.chargeAmount = chargeAmount.setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * @return the splitList
     */
    public ArrayList<TCSplit> getSplitList()
    {
        return splitList;
    }

    /**
     * @param splitList the splitList to set
     */
    public void setSplitList(ArrayList<TCSplit> splitList)
    {
        this.splitList = splitList;
    }

    /**
     * @return the scheme
     */
    public String getScheme()
    {
        return scheme;
    }

    /**
     * @param scheme the scheme to set
     */
    public void setScheme(String scheme)
    {
        this.scheme = scheme;
    }

    /**
     * @return the taxAmount
     */
    public BigDecimal getTaxAmount()
    {
        return taxAmount;
    }

    /**
     * @param taxAmount the taxAmount to set
     */
    public void setTaxAmount(BigDecimal taxAmount)
    {
        this.taxAmount = taxAmount;
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
}
