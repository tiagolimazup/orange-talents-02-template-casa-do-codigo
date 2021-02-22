package br.com.zup.bootcamp.casadocodigo.validation;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaQuery;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class ExistsValidator implements ConstraintValidator<Exists, Object> {

    final EntityManager entityManager;

    private Class<?> entityType;
    private String field;

    public ExistsValidator(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public void initialize(Exists annotation) {
        this.entityType = annotation.entity();
        this.field = annotation.field();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        CriteriaQuery<Long> query = new CountQuery(entityManager)
                .select(entityType)
                .where(field, value);

        long count = entityManager.createQuery(query).getSingleResult();

        return count > 0;
    }
}
