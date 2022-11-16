/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Ruby.model;

import java.io.Serializable;
import java.util.Date;

/**
 *
 * @author Pecherk
 */
public class MURecord implements Serializable
{
    private Long recId;
    private Date createDt;
    private Long channelId;
    private String address;
    private String accessCd;
    private String custNo;
    private String account;
    private String alias;
    private String role;
    private String type;
    private String state;
    private String result;

    /**
     * @return the recId
     */
    public Long getRecId()
    {
        return recId;
    }

    /**
     * @param recId the recId to set
     */
    public void setRecId(Long recId)
    {
        this.recId = recId;
    }

    /**
     * @return the createDt
     */
    public Date getCreateDt()
    {
        return createDt;
    }

    /**
     * @param createDt the createDt to set
     */
    public void setCreateDt(Date createDt)
    {
        this.createDt = createDt;
    }

    /**
     * @return the channelId
     */
    public Long getChannelId()
    {
        return channelId;
    }

    /**
     * @param channelId the channelId to set
     */
    public void setChannelId(Long channelId)
    {
        this.channelId = channelId;
    }

    /**
     * @return the address
     */
    public String getAddress()
    {
        return address;
    }

    /**
     * @param address the address to set
     */
    public void setAddress(String address)
    {
        this.address = address;
    }

    /**
     * @return the accessCd
     */
    public String getAccessCd()
    {
        return accessCd;
    }

    /**
     * @param accessCd the accessCd to set
     */
    public void setAccessCd(String accessCd)
    {
        this.accessCd = accessCd;
    }

    /**
     * @return the custNo
     */
    public String getCustNo()
    {
        return custNo;
    }

    /**
     * @param custNo the custNo to set
     */
    public void setCustNo(String custNo)
    {
        this.custNo = custNo;
    }

    /**
     * @return the account
     */
    public String getAccount()
    {
        return account;
    }

    /**
     * @param account the account to set
     */
    public void setAccount(String account)
    {
        this.account = account;
    }

    /**
     * @return the alias
     */
    public String getAlias()
    {
        return alias;
    }

    /**
     * @param alias the alias to set
     */
    public void setAlias(String alias)
    {
        this.alias = alias;
    }

    /**
     * @return the role
     */
    public String getRole()
    {
        return role;
    }

    /**
     * @param role the role to set
     */
    public void setRole(String role)
    {
        this.role = role;
    }

    /**
     * @return the type
     */
    public String getType()
    {
        return type;
    }

    /**
     * @param type the type to set
     */
    public void setType(String type)
    {
        this.type = type;
    }

    /**
     * @return the state
     */
    public String getState()
    {
        return state;
    }

    /**
     * @param state the state to set
     */
    public void setState(String state)
    {
        this.state = state;
    }

    /**
     * @return the result
     */
    public String getResult()
    {
        return result;
    }

    /**
     * @param result the result to set
     */
    public void setResult(String result)
    {
        this.result = result;
    }

}
