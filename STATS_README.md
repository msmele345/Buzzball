# BuzzBall Batting Stats Guide ⚾

A plain-English breakdown of every batting metric tracked in BuzzBall.
No sabermetrics degree required.

---

## Standard Stats

These are the numbers you'll see on any scoreboard or baseball card.

| Metric | Field | What It Means |
|--------|-------|---------------|
| **Games Played** | `gamesPlayed` | Total games the player appeared in during the season. |
| **At Bats** | `atBats` | The number of times a batter came to the plate and put the ball in play (or struck out). Walks, hit-by-pitches, and sacrifices don't count as at bats. |
| **Hits** | `hits` | Any time the batter reaches base safely because they hit the ball into fair territory — singles, doubles, triples, and home runs all count. |
| **Home Runs** | `homeRuns` | Hits that leave the playing field (or circle the bases via an inside-the-park HR). The glamour stat. |
| **RBI** | `rbi` | Runs Batted In — how many runners (including the batter) scored as a direct result of this batter's plate appearance. If you drive in the winning run, that's an RBI. |
| **Walks** | `walks` | Also called "base on balls." The pitcher threw four balls outside the strike zone before getting three strikes, so the batter gets a free trip to first base. Drawing lots of walks is a sign of patience and a good eye. |
| **Strikeouts** | `strikeouts` | The batter got three strikes before putting the ball in play. Every hitter strikes out — even the best do 20–25 % of the time — but fewer is generally better. |
| **Stolen Bases** | `stolenBases` | How many times the runner advanced to the next base while the pitcher was delivering the ball to home plate. Speed + smarts. |

### Calculated Rate Stats

| Metric | Field | What It Means | Good / Great |
|--------|-------|---------------|-------------|
| **Batting Average (AVG)** | `battingAverage` | Hits ÷ At Bats. The classic "how often does he get a hit?" number. | .270 / .300+ |
| **On-Base Percentage (OBP)** | `onBasePercentage` | How often the batter reaches base by *any* means — hits, walks, or hit-by-pitch. A batter who walks a lot will have a much higher OBP than AVG. | .340 / .380+ |
| **Slugging Percentage (SLG)** | `sluggingPercentage` | Total bases ÷ At Bats. Unlike AVG, this rewards extra-base hits. A home run counts as 4, a triple as 3, a double as 2, and a single as 1. A power hitter will have a high SLG even if their AVG is modest. | .430 / .500+ |
| **OPS** | `ops` | OBP + SLG mashed together into one number. It's a quick shorthand for "how good is this hitter overall?" Higher is better. | .770 / .900+ |

---

## Statcast Advanced Metrics

These come from MLB's Statcast system — high-speed cameras and radar that track every pitch, swing, and batted ball. They strip away luck and defense to show what a hitter's *quality of contact* actually looks like.

### Expected Stats ("x" Stats)

Traditional stats are affected by luck — a screaming line drive caught by a diving outfielder counts as an out, while a weak grounder that sneaks through the infield counts as a hit. **Expected stats** fix this by asking: *"Given how hard and at what angle this ball was hit, how often does a ball hit like this become a hit/extra-base hit?"* They use years of batted-ball data to calculate the answer.

| Metric | Field | What It Means | Good / Great |
|--------|-------|---------------|-------------|
| **Expected Batting Average (xBA)** | `xba` | What the batter's AVG *should* be based on how hard and at what angle they hit the ball, ignoring where fielders happened to be standing. If xBA is much higher than actual AVG, the hitter has been unlucky. | .270 / .300+ |
| **Expected Slugging (xSLG)** | `xslg` | Same idea, but for slugging. Based on exit velocity and launch angle, how much extra-base damage *should* this batter be doing? | .430 / .500+ |
| **Expected wOBA (xwOBA)** | `xwoba` | The expected version of wOBA (see FanGraphs section below). This is considered one of the single best measures of a hitter's true offensive ability because it combines contact quality with plate discipline, minus the noise of luck. | .330 / .370+ |

### Batted-Ball Quality

