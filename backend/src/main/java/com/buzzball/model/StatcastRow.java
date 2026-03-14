package com.buzzball.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents a row from Baseball Savant Statcast CSV export.
 * Column names match the Baseball Savant leaderboard CSV schema.
 */
@Data
@NoArgsConstructor
public class StatcastRow {
    @JsonProperty("player_id")
    private String playerId;

    @JsonProperty("player_name")
    private String playerName;

    @JsonProperty("team_id")
    private String teamId;

    @JsonProperty("xba")
    private String xba;

    @JsonProperty("xslg")
    private String xslg;

    @JsonProperty("xwoba")
    private String xwoba;

    @JsonProperty("exit_velocity_avg")
    private String exitVelocityAvg;

    @JsonProperty("barrel_batted_rate")
    private String barrelPct;

    @JsonProperty("hard_hit_percent")
    private String hardHitPct;

    @JsonProperty("launch_angle_avg")
    private String launchAngleAvg;

    @JsonProperty("sprint_speed")
    private String sprintSpeed;

    @JsonProperty("spin_rate_avg")
    private String spinRateAvg;

    @JsonProperty("xera")
    private String xera;

    @JsonProperty("whiff_percent")
    private String whiffPct;

    @JsonProperty("chase_percent")
    private String chasePct;
}
