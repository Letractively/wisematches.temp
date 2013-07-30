package billiongoods.core.personality.player.membership.impl;

import billiongoods.core.Membership;
import billiongoods.core.personality.player.membership.MembershipCard;

import javax.persistence.*;
import java.util.Date;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
@Entity
@Table(name = "account_membership")
public class HibernateMembershipCard implements MembershipCard, Cloneable {
    @Id
    @Column(name = "pid")
    private long player;

    @Column(name = "expiration")
    @Temporal(TemporalType.DATE)
    private Date expiration;

    @Column(name = "membership")
    @Enumerated(EnumType.ORDINAL)
    private Membership membership;

    HibernateMembershipCard() {
    }

    HibernateMembershipCard(long player, Membership membership, Date expiration) {
        this.player = player;
        this.expiration = expiration;
        this.membership = membership;
    }

    public long getPlayer() {
        return player;
    }

    @Override
    public boolean isExpired() {
        return expiration != null && expiration.getTime() <= System.currentTimeMillis();
    }

    @Override
    public Date getExpiration() {
        return expiration;
    }

    public Membership getMembership() {
        return membership;
    }

    @Override
    public Membership getValidMembership() {
        if (isExpired()) {
            return Membership.BASIC;
        }
        return membership;
    }

    public void setExpiration(Date expiration) {
        this.expiration = expiration;
    }

    public void setMembership(Membership membership) {
        this.membership = membership;
    }

    @Override
    public HibernateMembershipCard clone() {
        try {
            return (HibernateMembershipCard) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new IllegalStateException("Clone is not supported", e);
        }
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("HibernateActiveMembership");
        sb.append("{player=").append(player);
        sb.append(", expiration=").append(expiration);
        sb.append(", membership=").append(membership);
        sb.append('}');
        return sb.toString();
    }
}
