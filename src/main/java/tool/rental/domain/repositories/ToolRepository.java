package tool.rental.domain.repositories;

import tool.rental.app.Settings;
import tool.rental.domain.dao.CountIdAndSumCostDAO;
import tool.rental.domain.entities.Friend;
import tool.rental.domain.entities.Rental;
import tool.rental.domain.entities.Tool;
import tool.rental.domain.entities.User;
import tool.rental.domain.infra.db.DataBase;
import tool.rental.utils.ToastError;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.UUID;

/**
 * Represents a repository for tools in the tool rental system.
 */
public class ToolRepository {

    /**
     * Sets the current rental of a tool based on the provided result set.
     *
     * @param tool the tool to set the current rental for
     * @param result the result set containing the rental information
     * @throws SQLException if an error occurs while processing the result set
     */
    private void setCurrentRentalToTool(Tool tool, ResultSet result) throws SQLException {
        String rentalId = result.getString("r__id");
        if (rentalId == null) {
            return;
        }

        Friend friend = new Friend(
                result.getString("f__id"),
                result.getString("f__name"),
                result.getString("f__phone"),
                result.getString("f__social_security"),
                Settings.getUser()
        );

        Rental currentRental = new Rental(
                result.getString("r__id"),
                result.getLong("r__rental_timestamp"),
                result.getLong("r__devolution_timestamp"),
                friend,
                tool
        );
        tool.setCurrentRental(currentRental);

    }

    /**
     * Lists all tools, optionally filtering by rented only.
     *
     * @param rentedOnly whether to only include rented tools
     * @return a list of tools
     * @throws ToastError if an error occurs while listing the tools
     */
    public ArrayList<Tool> listAll(boolean rentedOnly) throws ToastError {
        try (DataBase db = new DataBase()) {
            String query = """
                    SELECT
                    	t.id as t__id,
                    	t.brand as t__brand,
                    	t.name as t__name,
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
                    	t.user_id =?
                    """;
            if (rentedOnly) {
                query += " and f__id is not null";
            }

            PreparedStatement stm = db.connection.prepareStatement(query);
            User user = Settings.getUser();

            stm.setString(1, user.getId());

            ResultSet result = db.executeQuery(stm);

            ArrayList<Tool> tools = new ArrayList<Tool>();

            while (result.next()) {
                Tool tool = new Tool(
                        result.getString("t__id"),
                        result.getString("t__brand"),
                        result.getString("t__name"),
                        result.getDouble("t__cost"),
                        user
                );

                this.setCurrentRentalToTool(tool, result);
                tools.add(tool);
            }

            tools.trimToSize();

            return tools;

        } catch (SQLException e) {
            throw new ToastError("Falha ao listar as ferramentas. " + e, "Erro de banco de dados.");
        }
    }

    /**
     * Counts and sums the cost of tools by user.
     *
     * @return a CountIdAndSumCostDAO object containing the count and sum
     * @throws ToastError if an error occurs while counting and summing
     */
    public CountIdAndSumCostDAO countAndSumCostByUser() throws ToastError {
        try (DataBase dataBase = new DataBase()) {
            String query = "SELECT COUNT(id) as total_count, SUM(cost) as total_cost from TOOL WHERE user_id = ?";
            PreparedStatement stm = dataBase.connection.prepareStatement(query);
            stm.setString(1, Settings.getUser().getId());

            ResultSet result = dataBase.executeQuery(stm);

            if (!result.next()) {
                return new CountIdAndSumCostDAO(0, 0.0);
            }

            return new CountIdAndSumCostDAO(
                    result.getInt("total_count"),
                    result.getDouble("total_cost")
            );

        } catch (SQLException e) {
            throw new ToastError(e.toString(), "Erro de banco de dados.");
        }
    }

    /**
     * Gets a tool by its ID.
     *
     * @param toolId the ID of the tool to retrieve
     * @return the tool, or null if not found
     * @throws ToastError if an error occurs while retrieving the tool
     */
    public Tool getById(String toolId) throws ToastError {
        try (DataBase db = new DataBase()) {
            String query = """
                        SELECT
                            t.id,
                            t.brand,
                            t.name,
                            t.cost,
                            u.id as u__id,
                            u.username as u__username,
                            u.has_mock as u__has_mock,
                            r.id as r__id,
                            r.rental_timestamp as r__rental_timestamp,
                            r.devolution_timestamp as r__devolution_timestamp,
                            f.id as f__id,
                            f.name as f__name,
                            f.phone as f__phone,
                            f.social_security as f__social_security
                            
                        FROM TOOL t
                        LEFT JOIN USER u on t.user_id = u.id
                        LEFT JOIN RENTAL r on r.tool_id = t.id and r.devolution_timestamp is null
                        LEFT JOIN FRIEND f on f.id = r.friend_id
                        WHERE t.id = ?
                    """;

            PreparedStatement stm = db.connection.prepareStatement(query);
            stm.setString(1, toolId);

            ResultSet result = db.executeQuery(stm);
            if (!result.next()) {
                return null;
            }

            User user = new User(
                    result.getString("u__id"),
                    result.getString("u__username"),
                    false,
                    result.getBoolean("u__has_mock")
            );

            Tool tool = new Tool(
                    result.getString("id"),
                    result.getString("brand"),
                    result.getString("name"),
                    result.getDouble("cost"),
                    user
            );

            this.setCurrentRentalToTool(tool, result);

            return tool;

        } catch (SQLException e) {
            throw new ToastError(e.toString(), "Erro de banco de dados.");
        }
    }

