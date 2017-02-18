#!/bin/bash
set -e

BRANCH_NAME=${TRAVIS_BRANCH}
ARTIFACT_VERSION=undefined

uploadReleaseToGitHub() {
    git fetch --tags
    LAST_TAG=$(git describe --tags --abbrev=0)
    THIS_RELEASE=$(git rev-parse --short ${BRANCH_NAME})
    local IFS=$'\n'
    RELEASE_NOTES_ARRAY=($(git log --format=%B $LAST_TAG..$THIS_RELEASE | tr -d '\r'))
    for i in "${RELEASE_NOTES_ARRAY[@]}"
    do
        RELEASE_NOTES="$RELEASE_NOTES\\n$i"
    done

    BODY="{
        \"tag_name\": \"$ARTIFACT_VERSION\",
        \"target_commitish\": \"$BRANCH_NAME\",
        \"name\": \"$ARTIFACT_VERSION\",
        \"body\": \"$RELEASE_NOTES\"
    }"

    # Create the release in GitHub and extract its id from the data.network.response
    RESPONSE_BODY=$(curl \
            -u ${REPO_USER}:${GITHUB_TOKEN} \
            --header "Accept: application/vnd.github.v3+json" \
            --header "Content-Type: application/json; charset=utf-8" \
            --request POST \
            --data "${BODY}" \
            https://api.github.com/repos/"${TRAVIS_REPO_SLUG}"/releases)

    # Extract the upload_url value
    UPLOAD_URL=$(echo ${RESPONSE_BODY} | python -c 'import sys, json; print json.load(sys.stdin)[sys.argv[1]]' upload_url)
    # And replace the end of it, which is generic and useless, by a relevant one
    UPLOAD_URL=$(echo ${UPLOAD_URL} | sed 's/{?name,label}/?name=app-debug.aar/')

    # Build the aar
    ./gradlew :app:assembleDebug
    # Copy it out of its cave
    cp app/build/outputs/aar/app-debug.apk .

    # Attach the artifact
    curl -D - \
    -u ${REPO_USER}:${GITHUB_TOKEN} \
    --header "Accept: application/vnd.github.v3+json" \
    --header "Content-Type: application/zip" \
    --data-binary "@app-debug.apk" \
    --request POST \
    ${UPLOAD_URL}

    echo "Release complete."
}

case ${BRANCH_NAME} in
    "master")
        ARTIFACT_VERSION=$(git rev-list --count HEAD)
        uploadReleaseToGitHub
        ;;
    *)
        echo "Branch is ${BRANCH_NAME}, which is not releasable. Skipping release."
        exit 0
        ;;
esac
