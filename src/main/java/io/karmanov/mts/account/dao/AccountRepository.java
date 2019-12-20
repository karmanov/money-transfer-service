package io.karmanov.mts.account.dao;

import io.karmanov.mts.account.model.Account;
import io.karmanov.mts.common.dao.JpaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class AccountRepository implements JpaRepository<Account> {

    private final Logger log = LoggerFactory.getLogger(AccountRepository.class);

    private final EntityManager entityManager;

    AccountRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public Optional<Account> findById(UUID id) {
        log.trace(">> findById({})", id);
        Account account = entityManager.find(Account.class, id);
        log.trace("<< findById()");
        return Optional.ofNullable(account);
    }

    @Override
    @Transactional
    public Optional<Account> findByIdForUpdate(UUID id) {
        Optional<Account> accountOpt = findById(id);
        accountOpt.ifPresent(a -> entityManager.lock(a, LockModeType.PESSIMISTIC_WRITE));
        return accountOpt;
    }

    @Override
    public void persist(Account account) {
        log.trace(">> persist({})", account);
        entityManager.persist(account);
        log.trace("<< persist()");
    }

    @Override
    public List<Account> listAll() {
        log.trace(">> listAll()");
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Account> cq = cb.createQuery(Account.class);
        Root<Account> rootEntry = cq.from(Account.class);
        CriteriaQuery<Account> all = cq.select(rootEntry);
        TypedQuery<Account> allQuery = entityManager.createQuery(all);
        log.debug("<< listAll()");
        return allQuery.getResultList();
    }
}
