/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Ruby.acx;

import Ruby.APController;
import Ruby.DBClient;
import static Ruby.acx.AXConstant.statementDateFormat;
import Ruby.model.AXRequest;
import Ruby.model.AXResponse;
import Ruby.model.AXSplit;
import Ruby.model.CBNode;
import Ruby.model.GPSplit;
import Ruby.model.TCSplit;
import Ruby.sms.ALController;

import com.neptunesoftware.supernova.ws.server.account.AccountWebServiceEndPointPort;
import com.neptunesoftware.supernova.ws.server.account.AccountWebServiceStub;
import com.neptunesoftware.supernova.ws.server.transaction.TransactionsWebServiceStub;
import com.neptunesoftware.supernova.ws.server.transfer.FundsTransferWebServiceEndPointPort;
import com.neptunesoftware.supernova.ws.server.transfer.FundsTransferWebServiceStub;
import com.neptunesoftware.supernova.ws.server.txnprocess.TxnProcessWebServiceEndPointPort;
import com.neptunesoftware.supernova.ws.server.txnprocess.TxnProcessWebServiceStub;
import com.neptunesoftware.supernova.ws.common.XAPIRequestBaseObject;
import com.neptunesoftware.supernova.ws.server.account.data.AccountBalanceOutputData;
import com.neptunesoftware.supernova.ws.server.account.data.AccountBalanceRequest;
import com.neptunesoftware.supernova.ws.server.casemgmt.CasemgmtWebServiceEndPointPort;
import com.neptunesoftware.supernova.ws.server.casemgmt.CasemgmtWebServiceStub;
import com.neptunesoftware.supernova.ws.server.casemgmt.data.CaseOutputData;
import com.neptunesoftware.supernova.ws.server.casemgmt.data.CaseRequestData;
import com.neptunesoftware.supernova.ws.server.channeladmin.ChannelAdminWebServiceEndPointPort;
import com.neptunesoftware.supernova.ws.server.channeladmin.ChannelAdminWebServiceStub;
import com.neptunesoftware.supernova.ws.server.channeladmin.data.CustomerChannelAccountRequestData;
import com.neptunesoftware.supernova.ws.server.channeladmin.data.CustomerChannelCreationData;
import com.neptunesoftware.supernova.ws.server.channeladmin.data.CustomerChannelUserOutputData;
import com.neptunesoftware.supernova.ws.server.channeladmin.data.CustomerChannelUserRequestData;
import com.neptunesoftware.supernova.ws.server.creditapp.CreditAppWebServiceEndPointPort;
import com.neptunesoftware.supernova.ws.server.creditapp.CreditAppWebServiceStub;
import com.neptunesoftware.supernova.ws.server.creditapp.data.CreditApplOutputData;
import com.neptunesoftware.supernova.ws.server.creditapp.data.CreditApplRequestData;
import com.neptunesoftware.supernova.ws.server.customer.CustomerWebServiceEndPointPort;
import com.neptunesoftware.supernova.ws.server.loanaccount.LoanAccountWebServiceEndPointPort;
import com.neptunesoftware.supernova.ws.server.loanaccount.LoanAccountWebServiceStub;
import com.neptunesoftware.supernova.ws.server.loanaccount.data.LoanAccountHistoryOutputData;
import com.neptunesoftware.supernova.ws.server.loanaccount.data.LoanAccountHistoryRequest;
import com.neptunesoftware.supernova.ws.server.loanaccount.data.LoanAccountRequestData;
import com.neptunesoftware.supernova.ws.server.loanaccount.data.LoanAccountSummaryOutputData;
import com.neptunesoftware.supernova.ws.server.transaction.TransactionsWebServiceEndPointPort;
import com.neptunesoftware.supernova.ws.server.transaction.data.DepositTxnOutputData;
import com.neptunesoftware.supernova.ws.server.transaction.data.GLTransferOutputData;
import com.neptunesoftware.supernova.ws.server.transaction.data.GLTransferRequest;
import com.neptunesoftware.supernova.ws.server.transaction.data.TransactionInquiryRequest;
import com.neptunesoftware.supernova.ws.server.transaction.data.TxnResponseOutputData;
import com.neptunesoftware.supernova.ws.server.transfer.data.FundsTransferOutputData;
import com.neptunesoftware.supernova.ws.server.transfer.data.FundsTransferRequestData;
import com.neptunesoftware.supernova.ws.server.txnprocess.data.XAPIBaseTxnRequestData;
import com.neptunesoftware.supernova.ws.server.workflow.WorkflowWebServiceEndPointPort;
import com.neptunesoftware.supernova.ws.server.workflow.WorkflowWebServiceStub;
import com.neptunesoftware.supernova.ws.server.workflow.data.WFViewRequestData;
import java.math.BigDecimal;
import java.net.URL;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Objects;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;

