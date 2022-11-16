/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Ruby;

import Ruby.model.DHRecord;
import Ruby.model.GPMember;
import Ruby.model.LHRecord;
import Ruby.model.MURecord;
import Ruby.model.RLCustomer;
import Ruby.model.AXBank;
import Ruby.model.AXPayment;
import Ruby.model.AXTxn;
import Ruby.acx.APLog;
import Ruby.acx.ATBox;
import Ruby.acx.AXCrypt;
import Ruby.acx.AXCryptNew;
import Ruby.acx.AXWorker;
import Ruby.model.AXCharge;
import Ruby.model.TCScheme;
import Ruby.model.AXSetting;
import Ruby.model.AXSplit;
import Ruby.model.CNActivity;
import Ruby.model.AXTier;
import Ruby.model.TCWaiver;
import Ruby.model.TCValue;
import Ruby.model.CFValue;
import Ruby.model.CNAccount;
import Ruby.model.CNBranch;
import Ruby.model.CNCurrency;
import Ruby.model.CNCustomer;
import Ruby.model.CNUser;
import Ruby.model.CLItem;
import Ruby.model.AXTerminal;
import Ruby.model.AXUser;
import Ruby.model.BNUser;
import Ruby.model.CAEvent;
import Ruby.model.CMChannel;
import Ruby.model.CNScheme;
import Ruby.model.LNDetail;
import Ruby.model.MXAlert;
import Ruby.model.MXMessage;
import Ruby.model.TCDeduction;
import Ruby.model.GPSplit;
import Ruby.model.TLDrawer;
import Ruby.model.URLimit;
import Ruby.model.USRole;
import Ruby.sms.ALController;
import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLRecoverableException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Objects;
import java.util.TreeMap;

/**
 *
 * @author Pecherk
 */
public final class DBClient
{
    
    private ATBox box;
    private Connection connection;
    private CallableStatement alertStatement;
    private AXCryptNew aXCryptNew = new AXCryptNew();
    
    public DBClient(ATBox box)
    {
        setBox(box);
    }
    
    private void connectToDB()
    {
        try
        {
            dispose();
            setConnection(DriverManager.getConnection(APController.databaseUrl, APController.cmSchemaName, APController.cmSchemaPassword));
            setAlertStatement(getConnection().prepareCall("{call " + APController.cmSchemaName + ".PHL_LOAD_ALERTS(?, ?, ?, ?, ?, ?)}"));
            getConnection().setAutoCommit(true);
        }
        catch (Exception ex)
        {
            getLog().logEvent(ex);
        }
    }
    
    public Object[][] executeQueryToArray(String query)
    {
        return rsToArray(executeQuery(query, true));
    }
    
    public ResultSet executeQueryToResultSet(String query)
    {
        return executeQuery(query, true);
    }
    
    public int getRowCount(ResultSet rs)
    {
        int records = 0;
        try
        {
            rs.last();
            records = rs.getRow();
            rs.beforeFirst();
        }
        catch (Exception ex)
        {
            getLog().logEvent(ex);
        }
        return records;
    }
    
    private Object[][] rsToArray(ResultSet rs)
    {
        if (rs != null)
        {
            try
            {
                int row = 0, records = getRowCount(rs), fields = rs.getMetaData().getColumnCount();
                Object[][] results = (records == 0) ? new Object[0][0] : new Object[records][fields];
                while (rs.next())
                {
                    for (int col = 0; col < fields; col++)
                    {
                        results[row][col] = rs.getObject(col + 1);
                    }
                    row++;
                }
                return results;
            }
            catch (Exception ex)
            {
                getLog().logEvent(ex);
            }
            finally
            {
                closeResult(rs);
            }
        }
        
        return new Object[0][0];
    }
    
    public void closeResult(ResultSet rs)
    {
        try
        {
            rs.close();
        }
        catch (Exception ex)
        {
            getLog().logEvent(ex);
        }
    }
    
    private ResultSet executeQuery(String query, boolean retry)
    {
        try
        {
            checkConnection();
            Long startTime = System.currentTimeMillis();
            if (isConnected())
            {
                Statement statement = getConnection().createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
                statement.closeOnCompletion();
                ResultSet rs = statement.executeQuery(query);
                getLog().logDebug("query", query, getWorker().prepareDuration(startTime));
                return rs;
            }
        }
        catch (Exception ex)
        {
            if (isRecoverable(ex))
            {
                dispose();
                if (retry)
                {
                    return executeQuery(query, false);
                }
            }
            getLog().logEvent(query, ex);
        }
        return null;
    }
    
    private boolean isRecoverable(Exception ex)
    {
        return ex instanceof SQLRecoverableException || String.valueOf(ex.getMessage()).contains("ORA-01000") || String.valueOf(ex.getMessage()).contains("Closed ");
    }
    
    public boolean isConnected() throws SQLException
    {
        return getConnection() != null ? !getConnection().isClosed() : false;
    }
    
    public void checkConnection()
    {
        try
        {
            if (!isConnected())
            {
                connectToDB();
            }
        }
        catch (Exception ex)
        {
            getLog().logEvent(ex);
        }
    }
    
    public boolean executeUpdate(String update, boolean retry)
    {
        try
        {
            checkConnection();
            Long startTime = System.currentTimeMillis();
            if (isConnected())
            {
                update = update.replaceAll("'null'", "NULL").replaceAll("'NULL'", "NULL");
                try (Statement statement = getConnection().createStatement())
                {
                    statement.executeUpdate(update);
                }
                getLog().logDebug("update", update, getWorker().prepareDuration(startTime));
                return true;
            }
        }
        catch (Exception ex)
        {
            if (isRecoverable(ex))
            {
                dispose();
                if (retry)
                {
                    return executeUpdate(update, false);
                }
            }
            getLog().logEvent(update, ex);
        }
        return false;
    }
    
    public void dispose()
    {
        try
        {
            getAlertStatement().close();
        }
        catch (Exception ex)
        {
            setAlertStatement(null);
        }
        try
        {
            getConnection().close();
        }
        catch (Exception ex)
        {
            setConnection(null);
        }
    }
    
    public CNAccount unmaskLedger(String glAccount, CNBranch cNBranch)
    {
        CNAccount cNAccount = new CNAccount();
        if (!getWorker().isBlank(glAccount))
        {
            glAccount = glAccount.trim();
            cNAccount = (glAccount.contains("***") && cNBranch.getGlPrefix() != null) ? queryGLAccount(cNBranch.getGlPrefix() + glAccount.substring(glAccount.indexOf("***") + 3)) : queryGLAccount(glAccount);
        }
        return cNAccount;
    }
    
    public CNBranch queryChannelBranch(Long channelId)
    {
        CNBranch cNBranch = new CNBranch();
        try (ResultSet rs = executeQueryToResultSet("SELECT ORIGIN_BU_ID FROM " + APController.coreSchemaName + ".SERVICE_CHANNEL WHERE CHANNEL_ID=" + channelId))
        {
            if (rs != null && rs.next())
            {
                cNBranch = queryBusinessUnit(rs.getLong("ORIGIN_BU_ID"));
            }
        }
        catch (Exception ex)
        {
            getLog().logEvent(ex);
        }
        return cNBranch;
    }
    
    public Long queryIdentityXref(Long customerTypeId, Long identityId)
    {
        Long identityXrefId = null;
        try (ResultSet rs = executeQueryToResultSet("SELECT CUST_IDENT_XREF_ID FROM " + APController.coreSchemaName + ".CUSTOMER_IDENTIFICATION_XREF WHERE CUST_TY_ID=" + customerTypeId + " AND IDENT_ID=" + identityId))
        {
            if (rs != null && rs.next())
            {
                identityXrefId = rs.getLong("CUST_IDENT_XREF_ID");
            }
        }
        catch (Exception ex)
        {
            getLog().logEvent(ex);
        }
        return identityXrefId;
    }
    
    public Long queryWorkflowItemId(Long custId)
    {
        Long workflowItemId = 0L;
        try (ResultSet rs = executeQueryToResultSet("SELECT WORK_ITEM_ID FROM " + APController.coreSchemaName + ".WF_WORK_ITEM WHERE CUST_ID=" + custId + " ORDER BY WORK_ITEM_ID DESC"))
        {
            if (rs != null && rs.next())
            {
                workflowItemId = rs.getLong("WORK_ITEM_ID");
            }
        }
        catch (Exception ex)
        {
            getLog().logEvent(ex);
        }
        return workflowItemId;
    }
    
    public boolean checkExists(String query)
    {
        boolean exists = false;
        try (ResultSet rs = executeQueryToResultSet(query))
        {
            exists = rs.next();
        }
        catch (Exception ex)
        {
            getLog().logEvent(ex);
        }
        return exists;
    }
    
    public AXUser loginAdminUser(String loginId, String password, String role)
    {
        
        AXUser user = new AXUser();
        try (ResultSet rs = executeQueryToResultSet("SELECT A.EMP_NO FROM " + APController.coreSchemaName + ".SYSUSER A, " + APController.coreSchemaName + ".SYSPWD_HIST B WHERE A.LOGIN_ID='" + loginId + "' AND B.SYSUSER_ID=A.SYSUSER_ID AND A.REC_ST=B.REC_ST AND B.PASSWD='" + aXCryptNew.encrypt(password) + "' AND B.REC_ST='A'"))
        {
            if (rs != null && rs.next())
            {
                user = queryUser(rs.getString("EMP_NO"), null, true);
            }
            if (getWorker().isBlank(user.getUserNumber()) && Objects.equals("AU", role) && !checkExists("SELECT EMP_NO FROM " + APController.cmSchemaName + ".PHU_ROLE WHERE ROLE='" + role + "'"))
            {
                user.setSysUser(loginId);
                user.setStaffName(loginId);
                user.setUserName(loginId);
                user.setUserNumber(loginId);
                user.getRoles().add(role);
            }
        }
        catch (Exception ex)
        {
            getLog().logEvent(ex);
        }
        return user;
    }
    
    public <T> T queryParameter(String code, Class<T> clazz)
    {
        T value = null;
        try (ResultSet rs = executeQueryToResultSet("SELECT PARAM_VALUE FROM " + APController.coreSchemaName + ".CTRL_PARAMETER WHERE PARAM_CD = '" + code + "'"))
        {
            if (rs != null && rs.next())
            {
                value = (T) clazz.getConstructor(String.class).newInstance(rs.getString("PARAM_VALUE"));
            }
        }
        catch (Exception ex)
        {
            getLog().logEvent(ex);
        }
        return value;
    }
    
    public boolean isDuplicate(Long channelId, String accountNumber, String referenceNumber, BigDecimal amount, String debitCredit)
    {
        return checkExists("SELECT ACCT_NO FROM " + APController.coreSchemaName + ".DEPOSIT_ACCOUNT_HISTORY WHERE CHANNEL_ID=" + channelId + " AND ACCT_NO='" + accountNumber + "' AND TRAN_DT>=SYSDATE-30 AND TRAN_REF_TXT='" + referenceNumber + "' AND DR_CR_IND='" + debitCredit + "' AND TXN_AMT=" + amount);
    }
    
    public boolean isLedgerDuplicate(Long channelId, String accountNumber, String referenceNumber, BigDecimal amount, String debitCredit)
    {
        return checkExists("SELECT GL_ACCT_NO FROM " + APController.coreSchemaName + ".GL_ACCOUNT_HISTORY WHERE CHANNEL_ID=" + channelId + " AND GL_ACCT_NO='" + accountNumber + "' AND TRAN_DT>=SYSDATE-30 AND TRAN_REF_TXT='" + referenceNumber + "' AND DR_CR_IND='" + debitCredit + "' AND TXN_AMT=" + amount);
    }
    
    public boolean isAccountValid(String accountNumber)
    {
        return checkExists("SELECT ACCT_NO FROM " + APController.coreSchemaName + ".V_ACCOUNTS WHERE PROD_CAT_TY='DP' AND ACCT_NO='" + accountNumber + "' AND REC_ST IN ('A')");
    }
    
    public String queryTxnResponse(Long channelId, String txnRef)
    {
        String respCode = null;
        try (ResultSet rs = executeQueryToResultSet("SELECT XAPI_CODE FROM " + APController.cmSchemaName + ".PHL_TXN_LOG WHERE TXN_REF='" + txnRef + "' AND TXN_DATE>=SYSDATE-30 AND CHANNEL_ID = " + channelId))
        {
            if (rs != null && rs.next())
            {
                respCode = rs.getString("XAPI_CODE");
            }
        }
        catch (Exception ex)
        {
            getLog().logEvent(ex);
        }
        return respCode;
    }
    
    public CNUser queryChannelUser(Long schemeId, String accessCode)
    {
        CNUser cNUser = new CNUser();
        if (!getWorker().isBlank(accessCode))
        {
            try (ResultSet rs = executeQueryToResultSet("SELECT U.CUST_CHANNEL_USER_ID, U.CUST_ID, U.CUST_CHANNEL_ID, U.ACCESS_CD, INITCAP(U.ACCESS_NM) AS ACCESS_NM, U.PASSWORD, U.PWD_RESET_FG, U.CHANNEL_ID, U.CHANNEL_SCHEME_ID, U.LOCKED_FG, NVL(U.RANDOM_SEED, 0) AS PIN_TRIES, NVL(U.RANDOM_NO_SEED, 0) AS PUK_TRIES, U.SECURITY_CD, U.QUIZ_CD FROM " + APController.coreSchemaName + ".CUSTOMER_CHANNEL C, " + APController.coreSchemaName + ".CUSTOMER_CHANNEL_USER U WHERE C.CUST_ID=U.CUST_ID AND C.CUST_CHANNEL_ID=U.CUST_CHANNEL_ID AND U.CHANNEL_SCHEME_ID=" + schemeId + " AND U.ACCESS_CD='" + accessCode + "' AND U.REC_ST='A'"))
            {
                if (rs != null && rs.next())
                {
                    cNUser.setUserId(rs.getLong("CUST_CHANNEL_USER_ID"));
                    cNUser.setCustId(rs.getLong("CUST_ID"));
                    cNUser.setCustChannelId(rs.getLong("CUST_CHANNEL_ID"));
                    cNUser.setAccessCode(rs.getString("ACCESS_CD"));
                    cNUser.setAccessName(rs.getString("ACCESS_NM"));
                    cNUser.setPwdReset(getWorker().isYes(rs.getString("PWD_RESET_FG")));
                    cNUser.setChannelId(rs.getLong("CHANNEL_ID"));
                    cNUser.setSchemeId(rs.getLong("CHANNEL_SCHEME_ID"));
                    cNUser.setLocked(getWorker().isYes(rs.getString("LOCKED_FG")));
                    cNUser.setSecurityCode(rs.getString("SECURITY_CD"));
                    cNUser.setAccessKey(rs.getString("QUIZ_CD"));
                    cNUser.setPinAttempts(rs.getInt("PIN_TRIES"));
                    cNUser.setPukAttempts(rs.getInt("PUK_TRIES"));
                    cNUser.setPassword(cNUser.isPwdReset() ? new AXCrypt(String.valueOf(cNUser.getUserId())).encrypt(getCrypt().decrypt(rs.getString("PASSWORD"))) : checkPassword(cNUser, rs.getString("PASSWORD")));
                    cNUser.setChargeAccount(queryChargeAccount(cNUser));
                    cNUser.setAccounts(queryAccounts(cNUser));
                    pushUserExpiry(cNUser.getUserId());
                }
            }
            catch (Exception ex)
            {
                getLog().logEvent(ex);
            }
        }
        return cNUser;
    }
    
    public ArrayList<CNAccount> queryAccounts(CNUser cNUser)
    {
        ArrayList<CNAccount> accounts = new ArrayList<>();
        try (ResultSet rs = executeQueryToResultSet("SELECT C.ACCT_ID, C.CUST_ID, C.MAIN_BRANCH_ID, C.PROD_ID, C.ACCT_NO, INITCAP(C.ACCT_NM) AS ACCT_NM, NVL(A.SHORT_NAME, C.ACCT_NO) AS SHORT_NAME, C.CRNCY_ID, C.PROD_CAT_TY, M.CUST_CAT, C.REC_ST FROM " + APController.coreSchemaName + ".CUST_CHANNEL_ACCOUNT A, " + APController.coreSchemaName + ".ACCOUNT C, " + APController.coreSchemaName + ".CUSTOMER M WHERE A.ACCT_ID=C.ACCT_ID AND A.REC_ST='A' AND A.CHANNEL_ID=" + cNUser.getChannelId() + " AND A.CUST_ID=C.CUST_ID AND M.CUST_ID=C.CUST_ID AND A.CUST_ID=" + cNUser.getCustId()))
        {
            if (rs != null)
            {
                while (rs.next())
                {
                    accounts.add(readAccount(rs));
                }
            }
        }
        catch (Exception ex)
        {
            getLog().logEvent(ex);
        }
        return accounts;
    }
    
    public CNAccount queryAccount(Long channelId, Long custId, Long accountId)
    {
        CNAccount account = new CNAccount();
        try (ResultSet rs = executeQueryToResultSet("SELECT C.ACCT_ID, C.CUST_ID, C.MAIN_BRANCH_ID, C.PROD_ID, C.ACCT_NO, INITCAP(C.ACCT_NM) AS ACCT_NM, NVL(A.SHORT_NAME, C.ACCT_NO) AS SHORT_NAME, C.CRNCY_ID, C.PROD_CAT_TY, M.CUST_CAT, C.REC_ST FROM " + APController.coreSchemaName + ".CUST_CHANNEL_ACCOUNT A, " + APController.coreSchemaName + ".ACCOUNT C, " + APController.coreSchemaName + ".CUSTOMER M WHERE A.ACCT_ID=C.ACCT_ID AND A.REC_ST='A' AND A.CHANNEL_ID=" + channelId + " AND A.CUST_ID=C.CUST_ID AND M.CUST_ID=C.CUST_ID AND A.CUST_ID=" + custId + " AND A.ACCT_ID=" + accountId))
        {
            if (rs != null && rs.next())
            {
                account = readAccount(rs);
            }
        }
        catch (Exception ex)
        {
            getLog().logEvent(ex);
        }
        if (getWorker().isBlank(account.getAccountNumber()) && (Objects.equals(ALController.channelId, channelId)))
        {
            try (ResultSet rs = executeQueryToResultSet("SELECT C.ACCT_ID, C.CUST_ID, C.MAIN_BRANCH_ID, C.PROD_ID, C.ACCT_NO, INITCAP(C.ACCT_NM) AS ACCT_NM, NVL(A.SHORT_NAME, C.ACCT_NO) AS SHORT_NAME, C.CRNCY_ID, C.PROD_CAT_TY, M.CUST_CAT, C.REC_ST FROM " + APController.coreSchemaName + ".CUST_CHANNEL_ACCOUNT A, " + APController.coreSchemaName + ".ACCOUNT C, " + APController.coreSchemaName + ".CUSTOMER M WHERE A.ACCT_ID=C.ACCT_ID AND A.REC_ST='A' AND A.CHANNEL_ID=" + channelId + " AND A.CUST_ID=C.CUST_ID AND M.CUST_ID=C.CUST_ID AND A.CUST_ID IN (SELECT GROUP_CUST_ID FROM " + APController.coreSchemaName + ".GROUP_MEMBER WHERE MEMBER_CUST_ID=" + custId + " AND REC_ST='A') AND A.ACCT_ID=" + accountId))
            {
                if (rs != null && rs.next())
                {
                    account = readAccount(rs);
                }
            }
            catch (Exception ex)
            {
                getLog().logEvent(ex);
            }
        }
        return account;
    }
    
    private CNAccount readAccount(final ResultSet rs) throws SQLException
    {
        CNAccount cNAccount = new CNAccount();
        cNAccount.setAcctId(rs.getLong("ACCT_ID"));
        cNAccount.setCustId(rs.getLong("CUST_ID"));
        cNAccount.setBranch(queryBusinessUnit(rs.getLong("MAIN_BRANCH_ID")));
        cNAccount.setProductId(rs.getLong("PROD_ID"));
        cNAccount.setAccountNumber(rs.getString("ACCT_NO"));
        cNAccount.setAccountType(rs.getString("PROD_CAT_TY"));
        cNAccount.setAccountName(rs.getString("ACCT_NM"));
        cNAccount.setCurrency(queryCurrency(rs.getLong("CRNCY_ID")));
        cNAccount.setCustCat(rs.getString("CUST_CAT"));
        cNAccount.setStatus(rs.getString("REC_ST"));
        cNAccount.setProductDesc(queryProductDesc(rs.getLong("PROD_ID")));
        return cNAccount;
    }
    
