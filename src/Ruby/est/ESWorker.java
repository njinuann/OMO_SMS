
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Ruby.est;

import Ruby.APController;
import Ruby.APMain;
import Ruby.DBClient;
import Ruby.acx.APLog;
import Ruby.acx.ATBox;
import Ruby.acx.AXCaller;
import Ruby.acx.AXClient;
import Ruby.acx.AXConstant;
import Ruby.acx.AXFile;
import Ruby.acx.AXResult;
import Ruby.acx.AXWorker;
import Ruby.acx.TRItem;
import Ruby.enu.TXType;
import Ruby.model.ALHeader;
import Ruby.model.AXRequest;
import Ruby.model.CNAccount;
import Ruby.model.ESRecord;
import Ruby.model.ESTask;
import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfEncryptor;
import com.itextpdf.text.pdf.PdfGState;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import com.itextpdf.text.pdf.PdfWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.time.DayOfWeek;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Objects;
import java.util.Properties;
import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.query.JRQueryExecuterFactory;
import net.sf.jasperreports.engine.util.JRProperties;
import net.sf.jasperreports.engine.xml.JRXmlLoader;

/**
 *
 * @author Pecherk
 */
public class ESWorker extends Thread
{

    private long time = 0L;
    private final ESTask task;
    private final String workDir;
    private final String mailBody;

    private Date runDate = new Date();
    private static final String tag = "estmt";
    private ATBox box = new ATBox(APMain.estLog);

    public ESWorker(ESTask task, String mailBody)
    {
        this.task = task;
        this.mailBody = mailBody;
        this.workDir = "est/work/" + task.getCode();
    }

    @Override
    public void run()
    {
        try
        {
            prepare();
            if (executeTask())
            {
                pushTask();
            }
            windup();
        }
        catch (Exception ex)
        {
            APMain.estLog.logEvent(ex);
        }
    }

    private boolean executeTask()
    {
        int sentItems = 0;
        Date startDate, minDate, endDate = getRunDate();
        switch (getTask().getCycle())
        {
            case "D":
                minDate = getWorker().toDate(getWorker().systemDateMinusDays(1));
                break;
            case "W":
                minDate = getWorker().toDate(getWorker().systemDateMinusWeeks(1));
                break;
            case "M":
                minDate = getWorker().toDate(getWorker().systemDateMinusMonths(1));
                break;
            case "Q":
                minDate = getWorker().toDate(getWorker().systemDateMinusMonths(3));
                break;
            default:
                minDate = getWorker().toDate(getWorker().systemDateMinusYears(1));
                break;
        }
        ArrayList<CNAccount> accounts = Objects.equals(getTask().getCycle(), "O") ? getClient().queryNowAccounts() : getClient().queryCycleAccounts(getTask());
        APMain.estLog.logDebug("------<" + getTask().getCode() + ">=<" + accounts.size() + ">------");
        switch (getTask().getRange())
        {
            case "D":
                startDate = getWorker().toDate(getWorker().systemDateMinusDays(1));
                break;
            case "W":
                startDate = getWorker().toDate(getWorker().systemDateMinusWeeks(1));
                break;
            case "M":
                startDate = getWorker().toDate(getWorker().systemDateMinusMonths(1));
                break;
            case "Q":
                startDate = getWorker().toDate(getWorker().systemDateMinusMonths(3));
                break;
            default:
                startDate = getWorker().toDate(getWorker().systemDateMinusYears(1));
                break;
        }
        for (CNAccount account : accounts)
        {
            if (checkExit())
            {
                return false;
            }
            if (ESController.isWhiteListed(account.getAccountNumber()) && (Objects.equals(getTask().getCycle(), "O") || getClient().isDue(account.getAcctId(), ESController.sentDateFieldId, minDate)))
            {
                sentItems += processRequest(account, Objects.equals(getTask().getCycle(), "O") ? getClient().queryCustomDate(ESController.startDateFieldId, account.getAcctId(), startDate) : startDate, Objects.equals(getTask().getCycle(), "O") ? getClient().queryCustomDate(ESController.endDateFieldId, account.getAcctId(), endDate) : endDate) ? 1 : 0;
            }
        }
        return accounts.isEmpty() || sentItems > 0;
    }

