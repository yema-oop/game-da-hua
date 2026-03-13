# 项目体系与版本控制方案

- 结构概览
  - GameServer: 服务器端 Maven Web 应用，打包为 WAR，位于 GameServer/。
  - GameClient: 客户端资源与产物，位于 GameClient/，其内部曾是独立的 Git 仓库。
- 版本控制策略
  - 目标：在根目录建立一个统一的 Monorepo，包含 GameServer 与 GameClient 的代码与资源。
  - 选项 A（推荐，当前优先方案）：将 GameServer 与 GameClient 作为同一仓库管理，移除 GameClient 的嵌套历史（已执行），保留代码结构即可；后续如需保留历史，可将 GameClient 作为子模块处理。 
  - 选项 B：将 GameClient 作为子模块引用本地路径或远端仓库。此方案需要额外配置远程仓库 URL。 
- 构建与运行
  - 服务器端：进入 GameServer 目录，执行 mvn -f pom.xml clean package -DskipTests；产物 WAR 在 GameServer/target/ 目录。
  - 客户端：当前为资源/产物丰富的目录；如需独立构建，请提供该客户端的构建工具信息，或迁移到统一构建系统后再描述。 
- 数据库与脚本
  - lcWJTEST.sql、数据库脚本1.txt 等在根目录，用于初始化演示环境。请在受控的本地/测试环境执行。
- 迁移计划
 1) 清点并提交根级 README，总结项目目标、模块划分与构建步骤。
 2) 确认并清理敏感信息与大体积产物，确保 .gitignore 覆盖构建产物。
 3) 将 GameServer、GameClient 的构建与部署流程写成清晰的本地开发指引。
 4) 如需要，逐步引入子模块 / subtree，保留历史或迁移历史。 
- 风险与注意
  - 在未确认无敏感信息前，不应暴露任何凭证性信息。
  - 大型二进制产物不应长期存在于版本库内，应通过二进制制品仓库或发布机制管理。
