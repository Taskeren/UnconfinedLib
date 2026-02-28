# 多流体小机器

[UnconfinedLib#1](https://github.com/Taskeren/UnconfinedLib/pull/1)

被关闭的拉取请求：~~[GT5-Unofficial#5846](https://github.com/GTNewHorizons/GT5-Unofficial/pull/5846)~~

## 设计

`UnconfinedMultiFluidBasicMachine` 标记了一个 `MTEBasicMachine` 的子类，需要实现多流体的处理，同时这个接口也提供了多流体所需要的额外字段。

多流体的处理具体实现在 `MTEBasicMachineMixin` 里，但是需要实现 `UnconfinedMultiFluidBasicMachine` 才会真正启用这些注入的方法。

下面是两种实现方式。

#### 1. 就地（in-place）实现

由 `MTEBasicMachineImplMixin` 直接注入到 `MTEBasicMachine` 实现 `UnconfinedMultiFluidBasicMachine` 接口。

实现会在 `MTEBasicMachine` 里注入三个额外字段，分别是

- `unconfined$inputFluids`, `unconfined$outputFluids`：存储机器内部的流体，并且把机器默认的 `fillableStack` 和
  `drainableStack` 代理给了这两个 tank。流体格数量和物品格数量相等，但不超过3个，容量恒为128B。
- `unconfined$recipeOutputFluids`：存储机器完成配方后输出的流体（等价于 `mOutputFluid`）

默认关闭，可以使用配置启用。

#### 2. 子类实现

可以参考 `MultiFluidBasicMachineWithRecipe`，通过实现 `UnconfinedMultiFluidBasicMachine` 使得注入的代码生效。

用这个方法可以更好的控制流体输入/输出格的数量和容量。

## “流体储罐”包设计

`unconfined.util.fluidtank` 存放了为多流体设计的储罐实现，所有储罐类都实现了 `IUnconfinedFluidTank`
，大部分额外功能使用委托设计代理给底层的实现。

- `UnconfinedFluidTank` 是最基础的实现。
- `UnconfinedFluidTankOverridden` 提供了把流体格代理给外部字段的功能，用于把机器自带的 `fillableStack` 和
  `drainableStack` 接入流体储罐管理。
- `UnconfinedFluidTankNoOverflow` 禁止同种流体占用 1 个以上的格子
- `UnconfinedFluidTankCached` 把一些热点代码需要的实例缓存起来。
- `UnconfinedFluidTankIntegrated` 实现了 `IFluidHandler` 和 `IFluidTank`，不过目前没用。

创建 `IUnconfinedFluidTank` 可以使用 `UnconfinedFluidTank.builder()`。
