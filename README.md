# Swing Alef

[![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://openjdk.org/)
[![Maven](https://img.shields.io/badge/Maven-3.9+-blue.svg)](https://maven.apache.org/)
[![License](https://img.shields.io/badge/License-MIT-green.svg)](LICENSE)

Uma biblioteca Java Swing completa para facilitar o desenvolvimento de aplicações Desktop.

## 🎯 Objetivo

Facilitar o desenvolvimento de aplicações Java Swing para iniciantes brasileiros, fornecendo componentes prontos para construção de aplicativos com interface gráfica padronizada.

## 📥 Instalação

### Maven

```xml
<dependency>
    <groupId>com.alef</groupId>
    <artifactId>swing-alef</artifactId>
    <version>1.0.0</version>
</dependency>
```

### Manual (JAR)

1. Baixe o arquivo `swing-alef-1.0.0.jar` da pasta `jar/`
2. Adicione ao classpath do seu projeto

## 🛠️ Build

```bash
# Compilar
mvn clean compile

# Gerar JAR
mvn package

# Executar exemplo
mvn exec:java -Dexec.mainClass="exemplo.ExemploAplicativoCompleto"

# Gerar Javadoc
mvn javadoc:javadoc
```

## ✨ Características Principais

- ✅ **Componentes em Português**: Nomes de classes e métodos em português brasileiro
- ✅ **JanelaAbas**: Frame principal com suporte a navegação por abas fecháveis
- ✅ **PainelAbas**: Sistema de abas com indicador de modificações e menus
- ✅ **PainelCrud**: Painel pronto para operações de CRUD
- ✅ **Campos de Formulário**: CampoTexto, CampoCep, CampoCpf, CampoMoeda, etc.
- ✅ **Tabelas Avançadas**: TabelaBase, TabelaEditavel, TabelaPaginada, TabelaAgrupada
- ✅ **Componentes Visuais**: Gráficos, Dashboard, Calendário, Assistentes
- ✅ **DialogoUtil**: Utilitário para diálogos em português
- ✅ **ImagemUtil**: Utilitário para carregar imagens do classpath
- ✅ **Layout Automático**: GridBagLayout para componentes responsivos
- ✅ **Compatibilidade FlatLaf**: Temas modernos e ícones SVG

## 📦 Estrutura de Packages

```
campo/                    # Campos de formulário especializados
├── CampoTexto.java       # Campo de texto com label
├── CampoEmail.java       # Campo de e-mail com validação
├── CampoSenha.java       # Campo com mascaramento
├── CampoCep.java         # CEP com máscara (99999-999)
├── CampoCpf.java         # CPF com validação e máscara
├── CampoCnpj.java        # CNPJ com validação e máscara
├── CampoMoeda.java       # Campo monetário (R$ 1.234,56)
├── CampoData.java        # Campo de data (dd/MM/yyyy)
├── CampoTelefone.java    # Telefone formatado
├── CampoEndereco.java    # Campo composto para endereço
├── CampoComboBox.java    # ComboBox com label
├── CampoCheckBox.java    # CheckBox estilizado
├── CampoRadioGroup.java  # Grupo de RadioButtons
├── CampoNumeroSpinner.java # Spinner para números
├── CampoBusca.java       # Campo de busca com autocomplete
├── CampoArquivo.java     # Campo para seleção de arquivos
├── CampoImagem.java      # Campo para upload de imagens
└── SeletorData.java      # Seletor de data avançado

componente/               # Componentes visuais avançados
├── PainelTemas.java      # Seletor de temas FlatLaf
├── Toast.java            # Notificações toast
├── SobreposicaoCarregamento.java # Overlay de carregamento
├── PainelDashboard.java  # Dashboard com cartões
├── CartaoDashboard.java  # Cartão para métricas
├── CartaoEstatistica.java # Cartão estatístico
├── CartaoKPI.java        # Cartão KPI
├── GraficoBarras.java    # Gráfico de barras
├── GraficoLinhas.java    # Gráfico de linhas
├── GraficoPizza.java     # Gráfico pizza
├── PainelCalendario.java # Calendário visual
├── PainelAssistente.java # Assistente passo-a-passo
├── EtapaAssistente.java  # Etapa do assistente
├── IndicadorEtapas.java  # Indicador de progresso
├── VisualizadorImagem.java # Visualizador de imagens
├── VisualizadorRelatorio.java # Visualizador de relatórios
├── RelatorioImprimivel.java # Relatório imprimível
├── ConstrutorRelatorio.java # Construtor de relatórios
├── TelaSplash.java       # Tela de splash
├── BotaoCarregamento.java # Botão com indicador de carregamento
└── PainelAvatar.java     # Painel com avatar

dialogo/                  # Diálogos e formulários
├── DialogoLogin.java     # Diálogo de login reutilizável
├── DialogoFormulario.java # Diálogo modal para formulários
├── DialogoBusca.java     # Diálogo de busca avançada
├── DialogoCarregamento.java # Diálogo de progresso
├── DialogoConfiguracoes.java # Diálogo de configurações
├── DialogoProgresso.java # Diálogo com barra de progresso
├── DialogoSobre.java     # Diálogo "Sobre"
└── DialogoUtil.java      # Utilitários para diálogos

janela/                   # Janelas principais
├── JanelaAbas.java       # Frame principal com abas
└── PainelAbas.java       # Painel de abas fecháveis

nucleo/                   # Núcleo da arquitetura
├── FabricaDialogo.java   # Factory para diálogos
└── Validavel.java        # Interface para validação

painel/                   # Paineis especializados
└── PainelCrud.java       # Painel CRUD completo

tabela/                   # Componentes de tabela
├── TabelaBase.java       # Tabela base com métodos úteis
├── TabelaEditavel.java   # Tabela editável inline
├── TabelaPaginada.java   # Tabela com paginação
├── TabelaAgrupada.java   # Tabela com agrupamento
├── TabelaModelo.java     # Modelo de tabela customizável
└── TabelaScrollPane.java # ScrollPane para tabelas

util/                     # Utilitários transversais
├── ValidationUtil.java   # Utilitários de validação
├── DataBinder.java       # Binding DTO->Campo
├── ImagemUtil.java       # Carregar imagens do classpath
├── ImageUtil.java        # Alias para ImagemUtil
├── ExportadorUtil.java   # Utilitários de exportação
├── GerenciadorAtalhos.java # Gerenciador de atalhos
├── KeyBindingManager.java # Gerenciador de teclas
└── TarefaAssincrona.java # Execução assíncrona

dto/                      # Data Transfer Objects
└── EnderecoDTO.java      # DTO para endereço

exemplo/                  # Exemplos de uso
├── ExemploAplicativoCompleto.java # Aplicação completa
├── ExemploCamposFormulario.java   # Campos de formulário
├── ExemploPainelClientes.java      # CRUD de clientes
├── ExemploTabelasAvancadas.java    # Tabelas avançadas
├── ExemploGraficos.java            # Gráficos e dashboard
├── ExemploAssistente.java          # Assistente passo-a-passo
├── ExemploDialogos.java            # Diálogos diversos
├── ExemploAtalhos.java             # Sistema de atalhos
├── ExemploCalendario.java          # Calendário visual
├── ExemploMidia.java               # Mídia e imagens
├── ExemploRelatorio.java           # Relatórios
├── ExemploCarregamento.java        # Carregamento e progresso
└── TesteCampos.java                # Teste de campos
```

### 1. Aplicação com Abas Fecháveis (JanelaAbas)

```java
import janela.JanelaAbas;
import componente.PainelTemas;
import javax.swing.UIManager;
import javax.swing.SwingUtilities;

public class MinhaAplicacao {
    public static void main(String[] args) {
        // Usar FlatLaf (opcional, mas recomendado)
        try {
            UIManager.setLookAndFeel("com.formdev.flatlaf.FlatLightLaf");
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> {
            JanelaAbas janela = new JanelaAbas("Minha Aplicação");
            PainelAbas painelAbas = janela.getPainelAbas();

            // Adicionar abas
            painelAbas.adicionarAba("Clientes", new ClientePanel());
            painelAbas.adicionarAba("Produtos", new ProdutoPanel());

            // Aba fixa (não fechável)
            painelAbas.adicionarAbaFixa("Home", null, new HomePanel());

            janela.setVisible(true);
        });
    }
}
```

**Recursos do PainelAbas:**

- ✅ Botão X para fechar cada aba
- ✅ Indicador de modificações (• no título)
- ✅ Menu de contexto (botão direito): Fechar, Fechar Outras, Fechar Todas
- ✅ Atalhos: `Ctrl+W` (fechar), `Ctrl+Tab` (próxima), `Ctrl+Shift+Tab` (anterior)
- ✅ Confirmação ao fechar com alterações não salvas
- ✅ Abas fixas que não podem ser fechadas

```java
// Marcar aba como modificada (mostra •)
frame.getTabbedPane().marcarModificado(componente, true);

// Fechar programaticamente
frame.getTabbedPane().fecharAbaAtual();

// Callback quando aba é fechada
painelAbas.setOnTabClosed(comp -> {
    System.out.println("Aba fechada: " + comp);
});

### 2. Login com DialogoLogin

```java
import dialogo.DialogoLogin;

// Criar diálogo de login
DialogoLogin login = new DialogoLogin(null, "Login do Sistema");

// Configurar autenticador
login.setAutenticador((usuario, senha) -> {
    // Sua lógica de autenticação (ex: banco de dados)
    return usuario.equals("admin") && senha.equals("123");
});

// Mostrar e verificar resultado
if (login.mostrar()) {
    // Login bem sucedido
    System.out.println("Usuário: " + login.getUsuario());
    new JanelaAbas("Sistema").setVisible(true);
} else {
    // Login cancelado
    System.exit(0);
}
```

**Recursos do DialogoLogin:**

- ✅ Campos CampoTexto e CampoSenha integrados
- ✅ Autenticador configurável via `BiFunction<String, String, Boolean>`
- ✅ Atalhos: `Enter` (entrar), `ESC` (cancelar)
- ✅ Mensagens de erro integradas
- ✅ Labels e textos customizáveis

```java
import janela.JanelaAbas;
import javax.swing.UIManager;
import javax.swing.SwingUtilities;

public class MinhaAplicacao {
    public static void main(String[] args) {
        // Usar FlatLaf (opcional, mas recomendado)
        try {
            UIManager.setLookAndFeel("com.formdev.flatlaf.FlatLightLaf");
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> {
            JanelaAbas janela = new JanelaAbas("Minha Aplicação");
            janela.setVisible(true);
        });
    }
}
```

### 4. Criar um Painel CRUD

```java
import painel.PainelCrud;
import janela.JanelaAbas;

// Dentro da aplicação
JanelaAbas janela = new JanelaAbas("Minha App");
PainelAbas painelAbas = janela.getPainelAbas();

PainelCrud painel = new PainelCrud("Gestão de Clientes");
painel.definirColunas(new String[]{"ID", "Nome", "Email"});

// Adicionar dados
painel.adicionarLinha(new Object[]{1, "João", "joao@email.com"});

// Adicionar botões
painel.adicionarBotao("Novo", () -> criarNovoCliente());
painel.adicionarBotao("Editar", () -> editarCliente());
painel.adicionarBotao("Deletar", () -> deletarCliente());

painelAbas.adicionarAba("Clientes", painel);
```

### 5. Criar um Formulário com Diálogo

```java
import dialogo.DialogoFormulario;
import campo.CampoTexto;
import campo.CampoEmail;

DialogoFormulario dialog = new DialogoFormulario(janela, "Novo Cliente");

CampoTexto campoNome = new CampoTexto("Nome");
CampoEmail campoEmail = new CampoEmail("Email");

dialog.adicionarCampo(campoNome);
dialog.adicionarCampo(campoEmail);

dialog.mostrarDialogo(() -> {
    String nome = campoNome.getValue();
    String email = campoEmail.getValue();

    if (campoNome.isValido() && campoEmail.isValido()) {
        salvarCliente(nome, email);
    }
});
```

### 6. Criar um Formulário Customizado

```java
import componente.PainelFormulario; // ou similar, verificar o nome exato
import campo.CampoTexto;
import campo.CampoNumeroSpinner;
import campo.CampoEmail;

PainelFormulario formulario = new PainelFormulario();

CampoTexto campoNome = new CampoTexto("Nome");
CampoEmail campoEmail = new CampoEmail("Email");
CampoNumeroSpinner campoIdade = new CampoNumeroSpinner("Idade", 0, 0, 150, 1);

formulario.adicionarCampo(campoNome);
formulario.adicionarCampo(campoEmail);
formulario.adicionarCampo(campoIdade);

formulario.adicionarBotao("Salvar", () -> {
    // Ação ao clicar no botão
    System.out.println("Nome: " + campoNome.getValue());
    System.out.println("Email: " + campoEmail.getValue());
});
```

## 📚 Componentes Disponíveis

### JanelaAbas

Frame principal com suporte a abas fecháveis e navegação.

**Métodos principais:**

- `getPainelAbas()` - Obtém o painel de abas
- `setVisible(boolean)` - Exibe/oculta a janela

### PainelCrud

Painel pronto para operações CRUD com tabela e barra de ferramentas.

**Métodos principais:**

- `adicionarBotao(String texto, Runnable acao)` - Adiciona botão à barra
- `definirColunas(String[] colunas)` - Define colunas da tabela
- `adicionarLinha(Object[] dados)` - Adiciona linha
- `removerLinhaAtual()` - Remove linha selecionada
- `obterLinhaAtual()` - Obtém dados da linha selecionada
- `limparTabela()` - Limpa todas as linhas

### TabelaBase

Tabela com métodos úteis para CRUD.

**Métodos principais:**

- `definirColunas(String[] colunas)`
- `adicionarLinha(Object[] dados)`
- `removerLinha(int linha)`
- `removerLinhaAtual()`
- `obterValor(int linha, int coluna)`
- `definirValor(int linha, int coluna, Object valor)`
- `obterLinhaAtual()`
- `limpar()`

### Campos de Formulário

#### CampoTexto

```java
CampoTexto campo = new CampoTexto("Seu Rótulo");
String valor = campo.getValue();
campo.setValue("novo valor");
boolean valido = campo.isValido(); // verifica se não está vazio
```

#### CampoEmail

```java
CampoEmail campo = new CampoEmail("E-mail");
String email = campo.getValue();
campo.setValue("user@example.com");
boolean valido = campo.isValido(); // valida formato de e-mail
// Validação visual em tempo real: borda verde se válido, vermelha se inválido
```

#### CampoSenha

```java
CampoSenha campo = new CampoSenha("Senha");
String senha = campo.getValue();
boolean segura = campo.isSegura(8); // verifica se tem no mínimo 8 caracteres
```

#### CampoNumeroSpinner

```java
// Construtor simples (0 a 999999, passo 1)
CampoNumeroSpinner campo = new CampoNumeroSpinner("Idade");

// Construtor completo (valor inicial, min, max, passo)
CampoNumeroSpinner campo = new CampoNumeroSpinner("Quantidade", 10, 1, 100, 5);
int valor = campo.getValue();
```

#### CampoCep

```java
CampoCep campo = new CampoCep();
String cep = campo.getValue(); // formato: 99999-999
boolean valido = campo.isValido(); // verifica formato
```

#### CampoTelefone

```java
// Telefone fixo: (99) 9999-9999
CampoTelefone telefoneFixo = new CampoTelefone("Telefone", false);

// Celular: (99) 99999-9999
CampoTelefone celular = new CampoTelefone("Celular", true);
```

#### CampoData

```java
CampoData campo = new CampoData("Data de Nascimento");
Date data = campo.getValue();
campo.setValue(new Date());
String dataStr = campo.getValueAsString(); // formato: dd/MM/yyyy
campo.setValueFromString("25/12/2025");
```

#### CampoEndereco

```java
CampoEndereco endereco = new CampoEndereco();
// Campos compostos: CEP, logradouro, número, bairro, cidade, país
// Inclui botão "Buscar CEP" para integração futura com API
```

### Componentes Visuais Avançados

#### Dashboard e Cartões

```java
import componente.PainelDashboard;
import componente.CartaoDashboard;
import componente.CartaoEstatistica;
import componente.CartaoKPI;

// Criar dashboard
PainelDashboard dashboard = new PainelDashboard();

// Adicionar cartões
dashboard.adicionarCartao(new CartaoDashboard("Clientes", "1,234", "↑ 12%"));
dashboard.adicionarCartao(new CartaoEstatistica("Vendas", 45678.90));
dashboard.adicionarCartao(new CartaoKPI("Meta", 85.5, "%"));
```

#### Gráficos

```java
import componente.GraficoBarras;
import componente.GraficoPizza;
import componente.GraficoLinhas;

// Gráfico de barras
GraficoBarras grafico = new GraficoBarras();
grafico.adicionarSerie("Vendas", new double[]{100, 200, 150, 300});

// Gráfico pizza
GraficoPizza pizza = new GraficoPizza();
pizza.adicionarFatia("Produto A", 40);
pizza.adicionarFatia("Produto B", 60);
```

#### Calendário Visual

```java
import componente.PainelCalendario;

PainelCalendario calendario = new PainelCalendario();
calendario.setDataSelecionada(new Date());
calendario.setOnDataSelecionada(data -> {
    System.out.println("Data selecionada: " + data);
});
```

#### Assistente Passo-a-Passo

```java
import componente.PainelAssistente;
import componente.EtapaAssistente;

PainelAssistente assistente = new PainelAssistente();

// Adicionar etapas
assistente.adicionarEtapa(new EtapaAssistente("Passo 1", painelPasso1));
assistente.adicionarEtapa(new EtapaAssistente("Passo 2", painelPasso2));

// Navegação
assistente.proximo();
assistente.anterior();
```

#### Relatórios e Impressão

```java
import componente.RelatorioImprimivel;
import componente.ConstrutorRelatorio;
import componente.VisualizadorRelatorio;

// Criar construtor de relatório
ConstrutorRelatorio construtor = new ConstrutorRelatorio("Relatório de Vendas");

// Adicionar seções
construtor.adicionarCabecalho("Empresa XYZ", "Relatório Mensal");
construtor.adicionarSecao("Período", "Janeiro 2025");
construtor.adicionarTabela("Produtos", new String[]{"Nome", "Quantidade", "Valor"},
    Arrays.asList(
        new Object[]{"Produto A", 10, 150.00},
        new Object[]{"Produto B", 5, 75.00}
    ));
construtor.adicionarRodape("Gerado em: " + new Date());

// Criar relatório imprimível
RelatorioImprimivel relatorio = construtor.construir();

// Visualizar
VisualizadorRelatorio visualizador = new VisualizadorRelatorio(relatorio);
visualizador.setVisible(true);

// Ou imprimir diretamente
relatorio.imprimir();
```

### Utilitários

#### DialogUtil ⭐ NOVO

Diálogos em português compatíveis com FlatLaf:

```java
import dialogo.DialogoUtil;

// Confirmação simples (Sim/Não)
if (DialogUtil.confirmar(parent, "Deseja continuar?")) {
    // Usuário clicou Sim
}

// Confirmação de exclusão
if (DialogUtil.confirmarExclusao(parent, "cliente")) {
    clienteRepository.excluir(cliente);
}

// Confirmação com Cancelar (Sim/Não/Cancelar)
int opcao = DialogUtil.confirmarComCancelar(parent, "Salvar alterações?");
// 0 = Sim, 1 = Não, 2 = Cancelar

// Mensagens informativas
DialogUtil.info(parent, "Operação concluída!");
DialogUtil.aviso(parent, "Campo obrigatório não preenchido");
DialogUtil.erro(parent, "Falha ao conectar no banco");

// Input de texto
String nome = DialogUtil.input(parent, "Digite o nome:");

// Seleção de opções
String[] opcoes = {"Opção A", "Opção B", "Opção C"};
String escolha = DialogUtil.selecionar(parent, "Escolha:", opcoes);
```

#### ImageUtil ⭐ NOVO

Utilitário para carregar imagens do classpath:

```java
import util.ImagemUtil;

// Carregar imagem do classpath (pasta resources)
Image imagem = ImagemUtil.carregarImagem("/icone.png");

// Carregar como ImageIcon
ImageIcon icone = ImagemUtil.carregarIcone("/logo.png");

// Redimensionar mantendo proporção
Image redimensionada = ImagemUtil.redimensionarProporcional(imagem, 64, 64);

// Verificar se imagem existe
if (ImagemUtil.existe("/foto.jpg")) {
    // ...
}
```

## 🎨 Temas e Customização

A biblioteca foi refatorada para trabalhar perfeitamente com **FlatLaf**. Para usar temas:

```java
// Light Theme
UIManager.setLookAndFeel("com.formdev.flatlaf.FlatLightLaf");

// Dark Theme
UIManager.setLookAndFeel("com.formdev.flatlaf.FlatDarkLaf");

// Intellij Theme
UIManager.setLookAndFeel("com.formdev.flatlaf.FlatIntelliJLaf");
```

## 📋 Exemplo Completo

Veja `ExemploAplicativoCompleto.java` para um exemplo funcional completo de:

- Criação de JanelaAbas
- Configuração de PainelCrud
- Diálogos para criar, editar e deletar clientes
- Integração com Toast e DialogoUtil
- Sistema de abas fecháveis
- Dashboard com cartões e gráficos

## 🔧 Dependência FlatLaf (Recomendado)

Para melhor aparência, use FlatLaf:

```xml
<dependency>
    <groupId>com.formdev</groupId>
    <artifactId>flatlaf</artifactId>
    <version>3.5.4</version>
</dependency>
```

## 📝 Notas Importantes

- **Compatibilidade**: Java 8, 11, 17, 21
- **Build Tool**: Maven 3.9+
- **FlatLaf**: Não é obrigatório, mas recomendado para melhor aparência
- **Componentes em Português**: A maioria dos nomes segue convenção em português
- **Sem Conflitos**: Todos os componentes permitem que o look and feel gerencie o visual

## 📁 Estrutura do Projeto (Maven)

```
swing-alef/
├── pom.xml
├── src/
│   ├── main/
│   │   └── java/
│   │       ├── campo/        # Campos de formulário
│   │       ├── componente/   # Componentes visuais
│   │       ├── dialogo/      # Diálogos e formulários
│   │       ├── dto/          # Data Transfer Objects
│   │       ├── exemplo/      # Exemplos de uso
│   │       ├── janela/       # Janelas principais
│   │       ├── nucleo/       # Núcleo da arquitetura
│   │       ├── painel/       # Paineis especializados
│   │       ├── tabela/       # Componentes de tabela
│   │       └── util/         # Utilitários
│   └── test/
│       └── java/
└── target/
    └── swing-alef-1.0.0.jar
```

## 🐛 Troubleshooting

### Os componentes parecem desalinhados

- Certifique-se de estar usando GridBagLayout
- Use painéis do pacote `componente` como base

### Cores estranhas com FlatLaf

- Limpe o cache de compilação
- Reinicie a aplicação
- Verifique se o FlatLaf foi configurado antes de criar componentes

### Componentes muito pequenos

- Os tamanhos padrão já estão configurados
- Customize através de `UIManager` ou sobrescrevendo métodos

## 📄 Licença

MIT License

Copyright (c) 2025 Álef Ismael de Souza

## 👨‍💻 Contribuições

Contribuições são bem-vindas! Por favor, abra uma issue ou pull request.

## 🙏 Agradecimentos

Desenvolvido com o objetivo de facilitar o aprendizado de Java Swing para desenvolvedores brasileiros.
