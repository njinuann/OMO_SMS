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
public final class LGItem
{
    private String prefix;
    private Object payload;

    public LGItem(String prefix, Object payload)
    {
        setPrefix(prefix);
        setPayload(payload);
    }

    /**
     * @return the prefix
     */
    public String getPrefix()
    {
        return prefix;
    }

    /**
     * @param prefix the prefix to set
     */
    public void setPrefix(String prefix)
    {
        this.prefix = prefix;
    }

    /**
     * @return the payload
     */
    public Object getPayload()
    {
        return payload;
    }

    /**
     * @param payload the payload to set
     */
    public void setPayload(Object payload)
    {
        this.payload = payload;
    }
}