/**
 *
 * @author Pecherk
 */
public final class AXClient
{

    private ATBox box;
    private AXResponse response = new AXResponse();

    public AXClient(ATBox toolBox)
    {
        setBox(toolBox);
    }

    private <T> T getPort(Class<T> clazz)
    {
        T port = null;
        try
        {
            boolean errorFound = false;
            for (CBNode cBNode : APController.getXapiNodes())
            {
                try
                {
                    switch (clazz.getSimpleName())
                    {
                        case "AccountWebServiceStub":
                            port = (T) new AccountWebServiceEndPointPort(new URL(((CBNode) cBNode).getContextUrl() + "AccountWebServiceEndPointPort?wsdl")).getAccountWebServiceStubPort();
                            break;
                        case "FundsTransferWebServiceStub":
                            port = (T) new FundsTransferWebServiceEndPointPort(new URL(((CBNode) cBNode).getContextUrl() + "FundsTransferWebServiceEndPointPort?wsdl")).getFundsTransferWebServiceStubPort();
                            break;
                        case "TxnProcessWebServiceStub":
                            port = (T) new TxnProcessWebServiceEndPointPort(new URL(((CBNode) cBNode).getContextUrl() + "TxnProcessWebServiceEndPointPort?wsdl")).getTxnProcessWebServiceStubPort();
                            break;
                        case "ChannelAdminWebServiceStub":
                            port = (T) new ChannelAdminWebServiceEndPointPort(new URL(((CBNode) cBNode).getContextUrl() + "ChannelAdminWebServiceEndPointPort?wsdl")).getChannelAdminWebServiceStubPort();
                            break;
                        case "WorkflowWebServiceStub":
                            port = (T) new WorkflowWebServiceEndPointPort(new URL(((CBNode) cBNode).getContextUrl() + "WorkflowWebServiceEndPointPort?wsdl")).getWorkflowWebServiceStubPort();
                            break;
                        case "CasemgmtWebServiceStub":
                            port = (T) new CasemgmtWebServiceEndPointPort(new URL(((CBNode) cBNode).getContextUrl() + "CasemgmtWebServiceEndPointPort?wsdl")).getCasemgmtWebServiceStubPort();
                            break;
                        case "CustomerWebServiceStub":
                            port = (T) new CustomerWebServiceEndPointPort(new URL(((CBNode) cBNode).getContextUrl() + "CustomerWebServiceBean?wsdl")).getCustomerWebServiceStubPort();
                            break;
                        case "LoanAccountWebServiceStub":
                            port = (T) new LoanAccountWebServiceEndPointPort(new URL(((CBNode) cBNode).getContextUrl() + "LoanAccountWebServiceEndPointPort?wsdl")).getLoanAccountWebServiceStubPort();
                            break;
                        case "CreditAppWebServiceStub":
                            port = (T) new CreditAppWebServiceEndPointPort(new URL(((CBNode) cBNode).getContextUrl() + "CreditAppWebServiceEndPointPort?wsdl")).getCreditAppWebServiceStubPort();
                            break;
                        case "TransactionsWebServiceStubStub":
                            port = (T) new TransactionsWebServiceEndPointPort(new URL(((CBNode) cBNode).getContextUrl() + "TransactionsWebServiceStubEndPointPort?wsdl")).getTransactionsWebServiceStubPort();
                            break;
                    }
                    swapNodes(cBNode);
                    break;
                }
                catch (Exception ex)
                {
                    cBNode.setOnline(Boolean.FALSE);
                    getLog().logError(ex);
                    errorFound = true;
                }
            }
            if (errorFound)
            {
                getWorker().sortArrayList(APController.getXapiNodes(), false);
            }
        }
        catch (Exception ex)
        {
            getLog().logError(ex);
        }
        return port;
    }

    private void swapNodes(CBNode cBNode)
    {
        int y = APController.getXapiNodes().indexOf(cBNode);
        for (int i = y + 1; i < APController.getXapiNodes().size(); i++)
        {
            if (APController.getXapiNodes().get(i).isOnline())
            {
                Collections.swap(APController.getXapiNodes(), y, i);
                y = i;
            }
        }
    }

    public javax.xml.datatype.XMLGregorianCalendar xMLGregorianCalendar(Calendar c)
    {
        try
        {
            GregorianCalendar gc = new GregorianCalendar();
            gc.setTimeInMillis(c.getTimeInMillis());
            javax.xml.datatype.XMLGregorianCalendar xc = DatatypeFactory.newInstance().newXMLGregorianCalendar(gc);
            return xc;
        }
        catch (DatatypeConfigurationException ex)
        {
            getResponse().setResult(processError(ex));
            return null;
        }
    }

