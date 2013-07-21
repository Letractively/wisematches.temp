package billiongoods.server.services.state.impl;

import javax.persistence.*;
import java.util.Date;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
@Entity
@Table(name = "player_activity")
public class HibernatePlayerActivity {
    @Id
    @Column(name = "pid")
    private long player;

    @Column(name = "last_activity")
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastActivityDate;

    public HibernatePlayerActivity() {
    }

    public HibernatePlayerActivity(long player, Date lastActivityDate) {
        this.player = player;
        this.lastActivityDate = lastActivityDate;
    }

    public long getPlayer() {
        return player;
    }

    public Date getLastActivityDate() {
        return lastActivityDate;
    }

    public void setLastActivityDate(Date lastActivityDate) {
        this.lastActivityDate = lastActivityDate;
    }
}
