#!/usr/bin/env bash
set -Eeuo pipefail

ROOT="/root/home/forks/MineColonies"
TASK="$ROOT/CLAUDE_PORT_TASK_MAX.md"
JAVA25="/usr/lib/jvm/java-25-openjdk-amd64"

STAMP="$(date +%Y%m%d-%H%M%S)"
RUN_LOG="/root/home/forks/claude-minecolonies-port-$STAMP.log"
BACKUP_DIR="/root/home/forks/minecolonies-claude-backup-$STAMP"

test -d "$ROOT/.git"
test -f "$TASK"
test -x "$JAVA25/bin/java"
command -v claude >/dev/null

cd "$ROOT"

test "$(git branch --show-current)" = "port/26.1" || {
    echo "Ожидалась ветка port/26.1"
    exit 1
}

ACTIVE="$(
    pgrep -af \
        'GradleDaemon|GradleWrapperMain|runClient|runServer|net\.minecraft\.client|net\.minecraft\.server' \
        || true
)"

if [[ -n "$ACTIVE" ]]
then
    echo "Обнаружен активный Gradle/Minecraft-процесс:"
    printf '%s\n' "$ACTIVE"
    exit 1
fi

mkdir -p "$BACKUP_DIR"

git status -sb \
    > "$BACKUP_DIR/git-status.txt"

git diff --binary \
    > "$BACKUP_DIR/working-tree.patch"

git diff --stat \
    > "$BACKUP_DIR/diff-stat.txt"

git ls-files --others --exclude-standard \
    > "$BACKUP_DIR/untracked-files.txt"

if [[ -s "$BACKUP_DIR/untracked-files.txt" ]]
then
    tar -czf "$BACKUP_DIR/untracked-files.tar.gz" \
        --files-from="$BACKUP_DIR/untracked-files.txt"
fi

cp "$TASK" "$BACKUP_DIR/"

printf 'Backup: %s\n' "$BACKUP_DIR"
printf 'Log:    %s\n' "$RUN_LOG"

export JAVA_HOME="$JAVA25"
export PATH="$JAVA25/bin:$PATH"

PROMPT="$(cat "$TASK")"

# Под root нельзя использовать --dangerously-skip-permissions.
# Вместо этого явно разрешаем рабочие инструменты.
#
# Deny rules передаются одной строкой через запятые.
# Они расположены после prompt, поэтому не поглощают его.
ALLOW_TOOLS="Read,Edit,Write,Glob,Grep,Bash"
DENY_TOOLS="Bash(git push:*),Bash(git reset:*),Bash(git clean:*),Bash(git restore:*),Bash(git checkout --:*),Bash(rm -rf:*)"

set +e
set -o pipefail

claude \
    -p "$PROMPT" \
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

printf '\nПродолжение этой сессии:\n\n'
printf 'cd %q\n' "$ROOT"
printf './continue_claude_minecolonies_max.sh\n'

exit "$RC"
