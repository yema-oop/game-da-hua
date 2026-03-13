# LCchaobian 项目总览

本仓库采用单一仓库（monorepo）管理结构，包含服务器端与客户端代码及相关脚本资源。目标是清晰的构建、部署与维护路径。 

目录结构要点:
- GameServer: 服务器端代码与 WAR 包配置，使用 Maven 构建。
- GameClient: 客户端资源与产物，历史上为独立仓库，现进入根仓库统一管理（未来可考虑迁移为子模块）。
- lcWJTEST.sql、数据库脚本1.txt 等数据库相关脚本。
- 3个函数.txt: PL/SQL 函数实现片段，供参考。

快速开始
- 构建服务器端: 进入 GameServer，执行
  mvn -f pom.xml clean package -DskipTests
- 运行 WAR: 将 GameServer/target/*.war 部署到你的 Servlet 容器（如 Tomcat / Jetty）。
- 数据库初始化: 根据本地环境执行 lcWJTEST.sql 与 数据库脚本1.txt 中的创建用户、表空间等语句。

版本控制说明
- 已删除 GameClient/.git 作为嵌套仓库的历史记录，当前以单一仓库管理为起点。若需要保留历史，请告知，我可以将 GameClient 转换为子模块并导入历史。 
- 根目录新增 .gitignore，避免把构建产物和大文件提交到版本库。

后续建议
- 如需严格分离历史，可以将 GameClient 迁移为子模块或使用 subtree；也可进一步将服务端和客户端各自迁出到独立仓库。
- 增加一份更详细的部署文档和环境依赖清单。
