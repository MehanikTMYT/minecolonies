# MineColonies 26.1.2 — максимальное автономное задание для Claude Code

## Роль

Ты — ведущий инженер по миграции крупных Java-модов Minecraft и NeoForge. Работай как владелец результата, а не как консультант. Самостоятельно доведи текущий порт MineColonies до компилируемого, собираемого и запускаемого состояния на Minecraft 26.1.2.

Не ограничивайся анализом. Читай исходники, локальные source JAR, Gradle-кэши, mappings, журналы компиляции и API целевой версии; затем редактируй код, запускай сборки, классифицируй новые ошибки и продолжай итерации. Не заявляй, что слой исправлен, пока это не подтверждено новой сборкой.

## Рабочая область

Основной репозиторий:

```text
/root/home/forks/MineColonies
```

Целевая ветка:

```text
port/26.1
```

Проверенный baseline 1.21.1:

```text
1e1b1d8ebf63bbd2c248736364fb59296a75c146
```

Baseline до начала порта собирался успешно:

```text
BUILD SUCCESSFUL
2060 Java sources
94 XML GUI files
157 recipe JSON
4820 generated files
```

Целевая платформа:

```text
Minecraft 26.1.2
NeoForge 26.1.2.81 как минимальная сборочная база
Java 25
Gradle 9.2.1
```

Java:

```text
/usr/lib/jvm/java-25-openjdk-amd64
```

Локальный OperaPublicaCreator:

```text
/root/home/forks/OperaPublicaCreator/gradle/mod.gradle
```

Во всех Gradle-командах используй:

```bash
-PopcScript=/root/home/forks/OperaPublicaCreator/gradle/mod.gradle
```

Рабочие локальные зависимости уже перенесены и проверены клиентом и dedicated server:

```text
BlockUI
Structurize
Domum Ornamentum
Multi-Piston
Regions Unexplored
EMI
```

Maven Local содержит:

```text
com.ldtteam:blockui:0.0.1-26.1.2
com.ldtteam:structurize:0.0.1-26.1.2
com.ldtteam:multipiston:0.0.1-26.1.2
```

Domum Ornamentum:

```text
/root/.m2/repository/com/ldtteam/domum-ornamentum/1.0.0-local/
domum-ornamentum-1.0.0-local-api.jar
domum-ornamentum-1.0.0-local-main.jar
```

`main.jar` содержит внутренние декоративные классы и NeoForge metadata. Текущий Gradle-мост подключает оба JAR. Не заменяй runtime JAR на API-only JAR.

## Текущее состояние

Сначала проверь реальное состояние и не полагайся слепо на числа ниже.

Последняя известная сводка после pass 5:

```text
Unique compiler errors: 2978
Files with errors: 581
cannot find symbol: 1721
method does not override: 334
```

Последний известный журнал:

```text
/root/home/forks/minecolonies-26.1.2-compile-pass5.log
```

В каталоге могут быть более новые `compile-pass*.log`. Найди самый новый валидный журнал и обязательно сделай свежий `compileJava`.

Уже применены крупные механические миграции:

```text
ResourceLocation -> Identifier
new Identifier(...) -> Identifier.parse/fromNamespaceAndPath
readResourceLocation/writeResourceLocation -> readIdentifier/writeIdentifier
CommandSourceStack#getCommandSenderWorld -> getLevel
часть ChunkPos x/z/toLong/asLong
часть Level isClientSide/random
массовый CompoundTag Optional migration
InteractionResultHolder и ItemInteractionResult -> InteractionResult
critereon package -> criterion
```

Legacy JEI временно исключён через:

```text
gradle/port26-jei-excludes.gradle
```

Итоговая версия должна использовать нативный EMI. Не возвращай JEI/JEMI runtime до успешной базовой сборки.

После автоматической миграции могли остаться неверные конструкции:

```java
new InteractionResult(...)
```

`InteractionResult` в целевой версии является интерфейсом. Найди и исправь их семантически. Используй целевые варианты результата и `heldItemTransformedTo`, когда старый holder возвращал изменённый `ItemStack`. Не создавай лишний общий адаптер, когда корректную семантику можно выразить прямо в месте вызова.

## Жёсткие ограничения безопасности

Не выполняй:

