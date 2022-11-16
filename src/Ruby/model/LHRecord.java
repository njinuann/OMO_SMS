/*
 * To change this license header; String choose License Headers in Project Properties.
 * To change this template file; String choose Tools | Templates
 * and open the template in the editor.
 */
package Ruby.model;

import java.math.BigDecimal;
import java.util.Date;

/**
 *
 * @author Pecherk
 */
public class LHRecord
{
    private Long acctHistId;
    private Long tranJournalId;
    private Date sysCreateTs;
    private String eventCd;
    private String glAcctNo;
    private Long originBuId;
    private String buNm;
    private Date tranDt;
    private Date valueDt;
    private Long channelId;
    private String channelDesc;
    private String tranRefTxt;
    private String chqNo;
    private String tranDesc;
    private String drCrInd;
    private String crncyCdIso;
    private BigDecimal acctAmt;
    private String contraAcctNo;
    private BigDecimal stmntBal;

    /**
     * @return the acctHistId
     */
    public Long getAcctHistId()
    {
        return acctHistId;
    }

    /**
     * @param acctHistId the acctHistId to set
     */
    public void setAcctHistId(Long acctHistId)
    {
        this.acctHistId = acctHistId;
    }

    /**
     * @return the tranJournalId
     */
    public Long getTranJournalId()
    {
        return tranJournalId;
    }

    /**
     * @param tranJournalId the tranJournalId to set
     */
    public void setTranJournalId(Long tranJournalId)
    {
        this.tranJournalId = tranJournalId;
    }

    /**
     * @return the sysCreateTs
     */
    public Date getSysCreateTs()
    {
        return sysCreateTs;
    }

    /**
     * @param sysCreateTs the sysCreateTs to set
     */
    public void setSysCreateTs(Date sysCreateTs)
    {
        this.sysCreateTs = sysCreateTs;
    }

    /**
     * @return the eventCd
     */
    public String getEventCd()
    {
        return eventCd;
    }

    /**
     * @param eventCd the eventCd to set
     */
    public void setEventCd(String eventCd)
    {
        this.eventCd = eventCd;
    }

    /**
     * @return the glAcctNo
     */
    public String getGlAcctNo()
    {
        return glAcctNo;
    }

    /**
     * @param glAcctNo the glAcctNo to set
     */
    public void setGlAcctNo(String glAcctNo)
    {
        this.glAcctNo = glAcctNo;
    }

    /**
     * @return the originBuId
     */
    public Long getOriginBuId()
    {
        return originBuId;
    }

    /**
     * @param originBuId the originBuId to set
     */
    public void setOriginBuId(Long originBuId)
    {
        this.originBuId = originBuId;
    }

    /**
     * @return the buNm
     */
    public String getBuNm()
    {
        return buNm;
    }

    /**
     * @param buNm the buNm to set
     */
    public void setBuNm(String buNm)
    {
        this.buNm = buNm;
    }

    /**
     * @return the tranDt
     */
    public Date getTranDt()
    {
        return tranDt;
    }

    /**
     * @param tranDt the tranDt to set
     */
    public void setTranDt(Date tranDt)
    {
        this.tranDt = tranDt;
    }

    /**
     * @return the valueDt
     */
    public Date getValueDt()
    {
        return valueDt;
    }

    /**
     * @param valueDt the valueDt to set
     */
    public void setValueDt(Date valueDt)
    {
        this.valueDt = valueDt;
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
     * @return the channelDesc
     */
    public String getChannelDesc()
    {
        return channelDesc;
    }

    /**
     * @param channelDesc the channelDesc to set
     */
    public void setChannelDesc(String channelDesc)
    {
        this.channelDesc = channelDesc;
    }

    /**
     * @return the tranRefTxt
     */
    public String getTranRefTxt()
    {
        return tranRefTxt;
    }

    /**
     * @param tranRefTxt the tranRefTxt to set
     */
    public void setTranRefTxt(String tranRefTxt)
    {
        this.tranRefTxt = tranRefTxt;
    }

    /**
     * @return the chqNo
     */
    public String getChqNo()
    {
        return chqNo;
    }

    /**
     * @param chqNo the chqNo to set
     */
    public void setChqNo(String chqNo)
    {
        this.chqNo = chqNo;
    }

    /**
     * @return the tranDesc
     */
    public String getTranDesc()
    {
        return tranDesc;
    }

    /**
     * @param tranDesc the tranDesc to set
     */
    public void setTranDesc(String tranDesc)
    {
        this.tranDesc = tranDesc;
    }

    /**
     * @return the drCrInd
     */
    public String getDrCrInd()
    {
        return drCrInd;
    }

    /**
     * @param drCrInd the drCrInd to set
     */
    public void setDrCrInd(String drCrInd)
    {
        this.drCrInd = drCrInd;
    }

    /**
     * @return the crncyCdIso
     */
    public String getCrncyCdIso()
    {
        return crncyCdIso;
    }

    /**
     * @param crncyCdIso the crncyCdIso to set
     */
    public void setCrncyCdIso(String crncyCdIso)
    {
        this.crncyCdIso = crncyCdIso;
    }

    /**
     * @return the acctAmt
     */
    public BigDecimal getAcctAmt()
    {
        return acctAmt;
    }

    /**
     * @param acctAmt the acctAmt to set
     */
    public void setAcctAmt(BigDecimal acctAmt)
    {
        this.acctAmt = acctAmt;
    }

    /**
     * @return the contraAcctNo
     */
    public String getContraAcctNo()
    {
        return contraAcctNo;
    }

    /**
     * @param contraAcctNo the contraAcctNo to set
     */
    public void setContraAcctNo(String contraAcctNo)
    {
        this.contraAcctNo = contraAcctNo;
    }

    /**
     * @return the stmntBal
     */
    public BigDecimal getStmntBal()
    {
        return stmntBal;
    }

    /**
     * @param stmntBal the stmntBal to set
     */
    public void setStmntBal(BigDecimal stmntBal)
    {
        this.stmntBal = stmntBal;
    }
}
