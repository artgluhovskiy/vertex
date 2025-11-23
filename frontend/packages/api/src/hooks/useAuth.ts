import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import { authService } from '../services';
import { queryKeys } from '../query-keys';
import type { AuthCredentials } from '@synapse/types/domain';

export const useAuth = () => {
  const queryClient = useQueryClient();

  const { data: user, isLoading } = useQuery({
    queryKey: queryKeys.auth.me,
    queryFn: authService.getCurrentUser,
    retry: false,
    // Don't show error in console for 401 (expected when not logged in)
    staleTime: 5 * 60 * 1000, // 5 minutes
    // Ensure loading state completes even on error
    refetchOnWindowFocus: false,
    refetchOnMount: false,
    refetchOnReconnect: false,
  });

  const loginMutation = useMutation({
    mutationFn: authService.login,
    onSuccess: (data) => {
      localStorage.setItem('auth_token', data.accessToken);
      queryClient.setQueryData(queryKeys.auth.me, data.user);
    },
  });

  const registerMutation = useMutation({
    mutationFn: authService.register,
    onSuccess: (data) => {
      localStorage.setItem('auth_token', data.accessToken);
      queryClient.setQueryData(queryKeys.auth.me, data.user);
    },
  });

  const logout = () => {
    authService.logout();
    queryClient.setQueryData(queryKeys.auth.me, null);
    queryClient.clear();
  };

  return {
    user,
    isLoading,
    isAuthenticated: !!user,
    login: (credentials: AuthCredentials) => loginMutation.mutateAsync(credentials),
    register: (credentials: AuthCredentials) => registerMutation.mutateAsync(credentials),
    logout,
    loginError: loginMutation.error,
    registerError: registerMutation.error,
  };
};
