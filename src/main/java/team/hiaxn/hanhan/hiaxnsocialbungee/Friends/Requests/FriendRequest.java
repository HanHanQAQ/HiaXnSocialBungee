package team.hiaxn.hanhan.hiaxnsocialbungee.Friends.Requests;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Plugin;
import team.hiaxn.hanhan.hiaxnsocialbungee.PlayerProfile.PlayerProfile;
import team.hiaxn.hanhan.hiaxnsocialbungee.Utils.HoverTextUtil;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

import static team.hiaxn.hanhan.hiaxnsocialbungee.Friends.Requests.Request.profileHashMap;

public class FriendRequest {

    private final FriendRequest instanceRequest = this; //实例化后的对像内存地址值
    private boolean responded = false; //接收好友请求的玩家是否响应了这个好友请求
    private String senderName;
    private PlayerProfile senderProfile;
    private String reciverName;
    private PlayerProfile reciverProfile;
    public FriendRequest(String senderName,String reciverName) throws Exception {
        this.senderName = senderName;
        this.reciverName = reciverName;
        sendRequest();
    }

    private void sendRequest() throws Exception {
        PlayerProfile targeterProfile = new PlayerProfile(reciverName);
        for (String name : targeterProfile.getPlayerFriendsArray()) {
            if (Objects.equals(name, senderName)) {
                ProxyServer.getInstance().getPlayer(senderName).sendMessage(
                        "§9§m-----------------------------------------------------\n" +
                        "§c你已经是该玩家的好友了!\n" +
                        "§9§m-----------------------------------------------------");
                return;
            }
        }
        for (String name : targeterProfile.getRequesters()) {
            if (name.equals(senderName)) {
                ProxyServer.getInstance().getPlayer(senderName).sendMessage(
                        "§9§m-----------------------------------------------------\n" +
                        "§e你已经向这个玩家发送了好友申请!请等待一会儿\n" +
                        "§9§m-----------------------------------------------------");
                return;
            }
        }
        if (!reciverProfile.isOnline()) {
            ProxyServer.getInstance().getPlayer(senderName).sendMessage(
                    "§9§m-----------------------------------------------------\n" +
                    "§c" + reciverName + "不在线!\n" +
                    "§9§m-----------------------------------------------------" );
            return;
        }
        sendNewRequest();
    }
    private void sendNewRequest() {
        reciverProfile.addRequester(senderName);
        reciverProfile.addFriendRequest(instanceRequest);
        profileHashMap.put(reciverName,reciverProfile);
        ProxyServer.getInstance().broadcast("§e[DEBUG] 玩家在本服务器 执行指令");
        ProxyServer.getInstance().getPlayer(senderName).sendMessage(
                "§9§m-----------------------------------------------------\n" +
                "§e你的好友请求已发送给" + reciverName + "!该请求需要在五分钟内接受!\n" +
                "§9§m-----------------------------------------------------" );
        ProxiedPlayer targetPlayer = ProxyServer.getInstance().getPlayer(reciverName);
        ProxyServer.getInstance().getPlayer(reciverName).sendMessage("§9§m-----------------------------------------------------\n");
        ProxyServer.getInstance().getPlayer(reciverName).sendMessage("§e好友请求: " + senderName);
        TextComponent accept = HoverTextUtil.BuildTextComponent("§a§l[§r§a接受§l]","§b点击接受好友请求","/friend accept " + senderName);
        TextComponent barLine = HoverTextUtil.BuildNoClickEventTextComponent(" §r§7§m-§r ");
        TextComponent deny = HoverTextUtil.BuildTextComponent("§c§l[§r§c拒绝§l]","§b点击拒绝好友请求","/friend deny " + senderName);
        HoverTextUtil.sendHoverMessage(targetPlayer,accept,barLine,deny);
        ProxyServer.getInstance().getPlayer(reciverName).sendMessage("§9§m-----------------------------------------------------\n");
        countDown();
    }
    private void countDown() {
        ProxyServer.getInstance().getScheduler().schedule(new Plugin(), new Runnable() {
            int count = 300;
            @Override
            public void run() {
                if (count == 514) {
                    try {
                        Thread.sleep(1145141919810L);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
                if (count == 0) {
                    ProxyServer.getInstance().getPlayer(reciverName).sendMessage(
                            "§9§m-----------------------------------------------------\n" +
                            "§e来自" + senderName + "的好友申请已失效。\n" +
                            "§9§m-----------------------------------------------------");
                    ProxyServer.getInstance().getPlayer(senderName).sendMessage(
                            "§9§m-----------------------------------------------------\n" +
                            "§e你向" + reciverName + "发送的好友申请已失效。\n" +
                            "§9§m-----------------------------------------------------");
                    profileHashMap.get(reciverName).removeRequester(senderName);
                    profileHashMap.get(reciverName).removeFriendRequest(instanceRequest);
                    profileHashMap.put(reciverName, reciverProfile);
                    count = 514;
                    return;
                }
                if (responded) {
                    profileHashMap.get(reciverName).removeRequester(senderName);
                    profileHashMap.get(reciverName).removeFriendRequest(instanceRequest);
                    profileHashMap.put(reciverName, reciverProfile);
                    count = 514;
                    return;
                }
                count--;
            }
        }, 0L, 20L, TimeUnit.SECONDS);
    }
    public void response() {
        this.responded = true;
    }
    public String getSenderName() {
        return this.senderName;
    }
}
