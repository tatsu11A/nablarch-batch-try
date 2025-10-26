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
├─ pom.xml
├─ README.md
├─ work/
│  ├─ input/
│  │  └─ users.csv              ← 取込CSV（UTF-8 / ヘッダ: id,name,email）
│  └─ output/                   ← 出力先（将来のExportで使用）
└─ src/
   ├─ main/
   │  ├─ java/com/example/nablarch/batch/
   │  │  ├─ HelloAction.java                ← NoInput（疎通）
   │  │  ├─ ImportUsersAction.java          ← 最小実装：CSV→DB（1クラス完結）
   │  │  ├─ ImportUsersCsvAction.java       ← 標準形：BatchAction<UserRow>
   │  │  ├─ reader/UsersCsvReader.java      ← 標準形：DataReader<UserRow>
   │  │  └─ domain/SampleDomainManager.java ← ドメイン定義の雛形
   │  └─ resources/
   │     ├─ batch-boot.xml                  ← 雛形起動設定
   │     ├─ batch-component-configuration.xml← 共通ハンドラ構成（DI）
   │     ├─ data-source.xml                 ← DB接続設定（H2など）
   │     ├─ import-users-boot.xml           ← 最小実装の起動設定
   │     └─ import-users-csv-boot.xml       ← 標準形の起動設定
   └─ test/ ...

実行手順
0) 事前準備

work/input/users.csv を配置（UTF-8）

id,name,email
1,Alice,alice@example.com
2,Bob,bob@example.com
3,Carol,carol@example.com

1) ビルド
mvn -q clean package -DskipTests


→ BUILD SUCCESS になればOK。


2) 実行：最小実装（ImportUsersAction）

CSV読み～DB upsert を1クラスで実施。
（文字コードを変える場合は -Dcharset=MS932 などに変更）

mvn exec:java ^
  -Dexec.mainClass=nablarch.fw.launcher.Main ^
  -Dexec.args="-requestPath ImportUsersAction -diConfig classpath:import-users-boot.xml -userId dev" ^
  -Dinput="work/input/users.csv" ^
  -Dcharset=UTF-8


期待ログ（抜粋）：

upsert id=1
upsert id=2
upsert id=3
[ImportUsersAction] finish: inserted/updated=3
exit code = [0]

3) 実行：標準形（ImportUsersCsvAction）

Reader 分離 + 1件処理（Nablarch標準形）。

mvn exec:java ^
  -Dexec.mainClass=nablarch.fw.launcher.Main ^
  -Dexec.args="-requestPath ImportUsersCsvAction -diConfig classpath:import-users-csv-boot.xml -userId dev"


期待ログ（抜粋）：

upsert id=1
upsert id=2
upsert id=3
TOTAL COMMIT COUNT = [3]
exit code = [0]