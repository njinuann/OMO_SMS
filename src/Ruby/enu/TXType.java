/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Ruby.enu;

import java.io.Serializable;

/**
 *
 * @author Pecherk
 */
public enum TXType implements Serializable
{
    Alert(TXGroup.Advice, "TA"),
    MpesaVerify(TXGroup.Verify, "MV"),
    MpesaAdvice(TXGroup.Advice, "PA", false),
    Registration(TXGroup.Enrollment, "MR"),
    UsersQuery(TXGroup.Query, "UQ"),
    MemberAccounts(TXGroup.Query, "MA"),
    AccountDetails(TXGroup.Query, "AD"),
    MemberImage(TXGroup.Query, "MI"),
    LoanAccounts(TXGroup.Query, "LA"),
    SavingsAccounts(TXGroup.Query, "SA"),
    CapitalAccounts(TXGroup.Query, "CA"),
    NWDAccounts(TXGroup.Query, "NA"),
    LoanTypes(TXGroup.Query, "LT"),
    StatementTransactions(TXGroup.Statement, "ST"),
    MemberEligibility(TXGroup.Score, "ME"),
    BranchCodes(TXGroup.Query, "BC"),
    ReferAFriend(TXGroup.Request, "RF"),
    SavingBalance(TXGroup.Balance, "SB", 700),
    CapitalBalance(TXGroup.Balance, "CB", 701),
    LoanBalance(TXGroup.Balance, "LB", 702),
    NWDBalance(TXGroup.Balance, "NB", 703),
    MiniStatement(TXGroup.Statement, "MS", 704),
    DepositLoan(TXGroup.Repayment, "DL"),
    TransferLoan(TXGroup.Repayment, "TL"),
    Deposit(TXGroup.Deposit, "DP", 5, 6, 7, 18, 23, 600, 601, 705),
    Withdrawal(TXGroup.Withdrawal, "WD", 706),
    Airtime(TXGroup.Payment, "AT", 707),
    Transfer(TXGroup.Transfer, "TR", 708),
    Statement(TXGroup.Statement, "SR", 709),
    ApplyLoan(TXGroup.Loan, "AL", 710),
    VerveeCash(TXGroup.Payment, "VC", 712),
    GoTV(TXGroup.Payment, "GT", 713),
    KenyaPower(TXGroup.Payment, "KP", 714, 722, 735),
    KisumuWater(TXGroup.Payment, "KW", 715),
    NairobiWater(TXGroup.Payment, "NW", 716),
    NHIF(TXGroup.Payment, "NH", 717),
    ZUKU(TXGroup.Payment, "ZK", 718),
    MultiChoice(TXGroup.Payment, "MC", 719),
    StarTimes(TXGroup.Payment, "SM", 720),
    Safaricom(TXGroup.Payment, "SF", 721),
    OtherPayment(TXGroup.Payment, "OP", 723),
    OutwardPesaLink(TXGroup.Transfer, "PL", 724),
    InwardPesaLink(TXGroup.Deposit, "PC", 737),
    MpesaDeposit(TXGroup.Deposit, "MD"),
    MpesaLoan(TXGroup.Repayment, "ML"),
    LoanRecovery(TXGroup.Recovery, "LR"),
    Reversal(TXGroup.Other, "RV"),
    Unknown(TXGroup.Other, "UK");

    private String code;
    private TXGroup group;
    private Integer[] vids;
    private boolean record = true;

    TXType(TXGroup group, String code, Integer... vids)
    {
        this(group, code, true, vids);
    }

    TXType(TXGroup group, String code, boolean record, Integer... vids)
    {
        setCode(code);
        setGroup(group);
        setRecord(record);
        setVids(vids);
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
     * @return the group
     */
    public TXGroup getGroup()
    {
        return group;
    }

    /**
     * @param group the group to set
     */
    public void setGroup(TXGroup group)
    {
        this.group = group;
    }

    /**
     * @return the vid
     */
    public Integer[] getVids()
    {
        return vids;
    }

    /**
     * @param vids the vids to set
     */
    public void setVids(Integer[] vids)
    {
        this.vids = vids;
    }

    /**
     * @return the record
     */
    public boolean isRecord()
    {
        return record;
    }

    /**
     * @param record the record to set
     */
    public void setRecord(boolean record)
    {
        this.record = record;
    }
}
