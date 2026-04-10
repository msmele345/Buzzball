import type { ReactElement } from "react";


const TrendingPLayersHeader = (): ReactElement => {
    return (
        <div className="flex items-baseline gap-3 mb-4">
            <h2 className="text-xl font-bold text-white border-l-2 border-neon-green pl-3">Trending Players</h2>
            <span className="text-xs text-text-muted">7-day WAR movers</span>
        </div>
    )
}

export default TrendingPLayersHeader;