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
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

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

        BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = initialiseGraphics(bufferedImage);

        final int originX = 10;
        final int originY = 40;

        Invoker invoker = new Invoker(evt.getMember());

        BufferedImage profPic = ImageLoader.loadUrl(evt.getAuthor().getAvatarUrl());
        if (profPic != null)
            g.drawImage(profPic, 460, 10, null);

        String nameString = evt.getAuthor().getName();
        if (evt.getMember().getNickname() != null && (evt.getMember().getNickname().length()+ evt.getAuthor().getName().length()) < 24)
            nameString += "(" + evt.getMember().getNickname() + ")";

        Text.drawString(g, Integer.toString((int) invoker.getLevel()), 399, 78, true, Color.BLACK, Constants.bigNumFont);

        Text.drawString(g, nameString, originX, originY, false, Constants.STATSIMG_COL, Constants.font28);
        Text.drawString(g, "Credit: " + invoker.getCredit(), originX, originY + 30, false, Constants.STATSIMG_COL, Constants.font28);
        Text.drawString(g, "Experience: " + invoker.getExp() + "/" + invoker.getExpForNextLevel(), originX, originY + 60, false, Constants.STATSIMG_COL,
                Constants.font28);


        int offset = 3;

        g.setColor(new Color(91, 91, 91));
        g.fillRect(originX-offset, originY + 80 -offset, (100*3)+(2*offset), 20+(2*offset));
        g.setColor(Color.GRAY);
        g.fillRect(originX, originY + 80, 100 * 3, 20);
        g.setColor(Color.WHITE);
        g.fillRect(originX, originY + 80, invoker.getPercentageExp() * 3, 20);

        Role r = evt.getMember().getRoles().get(0);
        if (r != null)
            Text.drawString(g, r.getName(), originX, originY + 135, false, r.getColor(), Constants.font28);

        String fileName = "res/images/" + evt.getAuthor().getName() + System.currentTimeMillis() + ".png";

        g.dispose();

        File outputFile = saveImage(fileName, bufferedImage);
        evt.getTextChannel().sendFile(outputFile).queue();

        Timer timer = new Timer();
        timer.schedule(new DeletionTimer(outputFile), 5 * 1000);

        System.gc();
    }

    @Override
    public void onIncorrectParams(TextChannel channel) {

    }

    private Graphics2D initialiseGraphics(BufferedImage bufferedImage) {
        Graphics2D g = bufferedImage.createGraphics();
        g.setFont(Constants.font28);

        Map<RenderingHints.Key, Object> renderingHintsMap = new HashMap<>();
        renderingHintsMap.put(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        renderingHintsMap.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        renderingHintsMap.put(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHints(renderingHintsMap);

        //Drawings to be done in this space
        BufferedImage img = ImageLoader.loadImage(Constants.BG_PATH);

        float opacity = 0.4f;
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, opacity));

        g.drawImage(img, 0, 0, null);

        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));

        drawStar(g);

        //Uncomment below to use natural background
//        g.setColor(Color.BLACK);
//        g.fillRect(0, 0, width, height);

        return g;
    }

    private void drawStar(Graphics g) {

        int midX = 400;
        int midY = 80;
        int radius[] = {60, 30, 60, 30};
        int nPoints = 10;
        int[] X = new int[nPoints];
        int[] Y = new int[nPoints];

        for (double current = 0.0; current < nPoints; current++) {
            int i = (int) current;
            double x = -Math.cos(current*((2*Math.PI)/nPoints))*radius[i % 4];
            double y = -Math.sin(current*((2*Math.PI)/nPoints))*radius[i % 4];

            X[i] = (int) y+midX;
            Y[i] = (int) x+midY;
        }

        g.setColor(Constants.STATSIMG_COL);
        g.fillPolygon(X, Y, nPoints);
    }

    private File saveImage(String fileName, BufferedImage bufferedImage) {
        File outputFile = new File(fileName);
        try { ImageIO.write(bufferedImage, "png", outputFile); }
        catch (IOException e) { e.printStackTrace(); }
        return outputFile;
    }

    private class DeletionTimer extends TimerTask {
        private File file;
        private DeletionTimer(File file) {
            this.file = file;
        }
        @Override
        public void run() {
            file.delete();
        }
    }
}