    public ArrayList<CNAccount> queryDepositAccounts(Long custId)
    {
        ArrayList<CNAccount> accounts = new ArrayList<>();
        try (ResultSet rs = executeQueryToResultSet("SELECT C.ACCT_ID, C.CUST_ID, C.MAIN_BRANCH_ID, C.PROD_ID, C.ACCT_NO, INITCAP(C.ACCT_NM) AS ACCT_NM, C.CRNCY_ID, C.PROD_CAT_TY, S.CLEARED_BAL, M.CUST_CAT, C.REC_ST FROM " + APController.coreSchemaName + ".ACCOUNT C, " + APController.coreSchemaName + ".DEPOSIT_ACCOUNT_SUMMARY S, " + APController.coreSchemaName + ".CUSTOMER M WHERE M.CUST_ID=C.CUST_ID AND C.CUST_ID=" + custId + " AND S.ACCT_NO=C.ACCT_NO AND C.REC_ST='A'"))
        {
            if (rs != null)
            {
                while (rs.next())
                {
                    accounts.add(readAccount(rs));
                }
            }
        }
        catch (Exception ex)
        {
            getLog().logEvent(ex);
        }
        return accounts;
    }
    
    public String queryProductDesc(Long prodId)
    {
        String proddsc = "";
        try (ResultSet rs = executeQueryToResultSet("SELECT PROD_DESC FROM " + APController.coreSchemaName + ".PRODUCT "
                + "WHERE PROD_ID=" + prodId + " AND  REC_ST='A'"))
        {
            if (rs != null)
            {
                if (rs.next())
                {
                    proddsc = rs.getString("PROD_DESC");
                }
            }
        }
        catch (Exception ex)
        {
            getLog().logEvent(ex);
        }
        return proddsc;
    }
    
    public CNAccount queryChargeAccount(CNUser cNUser)
    {
        CNAccount cNAccount = new CNAccount();
        try (ResultSet rs = executeQueryToResultSet("SELECT C.ACCT_ID, C.CUST_ID, C.MAIN_BRANCH_ID, C.PROD_ID, C.ACCT_NO, INITCAP(C.ACCT_NM) AS ACCT_NM, C.CRNCY_ID, C.PROD_CAT_TY, M.CUST_CAT, C.REC_ST FROM " + APController.coreSchemaName + ".ACCOUNT C, " + APController.coreSchemaName + ".CUSTOMER_CHANNEL L, " + APController.coreSchemaName + ".CUSTOMER M WHERE M.CUST_ID=C.CUST_ID AND C.ACCT_ID=L.CHRG_ACCT_ID AND L.CUST_CHANNEL_ID=" + cNUser.getCustChannelId()))
        {
            if (rs != null && rs.next())
            {
                cNAccount = readAccount(rs);
            }
        }
        catch (Exception ex)
        {
            getLog().logEvent(ex);
        }
        return cNAccount;
    }
    
    public CNAccount queryDepositAccount(String accountNumber)
    {
        return queryDepositAccount(accountNumber, true);
    }
    
    public CNAccount queryDepositAccount(String accountNumber, boolean checkOld)
    {
        CNAccount cNAccount = new CNAccount();
        if (!getWorker().isBlank(accountNumber))
        {
            try (ResultSet rs = executeQueryToResultSet("SELECT C.ACCT_ID, C.CUST_ID, C.MAIN_BRANCH_ID, C.PROD_ID, C.ACCT_NO, INITCAP(C.ACCT_NM) AS ACCT_NM, C.CRNCY_ID, C.PROD_CAT_TY, M.CUST_CAT, C.REC_ST FROM " + APController.coreSchemaName + ".ACCOUNT C, " + APController.coreSchemaName + ".CUSTOMER M WHERE M.CUST_ID=C.CUST_ID AND C.ACCT_NO='" + accountNumber + "'"))
            {
                if (rs != null && rs.next())
                {
                    cNAccount = readAccount(rs);
                }
            }
            catch (Exception ex)
            {
                getLog().logEvent(ex);
            }
            if (checkOld && getWorker().isBlank(cNAccount.getAccountNumber()))
            {
                return queryEquinoxAccount(accountNumber, true);
            }
        }
        return cNAccount;
    }
    
    private CNAccount queryEquinoxAccount(String accountNumber, boolean retry)
    {
        CNAccount cNAccount = new CNAccount();
        try (ResultSet rs = executeQueryToResultSet("SELECT C.ACCT_ID, C.CUST_ID, C.MAIN_BRANCH_ID, C.PROD_ID, C.ACCT_NO, INITCAP(C.ACCT_NM) AS ACCT_NM, C.CRNCY_ID, C.PROD_CAT_TY, M.CUST_CAT, C.REC_ST FROM " + APController.coreSchemaName + ".ACCOUNT C, " + APController.coreSchemaName + ".CUSTOMER M WHERE M.CUST_ID=C.CUST_ID AND C.OLD_ACCT_NO IN ('" + accountNumber + "', '" + getWorker().formatEquinoxAccount(accountNumber) + "', '" + getWorker().formatEquinoxAccount("0" + accountNumber) + "', '" + getWorker().formatEquinoxAccount("00" + accountNumber) + "', '" + getWorker().formatEquinoxAccount("000" + accountNumber) + "')"))
        {
            if (rs != null && rs.next())
            {
                cNAccount = readAccount(rs);
            }
        }
        catch (Exception ex)
        {
            getLog().logEvent(ex);
        }
        if (retry && getWorker().isBlank(cNAccount.getAccountNumber()))
        {
            return queryEquinoxAccount(String.valueOf(getWorker().convertToType(accountNumber, Long.class)), false);
        }
        return cNAccount;
    }
    
    public CNAccount queryDepositAccount(Long accountId)
    {
        CNAccount cNAccount = new CNAccount();
        try (ResultSet rs = executeQueryToResultSet("SELECT C.ACCT_ID, C.CUST_ID, C.MAIN_BRANCH_ID, C.PROD_ID, C.ACCT_NO, INITCAP(C.ACCT_NM) AS ACCT_NM, C.CRNCY_ID, C.PROD_CAT_TY, M.CUST_CAT, C.REC_ST FROM " + APController.coreSchemaName + ".ACCOUNT C, " + APController.coreSchemaName + ".CUSTOMER M WHERE M.CUST_ID=C.CUST_ID AND C.ACCT_ID=" + accountId))
        {
            if (rs != null && rs.next())
            {
                cNAccount = readAccount(rs);
            }
        }
        catch (Exception ex)
        {
            getLog().logEvent(ex);
        }
        return cNAccount;
    }
    
    public ArrayList<CNAccount> queryCycleAccounts(Long datefieldId, Date minDate)
    {
        ArrayList<CNAccount> accounts = new ArrayList<>();
        try (ResultSet rs = executeQueryToResultSet("SELECT C.ACCT_ID, C.CUST_ID, C.MAIN_BRANCH_ID, C.PROD_ID, C.ACCT_NO, INITCAP(C.ACCT_NM) AS ACCT_NM, C.CRNCY_ID, C.PROD_CAT_TY, M.CUST_CAT, C.REC_ST FROM " + APController.coreSchemaName + ".ACCOUNT C, " + APController.coreSchemaName + ".CUSTOMER M WHERE M.CUST_ID=C.CUST_ID AND C.ACCT_ID IN (SELECT A.PARENT_ID FROM " + APController.coreSchemaName + ".UDS_FIELD_VALUE A WHERE A.FIELD_ID=" + datefieldId + " AND A.REC_ST='A' AND (TO_DATE(A.FIELD_VALUE,'DD/MM/YYYY')<=" + getWorker().convertToOracleDate(minDate) + " OR A.FIELD_VALUE IS NULL))"))
        {
            if (rs != null)
            {
                while (rs.next())
                {
                    accounts.add(readAccount(rs));
                }
            }
        }
        catch (Exception ex)
        {
            getLog().logEvent(ex);
        }
        return accounts;
    }
    
    public ArrayList<CNAccount> queryNowAccounts(Long nowfieldId, Long fieldValue)
    {
        ArrayList<CNAccount> accounts = new ArrayList<>();
        try (ResultSet rs = executeQueryToResultSet("SELECT C.ACCT_ID, C.CUST_ID, C.MAIN_BRANCH_ID, C.PROD_ID, C.ACCT_NO, INITCAP(C.ACCT_NM) AS ACCT_NM, C.CRNCY_ID, C.PROD_CAT_TY, M.CUST_CAT, C.REC_ST FROM " + APController.coreSchemaName + ".ACCOUNT C, " + APController.coreSchemaName + ".CUSTOMER M WHERE M.CUST_ID=C.CUST_ID AND C.ACCT_ID IN (SELECT A.PARENT_ID FROM " + APController.coreSchemaName + ".UDS_FIELD_VALUE A WHERE A.FIELD_ID=" + nowfieldId + " AND A.REC_ST='A' AND A.FIELD_VALUE='" + fieldValue + "')"))
        {
            if (rs != null)
            {
                while (rs.next())
                {
                    accounts.add(readAccount(rs));
                }
            }
        }
        catch (Exception ex)
        {
            getLog().logEvent(ex);
        }
        return accounts;
    }
    
    public CNAccount queryLoanAccount(String accountNumber)
    {
        CNAccount cNAccount = new CNAccount();
        if (!getWorker().isBlank(accountNumber))
        {
            try (ResultSet rs = executeQueryToResultSet("SELECT C.ACCT_ID, C.CUST_ID, C.MAIN_BRANCH_ID, C.PROD_ID, C.ACCT_NO, INITCAP(C.ACCT_NM) AS ACCT_NM, E.CRNCY_ID, C.PROD_CAT_TY, M.CUST_CAT, C.REC_ST "
                    + "FROM " + APController.coreSchemaName + ".ACCOUNT C, " + APController.coreSchemaName + ".CURRENCY E, " + APController.coreSchemaName + ".CUSTOMER M WHERE M.CUST_ID=C.CUST_ID AND C.ACCT_NO='" + accountNumber + "' AND C.REC_ST='A' AND C.PROD_CAT_TY='LN' AND C.CRNCY_ID = E.CRNCY_ID"))
            {
                if (rs != null && rs.next())
                {
                    cNAccount = readAccount(rs);
                }
            }
            catch (Exception ex)
            {
                getLog().logEvent(ex);
            }
        }
        return cNAccount;
    }
    
    public CNAccount queryGLAccount(String accountNumber)
    {
        CNAccount cNAccount = new CNAccount();
        if (!getWorker().isBlank(accountNumber))
        {
            try (ResultSet rs = executeQueryToResultSet("SELECT C.GL_ACCT_ID, C.GL_ACCT_NO, C.ACCT_DESC, C.BU_ID, C.REC_ST FROM " + APController.coreSchemaName + ".GL_ACCOUNT C WHERE C.GL_ACCT_NO='" + accountNumber + "'"))
            {
                if (rs != null && rs.next())
                {
                    cNAccount.setAcctId(rs.getLong("GL_ACCT_ID"));
                    cNAccount.setBranch(queryBusinessUnit(rs.getLong("BU_ID")));
                    cNAccount.setAccountNumber(rs.getString("GL_ACCT_NO"));
                    cNAccount.setShortName(rs.getString("ACCT_DESC"));
                    cNAccount.setAccountName(rs.getString("ACCT_DESC"));
                    cNAccount.getCurrency().setCurrencyCode("ALL");
                    cNAccount.setStatus(rs.getString("REC_ST"));
                    cNAccount.setCustCat("GL");
                }
            }
            catch (Exception ex)
            {
                getLog().logEvent(ex);
            }
        }
        return cNAccount;
    }
    
    public LNDetail queryLoanDetail(CNAccount cNAccount)
    {
        LNDetail lNDetail = new LNDetail();
        try (ResultSet rs = executeQueryToResultSet("SELECT * FROM (SELECT A.ACCT_NO, PI.PRINCIPAL_REPAY_ACCT_ID RPMT_ACCT_ID, (SELECT NVL(SUM(RT.REPMNT_AMT),0) FROM " + APController.coreSchemaName + ".LN_ACCT_REPMNT_EVENT RT WHERE RT.ACCT_ID=R.ACCT_ID AND RT.DUE_DT=R.DUE_DT AND RT.REC_ST IN ('N','P')) AS REPMNT_AMT, R.DUE_DT, Y.CRNCY_CD, (SELECT NVL(SUM(RM.AMT_UNPAID),0) FROM " + APController.coreSchemaName + ".LN_ACCT_REPMNT_EVENT RM WHERE RM.EVENT_TYPE IN('PRINCIPAL','CHARGE','INTEREST') AND RM.DUE_DT < (SELECT TO_DATE(DISPLAY_VALUE,'DD/MM/YYYY') FROM " + APController.coreSchemaName + ".CTRL_PARAMETER WHERE PARAM_CD = 'S02') AND RM.ACCT_ID = A.ACCT_ID AND RM.REC_ST IN ('P','N')) AS AMT_UNPAID, NVL(Q.CLEARED_BAL,0) AS CLEARED_BAL FROM " + APController.coreSchemaName + ".ACCOUNT A LEFT OUTER JOIN " + APController.coreSchemaName + ".LOAN_ACCOUNT_PAYMENT_INFO PI ON PI.ACCT_ID=A.ACCT_ID, " + APController.coreSchemaName + ".CUSTOMER C, " + APController.coreSchemaName + ".LN_ACCT_REPMNT_EVENT R, " + APController.coreSchemaName + ".LOAN_ACCOUNT_SUMMARY Q, " + APController.coreSchemaName + ".BUSINESS_UNIT B, " + APController.coreSchemaName + ".CURRENCY Y WHERE C.CUST_ID = A.CUST_ID AND B.BU_ID = A.MAIN_BRANCH_ID AND A.ACCT_ID = R.ACCT_ID AND Y.CRNCY_ID = A.CRNCY_ID AND R.EVENT_TYPE IN('PRINCIPAL') AND R.REC_ST IN ('P','N') AND A.REC_ST in ('A','C','D','I') AND Q.LAST_DISBURSEMENT_DT IS NOT NULL AND Q.ACCT_ID = A.ACCT_ID AND A.ACCT_NO = '" + cNAccount.getAccountNumber() + "' ORDER BY R.DUE_DT ASC) WHERE ROWNUM=1"))
        {
            if (rs != null && rs.next())
            {
                lNDetail.setPaymentDueDate(rs.getDate("DUE_DT"));
                lNDetail.setMobileNumber(queryMobileContact(cNAccount.getCustId()));
                lNDetail.setRepaymentAmount(rs.getBigDecimal("REPMNT_AMT"));
                lNDetail.setUnpaidAmount(rs.getBigDecimal("AMT_UNPAID"));
                lNDetail.setClearedBalance(rs.getBigDecimal("CLEARED_BAL"));
                lNDetail.setLoanAccount(queryLoanAccount(rs.getString("ACCT_NO")));
                lNDetail.setRepaymentAccount(queryDepositAccount(rs.getLong("RPMT_ACCT_ID")));
            }
        }
        catch (Exception ex)
        {
            getLog().logEvent(ex);
        }
        return lNDetail;
    }
    
    private CNCustomer readCustomer(final ResultSet rs) throws SQLException
    {
        CNCustomer cNCustomer = new CNCustomer();
        cNCustomer.setCustId(rs.getLong("CUST_ID"));
        cNCustomer.setBuId(rs.getLong("MAIN_BRANCH_ID"));
        cNCustomer.setCustNo(rs.getString("CUST_NO"));
        cNCustomer.setCustCat(rs.getString("CUST_CAT"));
        cNCustomer.setCustName(rs.getString("CUST_NM"));
        cNCustomer.setMobileNumber(queryMobileContact(cNCustomer.getCustId()));
        return cNCustomer;
    }
    
    public CNCustomer queryCustomerByNumber(String customerNumber)
    {
        CNCustomer cNCustomer = new CNCustomer();
        try (ResultSet rs = executeQueryToResultSet("SELECT C.CUST_ID, C.CUST_NO, C.MAIN_BRANCH_ID, C.CUST_CAT, C.CUST_NM FROM " + APController.coreSchemaName + ".CUSTOMER C WHERE C.CUST_NO LIKE '%" + customerNumber + "'"))
        {
            if (rs != null && rs.next())
            {
                cNCustomer = readCustomer(rs);
            }
        }
        catch (Exception ex)
        {
            getLog().logEvent(ex);
        }
        return cNCustomer;
    }
    
    public CNCustomer queryCustomerById(Long custId)
    {
        CNCustomer cNCustomer = new CNCustomer();
        try (ResultSet rs = executeQueryToResultSet("SELECT C.CUST_ID, C.CUST_NO, C.MAIN_BRANCH_ID, C.CUST_CAT, C.CUST_NM FROM " + APController.coreSchemaName + ".CUSTOMER C WHERE C.CUST_ID=" + custId))
        {
            if (rs != null && rs.next())
            {
                cNCustomer = readCustomer(rs);
            }
        }
        catch (Exception ex)
        {
            getLog().logEvent(ex);
        }
        return cNCustomer;
    }
    
    public CNCustomer queryCustomerByAccessCode(Long schemeId, String accessCode)
    {
        CNCustomer cNCustomer = new CNCustomer();
        try (ResultSet rs = executeQueryToResultSet("SELECT C.CUST_ID, C.CUST_NO, C.MAIN_BRANCH_ID, C.CUST_CAT, C.CUST_NM FROM " + APController.coreSchemaName + ".CUSTOMER C, " + APController.coreSchemaName + ".CUSTOMER_CHANNEL_USER U WHERE C.CUST_ID=U.CUST_ID AND U.CHANNEL_SCHEME_ID = " + schemeId + " AND U.ACCESS_CD='" + accessCode + "' AND U.REC_ST IN ('A','S')"))
        {
            if (rs != null && rs.next())
            {
                cNCustomer = readCustomer(rs);
            }
        }
        catch (Exception ex)
        {
            getLog().logEvent(ex);
        }
        return cNCustomer;
    }
    
    public ArrayList<String> queryActiveAccessCodes(Long schemeId, Long custId)
    {
        ArrayList<String> accessCodes = new ArrayList<>();
        try (ResultSet rs = executeQueryToResultSet("SELECT U.ACCESS_CD FROM " + APController.coreSchemaName + ".CUSTOMER_CHANNEL_USER U WHERE U.CUST_ID=" + custId + " AND U.CHANNEL_SCHEME_ID = " + schemeId + " AND U.REC_ST IN ('A')"))
        {
            if (rs != null)
            {
                while (rs.next())
                {
                    accessCodes.add(rs.getString("ACCESS_CD"));
                }
            }
        }
        catch (Exception ex)
        {
            getLog().logEvent(ex);
        }
        return accessCodes;
    }
    
    public HashMap<Long, CLItem> queryCustomListItems(Long listId)
    {
        HashMap<Long, CLItem> itemsList = new HashMap<>();
        try (ResultSet rs = executeQueryToResultSet("SELECT ROWNUM, CUSTOM_LIST_ITEM_ID, CUSTOM_LIST_ITEM_CD, INITCAP(CUSTOM_LIST_ITEM_DESC) AS CUSTOM_LIST_ITEM_DESC FROM " + APController.coreSchemaName + ".CUSTOM_LIST_ITEM WHERE CUSTOM_LIST_ID=" + listId))
        {
            if (rs != null)
            {
                while (rs.next())
                {
                    itemsList.put(rs.getLong("CUSTOM_LIST_ITEM_ID"), new CLItem(rs.getInt("ROWNUM"), rs.getLong("CUSTOM_LIST_ITEM_ID"), rs.getString("CUSTOM_LIST_ITEM_CD"), rs.getString("CUSTOM_LIST_ITEM_DESC")));
                }
            }
        }
        catch (Exception ex)
        {
            getLog().logEvent(ex);
        }
        return itemsList;
    }
    
    public boolean upsertCustomField(Long fieldId, Long parentId, String fieldValue)
    {
        if (executeUpdate("MERGE INTO " + APController.coreSchemaName + ".UDS_FIELD_VALUE D USING (SELECT " + queryCustomEntityId() + " AS UDS_FIELD_VALUE_ID, " + fieldId + " AS FIELD_ID, " + parentId + " AS PARENT_ID, '" + fieldValue + "' AS FIELD_VALUE, 'A' AS REC_ST, 1 AS VERSION_NO, SYSDATE AS ROW_TS, 'SYSTEM' AS USER_ID, SYSDATE AS CREATE_DT, 'SYSTEM' AS CREATED_BY, SYSDATE AS SYS_CREATE_TS FROM DUAL) S ON (D.FIELD_ID = S.FIELD_ID AND D.PARENT_ID=S.PARENT_ID) WHEN MATCHED THEN UPDATE SET D.FIELD_VALUE = S.FIELD_VALUE WHEN NOT MATCHED THEN INSERT (UDS_FIELD_VALUE_ID, FIELD_ID, PARENT_ID, FIELD_VALUE, REC_ST, VERSION_NO, ROW_TS, USER_ID, CREATE_DT, CREATED_BY, SYS_CREATE_TS) VALUES(S.UDS_FIELD_VALUE_ID, S.FIELD_ID, S.PARENT_ID, S.FIELD_VALUE, S.REC_ST, S.VERSION_NO, S.ROW_TS, S.USER_ID, S.CREATE_DT, S.CREATED_BY, S.SYS_CREATE_TS)", true))
        {
            return updateCustomEntityId();
        }
        return false;
    }
    
