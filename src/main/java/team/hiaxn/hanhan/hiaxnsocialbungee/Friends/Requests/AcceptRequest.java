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

public class AcceptRequest {
    private final String senderName;
    private final String BeAccepterName;

    public AcceptRequest(String senderName, String BeAccepterName) {
        this.senderName = senderName;
        this.BeAccepterName = BeAccepterName;
        accept();
    }

    public void accept() {
        System.out.println(profileHashMap.toString());
        PlayerProfile senderProfile = profileHashMap.get(senderName); //因为好友申请是存在接受者的Profile里面的 所以叫做senderProfile
        if (senderProfile == null) { //为空就是从来没人给他发好友请求
            ProxyServer.getInstance().getPlayer(senderName).sendMessage(
                    "§9§m-----------------------------------------------------\n" +
                            "§c那人没有邀请你成为好友! 1 请尝试§e/friend " + BeAccepterName + "\n" +
                            "§9§m-----------------------------------------------------");
            return;
        }
        if (senderProfile.getRequesters() == null) {
            ProxyServer.getInstance().getPlayer(senderName).sendMessage(
                    "§9§m-----------------------------------------------------\n" +
                            "§c那人没有邀请你成为好友! 2 请尝试§e/friend " + BeAccepterName + "\n" +
                            "§9§m-----------------------------------------------------");
            return;
        }
        if (profileHashMap.get(senderName).getRequesters().contains(BeAccepterName)) {
            for (FriendRequest friendRequest : profileHashMap.get(senderName).getFriendRequestsList()) {
                if (friendRequest.getSenderName().equals(BeAccepterName)) {
                    acceptSql();
                    friendRequest.response();
                    return;
                }
            }
        }
        ProxyServer.getInstance().getPlayer(senderName).sendMessage(
                "§9§m-----------------------------------------------------\n" +
                        "§c那人没有邀请你成为好友! 3 请尝试§e/friend " + BeAccepterName + "\n" +
                        "§9§m-----------------------------------------------------");
    }

    public void acceptSql() {
        ProxyServer.getInstance().getPlayer(senderName).sendMessage(
                "§9§m-----------------------------------------------------\n" +
                        "§a你现在是" + BeAccepterName + "的好友了\n" +
                        "§9§m-----------------------------------------------------");
        ProxyServer.getInstance().getPlayer(BeAccepterName).sendMessage(
                "§9§m-----------------------------------------------------\n" +
                        "§a你现在是" + senderName + "的好友了\n" +
                        "§9§m-----------------------------------------------------");
        ProxyServer.getInstance().getScheduler().schedule(HiaXnSocialBungee.getInstance(), () -> {
            try {
                //目标玩家的查询结果集
                String select1 = "SELECT * FROM player_data WHERE name = ?";
                PreparedStatement selps1 = connection.prepareStatement(select1);
                selps1.setString(1, BeAccepterName);
                ResultSet sel1rs = selps1.executeQuery();
                sel1rs.next();
                //命令发送者的查询结果集
                String select2 = "SELECT * FROM player_data WHERE uuid = ?";
                PreparedStatement sel2ps = connection.prepareStatement(select2);
                sel2ps.setString(1, String.valueOf(ProxyServer.getInstance().getPlayer(senderName).getUniqueId()));
                ResultSet sel2rs = sel2ps.executeQuery();
                sel2rs.next();
                //命令发送者的数据修改PrepareStateMent
                String insertOwn = "UPDATE player_data SET friends = ? WHERE uuid = ?";
                PreparedStatement insOwnPs = connection.prepareStatement(insertOwn);
                insOwnPs.setString(2, String.valueOf(ProxyServer.getInstance().getPlayer(senderName).getUniqueId()));
                //目标的数据修改PrepareStateMent
                String insertTaget = "UPDATE player_data SET friends = ? WHERE name = ?";
                PreparedStatement insTargetPs = connection.prepareStatement(insertTaget);
                insTargetPs.setString(2, BeAccepterName);

                //空检测 friends为预设friends:null就执行直接更改 否则就执行使用 原好友 + “，” + 新好友 的方式进行更改
                //发送者的
                if (sel2rs.getString("friends").equals("friends:null")) {
                    insOwnPs.setString(1, BeAccepterName);
                } else {
                    insOwnPs.setString(1, sel2rs.getString("friends") + "," + BeAccepterName);
                }
                insOwnPs.executeUpdate();
                //目标的
                if (sel1rs.getString("friends").equals("friends:null")) {
                    insTargetPs.setString(1, ProxyServer.getInstance().getPlayer(senderName).getName());
                } else {
                    insTargetPs.setString(1, sel1rs.getString("friends") + "," + ProxyServer.getInstance().getPlayer(senderName).getName());
                }
                insTargetPs.executeUpdate();

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }, 0L, TimeUnit.SECONDS);
    }
}
