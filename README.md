# Notfis EDI Parser

## Descrição

O Notfis EDI Parser é uma aplicação que analisa e processa arquivos Notfis EDI, convertendo-os em uma estrutura JSON para facilitar a leitura e integração com outros sistemas. O objetivo deste projeto é simplificar a extração e manipulação de dados de arquivos Notfis em formatos personalizados para as versões 3.1 e 5.0, atendendo a requisitos específicos de sistemas legados e personalizados.

## Funcionalidades

- **Análise de Arquivos Notfis EDI**
  - Suporte para versões 3.1 e 5.0.
  - Estrutura flexível que se adapta a diferentes layouts não padronizados.

- **Configuração Dinâmica**
  - Permite a definição de campos como nome, formato, tamanho, posição e obrigatoriedade através de arquivos de configuração JSON.

- **Validação**
  - Verifica se todos os campos obrigatórios estão presentes e se seguem as especificações configuradas.

## Pré-requisitos

- **Java 11+**
- **Maven 3.6.3+**

## Arquivos de Configuração

Os arquivos de configuração JSON definem os campos necessários para suas respectivas versões do Notfis:

- `notfis31.json`
- `notfis50.json`

Cada arquivo JSON representa o layout esperado para a versão do Notfis correspondente, incluindo especificações de campos, como tamanho, posição, formato e obrigatoriedade.

## Dados de Entrada

Os dados de entrada devem estar no formato específico para garantir a validação e o processamento correto dos arquivos Notfis.

A classe `NotfisParser` fornece o método `parseNotfisLine`, que converte o conteúdo do arquivo Notfis em um objeto JSON estruturado.

### Exemplo de Uso

O método `parseNotfisLine` da classe `NotfisParser` pode ser utilizado para analisar o conteúdo do arquivo Notfis. O JSON de configuração e o conteúdo do arquivo Notfis são fornecidos conforme o exemplo abaixo:

```java
final var parser = new NotfisParser(NotfisType.VERSION31);
final List<NotfisLine> parsedLines = parser.parseNotfisLine(Paths.get("path/to/notfis.txt"));
final JSONObject jsonOutput = NotfisUtils.notfisLinesToJson(parsedLines);

System.out.println(jsonOutput.toString(2)); // JSON formatado
```
O JSON gerado seguirá o formato especificado, como no exemplo abaixo:
```json
{
  "000": [
    [
      {
        "name": "IDENTIFICADOR DE REGISTRO",
        "value": "000"
      },
      {
        "name": "IDENTIFICADOR DO REMETENTE",
        "value": "Google"
      },
      ...
    ]
  ],
  ...
}
```
## Instalação

Este projeto está disponível como dependência Maven:
```xml
<dependency>
  <groupId>io.github.tonyguerra122</groupId>
  <artifactId>notfis-parser</artifactId>
  <version>1.0.1</version>
</dependency>
```