    public CNAccount queryChannelLedger(Long channelId, CNBranch cNBranch)
    {
        String drContraGL = null;
        try (ResultSet rs = executeQueryToResultSet("SELECT GL_DR_ACCT FROM " + APController.coreSchemaName + ".SERVICE_CHANNEL WHERE CHANNEL_ID=" + channelId))
        {
            if (rs != null && rs.next())
            {
                drContraGL = rs.getString("GL_DR_ACCT");
            }
        }
        catch (Exception ex)
        {
            getLog().logEvent(ex);
        }
        return unmaskLedger(drContraGL, cNBranch);
    }
    
    private boolean updateCustomEntityId()
    {
        return executeUpdate("UPDATE " + APController.coreSchemaName + ".ENTITY SET NEXT_NO=(SELECT MAX(UDS_FIELD_VALUE_ID)+1 FROM " + APController.coreSchemaName + ".UDS_FIELD_VALUE) WHERE ENTITY_NM = 'UDS_FIELD_VALUE'", true);
    }
    
    private Long queryCustomEntityId()
    {
        Long entityId = 0L;
        try (ResultSet rs = executeQueryToResultSet("SELECT MAX(UDS_FIELD_VALUE_ID)+1 AS NEXT_NO FROM " + APController.coreSchemaName + ".UDS_FIELD_VALUE"))
        {
            if (rs != null && rs.next())
            {
                entityId = rs.getLong("NEXT_NO");
            }
        }
        catch (Exception ex)
        {
            getLog().logEvent(ex);
        }
        return entityId;
    }
    
    private Long nextSequenceId(String sequence)
    {
        Long seqId = null;
        try (ResultSet rs = executeQueryToResultSet("SELECT " + APController.cmSchemaName + "." + sequence + ".NEXTVAL FROM DUAL"))
        {
            if (rs != null && rs.next())
            {
                seqId = rs.getLong("NEXTVAL");
            }
        }
        catch (Exception ex)
        {
            getLog().logEvent(ex);
        }
        return seqId;
    }
    
    private boolean saveTiers(String parentKey, HashMap<BigDecimal, AXTier> tiers)
    {
        boolean RC = deleteTiers(parentKey);
        for (AXTier tier : tiers.values())
        {
            RC = executeUpdate("INSERT INTO " + APController.cmSchemaName + ".PHC_TIER(REC_ID, PARENT_KEY, TIER_MAX, TIER_VALUE) VALUES(" + APController.cmSchemaName + ".SEQ_PHC_TIER.NEXTVAL, '" + parentKey + "', " + tier.getTierMax() + ", " + tier.getValue() + ")", true);
        }
        return RC;
    }
    
    private boolean deleteTiers(String parentKey)
    {
        return executeUpdate("DELETE " + APController.cmSchemaName + ".PHC_TIER WHERE PARENT_KEY='" + parentKey + "'", true);
    }
    
    private HashMap<BigDecimal, AXTier> queryTiers(String parentKey)
    {
        HashMap<BigDecimal, AXTier> tiers = new HashMap<>();
        try (ResultSet rs = executeQueryToResultSet("SELECT REC_ID, PARENT_KEY, TIER_MAX, TIER_VALUE FROM " + APController.cmSchemaName + ".PHC_TIER WHERE PARENT_KEY='" + parentKey + "' ORDER BY TIER_MAX ASC"))
        {
            if (rs != null)
            {
                while (rs.next())
                {
                    AXTier tier = new AXTier();
                    tier.setTierId(rs.getLong("REC_ID"));
                    tier.setTierMax(rs.getBigDecimal("TIER_MAX"));
                    tier.setValue(rs.getBigDecimal("TIER_VALUE"));
                    tiers.put(tier.getTierMax(), tier);
                }
            }
        }
        catch (Exception ex)
        {
            getLog().logEvent(ex);
        }
        return tiers;
    }
    
    private HashMap<Long, TCDeduction> queryDeductions(String parentKey)
    {
        HashMap<Long, TCDeduction> deductions = new HashMap<>();
        try (ResultSet rs = executeQueryToResultSet("SELECT REC_ID, PARENT_KEY, BASIS, DESCRIPTION, ACCOUNT, VALUE_TYPE, VALUE FROM " + APController.cmSchemaName + ".PHC_DEDUCTION WHERE PARENT_KEY='" + parentKey + "' ORDER BY REC_ID ASC"))
        {
            if (rs != null)
            {
                while (rs.next())
                {
                    TCDeduction deduction = new TCDeduction();
                    deduction.setRecId(rs.getLong("REC_ID"));
                    deduction.setBasis(rs.getString("BASIS"));
                    deduction.setDescription(rs.getString("DESCRIPTION"));
                    deduction.setAccount(rs.getString("ACCOUNT"));
                    deduction.setValueType(rs.getString("VALUE_TYPE"));
                    deduction.setValue(rs.getBigDecimal("VALUE"));
                    deductions.put(deduction.getRecId(), deduction);
                }
            }
        }
        catch (Exception ex)
        {
            getLog().logEvent(ex);
        }
        return deductions;
    }
    
    public boolean upsertAlert(MXAlert mXAlert)
    {
        return checkExists("SELECT ALERT_CODE FROM " + APController.cmSchemaName + ".PHA_ALERT WHERE ALERT_CODE='" + mXAlert.getAlertCode() + "'")
                ? updateAlert(mXAlert)
                : saveAlert(mXAlert);
    }
    
    private boolean saveAlert(MXAlert mXAlert)
    {
        if (executeUpdate("INSERT INTO " + APController.cmSchemaName + ".PHA_ALERT(REC_ID, ALERT_CODE, CREATE_DT, ALERT_TYPE, ALERT_DESC, PRIORITY, RUN_TIME, CHRG_CODE, FREQUENCY, ALERT_DAYS, NEXT_DATE, LAST_DATE, EXPIRY_DATE, FILTER_BY, REC_ST) VALUES(" + APController.cmSchemaName + ".SEQ_PHA_ALERT.NEXTVAL, '" + mXAlert.getAlertCode() + "', SYSDATE, '" + mXAlert.getAlertType() + "', '" + mXAlert.getDescription() + "', " + mXAlert.getPriority() + ", '" + mXAlert.getRunTime() + "', '" + mXAlert.getChargeCode() + "', '" + mXAlert.getFrequency() + "', '" + mXAlert.getAlertDays() + "', " + getWorker().convertToOracleDate(mXAlert.getNextDate()) + ", " + getWorker().convertToOracleDate(mXAlert.getPreviousDate()) + ", " + getWorker().convertToOracleDate(mXAlert.getExpiryDate()) + ", '" + mXAlert.getFilterBy() + "', '" + mXAlert.getStatus() + "')", true))
        {
            return saveTemplates(ALController.channel, mXAlert.getAlertCode(), mXAlert.getTemplates()) && saveFilters(ALController.channel, mXAlert.getAlertCode(), mXAlert.getFilters());
        }
        return false;
    }
    
    private boolean updateAlert(MXAlert mXAlert)
    {
        if (executeUpdate("UPDATE " + APController.cmSchemaName + ".PHA_ALERT SET ALERT_CODE='" + mXAlert.getAlertCode() + "', ALERT_TYPE='" + mXAlert.getAlertType() + "', ALERT_DESC='" + mXAlert.getDescription() + "', PRIORITY=" + mXAlert.getPriority() + ", RUN_TIME='" + mXAlert.getRunTime() + "', CHRG_CODE='" + mXAlert.getChargeCode() + "', FREQUENCY='" + mXAlert.getFrequency() + "', NEXT_DATE=" + getWorker().convertToOracleDate(mXAlert.getNextDate()) + ", LAST_DATE=" + getWorker().convertToOracleDate(mXAlert.getPreviousDate()) + ", EXPIRY_DATE=" + getWorker().convertToOracleDate(mXAlert.getExpiryDate()) + ", FILTER_BY='" + mXAlert.getFilterBy() + "', REC_ST='" + mXAlert.getStatus() + "' WHERE ALERT_CODE='" + mXAlert.getAlertCode() + "'", true))
        {
            return saveTemplates(ALController.channel, mXAlert.getAlertCode(), mXAlert.getTemplates()) && saveFilters(ALController.channel, mXAlert.getAlertCode(), mXAlert.getFilters());
        }
        return false;
    }
    
    public boolean pushAlertDate(MXAlert mXAlert)
    {
        return executeUpdate("UPDATE " + APController.cmSchemaName + ".PHA_ALERT SET NEXT_DATE=" + getWorker().convertToOracleDate(mXAlert.getNextDate()) + ", LAST_DATE=" + getWorker().convertToOracleDate(mXAlert.getPreviousDate()) + ", REC_ST='" + mXAlert.getStatus() + "' WHERE ALERT_CODE='" + mXAlert.getAlertCode() + "'", true);
    }
    
    public boolean updateAlertStatus(MXAlert mXAlert)
    {
        return executeUpdate("UPDATE " + APController.cmSchemaName + ".PHA_ALERT SET REC_ST='" + mXAlert.getStatus() + "' WHERE ALERT_CODE='" + mXAlert.getAlertCode() + "'", true);
    }
    
    public boolean upsertSetting(AXSetting setting)
    {
        return checkExists("SELECT CODE FROM " + APController.cmSchemaName + ".PHL_SETTING WHERE CODE='" + setting.getCode() + "' AND CHANNEL='" + setting.getChannel() + "'") ? updateSetting(setting) : saveSetting(setting);
    }
    
    public boolean saveSetting(AXSetting setting)
    {
        return executeUpdate("INSERT INTO " + APController.cmSchemaName + ".PHL_SETTING(CODE, VALUE, CHANNEL, DESCRIPTION, SYS_USER, SYS_DATE) VALUES('" + setting.getCode() + "', '" + (setting.isEncrypted() && !getCrypt().isEncrypted(setting.getValue()) ? getCrypt().encrypt(setting.getValue()) : setting.getValue()) + "', '" + setting.getChannel() + "', '" + setting.getDescription() + "', '" + setting.getSysUser() + "', SYSDATE)", true);
    }
    
    public boolean updateSetting(AXSetting setting)
    {
        return executeUpdate("UPDATE " + APController.cmSchemaName + ".PHL_SETTING SET VALUE='" + (setting.isEncrypted() && !getCrypt().isEncrypted(setting.getValue()) ? getCrypt().encrypt(setting.getValue()) : setting.getValue()) + "', DESCRIPTION='" + setting.getDescription() + "', SYS_USER='" + setting.getSysUser() + "', SYS_DATE=SYSDATE WHERE CODE='" + setting.getCode() + "' AND CHANNEL='" + setting.getChannel() + "'", true);
    }
    
    public boolean deleteSetting(AXSetting setting)
    {
        return executeUpdate("DELETE " + APController.cmSchemaName + ".PHL_SETTING WHERE CODE='" + setting.getCode() + "' AND CHANNEL='" + setting.getChannel() + "'", true);
    }
    
    public boolean upsertTxn(AXTxn axTxn)
    {
        return getWorker().isBlank(axTxn.getRecId()) ? saveTxn(axTxn) : updateTxn(axTxn);
    }
    
    private boolean saveTxn(AXTxn axTxn)
    {
        return executeUpdate("INSERT INTO " + APController.cmSchemaName + ".PHL_TXN_LOG(REC_ID, TXN_REF, TXN_DATE, CHANNEL_ID, CHANNEL, CLIENT, TXN_CODE, TXN_TYPE, BU_ID, ACQUIRER, TERMINAL, ADVICE, ONUS, ACCESS_CD, ACCOUNT, CONTRA, CURRENCY, AMOUNT, DESCRIPTION, DETAIL, CHARGE_LEDGER, CHARGE, TXN_ID, CHG_ID, BALANCE, XAPI_CODE, RESULT, RESP_CODE, REC_ST) "
                + "VALUES(" + APController.cmSchemaName + ".SEQ_PHL_TXN_LOG.NEXTVAL, '" + axTxn.getTxnRef() + "', SYSDATE, " + axTxn.getChannelId() + ", '" + axTxn.getChannel() + "', '" + axTxn.getClient() + "', '" + axTxn.getTxnCode() + "', '" + axTxn.getTxnType() + "', " + axTxn.getBuId() + ", '" + axTxn.getAcquirer() + "', '" + axTxn.getTerminal() + "', '" + axTxn.getAdvice() + "', '" + axTxn.getOnus() + "', '" + axTxn.getAccessCd() + "', '" + axTxn.getAccount() + "', '" + axTxn.getContra() + "', '" + axTxn.getCurrency() + "', " + axTxn.getAmount() + ", '" + axTxn.getDescription() + "', '" + axTxn.getDetail() + "', '" + axTxn.getChargeLedger() + "', " + axTxn.getCharge() + ", " + axTxn.getTxnId() + ", " + axTxn.getChgId() + ", " + axTxn.getBalance() + ", '" + axTxn.getXapiCode() + "', '" + axTxn.getResult() + "', '" + axTxn.getRespCode() + "', '" + axTxn.getRecSt() + "')", true);
    }
    
    public boolean updateTxn(AXTxn axTxn)
    {
        return executeUpdate("UPDATE " + APController.cmSchemaName + ".PHL_TXN_LOG SET BALANCE=" + axTxn.getBalance() + ", REC_ST='" + axTxn.getRecSt() + "' WHERE REC_ID=" + axTxn.getRecId(), true);
    }
    
    public TreeMap<String, MXAlert> queryAlerts()
    {
        TreeMap<String, MXAlert> alerts = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        try (ResultSet rs = executeQueryToResultSet("SELECT REC_ID, ALERT_CODE, ALERT_TYPE, ALERT_DESC, PRIORITY, RUN_TIME, CHRG_CODE, FREQUENCY, ALERT_DAYS, NEXT_DATE, LAST_DATE, EXPIRY_DATE, FILTER_BY, REC_ST FROM " + APController.cmSchemaName + ".PHA_ALERT ORDER BY ALERT_CODE ASC"))
        {
            if (rs != null)
            {
                while (rs.next())
                {
                    MXAlert mXAlert = new MXAlert();
                    mXAlert.setRecId(rs.getLong("REC_ID"));
                    mXAlert.setAlertCode(rs.getString("ALERT_CODE"));
                    mXAlert.setAlertType(rs.getString("ALERT_TYPE"));
                    mXAlert.setDescription(rs.getString("ALERT_DESC"));
                    mXAlert.setPriority(rs.getInt("PRIORITY"));
                    mXAlert.setRunTime(rs.getString("RUN_TIME"));
                    mXAlert.setAlertDays(rs.getString("ALERT_DAYS"));
                    mXAlert.setChargeCode(rs.getString("CHRG_CODE"));
                    mXAlert.setFrequency(rs.getString("FREQUENCY"));
                    mXAlert.setNextDate(rs.getDate("NEXT_DATE"));
                    mXAlert.setPreviousDate(rs.getDate("LAST_DATE"));
                    mXAlert.setExpiryDate(rs.getDate("EXPIRY_DATE"));
                    mXAlert.setFilterBy(rs.getString("FILTER_BY"));
                    mXAlert.setStatus(rs.getString("REC_ST"));
                    mXAlert.setTemplates(queryTemplates(ALController.channel, mXAlert.getAlertCode()));
                    mXAlert.setFilters(queryFilters(ALController.channel, mXAlert.getAlertCode()));
                    alerts.put(mXAlert.getAlertCode(), mXAlert);
                }
            }
        }
        catch (Exception ex)
        {
            getLog().logEvent(ex);
        }
        return alerts;
    }
    
    public TreeMap<String, AXSetting> querySettings(String module)
    {
        TreeMap<String, AXSetting> settings = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        try (ResultSet rs = executeQueryToResultSet("SELECT CODE, VALUE, CHANNEL, DESCRIPTION, SYS_USER, SYS_DATE FROM " + APController.cmSchemaName + ".PHL_SETTING WHERE CHANNEL LIKE '" + module + "' ORDER BY CODE ASC"))
        {
            if (rs != null)
            {
                while (rs.next())
                {
                    AXSetting setting = new AXSetting();
                    setting.setCode(rs.getString("CODE"));
                    setting.setEncrypted(getCrypt().isEncrypted(rs.getString("VALUE")));
                    setting.setValue(setting.isEncrypted() ? getCrypt().decrypt(rs.getString("VALUE")) : rs.getString("VALUE"));
                    setting.setChannel(rs.getString("CHANNEL"));
                    setting.setDescription(rs.getString("DESCRIPTION"));
                    setting.setSysUser(rs.getString("SYS_USER"));
                    setting.setSysDate(rs.getDate("SYS_DATE"));
                    settings.put(setting.getCode(), setting);
                }
            }
        }
        catch (Exception ex)
        {
            getLog().logEvent(ex);
        }
        return settings;
    }
    
    public boolean upsertTerminal(AXTerminal terminal)
    {
        return checkExists("SELECT REC_CD FROM " + APController.cmSchemaName + ".PHT_TERMINAL WHERE REC_CD='" + terminal.getTerminalId() + "' AND CHANNEL='" + terminal.getChannel() + "'") ? updateTerminal(terminal) : saveTerminal(terminal);
    }
    
    private boolean saveTerminal(AXTerminal terminal)
    {
        return executeUpdate("INSERT INTO " + APController.cmSchemaName + ".PHT_TERMINAL(REC_CD, CHANNEL, SCHEME, LOCATION, OPERATOR, BU_NO, SYS_USER, SYS_DATE, REC_ST) VALUES('" + terminal.getTerminalId() + "', '" + terminal.getChannel() + "', '" + terminal.getScheme() + "', '" + terminal.getLocation().replaceAll("'", "''") + "', '" + terminal.getOperator() + "', '" + terminal.getBuCode() + "', '" + terminal.getSysUser() + "', SYSDATE, '" + terminal.getStatus() + "')", true) && saveTerminalAccounts(terminal);
    }
    
    private boolean updateTerminal(AXTerminal terminal)
    {
        return executeUpdate("UPDATE " + APController.cmSchemaName + ".PHT_TERMINAL SET CHANNEL='" + terminal.getChannel() + "', SCHEME='" + terminal.getScheme() + "', LOCATION='" + terminal.getLocation() + "', OPERATOR='" + terminal.getOperator() + "', BU_NO='" + terminal.getBuCode() + "', SYS_USER='" + terminal.getSysUser() + "', SYS_DATE=SYSDATE, REC_ST='" + terminal.getStatus() + "' WHERE REC_CD='" + terminal.getTerminalId() + "'", true) ? saveTerminalAccounts(terminal) : false;
    }
    
    private boolean saveTerminalAccounts(AXTerminal terminal)
    {
        boolean RC = deleteTerminalAccounts(terminal);
        for (String currency : terminal.getAccounts().keySet())
        {
            RC = executeUpdate("INSERT INTO " + APController.cmSchemaName + ".PHT_ACCOUNT(REC_ID, PARENT_KEY, CURRENCY, ACCOUNT) VALUES(" + APController.cmSchemaName + ".SEQ_PHT_ACCOUNT.NEXTVAL, '" + terminal.getTerminalId() + "', '" + currency + "', '" + terminal.getAccounts().get(currency).getAccountNumber() + "')", true);
        }
        return RC;
    }
    
    private boolean deleteTerminalAccounts(AXTerminal terminal)
    {
        return executeUpdate("DELETE " + APController.cmSchemaName + ".PHT_ACCOUNT WHERE PARENT_KEY='" + terminal.getTerminalId() + "'", true);
    }
    
    public TreeMap<String, AXTerminal> queryTerminals(String channel)
    {
        TreeMap<String, AXTerminal> terminals = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        try (ResultSet rs = executeQueryToResultSet("SELECT REC_CD, CHANNEL, SCHEME, LOCATION, OPERATOR, BU_NO, SYS_USER, SYS_DATE, REC_ST FROM " + APController.cmSchemaName + ".PHT_TERMINAL WHERE CHANNEL='" + channel + "' ORDER BY REC_CD ASC"))
        {
            if (rs != null)
            {
                while (rs.next())
                {
                    AXTerminal terminal = new AXTerminal();
                    terminal.setTerminalId(rs.getString("REC_CD"));
                    terminal.setChannel(rs.getString("CHANNEL"));
                    terminal.setScheme(rs.getString("SCHEME"));
                    terminal.setLocation(rs.getString("LOCATION"));
                    terminal.setOperator(rs.getString("OPERATOR"));
                    terminal.setBuCode(rs.getString("BU_NO"));
                    terminal.setSysUser(rs.getString("SYS_USER"));
                    terminal.setSysDate(rs.getDate("SYS_DATE"));
                    terminal.setStatus(rs.getString("REC_ST"));
                    terminals.put(terminal.getTerminalId(), terminal);
                }
            }
            terminals.values().stream().map((terminal)
                    ->
            {
                terminal.setAccounts(queryTerminalAccounts(terminal.getTerminalId(), true));
                return terminal;
            }).forEach((terminal)
                    ->
            {
                terminals.put(terminal.getTerminalId(), terminal);
            });
        }
        catch (Exception ex)
        {
            getLog().logEvent(ex);
        }
        return terminals;
    }
    
