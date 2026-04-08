import { Link } from 'react-router-dom';

export interface Crumb {
  label: string;
  to?: string;
}

interface BreadcrumbsProps {
  crumbs: Crumb[];
}

export function Breadcrumbs({ crumbs }: BreadcrumbsProps) {
  return (
    <nav aria-label="Breadcrumb" className="flex items-center gap-1.5 text-xs text-text-muted mb-4">
      <Link to="/" className="hover:text-white transition-colors">Dashboard</Link>
      {crumbs.map((crumb, i) => (
        <span key={i} className="flex items-center gap-1.5">
          <span aria-hidden>/</span>
          {crumb.to ? (
            <Link to={crumb.to} className="hover:text-white transition-colors">{crumb.label}</Link>
          ) : (
            <span className="text-text-secondary">{crumb.label}</span>
          )}
        </span>
      ))}
    </nav>
  );
}
