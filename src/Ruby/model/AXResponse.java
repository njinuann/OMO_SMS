/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Ruby.model;

import Ruby.acx.AXResult;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 *
 * @author Pecherk
 */
public class AXResponse implements Serializable
{
    private Long txnId;
    private Long applId;
    private String data;
    private Long chargeId;
    private String message;
    private String reference;
    private String accountNumber;
    private String customerNumber;
    private AXResult result = AXResult.SUCCESS;
    private LNDetail loanDetail = new LNDetail();
    private BigDecimal balance;
    private String xapiCode;
    private String respCode;

    public AXResponse()
    {
    }

    public AXResponse(AXResult result)
    {
        setResult(result);
    }

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
     * @return the balance
     */
    public BigDecimal getBalance()
    {
        return balance;
    }

    /**
     * @param balance the balance to set
     */
    public void setBalance(BigDecimal balance)
    {
        this.balance = balance;
    }

    /**
     * @return the chargeId
     */
    public Long getChargeId()
    {
        return chargeId;
    }

    /**
     * @param chargeId the chargeId to set
     */
    public void setChargeId(Long chargeId)
    {
        this.chargeId = chargeId;
    }

    /**
     * @return the result
     */
    public AXResult getResult()
    {
        return result;
    }

    /**
     * @param tXResult the result to set
     */
    public final void setResult(AXResult tXResult)
    {
        this.result = tXResult;
    }

    /**
     * @return the txnId
     */
    public Long getTxnId()
    {
        return txnId;
    }

    /**
     * @param txnId the txnId to set
     */
    public void setTxnId(Long txnId)
    {
        this.txnId = txnId;
    }

    /**
     * @return the message
     */
    public String getMessage()
    {
        return message;
    }

    /**
     * @param message the message to set
     */
    public void setMessage(String message)
    {
        this.message = message;
    }

    /**
     * @return the data
     */
    public String getData()
    {
        return data;
    }

    /**
     * @param data the data to set
     */
    public void setData(String data)
    {
        this.data = data;
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
     * @return the respCode
     */
    public String getRespCode()
    {
        return respCode;
    }

    /**
     * @param respCode the respCode to set
     */
    public void setRespCode(String respCode)
    {
        this.respCode = respCode;
    }

    /**
     * @return the accountNumber
     */
    public String getAccountNumber()
    {
        return accountNumber;
    }

    /**
     * @param accountNumber the accountNumber to set
     */
    public void setAccountNumber(String accountNumber)
    {
        this.accountNumber = accountNumber;
    }

    /**
     * @return the customerNumber
     */
    public String getCustomerNumber()
    {
        return customerNumber;
    }

    /**
     * @param customerNumber the customerNumber to set
     */
    public void setCustomerNumber(String customerNumber)
    {
        this.customerNumber = customerNumber;
    }

    /**
     * @return the applId
     */
    public Long getApplId()
    {
        return applId;
    }

    /**
     * @param applId the applId to set
     */
    public void setApplId(Long applId)
    {
        this.applId = applId;
    }

    /**
     * @return the loanDetail
     */
    public LNDetail getLoanDetail()
    {
        return loanDetail;
    }

    /**
     * @param loanDetail the loanDetail to set
     */
    public void setLoanDetail(LNDetail loanDetail)
    {
        this.loanDetail = loanDetail;
    }
}
