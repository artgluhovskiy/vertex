import { useState, useCallback, useEffect, useRef } from 'react';

interface UseResizableOptions {
  initialWidth: number;
  minWidth: number;
  maxWidth: number;
  onResize?: (width: number) => void;
}

interface UseResizableReturn {
  width: number;
  isResizing: boolean;
  handleMouseDown: (e: React.MouseEvent) => void;
  ref: React.RefObject<HTMLDivElement>;
}

/**
 * Custom hook for making an element resizable via drag handle.
 *
 * @param options - Configuration options for resizable behavior
 * @returns Width state, resizing state, and mouse down handler
 */
export function useResizable({
  initialWidth,
  minWidth,
  maxWidth,
  onResize,
}: UseResizableOptions): UseResizableReturn {
  const [width, setWidth] = useState(initialWidth);
  const [isResizing, setIsResizing] = useState(false);
  const ref = useRef<HTMLDivElement>(null);
  const startXRef = useRef<number>(0);
  const startWidthRef = useRef<number>(initialWidth);

  const handleMouseDown = useCallback((e: React.MouseEvent) => {
    e.preventDefault();
    setIsResizing(true);
    startXRef.current = e.clientX;
    startWidthRef.current = width;

    // Add cursor style to body during resize
    document.body.style.cursor = 'col-resize';
    document.body.style.userSelect = 'none';
  }, [width]);

  useEffect(() => {
    if (!isResizing) return;

    const handleMouseMove = (e: MouseEvent) => {
      const deltaX = e.clientX - startXRef.current;
      const newWidth = Math.max(
        minWidth,
        Math.min(maxWidth, startWidthRef.current + deltaX)
      );

      setWidth(newWidth);
      onResize?.(newWidth);
    };

    const handleMouseUp = () => {
      setIsResizing(false);

      // Remove cursor style from body
      document.body.style.cursor = '';
      document.body.style.userSelect = '';
    };

    document.addEventListener('mousemove', handleMouseMove);
    document.addEventListener('mouseup', handleMouseUp);

    return () => {
      document.removeEventListener('mousemove', handleMouseMove);
      document.removeEventListener('mouseup', handleMouseUp);
    };
  }, [isResizing, minWidth, maxWidth, onResize]);

  return {
    width,
    isResizing,
    handleMouseDown,
    ref,
  };
}