    private XAPIRequestBaseObject getBaseRequest(XAPIRequestBaseObject requestData, AXRequest aXRequest)
    {
        requestData.setChannelId(aXRequest.getChannelId());
        requestData.setChannelCode(aXRequest.getChannel());

        requestData.setCardNumber(getWorker().isBlank(aXRequest.getMsisdn()) ? aXRequest.getReference() : aXRequest.getMsisdn());
        requestData.setTransmissionTime(System.currentTimeMillis());
        requestData.setOriginatorUserId(aXRequest.getUserRole().getUserId());

        requestData.setTerminalNumber(aXRequest.getChannel());
        requestData.setReference(checkReference(aXRequest.getReference()));
        requestData.setSysUserId(aXRequest.getUserRole().getUserId());

        requestData.setUserRoleId(aXRequest.getUserRole().getUserRoleId());
        requestData.setUserLoginId(aXRequest.getUserRole().getUserName());
        requestData.setUserAccessCode(aXRequest.getMsisdn());

        requestData.setCurrBUId(aXRequest.getBranch().getBuId());
        requestData.setUserId(aXRequest.getUserRole().getUserId());
        return requestData;
    }

    public AXResponse queryDepositBalance(AXRequest aXRequest)
    {
        try
        {
            if (processCharge(aXRequest, false, 1))
            {
                setNewBalance(aXRequest).setResult(AXResult.SUCCESS);
            }
            else if (AXResult.INSUFFICIENT_FUNDS == getResponse().getResult())
            {
                setNewBalance(aXRequest).setResult(AXResult.INSUFFICIENT_FUNDS);
            }
        }
        catch (Exception ex)
        {
            getResponse().setResult(processError(ex));
        }
        return getResponse();
    }

    public AXResponse queryDepositMinistatement(AXRequest aXRequest)
    {
        try
        {
            if (processCharge(aXRequest, false, 1))
            {
                TransactionInquiryRequest inquiryRequest = (TransactionInquiryRequest) getBaseRequest(new TransactionInquiryRequest(), aXRequest);
                inquiryRequest.setAccountNumber(aXRequest.getAccount().getAccountNumber());

                getLog().setCall(getCallTag(aXRequest, true), inquiryRequest);
                List<DepositTxnOutputData> depositTxnOutputData = getPort(TransactionsWebServiceStub.class).findDepositMiniStatement(inquiryRequest);
                getLog().setCall(getCallTag(aXRequest, false), depositTxnOutputData);

                int i = 0;
                StringBuilder buffer = new StringBuilder();
                for (DepositTxnOutputData statTxn : depositTxnOutputData)
                {
                    buffer.append("[").append(i++).append(".").append(statTxn.getDrcrFlag()).append(";").append(statTxn.getTxnDescription()).append(";").append(statTxn.getTxnAmount()).append(";").append(statementDateFormat.format(new Date(statTxn.getTxnDate()))).append("]");
                    if (i >= aXRequest.getCount())
                    {
                        break;
                    }
                }
                setNewBalance(aXRequest).setResult(AXResult.SUCCESS);
                getResponse().setData(buffer.toString().trim());
            }
            else if (AXResult.INSUFFICIENT_FUNDS == getResponse().getResult())
            {
                setNewBalance(aXRequest).setResult(AXResult.INSUFFICIENT_FUNDS);
            }
        }
        catch (Exception ex)
        {
            getResponse().setResult(processError(ex));
        }
        return getResponse();
    }

