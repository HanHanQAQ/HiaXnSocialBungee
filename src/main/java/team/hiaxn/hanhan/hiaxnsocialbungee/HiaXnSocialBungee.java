package team.hiaxn.hanhan.hiaxnsocialbungee;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;
import org.checkerframework.checker.units.qual.A;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPubSub;
import team.hiaxn.hanhan.hiaxnsocialbungee.Friends.Requests.AcceptRequest;
import team.hiaxn.hanhan.hiaxnsocialbungee.Friends.Requests.FriendRequest;
import team.hiaxn.hanhan.hiaxnsocialbungee.MySql.MySqlManager;
import team.hiaxn.hanhan.hiaxnsocialbungee.Redis.RedisUtil;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class HiaXnSocialBungee extends Plugin {
    public HiaXnSocialBungee() throws IOException {
    }
    private static HiaXnSocialBungee instance;
    public static HiaXnSocialBungee getInstance() {
        return instance;
    }
    public Configuration configuration = ConfigurationProvider.getProvider(YamlConfiguration.class).load(new File(getDataFolder(), "config.yml"));
    public Configuration getConfig() { return this.configuration; };
    public static String serverName = "HiaXn";
    public static String serverAddress = "www.hiaxn.cn";
    public static JedisPubSub jedisPubSub;
    public static Connection connection;
    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();
        RedisConnect();
        MySqlManager.getNewConnection();
        MySqlManager.createTable("player_data","uuid varchar(255),name varchar(255),friends TEXT(65536),partys TEXT(65536),status varchar(255)");
        MySqlManager.keepConnect();
    }

    @Override
    public void onDisable() {
        jedisPubSub.unsubscribe("friends");
        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public void saveDefaultConfig() {
        if (!getDataFolder().exists())
            getDataFolder().mkdir();
        File file = new File(getDataFolder(), "config.yml");
        if (!file.exists()) {
            try (InputStream in = getResourceAsStream("config.yml")) {
                Files.copy(in, file.toPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    public void RedisConnect() {
        jedisPubSub = new JedisPubSub() {
            @Override
            public void onMessage(String channel, String message) {
                /*Request指令格式
                * frequest <senderName> <reciverName>
                 */
                if (channel.equals("friends")) {
                    if (message.split(" ")[0].equals("frequest")) {
                        switch (message.split(" ")[1]) {
                            case "add":
                                try {
                                    FriendRequest friendRequest = new FriendRequest(message.split(" ")[2],message.split(" ")[3]);
                                } catch (Exception e) {
                                    throw new RuntimeException(e);
                                }
                            case "accept":
                                AcceptRequest acceptRequest = new AcceptRequest(message.split(" ")[2],message.split(" ")[3]);
                        }

                    }
                }
                ProxyServer.getInstance().broadcast("消息来自频道"+"---"+channel + ": " + message);
            }
            @Override
            public void onSubscribe(String channel, int subscribedChannels) {
                getLogger().info(ChatColor.GREEN + "订阅频道" + "---" + channel + ": " + subscribedChannels);
            }
        };
        Executors.newScheduledThreadPool(1).scheduleWithFixedDelay(() -> {
            try(Jedis jedis = RedisUtil.getJedis()){
                getLogger().info(ChatColor.GREEN + "[HiaXnSocial Redis] jedis资源获取成功");
                // 订阅频道消息
                jedis.subscribe(jedisPubSub,"friends");
                getLogger().info(ChatColor.GREEN + "[HiaXnSocial Redis] 订阅频道: friends 成功");
                jedis.subscribe(jedisPubSub,"party");
                getLogger().info(ChatColor.GREEN + "[HiaXnSocial Redis] 订阅频道: party 成功");
            } catch (Exception e) {
                //getLogger().warning(ChatColor.RED + "[HiaXnSocial Redis] jedis资源获取异常，等待重试");
            }
        },0L,5L, TimeUnit.SECONDS);
    }
}
