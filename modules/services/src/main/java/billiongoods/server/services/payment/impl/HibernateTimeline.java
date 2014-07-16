package billiongoods.server.services.payment.impl;

import billiongoods.server.services.payment.Timeline;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.time.LocalDateTime;
import java.time.temporal.Temporal;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
@Embeddable
public class HibernateTimeline implements Timeline {
	@Column(name = "created")
	private LocalDateTime created;

	@Column(name = "started")
	private LocalDateTime started;

	@Column(name = "processed")
	private LocalDateTime processed;

	@Column(name = "shipped")
	private LocalDateTime shipped;

	@Column(name = "finished")
	private LocalDateTime finished;

	@Deprecated
	HibernateTimeline() {
	}

	HibernateTimeline(LocalDateTime created) {
		this.created = created;
	}

	@Override
	public Temporal getCreated() {
		return created;
	}

	@Override
	public Temporal getStarted() {
		return started;
	}

	@Override
	public Temporal getProcessed() {
		return processed;
	}

	@Override
	public Temporal getShipped() {
		return shipped;
	}

	@Override
	public Temporal getFinished() {
		return finished;
	}


	void setStarted(LocalDateTime started) {
		if (this.started != null) {
			throw new IllegalStateException("Timeline already has started point");
		}
		this.started = started;
	}

	void setProcessed(LocalDateTime processed) {
		if (this.processed != null) {
			throw new IllegalStateException("Timeline already has processed point");
		}
		this.processed = processed;
	}

	void setShipped(LocalDateTime shipped) {
		if (this.shipped != null) {
			throw new IllegalStateException("Timeline already has shipped point");
		}
		this.shipped = shipped;
	}

	void setFinished(LocalDateTime finished) {
		if (this.finished != null) {
			throw new IllegalStateException("Timeline already has finished point");
		}
		this.finished = finished;
	}
}