    public AXResponse postDepositTransfer(AXRequest aXRequest, Integer repeatCount)
    {
        try
        {
            if (AXResult.SUCCESS == checkBalance(aXRequest).getResult())
            {
                FundsTransferRequestData transferRequest = (FundsTransferRequestData) getBaseRequest(new FundsTransferRequestData(), aXRequest);
                transferRequest.setFromAccountNumber(aXRequest.isReversal() == aXRequest.isInverted() ? aXRequest.getAccount().getAccountNumber() : aXRequest.getContra().getAccountNumber());
                transferRequest.setToAccountNumber(aXRequest.isReversal() == aXRequest.isInverted() ? aXRequest.getContra().getAccountNumber() : aXRequest.getAccount().getAccountNumber());

                transferRequest.setTxnDescription(aXRequest.getNarration());
                transferRequest.setAcquiringInstitutionCode("");
                transferRequest.setTransactionAmount(aXRequest.getAmount());

                transferRequest.setAmount(aXRequest.getAmount());
                transferRequest.setFromCurrencyCode(aXRequest.getCurrency().getCurrencyCode());
                transferRequest.setToCurrencyCode(aXRequest.getContra().getCurrency().getCurrencyCode());

                transferRequest.setForwardingInstitutionCode("");
                transferRequest.setTrack2Data(aXRequest.getReference());
                transferRequest.setRetrievalReferenceNumber(aXRequest.getReference());

                Calendar calendar = Calendar.getInstance();
                calendar.setTime(getdClient().getProcessingDate());
                transferRequest.setLocalTransactionTime(xMLGregorianCalendar(calendar));

                getLog().setCall(getCallTag(aXRequest, true), transferRequest, repeatCount > 1);
                FundsTransferOutputData fundsTransferOutputData = getPort(FundsTransferWebServiceStub.class).internalDepositAccountTransfer(transferRequest);
                getLog().setCall(getCallTag(aXRequest, false), fundsTransferOutputData);

                getResponse().setResult(mapCode(fundsTransferOutputData.getResponseCode()));
                if (Objects.equals(AXConstant.XAPI_APPROVED, fundsTransferOutputData.getResponseCode()))
                {
                    getResponse().setTxnId(findTxnId(transferRequest, fundsTransferOutputData.getRetrievalReferenceNumber()));
                    if (processCharge(aXRequest, false, 1) || aXRequest.isInverted() || aXRequest.isReversal())
                    {
                        setNewBalance(aXRequest).setResult(AXResult.SUCCESS);
                    }
                    else if (!aXRequest.isReversal())
                    {
                        aXRequest.setReversal(true);
                        AXResult result = getResponse().getResult();
                        getResponse().setResult(postDepositTransfer(aXRequest, 1).getResult() != AXResult.SUCCESS ? AXResult.SUCCESS : result);
                    }
                }
            }
        }
        catch (Exception ex)
        {
            if (isRepeatable(repeatCount, ex))
            {
                return postDepositTransfer(aXRequest, ++repeatCount);
            }
            else
            {
                getResponse().setResult(processError(ex));
            }
        }
        return getResponse();
    }

    public AXResponse postDepositToGLTransfer(AXRequest aXRequest, Integer repeatCount)
    {
        try
        {
            if (AXResult.SUCCESS == checkBalance(aXRequest).getResult())
            {
                XAPIBaseTxnRequestData txnRequestData = (XAPIBaseTxnRequestData) getBaseRequest(new XAPIBaseTxnRequestData(), aXRequest);
                txnRequestData.setAcctNo(aXRequest.getAccount().getAccountNumber());
                txnRequestData.setContraAcctNo(aXRequest.getContra().getAccountNumber());

                txnRequestData.setTxnDescription(aXRequest.getNarration());
                txnRequestData.setTxnAmount(aXRequest.getAmount());
                txnRequestData.setTxnCurrencyCode(aXRequest.getCurrency().getCurrencyCode());

                txnRequestData.setTxnReference(aXRequest.getReference());
                getLog().setCall(getCallTag(aXRequest, true), txnRequestData, repeatCount > 1);
                TxnResponseOutputData responseOutputData = aXRequest.isReversal() ? getPort(TxnProcessWebServiceStub.class).postGLToDepositAccountTransfer(txnRequestData) : getPort(TxnProcessWebServiceStub.class).postDepositToGLAccountTransfer(txnRequestData);

                getLog().setCall(getCallTag(aXRequest, false), responseOutputData);
                getResponse().setResult(mapCode(responseOutputData.getResponseCode()));
                if (Objects.equals(AXConstant.XAPI_APPROVED, responseOutputData.getResponseCode()))
                {
                    getResponse().setTxnId(findTxnId(txnRequestData, responseOutputData.getRetrievalReferenceNumber()));
                    if (processCharge(aXRequest, false, 1) || aXRequest.isReversal())
                    {
                        setNewBalance(aXRequest).setResult(AXResult.SUCCESS);
                    }
                    else if (!aXRequest.isReversal())
                    {
                        aXRequest.setReversal(true);
                        AXResult result = getResponse().getResult();
                        getResponse().setResult(postDepositToGLTransfer(aXRequest, 1).getResult() != AXResult.SUCCESS ? AXResult.SUCCESS : result);
                    }
                }
            }
        }
        catch (Exception ex)
        {
            if (isRepeatable(repeatCount, ex))
            {
                return postDepositToGLTransfer(aXRequest, ++repeatCount);
            }
            else
            {
                getResponse().setResult(processError(ex));
            }
        }
        return getResponse();
    }

