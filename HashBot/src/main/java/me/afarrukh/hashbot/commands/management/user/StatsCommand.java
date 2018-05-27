package me.afarrukh.hashbot.commands.management.user;

import me.afarrukh.hashbot.commands.Command;
import me.afarrukh.hashbot.config.Constants;
import me.afarrukh.hashbot.entities.Invoker;
import me.afarrukh.hashbot.graphics.ImageLoader;
import me.afarrukh.hashbot.graphics.Text;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;

public class StatsCommand extends Command {

    private int width;
    private int height;

    public StatsCommand() {
        super("stats");
        width = Constants.WIDTH;
        height = Constants.HEIGHT;
    }

    @Override
    public void onInvocation(MessageReceivedEvent evt, String params) {
        evt.getMessage().delete().queue();

        BufferedImage br = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = initialiseGraphics(br);

        final int originX = 25;
        final int originY = 40;

        Invoker invoker = new Invoker(evt.getMember());

        BufferedImage profPic = ImageLoader.loadUrl(evt.getAuthor().getAvatarUrl());
        if(profPic != null)
            g.drawImage(profPic, 460, 10, null);

        String nameString = evt.getAuthor().getName();
        if(evt.getMember().getNickname() != null)
            nameString += " ("+evt.getMember().getNickname()+")";

        g.setStroke(new BasicStroke(BasicStroke.CAP_ROUND));
        Text.drawString(g, nameString, originX, originY, false, Constants.EMB_COL, Constants.font28);
        Text.drawString(g, Integer.toString((int)invoker.getLevel()), 375, 80, false, Constants.EMB_COL, Constants.font72);
        Text.drawString(g, "Experience: "+invoker.getExp()+"/"+invoker.getExpForNextLevel(), originX, originY+30, false, Constants.EMB_COL,
                Constants.font28);
        Text.drawString(g, "Credit: " +invoker.getCredit(), originX, originY+60, false, Constants.EMB_COL, Constants.font28);
        //Text.drawString(g, Integer.toString(invoker.getPercentageExp()), originX, originY+90, false, Constants.EMB_COL, Constants.font28);

        g.setColor(Color.GRAY); g.fillRect(originX, originY + 90, 100*3, 20);
        g.setColor(Color.WHITE); g.fillRect(originX, originY+ 90, invoker.getPercentageExp()*3, 20);

        Role r = evt.getMember().getRoles().get(0);
        if(r!= null)
            Text.drawString(g, r.getName(), originX, originY+145, false, r.getColor(), Constants.font28);

        String fileName = "res/images/"+evt.getAuthor().getName()+System.currentTimeMillis()+".png";

        g.dispose();

        File outputFile = saveImage(fileName, br);
        evt.getTextChannel().sendFile(outputFile).queue();
    }

    @Override
    public void onIncorrectParams(TextChannel channel) {

    }

    public Graphics2D initialiseGraphics(BufferedImage br) {
        Graphics2D g = br.createGraphics();
        g.setFont(Constants.font28);
        //Drawings to be done in this space
        BufferedImage img = ImageLoader.loadImage(Constants.BG_PATH);

        float opacity = 0.4f;
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, opacity));

        g.drawImage(img, 0, 0, null);

        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));

        //Uncomment below to use natural background
//        g.setColor(Color.BLACK);
//        g.fillRect(0, 0, width, height);

        return g;
    }

    public File saveImage(String fileName, BufferedImage br) {
        File outputFile = new File(fileName);
        try {
            ImageIO.write(br, "png", outputFile);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return outputFile;
    }
}
