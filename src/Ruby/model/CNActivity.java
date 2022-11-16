/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Ruby.model;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 *
 * @author Pecherk
 */
public class CNActivity
{
    private BigDecimal count = BigDecimal.ZERO;
    private BigDecimal total = BigDecimal.ZERO;

    /**
     * @return the count
     */
    public BigDecimal getCount()
    {
        return count;
    }

    /**
     * @param count the count to set
     */
    public void setCount(BigDecimal count)
    {
        this.count = count.setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * @return the total
     */
    public BigDecimal getTotal()
    {
        return total;
    }

    /**
     * @param total the total to set
     */
    public void setTotal(BigDecimal total)
    {
        this.total = total.setScale(2, RoundingMode.HALF_UP);
    }
}