    public AXResponse postGLToDepositTransfer(AXRequest aXRequest, Integer repeatCount)
    {
        try
        {
            XAPIBaseTxnRequestData txnRequestData = (XAPIBaseTxnRequestData) getBaseRequest(new XAPIBaseTxnRequestData(), aXRequest);
            txnRequestData.setAcctNo(aXRequest.getAccount().getAccountNumber());
            txnRequestData.setContraAcctNo(aXRequest.getContra().getAccountNumber());

            txnRequestData.setTxnDescription(aXRequest.getNarration());
            txnRequestData.setTxnAmount(aXRequest.getAmount());
            txnRequestData.setTxnCurrencyCode(aXRequest.getCurrency().getCurrencyCode());

            txnRequestData.setTxnReference(aXRequest.getReference());
            getLog().setCall(getCallTag(aXRequest, true), txnRequestData, repeatCount > 1);
            TxnResponseOutputData responseOutputData = aXRequest.isReversal() ? getPort(TxnProcessWebServiceStub.class).postDepositToGLAccountTransfer(txnRequestData) : getPort(TxnProcessWebServiceStub.class).postGLToDepositAccountTransfer(txnRequestData);

            getLog().setCall(getCallTag(aXRequest, false), responseOutputData);
            if (Objects.equals(AXConstant.XAPI_APPROVED, responseOutputData.getResponseCode()))
            {
                getResponse().setTxnId(findTxnId(txnRequestData, responseOutputData.getRetrievalReferenceNumber()));
                processCharge(aXRequest, false, 1);
                setNewBalance(aXRequest).setResult(AXResult.SUCCESS);
            }
            getResponse().setResult(mapCode(responseOutputData.getResponseCode()));
        }
        catch (Exception ex)
        {
            if (isRepeatable(repeatCount, ex))
            {
                return postGLToDepositTransfer(aXRequest, ++repeatCount);
            }
            else
            {
                getResponse().setResult(processError(ex));
            }
        }
        return getResponse();
    }

    public AXResponse postGLToGLTransfer(AXRequest aXRequest, Integer repeatCount)
    {
        try
        {
            GLTransferRequest glTransferRequest = (GLTransferRequest) getBaseRequest(new GLTransferRequest(), aXRequest);
            glTransferRequest.setFromGLAccountNumber(aXRequest.isReversal() ? aXRequest.getContra().getAccountNumber() : aXRequest.getAccount().getAccountNumber());
            glTransferRequest.setToGLAccountNumber(aXRequest.isReversal() ? aXRequest.getAccount().getAccountNumber() : aXRequest.getContra().getAccountNumber());

            glTransferRequest.setAmount(aXRequest.getAmount());
            glTransferRequest.setTransactionAmount(aXRequest.getAmount());
            glTransferRequest.setTransactionCurrencyCode(aXRequest.getCurrency().getCurrencyCode());

            glTransferRequest.setTxnDescription(aXRequest.getNarration());
            getLog().setCall(getCallTag(aXRequest, true), glTransferRequest, repeatCount > 1);
            GLTransferOutputData gLTransferOutputData = getPort(TransactionsWebServiceStub.class).postGLtoGLXfer(glTransferRequest);

            getLog().setCall(getCallTag(aXRequest, false), gLTransferOutputData);
            if (Objects.equals(AXConstant.XAPI_APPROVED, gLTransferOutputData.getResponseCode()))
            {
                getResponse().setTxnId(findTxnId(glTransferRequest, null));
                if (!getWorker().isBlank(getResponse().getTxnId()))
                {
                    getdClient().updateTxnRefText(getResponse().getTxnId(), glTransferRequest.getReference());
                }
            }
            getResponse().setResult(mapCode(gLTransferOutputData.getResponseCode()));
        }
        catch (Exception ex)
        {
            if (isRepeatable(repeatCount, ex))
            {
                return postGLToGLTransfer(aXRequest, ++repeatCount);
            }
            else
            {
                getResponse().setResult(processError(ex));
            }
        }
        return getResponse();
    }

    public boolean processCharge(AXRequest aXRequest, boolean splitOnFly, Integer repeatCount)
    {
        try
        {
            if (aXRequest.isCharged() == aXRequest.isReversal() && BigDecimal.ZERO.compareTo(aXRequest.getCharge().getChargeAmount()) < 0)
            {
                XAPIBaseTxnRequestData txnRequestData = (XAPIBaseTxnRequestData) getBaseRequest(new XAPIBaseTxnRequestData(), aXRequest);
                txnRequestData.setAcctNo(aXRequest.getCharge().getChargeAccount().getAccountNumber());
                txnRequestData.setContraAcctNo(aXRequest.getCharge().getSplitList().isEmpty() ? aXRequest.getCharge().getChargeLedger() : aXRequest.getCharge().getChannelLedger());

                txnRequestData.setTxnDescription(aXRequest.getCharge().getChargeNarration());
                txnRequestData.setTxnAmount(aXRequest.getCharge().getChargeAmount());
                txnRequestData.setTxnCurrencyCode(aXRequest.getCurrency().getCurrencyCode());

                txnRequestData.setTxnReference(aXRequest.getReference());
                getLog().setCall(aXRequest.isReversal() ? "revtxnchrgreq" : "txnchrgreq", txnRequestData, repeatCount > 1);
                TxnResponseOutputData responseOutputData = aXRequest.isReversal() ? getPort(TxnProcessWebServiceStub.class).postGLToDepositAccountTransfer(txnRequestData) : getPort(TxnProcessWebServiceStub.class).postDepositToGLAccountTransfer(txnRequestData);

                getLog().setCall(aXRequest.isReversal() ? "revtxnchrgres" : "txnchrgres", responseOutputData);
                getResponse().setResult(mapCode(responseOutputData.getResponseCode()));
                if (Objects.equals(AXConstant.XAPI_APPROVED, responseOutputData.getResponseCode()))
                {
                    getResponse().setChargeId(findTxnId(txnRequestData, responseOutputData.getRetrievalReferenceNumber()));
                    aXRequest.setCharged(!aXRequest.isReversal());
                    processSplit(aXRequest, splitOnFly);
                    return true;
                }
            }
        }
        catch (Exception ex)
        {
            if (isRepeatable(repeatCount, ex))
            {
                return processCharge(aXRequest, splitOnFly, ++repeatCount);
            }
            else
            {
                getResponse().setResult(processError(ex));
            }
        }
        return BigDecimal.ZERO.compareTo(aXRequest.getCharge().getChargeAmount()) >= 0;
    }

