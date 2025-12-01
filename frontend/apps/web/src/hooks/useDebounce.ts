import { useState, useEffect } from 'react';

/**
 * Custom hook that debounces a value by delaying its update.
 * Useful for optimizing search inputs and other frequently changing values.
 *
 * Algorithm:
 * 1. Store the value in local state
 * 2. Set up effect that updates debounced value after delay
 * 3. Clear timeout if value changes before delay completes
 * 4. Return the debounced value
 */
export function useDebounce<T>(value: T, delay: number = 300): T {
  const [debouncedValue, setDebouncedValue] = useState<T>(value);

  useEffect(() => {
    // Set up timeout to update debounced value after delay
    const timeoutId = setTimeout(() => {
      setDebouncedValue(value);
    }, delay);

    // Cleanup function: clear timeout if value changes before delay completes
    return () => {
      clearTimeout(timeoutId);
    };
  }, [value, delay]);

  return debouncedValue;
}
