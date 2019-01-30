package me.afarrukh.hashbot.commands.tagging;

public interface CategorisedCommand {

    default String getType() {
        if(this.getClass().getInterfaces().length > 1) {
            for(Class<?> itf: this.getClass().getInterfaces()) {
                if(itf.getSimpleName().equals("AdminCommand"))
                    continue;
                return itf.getSimpleName().replace("Command", "").toLowerCase();
            }
        }
        return getClass().getInterfaces()[0].getSimpleName().replace("Command", "").toLowerCase();
    }

}