    public AXTerminal queryTerminal(String channel, String terminalId, boolean retry)
    {
        AXTerminal terminal = new AXTerminal();
        try (ResultSet rs = executeQueryToResultSet("SELECT REC_CD, CHANNEL, SCHEME, LOCATION, OPERATOR, BU_NO, SYS_USER, SYS_DATE, REC_ST FROM " + APController.cmSchemaName + ".PHT_TERMINAL WHERE CHANNEL='" + channel + "' AND REC_CD='" + terminalId + "' AND REC_ST='A' ORDER BY REC_CD ASC"))
        {
            if (rs != null && rs.next())
            {
                terminal.setTerminalId(rs.getString("REC_CD"));
                terminal.setChannel(rs.getString("CHANNEL"));
                terminal.setScheme(rs.getString("SCHEME"));
                terminal.setLocation(rs.getString("LOCATION"));
                terminal.setOperator(rs.getString("OPERATOR"));
                terminal.setBuCode(rs.getString("BU_NO"));
                terminal.setSysUser(rs.getString("SYS_USER"));
                terminal.setSysDate(rs.getDate("SYS_DATE"));
                terminal.setStatus(rs.getString("REC_ST"));
                terminal.setAccounts(queryTerminalAccounts(terminal.getTerminalId(), true));
            }
        }
        catch (Exception ex)
        {
            if (retry && isRecoverable(ex))
            {
                return queryTerminal(channel, terminalId, false);
            }
            getLog().logEvent(ex);
        }
        return terminal;
    }
    
    public HashMap<String, CNAccount> queryTerminalAccounts(String terminalId, boolean retry)
    {
        HashMap<String, CNAccount> accounts = new HashMap<>();
        try (ResultSet rs = executeQueryToResultSet("SELECT REC_ID, CURRENCY, ACCOUNT FROM " + APController.cmSchemaName + ".PHT_ACCOUNT WHERE PARENT_KEY='" + terminalId + "' ORDER BY REC_ID ASC"))
        {
            if (rs != null)
            {
                while (rs.next())
                {
                    accounts.put(rs.getString("CURRENCY"), queryAnyAccount(rs.getString("ACCOUNT")));
                }
            }
        }
        catch (Exception ex)
        {
            if (retry && isRecoverable(ex))
            {
                return queryTerminalAccounts(terminalId, false);
            }
            getLog().logEvent(ex);
        }
        return accounts;
    }
    
    public void updateXapiErrors()
    {
        try (ResultSet rs = executeQueryToResultSet("SELECT ERROR_CODE, ERROR_DESC FROM " + APController.coreSchemaName + ".ERROR_CODE_DESCRIPTION_REF ORDER BY ERROR_CODE ASC"))
        {
            if (rs != null)
            {
                while (rs.next())
                {
                    if (!APController.getXcodes().containsKey(rs.getString("ERROR_CODE")))
                    {
                        APController.getXcodes().put(rs.getString("ERROR_CODE"), rs.getString("ERROR_DESC"));
                    }
                }
            }
        }
        catch (Exception ex)
        {
            getLog().logEvent(ex);
        }
    }
    
    public HashMap<String, String> queryTemplates(String channel, String parentKey)
    {
        HashMap<String, String> templates = new HashMap<>();
        try (ResultSet rs = executeQueryToResultSet("SELECT LANG_CODE, TEMPLATE FROM " + APController.cmSchemaName + ".PHL_TEMPLATE WHERE CHANNEL='" + channel + "' AND PARENT_KEY='" + parentKey + "' ORDER BY LANG_CODE ASC"))
        {
            if (rs != null)
            {
                while (rs.next())
                {
                    if (rs.getString("TEMPLATE") != null)
                    {
                        templates.put(rs.getString("LANG_CODE"), rs.getString("TEMPLATE"));
                    }
                }
            }
        }
        catch (Exception ex)
        {
            getLog().logEvent(ex);
        }
        return templates;
    }
    
    public ArrayList<String> queryFilters(String channel, String parentKey)
    {
        ArrayList<String> filters = new ArrayList<>();
        try (ResultSet rs = executeQueryToResultSet("SELECT DISTINCT FILTER FROM " + APController.cmSchemaName + ".PHL_FILTER WHERE CHANNEL='" + channel + "' AND PARENT_KEY='" + parentKey + "'"))
        {
            if (rs != null)
            {
                while (rs.next())
                {
                    if (rs.getString("FILTER") != null)
                    {
                        filters.add(rs.getString("FILTER"));
                    }
                }
            }
        }
        catch (Exception ex)
        {
            getLog().logEvent(ex);
        }
        return filters;
    }
    
    private boolean saveTemplates(String channel, String parentKey, HashMap<String, String> templates)
    {
        boolean RC = false;
        if (!templates.isEmpty())
        {
            RC = deleteTemplates(channel, parentKey);
        }
        for (String langCode : templates.keySet())
        {
            RC = executeUpdate("INSERT INTO " + APController.cmSchemaName + ".PHL_TEMPLATE(REC_ID, PARENT_KEY, CHANNEL, LANG_CODE, TEMPLATE) VALUES(" + APController.cmSchemaName + ".SEQ_PHL_TEMPLATE.NEXTVAL, '" + parentKey + "', '" + channel + "', '" + langCode + "', '" + String.valueOf(templates.get(langCode)).replace("'", "''") + "')", true);
        }
        return RC;
    }
    
    private boolean deleteTemplates(String channel, String parentKey)
    {
        return executeUpdate("DELETE " + APController.cmSchemaName + ".PHL_TEMPLATE WHERE CHANNEL='" + channel + "' AND PARENT_KEY='" + parentKey + "'", true);
    }
    
    private boolean saveFilters(String channel, String parentKey, ArrayList<String> filters)
    {
        boolean RC = deleteFilters(channel, parentKey);
        for (String filter : filters)
        {
            RC = executeUpdate("INSERT INTO " + APController.cmSchemaName + ".PHL_FILTER(REC_ID, PARENT_KEY, CHANNEL, FILTER) VALUES(" + APController.cmSchemaName + ".SEQ_PHL_FILTER.NEXTVAL, '" + parentKey + "', '" + channel + "', '" + filter.replaceAll("'", "''") + "')", true);
        }
        return RC;
    }
    
    private boolean deleteFilters(String channel, String parentKey)
    {
        return executeUpdate("DELETE " + APController.cmSchemaName + ".PHL_FILTER WHERE CHANNEL='" + channel + "' AND PARENT_KEY='" + parentKey + "'", true);
    }
    
    public boolean deleteAlert(MXAlert mXAlert)
    {
        return deleteTemplates(ALController.channel, mXAlert.getAlertCode()) && deleteFilters(ALController.channel, mXAlert.getAlertCode()) && executeUpdate("DELETE " + APController.cmSchemaName + ".PHA_ALERT WHERE ALERT_CODE='" + mXAlert.getAlertCode() + "'", true);
    }
    
    public boolean saveSplit(AXSplit split)
    {
        return executeUpdate("INSERT INTO " + APController.cmSchemaName + ".PHC_SPLIT(REC_ID, TXN_DT, TXN_REF, CHANNEL, DR_ACCT, CR_ACCT, CURRENCY, AMOUNT, DESCRIPTION, REVERSAL, REC_ST) VALUES(" + APController.cmSchemaName + ".SEQ_PHC_SPLIT.NEXTVAL, SYSDATE, '" + split.getTxnRef() + "', '" + split.getChannel() + "', '" + split.getDebitAccount() + "', '" + split.getCreditAccount() + "', '" + split.getCurrency() + "', " + split.getAmount() + ", '" + split.getDescription() + "', '" + split.getReversal() + "', '" + split.getStatus() + "')", true);
    }
    
    public boolean updateSplit(AXSplit split, String status)
    {
        return executeUpdate("UPDATE " + APController.cmSchemaName + ".PHC_SPLIT SET REC_ST='" + status + "' WHERE REC_ID=" + split.getRecId(), true);
    }
    
    public ArrayList<AXSplit> querySplits()
    {
        ArrayList<AXSplit> splits = new ArrayList<>();
        try (ResultSet rs = executeQueryToResultSet("SELECT REC_ID, TXN_DT, TXN_REF, CHANNEL, DR_ACCT, CR_ACCT, CURRENCY, AMOUNT, DESCRIPTION, REVERSAL, REC_ST FROM " + APController.cmSchemaName + ".PHC_SPLIT WHERE REC_ST IN ('P') AND ROWNUM<=5000"))
        {
            if (rs != null)
            {
                while (rs.next())
                {
                    AXSplit split = new AXSplit();
                    split.setRecId(rs.getLong("REC_ID"));
                    split.setTxnDate(rs.getDate("TXN_DT"));
                    split.setTxnRef(rs.getString("TXN_REF"));
                    split.setChannel(rs.getString("CHANNEL"));
                    split.setDebitAccount(rs.getString("DR_ACCT"));
                    split.setCreditAccount(rs.getString("CR_ACCT"));
                    split.setCurrency(rs.getString("CURRENCY"));
                    split.setAmount(rs.getBigDecimal("AMOUNT"));
                    split.setDescription(rs.getString("DESCRIPTION"));
                    split.setReversal(rs.getString("REVERSAL"));
                    split.setStatus(rs.getString("REC_ST"));
                    splits.add(split);
                }
            }
        }
        catch (Exception ex)
        {
            getLog().logEvent(ex);
        }
        return splits;
    }
    
    public ArrayList<GPSplit> queryGroupSplits()
    {
        ArrayList<GPSplit> splits = new ArrayList<>();
        try (ResultSet rs = executeQueryToResultSet("SELECT CHANNEL, DR_ACCT, CR_ACCT, CURRENCY, SUM(AMOUNT) AS AMOUNT, DESCRIPTION, TRUNC(TXN_DT) AS TXN_DT, REVERSAL FROM " + APController.cmSchemaName + ".PHC_SPLIT WHERE REC_ST IN ('P') AND TRUNC(TXN_DT)<=TRUNC(SYSDATE-1) GROUP BY CHANNEL, DR_ACCT, CR_ACCT, CURRENCY, DESCRIPTION, TRUNC(TXN_DT), REVERSAL"))
        {
            if (rs != null)
            {
                while (rs.next())
                {
                    GPSplit split = new GPSplit();
                    split.setChannel(rs.getString("CHANNEL"));
                    split.setDebitAccount(rs.getString("DR_ACCT"));
                    split.setCreditAccount(rs.getString("CR_ACCT"));
                    split.setCurrency(rs.getString("CURRENCY"));
                    split.setAmount(rs.getBigDecimal("AMOUNT"));
                    split.setDescription(rs.getString("DESCRIPTION"));
                    split.setReversal(rs.getString("REVERSAL"));
                    split.setTxnDate(rs.getDate("TXN_DT"));
                    splits.add(split);
                }
            }
        }
        catch (Exception ex)
        {
            getLog().logEvent(ex);
        }
        return splits;
    }
    
    public boolean updateGroupSplit(GPSplit split, String status)
    {
        return executeUpdate("UPDATE " + APController.cmSchemaName + ".PHC_SPLIT SET REC_ST='" + status + "' WHERE CHANNEL='" + split.getChannel() + "' AND DR_ACCT='" + split.getDebitAccount() + "' AND CR_ACCT='" + split.getCreditAccount() + "' AND CURRENCY='" + split.getCurrency() + "' AND DESCRIPTION='" + split.getDescription() + "' AND REVERSAL='" + split.getReversal() + "' AND TRUNC(TXN_DT)=" + getWorker().convertToOracleDate(split.getTxnDate()), true);
    }
    
    public boolean moveCustomerWFItem(Long custId, Long newBuId)
    {
        return executeUpdate("UPDATE " + APController.coreSchemaName + ".WF_WORK_ITEM SET BU_ID=" + newBuId + " WHERE WORK_ITEM_ID=(SELECT MAX(WORK_ITEM_ID) FROM " + APController.coreSchemaName + ".WF_WORK_ITEM WHERE CUST_ID=" + custId + ")", true);
    }
    
    public CFValue queryCustomField(Long fieldId, Long parentId)
    {
        CFValue cFValue = new CFValue();
        try (ResultSet rs = executeQueryToResultSet("SELECT A.UDS_FIELD_VALUE_ID, A.FIELD_ID, A.PARENT_ID, A.FIELD_VALUE, A.REC_ST FROM " + APController.coreSchemaName + ".V_UDS_FIELD_VALUE A WHERE A.FIELD_ID=" + fieldId + " AND A.PARENT_ID=" + parentId + " AND A.REC_ST='A'"))
        {
            if (rs != null && rs.next())
            {
                cFValue.setValueId(rs.getLong("UDS_FIELD_VALUE_ID"));
                cFValue.setFieldId(rs.getLong("FIELD_ID"));
                cFValue.setParentId(rs.getLong("PARENT_ID"));
                cFValue.setFieldValue(rs.getString("FIELD_VALUE"));
                cFValue.setStatus(rs.getString("REC_ST"));
            }
        }
        catch (Exception ex)
        {
            getLog().logEvent(ex);
        }
        return cFValue;
    }
    
    public ArrayList<String> queryContacts(Long custId, String contactType)
    {
        ArrayList<String> contacts = new ArrayList<>();
        try (ResultSet rs = executeQueryToResultSet("SELECT O.CONTACT FROM " + APController.coreSchemaName + ".V_CUSTOMER_CONTACT_MODE O WHERE O.CUST_ID = " + custId + " AND O.CONTACT_MODE_CAT_CD IN ('" + contactType + "')"))
        {
            if (rs != null)
            {
                while (rs.next())
                {
                    contacts.add(rs.getString("CONTACT"));
                }
            }
        }
        catch (Exception ex)
        {
            getLog().logEvent(ex);
        }
        return contacts;
    }
    
    public String queryMobileContact(Long custId)
    {
        String contact = null;
        try (ResultSet rs = executeQueryToResultSet("SELECT NVL((SELECT U.ACCESS_CD FROM " + APController.coreSchemaName + ".CUSTOMER_CHANNEL_USER U WHERE U.CUST_ID = " + custId + " AND U.CHANNEL_ID=" + ALController.channelId + " AND U.USER_CAT_CD='PER' AND U.REC_ST='A' AND ROWNUM=1),(SELECT O.CONTACT FROM " + APController.coreSchemaName + ".V_CUSTOMER_CONTACT_MODE O WHERE O.CUST_ID = " + custId + " AND O.CONTACT_MODE_CAT_CD IN ('MOBPHONE','TELPHONE') AND ROWNUM=1)) AS CONTACT FROM DUAL"))
        {
            if (rs != null && rs.next())
            {
                contact = rs.getString("CONTACT");
            }
        }
        catch (Exception ex)
        {
            getLog().logEvent(ex);
        }
        return contact;
    }
    
    public CNCurrency queryCurrency(Long currencyId)
    {
        CNCurrency cNCurrency = new CNCurrency();
        try (ResultSet rs = executeQueryToResultSet("SELECT CRNCY_ID, CRNCY_CD, INITCAP(CRNCY_NM) AS CRNCY_NM, CRNCY_POSN FROM " + APController.coreSchemaName + ".CURRENCY WHERE CRNCY_ID=" + currencyId + " AND REC_ST='A'"))
        {
            if (rs != null && rs.next())
            {
                cNCurrency.setCurrencyId(rs.getLong("CRNCY_ID"));
                cNCurrency.setCurrencyCode(rs.getString("CRNCY_CD"));
                cNCurrency.setCurrencyName(rs.getString("CRNCY_NM"));
                cNCurrency.setDecimalPlaces(rs.getInt("CRNCY_POSN"));
            }
        }
        catch (Exception ex)
        {
            getLog().logEvent(ex);
        }
        return cNCurrency;
    }
    
    public CNCurrency queryCurrency(String currencyCode)
    {
        CNCurrency cNCurrency = new CNCurrency();
        try (ResultSet rs = executeQueryToResultSet("SELECT CRNCY_ID, CRNCY_CD, INITCAP(CRNCY_NM) AS CRNCY_NM, CRNCY_POSN FROM " + APController.coreSchemaName + ".CURRENCY WHERE CRNCY_CD='" + currencyCode + "' AND REC_ST='A'"))
        {
            if (rs != null && rs.next())
            {
                cNCurrency.setCurrencyId(rs.getLong("CRNCY_ID"));
                cNCurrency.setCurrencyCode(rs.getString("CRNCY_CD"));
                cNCurrency.setCurrencyName(rs.getString("CRNCY_NM"));
                cNCurrency.setDecimalPlaces(rs.getInt("CRNCY_POSN"));
            }
        }
        catch (Exception ex)
        {
            getLog().logEvent(ex);
        }
        return cNCurrency;
    }
    
    public ArrayList<CNCurrency> queryCurrencies()
    {
        ArrayList<CNCurrency> currencies = new ArrayList<>();
        try (ResultSet rs = executeQueryToResultSet("SELECT CRNCY_ID, CRNCY_CD, INITCAP(CRNCY_NM) AS CRNCY_NM, CRNCY_POSN FROM " + APController.coreSchemaName + ".CURRENCY WHERE REC_ST='A' ORDER BY CRNCY_ID"))
        {
            if (rs != null)
            {
                while (rs.next())
                {
                    CNCurrency cNCurrency = new CNCurrency();
                    cNCurrency.setCurrencyId(rs.getLong("CRNCY_ID"));
                    cNCurrency.setCurrencyCode(rs.getString("CRNCY_CD"));
                    cNCurrency.setCurrencyName(rs.getString("CRNCY_NM"));
                    cNCurrency.setDecimalPlaces(rs.getInt("CRNCY_POSN"));
                    currencies.add(cNCurrency);
                }
            }
        }
        catch (Exception ex)
        {
            getLog().logEvent(ex);
        }
        return currencies;
    }
    
    public HashMap<String, BigDecimal> queryCustomerChannelLimits(Long custChannelId, Long currencyId)
    {
        HashMap<String, BigDecimal> limits = new HashMap<>();
        try (ResultSet rs = executeQueryToResultSet("SELECT LIMIT_TY_CD, DAILY_LIMIT FROM " + APController.coreSchemaName + ".CUST_CHANNEL_LIMIT WHERE CUST_CHANNEL_ID=" + custChannelId + " AND DAILY_LIMIT IS NOT NULL AND LIMIT_CRNCY_ID=" + currencyId + " AND REC_ST='A'"))
        {
            if (rs != null)
            {
                while (rs.next())
                {
                    limits.put(rs.getString("LIMIT_TY_CD"), rs.getBigDecimal("DAILY_LIMIT"));
                }
            }
        }
        catch (Exception ex)
        {
            getLog().logEvent(ex);
        }
        return limits;
    }
    
    public BNUser queryBankUser(String userName, String password, Long channelId)
    {
        BNUser user = new BNUser();
        try (ResultSet rs = executeQueryToResultSet("SELECT A.SYSUSER_ID, A.LOGIN_ID, A.FIRST_NM || ' ' || A.LAST_NM AS NAME, A.EMP_NO, A.MAIN_BRANCH_ID, A.BU_NM, A.ROLE_ID, A.ROLE_NM FROM " + APController.coreSchemaName + ".V_SYSUSER A, " + APController.coreSchemaName + ".SYSPWD_HIST B WHERE A.LOCKED_FG='N' AND A.LOGIN_ID='" + userName + "' AND B.SYSUSER_ID=A.SYSUSER_ID AND A.REC_ST=B.REC_ST AND B.PASSWD='" + getCrypt().encrypt(password) + "' AND B.REC_ST='A'"))
        {
            if (rs != null)
            {
                while (rs.next())
                {
                    user.setUserId(rs.getLong("SYSUSER_ID"));
                    user.setUserName(rs.getString("LOGIN_ID"));
                    user.setBranchId(rs.getLong("MAIN_BRANCH_ID"));
                    user.setBranchName(rs.getString("BU_NM"));
                    user.setStaffName(rs.getString("NAME"));
                    user.setRoleId(rs.getLong("ROLE_ID"));
                    user.setStaffNumber(rs.getString("EMP_NO"));
                    user.setRole(rs.getString("ROLE_NM"));
                    user.setRoles(queryUserRoles(user, channelId));
                }
            }
        }
        catch (Exception ex)
        {
            getLog().logEvent(ex);
        }
        return user;
    }
    
