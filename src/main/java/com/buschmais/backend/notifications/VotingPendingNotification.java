package com.buschmais.backend.notifications;

import com.buschmais.backend.voting.ADRReview;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;

import java.time.LocalDateTime;

@Data
public class VotingPendingNotification implements Notification {
	private final ADRReview adrReview;
	private final LocalDateTime creationTime;

	public VotingPendingNotification(
			@NonNull final ADRReview adrReview,
			@NonNull final LocalDateTime creationTime
	)
	{
		this.adrReview = adrReview;
		this.creationTime = creationTime;
	}

	@EqualsAndHashCode.Include
	@Override
	public NotificationType getType() {
		return NotificationType.VOTING_PENDING;
	}
}
