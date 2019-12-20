package io.karmanov.mts.transaction.dao;

import io.karmanov.mts.common.dao.JpaRepository;
import io.karmanov.mts.transaction.model.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class TransactionRepository implements JpaRepository<Transaction> {

    private final Logger log = LoggerFactory.getLogger(TransactionRepository.class);

    private final EntityManager entityManager;

    @Inject
    public TransactionRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public Optional<Transaction> findById(UUID id) {
        log.trace(">> findById({})", id);
        Transaction transaction = entityManager.find(Transaction.class, id);
        log.trace("<< findById()");
        return Optional.ofNullable(transaction);
    }

    @Override
    public void persist(Transaction transaction) {
        log.trace(">> persist({})", transaction);
        entityManager.persist(transaction);
        log.trace("<< persist()");
    }

    @Override
    public List<Transaction> listAll() {
        log.trace(">> listAll()");
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Transaction> cq = cb.createQuery(Transaction.class);
        Root<Transaction> rootEntry = cq.from(Transaction.class);
        CriteriaQuery<Transaction> all = cq.select(rootEntry);
        TypedQuery<Transaction> allQuery = entityManager.createQuery(all);
        log.trace("<< listAll()");
        return allQuery.getResultList();
    }
}
