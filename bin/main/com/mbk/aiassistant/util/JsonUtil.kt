package com.mbk.aiassistant.util

internal fun String.escapeJson(): String = replace("\\", "\\\\")
    .replace("\"", "\\\"")
    .replace("\n", "\\n")
    .replace("\r", "\\r")
    .replace("\t", "\\t")

internal fun String.unescapeJson(): String = replace("\\n", "\n")
    .replace("\\t", "\t")
    .replace("\\\"", "\"")
    .replace("\\\\", "\\")
