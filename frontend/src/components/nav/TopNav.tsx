import { Link, useLocation } from 'react-router-dom';
import clsx from 'clsx';

const NAV_LINKS = [
  { to: '/', label: 'Dashboard' },
  { to: '/teams', label: 'Teams' },
];

export function TopNav() {
  const { pathname } = useLocation();

  return (
    <nav className="sticky top-0 z-50 bg-bg-primary/90 backdrop-blur-sm border-b border-border">
      <div className="max-w-7xl mx-auto px-4 flex items-center h-12 gap-8">
        <Link to="/" className="text-lg font-black tracking-tight text-neon-green hover:opacity-80 transition-opacity">
          BUZZBALL
        </Link>
        <div className="flex gap-1">
          {NAV_LINKS.map(({ to, label }) => {
            const isActive = to === '/' ? pathname === '/' : pathname.startsWith(to);
            return (
              <Link
                key={to}
                to={to}
                className={clsx(
                  'px-3 py-1.5 rounded-md text-sm font-medium transition-colors duration-150',
                  isActive
                    ? 'text-white bg-bg-surface'
                    : 'text-text-secondary hover:text-white hover:bg-bg-surface-alt',
                )}
              >
                {label}
              </Link>
            );
          })}
        </div>
      </div>
    </nav>
  );
}
