import { create } from 'zustand';
import { persist } from 'zustand/middleware';

interface PlayerState {
  recentlyViewed: string[];
  addRecentlyViewed: (playerId: string) => void;
  clearHistory: () => void;
}

export const usePlayerStore = create<PlayerState>()(
  persist(
    (set) => ({
      recentlyViewed: [],
      addRecentlyViewed: (playerId) =>
        set((state) => ({
          recentlyViewed: [
            playerId,
            ...state.recentlyViewed.filter((id) => id !== playerId),
          ].slice(0, 10),
        })),
      clearHistory: () => set({ recentlyViewed: [] }),
    }),
    { name: 'buzzball-player-history' }
  )
);