    /**
     * Creates a new tool.
     *
     * @param brand the brand of the tool
     * @param name the name of the tool
     * @param cost the cost of the tool
     * @return the created tool
     * @throws ToastError if an error occurs while creating the tool
     */
    public Tool createTool(String brand, String name, double cost) throws ToastError {
        try (DataBase db = new DataBase()) {
            String id = UUID.randomUUID().toString();


            PreparedStatement stm = db.connection.prepareStatement("INSERT INTO TOOL VALUES(?, ?, ?, ?, ?)");
            stm.setString(1, id);
            stm.setString(2, brand);
            stm.setString(3, name);
            stm.setDouble(4, cost);
            stm.setString(5, Settings.getUser().getId());

            db.executeUpdate(stm);
            return new Tool(id, brand, name, cost, Settings.getUser());

        } catch (SQLException exc) {
            System.out.println(exc.getMessage());
            throw new ToastError(
                    "Não foi possível cadastrar a ferramenta devido a um erro com banco de dados",
                    "Erro de banco de dados"
            );
        }
    }

    /**
     * Checks if a tool exists by name and brand.
     *
     * @param name the name of the tool
     * @param brand the brand of the tool
     * @return true if the tool exists, false otherwise
     * @throws ToastError if an error occurs while checking for the tool
     */
    public boolean existsByNameAndBrand(String name, String brand) throws ToastError {
        try (DataBase db = new DataBase()) {
            PreparedStatement stm = db.connection.prepareStatement(
                    "SELECT id FROM TOOL WHERE user_id = ? and name = ? and brand = ?"
            );
            stm.setString(1, Settings.getUser().getId());
            stm.setString(2, name);
            stm.setString(3, brand);
            ResultSet result = db.executeQuery(stm);
            return result.next();

        } catch (SQLException exc) {
            System.out.println(exc.getMessage());
            throw new ToastError(
                    "Não foi possível verificar se o usuário existe devido a um erro com o banco de dados.",
                    "Erro de banco de dados."
            );
        }
    }

    /**
     * Checks if a tool is rented.
     *
     * @param toolId the ID of the tool to check
     * @return true if the tool is rented, false otherwise
     * @throws ToastError if an error occurs while checking for the rental
     */
    public boolean isToolRented(String toolId) throws ToastError {
        try (DataBase db = new DataBase()) {
            String query = """
                        SELECT
                            tool_id
                         FROM 
                            RENTAL
                         WHERE
                            tool_id = ? AND devolution_timestamp is null
                         ORDER BY 
                                tool_id DESC 
                    """;
            PreparedStatement stm = db.connection.prepareStatement(query);
            stm.setString(1, toolId);
            ResultSet result = db.executeQuery(stm);

            return result.next();

        } catch (SQLException e) {
            throw new ToastError(e.toString(), "Erro de banco de dados");
        }
    }

    /**
     * Checks if any tool is rented by a friend.
     *
     * @param friend the friend to check
     * @return true if any tool is rented by the friend, false otherwise
     * @throws ToastError if an error occurs while checking for the rental
     */
    public boolean isAnyToolRentedByFriend(Friend friend) throws ToastError {
        try (DataBase db = new DataBase()) {
            String query = """
                        SELECT 
                            friend_id
                        FROM
                            RENTAL
                        WHERE
                            friend_id = ? AND devolution_timestamp is null
                    """;
            PreparedStatement stm = db.connection.prepareStatement(query);
            stm.setString(1, friend.getId());
            ResultSet result = db.executeQuery(stm);

            return result.next();

        } catch (SQLException e) {
            throw new ToastError(e.getMessage(), "Erro de banco de dados");
        }
    }

    /**
     * Deletes a tool.
     *
     * @param tool the tool to delete
     * @throws ToastError if an error occurs while deleting the tool
     */
    public void deleteTool(Tool tool) throws ToastError {
        try (DataBase db = new DataBase()) {
            PreparedStatement stm = db.connection.prepareStatement("DELETE FROM TOOL WHERE id = ?");
            stm.setString(1, tool.getId());
            db.executeUpdate(stm);

        } catch (SQLException e) {
            System.out.println(e.getMessage());
            throw new ToastError("Não foi possível deletar a ferramenta selecionada.",
                    "Erro de banco de dados");
        }
    }

    /**
     * Updates a tool.
     *
     * @param tool the tool to update
     * @param brand the new brand of the tool
     * @param name the new name of the tool
     * @param cost the new cost of the tool
     * @throws ToastError if an error occurs while updating the tool
     */
    public void updateTool(Tool tool, String brand, String name, double cost) throws ToastError {
        try (DataBase db = new DataBase()) {
            PreparedStatement stm = db.connection.prepareStatement(
                    "UPDATE TOOL SET brand = ?, name = ?, cost = ? WHERE id = ? ");
            stm.setString(1, brand);
            stm.setString(2, name);
            stm.setDouble(3, cost);
            stm.setString(4, tool.getId());

            db.executeUpdate(stm);

        } catch (SQLException e) {
            System.out.println(e.getMessage());
            throw new ToastError("Não foi possível atualizar a ferramenta",
                    "Erro de banco de dados.");
        }
    }
}
