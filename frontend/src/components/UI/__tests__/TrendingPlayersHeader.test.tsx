import { render, screen } from '@testing-library/react';
import { describe, expect, it } from 'vitest';
import TrendingPlayersHeader from '../TrendingPlayersHeader';

describe("TrendingPlayerHeader", () => {
    it("should render the header with expected title", () => {
        render(<TrendingPlayersHeader />);

        expect(screen.getByText("Trending Players")).toBeInTheDocument();
        expect(screen.getByText("7-day WAR movers")).toBeInTheDocument();
    })
});