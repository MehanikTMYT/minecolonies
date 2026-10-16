#!/usr/bin/env bash
set -Eeuo pipefail

ROOT="/root/home/forks/MineColonies"
JAVA25="/usr/lib/jvm/java-25-openjdk-amd64"

test -d "$ROOT/.git"
test -f "$ROOT/CLAUDE_PORT_TASK_MAX.md"
test -x "$JAVA25/bin/java"
command -v claude >/dev/null

cd "$ROOT"

test "$(git branch --show-current)" = "port/26.1" || {
    echo "Ожидалась ветка port/26.1"
    exit 1
}

export JAVA_HOME="$JAVA25"
export PATH="$JAVA25/bin:$PATH"

INITIAL_PROMPT="$(cat <<'PROMPT'
Продолжай существующий порт MineColonies на Minecraft 26.1.2.

Это новая интерактивная сессия, поэтому сначала самостоятельно прочитай:

1. CLAUDE_PORT_TASK_MAX.md
2. текущий git status и полный git diff
3. /root/home/forks/minecolonies-26.1.2-claude-current.log
4. все файлы в /root/home/forks/minecolonies-26.1-port-audit/claude/
5. наиболее свежие minecolonies-26.1.2-compile-pass*.log

Предыдущая неинтерактивная сессия уже внесла много изменений. Не повторяй исправленные миграции вслепую и ничего не откатывай.

Сначала покажи в чате краткую проверяемую сводку:
- сколько уникальных compiler errors осталось;
- сколько файлов с ошибками;
- какие изменения сделала предыдущая сессия;
- результат последней compileJava;
- какие три корневых слоя ты будешь исправлять следующими.

После сводки сразу продолжай работу: читай целевой API, редактируй исходники, запускай compileJava и показывай существенные команды и результаты в чате.

Не останавливайся на анализе. Не делай commit или push. Не выполняй reset, clean, restore существующего рабочего дерева или массовое удаление исходников. После каждого слоя сообщай число ошибок до и после. После compileJava SUCCESS переходи к clean build и smoke tests.
PROMPT
)"

ALLOW_TOOLS="Read,Edit,Write,Glob,Grep,Bash"
DENY_TOOLS="Bash(git push:*),Bash(git reset:*),Bash(git clean:*),Bash(git restore:*),Bash(git checkout --:*),Bash(rm -rf:*)"

exec claude \
    "$INITIAL_PROMPT" \
    --model opus \
    --verbose \
    --allowedTools "$ALLOW_TOOLS" \
    --disallowedTools "$DENY_TOOLS"
