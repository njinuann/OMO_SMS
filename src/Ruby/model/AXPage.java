/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Ruby.model;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author Pecherk
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "page")
public final class AXPage implements Serializable, Comparable<AXPage>
{
    @XmlTransient
    private Integer page;
    @XmlElement(required = true)
    private String pageInfo;
    @XmlElement(required = true)
    private String pageText;
    @XmlTransient
    private AXPage nextPage;

    public AXPage()
    {
        this(0, null);
    }

    public AXPage(int page, String pageText)
    {
        setPage(page);
        setPageText(pageText);
    }

    /**
     * @return the page
     */
    public Integer getPage()
    {
        return page;
    }

    /**
     * @param page the page to set
     */
    public void setPage(Integer page)
    {
        this.page = page;
    }

    /**
     * @return the pageInfo
     */
    public String getPageInfo()
    {
        return pageInfo;
    }

    /**
     * @param pageInfo the pageInfo to set
     */
    public void setPageInfo(String pageInfo)
    {
        this.pageInfo = pageInfo;
    }

    /**
     * @return the pageText
     */
    public String getPageText()
    {
        return pageText;
    }

    /**
     * @param pageText the pageText to set
     */
    public void setPageText(String pageText)
    {
        this.pageText = pageText;
    }

    /**
     * @return the nextPage
     */
    public AXPage getNextPage()
    {
        return nextPage;
    }

    /**
     * @param nextPage the nextPage to set
     */
    public void setNextPage(AXPage nextPage)
    {
        this.nextPage = nextPage;
    }

    public boolean hasNextPage()
    {
        return getNextPage() != null;
    }

    @Override
    public int compareTo(AXPage o)
    {
        return getPage().compareTo(o.getPage());
    }
}
