package team.hiaxn.hanhan.hiaxnsocialbungee.Utils;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class HoverTextUtil {
    /**
     *
     * @param targetPlayer 目标玩家
     * @param textComponents TextComponent对象们 可以有多个
     */
    public static void sendHoverMessage(ProxiedPlayer targetPlayer, TextComponent... textComponents) {
        targetPlayer.sendMessage(textComponents);
    }

    /**
     *
     * @param message 显示的信息
     * @return 返回一个TextComponent 可在实例化对象TextComponent = HoverTextUtil.XXXXXXXXX
     */
    public static TextComponent BuildNoClickEventTextComponent(String message) {
        TextComponent textComponent = new TextComponent(message);
        return textComponent;
    }

    /**
     *
     * @param message 显示的信息
     * @param hoverText 鼠标放在上面显示的信息
     * @param runCommand 点击后执行的命令
     * @return 返回一个TextComponent 可在实例化对象TextComponent = HoverTextUtil.XXXXXXXXX
     */
    public static TextComponent BuildTextComponent(String message,String hoverText,String runCommand) {
        TextComponent textComponent = new TextComponent(message);
        textComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,new BaseComponent[]{new TextComponent(hoverText)}));
        textComponent.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, runCommand));
        return textComponent;
    }
}
