package tool.rental.Domain.Infra.DB;

import tool.rental.Utils.ToastError;

import java.nio.file.Paths;
import java.sql.*;
import java.util.Objects;

public class DataBase implements AutoCloseable {
    public Connection connection;
    public final ToastError ConnectionError = new ToastError(
            "Falha ao conectar ao banco de dados.",
            "Erro de banco de dados"
    );

    private enum METHOD {
        UPDATE,
        QUERY
    }


    public DataBase() throws ToastError {
        this.connection = this.getConnection();
        System.out.println("DB Connection open.");

    }

    public ResultSet executeQuery(PreparedStatement statement) throws ToastError {
        return this.executeStatement(statement, METHOD.QUERY);
    }

    public void executeUpdate(PreparedStatement statement) throws ToastError {
        this.executeStatement(statement, METHOD.UPDATE);
    }

    private ResultSet executeStatement(PreparedStatement statement, METHOD method) throws ToastError {
        try {
            System.out.println("Query: " + statement);
            statement.setQueryTimeout(30);

            if (Objects.requireNonNull(method) == METHOD.QUERY) {
                return statement.executeQuery();
            }
            statement.executeUpdate();
            return null;


        } catch (SQLException exception) {
            String message = exception.getMessage();
            if (message.equals("query does not return ResultSet") || message.equals("Query does not return results")) {
                return null;
            }
            System.out.println(message);
            throw this.ConnectionError;

        }
    }

    private Connection getConnection() throws ToastError {
        try {

            if (this.connection != null) {
                return this.connection;
            }

            Class.forName("org.sqlite.JDBC");

            String dir = Paths.get(
                    "src",
                    "main",
                    "java", "tool",
                    "rental",
                    "Domain",
                    "Infra",
                    "DB",
                    "db"
            ).toString();
            return DriverManager.getConnection("jdbc:sqlite:" + dir);

        } catch (SQLException | ClassNotFoundException exception) {
            System.out.println(exception.getMessage());
            throw this.ConnectionError;
        }
    }

    @Override
    public void close() throws ToastError {
        if (this.connection == null) {
            return;
        }

        try {
            this.connection.close();
            this.connection = null;

        } catch (SQLException exception) {
            System.out.println(exception.getMessage());

            throw new ToastError(
                    "Falha ao encerrar sessão do banco de dados.",
                    "Erro de banco de dados."
            );
        } finally {
            System.out.println("DB Connection closed.");
        }
    }
}
