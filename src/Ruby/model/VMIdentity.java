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
public class VMIdentity
{
    private Long recId;
    private String reqId;
    private String idType;
    private String idNumber;
    private String xapiCode;
    private String recSt;

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
     * @return the reqId
     */
    public String getReqId()
    {
        return reqId;
    }

    /**
     * @param reqId the reqId to set
     */
    public void setReqId(String reqId)
    {
        this.reqId = reqId;
    }

    /**
     * @return the idType
     */
    public String getIdType()
    {
        return idType;
    }

    /**
     * @param idType the idType to set
     */
    public void setIdType(String idType)
    {
        this.idType = idType;
    }

    /**
     * @return the idNumber
     */
    public String getIdNumber()
    {
        return idNumber;
    }

    /**
     * @param idNumber the idNumber to set
     */
    public void setIdNumber(String idNumber)
    {
        this.idNumber = idNumber;
    }

    /**
     * @return the xapiCode
     */
    public String getXapiCode()
    {
        return xapiCode;
    }

    /**
     * @param xapiCode the xapiCode to set
     */
    public void setXapiCode(String xapiCode)
    {
        this.xapiCode = xapiCode;
    }

    /**
     * @return the recSt
     */
    public String getRecSt()
    {
        return recSt;
    }

    /**
     * @param recSt the recSt to set
     */
    public void setRecSt(String recSt)
    {
        this.recSt = recSt;
    }
}
