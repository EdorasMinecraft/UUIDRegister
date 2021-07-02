package es.edoras.uuidregister;

import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PlayerJoinEvent implements Listener {
    private final Connection connection;

    public PlayerJoinEvent(UUIDRegister plugin){
        this.connection = plugin.connection;
    }

    @EventHandler
    public void onPostLogin(PostLoginEvent e){
        // Comprueba si un usuario está en la tabla `edocord`
        try {
            // Comprueba si un usuario está en la tabla `player_uuids`
            PreparedStatement checkname = connection.prepareStatement("SELECT `name` FROM `player_uuids` WHERE `uuid` = ? LIMIT 1");
            checkname.setString(1, e.getPlayer().getUniqueId().toString());
            ResultSet ansname = checkname.executeQuery();
            // Si está registrado...
            if(ansname.next()){
                if(!e.getPlayer().getName().equals(ansname.getString("name"))){
                    // Actualizar datos del usuario
                    PreparedStatement updateuser = connection.prepareStatement("UPDATE `player_uuids` SET `name` = ? WHERE `uuid` = ?;");
                    updateuser.setString(1, e.getPlayer().getName());
                    updateuser.setString(2, e.getPlayer().getUniqueId().toString());
                    updateuser.executeUpdate();
                }
            } else {
                // Registrar usuario en la tabla
                PreparedStatement register = connection.prepareStatement("INSERT INTO `player_uuids` (`name`, `uuid`) VALUES (?, ?);");
                register.setString(1, e.getPlayer().getName());
                register.setString(2, e.getPlayer().getUniqueId().toString());
                register.executeUpdate();
            }
        } catch (SQLException ex){
            ex.printStackTrace();
        }
    }
}