    private boolean processRequest(CNAccount account, Date startDate, Date endDate)
    {
        ESRecord record = new ESRecord();
        Long startTime = System.currentTimeMillis();
        try
        {
            ArrayList<String> contacts = getWorker().createArrayList(getClient().queryCustomField(ESController.addressFieldId, account.getAcctId(), null));
            if (contacts.isEmpty())
            {
                contacts = getClient().queryContacts(account.getCustId(), "EMAIL");
            }
            if (!contacts.isEmpty())
            {
                try
                {
                    setLog(new AXCaller(tag));
                    AXRequest aXRequest = new AXRequest();
                    aXRequest.setAccount(account);

                    aXRequest.setStartDate(startDate);
                    aXRequest.setEndDate(endDate);
                    APMain.apFrame.showEstSignal(getTask().getCode(), true);
                    //  APMain.apFrame.getEstMeter().showSignal(getTask().getCode(),"LP", true);
                    String statementFile = preparePdfStatement(aXRequest, Objects.equals(getTask().getDocument(), "LP"));

                    if (statementFile != null)
                    {
                        aXRequest.setRole(ESController.getRole());
                        aXRequest.setReference(APController.generateKey());
                        aXRequest.setChannel(ESController.getChannel().getCode());

                        aXRequest.setModule(ESController.module);
                        aXRequest.setCustomer(getClient().queryCustomer(aXRequest.getAccount().getCustId()));
                        aXRequest.getCharge().setScheme(ESController.chargeScheme);

                        aXRequest.setType(TXType.Statement);
                        aXRequest.setAccessCode(account.getAccountNumber());
                        aXRequest.setCurrency(aXRequest.getAccount().getCurrency());

                        aXRequest.setBranch(aXRequest.getAccount().getBranch());
                        aXRequest.setAlertCode(getTask().getCode());
                        aXRequest.getCharge().setCode(getTask().getCharge());

                        getWorker().setCharge(aXRequest, new HashMap<>());
                        getLog().setRequest(aXRequest);
                        record.setRecId(getClient().nextStatementId());

                        record.setAccount(account.getAccountNumber());
                        record.setAddress(getWorker().createCsvList(contacts));
                        record.setCharge(aXRequest.getCharge().getChargeAmount());

                        record.setTask(getTask().getCode());
                        record.setStartDt(startDate);
                        record.setEndDt(endDate);

                        if (AXResult.SUCCESS == getXapi().checkBalance(aXRequest).getResult())
                        {
                            record.setChgId(getXapi().getResponse().getChargeId());
                            if (sendEmail(ESController.smtpUsername, contacts.toArray(new String[0]), ESController.emailSubject + " [ AC " + getWorker().protectField(account.getAccountNumber(), 5, 1) + " ]", replaceMasks(mailBody, account), new String[]
                            {
                                statementFile
                            }))
                            {
                                getClient().upsertCustomField(ESController.sentDateFieldId, account.getAcctId(), getWorker().formatDate(AXConstant.cbsDateFormat, getClient().getSystemDate()));
                                getClient().upsertCustomField(ESController.addressFieldId, account.getAcctId(), null);
                                if (Objects.equals(getTask().getCycle(), "O"))
                                {
                                    getClient().upsertCustomField(ESController.nowFieldId, account.getAcctId(), String.valueOf(ESController.noValueId));
                                    getClient().upsertCustomField(ESController.startDateFieldId, account.getAcctId(), null);
                                    getClient().upsertCustomField(ESController.endDateFieldId, account.getAcctId(), null);
                                }
                                APMain.apFrame.showEstSignal(getTask().getCode(), false);
                                getXapi().processCharge(aXRequest, true, 1);
                                record.setStatus("S");
                            }
                        }
                        else if (AXResult.INSUFFICIENT_FUNDS == getXapi().getResponse().getResult())
                        {
                            record.setStatus("I");
                        }
                        else
                        {
                            record.setStatus("F");
                        }
                        try
                        {
                            switch (record.getStatus())
                            {
                                case "F":
                                    record.setResult("Failed");
                                    break;
                                case "I":
                                    record.setResult("Insufficient Funds");
                                    break;
                                case "R":
                                    record.setResult("Rejected");
                                    break;
                                case "S":
                                    record.setResult("Sent");
                                    break;
                                case "T":
                                    record.setResult("Invalid Template");
                                    break;
                            }
                        }
                        catch (Exception ex)
                        {
                            getLog().logError(ex);
                        }
                        getClient().saveStatementLog(record);
                    }
                }
                catch (Exception ex)
                {
                    getLog().logEvent(ex);
                }
                finally
                {
                    writeToLog(record, startTime);
                }
            }
            contacts.clear();
        }
        catch (Exception ex)
        {
            APMain.estLog.logEvent(ex);
        }
        return Objects.equals(record.getStatus(), "S");
    }

