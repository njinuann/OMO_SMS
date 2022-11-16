/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Ruby.model;

import java.util.ArrayList;
import java.util.Date;

/**
 *
 * @author Pecherk
 */
public class TLDrawer
{
    private Long drawerId;
    private String drawerNumber;
    private String drawerAccount;
    private Date openDate;
    private String status;
    private ArrayList<CNCurrency> currencies = new ArrayList<>();

    /**
     * @return the drawerId
     */
    public Long getDrawerId()
    {
        return drawerId;
    }

    /**
     * @param drawerId the drawerId to set
     */
    public void setDrawerId(Long drawerId)
    {
        this.drawerId = drawerId;
    }

    /**
     * @return the drawerNumber
     */
    public String getDrawerNumber()
    {
        return drawerNumber;
    }

    /**
     * @param drawerNumber the drawerNumber to set
     */
    public void setDrawerNumber(String drawerNumber)
    {
        this.drawerNumber = drawerNumber;
    }

    /**
     * @return the drawerAccount
     */
    public String getDrawerAccount()
    {
        return drawerAccount;
    }

    /**
     * @param drawerAccount the drawerAccount to set
     */
    public void setDrawerAccount(String drawerAccount)
    {
        this.drawerAccount = drawerAccount;
    }

    /**
     * @return the openDate
     */
    public Date getOpenDate()
    {
        return openDate;
    }

    /**
     * @param openDate the openDate to set
     */
    public void setOpenDate(Date openDate)
    {
        this.openDate = openDate;
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
     * @return the currencies
     */
    public ArrayList<CNCurrency> getCurrencies()
    {
        return currencies;
    }

    /**
     * @param currencies the currencies to set
     */
    public void setCurrencies(ArrayList<CNCurrency> currencies)
    {
        this.currencies = currencies;
    }
}
