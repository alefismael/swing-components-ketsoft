# Swing Alef

[![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://openjdk.org/)
[![Maven](https://img.shields.io/badge/Maven-3.9+-blue.svg)](https://maven.apache.org/)
[![License](https://img.shields.io/badge/License-MIT-green.svg)](LICENSE)

Uma biblioteca Java Swing completa para facilitar o desenvolvimento de aplicações Desktop com padrão de **CRUD** (Create, Read, Update, Delete).

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

1. Baixe o arquivo `swing-alef-1.0.0.jar` da pasta `target/`
2. Adicione ao classpath do seu projeto

## 🛠️ Build

```bash
# Compilar
mvn clean compile

# Gerar JAR
mvn package

# Executar exemplo
mvn exec:java

# Gerar Javadoc
mvn javadoc:javadoc
```

## ✨ Características Principais

- ✅ **Componentes Base**: Sem conflitos com temas como FlatLaf
- ✅ **BaseFrame**: Frame principal com suporte a navegação por abas
- ✅ **PainelCRUD**: Painel pronto para operações de CRUD
- ✅ **BaseFormularioDialog**: Diálogos modais para entrada de dados
- ✅ **Campos de Formulário em Português**: CampoTexto, CampoNumero, CampoCep, CampoSenha
- ✅ **Tabelas com Suporte a CRUD**: BaseTable com métodos úteis
- ✅ **Layout Automático**: GridBagLayout para componentes responsivos

## 📦 Estrutura de Packages

```
base/
  ├── BaseButton.java           - Botão base
  ├── BaseLabel.java            - Label base
  ├── BasePanel.java            - Painel base com GridBag
  ├── BaseFormPanel.java        - Painel para formulários
  ├── BaseTextField.java        - Campo de texto base
  ├── BaseSpinner.java          - Spinner para números
  ├── BaseFrame.java            - Frame principal com suporte F11
  ├── BaseCrudPanel.java        - Painel pronto para CRUD
  ├── BaseFormularioDialog.java - Diálogo para formulários
  └── BaseNavigationBar.java    - Barra de navegação

crud/
  ├── GenericCrudPanel.java     - Painel CRUD genérico com hooks
  ├── CrudDialogFactory.java    - Factory para criação de diálogos
  ├── CrudTableModel.java       - Model genérico para tabelas
  └── CrudDialogPresets.java    - Presets para diálogos CRUD

components/
  ├── CampoForm.java            - Classe abstrata base para campos
  ├── CampoTexto.java           - Campo de texto com label
  ├── CampoEmail.java           - Campo de e-mail com validação visual
  ├── CampoSenha.java           - Campo com mascaramento de senha
  ├── CampoNumeroSpinner.java   - Campo para números
  ├── CampoCep.java             - Campo específico para CEP (99999-999)
  ├── CampoTelefone.java        - Campo de telefone formatado
  ├── CampoData.java            - Campo de data (dd/MM/yyyy)
  └── CampoEndereco.java        - Campo composto para endereço

table/
  └── BaseTable.java            - Tabela base com CRUD

ui/
  ├── Toast.java                - Notificações toast
  └── LoadingOverlay.java       - Overlay de carregamento

util/
  ├── ValidationUtil.java       - Utilitários de validação
  └── DataBinder.java           - Binding de dados DTO->Campo

example/
  └── ExemploAplicativoClientes.java - Exemplo completo de uso
```

## 🚀 Como Usar

### 1. Aplicação Básica com BaseFrame

```java
import base.BaseFrame;
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
            BaseFrame frame = new BaseFrame("Minha Aplicação");
            frame.setVisible(true);
        });
    }
}
```

### 2. Criar um Painel CRUD

```java
import base.PainelCRUD;
import base.BaseFrame;

// Dentro da aplicação
BaseFrame frame = new BaseFrame("Minha App");

PainelCRUD painel = new PainelCRUD("Gestão de Clientes");
painel.definirColunas(new String[]{"ID", "Nome", "Email"});

// Adicionar dados
painel.adicionarLinha(new Object[]{1, "João", "joao@email.com"});

// Adicionar botões
painel.adicionarBotao("Novo", () -> criarNovoCliente());
painel.adicionarBotao("Editar", () -> editarCliente());
painel.adicionarBotao("Deletar", () -> deletarCliente());

frame.adicionarPainel("Clientes", painel);
frame.exibirPainel("Clientes");
```

### 3. Criar um Formulário com Diálogo

```java
import base.BaseFormularioDialog;
import components.CampoTexto;
import components.CampoEmail;

BaseFormularioDialog dialog = new BaseFormularioDialog(frame, "Novo Cliente");

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

### 4. Criar um Formulário Customizado

```java
import base.BaseFormPanel;
import components.CampoTexto;
import components.CampoNumeroSpinner;
import components.CampoEmail;

BaseFormPanel formulario = new BaseFormPanel();

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

### BaseFrame

Frame principal com suporte a múltiplos painéis com CardLayout.

**Métodos principais:**

- `adicionarPainel(String nome, JPanel painel)` - Adiciona um painel
- `exibirPainel(String nome)` - Exibe um painel específico
- `removerPainel(String nome)` - Remove um painel
- `obterPainel(String nome)` - Obtém um painel existente

### PainelCRUD

Painel pronto para operações CRUD com tabela e barra de ferramentas.

**Métodos principais:**

- `adicionarBotao(String texto, Runnable acao)` - Adiciona botão à barra
- `definirColunas(String[] colunas)` - Define colunas da tabela
- `adicionarLinha(Object[] dados)` - Adiciona linha
- `removerLinhaAtual()` - Remove linha selecionada
- `obterLinhaAtual()` - Obtém dados da linha selecionada
- `limparTabela()` - Limpa todas as linhas

### BaseTable

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

Veja `ExemploAplicativoClientes.java` para um exemplo funcional completo de:

- Criação de BaseFrame
- Configuração de PainelCRUD
- Diálogos para criar, editar e deletar clientes

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
│   │       ├── base/
│   │       ├── components/
│   │       ├── crud/
│   │       ├── table/
│   │       ├── ui/
│   │       └── util/
│   └── test/
│       └── java/
└── target/
    └── swing-alef-1.0.0.jar
```

## 🐛 Troubleshooting

### Os componentes parecem desalinhados

- Certifique-se de estar usando GridBagLayout
- Use `BasePanel` ou `BaseFormPanel` como base

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
