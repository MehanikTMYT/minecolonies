#!/usr/bin/env bash
set -Eeuo pipefail

ROOT="/root/home/forks/MineColonies"
TASK="$ROOT/CLAUDE_PORT_TASK_MAX.md"
JAVA25="/usr/lib/jvm/java-25-openjdk-amd64"

STAMP="$(date +%Y%m%d-%H%M%S)"
RUN_LOG="/root/home/forks/claude-minecolonies-continue-$STAMP.log"

test -d "$ROOT/.git"
test -f "$TASK"
test -x "$JAVA25/bin/java"
command -v claude >/dev/null

cd "$ROOT"

test "$(git branch --show-current)" = "port/26.1" || {
    echo "Ожидалась ветка port/26.1"
    exit 1
}

export JAVA_HOME="$JAVA25"
export PATH="$JAVA25/bin:$PATH"

ALLOW_TOOLS="Read,Edit,Write,Glob,Grep,Bash"
DENY_TOOLS="Bash(git push:*),Bash(git reset:*),Bash(git clean:*),Bash(git restore:*),Bash(git checkout --:*),Bash(rm -rf:*)"

CONTINUE_PROMPT="$(
cat <<'PROMPT'
Продолжай автономный порт MineColonies на Minecraft 26.1.2.

Сначала перечитай:
- CLAUDE_PORT_TASK_MAX.md;
- самый свежий compile log;
- minecolonies-26.1-port-audit/claude/current-plan.md, если он существует;
- текущий git diff.

Продолжай редактировать, компилировать и тестировать. Не останавливайся на анализе или плане. Работай до успешных compileJava и build либо до доказанного внешнего блокера. Не выполняй commit, push, reset, clean или удаление существующего порта.
PROMPT
)"

set +e
set -o pipefail

claude \
    -c \
    -p "$CONTINUE_PROMPT" \
    --model opus \
    --max-turns 200 \
    --verbose \
    --allowedTools "$ALLOW_TOOLS" \
    --disallowedTools "$DENY_TOOLS" \
    |& tee "$RUN_LOG"

RC="${PIPESTATUS[0]}"

set +o pipefail
set -e

printf '\nClaude exit code: %s\n' "$RC"
printf 'Log: %s\n' "$RUN_LOG"

exit "$RC"
