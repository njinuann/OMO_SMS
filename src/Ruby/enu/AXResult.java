/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Ruby.enu;

/**
 *
 * @author Pecherk
 */
public enum AXResult
{
    Success("00", ARType.Success),
    Duplicate("14", ARType.Ignore),
    Daily_Limit("13"),
    Insufficient_Funds("01"),
    Invalid_Account("22"),
    Not_Linked("02"),
    Invalid_Email("57"),
    Invalid_Amount("11"),
    Low_Amount("11"),
    High_Amount("12"),
    Success_Fosa("31", ARType.Success),
    Credit_Limit("23"),
    Min_Balance("52"),
    Not_Eligible("52"),
    Blacklisted("52"),
    Missed_Deposit("52"),
    Has_Loan("04"),
    Has_Arrears("04"),
    Below_Min("54"),
    Above_Max("55"),
    Missing_Name("41"),
    Invalid_Branch("42"),
    System_Error("06"),
    Invalid_Identity("40"),
    Identity_Exists("17"),
    Invalid_User("10"),
    Invalid_Mobile("43"),
    Invalid_Pan("46"),
    Txn_Not_Found("59"),
    Match_Not_Found("25"),
    Invalid_Date("58"),
    Unknown_Txn("06"),
    Missing_Photo("30"),
    Missing_Signature("30"),
    Not_Reversible("06"),
    Processing("14", ARType.Ignore),
    Invalid_Parameter("06"),
    Invalid_Product("06"),
    Not_Found("09"),
    Failed("30"),
    Retry("15"),
    /*ADDITION*/
    Defaulted_Gurantor("52"),
    Monthly_Loan_exists("52"),
    Akiba_Loan_exists("52"),
    Restricted_Customer("52"),
    Restricted_TxType("06");

    private String code;
    private ARType type;

    AXResult(String code)
    {
        this(code, ARType.Fail);
    }

    AXResult(String code, ARType type)
    {
        setCode(code);
        setType(type);
    }

    /**
     * @return the code
     */
    public String getCode()
    {
        return code;
    }

    /**
     * @param code the code to set
     */
    public void setCode(String code)
    {
        this.code = code;
    }

    /**
     * @return the type
     */
    public ARType getType()
    {
        return type;
    }

    /**
     * @param type the type to set
     */
    public void setType(ARType type)
    {
        this.type = type;
    }

    public String getMessage()
    {
        return name().replaceAll("_", " ");
    }
}
