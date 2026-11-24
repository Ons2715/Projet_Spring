package tn.fst.autoecole1.controller;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Field;
import java.util.List;

public abstract class BaseCrudController<T> {

    @PersistenceContext
    private EntityManager entityManager;

    private final Class<T> entityClass;

    protected BaseCrudController(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    @GetMapping
    public List<T> getAll() {
        return entityManager.createQuery(
                "SELECT entity FROM " + entityClass.getSimpleName() + " entity",
                entityClass
        ).getResultList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<T> getById(@PathVariable Integer id) {
        T entity = entityManager.find(entityClass, id);
        return entity == null
                ? ResponseEntity.notFound().build()
                : ResponseEntity.ok(entity);
    }

    @PostMapping
    @Transactional
    public ResponseEntity<T> create(@RequestBody T entity) {
        entityManager.persist(entity);
        entityManager.flush();
        return ResponseEntity.status(HttpStatus.CREATED).body(entity);
    }

    @PutMapping("/{id}")
    @Transactional
    public ResponseEntity<T> update(@PathVariable Integer id, @RequestBody T entity) {
        T existing = entityManager.find(entityClass, id);
        if (existing == null) {
            return ResponseEntity.notFound().build();
        }

        setId(entity, id);
        T updated = entityManager.merge(entity);
        entityManager.flush();
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        T entity = entityManager.find(entityClass, id);
        if (entity == null) {
            return ResponseEntity.notFound().build();
        }

        entityManager.remove(entity);
        return ResponseEntity.noContent().build();
    }

    private void setId(T entity, Integer id) {
        for (Class<?> type = entityClass; type != null; type = type.getSuperclass()) {
            for (Field field : type.getDeclaredFields()) {
                if (field.isAnnotationPresent(jakarta.persistence.Id.class)) {
                    try {
                        field.setAccessible(true);
                        field.set(entity, id);
                        return;
                    } catch (IllegalAccessException ex) {
                        throw new IllegalStateException("Unable to set entity id", ex);
                    }
                }
            }
        }
        throw new IllegalStateException("Entity " + entityClass.getName() + " has no @Id field");
    }
}
