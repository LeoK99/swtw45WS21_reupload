package com.buschmais.frontend.broadcasting;


public interface BroadcastListener {
	enum Event {
		ADR_CHANGED,					// Message is: adrId
		ADR_REVIEW_STS_CHANGED,			// Message is: adrId
		ADR_REVIEW_VOTE_CNT_CHANGED,	// Message is: adrId
		ADR_ACCESS_GROUPS_CHANGED,		// Message is: adrId
		USER_CHANGED,					// Message is: userId
		USER_DELETED,					// Message is: userId
		GROUP_CHANGED                   // Message is: groupId
	}

	void receiveBroadcast(Event event, String message);
}
