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
    <div className="min-h-screen flex items-center justify-center bg-gray-50 dark:bg-gray-950">
      <div className="w-full max-w-md p-8 bg-white dark:bg-gray-900 rounded-lg shadow-lg">
        <h1 className="text-3xl font-bold mb-6 text-center dark:text-gray-100">
          Synapse
        </h1>
        <h2 className="text-xl mb-6 text-center text-gray-600 dark:text-gray-400">
          Sign In
        </h2>

        <form onSubmit={handleSubmit} className="space-y-4">
          <div>
            <label htmlFor="email" className="block text-sm font-medium mb-2 dark:text-gray-300">
              Email
            </label>
            <input
              id="email"
              type="email"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              required
              className="w-full px-4 py-2 border border-gray-300 dark:border-gray-700 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 dark:bg-gray-800 dark:text-gray-100"
              placeholder="you@example.com"
            />
          </div>

          <div>
            <label htmlFor="password" className="block text-sm font-medium mb-2 dark:text-gray-300">
              Password
            </label>
            <input
              id="password"
              type="password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              required
              className="w-full px-4 py-2 border border-gray-300 dark:border-gray-700 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 dark:bg-gray-800 dark:text-gray-100"
              placeholder="••••••••"
            />
          </div>

          {loginError && (
            <div className="text-red-500 text-sm">
              {loginError.message || 'Login failed. Please try again.'}
            </div>
          )}

          <button
            type="submit"
            disabled={isLoading}
            className="w-full bg-blue-500 hover:bg-blue-600 text-white font-medium py-2 px-4 rounded-lg transition-colors disabled:opacity-50 disabled:cursor-not-allowed"
          >
            {isLoading ? 'Signing in...' : 'Sign In'}
          </button>
        </form>

        <div className="mt-6">
          <div className="relative">
            <div className="absolute inset-0 flex items-center">
              <div className="w-full border-t border-gray-300 dark:border-gray-700"></div>
            </div>
            <div className="relative flex justify-center text-sm">
              <span className="px-2 bg-white dark:bg-gray-900 text-gray-500 dark:text-gray-400">
                Or
              </span>
            </div>
          </div>

          <button
            onClick={handleDemoMode}
            className="mt-4 w-full bg-purple-500 hover:bg-purple-600 text-white font-medium py-2 px-4 rounded-lg transition-colors"
          >
            🚀 Try Demo Mode (No Backend Required)
          </button>
        </div>

        <p className="mt-6 text-center text-sm text-gray-600 dark:text-gray-400">
          Demo mode lets you explore the UI with mock data
        </p>
      </div>
    </div>
  );
};
