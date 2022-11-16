/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Ruby.acx;

/**
 *
 * @author Pecherk
 */
public final class AXNode
{
    private String code = "";
    private String description = "";
    private boolean approved = true;

    public AXNode(String code, String description)
    {
        this(code, description, true);
    }

    public AXNode(String code, String description, boolean approved)
    {
        setCode(code);
        setApproved(approved);
        setDescription(description);
    }

    public AXNode getClone(boolean approved)
    {
        return new AXNode(getCode(), getDescription(), approved);
    }

    @Override
    public String toString()
    {
        return getDescription();
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
}