    public ArrayList<USRole> queryUserRoles(BNUser user, Long channelId)
    {
        ArrayList<USRole> roles = new ArrayList<>();
        try (ResultSet rs = executeQueryToResultSet("SELECT USER_ID, LOGIN_ID, BU_ID, BU_CD, BU_NAME, USER_ROLE_ID, BU_ROLE_ID, SUPERVISOR_FLAG, BUSINESS_ROLE_ID, BU_ROLE_NAME, BUSINESS_ROLE_NM, DEFAULT_ROLE_FLAG FROM " + APController.coreSchemaName + ".V_USER_ROLE WHERE USER_ID=" + user.getUserId() + " AND REC_ST='A'"))
        {
            if (rs != null)
            {
                while (rs.next())
                {
                    USRole role = new USRole();
                    role.setUserId(rs.getLong("USER_ID"));
                    role.setUserName(rs.getString("LOGIN_ID"));
                    role.setBranchId(rs.getLong("BU_ID"));
                    role.setBranchCode(rs.getString("BU_CD"));
                    role.setBranchName(rs.getString("BU_NAME"));
                    role.setUserRoleId(rs.getLong("USER_ROLE_ID"));
                    role.setBuRoleId(rs.getLong("BU_ROLE_ID"));
                    role.setSupervisor(rs.getString("SUPERVISOR_FLAG"));
                    role.setRoleId(rs.getLong("BUSINESS_ROLE_ID"));
                    role.setRole(rs.getString("BU_ROLE_NAME"));
                    if (getWorker().isYes(rs.getString("DEFAULT_ROLE_FLAG")))
                    {
                        user.setRoleId(role.getRoleId());
                        user.setRole(role.getRole());
                    }
                    role.setDrawers(queryUserDrawers(role.getUserId(), role.getUserRoleId()));
                    role.setLimits(queryRoleLimits(role.getRoleId(), channelId));
                    roles.add(role);
                }
            }
            if (roles.size() == 1)
            {
                user.setRoleId(roles.get(0).getRoleId());
                user.setRole(roles.get(0).getRole());
            }
        }
        catch (Exception ex)
        {
            getLog().logEvent(ex);
        }
        return roles;
    }
    
    public ArrayList<USRole> queryUserRoles(Long userId, Long channelId)
    {
        ArrayList<USRole> roles = new ArrayList<>();
        try (ResultSet rs = executeQueryToResultSet("SELECT USER_ID, LOGIN_ID, BU_ID, BU_CD, BU_NAME, USER_ROLE_ID, BU_ROLE_ID, SUPERVISOR_FLAG, BUSINESS_ROLE_ID, BU_ROLE_NAME, BUSINESS_ROLE_NM, DEFAULT_ROLE_FLAG FROM " + APController.coreSchemaName + ".V_USER_ROLE WHERE USER_ID=" + userId + " AND REC_ST='A'"))
        {
            if (rs != null)
            {
                while (rs.next())
                {
                    USRole role = new USRole();
                    role.setUserId(rs.getLong("USER_ID"));
                    role.setUserName(rs.getString("LOGIN_ID"));
                    role.setBranchId(rs.getLong("BU_ID"));
                    role.setBranchCode(rs.getString("BU_CD"));
                    role.setBranchName(rs.getString("BU_NAME"));
                    role.setUserRoleId(rs.getLong("USER_ROLE_ID"));
                    role.setBuRoleId(rs.getLong("BU_ROLE_ID"));
                    role.setSupervisor(rs.getString("SUPERVISOR_FLAG"));
                    role.setRoleId(rs.getLong("BUSINESS_ROLE_ID"));
                    role.setRole(rs.getString("BU_ROLE_NAME"));
                    role.setDrawers(queryUserDrawers(role.getUserId(), role.getUserRoleId()));
                    role.setLimits(queryRoleLimits(role.getRoleId(), channelId));
                    roles.add(role);
                }
            }
        }
        catch (Exception ex)
        {
            getLog().logEvent(ex);
        }
        return roles;
    }
    
    public ArrayList<TLDrawer> queryUserDrawers(Long userId, Long userRoleId)
    {
        ArrayList<TLDrawer> drawers = new ArrayList<>();
        try (ResultSet rs = executeQueryToResultSet("SELECT V.DRAWER_ID, V.DRAWER_NO, R.GL_ACCT_NO, R.LAST_DRAWER_OPEN_DT, V.REC_ST FROM " + APController.coreSchemaName + ".V_DRAWER_USER_ROLES V, " + APController.coreSchemaName + ".DRAWER R WHERE R.DRAWER_ID=V.DRAWER_ID AND V.BU_ID=R.BU_ID AND V.USER_ROLE_ID=" + userRoleId + " AND V.SYSUSER_ID=" + userId + " AND V.DRAWER_ST='O' AND V.REC_ST='O'"))
        {
            if (rs != null)
            {
                while (rs.next())
                {
                    TLDrawer drawer = new TLDrawer();
                    drawer.setDrawerId(rs.getLong("DRAWER_ID"));
                    drawer.setDrawerNumber(rs.getString("DRAWER_NO"));
                    drawer.setDrawerAccount(rs.getString("GL_ACCT_NO"));
                    drawer.setOpenDate(rs.getDate("LAST_DRAWER_OPEN_DT"));
                    drawer.setStatus(rs.getString("REC_ST"));
                    drawer.setCurrencies(queryDrawerCurrencies(drawer.getDrawerId()));
                    drawers.add(drawer);
                }
            }
        }
        catch (Exception ex)
        {
            getLog().logEvent(ex);
        }
        return drawers;
    }
    
    public ArrayList<URLimit> queryRoleLimits(Long roleId, Long channelId)
    {
        ArrayList<URLimit> limits = new ArrayList<>();
        try (ResultSet rs = executeQueryToResultSet("SELECT ROLE_ID, CRNCY_CD_ISO, PREVENTIVE_CR_LIMIT, PREVENTIVE_DR_LIMIT FROM " + APController.coreSchemaName + ".V_BUSINESS_ROLE_CHANNEL_LIMIT WHERE REC_ST='A' AND CHANNEL_ID=" + channelId + " AND ROLE_ID=" + roleId))
        {
            if (rs != null)
            {
                while (rs.next())
                {
                    URLimit limit = new URLimit();
                    limit.setRoleId(rs.getLong("ROLE_ID"));
                    limit.setCurrency(rs.getString("CRNCY_CD_ISO"));
                    limit.setCreditLimit(rs.getBigDecimal("PREVENTIVE_CR_LIMIT"));
                    limit.setDebitLimit(rs.getBigDecimal("PREVENTIVE_DR_LIMIT"));
                    limits.add(limit);
                }
            }
        }
        catch (Exception ex)
        {
            getLog().logEvent(ex);
        }
        return limits;
    }
    
    public ArrayList<CNCurrency> queryDrawerCurrencies(Long drawerId)
    {
        ArrayList<CNCurrency> currencies = new ArrayList<>();
        try (ResultSet rs = executeQueryToResultSet("SELECT CRNCY_ID, CRNCY_CD, INITCAP(CRNCY_NM) AS CRNCY_NM, CRNCY_POSN FROM " + APController.coreSchemaName + ".CURRENCY WHERE CRNCY_ID IN (SELECT CRNCY_ID FROM " + APController.coreSchemaName + ".DRAWER_CURRENCY WHERE DRAWER_ID=" + drawerId + ") AND REC_ST='A'"))
        {
            if (rs != null)
            {
                while (rs.next())
                {
                    CNCurrency currency = new CNCurrency();
                    currency.setCurrencyId(rs.getLong("CRNCY_ID"));
                    currency.setCurrencyCode(rs.getString("CRNCY_CD"));
                    currency.setCurrencyName(rs.getString("CRNCY_NM"));
                    currency.setDecimalPlaces(rs.getInt("CRNCY_POSN"));
                    currencies.add(currency);
                }
            }
        }
        catch (Exception ex)
        {
            getLog().logEvent(ex);
        }
        return currencies;
    }
    
    public ArrayList<CNBranch> queryBusinessUnits()
    {
        ArrayList<CNBranch> branches = new ArrayList<>();
        try (ResultSet rs = executeQueryToResultSet("SELECT BU_ID, BU_NO, BU_NM, GL_PREFIX_CD, REC_ST FROM " + APController.coreSchemaName + ".BUSINESS_UNIT WHERE REC_ST='A' ORDER BY BU_NO"))
        {
            if (rs != null)
            {
                while (rs.next())
                {
                    CNBranch cNBranch = new CNBranch();
                    cNBranch.setBuId(rs.getLong("BU_ID"));
                    cNBranch.setBuCode(rs.getString("BU_NO"));
                    cNBranch.setBuName(rs.getString("BU_NM"));
                    cNBranch.setGlPrefix(rs.getString("GL_PREFIX_CD"));
                    cNBranch.setStatus(rs.getString("REC_ST"));
                    branches.add(cNBranch);
                }
            }
        }
        catch (Exception ex)
        {
            getLog().logEvent(ex);
        }
        return branches;
    }
    
    public CNBranch queryBusinessUnit(Long buId)
    {
        CNBranch cNBranch = new CNBranch();
        try (ResultSet rs = executeQueryToResultSet("SELECT BU_ID, BU_NO, BU_NM, GL_PREFIX_CD, REC_ST FROM " + APController.coreSchemaName + ".BUSINESS_UNIT WHERE REC_ST='A' AND BU_ID=" + buId))
        {
            if (rs != null && rs.next())
            {
                cNBranch.setBuId(rs.getLong("BU_ID"));
                cNBranch.setBuCode(rs.getString("BU_NO"));
                cNBranch.setBuName(rs.getString("BU_NM"));
                cNBranch.setGlPrefix(rs.getString("GL_PREFIX_CD"));
                cNBranch.setStatus(rs.getString("REC_ST"));
            }
        }
        catch (Exception ex)
        {
            getLog().logEvent(ex);
        }
        return cNBranch;
    }
    
    public CNBranch queryBusinessUnit(String buNo)
    {
        CNBranch cNBranch = new CNBranch();
        try (ResultSet rs = executeQueryToResultSet("SELECT BU_ID, BU_NO, BU_NM, GL_PREFIX_CD, REC_ST FROM " + APController.coreSchemaName + ".BUSINESS_UNIT WHERE REC_ST='A' AND BU_NO='" + buNo + "'"))
        {
            if (rs != null && rs.next())
            {
                cNBranch.setBuId(rs.getLong("BU_ID"));
                cNBranch.setBuCode(rs.getString("BU_NO"));
                cNBranch.setBuName(rs.getString("BU_NM"));
                cNBranch.setGlPrefix(rs.getString("GL_PREFIX_CD"));
                cNBranch.setStatus(rs.getString("REC_ST"));
            }
        }
        catch (Exception ex)
        {
            getLog().logEvent(ex);
        }
        return cNBranch;
    }
    
    public AXTxn queryTxn(Long channelId, String txnRef)
    {
        AXTxn axTxn = new AXTxn();
        try (ResultSet rs = executeQueryToResultSet("SELECT REC_ID, TXN_REF, ACCOUNT, CURRENCY, AMOUNT, XAPI_CODE, RESULT, RESP_CODE, REC_ST FROM " + APController.cmSchemaName + ".PHL_TXN_LOG WHERE CHANNEL_ID = " + channelId + " AND TXN_REF='" + txnRef + "' AND RESULT='SUCCESS' AND TXN_DATE>=SYSDATE-30 AND REC_ST = 'A'"))
        {
            if (rs != null && rs.next())
            {
                axTxn.setRecId(rs.getLong("REC_ID"));
                axTxn.setTxnRef(rs.getString("TXN_REF"));
                axTxn.setAccount(rs.getString("ACCOUNT"));
                axTxn.setCurrency(rs.getString("CURRENCY"));
                axTxn.setAmount(rs.getBigDecimal("AMOUNT"));
                axTxn.setXapiCode(rs.getString("XAPI_CODE"));
                axTxn.setResult(rs.getString("RESULT"));
                axTxn.setRespCode(rs.getString("RESP_CODE"));
                axTxn.setRecSt(rs.getString("REC_ST"));
            }
        }
        catch (Exception ex)
        {
            getLog().logEvent(ex);
        }
        return axTxn;
    }
    
    public boolean validateDuplicate(Long channelId, String txnRef, boolean reversed)
    {
        return checkExists("SELECT TXN_REF FROM " + APController.cmSchemaName + ".PHL_TXN_LOG WHERE CHANNEL_ID = " + channelId + " AND TXN_REF='" + txnRef + "' AND TXN_DATE>=SYSDATE-30 AND REC_ST = '" + (reversed ? "R" : "A") + "'");
    }
    
    public boolean deleteInvalidChannelUsers()
    {
        return executeUpdate("DELETE " + APController.coreSchemaName + ".CUST_CHANNEL_ACCOUNT WHERE CREATED_BY='SYSTEM' AND CUST_ID NOT IN(SELECT CUST_ID FROM " + APController.coreSchemaName + ".CUSTOMER_CHANNEL_USER)", true) && executeUpdate("DELETE " + APController.coreSchemaName + ".CUSTOMER_CHANNEL WHERE USER_ID='SYSTEM' AND CUST_ID NOT IN (SELECT CUST_ID FROM " + APController.coreSchemaName + ".CUST_CHANNEL_ACCOUNT)", true) && executeUpdate("DELETE " + APController.coreSchemaName + ".CUSTOMER_CHANNEL WHERE USER_ID='SYSTEM' AND CUST_ID NOT IN (SELECT CUST_ID FROM " + APController.coreSchemaName + ".CUSTOMER_CHANNEL_USER)", true) && executeUpdate("DELETE " + APController.coreSchemaName + ".CUST_CHANNEL_SCHEME WHERE USER_ID='SYSTEM' AND CUST_ID NOT IN (SELECT CUST_ID FROM " + APController.coreSchemaName + ".CUSTOMER_CHANNEL_USER)", true);
    }
    
    public Object[][] queryProducts()
    {
        return executeQueryToArray("SELECT PROD_ID, PROD_DESC FROM " + APController.coreSchemaName + ".PRODUCT WHERE REC_ST='A' ORDER BY PROD_ID");
    }
    
    public Object[][] queryBranches()
    {
        return executeQueryToArray("SELECT BU_ID, BU_NM FROM " + APController.coreSchemaName + ".BUSINESS_UNIT WHERE REC_ST='A' ORDER BY BU_ID");
    }
    
    public Object[][] queryProducts(int productId)
    {
        return executeQueryToArray("SELECT PROD_ID, PROD_DESC FROM " + APController.coreSchemaName + ".PRODUCT WHERE REC_ST='A' AND PROD_ID=" + productId + " ORDER BY PROD_CD");
    }
    
    public Object[][] queryChannelSchemes()
    {
        return executeQueryToArray("SELECT SCHEME_ID, SCHEME_DESC FROM " + APController.coreSchemaName + ".SERVICE_CHANNEL_SCHEME WHERE REC_ST='A' ORDER BY SCHEME_ID ASC");
    }
    
    public Object[][] queryCustomerTypes()
    {
        return executeQueryToArray("SELECT CUST_TY_ID, CUST_TY_DESC FROM " + APController.coreSchemaName + ".CUSTOMER_TYPE_REF WHERE REC_ST='A' ORDER BY CUST_TY_ID");
    }
    
    public ArrayList<CAEvent> queryAccountEvents(Long channelId)
    {
        ArrayList<CAEvent> events = new ArrayList<>();
        try (ResultSet rs = executeQueryToResultSet("SELECT A.CUST_CHANNEL_ACCT_ID, A.CUST_ID, M.CUST_NO, M.CUST_CAT, U.CHANNEL_USER_CUST_ID, U.ACCESS_CD, A.ACCT_ID, C.ACCT_NO, C.PROD_ID, NVL(A.SHORT_NAME, C.ACCT_NO) AS SHORT_NAME, A.AUDIT_ACTION FROM " + APController.coreSchemaName + ".CUST_CHANNEL_ACCOUNT$AUD A, " + APController.coreSchemaName + ".ACCOUNT C, " + APController.coreSchemaName + ".CUSTOMER M, " + APController.coreSchemaName + ".CUSTOMER_CHANNEL_USER U WHERE A.CHANNEL_ID=U.CHANNEL_ID AND A.CUST_ID=U.CUST_ID AND A.CUST_ID=M.CUST_ID AND C.ACCT_ID=A.ACCT_ID AND U.REC_ST='A' AND A.FORWARDED IS NULL AND A.CHANNEL_ID=" + channelId + " ORDER BY A.AUDIT_TS ASC"))
        {
            if (rs != null)
            {
                while (rs.next())
                {
                    CAEvent event = new CAEvent();
                    event.setCustChannelAcctId(rs.getLong("CUST_CHANNEL_ACCT_ID"));
                    event.setCustId(rs.getLong("CUST_ID"));
                    event.setAcctId(rs.getLong("ACCT_ID"));
                    event.setProdId(rs.getLong("PROD_ID"));
                    event.setCustNo(rs.getString("CUST_NO"));
                    event.setAcctNo(rs.getString("ACCT_NO"));
                    event.setAccessCd(rs.getString("ACCESS_CD"));
                    event.setShortName(rs.getString("SHORT_NAME"));
                    event.setChannelUserCustId(rs.getLong("CHANNEL_USER_CUST_ID"));
                    event.setAuditAction(rs.getString("AUDIT_ACTION"));
                    event.setCustCat(rs.getString("CUST_CAT"));
                    events.add(event);
                }
            }
        }
        catch (Exception ex)
        {
            getLog().logEvent(ex);
        }
        return events;
    }
    
    public ArrayList<CAEvent> queryGroupAccountEvents(Long channelId, Long memberCustId, String auditAction)
    {
        ArrayList<CAEvent> events = new ArrayList<>();
        try (ResultSet rs = executeQueryToResultSet("SELECT A.CUST_CHANNEL_ACCT_ID, A.CUST_ID, M.CUST_NO, M.CUST_CAT, U.CHANNEL_USER_CUST_ID, U.ACCESS_CD, A.ACCT_ID, C.ACCT_NO, C.PROD_ID, NVL(A.SHORT_NAME, C.ACCT_NO) AS SHORT_NAME, '" + auditAction + "' AS AUDIT_ACTION FROM " + APController.coreSchemaName + ".CUST_CHANNEL_ACCOUNT A, " + APController.coreSchemaName + ".ACCOUNT C, " + APController.coreSchemaName + ".CUSTOMER M, " + APController.coreSchemaName + ".CUSTOMER_CHANNEL_USER U WHERE A.CHANNEL_ID=U.CHANNEL_ID AND A.CUST_ID=U.CUST_ID AND A.CUST_ID=M.CUST_ID AND C.ACCT_ID=A.ACCT_ID AND U.REC_ST='A' AND A.CHANNEL_ID=" + channelId + " AND A.CUST_ID IN (SELECT MAIN_CUST_ID FROM " + APController.coreSchemaName + ".V_CUSTOMER_RELATIONSHIP WHERE REL_CUST_ID=" + memberCustId + ")"))
        {
            if (rs != null)
            {
                while (rs.next())
                {
                    CAEvent event = new CAEvent();
                    event.setCustChannelAcctId(rs.getLong("CUST_CHANNEL_ACCT_ID"));
                    event.setCustId(rs.getLong("CUST_ID"));
                    event.setAcctId(rs.getLong("ACCT_ID"));
                    event.setProdId(rs.getLong("PROD_ID"));
                    event.setCustNo(rs.getString("CUST_NO"));
                    event.setAcctNo(rs.getString("ACCT_NO"));
                    event.setAccessCd(rs.getString("ACCESS_CD"));
                    event.setShortName(rs.getString("SHORT_NAME"));
                    event.setChannelUserCustId(rs.getLong("CHANNEL_USER_CUST_ID"));
                    event.setAuditAction(rs.getString("AUDIT_ACTION"));
                    event.setCustCat(rs.getString("CUST_CAT"));
                    events.add(event);
                }
            }
        }
        catch (Exception ex)
        {
            getLog().logEvent(ex);
        }
        return events;
    }
    
    public Date getProcessingDate()
    {
        Date currentDate = new Date();
        try (ResultSet rs = executeQueryToResultSet("SELECT TO_DATE(DISPLAY_VALUE,'DD/MM/YYYY') AS PROC_DATE FROM " + APController.coreSchemaName + ".CTRL_PARAMETER WHERE PARAM_CD = 'S02'"))
        {
            if (rs != null && rs.next())
            {
                currentDate = rs.getDate("PROC_DATE");
            }
        }
        catch (Exception ex)
        {
            getLog().logEvent(ex);
        }
        return currentDate;
    }
    
    public Date getSystemDate()
    {
        Date currentDate = new Date();
        try (ResultSet rs = executeQueryToResultSet("SELECT SYSDATE AS SYS_DATE FROM DUAL"))
        {
            if (rs != null && rs.next())
            {
                currentDate = rs.getTimestamp("SYS_DATE");
            }
        }
        catch (Exception ex)
        {
            getLog().logEvent(ex);
        }
        return currentDate;
    }
    
