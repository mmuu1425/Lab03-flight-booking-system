package com.example.bonusservice.service;

import com.example.bonusservice.entity.Privilege;
import com.example.bonusservice.entity.PrivilegeHistory;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BonusService {

    // 获取或创建用户特权
    Privilege getOrCreatePrivilege(String username);

    // 获取用户特权信息
    Optional<Privilege> getPrivilege(String username);

    // 获取用户积分历史
    List<PrivilegeHistory> getPrivilegeHistory(String username);

    // 处理购买时的积分操作
    BonusOperationResult processPurchase(String username, UUID ticketUid, Integer price, boolean paidFromBalance);

    // 处理退票时的积分操作
    void processRefund(String username, UUID ticketUid);

    // 内部结果类
    class BonusOperationResult {
        private final int paidByMoney;
        private final int paidByBonuses;
        private final Privilege privilege;

        public BonusOperationResult(int paidByMoney, int paidByBonuses, Privilege privilege) {
            this.paidByMoney = paidByMoney;
            this.paidByBonuses = paidByBonuses;
            this.privilege = privilege;
        }

        public int getPaidByMoney() { return paidByMoney; }
        public int getPaidByBonuses() { return paidByBonuses; }
        public Privilege getPrivilege() { return privilege; }
    }
}