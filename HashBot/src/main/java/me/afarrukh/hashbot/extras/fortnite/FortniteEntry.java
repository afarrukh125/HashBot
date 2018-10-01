package me.afarrukh.hashbot.extras.fortnite;

import me.afarrukh.hashbot.config.Constants;
import me.afarrukh.hashbot.extras.fortnite.stats.*;
import me.afarrukh.hashbot.utils.APIUtils;
import net.dv8tion.jda.core.entities.Member;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.text.DecimalFormat;

class FortniteEntry {

    private Member member;
    private String userName;
    private String platform;

    private Statistic lifeTimeStatistic;
    private Statistic soloStatistic;
    private Statistic duoStatistic;
    private Statistic squadStatistic;

    FortniteEntry(Member member, String userName, String platform) {
        this.member = member;
        this.userName = userName;
        this.platform = platform;

        lifeTimeStatistic = new LifetimeStatistic(0, 0, 0, 0, 0);
        soloStatistic = new SoloStatistic(0, 0, 0, 0, 0);
        duoStatistic = new DuoStatistic(0, 0, 0, 0, 0);
        squadStatistic = new SquadStatistic(0, 0, 0, 0, 0);

        updateEntry();
    }

    public void updateEntry() {
        String data = pullRawData();
        if(data == null)
            return;

        try {
            JSONObject topLevelObject = (JSONObject) new JSONParser().parse(data);

            JSONArray lifeTimeArray = (JSONArray) topLevelObject.get("lifeTimeStats");
            for(Object o: lifeTimeArray) {
                JSONObject dataObject = (JSONObject) o;
                if(dataObject.get("key").equals("Wins")) {
                    Long winValue = Long.parseLong((String)dataObject.get("value"));
                    lifeTimeStatistic.setWins(Math.toIntExact(winValue));
                }
                else if(dataObject.get("key").equals("K/d"))
                    lifeTimeStatistic.setKd(Double.parseDouble((String) dataObject.get("value")));
                else if(dataObject.get("key").equals("Kills")) {
                    Long killsValue = Long.parseLong((String) dataObject.get("value"));
                    lifeTimeStatistic.setKills(killsValue.intValue());
                }
                else if(dataObject.get("key").equals("Matches Played")) {
                    Long matchesValue = Long.parseLong((String) dataObject.get("value"));
                    lifeTimeStatistic.setMatches(matchesValue.intValue());
                }
            }
            DecimalFormat df = new DecimalFormat("#.###");
            lifeTimeStatistic.setWinPercentage(Double.parseDouble(df.format((double)lifeTimeStatistic.getWins()/(double)lifeTimeStatistic.getMatches() * 100)));

            loadStatistic(soloStatistic, topLevelObject);
            loadStatistic(duoStatistic, topLevelObject);
            loadStatistic(squadStatistic, topLevelObject);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private void loadStatistic(Statistic statistic, JSONObject topLevelObject) {
        try {
            JSONObject statsObject = (JSONObject) topLevelObject.get("stats");
            JSONObject dataObject = (JSONObject) statsObject.get(statistic.getStatName());

            JSONObject matchesObject = (JSONObject) dataObject.get("matches");
            Long matchesValue = (Long) matchesObject.get("valueInt");
            statistic.setMatches(matchesValue.intValue());

            JSONObject winPercentageObject = (JSONObject) dataObject.get("winRatio");
            statistic.setWinPercentage((Double.parseDouble((String) winPercentageObject.get("displayValue"))));

            JSONObject winObject = (JSONObject) dataObject.get("top1");
            Long winVal = (Long) winObject.get("valueInt");
            statistic.setWins(Math.round(winVal.intValue()));

            JSONObject killsObject = (JSONObject) dataObject.get("kills");
            Long killsValue = (Long) killsObject.get("valueInt");
            statistic.setKills(killsValue.intValue());

            JSONObject kdObject = (JSONObject) dataObject.get("kd");
            statistic.setKd(Double.parseDouble((String) kdObject.get("displayValue")));
        } catch(NullPointerException e) {
            statistic.setKills(0);
            statistic.setMatches(0);
            statistic.setKd(0);
            statistic.setWinPercentage(0);
            statistic.setWins(0);
        }
    }

    private String pullRawData() {
        return APIUtils.getResponseFromURL("https://api.fortnitetracker.com/v1/profile/"+platform+"/" + userName, Constants.fortAPIHeader);
    }

    public Member getMember() {
        return member;
    }

    public String getUserName() {
        return userName;
    }

    public String getPlatform() {
        return platform;
    }

    public Statistic getLifeTimeStatistic() {
        return lifeTimeStatistic;
    }

    public Statistic getSoloStatistic() {
        return soloStatistic;
    }

    public Statistic getDuoStatistic() {
        return duoStatistic;
    }

    public Statistic getSquadStatistic() {
        return squadStatistic;
    }
}
