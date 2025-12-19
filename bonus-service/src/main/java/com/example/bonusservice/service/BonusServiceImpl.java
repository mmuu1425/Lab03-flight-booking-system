package com.example.bonusservice.service;

import com.example.bonusservice.entity.Privilege;
import com.example.bonusservice.entity.PrivilegeHistory;
import com.example.bonusservice.repository.PrivilegeRepository;
import com.example.bonusservice.repository.PrivilegeHistoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class BonusServiceImpl implements BonusService {

    private final PrivilegeRepository privilegeRepository;
    private final PrivilegeHistoryRepository privilegeHistoryRepository;

    public BonusServiceImpl(PrivilegeRepository privilegeRepository,
                            PrivilegeHistoryRepository privilegeHistoryRepository) {
        this.privilegeRepository = privilegeRepository;
        this.privilegeHistoryRepository = privilegeHistoryRepository;
    }

    @Override
    public Privilege getOrCreatePrivilege(String username) {
        Optional<Privilege> existingPrivilege = privilegeRepository.findByUsername(username);
        if (existingPrivilege.isPresent()) {
            return existingPrivilege.get();
        }

        Privilege newPrivilege = new Privilege(username);
        return privilegeRepository.save(newPrivilege);
    }

    @Override
    public Optional<Privilege> getPrivilege(String username) {
        return privilegeRepository.findByUsername(username);
    }

    @Override
    public List<PrivilegeHistory> getPrivilegeHistory(String username) {
        Optional<Privilege> privilege = privilegeRepository.findByUsername(username);
        if (privilege.isEmpty()) {
            return List.of();
        }

        return privilegeHistoryRepository.findByPrivilegeId(privilege.get().getId());
    }

    @Override
    @Transactional
    public BonusOperationResult processPurchase(String username, UUID ticketUid, Integer price, boolean paidFromBalance) {
        // 1. 幂等性检查：如果这个ticket已经处理过，直接返回当前状态
        Optional<PrivilegeHistory> existingHistory = privilegeHistoryRepository.findByTicketUid(ticketUid);
        if (existingHistory.isPresent()) {
            PrivilegeHistory history = existingHistory.get();
            Privilege privilege = history.getPrivilege();

            // 根据历史记录推断支付方式
            int paidByBonuses = 0;
            int paidByMoney = price;

            if ("DEBIT_THE_ACCOUNT".equals(history.getOperationType())) {
                // 之前使用了积分支付
                paidByBonuses = Math.abs(history.getBalanceDiff());
                paidByMoney = price - paidByBonuses;
            }

            return new BonusOperationResult(paidByMoney, paidByBonuses, privilege);
        }

        // 2. 正常处理逻辑
        Privilege privilege = getOrCreatePrivilege(username);
        int paidByBonuses = 0;
        int paidByMoney = price;

        if (paidFromBalance && privilege.getBalance() > 0) {
            paidByBonuses = Math.min(privilege.getBalance(), price);
            paidByMoney = price - paidByBonuses;

            privilege.setBalance(privilege.getBalance() - paidByBonuses);
            privilegeRepository.save(privilege);

            PrivilegeHistory debitHistory = new PrivilegeHistory(privilege, ticketUid, -paidByBonuses, "DEBIT_THE_ACCOUNT");
            privilegeHistoryRepository.save(debitHistory);
        } else {
            int earnedBonuses = (int) (price * 0.1);
            privilege.setBalance(privilege.getBalance() + earnedBonuses);
            privilegeRepository.save(privilege);

            PrivilegeHistory fillHistory = new PrivilegeHistory(privilege, ticketUid, earnedBonuses, "FILL_IN_BALANCE");
            privilegeHistoryRepository.save(fillHistory);
        }

        updatePrivilegeStatus(privilege);
        return new BonusOperationResult(paidByMoney, paidByBonuses, privilege);
    }

    @Override
    @Transactional
    public void processRefund(String username, UUID ticketUid) {
        // 幂等性检查：如果已经有退款记录，不再处理
        Optional<PrivilegeHistory> existingRefund = privilegeHistoryRepository.findByTicketUid(ticketUid)
                .filter(h -> h.getDatetime().isAfter(LocalDateTime.now().minusMinutes(1))); // 最近1分钟

        if (existingRefund.isPresent()) {
            return; // 已经处理过退款
        }

        Optional<Privilege> privilegeOpt = privilegeRepository.findByUsername(username);
        if (privilegeOpt.isEmpty()) {
            return;
        }

        Privilege privilege = privilegeOpt.get();

        // 查找该票相关的历史记录
        List<PrivilegeHistory> historyRecords = privilegeHistoryRepository.findByPrivilegeId(privilege.getId());

        for (PrivilegeHistory history : historyRecords) {
            if (history.getTicketUid().equals(ticketUid)) {
                int reverseAmount = -history.getBalanceDiff();
                String reverseOperationType = history.getOperationType().equals("FILL_IN_BALANCE")
                        ? "DEBIT_THE_ACCOUNT"
                        : "FILL_IN_BALANCE";

                privilege.setBalance(privilege.getBalance() + reverseAmount);
                privilegeRepository.save(privilege);

                PrivilegeHistory reverseHistory = new PrivilegeHistory(privilege, ticketUid, reverseAmount, reverseOperationType);
                privilegeHistoryRepository.save(reverseHistory);

                updatePrivilegeStatus(privilege);
                break;
            }
        }
    }

    // 更新特权状态（基于积分余额）
    private void updatePrivilegeStatus(Privilege privilege) {
        int balance = privilege.getBalance();
        String newStatus;

        if (balance >= 1000) {
            newStatus = "GOLD";
        } else if (balance >= 500) {
            newStatus = "SILVER";
        } else {
            newStatus = "BRONZE";
        }

        if (!privilege.getStatus().equals(newStatus)) {
            privilege.setStatus(newStatus);
            privilegeRepository.save(privilege);
        }
    }
}