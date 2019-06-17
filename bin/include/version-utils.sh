
latest_tag() {
    local description="$(git describe)"
    if [[ ${description} != *"-"* ]]; then
        echo "NO VALID RELEASE TAGS ARE FOUND"
        exit 1
    fi
    echo "${description%%-*}"
}

release_version() {
    local lates_tag="$(latest_tag)"
    echo "${lates_tag/*_}"
}

current_version() {
    local scripts_root="${BASH_SOURCE%/*/*}"
    local version=$(mvn -q exec:exec -Dexec.executable=echo -Dexec.args='${project.version}' --non-recursive)
    echo "${version}"
}