/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Ruby.sms;

import Ruby.APController;
import Ruby.APMain;
import Ruby.DBClient;
import Ruby.acx.ATBox;
import Ruby.acx.AXCaller;
import Ruby.acx.AXClient;
import Ruby.acx.AXCrypt;
import Ruby.acx.AXResult;
import Ruby.acx.AXWorker;
import Ruby.acx.TRItem;
import Ruby.model.ALHeader;
import Ruby.model.AXRequest;
import Ruby.model.CNAccount;
import Ruby.model.CNCustomer;
import Ruby.model.MXAlert;
import Ruby.model.MXMessage;
import Ruby.model.TCSplit;
import static Ruby.sms.ALProcessor.getdClient;
import com.mashape.unirest.http.Unirest;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.net.ssl.SSLContext;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContexts;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.json.JSONObject;

/**
 *
 * @author Pecherk
 */
public class ALWorker extends Thread
{

    private final MXAlert mXAlert;
    private final ATBox box = new ATBox(APMain.smsLog);
    private long startTime = System.currentTimeMillis();

    public ALWorker(MXAlert mXAlert)
    {
        this.mXAlert = mXAlert;
    }

    @Override
    public void run()
    {
        String alertCode = mXAlert.getAlertCode();        
        try
        {
            ALProcessor.runningAlerts.put(alertCode, new Date());
            APMain.smsLog.logEvent("===<" + mXAlert.getAlertCode() + ", " + mXAlert.getStatus() + ">===");

            if (!"R".equals(mXAlert.getStatus()) && !Objects.equals(mXAlert.getFilterBy(), "MN"))
            {
                getdClient().loadPendingAlerts(mXAlert);
            }

            updateAlert("R");

            if (executeAlert())
            {
                pushNextDate(mXAlert);
            }
            else
            {
                updateAlert("A");
            }
        }
        catch (Exception ex)
        {
            APMain.smsLog.logEvent(ex);
        }
        try
        {
            ALProcessor.runningAlerts.remove(alertCode);
        }
        catch (Exception ex)
        {
            APMain.smsLog.logEvent(ex);
        }
        dispose();
    }

    private void updateAlert(String status)
    {
        mXAlert.setStatus(status);
        getdClient().updateAlertStatus(mXAlert);
    }

