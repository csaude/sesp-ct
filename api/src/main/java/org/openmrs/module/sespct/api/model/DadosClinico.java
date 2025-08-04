package org.openmrs.module.sespct.api.model;

import javax.persistence.*;

@Entity
@Table(name = "sespct_dados_clinico")
public class DadosClinico {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "dados_clinico_id")
	private Integer id;
	
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "pedido_id", nullable = false, unique = true)
	private Pedido pedido;
	
	@Column(name = "nome", length = 200)
	private String nome;
	
	@Column(name = "categoria_profissional", length = 100)
	private String categoriaProfissional;
	
	@Column(name = "telefone", length = 20)
	private String telefone;
	
	@Column(name = "email", length = 100)
	private String email;
	
	// --- Getters and Setters ---
	
	public String getNome() {
		return nome;
	}
	
	public void setNome(String nome) {
		this.nome = nome;
	}
	
	public Pedido getPedido() {
		return pedido;
	}
	
	public void setPedido(Pedido pedido) {
		this.pedido = pedido;
	}
	
	public String getCategoriaProfissional() {
		return categoriaProfissional;
	}
	
	public void setCategoriaProfissional(String categoriaProfissional) {
		this.categoriaProfissional = categoriaProfissional;
	}
	
	public String getTelefone() {
		return telefone;
	}
	
	public void setTelefone(String telefone) {
		this.telefone = telefone;
	}
	
	public String getEmail() {
		return email;
	}
	
	public void setEmail(String email) {
		this.email = email;
	}
	
	@Override
	public String toString() {
		return "DadosClinico{" + "nome='" + nome + '\'' + ", categoriaProfissional='" + categoriaProfissional + '\''
		        + ", telefone='" + telefone + '\'' + ", email='" + email + '\'' + '}';
	}
}
