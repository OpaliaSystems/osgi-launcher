#!/bin/bash

set -euo pipefail

if [[ -n "${XMS:-}" ]]; then
  JAVA_OPTS="-Xms$XMS ${JAVA_OPTS:-}"
fi

if [[ -n "${XMX:-}" ]]; then
  JAVA_OPTS="-Xmx$XMX ${JAVA_OPTS:-}"
fi

if [[ -n "${XSS:-}" ]]; then
  JAVA_OPTS="-Xss$XSS ${JAVA_OPTS:-}"
fi

if [[ -n "${LOG_LEVEL:-}" ]]; then
  JAVA_OPTS="-Dlauncher.forced-root-log-level=$LOG_LEVEL ${JAVA_OPTS:-}"
fi

if [[ -n "${AUTO_SHUTDOWN:-}" ]]; then
  JAVA_OPTS="-Dlauncher.auto-shutdown=$AUTO_SHUTDOWN ${JAVA_OPTS:-}"
fi

if [[ -n "${AUTO_DEPLOYMENT:-}" ]]; then
  JAVA_OPTS="-Dlauncher.auto-deployment=$AUTO_DEPLOYMENT ${JAVA_OPTS:-}"
fi

if [[ -n "${AUTO_DEPLOYMENT_DIR:-}" ]]; then
  JAVA_OPTS="-Dlauncher.auto-deployment-directory=$AUTO_DEPLOYMENT_DIR ${JAVA_OPTS:-}"
fi

if [[ -n "${CACHE_DIR:-}" ]]; then
  JAVA_OPTS="-Dlauncher.cache-directory=$CACHE_DIR ${JAVA_OPTS:-}"
fi

if [[ -n "${PID_FILE:-}" ]]; then
  JAVA_OPTS="-Dlauncher.pid-file=$PID_FILE ${JAVA_OPTS:-}"
fi

if [[ -n "${BOOT_DELEGATIONS:-}" ]]; then
  JAVA_OPTS="-Dlauncher.boot-delegations=$BOOT_DELEGATIONS ${JAVA_OPTS:-}"
fi

if [[ -n "${EXTRA_EXPORT_PACKAGES:-}" ]]; then
  JAVA_OPTS="-Dlauncher.extra-export-packages=$EXTRA_EXPORT_PACKAGES ${JAVA_OPTS:-}"
fi

if [[ -n "${BUNDLE_ARTIFACTS:-}" ]]; then
  JAVA_OPTS="-Dlauncher.bundle-artifacts=$BUNDLE_ARTIFACTS ${JAVA_OPTS:-}"
fi

if [[ -n "${REMOTE_REPOSITORIES:-}" ]]; then
  JAVA_OPTS="-Dlauncher.remote-repositories=$REMOTE_REPOSITORIES ${JAVA_OPTS:-}"
fi

if [[ -n "${LOCAL_REPOSITORY:-}" ]]; then
  JAVA_OPTS="-Dlauncher.local-repository=$LOCAL_REPOSITORY ${JAVA_OPTS:-}"
fi

if [[ -n "${PROVIDE_LOGGING_SERVICE:-}" ]]; then
  JAVA_OPTS="-Dlauncher.provide-service.logging=$PROVIDE_LOGGING_SERVICE ${JAVA_OPTS:-}"
fi

export JAVA_OPTS

exec "$@"
