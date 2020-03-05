package com.permission.utils.baseinterface;

import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * **********************
 * * Author: xiashilong *
 * * Date: 2018-03-22   *
 * * Time: 14:55        *
 * * to: lz&xm          *
 * **********************
 **/

public class BaseJpaRepositoryImpl<T, ID extends Serializable> extends SimpleJpaRepository<T, ID> implements BaseJpaRepository<T, ID> {

    private final EntityManager em;

    private JpaEntityInformation<T, ?> entityInformation;

    //父类没有不带参数的构造方法，这里手动构造父类
    BaseJpaRepositoryImpl(JpaEntityInformation<T, ?> entityInformation,
                          EntityManager entityManager) {
        super(entityInformation, entityManager);
        // Keep the EntityManager around to used from the newly introduced methods.
        this.em = entityManager;
        this.entityInformation = entityInformation;
    }


    @Override
    @Transactional
    public T update(T entity) {
        return em.merge(this.combineCore(entity, this.em.find(super.getDomainClass(), this.entityInformation.getId(entity))));
    }

    @Override
    @Transactional
    public List<T> updateAll(Iterable<T> var1) {
        List<T> s = new ArrayList<>();
        if (var1 == null) {
            return s;
        } else {
            for (T s1 : var1) {
                s.add(update(s1));
            }
            return s;
        }
    }

    /**
     * 该方法是用于相同对象不同属性值的合并，如果两个相同对象中同一属性都有值，那么sourceBean中的值会覆盖tagetBean重点的值
     *
     * @param sourceBean 被提取的对象bean
     * @param targetBean 用于合并的对象bean
     * @return targetBean, 合并后的对象
     */
    private T combineCore(T sourceBean, T targetBean) {
        Class sourceBeanClass = sourceBean.getClass();
        Class targetBeanClass = targetBean.getClass();

        List<Field> sourceList = new ArrayList<>();
        while (sourceBeanClass != null) {//当父类为null的时候说明到达了最上层的父类(Object类).
            sourceList.addAll(Arrays.asList(sourceBeanClass.getDeclaredFields()));
            sourceBeanClass = sourceBeanClass.getSuperclass(); //得到父类,然后赋给自己
        }
        Field[] sourceFields = sourceList.toArray(new Field[0]);

        List<Field> targetList = new ArrayList<>();
        while (targetBeanClass != null) {
            targetList.addAll(Arrays.asList(targetBeanClass.getDeclaredFields()));
            targetBeanClass = targetBeanClass.getSuperclass();
        }
        Field[] targetFields = targetList.toArray(new Field[0]);

        for (int i = 0; i < sourceFields.length; i++) {
            Field sourceField = sourceFields[i];
            Field targetField = targetFields[i];
            sourceField.setAccessible(true);
            targetField.setAccessible(true);
            try {
                if ((sourceField.get(sourceBean) != null) && !"serialVersionUID".equals(sourceField.getName())) {
                    targetField.set(targetBean, sourceField.get(sourceBean));
                }
            } catch (IllegalArgumentException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return targetBean;
    }
}
