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
public enum ALHeader
{
    RecId("Record Id"),
    TxnDate("Txn Date"),
    ReqId("Request Id"),
    ChannelId("Channel Id"),
    Channel("Channel"),
    Application("Application"),
    Operator("Operator"),
    TxnCode("Txn Code"),
    TranType("Tran Type"),
    BankCode("Bank Code"),
    VmtNumber("VMT Number"),
    Msisdn("Mobile Number"),
    VmtAcct("VMT Account"),
    GroupId("Group Id"),
    ShortCode("Short Code"),
    TimeStamp("Time Stamp"),
    TranId("Tran Id"),
    LinkType("Link Type"),
    AppTranId("App Tran Id"),
    Account("Account"),
    Contra("Contra"),
    Currency("Currency"),
    Amount("Amount"),
    Description("Description"),
    Charge("Charge"),
    DueDate("Due Date"),
    MaxAmt("Max Amt"),
    State("State"),
    Receipt("Receipt"),
    Balance("Balance"),
    XapiCode("Xapi Code"),
    XapiMsg("Xapi Message"),
    Result("Result"),
    TxnId("Txn Id"),
    ChgId("Chg Id"),
    Response("Response"),
    AlertId("Alert Id"),
    CreateDt("Create Date"),
    AlertCode("Alert Code"),
    AlertType("Alert Type"),
    CustId("Cust Id"),
    CustName("Customer Name"),
    ChargeAcct("Charge Account"),
    SchemeId("Scheme Id"),
    AccessCode("Access Code"),
    Message("Message"),
    RecSt("Status");

    private String text;

    ALHeader(String text)
    {
        setText(text);
    }

    @Override
    public String toString()
    {
        return getText();
    }

    /**
     * @return the text
     */
    public String getText()
    {
        return text;
    }

    /**
     * @param text the text to set
     */
    public void setText(String text)
    {
        this.text = text;
    }
}
