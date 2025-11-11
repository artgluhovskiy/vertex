import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '@synapse/api/hooks';
import { useQueryClient } from '@tanstack/react-query';
import { queryKeys } from '@synapse/api/query-keys';

export const LoginPage: React.FC = () => {
  const navigate = useNavigate();
  const queryClient = useQueryClient();
  const { login, loginError } = useAuth();
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [isLoading, setIsLoading] = useState(false);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setIsLoading(true);

    try {
      await login({ email, password });
      navigate('/');
    } catch (error) {
      console.error('Login failed:', error);
    } finally {
      setIsLoading(false);
    }
  };

  const handleDemoMode = () => {
    // Set mock user data for demo mode
    const mockUser = {
      id: 'demo-user-123',
      email: 'demo@synapse.com',
      createdAt: new Date().toISOString(),
    };

    // Store demo token
    localStorage.setItem('auth_token', 'demo-token-12345');

    // Set mock user in React Query cache
    queryClient.setQueryData(queryKeys.auth.me, mockUser);

    // Navigate to dashboard
    navigate('/');
  };

  return (
    <div className="min-h-screen flex items-center justify-center bg-light-bg-secondary dark:bg-dark-bg-primary">
      <div className="w-full max-w-md p-8 card-elevated animate-fade-in">
        <h1 className="text-3xl font-bold mb-6 text-center text-light-text-primary dark:text-dark-text-primary">
          Synapse
        </h1>
        <h2 className="text-xl mb-6 text-center text-light-text-secondary dark:text-dark-text-secondary">
          Sign In
        </h2>

        <form onSubmit={handleSubmit} className="space-y-4">
          <div>
            <label htmlFor="email" className="block text-sm font-medium mb-2 text-light-text-secondary dark:text-dark-text-secondary">
              Email
            </label>
            <input
              id="email"
              type="email"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              required
              className="input-base"
              placeholder="you@example.com"
            />
          </div>

          <div>
            <label htmlFor="password" className="block text-sm font-medium mb-2 text-light-text-secondary dark:text-dark-text-secondary">
              Password
            </label>
            <input
              id="password"
              type="password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              required
              className="input-base"
              placeholder="••••••••"
            />
          </div>

          {loginError && (
            <div className="text-error text-sm">
              {loginError.message || 'Login failed. Please try again.'}
            </div>
          )}

          <button
            type="submit"
            disabled={isLoading}
            className="btn-primary w-full disabled:opacity-50 disabled:cursor-not-allowed"
          >
            {isLoading ? 'Signing in...' : 'Sign In'}
          </button>
        </form>

        <div className="mt-6">
          <div className="relative">
            <div className="absolute inset-0 flex items-center">
              <div className="w-full border-t border-light-border-primary dark:border-dark-border-primary"></div>
            </div>
            <div className="relative flex justify-center text-sm">
              <span className="px-2 bg-light-bg-tertiary dark:bg-dark-bg-tertiary text-light-text-muted dark:text-dark-text-muted">
                Or
              </span>
            </div>
          </div>

          <button
            onClick={handleDemoMode}
            className="btn-secondary w-full mt-4"
          >
            🚀 Try Demo Mode (No Backend Required)
          </button>
        </div>

        <p className="mt-6 text-center text-sm text-light-text-muted dark:text-dark-text-muted">
          Demo mode lets you explore the UI with mock data
        </p>
      </div>
    </div>
  );
};
