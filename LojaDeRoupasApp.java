import java.math.BigDecimal;
import java.sql.*;
import java.util.Scanner;

public class LojaDeRoupasApp {
    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        try (Connection conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/seubanco", "seuusuario",
                "suasenha")) {
            criarTabela(conn); // Certifique-se de chamar esta função apenas uma vez

            while (true) {
                exibirMenu();
                int escolha = scanner.nextInt();
                scanner.nextLine(); // Limpar o buffer

                switch (escolha) {
                    case 1:
                        inserirProduto(conn);
                        break;
                    case 2:
                        excluirProduto(conn);
                        break;
                    case 3:
                        consultarProdutos(conn);
                        break;
                    case 4:
                        alterarProduto(conn);
                        break;
                    case 5:
                        System.out.println("Saindo do programa. Até mais!");
                        System.exit(0);
                    default:
                        System.out.println("Escolha inválida. Tente novamente.");
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao conectar ao banco de dados. Verifique as credenciais e a conexão.");
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Erro inesperado. Encerrando o programa.");
            e.printStackTrace();
        }
    }

    private static void criarTabela(Connection conn) throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS produto (" +
                "id SERIAL PRIMARY KEY," +
                "nome VARCHAR(255) NOT NULL," +
                "tipo VARCHAR(255) NOT NULL," +
                "preco NUMERIC(10, 2) NOT NULL," +
                "quantidade_estoque INTEGER NOT NULL)";
        try (Statement statement = conn.createStatement()) {
            statement.executeUpdate(sql);
        }
    }

    private static void exibirMenu() {
        System.out.println("===== Menu =====");
        System.out.println("1. Inserir Produto");
        System.out.println("2. Excluir Produto");
        System.out.println("3. Consultar Produtos");
        System.out.println("4. Alterar Produto");
        System.out.println("5. Sair");
        System.out.print("Escolha uma opção: ");
    }

    private static void inserirProduto(Connection conn) {
        try {
            System.out.print("Nome do produto: ");
            String nome = scanner.nextLine();

            System.out.print("Tipo do produto: ");
            String tipo = scanner.nextLine();

            BigDecimal preco = null;
            try {
                System.out.print("Preço do produto: ");
                preco = scanner.nextBigDecimal();
            } catch (java.util.InputMismatchException e) {
                System.out.println("Erro: Insira um valor numérico para o preço.");
                scanner.nextLine(); // Limpar o buffer
                return;
            }

            int quantidadeEstoque = 0;
            try {
                System.out.print("Quantidade em estoque: ");
                quantidadeEstoque = scanner.nextInt();
            } catch (java.util.InputMismatchException e) {
                System.out.println("Erro: Insira um valor numérico para a quantidade em estoque.");
                scanner.nextLine(); // Limpar o buffer
                return;
            }

            String sql = "INSERT INTO produto (nome, tipo, preco, quantidade_estoque) VALUES (?, ?, ?, ?)";
            try (PreparedStatement statement = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                statement.setString(1, nome);
                statement.setString(2, tipo);
                statement.setBigDecimal(3, preco);
                statement.setInt(4, quantidadeEstoque);
                statement.executeUpdate();

                try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        System.out.println("Produto inserido com ID: " + generatedKeys.getInt(1));
                    } else {
                        System.out.println("Falha ao obter o ID do produto após inserção.");
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao inserir o produto. Por favor, tente novamente mais tarde.");
            e.printStackTrace();
        }
    }

    private static void excluirProduto(Connection conn) {
        try {
            System.out.print("ID do produto a ser excluído: ");
            int id = scanner.nextInt();

            String sql = "DELETE FROM produto WHERE id = ?";
            try (PreparedStatement statement = conn.prepareStatement(sql)) {
                statement.setInt(1, id);
                int rowsAffected = statement.executeUpdate();

                if (rowsAffected > 0) {
                    System.out.println("Produto removido com sucesso.");
                } else {
                    System.out.println("Nenhum produto encontrado com o ID fornecido.");
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao excluir o produto. Por favor, tente novamente mais tarde.");
            e.printStackTrace();
        } catch (java.util.InputMismatchException e) {
            System.out.println("Erro: Insira um ID válido para excluir o produto.");
            scanner.nextLine(); // Limpar o buffer
        }
    }

    private static void consultarProdutos(Connection conn) {
        try {
            String sql = "SELECT * FROM produto";
            try (Statement statement = conn.createStatement();
                    ResultSet resultSet = statement.executeQuery(sql)) {

                while (resultSet.next()) {
                    int id = resultSet.getInt("id");
                    String nome = resultSet.getString("nome");
                    String tipo = resultSet.getString("tipo");
                    BigDecimal preco = resultSet.getBigDecimal("preco");
                    int quantidadeEstoque = resultSet.getInt("quantidade_estoque");

                    Produto produto = new Produto();
                    produto.setId(id);
                    produto.setNome(nome);
                    produto.setTipo(tipo);
                    produto.setPreco(preco);
                    produto.setQuantidadeEstoque(quantidadeEstoque);

                    System.out.println(produto);
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao consultar produtos. Por favor, tente novamente mais tarde.");
            e.printStackTrace();
        }
    }

    private static void alterarProduto(Connection conn) {
        try {
            System.out.print("ID do produto a ser alterado: ");
            int id = scanner.nextInt();
            scanner.nextLine(); // Limpar o buffer

            // Verifica se o produto existe antes de prosseguir
            if (!produtoExiste(conn, id)) {
                System.out.println("Nenhum produto encontrado com o ID fornecido.");
                return;
            }

            // Obtém as novas informações do produto
            System.out.print("Novo nome do produto: ");
            String novoNome = scanner.nextLine();

            System.out.print("Novo tipo do produto: ");
            String novoTipo = scanner.nextLine();

            BigDecimal novoPreco = null;
            try {
                System.out.print("Novo preço do produto: ");
                novoPreco = scanner.nextBigDecimal();
            } catch (java.util.InputMismatchException e) {
                System.out.println("Erro: Insira um valor numérico para o novo preço.");
                scanner.nextLine(); // Limpar o buffer
                return;
            }

            int novaQuantidadeEstoque = 0;
            try {
                System.out.print("Nova quantidade em estoque: ");
                novaQuantidadeEstoque = scanner.nextInt();
            } catch (java.util.InputMismatchException e) {
                System.out.println("Erro: Insira um valor numérico para a nova quantidade em estoque.");
                scanner.nextLine(); // Limpar o buffer
                return;
            }

            // Atualiza as informações do produto no banco de dados
            String sql = "UPDATE produto SET nome = ?, tipo = ?, preco = ?, quantidade_estoque = ? WHERE id = ?";
            try (PreparedStatement statement = conn.prepareStatement(sql)) {
                statement.setString(1, novoNome);
                statement.setString(2, novoTipo);
                statement.setBigDecimal(3, novoPreco);
                statement.setInt(4, novaQuantidadeEstoque);
                statement.setInt(5, id);

                int rowsAffected = statement.executeUpdate();

                if (rowsAffected > 0) {
                    System.out.println("Produto alterado com sucesso.");
                } else {
                    System.out.println("Nenhum produto encontrado com o ID fornecido.");
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao alterar o produto. Por favor, tente novamente mais tarde.");
            e.printStackTrace();
        } catch (java.util.InputMismatchException e) {
            System.out.println("Erro: Insira um ID válido para alterar o produto.");
            scanner.nextLine(); // Limpar o buffer
        }
    }

    // Método auxiliar para verificar se um produto com o ID fornecido existe
    private static boolean produtoExiste(Connection conn, int id) throws SQLException {
        String sql = "SELECT id FROM produto WHERE id = ?";
        try (PreparedStatement statement = conn.prepareStatement(sql)) {
            statement.setInt(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next();
            }
        }
    }
}