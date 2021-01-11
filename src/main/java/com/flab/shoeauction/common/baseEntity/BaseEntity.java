package com.flab.shoeauction.common.baseEntity;

import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import java.time.LocalDateTime;

/**
 *  JPA Auditng 을 사용하여 모든 Entity에 공통적으로 적용되는 생성일/수정일을 공통으로 관리
 * @Column(undatable = false) : 생성일은 초기 생성 후 변경되면 안되기 때문에 updatable을 false로 설정
 * @MappedSuperClass : JPA에서 속성만 받아쓰는 상속관계에 명시
 */

@EntityListeners(AuditingEntityListener.class)
@MappedSuperclass
@Getter
public class BaseEntity {
    @Column(updatable = false)
    @CreatedDate
    private LocalDateTime createDate;

    @LastModifiedDate
    private LocalDateTime modifiedDate;
}
