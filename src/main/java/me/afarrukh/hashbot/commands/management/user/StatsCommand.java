package me.afarrukh.hashbot.commands.management.user;

import me.afarrukh.hashbot.commands.Command;
import me.afarrukh.hashbot.config.Constants;
import me.afarrukh.hashbot.entities.Invoker;
import me.afarrukh.hashbot.graphics.ImageLoader;
import me.afarrukh.hashbot.graphics.Text;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.NotNull;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import static java.lang.Runtime.getRuntime;
import static java.util.Collections.synchronizedList;
import static java.util.concurrent.Executors.newFixedThreadPool;

public class StatsCommand extends Command {

    public static final String GLOBAL_PARAM_FLAG = "global";
    private final int width;
    private final int height;

    public StatsCommand() {
        super("stats");
        addAlias("balance");
        addAlias("credits");
        addAlias("bal");
        width = Constants.WIDTH;
        height = Constants.HEIGHT;
        description = "Displays your statistics. Optionally, you can provide 'global' as a parameter to view your global stats";
    }

    @Override
    public void onInvocation(MessageReceivedEvent evt, String params) {

        BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g;
        try {
            g = initialiseGraphics(bufferedImage);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        final int originX = 10;
        final int originY = 40;

        Invoker invoker = Invoker.of(evt.getMember());

        var exp = new AtomicLong((int) invoker.getExp());
        int level = invoker.getLevel();
        int nextLevelExp = invoker.getExpForNextLevel();

        boolean global = false;

        if (params != null && params.equalsIgnoreCase(GLOBAL_PARAM_FLAG)) {
            global = true;
            exp = new AtomicLong();
            var executorService = newFixedThreadPool(getThreads());
            for (Guild guild : evt.getJDA().getGuilds()) {
                AtomicLong finalExp = exp;
                executorService.execute(() -> {
                    Member m = guild.getMemberById(evt.getAuthor().getId());
                    if (m == null)
                        return;
                    Invoker tmpInvoker = Invoker.of(m);
                    finalExp.addAndGet(Invoker.parseTotalExperienceFromLevel(tmpInvoker.getLevel()));
                    finalExp.addAndGet(tmpInvoker.getExp());
                });
            }
            executorService.shutdown();
            try {
                executorService.awaitTermination(30, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            var data = Invoker.parseLevelFromTotalExperience(exp.get());
            level = data.level();
            exp.set(data.exp());
            nextLevelExp = Invoker.getExperienceForNextLevel(level);
        }

        BufferedImage profPic = ImageLoader.loadUrl(evt.getAuthor().getAvatarUrl());
        if (profPic != null)
            g.drawImage(profPic, 460, 10, null);

        String nameString = evt.getAuthor().getName();
        if (evt.getMember().getNickname() != null && (evt.getMember().getNickname().length() + evt.getAuthor().getName().length()) < 24)
            nameString += "(" + evt.getMember().getNickname() + ")";

        Text.drawString(g, Integer.toString(level), 399, 78, true, Color.BLACK, Constants.bigNumFont);

        Text.drawString(g, nameString, originX, originY, false, Constants.STATSIMG_COL, Constants.font28);
        Text.drawString(g, "Credit: " + invoker.getCredit(), originX, originY + 30, false, Constants.STATSIMG_COL, Constants.font28);
        Text.drawString(g, "Exp: " + exp + "/" + nextLevelExp, originX, originY + 60, false, Constants.STATSIMG_COL,
                Constants.font28);


        int offset = 3;

        g.setColor(new Color(91, 91, 91));
        g.fillRect(originX - offset, originY + 80 - offset, (100 * 3) + (2 * offset), 20 + (2 * offset));
        g.setColor(Color.GRAY);
        g.fillRect(originX, originY + 80, 100 * 3, 20);
        g.setColor(Color.WHITE);
        if (global)
            g.fillRect(originX, originY + 80, Invoker.getPercentageExp(exp.get(), level) * 3, 20);
        else
            g.fillRect(originX, originY + 80, invoker.getPercentageExp() * 3, 20);


        if (!global) {
            Role r = null;
            if (!evt.getMember().getRoles().isEmpty())
                r = evt.getMember().getRoles().get(0);
            if (r != null)
                Text.drawString(g, r.getName(), originX, originY + 140, false, r.getColor(), Constants.font28);
        } else {
            Text.drawString(g, "Global Stats", originX, originY + 140, false, Color.WHITE, Constants.font28);
        }

        String fileName = "res/images/" + evt.getAuthor().getName() + System.currentTimeMillis() + ".png";

        g.dispose();

        File outputFile = saveImage(fileName, bufferedImage);
        evt.getChannel().sendFile(outputFile).queue(m -> outputFile.delete());
    }

    private int getThreads() {
        return getRuntime().availableProcessors() * 2;
    }

    @Override
    public void onIncorrectParams(TextChannel channel) {

    }

    private Graphics2D initialiseGraphics(BufferedImage bufferedImage) throws InterruptedException {
        Graphics2D g = bufferedImage.createGraphics();
        g.setFont(Constants.font28);

        Map<RenderingHints.Key, Object> renderingHintsMap = new HashMap<>();
        renderingHintsMap.put(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        renderingHintsMap.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        renderingHintsMap.put(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHints(renderingHintsMap);

        //Drawings to be done in this space
        BufferedImage img = ImageLoader.loadImage(Constants.BG_PATH);

        float opacity = 1.0f;
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, opacity));

        final int numberPartitions = getThreads();

        List<SubImage> subImages = subImages(img, numberPartitions);

        var executorService = newFixedThreadPool(numberPartitions);
        for (var subImage : subImages) {
            executorService.execute(() -> {
                drawImageParallel(g, subImage.bufferedImage(), subImage.x(), subImage.y());
            });
        }

        executorService.shutdown();
        executorService.awaitTermination(30, TimeUnit.SECONDS);

        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));

        drawStar(g);
        return g;
    }

    @NotNull
    private List<SubImage> subImages(BufferedImage bufferedImage, int numberPartitions) throws InterruptedException {
        List<SubImage> subImages = synchronizedList(new ArrayList<>());
        int unitWidth = bufferedImage.getWidth() / 4;
        int unitHeight = bufferedImage.getHeight() / 4;

        var executorService = newFixedThreadPool(numberPartitions);
        for (int i = 0; i < bufferedImage.getWidth(); i += unitWidth) {
            for (int j = 0; j < bufferedImage.getHeight(); j += unitHeight) {
                int finalI = i;
                int finalJ = j;
                executorService.execute(() -> {
                    var subImage = bufferedImage.getSubimage(finalI, finalJ, unitWidth, unitHeight);
                    subImages.add(new SubImage(finalI,
                            finalJ,
                            subImage));
                });
            }
        }
        executorService.shutdown();
        executorService.awaitTermination(30, TimeUnit.MINUTES);
        return subImages;
    }

    private void drawImageParallel(Graphics2D g, BufferedImage img, int x, int y) {
        g.drawImage(img, x, y, null);
    }

    private void drawStar(Graphics g) {

        int midX = 400;
        int midY = 80;
        int[] radius = {60, 30, 60, 30};
        int nPoints = 10;
        int[] X = new int[nPoints];
        int[] Y = new int[nPoints];

        for (double current = 0.0; current < nPoints; current++) {
            int i = (int) current;
            double x = -Math.cos(current * ((2 * Math.PI) / nPoints)) * radius[i % 4];
            double y = -Math.sin(current * ((2 * Math.PI) / nPoints)) * radius[i % 4];

            X[i] = (int) y + midX;
            Y[i] = (int) x + midY;
        }

        g.setColor(Constants.STATSIMG_COL);
        g.fillPolygon(X, Y, nPoints);
    }

    private File saveImage(String fileName, BufferedImage bufferedImage) {
        File outputFile = new File(fileName);
        try {
            ImageIO.write(bufferedImage, "png", outputFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return outputFile;
    }

    private record SubImage(int x, int y, BufferedImage bufferedImage) {
    }
}