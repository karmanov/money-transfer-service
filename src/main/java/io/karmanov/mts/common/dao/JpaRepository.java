package io.karmanov.mts.common.dao;

import org.jboss.resteasy.spi.NotImplementedYetException;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface JpaRepository<T> {

    Optional<T> findById(UUID id);

    default Optional<T> findByIdForUpdate(UUID id) {
        throw new NotImplementedYetException();
    }

    void persist(T t);

    List<T> listAll();
}
