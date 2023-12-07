import java.math.BigDecimal;

public class Produto {
    private int id;
    private String nome;
    private String tipo;
    private BigDecimal preco;
    private int quantidadeEstoque;

    // Construtores
    public Produto() {
    }

    public Produto(String nome, String tipo, BigDecimal preco, int quantidadeEstoque) {
        this.nome = nome;
        this.tipo = tipo;
        this.preco = preco;
        this.quantidadeEstoque = quantidadeEstoque;
    }

    // Getters e Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public BigDecimal getPreco() {
        return preco;
    }

    public void setPreco(BigDecimal preco) {
        this.preco = preco;
    }

    public int getQuantidadeEstoque() {
        return quantidadeEstoque;
    }

    public void setQuantidadeEstoque(int quantidadeEstoque) {
        this.quantidadeEstoque = quantidadeEstoque;
    }

    // Método toString
    @Override
    public String toString() {
        return "ID: " + id +
                ", Nome: " + nome +
                ", Tipo: " + tipo +
                ", Preço: " + preco +
                ", Quantidade em Estoque: " + quantidadeEstoque;
    }
}