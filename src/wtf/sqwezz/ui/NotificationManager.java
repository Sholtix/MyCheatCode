package wtf.sqwezz.ui;

import com.mojang.blaze3d.matrix.MatrixStack;

import java.util.concurrent.CopyOnWriteArrayList;

import wtf.sqwezz.Vredux;
import wtf.sqwezz.command.friends.FriendStorage;
import wtf.sqwezz.functions.impl.render.HUD;
import wtf.sqwezz.ui.styles.Style;
import wtf.sqwezz.utils.animations.Animation;
import wtf.sqwezz.utils.animations.Direction;
import wtf.sqwezz.utils.animations.impl.EaseBackIn;
import wtf.sqwezz.utils.animations.impl.EaseInOutQuad;
import wtf.sqwezz.utils.client.IMinecraft;
import wtf.sqwezz.utils.math.Vector4i;
import wtf.sqwezz.utils.render.DisplayUtils;
import wtf.sqwezz.utils.math.MathUtil;
import wtf.sqwezz.utils.render.ColorUtils;
import wtf.sqwezz.utils.render.font.Fonts;
import net.minecraft.util.ResourceLocation;

public class NotificationManager implements IMinecraft {
    public static final FriendStorage NOTIFICATION_MANAGER = null;
    private final CopyOnWriteArrayList<NotificationNurik> notificationsNur = new CopyOnWriteArrayList();
    private MathUtil AnimationMath;
    boolean state;

    public void add(String text, String content, int time) {
        //NotificationNurik notifications1 = Vredux.getInstance().getFunctionRegistry().getNotifications();
        this.notificationsNur.add(new NotificationNurik(text, content, time));
    }

    public void draw(MatrixStack stack) {
        //NotificationNurik notifications1 = Vredux.getInstance().getFunctionRegistry().getNotifications();
        int yOffset = 0;

        for (NotificationNurik notificationC : this.notificationsNur) {
            if (System.currentTimeMillis() - notificationC.getTime() <= (long) notificationC.time2 * 1000L - 300L) {
                notificationC.yAnimation.setDirection(Direction.FORWARDS);
            }
            notificationC.alpha = (float) notificationC.animation.getOutput();
            if (System.currentTimeMillis() - notificationC.getTime() > (long) notificationC.time2 * 1000L) {
                notificationC.yAnimation.setDirection(Direction.BACKWARDS);
            }
            if (notificationC.yAnimation.finished(Direction.BACKWARDS)) {
                this.notificationsNur.remove(notificationC);
                continue;
            }
            float x = (float) mc.getMainWindow().scaledWidth() - (Fonts.sfMedium.getWidth(notificationC.getText(), 7.0f) + 8.0f) - 10.0f;
            float y = mc.getMainWindow().scaledHeight() - 40;
            notificationC.yAnimation.setEndPoint(x);
            notificationC.yAnimation.setDuration(500);
            notificationC.setX((float) (x * notificationC.yAnimation.getOutput()));
            notificationC.setY(MathUtil.fast(notificationC.getY(), y -= (float) ((double) notificationC.draw(stack) + (yOffset * 31) - 23.0) + 2, 15.0f));
            --yOffset;
        }
    }

    private class NotificationNurik {
        private float x = 0.0f;
        private float y = mc.getMainWindow().getScaledHeight() - 24;
        private String text;
        private String content;
        private long time = System.currentTimeMillis();
        public Animation animation = new EaseInOutQuad(500, 1.0, Direction.FORWARDS);
        public Animation yAnimation = new EaseBackIn(500, 3.0, 1.0f);
        float alpha;
        int time2 = 3;
        private boolean isState;
        private boolean state;

        public NotificationNurik(String text, String content, int time) {
            this.text = text;
            this.content = content;
            this.time2 = time;
        }

        public float draw(MatrixStack stack) {
            mc.gameRenderer.setupOverlayRendering(2);

            float screenWidth = mc.getMainWindow().getScaledWidth();
            float posY = (float) mc.getMainWindow().scaledHeight() + 435 - y;

            float textWidth = wtf.sqwezz.utils.font.Fonts.msBold[12].getWidth(this.text) + 28.0F + 15F + 5;
            float infoWidth = wtf.sqwezz.utils.font.Fonts.msBold[12].getWidth("Информация");

            float widthh;
            if (textWidth < 100) {
                widthh = 100;
            } else {
                widthh = textWidth;
            }
            //print(textWidth + "");

            float screenStart = screenWidth- widthh + 8;
            MatrixStack ms = new MatrixStack();
            Style style = Vredux.getInstance().getStyleManager().getCurrentStyle();
            Vector4i vector4i = new Vector4i(HUD.getColor(0), HUD.getColor(90), HUD.getColor(180), HUD.getColor(170));
            DisplayUtils.drawShadow(screenStart, posY, widthh - 12f, 11.0F + 15 + 2, 12, style.getFirstColor().getRGB(), style.getSecondColor().getRGB());
            DisplayUtils.drawGradientRound(screenStart + 0.5f, posY + 0.5f, widthh - 12f - 1f, 11.0F + 15 - 1 + 2, 4f + 0.5f, vector4i.x, vector4i.y, vector4i.z, vector4i.w); // outline
            DisplayUtils.drawRoundedRect(screenStart, posY, widthh - 12f, 11.0F + 15 + 2, 4f, ColorUtils.rgba(13,13,13, 230));
            DisplayUtils.drawImage(new ResourceLocation("Vredux/images/hud/util.png"),screenStart + 5, posY + ((11 + 15 + 2) / 2) - 16 / 2,16,16,-1);


            //DisplayUtils.drawRoundedRect(screenStart, posY, textWidth - 12f, 11.0F + 15, 4.0F, new Color(13,13,13).getRGB());

            //Color style1 = sqwezz.getInstance().getStyleManager().getCurrentStyle().getFirstColor();
            //DisplayUtils.drawRoundedRect(screenStart, posY, textWidth - 12f, 11.0F + 15, 4.0F, new Color(13,13,13).getRGB());

            float textX = screenStart + textWidth - 12f - (wtf.sqwezz.utils.font.Fonts.msBold[12].getWidth(this.text)) - 8 ;
            wtf.sqwezz.utils.font.Fonts.msBold[14].drawString(stack, "Информация", (double) (textX), (double) (posY + 7.0F), -1);

            wtf.sqwezz.utils.font.Fonts.msBold[12].drawString(stack, this.text, (double) (textX), (double) (posY + 17.0F), -1);


            mc.gameRenderer.setupOverlayRendering();

            return 28.0F;
        }

        public float getX() {
            return this.x;
        }

        public float getY() {
            return this.y;
        }

        public void setX(float x) {
            this.x = x;
        }

        public void setY(float y) {
            this.y = y;
        }

        public String getText() {
            return this.text;
        }

        public String getContent() {
            return this.content;
        }

        public long getTime() {
            return this.time;
        }
    }
}