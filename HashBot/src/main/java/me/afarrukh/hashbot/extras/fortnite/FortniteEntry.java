package me.afarrukh.hashbot.extras.fortnite;

import me.afarrukh.hashbot.config.Constants;
import me.afarrukh.hashbot.utils.APIUtils;
import net.dv8tion.jda.core.entities.Member;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;

class FortniteEntry {

    private Member member;
    private String userName;
    private String platform;

    private long kda;
    private int matchesPlayed;
    private long winPercentage;

    FortniteEntry(Member member, String userName, String platform) {
        this.member = member;
        this.userName = userName;
        this.platform = platform;
    }

    public void updateEntry() {

    }

    private String pullRawData() {
        HashMap<String, String> headers = new HashMap<>();

        headers.put("TRN-Api-Key", Constants.FTN_KEY);

        return APIUtils.getResponseFromURL("https://api.fortnitetracker.com/v1/profile/"+platform+"/" + userName, headers);
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

    public long getKda() {
        return kda;
    }

    public int getMatchesPlayed() {
        return matchesPlayed;
    }

    public long getWinPercentage() {
        return winPercentage;
    }
}