```text
git reset --hard
git clean -fd
git clean -fdx
git checkout -- .
git restore .
rm -rf по репозиторию или крупным деревьям
git push
git push --force
```

Не удаляй пользовательские изменения ради уменьшения числа ошибок.

Не изменяй другие репозитории в `/root/home/forks`; разрешено только читать их исходники и артефакты.

Не изменяй BlockUI, Structurize, Domum Ornamentum, Multi-Piston или OperaPublicaCreator в этой задаче.

Не добавляй в Git:

```text
CLAUDE_PORT_TASK.md
CLAUDE_PORT_TASK_MAX.md
.port-backups
reports/
*.log
/tmp/*
```

Не создавай commits и не push без прямой команды пользователя.

Перед крупным слоем сохраняй:

```bash
git diff --binary > /root/home/forks/minecolonies-port-before-<layer>.patch
```

Не применяй массовую замену без:
- точного перечня конструкций;
- резервной копии;
- `git diff --check`;
- новой компиляции;
- сравнения числа ошибок.

Не скрывай ошибки:
- не удаляй `@Override` массово;
- не комментируй функциональность;
- не заменяй логику на `return null`, `false`, `0`, `ItemStack.EMPTY` или `UnsupportedOperationException`;
- не исключай из source set основные пакеты;
- не создавай пустые заглушки удалённых vanilla/NeoForge классов;
- не вставляй unchecked casts только ради компиляции;
- не используй `Optional#get()` без доказанной обязательности значения.

Временно отключать можно только внешние необязательные интеграции, с документированием причины и задачи восстановления.

## Определение готовности

Работа не завершена, пока не выполнено:

```text
clean compileJava -> SUCCESS
clean build -> SUCCESS
datagen/resources validation -> SUCCESS или доказанно неприменимо
runServer smoke test -> сервер дошёл до готовности без crash
runClient smoke test -> главное меню и тестовый мир загружаются без crash
ядро MineColonies регистрируется
обязательные зависимости загружаются
нет JEI runtime
нет API-only JAR вместо runtime JAR
```

После первого `compileJava SUCCESS` не останавливайся.

## Рабочий цикл

### 0. Аудит

```bash
cd /root/home/forks/MineColonies

git branch --show-current
git status -sb
git diff --check
git diff --stat
git diff -- build.gradle settings.gradle gradle.properties gradle/dependencies.gradle gradle/wrapper/gradle-wrapper.properties
```

Проверь Java и Gradle:

```bash
/usr/lib/jvm/java-25-openjdk-amd64/bin/java -version

JAVA_HOME=/usr/lib/jvm/java-25-openjdk-amd64 PATH=/usr/lib/jvm/java-25-openjdk-amd64/bin:$PATH ./gradlew   -PopcScript=/root/home/forks/OperaPublicaCreator/gradle/mod.gradle   --version
```

Найди свежие логи:

```bash
find /root/home/forks   -maxdepth 1   -type f   -name 'minecolonies-26.1.2-compile-pass*.log'   -printf '%T@ %p\n'   | sort -n
```

Проверь процессы:

```bash
pgrep -af 'GradleDaemon|GradleWrapperMain|runClient|runServer|net\.minecraft\.client|net\.minecraft\.server' || true
```

### 1. Свежая диагностическая компиляция

```bash
cat > /tmp/minecolonies-javac-diagnostics.gradle <<'GRADLE'
allprojects {
    tasks.withType(
        org.gradle.api.tasks.compile.JavaCompile
    ).configureEach {
        options.compilerArgs.addAll([
            '-Xmaxerrs', '10000',
            '-Xmaxwarns', '1000',
            '-Xdiags:verbose'
        ])
    }
}
GRADLE
```

```bash
JAVA_HOME=/usr/lib/jvm/java-25-openjdk-amd64 PATH=/usr/lib/jvm/java-25-openjdk-amd64/bin:$PATH ./gradlew   -PopcScript=/root/home/forks/OperaPublicaCreator/gradle/mod.gradle   --init-script /tmp/minecolonies-javac-diagnostics.gradle   clean compileJava   --no-daemon   --no-build-cache   --no-configuration-cache   --stacktrace   --console=plain   |& tee /root/home/forks/minecolonies-26.1.2-claude-current.log
```

Не считай повторённые строки stacktrace отдельными ошибками. Уникальные заголовки:

```regex
^/.*\.java:\d+: error:
```

Создай локальный отчёт:

```text
/root/home/forks/minecolonies-26.1-port-audit/claude/
```

Файлы:

```text
compiler-summary.md
error-files.txt
error-groups.tsv
current-plan.md
resolved-layers.md
blocked-items.md
```

Не добавляй их в Git.

### 2. Классификация

Группируй по корневой причине, не по файлам. Приоритет:

1. Imports/package/class rename и каскады.
2. Базовые интерфейсы и сигнатуры.
3. Сериализация/NBT/registries.
4. Datagen и рецепты.
5. Entities и AI.
6. Client GUI.
7. Render states/models/armor.
8. Опциональные интеграции.
9. Ресурсы/runtime.

Для каждой группы фиксируй:
- число ошибок до;
- число файлов;
- фактическую целевую API-форму;
- источник подтверждения;
- план;
- риски семантики;
- число ошибок после.

### 3. Итерации

Для каждого слоя:

1. Прочитай все затрагиваемые исходники.
2. Найди целевую сигнатуру в source JAR, Gradle cache, NeoForge source или mappings.
3. Найди рабочий аналог в уже перенесённых локальных модах.
4. Исправь логически законченную группу.
5. Выполни `git diff --check`.
6. Запусти `compileJava`.
7. Сравни число ошибок.
8. Исправь новые каскады текущего слоя.
9. Продолжай без ожидания пользователя, пока есть следующий технически определённый слой.

Не останавливайся после плана.

## Источники истины

Порядок:

1. Фактические Minecraft/NeoForge 26.1.2 классы в Gradle cache.
2. Sources JAR и `javap` целевой версии.
3. Официальные NeoForge primers/docs.
4. Рабочие порты BlockUI, Structurize, Domum Ornamentum, Multi-Piston.
5. Mojang mappings.
6. Внешние примеры.

Не угадывай сигнатуры по памяти.

Полезные команды:

```bash
find ~/.gradle/caches   -type f   \( -name '*sources.jar' -o -name '*.jar' \)   | grep -Ei 'minecraft|neoforge|neoform'   | head -n 200
```

```bash
jar tf path/to/sources.jar | grep 'ClassName'
unzip -p path/to/sources.jar path/to/ClassName.java | sed -n '1,240p'
javap -classpath path/to/jar -p fully.qualified.ClassName
```

```bash
find /root/home/forks -maxdepth 1 -type d -printf '%f\n' | sort
rg -n 'targetMethod|TargetClass' /root/home/forks/<working-port>
```

## Миграционные области

### Identifier

Проверь остатки:

```text
ResourceLocation
ResourceLocationException
new Identifier
Identifier.location()
readResourceLocation
writeResourceLocation
```

Используй фактические формы 26.1.2:

```text
Identifier
Identifier.parse
Identifier.tryParse
Identifier.fromNamespaceAndPath
readIdentifier
writeIdentifier
```

NBT-строки возвращают `Optional<String>`; обрабатывай отсутствие осмысленно.

### NBT и сохранение

Различай:
- обязательное поле;
- необязательное поле старого мира;
- безопасный default;
- повреждённые данные.

Проверь методы:

```text
getIntOr
getStringOr
getBooleanOr
getDoubleOr
getLongOr
getCompoundOrEmpty
getListOrEmpty
```

Особые зоны:

```text
IFactoryController.deserializeTag
StandardFactoryController
request system factories
assignment data stores
SettingsFactories
TileEntityDecorationController
AbstractAssignedCitizenModule
StandardRequestManager
StandardRetryingRequestResolverFactory
```

Новая Entity/BlockEntity/Mob сериализация может использовать `ValueInput/ValueOutput`:

```text
loadAdditional
saveAdditional
readAdditionalSaveData
addAdditionalSaveData
saveWithId
```

Не добавляй фиктивные перегрузки вместо миграции super contract.

### InteractionResult

Проверь:

```text
InteractionResultHolder
ItemInteractionResult
new InteractionResult
sidedSuccess
PASS_TO_DEFAULT_BLOCK_INTERACTION
SKIP_DEFAULT_BLOCK_INTERACTION
heldItemTransformedTo
```

Сохраняй server/client, consume/pass/fail, default block interaction и изменение held item.