    public void processSplit(AXRequest aXRequest, boolean splitOnFly)
    {
        try
        {
            aXRequest.getCharge().getSplitList().stream().filter((split) -> (BigDecimal.ZERO.compareTo(split.getAmount()) < 0)).forEach((split)
                    ->
            {
                AXSplit aXSplit = new AXSplit();
                aXSplit.setTxnRef(aXRequest.getReference());
                aXSplit.setChannel(aXRequest.getChannel());

                aXSplit.setAmount(split.getAmount());
                aXSplit.setCurrency(aXRequest.getCurrency().getCurrencyCode());
                aXSplit.setDescription(split.getDescription());

                aXSplit.setDebitAccount(aXRequest.isReversal() ? split.getAccount() : aXRequest.getCharge().getChannelLedger());
                aXSplit.setCreditAccount(aXRequest.isReversal() ? aXRequest.getCharge().getChannelLedger() : split.getAccount());
                aXSplit.setReversal(aXRequest.isReversal() ? "Y" : "N");

                try
                {
                    if (getWorker().isLedger(split.getAccount()))
                    {
                        aXSplit.setStatus(splitOnFly ? (splitLedger(aXRequest, split, 1) ? "S" : "F") : "P");
                    }
                    else
                    {
                        aXSplit.setStatus(splitOnFly ? (splitDeposit(aXRequest, split, 1) ? "S" : "F") : "P");
                    }
                }
                catch (Exception ex)
                {
                    getLog().logError(ex);
                }
                getdClient().saveSplit(aXSplit);
            });
        }
        catch (Exception ex)
        {
            getLog().logError(ex);
        }
    }

    public boolean postSplit(AXRequest aXRequest, GPSplit split)
    {
        try
        {
            boolean debitLedger = getWorker().isLedger(split.getDebitAccount());
            boolean creditLedger = getWorker().isLedger(split.getCreditAccount());
            if (debitLedger && creditLedger)
            {
                GLTransferRequest glTransferRequest = (GLTransferRequest) getBaseRequest(new GLTransferRequest(), aXRequest);
                glTransferRequest.setFromGLAccountNumber(split.getDebitAccount());
                glTransferRequest.setToGLAccountNumber(split.getCreditAccount());
                glTransferRequest.setAmount(aXRequest.getAmount());

                glTransferRequest.setTransactionAmount(split.getAmount());
                glTransferRequest.setTransactionCurrencyCode(split.getCurrency());
                glTransferRequest.setTxnDescription(split.getDescription());
                getLog().setCall(aXRequest.isReversal() ? "revchrgspltreq" : "chrgspltreq", glTransferRequest);

                GLTransferOutputData gLTransferOutputData = getPort(TransactionsWebServiceStub.class).postGLtoGLXfer(glTransferRequest);
                getLog().setCall(aXRequest.isReversal() ? "revchrgspltres" : "chrgspltres", gLTransferOutputData);
                return Objects.equals(AXConstant.XAPI_APPROVED, gLTransferOutputData.getResponseCode());
            }
            else
            {
                XAPIBaseTxnRequestData requestData = (XAPIBaseTxnRequestData) getBaseRequest(new XAPIBaseTxnRequestData(), aXRequest);
                requestData.setAcctNo(debitLedger ? split.getCreditAccount() : split.getDebitAccount());
                requestData.setContraAcctNo(debitLedger ? split.getDebitAccount() : split.getCreditAccount());

                requestData.setTxnDescription(split.getDescription());
                requestData.setTxnAmount(split.getAmount());
                requestData.setTxnCurrencyCode(split.getCurrency());
                requestData.setTxnReference(checkReference(aXRequest.getReference()));

                getLog().setCall(Objects.equals(split.getReversal(), "Y") ? "revchrgspltreq" : "chrgspltreq", requestData);
                TxnResponseOutputData txnResponseOutputData = debitLedger ? getPort(TxnProcessWebServiceStub.class).postGLToDepositAccountTransfer(requestData) : getPort(TxnProcessWebServiceStub.class).postDepositToGLAccountTransfer(requestData);
                getLog().setCall(Objects.equals(split.getReversal(), "Y") ? "revchrgspltres" : "chrgspltres", txnResponseOutputData);
                return Objects.equals(AXConstant.XAPI_APPROVED, txnResponseOutputData.getResponseCode());
            }
        }
        catch (Exception ex)
        {
            getLog().logError(ex);
        }
        return false;
    }

