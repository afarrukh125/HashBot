package me.afarrukh.hashbot.extras.fortnite.stats;

public abstract class Statistic {
    
    protected String statName;
    protected int wins;
    protected double kd;
    protected double winPercentage;
    protected int kills;
    protected int matches;

    protected Statistic(int wins, long kd, long winPercentage, int kills, int matches) {
        this.wins = wins;
        this.kd = kd;
        this.winPercentage = winPercentage;
        this.kills = kills;
        this.matches = matches;
        statName = null;
    }

    public void setStatName(String statName) {
        this.statName = statName;
    }

    public String getStatName() {
        return statName;
    }

    public int getWins() {
        return wins;
    }

    public void setWins(int wins) {
        this.wins = wins;
    }

    public double getKd() {
        return kd;
    }

    public void setKd(double kd) {
        this.kd = kd;
    }

    public double getWinPercentage() {
        return winPercentage;
    }

    public void setWinPercentage(double winPercentage) {
        this.winPercentage = winPercentage;
    }

    public int getKills() {
        return kills;
    }

    public void setKills(int kills) {
        this.kills = kills;
    }

    public int getMatches() {
        return matches;
    }

    public void setMatches(int matches) {
        this.matches = matches;
    }
}
