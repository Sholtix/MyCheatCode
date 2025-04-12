package wtf.sqwezz.ui.display.impl;

import com.google.common.eventbus.Subscribe;
import com.mojang.blaze3d.matrix.MatrixStack;
import wtf.sqwezz.Vredux;
import wtf.sqwezz.command.staffs.StaffStorage;
import wtf.sqwezz.events.EventDisplay;
import wtf.sqwezz.events.EventUpdate;
import wtf.sqwezz.ui.display.ElementRenderer;
import wtf.sqwezz.ui.display.ElementUpdater;
import wtf.sqwezz.ui.styles.Style;
import wtf.sqwezz.utils.client.IMinecraft;
import wtf.sqwezz.utils.render.ColorUtils;
import wtf.sqwezz.utils.render.DisplayUtils;
import wtf.sqwezz.utils.render.font.Fonts;
import wtf.sqwezz.utils.text.GradientUtil;
import wtf.sqwezz.functions.impl.render.HUD;
import wtf.sqwezz.utils.drag.Dragging;
import wtf.sqwezz.utils.math.Vector4i;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import net.minecraft.client.network.play.NetworkPlayerInfo;
import net.minecraft.scoreboard.Team;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

import java.awt.*;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;


@FieldDefaults(level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class StaffListNextgenRenderer implements ElementRenderer, ElementUpdater {

    final Dragging dragging;


    private Set<StaffData> staffPlayers = new LinkedHashSet<>();
    private final Pattern namePattern = Pattern.compile("^\\w{3,16}$");
    private final Pattern prefixMatches = Pattern.compile(".*(mod|der|adm|help|wne|хелп|адм|поддержка|кура|own|taf|curat|dev|supp|yt|сотруд|мод|мл.|стаже|зам|владе).*");
    @Override
    public void update(EventUpdate e) {
        staffPlayers.clear();
        IMinecraft.mc.world.getScoreboard().getTeams().stream().sorted(Comparator.comparing(Team::getName)).toList().forEach(team -> {
            String staffName = team.getMembershipCollection().toString().replaceAll("[\\[\\]]", "");
            boolean vanish = true;

            if (IMinecraft.mc.getConnection() != null) {
                for (NetworkPlayerInfo info : IMinecraft.mc.getConnection().getPlayerInfoMap()) {
                    if (info.getGameProfile().getName().equals(staffName)) {
                        vanish = false;
                    }
                }
            }

            if (namePattern.matcher(staffName).matches() && !staffName.equals(IMinecraft.mc.player.getName().getString())) {
                if (!vanish) {
                    if (prefixMatches.matcher(team.getPrefix().getString().toLowerCase(Locale.ROOT)).matches() || StaffStorage.isStaff(staffName)) {
                        staffPlayers.add(new StaffData(team.getPrefix(), staffName, StaffData.Status.NONE));
                    }
                }
                if (vanish && !team.getPrefix().getString().isEmpty()) {
                    staffPlayers.add(new StaffData(team.getPrefix(), staffName, StaffData.Status.VANISHED));
                }
            }
        });

        this.staffPlayers = staffPlayers.stream()
                .sorted(Comparator.comparing(this::getPriority))
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    float width;
    float height;

    @Subscribe
    public void render(EventDisplay eventDisplay) {

        float posX = dragging.getX();
        float posY = dragging.getY();
        float padding = 3;
        float fontSize = 6.0f;
        MatrixStack ms = eventDisplay.getMatrixStack();
        ITextComponent name = GradientUtil.gradient("Staff Statistics");


        drawStyledRect(posX, posY, width, height, 4);
        wtf.sqwezz.utils.font.Fonts.msSemiBold[16].drawCenteredString(ms, name, posX + width / 2, posY - 1f + padding + 4f, -1);

        posY += fontSize + padding * 2;

        float maxWidth = Fonts.sfMedium.getWidth(name, fontSize) + padding * 2;
        float localHeight = fontSize + padding * 2;
        posY += 3.5f;
        for (StaffData f : staffPlayers) {
            ITextComponent prefix = f.getPrefix();

            float prefixWidth = Fonts.sfMedium.getWidth(prefix, fontSize);
            String staff = (prefix.getString().isEmpty() ? "" : " ") + f.getName();
            float nameWidth = Fonts.sfMedium.getWidth(staff, fontSize);


            float localWidth = prefixWidth + nameWidth + Fonts.sfMedium.getWidth(f.getStatus().string, fontSize) + padding * 3 + (f.getStatus().string.equals("V") ? 7.5f : 0);

            if (f.getStatus().string.equals("V")) {
                Fonts.sfMedium.drawText(ms, prefix, posX + padding + 12f, posY + 0.5f, fontSize, 255);
                Fonts.sfMedium.drawText(ms, staff, posX + padding + 12f + prefixWidth, posY + 0.5f, -1, fontSize, 0.05f);
                DisplayUtils.drawImage(new ResourceLocation("Vredux/images/hud/staff1.png"),posX + padding + 2.5f, posY - 1,8,8,new Color(200,0,0).getRGB());
            } else {
                Fonts.sfMedium.drawText(ms, prefix, posX + padding, posY + 0.5f, fontSize, 255);
                Fonts.sfMedium.drawText(ms, staff, posX + padding + prefixWidth, posY + 0.5f, -1, fontSize, 0.05f);
            }



            //Fonts.sfMedium.drawText(ms, f.getStatus().string, posX + width - padding - Fonts.sfMedium.getWidth(f.getStatus().string, fontSize), posY + 0.5f, f.getStatus().color, fontSize, 0.05f);

            if (localWidth > maxWidth) {
                maxWidth = localWidth;
            }

            posY += fontSize + padding;
            localHeight += fontSize + padding;
        }

        width = Math.max(maxWidth, 80);
        height = localHeight + 2.5f;
        dragging.setWidth(width);
        dragging.setHeight(height);
    }

    @AllArgsConstructor
    @Data
    public static class StaffData {
        ITextComponent prefix;
        String name;
        Status status;

        public enum Status {
            NONE("", -1),
            VANISHED("V", ColorUtils.rgb(254, 68, 68));
            public final String string;
            public final int color;

            Status(String string, int color) {
                this.string = string;
                this.color = color;
            }
        }

        @Subscribe
        public String toString() {
            return prefix.getString();
        }
    }


    private int getPriority(StaffData staffData) {
        return switch (staffData.toString()) {
            case "admin", "админ" -> 0;
            case "ml.admin" -> 1;
            case "gl.moder" -> 2;
            case "st.moder", "s.moder" -> 3;
            case "moder", "модератор", "куратор" -> 4;
            case "j.moder" -> 5;
            case "st.helper" -> 6;
            case "helper+" -> 7;
            case "helper" -> 8;
            case "yt+" -> 9;
            case "yt" -> 10;
            default -> 11;
        };
    }

    private void drawStyledRect(float x,
                                float y,
                                float width,
                                float height,
                                float radius) {
        Style style = Vredux.getInstance().getStyleManager().getCurrentStyle();
        Vector4i vector4i = new Vector4i(HUD.getColor(0), HUD.getColor(90), HUD.getColor(180), HUD.getColor(170));
        DisplayUtils.drawShadow(x, y, width, height + 5, 12, style.getFirstColor().getRGB(), style.getSecondColor().getRGB());
        DisplayUtils.drawGradientRound(x + 0.5f, y + 0.5f, width - 1f, height + 4, radius + 0.5f, vector4i.x, vector4i.y, vector4i.z, vector4i.w); // outline
        DisplayUtils.drawRoundedRect(x, y, width, height + 5, radius, ColorUtils.rgba(13,13,13, 230));
    }
}
