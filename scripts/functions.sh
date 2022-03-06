#!/bin/bash

function printInfo() {
    echo -e "\e[96m$1\e[0m"
}

function printError() {
    echo -e "\e[91m$1\e[0m"
}

function printSuccess() {
    echo -e "\e[92m$1\e[0m"
}

function requiredParameter() {
    if [[ -z $1 ]]; then
        echo "Missing required parameter $2"
        exit 1
    fi
}

function computeReleaseVersion() {
    FLAG=$1
    VERSION=$2

    echo $(./scripts/semver.sh bump $FLAG $VERSION)
}

function computeSnapshotVersion() {    
    NOW=$(date +"%Y%m%d%H%M%S")

    if [[ -z $1 ]]; then        
        SUFFIX="$(getCommitRefSlug)-$NOW"
    else
        # CI only (git command fails to return good branch name, 'head' returned)
        SUFFIX="$1-$NOW"
    fi
    VERSION=$(./scripts/semver.sh bump patch $(getCurrentVersion))

    echo $(./scripts/semver.sh bump prerel $SUFFIX $VERSION)
}

function getCurrentVersion() {
    LATEST_TAG_HASH=$(git rev-list --tags --max-count=1)
    STATUS=0

    if [[ -z ${LATEST_TAG_HASH} ]]; then
        CURRENT_VERSION="0.0.0"
        STATUS=1
    else
        CURRENT_VERSION=$(git describe --tags $LATEST_TAG_HASH)
    fi

    echo $CURRENT_VERSION
    return $STATUS
}

function slugify() {
    SLUG=$(echo $1 | tr '[A-Z]' '[a-z]') # to lowercase
    SLUG=${SLUG/\/-/} # replace '/' by '-'

    echo $SLUG
}

function getCommitRefSlug() {
    echo $(slugify $(git rev-parse --abbrev-ref HEAD))
}