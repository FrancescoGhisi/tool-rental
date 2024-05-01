package tool.rental.Domain.Repositories;

import tool.rental.App.Settings;
import tool.rental.Domain.Entities.User;
import tool.rental.Domain.Infra.DB.DataBase;
import tool.rental.Utils.ToastError;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class FriendRepository {
    public int countByUser() throws ToastError {
        try (DataBase dataBase = new DataBase()) {
            String query = "SELECT COUNT(id) as total from FRIEND WHERE user_id = ?";
            PreparedStatement stm = dataBase.connection.prepareStatement(query);
            stm.setString(1, Settings.getUser().getId());

            ResultSet result = dataBase.executeQuery(stm);

            if (!result.next()) {
                return 0;
            }

            return result.getInt("total");


        } catch (SQLException e) {
            throw new ToastError(e.toString(), "Erro de banco de dados.");
        }

    }
}
