package br.com.zup.bootcamp.casadocodigo.validation;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class UniqueValueValidator implements ConstraintValidator<UniqueValue, String> {

    final EntityManager entityManager;

    private Class<?> entityType;
    private String field;

    public UniqueValueValidator(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public void initialize(UniqueValue annotation) {
        this.entityType = annotation.entity();
        this.field = annotation.field();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();

        CriteriaQuery<Long> q = cb.createQuery(Long.class);
        Root<?> r = q.from(entityType);

        q.select(cb.count(r));
        q.where(cb.equal(r.get(field), value));

        long count = entityManager.createQuery(q).getSingleResult();

        return count == 0;
    }
}
