/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Ruby.model;

import java.util.ArrayList;

/**
 *
 * @author Pecherk
 */
public class CNUser
{
    private Long custId;
    private Long userId;
    private Long channelId;
    private Long schemeId;
    private String accessKey;
    private String accessName;
    private String accessCode;
    private String password;
    private String language;
    private Long custChannelId;
    private String securityCode;
    private Integer pinAttempts = 0;
    private Integer pukAttempts = 0;
    private Boolean locked = Boolean.FALSE;
    private Boolean pwdReset = Boolean.FALSE;
    private CNAccount chargeAccount = new CNAccount();
    private ArrayList<CNAccount> accounts = new ArrayList<>();
    private String newPassword;

    /**
     * @return the custId
     */
    public Long getCustId()
    {
        return custId;
    }

    /**
     * @param custId the custId to set
     */
    public void setCustId(Long custId)
    {
        this.custId = custId;
    }

    /**
     * @return the userId
     */
    public Long getUserId()
    {
        return userId;
    }

    /**
     * @param userId the userId to set
     */
    public void setUserId(Long userId)
    {
        this.userId = userId;
    }

    /**
     * @return the accessName
     */
    public String getAccessName()
    {
        return accessName;
    }

    /**
     * @param accessName the accessName to set
     */
    public void setAccessName(String accessName)
    {
        this.accessName = accessName;
    }

    /**
     * @return the accessCode
     */
    public String getAccessCode()
    {
        return accessCode;
    }

    /**
     * @param accessCode the accessCode to set
     */
    public void setAccessCode(String accessCode)
    {
        this.accessCode = accessCode;
    }

    /**
     * @return the locked
     */
    public Boolean isLocked()
    {
        return locked;
    }

    /**
     * @param locked the locked to set
     */
    public void setLocked(Boolean locked)
    {
        this.locked = locked;
    }

    /**
     * @return the password
     */
    public String getPassword()
    {
        return password;
    }

    /**
     * @param password the password to set
     */
    public void setPassword(String password)
    {
        this.password = password;
    }

    /**
     * @return the schemeId
     */
    public Long getSchemeId()
    {
        return schemeId;
    }

    /**
     * @param schemeId the schemeId to set
     */
    public void setSchemeId(Long schemeId)
    {
        this.schemeId = schemeId;
    }

    /**
     * @return the securityCode
     */
    public String getSecurityCode()
    {
        return securityCode;
    }

    /**
     * @param securityCode the securityCode to set
     */
    public void setSecurityCode(String securityCode)
    {
        this.securityCode = securityCode;
    }

    /**
     * @return the pwdReset
     */
    public Boolean isPwdReset()
    {
        return pwdReset;
    }

    /**
     * @param pwdReset the pwdReset to set
     */
    public void setPwdReset(Boolean pwdReset)
    {
        this.pwdReset = pwdReset;
    }

    /**
     * @return the imsi
     */
    public String getAccessKey()
    {
        return accessKey;
    }

    /**
     * @param accessKey the imsi to set
     */
    public void setAccessKey(String accessKey)
    {
        this.accessKey = accessKey;
    }

    /**
     * @return the language
     */
    public String getLanguage()
    {
        return language;
    }

    /**
     * @param language the language to set
     */
    public void setLanguage(String language)
    {
        this.language = language;
    }

    /**
     * @return the pinAttempts
     */
    public Integer getPinAttempts()
    {
        return pinAttempts;
    }

    /**
     * @param pinAttempts the pinAttempts to set
     */
    public void setPinAttempts(Integer pinAttempts)
    {
        this.pinAttempts = pinAttempts;
    }

    /**
     * @return the pukAttempts
     */
    public Integer getPukAttempts()
    {
        return pukAttempts;
    }

    /**
     * @param pukAttempts the pukAttempts to set
     */
    public void setPukAttempts(Integer pukAttempts)
    {
        this.pukAttempts = pukAttempts;
    }

    /**
     * @return the custChannelId
     */
    public Long getCustChannelId()
    {
        return custChannelId;
    }

    /**
     * @param custChannelId the custChannelId to set
     */
    public void setCustChannelId(Long custChannelId)
    {
        this.custChannelId = custChannelId;
    }

    /**
     * @return the chargeAccount
     */
    public CNAccount getChargeAccount()
    {
        return chargeAccount;
    }

    /**
     * @param chargeAccount the chargeAccount to set
     */
    public void setChargeAccount(CNAccount chargeAccount)
    {
        this.chargeAccount = chargeAccount;
    }

    /**
     * @return the accounts
     */
    public ArrayList<CNAccount> getAccounts()
    {
        return accounts;
    }

    /**
     * @param accounts the accounts to set
     */
    public void setAccounts(ArrayList<CNAccount> accounts)
    {
        this.accounts = accounts;
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
     * @return the newPassword
     */
    public String getNewPassword()
    {
        return newPassword;
    }

    /**
     * @param newPassword the newPassword to set
     */
    public void setNewPassword(String newPassword)
    {
        this.newPassword = newPassword;
    }
}