    private boolean executeAlert()
    {
        int sentItems = 0;
        boolean empty = true;
        try
        {
            if (Objects.equals(mXAlert.getFilterBy(), "MN"))
            {
                for (String list : mXAlert.getFilters())
                {
                    try
                    {
                        for (String msisdn : list.split(","))
                        {
                            empty = false;
                            startTime = System.currentTimeMillis();

                            setNewCaller();
                            MXMessage mXRequest = new MXMessage();
                            mXRequest.setAlertId(String.valueOf(System.currentTimeMillis()));
                            mXRequest.setAlertCode(mXAlert.getAlertCode());

                            mXRequest.setAlertType(mXAlert.getAlertType());
                            mXRequest.setMsisdn(msisdn);
                            if (processRequest(mXAlert, mXRequest))
                            {
                                sentItems++;
                            }
                        }
                    }
                    catch (Exception ex)
                    {
                        APMain.smsLog.logEvent(ex);
                    }
                }
            }
            else
            {
                ArrayList<MXMessage> pendingAlerts = getdClient().queryPendingAlerts(mXAlert);
                APMain.smsLog.logEvent("~~~<" + mXAlert.getAlertCode() + " = " + pendingAlerts.size() + ">~~~");
                for (MXMessage message : pendingAlerts)
                {
                    try
                    {
                        empty = false;
                        BigDecimal limit = BigDecimal.ZERO;
                        CNAccount chargeAccount = getdClient().queryDepositAccount(message.getChargeAcct());

                        setNewCaller();
                        AXRequest aXRequest = new AXRequest();

                        aXRequest.setUserRole(ALController.getSystemRole());
                        aXRequest.setReference(message.getAlertId());
                        aXRequest.setChannelId(ALController.channelId);

                        aXRequest.setChannel(ALController.channel);
                        aXRequest.setAccount(chargeAccount);

                        aXRequest.getCharge().setScheme(ALController.chargeScheme);
                        aXRequest.setTxnCode(mXAlert.getChargeCode());

                        aXRequest.setMsisdn(message.getMsisdn());
                        aXRequest.setCurrency(aXRequest.getAccount().getCurrency());

                        aXRequest.setBranch(aXRequest.getAccount().getBranch());
                        getWorker().setCharge(aXRequest, new HashMap<>());

                        getCaller().setAlertCode(message.getAlertCode());
                        getCaller().setReference(message.getAlertId());
                        getCaller().setNarration(message.getDescription());

                        if ("CR".equals(mXAlert.getAlertType()) || "DR".equals(mXAlert.getAlertType()) || "CV".equals(mXAlert.getAlertType()) || "DV".equals(mXAlert.getAlertType()))
                        {
                            BigDecimal minimumAmount = aXRequest.getCharge().getChargeAmount().add(BigDecimal.ONE);
                            limit = getWorker().convertToType((getdClient().queryCustomField("CR".equals(mXAlert.getAlertType()) || "CV".equals(mXAlert.getAlertType()) ? ALController.creditLimitFieldId : ALController.debitLimitFieldId, getdClient().queryDepositAccount(message.getAcctNo()).getAcctId()).getFieldValue()), BigDecimal.class, minimumAmount);
                            limit = minimumAmount.compareTo(limit) >= 0 ? minimumAmount : limit;
                        }
                        if (BigDecimal.ZERO.compareTo(limit) < 0 && (limit.compareTo(message.getTxnAmt()) > 0 || message.getChargeAmount().compareTo(message.getTxnAmt()) >= 0))
                        {
                            message.setStatus("L");
                        }
                        else
                        {
                            startTime = System.currentTimeMillis();
                            ArrayList<CNCustomer> members = new ArrayList<>();
                            if ("GRP".equalsIgnoreCase(chargeAccount.getCustCat()))
                            {
                                members = getdClient().queryGroupMembers(message.getCustId());
                                aXRequest.getCharge().setChargeAmount(aXRequest.getCharge().getChargeAmount().multiply(new BigDecimal(members.size())));
                                for (TCSplit split : aXRequest.getCharge().getSplitList())
                                {
                                    split.setAmount(split.getAmount().multiply(new BigDecimal(members.size())));
                                }
                            }
                            getCaller().setRequest(aXRequest);
                            boolean billable = message.getChargeId() == 0 || getWorker().isBlank(message.getChargeId());
                            if (!billable || getxClient().processCharge(aXRequest, false, 1))
                            {
                                if (billable)
                                {
                                    message.setChargeId(getxClient().getResponse().getChargeId());
                                }
                                if ("GRP".equalsIgnoreCase(chargeAccount.getCustCat()))
                                {
                                    int sentCount = 0;
                                    message.setMsisdn("GROUP");
                                    sentCount = members.stream().map((cNCustomer)
                                            ->
                                    {
                                        setNewCaller();
                                        getCaller().setRequest(aXRequest);
                                        MXMessage mxRequest = getWorker().cloneObject(message, MXMessage.class);

                                        mxRequest.setCustId(cNCustomer.getCustId());
                                        mxRequest.setCustName(cNCustomer.getCustName());
                                        mxRequest.setMsisdn(cNCustomer.getMobileNumber());
                                        return mxRequest;
                                    }).filter((mxRequest) -> (processRequest(mXAlert, mxRequest))).map((mxRequest)
                                            ->
                                    {
                                        return mxRequest;
                                    }).map((_item) -> 1).reduce(sentCount, Integer::sum);

                                    members.clear();

                                    if (sentCount > 0)
                                    {
                                        logEvent(message);
                                        sentItems++;
                                    }

                                    sentItems = sentCount > 0 ? sentItems + 1 : sentItems;
                                    message.setStatus(sentCount > 0 ? "S" : "F");
                                }
                                else if (processRequest(mXAlert, message))
                                {
                                    sentItems++;
                                }
                            }
                            else if (billable && AXResult.INSUFFICIENT_FUNDS == getxClient().getResponse().getResult())
                            {
                                message.setStatus("I");
                            }
                            else
                            {
                                message.setStatus("C");
                            }
                        }
                    }
                    catch (Exception ex)
                    {
                        APMain.smsLog.logEvent(ex);
                    }
                    getdClient().updateAlert(message);
                    logEvent(message);
                }

                pendingAlerts.clear();
            }
        }
        catch (Exception ex)
        {
            APMain.smsLog.logEvent(ex);
        }
        return empty || sentItems > 0;
    }