    public void loadPendingAlerts(MXAlert mXAlert)
    {
        checkConnection();
        if (!"SE".equals(mXAlert.getAlertType()) && !"SD".equals(mXAlert.getAlertType()) && !"PC".equals(mXAlert.getAlertType()))
        {
            String[] days = !getWorker().isBlank(mXAlert.getAlertDays()) ? mXAlert.getAlertDays().split(",") : new String[1];
            int times = ("LA".equals(mXAlert.getAlertType()) || "LD".equals(mXAlert.getAlertType())) ? days.length : 1;
            for (int i = 0; i < times; i++)
            {
                if (mXAlert.getFilters().isEmpty())
                {
                    mXAlert.getFilters().add("ALL");
                }
                for (String filter : mXAlert.getFilters())
                {
                    try
                    {
//                        System.err.println("mXAlert.getAlertCode() "+mXAlert.getAlertCode());
//                        System.err.println("mXAlert.getAlertType() "+mXAlert.getAlertType());
//                        System.err.println("mXAlert.get>>>  "+(("LA".equals(mXAlert.getAlertType()) || "LD".equals(mXAlert.getAlertType())) ? getWorker().convertToType(days[i], Integer.class) : 1));
//                        System.err.println("mXAlert.APController.getCurrency().getCurrencyCode()() "+APController.getCurrency().getCurrencyCode());
//                        System.err.println("mXAlert.mXAlert.getFilterBy()() "+mXAlert.getFilterBy());
//                        System.err.println("mXAlert.getAlertCode() "+filter);
                        getAlertStatement().setString(1, mXAlert.getAlertCode());
                        getAlertStatement().setString(2, mXAlert.getAlertType());
                        
                        getAlertStatement().setInt(3, (("LA".equals(mXAlert.getAlertType()) || "LD".equals(mXAlert.getAlertType())) ? getWorker().convertToType(days[i], Integer.class) : 1));
                        getAlertStatement().setString(4, APController.getCurrency().getCurrencyCode());
                        getAlertStatement().setString(5, mXAlert.getFilterBy());
                        
                        getAlertStatement().setString(6, filter);
                        getAlertStatement().execute();
                    }
                    catch (Exception ex)
                    {
                        if (isRecoverable(ex))
                        {
                            connectToDB();
                        }
                        getLog().logEvent("======<" + mXAlert.getAlertCode() + ", " + mXAlert.getAlertType() + ", " + mXAlert.getFilterBy() + ", " + filter + ">======", ex);
                    }
                }
            }
        }
        dispose();
    }
    
    public ArrayList<MXMessage> queryPendingAlerts(MXAlert mXAlert)
    {
        ArrayList<MXMessage> alerts = new ArrayList<>();
        try (ResultSet rs = executeQueryToResultSet("SELECT ALERT_ID, ALERT_DATE, ALERT_TIME, ALERT_CODE, ALERT_TYPE, TXN_ID, BU_ID, CUST_ID, CUST_NAME, ACCT_NO, CONTRA, CHRG_ACCT, TXN_DATE, CURRENCY, NVL(TXN_AMT,0) AS TXN_AMT, NVL(TXN_CHG,0) AS TXN_CHG, CHRG_AMT, CHG_ID, TXN_DESC, CLRD_BAL, CONTACT, CASE WHEN NVL(ORIGINATOR,CUST_NAME) ='null' then CUST_NAME else NVL(ORIGINATOR,CUST_NAME) end AS ORIGINATOR, SCHEME_ID, ACCESS_CD, PASSWORD, MATURITY_DT, START_DT, TENOR, RATE, REC_ST,PROD_ID,GUARANTOR_NM FROM " + APController.cmSchemaName + ".PHA_ALERTS WHERE REC_ST='P' AND ALERT_TYPE='" + mXAlert.getAlertType() + "' AND (ALERT_CODE='" + mXAlert.getAlertCode() + "' OR ALERT_CODE IS NULL) " + ("CS".equals(mXAlert.getFilterBy()) ? "AND SCHEME_ID IN (" + getWorker().createCSVList(mXAlert.getFilters()) + ") " : "") + "AND ALERT_DATE>=SYSDATE-1 AND ROWNUM<=5000"))
        {
            if (rs != null)
            {
                while (rs.next())
                {
                    MXMessage message = new MXMessage();
                    message.setAlertId(rs.getString("ALERT_ID"));
                    message.setAlertDate(rs.getDate("ALERT_DATE"));
                    message.setAlertTime(rs.getDate("ALERT_TIME"));
                    message.setAlertCode(mXAlert.getAlertCode());
                    message.setDescription(mXAlert.getDescription());
                    message.setAlertType(rs.getString("ALERT_TYPE"));
                    message.setTxnId(rs.getString("TXN_ID"));
                    message.setCustId(rs.getLong("CUST_ID"));
                    message.setSchemeId(rs.getLong("SCHEME_ID"));
                    message.setCustName(rs.getString("CUST_NAME"));
                    message.setAcctNo(rs.getString("ACCT_NO"));
                    message.setContra(rs.getString("CONTRA"));
                    message.setChargeAcct(rs.getString("CHRG_ACCT"));
                    message.setTxnDate(rs.getDate("TXN_DATE"));
                    message.setCurrency(rs.getString("CURRENCY"));
                    message.setTxnAmt(rs.getBigDecimal("TXN_AMT"));
                    message.setTxnChg(rs.getBigDecimal("TXN_CHG"));
                    message.setChargeId(rs.getLong("CHG_ID"));
                    message.setTxnDesc(rs.getString("TXN_DESC"));
                    message.setAccessCode(rs.getString("ACCESS_CD"));
                    message.setPassword(rs.getString("PASSWORD"));
                    message.setClrdBal(rs.getBigDecimal("CLRD_BAL"));
                    message.setMsisdn(rs.getString("CONTACT"));
                    message.setMaturityDate(rs.getDate("MATURITY_DT"));
                    message.setStartDate(rs.getDate("START_DT"));
                    message.setTenor(rs.getString("TENOR"));
                    message.setRate(rs.getBigDecimal("RATE"));
                    message.setStatus(rs.getString("REC_ST"));
                    message.setProdId(rs.getLong("PROD_ID"));
                    message.setOriginator(rs.getString("ORIGINATOR"));
                    message.setGuarantor(rs.getString("GUARANTOR_NM"));
                    message.setProdDesc(queryProductDesc(message.getProdId()));
                    alerts.add(message);
                }
            }
        }
        catch (Exception ex)
        {
            getLog().logEvent(ex);
        }
        return alerts;
    }
    
    public TreeMap<String, AXCharge> queryCharges(String scheme)
    {
        TreeMap<String, AXCharge> charges = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        try (ResultSet rs = executeQueryToResultSet("SELECT REC_ID, REC_CD, DESCRIPTION, ACCOUNT, LEDGER, SCHEME, SYS_USER, SYS_DATE, REC_ST FROM " + APController.cmSchemaName + ".PHC_CHARGE WHERE SCHEME='" + scheme + "' ORDER BY REC_CD ASC"))
        {
            if (rs != null)
            {
                while (rs.next())
                {
                    AXCharge charge = new AXCharge();
                    charge.setRecId(rs.getLong("REC_ID"));
                    charge.setCode(rs.getString("REC_CD"));
                    charge.setDescription(rs.getString("DESCRIPTION"));
                    charge.setChargeAccount(rs.getString("ACCOUNT"));
                    charge.setChargeLedger(rs.getString("LEDGER"));
                    charge.setScheme(rs.getString("SCHEME"));
                    charge.setSysUser(rs.getString("SYS_USER"));
                    charge.setSysDate(rs.getDate("SYS_DATE"));
                    charge.setStatus(rs.getString("REC_ST"));
                    charges.put(charge.getCode(), setValues(setWaivers(charge)));
                }
            }
        }
        catch (Exception ex)
        {
            getLog().logEvent(ex);
        }
        return charges;
    }
    
    public AXCharge queryCharge(String scheme, String code)
    {
        AXCharge charge = new AXCharge();
        try (ResultSet rs = executeQueryToResultSet("SELECT REC_ID, REC_CD, DESCRIPTION, ACCOUNT, LEDGER, SCHEME, SYS_USER, SYS_DATE, REC_ST FROM " + APController.cmSchemaName + ".PHC_CHARGE WHERE SCHEME='" + scheme + "' AND REC_CD='" + code + "' ORDER BY REC_CD ASC"))
        {
            if (rs != null && rs.next())
            {
                charge.setRecId(rs.getLong("REC_ID"));
                charge.setCode(rs.getString("REC_CD"));
                charge.setDescription(rs.getString("DESCRIPTION"));
                charge.setChargeAccount(rs.getString("ACCOUNT"));
                charge.setChargeLedger(rs.getString("LEDGER"));
                charge.setScheme(rs.getString("SCHEME"));
                charge.setSysUser(rs.getString("SYS_USER"));
                charge.setSysDate(rs.getDate("SYS_DATE"));
                charge.setStatus(rs.getString("REC_ST"));
                charge = setValues(setWaivers(charge));
            }
        }
        catch (Exception ex)
        {
            getLog().logEvent(ex);
        }
        return charge;
    }
    
    public AXCharge setValues(AXCharge charge)
    {
        try (ResultSet rs = executeQueryToResultSet("SELECT REC_ID, CURRENCY, VALUE_TYPE, MIN_VALUE, MAX_VALUE, VALUE FROM " + APController.cmSchemaName + ".PHC_VALUE WHERE PARENT_KEY='" + charge.getRecId() + "' ORDER BY CURRENCY ASC"))
        {
            charge.getValues().clear();
            if (rs != null)
            {
                while (rs.next())
                {
                    TCValue tCValue = new TCValue();
                    tCValue.setValueId(rs.getLong("REC_ID"));
                    tCValue.setCurrency(rs.getString("CURRENCY"));
                    tCValue.setChargeType(rs.getString("VALUE_TYPE"));
                    tCValue.setMinAmount(rs.getBigDecimal("MIN_VALUE"));
                    tCValue.setMaxAmount(rs.getBigDecimal("MAX_VALUE"));
                    tCValue.setValue(rs.getBigDecimal("VALUE"));
                    tCValue.setTiers(queryTiers(String.valueOf(tCValue.getValueId())));
                    tCValue.setDeductions(queryDeductions(String.valueOf(tCValue.getValueId())));
                    charge.getValues().put(rs.getString("CURRENCY"), tCValue);
                }
            }
        }
        catch (Exception ex)
        {
            getLog().logEvent(ex);
        }
        return charge;
    }
    
    public AXCharge setWaivers(AXCharge charge)
    {
        try (ResultSet rs = executeQueryToResultSet("SELECT REC_ID, PROD_ID, MATCH_ACCT, WAIVED_PERC, CONDITION, THRESHOLD FROM " + APController.cmSchemaName + ".PHC_WAIVER WHERE PARENT_KEY='" + charge.getRecId() + "' ORDER BY PROD_ID ASC"))
        {
            charge.getWaivers().clear();
            if (rs != null)
            {
                while (rs.next())
                {
                    TCWaiver tXWaiver = new TCWaiver();
                    tXWaiver.setWaiverId(rs.getLong("REC_ID"));
                    tXWaiver.setProductId(rs.getLong("PROD_ID"));
                    tXWaiver.setMatchAccount(rs.getString("MATCH_ACCT"));
                    tXWaiver.setWaivedPercentage(rs.getBigDecimal("WAIVED_PERC"));
                    tXWaiver.setWaiverCondition(rs.getString("CONDITION"));
                    tXWaiver.setThresholdValue(rs.getBigDecimal("THRESHOLD"));
                    charge.getWaivers().put(rs.getLong("PROD_ID"), tXWaiver);
                }
            }
        }
        catch (Exception ex)
        {
            getLog().logEvent(ex);
        }
        return charge;
    }
    
    public TreeMap<String, TCScheme> querySchemes(String channel)
    {
        TreeMap<String, TCScheme> schemes = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        try (ResultSet rs = executeQueryToResultSet("SELECT REC_CD, CREATE_DT, CHANNEL, DESCRIPTION, SYS_USER, SYS_DATE, REC_ST FROM " + APController.cmSchemaName + ".PHC_SCHEME WHERE CHANNEL='" + channel + "' ORDER BY REC_CD ASC"))
        {
            if (rs != null)
            {
                while (rs.next())
                {
                    TCScheme scheme = new TCScheme();
                    scheme.setCode(rs.getString("REC_CD"));
                    scheme.setChannel(rs.getString("CHANNEL"));
                    scheme.setDescription(rs.getString("DESCRIPTION"));
                    scheme.setSysUser(rs.getString("SYS_USER"));
                    scheme.setSysDate(rs.getDate("SYS_DATE"));
                    scheme.setStatus(rs.getString("REC_ST"));
                    schemes.put(scheme.getCode(), scheme);
                }
            }
        }
        catch (Exception ex)
        {
            getLog().logEvent(ex);
        }
        return schemes;
    }
    
    public TreeMap<String, AXBank> queryBanks()
    {
        TreeMap<String, AXBank> banks = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        try (ResultSet rs = executeQueryToResultSet("SELECT BIN, CREATE_DT, BANK_NAME, LORO_LEDGER, TRANSFER_LEDGER, SUSPENSE_LEDGER, SYS_USER, SYS_DATE, REC_ST FROM " + APController.cmSchemaName + ".PHL_BANK ORDER BY BIN ASC"))
        {
            if (rs != null)
            {
                while (rs.next())
                {
                    AXBank bank = new AXBank();
                    bank.setBin(rs.getString("BIN"));
                    bank.setName(rs.getString("BANK_NAME"));
                    bank.setLoroLedger(rs.getString("LORO_LEDGER"));
                    bank.setTransferLedger(rs.getString("TRANSFER_LEDGER"));
                    bank.setSuspenseLedger(rs.getString("SUSPENSE_LEDGER"));
                    bank.setSysUser(rs.getString("SYS_USER"));
                    bank.setSysDate(rs.getDate("SYS_DATE"));
                    bank.setStatus(rs.getString("REC_ST"));
                    banks.put(bank.getBin(), bank);
                }
            }
        }
        catch (Exception ex)
        {
            getLog().logEvent(ex);
        }
        return banks;
    }
    
    public AXBank queryBank(String BIN)
    {
        AXBank bank = new AXBank();
        if (!getWorker().isBlank(BIN))
        {
            try (ResultSet rs = executeQueryToResultSet("SELECT BIN, CREATE_DT, BANK_NAME, LORO_LEDGER, TRANSFER_LEDGER, SUSPENSE_LEDGER, SYS_USER, SYS_DATE, REC_ST FROM " + APController.cmSchemaName + ".PHL_BANK WHERE BIN='" + BIN + "'"))
            {
                if (rs != null && rs.next())
                {
                    bank.setBin(rs.getString("BIN"));
                    bank.setName(rs.getString("BANK_NAME"));
                    bank.setLoroLedger(rs.getString("LORO_LEDGER"));
                    bank.setTransferLedger(rs.getString("TRANSFER_LEDGER"));
                    bank.setSuspenseLedger(rs.getString("SUSPENSE_LEDGER"));
                    bank.setSysUser(rs.getString("SYS_USER"));
                    bank.setSysDate(rs.getDate("SYS_DATE"));
                    bank.setStatus(rs.getString("REC_ST"));
                }
            }
            catch (Exception ex)
            {
                getLog().logEvent(ex);
            }
        }
        return bank;
    }
    
    public TreeMap<String, AXUser> queryUsers()
    {
        TreeMap<String, AXUser> users = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        try (ResultSet rs = executeQueryToResultSet("SELECT A.EMP_NO, A.CREATE_DT, A.LOGIN_ID, A.STAFF_NAME, A.SYS_USER, A.SYS_DATE, A.REC_ST FROM " + APController.cmSchemaName + ".PHU_USER A, " + APController.coreSchemaName + ".SYSUSER B WHERE A.EMP_NO=B.EMP_NO AND B.REC_ST='A' ORDER BY A.EMP_NO ASC"))
        {
            if (rs != null)
            {
                while (rs.next())
                {
                    AXUser user = new AXUser();
                    user.setUserNumber(rs.getString("EMP_NO"));
                    user.setUserName(rs.getString("LOGIN_ID"));
                    user.setStaffName(rs.getString("STAFF_NAME"));
                    user.setSysUser(rs.getString("SYS_USER"));
                    user.setSysDate(rs.getDate("SYS_DATE"));
                    user.setStatus(rs.getString("REC_ST"));
                    users.put(user.getUserNumber(), setUserRoles(user));
                }
            }
        }
        catch (Exception ex)
        {
            getLog().logEvent(ex);
        }
        return users;
    }
    
    public AXUser queryUser(String userNumber, String userName, boolean enrolled)
    {
        AXUser user = new AXUser();
        if (enrolled)
        {
            try (ResultSet rs = executeQueryToResultSet("SELECT A.EMP_NO, A.CREATE_DT, A.LOGIN_ID, A.STAFF_NAME, A.SYS_USER, A.SYS_DATE, A.REC_ST FROM " + APController.cmSchemaName + ".PHU_USER A, " + APController.coreSchemaName + ".SYSUSER B WHERE A.EMP_NO=B.EMP_NO AND (A.EMP_NO='" + userNumber + "' OR A.LOGIN_ID='" + userName + "') AND B.REC_ST='A' ORDER BY A.EMP_NO ASC"))
            {
                if (rs != null && rs.next())
                {
                    user.setUserNumber(rs.getString("EMP_NO"));
                    user.setUserName(rs.getString("LOGIN_ID"));
                    user.setStaffName(rs.getString("STAFF_NAME"));
                    user.setSysUser(rs.getString("SYS_USER"));
                    user.setSysDate(rs.getDate("SYS_DATE"));
                    user.setStatus(rs.getString("REC_ST"));
                    user = setUserRoles(user);
                }
            }
            catch (Exception ex)
            {
                getLog().logEvent(ex);
            }
        }
        else
        {
            try (ResultSet rs = executeQueryToResultSet("SELECT EMP_NO, LOGIN_ID, FIRST_NM || ' ' || LAST_NM AS NAME, REC_ST FROM " + APController.coreSchemaName + ".SYSUSER WHERE (EMP_NO='" + userNumber + "' OR LOGIN_ID='" + userName + "') AND REC_ST='A'"))
            {
                if (rs != null && rs.next())
                {
                    user.setUserNumber(rs.getString("EMP_NO"));
                    user.setUserName(rs.getString("LOGIN_ID"));
                    user.setStaffName(rs.getString("NAME"));
                    user.setStatus(rs.getString("REC_ST"));
                }
            }
            catch (Exception ex)
            {
                getLog().logEvent(ex);
            }
        }
        return user;
    }
    
    public AXUser setUserRoles(AXUser user)
    {
        try (ResultSet rs = executeQueryToResultSet("SELECT ROLE FROM " + APController.cmSchemaName + ".PHU_ROLE WHERE EMP_NO='" + user.getUserNumber() + "'"))
        {
            user.getRoles().clear();
            if (rs != null)
            {
                while (rs.next())
                {
                    if (rs.getString("ROLE") != null)
                    {
                        user.getRoles().add(rs.getString("ROLE"));
                    }
                }
            }
        }
        catch (Exception ex)
        {
            getLog().logEvent(ex);
        }
        return user;
    }
    
    public CNScheme queryChannelScheme(Long schemeId)
    {
        CNScheme scheme = new CNScheme();
        try (ResultSet rs = executeQueryToResultSet("SELECT SCHEME_ID, CHANNEL_ID, SCHEME_CD, SCHEME_DESC, REC_ST FROM " + APController.coreSchemaName + ".SERVICE_CHANNEL_SCHEME WHERE SCHEME_ID=" + schemeId))
        {
            if (rs != null && rs.next())
            {
                scheme.setSchemeId(rs.getLong("SCHEME_ID"));
                scheme.setChannelId(rs.getLong("CHANNEL_ID"));
                scheme.setSchemeCode(rs.getString("SCHEME_CD"));
                scheme.setName(rs.getString("SCHEME_DESC"));
                scheme.setStatus(rs.getString("REC_ST"));
            }
        }
        catch (Exception ex)
        {
            getLog().logEvent(ex);
        }
        return scheme;
    }
    
    public CMChannel queryCustomerChannelId(Long schemeId, Long custId)
    {
        CMChannel customerChannel = new CMChannel();
        try (ResultSet rs = executeQueryToResultSet("SELECT CUST_CHANNEL_ID, REC_ST FROM " + APController.coreSchemaName + ".CUSTOMER_CHANNEL WHERE CHANNEL_SCHEME_ID=" + schemeId + " AND CUST_ID=" + custId))
        {
            if (rs != null && rs.next())
            {
                customerChannel.setCustChannelId(rs.getLong("CUST_CHANNEL_ID"));
                customerChannel.setStatus(rs.getString("REC_ST"));
            }
        }
        catch (Exception ex)
        {
            getLog().logEvent(ex);
        }
        return customerChannel;
    }
    
    public Long queryTxnId(Long channelId, String accessCode, String reference)
    {
        Long txnId = null;
        try (ResultSet rs = executeQueryToResultSet("SELECT MAX(TRAN_JOURNAL_ID) AS TRAN_JOURNAL_ID FROM " + APController.coreSchemaName + ".XAPI_ACTIVITY_HISTORY WHERE REQ_RECEIVED_TIME>SYSDATE-1 AND REQ_CHANNEL_ID=" + channelId + " AND REFERENCE = '" + reference + "' AND CARD_NO='" + accessCode + "'"))
        {
            if (rs != null && rs.next())
            {
                txnId = rs.getLong("TRAN_JOURNAL_ID");
            }
        }
        catch (Exception ex)
        {
            getLog().logEvent(ex);
        }
        return txnId;
    }
    
