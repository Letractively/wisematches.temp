package billiongoods.server.services.state.impl;

import javax.persistence.*;
import java.util.Date;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
@Entity
@Table(name = "account_personality")
public class HibernatePlayerActivity {
	@Id
	@Column(name = "id")
	private Long player;

	@Column(name = "last_activity")
	@Temporal(TemporalType.TIMESTAMP)
	private Date lastActivityDate;

	public HibernatePlayerActivity() {
	}

	public HibernatePlayerActivity(Long player, Date lastActivityDate) {
		this.player = player;
		this.lastActivityDate = lastActivityDate;
	}

	public Long getPlayer() {
		return player;
	}

	public Date getLastActivityDate() {
		return lastActivityDate;
	}

	public void setLastActivityDate(Date lastActivityDate) {
		this.lastActivityDate = lastActivityDate;
	}
}
