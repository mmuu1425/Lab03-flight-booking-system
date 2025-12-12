package com.example.bonusservice.dto;

public class BonusOperationResponse {
    private Integer paidByMoney;
    private Integer paidByBonuses;
    private PrivilegeShortInfo privilege;

    public BonusOperationResponse() {}

    public BonusOperationResponse(Integer paidByMoney, Integer paidByBonuses, PrivilegeShortInfo privilege) {
        this.paidByMoney = paidByMoney;
        this.paidByBonuses = paidByBonuses;
        this.privilege = privilege;
    }

    // Getters and Setters
    public Integer getPaidByMoney() { return paidByMoney; }
    public void setPaidByMoney(Integer paidByMoney) { this.paidByMoney = paidByMoney; }

    public Integer getPaidByBonuses() { return paidByBonuses; }
    public void setPaidByBonuses(Integer paidByBonuses) { this.paidByBonuses = paidByBonuses; }

    public PrivilegeShortInfo getPrivilege() { return privilege; }
    public void setPrivilege(PrivilegeShortInfo privilege) { this.privilege = privilege; }
}