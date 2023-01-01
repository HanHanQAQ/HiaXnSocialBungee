package team.hiaxn.hanhan.hiaxnsocialbungee.Friends.Requests;

import net.md_5.bungee.api.ProxyServer;
import team.hiaxn.hanhan.hiaxnsocialbungee.HiaXnSocialBungee;
import team.hiaxn.hanhan.hiaxnsocialbungee.PlayerProfile.PlayerProfile;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.TimeUnit;

import static team.hiaxn.hanhan.hiaxnsocialbungee.Friends.Requests.Request.profileHashMap;
import static team.hiaxn.hanhan.hiaxnsocialbungee.HiaXnSocialBungee.connection;

public class DenyRequest {
    private final String senderName;
    private final String BeAccepterName;

    public DenyRequest(String senderName, String BeAccepterName) {
        this.senderName = senderName;
        this.BeAccepterName = BeAccepterName;
        deny();
    }

    public void deny() {
        PlayerProfile senderProfile = profileHashMap.get(senderName); //因为好友申请是存在接受者的Profile里面的 所以叫做senderProfile
        if (senderProfile == null) { //为空就是从来没人给他发好友请求
            ProxyServer.getInstance().getPlayer(senderName).sendMessage(
                    "§9§m-----------------------------------------------------\n" +
                            "§c那人没有邀请你成为好友! 请尝试§e/friend " + BeAccepterName + "\n" +
                            "§9§m-----------------------------------------------------");
            return;
        }
        if (senderProfile.getRequesters() == null) {
            ProxyServer.getInstance().getPlayer(senderName).sendMessage(
                    "§9§m-----------------------------------------------------\n" +
                            "§c那人没有邀请你成为好友! 请尝试§e/friend " + BeAccepterName + "\n" +
                            "§9§m-----------------------------------------------------");
            return;
        }
        if (profileHashMap.get(senderName).getRequesters().contains(BeAccepterName)) {
            for (FriendRequest friendRequest : profileHashMap.get(senderName).getFriendRequestsList()) {
                if (friendRequest.getSenderName().equals(BeAccepterName)) {
                    denyed();
                    friendRequest.response();
                    return;
                }
            }
        }
        ProxyServer.getInstance().getPlayer(senderName).sendMessage(
                "§9§m-----------------------------------------------------\n" +
                        "§c那人没有邀请你成为好友! 请尝试§e/friend " + BeAccepterName + "\n" +
                        "§9§m-----------------------------------------------------");
    }

    public void denyed() {
        ProxyServer.getInstance().getPlayer(senderName).sendMessage(
                "§9§m-----------------------------------------------------\n" +
                        "§e已拒绝 " + BeAccepterName + " 的好友申请!\n" +
                        "§9§m-----------------------------------------------------");

    }
}
