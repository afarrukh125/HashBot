package me.afarrukh.hashbot.gameroles;

import java.util.Objects;

/**
 * More information regarding GameRoles is given in the GameRoleManager class
 */
public class GameRole {
    private final String creatorId;
    private final String name;

    public GameRole(String name, String creatorId) {
        this.name = name;
        this.creatorId = creatorId;
    }

    public String getName() {
        return name;
    }

    public String getCreatorId() {
        return creatorId;
    }

    @Override
    public String toString() {
        return "GameRole{" +
                "creatorId='" + creatorId + '\'' +
                ", name='" + name + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GameRole gameRole = (GameRole) o;
        return Objects.equals(creatorId, gameRole.creatorId) &&
                Objects.equals(name, gameRole.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(creatorId, name);
    }
}
