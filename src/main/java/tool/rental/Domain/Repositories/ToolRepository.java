package tool.rental.Domain.Repositories;

import tool.rental.App.Settings;
import tool.rental.Domain.Entities.Friend;
import tool.rental.Domain.Entities.Rental;
import tool.rental.Domain.Entities.Tool;
import tool.rental.Domain.Entities.User;
import tool.rental.Domain.Infra.DB.DataBase;
import tool.rental.Utils.ToastError;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class ToolRepository {
    public ArrayList<Tool> listAll() throws ToastError {
        try (DataBase db = new DataBase()) {
            PreparedStatement stm = db.connection.prepareStatement(
                    """
                            SELECT
                            	t.id as t__id,
                            	t.brand as t__brand,
                            	t.cost as t__cost,
                            	r.id as r__id,
                            	r.rental_timestamp as r__rental_timestamp,
                            	r.devolution_timestamp as r__devolution_timestamp,
                            	f.id as f__id,
                            	f.name as f__name,
                            	f.phone as f__phone,
                            	f.social_security as f__social_security
                            FROM
                            	TOOL t
                            LEFT JOIN RENTAL r
                            	on	r.tool_id = t.id
                            	and r.devolution_timestamp is null
                            	
                            LEFT JOIN FRIEND f
                            	on f.id = r.friend_id
                            WHERE
                            	t.user_id = ?
                            """
            );
            User user = Settings.getUser();

            stm.setString(1, user.getId());

            ResultSet result = db.executeQuery(stm);

            ArrayList<Tool> tools = new ArrayList<Tool>();

            while (result.next()) {
                Tool tool = new Tool(
                        result.getString("t__id"),
                        result.getString("t__brand"),
                        result.getDouble("t__cost"),
                        user
                );

                String rentalId = result.getString("r__id");
                if (rentalId != null) {
                    Friend friend = new Friend(
                            result.getString("f__id"),
                            result.getString("f__name"),
                            result.getString("f__phone"),
                            result.getString("f__social_security"),
                            user
                    );

                    Rental latestRental = new Rental(
                            result.getString("r__id"),
                            result.getInt("r__rental_timestamp"),
                            result.getInt("r__devolution_timestamp"),
                            friend,
                            tool
                    );
                    tool.setLatestRental(latestRental);
                }

                tools.add(tool);
            }

            tools.trimToSize();

            return tools;

        } catch (SQLException e) {
            throw new ToastError("Falha ao listar as ferramentas. " + e, "Erro de banco de dados.");
        }
    }
}