### Advancement criteria

Пакет:

```text
net.minecraft.advancements.criterion
```

Проверь generic bounds и lookup:

```text
SimpleCriterionTrigger<T>
SimpleInstance
ContextAwarePredicate
ItemPredicate
EntityPredicate
ItemUsedOnLocationTrigger
InventoryChangeTrigger
StatePropertiesPredicate
HolderGetter
HolderLookup.Provider
```

Не передавай `null` lookup. Протяни реальный provider/context.

Согласованно перенеси все custom triggers в `com.minecolonies.api.advancements`.

### Registries

Lookup часто возвращает `Optional` или `Holder.Reference<T>`.

Не делай глобальный `.get()`. Для обязательных внутренних registries используй информативный `orElseThrow`; для datapack/старых миров — безопасную обработку.

### Recipes и datagen

Типичные зоны:

```text
shaped/shapeless/smelting signatures
RecipeOutput
save(RecipeOutput, Identifier)
ItemStackTemplate
Recipe.CommonInfo
Recipe.BookInfo
Holder<Item>
addOptionalTag
ExistingFileHelper
model generators
```

Используй рабочие порты Structurize/DO как примеры. Не меняй результат рецепта ради компиляции.

### Items, armor и components

Проверь:

```text
ArmorItem
ArmorMaterial
SwordItem
Tier
Tiers
UseAnim
getFoodProperties
getCraftingRemainingItem
setNoRepair
appendHoverText
```

Не создавай копии удалённых vanilla классов. Перенеси на новый equipment/component API, сохрани durability, repair, slot, attributes, tooltip и models.

### Entities

Проверь пакеты, hierarchy и generics:

```text
Horse
AbstractHorse
Bee
Cow
Sheep
MushroomCow
AbstractArrow
Arrow
ThrownTrident
ThrownPotion
AbstractMinecart
MobSpawnType
DimensionTransition
SynchedEntityData
goalSelector
attributes
RemovalReason
```

Не вставляй unchecked casts вместо восстановления правильной hierarchy.

### Blocks/world

Проверь новые сигнатуры:

```text
updateShape
onRemove
wasExploded
isSolidRender
BlockPos
ChunkPos
LevelWriter.addFreshEntity
serverTick
tickets
build height
```

### Networking

Проверь:

```text
RegistryFriendlyByteBuf
StreamCodec
CustomPacketPayload
PayloadRegistrar
Holder serialization
ItemStack codecs
```

### GUI и BlockUI

Не делай слепую замену `GuiGraphics -> GuiGraphicsExtractor`.

Изучи локальный BlockUI 26.1.2 и переноси MineColonies под его фактические callbacks:
- extraction;
- scissor;
- tooltips;
- item rendering;
- textures;
- input;
- final width/height fields.

### Rendering

Отдельный слой:

```text
RegisterRenderers
registerEntityRenderer
registerBlockEntityRenderer
RenderType
render states
renderer generics
HumanoidModel.setupAnim
armor layers
VertexBuffer
BakedModel
ModelData
BlockEntityWithoutLevelRenderer
item properties
atlas/model manager
```

Не стирай generics. Восстанови правильные render states и contracts.

### Chat/components/profiles

`ClickEvent`, `HoverEvent`, `ResolvableProfile` могут быть интерфейсами/абстракциями. Используй новые concrete forms/factories и сохрани действие.

### Inventory/capabilities

Проверь:

```text
INBTSerializable
ItemHandler
IItemHandler
CombinedItemHandler
Inventory.items private
getItems
serializeNBT/deserializeNBT
```

Не применяй reflection для приватных полей.

### NeoForge model data

Старые пакеты model data/generators могли быть удалены. Изучи новый API и локальные порты. Не добавляй старую NeoForge версию как dependency.

### Опциональные интеграции

До базового успеха:

```text
JEI — исключён, позднее заменить EMI
JourneyMap — изолировать только при доказанном blocker
Dynamic Trees — изолировать только при доказанном blocker
```

Не отключай MineColonies core и обязательные LDTTeam зависимости.

## Стандарты качества

