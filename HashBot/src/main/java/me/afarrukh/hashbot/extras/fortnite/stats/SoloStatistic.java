package me.afarrukh.hashbot.extras.fortnite.stats;

public class SoloStatistic extends Statistic {

    public SoloStatistic(int wins, long kd, long winPercentage, int kills, int matches) {
        super(wins, kd, winPercentage, kills, matches);
        this.statName = "p2";
    }
}
