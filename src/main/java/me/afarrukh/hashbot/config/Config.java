package me.afarrukh.hashbot.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.List;

public class Config {

    private final String prefix;
    private final String botToken;
    private final List<String> ownerIds;
    private final String dbUri;
    private final String dbUsername;
    private final String dbPassword;

    public Config(@JsonProperty("prefix") String prefix,
                  @JsonProperty(value = "botToken", required = true) String botToken,
                  @JsonProperty("ownerIds") List<String> ownerIds,
                  @JsonProperty(value = "dbUri", required = true) String dbUri,
                  @JsonProperty(value = "dbUsername", required = true) String dbUsername,
                  @JsonProperty(value = "dbPassword", required = true) String dbPassword) {
        this.prefix = prefix;
        this.botToken = botToken;
        this.ownerIds = ownerIds;
        this.dbUri = dbUri;
        this.dbUsername = dbUsername;
        this.dbPassword = dbPassword;
    }

    public String getPrefix() {
        return prefix;
    }

    public String getBotToken() {
        return botToken;
    }

    public List<String> getOwnerIds() {
        return ownerIds;
    }

    public String getDbUri() {
        return dbUri;
    }

    public String getDbUsername() {
        return dbUsername;
    }

    public String getDbPassword() {
        return dbPassword;
    }

    @Override
    public boolean equals(Object o) {
        return EqualsBuilder.reflectionEquals(this, o);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}
