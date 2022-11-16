/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Ruby.model;

/**
 *
 * @author Pecherk
 */
public final class AXNode
{
    private String description;

    public AXNode(String description)
    {
        setDescription(description);
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

    @Override
    public String toString()
    {
        return getDescription();
    }
}
