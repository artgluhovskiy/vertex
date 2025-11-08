import { create } from 'zustand';
import { persist } from 'zustand/middleware';

interface UiState {
  theme: 'light' | 'dark';
  sidebarCollapsed: boolean;
  rightPanelCollapsed: boolean;
  fullscreen: boolean;
  toggleTheme: () => void;
  toggleSidebar: () => void;
  toggleRightPanel: () => void;
  toggleFullscreen: () => void;
}

export const useUiStore = create<UiState>()(
  persist(
    (set) => ({
      theme: 'dark',
      sidebarCollapsed: false,
      rightPanelCollapsed: false,
      fullscreen: false,
      toggleTheme: () => set((state) => ({ theme: state.theme === 'dark' ? 'light' : 'dark' })),
      toggleSidebar: () => set((state) => ({ sidebarCollapsed: !state.sidebarCollapsed })),
      toggleRightPanel: () =>
        set((state) => ({ rightPanelCollapsed: !state.rightPanelCollapsed })),
      toggleFullscreen: () => set((state) => ({ fullscreen: !state.fullscreen })),
    }),
    {
      name: 'ui-storage',
    }
  )
);