    public boolean processRequest(MXAlert alert, MXMessage message)
    {
        try
        {
            message.setDescription(alert.getDescription());
            message.setMsisdn(getWorker().formatMsisdn(message.getMsisdn()));
            if (message.getMsisdn() != null && (!getWorker().isYes(ALController.restricted) || ALController.recipients.contains(message.getMsisdn())))
            {
                String langCode = APController.defaultLanguage;
                try
                {
                    langCode = APController.getLanguages().get(Long.parseLong(getdClient().queryCustomField(APController.languageFieldId, message.getCustId()).getFieldValue())).getItemCode();
                }
                catch (Exception ex)
                {
                    ex = null;
                }
                if ("LA".equals(alert.getAlertType()) || "LD".equals(alert.getAlertType()) || "GR".equals(alert.getAlertType()))
                {
                    message.setLoanDetail(getdClient().queryLoanDetail(getdClient().queryLoanAccount(message.getAcctNo())));
                }

                String template = alert.getTemplates().get(getWorker().checkBlank(langCode, APController.defaultLanguage));
                if (!getWorker().isBlank(template))
                {
                    message.setMsisdn(message.getMsisdn());
                    message.setMessage(replaceMasks(message, template));

                    AXResult result = sendMessage(message, getCaller());
                    message.setStatus(result == AXResult.SUCCESS ? "S" : (result == AXResult.DUPLICATE ? "D" : "F"));

                    if ("PC".equals(alert.getAlertType()))
                    {
                        message.setMessage(template.replaceAll("\\{SECCODE\\}", "XXXX"));
                        message.setMessage(template.replaceAll("\\{PIN\\}", "XXXX"));
                        message.setMessage(message.getMessage());
                    }
                }
                else
                {
                    message.setStatus("T");
                }
            }
            else
            {
                message.setStatus("R");
            }
        }
        catch (Exception ex)
        {
            getCaller().logError(ex);
        }
        try
        {
            setResult(message);
            getdClient().saveMessage(message);
            setTreeItem(message);
        }
        catch (Exception ex)
        {
            getCaller().logError(ex);
        }
        return Objects.equals(message.getStatus(), "S");
    }

    private void setTreeItem(MXMessage message)
    {
        if (APMain.apFrame != null && Objects.equals(message.getStatus(), "S"))
        {
            APMain.apFrame.showSmsSignal(message.getAlertType(), true);
            APMain.apFrame.showSmsSignal(message.getAlertType(), false);
        }

        TRItem treeItem = new TRItem();
        treeItem.setCode(message.getAlertType());
        treeItem.setApproved(Objects.equals(message.getStatus(), "S"));

        treeItem.setText(message.getAlertId() + "~" + message.getMsisdn());
        getWorker().extractFields(message, treeItem.getRequest());
        getWorker().extractFields(message, treeItem.getResponse());

        treeItem.getDetail().put(ALHeader.AlertId, message.getAlertId());
        treeItem.getDetail().put(ALHeader.CreateDt, message.getAlertDate());
        treeItem.getDetail().put(ALHeader.AlertCode, message.getAlertCode());

        treeItem.getDetail().put(ALHeader.AlertType, message.getAlertType());
        treeItem.getDetail().put(ALHeader.Msisdn, message.getMsisdn());
        treeItem.getDetail().put(ALHeader.Description, message.getDescription());

        treeItem.getDetail().put(ALHeader.TxnId, message.getTxnId());
        treeItem.getDetail().put(ALHeader.CustId, message.getCustId());
        treeItem.getDetail().put(ALHeader.CustName, message.getCustName());

        treeItem.getDetail().put(ALHeader.Account, message.getAcctNo());
        treeItem.getDetail().put(ALHeader.Contra, message.getContra());
        treeItem.getDetail().put(ALHeader.ChargeAcct, message.getChargeAcct());

        treeItem.getDetail().put(ALHeader.TxnDate, message.getTxnDate());
        treeItem.getDetail().put(ALHeader.Currency, message.getCurrency());
        treeItem.getDetail().put(ALHeader.Amount, message.getTxnAmt());

        treeItem.getDetail().put(ALHeader.Charge, message.getTxnChg());
        treeItem.getDetail().put(ALHeader.ChgId, message.getChargeId());
        treeItem.getDetail().put(ALHeader.SchemeId, message.getSchemeId());

        treeItem.getDetail().put(ALHeader.AccessCode, message.getAccessCode());
        treeItem.getDetail().put(ALHeader.Balance, message.getClrdBal());
        treeItem.getDetail().put(ALHeader.Message, message.getMessage());

        treeItem.getDetail().put(ALHeader.Result, message.getResult());
        treeItem.getDetail().put(ALHeader.RecSt, message.getStatus());
        ALController.addTreeItem(treeItem);
    }

