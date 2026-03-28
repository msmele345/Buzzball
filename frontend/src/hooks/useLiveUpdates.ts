import { useEffect, useRef } from 'react';
import { useQueryClient } from '@tanstack/react-query';

const SSE_URL = '/api/v1/live/updates';
const MAX_JITTER_MS = 2000;
const MAX_RETRIES = 5;
const BASE_DELAY_MS = 1000;

/**
 * Connects to the SSE live updates endpoint and invalidates React Query
 * caches on data-refresh events. Uses exponential backoff on connection
 * failures (up to MAX_RETRIES) to avoid flooding the server.
 */
export function useLiveUpdates() {
  const queryClient = useQueryClient();
  const retriesRef = useRef(0);

  useEffect(() => {
    let es: EventSource | null = null;
    let retryTimeout: ReturnType<typeof setTimeout> | null = null;

    function connect() {
      es = new EventSource(SSE_URL);

      es.addEventListener('data-refresh', () => {
        const jitter = Math.random() * MAX_JITTER_MS;
        setTimeout(() => {
          queryClient.invalidateQueries({ queryKey: ['dashboard'] });
          queryClient.invalidateQueries({ queryKey: ['trending'] });
          queryClient.invalidateQueries({ queryKey: ['league-leaders'] });
          queryClient.invalidateQueries({ queryKey: ['teams'] });
        }, jitter);
      });

      es.onopen = () => {
        retriesRef.current = 0;
      };

      es.onerror = () => {
        es?.close();
        es = null;

        if (retriesRef.current < MAX_RETRIES) {
          const delay = BASE_DELAY_MS * Math.pow(2, retriesRef.current);
          retriesRef.current += 1;
          retryTimeout = setTimeout(connect, delay);
        }
      };
    }

    connect();

    return () => {
      es?.close();
      if (retryTimeout) clearTimeout(retryTimeout);
    };
  }, [queryClient]);
}
