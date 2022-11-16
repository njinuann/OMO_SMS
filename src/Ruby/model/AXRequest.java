/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Ruby.model;

import Ruby.acx.TXType;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 *
 * @author Pecherk
 */
public class AXRequest implements Serializable
{
    private String channel;
    private Long channelId;
    private String txnCode;
    private TXType txnType;
    private String reference;
    private String msisdn;
    private String narration;
    private String custNo;
    private String custName;
    private USRole userRole = new USRole();
    private CNAccount account = new CNAccount();
    private CNAccount contra = new CNAccount();
    private BigDecimal amount = BigDecimal.ZERO;
    private BigDecimal disbAmount = BigDecimal.ZERO;
    private TXCharge charge = new TXCharge();
    private CNBranch branch = new CNBranch();
    private CNCurrency currency = new CNCurrency();
    private boolean charged;
    private boolean reversal;
    private Integer loanTerm;
    private Integer count;
    private Long productId;
    private CNUser user = new CNUser();
    private String termCode;
    private String detail;
    private String vmtNumber;
    private boolean setBalance = true;
    private boolean inverted;

    /**
     * @return the reference
     */
    public String getReference()
    {
        return reference;
    }

    /**
     * @param reference the reference to set
     */
    public void setReference(String reference)
    {
        this.reference = reference;
    }

    /**
     * @return the amount
     */
    public BigDecimal getAmount()
    {
        return amount;
    }

    /**
     * @param txnAmount the amount to set
     */
    public void setAmount(BigDecimal txnAmount)
    {
        this.amount = txnAmount;
    }

    /**
     * @return the narration
     */
    public String getNarration()
    {
        return narration;
    }

    /**
     * @param narration the narration to set
     */
    public void setNarration(String narration)
    {
        this.narration = narration;
    }

    /**
     * @return the msisdn
     */
    public String getMsisdn()
    {
        return msisdn;
    }

    /**
     * @param msisdn the msisdn to set
     */
    public void setMsisdn(String msisdn)
    {
        this.msisdn = msisdn;
    }

    /**
     * @return the menuCode
     */
    public String getTxnCode()
    {
        return txnCode;
    }

    /**
     * @param txnCode the menuCode to set
     */
    public void setTxnCode(String txnCode)
    {
        this.txnCode = txnCode;
    }

    /**
     * @return the reversal
     */
    public boolean isReversal()
    {
        return reversal;
    }

    /**
     * @param reversal the reversal to set
     */
    public void setReversal(boolean reversal)
    {
        this.reversal = reversal;
    }

    /**
     * @return the account
     */
    public CNAccount getAccount()
    {
        return account;
    }

    /**
     * @param account the account to set
     */
    public void setAccount(CNAccount account)
    {
        this.account = account;
    }

    /**
     * @return the contra
     */
    public CNAccount getContra()
    {
        return contra;
    }

    /**
     * @param contra the contra to set
     */
    public void setContra(CNAccount contra)
    {
        this.contra = contra;
    }

    /**
     * @return the branch
     */
    public CNBranch getBranch()
    {
        return branch;
    }

    /**
     * @param cNBranch the branch to set
     */
    public void setBranch(CNBranch cNBranch)
    {
        this.branch = cNBranch;
    }

    /**
     * @return the currency
     */
    public CNCurrency getCurrency()
    {
        return currency;
    }

    /**
     * @param cNCurrency the currency to set
     */
    public void setCurrency(CNCurrency cNCurrency)
    {
        this.currency = cNCurrency;
    }

    /**
     * @return the charge
     */
    public TXCharge getCharge()
    {
        return charge;
    }

    /**
     * @param charge the charge to set
     */
    public void setCharge(TXCharge charge)
    {
        this.charge = charge;
    }

    /**
     * @return the charged
     */
    public boolean isCharged()
    {
        return charged;
    }

    /**
     * @param charged the charged to set
     */
    public void setCharged(boolean charged)
    {
        this.charged = charged;
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
     * @return the channel
     */
    public String getChannel()
    {
        return channel;
    }

    /**
     * @param channel the channel to set
     */
    public void setChannel(String channel)
    {
        this.channel = channel;
    }

    /**
     * @return the txnType
     */
    public TXType getTxnType()
    {
        return txnType;
    }

    /**
     * @param txnType the txnType to set
     */
    public void setTxnType(TXType txnType)
    {
        this.txnType = txnType;
    }

    /**
     * @return the count
     */
    public Integer getCount()
    {
        return count;
    }

    /**
     * @param count the count to set
     */
    public void setCount(Integer count)
    {
        this.count = count;
    }

    /**
     * @return the detail
     */
    public String getDetail()
    {
        return detail;
    }

    /**
     * @param detail the detail to set
     */
    public void setDetail(String detail)
    {
        this.detail = detail;
    }

    /**
     * @return the userRole
     */
    public USRole getUserRole()
    {
        return userRole;
    }

    /**
     * @param userRole the userRole to set
     */
    public void setUserRole(USRole userRole)
    {
        this.userRole = userRole;
    }

    /**
     * @return the user
     */
    public CNUser getUser()
    {
        return user;
    }

    /**
     * @param user the user to set
     */
    public void setUser(CNUser user)
    {
        this.user = user;
    }

    /**
     * @return the inverted
     */
    public boolean isInverted()
    {
        return inverted;
    }

    /**
     * @param inverted the inverted to set
     */
    public void setInverted(boolean inverted)
    {
        this.inverted = inverted;
    }

    /**
     * @return the setBalance
     */
    public boolean isSetBalance()
    {
        return setBalance;
    }

    /**
     * @param setBalance the setBalance to set
     */
    public void setSetBalance(boolean setBalance)
    {
        this.setBalance = setBalance;
    }

    /**
     * @return the vmtNumber
     */
    public String getVmtNumber()
    {
        return vmtNumber;
    }

    /**
     * @param vmtNumber the vmtNumber to set
     */
    public void setVmtNumber(String vmtNumber)
    {
        this.vmtNumber = vmtNumber;
    }

    /**
     * @return the loanTerm
     */
    public Integer getLoanTerm()
    {
        return loanTerm;
    }

    /**
     * @param loanTerm the loanTerm to set
     */
    public void setLoanTerm(Integer loanTerm)
    {
        this.loanTerm = loanTerm;
    }

    /**
     * @return the termCode
     */
    public String getTermCode()
    {
        return termCode;
    }

    /**
     * @param termCode the termCode to set
     */
    public void setTermCode(String termCode)
    {
        this.termCode = termCode;
    }

    /**
     * @return the disbAmount
     */
    public BigDecimal getDisbAmount()
    {
        return disbAmount;
    }

    /**
     * @param disbAmount the disbAmount to set
     */
    public void setDisbAmount(BigDecimal disbAmount)
    {
        this.disbAmount = disbAmount;
    }

    /**
     * @return the productId
     */
    public Long getProductId()
    {
        return productId;
    }

    /**
     * @param productId the productId to set
     */
    public void setProductId(Long productId)
    {
        this.productId = productId;
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
     * @return the custName
     */
    public String getCustName()
    {
        return custName;
    }

    /**
     * @param custName the custName to set
     */
    public void setCustName(String custName)
    {
        this.custName = custName;
    }
}
