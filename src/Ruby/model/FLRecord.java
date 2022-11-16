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
public final class FLRecord implements Serializable
{
    private Long recId;
    private Date createDt;
    private String type;
    private String recipient;
    private String fileName;
    private Long fileSize;
    private Integer records;

    public FLRecord(String type)
    {
        setType(type);
    }

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
     * @return the recipient
     */
    public String getRecipient()
    {
        return recipient;
    }

    /**
     * @param recipient the recipient to set
     */
    public void setRecipient(String recipient)
    {
        this.recipient = recipient;
    }

    /**
     * @return the fileName
     */
    public String getFileName()
    {
        return fileName;
    }

    /**
     * @param fileName the fileName to set
     */
    public void setFileName(String fileName)
    {
        this.fileName = fileName;
    }

    /**
     * @return the fileSize
     */
    public Long getFileSize()
    {
        return fileSize;
    }

    /**
     * @param fileSize the fileSize to set
     */
    public void setFileSize(Long fileSize)
    {
        this.fileSize = fileSize;
    }

    /**
     * @return the records
     */
    public Integer getRecords()
    {
        return records;
    }

    /**
     * @param records the records to set
     */
    public void setRecords(Integer records)
    {
        this.records = records;
    }
}
