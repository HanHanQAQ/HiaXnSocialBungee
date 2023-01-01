package team.hiaxn.hanhan.hiaxnsocialbungee.MySql;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;
import team.hiaxn.hanhan.hiaxnsocialbungee.HiaXnSocialBungee;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.concurrent.RunnableFuture;
import java.util.concurrent.TimeUnit;

import static team.hiaxn.hanhan.hiaxnsocialbungee.HiaXnSocialBungee.connection;

/**
 * @Author HanHan
 * @Date 2022.12.31
 */
public class MySqlManager {

    public MySqlManager() throws IOException {}
    /**
     * @param tableName 表名
     * @param params 表的参数 示例写法：uuid varchar(255),name varchar(255),friends TEXT(65536),partys TEXT(65536),status varchar(255)
     */
    public static void createTable(String tableName,String params) {
        try {
            String createSql = "CREATE TABLE IF NOT EXISTS " + tableName + "(" + params + ")";
            PreparedStatement ps = connection.prepareStatement(createSql);
            ps.executeUpdate();
            HiaXnSocialBungee.getInstance().getLogger().info("成功的创建/找到了HiaXnSocialBungee数据库");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public static Connection getNewConnection() { //获取链接柄
        // 获取Config中的MySql配置信息
        String host =HiaXnSocialBungee.getInstance().getConfig().getString("MySql.ip");
        int port = HiaXnSocialBungee.getInstance().getConfig().getInt("MySql.port");
        String database = HiaXnSocialBungee.getInstance().getConfig().getString("MySql.database");
        String user = HiaXnSocialBungee.getInstance().getConfig().getString("MySql.user");
        String password = HiaXnSocialBungee.getInstance().getConfig().getString("MySql.password");
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            String url = "jdbc:mysql://" + host + ":" + port + "/" + database + "?useSSL=false";
            Connection connection = DriverManager.getConnection(url, user, password);
            HiaXnSocialBungee.getInstance().getLogger().info(ChatColor.GREEN + "[HiaXnSocialBungee MySql] 链接至MySql");
            return connection;
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            HiaXnSocialBungee.getInstance().getLogger().warning(ChatColor.RED + "[HiaXnSocialBungee MySql] 没有链接至MySql");
            return null;
        }
    }
    public static void keepConnect() {
        ProxyServer.getInstance().getScheduler().schedule(new Plugin(), () -> {
            try {
                if (connection != null && !connection.isClosed()) {
                    connection.createStatement().execute("SELECT 1");
                }
            } catch (SQLException e) {
                connection = getNewConnection();
            }
        }, 0, 2L, TimeUnit.SECONDS);
    }
}