    public void allowAllHosts(AXCaller caller)
    {
        try
        {
            SSLContext sslcontext = SSLContexts.custom()
                    .loadTrustMaterial(null, new TrustSelfSignedStrategy())
                    .build();

            SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslcontext, SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
            CloseableHttpClient httpclient = HttpClients.custom()
                    .setSSLSocketFactory(sslsf)
                    .build();
            Unirest.setHttpClient(httpclient);
        }
        catch (Exception ex)
        {
            caller.logError(ex);
        }
    }

    public AXResult sendMessage(MXMessage message, AXCaller caller)
    {
        try
        {
            allowAllHosts(caller);
            JSONObject request = new JSONObject();
            request.put("to", message.getMsisdn());
            request.put("message", message.getMessage());
            request.put("from", ALController.senderId);
            request.put("transactionID", message.getAlertId());
            request.put("clientid", ALController.clientId);
            request.put("username", ALController.user);
            request.put("password", ALController.password);

            JSONObject response = Unirest.post(ALController.sendSmsURL).header("Content-Type", "application/json").header("Cache-Control", "no-cache").body(request.toString()).asJson().getBody().getObject();

//            JSONObject response = Unirest.post(ALController.sendSmsURL).header("Content-Type", "application/json").header("Cache-Control", "no-cache").header("Accept", "application/json")
//                    .queryString("to", message.getMsisdn())
//                    .queryString("message", message.getMessage())
//                    .queryString("from", ALController.senderId)
//                    .queryString("transactionID", message.getAlertId())
//                    .queryString("clientid", ALController.clientId)
//                    .queryString("username", ALController.user)
//                    .queryString("password", ALController.password)
//                    .asJson().getBody().getObject();
//            caller.setCall("sndsms", request.toString() + " = " + response.toString());
            // return Objects.equals(response.getInteger("ErrorCode"), 0) ? AXResult.SUCCESS : AXResult.FAILED;
            return Objects.equals(response.getString("ResultCode"), "0") ? AXResult.SUCCESS : AXResult.FAILED;
        }
        catch (Exception ex)
        {
            caller.logError(ex);
        }
        return AXResult.FAILED;
    }

    public void pushNextDate(MXAlert mXAlert)
    {
        LocalDate nextDate = getWorker().getProcessingDate();
        mXAlert.setPreviousDate(getdClient().getProcessingDate());

        if ("R".equals(mXAlert.getStatus()))
        {
            mXAlert.setStatus("A");
        }

        if ("BR".equalsIgnoreCase(mXAlert.getAlertType()) && "Real-Time".equalsIgnoreCase(mXAlert.getFrequency()))
        {
            mXAlert.setFrequency("Once");
        }

        switch (mXAlert.getFrequency())
        {
            case "Once":
                mXAlert.setStatus("C");
                break;
            case "Real-Time":
                mXAlert.setNextDate(getdClient().getProcessingDate());
                break;
            case "Daily":
                mXAlert.setNextDate(getWorker().toDate(nextDate.plusDays(1L)));
                break;
            case "Weekly":
                mXAlert.setNextDate(getWorker().toDate(nextDate.plusWeeks(1L)));
                break;
            case "Monthly":
                mXAlert.setNextDate(getWorker().toDate(nextDate.plusMonths(1L)));
                break;
            case "Annually":
                mXAlert.setNextDate(getWorker().toDate(nextDate.plusYears(1L)));
                break;
            default:
                break;
        }
        getdClient().pushAlertDate(mXAlert);
    }

