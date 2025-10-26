# nablarch-batch-try

Nablarch バッチアプリケーションの学習用リポジトリです。  
公式の archetype を利用して雛形プロジェクトを生成し、学習を進めています。

---

## 開発環境

- Java Version : 21
- Maven : 3.9.11

---

## 雛形プロジェクトの生成方法

このプロジェクトは [Nablarch batch archetype](https://mvnrepository.com/artifact/com.nablarch.archetype/nablarch-batch-archetype) を利用して作成しています。  
以下のコマンドを実行すると、Nablarch 6u3 のバッチ雛形プロジェクトが生成されます。

```bash
# archetype を指定してプロジェクト生成
mvn archetype:generate \
  -DarchetypeGroupId=com.nablarch.archetype \
  -DarchetypeArtifactId=nablarch-batch-archetype \
  -DarchetypeVersion=6u3

#実行後の対話 
Define value for property 'groupId': com.example
Define value for property 'artifactId': nablarch-batch-try
Define value for property 'version' 1.0-SNAPSHOT: 1.0-SNAPSHOT
Define value for property 'package' com.example: com.example.nablarch.batch
Confirm properties configuration:
groupId: com.example
artifactId: nablarch-batch-try
version: 1.0-SNAPSHOT
package: com.example.nablarch.batch
 Y:
 
 
 上記を確定すると、以下のような構成でプロジェクトが生成されます：
 nablarch-batch-try/
 ├── pom.xml
 ├── src/main/java/com/example/nablarch/batch/...
 ├── src/main/resources/...
 ├── work/
 └── ...
 
 
 # ビルド
mvn clean package -DskipTests
→ エラーがなければ成功。

nablarch-batch-try/
├── db/
│   └── （DDLや初期データを置く）
├── h2/
│   └── （組み込みDB H2 の設定ファイル置き場）
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/nablarch/example/batch/
│   │   │       ├── HelloAction.java
│   │   │       ├── NoInput.java
│   │   │       └── （今後追加予定: chunk配下にReader/Processor/Writer）
│   │   └── resources/
│   │       ├── batch-boot.xml
│   │       ├── log.properties
│   │       └── （ジョブ設定XMLを配置予定）
│   └── test/
│       └── （単体テストクラスを配置する場所）
├── tools/
│   └── static-analysis/
│       └── spotbugs/
│           └── （静的解析設定ファイル）
├── work/
│   ├── input/
│   ├── output/
│   └── （入出力ファイルを置く場所）
├── pom.xml
├── distribution.xml
├── app.log
└── monitor.log

| フォルダ／ファイル                           | 役割                                                                          
| ----------------------------------- | ----------------------------------------------------------------------------- |
| **db/**                             | テーブル定義や初期データ（DDL／INSERT文）を配置。                                                 
| **h2/**                             | 組み込みデータベース H2 の接続設定置き場。                                                       
| **src/main/java/**                  | バッチ本体の Java コード。                                                              
| ┗ **HelloAction.java**             | archetype生成時のサンプルジョブ。`Action<ExecutionContext>`を実装。ログに "Hello" を出力するだけの最小バッチ。 
| ┗ **NoInput.java**                 | 入力を取らない固定ジョブの例。                                                               
| **src/main/resources/**             | 各種設定ファイルを格納。                                                                  
| ┗ **batch-boot.xml**               | ハンドラー構成（共通処理フロー）を定義。                                                          
| ┗ **log.properties**               | ログ出力設定（app.log／monitor.log）。                                                  
| ┗ **（ジョブ設定XML）**            | 今後追加予定。chunk構成ジョブのReader/Writerなどを定義。                                         
| **distribution.xml**                | バッチIDと実行クラスの対応定義。`HelloAction` などを登録。                                         
| **work/**                           | CSVなどの入出力ファイルを置くディレクトリ。                                                       
| **tools/static-analysis/spotbugs/** | SpotBugsによる静的解析設定。                                                            
| **pom.xml**                         | Mavenビルド設定（依存ライブラリ・プラグインなど）。                                                  
| **app.log / monitor.log**           | 実行ログ（アプリ用／監視用）。                                                               