    public String preparePdfStatement(AXRequest aXRequest, boolean locked)
    {
        try
        {

            HashMap parametersMap = new HashMap();
            parametersMap.put("ADVICE_CURSOR", null);
            parametersMap.put("I_BU_ID", aXRequest.getAccount().getBranch().getBuId());

            parametersMap.put("I_ACCT_NO", aXRequest.getAccount().getAccountNumber());
            parametersMap.put("I_START_DATE", aXRequest.getStartDate());
            parametersMap.put("I_END_DATE", aXRequest.getEndDate());

            parametersMap.put("REQUEST_MODE", null);
            parametersMap.put("USER_BU_ID", aXRequest.getAccount().getBranch().getBuId());
            parametersMap.put("RPT_CD", "0");

            JRProperties.setProperty(JRQueryExecuterFactory.QUERY_EXECUTER_FACTORY_PREFIX + "plsql",
                    "com.jaspersoft.jrx.query.PlSqlQueryExecuterFactory");

            JasperDesign jasperDesign = JRXmlLoader.load(new File("est/report/rpt_dp_customer_estmt.jrxml"));
            JasperReport jasperReport = JasperCompileManager.compileReport(jasperDesign);

            jasperReport.setProperty("net.sf.jasperreports.query.executer.factory.plsql",
                    "com.jaspersoft.jrx.query.PlSqlQueryExecuterFactory");

            // JasperPrint jasperPrint = JasperFillManager.fillReport("est/report/rpt_dp_customer_estmt.jasper", parametersMap, getClient().getConnection());
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parametersMap, getClient().getConnection());

            String statementFile = workDir + "/" + ESController.statementFileName + " [ AC " + getWorker().protectField(aXRequest.getAccount().getAccountNumber(), 5, 1) + " ].pdf";
            JasperExportManager.exportReportToPdfFile(jasperPrint, statementFile);

            if (stampPdfStatement(aXRequest, statementFile, locked ? aXRequest.getAccount().getAccountNumber().substring(aXRequest.getAccount().getAccountNumber().length() - 5) : null, true))
            {
                return statementFile;
            }
            getFile().deleteFile(statementFile);
        }
        catch (Exception ex)
        {
            getLog().logError(ex);
        }
        return null;
    }

    private boolean stampPdfStatement(AXRequest aXRequest, String fileName, String password, boolean addWaterMark)
    {
        try
        {
            int i = 0, pages = 0;
            PdfReader reader = new PdfReader(new FileInputStream(fileName));
            if ((pages = reader.getNumberOfPages()) > 0)
            {
                aXRequest.setPages(pages);
                if (addWaterMark)
                {
                    PdfStamper stamper = new PdfStamper(reader, new FileOutputStream(fileName));
                    Image watermark = Image.getInstance("est/report/watermark.png");
                    while (i++ < pages)
                    {
                        watermark.setAbsolutePosition((reader.getCropBox(i).getWidth() - watermark.getScaledWidth()) / 2, (reader.getCropBox(i).getHeight() - watermark.getScaledHeight()) / 2); //align it center
                        PdfContentByte contentByte = stamper.getOverContent(i);
                        PdfGState state = new PdfGState();
                        state.setFillOpacity(0.2f);
                        contentByte.setGState(state);
                        contentByte.addImage(watermark);
                    }
                    stamper.close();
                }
                if (!getWorker().isBlank(password))
                {
                    PdfEncryptor.encrypt(new PdfReader(new FileInputStream(fileName)), new FileOutputStream(fileName), password.getBytes(), password.getBytes(), PdfWriter.ALLOW_PRINTING, true);
                }
                return true;
            }
        }
        catch (Exception ex)
        {
            getLog().logError(ex);
        }
        return false;
    }

    public boolean sendEmail(String senderAddress, String recipients[], String subject, String message, String[] attachments)
    {
        try
        {
            Properties properties = new Properties();
            properties.put("mail.smtp.host", ESController.smtpHost);
            properties.put("mail.smtp.ssl.enable", ESController.sslEnable);
            properties.put("mail.transport.protocol", ESController.transportProtocol);

            properties.put("mail.smtp.ssl.trust", ESController.sslTrust);
            properties.put("mail.smtp.starttls.enable", ESController.starttlsEnable);
            properties.put("mail.smtp.ssl.protocols", ESController.sslProtocols);
            properties.put("mail.smtp.auth", ESController.smtpAuth);

            Session session = Session.getDefaultInstance(properties, new javax.mail.Authenticator()
            {
                @Override
                protected PasswordAuthentication getPasswordAuthentication()
                {
                    return new PasswordAuthentication(ESController.smtpUsername, ESController.smtpPassword);
                }
            });
            session.setDebug(APController.isDebugMode());
            Message mimeMessage = new MimeMessage(session);

            InternetAddress addressFrom = new InternetAddress(senderAddress, ESController.senderAlias);
            mimeMessage.setFrom(addressFrom);
            InternetAddress[] addressTo = new InternetAddress[recipients.length];

            for (int i = 0; i < recipients.length; i++)
            {
                if (!recipients[i].trim().equalsIgnoreCase(""))
                {
                    addressTo[i] = new InternetAddress(recipients[i]);
                }
            }

            mimeMessage.setRecipients(Message.RecipientType.TO, addressTo);
            mimeMessage.setSubject(subject);
            MimeBodyPart messageBody = new MimeBodyPart();

            messageBody.setContent(message, "text/html");
            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(messageBody);

            for (String fileName : attachments)
            {
                MimeBodyPart bodyPart = new MimeBodyPart();
                FileDataSource dataSource = new FileDataSource(fileName);

                bodyPart.setDataHandler(new DataHandler(dataSource));
                bodyPart.setFileName(dataSource.getName());
                multipart.addBodyPart(bodyPart);
            }

            mimeMessage.setContent(multipart);
            mimeMessage.setSentDate(new Date());
            Transport.send(mimeMessage, mimeMessage.getAllRecipients());
            return true;
        }
        catch (Exception ex)
        {
            getLog().logError(ex);
        }
        return false;
    }

    public String replaceMasks(String text, CNAccount account)
    {
        try
        {
            if (!getWorker().isBlank(text))
            {
                ArrayList<String> holdersList = getWorker().extractPlaceHolders(text);
                for (String placeHolder : holdersList)
                {
                    String replacement = placeHolder;
                    switch (placeHolder.toUpperCase())
                    {
                        case "{ACCOUNT}":
                            replacement = getWorker().checkBlank(getWorker().protectField(account.getAccountNumber(), 5, 1), "<>");
                            break;
                        case "{DATE}":
                            replacement = getWorker().checkBlank(getWorker().formatDate(AXConstant.displayDateFormat2, getClient().getSystemDate()), "<>");
                            break;
                        case "{NAME}":
                            replacement = getWorker().firstName(getWorker().checkBlank(account.getAccountName(), "<>"));
                            break;
                    }
                    text = getWorker().replaceAll(text, placeHolder, replacement);
                }
                holdersList.clear();
                return text.replaceAll(" ~ <>", "").replaceAll("~<>", "").trim();
            }
        }
        catch (Exception ex)
        {
            getLog().logEvent(ex);
        }
        return text;
    }

    public void pushTask()
    {
        try
        {
            getTask().setPreviousDate(getRunDate());
            switch (getTask().getCycle())
            {
                case "O":
                case "D":
                    getTask().setNextDate(getClient().getSystemDate());
                    break;
                case "W":
                    getTask().setNextDate(getWorker().toDate(getWorker().toLocalDate(getRunDate()).with(TemporalAdjusters.next(DayOfWeek.MONDAY))));
                    break;
                case "M":
                    getTask().setNextDate(getWorker().toDate(getWorker().toLocalDate(getRunDate()).with(TemporalAdjusters.firstDayOfNextMonth())));
                    break;
                case "Q":
                    getTask().setNextDate(getWorker().toDate(getWorker().toLocalDate(getRunDate()).with(TemporalAdjusters.firstDayOfYear()).plusMonths(((int) Math.ceil((double) getWorker().toLocalDate(getRunDate()).getMonthValue() / 3)) * 3).with(TemporalAdjusters.firstDayOfMonth())));
                    break;
                case "A":
                    getTask().setNextDate(getWorker().toDate(getWorker().toLocalDate(getRunDate()).with(TemporalAdjusters.firstDayOfNextYear())));
                    break;
                default:
                    getTask().setNextDate(getWorker().toDate(getWorker().getSystemDate().plusDays(1L)));
                    break;
            }
            getClient().pushTask(getTask());
        }
        catch (Exception ex)
        {
            APMain.estLog.logEvent(ex);
        }
    }

    private void setTree(ESRecord record)
    {
        try
        {
            TRItem treeItem = new TRItem();
            treeItem.setCode(getTask().getCycle());
            treeItem.setApproved(Objects.equals(record.getStatus(), "S"));

            treeItem.setText(record.getRecId() + "~" + record.getAccount());
            getWorker().extractFields(record, treeItem.getRequest());
            getWorker().extractFields(record, treeItem.getResponse());

            treeItem.getDetail().put(ALHeader.RecId, record.getRecId());
            treeItem.getDetail().put(ALHeader.CreateDt, record.getCreateDt());
            treeItem.getDetail().put(ALHeader.Task, record.getTask());

            treeItem.getDetail().put(ALHeader.Account, record.getAccount());
            treeItem.getDetail().put(ALHeader.Address, record.getAddress());
            treeItem.getDetail().put(ALHeader.StartDt, record.getStartDt());

            treeItem.getDetail().put(ALHeader.EndDt, record.getEndDt());
            treeItem.getDetail().put(ALHeader.Charge, record.getCharge());
            treeItem.getDetail().put(ALHeader.ChgId, record.getChgId());

            treeItem.getDetail().put(ALHeader.Result, record.getResult());
            treeItem.getDetail().put(ALHeader.RecSt, record.getStatus());
            APMain.apFrame.insertTreeItem(treeItem, ESController.module);
        }
        catch (Exception ex)
        {
            getLog().logEvent(ex);
        }
    }

    private void writeToLog(ESRecord record, long startTime)
    {
        try
        {
            setTree(record);
            getLog().setRecord(record);
            getLog().setResult(record.getStatus() + " ~ " + record.getResult());
            getLog().setDuration((System.currentTimeMillis() - startTime) + " Ms");
            APMain.estLog.logEvent(getLog());
        }
        catch (Exception ex)
        {
            APMain.estLog.logEvent(ex);
        }
    }

    private void prepare()
    {
        try
        {
            ESProcessor.wip.put(getTask().getCode(), time = 0L);
            getFile().createDirectory(workDir);
            getFile().clearDirectory(workDir);
            setRunDate(getClient().getSystemDate());
        }
        catch (Exception ex)
        {
            APMain.estLog.logEvent(ex);
        }
    }

    private boolean checkExit()
    {
        boolean exit;
        if (!(exit = (time != ESProcessor.wip.getOrDefault(getTask().getCode(), 0L))))
        {
            ESProcessor.wip.put(getTask().getCode(), time = System.currentTimeMillis());
        }
        return exit;
    }

    private void windup()
    {
        ESProcessor.wip.remove(getTask().getCode());
    }

    private AXFile getFile()
    {
        return getBox().getxFile();
    }

    private APLog getLog()
    {
        return getBox().getLog();
    }

    private void setLog(AXCaller caller)
    {
        getBox().setLog(caller);
    }

    /**
     * @return the xClient
     */
    public AXWorker getWorker()
    {
        return getBox().getWorker();
    }

    /**
     * @return the xClient
     */
    public DBClient getClient()
    {
        return getBox().getdClient();
    }

    /**
     * @return the xClient
     */
    public AXClient getXapi()
    {
        return getBox().getXclient();
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

    /**
     * @return the task
     */
    public ESTask getTask()
    {
        return task;
    }

    /**
     * @return the runDate
     */
    public Date getRunDate()
    {
        return runDate;
    }

    /**
     * @param runDate the runDate to set
     */
    public void setRunDate(Date runDate)
    {
        this.runDate = runDate;
    }
}
