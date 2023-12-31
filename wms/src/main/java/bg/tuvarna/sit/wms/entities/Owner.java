package bg.tuvarna.sit.wms.entities;

import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import lombok.Getter;
import lombok.Setter;

/**
 * Represents an owner user within the system.
 * <p>
 * Owners may possess or manage properties and have the ability to receive
 * notifications. This entity inherits the common user attributes from
 * the {@link User} class. Each owner can have multiple notifications
 * associated with them.
 * </p>
 *
 * @author Yavor Chamov
 * @since 1.0.0
 * @see User
 */
@Entity
@Table(name = "owners")
@Getter
@Setter
public class Owner extends User {

  @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL, orphanRemoval = true)
  private Set<Warehouse> warehouses;

  @OneToMany(mappedBy = "sender", cascade = CascadeType.ALL)
  private Set<Review> sentReviews;
}
