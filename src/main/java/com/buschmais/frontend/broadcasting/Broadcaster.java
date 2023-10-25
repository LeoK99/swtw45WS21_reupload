package com.buschmais.frontend.broadcasting;
import lombok.NonNull;

import java.io.Serializable;
import java.util.WeakHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Broadcaster implements Serializable {
	static ExecutorService executorService = Executors.newSingleThreadExecutor();
	private static final WeakHashMap<BroadcastListener, Object> listeners = new WeakHashMap<>();

	public static synchronized void registerListener(@NonNull final BroadcastListener listener){
		listeners.put(listener, null);
	}
	public static synchronized void unregisterListener(@NonNull final BroadcastListener listener){
		listeners.remove(listener);
	}

	public static synchronized void broadcastMessage(final BroadcastListener.Event event,
													 final String message,
													 final Object sender) {
		for (final BroadcastListener listener: listeners.keySet())
		{
			if (listener != sender)
			{
				executorService.execute(() -> listener.receiveBroadcast(event, message));
			}
		}
	}
}