| Metric | Field | What It Means | Good / Great |
|--------|-------|---------------|-------------|
| **Avg Exit Velocity** | `exitVelocityAvg` | The average speed (mph) of the ball coming off the bat. Harder contact = more damage. Think of it as "how hard does this guy hit the ball?" | 89+ mph / 92+ mph |
| **Barrel %** | `barrelPct` | A "barrel" is the best possible combination of exit velocity (≥ 98 mph) and launch angle (roughly 26–30°) — the sweet spot that produces home runs and extra-base hits at the highest rate. This is the percentage of batted balls that were barrels. | 8 % / 12 %+ |
| **Hard-Hit %** | `hardHitPct` | Percentage of batted balls with an exit velocity of 95 mph or harder. A broader measure of contact quality than Barrel %. | 40 % / 45 %+ |
| **Avg Launch Angle** | `launchAngleAvg` | The average vertical angle (degrees) the ball leaves the bat. Negative = ground ball; 10–25° = line drive sweet spot; 25–35° = fly balls with HR potential; 40°+ = pop-ups. Context matters — power hitters aim higher, contact hitters aim lower. | 10–18° (varies by hitter type) |

### Speed

| Metric | Field | What It Means | Good / Great |
|--------|-------|---------------|-------------|
| **Sprint Speed** | `sprintSpeedFt` | The player's top running speed measured in feet per second, based on Statcast tracking. Faster runners beat out more infield singles, steal more bases, and take extra bases on hits to the outfield. | 27+ ft/s / 30+ ft/s |

---

## FanGraphs Advanced Metrics

These come from FanGraphs, a popular baseball analytics site. They go a step beyond traditional stats to put a hitter's production in full context — adjusting for ballpark, league, and the true run value of each event.

| Metric | Field | What It Means | Good / Great |
|--------|-------|---------------|-------------|
| **wOBA (Weighted On-Base Average)** | `woba` | Like OBP, but not all ways of reaching base are treated equally. A home run is worth more than a single, which is worth more than a walk. wOBA assigns each outcome its actual run value so you get one number that captures a hitter's total offensive contribution. It's on the same scale as OBP, so the numbers look familiar. | .320 / .370+ |
| **wRC+ (Weighted Runs Created Plus)** | `wrcPlus` | Takes wOBA and adjusts it for ballpark and league so you can compare a hitter at Coors Field to one at Oracle Park on equal footing. **100 is exactly league average.** Every point above 100 is one percent better than average; every point below is one percent worse. A wRC+ of 130 means the hitter was 30 % better than the average MLB hitter. | 110 / 130+ |
| **fWAR (FanGraphs Wins Above Replacement)** | `fWar` | The big-picture number: how many extra wins did this player contribute compared to a freely available "replacement-level" player (think a AAAA call-up)? It rolls batting, baserunning, and positional value into one number. A 2-WAR player is a solid starter; a 5+ WAR player is an All-Star; 8+ is MVP territory. | 2.0 / 5.0+ |
| **BABIP (Batting Average on Balls In Play)** | `babip` | Batting average but *only* on balls the batter put in play (excluding home runs and strikeouts). The league average is roughly .300. If a hitter's BABIP is way above .300, they may be getting lucky with where balls are landing; way below, they may be getting unlucky. It's a key tool for figuring out if a hot streak (or slump) is real or just noise. | ~.300 (league avg) |

---

## Quick Reference Cheat Sheet

Want to answer a question fast? Here's which stat to look at:

| Question | Go-To Stat |
|----------|-----------|
| How often does he get a hit? | Batting Average / xBA |
| How hard does he hit the ball? | Exit Velocity / Hard-Hit % |
| Does he hit for power? | SLG / Barrel % / Home Runs |
| Is he patient at the plate? | Walks / OBP |
| Is he getting lucky or unlucky? | Compare xBA vs. AVG, xwOBA vs. wOBA, check BABIP |
| How good is he overall? | wRC+ (park-adjusted, single number) |
| How valuable is he to his team? | fWAR |
| Is he fast? | Sprint Speed / Stolen Bases |

