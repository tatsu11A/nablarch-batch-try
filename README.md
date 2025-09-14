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