    private boolean splitDeposit(AXRequest aXRequest, TCSplit split, Integer repeatCount)
    {
        try
        {
            XAPIBaseTxnRequestData requestData = (XAPIBaseTxnRequestData) getBaseRequest(new XAPIBaseTxnRequestData(), aXRequest);
            requestData.setAcctNo(split.getAccount());
            requestData.setContraAcctNo(aXRequest.getCharge().getChannelLedger());

            requestData.setTxnDescription(split.getDescription());
            requestData.setTxnAmount(split.getAmount());
            requestData.setTxnCurrencyCode(aXRequest.getCurrency().getCurrencyCode());

            requestData.setTxnReference(aXRequest.getReference() + split.getReference());
            getLog().setCall(aXRequest.isReversal() ? "revchrgspltreq" : "chrgspltreq", requestData, repeatCount > 1);
            TxnResponseOutputData txnResponseOutputData = aXRequest.isReversal() ? getPort(TxnProcessWebServiceStub.class).postDepositToGLAccountTransfer(requestData) : getPort(TxnProcessWebServiceStub.class).postGLToDepositAccountTransfer(requestData);

            getLog().setCall(aXRequest.isReversal() ? "revchrgspltres" : "chrgspltres", txnResponseOutputData);
            return Objects.equals(AXConstant.XAPI_APPROVED, txnResponseOutputData.getResponseCode());
        }
        catch (Exception ex)
        {
            if (isRepeatable(repeatCount, ex))
            {
                return splitDeposit(aXRequest, split, ++repeatCount);
            }
            else
            {
                getLog().logError(ex);
            }
        }
        return false;
    }

    private boolean splitLedger(AXRequest aXRequest, TCSplit split, Integer repeatCount)
    {
        try
        {
            GLTransferRequest glTransferRequest = (GLTransferRequest) getBaseRequest(new GLTransferRequest(), aXRequest);
            glTransferRequest.setFromGLAccountNumber(aXRequest.isReversal() ? split.getAccount() : aXRequest.getCharge().getChannelLedger());
            glTransferRequest.setToGLAccountNumber(aXRequest.isReversal() ? aXRequest.getCharge().getChannelLedger() : split.getAccount());

            glTransferRequest.setAmount(aXRequest.getAmount());
            glTransferRequest.setTransactionAmount(split.getAmount());
            glTransferRequest.setTransactionCurrencyCode(aXRequest.getCurrency().getCurrencyCode());

            glTransferRequest.setTxnDescription(split.getDescription());
            getLog().setCall(aXRequest.isReversal() ? "revchrgspltreq" : "chrgspltreq", glTransferRequest, repeatCount > 1);
            GLTransferOutputData gLTransferOutputData = getPort(TransactionsWebServiceStub.class).postGLtoGLXfer(glTransferRequest);

            getLog().setCall(aXRequest.isReversal() ? "revchrgspltres" : "chrgspltres", gLTransferOutputData);
            return Objects.equals(AXConstant.XAPI_APPROVED, gLTransferOutputData.getResponseCode());
        }
        catch (Exception ex)
        {
            if (isRepeatable(repeatCount, ex))
            {
                return splitLedger(aXRequest, split, ++repeatCount);
            }
            else
            {
                getLog().logError(ex);
            }
        }
        return false;
    }

