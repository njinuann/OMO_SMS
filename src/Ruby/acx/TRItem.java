/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Ruby.acx;

import Ruby.model.ALHeader;
import java.io.Serializable;
import java.util.LinkedHashMap;

/**
 *
 * @author Pecherk
 */
public final class TRItem implements Serializable
{
    private String code;
    private String text;
    private final LinkedHashMap<String, Object> request = new LinkedHashMap<>();
    private final LinkedHashMap<String, Object> response = new LinkedHashMap<>();
    private final LinkedHashMap<ALHeader, Object> detail = new LinkedHashMap<>();
    private boolean approved;

    public TRItem()
    {
        this(null, null, false);
    }

    public TRItem(String code, String description, boolean approved)
    {
        setCode(code);
        setText(description);
        setApproved(approved);
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

    /**
     * @return the request
     */
    public LinkedHashMap<String, Object> getRequest()
    {
        return request;
    }

    /**
     * @return the response
     */
    public LinkedHashMap<String, Object> getResponse()
    {
        return response;
    }

    /**
     * @return the detail
     */
    public LinkedHashMap<ALHeader, Object> getDetail()
    {
        return detail;
    }

    /**
     * @return the text
     */
    public String getText()
    {
        return text;
    }

    /**
     * @param text the text to set
     */
    public void setText(String text)
    {
        this.text = text;
    }

    /**
     * @return the approved
     */
    public boolean isApproved()
    {
        return approved;
    }

    /**
     * @param approved the approved to set
     */
    public void setApproved(boolean approved)
    {
        this.approved = approved;
    }

    @Override
    public String toString()
    {
        return getText();
    }
}
