package es.edoras.uuidregister;

import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class UUIDRegister extends Plugin {
    public Connection connection;
    public Configuration config;

    @Override
    public void onEnable() {
        // Crea la configuración en caso de que no exista
        if (!getDataFolder().exists()) getDataFolder().mkdir();
        File file = new File(getDataFolder(), "config.yml");
        if (!file.exists()) {
            try (InputStream in = getResourceAsStream("config.yml")) {
                Files.copy(in, file.toPath());
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        // Carga la configuración
        try {
            config = ConfigurationProvider.getProvider(YamlConfiguration.class).load(new File(getDataFolder(), "config.yml"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Registrar listener
        this.getProxy().getPluginManager().registerListener(this, new PlayerJoinEvent(this));

        // Base de datos
        try {
            this.openConnection();
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void openConnection() throws SQLException, ClassNotFoundException {
        if (connection != null && !connection.isClosed()) {
            return;
        }

        synchronized(this){
            if (connection != null && !connection.isClosed()) {
                return;
            }

            connection = DriverManager.getConnection("jdbc:mysql://" + config.getString("database.host") + ":" + config.getInt("database.port") + "/" + config.getString("database.database"), config.getString("database.user"), config.getString("database.password"));
        }
    }
}
