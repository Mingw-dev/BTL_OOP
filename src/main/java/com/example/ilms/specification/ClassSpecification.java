package com.example.ilms.specification;

import com.example.ilms.entity.ClassEntity;
import com.example.ilms.entity.ClassMember;
import com.example.ilms.entity.User;
import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;

import java.util.ArrayList;
import java.util.Locale;
import java.util.UUID;
import java.util.List;

public class ClassSpecification {
    public static Specification<ClassEntity> filterClass(
            String search, String status, UUID semesterId,
            Boolean isArchived, User currentUser, boolean isAdmin
    ){
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            //Xử lý các tham số lọc động(Chỉ lọc khi client có truyền lên)
            if(search != null && !search.trim().isEmpty()){
                String searchKeyword = "%" + search.toLowerCase() + "%";
                Predicate searchByName = cb.like(cb.lower(root.get("name")), searchKeyword);
                Predicate searchByCode = cb.like(cb.lower(root.get("classCode")), searchKeyword);

                predicates.add(cb.or(searchByName, searchByCode));
            }
            if (status != null && !status.trim().isEmpty()) {
                predicates.add(cb.equal(root.get("visibility"), status));
            }
            //tại chưa có học kì nên để tạm
//            if (semesterId != null) {
//                predicates.add(cb.equal(root.get("semesterId"), semesterId));
//            }
            if (isArchived != null) {
                predicates.add(cb.equal(root.get("isArchived"), isArchived));
            }
            /* Phân quyền
            Nếu là Admin -> lấy tất cả
            Nếu là User -> ép điều kiện OR*/
            if (!isAdmin) {

    Predicate isPublic = cb.equal(root.get("visibility"), "PUBLIC");

    Join<ClassEntity, ClassMember> membersJoin = root.join("members", JoinType.LEFT);
    Predicate isMember = cb.equal(membersJoin.get("user"), currentUser);

    Predicate isOwner = cb.equal(root.get("owner"), currentUser);


    predicates.add(cb.or(isPublic, isMember, isOwner));

    query.distinct(true);
}
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
