package me.afarrukh.hashbot.extras.fortnite.stats;

public class SquadStatistic extends Statistic {

    public SquadStatistic(int wins, long kd, long winPercentage, int kills, int matches) {
        super(wins, kd, winPercentage, kills, matches);
        this.statName = "p9";
    }

}