    public boolean updateTxnRefText(Long txnId, String reference)
    {
        return executeUpdate("UPDATE " + APController.coreSchemaName + ".GL_ACCOUNT_HISTORY SET TRAN_REF_TXT='" + reference + "' WHERE TRAN_JOURNAL_ID=" + txnId, true) && executeUpdate("UPDATE " + APController.coreSchemaName + ".TXN_JOURNAL SET TRAN_REF_TXT='" + reference + "' WHERE TRAN_JOURNAL_ID=" + txnId, true);
    }
    
    public boolean deleteXapiHistory()
    {
        return executeUpdate("DELETE " + APController.coreSchemaName + ".XAPI_ACTIVITY_HISTORY WHERE REQ_RECEIVED_TIME<SYSDATE-2", true);
    }
    
    public TreeMap<String, AXPayment> queryPayments(String channel)
    {
        TreeMap<String, AXPayment> payments = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        try (ResultSet rs = executeQueryToResultSet("SELECT REC_ID, CREATE_DT, CHANNEL, TXN_CD, TYPE, DESCRIPTION, CODE_FIELD, CODE, DETAIL_FIELD, ACCOUNT, SYS_USER, SYS_DATE, REC_ST FROM " + APController.cmSchemaName + ".PHB_PAYMENT WHERE CHANNEL='" + channel + "' ORDER BY REC_ID ASC"))
        {
            if (rs != null)
            {
                while (rs.next())
                {
                    AXPayment payment = new AXPayment();
                    payment.setRecId(rs.getLong("REC_ID"));
                    payment.setCode(rs.getString("CODE"));
                    payment.setCreateDt(rs.getDate("CREATE_DT"));
                    payment.setDescription(rs.getString("DESCRIPTION"));
                    payment.setChannel(rs.getString("CHANNEL"));
                    payment.setCodeField(rs.getInt("CODE_FIELD"));
                    payment.setDetailField(rs.getInt("DETAIL_FIELD"));
                    payment.setTxnCd(rs.getString("TXN_CD"));
                    payment.setType(rs.getString("TYPE"));
                    payment.setAccount(rs.getString("ACCOUNT"));
                    payment.setSysUser(rs.getString("SYS_USER"));
                    payment.setSysDate(rs.getDate("SYS_DATE"));
                    payment.setStatus(rs.getString("REC_ST"));
                    payments.put(payment.getCode(), payment);
                }
            }
        }
        catch (Exception ex)
        {
            getLog().logEvent(ex);
        }
        return payments;
    }
    
    public AXPayment queryPayment(String channel, String code)
    {
        AXPayment payment = new AXPayment();
        try (ResultSet rs = executeQueryToResultSet("SELECT REC_ID, CREATE_DT, CHANNEL, TXN_CD, TYPE, DESCRIPTION, CODE_FIELD, CODE, DETAIL_FIELD, ACCOUNT, SYS_USER, SYS_DATE, REC_ST FROM " + APController.cmSchemaName + ".PHB_PAYMENT WHERE CHANNEL='" + channel + "' AND CODE='" + code + "' ORDER BY REC_ID ASC"))
        {
            if (rs != null)
            {
                while (rs.next())
                {
                    payment.setRecId(rs.getLong("REC_ID"));
                    payment.setCode(rs.getString("CODE"));
                    payment.setCreateDt(rs.getDate("CREATE_DT"));
                    payment.setDescription(rs.getString("DESCRIPTION"));
                    payment.setChannel(rs.getString("CHANNEL"));
                    payment.setCodeField(rs.getInt("CODE_FIELD"));
                    payment.setDetailField(rs.getInt("DETAIL_FIELD"));
                    payment.setTxnCd(rs.getString("TXN_CD"));
                    payment.setType(rs.getString("TYPE"));
                    payment.setAccount(rs.getString("ACCOUNT"));
                    payment.setSysUser(rs.getString("SYS_USER"));
                    payment.setSysDate(rs.getDate("SYS_DATE"));
                    payment.setStatus(rs.getString("REC_ST"));
                }
            }
        }
        catch (Exception ex)
        {
            getLog().logEvent(ex);
        }
        return payment;
    }
    
    public ArrayList<CNCustomer> queryGroupMembers(long groupCustId)
    {
        ArrayList<CNCustomer> customers = new ArrayList<>();
        try (ResultSet rs = executeQueryToResultSet("SELECT C.CUST_NO FROM " + APController.coreSchemaName + ".GROUP_MEMBER G, " + APController.coreSchemaName + ".CUSTOMER C WHERE G.GROUP_CUST_ID=" + groupCustId + " AND C.CUST_ID=G.MEMBER_CUST_ID AND G.REC_ST='A'"))
        {
            if (rs != null)
            {
                while (rs.next())
                {
                    customers.add(queryCustomerByNumber(rs.getString("CUST_NO")));
                }
            }
        }
        catch (Exception ex)
        {
            getLog().logEvent(ex);
        }
        return customers;
    }
    
    public ArrayList<GPMember> queryGroupMembers(Long groupCustId)
    {
        ArrayList<GPMember> members = new ArrayList<>();
        try (ResultSet rs = executeQueryToResultSet("SELECT GROUP_MEMBER_ID, GROUP_CUST_ID, MEMBERSHIP_REF_NO, MEMBER_CUST_ID, DEFLT_ACCT_NO, GROUP_RELSHIP_ID, SUB_GROUP_ID, REC_ST FROM " + APController.coreSchemaName + ".GROUP_MEMBER WHERE GROUP_CUST_ID=" + groupCustId + " AND REC_ST='A'"))
        {
            if (rs != null)
            {
                while (rs.next())
                {
                    GPMember member = new GPMember();
                    member.setGroupMemberId(rs.getLong("GROUP_MEMBER_ID"));
                    member.setGroupCustId(rs.getLong("GROUP_CUST_ID"));
                    member.setMemberRefNo(rs.getString("MEMBERSHIP_REF_NO"));
                    member.setMemberCustId(rs.getLong("MEMBER_CUST_ID"));
                    member.setDefaultAcctNo(rs.getString("DEFLT_ACCT_NO"));
                    member.setGroupXshipId(rs.getLong("GROUP_RELSHIP_ID"));
                    member.setSubGroupId(rs.getLong("SUB_GROUP_ID"));
                    member.setStatus(rs.getString("REC_ST"));
                    members.add(member);
                }
            }
        }
        catch (Exception ex)
        {
            getLog().logEvent(ex);
        }
        return members;
    }
    
    public GPMember queryGroupMember(Long groupCustId, Long memberCustId)
    {
        GPMember member = new GPMember();
        try (ResultSet rs = executeQueryToResultSet("SELECT GROUP_MEMBER_ID, GROUP_CUST_ID, MEMBERSHIP_REF_NO, MEMBER_CUST_ID, DEFLT_ACCT_NO, GROUP_RELSHIP_ID, SUB_GROUP_ID, REC_ST FROM " + APController.coreSchemaName + ".GROUP_MEMBER WHERE GROUP_CUST_ID=" + groupCustId + " AND MEMBER_CUST_ID=" + memberCustId + " AND REC_ST='A'"))
        {
            if (rs != null)
            {
                while (rs.next())
                {
                    member.setGroupMemberId(rs.getLong("GROUP_MEMBER_ID"));
                    member.setGroupCustId(rs.getLong("GROUP_CUST_ID"));
                    member.setMemberRefNo(rs.getString("MEMBERSHIP_REF_NO"));
                    member.setMemberCustId(rs.getLong("MEMBER_CUST_ID"));
                    member.setDefaultAcctNo(rs.getString("DEFLT_ACCT_NO"));
                    member.setGroupXshipId(rs.getLong("GROUP_RELSHIP_ID"));
                    member.setSubGroupId(rs.getLong("SUB_GROUP_ID"));
                    member.setStatus(rs.getString("REC_ST"));
                }
            }
        }
        catch (Exception ex)
        {
            getLog().logEvent(ex);
        }
        return member;
    }
    
    public ArrayList<RLCustomer> queryXshipMembers(Long groupCustId)
    {
        ArrayList<RLCustomer> customers = new ArrayList<>();
        try (ResultSet rs = executeQueryToResultSet("SELECT CUST_RELSHIP_ID, CUST_RELSHIP_TY_ID, MAIN_CUST, MAIN_CUST_NM, REL_CUST_NO, REL_CUST_NM, MAIN_CUST_ID, REL_CUST_ID, REL_SHIP, RELSHIP_TY, CUST_RELSHIP_TY, CUST_CAT, REC_ST FROM " + APController.coreSchemaName + ".V_CUSTOMER_RELATIONSHIP_PHL WHERE MAIN_CUST_ID=" + groupCustId + " AND REC_ST='A'"))
        {
            if (rs != null)
            {
                while (rs.next())
                {
                    RLCustomer customer = new RLCustomer();
                    customer.setMainCustId(rs.getLong("MAIN_CUST_ID"));
                    customer.setMainCustNo(rs.getString("MAIN_CUST"));
                    customer.setRelCustId(rs.getLong("REL_CUST_ID"));
                    customer.setRelCustNo(rs.getString("REL_CUST_NO"));
                    customer.setRelCustName(rs.getString("REL_CUST_NM"));
                    customer.setXshipTypeIId(rs.getLong("CUST_RELSHIP_TY_ID"));
                    customer.setStatus(rs.getString("REC_ST"));
                    customers.add(customer);
                }
            }
        }
        catch (Exception ex)
        {
            getLog().logEvent(ex);
        }
        return customers;
    }
    
    public RLCustomer queryXshipMember(Long groupCustId, Long relCustId)
    {
        RLCustomer customer = new RLCustomer();
        try (ResultSet rs = executeQueryToResultSet("SELECT CUST_RELSHIP_ID, CUST_RELSHIP_TY_ID, MAIN_CUST, MAIN_CUST_NM, REL_CUST_NO, REL_CUST_NM, MAIN_CUST_ID, REL_CUST_ID, REL_SHIP, RELSHIP_TY, CUST_RELSHIP_TY, CUST_CAT, REC_ST FROM " + APController.coreSchemaName + ".V_CUSTOMER_RELATIONSHIP_PHL WHERE MAIN_CUST_ID=" + groupCustId + " AND REL_CUST_ID=" + relCustId + " AND REC_ST='A'"))
        {
            if (rs != null && rs.next())
            {
                customer.setMainCustId(rs.getLong("MAIN_CUST_ID"));
                customer.setMainCustNo(rs.getString("MAIN_CUST"));
                customer.setRelCustId(rs.getLong("REL_CUST_ID"));
                customer.setRelCustNo(rs.getString("REL_CUST_NO"));
                customer.setRelCustName(rs.getString("REL_CUST_NM"));
                customer.setXshipTypeIId(rs.getLong("CUST_RELSHIP_TY_ID"));
                customer.setStatus(rs.getString("REC_ST"));
            }
        }
        catch (Exception ex)
        {
            getLog().logEvent(ex);
        }
        return customer;
    }
    
    public ArrayList<CNCustomer> queryGroupLoanBeneficiaries(long groupCustId, String accountNumber)
    {
        ArrayList<CNCustomer> customers = new ArrayList<>();
        try (ResultSet rs = executeQueryToResultSet("SELECT CUST_NO FROM " + APController.coreSchemaName + ".CUSTOMER WHERE CUST_ID IN (SELECT MEMBER_ID FROM " + APController.coreSchemaName + ".GROUP_LOAN_ALLOTMENT_MEMO WHERE GRP_CUST_ID=" + groupCustId + " AND APPL_ID=(SELECT APPL_ID FROM " + APController.coreSchemaName + ".LOAN_ACCOUNT WHERE ACCT_NO='" + accountNumber + "'))"))
        {
            if (rs != null)
            {
                while (rs.next())
                {
                    customers.add(queryCustomerByNumber(rs.getString("CUST_NO")));
                }
            }
        }
        catch (Exception ex)
        {
            getLog().logEvent(ex);
        }
        return customers;
    }
    
    public ArrayList<DHRecord> queryDepositAccountHistory(String accountsList, Long acctHistId, Long pageSize)
    {
        ArrayList<DHRecord> records = new ArrayList<>();
        try (ResultSet rs = executeQueryToResultSet("SELECT Q.* FROM (SELECT ACCT_HIST_ID, TRAN_JOURNAL_ID, SYS_CREATE_TS, EVENT_CD, ACCT_NO, ORIGIN_BU_ID, BU_NM, TRAN_DT, VALUE_DT, CHANNEL_ID,CHANNEL_DESC, TRAN_REF_TXT, CHQ_NO, TRAN_DESC, DR_CR_IND, ACCT_CRNCY_CD_ISO, ACCT_AMT, TXN_CONTRA_ACCT_NO, DEPOSITOR_PAYEE_NM, REV_TXN_FG, REV_TRAN_JOURNAL_ID, STMNT_BAL FROM " + APController.coreSchemaName + ".V_DEPOSIT_ACCOUNT_HISTORY_EXT WHERE ACCT_NO IN (" + getWorker().cleanCSVList(accountsList) + ") AND ACCT_HIST_ID>" + acctHistId + " AND CHRG_ID IS NULL ORDER BY ACCT_HIST_ID ASC) Q WHERE ROWNUM<=" + pageSize))
        {
            if (rs != null)
            {
                rs.beforeFirst();
                while (rs.next())
                {
                    DHRecord record = new DHRecord();
                    record.setAcctHistId(rs.getLong("ACCT_HIST_ID"));
                    record.setTranJournalId(rs.getLong("TRAN_JOURNAL_ID"));
                    record.setSysCreateTs(rs.getDate("SYS_CREATE_TS"));
                    record.setEventCd(rs.getString("EVENT_CD"));
                    record.setAcctNo(rs.getString("ACCT_NO"));
                    record.setOriginBuId(rs.getLong("ORIGIN_BU_ID"));
                    record.setBuNm(rs.getString("BU_NM"));
                    record.setTranDt(rs.getDate("TRAN_DT"));
                    record.setValueDt(rs.getDate("VALUE_DT"));
                    record.setChannelId(rs.getLong("CHANNEL_ID"));
                    record.setChannelDesc(rs.getString("CHANNEL_DESC"));
                    record.setTranRefTxt(rs.getString("TRAN_REF_TXT"));
                    record.setChqNo(rs.getString("CHQ_NO"));
                    record.setTranDesc(rs.getString("TRAN_DESC"));
                    record.setDrCrInd(rs.getString("DR_CR_IND"));
                    record.setAcctCrncyCdIso(rs.getString("ACCT_CRNCY_CD_ISO"));
                    record.setAcctAmt(rs.getBigDecimal("ACCT_AMT"));
                    record.setTxnContraAcctNo(rs.getString("TXN_CONTRA_ACCT_NO"));
                    record.setDepositorPayeeNm(rs.getString("DEPOSITOR_PAYEE_NM"));
                    record.setRevTranJournalId(rs.getLong("REV_TRAN_JOURNAL_ID"));
                    record.setRevTxnFg(rs.getString("REV_TXN_FG"));
                    record.setStmntBal(rs.getBigDecimal("STMNT_BAL"));
                    records.add(record);
                }
            }
        }
        catch (Exception ex)
        {
            getLog().logEvent(ex);
        }
        return records;
    }
    
    public ArrayList<LHRecord> queryGLAccountHistory(String accountsList, Long acctHistId, Long pageSize)
    {
        ArrayList<LHRecord> records = new ArrayList<>();
        try (ResultSet rs = executeQueryToResultSet("SELECT Q.* FROM (SELECT ACCT_HIST_ID, TRAN_JOURNAL_ID, SYS_CREATE_TS, EVENT_CD, GL_ACCT_NO, ORIGIN_BU_ID, BU_NM, TRAN_DT, VALUE_DT, CHANNEL_ID,CHANNEL_DESC, TRAN_REF_TXT, CHQ_NO, TRAN_DESC, DR_CR_IND, CRNCY_CD_ISO, ACCT_AMT, CONTRA_ACCT_NO, STMNT_BAL FROM " + APController.coreSchemaName + ".V_GL_ACCOUNT_HISTORY WHERE GL_ACCT_NO IN (" + getWorker().cleanCSVList(accountsList) + ") AND ACCT_HIST_ID>" + acctHistId + " ORDER BY ACCT_HIST_ID ASC) Q WHERE ROWNUM<=" + pageSize))
        {
            if (rs != null)
            {
                rs.beforeFirst();
                while (rs.next())
                {
                    LHRecord record = new LHRecord();
                    record.setAcctHistId(rs.getLong("ACCT_HIST_ID"));
                    record.setTranJournalId(rs.getLong("TRAN_JOURNAL_ID"));
                    record.setSysCreateTs(rs.getDate("SYS_CREATE_TS"));
                    record.setEventCd(rs.getString("EVENT_CD"));
                    record.setGlAcctNo(rs.getString("GL_ACCT_NO"));
                    record.setOriginBuId(rs.getLong("ORIGIN_BU_ID"));
                    record.setBuNm(rs.getString("BU_NM"));
                    record.setTranDt(rs.getDate("TRAN_DT"));
                    record.setValueDt(rs.getDate("VALUE_DT"));
                    record.setChannelId(rs.getLong("CHANNEL_ID"));
                    record.setChannelDesc(rs.getString("CHANNEL_DESC"));
                    record.setTranRefTxt(rs.getString("TRAN_REF_TXT"));
                    record.setChqNo(rs.getString("CHQ_NO"));
                    record.setTranDesc(rs.getString("TRAN_DESC"));
                    record.setDrCrInd(rs.getString("DR_CR_IND"));
                    record.setCrncyCdIso(rs.getString("CRNCY_CD_ISO"));
                    record.setAcctAmt(rs.getBigDecimal("ACCT_AMT"));
                    record.setContraAcctNo(rs.getString("CONTRA_ACCT_NO"));
                    record.setStmntBal(rs.getBigDecimal("STMNT_BAL"));
                    records.add(record);
                }
            }
        }
        catch (Exception ex)
        {
            getLog().logEvent(ex);
        }
        return records;
    }
    
    public boolean upsertPayment(AXPayment payment)
    {
        return checkExists("SELECT CODE FROM " + APController.cmSchemaName + ".PHB_PAYMENT WHERE CHANNEL='" + payment.getChannel() + "' AND CODE='" + payment.getCode() + "'") ? updatePayment(payment) : savePayment(payment);
    }
    
    private boolean savePayment(AXPayment payment)
    {
        return executeUpdate("INSERT INTO " + APController.cmSchemaName + ".PHB_PAYMENT(REC_ID, CREATE_DT, CHANNEL, TXN_CD, TYPE, DESCRIPTION, CODE_FIELD, CODE, DETAIL_FIELD, ACCOUNT, SYS_USER, SYS_DATE, REC_ST) VALUES(" + APController.cmSchemaName + ".SEQ_PHB_PAYMENT.NEXTVAL, SYSDATE, '" + payment.getChannel() + "', '" + payment.getTxnCd() + "', '" + payment.getType() + "', '" + payment.getDescription() + "', " + payment.getCodeField() + ", '" + payment.getCode() + "', " + payment.getDetailField() + ", '" + payment.getAccount() + "', '" + payment.getSysUser() + "', SYSDATE, '" + payment.getStatus() + "')", true);
    }
    
    private boolean updatePayment(AXPayment payment)
    {
        return executeUpdate("UPDATE " + APController.cmSchemaName + ".PHB_PAYMENT SET DESCRIPTION='" + payment.getDescription() + "', ACCOUNT='" + payment.getAccount() + "', TXN_CD='" + payment.getTxnCd() + "', TYPE='" + payment.getType() + "', CODE_FIELD=" + payment.getCodeField() + ", DETAIL_FIELD=" + payment.getDetailField() + ", SYS_USER='" + payment.getSysUser() + "', SYS_DATE=SYSDATE, REC_ST='" + payment.getStatus() + "' WHERE REC_ID=" + payment.getRecId(), true);
    }
    
    public boolean saveEnrollLog(MURecord record)
    {
        return executeUpdate("INSERT INTO " + APController.cmSchemaName + ".PHL_ENRL_LOG(REC_ID, CREATE_DT, CHANNEL_ID, ADDRESS, ACCESS_CD, CUST_NO, ACCOUNT, ALIAS, ROLE, TYPE, STATE, RESULT) VALUES(" + APController.cmSchemaName + ".SEQ_PHL_ENRL_LOG.NEXTVAL, SYSDATE, " + record.getChannelId() + ", '" + record.getAddress() + "', '" + record.getAccessCd() + "', '" + record.getCustNo() + "', '" + record.getAccount() + "', '" + record.getAlias().replace("'", "''") + "', '" + record.getRole() + "', '" + record.getType() + "', '" + record.getState() + "', '" + record.getResult() + "')", true);
    }
    
