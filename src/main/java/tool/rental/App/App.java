package tool.rental.App;


import tool.rental.Domain.Infra.DB.DataBase;
import tool.rental.Utils.PresentationFrame;
import tool.rental.Utils.Toast;
import tool.rental.Utils.ToastError;

import java.sql.SQLException;
import java.sql.Statement;

public class App {
    public static void main(String[] args) throws Exception {
        try {
            setupDB();
            runApp();
        }
        catch (Toast exc) {
            exc.display();
            if (exc.getStopRunTime()) {
                throw new Exception("Stop has been called.");
            }
        }

        catch (RuntimeException exc) {
            runApp();
        }
    }

    public static void runApp() throws Toast {
        PresentationFrame firstFrame = Settings.FIRST_FRAME;
        firstFrame.setVisible(true);
    }

    public static void setupDB() throws Toast {
        String query = """
                -- "USER" definition
                                
                CREATE TABLE IF NOT EXISTS "USER" (
                	id TEXT(36) NOT NULL,
                	username TEXT(25) NOT NULL,
                	password TEXT(100) NOT NULL,
                	CONSTRAINT USER_PK PRIMARY KEY (id)
                );
                                
                CREATE UNIQUE INDEX USER_username_IDX ON "USER" (username);
                                
                                
                -- CACHE definition
                                
                CREATE TABLE IF NOT EXISTS CACHE (
                	id TEXT NOT NULL,
                	logged_user_id TEXT NOT NULL,
                	CONSTRAINT CACHE_PK PRIMARY KEY (id),
                	CONSTRAINT CACHE_USER_FK FOREIGN KEY (logged_user_id) REFERENCES "USER"(id) ON DELETE CASCADE
                );
                                
                                
                -- FRIEND definition
                                
                CREATE TABLE IF NOT EXISTS FRIEND (
                	id TEXT NOT NULL,
                	name TEXT NOT NULL,
                	phone TEXT NOT NULL,
                	social_security TEXT NOT NULL,
                	user_id TEXT NOT NULL,
                	CONSTRAINT FRIENDS_PK PRIMARY KEY (id),
                	CONSTRAINT FRIEND_USER_FK FOREIGN KEY (user_id) REFERENCES "USER"(id) ON DELETE CASCADE
                );
                                
                CREATE INDEX FRIEND_user_id_IDX ON FRIEND (user_id);
                                
                                
                -- RENTAL definition
                                
                CREATE TABLE IF NOT EXISTS RENTAL (
                	id TEXT(36) NOT NULL,
                	rental_timestamp INTEGER NOT NULL,
                	devolution_timestamp INTEGER,
                	friend_id TEXT(36) NOT NULL,
                	CONSTRAINT RENTAL_PK PRIMARY KEY (id),
                	CONSTRAINT RENTAL_FRIEND_FK FOREIGN KEY (friend_id) REFERENCES FRIEND(id) ON DELETE CASCADE
                );
                                
                CREATE INDEX RENTAL_friend_id_IDX ON RENTAL (friend_id);
                                
                                
                -- TOOL definition
                                
                CREATE TABLE IF NOT EXISTS TOOL (
                	id TEXT NOT NULL,
                	brand TEXT NOT NULL,
                	cost REAL NOT NULL,
                	user_id TEXT NOT NULL,
                	CONSTRAINT TOOL_PK PRIMARY KEY (id),
                	CONSTRAINT TOOL_USER_FK FOREIGN KEY (user_id) REFERENCES "USER"(id) ON DELETE CASCADE
                );
                                
                CREATE INDEX TOOL_user_id_IDX ON TOOL (user_id);
                """;

        try (DataBase db = new DataBase()) {
            Statement stm = db.connection.createStatement();
            stm.executeQuery(query);

        } catch (SQLException e) {
            if (e.getMessage().equals("query does not return ResultSet")) {
                System.out.println("DB connected.");
                return;
            }

            throw new ToastError("Erro ao iniciar o banco de dados.",
                    "Erro de banco de dados.");
        }
    }
}