Каждое исправление:
1. Компилируется против фактического 26.1.2 API.
2. Сохраняет поведение или документирует неизбежное изменение.
3. Не использует raw types без необходимости.
4. Не прячет generic/type errors suppressions.
5. Не проглатывает исключения.
6. Не создаёт client imports на dedicated server path.
7. Сохраняет стиль MineColonies.
8. Проверяется diff и новой сборкой.

Хороший слой:
- исправляет один API contract;
- имеет ограниченную область;
- уменьшает ошибки;
- не создаёт десятки новых overload conflicts.

Плохой слой:
- удаляет `@Override`;
- вставляет casts;
- вызывает `Optional.get()`;
- создаёт заглушки;
- исключает core package;
- комментирует runtime logic.

## После compileJava

### Полный build

```bash
JAVA_HOME=/usr/lib/jvm/java-25-openjdk-amd64 PATH=/usr/lib/jvm/java-25-openjdk-amd64/bin:$PATH ./gradlew   -PopcScript=/root/home/forks/OperaPublicaCreator/gradle/mod.gradle   clean build   --no-daemon   --no-build-cache   --no-configuration-cache   --stacktrace   --console=plain   |& tee /root/home/forks/minecolonies-26.1.2-claude-build.log
```

Исправь test compilation, datagen, resources, metadata и publication variants.

### JAR audit

Не выбирай:

```text
*-api.jar
*-sources.jar
*-javadoc.jar
*-test.jar
```

Проверь основной JAR и NeoForge metadata.

### Dedicated server smoke test

Используй Gradle run task, не копируй автоматически в production. Сервер должен пройти registries, datapacks, recipes, loot, structures и world load, затем корректно остановиться.

### Client smoke test

Проверь:
- главное меню;
- тестовый мир;
- создание/загрузка колонии;
- основные BlockUI окна;
- citizens/raiders;
- предметы, armor, tooltips;
- отсутствие JEI.

## EMI после базовой сборки

После стабильного core-port:
1. Изучи нативный EMI plugin API целевой версии.
2. Перенеси полезные категории старого JEI:
   - colony job recipes;
   - crops;
   - florist;
   - fisherman;
   - compost;
   - tools;
   - teaching transfer, если корректно поддерживается.
3. Не добавляй JEMI.
4. Не возвращай JEI runtime.
5. Сохрани dedicated server compatibility.

## Рабочий статус

Обновляй:

```text
/root/home/forks/minecolonies-26.1-port-audit/claude/current-plan.md
```

Формат:

```markdown
# Current status
- Latest compile errors:
- Files with errors:
- Last successful layer:
- Current layer:
- Next layer:
- Hard blockers:

# Error groups
| Group | Before | After | Files | Status |

# Changed files in current layer
# Verification commands
# Known temporary exclusions
```

Большое число ошибок не является blocker.

Настоящий blocker:
- отсутствует обязательная dependency;
- повреждён артефакт;
- API-информация недоступна после local source audit;
- конфликт зависимого порта требует решения владельца.

## При ограничении контекста

Перед завершением:
1. Обнови `current-plan.md`.
2. Запиши последнюю build-команду и результат.
3. Запиши незавершённые изменения.
4. Выполни `git diff --check`.
5. Не оставляй полуприменённую массовую замену.

При продолжении новой сессией сначала прочитай:
- этот файл;
- `current-plan.md`;
- свежий compile log;
- `git diff`.

## Финальный ответ

Допустим только после максимально возможного проверенного результата. Укажи:

```text
compileJava: SUCCESS/FAILED
build: SUCCESS/FAILED
runServer: SUCCESS/FAILED/NOT RUN
runClient: SUCCESS/FAILED/NOT RUN
оставшиеся уникальные ошибки
настоящие blockers
исправленные подсистемы
временные отключения
пути логов
git diff --stat
```

Если ошибки остаются, не называй работу завершённой.

## Начни немедленно

1. Выполни аудит.
2. Сделай свежий compile log.
3. Исправь остатки `InteractionResult`.
4. Закрой остаточный NBT/request-system слой.
5. Перейди к advancement predicates и registries.
6. Продолжай слой за слоем до `compileJava SUCCESS`.
7. Затем выполни полный build и runtime smoke tests.

Работай автономно. Не проси пользователя выбирать между очевидными техническими вариантами. Спрашивай только при настоящем архитектурном конфликте, который нельзя разрешить по исходному поведению и целевому API.