    public boolean isAccountPermitted(Long schemeId, String accountNumber)
    {
        return checkExists("SELECT ACCT_NO FROM " + APController.coreSchemaName + ".ACCOUNT WHERE ACCT_NO='" + accountNumber + "' AND PROD_ID IN (SELECT PROD_ID FROM " + APController.coreSchemaName + ".PRODUCT_CHANNEL_SCHEME WHERE SCHEME_ID=" + schemeId + ")");
    }
    
    public CNActivity queryMonthActivity(Long channelId, String accountNumber, String txnCode)
    {
        CNActivity cNActivity = new CNActivity();
        try (ResultSet rs = executeQueryToResultSet("SELECT COUNT(*) AS COUNT, NVL(SUM(AMOUNT),0) AS TOTAL FROM " + APController.cmSchemaName + ".PHL_TXN_LOG WHERE REC_ST='A' AND CHANNEL_ID=" + channelId + " AND TXN_CODE='" + txnCode + "' AND ACCOUNT='" + accountNumber + "' AND TXN_DATE>=TRUNC(ADD_MONTHS(LAST_DAY(SYSDATE),-1)+1) AND TXN_DATE<=TRUNC(LAST_DAY(SYSDATE))"))
        {
            if (rs != null && rs.next())
            {
                cNActivity.setCount(rs.getBigDecimal("COUNT"));
                cNActivity.setTotal(rs.getBigDecimal("TOTAL"));
            }
        }
        catch (Exception ex)
        {
            getLog().logEvent(ex);
        }
        return cNActivity;
    }
    
    public CNActivity queryDayActivity(Long channelId, String accountNumber, String txnType)
    {
        CNActivity cNActivity = new CNActivity();
        try (ResultSet rs = executeQueryToResultSet("SELECT COUNT(*) AS COUNT, NVL(SUM(AMOUNT),0) AS TOTAL FROM " + APController.cmSchemaName + ".PHL_TXN_LOG WHERE REC_ST='A' AND CHANNEL_ID=" + channelId + " AND TXN_TYPE='" + txnType + "' AND ACCOUNT='" + accountNumber + "' AND TXN_DATE>=TRUNC(SYSDATE)"))
        {
            if (rs != null && rs.next())
            {
                cNActivity.setCount(rs.getBigDecimal("COUNT"));
                cNActivity.setTotal(rs.getBigDecimal("TOTAL"));
            }
        }
        catch (Exception ex)
        {
            getLog().logEvent(ex);
        }
        return cNActivity;
    }
    
    public int countRecords(String query)
    {
        int count = 0;
        try (ResultSet rs = executeQueryToResultSet(query))
        {
            count = getRowCount(rs);
        }
        catch (Exception ex)
        {
            getLog().logEvent(ex);
        }
        return count;
    }
    
    private boolean saveValues(AXCharge charge)
    {
        boolean RC = deleteValues(charge);
        for (TCValue tCValue : charge.getValues().values())
        {
            tCValue.setValueId(nextValueId());
            if (executeUpdate("INSERT INTO " + APController.cmSchemaName + ".PHC_VALUE(REC_ID, PARENT_KEY, CURRENCY, VALUE_TYPE, MIN_VALUE, MAX_VALUE, VALUE) VALUES(" + tCValue.getValueId() + ", '" + charge.getRecId() + "', '" + tCValue.getCurrency() + "', '" + tCValue.getChargeType() + "', " + tCValue.getMinAmount() + ", " + tCValue.getMaxAmount() + ", " + tCValue.getValue() + ")", true))
            {
                RC = saveTiers(String.valueOf(tCValue.getValueId()), tCValue.getTiers()) && saveDeductions(tCValue);
            }
        }
        return RC;
    }
    
    private boolean saveDeductions(TCValue value)
    {
        boolean RC = deleteDeductions(value);
        for (TCDeduction tCDeduction : value.getDeductions().values())
        {
            tCDeduction.setRecId(nextDeductionId());
            RC = executeUpdate("INSERT INTO " + APController.cmSchemaName + ".PHC_DEDUCTION(REC_ID, PARENT_KEY, BASIS, DESCRIPTION, ACCOUNT, VALUE_TYPE, VALUE) VALUES(" + tCDeduction.getRecId() + ", '" + value.getValueId() + "', '" + tCDeduction.getBasis() + "', '" + tCDeduction.getDescription() + "', '" + tCDeduction.getAccount() + "', '" + tCDeduction.getValueType() + "', " + tCDeduction.getValue() + ")", true);
        }
        return RC;
    }
    
    private boolean saveWaivers(AXCharge charge)
    {
        boolean RC = deleteWaivers(charge);
        for (TCWaiver tXWaiver : charge.getWaivers().values())
        {
            tXWaiver.setWaiverId(nextWaiverId());
            RC = executeUpdate("INSERT INTO " + APController.cmSchemaName + ".PHC_WAIVER(REC_ID, PARENT_KEY, PROD_ID, MATCH_ACCT, WAIVED_PERC, CONDITION, THRESHOLD) VALUES(" + tXWaiver.getWaiverId() + ", '" + charge.getRecId() + "', " + tXWaiver.getProductId() + ", '" + tXWaiver.getMatchAccount() + "', " + tXWaiver.getWaivedPercentage() + ", '" + tXWaiver.getWaiverCondition() + "', " + tXWaiver.getThresholdValue() + ")", true);
        }
        return RC;
    }
    
    public boolean moveCustomerWFItem(long custId, long newBuId)
    {
        return executeUpdate("UPDATE " + APController.coreSchemaName + ".WF_WORK_ITEM SET BU_ID=" + newBuId + " WHERE WORK_ITEM_ID=(SELECT MAX(WORK_ITEM_ID) FROM " + APController.coreSchemaName + ".WF_WORK_ITEM WHERE CUST_ID=" + custId + ")", true);
    }
    
    public CNAccount queryAnyAccount(String accountNumber)
    {
        return getWorker().isLedger(accountNumber) ? queryGLAccount(accountNumber) : queryDepositAccount(accountNumber);
    }
    
    private boolean deleteDeductions(TCValue value)
    {
        return executeUpdate("DELETE " + APController.cmSchemaName + ".PHC_DEDUCTION WHERE PARENT_KEY='" + value.getValueId() + "'", true);
    }
    
    private boolean deleteValues(AXCharge charge)
    {
        return executeUpdate("DELETE " + APController.cmSchemaName + ".PHC_VALUE WHERE PARENT_KEY='" + charge.getRecId() + "'", true);
    }
    
    private boolean deleteWaivers(AXCharge charge)
    {
        return executeUpdate("DELETE " + APController.cmSchemaName + ".PHC_WAIVER WHERE PARENT_KEY='" + charge.getRecId() + "'", true);
    }
    
    public int countPendingAlerts()
    {
        return countRecords("SELECT REC_ST FROM " + APController.cmSchemaName + ".PHA_ALERTS WHERE REC_ST='P'");
    }
    
    public int countPendingAlerts(ArrayList<String> prefixes)
    {
        return countRecords("SELECT REC_ST FROM " + APController.cmSchemaName + ".PHA_ALERTS WHERE AND REC_ST='P'");
    }
    
    public boolean upsertCharge(AXCharge charge)
    {
        return checkExists("SELECT REC_ID FROM " + APController.cmSchemaName + ".PHC_CHARGE WHERE REC_ID=" + charge.getRecId()) ? updateCharge(charge) : saveCharge(charge);
    }
    
    private boolean saveCharge(AXCharge charge)
    {
        return executeUpdate("INSERT INTO " + APController.cmSchemaName + ".PHC_CHARGE(REC_ID, REC_CD, CREATE_DT, DESCRIPTION, ACCOUNT, LEDGER, SCHEME, SYS_USER, SYS_DATE, REC_ST) VALUES(" + charge.getRecId() + ", '" + charge.getCode() + "', SYSDATE, '" + charge.getDescription() + "', '" + charge.getChargeAccount() + "', '" + charge.getChargeLedger() + "', '" + charge.getScheme() + "', '" + charge.getSysUser() + "', SYSDATE, '" + charge.getStatus() + "')", true) ? saveValues(charge) && saveWaivers(charge) : false;
    }
    
    private boolean updateCharge(AXCharge charge)
    {
        return executeUpdate("UPDATE " + APController.cmSchemaName + ".PHC_CHARGE SET REC_CD='" + charge.getCode() + "', DESCRIPTION='" + charge.getDescription() + "', ACCOUNT='" + charge.getChargeAccount() + "', LEDGER='" + charge.getChargeLedger() + "', SCHEME='" + charge.getScheme() + "', SYS_USER='" + charge.getSysUser() + "', SYS_DATE=SYSDATE, REC_ST='" + charge.getStatus() + "' WHERE REC_ID=" + charge.getRecId(), true) ? saveValues(charge) && saveWaivers(charge) : false;
    }
    
    public boolean upsertScheme(TCScheme scheme)
    {
        return checkExists("SELECT REC_CD FROM " + APController.cmSchemaName + ".PHC_SCHEME WHERE REC_CD='" + scheme.getCode() + "'") ? updateScheme(scheme) : saveScheme(scheme);
    }
    
    private boolean saveScheme(TCScheme scheme)
    {
        return executeUpdate("INSERT INTO " + APController.cmSchemaName + ".PHC_SCHEME(REC_CD, CREATE_DT, CHANNEL, DESCRIPTION, SYS_USER, SYS_DATE, REC_ST) VALUES('" + scheme.getCode() + "', SYSDATE, '" + scheme.getChannel() + "', '" + scheme.getDescription() + "', '" + scheme.getSysUser() + "', SYSDATE, '" + scheme.getStatus() + "')", true);
    }
    
    private boolean updateScheme(TCScheme scheme)
    {
        return executeUpdate("UPDATE " + APController.cmSchemaName + ".PHC_SCHEME SET CHANNEL='" + scheme.getChannel() + "', DESCRIPTION='" + scheme.getDescription() + "', SYS_USER='" + scheme.getSysUser() + "', SYS_DATE=SYSDATE, REC_ST='" + scheme.getStatus() + "' WHERE REC_CD='" + scheme.getCode() + "'", true);
    }
    
    public boolean upsertBank(AXBank bank)
    {
        return checkExists("SELECT BIN FROM " + APController.cmSchemaName + ".PHL_BANK WHERE BIN='" + bank.getBin() + "'") ? updateBank(bank) : saveBank(bank);
    }
    
    private boolean saveBank(AXBank bank)
    {
        return executeUpdate("INSERT INTO " + APController.cmSchemaName + ".PHL_BANK(BIN, CREATE_DT, BANK_NAME, LORO_LEDGER, TRANSFER_LEDGER, SUSPENSE_LEDGER, SYS_USER, SYS_DATE, REC_ST) VALUES('" + bank.getBin() + "', SYSDATE, '" + bank.getName() + "', '" + bank.getLoroLedger() + "', '" + bank.getTransferLedger() + "', '" + bank.getSuspenseLedger() + "', '" + bank.getSysUser() + "', SYSDATE, '" + bank.getStatus() + "')", true);
    }
    
    private boolean updateBank(AXBank bank)
    {
        return executeUpdate("UPDATE " + APController.cmSchemaName + ".PHL_BANK SET BANK_NAME='" + bank.getName() + "', LORO_LEDGER='" + bank.getLoroLedger() + "', TRANSFER_LEDGER='" + bank.getTransferLedger() + "', SUSPENSE_LEDGER='" + bank.getSuspenseLedger() + "', SYS_USER='" + bank.getSysUser() + "', SYS_DATE=SYSDATE, REC_ST='" + bank.getStatus() + "' WHERE BIN='" + bank.getBin() + "'", true);
    }
    
    public boolean upsertUser(AXUser user)
    {
        return checkExists("SELECT EMP_NO FROM " + APController.cmSchemaName + ".PHU_USER WHERE EMP_NO='" + user.getUserNumber() + "'") ? updateUser(user) : saveUser(user);
    }
    
    private boolean saveUser(AXUser user)
    {
        return executeUpdate("INSERT INTO " + APController.cmSchemaName + ".PHU_USER(EMP_NO, CREATE_DT, LOGIN_ID, STAFF_NAME, SYS_USER, SYS_DATE, REC_ST) VALUES('" + user.getUserNumber() + "', SYSDATE, '" + user.getUserName() + "', '" + user.getStaffName() + "', '" + user.getSysUser() + "', SYSDATE, '" + user.getStatus() + "')", true) && saveUserRoles(user);
    }
    
    private boolean updateUser(AXUser user)
    {
        return executeUpdate("UPDATE " + APController.cmSchemaName + ".PHU_USER SET LOGIN_ID='" + user.getUserName() + "', STAFF_NAME='" + user.getStaffName() + "', SYS_USER='" + user.getSysUser() + "', SYS_DATE=SYSDATE, REC_ST='" + user.getStatus() + "' WHERE EMP_NO='" + user.getUserNumber() + "'", true) && saveUserRoles(user);
    }
    
    private boolean saveUserRoles(AXUser user)
    {
        boolean RC = executeUpdate("DELETE " + APController.cmSchemaName + ".PHU_ROLE WHERE EMP_NO='" + user.getUserNumber() + "'", true);
        for (String role : user.getRoles())
        {
            RC = executeUpdate("INSERT INTO " + APController.cmSchemaName + ".PHU_ROLE(EMP_NO, ROLE) VALUES('" + user.getUserNumber() + "', '" + role + "')", true);
        }
        return RC;
    }
    
    private String checkPassword(CNUser cNUser, String password)
    {
        if (!getWorker().isBlank(password) && getCrypt().isEqEncrypted(password))
        {
            String pwd = new AXCrypt(String.valueOf(cNUser.getUserId())).encrypt(getCrypt().eqDecrypt(password));
            changeUserPin(cNUser.getUserId(), pwd);
            return pwd;
        }
        return password;
    }
    
    public boolean updateAccountEvent(CAEvent event)
    {
        return executeUpdate("UPDATE " + APController.coreSchemaName + ".CUST_CHANNEL_ACCOUNT$AUD SET FORWARDED='Y' WHERE FORWARDED IS NULL AND CUST_CHANNEL_ACCT_ID=" + event.getCustChannelAcctId(), true);
    }
    
    public boolean changeUserPin(Long userId, String newPin)
    {
        return executeUpdate("UPDATE " + APController.coreSchemaName + ".CUSTOMER_CHANNEL_USER SET PASSWORD='" + newPin + "', PWD_RESET_FG='N' WHERE CUST_CHANNEL_USER_ID=" + userId, true) && unlockChannelUser(userId);
    }
    
    public boolean updateUserKey(Long userId, String newKey)
    {
        return executeUpdate("UPDATE " + APController.coreSchemaName + ".CUSTOMER_CHANNEL_USER SET QUIZ_CD='" + newKey + "' WHERE CUST_CHANNEL_USER_ID=" + userId, true);
    }
    
    public boolean unlockChannelUser(Long userId)
    {
        return executeUpdate("UPDATE " + APController.coreSchemaName + ".CUSTOMER_CHANNEL_USER SET LOCKED_FG='N', RANDOM_SEED=0, SECURITY_CD=NULL, EXPIRY_DT=SYSDATE+180 WHERE CUST_CHANNEL_USER_ID=" + userId, true);
    }
    
    public boolean pushUserExpiry(Long userId)
    {
        return executeUpdate("UPDATE " + APController.coreSchemaName + ".CUSTOMER_CHANNEL_USER SET EXPIRY_DT=SYSDATE+180 WHERE CUST_CHANNEL_USER_ID=" + userId, true);
    }
    
    private boolean updateNextEntityId(String tableName, String columnName)
    {
        return executeUpdate("UPDATE " + APController.coreSchemaName + ".ENTITY SET NEXT_NO=(SELECT MAX(" + columnName + ")+1 FROM " + APController.coreSchemaName + "." + tableName + ") WHERE ENTITY_NM = '" + tableName + "'", true);
    }
    
    public boolean saveChannelAccount(CNUser cNUser, CNAccount cNAccount)
    {
        return executeUpdate("INSERT INTO " + APController.coreSchemaName + ".CUST_CHANNEL_ACCOUNT (CUST_CHANNEL_ACCT_ID, CUST_ID, CHANNEL_ID, ACCT_ID, SHORT_NAME, REC_ST, VERSION_NO, ROW_TS, USER_ID, CREATE_DT, CREATED_BY, SYS_CREATE_TS, CUST_CHANNEL_ID) VALUES ((SELECT MAX(CUST_CHANNEL_ACCT_ID) + 1 FROM " + APController.coreSchemaName + ".CUST_CHANNEL_ACCOUNT), " + cNAccount.getCustId() + ", " + cNUser.getChannelId() + ", " + cNAccount.getAcctId() + ", NULL, 'A', 1, SYSDATE, 'SYSTEM', SYSDATE, 'SYSTEM', SYSDATE, " + cNUser.getCustChannelId() + ")", true) && updateNextEntityId("CUST_CHANNEL_ACCOUNT", "CUST_CHANNEL_ACCT_ID");
    }
    
    public boolean updateChannelAccount(CNUser cNUser, CNAccount cNAccount)
    {
        return executeUpdate("UPDATE " + APController.coreSchemaName + ".CUSTOMER_CHANNEL SET CHRG_ACCT_ID=" + cNAccount.getAcctId() + " WHERE CUST_CHANNEL_ID=" + cNUser.getCustChannelId(), true);
    }
    
    public boolean updateAccessName(CNUser cNUser)
    {
        return executeUpdate("UPDATE " + APController.coreSchemaName + ".CUSTOMER_CHANNEL_USER SET ACCESS_NM='" + cNUser.getAccessName() + "' WHERE CUST_CHANNEL_USER_ID=" + cNUser.getUserId(), true);
    }
    
    public boolean updateEnrollmentDevice(Long schemeId, String accessCode, String deviceId)
    {
        return executeUpdate("UPDATE " + APController.coreSchemaName + ".CUSTOMER_CHANNEL_USER SET DEVICE_ID='" + deviceId + "' WHERE ACCESS_CD='" + accessCode + "' AND CHANNEL_SCHEME_ID=" + schemeId, true);
    }
    
    public void updateAlert(MXMessage mXRequest)
    {
        executeUpdate("UPDATE " + APController.cmSchemaName + ".PHA_ALERTS SET ALERT_CODE='" + mXRequest.getAlertCode() + "', CONTACT='" + mXRequest.getMsisdn() + "', CHRG_AMT=" + mXRequest.getChargeAmount() + ", CHG_ID='" + mXRequest.getChargeId() + "', REC_ST='" + mXRequest.getStatus() + "' WHERE ALERT_ID=" + mXRequest.getAlertId(), true);
    }
    
    public void saveMessage(MXMessage mXMessage)
    {
        executeUpdate("INSERT INTO " + APController.cmSchemaName + ".PHA_MESSAGE(REC_ID, CREATE_DT, ALERT_ID, ALERT_CODE, CUST_ID, CONTACT, MESSAGE, RESP_ID, REC_ST) VALUES(" + APController.cmSchemaName + ".SEQ_PHA_MESSAGE.NEXTVAL, SYSDATE, " + mXMessage.getAlertId() + ", '" + mXMessage.getAlertCode() + "', " + mXMessage.getCustId() + ", '" + mXMessage.getMsisdn() + "', '" + String.valueOf(mXMessage.getMessage()).replace("'", "''") + "', '" + mXMessage.getResponseId() + "', '" + mXMessage.getStatus() + "')", true);
    }
    
    public void expireOldAlerts()
    {
        executeUpdate("UPDATE " + APController.cmSchemaName + ".PHA_ALERTS SET REC_ST='E' WHERE ALERT_DATE<SYSDATE-5 AND REC_ST='P'", true);
    }
    
    public String queryBankName()
    {
        return getWorker().capitalize(queryParameter("S04", String.class));
    }
    
    public String querySwiftCode()
    {
        return getWorker().capitalize(queryParameter("S14", String.class));
    }
    
    public Long nextChargeId()
    {
        return nextSequenceId("SEQ_PHC_CHARGE");
    }
    
    public Long nextDeductionId()
    {
        return nextSequenceId("SEQ_PHC_DEDUCTION");
    }
    
    public Long nextValueId()
    {
        return nextSequenceId("SEQ_PHC_VALUE");
    }
    
    public Long nextWaiverId()
    {
        return nextSequenceId("SEQ_PHC_WAIVER");
    }

    /**
     * @return the connection
     */
    public Connection getConnection()
    {
        return connection;
    }

    /**
     * @param connection the connection to set
     */
    public void setConnection(Connection connection)
    {
        this.connection = connection;
    }

    /**
     * @return the alertStatement
     */
    public CallableStatement getAlertStatement()
    {
        return alertStatement;
    }

    /**
     * @param alertStatement the alertStatement to set
     */
    public void setAlertStatement(CallableStatement alertStatement)
    {
        this.alertStatement = alertStatement;
    }

    /**
     * @return the worker
     */
    public AXWorker getWorker()
    {
        return getBox().getWorker();
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

    /**
     * @param box the box to set
     */
    public void setBox(ATBox box)
    {
        this.box = box;
    }
    
    private APLog getLog()
    {
        return getBox().getLog();
    }
}
