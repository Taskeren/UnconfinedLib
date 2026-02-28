# Standalone 发行版

Unconfined 既可以作为库（不修改任何既有内容），也可以作为独立模组（内建功能的实现）使用。

作为库使用时，应该选择没有后缀的版本；作为独立模组使用的时候选择 _standalone_ 后缀的分支。

Standalone 版的 JAR 的 `MANIFEST.MF` 里会有一个特殊的标记 _Unconfined-Standalone: true_
，在运行时会读取，默认启用内建的内容（会保存到配置文件里，所以之后换成普通的 JAR 并不会取消这个操作，有这个需求，需要删除配置文件）。

除了使用 standalone 版本的 JAR，还可以使用 JVM 参数 `unconfined.standalone=true` 来启动独立版本的功能。
