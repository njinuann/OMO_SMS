/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Ruby.model;

import java.io.Serializable;

/**
 *
 * @author Pecherk
 */
public class GPMember implements Serializable
{
    private Long groupMemberId;
    private Long groupCustId;
    private String memberRefNo;
    private Long memberCustId;
    private String defaultAcctNo;
    private Long groupXshipId;
    private Long subGroupId;
    private String status;

    /**
     * @return the groupMemberId
     */
    public Long getGroupMemberId()
    {
        return groupMemberId;
    }

    /**
     * @param groupMemberId the groupMemberId to set
     */
    public void setGroupMemberId(Long groupMemberId)
    {
        this.groupMemberId = groupMemberId;
    }

    /**
     * @return the groupCustId
     */
    public Long getGroupCustId()
    {
        return groupCustId;
    }

    /**
     * @param groupCustId the groupCustId to set
     */
    public void setGroupCustId(Long groupCustId)
    {
        this.groupCustId = groupCustId;
    }

    /**
     * @return the memberRefNo
     */
    public String getMemberRefNo()
    {
        return memberRefNo;
    }

    /**
     * @param memberRefNo the memberRefNo to set
     */
    public void setMemberRefNo(String memberRefNo)
    {
        this.memberRefNo = memberRefNo;
    }

    /**
     * @return the memberCustId
     */
    public Long getMemberCustId()
    {
        return memberCustId;
    }

    /**
     * @param memberCustId the memberCustId to set
     */
    public void setMemberCustId(Long memberCustId)
    {
        this.memberCustId = memberCustId;
    }

    /**
     * @return the defaultAcctNo
     */
    public String getDefaultAcctNo()
    {
        return defaultAcctNo;
    }

    /**
     * @param defaultAcctNo the defaultAcctNo to set
     */
    public void setDefaultAcctNo(String defaultAcctNo)
    {
        this.defaultAcctNo = defaultAcctNo;
    }

    /**
     * @return the groupXshipId
     */
    public Long getGroupXshipId()
    {
        return groupXshipId;
    }

    /**
     * @param groupXshipId the groupXshipId to set
     */
    public void setGroupXshipId(Long groupXshipId)
    {
        this.groupXshipId = groupXshipId;
    }

    /**
     * @return the subGroupId
     */
    public Long getSubGroupId()
    {
        return subGroupId;
    }

    /**
     * @param subGroupId the subGroupId to set
     */
    public void setSubGroupId(Long subGroupId)
    {
        this.subGroupId = subGroupId;
    }

    /**
     * @return the status
     */
    public String getStatus()
    {
        return status;
    }

    /**
     * @param status the status to set
     */
    public void setStatus(String status)
    {
        this.status = status;
    }
}
