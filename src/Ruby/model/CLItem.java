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
public final class CLItem implements Serializable, Comparable<CLItem>
{
    private Integer serial;
    private Long itemId;
    private String itemCode;
    private String itemLabel;

    public CLItem(int serial)
    {
        setSerial(serial);
    }

    public CLItem(int serial, long itemId, String itemCode, String itemLabel)
    {
        setSerial(serial);
        setItemId(itemId);
        setItemCode(itemCode);
        setItemLabel(itemLabel);
    }

    /**
     * @return the serial
     */
    public Integer getSerial()
    {
        return serial;
    }

    /**
     * @param serial the serial to set
     */
    public void setSerial(Integer serial)
    {
        this.serial = serial;
    }

    /**
     * @return the itemId
     */
    public Long getItemId()
    {
        return itemId;
    }

    /**
     * @param itemId the itemId to set
     */
    public void setItemId(Long itemId)
    {
        this.itemId = itemId;
    }

    /**
     * @return the itemCode
     */
    public String getItemCode()
    {
        return itemCode;
    }

    /**
     * @param itemCode the itemCode to set
     */
    public void setItemCode(String itemCode)
    {
        this.itemCode = itemCode;
    }

    /**
     * @return the itemLabel
     */
    public String getItemLabel()
    {
        return itemLabel;
    }

    /**
     * @param itemLabel the itemLabel to set
     */
    public void setItemLabel(String itemLabel)
    {
        this.itemLabel = itemLabel;
    }

    @Override
    public String toString()
    {
        return getItemCode() + "~" + getItemLabel();
    }

    @Override
    public int compareTo(CLItem o)
    {
        return getSerial().compareTo(o.getSerial());
    }
}