    public AXResponse setNewBalance(AXRequest aXRequest, boolean validating)
    {
        try
        {
            if (aXRequest.isSetBalance())
            {
                AccountBalanceRequest accountBalanceRequest = (AccountBalanceRequest) getBaseRequest(new AccountBalanceRequest(), aXRequest);
                accountBalanceRequest.setAccountNumber((validating && aXRequest.isInverted()) ? aXRequest.getContra().getAccountNumber() : aXRequest.getAccount().getAccountNumber());
                getLog().setCall("acctbalreq", accountBalanceRequest);

                AccountBalanceOutputData accountBalanceOutputData = getPort(AccountWebServiceStub.class).findAccountBalance(accountBalanceRequest);
                getLog().setCall("acctbalres", accountBalanceOutputData);
                getResponse().setBalance(accountBalanceOutputData.getAvailableBalance());
            }
            if (validating)
            {
                getResponse().setResult(AXResult.SUCCESS);
            }
        }
        catch (Exception ex)
        {
            if (validating)
            {
                getResponse().setResult(processError(ex));
            }
            else
            {
                getLog().logError(ex);
            }
        }
        return getResponse();
    }

//    public boolean approveWorkFlowItem(AXRequest aXRequest, Long custId, Long eventId, Integer repeatCount)
//    {
//        try
//        {
//            WFViewRequestData wFViewRequestData = (WFViewRequestData) getBaseRequest(new WFViewRequestData(), aXRequest);
//            wFViewRequestData.setWorkItemId(getdClient().queryWorkflowItemId(custId));
//            wFViewRequestData.setEventId(eventId);
//
//            getLog().setCall("apprvwfitemreq", wFViewRequestData);
//            getPort(WorkflowWebService.class).saveData(wFViewRequestData);
//            return true;
//        }
//        catch (Exception ex)
//        {
//            if (isRepeatable(repeatCount, ex))
//            {
//                return approveWorkFlowItem(aXRequest, custId, eventId, ++repeatCount);
//            }
//            else
//            {
//                processError(ex);
//            }
//        }
//        return false;
//    }

    public AXResponse checkBalance(AXRequest aXRequest)
    {
        getResponse().setResult(AXResult.SUCCESS);
        BigDecimal netAmount = aXRequest.getAmount().add(aXRequest.getCharge().getChargeAmount());
        if (!aXRequest.isReversal() && BigDecimal.ZERO.compareTo(netAmount) < 0)
        {
            if (setNewBalance(aXRequest, true).getResult() == AXResult.SUCCESS && BigDecimal.ZERO.compareTo(getResponse().getBalance()) >= 0 || getResponse().getBalance().compareTo(netAmount) < 0)
            {
                getResponse().setResult(AXResult.INSUFFICIENT_FUNDS);
            }
        }
        return getResponse();
    }

    private AXResult processError(Exception ex)
    {
        getLog().logError(ex);
        return mapCode(getWorker().errorCode(ex));
    }

    private AXResult mapCode(String code)
    {
        getResponse().setXapiCode(code);
        AXResult result = !Objects.equals(AXConstant.XAPI_APPROVED, code) ? AXResult.valueOf(APController.getRcodes().getProperty(code, AXResult.FAILED.name())) : AXResult.SUCCESS;
        return result == AXResult.SUCCESS && !Objects.equals(AXConstant.XAPI_APPROVED, code) ? AXResult.FAILED : result;
    }

    public Long findTxnId(XAPIRequestBaseObject requestBaseObject, String retrievalReference)
    {
        Long txnId = getWorker().extractXmlValue(retrievalReference, "TxnId", false, Long.class);
        return getWorker().isBlank(txnId) ? getdClient().queryTxnId(requestBaseObject.getChannelId(), requestBaseObject.getCardNumber(), requestBaseObject.getReference()) : txnId;
    }

    public AXResponse setNewBalance(AXRequest aXRequest)
    {
        return setNewBalance(aXRequest, false);
    }

    private boolean isRepeatable(Integer repeatCount, Exception ex)
    {
        return repeatCount < ALController.retryCount && String.valueOf(ex.getMessage()).contains("OptimisticConcurrencyException");
    }

    private String getCallTag(AXRequest aXRequest, boolean request)
    {
        return (aXRequest.isReversal() ? "rev" : "") + aXRequest.getTxnType().name().toLowerCase() + (request ? "req" : "res");
    }

    private String checkReference(String reference)
    {
        return String.valueOf(reference).length() > 30 ? reference.substring(reference.length() - 30) : reference;
    }

    public void dispose()
    {
        getBox().dispose();
    }

    /**
     * @return the response
     */
    public AXResponse getResponse()
    {
        return response;
    }

    /**
     * @param tXResponse the response to set
     */
    public void setResponse(AXResponse tXResponse)
    {
        this.response = tXResponse;
    }

    /**
     * @return the log
     */
    public APLog getLog()
    {
        return getBox().getLog();
    }

    /**
     * @return the worker
     */
    public AXWorker getWorker()
    {
        return getBox().getWorker();
    }

    /**
     * @return the dClient
     */
    public DBClient getdClient()
    {
        return getBox().getdClient();
    }

    /**
     * @return the box
     */
    public ATBox getBox()
    {
        return box;
    }

    /**
     * @param box the box to set
     */
    public void setBox(ATBox box)
    {
        this.box = box;
    }
}
