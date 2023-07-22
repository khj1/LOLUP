package com.lolup.lolup_project.riotapi.match;

public enum QueueType {
	RANKED_SOLO(420),
	RANKED_TEAM(440);

	public final int queueId;

	QueueType(final int queueId) {
		this.queueId = queueId;
	}

	public int getQueueId() {
		return queueId;
	}
}
