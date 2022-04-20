package de.hska.vslab.products;

import org.hibernate.Session;
import org.hibernate.Transaction;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;

public class ProductRepositoryExtensionImpl implements ProductRepositoryExtension {

    @PersistenceContext
    EntityManager em;

    @Override
    public List<Product> searchProduct(String searchDescription, Double searchMinPrice, Double searchMaxPrice) {
        Session session = em.unwrap(Session.class);
        Transaction transaction = null;
        List<Product> productList;

        try {
            transaction = session.beginTransaction();
            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<Product> criteriaQuery = builder.createQuery(Product.class);
            Root<Product> root = criteriaQuery.from(Product.class);

            List<Predicate> predicates = new ArrayList<>();
            if (searchDescription != null && searchDescription.length() > 0) {    // searchValue is set:
                searchDescription = "%" + searchDescription + "%";
                predicates.add(builder.like(root.get("details"), searchDescription));
            }

            if ((searchMinPrice != null) && (searchMaxPrice != null)) {
                predicates.add(builder.between(root.get("price"), searchMinPrice, searchMaxPrice));
            } else if (searchMinPrice != null) {
                predicates.add(builder.ge(root.get("price"), searchMinPrice));
            } else if (searchMaxPrice != null) {
                predicates.add(builder.le(root.get("price"), searchMaxPrice));
            }
            criteriaQuery.where(builder.and(predicates.toArray(Predicate[]::new)));
            productList = session.createQuery(criteriaQuery).getResultList();
            transaction.commit();
            return productList;
        } catch (Exception e) {
            transaction.rollback();
            throw e;
        }
    }

    @Override
    public void deleteByCategoryId(Integer categoryId) {
        Session session = em.unwrap(Session.class);
        Transaction transaction = null;
        List<Product> productList;

        try {
            transaction = session.beginTransaction();
            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<Product> criteriaQuery = builder.createQuery(Product.class);
            Root<Product> root = criteriaQuery.from(Product.class);
            criteriaQuery.where(builder.equal(root.get("categoryId"), categoryId));
            productList = session.createQuery(criteriaQuery).getResultList();
            for (Product product : productList) {
                session.delete(product);
            }
            transaction.commit();

        } catch (Exception e) {
            transaction.rollback();
            e.printStackTrace();
        }
    }
}
