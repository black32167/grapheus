
version() {
    local description="$(git describe)"
    if [[ ${description} != *"-"* ]]; then
        echo "NO VALID RELEASE TAGS ARE FOUND"
        exit 1
    fi
    echo "${description%%-*}"
}