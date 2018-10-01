package me.afarrukh.hashbot.extras.fortnite.stats;

public class DuoStatistic extends Statistic {

    public DuoStatistic(int wins, long kd, long winPercentage, int kills, int matches) {
        super(wins, kd, winPercentage, kills, matches);
        this.statName = "p10";
    }
}
