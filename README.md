<h1 align="center">
  Banco Digital
</h1>

<p align="center"><em> Bootcamp CDB </em></p>

<!--toc:start-->

- [Instalação](#Instalação)
- [Configuração](#Configuração)
- [Funcionalidades](#Funcionalidades)

<!--toc:end-->

## Instalação

1. Clone o repositório git para sua máquina.
```
git clone https://github.com/PauloRPA/BancoDigital
```
2. Entre na pasta do repositório e rode o comando `mvn spring-boot:run`.
3. Espere o processo de build e após o seu fim a aplicação estará disponível na porta `9999`.

Caso o maven não esteja instalado na sua maquina, é possivel usar o wrapper 'mvnw' disponível na pasta root do repositório.
Use então: `./mvnw spring-boot:run` ou `./mvnw.bat spring-boot:run` caso esteja usando Windows.

## Configuração

A aplicação roda usando o perfil 'dev' por padrão, disponibilizando um banco em memória (H2)

### Variáveis de ambiente

- `SECRET`: (String) Chave usada para assinar os tokens usados pela aplicação.
- `SECURE_COOKIES`: (Boolean) Os cookies usados pela aplicação devem ser "secure" ou não.
- `CEP_SERVICE_URL`: (String) Url serviço de CEP com %s representando o local onde deve ser inserido o CEP.
- `CEP_SERVICE_TIMEOUT`: (String) Tempo maximo de resposta para o serviço de CEP responder.
- `BANK_NAME`: (String) Nome do banco.
- `TIMEZONE`: (String) Timezone a ser usada pela aplicação.
- `ACCESS_TOKEN_EXPIRATION_SEC`: (Integer) Tempo em segundos para o token de acesso expirar.
- `REFRESH_TOKEN_EXPIRATION_SEC`: (Integer) Tempo em segundos para o token de refresh expirar.
- `BD_PORT`: (Integer) Porta na qual a aplicação rodará.
- `LOG_APPLICATION`: (INFO,DEBUG,TRACE,ERROR) Nível de logging para aplicação.
- `LOG_HIBERNATE`: (INFO,DEBUG,TRACE,ERROR) Nível de logging para hibernate.
- `LOG_ROOT`: (INFO,DEBUG,TRACE,ERROR) Nível de logging para o framework.
- `LOG_WEB`: (INFO,DEBUG,TRACE,ERROR) Nível de logging para spring web.
- `LOG_SEC`: (INFO,DEBUG,TRACE,ERROR) Nível de logging para spring security.
- `LOG_TRANSACTION`: (INFO,DEBUG,TRACE,ERROR) Nível de logging para transactions.
- `LOG_JPA`: (INFO,DEBUG,TRACE,ERROR) Nível de logging para JPA.

## Funcionalidades

Todas as funcionalidades pertencentes a esta aplicação foram definidas em:
- [CDB Projeto final Parte 1](doc/Projeto_final_1.pdf)
- [CDB Projeto final Parte 2](doc/Projeto_final_2.pdf)