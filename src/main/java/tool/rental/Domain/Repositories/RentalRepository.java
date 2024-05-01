package tool.rental.Domain.Repositories;

import tool.rental.App.Settings;
import tool.rental.Domain.Entities.User;
import tool.rental.Domain.Infra.DB.DataBase;
import tool.rental.Utils.ToastError;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class RentalRepository {
    public int countBorrowedByUser() throws ToastError {
        try (DataBase dataBase = new DataBase()) {
            String query = """
                    SELECT
                    	COUNT(r.id) as total_count
                    FROM RENTAL r
                    LEFT JOIN TOOL t on t.id = r.tool_id
                                        
                    WHERE t.user_id = ? AND r.devolution_timestamp is null
            """;
            PreparedStatement stm = dataBase.connection.prepareStatement(query);
            stm.setString(1, Settings.getUser().getId());

            ResultSet result = dataBase.executeQuery(stm);

            if (!result.next()) {
                return 0;
            }

            return result.getInt("total_count");
        }

        catch (SQLException e) {
            throw new ToastError(e.getMessage(), "Erro de banco de dados.");
        }
    }
}
