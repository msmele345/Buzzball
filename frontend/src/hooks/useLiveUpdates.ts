import { useEffect } from 'react';
import { useQueryClient } from '@tanstack/react-query';

const SSE_URL = '/api/v1/live/updates';
const MAX_JITTER_MS = 2000;

/**
 * Connects to the SSE live updates endpoint and invalidates React Query
 * caches on data-refresh events. Jitter (0–2s) prevents stampede.
 */
export function useLiveUpdates() {
  const queryClient = useQueryClient();

  useEffect(() => {
    const es = new EventSource(SSE_URL);

    es.addEventListener('data-refresh', () => {
      const jitter = Math.random() * MAX_JITTER_MS;
      setTimeout(() => {
        queryClient.invalidateQueries({ queryKey: ['dashboard'] });
        queryClient.invalidateQueries({ queryKey: ['trending'] });
        queryClient.invalidateQueries({ queryKey: ['league-leaders'] });
        queryClient.invalidateQueries({ queryKey: ['teams'] });
      }, jitter);
    });

    es.onerror = () => {
      // EventSource will auto-reconnect on error; no manual action needed
      console.warn('SSE connection error — will retry automatically');
    };

    return () => {
      es.close();
    };
  }, [queryClient]);
}
