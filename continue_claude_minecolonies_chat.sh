#!/usr/bin/env bash
set -Eeuo pipefail

ROOT="/root/home/forks/MineColonies"
JAVA25="/usr/lib/jvm/java-25-openjdk-amd64"

test -d "$ROOT/.git"
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

echo "Запускается интерактивное продолжение Claude Code."
echo "Для выхода: /exit или Ctrl+C."
echo "Для отсоединения от tmux: Ctrl+B, затем D."
echo

exec claude \
  -c \
  --model opus \
  --verbose \
  --allowedTools "$ALLOW_TOOLS" \
  --disallowedTools "$DENY_TOOLS"
