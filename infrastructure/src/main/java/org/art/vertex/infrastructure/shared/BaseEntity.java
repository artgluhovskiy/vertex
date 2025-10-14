/*
 * Copyrights 2024 Playtika Ltd., all rights reserved to Playtika Ltd.
 * privacy+e17bb14d-edc1-4d26-930d-486fcc1ab8fe@playtika.com
 */

package org.art.vertex.infrastructure.shared;

import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.UUID;

@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@MappedSuperclass
public abstract class BaseEntity {

    @Id
    @Column(name = "id", updatable = false, nullable = false)
    protected UUID id;

    /*
        Implementing equals/hashcode for a persistent entity manually, since the generated Lombok equals/hashcode violate the contract:
        https://thorben-janssen.com/lombok-hibernate-how-to-avoid-common-pitfalls/
        https://thorben-janssen.com/ultimate-guide-to-implementing-equals-and-hashcode-with-hibernate/
    */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        BaseEntity other = (BaseEntity) obj;
        if (getId() == null) {
            return false;
        } else {
            return getId().equals(other.getId());
        }
    }

    @Override
    public int hashCode() {
        return 13;
    }
}