    public String replaceMasks(MXMessage mXMessage, String message)
    {
        if (!getWorker().isBlank(message))
        {
            ArrayList<String> holdersList = getWorker().extractPlaceHolders(message);
            for (String holder : holdersList)
            {
                String replacement = "<>";
                switch (holder.toUpperCase())
                {
                    case "{ACCOUNT}":
                        replacement = getWorker().checkBlank(getWorker().protectField(mXMessage.getAcctNo(), 4, 5), "<>");
                        break;
                    case "{CONTRA}":
                        replacement = getWorker().checkBlank(getWorker().protectField(mXMessage.getContra(), 4, 5), "<>");
                        break;
                    case "{NAME}":
                        replacement = getWorker().checkBlank(getWorker().truncateName(mXMessage.getCustName(), 2), "<>");
                        break;
                    case "{DATE}":
                        replacement = getWorker().checkBlank(getWorker().formatDisplayDate(mXMessage.getTxnDate()), "<>");
                        break;
                    case "{CURRENCY}":
                        replacement = getWorker().checkBlank(mXMessage.getCurrency(), "<>");
                        break;
                    case "{AMOUNT}":
                        replacement = getWorker().formatAmount(mXMessage.getTxnAmt());
                        break;
                    case "{CHARGE}":
                        replacement = getWorker().formatAmount(mXMessage.getTxnChg());
                        break;
                    case "{DESCRIPTION}":
                        replacement = getWorker().checkBlank(mXMessage.getTxnDesc(), "<>");
                        break;
                    case "{BALANCE}":
                        replacement = getWorker().formatAmount(mXMessage.getClrdBal());
                        break;
                    case "{DUEDATE}":
                        replacement = getWorker().checkBlank(getWorker().formatDisplayDate(mXMessage.getLoanDetail().getPaymentDueDate()), "<>");
                        break;
                    case "{PRODDESC}":
                        replacement = getWorker().checkBlank(mXMessage.getProdDesc(), "<>");
                        break;
                    case "{REPAYAMT}":
                        replacement = getWorker().formatAmount(mXMessage.getLoanDetail().getRepaymentAmount());
                        break;
                    case "{ARREARS}":
                        replacement = getWorker().formatAmount(mXMessage.getLoanDetail().getUnpaidAmount());
                        break;
                    case "{MATURITY}":
                        replacement = getWorker().checkBlank(getWorker().formatDisplayDate(mXMessage.getMaturityDate()), "<>");
                        break;
                    case "{STARTDATE}":
                        replacement = getWorker().checkBlank(getWorker().formatDisplayDate(mXMessage.getStartDate()), "<>");
                        break;
                    case "{TENOR}":
                        replacement = getWorker().checkBlank(mXMessage.getTenor(), "<>");
                        break;
                    case "{RATE}":
                        replacement = getWorker().formatAmount(mXMessage.getRate());
                        break;
                    case "{ORIGINATOR}":
                        replacement = getWorker().checkBlank(getWorker().truncateName(mXMessage.getOriginator(), 2), getWorker().truncateName(mXMessage.getCustName(), 2));
                        break;
                    case "{GUARANTOR}":
                        replacement = getWorker().checkBlank(getWorker().truncateName(mXMessage.getGuarantor(), 2), "<>");
                        break;
                }
                message = message.replaceAll(holder.replace("{", "\\{").replace("}", "\\}"), getWorker().cleanField(replacement, false));
            }
            holdersList.clear();
        }
        return message;
    }

    private void setResult(MXMessage message)
    {
        switch (message.getStatus())
        {
            case "F":
                message.setResult("Failed");
                break;
            case "I":
                message.setResult("Insufficient Funds");
                break;
            case "L":
                message.setResult("Below Limit");
                break;
            case "R":
                message.setResult("Rejected");
                break;
            case "S":
                message.setResult("Sent");
                break;
            case "T":
                message.setResult("Invalid Template");
                break;
        }
    }

    private void logEvent(MXMessage message)
    {
        if (!"L".equals(message.getStatus()) && !"R".equals(message.getStatus()))
        {
            getCaller().setCall("message", message);
            getCaller().setCall("duration", (System.currentTimeMillis() - startTime) + " Ms");
            APMain.smsLog.logEvent(getCaller());
        }
    }

    private void setNewCaller()
    {
        getBox().setLog(new AXCaller("alert"));
    }

    public void dispose()
    {
        getBox().dispose();
    }

    /**
     * @return the tDClient
     */
    public DBClient getdClient()
    {
        return getBox().getdClient();
    }

    public AXWorker getWorker()
    {
        return getBox().getWorker();
    }

    /**
     * @return the mXClient
     */
    public AXClient getxClient()
    {
        return getBox().getXclient();
    }

    /**
     * @return the caller
     */
    public AXCaller getCaller()
    {
        return getBox().getLog(AXCaller.class);
    }

    /**
     * @return the crypt
     */
    public AXCrypt getCrypt()
    {
        return getBox().getXcrypt();
    }

    /**
     * @return the box
     */
    public ATBox getBox()
    {
        return box;
    }
}
